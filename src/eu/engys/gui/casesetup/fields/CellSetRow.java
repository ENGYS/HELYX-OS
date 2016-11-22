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

import static eu.engys.gui.mesh.panels.ShapesPanel.CENTRE_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.DELTA_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.FILE_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.INCLUDE_CUT_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.INCLUDE_INSIDE_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.INCLUDE_OUTSIDE_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.INNER_RADIUS_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.I_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.J_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.K_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.MAX_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.MIN_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.ORIGIN_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.OUTER_RADIUS_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.OUTSIDE_POINT_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.POINT_1_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.POINT_2_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.RADIUS_LABEL;
import static eu.engys.gui.mesh.panels.ShapesPanel.ROTATION_LABEL;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.surface.Box;
import eu.engys.core.project.geometry.surface.Cylinder;
import eu.engys.core.project.geometry.surface.Ring;
import eu.engys.core.project.geometry.surface.RotatedBox;
import eu.engys.core.project.geometry.surface.Sphere;
import eu.engys.core.project.geometry.surface.StlArea;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.ChangeSurfaceEvent;
import eu.engys.gui.mesh.actions.AddSTLArea;
import eu.engys.gui.mesh.panels.ShapesPanel;
import eu.engys.gui.view3D.BoxEventButton;
import eu.engys.gui.view3D.RotatedBoxEventButton;
import eu.engys.util.bean.BeanModel;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.FieldChangeListener;
import eu.engys.util.ui.TitledBorderWithAction;
import eu.engys.util.ui.builder.JComboBoxController;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.StringField;
import net.java.dev.designgridlayout.Componentizer;

