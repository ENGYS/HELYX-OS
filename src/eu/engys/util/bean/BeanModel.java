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
package eu.engys.util.bean;

import static eu.engys.util.ui.ComponentsFactory.checkField;
import static eu.engys.util.ui.ComponentsFactory.doubleArrayField;
import static eu.engys.util.ui.ComponentsFactory.doubleField;
import static eu.engys.util.ui.ComponentsFactory.doublePointField;
import static eu.engys.util.ui.ComponentsFactory.intArrayField;
import static eu.engys.util.ui.ComponentsFactory.intField;
import static eu.engys.util.ui.ComponentsFactory.radioField;
import static eu.engys.util.ui.ComponentsFactory.stringField;
import static eu.engys.util.ui.RadioFieldPanel.PROPERTY_NAME;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.binder.Binders;
import com.jgoodies.binding.value.AbstractWrappedValueModel;
import com.jgoodies.binding.value.BindingConverter;
import com.jgoodies.binding.value.ValueModel;

import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.ListBuilder;
import eu.engys.util.ui.ListFieldPanel;
import eu.engys.util.ui.RadioFieldPanel;
import eu.engys.util.ui.builder.JCheckBoxController;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.IntegerField;
import eu.engys.util.ui.textfields.StringField;

public class BeanModel<T> extends PresentationModel<T> implements BeanModelListener {

    private static final String VALUE_PROPERTY = "value";
    private Map<String, List<BeanModelListener>> listeners = new HashMap<>();

    public BeanModel() {
        super();
        addListener();
    }

    public BeanModel(T t) {
        super(t);
        addListener();
    }
    
    @Override
    public void setBean(T newBean) {
//        System.out.println("BeanModel.setBean() " + newBean);
        super.setBean(newBean);
    }

