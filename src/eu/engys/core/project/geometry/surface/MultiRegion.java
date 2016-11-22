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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.TransfromMode;
import eu.engys.core.project.geometry.stl.AffineTransform;

public abstract class MultiRegion extends Surface {

	protected Map<String, Region> regionsMap = new HashMap<String, Region>();
	protected List<Region> regions = new ArrayList<Region>();
    private boolean modified = false;

	public MultiRegion(String name) {
		super(name);
	}

	public void addRegion(Region region) {
	    setName(region);
		region.setParent(this);
		regions.add(region);
		regionsMap.put(region.getName(), region);
	}

	private void setName(Region region) {
	    String name = region.getName();
	    int counter = 0;
	    while (regionsMap.containsKey(name)) {
	        name += counter++;
        }
	    
	    region.rename(name); 
    }

    public Region[] getRegions() {
		return regions.toArray(new Region[regions.size()]);
	}

	@Override
	public boolean hasRegions() {
		return true;
	}

	protected void clearRegions() {
		regions.clear();
	}

	@Override
	public String getPatchName() {
		return getName();
	}

	public void renameRegion(String oldName, String newName) {
		regionsMap.put(newName, regionsMap.remove(oldName));
	}

	public void removeRegion(String name) {
	    regions.remove(regionsMap.remove(name));
	}

	public boolean isSingleton() {
		return regions.size() == 1;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		for (Region region : regions) {
			region.setVisible(visible);
		}
	}

	@Override
	public void setTransformation(AffineTransform transformation) {
	    super.setTransformation(transformation);
	    for (Region region : regions) {
	        region.setTransformation(transformation);
	    }
	}
	
	@Override
	public void setTransformMode(TransfromMode transformMode) {
	    super.setTransformMode(transformMode);
	    for (Region region : regions) {
	        region.setTransformMode(transformMode);
	    }
	}
	
	@Override
	protected void cloneSurface(Surface surface) {
	    super.cloneSurface(surface);
	    MultiRegion mr = (MultiRegion) surface;
	    for (Region region : regions) {
	        mr.addRegion((Region) region.cloneSurface());
	    }
	}

    public void setModified(boolean modified) {
        this.modified = modified;
    }
    public boolean isModified() {
        return modified;
    }
    
    @Override
    public void copyFrom(Surface delegate, boolean changeGeometry, boolean changeSurface, boolean changeVolume, boolean changeLayer, boolean changeZone) {
        super.copyFrom(delegate, changeGeometry, changeSurface, changeVolume, changeLayer, changeZone);
    }
}
