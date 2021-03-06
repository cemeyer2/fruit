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

public class ASTUseStmtNode extends ASTNode implements ISpecificationPartConstruct
{
    org.eclipse.photran.internal.core.lexer.Token label; // in ASTUseStmtNode
    org.eclipse.photran.internal.core.lexer.Token useToken; // in ASTUseStmtNode
    org.eclipse.photran.internal.core.lexer.Token name; // in ASTUseStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTComma; // in ASTUseStmtNode
    IASTListNode<ASTRenameNode> renameList; // in ASTUseStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTOnly; // in ASTUseStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTColon; // in ASTUseStmtNode
    IASTListNode<ASTOnlyNode> onlyList; // in ASTUseStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTEos; // in ASTUseStmtNode

    public org.eclipse.photran.internal.core.lexer.Token getLabel()
    {
        return this.label;
    }

    public void setLabel(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.label = newValue;
    }


    public org.eclipse.photran.internal.core.lexer.Token getUseToken()
    {
        return this.useToken;
    }

    public void setUseToken(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.useToken = newValue;
    }


    public org.eclipse.photran.internal.core.lexer.Token getName()
    {
        return this.name;
    }

    public void setName(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.name = newValue;
    }


    public IASTListNode<ASTRenameNode> getRenameList()
    {
        return this.renameList;
    }

    public void setRenameList(IASTListNode<ASTRenameNode> newValue)
    {
        this.renameList = newValue;
    }


    public IASTListNode<ASTOnlyNode> getOnlyList()
    {
        return this.onlyList;
    }

    public void setOnlyList(IASTListNode<ASTOnlyNode> newValue)
    {
        this.onlyList = newValue;
    }


    public void accept(IASTVisitor visitor)
    {
        visitor.visitASTUseStmtNode(this);
        visitor.visitISpecificationPartConstruct(this);
        visitor.visitASTNode(this);
    }

    @Override protected int getNumASTFields()
    {
        return 9;
    }

    @Override protected IASTNode getASTField(int index)
    {
        switch (index)
        {
        case 0:  return this.label;
        case 1:  return this.useToken;
        case 2:  return this.name;
        case 3:  return this.hiddenTComma;
        case 4:  return this.renameList;
        case 5:  return this.hiddenTOnly;
        case 6:  return this.hiddenTColon;
        case 7:  return this.onlyList;
        case 8:  return this.hiddenTEos;
        default: return null;
        }
    }

    @Override protected void setASTField(int index, IASTNode value)
    {
        switch (index)
        {
        case 0:  this.label = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 1:  this.useToken = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 2:  this.name = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 3:  this.hiddenTComma = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 4:  this.renameList = (IASTListNode<ASTRenameNode>)value; return;
        case 5:  this.hiddenTOnly = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 6:  this.hiddenTColon = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 7:  this.onlyList = (IASTListNode<ASTOnlyNode>)value; return;
        case 8:  this.hiddenTEos = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        default: throw new IllegalArgumentException("Invalid index");
        }
    }
}

