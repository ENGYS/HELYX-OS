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
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;

public class Plane extends BaseSurface {

	private double diagonal = 1;

	/**
	 * @deprecated Use GeometryFactory!!
	 */
	@Deprecated
	public Plane(String name) {
		super(name);
		Dictionary geometryDictionary = new Dictionary(plane);
		geometryDictionary.setName(name);
		setGeometryDictionary(geometryDictionary);
	}

	@Override
	public Type getType() {
		return Type.PLANE;
	}

	public double[] getCenter() {
		if (getGeometryDictionary() != null && getGeometryDictionary().subDict(POINT_AND_NORMAL_DICT_KEY) != null && getGeometryDictionary().subDict(POINT_AND_NORMAL_DICT_KEY).found(BASE_POINT_KEY))
			return getGeometryDictionary().subDict(POINT_AND_NORMAL_DICT_KEY).lookupDoubleArray(BASE_POINT_KEY);
		else
			return null;
	}

    public void setCenter(double[] center) {
        if (getGeometryDictionary() != null && getGeometryDictionary().isDictionary(POINT_AND_NORMAL_DICT_KEY)){
            Dictionary dict = getGeometryDictionary().subDict(POINT_AND_NORMAL_DICT_KEY);
            dict.add(BASE_POINT_KEY, center);
        }
    }

	public double[] getNormal() {
		if (getGeometryDictionary() != null && getGeometryDictionary().subDict(POINT_AND_NORMAL_DICT_KEY) != null && getGeometryDictionary().subDict(POINT_AND_NORMAL_DICT_KEY).found(NORMAL_VECTOR_KEY))
			return getGeometryDictionary().subDict(POINT_AND_NORMAL_DICT_KEY).lookupDoubleArray(NORMAL_VECTOR_KEY);
		else
			return null;
	}

    public void setNormal(double[] normal) {
        if (getGeometryDictionary() != null && getGeometryDictionary().isDictionary(POINT_AND_NORMAL_DICT_KEY)){
            Dictionary dict = getGeometryDictionary().subDict(POINT_AND_NORMAL_DICT_KEY);
            dict.add(NORMAL_VECTOR_KEY, normal);
        }
    }

	public void setDiagonal(double diagonal) {
		this.diagonal = diagonal;
	}

	public double getDiagonal() {
		return diagonal;
	}
    
    @Override
    public Surface cloneSurface() {
        Plane plane = new Plane(name);
        cloneSurface(plane);
        plane.diagonal = this.diagonal;
        return plane;
    }
    
    @Override
    public vtkPolyData getDataSet() {

        vtkPlaneSource planeSource = new vtkPlaneSource();
        planeSource.SetOrigin(0, 0, 0);
        planeSource.SetPoint1(diagonal, 0, 0);
        planeSource.SetPoint2(0, diagonal, 0);
        planeSource.SetCenter(getCenter());
        planeSource.SetNormal(getNormal());
        planeSource.Update();

        return planeSource.GetOutput();
    }
}
