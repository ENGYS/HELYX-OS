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
package eu.engys.gui.mesh.panels;

import static eu.engys.util.ui.ComponentsFactory.stringField;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;
import eu.engys.core.project.geometry.surface.Box;
import eu.engys.core.project.geometry.surface.Cylinder;
import eu.engys.core.project.geometry.surface.Plane;
import eu.engys.core.project.geometry.surface.PlaneRegion;
import eu.engys.core.project.geometry.surface.Ring;
import eu.engys.core.project.geometry.surface.Sphere;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.gui.mesh.GeometryPanel;
import eu.engys.gui.view3D.BoxEventButton;
import eu.engys.util.Symbols;
import eu.engys.util.bean.BeanModel;
import eu.engys.util.ui.FieldChangeListener;
import eu.engys.util.ui.TreeUtil;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.StringField;
import net.java.dev.designgridlayout.RowGroup;

public class ShapesPanel extends JPanel {

    private final class ChangeSurfaceAction extends FieldChangeListener {
        @Override
        public void fieldChanged(Object source) {
            //System.err.println("ShapesPanel.ChangeSurfaceAction.fieldChanged() " + source);
            meshPanel.changeSurface();
        }
    }

    private final class RenameAction extends FieldChangeListener {
        @Override
        public void fieldChanged(Object source) {
            //System.out.println("ShapesPanel.RenameAction.fieldChanged() " + source);
            if (source instanceof StringField) {
                StringField field = (StringField) source;
                meshPanel.renameSurface(field.getText());
            }
        }
    }

    public static final String BOX_NAME_LABEL = "Box Name";
    public static final String SPHERE_NAME_LABEL = "Sphere Name";
    public static final String CYLINDER_NAME_LABEL = "Cylinder Name";
    public static final String PLANE_NAME_LABEL = "Plane Name";
    public static final String RING_NAME_LABEL = "Ring Name";
    public static final String SURFACE_NAME_LABEL = "Surface Name";

    public static final String OUTER_RADIUS_LABEL = "Outer Radius " + Symbols.M;
    public static final String INNER_RADIUS_LABEL = "Inner Radius " + Symbols.M;
    public static final String PATCH_NAME_LABEL = "Patch Name";
    public static final String NORMAL_LABEL = "Normal";
    public static final String ORIGIN_LABEL = "Origin";
    public static final String POINT_1_LABEL = "Point 1";
    public static final String POINT_2_LABEL = "Point 2";
    public static final String MIN_LABEL = "Min " + Symbols.M;
    public static final String MAX_LABEL = "Max " + Symbols.M;
    public static final String RADIUS_LABEL = "Radius " + Symbols.M;
    public static final String CENTRE_LABEL = "Centre";
    public static final String DELTA_LABEL = "Delta";
    public static final String ROTATION_LABEL = "Rotation";
    public static final String I_LABEL = "i" + Symbols.HAT;
    public static final String J_LABEL = "j" + Symbols.HAT;
    public static final String K_LABEL = "k" + Symbols.HAT;
    
    public static final String FILE_LABEL = "File";
    public static final String INCLUDE_CUT_LABEL = "Include Cut Cells";
    public static final String INCLUDE_INSIDE_LABEL = "Include Inside";
    public static final String INCLUDE_OUTSIDE_LABEL = "Include Outside";
    public static final String OUTSIDE_POINT_LABEL = "Outside Point";
    public static final int DECIMAL_PLACES = 4;

    private StringField stlNameField;
    private StringField boxNameField;
    private StringField cylinderNameField;
    private StringField sphereNameField;
    private StringField ringNameField;
    private StringField planeNameField;
    private StringField regionNameField;

    private BeanModel<Box> boxModel;
    private BeanModel<Cylinder> cylinderModel;
    private BeanModel<Sphere> sphereModel;
    private BeanModel<Ring> ringModel;
    private BeanModel<Plane> planePointAndNormalModel;
    private BeanModel<Stl> stlModel;
    
    private PanelBuilder boxBuilder;
    private PanelBuilder cylinderBuilder;
    private PanelBuilder sphereBuilder;
    private PanelBuilder ringBuilder;
    private PanelBuilder planeBuilder;
    private PanelBuilder regionBuilder;
    private PanelBuilder stlBuilder;

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
    private Surface selectedSurface;
    
    private RenameAction renameAction;
    private ChangeSurfaceAction changeSurfaceAction;

    private BoxEventButton showBoxButton;
    public static final String MIN_MAX_BOX = "Min-Max";
    public static final String CENTRE_DELTA_BOX = "Centre-Delta";

    public ShapesPanel(GeometryPanel panel) {
        super(new BorderLayout());
        this.meshPanel = panel;
        layoutComponents();
    }

