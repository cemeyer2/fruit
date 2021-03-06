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
package org.eclipse.photran.core.vpg.util;

import org.eclipse.photran.internal.core.lexer.Token;

/**
 * An <b>offset</b> and a <b>length</b> (simply two integers, typically non-negative).
 * 
 * <code>OffsetLength</code>s are frequently used to store the position of a piece of text in a file.
 * 
 * @author Jeff Overbey
 */
public final class OffsetLength
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Static Methods
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** @return the offset of the first character beyond this region */
    public static int getPositionPastEnd(int offset, int length)
    {
        return offset + Math.max(length, 1);
    }

    /** @return true iff every character in the "other" region is also in "this" region */
    public static boolean contains(int thisOffset, int thisLength, int otherOffset, int otherLength)
    {
        return thisOffset <= otherOffset
            && getPositionPastEnd(otherOffset, otherLength) <= getPositionPastEnd(thisOffset, thisLength);
    }
    
    /** @return true iff every character in the "other" region is also in "this" region */
    public static boolean contains(int thisOffset, int thisLength, OffsetLength other)
    {
        return other != null && contains(thisOffset, thisLength, other.offset, other.length);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fields
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private int offset = 0, length = 0;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** Creates an <code>OffsetLength</code> with the given offset and length */
    public OffsetLength(int offset, int length)
    {
        this.offset = offset;
        this.length = length;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Basic Accessor/Mutator Methods
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** @return the offset */
    public int getOffset()
    {
        return offset;
    }

    /** Sets the offset */
    public void setOffset(int offset)
    {
        this.offset = Math.max(offset, 0);
    }

    /** @return the length */
    public int getLength()
    {
        return length;
    }

    /** Sets the length */
    public void setLength(int length)
    {
        this.length = Math.max(length, 0);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Computed Accessor Methods
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** @return the offset of the first character beyond this region */
   public int getPositionPastEnd()
    {
        return getPositionPastEnd(offset, length);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Inquiry & Comparison Methods
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
   /** @return true iff every character in the "other" region is also in this region */
    public boolean contains(OffsetLength other)
    {
        return contains(this.offset, this.length, other);
    }
    
    /** @return true iff every character in the given token is also in this region */
    public boolean containsFileRange(Token token)
    {
        return contains(this.offset, this.length, token.getFileOffset(), token.getLength());
    }
    
    /** @return true iff both the offset and length are equal to those of the supplied <code>OffsetLength</code> */
    public boolean equals(OffsetLength other)
    {
        return this.offset == other.offset && this.length == other.length;
    }

    /** @return true iff this offset is greater than or equal to the target offset */
    public boolean isOnOrAfter(int targetOffset)
    {
        return this.offset >= targetOffset;
    }

    /** @return true iff this offset is less than the target offset */
    public boolean isBefore(int targetOffset)
    {
        return this.offset < targetOffset;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // toString
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public String toString()
    {
        return "offset " + offset + ", length " + length;
    }
}
