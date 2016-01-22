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

package eu.engys.gui.casesetup.fields;

import static eu.engys.core.project.geometry.Surface.CENTRE_KEY;
import static eu.engys.core.project.geometry.Surface.INNER_RADIUS_KEY;
import static eu.engys.core.project.geometry.Surface.MAX_KEY;
import static eu.engys.core.project.geometry.Surface.MIN_KEY;
import static eu.engys.core.project.geometry.Surface.OUTER_RADIUS_KEY;
import static eu.engys.core.project.geometry.Surface.POINT1_KEY;
import static eu.engys.core.project.geometry.Surface.POINT2_KEY;
import static eu.engys.core.project.geometry.Surface.RADIUS_KEY;
import static eu.engys.core.project.system.SetFieldsDict.BOX_TO_CELL_KEY;
import static eu.engys.core.project.system.SetFieldsDict.CYLINDER_TO_CELL_KEY;
import static eu.engys.core.project.system.SetFieldsDict.RING_TO_CELL_KEY;
import static eu.engys.core.project.system.SetFieldsDict.SPHERE_TO_CELL_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.VALUE_KEY;
import static eu.engys.gui.mesh.panels.GeometriesPanelBuilder.CENTRE_LABEL;
import static eu.engys.gui.mesh.panels.GeometriesPanelBuilder.INNER_RADIUS_LABEL;
import static eu.engys.gui.mesh.panels.GeometriesPanelBuilder.MAX_LABEL;
import static eu.engys.gui.mesh.panels.GeometriesPanelBuilder.MIN_LABEL;
import static eu.engys.gui.mesh.panels.GeometriesPanelBuilder.OUTER_RADIUS_LABEL;
import static eu.engys.gui.mesh.panels.GeometriesPanelBuilder.POINT_1_LABEL;
import static eu.engys.gui.mesh.panels.GeometriesPanelBuilder.POINT_2_LABEL;
import static eu.engys.gui.mesh.panels.GeometriesPanelBuilder.RADIUS_LABEL;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FieldChangeListener;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.surface.Box;
import eu.engys.core.project.geometry.surface.Cylinder;
import eu.engys.core.project.geometry.surface.Ring;
import eu.engys.core.project.geometry.surface.Sphere;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.AddSurfaceEvent;
import eu.engys.gui.events.view3D.ChangeSurfaceEvent;
import eu.engys.gui.events.view3D.RemoveSurfaceEvent;
import eu.engys.gui.view3D.BoxEventButton;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;

