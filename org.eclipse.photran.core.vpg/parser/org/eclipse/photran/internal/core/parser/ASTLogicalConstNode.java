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

public class ASTLogicalConstNode extends ASTNode implements IExpr
{
    org.eclipse.photran.internal.core.lexer.Token isTrue; // in ASTLogicalConstNode
    org.eclipse.photran.internal.core.lexer.Token isFalse; // in ASTLogicalConstNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTUnderscore; // in ASTLogicalConstNode
    ASTNamedConstantUseNode namedConstKind; // in ASTLogicalConstNode
    org.eclipse.photran.internal.core.lexer.Token intKind; // in ASTLogicalConstNode

    public boolean isTrue()
    {
        return this.isTrue != null;
    }

    public void setIsTrue(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.isTrue = newValue;
    }


    public boolean isFalse()
    {
        return this.isFalse != null;
    }

    public void setIsFalse(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.isFalse = newValue;
    }


    public ASTNamedConstantUseNode getNamedConstKind()
    {
        return this.namedConstKind;
    }

    public void setNamedConstKind(ASTNamedConstantUseNode newValue)
    {
        this.namedConstKind = newValue;
    }


    public org.eclipse.photran.internal.core.lexer.Token getIntKind()
    {
        return this.intKind;
    }

    public void setIntKind(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.intKind = newValue;
    }


    public void accept(IASTVisitor visitor)
    {
        visitor.visitASTLogicalConstNode(this);
        visitor.visitIExpr(this);
        visitor.visitASTNode(this);
    }

    @Override protected int getNumASTFields()
    {
        return 5;
    }

    @Override protected IASTNode getASTField(int index)
    {
        switch (index)
        {
        case 0:  return this.isTrue;
        case 1:  return this.isFalse;
        case 2:  return this.hiddenTUnderscore;
        case 3:  return this.namedConstKind;
        case 4:  return this.intKind;
        default: return null;
        }
    }

    @Override protected void setASTField(int index, IASTNode value)
    {
        switch (index)
        {
        case 0:  this.isTrue = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 1:  this.isFalse = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 2:  this.hiddenTUnderscore = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 3:  this.namedConstKind = (ASTNamedConstantUseNode)value; return;
        case 4:  this.intKind = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        default: throw new IllegalArgumentException("Invalid index");
        }
    }
}

