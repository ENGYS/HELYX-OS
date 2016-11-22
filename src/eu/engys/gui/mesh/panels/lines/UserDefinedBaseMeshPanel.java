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
package eu.engys.gui.mesh.panels.lines;

import static eu.engys.core.project.geometry.Surface.ELEMENTS_KEY;
import static eu.engys.core.project.geometry.Surface.MAX_KEY;
import static eu.engys.core.project.geometry.Surface.MIN_KEY;
import static eu.engys.util.ui.ComponentsFactory.doublePointField;
import static eu.engys.util.ui.ComponentsFactory.labelField;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.geometry.surface.MultiPlane;
import eu.engys.core.project.system.SystemFolder;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.ChangeSurfaceEvent;
import eu.engys.gui.view3D.BoxEventButton;
import eu.engys.util.Symbols;
import eu.engys.util.bean.BeanModel;
import eu.engys.util.bean.BeanPanelBuilder;
import eu.engys.util.ui.FieldChangeListener;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.IntegerField;

public class UserDefinedBaseMeshPanel {

    public static final String USER_DEFINED_LABEL = "User Defined";

    public static final String MIN_LABEL = "Min " + Symbols.M;
    public static final String MAX_LABEL = "Max " + Symbols.M;
    public static final String N_ELEMENTS_LABEL = "Elements";
    public static final String CELL_SIZE_LABEL = "Cell Size " + Symbols.M;

    private final static Icon FIT_ICON = ResourcesUtil.getIcon("fit.boundingbox.icon");

    private BeanPanelBuilder builder;

    private DoubleField[] boxMin;
    private DoubleField[] boxMax;
    private DoubleField[] cellSize;
    private JToggleButton showBoxButton;

    private Model model;

//    private DictionaryModel minMaxModel;
    private BeanModel<MultiPlane> minMaxModel;
    private UpdateBlockListener blockListener;
    private JButton fitButton;

    public UserDefinedBaseMeshPanel(Model model, BeanPanelBuilder builder) {
        this.model = model;
        this.builder = builder;

        minMaxModel = new BeanModel<>(new MultiPlane("BoundingBox"));
        builder.startBean(USER_DEFINED_LABEL, minMaxModel);
        layoutComponents();
        builder.endBean();
    }

    private void layoutComponents() {
        blockListener = new UpdateBlockListener();

        boxMin = minMaxModel.bindPoint(MIN_KEY, 4);
        boxMax = minMaxModel.bindPoint(MAX_KEY, 4);
        
        showBoxButton = new BoxEventButton(boxMin, boxMax);

        builder.addComponent("", labelField("X"), labelField("Y"), labelField("Z"));
        builder.addComponent(MIN_LABEL, boxMin[0], boxMin[1], boxMin[2], showBoxButton);
        builder.addComponentAndSpan(MAX_LABEL, boxMax);

        elements = minMaxModel.bindIntegerArray(ELEMENTS_KEY, 3);
        fitButton = new JButton(new FitBoundingBoxAction());
        fitButton.setPreferredSize(new Dimension(36, 48));
        fitButton.setEnabled(false);
        
        addBlockListener();
        
        builder.addComponent(N_ELEMENTS_LABEL, elements[0], elements[1], elements[2], fitButton);
        cellSize = doublePointField(3);
        cellSize[0].setEnabled(false);
        cellSize[1].setEnabled(false);
        cellSize[2].setEnabled(false);
        builder.addComponentAndSpan(CELL_SIZE_LABEL, cellSize);
    }

    public void load() {
        blockListener.setAdjusting(true);

        MultiPlane block = model.getGeometry().getBlock();
        minMaxModel.setBean(block);
        updateDelta();

        blockListener.setAdjusting(false);
    }

    public void save() {
        model.getProject().getSystemFolder().getBlockMeshDict().setFromFile(false);
        model.getGeometry().setAutoBoundingBox(false);
        model.getGeometry().setCellSize(new double[] { cellSize[0].getDoubleValue(), cellSize[1].getDoubleValue(), cellSize[2].getDoubleValue() });
        model.getGeometry().saveUserDefinedBlock(model, minMaxModel.getBean());
    }

    public void updateBlock() {
        blockListener.setAdjusting(true);
        if (model.getGeometry().hasBlock()) {
            editBlock();
        } else {
            addBlock();
        }
        updateDelta();
        save();
        blockListener.setAdjusting(false);
    }

    private void editBlock() {
        MultiPlane block = model.getGeometry().getBlock();
        block.updatePlanes();
        EventManager.triggerEvent(this, new ChangeSurfaceEvent(minMaxModel.getBean(), false));
    }

    private void addBlock() {
        SystemFolder systemFolder = model.getProject().getSystemFolder();
        model.getGeometry().loadBlock(systemFolder.getBlockMeshDict(), systemFolder.getSnappyHexMeshDict());
        model.blockChanged();
        minMaxModel.setBean(model.getGeometry().getBlock());
        EventManager.triggerEvent(this, new ChangeSurfaceEvent(model.getGeometry().getBlock(), true));
    }

    private void updateDelta() {
        double[] d = model.getGeometry().getBlock().getDelta();
        for (int i = 0; i < d.length; i++) {
            cellSize[i].setDoubleValue(d[i]);
        }
    }

    public void turnOffShowBoxButton() {
        if (showBoxButton.isSelected()) {
            showBoxButton.doClick();
        }
    }

    public void resetToDefault() {
        blockListener.setAdjusting(true);
        minMaxModel.setBean(new MultiPlane("BoundingBox"));
        blockListener.setAdjusting(false);
//        minMaxModel.setDictionary(new Dictionary(defaultBoxModelDict));
    }

//    private Dictionary defaultBoxModelDict = new Dictionary("block") {
//        {
//            add(MIN_KEY, new String[] { "-1.0", "-1.0", "-1.0" });
//            add(MAX_KEY, new String[] { "1.0", "1.0", "1.0" });
//            add("patch0", "ffminx");
//            add("patch1", "ffminx");
//            add("patch2", "ffminx");
//            add("patch3", "ffmaxx");
//            add("patch4", "ffminz");
//            add("patch5", "ffmaxz");
//            add(ELEMENTS_KEY, new String[] { "10", "10", "10" });
//        }
//    };

    private IntegerField[] elements;

    void addBlockListener() {
        addBlockListener(boxMin);
        addBlockListener(boxMax);
        addBlockListener(elements);
    }
    
    void addBlockListener(JComponent... components) {
        for (JComponent c : components) {
            c.addPropertyChangeListener("value", blockListener);
        }
    }

    public void geometryChanged(){
        fitButton.setEnabled(model.getGeometry().getSurfaces().length > 0);
    }

    
    private class UpdateBlockListener extends FieldChangeListener {
        @Override
        public void fieldChanged(Object source) {
            updateBlock();
        }
    }

    private class FitBoundingBoxAction extends AbstractAction {

        public FitBoundingBoxAction() {
            super("", FIT_ICON);
            putValue(SHORT_DESCRIPTION, "Fit Bounding Box");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BoundingBox bb = model.getGeometry().computeBoundingBox();

            blockListener.setAdjusting(true);

            boxMin[0].setDoubleValue(bb.getXmin());
            boxMin[1].setDoubleValue(bb.getYmin());
            boxMin[2].setDoubleValue(bb.getZmin());

            boxMax[0].setDoubleValue(bb.getXmax());
            boxMax[1].setDoubleValue(bb.getYmax());
            boxMax[2].setDoubleValue(bb.getZmax());

            blockListener.setAdjusting(false);
            
            updateBlock();
        }

    }

}
