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


package eu.engys.core.project.geometry.surface;

import java.io.File;
import java.util.List;

import vtk.vtkPolyData;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.TransfromMode;
import eu.engys.core.project.geometry.Type;

public class Stl extends MultiRegion {

	private String fileName;

	/**
	 * @deprecated Use GeometryFactory!!
	 */
	@Deprecated
	public Stl(String name) {
		super(name);
		this.fileName = getName() + ".stl";
		Dictionary defaultSTLDictionary = new Dictionary(stl);
		defaultSTLDictionary.setName(fileName);
		defaultSTLDictionary.add("name", getName());
		defaultSTLDictionary.add("appendRegionName", "false");
		setGeometryDictionary(defaultSTLDictionary);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(File file) {
		this.fileName = file.getName();
	}

	@Override
	public Type getType() {
		return Type.STL;
	}

	public Solid[] getSolids() {
		return regions.toArray(new Solid[0]);
	}

	@Override
	public boolean isSingleton() {
		return regions.size() == 1;
	}

	public void setSolids(List<Solid> solids) {
		for (Solid solid : solids) {
			addRegion(solid);
		}
	}

	@Override
	public void rename(String newName) {
		super.rename(newName);
		if (getGeometryDictionary() != null && getGeometryDictionary().isField("name")) {
			getGeometryDictionary().add("name", newName);
			
			if (getTransformMode() == TransfromMode.TO_FILE) {
			    String dictName = getGeometryDictionary().getName();
			    if (dictName.endsWith(".stl")) {
			        this.fileName = newName + ".stl";
			    } else if (dictName.endsWith(".STL")) {
			        this.fileName = newName + ".STL";
			    }
			    
			    getGeometryDictionary().setName(fileName);
			    setModified(true);
			}
		}
	}

	@Override
	public boolean hasLayers() {
		return true;
	}

	@Override
	public boolean hasSurfaceRefinement() {
		return true;
	}

	@Override
	public boolean hasVolumeRefinement() {
		return true;
	}

	@Override
	public boolean hasZones() {
		return true;
	}

	public void buildGeometryDictionary(Dictionary dictionary) {
		Dictionary geometryDict = new Dictionary(dictionary);
		geometryDict.setName(fileName);
		setGeometryDictionary(geometryDict);
	}

	@Override
	public String getPatchName() {
	    if (getGeometryDictionary().found("name")) {
	        return getGeometryDictionary().lookup("name");
	    } else {
	        return super.getPatchName();
	    }
	}
    
    @Override
    public Surface cloneSurface() {
        Stl s = new Stl(name);
        s.fileName = this.fileName;
        cloneSurface(s);
        return s;
    }
    
    @Override
    public vtkPolyData getDataSet() {
        return null;
    }
}
