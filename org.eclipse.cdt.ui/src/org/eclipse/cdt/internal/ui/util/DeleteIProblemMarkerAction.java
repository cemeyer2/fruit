/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.ui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionDelegate;

import org.eclipse.cdt.core.model.ICModelMarker;

/**
 * @author Bogdan Gheorghe
 */
public class DeleteIProblemMarkerAction extends ActionDelegate implements IObjectActionDelegate {

    private IStructuredSelection  selection;

	/**
	 * @see ActionDelegate#run(IAction)
	 */
	@Override
	public void run(IAction action) {

		if (selection != null) {
			if (selection.isEmpty()) {
				return;
			}
			try {
				List<?> list = selection.toList();
				List<IMarker> listMarkers = new ArrayList<IMarker>();
				Iterator<?> iterator = list.iterator();
				while (iterator.hasNext()) {
					IMarker marker = (IMarker)iterator.next();
					if (marker.isSubtypeOf(ICModelMarker.INDEXER_MARKER)) {
						listMarkers.add(marker);
					}
				}
				// Bail out early
				if (listMarkers.isEmpty()) {
					return;
				}
				IMarker[] markers = new IMarker[listMarkers.size()];
				listMarkers.toArray(markers);
				// be sure to only invoke one workspace operation
				ResourcesPlugin.getWorkspace().deleteMarkers(markers);
				selection = null;
			} catch (CoreException e) {
			}
		}
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		boolean enable = false;
		if (selection instanceof IStructuredSelection) {
			Object object = ((IStructuredSelection) selection).getFirstElement();
			if (object instanceof IMarker) {
				try {
					IMarker marker = (IMarker) object;
					if (marker.isSubtypeOf(ICModelMarker.INDEXER_MARKER)) {
							enable = true;
					}
					this.selection = (IStructuredSelection)selection;
					action.setEnabled(enable);
				} catch (CoreException e) {
				}
			}
		}
	}

}
