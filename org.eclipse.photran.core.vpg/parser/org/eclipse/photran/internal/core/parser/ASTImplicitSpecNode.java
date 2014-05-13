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

public class ASTImplicitSpecNode extends ASTNode
{
    ASTTypeSpecNode typeSpec; // in ASTImplicitSpecNode
    org.eclipse.photran.internal.core.lexer.Token charRanges; // in ASTImplicitSpecNode

    public ASTTypeSpecNode getTypeSpec()
    {
        return this.typeSpec;
    }

    public void setTypeSpec(ASTTypeSpecNode newValue)
    {
        this.typeSpec = newValue;
    }


    public org.eclipse.photran.internal.core.lexer.Token getCharRanges()
    {
        return this.charRanges;
    }

    public void setCharRanges(org.eclipse.photran.internal.core.lexer.Token newValue)
    {
        this.charRanges = newValue;
    }


    public void accept(IASTVisitor visitor)
    {
        visitor.visitASTImplicitSpecNode(this);
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
        case 0:  return this.typeSpec;
        case 1:  return this.charRanges;
        default: return null;
        }
    }

    @Override protected void setASTField(int index, IASTNode value)
    {
        switch (index)
        {
        case 0:  this.typeSpec = (ASTTypeSpecNode)value; return;
        case 1:  this.charRanges = (org.eclipse.photran.internal.core.lexer.Token)value; return;
        default: throw new IllegalArgumentException("Invalid index");
        }
    }
}
