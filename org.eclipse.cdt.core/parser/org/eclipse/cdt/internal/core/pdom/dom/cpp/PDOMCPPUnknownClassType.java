/*******************************************************************************
 * Copyright (c) 2008 Google, Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sergey Prigogin (Google) - initial API and implementation
 *    Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.internal.core.pdom.dom.cpp;

import org.eclipse.cdt.core.dom.IPDOMVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IField;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPScope;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexFileSet;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;
import org.eclipse.cdt.core.parser.util.ObjectMap;
import org.eclipse.cdt.internal.core.Util;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPUnknownClass;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPDeferredClassInstance;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownClassInstance;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownClassType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics;
import org.eclipse.cdt.internal.core.index.IIndexCPPBindingConstants;
import org.eclipse.cdt.internal.core.index.IIndexScope;
import org.eclipse.cdt.internal.core.index.IIndexType;
import org.eclipse.cdt.internal.core.pdom.PDOM;
import org.eclipse.cdt.internal.core.pdom.db.PDOMNodeLinkedList;
import org.eclipse.cdt.internal.core.pdom.dom.IPDOMMemberOwner;
import org.eclipse.cdt.internal.core.pdom.dom.PDOMLinkage;
import org.eclipse.cdt.internal.core.pdom.dom.PDOMNode;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Sergey Prigogin
 */
