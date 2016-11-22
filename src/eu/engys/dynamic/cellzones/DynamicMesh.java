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
package eu.engys.dynamic.cellzones;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.modules.cellzones.CellZonePanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.cellzones.CellZoneType;
import eu.engys.dynamic.DynamicModule;

public class DynamicMesh implements CellZoneType {

    public static final String DYNAMIC_MESH_LABEL = "Dynamic Mesh";

    private boolean enabled;

    private CellZonePanel panel;

    protected Model model;
    protected DynamicModule module;

    public DynamicMesh(Model model, DynamicModule module) {
        this.model = model;
        this.module = module;
        this.enabled = false;
        this.panel = createCellZonePanel();
    }
    
    protected CellZonePanel createCellZonePanel(){
        return new MultiBodyDynamicMeshPanel(model, module);
    }

    @Override
    public String getKey() {
        return CellZoneType.DYNAMIC_MESH_KEY;
    }

    @Override
    public String getLabel() {
        return DYNAMIC_MESH_LABEL;
    }

    @Override
    public void updateStatusByState() {
        setEnabled(module.getDynamicData().getAlgorithm().getType().isMultiRigidBody());
        panel.stateChanged();
    }

    @Override
    public Dictionary getDefaultDictionary() {
        return new Dictionary("");
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public CellZonePanel getPanel() {
        return panel;
    }

    @Override
    public String toString() {
        return getKey();
    }

    @Override
    public int compareTo(CellZoneType type) {
        return getLabel().compareTo(type.getLabel());
    }

}
