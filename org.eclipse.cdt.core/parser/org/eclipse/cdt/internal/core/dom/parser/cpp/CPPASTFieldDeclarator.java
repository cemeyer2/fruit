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
import org.eclipse.cdt.core.dom.ast.IASTFieldDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;

/**
 * @author jcamelon
 */
public class CPPASTFieldDeclarator extends CPPASTDeclarator implements
        IASTFieldDeclarator, IASTAmbiguityParent {

    private IASTExpression bitField;

    
    public CPPASTFieldDeclarator() {
	}

	public CPPASTFieldDeclarator(IASTName name, IASTExpression bitField) {
		super(name);
		setBitFieldSize(bitField);
	}

	public IASTExpression getBitFieldSize() {
        return bitField;
    }

    public void setBitFieldSize(IASTExpression size) {
        this.bitField = size;
        if (size != null) {
			size.setParent(this);
			size.setPropertyInParent(FIELD_SIZE);
		}
    }

    @Override
	protected boolean postAccept( ASTVisitor action ){
        if( bitField != null ) if( !bitField.accept( action ) ) return false;
        
        IASTInitializer initializer = getInitializer();
        if( initializer != null ) if( !initializer.accept( action ) ) return false;
        return true;
    }

    public void replace(IASTNode child, IASTNode other) {
        if( child == bitField )
        {
            other.setPropertyInParent( child.getPropertyInParent() );
            other.setParent( child.getParent() );
            bitField  = (IASTExpression) other;
        }
        
    }

}
