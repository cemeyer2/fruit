/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.core.dom.ast;

/**
 * Represents a node location that is directly in the source file.
 * 
 * @author Doug Schaefer
 */
public interface IASTFileLocation extends IASTNodeLocation {

	/**
	 * The name of the file.
	 * 
	 * @return the name of the file
	 */
	public String getFileName();
    
	/**
	 * Returns the offset within the file where this location starts.
	 */
	public int getNodeOffset();

	/**
	 * Returns the length of this location in terms of characters.
	 */
	public int getNodeLength();

    /**
     * Get the starting line number. Locations obtained via the index do not have line numbers
     * and return <code>0</code>.
     * 
     * @return int representing line number or <code>0</code> if not applicable
     */
    public int getStartingLineNumber();
    
    /**
     * Get the ending line number. Locations obtained via the index do not have line numbers
     * and return <code>0</code>.
     * 
     * @return int representing line number or <code>0</code> if not applicable
     */
    public int getEndingLineNumber();
}
