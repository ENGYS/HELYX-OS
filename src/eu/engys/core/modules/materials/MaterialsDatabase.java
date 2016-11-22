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
package eu.engys.core.modules.materials;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.defaults.Defaults;
import eu.engys.core.project.materials.MaterialsReader;
import eu.engys.core.project.materials.compressible.CompressibleMaterial;
import eu.engys.core.project.materials.incompressible.IncompressibleMaterial;

public class MaterialsDatabase {

    private Map<String, CompressibleMaterial > compressibleMaterialsMap = new HashMap<>();
    private Map<String, IncompressibleMaterial > incompressibleMaterialsMap = new HashMap<>();
	public static final String AIR = "air";
	public static final String MERCURY = "mercury";
	public static final String WATER = "water";
	public static final String OIL = "oil";

    @Inject
    public MaterialsDatabase(Defaults defaults, MaterialsReader reader) {
        load(defaults, reader);
    }

    private void load(Defaults defaults, MaterialsReader reader) {
        compressibleMaterialsMap.clear();
        incompressibleMaterialsMap.clear();

        Dictionary compressibleMaterials = defaults.getCompressibleMaterials();
        Dictionary incompressibleMaterials = defaults.getIncompressibleMaterials();

        for (Dictionary matDict : compressibleMaterials.getDictionaries()) {
            String name = matDict.getName();
            matDict.add("materialName", name);
            compressibleMaterialsMap.put(name, reader.readCompressibleMaterial(matDict));
        }
        for (Dictionary matDict : incompressibleMaterials.getDictionaries()) {
            String name = matDict.getName();
            matDict.add("materialName", name);
            incompressibleMaterialsMap.put(name, reader.readIncompressibleMaterial(matDict));
        }
    }

    public Map<String, CompressibleMaterial> getCompressibleMaterialsMap() {
        return compressibleMaterialsMap;
    }

    public Map<String, IncompressibleMaterial> getIncompressibleMaterialsMap() {
        return incompressibleMaterialsMap;
    }

    public Collection<CompressibleMaterial> getCompressibleMaterials() {
        return Collections.unmodifiableCollection(compressibleMaterialsMap.values());
    }

    public Collection<IncompressibleMaterial> getIncompressibleMaterials() {
        return Collections.unmodifiableCollection(incompressibleMaterialsMap.values());
    }

    public CompressibleMaterial getCompressibleMaterial(String materialName) {
        return new CompressibleMaterial(compressibleMaterialsMap.get(materialName));
    }

    public IncompressibleMaterial getIncompressibleMaterial(String materialName) {
        return new IncompressibleMaterial (incompressibleMaterialsMap.get(materialName));
    }

}
