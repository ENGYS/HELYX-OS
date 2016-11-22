/*******************************************************************************
 *  |       o                                                                   |
 *  |    o     o       | HELYX-OS: The Open Source GUI for OpenFOAM             |
 *  |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 *  |    o     o       | http://www.engys.com                                   |
 *  |       o          |                                                        |
 *  |---------------------------------------------------------------------------|
 *  |   License                                                                 |
 *  |   This file is part of HELYX-OS.                                          |
 *  |                                                                           |
 *  |   HELYX-OS is free software; you can redistribute it and/or modify it     |
 *  |   under the terms of the GNU General Public License as published by the   |
 *  |   Free Software Foundation; either version 2 of the License, or (at your  |
 *  |   option) any later version.                                              |
 *  |                                                                           |
 *  |   HELYX-OS is distributed in the hope that it will be useful, but WITHOUT |
 *  |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 *  |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 *  |   for more details.                                                       |
 *  |                                                                           |
 *  |   You should have received a copy of the GNU General Public License       |
 *  |   along with HELYX-OS; if not, write to the Free Software Foundation,     |
 *  |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
 *******************************************************************************/

package eu.engys.core.dictionary;

import static eu.engys.core.dictionary.Dictionary.VERBOSE;

import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListReader {

	private ListField list;
	private Dictionary parent;
	private boolean simpleList;

	public ListReader(ListField list, Dictionary parent) {
		this.list = list;
		this.parent = parent;
	}

	public void readList(StringTokenizer st, Stack<String> stack) {
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			token = token.trim(); /* tolgo gli spazi */
			stack.push(token);/* metto nella pila */

			if (stack.peek().equals("{")) {
				stack.pop();
				String name = stack.peek();
				Dictionary d = new Dictionary(name);
				if (VERBOSE)
					System.out.println("START LIST DICTIONARY: " + name);

				new DictionaryReader(d).readDictionary(st, stack);
				list.add(d);
			} else if (stack.peek().equals("}")) {
				stack.pop();
				String name = stack.pop();
				if (VERBOSE)
					System.out.println("END LIST DICTIONARY: " + name);
				return;
			} else if (stack.peek().equals(";")) {
				stack.pop();
				String field = stack.pop();
				if (VERBOSE)
					System.out.println(list.getName()+", "+field);

				if (field.endsWith(")")) {
					if (list.getListElements().isEmpty()) {
						Pattern pattern = Pattern.compile("(\\w+)\\s+nonuniform\\s*(List<scalar>)?\\s*(\\d+)?\\s*");
						Matcher listFieldMatcher = pattern.matcher(list.getName());
						if (field.equals(")")) {
							if (listFieldMatcher.matches()) {/* is an empty array nonuniform 'nonuniform 0()' */
								parent.add(listFieldMatcher.group(1), "nonuniform 0()");
								simpleList = true;
							}
						} else {
							if (listFieldMatcher.matches()) {
								field = processField(field);
								
								parent.add(listFieldMatcher.group(1), "nonuniform List<scalar> "+(listFieldMatcher.groupCount() == 3 ? listFieldMatcher.group(3)+" " : "" )+"( "+field);
							} else if (list.getName().contains(" uniform")) {/* is a simple array (val1 val2 val3) not empty '( )' */
								parent.add(list.getName().replace("uniform", "").trim(), "uniform ( " + field);
							} else if (field.startsWith("<")) {/* is a dataTable ( +++ (+++ +++ +++) ) */
								parent.add(list.getName(), "( " + field.replace("<", "(").replace(">", ")"));
							} else {
								field = processField(field);
								parent.add(list.getName(), "( " + field);
							}
							simpleList = true;
						}
					}
					if (VERBOSE)
						System.out.println("END LIST");
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

	public boolean isSimpleList() {
		return simpleList;
	}
	
}
