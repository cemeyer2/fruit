/*******************************************************************************
 * Copyright (c) 2006 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Markus Schorn - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.cdt.internal.ui.viewsupport;

public class AsyncTreeWorkInProgressNode {

    private Object fParent;

    public AsyncTreeWorkInProgressNode(Object parentElement) {
        fParent= parentElement;
    }
    public Object getParent() {
        return fParent;
    }
    @Override
	public String toString() {
        return "..."; //$NON-NLS-1$
    }
}
