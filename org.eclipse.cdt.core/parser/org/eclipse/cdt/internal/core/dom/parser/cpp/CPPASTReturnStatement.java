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
package org.eclipse.cdt.internal.core.dom.parser.cpp;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;

/**
 * @author jcamelon
 */
public class CPPASTReturnStatement extends CPPASTNode implements IASTReturnStatement, IASTAmbiguityParent {
    
	private IASTExpression retValue;
    
    public CPPASTReturnStatement() {
	}

	public CPPASTReturnStatement(IASTExpression retValue) {
		setReturnValue(retValue);
	}

	public IASTExpression getReturnValue() {
        return retValue;
    }

    public void setReturnValue(IASTExpression returnValue) {
        retValue = returnValue;
        if (returnValue != null) {
			returnValue.setParent(this);
			returnValue.setPropertyInParent(RETURNVALUE);
		}
    }

    @Override
	public boolean accept(ASTVisitor action) {
        if (action.shouldVisitStatements) {
            switch (action.visit(this)) {
            case ASTVisitor.PROCESS_ABORT:
                return false;
            case ASTVisitor.PROCESS_SKIP:
                return true;
            default:
                break;
            }
        }
        if (retValue != null)
            if (!retValue.accept(action))
                return false;
        
        if( action.shouldVisitStatements ){
		    switch( action.leave( this ) ){
	            case ASTVisitor.PROCESS_ABORT : return false;
	            case ASTVisitor.PROCESS_SKIP  : return true;
	            default : break;
	        }
		}
        return true;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (child == retValue) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            retValue = (IASTExpression) other;
        }
    }
}
