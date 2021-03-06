/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.core.dom.parser.cpp;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBinding;

/**
 * @author aniefer
 */
public interface ICPPInternalBinding extends ICPPBinding {
    //methods required by the CPPVisitor but not meant for the public interface
    
    //implementors should keep the node with the lowest offset in declarations[0]
    IASTNode [] getDeclarations();
    IASTNode getDefinition();
    
	/**
	 * @param declarator
	 */
	void addDefinition( IASTNode node );
	void addDeclaration( IASTNode node );
	void removeDeclaration(IASTNode node);
}
