/*******************************************************************************
 * Copyright (c) 2008 Symbian Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Andrew Ferguson (Symbian) - Initial implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.ui.text.doctools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.ICDescriptor;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.text.doctools.IDocCommentOwner;

/**
 * A ProjectMap is an internal abstraction which
 * <ul>
 * <li>Maintains mappings from project relative paths to comment-owner ID's
 * <li>Manages persistence of these mappings to the .cproject file.
 * </ul>
 * for a particular {@link IProject}
 * 
 * @since 5.0
 */
class ProjectMap {
	/** .cproject xml element/attribute names **/
	private static final String ATTRVAL_STORAGEID= "org.eclipse.cdt.internal.ui.text.commentOwnerProjectMappings"; //$NON-NLS-1$
	private static final String ELEMENT_DOC_COMMENT_OWNER = "doc-comment-owner"; //$NON-NLS-1$
	private static final String ATTRKEY_DCO_ID = "id"; //$NON-NLS-1$
	private static final String ELEMENT_PATH = "path"; //$NON-NLS-1$
	private static final String ATTRKEY_PATH_VALUE = "value"; //$NON-NLS-1$
	
	private IProject fProject;
	private Map<IPath, String> fMap;
	
	/**
	 * Loads the project map
	 * @param project
	 */
	public ProjectMap(IProject project) {
		try {
			fMap= load(project);
		} catch(CoreException ce) {
			CUIPlugin.log(ce);
			fMap= new HashMap<IPath, String>();
		}
		fProject= project;
	}
	
	/**
	 * Returns the id of the doc comment owner mapped to the resource specified, or null
	 * if no owner is mapped within this project. Ownership is inherited from parents and
	 *  may be overridden. 
	 * @param resource
	 * @return possibly null
	 */
	public String getOwnerID(IResource resource) {
		String id= null;
		if(resource!=null) {
			for(IPath p= resource.getProjectRelativePath(); ; p= p.removeLastSegments(1)) {
				if(fMap.containsKey(p)) {
					id= fMap.get(p);
					break;
				}
				if(p.isEmpty())
					break;
			}
		}
		return id;
	}

	/**
	 * Creates a new (or updates the existing) mapping associating the specified
	 * {@link IDocCommentOwner} with the specified {@link IResource}
	 * @param resource the non-null resource to create an association for
	 * @param owner the owner to associate the resource with, or <code>null</code> to ensure there
	 * is no association.
	 */
	public void setCommentOwner(IResource resource, IDocCommentOwner owner) {
		Assert.isNotNull(resource);
		if(ResourcesPlugin.getWorkspace().getRoot().equals(resource))
			throw new IllegalStateException();
		if(owner!=null) {
			fMap.put(resource.getProjectRelativePath(), owner.getID());
		} else {
			fMap.remove(resource.getProjectRelativePath());
		}
		try {
			save();
		} catch(CoreException ce) {
			CUIPlugin.log(ce);
		}
	}

	public boolean isEmpty() {
		return fMap.isEmpty();
	}
	
	private static Map<IPath, String> load(IProject project) throws CoreException {
		Map<IPath, String> result= new HashMap<IPath, String>();
		ICDescriptor pd= CCorePlugin.getDefault().getCProjectDescription(project, true);
		Element e= pd.getProjectData(ATTRVAL_STORAGEID);
		if(e.hasChildNodes()) {
			NodeList commentOwners= e.getElementsByTagName(ELEMENT_DOC_COMMENT_OWNER);
			for(int i=0; i<commentOwners.getLength(); i++) {
				Element node= (Element) commentOwners.item(i);
				Node commentOwnerIDNode= node.getAttributes().getNamedItem(ATTRKEY_DCO_ID);

				if(commentOwnerIDNode != null) {
					String commentOwnerID= commentOwnerIDNode.getNodeValue();

					NodeList paths= node.getElementsByTagName(ELEMENT_PATH);
					for(int j=0; j<paths.getLength(); j++) {
						Node path= paths.item(i);
						Node pathValue= path.getAttributes().getNamedItem(ATTRKEY_PATH_VALUE);
						if(pathValue != null) {
							result.put(Path.fromPortableString(pathValue.getNodeValue()), commentOwnerID);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Write the map to the .cproject file
	 */
	public void save() throws CoreException {
		ICDescriptor pd= CCorePlugin.getDefault().getCProjectDescription(fProject, true);

		// remove current associations
		Element data= pd.getProjectData(ATTRVAL_STORAGEID);
		NodeList nl= data.getChildNodes();
		for(int i=0; i<nl.getLength(); i++) {
			Node node= nl.item(i);
			if(node.getNodeType()== Node.ELEMENT_NODE) {
				data.removeChild(node);
			}
		}

		// invert and persist associations
		for(Iterator<String> i= fMap.values().iterator(); i.hasNext();) {
			String cid= i.next();
			Element commentNode= data.getOwnerDocument().createElement(ELEMENT_DOC_COMMENT_OWNER);
			commentNode.setAttribute(ATTRKEY_DCO_ID, cid);
			for(Iterator<IPath> j= fMap.keySet().iterator(); j.hasNext(); ) {
				IPath path= j.next();
				String ccid= fMap.get(path);
				if(cid.equals(ccid)) {
					Element pathNode= data.getOwnerDocument().createElement(ELEMENT_PATH);
					pathNode.setAttribute(ATTRKEY_PATH_VALUE, path.toPortableString());
					commentNode.appendChild(pathNode);
				}
			}
			data.appendChild(commentNode);
		}
		pd.saveProjectData();
	}
}