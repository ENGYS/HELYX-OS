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


package eu.engys.core.dictionary;

import static eu.engys.core.dictionary.Dictionary.VERBOSE;

import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatrixReader {

	private FieldElement matrix;
	private Dictionary parent;

	public MatrixReader(FieldElement matrix, Dictionary parent) {
		this.matrix = matrix;
		this.parent = parent;
	}

	public void readMatrix(StringTokenizer st, Stack<String> stack) {
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			token = token.trim(); //tolgo gli spazi
			stack.push(token);//metto nella pila

			if (stack.peek().equals(";")) {
				stack.pop();
				String field = stack.pop(); 
				if (VERBOSE) System.out.println(field);

				if (field.endsWith(")")) {
					Pattern pattern = Pattern.compile("(\\w+)\\s+nonuniform\\s+(List<vector>)?\\s+(\\d+)?");
        			Matcher matcher = pattern.matcher(matrix.getName());
        			
            		if (matcher.matches()) {
            			field = processField(field);
            			parent.add(matcher.group(1), "nonuniform List<vector> "+(matcher.groupCount() == 3 ? matcher.group(3)+" " : "" )+"(( "+field);
            		} else {
            			field = processField(field);
            			parent.add(matrix.getName(), "(( "+field);
            		}
					return;
				}
			}
		}
	}

	private String processField(String field) {
		if (field.contains("|")) {
			field = field.replace("|", ") (");
		}
		return field;
	}

	public static void matrixToVector(Dictionary dictionary, String key) {
		if ( dictionary.found(key) ) {
			String value = dictionary.lookupString(key);
			value = value.replace("(", "").replace(")", "");
			value = "( "+value+" )";
			dictionary.add(key, value);
		}
	}
	
	public static void vectorToMatrix(Dictionary dictionary, String key) {
		if ( dictionary.found(key) ) {
			String value = dictionary.lookupString(key);
			value = value.replace("(", "").replace(")", "");
			value = "(( "+value+" ))";
			dictionary.add(key, value);
		}
	}
}
