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

package eu.engys.gui.casesetup.cellzones;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.modules.cellzones.CellZonePanel;
import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.core.project.zero.cellzones.CellZoneType;
import eu.engys.util.ui.builder.PanelBuilder;

public class SourcePanelContainer extends JPanel {

    private PanelBuilder builder;
    private List<CellZoneType> types;

    public SourcePanelContainer(List<CellZoneType> types) {
        super(new BorderLayout());
        this.types = types;
        this.builder = new PanelBuilder();
        layoutComponents();
    }

    private void layoutComponents() {
        for (CellZoneType type : types) {
            CellZonePanel typePanel = type.getPanel();
            typePanel.layoutPanel();

            builder.startHidable(type.getKey());

            builder.startGroup("empty");
            builder.endGroup();

            builder.startGroup(type.getLabel());
            builder.addComponent(typePanel.getPanel());
            builder.endGroup();

            builder.endHidable();
        }
        add(builder.removeMargins().getPanel());
    }

    public void handleSelectionOnTree(CellZone cellZone) {
        if (cellZone != null) {
            for (CellZoneType type : types) {
                if (cellZone.hasType(type.getKey()) && type.isEnabled()) {
                    showPanel(type, cellZone.getDictionary(type.getKey()));
                } else {
                    hidePanel(type);
                }
            }
        } else {
            hideAllPanels();
        }
    }

    public void showPanel(CellZoneType type, Dictionary cellZoneDictionary) {
        type.getPanel().loadFromDictionary(cellZoneDictionary);
        builder.setShowing(type.getKey(), type.getLabel());
    }

    public void hidePanel(CellZoneType type) {
        builder.setShowing(type.getKey(), "empty");
    }

    private void hideAllPanels() {
        for (CellZoneType type : types) {
            hidePanel(type);
        }
    }

    public void saveDictionaryToCellZone(CellZone zone) {
        for (CellZoneType type : types) {
            String typeKey = type.getKey();
            if (type.isEnabled()) {
                if (zone.getTypes().contains(typeKey)) {
                    Dictionary dict = type.getPanel().saveToDictionary();
                    zone.setDictionary(typeKey, dict);
                } else {
                    zone.removeDictionary(typeKey);
                }
            } else {
                zone.removeDictionary(typeKey);
            }
        }
    }
}
