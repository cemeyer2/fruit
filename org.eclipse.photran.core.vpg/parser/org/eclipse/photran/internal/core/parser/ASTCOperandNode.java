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

public class ASTCOperandNode extends ASTNode
{
    ASTNameNode name; // in ASTCOperandNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTLparen; // in ASTCOperandNode
    IASTListNode<ASTFunctionArgListNode> functionArgList; // in ASTCOperandNode
    IASTListNode<ASTSectionSubscriptNode> primarySectionSubscriptList; // in ASTCOperandNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTRparen; // in ASTCOperandNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTPercent; // in ASTCOperandNode
    IASTListNode<ASTDataRefNode> derivedTypeComponentRef; // in ASTCOperandNode
    org.eclipse.photran.internal.core.lexer.Token hiddenLparen2; // in ASTCOperandNode
    IASTListNode<ASTSectionSubscriptNode> componentSectionSubscriptList; // in ASTCOperandNode
    org.eclipse.photran.internal.core.lexer.Token hiddenRparen2; // in ASTCOperandNode
    org.eclipse.photran.internal.core.lexer.Token stringConst; // in ASTCOperandNode

    public ASTNameNode getName()
    {
        return this.name;
    }

    public void setName(ASTNameNode newValue)
    {
        this.name = newValue;
    }


    public IASTListNode<ASTFunctionArgListNode> getFunctionArgList()
    {
        return this.functionArgList;
    }

    public void setFunctionArgList(IASTListNode<ASTFunctionArgListNode> newValue)
    {
        this.functionArgList = newValue;
    }


    public IASTListNode<ASTSectionSubscriptNode> getPrimarySectionSubscriptList()
    {
        return this.primarySectionSubscriptList;
    }

    public void setPrimarySectionSubscriptList(IASTListNode<ASTSectionSubscriptNode> newValue)
    {
        this.primarySectionSubscriptList = newValue;
    }


    public IASTListNode<ASTDataRefNode> getDerivedTypeComponentRef()
    {
        return this.derivedTypeComponentRef;
    }

    public void setDerivedTypeComponentRef(IASTListNode<ASTDataRefNode> newValue)
    {
        this.derivedTypeComponentRef = newValue;
    }


    public IASTListNode<ASTSectionSubscriptNode> getComponentSectionSubscriptList()
    {
        return this.componentSectionSubscriptList;
    }

    public void setComponentSectionSubscriptList(IASTListNode<ASTSectionSubscriptNode> newValue)
    {
        this.componentSectionSubscriptList = newValue;
    }


    public org.eclipse.photran.internal.core.lexer.Token getStringConst()
    {
        return this.stringConst;
    }

    public void setStringConst(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.stringConst = newValue;
    }


    public void accept(IASTVisitor visitor)
    {
        visitor.visitASTCOperandNode(this);
        visitor.visitASTNode(this);
    }

    @Override protected int getNumASTFields()
    {
        return 11;
    }

    @Override protected IASTNode getASTField(int index)
    {
        switch (index)
        {
        case 0:  return this.name;
        case 1:  return this.hiddenTLparen;
        case 2:  return this.functionArgList;
        case 3:  return this.primarySectionSubscriptList;
        case 4:  return this.hiddenTRparen;
        case 5:  return this.hiddenTPercent;
        case 6:  return this.derivedTypeComponentRef;
        case 7:  return this.hiddenLparen2;
        case 8:  return this.componentSectionSubscriptList;
        case 9:  return this.hiddenRparen2;
        case 10: return this.stringConst;
        default: return null;
        }
    }

    @Override protected void setASTField(int index, IASTNode value)
    {
        switch (index)
        {
        case 0:  this.name = (ASTNameNode)value; return;
        case 1:  this.hiddenTLparen = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 2:  this.functionArgList = (IASTListNode<ASTFunctionArgListNode>)value; return;
        case 3:  this.primarySectionSubscriptList = (IASTListNode<ASTSectionSubscriptNode>)value; return;
        case 4:  this.hiddenTRparen = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 5:  this.hiddenTPercent = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 6:  this.derivedTypeComponentRef = (IASTListNode<ASTDataRefNode>)value; return;
        case 7:  this.hiddenLparen2 = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 8:  this.componentSectionSubscriptList = (IASTListNode<ASTSectionSubscriptNode>)value; return;
        case 9:  this.hiddenRparen2 = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 10: this.stringConst = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        default: throw new IllegalArgumentException("Invalid index");
        }
    }
}

