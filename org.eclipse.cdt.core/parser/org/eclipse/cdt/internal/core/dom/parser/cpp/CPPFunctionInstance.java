/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
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

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.core.parser.util.ObjectMap;
import org.eclipse.cdt.internal.core.dom.parser.ASTInternal;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPTemplates;

/**
 * @author aniefer
 */
public class CPPFunctionInstance extends CPPInstance implements ICPPFunction, ICPPInternalFunction {
	private IFunctionType type = null;
	private IParameter [] parameters = null;
	/**
	 * @param scope
	 * @param orig
	 * @param argMap
	 * @param args
	 */
	public CPPFunctionInstance(ICPPScope scope, IBinding orig, ObjectMap argMap, IType[] args) {
		super(scope, orig, argMap, args);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.IFunction#getParameters()
	 */
	public IParameter[] getParameters() throws DOMException {
		if( parameters == null ){
			IParameter [] params = ((ICPPFunction)getTemplateDefinition()).getParameters();
			parameters = new IParameter[ params.length ];
			for (int i = 0; i < params.length; i++) {
				parameters[i] = new CPPParameterSpecialization( (ICPPParameter)params[i], null, getArgumentMap() );
			}
		}
		
		return parameters;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.IFunction#getFunctionScope()
	 */
	public IScope getFunctionScope() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.IFunction#getType()
	 */
	public IFunctionType getType() throws DOMException {
		if( type == null ){
			type = (IFunctionType) CPPTemplates.instantiateType( ((ICPPFunction)getTemplateDefinition()).getType(), getArgumentMap(), getScope() );
		}
		return type;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.IFunction#isStatic()
	 */
	public boolean isStatic() throws DOMException {
		return ((ICPPFunction)getTemplateDefinition()).isStatic();
	}

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction#isMutable()
     */
    public boolean isMutable() throws DOMException {
        return ((ICPPFunction)getTemplateDefinition()).isMutable();
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction#isInline()
     */
    public boolean isInline() throws DOMException {
        return ((ICPPFunction)getTemplateDefinition()).isInline();
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction#isInline()
     */
    public boolean isExternC() throws DOMException {
        return ((ICPPFunction)getTemplateDefinition()).isExternC();
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.IFunction#isExtern()
     */
    public boolean isExtern() throws DOMException {
        return ((ICPPFunction)getTemplateDefinition()).isExtern();
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.IFunction#isAuto()
     */
    public boolean isAuto() throws DOMException {
        return ((ICPPFunction)getTemplateDefinition()).isAuto();
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.IFunction#isRegister()
     */
    public boolean isRegister() throws DOMException {
        return ((ICPPFunction)getTemplateDefinition()).isRegister();
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.IFunction#takesVarArgs()
     */
    public boolean takesVarArgs() throws DOMException {
        return ((ICPPFunction)getTemplateDefinition()).takesVarArgs();
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPInternalFunction#isStatic(boolean)
     */
    public boolean isStatic( boolean resolveAll ) {
    	ICPPFunction func = (ICPPFunction) getTemplateDefinition();
    	try {
			return ASTInternal.isStatic(func, resolveAll);
		} catch (DOMException e) {
			return false;
		}
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPInternalFunction#resolveParameter(org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration)
     */
    public IBinding resolveParameter( IASTParameterDeclaration param ) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
	public boolean equals(Object obj) {
    	if( (obj instanceof ICPPTemplateInstance) && (obj instanceof ICPPFunction)){
    		try {
    			ICPPFunctionType ct1= (ICPPFunctionType) ((ICPPFunction)getSpecializedBinding()).getType();
    			ICPPFunctionType ct2= (ICPPFunctionType) ((ICPPFunction)((ICPPTemplateInstance)obj).getTemplateDefinition()).getType();
    			if(!ct1.isSameType(ct2))
    				return false;

    			ObjectMap m1 = getArgumentMap(), m2 = ((ICPPTemplateInstance)obj).getArgumentMap();
    			if( m1 == null || m2 == null || m1.size() != m2.size())
    				return false;
    			for( int i = 0; i < m1.size(); i++ ){
    				IType t1 = (IType) m1.getAt( i );
    				IType t2 = (IType) m2.getAt( i );
    				if(!CPPTemplates.isSameTemplateArgument(t1, t2))
    					return false;
    			}
    			return true;
    		} catch(DOMException de) {
    			CCorePlugin.log(de);
    		}
    	}

    	return false;
    }
}
