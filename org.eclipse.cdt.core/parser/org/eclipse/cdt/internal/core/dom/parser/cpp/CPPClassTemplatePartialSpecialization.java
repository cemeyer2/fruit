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

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplatePartialSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.core.parser.util.ObjectMap;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPTemplates;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

/**
 * @author aniefer
 */
public class CPPClassTemplatePartialSpecialization extends CPPClassTemplate implements
		ICPPClassTemplatePartialSpecialization, ICPPSpecialization {

	private IType [] arguments;
	/**
	 * @param name
	 */
	public CPPClassTemplatePartialSpecialization(ICPPASTTemplateId name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateSpecialization#getArguments()
	 */
	public IType[] getArguments() {
		if( arguments == null ){
			ICPPASTTemplateId id= (ICPPASTTemplateId) getTemplateName();
			arguments = CPPTemplates.createTemplateArgumentArray(id);
		}
		return arguments;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplatePartialSpecialization#getPrimaryClassTemplate()
	 */
	public ICPPClassTemplate getPrimaryClassTemplate() {
		ICPPASTTemplateId id = (ICPPASTTemplateId) getTemplateName();
		return (ICPPClassTemplate) id.getTemplateName().resolveBinding();
	}

	@Override
	public IBinding instantiate( IType [] args ){
		args= SemanticUtil.getSimplifiedTypes(args);
		ICPPSpecialization instance = getInstance( args );
		if( instance != null ){
			return instance;
		}
		
		IType [] specArgs = getArguments();
		if( specArgs.length != args.length ){
			return null;
		}
		
		boolean argsContainDependentType= false;
		ObjectMap argMap = new ObjectMap( specArgs.length );
		int numSpecArgs = specArgs.length;
		for( int i = 0; i < numSpecArgs; i++ ){
			IType spec = specArgs[i];
			IType arg = args[i];
			
			argsContainDependentType= argsContainDependentType || CPPTemplates.isDependentType(arg);
			try {
				if( !CPPTemplates.deduceTemplateArgument( argMap,  spec, arg ) )
					return null;
			} catch (DOMException e) {
				return null;
			}
		}
		
		if (argsContainDependentType) {
			return deferredInstance( argMap, args );
		}
		
		ICPPTemplateParameter [] params = getTemplateParameters();
		int numParams = params.length;
		for( int i = 0; i < numParams; i++ ){
			if( params[i] instanceof IType && !argMap.containsKey( params[i] ) )
				return null;
		}
		
		instance = (ICPPTemplateInstance) CPPTemplates.createInstance( (ICPPScope) getScope(), this, argMap, args );
		addSpecialization( args, instance );
		
		return instance;
	}

	public IBinding getSpecializedBinding() {
		return getPrimaryClassTemplate();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization#getArgumentMap()
	 */
	public ObjectMap getArgumentMap() {
		IType[] arg= getArguments();
		ICPPTemplateParameter[] params;
		try {
			params = getPrimaryClassTemplate().getTemplateParameters();
		} catch (DOMException e) {
			return ObjectMap.EMPTY_MAP;
		}
		// lengths should be equal, be defensive
		final int len= Math.min(params.length, arg.length);
		ObjectMap map = new ObjectMap(len);
		for (int i = 0; i < len; i++) {
			map.put(params[i], arg[i]);
		}

		return map;
	}
}
