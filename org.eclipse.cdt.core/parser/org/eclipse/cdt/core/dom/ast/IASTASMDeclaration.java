/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Rational Software - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.core.dom.ast;

/**
 * ASM Statement as a Declaration.
 * 
 * @author jcamelon
 */
public interface IASTASMDeclaration extends IASTDeclaration {

	/**
	 * Get the assembly value.
	 * 
	 */
	public String getAssembly();

	/**
	 * Set the assembly value.
	 * 
	 * @param assembly
	 */
	public void setAssembly(String assembly);
}