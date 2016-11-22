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

import static eu.engys.core.project.constant.ConstantFolder.CONSTANT;
import static eu.engys.dynamic.DynamicMeshDict.DYNAMIC_FV_MESH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DYNAMIC_MESH_DICT;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.project.Model;
import eu.engys.dynamic.data.DynamicAlgorithmType;
import eu.engys.dynamic.data.DynamicDataReader;
import eu.engys.util.progress.SilentMonitor;

public class DynamicReader {

    private Model model;
    private DynamicModule module;

    public DynamicReader(Model model, DynamicModule module) {
        this.model = model;
        this.module = module;
    }

    public void readDynamicMeshDict() {
        Dictionary dynamicMeshDict;
        if (dynamicMeshDictExists()) {
            dynamicMeshDict = readFromFile();
        } else {
            dynamicMeshDict = readFromDefaults();
        }
        module.setDynamicMeshDict(dynamicMeshDict);
        module.getDynamicData().setAlgorithm(new DynamicDataReader().readAlgorithm(module.getDynamicMeshDict()));
    }

    private boolean dynamicMeshDictExists() {
        if (model.getProject() == null) {
            return false;
        }
        return Files.exists(Paths.get(model.getProject().getBaseDir().getAbsolutePath(), CONSTANT, DYNAMIC_MESH_DICT));
    }

    public void loadState() {
        if (module.getDynamicMeshDict().found(DYNAMIC_FV_MESH_KEY)) {
            DynamicAlgorithmType data = DynamicAlgorithmType.byKey(module.getDynamicMeshDict().lookupString(DYNAMIC_FV_MESH_KEY));
            model.getState().setDynamic(data.isOn());
        } else {
            model.getState().setDynamic(false);
        }
    }

    private Dictionary readFromFile() {
        Path dynamicMeshDictPath = Paths.get(model.getProject().getBaseDir().getAbsolutePath(), CONSTANT, DYNAMIC_MESH_DICT);
        File file = dynamicMeshDictPath.toFile();
        return DictionaryUtils.readDictionary(file, new SilentMonitor());
    }

    private Dictionary readFromDefaults() {
        return ModulesUtil.readDictionary(module, null, DynamicMeshDict.DYNAMIC_MESH_DICT);
    }

}
