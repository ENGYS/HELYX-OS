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
package eu.engys.dynamic;

import static eu.engys.core.project.zero.cellzones.CellZoneType.DYNAMIC_MESH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DYNAMIC_MESH_DICT;
import static eu.engys.dynamic.DynamicMeshDict.MULTI_SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.modules.CaseSetupReader;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.State;
import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.dynamic.data.DynamicDataReader;

public class DynamicCaseSetupReader implements CaseSetupReader {

    private DynamicModule module;

    public DynamicCaseSetupReader(Model model, DynamicModule module) {
        this.module = module;
    }

    @Override
    public void readFromState(State state, String stateString) {
        if (stateString.contains(DynamicModule.DYNAMIC_KEY)) {
            state.setDynamic(true);
        }
    }

    @Override
    public void readFromSystem(Dictionary system) {
    }

    @Override
    public void readFromConstant(Model loadedModel, Dictionary constant) {
        if (constant.isDictionary(DYNAMIC_MESH_DICT)) {
            Dictionary dynamicMeshDict = new Dictionary(constant.subDict(DYNAMIC_MESH_DICT));
            module.setDynamicMeshDict(dynamicMeshDict);
            module.getDynamicData().setAlgorithm(new DynamicDataReader().readAlgorithm(dynamicMeshDict));

            if (module.getDynamicData().getAlgorithm().getType().isMultiRigidBody()) {
                if (dynamicMeshDict.found(MULTI_SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY)) {
                    Dictionary coeffsDict = dynamicMeshDict.subDict(MULTI_SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY);
                    addCellZones(loadedModel, coeffsDict);
                }
            }
        }
    }

    private void addCellZones(Model loadedModel, Dictionary coeffsDict) {
        for (Dictionary d : coeffsDict.getDictionaries()) {
            CellZone cz = new CellZone(d.getName());
            cz.setDictionary(DYNAMIC_MESH_KEY, new Dictionary(""));
            cz.getTypes().add(DYNAMIC_MESH_KEY);
            loadedModel.getCellZones().add(cz);
        }
    }

    @Override
    public void readMaterials(Model model, Dictionary globals) {
    }
}
