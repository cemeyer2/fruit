/*******************************************************************************
 * Copyright (c) 2006, 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Markus Schorn - initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.core.index;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IMacroBinding;
import org.eclipse.core.runtime.CoreException;

/**
 * Represents a macro stored in the index. 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will work or
 * that it will remain the same. Please do not use this API without consulting
 * with the CDT team.
 * </p>
 * 
 * @since 4.0
 */
public interface IIndexMacro extends IMacroBinding, IIndexBinding {
	
	IIndexMacro[] EMPTY_INDEX_MACRO_ARRAY = new IIndexMacro[0];

	/**
	 * If available, return the file location for the macro definition of this macro,
	 * otherwise return <code>null</code>.
	 */
	IASTFileLocation getFileLocation() throws CoreException;
	
	/**
	 * Returns the file in which this macro is defined and belongs to.
	 * @throws CoreException 
	 */
	IIndexFile getFile() throws CoreException;

	/**
	 * Returns the name of the definition of this macro, or <code>null</code> if not available.
	 * @throws CoreException 
	 * @since 5.0
	 */
	IIndexName getDefinition() throws CoreException;
}
