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

package eu.engys.core.dictionary.model;

import static eu.engys.util.Symbols.DOT;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.IntegerField;
import eu.engys.util.ui.textfields.StringField;

public class OneDictionaryPerRowTableAdapter extends DictionaryTableAdapter {

    public static final String ROW_NAME = "one.dict.row";
    
    private final int NAME_COL = 0;
    protected final String[] columnNames;
    protected final String[] columnKeys;
    protected final String[] rowNames;
    protected final String[] rowKeys;
    protected final String dictKey;

    protected final Class<?>[] type;

    public static void main(String[] args) {
        String[] columnNames = { "Name", "Thickness [m]", "Conductivity [W/K" + DOT + "m]" };
        String[] columnKeys = { "thickness", "lambda" };
        Class<?>[] type = { String.class, Double.class, Double.class };

        JPanel table = new OneDictionaryPerRowTableAdapter(new DictionaryModel(), columnNames, columnKeys, null, null, "layers", type);
        JFrame f = UiUtil.defaultTestFrame("test", table);
        f.setSize(600, 600);
        f.setVisible(true);
    }

    public OneDictionaryPerRowTableAdapter(DictionaryModel dictionaryModel, String[] columnNames, String[] columnKeys, String[] rowNames, String[] rowKeys, String key, Class<?>[] type) {
        super(dictionaryModel, columnNames);
        this.columnNames = columnNames;
        this.columnKeys = columnKeys;
        this.rowNames = rowNames;
        this.rowKeys = rowKeys;
        this.dictKey = key;
        this.type = type;

        setName(ROW_NAME);

        if (isStaticTable()) {
            setRowHeader();
            hideButtonsPanel();
        }
    }

    @Override
    protected void addRow() {
        if (!isStaticTable()) {
            JTextField[] row = new JTextField[type.length];
            for (int i = 0; i < type.length; i++) {
                Class<?> klass = type[i];
                if (klass == Integer.class) {
                    row[i] = new IntegerField();
                    ((IntegerField) row[i]).setIntValue(Integer.valueOf(0));
                } else if (klass == Double.class) {
                    row[i] = new DoubleField();
                    ((DoubleField) row[i]).setDoubleValue(Integer.valueOf(0));
                } else if (klass == String.class) {
                    row[i] = new StringField("name" + (getRowsMap().size() + 1));
                } else {
                    row[i] = new StringField("");
                }
            }
            addRow(row);
        }
    }

    private boolean isStaticTable() {
        return rowNames != null;
    }

    public void setRowHeader() {
        for (int i = 0; i < rowNames.length; i++) {
            JComponent[] row = new JComponent[columnKeys.length + 1];
            row[0] = new JLabel(rowNames[i]);
            for (int j = 1; j < row.length; j++) {
                Class<?> klass = type[j];
                if (klass == Integer.class) {
                    row[j] = new IntegerField();
                    ((IntegerField) row[j]).setIntValue(Integer.valueOf(0));
                } else if (klass == Double.class) {
                    row[j] = new DoubleField();
                    ((DoubleField) row[j]).setDoubleValue(Double.valueOf(0));
                } else {
                    row[j] = new JLabel("");
                }
            }
            addRow(row, false);
        }
    }

    @Override
    public void load() {
        if (dictionaryModel.getDictionary().found(dictKey)) {
            Dictionary dict = dictionaryModel.getDictionary().subDict(dictKey);
            if (isStaticTable() && dict.getDictionaries().size() != rowKeys.length) {
                setRowHeader();
            } else {
                int rowIndex = 0;
                for (Dictionary d : dict.getDictionaries()) {
                    JComponent[] row = new JComponent[columnKeys.length + 1];
                    if (isStaticTable())
                        row[0] = new JLabel(rowNames[rowIndex]);
                    else
                        row[0] = new StringField(d.getName());

                    for (int k = 0; k < columnKeys.length; k++) {
                        String value = d.lookup(columnKeys[k]);
                        int j = k + 1;
                        if (value == null)
                            row[j] = new JTextField("0");
                        else if (type[j] == Double.class) {
                            row[j] = new DoubleField();
                            ((DoubleField) row[j]).setDoubleValue(Double.valueOf(value));
                        } else if (type[j] == Integer.class) {
                            row[j] = new IntegerField();
                            ((IntegerField) row[j]).setIntValue(Integer.valueOf(value));
                        } else {
                            row[j] = new JLabel(value);
                        }
                    }
                    addRow(row);
                    rowIndex++;
                }
            }
        }
    }

    @Override
    protected void save() {
        Dictionary layers = new Dictionary(dictKey);
        for (int r = 0; r < getRowsMap().values().size(); r++) {
            String name;
            if (isStaticTable()) {
                name = rowKeys[r];
            } else {
                JComponent[][] fields = getRowsMap().values().toArray(new JComponent[0][0]);
                name = String.valueOf(((JTextField) fields[r][NAME_COL]).getText());
            }

            Dictionary layer = new Dictionary(name);
            for (int k = 0; k < columnKeys.length; k++) {
                JComponent[][] fields = getRowsMap().values().toArray(new JComponent[0][0]);
                String value = String.valueOf(((JTextField) fields[r][k + 1]).getText());
                layer.add(columnKeys[k], value);
            }
            layers.add(layer);
        }
        dictionaryModel.getDictionary().add(layers);
    }

}
