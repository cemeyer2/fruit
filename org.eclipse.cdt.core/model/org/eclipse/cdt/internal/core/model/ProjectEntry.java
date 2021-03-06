/*******************************************************************************
 * Copyright (c) 2000, 2006 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.core.model;

import org.eclipse.cdt.core.model.IPathEntry;
import org.eclipse.cdt.core.model.IProjectEntry;
import org.eclipse.core.runtime.IPath;


public class ProjectEntry extends PathEntry implements IProjectEntry {

	public ProjectEntry(IPath path, boolean isExported) {
		super(IPathEntry.CDT_PROJECT, path, isExported);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IProjectEntry) {
			IProjectEntry otherEntry = (IProjectEntry)obj;
			if (!super.equals(otherEntry)) {
				return false;
			}
			if (path == null) {
				if (otherEntry.getPath() != null) {
					return false;
				}
			} else {
				if (!path.toString().equals(otherEntry.getPath().toString())) {
					return false;
				} 
			}
			return true;
		}
		return super.equals(obj);
	}

}
