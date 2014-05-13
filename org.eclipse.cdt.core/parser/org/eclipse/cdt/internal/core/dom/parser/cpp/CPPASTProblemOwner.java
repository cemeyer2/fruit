/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *    Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.internal.core.dom.parser.cpp;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemHolder;

/**
 * @author jcamelon
 */
abstract class CPPASTProblemOwner extends CPPASTNode implements IASTProblemHolder {

    private IASTProblem problem;

    
    public CPPASTProblemOwner() {
	}

	public CPPASTProblemOwner(IASTProblem problem) {
		setProblem(problem);
	}

	public IASTProblem getProblem() {
        return problem;
    }
    
    public void setProblem(IASTProblem p) {
        problem = p;
        if (p != null) {
			p.setParent(this);
			p.setPropertyInParent(PROBLEM);
		}
    }
    
    	@Override
	public boolean accept( ASTVisitor action ){
        if( action.shouldVisitProblems ){
		    switch( action.visit( getProblem() ) ){
	            case ASTVisitor.PROCESS_ABORT : return false;
	            case ASTVisitor.PROCESS_SKIP  : return true;
	            default : break;
	        }
		    switch( action.leave( getProblem() ) ){
	            case ASTVisitor.PROCESS_ABORT : return false;
	            case ASTVisitor.PROCESS_SKIP  : return true;
	            default : break;
	        }
		}
        return true;
    }
}
