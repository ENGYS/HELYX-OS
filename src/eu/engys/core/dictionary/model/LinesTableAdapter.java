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

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.IntegerField;

public class LinesTableAdapter extends DictionaryTableAdapter {

	private static final String LEVELS = "levels";
	private static final String LEVEL = "level";
	private Dictionary lineDictionary;
	private final Class<?>[] type;

	public LinesTableAdapter(DictionaryModel dictionaryModel, Dictionary lineDictionary, String[] columnNames, final Class<?>[] type) {
		super(dictionaryModel, columnNames, "", LEAVE_ONE_LINE, true);
		this.lineDictionary = lineDictionary;
		this.type = type;
		fixOldStyleLevels();
	}

	public JButton getButton() {
		JButton b = new JButton(new AbstractAction("Edit") {
			@Override
			public void actionPerformed(ActionEvent e) {
				getDialog().setVisible(true);
			}

		});
		return b;
	}

	private JDialog getDialog() {
		final JDialog dialog = new JDialog(UiUtil.getActiveWindow(), "Refinement Level", ModalityType.MODELESS);
		dialog.setName("line.adapter.dialog");

		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton = new JButton(new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
				dialog.setVisible(false);
			}
		});
		okButton.setName("OK");
		buttonsPanel.add(okButton);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(new JScrollPane(this), BorderLayout.CENTER);
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

		dialog.add(mainPanel);
		dialog.setSize(600, 400);
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.getRootPane().setDefaultButton(okButton);
		return dialog;
	}

	private void fixOldStyleLevels() {
		if (lineDictionary.found(LEVEL)) {
			Dictionary clone = new Dictionary(lineDictionary);
			String levelValue = clone.lookup(LEVEL);
			clone.remove(LEVEL);
			clone.add(LEVELS, "( ( 0.0 " + levelValue + " ) )");
			this.lineDictionary = clone;
		}
	}

	public Dictionary getLineDictionary() {
		return lineDictionary;
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
		String value = lineDictionary.lookup(LEVELS);
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
				ex.printStackTrace();
			}
		}
		if (getRowsMap().isEmpty()) {
			addRow();
		}
	}

	@Override
	protected void save() {
		if (getRowsMap().isEmpty()) {
			lineDictionary.remove(LEVELS);
			return;
		}
		StringBuilder sb = new StringBuilder();
		TreeMap<Integer, Double> orderedMap = getOrderedMap();
		sb.append("( ");
		for (Integer key : orderedMap.descendingKeySet()) {
			sb.append("( ");
			sb.append(orderedMap.get(key));
			sb.append(" ");
			sb.append(key);
			sb.append(" )");
			sb.append(" ");
		}
		sb.append(")");
		lineDictionary.add(LEVELS, sb.toString());
	}

	private TreeMap<Integer, Double> getOrderedMap() {
		Map<Integer, Double> map = new HashMap<>();
		for (JComponent[] row : getRowsMap().values()) {
			map.put(((IntegerField) row[1]).getIntValue(), ((DoubleField) row[0]).getDoubleValue());
		}
		return new TreeMap<Integer, Double>(map);
	}

}
