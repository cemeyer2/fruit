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

public class ASTProcComponentDefStmtNode extends ASTNode implements IComponentDefStmt
{
    org.eclipse.photran.internal.core.lexer.Token label; // in ASTProcComponentDefStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTProcedure; // in ASTProcComponentDefStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTLparen; // in ASTProcComponentDefStmtNode
    ASTProcInterfaceNode procInterface; // in ASTProcComponentDefStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTComma; // in ASTProcComponentDefStmtNode
    IASTListNode<ASTProcComponentAttrSpecNode> procComponentAttrSpecList; // in ASTProcComponentDefStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTColon; // in ASTProcComponentDefStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTColon2; // in ASTProcComponentDefStmtNode
    IASTListNode<ASTProcDeclNode> procDeclList; // in ASTProcComponentDefStmtNode

    public org.eclipse.photran.internal.core.lexer.Token getLabel()
    {
        return this.label;
    }

    public void setLabel(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.label = newValue;
    }


    public ASTProcInterfaceNode getProcInterface()
    {
        return this.procInterface;
    }

    public void setProcInterface(ASTProcInterfaceNode newValue)
    {
        this.procInterface = newValue;
    }


    public IASTListNode<ASTProcComponentAttrSpecNode> getProcComponentAttrSpecList()
    {
        return this.procComponentAttrSpecList;
    }

    public void setProcComponentAttrSpecList(IASTListNode<ASTProcComponentAttrSpecNode> newValue)
    {
        this.procComponentAttrSpecList = newValue;
    }


    public IASTListNode<ASTProcDeclNode> getProcDeclList()
    {
        return this.procDeclList;
    }

    public void setProcDeclList(IASTListNode<ASTProcDeclNode> newValue)
    {
        this.procDeclList = newValue;
    }


    public void accept(IASTVisitor visitor)
    {
        visitor.visitASTProcComponentDefStmtNode(this);
        visitor.visitIComponentDefStmt(this);
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
        case 1:  return this.hiddenTProcedure;
        case 2:  return this.hiddenTLparen;
        case 3:  return this.procInterface;
        case 4:  return this.hiddenTComma;
        case 5:  return this.procComponentAttrSpecList;
        case 6:  return this.hiddenTColon;
        case 7:  return this.hiddenTColon2;
        case 8:  return this.procDeclList;
        default: return null;
        }
    }

    @Override protected void setASTField(int index, IASTNode value)
    {
        switch (index)
        {
        case 0:  this.label = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 1:  this.hiddenTProcedure = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 2:  this.hiddenTLparen = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 3:  this.procInterface = (ASTProcInterfaceNode)value; return;
        case 4:  this.hiddenTComma = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 5:  this.procComponentAttrSpecList = (IASTListNode<ASTProcComponentAttrSpecNode>)value; return;
        case 6:  this.hiddenTColon = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 7:  this.hiddenTColon2 = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 8:  this.procDeclList = (IASTListNode<ASTProcDeclNode>)value; return;
        default: throw new IllegalArgumentException("Invalid index");
        }
    }
}

