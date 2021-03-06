/*******************************************************************************
 * Copyright (c) 2000, 2008 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Anton Leherbauer (Wind River Systems)
 *     Warren Paul (Nokia) - Bug 218266
 *******************************************************************************/

package org.eclipse.cdt.internal.core.model;

import java.net.URI;

import org.eclipse.cdt.core.model.ICElement;

/**
 * ExternalTranslationUnit
 */
public class ExternalTranslationUnit extends TranslationUnit {

	public ExternalTranslationUnit(ICElement parent, URI uri, String contentTypeID) {
		super(parent, uri, contentTypeID);
	}

}
