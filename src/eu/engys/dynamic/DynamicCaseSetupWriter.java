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

import static eu.engys.dynamic.DynamicModule.DYNAMIC_KEY;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.modules.CaseSetupWriter;
import eu.engys.core.project.Model;
import eu.engys.core.project.materials.Materials;
import eu.engys.core.project.state.State;
import eu.engys.dynamic.data.DynamicDataWriter;

public class DynamicCaseSetupWriter implements CaseSetupWriter {

    private Model model;
    private DynamicModule module;

    public DynamicCaseSetupWriter(Model model, DynamicModule module) {
        this.model = model;
        this.module = module;
    }

    @Override
    public void addState(State state, StringBuffer sb) {
        if (state.isDynamic())
            sb.append(" " + DYNAMIC_KEY);
    }

    @Override
    public void addToSystem(State state, Dictionary system) {
    }

    @Override
    public void addToConstant(State state, Dictionary constant) {
        if (state.isDynamic()) {
            Dictionary dynamicMeshDict = new DynamicDataWriter(model).writeAlgorithm(module.getDynamicData().getAlgorithm());
            module.setDynamicMeshDict(dynamicMeshDict);
            constant.add(new Dictionary(dynamicMeshDict));
        }
    }

    @Override
    public void addToMaterials(Materials materials, Dictionary materialsProperties) {
    }

}
