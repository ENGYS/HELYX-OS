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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class CompactCharSequence implements CharSequence, Serializable {

	static final long serialVersionUID = 1L;

	private static final String ENCODING = "ISO-8859-1";
	private final int offset;
	private final int end;
	private final byte[] data;

	public CompactCharSequence(String str) {
		try {
			data = str.getBytes(ENCODING);
			offset = 0;
			end = data.length;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unexpected: " + ENCODING + " not supported!");
		}
	}

	public CompactCharSequence(byte[] data, int offset, int end) {
		this.data = data;
		this.offset = offset;
		this.end = end;
	}

	public char charAt(int index) {
		int ix = index+offset;
		if (ix >= end) {
			throw new StringIndexOutOfBoundsException("Invalid index " +
					index + " length " + length());
		}
		return (char) (data[ix] & 0xff);
	}

	public int length() {
		return end - offset;
	}
	
	public CharSequence subSequence(int start, int end) {
		if (start < 0 || end > (this.end-offset)) {
			throw new IllegalArgumentException("Illegal range " +
					start + "-" + end + " for sequence of length " + length());
		}
		return new CompactCharSequence(data, start + offset, end + offset);
	}
	
	public String toString() {
		try {
			return new String(data, offset, end-offset, ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unexpected: " + ENCODING + " not supported");
		}
	}

	public void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin)
    {
        if (srcBegin < 0)
            throw new StringIndexOutOfBoundsException(srcBegin);
        if ((srcEnd < 0) || (srcEnd > end))
            throw new StringIndexOutOfBoundsException(srcEnd);
        if (srcBegin > srcEnd)
            throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
        
        System.arraycopy(data, srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }
}
