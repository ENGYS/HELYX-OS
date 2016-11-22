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

import static eu.engys.util.ui.ComponentsFactory.checkBoxControllerField;
import static eu.engys.util.ui.ComponentsFactory.checkField;
import static eu.engys.util.ui.ComponentsFactory.comboBoxControllerField;
import static eu.engys.util.ui.ComponentsFactory.doubleArrayField;
import static eu.engys.util.ui.ComponentsFactory.doubleField;
import static eu.engys.util.ui.ComponentsFactory.doublePointField;
import static eu.engys.util.ui.ComponentsFactory.intArrayField;
import static eu.engys.util.ui.ComponentsFactory.intField;
import static eu.engys.util.ui.ComponentsFactory.listField;
import static eu.engys.util.ui.ComponentsFactory.radioField;
import static eu.engys.util.ui.ComponentsFactory.selectField;
import static eu.engys.util.ui.ComponentsFactory.spinnerField;
import static eu.engys.util.ui.ComponentsFactory.stringField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.dictionary.DimensionedScalar;
import eu.engys.core.project.zero.patches.Patches;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.FieldChangeListener;
import eu.engys.util.ui.FileFieldPanel;
import eu.engys.util.ui.ListBuilder;
import eu.engys.util.ui.ListFieldPanel;
import eu.engys.util.ui.RadioFieldPanel;
import eu.engys.util.ui.SelectionValueConfigurator;
import eu.engys.util.ui.builder.JCheckBoxController;
import eu.engys.util.ui.builder.JComboBoxController;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.IntegerField;
import eu.engys.util.ui.textfields.SpinnerField;
import eu.engys.util.ui.textfields.StringField;

