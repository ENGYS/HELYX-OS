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

package eu.engys.core.project.geometry.surface;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;
import eu.engys.util.VTKSettings;
import vtk.vtkPolyData;

public class Solid extends Region {
    private vtkPolyData dataSet;
	
	public Solid(String name) {
		super(name);
	}

	@Override
	public Type getType() {
		return Type.SOLID;
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
		return false;
	}
	
	@Override
	public boolean hasZones() {
		return false;
	}
	
	@Override
	public Surface cloneSurface() {
	    Solid solid = new Solid(name);
	    solid.setParent(getParent());
	    cloneSurface(solid);
	    
	    if (VTKSettings.librariesAreLoaded()) {
	        solid.dataSet = new vtkPolyData();
	        solid.dataSet.ShallowCopy(dataSet);
	    }
	    
        return solid;
	}

    public void setDataSet(vtkPolyData dataSet) {
        this.dataSet = dataSet;
    }
    
    @Override
    public vtkPolyData getDataSet() {
        return dataSet;
    }
    
    @Override
    public Dictionary toGeometryDictionary() {
        return null;
    }
    
    @Override
    public void fromGeometryDictionary(Dictionary g) {
    }
    
}
