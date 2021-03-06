/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.ui.text;

import org.eclipse.cdt.core.model.ITranslationUnit;

/**
 * Context information for quick fix and quick assist processors.
 * <p>
 * Note: this interface is not intended to be implemented.
 * </p>
 */
public interface IInvocationContext {
	/**
	 * @return Returns the offset of the current selection
	 */
	int getSelectionOffset();

	/**
	 * @return Returns the length of the current selection
	 */
	int getSelectionLength();
	
	/**
	 * @return ITranslationUnit or null
	 */
	ITranslationUnit getTranslationUnit();
}
