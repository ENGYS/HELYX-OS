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

package eu.engys.core.project.materials;

import eu.engys.core.project.Model;
import eu.engys.core.project.materials.compressible.CompressibleMaterial;
import eu.engys.core.project.materials.incompressible.IncompressibleMaterial;
import eu.engys.util.bean.AbstractBean;

public abstract class Material extends AbstractBean {

	public static final String NAME_KEY = "name";
	
    private String name;
//	private Dictionary dictionary;

	public static Material newMaterial(Model model, String name) {
	   return model.getState().isCompressible() ? new CompressibleMaterial(name) : new IncompressibleMaterial(name);
	}

	public static Material newMaterial(String name, Material m) {
	    return m instanceof CompressibleMaterial ? new CompressibleMaterial(name, (CompressibleMaterial) m) : new IncompressibleMaterial(name, (IncompressibleMaterial) m);
	}

	public static Material newMaterial(Material m) {
	    return m instanceof CompressibleMaterial ? new CompressibleMaterial((CompressibleMaterial) m) : new IncompressibleMaterial((IncompressibleMaterial) m);
	}
	
    public static Material newDefaultMaterial(Model model, String materialName) {
        if (model.getState().isCompressible()) {
            return new CompressibleMaterial(materialName, model.getMaterialsDatabase().getCompressibleMaterial(materialName));
        } else {
            return new IncompressibleMaterial(materialName, model.getMaterialsDatabase().getIncompressibleMaterial(materialName));
        }
    }
	
	protected Material() {
	}

	protected Material(String name) {
		this.name = name;
	}

    public String getName() {
		return name;
	}
	
//	public Dictionary getDictionary() {
//		return dictionary;
//	}

	@Override
	public String toString() {
	    return name;
	}

//	public void setDictionary(Dictionary d) {
//		this.dictionary = d;
//	}

	public void setName(String name) {
	    firePropertyChange(NAME_KEY, this.name, this.name = name);
	}

}
