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

public class ASTEnumeratorDefStmtNode extends ASTNode
{
    org.eclipse.photran.internal.core.lexer.Token label; // in ASTEnumeratorDefStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTEnumerator; // in ASTEnumeratorDefStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTColon; // in ASTEnumeratorDefStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTColon2; // in ASTEnumeratorDefStmtNode
    IASTListNode<ASTEnumeratorNode> enumeratorList; // in ASTEnumeratorDefStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTEos; // in ASTEnumeratorDefStmtNode

    public org.eclipse.photran.internal.core.lexer.Token getLabel()
    {
        return this.label;
    }

    public void setLabel(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.label = newValue;
    }


    public IASTListNode<ASTEnumeratorNode> getEnumeratorList()
    {
        return this.enumeratorList;
    }

    public void setEnumeratorList(IASTListNode<ASTEnumeratorNode> newValue)
    {
        this.enumeratorList = newValue;
    }


    public void accept(IASTVisitor visitor)
    {
        visitor.visitASTEnumeratorDefStmtNode(this);
        visitor.visitASTNode(this);
    }

    @Override protected int getNumASTFields()
    {
        return 6;
    }

    @Override protected IASTNode getASTField(int index)
    {
        switch (index)
        {
        case 0:  return this.label;
        case 1:  return this.hiddenTEnumerator;
        case 2:  return this.hiddenTColon;
        case 3:  return this.hiddenTColon2;
        case 4:  return this.enumeratorList;
        case 5:  return this.hiddenTEos;
        default: return null;
        }
    }

    @Override protected void setASTField(int index, IASTNode value)
    {
        switch (index)
        {
        case 0:  this.label = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 1:  this.hiddenTEnumerator = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 2:  this.hiddenTColon = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 3:  this.hiddenTColon2 = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 4:  this.enumeratorList = (IASTListNode<ASTEnumeratorNode>)value; return;
        case 5:  this.hiddenTEos = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        default: throw new IllegalArgumentException("Invalid index");
        }
    }
}

