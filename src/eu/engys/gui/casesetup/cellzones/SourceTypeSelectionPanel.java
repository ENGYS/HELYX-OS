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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import eu.engys.core.project.Model;
import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.core.project.zero.cellzones.CellZoneType;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.builder.PanelBuilder;

public class SourceTypeSelectionPanel extends JPanel {

    public static final String ZONE_TYPE_ACTIVE = "zoneTypeActive";
    public static final String ZONE_TYPE_INACTIVE = "zoneTypeInactive";
    public static final String UPDATE_SELECTION = "updateSelection";

    private Map<CellZoneType, JCheckBox> checkBoxMap = new HashMap<>();
    private PanelBuilder builder;
    private ActionListener checkBoxListener;

    public SourceTypeSelectionPanel(Model m, List<CellZoneType> types) {
        super(new BorderLayout());

        builder = new PanelBuilder();

        checkBoxListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox checkBox = (JCheckBox) e.getSource();
                boolean selected = checkBox.isSelected();
                CellZoneType type = (CellZoneType) checkBox.getClientProperty("type");
                firePropertyChange(selected ? ZONE_TYPE_ACTIVE : ZONE_TYPE_INACTIVE, null, type);
            }
        };

        for (CellZoneType zoneType : types) {
            JCheckBox checkBox = ComponentsFactory.checkField(zoneType.getLabel());
            checkBox.putClientProperty("type", zoneType);
            checkBox.addActionListener(checkBoxListener);

            checkBox.setEnabled(false);
            checkBox.setName(zoneType.getLabel());
            checkBoxMap.put(zoneType, checkBox);
            builder.addComponent(checkBox);
        }
        add(builder.removeMargins().getPanel());
    }

    public void handleSelectionOnTree(CellZone cellZone) {
        removeListeners();
        if (cellZone != null) {
            selectCheckBoxes(cellZone);
        } else {
            unselectAndDisableCheckBoxes();
        }
        addListeners();
    }

    public void saveTypesToCellZone(CellZone zone) {
        for (CellZoneType type : checkBoxMap.keySet()) {
            JCheckBox checkBox = checkBoxMap.get(type);
            if (checkBox.isSelected()) {
                zone.getTypes().add(type.getKey());
            } else {
                zone.getTypes().remove(type.getKey());
            }
        }
    }

    private void selectCheckBoxes(CellZone cellZone) {
        for (CellZoneType type : checkBoxMap.keySet()) {
            JCheckBox checkBox = checkBoxMap.get(type);
            checkBox.setEnabled(type.isEnabled());
            if (cellZone.getTypes().contains(type.getKey()) && type.isEnabled()) {
                checkBox.setSelected(true);
            } else {
                checkBox.setSelected(false);
            }
        }
    }

    private void unselectAndDisableCheckBoxes() {
        for (CellZoneType type : checkBoxMap.keySet()) {
            JCheckBox checkBox = checkBoxMap.get(type);
            checkBox.setSelected(false);
            checkBox.setEnabled(false);
        }
    }

    private void addListeners() {
        for (JCheckBox check : checkBoxMap.values()) {
            check.addActionListener(checkBoxListener);
        }
    }

    private void removeListeners() {
        for (JCheckBox check : checkBoxMap.values()) {
            check.removeActionListener(checkBoxListener);
        }
    }
}
