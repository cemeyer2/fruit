/*******************************************************************************
 * Copyright (c) 2007 University of Illinois at Urbana-Champaign and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UIUC - Initial API and implementation
 *******************************************************************************/
package org.eclipse.photran.internal.core.parser;

import java.io.PrintStream;
import java.util.Iterator;

import java.util.List;

import org.eclipse.photran.internal.core.parser.Parser.ASTNode;
import org.eclipse.photran.internal.core.parser.Parser.ASTNodeWithErrorRecoverySymbols;
import org.eclipse.photran.internal.core.parser.Parser.IASTListNode;
import org.eclipse.photran.internal.core.parser.Parser.IASTNode;
import org.eclipse.photran.internal.core.parser.Parser.IASTVisitor;
import org.eclipse.photran.internal.core.lexer.Token;

import org.eclipse.photran.internal.core.lexer.*;                   import org.eclipse.photran.internal.core.analysis.binding.ScopingNode;

public class ASTComponentArraySpecNode extends ASTNode
{
    IASTListNode<ASTExplicitShapeSpecNode> explicitShapeSpecList; // in ASTComponentArraySpecNode
    IASTListNode<ASTDeferredShapeSpecListNode> deferredShapeSpecList; // in ASTComponentArraySpecNode

    public IASTListNode<ASTExplicitShapeSpecNode> getExplicitShapeSpecList()
    {
        return this.explicitShapeSpecList;
    }

    public void setExplicitShapeSpecList(IASTListNode<ASTExplicitShapeSpecNode> newValue)
    {
        this.explicitShapeSpecList = newValue;
    }


    public IASTListNode<ASTDeferredShapeSpecListNode> getDeferredShapeSpecList()
    {
        return this.deferredShapeSpecList;
    }

    public void setDeferredShapeSpecList(IASTListNode<ASTDeferredShapeSpecListNode> newValue)
    {
        this.deferredShapeSpecList = newValue;
    }


    public void accept(IASTVisitor visitor)
    {
        visitor.visitASTComponentArraySpecNode(this);
        visitor.visitASTNode(this);
    }

    @Override protected int getNumASTFields()
    {
        return 2;
    }

    @Override protected IASTNode getASTField(int index)
    {
        switch (index)
        {
        case 0:  return this.explicitShapeSpecList;
        case 1:  return this.deferredShapeSpecList;
        default: return null;
        }
    }

    @Override protected void setASTField(int index, IASTNode value)
    {
        switch (index)
        {
        case 0:  this.explicitShapeSpecList = (IASTListNode<ASTExplicitShapeSpecNode>)value; return;
        case 1:  this.deferredShapeSpecList = (IASTListNode<ASTDeferredShapeSpecListNode>)value; return;
        default: throw new IllegalArgumentException("Invalid index");
        }
    }
}

