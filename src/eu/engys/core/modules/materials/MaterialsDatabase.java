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

package eu.engys.core.modules.materials;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.defaults.Defaults;

public class MaterialsDatabase {

    private Map<String, Dictionary> compressibleMaterialsMap = new HashMap<>();
    private Map<String, Dictionary> incompressibleMaterialsMap = new HashMap<>();
	public static final String AIR = "air";
	public static final String MERCURY = "mercury";
	public static final String WATER = "water";
	public static final String OIL = "oil";

    @Inject
    public MaterialsDatabase(Defaults defaults) {
        load(defaults);
    }

    private void load(Defaults defaults) {
        compressibleMaterialsMap.clear();
        incompressibleMaterialsMap.clear();

        Dictionary compressibleMaterials = defaults.getCompressibleMaterials();
        Dictionary incompressibleMaterials = defaults.getIncompressibleMaterials();

        for (Dictionary matDict : compressibleMaterials.getDictionaries()) {
            matDict.add("materialName", matDict.getName());
            compressibleMaterialsMap.put(matDict.getName(), matDict);
        }
        for (Dictionary matDict : incompressibleMaterials.getDictionaries()) {
            matDict.add("materialName", matDict.getName());
            incompressibleMaterialsMap.put(matDict.getName(), matDict);
        }
    }

    public Map<String, Dictionary> getCompressibleMaterialsMap() {
        return compressibleMaterialsMap;
    }

    public Map<String, Dictionary> getIncompressibleMaterialsMap() {
        return incompressibleMaterialsMap;
    }

    public Collection<Dictionary> getCompressibleMaterials() {
        return Collections.unmodifiableCollection(compressibleMaterialsMap.values());
    }

    public Collection<Dictionary> getIncompressibleMaterials() {
        return Collections.unmodifiableCollection(incompressibleMaterialsMap.values());
    }

    public Dictionary getCompressibleMaterial(String materialName) {
        return new Dictionary(compressibleMaterialsMap.get(materialName));
    }

    public Dictionary getIncompressibleMaterial(String materialName) {
        return new Dictionary(incompressibleMaterialsMap.get(materialName));
    }

}
