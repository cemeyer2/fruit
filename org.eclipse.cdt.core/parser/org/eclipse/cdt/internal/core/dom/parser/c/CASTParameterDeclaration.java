/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Rational Software - Initial API and implementation
 * Yuan Zhang / Beth Tibbitts (IBM Research)
 *******************************************************************************/
package org.eclipse.cdt.internal.core.dom.parser.c;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;

/**
 * @author jcamelon
 */
public class CASTParameterDeclaration extends CASTNode implements
        IASTParameterDeclaration {

    private IASTDeclSpecifier declSpec;
    private IASTDeclarator declarator;

    
    public CASTParameterDeclaration() {
	}

	public CASTParameterDeclaration(IASTDeclSpecifier declSpec, IASTDeclarator declarator) {
		setDeclSpecifier(declSpec);
		setDeclarator(declarator);
	}

	public IASTDeclSpecifier getDeclSpecifier() {
        return declSpec;
    }

    public IASTDeclarator getDeclarator() {
        return declarator;
    }

    public void setDeclSpecifier(IASTDeclSpecifier declSpec) {
        this.declSpec = declSpec;
        if (declSpec != null) {
			declSpec.setParent(this);
			declSpec.setPropertyInParent(DECL_SPECIFIER);
		}
    }

    public void setDeclarator(IASTDeclarator declarator) {
        this.declarator = declarator;
        if (declarator != null) {
			declarator.setParent(this);
			declarator.setPropertyInParent(DECLARATOR);
		}
    }

    @Override
	public boolean accept( ASTVisitor action ){
        if( action.shouldVisitParameterDeclarations ){
		    switch( action.visit( this ) ){
	            case ASTVisitor.PROCESS_ABORT : return false;
	            case ASTVisitor.PROCESS_SKIP  : return true;
	            default : break;
	        }
		}
        
        if( declSpec != null ) if( !declSpec.accept( action ) ) return false;
        if( declarator != null ) if( !declarator.accept( action ) ) return false;    
        if( action.shouldVisitParameterDeclarations ){
		    switch( action.leave( this ) ){
	            case ASTVisitor.PROCESS_ABORT : return false;
	            case ASTVisitor.PROCESS_SKIP  : return true;
	            default : break;
	        }
		}
        return true;
    }
}