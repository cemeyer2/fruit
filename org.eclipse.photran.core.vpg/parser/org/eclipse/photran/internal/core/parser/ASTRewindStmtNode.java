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

public class ASTRewindStmtNode extends ASTNode implements IActionStmt
{
    org.eclipse.photran.internal.core.lexer.Token label; // in ASTRewindStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTRewind; // in ASTRewindStmtNode
    ASTUnitIdentifierNode unitIdentifier; // in ASTRewindStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTLparen; // in ASTRewindStmtNode
    IASTListNode<ASTPositionSpecListNode> positionSpecList; // in ASTRewindStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTRparen; // in ASTRewindStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTEos; // in ASTRewindStmtNode

    public org.eclipse.photran.internal.core.lexer.Token getLabel()
    {
        return this.label;
    }

    public void setLabel(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.label = newValue;
    }


    public ASTUnitIdentifierNode getUnitIdentifier()
    {
        return this.unitIdentifier;
    }

    public void setUnitIdentifier(ASTUnitIdentifierNode newValue)
    {
        this.unitIdentifier = newValue;
    }


    public IASTListNode<ASTPositionSpecListNode> getPositionSpecList()
    {
        return this.positionSpecList;
    }

    public void setPositionSpecList(IASTListNode<ASTPositionSpecListNode> newValue)
    {
        this.positionSpecList = newValue;
    }


    public void accept(IASTVisitor visitor)
    {
        visitor.visitASTRewindStmtNode(this);
        visitor.visitIActionStmt(this);
        visitor.visitASTNode(this);
    }

    @Override protected int getNumASTFields()
    {
        return 7;
    }

    @Override protected IASTNode getASTField(int index)
    {
        switch (index)
        {
        case 0:  return this.label;
        case 1:  return this.hiddenTRewind;
        case 2:  return this.unitIdentifier;
        case 3:  return this.hiddenTLparen;
        case 4:  return this.positionSpecList;
        case 5:  return this.hiddenTRparen;
        case 6:  return this.hiddenTEos;
        default: return null;
        }
    }

    @Override protected void setASTField(int index, IASTNode value)
    {
        switch (index)
        {
        case 0:  this.label = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 1:  this.hiddenTRewind = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 2:  this.unitIdentifier = (ASTUnitIdentifierNode)value; return;
        case 3:  this.hiddenTLparen = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 4:  this.positionSpecList = (IASTListNode<ASTPositionSpecListNode>)value; return;
        case 5:  this.hiddenTRparen = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 6:  this.hiddenTEos = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        default: throw new IllegalArgumentException("Invalid index");
        }
    }
}

