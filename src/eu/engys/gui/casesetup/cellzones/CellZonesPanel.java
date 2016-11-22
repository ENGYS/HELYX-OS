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
package eu.engys.gui.casesetup.cellzones;

import static eu.engys.gui.casesetup.cellzones.SourceTypeSelectionPanel.UPDATE_SELECTION;
import static eu.engys.gui.casesetup.cellzones.SourceTypeSelectionPanel.ZONE_TYPE_ACTIVE;
import static eu.engys.gui.casesetup.cellzones.SourceTypeSelectionPanel.ZONE_TYPE_INACTIVE;
import static eu.engys.util.ui.ComponentsFactory.stringField;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.swing.JComponent;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.core.project.zero.cellzones.CellZoneType;
import eu.engys.gui.AbstractGUIPanel;
import eu.engys.gui.tree.TreeNodeManager;
import eu.engys.util.Util;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.StringField;

public class CellZonesPanel extends AbstractGUIPanel {

    public static final String CELL_ZONES = "Cell Zones";
    public static final String CELL_ZONE_NAME_LABEL = "Cell Zone Name";
    public static final String CELL_ZONE_TYPE_LABEL = "Cell Zone Type";

    private CellZonesTreeNodeManager treeNodeManager;
    private List<CellZoneType> types;

    private StringField zoneNameField;
    private SourcePanelContainer centerPanel;
    private SourceTypeSelectionPanel zoneTypePanel;

    @Inject
    public CellZonesPanel(Model model, Set<CellZoneType> cellZoneTypes, Set<ApplicationModule> modules) {
        super(CELL_ZONES, model);
        this.treeNodeManager = new CellZonesTreeNodeManager(model, this);
        this.types = new LinkedList<>();
        types.addAll(cellZoneTypes);
        types.addAll(ModulesUtil.getCellZoneTypes(modules));
        Collections.sort(types, new CellZoneComparator());
        model.addObserver(treeNodeManager);
    }

    protected JComponent layoutComponents() {
        centerPanel = new SourcePanelContainer(types);

        PanelBuilder typeBuilder = new PanelBuilder();
        this.zoneNameField = initNameField();
        this.zoneTypePanel = initTypePanel();
        typeBuilder.addComponent(CELL_ZONE_NAME_LABEL, zoneNameField);
        typeBuilder.addComponent(CELL_ZONE_TYPE_LABEL, zoneTypePanel);

        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.addComponent(typeBuilder.removeMargins().getPanel());
        panelBuilder.addComponent(centerPanel);

        return panelBuilder.removeMargins().getPanel();
    }

    private StringField initNameField() {
        final StringField zoneNameField = stringField();
        zoneNameField.setEnabled(false);
        return zoneNameField;
    }

    private SourceTypeSelectionPanel initTypePanel() {
        SourceTypeSelectionPanel panel = new SourceTypeSelectionPanel(model, types);
        panel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ZONE_TYPE_ACTIVE)) {
                    CellZone[] selection = treeNodeManager.getSelectedValues();
                    CellZoneType type = (CellZoneType) evt.getNewValue();
                    if (Util.isVarArgsNotNullAndOfSize(1, selection)) {
                        showPanel(type, selection[0]);
                    }
                } else if (evt.getPropertyName().equals(ZONE_TYPE_INACTIVE)) {
                    CellZone[] selection = treeNodeManager.getSelectedValues();
                    CellZoneType type = (CellZoneType) evt.getNewValue();
                    if (Util.isVarArgsNotNullAndOfSize(1, selection)) {
                        hidePanel(type, selection[0]);
                    }
                } else if (evt.getPropertyName().equals(UPDATE_SELECTION)) {
                    updateSelection(treeNodeManager.getSelectedValues());
                }
            }

            private void showPanel(CellZoneType type, CellZone cellZone) {
                String typeKey = type.getKey();
                cellZone.getTypes().add(typeKey);
                if (!cellZone.hasDictionary(typeKey)) {
                    cellZone.setDictionary(typeKey, type.getDefaultDictionary());
                }
                centerPanel.showPanel(cellZone.getName(), type, cellZone.getDictionary(typeKey));
            }

            private void hidePanel(CellZoneType type, CellZone cellZone) {
                cellZone.getTypes().remove(type.getKey());
                centerPanel.hidePanel(type);
            }

        });
        return panel;
    }

    public void updateSelection(CellZone[] selection) {
        if (Util.isVarArgsNotNullAndOfSize(1, selection)) {
            zoneNameField.setValue(selection[0].getName());
            zoneNameField.setEnabled(false);
            zoneTypePanel.handleSelectionOnTree(selection[0]);
            centerPanel.handleSelectionOnTree(selection[0]);
        } else {
            StringBuilder sb = new StringBuilder();
            for (CellZone cellZone : selection) {
                sb.append(cellZone.getName() + " ");
            }
            zoneNameField.setValue(sb.toString());
            zoneNameField.setEnabled(false);
            zoneTypePanel.handleSelectionOnTree(null);
            centerPanel.handleSelectionOnTree(null);
        }
    }

    @Override
    public void load() {
        for (CellZoneType type : types) {
            type.updateStatusByState();
        }
    }

    @Override
    public void stateChanged() {
        for (CellZoneType type : types) {
            type.updateStatusByState();
        }
        fixCellZonesTypes();
    }

    private void fixCellZonesTypes() {
        for (CellZone zone : model.getCellZones()) {
            for (CellZoneType type : types) {
                if (!type.isEnabled()) {
                    zone.getTypes().remove(type.getKey());
                }
            }
            centerPanel.saveDictionaryToCellZone(zone);
        }
    }

    // When change selection
    public void saveCellZones(CellZone[] values) {
        if (Util.isVarArgsNotNull(values)) {
            for (CellZone zone : values) {
                zoneTypePanel.saveTypesToCellZone(zone);
                centerPanel.saveDictionaryToCellZone(zone);
            }
        }
    }

    @Override
    public void clear() {
        treeNodeManager.getSelectionHandler().handleSelection(false, (Object[]) new CellZone[0]);
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
