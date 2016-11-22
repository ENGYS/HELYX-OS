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

import static eu.engys.core.project.zero.cellzones.CellZoneType.DYNAMIC_MESH_KEY;

import java.util.ArrayList;
import java.util.List;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.modules.cellzones.CellZonesView;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.core.project.zero.cellzones.CellZoneType;
import eu.engys.core.project.zero.cellzones.CellZones;
import eu.engys.dynamic.DynamicModule;
import eu.engys.dynamic.data.multibody.MultiBodyAlgorithm;

public class DynamicCellZonesView implements CellZonesView {

    protected Model model;
    protected DynamicModule module;

    public DynamicCellZonesView(Model model, DynamicModule module) {
        this.model = model;
        this.module = module;
    }

    @Override
    public List<CellZoneType> getCellZoneTypes() {
        List<CellZoneType> types = new ArrayList<CellZoneType>();
        types.add(new DynamicMesh(model, module));
        return types;
    }

    @Override
    public void updateCellZonesFromModel(CellZones cellZones) {
        if (module.getDynamicData().getAlgorithm().getType().isMultiRigidBody()) {
            MultiBodyAlgorithm multiBody = (MultiBodyAlgorithm) module.getDynamicData().getAlgorithm();
            for (CellZone cz : cellZones) {
                if (multiBody.getSingleBodyAlgorithms().containsKey(cz.getName())) {
                    cz.setDictionary(DYNAMIC_MESH_KEY, new Dictionary(""));
                    cz.getTypes().add(CellZoneType.DYNAMIC_MESH_KEY);
                }
            }
        }
    }

    @Override
    public void updateModelFromCellZones() {
    }

}
