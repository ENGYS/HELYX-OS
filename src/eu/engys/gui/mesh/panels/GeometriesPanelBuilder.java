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

package eu.engys.gui.mesh.panels;

import static eu.engys.util.ui.ComponentsFactory.stringField;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.java.dev.designgridlayout.RowGroup;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FieldChangeListener;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;
import eu.engys.core.project.geometry.surface.PlaneRegion;
import eu.engys.gui.mesh.GeometryPanel;
import eu.engys.gui.view3D.BoxEventButton;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.StringField;

public class GeometriesPanelBuilder {

    public static final String BOX_NAME_LABEL = "Box Name";
    public static final String SPHERE_NAME_LABEL = "Sphere Name";
    public static final String CYLINDER_NAME_LABEL = "Cylinder Name";
    public static final String PLANE_NAME_LABEL = "Plane Name";
    public static final String RING_NAME_LABEL = "Ring Name";
    public static final String SURFACE_NAME_LABEL = "Surface Name";

    public static final String OUTER_RADIUS_LABEL = "Outer Radius";
    public static final String INNER_RADIUS_LABEL = "Inner Radius";
    public static final String PATCH_NAME_LABEL = "Patch Name";
    public static final String NORMAL_LABEL = "Normal";
    public static final String ORIGIN_LABEL = "Origin";
    public static final String POINT_1_LABEL = "Point 1";
    public static final String POINT_2_LABEL = "Point 2";
    public static final String MIN_LABEL = "Min";
    public static final String MAX_LABEL = "Max";
    public static final String RADIUS_LABEL = "Radius";
    public static final String CENTRE_LABEL = "Centre";
    public static final int DECIMAL_PLACES = 4;

    private StringField stlNameField;
    private StringField boxNameField;
    private StringField cylinderNameField;
    private StringField sphereNameField;
    private StringField ringNameField;
    private StringField planeNameField;
    private StringField regionNameField;

    private DictionaryModel boxModel;
    private DictionaryModel cylinderModel;
    private DictionaryModel sphereModel;
    private DictionaryModel ringModel;
    private DictionaryModel planeModel;
    private DictionaryModel planePointAndNormalModel;

    private PanelBuilder boxBuilder;
    private PanelBuilder cylinderBuilder;
    private PanelBuilder sphereBuilder;
    private PanelBuilder ringBuilder;
    private PanelBuilder planeBuilder;
    private PanelBuilder regionBuilder;
    private DictionaryModel stlModel;
    PanelBuilder stlBuilder;

    private RowGroup boxGroup;
    private RowGroup sphereGroup;
    private RowGroup ringGroup;
    private RowGroup planeGroup;
    private RowGroup cylinderGroup;
    private RowGroup stlGroup;
    private RowGroup regionGroup;
    private RowGroup noneGroup;

    private GeometryPanel meshPanel;

    private RowGroup selected;
    Surface selectedSurface;
    private PropertyChangeListener renameAction;

    private BoxEventButton showBoxButton;

    public GeometriesPanelBuilder(GeometryPanel panel) {
        super();
        this.meshPanel = panel;
    }

    public void addComponents(PanelBuilder builder) {
        FieldChangeListener listener = new FieldChangeListener() {

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
                if (!isAdjusting() && selectedSurface != null) {
                    meshPanel.changeSurface(selectedSurface);
                }
            }
        };

        addSTLComponent(builder, listener);
        addBoxComponent(builder, listener);
        addCylinderComponent(builder, listener);
        addPlaneComponent(builder, listener);
        addSphereComponent(builder, listener);
        addRingComponent(builder, listener);
        addNoneComponent(builder, listener);

