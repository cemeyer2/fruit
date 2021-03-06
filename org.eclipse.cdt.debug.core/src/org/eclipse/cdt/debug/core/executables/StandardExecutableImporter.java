/*******************************************************************************
 * Copyright (c) 2008 Nokia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Nokia - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.debug.core.executables;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.IBinaryParser;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.debug.core.CDebugCorePlugin;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class StandardExecutableImporter implements IExecutableImporter {

	public static final String DEBUG_PROJECT_ID = "org.eclipse.cdt.debug"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.debug.core.executables.IExecutableImporter#importExecutables(java.lang.String[],
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void importExecutables(String[] fileNames, IProgressMonitor monitor) {
		monitor.beginTask("Import Executables", fileNames.length);

		IProject exeProject = null;
		boolean checkProject = false;
		// Weed out existing ones
		for (String path : fileNames) {

			try {
				path = new File(path).getCanonicalPath();
			} catch (IOException e1) {
			}
			if (!ExecutablesManager.getExecutablesManager().executableExists(Path.fromOSString(path))) {
				if (!checkProject) {
					// See if the default project exists
					String defaultProjectName = "Executables";
					ICProject cProject = CoreModel.getDefault().getCModel().getCProject(defaultProjectName);
					if (cProject.exists()) {
						exeProject = cProject.getProject();
					} else {
						final String[] ignoreList = { ".project", //$NON-NLS-1$
								".cdtproject", //$NON-NLS-1$
								".cproject", //$NON-NLS-1$
								".cdtbuild", //$NON-NLS-1$
								".settings", //$NON-NLS-1$
						};

						IWorkspace workspace = ResourcesPlugin.getWorkspace();
						IProject newProjectHandle = workspace.getRoot().getProject(defaultProjectName);

						int projectSuffix = 2;
						while (newProjectHandle.exists()){						
							newProjectHandle = workspace.getRoot().getProject(defaultProjectName + projectSuffix);
							projectSuffix++;
						}

						IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());
						description.setLocation(null);
						IFileStore store;
						try {
							store = EFS.getStore(workspace.getRoot().getLocationURI());
							store = store.getChild(newProjectHandle.getName());
							for (String deleteName : ignoreList) {
								IFileStore projFile = store.getChild(deleteName);
								projFile.delete(EFS.NONE, null);
							}
							exeProject = CCorePlugin.getDefault().createCProject(description, newProjectHandle, null, DEBUG_PROJECT_ID);
						} catch (OperationCanceledException e) {
							e.printStackTrace();
						} catch (CoreException e) {
							e.printStackTrace();
						}
					}
					checkProject = true;
				}

				importExecutable(exeProject, path);
			}
			monitor.worked(1);
			if (monitor.isCanceled()) {
				break;
			}
		}
		monitor.done();
	}

	private void importExecutable(IProject exeProject, String path) {

		IPath location = Path.fromOSString(path);
		String executableName = location.toFile().getName();
		IFile exeFile = exeProject.getProject().getFile(executableName);
		if (!exeFile.exists() && validateBinaryParsers(exeProject, new File(path))) {
			try {
				exeFile.createLink(location, 0, null);
			} catch (Exception e) {
			}
		}

	}

	private boolean isExtensionVisible(IExtension ext) {
		IConfigurationElement[] elements = ext.getConfigurationElements();
		for (int i = 0; i < elements.length; i++) {
			IConfigurationElement[] children = elements[i].getChildren("filter"); //$NON-NLS-1$
			for (int j = 0; j < children.length; j++) {
				String name = children[j].getAttribute("name"); //$NON-NLS-1$
				if (name != null && name.equals("visibility")) { //$NON-NLS-1$
					String value = children[j].getAttribute("value"); //$NON-NLS-1$
					if (value != null && value.equals("private")) { //$NON-NLS-1$
						return false;
					}
				}
			}
			return true;
		}
		return false; // invalid extension definition (must have at least
		// cextension elements)
	}

	private IBinaryParser instantiateBinaryParser(IExtension ext) {
		IBinaryParser parser = null;
		IConfigurationElement[] elements = ext.getConfigurationElements();
		for (int i = 0; i < elements.length; i++) {
			IConfigurationElement[] children = elements[i].getChildren("run"); //$NON-NLS-1$
			for (int j = 0; j < children.length; j++) {
				try {
					parser = (IBinaryParser) children[j].createExecutableExtension("class");
				} catch (CoreException e) {
					CDebugCorePlugin.log(e);
				}
			}
		}
		return parser;
	}

	private boolean validateBinaryParsers(IProject exeProject, File file) {
		IExtension[] binaryParserExtensions;
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(CCorePlugin.PLUGIN_ID, CCorePlugin.BINARY_PARSER_SIMPLE_ID);
		if (point != null) {
			IExtension[] exts = point.getExtensions();
			ArrayList<IExtension> extensionsInUse = new ArrayList<IExtension>();
			for (int i = 0; i < exts.length; i++) {
				if (isExtensionVisible(exts[i])) {
					extensionsInUse.add(exts[i]);
				}
			}
			binaryParserExtensions = extensionsInUse.toArray(new IExtension[extensionsInUse.size()]);

			for (int i = 0; i < binaryParserExtensions.length; i++) {
				IBinaryParser parser = instantiateBinaryParser(binaryParserExtensions[i]);
				if (isBinary(file, parser)) {
					String parserID = binaryParserExtensions[i].getUniqueIdentifier();
					// Make sure the project has this parser
					ICProjectDescription pd = CCorePlugin.getDefault().getProjectDescription(exeProject);
					try {
						pd.getDefaultSettingConfiguration().create(CCorePlugin.BINARY_PARSER_UNIQ_ID, parserID);
						CCorePlugin.getDefault().setProjectDescription(exeProject, pd, true, new NullProgressMonitor());
					} catch (CoreException e) {
					}
					return true;
				}
			}
		}

		return false;
	}

	private boolean isBinary(File file, IBinaryParser parser) {
		if (parser != null) {
			try {
				IBinaryParser.IBinaryFile bin = parser.getBinary(new Path(file.getAbsolutePath()));
				return bin != null && (bin.getType() == IBinaryParser.IBinaryFile.EXECUTABLE || bin.getType() == IBinaryParser.IBinaryFile.SHARED);
			} catch (IOException e) {
				return false;
			}
		} else
			return false;
	}

}
