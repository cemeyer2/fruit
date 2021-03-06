/*******************************************************************************
 * Copyright (c) 2007, 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Markus Schorn - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.cdt.internal.core.pdom.indexer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.IndexLocationFactory;
import org.eclipse.cdt.core.model.AbstractLanguage;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.pdom.IndexerInputAdapter;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Provides information about translation-units.
 * @since 5.0
 */
public class ProjectIndexerInputAdapter extends IndexerInputAdapter {
	private final static boolean CASE_SENSITIVE_FILES= !new File("a").equals(new File("A"));  //$NON-NLS-1$//$NON-NLS-2$

	private final ICProject fCProject;
	private final HashMap<String, IIndexFileLocation> fIflCache;
	private final FileExistsCache fExistsCache;

	public ProjectIndexerInputAdapter(ICProject cproject) {
		this(cproject, true);
	}

	public ProjectIndexerInputAdapter(ICProject cproject, boolean useCache) {
		fCProject= cproject;
		if (useCache) {
			fIflCache= new HashMap<String, IIndexFileLocation>();
			fExistsCache= new FileExistsCache();
		}
		else {
			fIflCache= null;
			fExistsCache= null;
		}
	}

	@Override
	public IIndexFileLocation resolveASTPath(String astPath) {
		if (fIflCache == null) {
			return doResolveASTPath(astPath);
		}
		IIndexFileLocation result= fIflCache.get(astPath);
		if (result == null) {
			result = doResolveASTPath(astPath);
			fIflCache.put(astPath, result);
		}
		return result;
	}

	private IIndexFileLocation doResolveASTPath(String astPath) {
		return IndexLocationFactory.getIFLExpensive(fCProject, astPath);
	}

	@Override
	public IIndexFileLocation resolveIncludeFile(String includePath) {
		if (fIflCache == null) {
			return doResolveASTPath(includePath);
		}
		if (!fExistsCache.exists(includePath)) {
			return null;
		}
		IIndexFileLocation result= fIflCache.get(includePath);
		if (result == null) {
			result = doResolveASTPath(includePath);
			if (result.getFullPath() == null && !CASE_SENSITIVE_FILES) {
				try {
					File location= new File(includePath);
					String canonicalPath= location.getCanonicalPath();
					if (!includePath.equals(canonicalPath)) {
						result= IndexLocationFactory.getExternalIFL(canonicalPath);
						fIflCache.put(canonicalPath, result);
					}
				}
				catch (IOException e) {
					// just use the original
				}
			}
			fIflCache.put(includePath, result);
		}
		return result;
	}
	
	@Override
	public boolean doesIncludeFileExist(String includePath) {
		if (fExistsCache != null) {
			return fExistsCache.exists(includePath);
		}
		return new File(includePath).exists();
	}

	@Override
	public String getASTPath(IIndexFileLocation ifl) {
		IPath path= IndexLocationFactory.getAbsolutePath(ifl);
		if (path != null) {
			return path.toString();
		}
		return ifl.getURI().getPath();
	}

	@Override
	public IScannerInfo getBuildConfiguration(int linkageID, Object tu) {
		IScannerInfo info= ((ITranslationUnit) tu).getScannerInfo(true);
		if (info == null) {
			info= new ScannerInfo();
		}
		return info;
	}

	@Override
	public long getLastModified(IIndexFileLocation ifl) {
		String fullPath= ifl.getFullPath();
		if (fullPath != null) {
			IResource res= ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(fullPath));
			if (res != null) {
				return res.getLocalTimeStamp();
			}
			return 0;
		}
		IPath location= IndexLocationFactory.getAbsolutePath(ifl);
		if (location != null) {
			return location.toFile().lastModified();
		}
		return 0;
	}

	
	@Override
	public AbstractLanguage[] getLanguages(Object tuo) {
		ITranslationUnit tu= (ITranslationUnit) tuo;
		try {
			ILanguage lang= tu.getLanguage();
			if (lang instanceof AbstractLanguage) {
				return new AbstractLanguage[] {(AbstractLanguage) lang};
			}
		}
		catch (CoreException e) {
			CCorePlugin.log(e);
		}
		return new AbstractLanguage[0];
	}

	@Override
	public boolean isFileBuildConfigured(Object tuo) {
		ITranslationUnit tu= (ITranslationUnit) tuo;
		return !CoreModel.isScannerInformationEmpty(tu.getResource());
	}

	@Override
	public boolean isSourceUnit(Object tuo) {
		ITranslationUnit tu= (ITranslationUnit) tuo;
		return tu.isSourceUnit();
	}

	@Override
	public IIndexFileLocation resolveFile(Object tuo) {
		ITranslationUnit tu= (ITranslationUnit) tuo;
		return IndexLocationFactory.getIFL(tu);
	}
	
	@Override
	public boolean canBePartOfSDK(IIndexFileLocation ifl) {
		return ifl.getFullPath() == null;
	}

	@Override
	public Object getInputFile(IIndexFileLocation location) {
		try {
			return CoreModelUtil.findTranslationUnitForLocation(location, fCProject);
		} catch (CModelException e) {
			CCorePlugin.log(e);
		}
		return null;
	}

	@Override
	public CodeReader getCodeReader(Object tuo) {
		ITranslationUnit tu= (ITranslationUnit) tuo;
		return tu.getCodeReader();
	}
}
