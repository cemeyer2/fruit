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

public class ASTSavedEntityNode extends ASTNode
{
    org.eclipse.photran.internal.core.lexer.Token variableName; // in ASTSavedEntityNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTSlash; // in ASTSavedEntityNode
    org.eclipse.photran.internal.core.lexer.Token commonBlockName; // in ASTSavedEntityNode
    org.eclipse.photran.internal.core.lexer.Token hiddenTSlash2; // in ASTSavedEntityNode

    public org.eclipse.photran.internal.core.lexer.Token getVariableName()
    {
        return this.variableName;
    }

    public void setVariableName(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.variableName = newValue;
    }


    public org.eclipse.photran.internal.core.lexer.Token getCommonBlockName()
    {
        return this.commonBlockName;
    }

    public void setCommonBlockName(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.commonBlockName = newValue;
    }


    public void accept(IASTVisitor visitor)
    {
        visitor.visitASTSavedEntityNode(this);
        visitor.visitASTNode(this);
    }

    @Override protected int getNumASTFields()
    {
        return 4;
    }

    @Override protected IASTNode getASTField(int index)
    {
        switch (index)
        {
        case 0:  return this.variableName;
        case 1:  return this.hiddenTSlash;
        case 2:  return this.commonBlockName;
        case 3:  return this.hiddenTSlash2;
        default: return null;
        }
    }

    @Override protected void setASTField(int index, IASTNode value)
    {
        switch (index)
        {
        case 0:  this.variableName = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 1:  this.hiddenTSlash = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 2:  this.commonBlockName = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        case 3:  this.hiddenTSlash2 = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        default: throw new IllegalArgumentException("Invalid index");
        }
    }
}