public abstract class CellSetRow extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(CellSetRow.class);

    public static final String BOX = "Box";
    public static final String RBOX = "Rotated Box";
    public static final String SPHERE = "Sphere";
    public static final String CYLINDER = "Cylinder";
    public static final String RING = "Ring";
    public static final String SURFACE = "Surface";

    public static final String CELLSET_NAME = "cellset";
    public static final String CELLSET_ROW_NAME = "cellset.row";
    public static final String CELLSET_VALUE_NAME = "cellset.value";
    public static final String CELLSET_SLIDER_NAME = "cellset.slider";

    private BeanModel<Box> boxModel = new BeanModel<>();
    private BeanModel<RotatedBox> rBoxModel = new BeanModel<>();
    private BeanModel<Sphere> sphereModel = new BeanModel<>();
    private BeanModel<Cylinder> cylinderModel = new BeanModel<>();
    private BeanModel<Ring> ringModel = new BeanModel<>();
    private BeanModel<StlArea> surfaceModel = new BeanModel<>();

    private FieldChangeListener listener;
    private JPanel coordinatesPanel;
    // private JComboBox<String> choice;

    private BoxEventButton showBoxButton;
    private RotatedBoxEventButton showRotatedBoxButton;

    protected Model model;
    protected Surface surface;

    // private SurfaceTypeListener surfaceTypeListener;
    private ProgressMonitor monitor;
    private JComboBoxController boxType;

    private ActionListener boxTypeListener;

    private String selectedKey;

    public CellSetRow(Model model, Surface surface, ProgressMonitor monitor) {
        super(new BorderLayout());
        this.model = model;
        this.surface = surface;
        this.monitor = monitor;
        this.listener = new ValueFieldChangeListener();
        setName(CELLSET_ROW_NAME + "." + surface.getName());
    }

    protected void layoutComponents() {
        // setBorder(BorderFactory.createTitledBorder(BOX));
        setOpaque(false);
        coordinatesPanel = createCoordinatersPanel();
        // choice = create3DCombo();
        JPanel valuePanel = createValuePanel();

        // add(choice, BorderLayout.NORTH);
        add(coordinatesPanel, BorderLayout.CENTER);
        add(valuePanel, BorderLayout.SOUTH);
    }

    protected abstract JPanel createValuePanel();

    protected void _load() {
        // choice.removeActionListener(surfaceTypeListener);
        listener.setAdjusting(true);
        if (surface != null) {
            if (surface instanceof Box) {
                // choice.setSelectedItem(BOX);
                showPanel(BOX);
                Box box = (Box) surface;
                boxModel.setBean(box);
                if (box.getRotation()[0] != 0 || box.getRotation()[1] != 0 || box.getRotation()[2] != 0) {
                    boxType.setSelectedKey(ShapesPanel.CENTRE_DELTA_BOX);
                } else {
                    boxType.setSelectedKey(ShapesPanel.MIN_MAX_BOX);
                }
            } else if (surface instanceof RotatedBox) {
                // choice.setSelectedItem(RBOX);
                showPanel(RBOX);
                rBoxModel.setBean((RotatedBox) surface);
            } else if (surface instanceof Sphere) {
                // choice.setSelectedItem(SPHERE);
                showPanel(SPHERE);
                sphereModel.setBean((Sphere) surface);
            } else if (surface instanceof Cylinder) {
                // choice.setSelectedItem(CYLINDER);
                showPanel(CYLINDER);
                cylinderModel.setBean((Cylinder) surface);
            } else if (surface instanceof Ring) {
                // choice.setSelectedItem(RING);
                showPanel(RING);
                ringModel.setBean((Ring) surface);
            } else if (surface instanceof StlArea) {
                // choice.setSelectedItem(SURFACE);
                showPanel(SURFACE);
                surfaceModel.setBean((StlArea) surface);
            }
            load();
        } else {
            addNewBox();
        }
        listener.setAdjusting(false);
        // choice.addActionListener(surfaceTypeListener);
    }

    protected abstract void load();

    protected abstract void newBox();

    private void addNewBox() {
        surface = model.getGeometry().getFactory().newSurface(Box.class, model.getGeometry().getAName(BOX));
        boxModel.setBean((Box) surface);
        newBox();
    }

    // private JComboBox<String> create3DCombo() {
    // surfaceTypeListener = new SurfaceTypeListener();
    // choice = new JComboBox<String>(new String[] { BOX, RBOX, SPHERE, CYLINDER, RING, SURFACE });
    // choice.setName(CELLSET_NAME + "." + index);
    // choice.addActionListener(surfaceTypeListener);
    // return choice;
    // }

    private JPanel createCoordinatersPanel() {
        CardLayout c = new CardLayout();
        JPanel p = new JPanel(c);
        p.setOpaque(false);
        p.add(getBoxPanel(), BOX);
        p.add(getRBoxPanel(), RBOX);
        p.add(getSpherePanel(), SPHERE);
        p.add(getCylinderPanel(), CYLINDER);
        p.add(getRingPanel(), RING);
        p.add(getSurfacePanel(), SURFACE);
        c.show(p, BOX);
        return p;
    }

    private JPanel getBoxPanel() {
        PanelBuilder boxBuilder = new PanelBuilder();
        DoubleField[] boxMin = boxModel.bindPoint(Box.MIN_KEY);
        DoubleField[] boxMax = boxModel.bindPoint(Box.MAX_KEY);

        DoubleField[] center = boxModel.bindPoint(Box.CENTER_KEY);
        DoubleField[] delta = boxModel.bindPoint(Box.DELTA_KEY);
        DoubleField[] rotation = boxModel.bindPoint(Box.ROTATION_KEY);

        addChangeSurfaceAction(boxMin);
        addChangeSurfaceAction(boxMax);
        addChangeSurfaceAction(center);
        addChangeSurfaceAction(delta);
        addChangeSurfaceAction(rotation);

        showBoxButton = new BoxEventButton(boxMin, boxMax);
        showRotatedBoxButton = new RotatedBoxEventButton(center, delta, rotation);

        boxType = (JComboBoxController) boxBuilder.startChoice("Type");
        boxBuilder.startGroup(ShapesPanel.MIN_MAX_BOX);
        boxBuilder.addComponent(MIN_LABEL, boxMin[0], boxMin[1], boxMin[2], showBoxButton);
        boxBuilder.addComponentAndSpan(MAX_LABEL, boxMax);
        boxBuilder.endGroup();
        boxBuilder.startGroup(ShapesPanel.CENTRE_DELTA_BOX);
        boxBuilder.addComponent(CENTRE_LABEL, center[0], center[1], center[2], showRotatedBoxButton);
        boxBuilder.addComponentAndSpan(DELTA_LABEL, delta);
        boxBuilder.addComponentAndSpan(ROTATION_LABEL, rotation);
        boxBuilder.endGroup();
        boxBuilder.endChoice();

        boxTypeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (boxType.getSelectedKey().equals(ShapesPanel.MIN_MAX_BOX)) {
                    boxModel.getBean().setRotation(Box.DEFAULT_ROTATION);
                    if (showRotatedBoxButton.isSelected()) {
                        showRotatedBoxButton.doClick();
                        showBoxButton.doClick();
                    }
                } else if (boxType.getSelectedKey().equals(ShapesPanel.CENTRE_DELTA_BOX)) {
                    if (showBoxButton.isSelected()) {
                        showBoxButton.doClick();
                        showRotatedBoxButton.doClick();
                    }
                }
            }
        };
        boxType.addActionListener(boxTypeListener);
        return boxBuilder.getPanel();
    }

    private JPanel getRBoxPanel() {
        PanelBuilder rboxBuilder = new PanelBuilder();
        DoubleField[] boxOrigin = rBoxModel.bindPoint(RotatedBox.ORIGIN_KEY);
        DoubleField[] boxI = rBoxModel.bindPoint(RotatedBox.I_KEY);
        DoubleField[] boxJ = rBoxModel.bindPoint(RotatedBox.J_KEY);
        DoubleField[] boxK = rBoxModel.bindPoint(RotatedBox.K_KEY);

        addChangeSurfaceAction(boxOrigin);
        addChangeSurfaceAction(boxI);
        addChangeSurfaceAction(boxJ);
        addChangeSurfaceAction(boxK);

        rboxBuilder.addComponent(ORIGIN_LABEL, boxOrigin);
        rboxBuilder.addComponent(I_LABEL, boxI);
        rboxBuilder.addComponent(J_LABEL, boxJ);
        rboxBuilder.addComponent(K_LABEL, boxK);

        return rboxBuilder.getPanel();
    }

    private JPanel getSpherePanel() {
        DoubleField[] centre = sphereModel.bindPoint(Sphere.CENTRE_KEY);
        DoubleField radius = sphereModel.bindDouble(Sphere.RADIUS_KEY);

        PanelBuilder sphereBuilder = new PanelBuilder();
        sphereBuilder.addComponent(CENTRE_LABEL, centre);
        sphereBuilder.addComponent(RADIUS_LABEL, radius);

        addChangeSurfaceAction(centre);
        addChangeSurfaceAction(radius);

        return sphereBuilder.getPanel();
    }

    private JPanel getCylinderPanel() {
        DoubleField[] point1 = cylinderModel.bindPoint(Cylinder.POINT1_KEY);
        DoubleField[] point2 = cylinderModel.bindPoint(Cylinder.POINT2_KEY);
        DoubleField radius = cylinderModel.bindDouble(Cylinder.RADIUS_KEY);

        PanelBuilder cylinderBuilder = new PanelBuilder();
        cylinderBuilder.addComponent(POINT_1_LABEL, point1);
        cylinderBuilder.addComponent(POINT_2_LABEL, point2);
        cylinderBuilder.addComponent(RADIUS_LABEL, radius);

        addChangeSurfaceAction(point1);
        addChangeSurfaceAction(point2);
        addChangeSurfaceAction(radius);

        return cylinderBuilder.getPanel();
    }

    private JPanel getRingPanel() {
        DoubleField[] point1 = ringModel.bindPoint(Ring.POINT1_KEY);
        DoubleField[] point2 = ringModel.bindPoint(Ring.POINT2_KEY);
        DoubleField radius1 = ringModel.bindDouble(Ring.INNER_RADIUS_KEY);
        DoubleField radius2 = ringModel.bindDouble(Ring.OUTER_RADIUS_KEY);

        PanelBuilder ringBuilder = new PanelBuilder();
        ringBuilder.addComponent(POINT_1_LABEL, point1);
        ringBuilder.addComponent(POINT_2_LABEL, point2);
        ringBuilder.addComponent(INNER_RADIUS_LABEL, radius1);
        ringBuilder.addComponent(OUTER_RADIUS_LABEL, radius2);

        addChangeSurfaceAction(point1);
        addChangeSurfaceAction(point2);
        addChangeSurfaceAction(radius1, radius2);

        return ringBuilder.getPanel();
    }

    private JPanel getSurfacePanel() {
        StringField file = surfaceModel.bindLabel(StlArea.FILE);
        DoubleField[] point = surfaceModel.bindPoint(StlArea.OUTSIDE_POINT);
        JCheckBox cut = surfaceModel.bindBoolean(StlArea.INCLUDE_CUT);
        JCheckBox inside = surfaceModel.bindBoolean(StlArea.INCLUDE_INSIDE);
        JCheckBox outside = surfaceModel.bindBoolean(StlArea.INCLUDE_OUTSIDE);

        file.setEnabled(false);
        JButton button = new JButton(new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddSTLArea(model, monitor) {
                    @Override
                    public void postLoad(StlArea stlArea) {
                        StlArea bean = surfaceModel.getBean();
                        bean.setDataSet(stlArea.getDataSet());
                        bean.setFile(stlArea.getFile());
                    }
                }.execute();
            }
        });
        JComponent c = Componentizer.create().minAndMore(file).minToPref(button).component();
        PanelBuilder surfaceBuilder = new PanelBuilder();
        surfaceBuilder.addComponent(FILE_LABEL, c);
        surfaceBuilder.addComponent(OUTSIDE_POINT_LABEL, point);
        surfaceBuilder.addComponent(INCLUDE_CUT_LABEL, cut);
        surfaceBuilder.addComponent(INCLUDE_INSIDE_LABEL, inside);
        surfaceBuilder.addComponent(INCLUDE_OUTSIDE_LABEL, outside);

        file.setName(CELLSET_NAME + "." + FILE_LABEL);
        button.setName(CELLSET_NAME + "." + "button");

        addChangeSurfaceAction(file);

        return surfaceBuilder.getPanel();
    }

    void addChangeSurfaceAction(JTextField... components) {
        for (JTextField c : components) {
            c.addPropertyChangeListener("value", listener);
        }
    }

    public Surface getSurface() {
        return surface;
    }

    // private final class SurfaceTypeListener implements ActionListener {
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // Surface existingSurface = surface;
    // if (existingSurface != null) {
    // EventManager.triggerEvent(this, new RemoveSurfaceEvent(surface));
    // }
    //
    // listener.setAdjusting(true);
    // if (BOX.equals(choice.getSelectedItem())) {
    // boxType.removeActionListener(boxTypeListener);
    // showPanel(BOX);
    // surface = model.getGeometry().getFactory().newSurface(Box.class, model.getGeometry().getAName(BOX));
    // boxModel.setBean((Box) surface);
    // boxType.addActionListener(boxTypeListener);
    // } else if (RBOX.equals(choice.getSelectedItem())) {
    // showPanel(RBOX);
    // surface = model.getGeometry().getFactory().newSurface(RotatedBox.class, model.getGeometry().getAName(RBOX));
    // rBoxModel.setBean((RotatedBox) surface);
    // } else if (SPHERE.equals(choice.getSelectedItem())) {
    // showPanel(SPHERE);
    // surface = model.getGeometry().getFactory().newSurface(Sphere.class, model.getGeometry().getAName(SPHERE));
    // sphereModel.setBean((Sphere) surface);
    // } else if (CYLINDER.equals(choice.getSelectedItem())) {
    // showPanel(CYLINDER);
    // surface = model.getGeometry().getFactory().newSurface(Cylinder.class, model.getGeometry().getAName(CYLINDER));
    // cylinderModel.setBean((Cylinder) surface);
    // } else if (RING.equals(choice.getSelectedItem())) {
    // showPanel(RING);
    // surface = model.getGeometry().getFactory().newSurface(Ring.class, model.getGeometry().getAName(RING));
    // ringModel.setBean((Ring) surface);
    // } else if (SURFACE.equals(choice.getSelectedItem())) {
    // showPanel(SURFACE);
    // surface = model.getGeometry().getFactory().newSurface(StlArea.class, model.getGeometry().getAName(SURFACE));
    // surfaceModel.setBean((StlArea) surface);
    // }
    // listener.setAdjusting(false);
    // // newBox();
    // if (existingSurface != null) {
    // EventManager.triggerEvent(this, new AddSurfaceEvent(surface));
    // }
    // }
    //
    // }

    private void showPanel(String key) {
        this.selectedKey = key;
        CardLayout layout = (CardLayout) coordinatesPanel.getLayout();
        layout.show(coordinatesPanel, key);
        // setBorder(BorderFactory.createTitledBorder(key));
        if (getBorder() instanceof TitledBorderWithAction) {
            ((TitledBorderWithAction) getBorder()).setTitle(key);
        }
    }

    public void clear() {
        boxModel.release();
        rBoxModel.release();
        sphereModel.release();
        cylinderModel.release();
        ringModel.release();
        surfaceModel.release();

        if (showBoxButton.isSelected()) {
            showBoxButton.doClick();
        }
        if (showRotatedBoxButton.isSelected()) {
            showRotatedBoxButton.doClick();
        }
    }

    private class ValueFieldChangeListener extends FieldChangeListener {
        @Override
        public void fieldChanged(Object source) {
            EventManager.triggerEvent(this, new ChangeSurfaceEvent(surface, false));
        }
    }

    public String getSelectedKey() {
        return selectedKey;
    };

}
