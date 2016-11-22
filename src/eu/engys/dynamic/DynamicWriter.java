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

import java.io.File;
import java.nio.file.Paths;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.project.Model;
import eu.engys.dynamic.data.DynamicDataWriter;
import eu.engys.util.progress.SilentMonitor;

public class DynamicWriter {

    private Model model;
    private DynamicModule module;

    public DynamicWriter(Model model, DynamicModule module) {
        this.model = model;
        this.module = module;
    }

    public void write() {
        Dictionary dynamicMeshDict = new DynamicDataWriter(model).writeAlgorithm(module.getDynamicData().getAlgorithm());
        module.setDynamicMeshDict(dynamicMeshDict);
        
        File constDir = Paths.get(model.getProject().getBaseDir().getAbsolutePath(), CONSTANT).toFile();
        DictionaryUtils.writeDictionary(constDir, DictionaryUtils.header(CONSTANT, dynamicMeshDict), new SilentMonitor());
    }

}
