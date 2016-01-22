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

import vtk.vtkPlaneSource;
import vtk.vtkPolyData;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;


public class PlaneRegion extends Region {
	
	double[] origin;
	double[] point1;
	double[] point2;
	
	int resolutionX;
	int resolutionY;
	
	public PlaneRegion(String name) {
		super(name);
	}

	public double[] getPoint1() {
		return point1;
	}
	
	public double[] getPoint2() {
		return point2;
	}
	
	public double[] getOrigin() {
		return origin;
	}
	
	@Override
	public Type getType() {
		return Type.PLANE;
	}
	@Override
	public String getPatchName() {
		return getName();
	}
	
	@Override
	public boolean hasLayers() {
		return true;
	}
	
	@Override
	public boolean hasSurfaceRefinement() {
		return false;
	}
	
	@Override
	public boolean hasVolumeRefinement() {
		return false;
	}

	public int getResolutionX() {
		return resolutionX;
	}

	public int getResolutionY() {
		return resolutionY;
	}
	
	@Override
	public vtkPolyData getDataSet() {
	    vtkPlaneSource planeSource = new vtkPlaneSource();
        planeSource.SetOrigin(getOrigin());
        planeSource.SetPoint1(getPoint1());
        planeSource.SetPoint2(getPoint2());
        planeSource.SetXResolution(getResolutionX());
        planeSource.SetYResolution(getResolutionY());
        planeSource.Update();
	    return planeSource.GetOutput();
	}
	
	@Override
	public Surface cloneSurface() {
	    PlaneRegion region = new PlaneRegion(name);
        cloneSurface(region);
        region.origin = origin;
        region.point1 = point1;
        region.point2 = point2;
        
        region.resolutionX = resolutionX;
        region.resolutionY = resolutionY;
        
	    return region;
	}
}
