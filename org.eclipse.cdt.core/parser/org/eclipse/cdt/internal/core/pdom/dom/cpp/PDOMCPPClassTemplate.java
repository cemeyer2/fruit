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
package org.eclipse.cdt.internal.core.pdom.dom.cpp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.IPDOMNode;
import org.eclipse.cdt.core.dom.IPDOMVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplatePartialSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateNonTypeParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateScope;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexFileSet;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.core.parser.util.ObjectMap;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPDeferredClassInstance;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPInternalTemplateInstantiator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPTemplates;
import org.eclipse.cdt.internal.core.index.IIndexCPPBindingConstants;
import org.eclipse.cdt.internal.core.index.IIndexScope;
import org.eclipse.cdt.internal.core.pdom.PDOM;
import org.eclipse.cdt.internal.core.pdom.db.PDOMNodeLinkedList;
import org.eclipse.cdt.internal.core.pdom.dom.BindingCollector;
import org.eclipse.cdt.internal.core.pdom.dom.PDOMNode;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Bryan Wilkinson
 */
public class PDOMCPPClassTemplate extends PDOMCPPClassType
		implements ICPPClassTemplate, ICPPInternalTemplateInstantiator, ICPPTemplateScope {
	
	private static final int PARAMETERS = PDOMCPPClassType.RECORD_SIZE + 0;
	private static final int INSTANCES = PDOMCPPClassType.RECORD_SIZE + 4;
	private static final int SPECIALIZATIONS = PDOMCPPClassType.RECORD_SIZE + 8;
	private static final int FIRST_PARTIAL = PDOMCPPClassType.RECORD_SIZE + 12;
	
	/**
	 * The size in bytes of a PDOMCPPClassTemplate record in the database.
	 */
	@SuppressWarnings("hiding")
	protected static final int RECORD_SIZE = PDOMCPPClassType.RECORD_SIZE + 16;
	
	private ICPPTemplateParameter[] params;  // Cached template parameters.
	
	public PDOMCPPClassTemplate(PDOM pdom, PDOMNode parent, ICPPClassTemplate template)	throws CoreException {
		super(pdom, parent, template);
	}

	public PDOMCPPClassTemplate(PDOM pdom, int bindingRecord) {
		super(pdom, bindingRecord);
	}

	@Override
	protected int getRecordSize() {
		return RECORD_SIZE;
	}

	@Override
	public int getNodeType() {
		return IIndexCPPBindingConstants.CPP_CLASS_TEMPLATE;
	}
	
	private static class TemplateParameterCollector implements IPDOMVisitor {
		private List<IPDOMNode> params = new ArrayList<IPDOMNode>();
		public boolean visit(IPDOMNode node) throws CoreException {
			if (node instanceof ICPPTemplateParameter)
				params.add(node);
			return false;
		}
		public void leave(IPDOMNode node) throws CoreException {
		}
		public ICPPTemplateParameter[] getTemplateParameters() {
			return params.toArray(new ICPPTemplateParameter[params.size()]);
		}
	}
	
	public ICPPTemplateParameter[] getTemplateParameters() {
		if (params == null) {
			try {
				PDOMNodeLinkedList list = new PDOMNodeLinkedList(pdom, record + PARAMETERS, getLinkageImpl());
				TemplateParameterCollector visitor = new TemplateParameterCollector();
				list.accept(visitor);
				params = visitor.getTemplateParameters();
			} catch (CoreException e) {
				CCorePlugin.log(e);
				params = ICPPTemplateParameter.EMPTY_TEMPLATE_PARAMETER_ARRAY;
			}
		}
		// Copy to a new array for safety.
		ICPPTemplateParameter[] result = new ICPPTemplateParameter[params.length];
		System.arraycopy(params, 0, result, 0, params.length);
		return result;
	}
	
	private PDOMCPPClassTemplatePartialSpecialization getFirstPartial() throws CoreException {
		int value = pdom.getDB().getInt(record + FIRST_PARTIAL);
		return value != 0 ? new PDOMCPPClassTemplatePartialSpecialization(pdom, value) : null;
	}
	
	public void addPartial(PDOMCPPClassTemplatePartialSpecialization partial) throws CoreException {
		PDOMCPPClassTemplatePartialSpecialization first = getFirstPartial();
		partial.setNextPartial(first);
		pdom.getDB().putInt(record + FIRST_PARTIAL, partial.getRecord());
	}
		
	public ICPPClassTemplatePartialSpecialization[] getPartialSpecializations() throws DOMException {
		try {
			ArrayList<PDOMCPPClassTemplatePartialSpecialization> partials =
				new ArrayList<PDOMCPPClassTemplatePartialSpecialization>();
			for (PDOMCPPClassTemplatePartialSpecialization partial = getFirstPartial();
					partial != null;
					partial = partial.getNextPartial()) {
				partials.add(partial);
			}
			
			return partials.toArray(new ICPPClassTemplatePartialSpecialization[partials.size()]);
		} catch (CoreException e) {
			CCorePlugin.log(e);
			return new ICPPClassTemplatePartialSpecialization[0];
		}
	}
	
	public ICPPTemplateDefinition getTemplateDefinition() throws DOMException {
		return null;
	}
	
	private class PDOMCPPTemplateScope implements ICPPTemplateScope, IIndexScope {
		public IBinding[] find(String name) throws DOMException {
			return CPPSemantics.findBindings(this, name, false);
		}

		public final IBinding getBinding(IASTName name, boolean resolve) throws DOMException {
			return getBinding(name, resolve, IIndexFileSet.EMPTY);
		}

		public final IBinding[] getBindings(IASTName name, boolean resolve, boolean prefix) throws DOMException {
			return getBindings(name, resolve, prefix, IIndexFileSet.EMPTY);
		}

		public IBinding getBinding(IASTName name, boolean resolve, IIndexFileSet fileSet)
				throws DOMException {
			try {
				BindingCollector visitor = new BindingCollector(getLinkageImpl(), name.toCharArray());
				PDOMNodeLinkedList list = new PDOMNodeLinkedList(pdom, record + PARAMETERS, getLinkageImpl());
				list.accept(visitor);
				
				return CPPSemantics.resolveAmbiguities(name, visitor.getBindings());
			} catch (CoreException e) {
				CCorePlugin.log(e);
			}
			return null;
		}
		
		public IBinding[] getBindings(IASTName name, boolean resolve, boolean prefixLookup, IIndexFileSet fileSet)
				throws DOMException {
			IBinding[] result = null;
			try {
				BindingCollector visitor = new BindingCollector(getLinkageImpl(), name.toCharArray(), null,
						prefixLookup, !prefixLookup);
				PDOMNodeLinkedList list = new PDOMNodeLinkedList(pdom, record + PARAMETERS, getLinkageImpl());
				list.accept(visitor);
				result = (IBinding[]) ArrayUtil.addAll(IBinding.class, result, visitor.getBindings());
			} catch (CoreException e) {
				CCorePlugin.log(e);
			}
			return (IBinding[]) ArrayUtil.trim(IBinding.class, result);
		}
		
		public IIndexScope getParent() {
			return PDOMCPPClassTemplate.this.getParent();
		}
		
		public ICPPTemplateDefinition getTemplateDefinition()
				throws DOMException {
			return null;
		}

		public IIndexName getScopeName() {
			return null;
		}

		public IIndexBinding getScopeBinding() {
			return PDOMCPPClassTemplate.this;
		}
	}
	
	private PDOMCPPTemplateScope scope;
	
	@Override
	public IIndexScope getScope() {
		if (scope == null) {
			scope = new PDOMCPPTemplateScope();
		}
		return scope;
	}
	
	@Override
	public void accept(IPDOMVisitor visitor) throws CoreException {
		super.accept(visitor);
		PDOMNodeLinkedList list = new PDOMNodeLinkedList(pdom, record + PARAMETERS, getLinkageImpl());
		list.accept(visitor);
		list = new PDOMNodeLinkedList(pdom, record + INSTANCES, getLinkageImpl());
		list.accept(visitor);
	}
	
	public void specializationsAccept(IPDOMVisitor visitor) throws CoreException {
		PDOMNodeLinkedList list= new PDOMNodeLinkedList(pdom, record + SPECIALIZATIONS, getLinkageImpl());
		list.accept(visitor);
	}

	@Override
	public void addMember(PDOMNode member) throws CoreException {
		if (member instanceof ICPPTemplateParameter) {
			PDOMNodeLinkedList list = new PDOMNodeLinkedList(pdom, record + PARAMETERS, getLinkageImpl());
			list.addMember(member);
		} else if (member instanceof ICPPTemplateInstance) {
			PDOMNodeLinkedList list = new PDOMNodeLinkedList(pdom, record + INSTANCES, getLinkageImpl());
			list.addMember(member);
		} else if (member instanceof ICPPSpecialization) {
			if (this.equals(((ICPPSpecialization)member).getSpecializedBinding())) {
				PDOMNodeLinkedList list = new PDOMNodeLinkedList(pdom, record + SPECIALIZATIONS, getLinkageImpl());
				list.addMember(member);
			} else {
				super.addMember(member);
			}
		} else {
			super.addMember(member);
		}
	}

	public ICPPSpecialization deferredInstance(ObjectMap argMap, IType[] arguments) {
		ICPPSpecialization instance = getInstance(arguments);
		if (instance == null) {
			instance = new CPPDeferredClassInstance(this, argMap, arguments);
		}
		return instance;
	}

	private static class InstanceFinder implements IPDOMVisitor {
		private ICPPSpecialization instance = null;
		private IType[] arguments;
		
		public InstanceFinder(IType[] arguments) {
			this.arguments = arguments;
		}
		
		public boolean visit(IPDOMNode node) throws CoreException {
			if (instance == null && node instanceof PDOMCPPSpecialization) {
				PDOMCPPSpecialization spec = (PDOMCPPSpecialization) node;
				if (spec.matchesArguments(arguments)) {
					instance = spec;
				}
			}
			return false;
		}
		public void leave(IPDOMNode node) throws CoreException {
		}
		public ICPPSpecialization getInstance() {
			return instance;
		}
	}
	
	public ICPPSpecialization getInstance(IType[] arguments) {
		try {
			InstanceFinder visitor = new InstanceFinder(arguments);
			
			PDOMNodeLinkedList list = new PDOMNodeLinkedList(pdom, record + INSTANCES, getLinkageImpl());
			list.accept(visitor);
			
			if (visitor.getInstance() == null) {
				list = new PDOMNodeLinkedList(pdom, record + SPECIALIZATIONS, getLinkageImpl());
				list.accept(visitor);
			}
			
			return visitor.getInstance();
		} catch (CoreException e) {
			CCorePlugin.log(e);
		}
		return null;
	}

	public IBinding instantiate(IType[] arguments) {
		ICPPTemplateDefinition template = null;
		try {
			template = CPPTemplates.matchTemplatePartialSpecialization(this, arguments);
		} catch (DOMException e) {
			return e.getProblem();
		}
		
		if (template instanceof IProblemBinding)
			return template;
		if (template != null && template instanceof ICPPClassTemplatePartialSpecialization) {
			return ((PDOMCPPClassTemplate) template).instantiate(arguments);	
		}
		
		return CPPTemplates.instantiateTemplate(this, arguments, null);
	}
	
	@Override
	public boolean isSameType(IType type) {
		if (type instanceof ITypedef) {
			return type.isSameType(this);
		}
		
		if (type instanceof PDOMNode) {
			PDOMNode node= (PDOMNode) type;
			if (node.getPDOM() == getPDOM()) {
				return node.getRecord() == getRecord();
			}
		}

		try {
			if (type instanceof ICPPClassTemplate  && !(type instanceof ProblemBinding)) {
				boolean same= !(type instanceof ICPPClassTemplatePartialSpecialization);
				ICPPClassType ctype= (ICPPClassType) type;
				try {
					if (same && ctype.getKey() == getKey()) {
						char[][] qname= ctype.getQualifiedNameCharArray();
						same= hasQualifiedName(qname, qname.length - 1);
					}
				} catch (DOMException e) {
					CCorePlugin.log(e);
				}
				if (!same)
					return false;
				
				ICPPTemplateParameter[] params= getTemplateParameters();
				ICPPTemplateParameter[] oparams= ((ICPPClassTemplate) type).getTemplateParameters();
				
				if (params == null && oparams == null)
					return true;
				
				if (params == null || oparams == null)
					return false;
				
				if (params.length != oparams.length)
					return false;
				
				for (int i = 0; same && i < params.length; i++) {
					ICPPTemplateParameter p= params[i];
					ICPPTemplateParameter op= oparams[i];
					if (p instanceof IType && op instanceof IType) {
						same &= (((IType)p).isSameType((IType)op));
					} else {
						if(p instanceof ICPPTemplateNonTypeParameter
						&& op instanceof ICPPTemplateNonTypeParameter) {
							IType pt= ((ICPPTemplateNonTypeParameter)p).getType();
							IType opt= ((ICPPTemplateNonTypeParameter)op).getType();
							return pt!=null && opt!=null ? pt.isSameType(opt) : pt == opt;
						}
						return false;
					}
				}
				
				return same;
			}
		} catch (DOMException e) {
			CCorePlugin.log(e);
			return false;
		}
		
		return false;
	}
}
