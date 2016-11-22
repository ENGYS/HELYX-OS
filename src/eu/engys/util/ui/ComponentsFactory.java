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
package eu.engys.util.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListDataListener;

import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.ui.builder.JCheckBoxController;
import eu.engys.util.ui.builder.JComboBoxController;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.IntegerField;
import eu.engys.util.ui.textfields.SpinnerField;
import eu.engys.util.ui.textfields.StringField;

public class ComponentsFactory {

    public static JLabel labelField(String text) {
        return new JLabel(text);
    }

    public static JTextArea labelArea() {
        return new JTextArea();
    }

    public static JLabel[] labelArrayField(String... strings) {
        JLabel[] value = new JLabel[strings.length];
        for (int i = 0; i < value.length; i++) {
            value[i] = labelField(strings[i]);
        }
        return value;
    }

    public static JCheckBox checkField() {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        return checkBox;
    }

    public static JCheckBox checkField(String string) {
        JCheckBox checkBox = new JCheckBox(string);
        checkBox.setOpaque(false);
        return checkBox;
    }

    public static JCheckBox checkField(boolean def) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(def);
        checkBox.setOpaque(false);
        return checkBox;
    }

    public static JCheckBox checkField(String string, boolean def) {
        JCheckBox checkBox = new JCheckBox(string);
        checkBox.setSelected(def);
        checkBox.setOpaque(false);
        return checkBox;
    }

    public static JCheckBox checkField(String string, boolean def, Color color) {
        JCheckBox checkBox = new JCheckBox(string);
        checkBox.setSelected(def);
        checkBox.setOpaque(false);
        checkBox.setForeground(color.darker());
        checkBox.putClientProperty("Synthetica.background", color);
        checkBox.putClientProperty("Synthetica.background.alpha", UIManager.get("Synthetica.checkbox.background.alpha"));
        return checkBox;
    }

    public static StringField stringField() {
        return new StringField();
    }

    public static StringField stringField(boolean checkEmptyStrings, boolean checkForbidden) {
        return new StringField(checkEmptyStrings, checkForbidden);
    }

    public static StringField stringField(String text, Integer columns) {
        return new StringField(text, columns, true, true);
    }

    public static StringField stringField(String text) {
        return new StringField(text);
    }

    public static StringField stringField(String text, boolean checkEmptyStrings, boolean checkForbidden) {
        return new StringField(text, checkEmptyStrings, checkForbidden);
    }

    public static JPasswordField passwordField() {
        return new JPasswordField(20);
    }

    public static SpinnerField spinnerField() {
        return new SpinnerField(0, Integer.MAX_VALUE, 0);
    }

    public static SpinnerField spinnerField(Integer def) {
        return new SpinnerField(0, Integer.MAX_VALUE, def);
    }

    public static SpinnerField spinnerField(Integer lb, Integer ub) {
        return new SpinnerField(lb, ub, Math.max(0, lb));
    }

    public static IntegerField intField() {
        return new IntegerField(0, Integer.MAX_VALUE, 0);
    }

    public static IntegerField intField(Integer lb, Integer ub) {
        return new IntegerField(lb, ub, Math.max(0, lb));
    }

    public static IntegerField intField(Integer lb, Integer ub, Integer value, boolean checkEmptyValue) {
        return new IntegerField(lb, ub, value, checkEmptyValue);
    }

    public static IntegerField intField(Integer def) {
        return new IntegerField(0, Integer.MAX_VALUE, def);
    }

    public static IntegerField[] intArrayField(Integer dimensions) {
        IntegerField[] value = new IntegerField[dimensions];
        for (int i = 0; i < value.length; i++) {
            value[i] = intField();
        }
        return value;
    }

    public static DoubleField doubleField() {
        return new DoubleField(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0);
    }

    public static DoubleField doubleField(Double def) {
        return new DoubleField(-Double.MAX_VALUE, Double.MAX_VALUE, def);
    }

    public static DoubleField doubleField(Integer places) {
        return new DoubleField(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, places);
    }

    public static DoubleField doubleField(Integer places, Double def) {
        return new DoubleField(-Double.MAX_VALUE, Double.MAX_VALUE, def, places);
    }

    public static DoubleField doubleField(Double lb, Double ub) {
        return new DoubleField(lb, ub, lb);
    }

    public static DoubleField doubleField(Double def, Double lb, Double ub) {
        return new DoubleField(lb, ub, def);
    }

    public static DoubleField[] doublePointField() {
        return new DoubleField[] { doubleField(), doubleField(), doubleField() };
    }

    public static DoubleField[] doublePointField(Integer places) {
        return new DoubleField[] { doubleField(places), doubleField(places), doubleField(places) };
    }

    public static DoubleField[] doublePointField(Double d1, Double d2, Double d3) {
        return new DoubleField[] { doubleField(d1), doubleField(d2), doubleField(d3) };
    }

    public static DoubleField[] doublePointField(Double d1, Double d2, Double d3, Double lb, Double ub ) {
        return new DoubleField[] { doubleField(d1, lb, ub), doubleField(d2,lb, ub), doubleField(d3, lb, ub) };
    }

    public static DoubleField[] doublePointField(Integer places, Double d) {
        return new DoubleField[] { doubleField(places, d), doubleField(places, d), doubleField(places, d) };
    }

    public static DoubleField[] doubleArrayField(Integer dimensions) {
        DoubleField[] value = new DoubleField[dimensions];
        for (int i = 0; i < value.length; i++) {
            value[i] = doubleField();
        }
        return value;
    }

    public static DoubleField[] doubleArrayField(Integer dimensions, Integer places) {
        DoubleField[] value = new DoubleField[dimensions];
        for (int i = 0; i < value.length; i++) {
            value[i] = doubleField(places);
        }
        return value;
    }

    public static class SelectField<T> extends JComboBox<T> {
        
        private final class SelectFieldCellRenderer implements ListCellRenderer<T> {
            
            private ListCellRenderer<? super T> delegate;
            
            private HashMap<T, Icon> iconFromKey = new HashMap<>();
            private HashMap<T, String> labelFromKey = new HashMap<>();
            
            private SelectFieldCellRenderer(ListCellRenderer<? super T> renderer) {
                this.delegate = renderer;
            }
            
            @Override
            public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
                delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String text = labelFromKey.get(value);
                Icon icon = iconFromKey.get(value);
                
                if (delegate instanceof JLabel) {
                    JLabel label = (JLabel) delegate;
                    label.setText(text);
                    label.setIcon(icon);
                    return label;
                } else {
                    return new JLabel(text, icon, SwingConstants.LEFT);
                }
            }

            public void addItem(T key, String label, Icon icon) {
                labelFromKey.put(key, label);
                iconFromKey.put(key, icon);
            }

            public void removeAllItems() {
                labelFromKey.clear();
                iconFromKey.clear();
            }
        }

        private SelectFieldCellRenderer renderer;
        
        public SelectField(T[] keys) {
            this();
            for (T string : keys) {
                addItem(string);
            }
        }

        public SelectField(T[] keys, final String[] labels, final Icon[] icons) {
            this();
            for (int i = 0; i < keys.length; i++) {
                addItem(keys[i], labels[i], icons[i]);
            }
        }
        
        public SelectField() {
            super();
        }

        @Override
        protected void fireActionEvent() {
            super.fireActionEvent();
            firePropertyChange("value", null, getSelectedItem());
        }
        
        @Override
        public void addItem(T item) {
            super.addItem(item);
            setMaximumRowCount(getItemCount());
        }
        
        public void addItem(T key, String label, Icon icon) {
            addItem(key);
            if (renderer == null) {
                renderer = new SelectFieldCellRenderer(getRenderer());
                setRenderer(renderer);
            }
            renderer.addItem(key, label, icon);
        }
        
        @Override
        public void removeAllItems() {
            super.removeAllItems();
            if (renderer != null) { 
                renderer.removeAllItems();
            }
        }
    }
    
    public static <T> SelectField<T> selectField() {
        SelectField<T> combo = new SelectField<T>();
        return combo;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> JComboBox<T> selectField(final ListBuilder builder) {
        final JComboBox<T> combo = selectField();
        combo.setModel(new ComboBoxModel() {

            private Object selected;

            @Override
            public void removeListDataListener(ListDataListener l) {
            }

            @Override
            public int getSize() {
                return builder.getSourceElements().length;
            }

            @Override
            public Object getElementAt(int index) {
                return builder.getSourceElements()[index];
            }

            @Override
            public void addListDataListener(ListDataListener l) {
            }

            @Override
            public void setSelectedItem(Object anItem) {
                this.selected = anItem;
                combo.revalidate();
                combo.repaint();
            }

            @Override
            public Object getSelectedItem() {
                return selected;
            }
        });
        return combo;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> JComboBox<T> selectField(final ListModel<T> listModel) {
        final JComboBox<T> combo = selectField();
        combo.setModel(new ComboBoxModel() {
            
            private Object selected;
            
            @Override
            public void removeListDataListener(ListDataListener l) {
            }
            
            @Override
            public int getSize() {
                return listModel.getSize();
            }
            
            @Override
            public Object getElementAt(int index) {
                return listModel.getElementAt(index);
            }
            
            @Override
            public void addListDataListener(ListDataListener l) {
            }
            
            @Override
            public void setSelectedItem(Object anItem) {
                this.selected = anItem;
                combo.revalidate();
                combo.repaint();
            }
            
            @Override
            public Object getSelectedItem() {
                return selected;
            }
        });
        return combo;
    }

    @SuppressWarnings("unchecked")
    public static <T> JComboBox<T> selectField(T... items) {
        JComboBox<T> combo = new SelectField<>(items);
        return combo;
    }
    
    public static JComboBox<String> selectField(String... items) {
        JComboBox<String> combo = new SelectField<>(items);
        return combo;
    }

    public static <E> JComboBox<E> selectField(final E[] keys, final String[] labels) {
        return selectField(keys, labels, new Icon[keys.length]);
    }

    public static JComboBox<String> selectField(final String[] keys, final String[] labels) {
        return selectField(keys, labels, new Icon[keys.length]);
    }

    public static <E> SelectField<E> selectField(final E[] keys, final String[] labels, final Icon[] icons) {
        SelectField<E> combo = new SelectField<>(keys, labels, icons);
        return combo;
    }

    private static JComboBoxWithItemsSupport<String> selectFieldWithItemSupport() {
        JComboBoxWithItemsSupport<String> combo = new JComboBoxWithItemsSupport<String>() {
            @Override
            protected void fireActionEvent() {
                super.fireActionEvent();
                firePropertyChange("value", null, getSelectedItem());
            }
        };
        return combo;
    }

    public static JComboBoxWithItemsSupport<String> selectFieldWithItemSupport(final String[] keys) {
        JComboBoxWithItemsSupport<String> combo = selectFieldWithItemSupport();
        for (String string : keys) {
            combo.addItem(string);
        }
        return combo;
    }

    public static JComboBoxWithItemsSupport<String> selectFieldWithItemSupport(final String[] keys, final String[] labels) {
        JComboBoxWithItemsSupport<String> combo = selectFieldWithItemSupport();
        for (int i = 0; i < keys.length; i++) {
            combo.addItem(keys[i], labels[i]);
        }
        return combo;
    }

    public static JComboBoxController comboBoxControllerField() {
        JComboBoxController combo = new JComboBoxController() {
            @Override
            protected void fireActionEvent() {
                super.fireActionEvent();
                firePropertyChange("value", null, getSelectedItem());
            }
        };
        return combo;
    }

    public static JCheckBoxController checkBoxControllerField(String name) {
        JCheckBoxController combo = new JCheckBoxController(name) {
            @Override
            protected void fireActionPerformed(ActionEvent event) {
                super.fireActionPerformed(event);
                firePropertyChange("value", null, isSelected());
            }
        };
        return combo;
    }

    public static RadioFieldPanel radioField(final String[] keys, final String[] items) {
        RadioFieldPanel panel = new RadioFieldPanel();
        for (int i = 0; i < keys.length; i++) {
            panel.addButton(items[i], keys[i]);
        }
        if (keys.length == 1) {
            panel.select(keys[0]);
        }
        return panel;
    }

    public static RadioFieldPanel radioField(String... items) {
        RadioFieldPanel panel = new RadioFieldPanel();
        for (String string : items) {
            panel.addButton(string);
        }
        if (items.length == 1) {
            panel.select(items[0]);
        }
        return panel;
    }

    public static ListFieldPanel listField(ListBuilder listBuilder) {
        return new ListFieldPanel(listBuilder);
    }

    public static FileFieldPanel fileField(SelectionMode mode, String tooltip, boolean selectFile) {
        return fileField(mode, tooltip, "", selectFile);
    }

    public static FileFieldPanel fileField(SelectionMode mode, String tooltip, String prompt, boolean selectFile) {
        return new FileFieldPanel(mode, tooltip, prompt, selectFile);
    }

}
