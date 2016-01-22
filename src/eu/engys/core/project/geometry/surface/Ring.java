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

import vtk.vtkAppendPolyData;
import vtk.vtkCellArray;
import vtk.vtkIdList;
import vtk.vtkLineSource;
import vtk.vtkPolyData;
import vtk.vtkPolyDataNormals;
import vtk.vtkTubeFilter;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;

public class Ring extends BaseSurface {

	/**
	 * @deprecated  Use GeometryFactory!!
	 */
	@Deprecated
	public Ring(String name) {
		super(name);
		Dictionary geometryDictionary = new Dictionary(ring);
		geometryDictionary.setName(name);
		setGeometryDictionary(geometryDictionary);
	}

	@Override
	public Type getType() {
		return Type.RING;
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

	public double getInnerRadius() {
		if (getGeometryDictionary() != null && getGeometryDictionary().found(INNER_RADIUS_KEY))
			return Double.valueOf(getGeometryDictionary().lookup(INNER_RADIUS_KEY));
		else
			return 0;
	}

	public double getOuterRadius() {
		if (getGeometryDictionary() != null && getGeometryDictionary().found(OUTER_RADIUS_KEY))
			return Double.valueOf(getGeometryDictionary().lookup(OUTER_RADIUS_KEY));
		else
			return 0;
	}
	
	@Override
	public Surface cloneSurface() {
	    Surface box = new Ring(name);
	    cloneSurface(box);
	    return box;
	}
	
	@Override
	public vtkPolyData getDataSet() {

        vtkLineSource lineSource = new vtkLineSource();
        lineSource.SetPoint1(getPoint1());
        lineSource.SetPoint2(getPoint2());

        vtkTubeFilter internalTubeFilter = new vtkTubeFilter();
        internalTubeFilter.SetInputConnection(lineSource.GetOutputPort());
        internalTubeFilter.SetCapping(0);
        internalTubeFilter.SetRadius(getInnerRadius());
        internalTubeFilter.SetNumberOfSides(50);
        internalTubeFilter.Update();

        vtkTubeFilter externalTubeFilter = new vtkTubeFilter();
        externalTubeFilter.SetInputConnection(lineSource.GetOutputPort());
        externalTubeFilter.SetCapping(0);
        externalTubeFilter.SetRadius(getOuterRadius());
        externalTubeFilter.SetNumberOfSides(50);
        externalTubeFilter.Update();

        vtkAppendPolyData append = new vtkAppendPolyData();
        append.AddInputConnection(internalTubeFilter.GetOutputPort());
        append.AddInputConnection(externalTubeFilter.GetOutputPort());
        append.Update();

        vtkPolyData outputMesh = new vtkPolyData();
        outputMesh.DeepCopy(append.GetOutput());
        vtkCellArray outputTriangles = outputMesh.GetPolys();

        int length = internalTubeFilter.GetOutput().GetNumberOfPoints();
        for (int ptId = 0; ptId < 50; ptId++) {
            // Triangle one extremity
            vtkIdList triangle = new vtkIdList();
            triangle.InsertNextId(ptId);
            triangle.InsertNextId(ptId + length);
            triangle.InsertNextId((ptId + 1) % 50 + length);
            outputTriangles.InsertNextCell(triangle);

            triangle = new vtkIdList();
            triangle.InsertNextId(ptId);
            triangle.InsertNextId((ptId + 1) % 50 + length);
            triangle.InsertNextId((ptId + 1) % 50);
            outputTriangles.InsertNextCell(triangle);

            // Triangle the other extremity
            int offset = length - 50;
            triangle = new vtkIdList();
            triangle.InsertNextId(ptId + offset);
            triangle.InsertNextId(ptId + +offset + length);
            triangle.InsertNextId((ptId + 1) % 50 + offset + length);
            outputTriangles.InsertNextCell(triangle);

            triangle = new vtkIdList();
            triangle.InsertNextId((ptId + 1) % 50 + length + offset);
            triangle.InsertNextId((ptId + 1) % 50 + offset);
            triangle.InsertNextId(ptId + offset);
            outputTriangles.InsertNextCell(triangle);
        }

        vtkPolyDataNormals normals = new vtkPolyDataNormals();
        normals.SetInputData(outputMesh);
        normals.Update();

	    return normals.GetOutput();
	}
}
