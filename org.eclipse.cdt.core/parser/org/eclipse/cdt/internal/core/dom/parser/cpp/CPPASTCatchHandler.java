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
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCatchHandler;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;

/**
 * @author jcamelon
 */
public class CPPASTCatchHandler extends CPPASTNode implements ICPPASTCatchHandler, IASTAmbiguityParent {

    private boolean isCatchAll;
    private IASTStatement body;
    private IASTDeclaration declaration;
    
    public CPPASTCatchHandler() {
	}

	public CPPASTCatchHandler(IASTDeclaration declaration, IASTStatement body) {
		setCatchBody(body);
		setDeclaration(declaration);
	}

	public void setIsCatchAll(boolean isEllipsis) {
        isCatchAll = isEllipsis;
    }

    public boolean isCatchAll() {
        return isCatchAll;
    }

    public void setCatchBody(IASTStatement compoundStatement) {
        body = compoundStatement;
        if (compoundStatement != null) {
			compoundStatement.setParent(this);
			compoundStatement.setPropertyInParent(CATCH_BODY);
		}
    }

    public IASTStatement getCatchBody() {
        return body;
    }

    public void setDeclaration(IASTDeclaration decl) {
        declaration = decl;
        if (decl != null) {
			decl.setParent(this);
			decl.setPropertyInParent(DECLARATION);
		}
    }

    public IASTDeclaration getDeclaration() {
        return declaration;
    }

    @Override
	public boolean accept( ASTVisitor action ){
        if( action.shouldVisitStatements ){
		    switch( action.visit( this ) ){
	            case ASTVisitor.PROCESS_ABORT : return false;
	            case ASTVisitor.PROCESS_SKIP  : return true;
	            default : break;
	        }
		}
        if( declaration != null ) if( !declaration.accept( action ) ) return false;
        if( body != null ) if( !body.accept( action ) ) return false;
        
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
        if( body == child )
        {
            other.setPropertyInParent( child.getPropertyInParent() );
            other.setParent( child.getParent() );
            body = (IASTStatement) other;
        }
        if( declaration == child )
        {
            other.setParent( child.getParent() );
            other.setPropertyInParent( child.getPropertyInParent() );
            declaration = (IASTDeclaration) other;
        }

    }
}
