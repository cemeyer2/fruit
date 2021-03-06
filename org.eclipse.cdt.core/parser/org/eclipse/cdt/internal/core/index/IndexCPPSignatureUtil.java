/*******************************************************************************
 * Copyright (c) 2007, 2008 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    QNX - Initial API and implementation
 *    Andrew Ferguson (Symbian)
 *    Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.internal.core.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplatePartialSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.core.parser.util.ObjectMap;
import org.eclipse.core.runtime.CoreException;

/**
 * Determines the signatures and signature hashes for bindings that can have
 * siblings with the same name.
 * 
 * @author Bryan Wilkinson
 */
public class IndexCPPSignatureUtil {
	
	/**
	 * Returns the signature for the binding.  Returns an empty string if a
	 * signature is not required for the binding.
	 * 
	 * @param binding
	 * @return the signature or an empty string
	 * @throws CoreException
	 * @throws DOMException
	 */
	public static String getSignature(IBinding binding) throws CoreException, DOMException {
		StringBuffer buffer = new StringBuffer();
		if (binding instanceof ICPPTemplateInstance) {
			ICPPTemplateInstance inst = (ICPPTemplateInstance) binding;
			buffer.append(getTemplateArgString(inst.getArguments(), true));
		} else if (binding instanceof ICPPClassTemplatePartialSpecialization) {
			ICPPClassTemplatePartialSpecialization partial = (ICPPClassTemplatePartialSpecialization) binding;
			buffer.append(getTemplateArgString(partial.getArguments(), false));
		} else if (binding instanceof ICPPSpecialization) {
			ICPPSpecialization spec = (ICPPSpecialization) binding;
			if (!(spec instanceof ICPPTemplateDefinition)
					&& spec.getSpecializedBinding() instanceof ICPPTemplateDefinition) {
				ICPPTemplateDefinition template = (ICPPTemplateDefinition) spec.getSpecializedBinding();
				ICPPTemplateParameter[] params = template.getTemplateParameters();
				ObjectMap argMap = spec.getArgumentMap();
				IType[] args = new IType[params.length];
				for (int i = 0; i < params.length; i++) {
					args[i] = (IType) argMap.get(params[i]);
				}
				buffer.append(getTemplateArgString(args, false));	
			}
		} 
		
		if (binding instanceof IFunction) {
			IFunction function = (IFunction) binding;
			buffer.append(getFunctionParameterString((function.getType())));
		}
		
		return buffer.toString();
	}
	
	/**
	 * Constructs a string in the format:
	 *   <typeName1,typeName2,...>
	 * 
	 * @param types
	 * @param qualifyTemplateParameters
	 * @return
	 * @throws CoreException
	 * @throws DOMException
	 */
	private static String getTemplateArgString(IType[] types, boolean qualifyTemplateParameters) throws CoreException, DOMException {
		StringBuilder buffer = new StringBuilder();
		buffer.append('<');
		for (int i = 0; i < types.length; i++) {
			if (i>0) {
				buffer.append(',');
			}
			final IType type = types[i];
			if (qualifyTemplateParameters && type instanceof ICPPTemplateParameter) {
				List<IBinding> parents = new ArrayList<IBinding>();
				IScope parentScope= ((ICPPTemplateParameter) type).getScope();
				IBinding lastBinding= null;
				while (parentScope != null) {
					IBinding binding = getBindingForScope(parentScope);
					if (binding != null) {
						// template definitions are returned as binding for template scope and
						// the class/function scope.
						if (!binding.equals(lastBinding)) { 
							parents.add(lastBinding= binding);
						}
					}
					parentScope= parentScope.getParent();
				}
				//identical template parameters from different template specializations must have unique signatures
				Collections.reverse(parents);
				for (IBinding binding : parents) {
					if (binding != null) {
						buffer.append(binding.getNameCharArray());
						if (binding instanceof ICPPSpecialization) {
							ICPPSpecialization spec= (ICPPSpecialization) binding;
							appendTemplateArgs(spec.getArgumentMap().valueArray(), buffer);
						}
						buffer.append("::"); //$NON-NLS-1$
					}
				}
				buffer.append(((ICPPTemplateParameter)type).getName());
			} else {
				buffer.append(ASTTypeUtil.getType(type));
			}
		}
		buffer.append('>');
		return buffer.toString();
	}

	private static IBinding getBindingForScope(IScope parentScope) throws DOMException {
		IBinding binding= null;
		if (parentScope instanceof IIndexScope) {
			binding= ((IIndexScope)parentScope).getScopeBinding();
		}
		else {
			final IName scopeName = parentScope.getScopeName();
			if (scopeName instanceof IASTName) {
				binding= ((IASTName) scopeName).resolveBinding();
			}
		}
		return binding;
	}
	
	private static void appendTemplateArgs(Object[] values, StringBuilder buffer) {
		boolean needcomma= false;
		buffer.append('<');
		for (final Object val : values) {
			if (val instanceof IType) {
				if (needcomma)
					buffer.append(',');
				needcomma= true;
				buffer.append(ASTTypeUtil.getType((IType) val));
			}
		}
		buffer.append('>');
	}

	/**
	 * Constructs a string in the format:
	 *   (paramName1,paramName2,...)
	 * 
	 * @param fType
	 * @return
	 * @throws DOMException
	 */
	private static String getFunctionParameterString(IFunctionType fType) throws DOMException {
		IType[] types = fType.getParameterTypes();
		if(types.length==1) {
			if(types[0] instanceof IBasicType) {
				if(((IBasicType)types[0]).getType()==IBasicType.t_void) {
					types = new IType[0];
				}
			}
		}
		StringBuffer result = new StringBuffer();
		result.append('(');
		for(int i=0; i<types.length; i++) {
			if (i>0) {
				result.append(',');
			}
			result.append(ASTTypeUtil.getType(types[i]));
		}
		result.append(')');
		return result.toString();
	}
	
	/**
	 * Gets the signature hash for the passed binding.
	 * 
	 * @param binding
	 * @return the hash code of the binding's signature string
	 * @throws CoreException
	 * @throws DOMException
	 */
	public static Integer getSignatureHash(IBinding binding) throws CoreException, DOMException {
		String sig = getSignature(binding);
		return sig.length() == 0 ? null : new Integer(sig.hashCode());
	}

	/**
	 * @return compares two bindings for signature information. Signature information covers
	 * function signatures, or template specialization/instance arguments.
	 * @param a
	 * @param b
	 */
	public static int compareSignatures(IBinding a, IBinding b) {
		try {
			int siga= getSignature(a).hashCode();
			int sigb= getSignature(b).hashCode();
			return siga<sigb ? -1 : (siga>sigb ? 1 : 0);
		} catch(CoreException ce) {
			CCorePlugin.log(ce);
		} catch(DOMException de) {
			CCorePlugin.log(de);
		}
		return 0;
	}
}
