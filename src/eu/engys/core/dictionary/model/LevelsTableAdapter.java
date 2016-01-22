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


package eu.engys.core.dictionary.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JComponent;
import javax.swing.JTextField;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.IntegerField;

public class LevelsTableAdapter extends DictionaryTableAdapter {

	protected final String[] columnNames;
	private static final String DICTKEY = "levels";
	private static final String MODE = "distance";
	private final Class<?>[] type;

	public LevelsTableAdapter(DictionaryModel dictionaryModel, String[] names, final Class<?>[] type) {
		super(dictionaryModel, names);
		this.columnNames = names;
		this.type = type;
	}

	@Override
	protected void addRow() {
		JTextField[] fields = new JTextField[2];
		fields[0] = new DoubleField();
		fields[1] = new IntegerField();
		addRow(fields);
	}

	@Override
	public void load() {
		Dictionary dictionary = dictionaryModel.getDictionary();
		if (MODE.equals(dictionary.lookup("mode")) && dictionary.isField(DICTKEY)) {
			String value = dictionary.lookup(DICTKEY);
			if (value != null && value.startsWith("(") && value.endsWith(")")) {
				value = value.substring(1, value.length() - 1);
				try {
					Pattern regex = Pattern.compile("\\((\\s*\\d+\\.?\\d+)\\s*(\\d+\\s*)\\)");
					Matcher regexMatcher = regex.matcher(value);
					while (regexMatcher.find()) {
						JTextField[] row = new JTextField[type.length];
						for (int j = 1; j <= regexMatcher.groupCount(); j++) {
							String cellValue = regexMatcher.group(j).trim();
							int i = j - 1;
							Class<?> klass = type[i];
							if (klass == Integer.class) {
								row[i] = new IntegerField();
								((IntegerField) row[i]).setIntValue(Integer.valueOf(cellValue));
							} else if (klass == Double.class) {
								row[i] = new DoubleField();
								((DoubleField) row[i]).setDoubleValue(Double.valueOf(cellValue));
							} else {
								row[i] = new JTextField();
							}
						}
						addRow(row);
					}
				} catch (PatternSyntaxException ex) {
					// Syntax error in the regular expression
				}
			}
		}
	}

	@Override
	protected void save() {
		if (getRowsMap().isEmpty()) {
			dictionaryModel.getDictionary().remove(DICTKEY);
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("( ");

		TreeMap<Integer, Double> orderedMap = getOrderedMap();
		for (Integer key : orderedMap.descendingKeySet()) {
			sb.append("( ");
			sb.append(orderedMap.get(key));
			sb.append(" ");
			sb.append(key);
			sb.append(" )");
			sb.append(" ");
		}
		sb.append(")");
		dictionaryModel.getDictionary().add(DICTKEY, sb.toString());
	}

	private TreeMap<Integer, Double> getOrderedMap() {
		Map<Integer, Double> map = new HashMap<>();
		for (JComponent[] row : getRowsMap().values()) {
			map.put(((IntegerField) row[1]).getIntValue(), ((DoubleField) row[0]).getDoubleValue());
		}
		return new TreeMap<Integer, Double>(map);
	}

}