public class DictionaryModel {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryModel.class);

    private final String key;

    private Dictionary dictionary;
    private List<DictionaryModel> companions = new LinkedList<>();

    public interface DictionaryListener {
        public void dictionaryChanged() throws DictionaryError;
    }

    private List<DictionaryListener> listeners = new CopyOnWriteArrayList<>();
    private Map<String, DictionaryModel> subModels = new HashMap<>();

    public static class DictionaryError extends Exception {
        private String[] messages;

        public DictionaryError(String... msg) {
            super(msg[0]);
            this.messages = msg;
        }

        public String[] getMessages() {
            return messages;
        }
    }

    public DictionaryModel() {
        this(null, new Dictionary(""));
    }

    public DictionaryModel(String key) {
        this(key, new Dictionary(""));
    }

    public DictionaryModel(Dictionary dictionary) {
        this(null, dictionary);
    }

    public DictionaryModel(String key, Dictionary dictionary) {
        this.key = key;
        this.dictionary = dictionary;
    }

    public void addCompanion(DictionaryModel companion) {
        this.companions.add(companion);
    }

    public void setCompanion(DictionaryModel companion) {
        this.companions.clear();
        this.companions.add(companion);
    }

    public List<DictionaryModel> getCompanions() {
        return companions;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public DictionaryModel subModel(String key) {
        if (subModels.containsKey(key)) {
            return subModels.get(key);
        } else {
            Dictionary subDict = dictionary.subDict(key);
            DictionaryModel model = new DictionaryModel(subDict != null ? subDict : new Dictionary(key));
            subModels.put(key, model);
            return model;
        }
    }

    public void refresh() {
        try {
            fireDictionaryChange();
        } catch (DictionaryError e) {
        }
    }

    public void setDictionary(Dictionary dictionary) {
        try {
            if (dictionary != null) {
                this.dictionary = dictionary;
                for (String key : subModels.keySet()) {
                    DictionaryModel subModel = subModels.get(key);
                    subModel.setDictionary(dictionary.subDict(key));
                }
                fireDictionaryChange();
            }
        } catch (DictionaryError e) {
            String[] messages = e.getMessages();

            StringBuilder sb = new StringBuilder();
            for (String message : messages) {
                sb.append(message);
                sb.append("\n");
            }
            // System.err.println(sb.toString());
            logger.warn(sb.toString());
        }
    }

    private void fireDictionaryChange() throws DictionaryError {
        List<String> errors = new ArrayList<String>();
        for (DictionaryListener listener : listeners) {
            try {
                // System.out.println("Listener: "+listener.getClass());
                // System.out.println("pre: "+dictionary);
                listener.dictionaryChanged();
                // System.out.println("post: "+dictionary);

            } catch (DictionaryError e) {
                errors.add(e.getMessage());
                // e.printStackTrace();
            }
        }
        if (!errors.isEmpty()) {
            throw new DictionaryError(errors.toArray(new String[errors.size()]));
        }
    }

    public void addDictionaryListener(DictionaryListener listener) {
        listeners.add(listener);
    }

    /*
     * BINDING
     */
    public StringField bindLabel(String key) {
        StringField field = stringField();
        field.addPropertyChangeListener(new LabelFieldHandler(key, field));
        return field;
    }

    public StringField bindLabel(String key, boolean allowEmpty) {
        StringField field = stringField();
        field.setToVerifier(allowEmpty, true);
        field.addPropertyChangeListener(new LabelFieldHandler(key, field));
        return field;
    }

    public JCheckBox bindBoolean(String key) {
        return bindBoolean(key, false);
    }

    public JCheckBox bindBoolean(String key, boolean def) {
        return bindBoolean(key, def, true);
    }

    public JCheckBox bindBoolean(String key, boolean def, boolean fireEvent) {
    	JCheckBox field = checkField(def);
    	field.addActionListener(new BooleanFieldHandler(key, field, def, fireEvent));
    	return field;
    }

    public JCheckBox bindBoolean(String key, String trueValue, String falseValue) {
        JCheckBox field = checkField();
        field.addActionListener(new BooleanValuesFieldHandler(key, field, trueValue, falseValue, false));
        return field;
    }

    public JCheckBox bindBoolean(String key, String trueValue, String falseValue, boolean def) {
        JCheckBox field = checkField(def);
        field.addActionListener(new BooleanValuesFieldHandler(key, field, trueValue, falseValue, false));
        return field;
    }

    public JCheckBox bindBoolean(String key, String trueValue, String falseValue, boolean def, boolean lightEvent) {
        JCheckBox field = checkField(def);
        field.addActionListener(new BooleanValuesFieldHandler(key, field, trueValue, falseValue, lightEvent));
        return field;
    }

    public SpinnerField bindSpinner(String key) {
        SpinnerField field = spinnerField();
        field.addPropertyChangeListener(new SpinnerFieldHandler(key, field));
        return field;
    }

    public IntegerField bindIntegerPositive(String key) {
        IntegerField field = intField(0, Integer.MAX_VALUE);
        field.addPropertyChangeListener(new IntFieldHandler(key, field));
        return field;
    }

    public IntegerField bindIntegerNegative(String key) {
        IntegerField field = intField(-Integer.MAX_VALUE, 0);
        field.addPropertyChangeListener(new IntFieldHandler(key, field));
        return field;
    }

    public IntegerField bindInteger(String key) {
        IntegerField field = intField(-Integer.MAX_VALUE, Integer.MAX_VALUE);
        field.addPropertyChangeListener(new IntFieldHandler(key, field));
        return field;
    }
    
    public IntegerField bindInteger(String key, Integer lb, Integer ub) {
        IntegerField field = intField(lb, ub);
        field.addPropertyChangeListener(new IntFieldHandler(key, field));
        return field;
    }
    
    public IntegerField bindIntegerAngle_360(String key) {
        // Negative value = disabled
        IntegerField field = intField(-Integer.MAX_VALUE, 359);
        field.addPropertyChangeListener(new IntFieldHandler(key, field));
        return field;
    }

    public IntegerField bindIntegerAngle_180(String key) {
        // Negative value = disabled
        IntegerField field = intField(-Integer.MAX_VALUE, 180);
        field.addPropertyChangeListener(new IntFieldHandler(key, field));
        return field;
    }

    // public IntegerField bindInteger(String key, String name) {
    // IntegerField field = bindInteger(key);
    // return field;
    // }

    public IntegerField bindIntegerLevels(String key, String mode) {
        IntegerField field = intField();
        field.addPropertyChangeListener(new IntLevelsFieldHandler(key, mode, field));
        return field;
    }

    public IntegerField[] bindIntegerArray(String key, Integer dimensions) {
        return bindIntegerArray(key, dimensions, null);
    }

    public IntegerField[] bindIntegerArray(String key, Integer dimensions, FieldChangeListener listener) {
        IntegerField[] field = intArrayField(dimensions);
        for (int i = 0; i < field.length; i++) {
            IntegerField f = field[i];
            f.addPropertyChangeListener(new IntPointFieldHandler(key, field, listener));
        }
        return field;
    }

    public DoubleField bindDimensionedDouble(String key, String dimensions, Double lb, Double ub) {
        DoubleField field = doubleField(lb, ub);
        field.addPropertyChangeListener(new DoubleDimensionedFieldHandler(key, field, dimensions));
        return field;
    }

    public DoubleField bindDimensionedDouble(String key, String dimensions) {
        DoubleField field = doubleField();
        field.addPropertyChangeListener(new DoubleDimensionedFieldHandler(key, field, dimensions));
        return field;
    }

    public DoubleField bindUniformDouble(String key) {
        DoubleField field = doubleField();
        field.addPropertyChangeListener(new DoubleUniformFieldHandler(key, field));
        return field;
    }

    // public DoubleField bindUniformDoubleWithName(String key, String name) {
    // DoubleField field = doubleField();
    // field.addPropertyChangeListener(new DoubleUniformFieldHandler(key,
    // field));
    // return field;
    // }

    public DoubleField bindUniformDouble(String key, double lb, double ub, double def) {
        DoubleField field = doubleField(def, lb, ub);
        field.addPropertyChangeListener(new DoubleUniformFieldHandler(key, field));
        return field;
    }

    public DoubleField bindUniformDouble(String key, double lb, double ub) {
        DoubleField field = doubleField(lb, ub);
        field.addPropertyChangeListener(new DoubleUniformFieldHandler(key, field));
        return field;
    }

    public DoubleField bindUniformNegativeDouble(String key) {
        DoubleField field = doubleField();
        field.addPropertyChangeListener(new DoubleUniformNegativeFieldHandler(key, field));
        return field;
    }

    public DoubleField bindUniformPositiveDouble(String key) {
        DoubleField field = doubleField();
        field.addPropertyChangeListener(new DoubleUniformPositiveFieldHandler(key, field));
        return field;
    }

    public DoubleField bindConstantDouble(String key) {
        DoubleField field = doubleField();
        field.addPropertyChangeListener(new DoubleConstantFieldHandler(key, field));
        return field;
    }

    public DoubleField bindUniformDouble(String key1, String key2) {
        DoubleField field = doubleField();
        field.addPropertyChangeListener(new DoubleUniformFieldHandler(key1, field));
        field.addPropertyChangeListener(new DoubleUniformFieldHandler(key2, field));
        return field;
    }

    public DoubleField bindDoubleAndUniformDouble(String key1, String key2) {
        DoubleField field = doubleField();
        field.addPropertyChangeListener(new DoubleFieldHandler(key1, field, null));
        field.addPropertyChangeListener(new DoubleUniformFieldHandler(key2, field));
        return field;
    }

    public DoubleField bindDouble(String key) {
        return bindDouble(key, (FieldChangeListener) null);
    }

    public DoubleField bindDouble(String key, FieldChangeListener listener) {
        DoubleField field = doubleField();
        field.addPropertyChangeListener(new DoubleFieldHandler(key, field, listener));
        return field;
    }

    public DoubleField bindDouble(String key, Integer places, FieldChangeListener listener) {
        DoubleField field = doubleField(places);
        field.addPropertyChangeListener(new DoubleFieldHandler(key, field, listener));
        return field;
    }
    
    public JComponent bindCheckAndDouble(final String key, Double def) {
        final JCheckBox checkBox = checkField();
        final DoubleField field = doubleField(def);
        field.addPropertyChangeListener(new DoubleFieldHandler(key, field, null));
        field.setEnabled(false);
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox.isSelected()) {
                    field.setEnabled(true);
                } else {
                    field.setValue(null);
                    field.setEnabled(false);
                }
            }
        });
        DictionaryModel.this.addDictionaryListener(new DictionaryListener() {
            @Override
            public void dictionaryChanged() throws DictionaryError {
                checkBox.setSelected(dictionary.found(key));
                field.setEnabled(checkBox.isSelected());
            }
        });
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            public void setName(String name) {
                super.setName(name);
                checkBox.setName(name);
                field.setName(name);
            }
        };
        panel.add(checkBox, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }
    
    public DoubleField bindDouble(String key, Double def) {
        DoubleField field = doubleField(def);
        field.addPropertyChangeListener(new DoubleFieldHandler(key, field, null));
        return field;
    }

    public DoubleField bindDouble(String key, double lb, double ub) {
        DoubleField field = doubleField(lb, ub);
        field.addPropertyChangeListener(new DoubleFieldHandler(key, field, null));
        return field;
    }

    public DoubleField bindDoublePositive(String key) {
        DoubleField field = doubleField(0.0, Double.MAX_VALUE);
        field.addPropertyChangeListener(new DoubleFieldHandler(key, field, null));
        return field;
    }

    public DoubleField bindDoubleNegative(String key) {
        DoubleField field = doubleField(-Double.MAX_VALUE, 0.0);
        field.addPropertyChangeListener(new DoubleFieldHandler(key, field, null));
        return field;
    }

    public DoubleField bindDoubleAngle_360(String key) {
        // Negative value = disabled
        DoubleField field = doubleField(-Double.MAX_VALUE, 360 - Double.MIN_VALUE);
        field.addPropertyChangeListener(new DoubleFieldHandler(key, field, null));
        return field;
    }

    public DoubleField bindDoubleAngle_180(String key) {
        // Negative value = disabled
        DoubleField field = doubleField(-Double.MAX_VALUE, 180.0);
        field.addPropertyChangeListener(new DoubleFieldHandler(key, field, null));
        return field;
    }

    public DoubleField[] bindPoint(String key, FieldChangeListener listener) {
        DoubleField[] field = doublePointField();
        for (int i = 0; i < field.length; i++) {
            DoubleField f = field[i];
            f.addPropertyChangeListener(new PointFieldHandler(key, field, listener));
        }
        return field;
    }

    public DoubleField[] bindPoint(String key, Integer places, FieldChangeListener listener) {
        DoubleField[] field = doublePointField(places);
        for (int i = 0; i < field.length; i++) {
            DoubleField f = field[i];
            f.addPropertyChangeListener(new PointFieldHandler(key, field, listener));
        }
        return field;
    }

    public DoubleField[] bindPointAndUniformPoint(String key1, String key2) {
        DoubleField[] field = doublePointField();
        for (int i = 0; i < field.length; i++) {
            DoubleField f = field[i];
            f.addPropertyChangeListener(new PointFieldHandler(key1, field, null));
            f.addPropertyChangeListener(new PointUniformFieldHandler(key2, field));
        }
        return field;
    }

    public DoubleField[] bindPoint(String key) {
        return bindPoint(key, null);
    }

    public ShowLocationAdapter bindLocation(String key, int places) {
        return bindLocation(key, places, Color.RED);
    }

    public ShowAxisAdapter bindAxis(String key, Color colorKey) {
        DoubleField[] axis = bindPoint(key, null);
        DoubleField[] center = bindPoint(key, null);
        return new ShowAxisAdapter(axis, center);
    }

    public ShowLocationAdapter bindLocation(String key, int places, Color colorKey) {
        DoubleField[] field = bindPoint(key, places, null);
        return new ShowLocationAdapter(field, colorKey);
    }

    public DoubleField[] bindUniformPoint(String key) {
        DoubleField[] field = doublePointField();
        for (int i = 0; i < field.length; i++) {
            DoubleField f = field[i];
            f.addPropertyChangeListener(new PointUniformFieldHandler(key, field));
        }
        return field;
    }

    public DoubleField[] bindUniformPoint(String key1, String key2) {
        DoubleField[] field = doublePointField();
        for (int i = 0; i < field.length; i++) {
            DoubleField f = field[i];
            f.addPropertyChangeListener(new PointUniformFieldHandler(key1, field));
            f.addPropertyChangeListener(new PointUniformFieldHandler(key2, field));
        }
        return field;
    }

    public DoubleField[] bindDimensionedPoint(String key, String dimensions) {
        DoubleField[] field = doublePointField();
        for (int i = 0; i < field.length; i++) {
            DoubleField f = field[i];
            f.addPropertyChangeListener(new PointDimensionedFieldHandler(key, field, dimensions));
        }
        return field;
    }

    public DoubleField[] bindArray(String key, int dimensions) {
        DoubleField[] field = doubleArrayField(dimensions);
        for (int i = 0; i < field.length; i++) {
            DoubleField f = field[i];
            f.setColumns(1);
            f.addPropertyChangeListener(new PointFieldHandler(key, field, null));
        }
        return field;
    }

    public FileFieldPanel bindFile(String key) {
        FileFieldPanel field = ComponentsFactory.fileField(SelectionMode.FILES_ONLY, "Select file", true);
        field.addPropertyChangeListener(new FileFieldHandler(key, field));
        return field;
    }

    public FileFieldPanel bindFolder(String key) {
        FileFieldPanel field = ComponentsFactory.fileField(SelectionMode.DIRS_ONLY, "Select folder", true);
        field.addPropertyChangeListener(new FileFieldHandler(key, field));
        return field;
    }

    public JComboBox<String> bindSelection(String key) {
        JComboBox<String> combo = selectField();
        combo.addActionListener(new SelectFieldHandler(key, combo, null));
        return combo;
    }

    public JComboBox<String> bindSelection(String key, ListBuilder builder) {
        JComboBox<String> combo = selectField(builder);
        combo.addActionListener(new SelectFieldHandler(key, combo, null));
        return combo;
    }

    public JComboBox<String> bindSelection(String key, ListModel<String> listModel) {
        JComboBox<String> combo = selectField(listModel);
        combo.addActionListener(new SelectFieldHandler(key, combo, null));
        return combo;
    }

    public JComboBox<String> bindSelection(String key, String... keys) {
        JComboBox<String> combo = selectField(keys, keys);
        combo.addActionListener(new SelectFieldHandler(key, combo, null));
        return combo;
    }

    public JComboBox<String> bindSelection(String key, String[] keys, String[] items) {
        return bindSelection(key, keys, items, null);
    }

    public JComboBox<String> bindSelection(String key, String[] keys, String[] items, SelectionValueConfigurator configurator) {
        JComboBox<String> combo = selectField(keys, items);
        combo.addActionListener(new SelectFieldHandler(key, combo, configurator));
        return combo;
    }

    public JComboBoxController bindComboBoxController(String key) {
        JComboBoxController combo = comboBoxControllerField();
        combo.addActionListener(new SelectFieldHandler(key, combo, null));
        return combo;
    }

    public JCheckBoxController bindCheckBoxController(String key, String name) {
        JCheckBoxController combo = checkBoxControllerField(name);
        combo.addActionListener(new BooleanFieldHandler(key, combo, false, true));
        return combo;
    }

    public RadioFieldPanel bindChoice(String key, String[] keys, String[] items) {
        RadioFieldPanel panel = radioField(keys, items);
        panel.addPropertyChangeListener(new ChoiceFieldHandler(key, panel));
        return panel;
    }

    public ListFieldPanel bindList(String key, ListBuilder listBuilder) {
        ListFieldPanel field = listField(listBuilder);
        field.addPropertyChangeListener(new ListFieldHandler(key, field));
        return field;
    }

    public PatchesMapTableAdapter bindPatchMapTable(DictionaryModel dictionaryModel, Patches targetPatches) {
        return new PatchesMapTableAdapter(dictionaryModel, targetPatches);
    }

    public JPanel bindTableLevels(String[] columnNames, final Class<?>[] type) {
        return new LevelsTableAdapter(this, columnNames, type);
    }

    public PointTableAdapter bindPointMatrix(String[] columnNames, final String tableKey, int linesToLeave, boolean showPoint) {
        return new PointTableAdapter(this, columnNames, tableKey, linesToLeave, showPoint);
    }

    public JPanel bindOneDictionaryPerRowTable(String[] columnNames, final String[] columnKeys, final String tableKey, String[] rowNames, String[] rowKeys, final Class<?>[] type) {
        return new OneDictionaryPerRowTableAdapter(this, columnNames, columnKeys, rowNames, rowKeys, tableKey, type);
    }

    class DoubleDimensionedFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private String dimensions;
        private DoubleField field;

        public DoubleDimensionedFieldHandler(String key, DoubleField field, String dimensions) {
            this.key = key;
            this.field = field;
            this.dimensions = dimensions;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                dictionary.add(new DimensionedScalar(key, Double.toString(field.getDoubleValue()), dimensions));
                logger.trace("DoubleFieldHandler -> value: {}", dictionary.lookup(key));
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key);
            if (value != null) {
                field.setDoubleValue(parseDouble(value));
            } else {
                field.setDoubleValue(0);
            }
            logger.trace("DoubleFieldHandler ->  read value: {}", value);
        }
    }

    class PointDimensionedFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private String dimensions;
        private DoubleField[] field;

        public PointDimensionedFieldHandler(String key, DoubleField[] field, String dimensions) {
            this.key = key;
            this.field = field;
            this.dimensions = dimensions;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                StringBuilder sb = new StringBuilder();
                sb.append("( ");
                for (int i = 0; i < field.length; i++) {
                    sb.append(field[i].getDoubleValue());
                    sb.append(" ");
                }
                sb.append(")");
                dictionary.add(new DimensionedScalar(key, sb.toString(), dimensions));
                logger.trace("PointDimensionedFieldHandler -> value: {}", dictionary.lookup(key));
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key);
            if (value != null) {
                String[] values = value.trim().substring(1, value.length() - 1).trim().split("\\s+");
                for (int i = 0; i < values.length; i++) {
                    field[i].setDoubleValue(parseDouble(values[i]));
                }
            } else {
                for (int i = 0; i < field.length; i++) {
                    field[i].setDoubleValue(0);
                }
            }
            logger.trace("PointDimensionedFieldHandler ->  value: {}", value);
        }

    }

    class LabelFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private StringField field;

        public LabelFieldHandler(String key, StringField field) {
            this.key = key;
            this.field = field;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                if (field.getValue() == null) {
                    if (dictionary.found(key))
                        dictionary.remove(key);
                } else {
                    dictionary.add(key, field.getStringValue());
                }
                logger.trace("LabelFieldHandler -> value: {}", dictionary.lookup(key));
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key);
            field.setValue(value);
            logger.trace("LabelFieldHandler ->  value: {}", value);
        }
    }

    class ListFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private ListFieldPanel field;

        public ListFieldHandler(String key, ListFieldPanel field) {
            this.key = key;
            this.field = field;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                String value = DictionaryUtils.stringArray2String(field.getValues());
                dictionary.add(key, value);
                logger.trace("ListFieldHandler -> value: {}", dictionary.lookup(key));
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            if (dictionary.isField(key)) {
                String value = dictionary.lookup(key);
                String[] values = DictionaryUtils.string2StringArray(value);
                field.setValues(values);
                logger.trace("ListFieldHandler ->  value: {}", value);
            } else {
                field.setValues(new String[0]);
            }
        }
    }

    class ChoiceFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private RadioFieldPanel field;

        public ChoiceFieldHandler(String key, RadioFieldPanel field) {
            this.key = key;
            this.field = field;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                dictionary.add(key, field.getSelectedKey());
                logger.trace("ChoiceFieldHandler -> value: {}", dictionary.lookup(key));
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key);
            field.select(value);
            logger.trace("ChoiceFieldHandler ->  value: {}", value);
        }
    }

    class SpinnerFieldHandler implements PropertyChangeListener, DictionaryListener {
        public SpinnerFieldHandler(String key, SpinnerField field) {
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {

        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        }
    }

    class IntFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private IntegerField field;

        public IntFieldHandler(String key, IntegerField field) {
            this.key = key;
            this.field = field;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                if (field.getValue() == null) {
                    if (dictionary.found(key))
                        dictionary.remove(key);
                } else {
                    dictionary.add(key, Integer.toString(field.getIntValue()));
                }
                logger.trace("IntegerFieldHandler -> value: {}", dictionary.lookup(key));
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key);
            if (value != null) {
                field.setIntValue(parseInt(value));
            } else {
                field.setValue(null);
            }
            logger.trace("IntegerFieldHandler ->  value: {}", value);
        }
    }

    class IntLevelsFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private IntegerField field;
        private String mode;

        public IntLevelsFieldHandler(String key, String mode, IntegerField field) {
            this.key = key;
            this.mode = mode;
            this.field = field;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                String lookupMode = dictionary.lookup("mode");
                if (mode.equals(lookupMode)) {
                    if (field.getValue() == null) {
                        if (dictionary.found(key))
                            dictionary.remove(key);
                    } else {
                        dictionary.add(key, String.format("(( 1E5 %s ))", Integer.toString(field.getIntValue())));
                    }
                    logger.trace("IntegerFieldHandler -> value: {}", dictionary.lookup(key));
                }
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String lookupMode = dictionary.lookup("mode");
            if (lookupMode != null) {
                if (mode.equals(lookupMode)) {
                    String value = dictionary.lookup(key);
                    if (value != null && value.length() > 4) {
                        String[] values = value.trim().substring(2, value.length() - 2).trim().split("\\s+");
                        field.setIntValue(parseInt(values[1]));
                    } else {
                        field.setValue(null);
                    }
                    logger.trace("IntegerFieldHandler ->  value: {}", value);
                } else {
                    field.setValue(null);
                }
            } else {
                field.setValue(null);
            }
        }
    }

    class IntPointFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private IntegerField[] field;
        private FieldChangeListener listener;

        public IntPointFieldHandler(String key, IntegerField[] field, FieldChangeListener listener) {
            this.key = key;
            this.field = field;
            this.listener = listener;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                StringBuilder sb = new StringBuilder();
                sb.append("(");
                for (int i = 0; i < field.length; i++) {
                    if (field[i].getValue() != null) {
                        sb.append(field[i].getIntValue());
                        sb.append(" ");
                    } else {
                        if (dictionary.found(key))
                            dictionary.remove(key);
                        return;
                    }
                }
                sb.append(")");
                dictionary.add(key, sb.toString());
                logger.trace("PointFieldHandler -> value: {}", dictionary.lookup(key));
                if (listener != null) {
                    listener.fieldChanged(evt.getSource());
                }
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key);
            if (listener != null) {
                listener.setAdjusting(true);
            }
            if (value != null) {
                String[] values = value.trim().substring(1, value.length() - 1).trim().split("\\s+");
                for (int i = 0; i < values.length; i++) {
                    field[i].setIntValue(parseInt(values[i]));
                }
                logger.trace("PointFieldHandler ->  value: {}", value);
            } else {
                for (int i = 0; i < field.length; i++) {
                    field[i].setValue(null);
                }
            }
            if (listener != null) {
                listener.setAdjusting(false);
            }
        }
    }

    class DoubleFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private DoubleField field;
        private FieldChangeListener listener;

        public DoubleFieldHandler(String key, DoubleField field, FieldChangeListener listener) {
            this.key = key;
            this.field = field;
            this.listener = listener;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                if (field.getValue() == null) {
                    if (dictionary.found(key))
                        dictionary.remove(key);
                } else {
                    dictionary.add(key, Double.toString(field.getDoubleValue()));
                    logger.trace("DoubleFieldHandler -> value: {}", dictionary.lookup(key));
                    if (listener != null) {
                        listener.fieldChanged(evt.getSource());
                    }
                }
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key);
            if (listener != null)
                listener.setAdjusting(true);
            if (value != null) {
                field.setDoubleValue(parseDouble(value));
            } else {
                field.setValue(field.getDefaultValue());
            }
            if (listener != null)
                listener.setAdjusting(false);

            logger.trace("DoubleFieldHandler ->  value: {}", value);
        }
    }

    class DoubleUniformFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private DoubleField field;

        public DoubleUniformFieldHandler(String key, DoubleField field) {
            this.key = key;
            this.field = field;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                if (!Double.isInfinite(field.getDoubleValue())) {
                    dictionary.addUniform(key, field.getDoubleValue());
                    logger.trace("DoubleUniformFieldHandler -> value: {}", dictionary.lookup(key));
                }
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key); // uniform value
            logger.trace("DoubleUniformFieldHandler ->  value: {}", value);
            if (value != null) {
                if (value.startsWith("nonuniform")) {
                    field.setDoubleValue(Double.POSITIVE_INFINITY);
                } else {
                    value = value.replace("uniform ", "");
                    field.setDoubleValue(parseDouble(value));
                }
            } else {
                field.setDoubleValue(field.getDefaultValue());
            }
        }
    }

    class DoubleUniformNegativeFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private DoubleField field;

        public DoubleUniformNegativeFieldHandler(String key, DoubleField field) {
            this.key = key;
            this.field = field;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                dictionary.addUniform(key, -Math.abs(field.getDoubleValue()));
                logger.trace("DoubleUniformFieldHandler -> " + dictionary.toString());
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key); // uniform value
            logger.trace("DoubleUniformFieldHandler ->  value: " + value);
            if (value != null) {
                value = value.replace("uniform ", "");
                field.setDoubleValue(Math.abs(parseDouble(value)));
            } else {
                field.setDoubleValue(field.getDefaultValue());
            }
        }
    }

    class DoubleUniformPositiveFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private DoubleField field;

        public DoubleUniformPositiveFieldHandler(String key, DoubleField field) {
            this.key = key;
            this.field = field;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                dictionary.addUniform(key, Math.abs(field.getDoubleValue()));
                logger.trace("DoubleUniformFieldHandler -> " + dictionary.toString());
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key); // uniform value
            logger.trace("DoubleUniformFieldHandler ->  value: " + value);
            if (value != null) {
                value = value.replace("uniform ", "");
                field.setDoubleValue(Math.abs(parseDouble(value)));
            } else {
                field.setDoubleValue(field.getDefaultValue());
            }
        }
    }

    class DoubleConstantFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private DoubleField field;

        public DoubleConstantFieldHandler(String key, DoubleField field) {
            this.key = key;
            this.field = field;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                dictionary.addConstant(key, field.getDoubleValue());
                logger.trace("DoubleUniformFieldHandler -> value: {}", dictionary.lookup(key));
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key); // uniform value
            logger.trace("DoubleUniformFieldHandler ->  value: {}", value);
            if (value != null) {
                value = value.replace("constant ", "");
                field.setDoubleValue(parseDouble(value));
            } else {
                field.setDoubleValue(field.getDefaultValue());
            }
        }
    }

    class BooleanFieldHandler implements ActionListener, DictionaryListener {

        private JCheckBox check;
        private String key;
        private final boolean def;
		private boolean fireEvent;

        public BooleanFieldHandler(String key, JCheckBox check, boolean def, boolean fireEvent) {
            this.check = check;
            this.key = key;
            this.def = def;
			this.fireEvent = fireEvent;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean selected = check.isSelected();
            dictionary.add(key, selected ? "true" : "false");
            logger.trace("BooleanFieldHandler -> value: {}", dictionary.lookup(key));
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key);
            String correctedForYesNo = value == null ? String.valueOf(def) : value.equals("yes") ? "true" : value.equals("no") ? "false" : value;
            boolean b = Boolean.parseBoolean(correctedForYesNo);
            if (b != check.isSelected()) {
            	if(fireEvent){
            		check.doClick();
            	}
                check.setSelected(b);
            }
            logger.trace("BooleanFieldHandler ->  value: {}", value);
        }
    }

    class BooleanValuesFieldHandler implements ActionListener, DictionaryListener {

        private JCheckBox check;
        private String key;
        private final String trueValue;
        private final String falseValue;
        private boolean lightEvent;

        public BooleanValuesFieldHandler(String key, JCheckBox check, String trueValue, String falseValue, boolean lightEvent) {
            this.check = check;
            this.key = key;
            this.trueValue = trueValue;
            this.falseValue = falseValue;
            this.lightEvent = lightEvent;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean selected = check.isSelected();
            dictionary.add(key, selected ? trueValue : falseValue);
            logger.trace("BooleanValuesFieldHandler -> value: {}", dictionary.lookup(key));
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key);
            boolean selected = value != null && value.equals(trueValue);
            if (shouldClick(selected, check.isSelected())) {
                if(lightEvent){
                    check.setSelected(!check.isSelected());
                } else {
                    check.doClick();
                }
            }
            logger.trace("BooleanValuesFieldHandler ->  value: {}", value);
        }

        private boolean shouldClick(boolean select, boolean isSelected) {
            return (isSelected && !select) || (!isSelected && select);
        }
    }

    class FileFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private FileFieldPanel field;

        public FileFieldHandler(String key, FileFieldPanel field) {
            this.key = key;
            this.field = field;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                if (field.getFilePath() != null && !field.getFilePath().isEmpty()) {
                    String value = "\"" + field.getFilePath() + "\"";
                    dictionary.add(key, value);
                    logger.trace("FileFieldHandler -> value: {}", dictionary.lookup(key));
                }
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            loadFromDictionary();
        }

        private void loadFromDictionary() {
            String value = dictionary.lookup(key);
            if (value != null) {
                value = value.replace("\"", "");
                File file = new File(value);
                if (file.exists()) {
                    field.setFile(file);
                } else {
                    field.setFile(new File(""));
                    // throw new DictionaryError("File doesn't exist: " +
                    // value);
                }
            } else {
                field.setFile(new File(""));
            }
        }
    }

    class PointFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private DoubleField[] field;
        private FieldChangeListener listener;

        public PointFieldHandler(String key, DoubleField[] field, FieldChangeListener listener) {
            this.key = key;
            this.field = field;
            this.listener = listener;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                StringBuilder sb = new StringBuilder();
                sb.append("( ");
                for (int i = 0; i < field.length; i++) {
                    if (field[i].getValue() != null) {
                        sb.append(field[i].getDoubleValue() + " ");
                    } else {
                        sb.append("0 ");
                    }
                }
                sb.append(")");
                dictionary.add(key, sb.toString());
                logger.trace("PointFieldHandler -> value: {}", dictionary.lookup(key));
                if (listener != null) {
                    listener.fieldChanged(evt.getSource());
                }
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key);
            if (listener != null)
                listener.setAdjusting(true);
            if (value != null && !value.isEmpty()) {
                try {
                    String[] values = value.trim().substring(1, value.length() - 1).trim().split("\\s+");
                    for (int i = 0; i < values.length; i++) {
                        field[i].setDoubleValue(parseDouble(values[i]));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                logger.trace("PointFieldHandler ->  value: {}", value);
            } else {
                for (int i = 0; i < field.length; i++) {
                    field[i].setDoubleValue(0);
                }
            }
            if (listener != null)
                listener.setAdjusting(false);
        }
    }

    class PointUniformFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private DoubleField[] field;

        public PointUniformFieldHandler(String key, DoubleField[] field) {
            this.key = key;
            this.field = field;
            DictionaryModel.this.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                for (int i = 0; i < field.length; i++) {
                    if (Double.isInfinite(field[i].getDoubleValue())) { 
                        return;
                    }
                }
                dictionary.addUniform(key, DoubleField.toArray(field));
                logger.trace("PointFieldHandler -> value: {}", dictionary.lookup(key));
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            String value = dictionary.lookup(key);
            if (value != null) {
                if (value.startsWith("nonuniform")) {
                    for (int i = 0; i < field.length; i++) {
                        field[i].setDoubleValue(Double.POSITIVE_INFINITY);
                    }
                } else {
                    value = value.replace("uniform ", "");
                    String[] values = value.trim().substring(1, value.length() - 1).trim().split("\\s+");
                    for (int i = 0; i < values.length; i++) {
                        field[i].setDoubleValue(parseDouble(values[i]));
                    }
                }
                logger.trace("PointFieldHandler ->  value: {}", value);
            } else {
                for (int i = 0; i < field.length; i++) {
                    field[i].setDoubleValue(0);
                }
            }
        }
    }

    class SelectFieldHandler implements ActionListener, DictionaryListener {

        private JComboBox<?> combo;
        private String key;
        private SelectionValueConfigurator configurator;

        public SelectFieldHandler(String key, JComboBox<?> combo, SelectionValueConfigurator configurator) {
            this.combo = combo;
            this.key = key;
            this.configurator = configurator;
            DictionaryModel.this.addDictionaryListener(this);
            try {
                loadFromDictionary();
            } catch (DictionaryError e) {
                // e.printStackTrace();
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object item;
            if (combo instanceof JComboBoxController) {
                item = ((JComboBoxController) combo).getSelectedKey();
            } else {
                item = combo.getSelectedItem();
            }
            if (item != null && item instanceof String) {
                String value = (String) item;
                if (configurator != null)
                    value = configurator.write(value);
                if (value != null)
                    dictionary.add(key, value);
                logger.trace("SelectFieldHandler -> value: {}", dictionary.lookup(key));
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            loadFromDictionary();
        }

        public void loadFromDictionary() throws DictionaryError {
            if (dictionary.found(key)) {
                String value = dictionary.lookup(key);
                if (configurator != null)
                    value = configurator.read(value);

                if (combo instanceof JComboBoxController) {
                    ((JComboBoxController) combo).setSelectedKey(value);
                } else if (contains(value)) {
                    combo.setSelectedItem(value);
                } else if (value == null || "".equals(value)) {
                    combo.setSelectedIndex(-1);
                } else if (combo.getItemCount() > 0) {
                    combo.setSelectedIndex(0);
                    throw new DictionaryError(String.format("Missing %s value. Set to %s", value, combo.getItemAt(0)));
                }

                logger.trace("SelectFieldHandler -> value: {}", value);
            } else {
                combo.setSelectedIndex(-1);
                // throw new
                // DictionaryError(String.format("Missing %s key in %s dictionary",
                // key, dictionary.getName()));
            }
        }

        private boolean contains(String value) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (value.equals(combo.getItemAt(i))) {
                    return true;
                }
            }
            return false;
        }

    }

    public String getKey() {
        return key != null ? key : String.valueOf(hashCode());
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private int parseInt(String value) {
        try {
            return Double.valueOf(value).intValue();
        } catch (Exception e) {
            return 0;
        }
    }
}
