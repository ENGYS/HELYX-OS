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

import vtk.vtkLineSource;
import vtk.vtkPolyData;
import vtk.vtkTubeFilter;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;

public class Cylinder extends BaseSurface {

	/**
	 * @deprecated  Use GeometryFactory!!
	 */
	@Deprecated
	public Cylinder(String name) {
		super(name);
		Dictionary geometryDictionary = new Dictionary(cylinder);
		geometryDictionary.setName(name);
		setGeometryDictionary(geometryDictionary);
	}

	@Override
	public Type getType() {
		return Type.CYLINDER;
	}

	public double[] getPoint1() {
		if (getGeometryDictionary() != null && getGeometryDictionary().found(POINT1_KEY))
			return getGeometryDictionary().lookupDoubleArray(POINT1_KEY);
		else
			return new double[] { 0, 0, 0 };
	}

	public double[] getPoint2() {
		if (getGeometryDictionary() != null && getGeometryDictionary().found(POINT2_KEY))
			return getGeometryDictionary().lookupDoubleArray(POINT2_KEY);
		else
			return new double[] { 0, 0, 0 };
	}

	public double getRadius() {
		if (getGeometryDictionary() != null && getGeometryDictionary().found(RADIUS_KEY))
			return Double.valueOf(getGeometryDictionary().lookup(RADIUS_KEY));
		else
			return 0;
	}
    
    @Override
    public Surface cloneSurface() {
        Surface box = new Cylinder(name);
        cloneSurface(box);
        return box;
    }
    
    @Override
    public vtkPolyData getDataSet() {
        double[] point1 = getPoint1();
        double[] point2 = getPoint2();
        double   radius = getRadius();
        
        vtkLineSource lineSource = new vtkLineSource();
        lineSource.SetPoint1(point1);
        lineSource.SetPoint2(point2);

        // Create a tube (cylinder) around the line
        vtkTubeFilter tubeFilter = new vtkTubeFilter();
        tubeFilter.SetInputConnection(lineSource.GetOutputPort());
        tubeFilter.SetCapping(1);
        tubeFilter.SetRadius(radius);
        tubeFilter.SetNumberOfSides(50);
        tubeFilter.Update();
        
        return tubeFilter.GetOutput();
    }
}
