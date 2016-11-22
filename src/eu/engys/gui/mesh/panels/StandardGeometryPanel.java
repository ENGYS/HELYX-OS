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

import static eu.engys.core.project.system.SnappyHexMeshDict.BAFFLE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.BOUNDARY_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.EXPANSION_RATIO_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FACE_TYPE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FACE_ZONE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FINAL_LAYER_THICKNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.GAP_LEVEL_INCREMENT_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.INTERNAL_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.IS_CELL_ZONE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.LEVEL_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_THICKNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.NONE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_SURFACE_LAYERS_KEY;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.google.inject.Inject;

import eu.engys.core.controller.Controller;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.IntegerField;
import eu.engys.util.ui.textfields.StringField;

public class StandardGeometryPanel extends AbstractGeometryPanel {

    private static final String LAYER_MIN_THICKNESS_LABEL = "Layer Min Thickness";

    @Inject
    public StandardGeometryPanel(Model model, Controller controller, ProgressMonitor monitor) {
        super(model, controller, monitor);
    }

    @Override
    protected DefaultGeometryActions getGeometryActions(Controller controller) {
        return new DefaultGeometryActions(this, controller);
    }

    @Override
    protected JPanel getSurfacesPanel() {
        surfaceBuilder = new PanelBuilder();
        surfaceBuilder.addComponent(LEVEL_LABEL, surfaceModel.bindIntegerArray(LEVEL_KEY, 2));
        surfaceBuilder.addComponent(PROXIMITY_REFINEMENT_LABEL, surfaceModel.bindIntegerPositive(GAP_LEVEL_INCREMENT_KEY));
        surfaceBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(SURFACE_LABEL));
        return surfaceBuilder.getPanel();
    }

    @Override
    protected JPanel getLayersPanel() {
        layersBuilder = new PanelBuilder();
        layersBuilder.addComponent(NUMBER_OF_LAYERS_LABEL, layerModel.bindIntegerPositive(N_SURFACE_LAYERS_KEY));
        layersBuilder.addComponent(FINAL_LAYER_THICKNESS_LABEL, layerModel.bindDouble(FINAL_LAYER_THICKNESS_KEY, (Double) null));
        layersBuilder.addComponent(LAYER_STRETCHING_LABEL, layerModel.bindDouble(EXPANSION_RATIO_KEY, (Double) null));
        layersBuilder.addComponent(LAYER_MIN_THICKNESS_LABEL, layerModel.bindDouble(MIN_THICKNESS_KEY, (Double) null));
        return layersBuilder.getPanel();
    }

    @Override
    protected JPanel getZonesPanel() {
        zonesBuilder = new PanelBuilder();
        String[] TYPE_KEYS = { NONE_KEY, INTERNAL_KEY, BOUNDARY_KEY, BAFFLE_KEY };
        String[] TYPE_LABELS = { NONE_LABEL, INTERNAL_LABEL, BOUNDARY_LABEL, BAFFLE_LABEL };
        final JComboBox<String> zoneType = zoneModel.bindSelection(FACE_TYPE_KEY, TYPE_KEYS, TYPE_LABELS);
        final StringField zoneName = zoneModel.bindLabel(FACE_ZONE_KEY, true);
        final JCheckBox isCellZone = zoneModel.bindBoolean(IS_CELL_ZONE_KEY);
        final IntegerField[] zoneLevel = zoneModel.bindIntegerArray(LEVEL_KEY, 2);

        zonesBuilder.addComponent(TYPE_LABEL, zoneType);
        zonesBuilder.addComponent(NAME_LABEL, zoneName);
        zonesBuilder.addComponent(CELL_ZONE_LABEL, isCellZone);
        zonesBuilder.addComponent(LEVEL_LABEL, zoneLevel);

        zoneName.setEnabled(false);
        isCellZone.setEnabled(false);
        zoneLevel[0].setEnabled(false);
        zoneLevel[1].setEnabled(false);

        zoneType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean enabled = zoneType.getSelectedIndex() > 0;
                zoneName.setEnabled(enabled);
                zoneLevel[0].setEnabled(enabled);
                zoneLevel[1].setEnabled(enabled);
                isCellZone.setEnabled(enabled);
            }
        });
        return zonesBuilder.getPanel();
    }

    // public static void main(String[] args) {
    // SwingUtilities.invokeLater(new Runnable() {
    //
    // @Override
    // public void run() {
    // new HelyxOSLookAndFeel().init();
    // StandardGeometryPanel panel = new StandardGeometryPanel(new Model());
    // panel.layoutPanel();
    // UiUtil.show("Prova", panel);
    // panel.layerModel.setDictionary(new Dictionary("pippo"));
    // }
    // });
    // }

    @Override
    public JButton[] getShapeButtons() {
        JButton[] buttons = new JButton[5];

        JButton stlButton = new JButton(ActionManager.getInstance().get("mesh.stl"));
        stlButton.setName("add.stl.button");
        buttons[0] = stlButton;

        JButton boxButton = new JButton(ActionManager.getInstance().get("mesh.box"));
        boxButton.setName("add.box.button");
        buttons[1] = boxButton;

        stlButton.setPreferredSize(boxButton.getPreferredSize());

        JButton sphereButton = new JButton(ActionManager.getInstance().get("mesh.sphere"));
        sphereButton.setName("add.sphere.button");
        buttons[2] = sphereButton;

        JButton cylinderButton = new JButton(ActionManager.getInstance().get("mesh.cylinder"));
        cylinderButton.setName("add.cylinder.button");
        buttons[3] = cylinderButton;

        JButton planeButton = new JButton(ActionManager.getInstance().get("mesh.plane"));
        planeButton.setName("add.plane.button");
        buttons[4] = planeButton;

        return buttons;
    }

}
