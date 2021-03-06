/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 * Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.internal.core.dom.parser.cpp;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateScope;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;

/**
 * @author jcamelon
 */
public class CPPASTTemplateDeclaration extends CPPASTNode implements
        ICPPASTTemplateDeclaration, IASTAmbiguityParent {

    private boolean exported;
    private IASTDeclaration declaration;
    private ICPPTemplateScope templateScope;

    
    public CPPASTTemplateDeclaration() {
	}

	public CPPASTTemplateDeclaration(IASTDeclaration declaration) {
		setDeclaration(declaration);
	}

	public boolean isExported() {
        return exported;
    }

    public void setExported(boolean value) {
        exported = value;
    }

    public IASTDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(IASTDeclaration declaration) {
        this.declaration = declaration;
        if (declaration != null) {
			declaration.setParent(this);
			declaration.setPropertyInParent(OWNED_DECLARATION);
		}
    }

    public ICPPASTTemplateParameter [] getTemplateParameters() {
        if( parameters == null ) return ICPPASTTemplateParameter.EMPTY_TEMPLATEPARAMETER_ARRAY;
        parameters = (ICPPASTTemplateParameter[]) ArrayUtil.removeNullsAfter( ICPPASTTemplateParameter.class, parameters, parametersPos );
        return parameters;
    }

    public void addTemplateParamter(ICPPASTTemplateParameter parm) {
    	if (parm != null) {
    		parameters = (ICPPASTTemplateParameter[]) ArrayUtil.append( ICPPASTTemplateParameter.class, parameters, ++parametersPos, parm );
    		parm.setParent(this);
			parm.setPropertyInParent(PARAMETER);
    	}
    }

    private ICPPASTTemplateParameter [] parameters = null;
    private int parametersPos=-1;
    @Override
	public boolean accept( ASTVisitor action ){
        if( action.shouldVisitDeclarations ){
		    switch( action.visit( this ) ){
	            case ASTVisitor.PROCESS_ABORT : return false;
	            case ASTVisitor.PROCESS_SKIP  : return true;
	            default : break;
	        }
		}
        
        ICPPASTTemplateParameter [] params = getTemplateParameters();
        for ( int i = 0; i < params.length; i++ ) {
            if( !params[i].accept( action ) ) return false;
        }
        
        if( declaration != null ) if( !declaration.accept( action ) ) return false;
        
        if( action.shouldVisitDeclarations ){
		    switch( action.leave( this ) ){
	            case ASTVisitor.PROCESS_ABORT : return false;
	            case ASTVisitor.PROCESS_SKIP  : return true;
	            default : break;
	        }
		}
        return true;
    }

	public ICPPTemplateScope getScope() {
		if( templateScope == null )
			templateScope = new CPPTemplateScope( this );
		return templateScope;
	}
    
    public void replace(IASTNode child, IASTNode other) {
        if( declaration == child )
        {
            other.setParent( child.getParent() );
            other.setPropertyInParent( child.getPropertyInParent() );
            declaration = (IASTDeclaration) other;
        }
    }
}
