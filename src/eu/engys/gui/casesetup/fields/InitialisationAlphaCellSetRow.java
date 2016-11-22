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
package eu.engys.gui.casesetup.fields;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.textfields.DoubleField;

public class InitialisationAlphaCellSetRow extends CellSetRow {

    private DoubleField valueField;
    private double value;
    private String fieldName;
    private SliderChangeListener valueSliderListener;
    private TextFieldChangeListener valueFieldListener;

    public InitialisationAlphaCellSetRow(Model model, String fieldName, Surface surface, double value, ProgressMonitor monitor) {
        super(model, surface, monitor);
        this.fieldName = fieldName;
        this.value = value;
        layoutComponents();
        _load();
    }
    
    @Override
    protected JPanel createValuePanel() {
        valueField = ComponentsFactory.doubleField(0.0, 0.0, 1.0);
        valueField.setName(CELLSET_VALUE_NAME + "." + surface.getName());
        JSlider valueSlider = createSlider();
        valueSlider.addChangeListener(valueSliderListener = new SliderChangeListener(valueField));
        valueField.addPropertyChangeListener(valueFieldListener = new TextFieldChangeListener(valueSlider));

        JPanel valuePanel = new JPanel(new GridBagLayout());
        valuePanel.setOpaque(false);
        valuePanel.add(valueSlider, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        valuePanel.add(valueField, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(12, 5, 0, 0), 0, 0));

        valuePanel.add(label(getLeftLabel()), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        valuePanel.add(label(getRightLabel()), new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        valuePanel.add(label(""), new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        return valuePanel;
    }

    private JSlider createSlider() {
        JSlider valueSlider = new JSlider(JSlider.HORIZONTAL, 0, fieldName.equals(Fields.ALPHA_1) ? 1 : 100, 0);
        valueSlider.setName(CELLSET_SLIDER_NAME + "." + surface.getName());
        valueSlider.setMajorTickSpacing(10);
        valueSlider.setPaintTicks(true);
        valueSlider.setPaintTrack(true);
        valueSlider.setPaintLabels(false);
        return valueSlider;
    }
    
    private class TextFieldChangeListener implements PropertyChangeListener {

        private JSlider slider;

        public TextFieldChangeListener(JSlider slider) {
            this.slider = slider;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                slider.removeChangeListener(valueSliderListener);
                DoubleField field = (DoubleField) evt.getSource();
                slider.setValue((int) (field.getDoubleValue() * 100));
                slider.addChangeListener(valueSliderListener);
            }
        }
    };

    private class SliderChangeListener implements ChangeListener {

        private DoubleField field;

        public SliderChangeListener(DoubleField field) {
            this.field = field;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            field.removePropertyChangeListener(valueFieldListener);
            JSlider slider = (JSlider) e.getSource();
            double value = slider.getValue();
            field.setDoubleValue(value / slider.getMaximum());
            field.addPropertyChangeListener(valueFieldListener);
        }
    };

    private String getLeftLabel() {
        String label;
        if (model.getState().getMultiphaseModel().isMultiphase()) {
            if (fieldName.equals(Fields.ALPHA_1)) {
                label = model.getMaterials().get(1).getName();
            } else if (fieldName.startsWith(Fields.ALPHA + ".")) {
                label = "No " + Fields.PHASE_OS(fieldName);
            } else if (fieldName.startsWith(Fields.ALPHA)) {
                label = "No " + Fields.PHASE(fieldName);
            } else {
                label = "No " + fieldName;
            }
        } else {
            label = "No " + fieldName;
        }
        return label;
    }

    private String getRightLabel() {
        String label;
        if (model.getState().getMultiphaseModel().isMultiphase()) {
            if (fieldName.equals(Fields.ALPHA_1)) {
                label = model.getMaterials().get(0).getName();
            } else if (fieldName.startsWith(Fields.ALPHA)) {
                label = Fields.PHASE(fieldName);
            } else {
                label = fieldName;
            }
        } else {
            label = fieldName;
        }
        return label;
    }

    private JLabel label(String name) {
        return new JLabel(name);
    }

    @Override
    protected void load() {
        valueField.setDoubleValue(value);        
    }
    
    @Override
    protected void newBox() {
        valueField.setDoubleValue(0);        
    }

    public Double getValue() {
        return value = valueField.getDoubleValue();
    }
}
