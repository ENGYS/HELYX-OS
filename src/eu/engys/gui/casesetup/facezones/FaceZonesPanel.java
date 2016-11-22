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
package eu.engys.gui.casesetup.facezones;

import static eu.engys.util.ui.ComponentsFactory.stringField;

import javax.inject.Inject;
import javax.swing.JComponent;

import eu.engys.core.project.Model;
import eu.engys.core.project.zero.facezones.FaceZone;
import eu.engys.gui.AbstractGUIPanel;
import eu.engys.gui.tree.TreeNodeManager;
import eu.engys.util.Util;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.StringField;

public class FaceZonesPanel extends AbstractGUIPanel {

    public static final String FACE_ZONES = "Face Zones";
    public static final String FACE_ZONE_NAME_LABEL = "Face Zone Name";
    public static final String FACE_ZONE_TYPE_LABEL = "Face Zone Type";

    private FaceZonesTreeNodeManager treeNodeManager;

    private StringField zoneNameField;

    @Inject
    public FaceZonesPanel(Model model) {
        super(FACE_ZONES, model);
        this.treeNodeManager = new FaceZonesTreeNodeManager(model, this);
        model.addObserver(treeNodeManager);
    }

    protected JComponent layoutComponents() {
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.addComponent(FACE_ZONE_NAME_LABEL, zoneNameField = initNameField());
        return panelBuilder.removeMargins().getPanel();
    }

    private StringField initNameField() {
        final StringField zoneNameField = stringField();
        zoneNameField.setEnabled(false);
        return zoneNameField;
    }

    public void updateSelection(FaceZone[] selection) {
        if (Util.isVarArgsNotNullAndOfSize(1, selection)) {
            zoneNameField.setValue(selection[0].getName());
            zoneNameField.setEnabled(false);
        } else {
            StringBuilder sb = new StringBuilder();
            for (FaceZone cellZone : selection) {
                sb.append(cellZone.getName() + " ");
            }
            zoneNameField.setValue(sb.toString());
            zoneNameField.setEnabled(false);
        }
    }

    @Override
    public void clear() {
        treeNodeManager.getSelectionHandler().handleSelection(false, (Object[]) new FaceZone[0]);
    }

    @Override
    public void save() {
        treeNodeManager.getSelectionHandler().handleSelection(false, (Object[]) treeNodeManager.getSelectedValues());
    }

    @Override
    public TreeNodeManager getTreeNodeManager() {
        return treeNodeManager;
    }

}
