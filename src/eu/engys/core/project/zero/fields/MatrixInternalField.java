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

package eu.engys.core.project.zero.fields;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MatrixInternalField implements InternalField {

	private int size;
	private CharSequence buffer;

	public MatrixInternalField(int size) {
		this.size = size;
	}
	
	@Override
	public int getSize() {
		return size;
	}

	@Override
	public double[][] getValue() {
		return null;
	}
	
	@Override
	public void write(FileWriter writer) throws IOException {
		int len = buffer.length();
		int BUF_LENGTH = 1024;
		for (int start = 0, end = Math.min(BUF_LENGTH, len); end <= len + BUF_LENGTH; start = end, end += BUF_LENGTH) {
			writer.write(buffer.subSequence(start, Math.min(end, len)).toString());
		}
		
		writer.write(";\n");
		buffer = null;
	}
	
	@Override
	public void buffer(File file) {
		buffer = FieldReader.extractString(file, Field.INTERNAL_FIELD);
	}
	
}