public class CellSetRow extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(CellSetRow.class);

    public static final String BOX = "Box";
    public static final String SPHERE = "Sphere";
    public static final String CYLINDER = "Cylinder";
    public static final String RING = "Ring";

    public static final String CELLSET_NAME = "cellset";
    public static final String CELLSET_ROW_NAME = "cellset.row";
    public static final String CELLSET_VALUE_NAME = "cellset.value";
    public static final String CELLSET_SLIDER_NAME = "cellset.slider";

    private DictionaryModel boxModel = new DictionaryModel(getDefaultBoxModelDictionary());
    private DictionaryModel sphereModel = new DictionaryModel(getDefaultSphereModelDictionary());
    private DictionaryModel cylinderModel = new DictionaryModel(getDefaultCylinderModelDictionary());
    private DictionaryModel ringModel = new DictionaryModel(getDefaultRingModelDictionary());
    private FieldChangeListener listener;
    private JPanel coordinatesPanel;
    private Dictionary loadedDictionary;
    private Surface surface;
    private Integer index;
    private JComboBox<String> choice;
    private DoubleField valueField;
    private SliderChangeListener valueSliderListener;
    private TextFieldChangeListener valueFieldListener;

    private BoxEventButton showBoxButton;

    private Model model;
    private String fieldName;

    public CellSetRow(Model model, String fieldName, Dictionary dictionary, Integer index) {
        super(new BorderLayout());
        this.model = model;
        this.fieldName = fieldName;
        this.loadedDictionary = dictionary;
        this.index = index;
        this.listener = new ValueFieldChangeListener();
        setName(CELLSET_ROW_NAME + "." + index);
        layoutComponents();
        load();
    }

    private void layoutComponents() {
        setBorder(BorderFactory.createTitledBorder(BOX));
        setOpaque(false);
        coordinatesPanel = createCoordinatersPanel();
        choice = create3DCombo();
        JPanel valuePanel = createValuePanel();

        add(choice, BorderLayout.NORTH);
        add(coordinatesPanel, BorderLayout.CENTER);
        add(valuePanel, BorderLayout.SOUTH);
    }

    private JPanel createValuePanel() {
        valueField = ComponentsFactory.doubleField(0.0, 0.0, 1.0);
        valueField.setName(CELLSET_VALUE_NAME + "." + index);
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

    private void load() {
        if (!loadedDictionary.isEmpty()) {
            Dictionary newDict = new Dictionary("");
            if (loadedDictionary.found("box")) {
                String boxValue = loadedDictionary.lookup("box");
                String min = boxValue.substring(0, boxValue.indexOf(")") + 1).trim();
                String max = boxValue.replace(min, "").trim();
                newDict.add(MIN_KEY, min);
                newDict.add(MAX_KEY, max);
                choice.setSelectedItem(BOX);
                boxModel.setDictionary(newDict);
                surface.setGeometryDictionary(boxModel.getDictionary());
            } else if (loadedDictionary.found(CENTRE_KEY) && loadedDictionary.found(RADIUS_KEY)) {// sphere
                newDict.add(CENTRE_KEY, loadedDictionary.lookup(CENTRE_KEY));
                newDict.add(RADIUS_KEY, loadedDictionary.lookup(RADIUS_KEY));
                choice.setSelectedItem(SPHERE);
                sphereModel.setDictionary(newDict);
                surface.setGeometryDictionary(sphereModel.getDictionary());
            } else if (loadedDictionary.found(POINT1_KEY) && loadedDictionary.found(RADIUS_KEY)) {// cylinder
                newDict.add(RADIUS_KEY, loadedDictionary.lookup(RADIUS_KEY));
                newDict.add(POINT1_KEY, loadedDictionary.lookup(POINT1_KEY));
                newDict.add(POINT2_KEY, loadedDictionary.lookup(POINT2_KEY));
                choice.setSelectedItem(CYLINDER);
                cylinderModel.setDictionary(newDict);
                surface.setGeometryDictionary(cylinderModel.getDictionary());
            } else if (loadedDictionary.found(INNER_RADIUS_KEY) && loadedDictionary.found(OUTER_RADIUS_KEY)) {// ring
                newDict.add(POINT1_KEY, loadedDictionary.lookup(POINT1_KEY));
                newDict.add(POINT2_KEY, loadedDictionary.lookup(POINT2_KEY));
                newDict.add(INNER_RADIUS_KEY, loadedDictionary.lookup(INNER_RADIUS_KEY));
                newDict.add(OUTER_RADIUS_KEY, loadedDictionary.lookup(OUTER_RADIUS_KEY));
                choice.setSelectedItem(RING);
                ringModel.setDictionary(newDict);
                surface.setGeometryDictionary(ringModel.getDictionary());
            }

            if (loadedDictionary.found(VALUE_KEY)) {
                String value = loadedDictionary.lookup(VALUE_KEY);
                valueField.setDoubleValue(Double.parseDouble(value));
            }
        } else {
            addNewBox();
        }
    }

    private void addNewBox() {
        surface = model.getGeometry().getFactory().newSurface(Box.class, "Box_" + index);
        boxModel.setDictionary(surface.getGeometryDictionary());
    }

    private JSlider createSlider() {
        JSlider valueSlider = new JSlider(JSlider.HORIZONTAL, 0, fieldName.equals(Fields.ALPHA_1) ? 1 : 100, 0);
        valueSlider.setName(CELLSET_SLIDER_NAME + "." + index);
        valueSlider.setMajorTickSpacing(10);
        valueSlider.setPaintTicks(true);
        valueSlider.setPaintTrack(true);
        valueSlider.setPaintLabels(false);
        return valueSlider;
    }

    private JComboBox<String> create3DCombo() {
        choice = new JComboBox<String>(new String[] { BOX, SPHERE, CYLINDER, RING });
        choice.setName(CELLSET_NAME + "." + index);
        choice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) coordinatesPanel.getLayout();
                Surface existingSurface = surface;
                if (existingSurface != null) {
                    EventManager.triggerEvent(this, new RemoveSurfaceEvent(surface));
                }
                if (choice.getSelectedIndex() == 0) {
                    layout.show(coordinatesPanel, BOX);
                    setBorder(BorderFactory.createTitledBorder(BOX));
                    surface = model.getGeometry().getFactory().newSurface(Box.class, BOX + "_" + index);
                    boxModel.setDictionary(surface.getGeometryDictionary());
                } else if (choice.getSelectedIndex() == 1) {
                    layout.show(coordinatesPanel, SPHERE);
                    setBorder(BorderFactory.createTitledBorder(SPHERE));
                    surface = model.getGeometry().getFactory().newSurface(Sphere.class, SPHERE + "_" + index);
                    sphereModel.setDictionary(surface.getGeometryDictionary());
                } else if (choice.getSelectedIndex() == 2) {
                    layout.show(coordinatesPanel, CYLINDER);
                    setBorder(BorderFactory.createTitledBorder(CYLINDER));
                    surface = model.getGeometry().getFactory().newSurface(Cylinder.class, CYLINDER + "_" + index);
                    cylinderModel.setDictionary(surface.getGeometryDictionary());
                } else if (choice.getSelectedIndex() == 3) {
                    layout.show(coordinatesPanel, RING);
                    setBorder(BorderFactory.createTitledBorder(RING));
                    surface = model.getGeometry().getFactory().newSurface(Ring.class, RING + "_" + index);
                    ringModel.setDictionary(surface.getGeometryDictionary());
                }
                if (existingSurface != null) {
                    EventManager.triggerEvent(this, new AddSurfaceEvent(surface));
                }
            }
        });
        return choice;
    }

    private JPanel createCoordinatersPanel() {
        CardLayout c = new CardLayout();
        JPanel p = new JPanel(c);
        p.setOpaque(false);
        p.add(getBoxPanel(), BOX);
        p.add(getSpherePanel(), SPHERE);
        p.add(getCylinderPanel(), CYLINDER);
        p.add(getRingPanel(), RING);
        c.show(p, BOX);
        return p;
    }

    private JPanel getBoxPanel() {
        PanelBuilder boxBuilder = new PanelBuilder();
        DoubleField[] boxMin = boxModel.bindPoint(MIN_KEY, listener);
        DoubleField[] boxMax = boxModel.bindPoint(MAX_KEY, listener);

        showBoxButton = new BoxEventButton(boxMin, boxMax);

        boxBuilder.addComponent(MIN_LABEL, boxMin[0], boxMin[1], boxMin[2], showBoxButton);
        boxBuilder.addComponentAndSpan(MAX_LABEL, boxMax);

        return boxBuilder.getPanel();
    }

    private JPanel getSpherePanel() {
        PanelBuilder sphereBuilder = new PanelBuilder();
        sphereBuilder.addComponent(CENTRE_LABEL, sphereModel.bindPoint(CENTRE_KEY, listener));
        sphereBuilder.addComponent(RADIUS_LABEL, sphereModel.bindDouble(RADIUS_KEY, listener));
        return sphereBuilder.getPanel();

    }

    private JPanel getCylinderPanel() {
        PanelBuilder cylinderBuilder = new PanelBuilder();
        cylinderBuilder.addComponent(POINT_1_LABEL, cylinderModel.bindPoint(POINT1_KEY, listener));
        cylinderBuilder.addComponent(POINT_2_LABEL, cylinderModel.bindPoint(POINT2_KEY, listener));
        cylinderBuilder.addComponent(RADIUS_LABEL, cylinderModel.bindDouble(RADIUS_KEY, listener));
        return cylinderBuilder.getPanel();
    }

    private JPanel getRingPanel() {
        PanelBuilder ringBuilder = new PanelBuilder();
        ringBuilder.addComponent(POINT_1_LABEL, ringModel.bindPoint(POINT1_KEY, listener));
        ringBuilder.addComponent(POINT_2_LABEL, ringModel.bindPoint(POINT2_KEY, listener));
        ringBuilder.addComponent(INNER_RADIUS_LABEL, ringModel.bindDouble(INNER_RADIUS_KEY, listener));
        ringBuilder.addComponent(OUTER_RADIUS_LABEL, ringModel.bindDouble(OUTER_RADIUS_KEY, listener));
        return ringBuilder.getPanel();
    }

    public Surface getSurface() {
        return surface;
    }

    public Dictionary getDictionary() {
        switch (choice.getItemAt(choice.getSelectedIndex())) {
        case BOX: {
            Dictionary boxToCell = new Dictionary(BOX_TO_CELL_KEY);
            String min = boxModel.getDictionary().lookup(MIN_KEY);
            String max = boxModel.getDictionary().lookup(MAX_KEY);
            boxToCell.add("box", min + " " + max);
            boxToCell.add(VALUE_KEY, String.valueOf(valueField.getDoubleValue()));
            return boxToCell;
        }
        case SPHERE: {
            Dictionary sphereToCell = new Dictionary(SPHERE_TO_CELL_KEY);
            String centre = sphereModel.getDictionary().lookup(CENTRE_KEY);
            String radius = sphereModel.getDictionary().lookup(RADIUS_KEY);
            sphereToCell.add(CENTRE_KEY, centre);
            sphereToCell.add(RADIUS_KEY, radius);
            sphereToCell.add(VALUE_KEY, String.valueOf(valueField.getDoubleValue()));
            return sphereToCell;
        }
        case CYLINDER: {
            Dictionary cylinderToCell = new Dictionary(CYLINDER_TO_CELL_KEY);
            String p1 = cylinderModel.getDictionary().lookup(POINT1_KEY);
            String p2 = cylinderModel.getDictionary().lookup(POINT2_KEY);
            String radius = cylinderModel.getDictionary().lookup(RADIUS_KEY);
            cylinderToCell.add(POINT1_KEY, p1);
            cylinderToCell.add(POINT2_KEY, p2);
            cylinderToCell.add(RADIUS_KEY, radius);
            cylinderToCell.add(VALUE_KEY, String.valueOf(valueField.getDoubleValue()));
            return cylinderToCell;
        }
        case RING: {
            Dictionary ringToCell = new Dictionary(RING_TO_CELL_KEY);
            String p1 = ringModel.getDictionary().lookup(POINT1_KEY);
            String p2 = ringModel.getDictionary().lookup(POINT2_KEY);
            String iRadius = ringModel.getDictionary().lookup(INNER_RADIUS_KEY);
            String oRadius = ringModel.getDictionary().lookup(OUTER_RADIUS_KEY);
            ringToCell.add(POINT1_KEY, p1);
            ringToCell.add(POINT2_KEY, p2);
            ringToCell.add(INNER_RADIUS_KEY, iRadius);
            ringToCell.add(OUTER_RADIUS_KEY, oRadius);
            ringToCell.add(VALUE_KEY, String.valueOf(valueField.getDoubleValue()));
            return ringToCell;
        }
        default:
            return null;
        }
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

    private class ValueFieldChangeListener implements FieldChangeListener {

        boolean adjusting = false;

        @Override
        public void actionPerformed(ActionEvent e) {
        }

        @Override
        public void setAdjusting(boolean b) {
            this.adjusting = b;
        }

        @Override
        public boolean isAdjusting() {
            return adjusting;
        }

        @Override
        public void fieldChanged() {
            if (!isAdjusting()) {
                if (surface.getType().isBox())
                    surface.setGeometryDictionary(boxModel.getDictionary());
                else if (surface.getType().isSphere())
                    surface.setGeometryDictionary(sphereModel.getDictionary());
                else if (surface.getType().isCylinder())
                    surface.setGeometryDictionary(cylinderModel.getDictionary());
                else if (surface.getType().isRing())
                    surface.setGeometryDictionary(ringModel.getDictionary());
                else
                    logger.error("Unknow surface type");

                EventManager.triggerEvent(this, new ChangeSurfaceEvent(surface, false));
            }
        }
    };

    private Dictionary getDefaultBoxModelDictionary() {
        Dictionary dictionary = new Dictionary(BOX_TO_CELL_KEY);
        dictionary.add(MIN_KEY, "(0.0 0.0 0.0)");
        dictionary.add(MAX_KEY, "(2.0 2.0 1.0)");
        return dictionary;
    }

    private Dictionary getDefaultSphereModelDictionary() {
        Dictionary dictionary = new Dictionary(SPHERE_TO_CELL_KEY);
        dictionary.add(CENTRE_KEY, "(0.0 0.0 0.0 )");
        dictionary.add(RADIUS_KEY, "2.0");
        return dictionary;
    }

    private Dictionary getDefaultCylinderModelDictionary() {
        Dictionary dictionary = new Dictionary(CYLINDER_TO_CELL_KEY);
        dictionary.add(POINT1_KEY, "(0.0 0.0 -1.5 )");
        dictionary.add(POINT2_KEY, "(0.0 0.0 1.5 )");
        dictionary.add(RADIUS_KEY, "2.0");
        return dictionary;
    }

    private Dictionary getDefaultRingModelDictionary() {
        Dictionary dictionary = new Dictionary(RING_TO_CELL_KEY);
        dictionary.add(POINT1_KEY, "(0.0 0.0 0 )");
        dictionary.add(POINT2_KEY, "(0.05 0.0 0 )");
        dictionary.add(INNER_RADIUS_KEY, "0.2");
        dictionary.add(OUTER_RADIUS_KEY, "0.5");
        return dictionary;
    }

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

    public void clear() {
        if (showBoxButton.isSelected()) {
            showBoxButton.doClick();
        }
    }

}