class PDOMCPPUnknownClassType extends PDOMCPPBinding implements ICPPClassScope, ICPPUnknownClassType,
		IPDOMMemberOwner, IIndexType, IIndexScope {

	private static final int KEY = PDOMCPPBinding.RECORD_SIZE + 0; // byte
	private static final int MEMBERLIST = PDOMCPPBinding.RECORD_SIZE + 4;
	@SuppressWarnings("hiding")
	protected static final int RECORD_SIZE = PDOMCPPBinding.RECORD_SIZE + 8;
	
	private ICPPScope unknownScope;

	public PDOMCPPUnknownClassType(PDOM pdom, PDOMNode parent, ICPPUnknownClassType classType)
			throws CoreException {
		super(pdom, parent, classType.getNameCharArray());

		setKind(classType);
		// linked list is initialized by storage being zero'd by malloc
	}

	public PDOMCPPUnknownClassType(PDOM pdom, int bindingRecord) {
		super(pdom, bindingRecord);
	}

	@Override
	public void update(PDOMLinkage linkage, IBinding newBinding) throws CoreException {
		if (newBinding instanceof ICPPClassType) {
			ICPPClassType ct= (ICPPClassType) newBinding;
			setKind(ct);
			super.update(linkage, newBinding);
		}
	}

	private void setKind(ICPPClassType ct) throws CoreException {
		try {
			pdom.getDB().putByte(record + KEY, (byte) ct.getKey());
		} catch (DOMException e) {
			throw new CoreException(Util.createStatus(e));
		}
	}
	
	public void addMember(PDOMNode member) throws CoreException {
		PDOMNodeLinkedList list = new PDOMNodeLinkedList(pdom, record + MEMBERLIST, getLinkageImpl());
		list.addMember(member);
	}

	@Override
	protected int getRecordSize() {
		return RECORD_SIZE;
	}

	@Override
	public int getNodeType() {
		return IIndexCPPBindingConstants.CPP_UNKNOWN_CLASS_TYPE;
	}
	
	@Override
	public void accept(IPDOMVisitor visitor) throws CoreException {
		super.accept(visitor);
		PDOMNodeLinkedList list = new PDOMNodeLinkedList(pdom, record + MEMBERLIST, getLinkageImpl());
		list.accept(visitor);
	}

	public ICPPMethod[] getImplicitMethods() {
		return ICPPMethod.EMPTY_CPPMETHOD_ARRAY;
	}

	public IScope getCompositeScope() throws DOMException {
		return this;
	}

    public ICPPScope getUnknownScope() {
    	if (unknownScope == null) {
    		unknownScope= new PDOMCPPUnknownScope(this, getUnknownName());
    	}
    	return unknownScope;
    }
    
	public IIndexBinding getScopeBinding() {
		return this;
	}

	@Override
	public boolean isGloballyQualified() throws DOMException {
		try {
			return getParentNode() instanceof PDOMLinkage;
		} catch (CoreException e) {
			return true;
		}
	}

	public ICPPClassType getClassType() {
		return this;
	}

	@Override
	public void addChild(PDOMNode member) throws CoreException {
		addMember(member);
	}

	@Override
	public IBinding getBinding(IASTName name, boolean resolve, IIndexFileSet fileSet) throws DOMException {
		return null;
	}
	
	@Override
	public IBinding[] getBindings(IASTName name, boolean resolve, boolean prefixLookup, IIndexFileSet fileSet)
			throws DOMException {
		return IBinding.EMPTY_BINDING_ARRAY;
	}
	
	public IBinding[] find(String name) throws DOMException {
		return CPPSemantics.findBindings(this, name, false);
	}
	
	// Not implemented

	@Override
	public Object clone() { fail(); return null; }
	public IField findField(String name) throws DOMException { fail(); return null; }

	@Override
	public boolean mayHaveChildren() {
		return true;
	}

	/* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType#getBases()
     */
    public ICPPBase[] getBases() {
        return ICPPBase.EMPTY_BASE_ARRAY;
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.ICompositeType#getFields()
     */
    public IField[] getFields() {
        return IField.EMPTY_FIELD_ARRAY;
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType#getDeclaredFields()
     */
    public ICPPField[] getDeclaredFields() {
        return ICPPField.EMPTY_CPPFIELD_ARRAY;
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType#getMethods()
     */
    public ICPPMethod[] getMethods() {
        return ICPPMethod.EMPTY_CPPMETHOD_ARRAY;
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType#getAllDeclaredMethods()
     */
    public ICPPMethod[] getAllDeclaredMethods() {
        return ICPPMethod.EMPTY_CPPMETHOD_ARRAY;
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType#getDeclaredMethods()
     */
    public ICPPMethod[] getDeclaredMethods() {
        return ICPPMethod.EMPTY_CPPMETHOD_ARRAY;
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType#getConstructors()
     */
    public ICPPConstructor[] getConstructors() {
        return ICPPConstructor.EMPTY_CONSTRUCTOR_ARRAY;
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType#getFriends()
     */
    public IBinding[] getFriends() {
        return IBinding.EMPTY_BINDING_ARRAY;
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.ICompositeType#getKey()
     */
    public int getKey() {
        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.core.dom.ast.IType#isSameType(org.eclipse.cdt.core.dom.ast.IType)
     */
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
		
		if (type instanceof ICPPUnknownClassType 
				&& type instanceof ICPPUnknownClassInstance == false
				&& type instanceof ICPPDeferredClassInstance == false) {
			ICPPUnknownClassType rhs= (ICPPUnknownClassType) type;
			if (CharArrayUtils.equals(getNameCharArray(), rhs.getNameCharArray())) {
				final ICPPUnknownBinding lhsContainer = getUnknownContainerBinding();
				final ICPPUnknownBinding rhsContainer = rhs.getUnknownContainerBinding();
				if (lhsContainer instanceof IType && rhsContainer instanceof IType) {
					return ((IType)lhsContainer).isSameType((IType) rhsContainer);
				}
			}
		}
		return false;
    }

	public ICPPClassType[] getNestedClasses() {
		return ICPPClassType.EMPTY_CLASS_ARRAY;
	}
	
	public IBinding resolvePartially(ICPPUnknownBinding parentBinding,	ObjectMap argMap, ICPPScope instantiationScope) {
		if (parentBinding instanceof PDOMNode && isChildOf((PDOMNode) parentBinding)) {
			return this;
		}
		return new CPPUnknownClass(parentBinding, getUnknownName());
	}

	public IASTName getUnknownName() {
		return new CPPASTName(getNameCharArray());
	}
	
	public ICPPUnknownBinding getUnknownContainerBinding() {
		try {
			return (ICPPUnknownBinding) getParentBinding();
		} catch (CoreException e) {
			return null;
		}
	}
}
