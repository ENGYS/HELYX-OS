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

package eu.engys.gui.mesh.panels.lines;

import static eu.engys.core.project.geometry.Surface.MAX_KEY;
import static eu.engys.core.project.geometry.Surface.MIN_KEY;
import static eu.engys.core.project.system.BlockMeshDict.ELEMENTS_KEY;
import static eu.engys.util.ui.ComponentsFactory.doublePointField;
import static eu.engys.util.ui.ComponentsFactory.labelField;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FieldChangeListener;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.geometry.surface.MultiPlane;
import eu.engys.core.project.system.SystemFolder;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.ChangeSurfaceEvent;
import eu.engys.gui.view3D.BoxEventButton;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.IntegerField;

public class UserDefinedBaseMeshPanel {

    public static final String USER_DEFINED_LABEL = "User Defined";

    public static final String CELL_SIZE_LABEL = "Cell Size [m]";
    public static final String N_ELEMENTS_LABEL = "Elements";
    public static final String MAX_LABEL = "Max";
    public static final String MIN_LABEL = "Min";

    private final static Icon FIT_ICON = ResourcesUtil.getIcon("fit.boundingbox.icon");

    private DictionaryPanelBuilder builder;

    private DoubleField[] boxMin;
    private DoubleField[] boxMax;
    private DoubleField[] cellSize;
    private JToggleButton showBoxButton;

    private Model model;

    private DictionaryModel minMaxModel;
    private UpdateBlockListener blockListener;

    public UserDefinedBaseMeshPanel(Model model, DictionaryPanelBuilder builder) {
        this.model = model;
        this.builder = builder;

        minMaxModel = new DictionaryModel(new Dictionary(defaultBoxModelDict));
        builder.startDictionary(USER_DEFINED_LABEL, minMaxModel);
        layoutComponents();
        builder.endDictionary();
    }

    private void layoutComponents() {
        blockListener = new UpdateBlockListener();

        boxMin = minMaxModel.bindPoint(MIN_KEY, 4, blockListener);
        boxMax = minMaxModel.bindPoint(MAX_KEY, 4, blockListener);
        showBoxButton = new BoxEventButton(boxMin, boxMax);

        builder.addComponent("", labelField("X"), labelField("Y"), labelField("Z"));
        builder.addComponent(MIN_LABEL, boxMin[0], boxMin[1], boxMin[2], showBoxButton);
        builder.addComponentAndSpan(MAX_LABEL, boxMax);

        IntegerField[] elements = minMaxModel.bindIntegerArray(ELEMENTS_KEY, 3, blockListener);
        JButton fitButton = new JButton(new FitBoundingBoxAction());
        fitButton.setPreferredSize(new Dimension(36, 48));

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
        minMaxModel.setDictionary(block.getGeometryDictionary());
        updateDelta();

        blockListener.setAdjusting(false);
    }

    public void save() {
        model.getProject().getSystemFolder().getBlockMeshDict().setFromFile(false);
        model.getGeometry().setAutoBoundingBox(false);
        model.getGeometry().setCellSize(new double[] { cellSize[0].getDoubleValue(), cellSize[1].getDoubleValue(), cellSize[2].getDoubleValue() });
        model.getGeometry().saveUserDefinedBlock(model, minMaxModel.getDictionary());
    }

    public void updateBlock() {
        if (model.getGeometry().hasBlock()) {
            editBlock();
        } else {
            addBlock();
        }
        updateDelta();
        save();
    }

    private void editBlock() {
        MultiPlane block = model.getGeometry().getBlock();
        block.setGeometryDictionary(minMaxModel.getDictionary());

        EventManager.triggerEvent(this, new ChangeSurfaceEvent(block, false));
    }

    private void addBlock() {
        SystemFolder systemFolder = model.getProject().getSystemFolder();
        model.getGeometry().loadBlock(systemFolder.getBlockMeshDict(), systemFolder.getSnappyHexMeshDict());
        model.blockChanged();
        minMaxModel.setDictionary(model.getGeometry().getBlock().getGeometryDictionary());
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
        minMaxModel.setDictionary(new Dictionary(defaultBoxModelDict));
    }

    private Dictionary defaultBoxModelDict = new Dictionary("block") {
        {
            add(MIN_KEY, new String[] { "-1.0", "-1.0", "-1.0" });
            add(MAX_KEY, new String[] { "1.0", "1.0", "1.0" });
            add("patch0", "ffminx");
            add("patch1", "ffminx");
            add("patch2", "ffminx");
            add("patch3", "ffmaxx");
            add("patch4", "ffminz");
            add("patch5", "ffmaxz");
            add(ELEMENTS_KEY, new String[] { "10", "10", "10" });
        }
    };

    private class UpdateBlockListener implements FieldChangeListener {

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
                updateBlock();
            }
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
