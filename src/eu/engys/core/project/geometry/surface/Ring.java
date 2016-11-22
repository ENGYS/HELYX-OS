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

import static eu.engys.core.dictionary.Dictionary.TYPE;

import org.apache.commons.lang.ArrayUtils;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;
import vtk.vtkAppendPolyData;
import vtk.vtkCellArray;
import vtk.vtkIdList;
import vtk.vtkLineSource;
import vtk.vtkPolyData;
import vtk.vtkPolyDataNormals;
import vtk.vtkTubeFilter;

public class Ring extends BaseSurface {

    public static final double[] DEFAULT_P1 = {0,0,0};
    public static final double[] DEFAULT_P2 = {1,0,0};
    public static final double DEFAULT_RADIUS1 = 1.0;
    public static final double DEFAULT_RADIUS2 = 2.0;
    
    private double[] point1 = DEFAULT_P1;
    private double[] point2 = DEFAULT_P2;
    private double innerRadius = DEFAULT_RADIUS1;
    private double outerRadius = DEFAULT_RADIUS2;
    
    public static final Dictionary ring = new Dictionary("ring") {
        {
            add(TYPE, SEARCHABLE_RING_KEY);
            add(POINT1_KEY, "(0 0 0)");
            add(POINT2_KEY, "(0.05 0 0)");
            add(INNER_RADIUS_KEY, "0.2");
            add(OUTER_RADIUS_KEY, "0.5");
        }
    };

    /**
	 * @deprecated  Use GeometryFactory!!
	 */
	@Deprecated
	public Ring(String name) {
		super(name);
	}

	@Override
	public Type getType() {
		return Type.RING;
	}

	public double[] getPoint1() {
	    return point1;
	}
	public void setPoint1(double[] point1) {
	    firePropertyChange(POINT1_KEY, this.point1, this.point1 = point1);
    }
    public void setPoint1(double d1, double d2, double d3) {
        setPoint1(new double[]{d1,d2,d3});
    }

	public double[] getPoint2() {
	    return point2;
	}
	public void setPoint2(double[] point2) {
	    firePropertyChange(POINT2_KEY, this.point2, this.point2 = point2);
    }
    public void setPoint2(double d1, double d2, double d3) {
        setPoint2(new double[]{d1,d2,d3});
    }

	public double getInnerRadius() {
	    return innerRadius;
	}
	public void setInnerRadius(double innerRadius) {
        firePropertyChange(INNER_RADIUS_KEY, this.innerRadius, this.innerRadius = innerRadius);
    }
	
	public double getOuterRadius() {
	    return outerRadius;
	}
	public void setOuterRadius(double outerRadius) {
	    firePropertyChange(OUTER_RADIUS_KEY, this.outerRadius, this.outerRadius = outerRadius);
    }
	
	@Override
	public Surface cloneSurface() {
	    Ring ring = new Ring(name);
        ring.point1 = ArrayUtils.clone(point1);
        ring.point2 = ArrayUtils.clone(point2);
        ring.innerRadius = innerRadius;
        ring.outerRadius = outerRadius;
	    cloneSurface(ring);
	    return ring;
	}
    
    @Override
    public void copyFrom(Surface delegate, boolean changeGeometry, boolean changeSurface, boolean changeVolume, boolean changeLayer, boolean changeZone) {
        if (delegate instanceof Ring) {
            Ring ring = (Ring) delegate;
            if (changeGeometry) {
                setPoint1(ring.getPoint1());
                setPoint2(ring.getPoint2());
                setInnerRadius(ring.getInnerRadius());
                setOuterRadius(ring.getOuterRadius());
            }
            super.copyFrom(delegate, changeGeometry, changeSurface, changeVolume, changeLayer, changeZone);
        }
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
    
    @Override
    public Dictionary toGeometryDictionary() {
        Dictionary d = new Dictionary(name, ring);
        d.add(POINT1_KEY, point1);
        d.add(POINT2_KEY, point2);
        d.add(INNER_RADIUS_KEY, innerRadius);
        d.add(OUTER_RADIUS_KEY, outerRadius);
        return d;
    }
    
    @Override
    public void fromGeometryDictionary(Dictionary g) {
        if (g != null && g.found(TYPE) && g.lookup(TYPE).equals(SEARCHABLE_RING_KEY) ) {
            if (g.found(POINT1_KEY))
                setPoint1(g.lookupDoubleArray(POINT1_KEY));
            else
                setPoint1(DEFAULT_P1);        

            if (g.found(POINT2_KEY))
                setPoint2(g.lookupDoubleArray(POINT2_KEY));
            else
                setPoint2(DEFAULT_P2);        
            
            if (g.found(INNER_RADIUS_KEY))
                setInnerRadius(g.lookupDouble(INNER_RADIUS_KEY));
            else
                setInnerRadius(DEFAULT_RADIUS1);         
            
            if (g.found(OUTER_RADIUS_KEY))
                setOuterRadius(g.lookupDouble(OUTER_RADIUS_KEY));
            else
                setOuterRadius(DEFAULT_RADIUS1);        
        }
    }
}
