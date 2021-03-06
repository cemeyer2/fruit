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

public class ASTIntentStmtNode extends ASTNode implements ISpecificationStmt
{
    org.eclipse.photran.internal.core.lexer.Token label; // in ASTIntentStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTIntent; // in ASTIntentStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTLparen; // in ASTIntentStmtNode
    ASTIntentSpecNode intentSpec; // in ASTIntentStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTRparen; // in ASTIntentStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTColon; // in ASTIntentStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTColon2; // in ASTIntentStmtNode
    IASTListNode<ASTIntentParListNode> variableList; // in ASTIntentStmtNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTEos; // in ASTIntentStmtNode

    public org.eclipse.photran.internal.core.lexer.Token getLabel()
    {
        return this.label;
    }

    public void setLabel(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.label = newValue;
    }


    public ASTIntentSpecNode getIntentSpec()
    {
        return this.intentSpec;
    }

    public void setIntentSpec(ASTIntentSpecNode newValue)
    {
        this.intentSpec = newValue;
    }


    public IASTListNode<ASTIntentParListNode> getVariableList()
    {
        return this.variableList;
    }

    public void setVariableList(IASTListNode<ASTIntentParListNode> newValue)
    {
        this.variableList = newValue;
    }


    public void accept(IASTVisitor visitor)
    {
        visitor.visitASTIntentStmtNode(this);
        visitor.visitISpecificationStmt(this);
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
        case 1:  return this.hiddenTIntent;
        case 2:  return this.hiddenTLparen;
        case 3:  return this.intentSpec;
        case 4:  return this.hiddenTRparen;
        case 5:  return this.hiddenTColon;
        case 6:  return this.hiddenTColon2;
        case 7:  return this.variableList;
        case 8:  return this.hiddenTEos;
        default: return null;
        }
    }

    @Override protected void setASTField(int index, IASTNode value)
    {
        switch (index)
        {
        case 0:  this.label = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 1:  this.hiddenTIntent = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 2:  this.hiddenTLparen = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 3:  this.intentSpec = (ASTIntentSpecNode)value; return;
        case 4:  this.hiddenTRparen = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 5:  this.hiddenTColon = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 6:  this.hiddenTColon2 = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 7:  this.variableList = (IASTListNode<ASTIntentParListNode>)value; return;
        case 8:  this.hiddenTEos = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        default: throw new IllegalArgumentException("Invalid index");
        }
    }
}

