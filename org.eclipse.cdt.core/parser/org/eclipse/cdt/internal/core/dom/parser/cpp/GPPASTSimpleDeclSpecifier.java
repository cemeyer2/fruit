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
import org.eclipse.cdt.core.dom.ast.gnu.cpp.IGPPASTSimpleDeclSpecifier;

/**
 * @author jcamelon
 */
public class GPPASTSimpleDeclSpecifier extends CPPASTSimpleDeclSpecifier
        implements IGPPASTSimpleDeclSpecifier {

    private boolean longLong;
    private boolean restrict;
    private boolean complex=false;
    private boolean imaginary=false;
    private IASTExpression typeOfExpression;

    
    
    public GPPASTSimpleDeclSpecifier() {
	}

	public GPPASTSimpleDeclSpecifier(IASTExpression typeofExpression) {
		setTypeofExpression(typeofExpression);
	}

	public boolean isLongLong() {
        return longLong;
    }

    public void setLongLong(boolean value) {
        longLong = value;
    }

    public boolean isRestrict() {
        return restrict;
    }

    public void setRestrict(boolean value) {
        restrict = value;
    }

    public void setTypeofExpression(IASTExpression typeofExpression) {
        typeOfExpression = typeofExpression;
        if (typeofExpression != null) {
			typeofExpression.setParent(this);
			typeofExpression.setPropertyInParent(TYPEOF_EXPRESSION);
		}
    }

    public IASTExpression getTypeofExpression() {
        return typeOfExpression;
    }

    @Override
	public boolean accept( ASTVisitor action ){
        if( action.shouldVisitDeclSpecifiers ){
		    switch( action.visit( this ) ){
	            case ASTVisitor.PROCESS_ABORT : return false;
	            case ASTVisitor.PROCESS_SKIP  : return true;
	            default : break;
	        }
		}
        if( typeOfExpression != null )
            if( !typeOfExpression.accept( action ) ) return false;
           
        if( action.shouldVisitDeclSpecifiers ){
		    switch( action.leave( this ) ){
	            case ASTVisitor.PROCESS_ABORT : return false;
	            case ASTVisitor.PROCESS_SKIP  : return true;
	            default : break;
	        }
		}
        return true;
    }

	public boolean isComplex() {
		return complex;
	}

	public void setComplex(boolean value) {
		this.complex = value;
	}

	public boolean isImaginary() {
		return imaginary;
	}

	public void setImaginary(boolean value) {
		this.imaginary = value;
	}
}
