/*--------------------------------*- Java -*---------------------------------*\
 |		 o                                                                   |                                                                                     
 |    o     o       | HelyxOS: The Open Source GUI for OpenFOAM              |
 |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 |    o     o       | http://www.engys.com                                   |
 |       o          |                                                        |
 |---------------------------------------------------------------------------|
 |	 License                                                                 |
 |   This file is part of HelyxOS.                                           |
 |                                                                           |
 |   HelyxOS is free software; you can redistribute it and/or modify it      |
 |   under the terms of the GNU General Public License as published by the   |
 |   Free Software Foundation; either version 2 of the License, or (at your  |
 |   option) any later version.                                              |
 |                                                                           |
 |   HelyxOS is distributed in the hope that it will be useful, but WITHOUT  |
 |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 |   for more details.                                                       |
 |                                                                           |
 |   You should have received a copy of the GNU General Public License       |
 |   along with HelyxOS; if not, write to the Free Software Foundation,      |
 |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
\*---------------------------------------------------------------------------*/

package eu.engys.util;

import java.util.Arrays;


public class CompactStringBuilder {

	/**
     * The value is used for character storage.
     */
    byte[] value;

    /**
     * The count is the number of characters used.
     */
    int count;
    
    /**
     * Creates an CompactStringBuilder of 16 .
     */
    public CompactStringBuilder() {
        value = new byte[16];
    }

    /**
     * Creates an CompactStringBuilder of the specified capacity.
     */
    public CompactStringBuilder(int capacity) {
    	value = new byte[capacity];
    }
    
    public int length() {
        return count;
    }
    
	public void append(String str) {
		if (str == null) str = "null";
        int len = str.length();
        
        ensureCapacityInternal(count + len);
        
        CompactCharSequence compactString = new CompactCharSequence(str);
        compactString.getBytes(0, len, value, count);
        
        count += len;		
	}
	
	/**
     * This method has the same contract as ensureCapacity, but is
     * never synchronized.
     */
    private void ensureCapacityInternal(int minimumCapacity) {
        // overflow-conscious code
        if (minimumCapacity - value.length > 0)
            expandCapacity(minimumCapacity);
    }

    /**
     * This implements the expansion semantics of ensureCapacity with no
     * size check or synchronization.
     */
    void expandCapacity(int minimumCapacity) {
        int newCapacity = value.length * 2 + 2;
        if (newCapacity - minimumCapacity < 0)
            newCapacity = minimumCapacity;
        if (newCapacity < 0) {
            if (minimumCapacity < 0) // overflow
                throw new OutOfMemoryError();
            newCapacity = Integer.MAX_VALUE;
        }
        value = Arrays.copyOf(value, newCapacity);
    }
    
    @Override
    public String toString() {
    	return new CompactCharSequence(value, 0, count).toString();
    }

	public CharSequence toCompactCharSequence() {
		return new CompactCharSequence(value, 0, count);
	}
}