    private void addListener() {
        addPropertyChangeListener("afterBean", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Object obj = evt.getNewValue();
                for (String propertyName : listeners.keySet()) {
                    for (BeanModelListener listener : listeners.get(propertyName)) {

                        Object value = getValue(propertyName);
                        if (value != null) {
                            listener.beanModelChanged(value);
                        }
                        // try {
                        // PropertyDescriptor pd = new PropertyDescriptor(propertyName, obj.getClass());
                        // Object value = pd.getReadMethod().invoke(obj, (Object[]) null);
                        // } catch (Exception e) {
                        // e.printStackTrace();
                        // }
                    }
                }

            }
        });
    }

    public BeanModelListener getListener(String propertyName, Object bean) {
        for (BeanModelListener listener : listeners.get(propertyName)) {
            if (listener.getBeanObject().getClass() == bean.getClass()) {
                return listener;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void beanModelChanged(Object value) {
        if (value != null && value.getClass() == getBean().getClass()) {
            setBean((T) value);
        }
    }

    @Override
    public T getBeanObject() {
        return getBean();
    }

    @Override
    public BeanModel<T> getBeanModel() {
        return this;
    }

    public void addBeanModelListener(String propertyName, BeanModelListener listener) {
        if (!listeners.containsKey(propertyName)) {
            listeners.put(propertyName, new ArrayList<BeanModelListener>());
        }
        listeners.get(propertyName).add(listener);
    }

    /*
     * BINDING
     */
    public StringField bindLabel(String key) {
        StringField field = stringField();
        Binders.binderFor(this).bindBeanProperty(key).to(field);
        return field;
    }

    public JCheckBox bindBoolean(String key) {
        JCheckBox field = checkField();
        Binders.binderFor(this).bindBeanProperty(key).to(field);
        return field;
    }

    public IntegerField bindInteger(String key) {
        IntegerField field = intField(-Integer.MAX_VALUE, Integer.MAX_VALUE);
        Binders.binderFor(this).bindBeanProperty(key).to(field);
        return field;
    }

    public IntegerField bindInteger(String key, Integer lb, Integer ub) {
        IntegerField field = intField(lb, ub);
        Binders.binderFor(this).bindBeanProperty(key).to(field);
        return field;
    }

    public IntegerField bindIntegerPositive(String key) {
        IntegerField field = intField(0, Integer.MAX_VALUE);
        Binders.binderFor(this).bindBeanProperty(key).to(field);
        return field;
    }

    public DoubleField bindDouble(String key) {
        DoubleField field = doubleField();
        Binders.binderFor(this).bindBeanProperty(key).to(field);
        return field;
    }

    public DoubleField bindDouble(String key, Integer places) {
        DoubleField field = doubleField(places);
        Binders.binderFor(this).bindBeanProperty(key).to(field);
        return field;
    }

    public DoubleField bindDouble(String key, Double def) {
        DoubleField field = doubleField(def);
        Binders.binderFor(this).bindBeanProperty(key).to(field);
        return field;
    }

    public DoubleField bindDouble(String key, double lb, double ub) {
        DoubleField field = doubleField(lb, ub);
        Binders.binderFor(this).bindBeanProperty(key).to(field);
        return field;
    }

    public DoubleField bindDoublePositive(String key) {
        DoubleField field = doubleField(0.0, Double.MAX_VALUE);
        Binders.binderFor(this).bindBeanProperty(key).to(field);
        return field;
    }

    public DoubleField bindDoubleNegative(String key) {
        DoubleField field = doubleField(-Double.MAX_VALUE, 0.0);
        Binders.binderFor(this).bindBeanProperty(key).to(field);
        return field;
    }

    public JComponent bindChoice(String key, String[] keys, String[] labels) {
        RadioFieldPanel field = radioField(keys, labels);
        JComponent c = new RadioWrapper(field);
        ValueModel valueModel = getComponentModel(key);
        Bindings.bind(c, VALUE_PROPERTY, valueModel);
        return field;
    }

    public ListFieldPanel bindList(String key, ListBuilder listBuilder) {
        ListFieldPanel field = new ListFieldPanel(listBuilder);
        AbstractWrappedValueModel m = getComponentModel(key);
        Bindings.bind(field, ListFieldPanel.VALUES, m);
        return field;
    }

    public JCheckBoxController bindCheckBoxController(String key, String label) {
        JCheckBoxController check = ComponentsFactory.checkBoxControllerField(label);
        Binders.binderFor(this).bindBeanProperty(key).to(check);
        return check;
    }

    public <E> JComboBox<E> bindSelection(String key, ListModel<E> listModel) {
        JComboBox<E> field = ComponentsFactory.selectField(listModel);
        Binders.binderFor(this).bindBeanProperty(key).asSelectionIn(listModel).to(field);
        return field;
    }

    @SuppressWarnings("unchecked")
    public <E> JComboBox<E> bindSelection(String key, E... items) {
        JComboBox<E> field = ComponentsFactory.selectField(items);
        Binders.binderFor(this).bindBeanProperty(key).asSelectionIn(items).to(field);
        return field;
    }

    public IntegerField[] bindIntegerArray(String key, int dimensions) {
        IntegerField[] fields = intArrayField(dimensions);
        for (int i = 0; i < fields.length; i++) {
            IntegerField f = fields[i];
            f.setColumns(1);
        }

        JComponent c = new IntegerWrapper(fields);
        ValueModel valueModel = getComponentModel(key);
        Bindings.bind(c, VALUE_PROPERTY, valueModel);

        return fields;
    }

    public DoubleField[] bindDoubleArray(String key, int dimensions) {
        DoubleField[] fields = doubleArrayField(dimensions);
        for (int i = 0; i < fields.length; i++) {
            DoubleField f = fields[i];
            f.setColumns(1);
        }

        JComponent c = new DoubleWrapper(fields);
        ValueModel valueModel = getComponentModel(key);
        Bindings.bind(c, VALUE_PROPERTY, valueModel);

        return fields;
    }

    @SuppressWarnings("unchecked")
    public <E> BeanComboBoxController<E> bindComboController(final String key, BeanModel<? extends E>... models) {
        for (BeanModel<? extends E> model : models) {
            addBeanModelListener(key, model);
        }
        BeanComboBoxController<E> combo = new BeanComboBoxController<>(models);
        BindingConverter<E, BeanModel<? extends E>> converter = new BindingConverter<E, BeanModel<? extends E>>() {
            @Override
            public BeanModel<? extends E> targetValue(E sourceValue) {
                BeanModelListener listener = getListener(key, sourceValue);
                return listener != null ? (BeanModel<? extends E>) listener.getBeanModel() : null;
            }

            @Override
            public E sourceValue(BeanModel<? extends E> targetValue) {
                return targetValue == null ? null : targetValue.getBean();
            }
        };
        Binders.binderFor(this).bindBeanProperty(key).converted(converter).asSelectionIn(models).to(combo);

        return combo;
    }

    public DoubleField[] bindPoint(String key) {
        return bindPoint(key, DoubleField.DEFAULT_PLACES);
    }

    public DoubleField[] bindPoint(String key, Integer places) {
        DoubleField[] field = doublePointField(places);
        JComponent c = new DoubleWrapper(field);
        ValueModel valueModel = getComponentModel(key);
        Bindings.bind(c, "value", valueModel);
        
        return field;
    }

    public static class RadioWrapper extends JComponent {

        private RadioFieldPanel panel;

        public RadioWrapper(RadioFieldPanel panel) {
            this.panel = panel;
            panel.addPropertyChangeListener(PROPERTY_NAME, new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    firePropertyChange(VALUE_PROPERTY, null, evt.getNewValue());
                }
            });
        }

        public String getValue() {
            return panel.getSelectedKey();
        }

        public void setValue(String value) {
            String oldValue = getValue();
            panel.select(value);
            firePropertyChange(VALUE_PROPERTY, oldValue, value);
        }
    }

    public static class DoubleWrapper extends JComponent {
        private DoubleField[] fields;
        
        public DoubleWrapper(DoubleField[] fields) {
            this.fields = fields;
            for (DoubleField df : fields) {
                df.addPropertyChangeListener(VALUE_PROPERTY, new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        firePropertyChange(VALUE_PROPERTY, getValue(), getValue());
                    }
                });
            }
        }

        public double[] getValue() {
            double[] value = new double[fields.length];
            for (int i = 0; i < value.length; i++) {
                value[i] = fields[i].getDoubleValue();
            }
            return value;
        }

        public void setValue(double[] value) {
            double[] oldValue = getValue();
            if (value != null) {
                for (int i = 0; i < value.length; i++) {
                    fields[i].setDoubleValue(value[i]);
                }
            }
            firePropertyChange(VALUE_PROPERTY, oldValue, value);
        }
    }
    
    public static class IntegerWrapper extends JComponent {
        private IntegerField[] fields;

        public IntegerWrapper(IntegerField[] fields) {
            this.fields = fields;
            for (IntegerField df : fields) {
                df.addPropertyChangeListener(VALUE_PROPERTY, new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        firePropertyChange(VALUE_PROPERTY, getValue(), getValue());
                    }
                });
            }
        }
        
        public int[] getValue() {
            int[] value = new int[fields.length];
            for (int i = 0; i < value.length; i++) {
                value[i] = fields[i].getIntValue();
            }
            return value;
        }
        
        public void setValue(int[] value) {
            int[] oldValue = getValue();
            if (value != null) {
                for (int i = 0; i < value.length; i++) {
                    fields[i].setIntValue(value[i]);
                }
            }
            firePropertyChange(VALUE_PROPERTY, oldValue, value);
        }
    }
}