        renameAction = new PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("value") && evt.getSource() instanceof StringField) {
                    StringField field = (StringField) evt.getSource();
                    meshPanel.renameSurface(field.getText());
                }
            }
        };
        addRenameAction();

        selected = noneGroup;
    }

    private void addSTLComponent(PanelBuilder builder, FieldChangeListener listener) {
        stlModel = new DictionaryModel(new Dictionary(""));

        stlBuilder = new PanelBuilder();
        stlBuilder.addComponent(SURFACE_NAME_LABEL, stlNameField = stringField());
        stlBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(""));

        stlGroup = new RowGroup();
        builder.addComponentToGroup(stlGroup, stlBuilder.getPanel());
        stlGroup.hide();
    }

    private void addBoxComponent(PanelBuilder builder, FieldChangeListener listener) {
        boxModel = new DictionaryModel(new Dictionary(""));

        DoubleField[] boxMin = boxModel.bindPoint(Surface.MIN_KEY, DECIMAL_PLACES, listener);
        DoubleField[] boxMax = boxModel.bindPoint(Surface.MAX_KEY, DECIMAL_PLACES, listener);
        showBoxButton = new BoxEventButton(boxMin, boxMax);

        boxBuilder = new PanelBuilder();
        boxBuilder.addComponent(BOX_NAME_LABEL, boxNameField = stringField());
        // boxBuilder.addComponent("", labelField("X"), labelField("Y"), labelField("Z"));
        boxBuilder.addComponent(MIN_LABEL, boxMin[0], boxMin[1], boxMin[2], showBoxButton);
        boxBuilder.addComponentAndSpan(MAX_LABEL, boxMax);
        boxBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(""));

        boxGroup = new RowGroup();
        builder.addComponentToGroup(boxGroup, boxBuilder.getPanel());
        boxGroup.hide();
    }

    private void addCylinderComponent(PanelBuilder builder, FieldChangeListener listener) {
        cylinderModel = new DictionaryModel(new Dictionary(""));

        cylinderBuilder = new PanelBuilder();
        cylinderBuilder.addComponent(CYLINDER_NAME_LABEL, cylinderNameField = stringField());
        cylinderBuilder.addComponent(POINT_1_LABEL, cylinderModel.bindPoint(Surface.POINT1_KEY, DECIMAL_PLACES, listener));
        cylinderBuilder.addComponent(POINT_2_LABEL, cylinderModel.bindPoint(Surface.POINT2_KEY, DECIMAL_PLACES, listener));
        cylinderBuilder.addComponent(RADIUS_LABEL, cylinderModel.bindDouble(Surface.RADIUS_KEY, DECIMAL_PLACES, listener));
        cylinderBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(""));

        cylinderGroup = new RowGroup();
        builder.addComponentToGroup(cylinderGroup, cylinderBuilder.getPanel());
        cylinderGroup.hide();
    }

    private void addPlaneComponent(PanelBuilder builder, FieldChangeListener listener) {
        planeModel = new DictionaryModel(new Dictionary(""));
        planePointAndNormalModel = new DictionaryModel(new Dictionary(""));

        planeBuilder = new PanelBuilder();
        planeBuilder.addComponent(PLANE_NAME_LABEL, planeNameField = stringField());
        planeBuilder.addComponent(ORIGIN_LABEL, planePointAndNormalModel.bindPoint(Surface.BASE_POINT_KEY, DECIMAL_PLACES, listener));
        planeBuilder.addComponent(NORMAL_LABEL, planePointAndNormalModel.bindPoint(Surface.NORMAL_VECTOR_KEY, DECIMAL_PLACES, listener));
        planeBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(""));

        regionBuilder = new PanelBuilder();
        regionBuilder.addComponent(PATCH_NAME_LABEL, regionNameField = stringField());
        regionBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(""));

        planeGroup = new RowGroup();
        builder.addComponentToGroup(planeGroup, planeBuilder.getPanel());
        planeGroup.hide();

        regionGroup = new RowGroup();
        builder.addComponentToGroup(regionGroup, regionBuilder.getPanel());
        regionGroup.hide();
    }

    private void addSphereComponent(PanelBuilder builder, FieldChangeListener listener) {
        sphereModel = new DictionaryModel(new Dictionary(""));

        sphereBuilder = new PanelBuilder();
        sphereBuilder.addComponent(SPHERE_NAME_LABEL, sphereNameField = stringField());
        sphereBuilder.addComponent(CENTRE_LABEL, sphereModel.bindPoint(Surface.CENTRE_KEY, DECIMAL_PLACES, listener));
        sphereBuilder.addComponent(RADIUS_LABEL, sphereModel.bindDouble(Surface.RADIUS_KEY, DECIMAL_PLACES, listener));
        sphereBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(""));

        sphereGroup = new RowGroup();
        builder.addComponentToGroup(sphereGroup, sphereBuilder.getPanel());
        sphereGroup.hide();
    }

    private void addRingComponent(PanelBuilder builder, FieldChangeListener listener) {
        ringModel = new DictionaryModel(new Dictionary(""));

        ringBuilder = new PanelBuilder();
        ringBuilder.addComponent(RING_NAME_LABEL, ringNameField = stringField());
        ringBuilder.addComponent(POINT_1_LABEL, ringModel.bindPoint(Surface.POINT1_KEY, DECIMAL_PLACES, listener));
        ringBuilder.addComponent(POINT_2_LABEL, ringModel.bindPoint(Surface.POINT2_KEY, DECIMAL_PLACES, listener));
        ringBuilder.addComponent(INNER_RADIUS_LABEL, ringModel.bindDouble(Surface.INNER_RADIUS_KEY, DECIMAL_PLACES, listener));
        ringBuilder.addComponent(OUTER_RADIUS_LABEL, ringModel.bindDouble(Surface.OUTER_RADIUS_KEY, DECIMAL_PLACES, listener));
        ringBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(""));

        ringGroup = new RowGroup();
        builder.addComponentToGroup(ringGroup, ringBuilder.getPanel());
        ringGroup.hide();
    }

    private void addNoneComponent(PanelBuilder builder, FieldChangeListener listener) {
        noneGroup = new RowGroup();
        builder.addComponentToGroup(noneGroup, new JLabel("Select or Add a geometry"));
        noneGroup.show();
    }

    void addRenameAction() {
        stlNameField.addPropertyChangeListener(renameAction);
        boxNameField.addPropertyChangeListener(renameAction);
        cylinderNameField.addPropertyChangeListener(renameAction);
        sphereNameField.addPropertyChangeListener(renameAction);
        ringNameField.addPropertyChangeListener(renameAction);
        planeNameField.addPropertyChangeListener(renameAction);
        regionNameField.addPropertyChangeListener(renameAction);
    }

    void remRenameAction() {
        stlNameField.removePropertyChangeListener(renameAction);
        boxNameField.removePropertyChangeListener(renameAction);
        cylinderNameField.removePropertyChangeListener(renameAction);
        sphereNameField.removePropertyChangeListener(renameAction);
        ringNameField.removePropertyChangeListener(renameAction);
        planeNameField.removePropertyChangeListener(renameAction);
        regionNameField.removePropertyChangeListener(renameAction);
    }

    private void showSTL() {
        hideSelectedInEDT();
        showInEDT(stlGroup);
    }

    private void showBox() {
        hideSelectedInEDT();
        showInEDT(boxGroup);
    }

    private void showCylinder() {
        hideSelectedInEDT();
        showInEDT(cylinderGroup);
    }

    private void showSphere() {
        hideSelectedInEDT();
        showInEDT(sphereGroup);
    }

    private void showRing() {
        hideSelectedInEDT();
        showInEDT(ringGroup);
    }

    private void showPlane() {
        hideSelectedInEDT();
        showInEDT(planeGroup);
    }

    private void showRegion() {
        hideSelectedInEDT();
        showInEDT(regionGroup);
    }

    private void showNone() {
        hideSelectedInEDT();
        showInEDT(noneGroup);
    }

    private void showInEDT(final RowGroup group) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                group.show();
                selected = group;
            }
        });
    }

    private void hideSelectedInEDT() {
        if (selected != null)
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    selected.hide();
                }
            });
    }

    public void showPanel(Surface[] surfaces) {
        stop();
        if (surfaces == null || surfaces.length == 0) {
            showNone();
            selectedSurface = null;
            return;
        }
        boolean singleSelection = surfaces.length == 1;

        Type type = surfaces[0].getType();
        String name;
        boolean visible = true;
        if (singleSelection) {
            name = surfaces[0].getName();
            visible = surfaces[0].isVisible();
        } else {
            StringBuilder sb = new StringBuilder();
            for (Surface surface : surfaces) {
                sb.append(surface.getName());
                sb.append(" ");
                visible = visible && surface.isVisible();
            }
            name = sb.toString();
        }

        selectedSurface = surfaces[0];

        Dictionary dict = surfaces[0].getGeometryDictionary();
        switch (type) {
        case BOX:
            showBox();
            boxModel.setDictionary(dict);
            updatePanel(singleSelection, visible, name, boxNameField, boxBuilder);
            break;

        case CYLINDER:
            showCylinder();
            cylinderModel.setDictionary(dict);
            updatePanel(singleSelection, visible, name, cylinderNameField, cylinderBuilder);
            break;

        case SPHERE:
            showSphere();
            sphereModel.setDictionary(dict);
            updatePanel(singleSelection, visible, name, sphereNameField, sphereBuilder);
            break;
        case RING:
            showRing();
            ringModel.setDictionary(dict);
            updatePanel(singleSelection, visible, name, ringNameField, ringBuilder);
            break;

        case STL:
            showSTL();
            stlModel.setDictionary(dict);
            updatePanel(singleSelection, visible, name, stlNameField, stlBuilder);
            break;

        case PLANE:
            showPlane();
            planePointAndNormalModel.setDictionary(dict.subDict("pointAndNormalDict"));
            Dictionary copyDict = new Dictionary(dict);
            copyDict.remove("pointAndNormalDict");
            planeModel.setDictionary(copyDict);
            updatePanel(singleSelection, visible, name, planeNameField, planeBuilder);
            break;

        case SOLID:
        case REGION:
            showRegion();
            updatePanel(singleSelection, visible, name, regionNameField, regionBuilder);
            regionNameField.setEnabled(singleSelection && selectedSurface instanceof PlaneRegion);
            break;

        case MULTI:
        case LINE:
            break;
        }
    }

    private void updatePanel(boolean singleSelection, boolean visible, String name, StringField nameField, PanelBuilder builder) {
        remRenameAction();
        nameField.setValue(name);
        if (visible) {
            builder.setEnabled(true);
            if (singleSelection) {
                nameField.setEnabled(true);
                builder.setEnabled(true);
            } else {
                nameField.setEnabled(false);
                builder.setEnabled(false);
            }
        } else {
            builder.setEnabled(false);
        }
        addRenameAction();
    }

    public Dictionary getBoxDictionary() {
        Dictionary dict = boxModel.getDictionary();
        if (boxNameField.isEnabled()) {
            String name = boxNameField.getText();
            dict.setName(name);
        }

        return dict;
    }

    public Dictionary getCylinderDictionary() {
        Dictionary dict = cylinderModel.getDictionary();
        if (cylinderNameField.isEnabled()) {
            String name = cylinderNameField.getText();
            dict.setName(name);
        }

        return dict;
    }

    public Dictionary getSphereDictionary() {
        Dictionary dict = sphereModel.getDictionary();
        if (sphereNameField.isEnabled()) {
            String name = sphereNameField.getText();
            dict.setName(name);
        }

        return dict;
    }

    public Dictionary getRingDictionary() {
        Dictionary dict = ringModel.getDictionary();
        if (ringNameField.isEnabled()) {
            String name = ringNameField.getText();
            dict.setName(name);
        }

        return dict;
    }

    public Dictionary getPlaneDictionary() {
        Dictionary dict = planeModel.getDictionary();
        Dictionary subDict = planePointAndNormalModel.getDictionary();
        if (planeNameField.isEnabled()) {
            String name = planeNameField.getText();
            dict.setName(name);
        }
        dict.add(subDict);
        return dict;
    }

    public Dictionary getSTLDictionary() {
        Dictionary dict = stlModel.getDictionary();
        if (stlNameField.isEnabled())
            dict.add("name", stlNameField.getText());
        return dict;
    }

    public String getRegionName() {
        return regionNameField.getText();
    }

    public void stop() {
        if (showBoxButton.isSelected()) {
            showBoxButton.doClick();
        }
    }
}