    private void layoutComponents() {
        PanelBuilder builder = new PanelBuilder();
        builder.addButtons(meshPanel.getShapeButtons());
        builder.addSeparator("");
        
        changeSurfaceAction = new ChangeSurfaceAction();

        addSTLComponent(builder);
        addBoxComponent(builder);
        addCylinderComponent(builder);
        addPlaneComponent(builder);
        addSphereComponent(builder);
        addRingComponent(builder);
        addNoneComponent(builder);

        renameAction = new RenameAction();
        addRenameAction();

        selected = noneGroup;
        
        add(builder.removeMargins().getPanel());
    }

    private void addSTLComponent(PanelBuilder builder) {
        stlModel = new BeanModel<>();

        stlBuilder = new PanelBuilder();
        stlBuilder.addComponent(SURFACE_NAME_LABEL, stlNameField = stringField());
        stlBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(""));

        stlGroup = new RowGroup();
        builder.addComponentToGroup(stlGroup, stlBuilder.getPanel());
        stlGroup.hide();
    }

    private void addBoxComponent(PanelBuilder builder) {
        boxModel = new BeanModel<>();
        
        boxNameField = stringField();
        DoubleField[] boxMin = boxModel.bindPoint(Surface.MIN_KEY, DECIMAL_PLACES);
        DoubleField[] boxMax = boxModel.bindPoint(Surface.MAX_KEY, DECIMAL_PLACES);

        addChangeSurfaceAction(boxMin);
        addChangeSurfaceAction(boxMax);
        
        showBoxButton = new BoxEventButton(boxMin, boxMax);

        boxBuilder = new PanelBuilder();
        boxBuilder.addComponent(BOX_NAME_LABEL, boxNameField);
        // boxBuilder.addComponent("", labelField("X"), labelField("Y"), labelField("Z"));
        boxBuilder.addComponent(MIN_LABEL, boxMin[0], boxMin[1], boxMin[2], showBoxButton);
        boxBuilder.addComponentAndSpan(MAX_LABEL, boxMax);
        boxBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(""));

        boxGroup = new RowGroup();
        builder.addComponentToGroup(boxGroup, boxBuilder.getPanel());
        boxGroup.hide();
    }

    private void addCylinderComponent(PanelBuilder builder) {
        cylinderModel = new BeanModel<>();

        cylinderNameField = stringField();
        DoubleField[] point1 = cylinderModel.bindPoint(Surface.POINT1_KEY, DECIMAL_PLACES);
        DoubleField[] point2 = cylinderModel.bindPoint(Surface.POINT2_KEY, DECIMAL_PLACES);
        DoubleField radius = cylinderModel.bindDouble(Surface.RADIUS_KEY, DECIMAL_PLACES);
                
        cylinderBuilder = new PanelBuilder();
        cylinderBuilder.addComponent(CYLINDER_NAME_LABEL, cylinderNameField);
        cylinderBuilder.addComponent(POINT_1_LABEL, point1);
        cylinderBuilder.addComponent(POINT_2_LABEL, point2);
        cylinderBuilder.addComponent(RADIUS_LABEL, radius);
        cylinderBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(""));

        addChangeSurfaceAction(point1);
        addChangeSurfaceAction(point2);
        addChangeSurfaceAction(radius);

        cylinderGroup = new RowGroup();
        builder.addComponentToGroup(cylinderGroup, cylinderBuilder.getPanel());
        cylinderGroup.hide();
    }

    private void addSphereComponent(PanelBuilder builder) {
        sphereModel = new BeanModel<>();

        sphereNameField = stringField();
        DoubleField[] centre = sphereModel.bindPoint(Surface.CENTRE_KEY, DECIMAL_PLACES);
        DoubleField radius = sphereModel.bindDouble(Surface.RADIUS_KEY, DECIMAL_PLACES);
        
        sphereBuilder = new PanelBuilder();
        sphereBuilder.addComponent(SPHERE_NAME_LABEL, sphereNameField);
        sphereBuilder.addComponent(CENTRE_LABEL, centre);
        sphereBuilder.addComponent(RADIUS_LABEL, radius);
        sphereBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(""));

        addChangeSurfaceAction(centre);
        addChangeSurfaceAction(radius);
        
        sphereGroup = new RowGroup();
        builder.addComponentToGroup(sphereGroup, sphereBuilder.getPanel());
        sphereGroup.hide();
    }

    private void addPlaneComponent(PanelBuilder builder) {
        planePointAndNormalModel = new BeanModel<>();

        planeNameField = stringField();
        DoubleField[] origin = planePointAndNormalModel.bindPoint(Surface.BASE_POINT_KEY, DECIMAL_PLACES);
        DoubleField[] normal = planePointAndNormalModel.bindPoint(Surface.NORMAL_VECTOR_KEY, DECIMAL_PLACES);
                
        planeBuilder = new PanelBuilder();
        planeBuilder.addComponent(PLANE_NAME_LABEL, planeNameField);
        planeBuilder.addComponent(ORIGIN_LABEL, origin);
        planeBuilder.addComponent(NORMAL_LABEL, normal);
        planeBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(""));

        addChangeSurfaceAction(origin);
        addChangeSurfaceAction(normal);
        
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

    private void addRingComponent(PanelBuilder builder) {
        ringModel = new BeanModel<>();

        ringNameField = stringField();
        DoubleField[] point1 = ringModel.bindPoint(Surface.POINT1_KEY, DECIMAL_PLACES);
        DoubleField[] point2 = ringModel.bindPoint(Surface.POINT2_KEY, DECIMAL_PLACES);
        DoubleField radius1 = ringModel.bindDouble(Surface.INNER_RADIUS_KEY, DECIMAL_PLACES);
        DoubleField radius2 = ringModel.bindDouble(Surface.OUTER_RADIUS_KEY, DECIMAL_PLACES);
        
        ringBuilder = new PanelBuilder();
        ringBuilder.addComponent(RING_NAME_LABEL, ringNameField);
        ringBuilder.addComponent(POINT_1_LABEL, point1);
        ringBuilder.addComponent(POINT_2_LABEL, point2);
        ringBuilder.addComponent(INNER_RADIUS_LABEL, radius1);
        ringBuilder.addComponent(OUTER_RADIUS_LABEL, radius2);
        ringBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(""));

        addChangeSurfaceAction(point1);
        addChangeSurfaceAction(point2);
        addChangeSurfaceAction(radius1, radius2);
        
        ringGroup = new RowGroup();
        builder.addComponentToGroup(ringGroup, ringBuilder.getPanel());
        ringGroup.hide();
    }

    private void addNoneComponent(PanelBuilder builder) {
        noneGroup = new RowGroup();
        builder.addComponentToGroup(noneGroup, new JLabel("Select or Add a geometry"));
        noneGroup.show();
    }

    void addChangeSurfaceAction(JTextField... components) {
        for (JTextField c : components) {
            c.addPropertyChangeListener("value", changeSurfaceAction);
        }
    }
    
    void addRenameAction() {
        stlNameField.addPropertyChangeListener("value", renameAction);
        boxNameField.addPropertyChangeListener("value", renameAction);
        cylinderNameField.addPropertyChangeListener("value", renameAction);
        sphereNameField.addPropertyChangeListener("value", renameAction);
        ringNameField.addPropertyChangeListener("value", renameAction);
        planeNameField.addPropertyChangeListener("value", renameAction);
        regionNameField.addPropertyChangeListener("value", renameAction);
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
        if (surfaces == null || surfaces.length == 0 || !TreeUtil.isConsistent(surfaces, surfaces[0].getClass())) {
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

        selectedSurface = surfaces[0].cloneSurface();

        renameAction.setAdjusting(true);
        changeSurfaceAction.setAdjusting(true);

        switch (type) {
        case BOX:
            showBox();
            boxModel.setBean((Box) selectedSurface);
            updatePanel(singleSelection, visible, name, boxNameField, boxBuilder);
            break;

        case CYLINDER:
            showCylinder();
            cylinderModel.setBean((Cylinder) selectedSurface);
            updatePanel(singleSelection, visible, name, cylinderNameField, cylinderBuilder);
            break;

        case SPHERE:
            showSphere();
            sphereModel.setBean((Sphere) selectedSurface);
            updatePanel(singleSelection, visible, name, sphereNameField, sphereBuilder);
            break;
        case RING:
            showRing();
            ringModel.setBean((Ring) selectedSurface);
            updatePanel(singleSelection, visible, name, ringNameField, ringBuilder);
            break;

        case STL:
            showSTL();
            stlModel.setBean((Stl) selectedSurface);
            updatePanel(singleSelection, visible, name, stlNameField, stlBuilder);
            break;

        case PLANE:
            showPlane();
            planePointAndNormalModel.setBean((Plane) selectedSurface);
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
        
        renameAction.setAdjusting(false);
        changeSurfaceAction.setAdjusting(false);
    }

    private void updatePanel(boolean singleSelection, boolean visible, String name, StringField nameField, PanelBuilder builder) {
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
    }

    public String getRegionName() {
        return regionNameField.getText();
    }

    public void stop() {
        if (showBoxButton.isSelected()) {
            showBoxButton.doClick();
        }
    }

    public Surface getSelectedSurface() {
        return selectedSurface;
    }
}
