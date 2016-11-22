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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

import eu.engys.util.ColorUtil;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.textfields.DoubleField;

public class PointTableAdapter extends DictionaryTableAdapter {

    private String dictKey;
    private List<JToggleButton> buttons;
    private List<Boolean> visibility;
    private final boolean showPoint;

    public PointTableAdapter(DictionaryModel dictionaryModel, String[] columnNames, String dictKey, int linesToLeave, boolean showPoint) {
        super(dictionaryModel, columnNames, PointInfo.LABEL, linesToLeave, false);
        this.dictKey = dictKey;
        this.showPoint = showPoint;
        this.buttons = new LinkedList<>();
        this.visibility = new LinkedList<Boolean>();
    }

    @Override
    protected void addRow() {
        DoubleField[] fields = ComponentsFactory.doublePointField(4);
        addRow(fields);
    }

    @Override
    protected void removeRow() {
        updateVisibilityList();
        super.removeRow();
        visibility.clear();
    }

    private void updateVisibilityList() {
        visibility.clear();
        for (int i = 0; i < buttons.size() - 1; i++) {
            visibility.add(buttons.get(i).isSelected());
        }
        turnOffPointsIn3D();
        buttons.clear();
    }

    @Override
    protected JComponent[] createComponent(final JComponent[] field) {
        if (showPoint) {
            List<JComponent> comps = new ArrayList<JComponent>(Arrays.asList(field));
            JToggleButton showMaterialPoint = null;
            if (visibility.size() > getRowsMap().size()) {
                showMaterialPoint = ShowLocationAdapter.newShowPointButton((DoubleField[]) field, getRowsMap().size(), visibility.get(getRowsMap().size()));
            } else {
                showMaterialPoint = ShowLocationAdapter.newShowPointButton((DoubleField[]) field, getRowsMap().size(), false);
            }
            showMaterialPoint.getAction().addPropertyChangeListener(new PropertyChangeListener() {
                
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if(evt.getPropertyName().equals(PointInfo.PROPERTY_NAME)){
                        firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
                    }
                }
            });
            
            buttons.add(showMaterialPoint);
            comps.add(showMaterialPoint);
            return comps.toArray(new JComponent[0]);
        } else {
            return field;
        }
    }

    @Override
    protected void triggerEventFor3D(JComponent[] comp) {
        String key = ColorUtil.getColor(getRowsMap().size()).toString();
        firePropertyChange(PointInfo.PROPERTY_NAME, null, new PointInfo((DoubleField[]) comp, key, EventActionType.REMOVE, null));
    }

    public void turnOffPointsIn3D() {
        for (JToggleButton b : buttons) {
            if (b.isSelected()) {
                b.doClick();
            }
        }
    }

    @Override
    public void load() {
        turnOffPointsIn3D();
        buttons.clear();
        String value = dictionaryModel.getDictionary().lookup(dictKey);
        if (value != null && value.startsWith("(") && value.endsWith(")")) {
            value = value.substring(1, value.length() - 1).trim();
            try {
                Pattern regex = Pattern.compile("(\\([^\\)]*\\))");
                Matcher regexMatcher = regex.matcher(value);

                while (regexMatcher.find()) {
                    DoubleField[] fields = new DoubleField[3];
                    String row = regexMatcher.group().trim();
                    Pattern rowRegex = Pattern.compile("(\\s*\\-?\\d*\\.?\\d+([eE][-+]?[0-9]+)*\\s*)");
                    Matcher rowRegexMatcher = rowRegex.matcher(row);

                    int columnCounter = 0;
                    while (rowRegexMatcher.find()) {
                        fields[columnCounter] = new DoubleField();
                        fields[columnCounter].setDoubleValue(Double.valueOf(rowRegexMatcher.group().trim()));
                        if (columnCounter > fields.length) {
                            break;
                        }
                        columnCounter++;
                    }
                    addRow(fields);
                }
            } catch (PatternSyntaxException ex) {
                // Syntax error in the regular expression
            }
        }
    }

    @Override
    protected void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        for (JComponent[] row : getRowsMap().values()) {
            sb.append("( ");
            for (DoubleField doubleField : (DoubleField[]) row) {
                sb.append(doubleField.getDoubleValue());
                sb.append(" ");
            }
            sb.append(")");
            sb.append(" ");
        }
        sb.append(")");
        dictionaryModel.getDictionary().add(dictKey, sb.toString());
    }
}
