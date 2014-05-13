/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Rational Software - Initial API and implementation
 * Markus Schorn (Wind River Systems)
 * Yuan Zhang / Beth Tibbitts (IBM Research)
 *******************************************************************************/
package org.eclipse.cdt.internal.core.dom.parser.c;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.parser.util.ArrayUtil;

/**
 * @author jcamelon
 */
public class CASTSimpleDeclaration extends CASTNode implements IASTSimpleDeclaration {


    public CASTSimpleDeclaration() {
	}

	public CASTSimpleDeclaration(IASTDeclSpecifier declSpecifier) {
		setDeclSpecifier(declSpecifier);
	}

	public IASTDeclSpecifier getDeclSpecifier() {
        return declSpecifier;
    }

    public IASTDeclarator[] getDeclarators() {
        if( declarators == null ) return IASTDeclarator.EMPTY_DECLARATOR_ARRAY;
        declarators = (IASTDeclarator[]) ArrayUtil.removeNullsAfter( IASTDeclarator.class, declarators, declaratorsPos );
        return declarators;
    }
    
    public void addDeclarator( IASTDeclarator d ) {
    	if (d != null) {
    		d.setParent(this);
			d.setPropertyInParent(DECLARATOR);
    		declarators = (IASTDeclarator[]) ArrayUtil.append( IASTDeclarator.class, declarators, ++declaratorsPos, d );    		
    	}
    }
    

    private IASTDeclarator [] declarators = null;
    private int declaratorsPos=-1;
    private IASTDeclSpecifier declSpecifier;


    public void setDeclSpecifier(IASTDeclSpecifier declSpecifier) {
        this.declSpecifier = declSpecifier;
        if (declSpecifier != null) {
			declSpecifier.setParent(this);
			declSpecifier.setPropertyInParent(DECL_SPECIFIER);
		}
    }
    
    @Override
	public boolean accept( ASTVisitor action ){
        if( action.shouldVisitDeclarations ){
		    switch( action.visit( this ) ){
	            case ASTVisitor.PROCESS_ABORT : return false;
	            case ASTVisitor.PROCESS_SKIP  : return true;
	            default : break;
	        }
		}
        
        if( declSpecifier != null ) if( !declSpecifier.accept( action ) ) return false;
        IASTDeclarator [] dtors = getDeclarators();
        for( int i = 0; i < dtors.length; i++ )
            if( !dtors[i].accept( action ) ) return false;
        
        if( action.shouldVisitDeclarations ){
		    switch( action.leave( this ) ){
	            case ASTVisitor.PROCESS_ABORT : return false;
	            case ASTVisitor.PROCESS_SKIP  : return true;
	            default : break;
	        }
		}
        return true;
    }
}