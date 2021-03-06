/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
 * This is a case in a switch statement. Note that in the grammar, a statement
 * is part of the clause. For the AST, just go on to the next statement to find
 * it. It's really only there to ensure that there is at least one statement
 * following this clause.
 * 
 * @author Doug Schaefer
 */
public interface IASTCaseStatement extends IASTStatement {

	/**
	 * <code>ASTNodeProperty</code> that represents the relationship between a
	 * case statement and the expression it contains.
	 */
	public static final ASTNodeProperty EXPRESSION = new ASTNodeProperty(
			"IASTCaseStatement.EXPRESSION - expression for case statement"); //$NON-NLS-1$

	/**
	 * The expression that determines whether this case should be taken.
	 * 
	 */
	public IASTExpression getExpression();

	/**
	 * Set the expression.
	 * 
	 * @param expression
	 */
	public void setExpression(IASTExpression expression);

}
