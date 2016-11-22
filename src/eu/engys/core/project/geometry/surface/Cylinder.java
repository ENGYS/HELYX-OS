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

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;
import vtk.vtkLineSource;
import vtk.vtkPolyData;
import vtk.vtkTubeFilter;

public class Cylinder extends BaseSurface {

	public static final double[] DEFAULT_P1 = {0,0,0};
	public static final double[] DEFAULT_P2 = {1,0,0};
	public static final double DEFAULT_RADIUS = 1.0;
	
    private double[] point1 = DEFAULT_P1;
    private double[] point2 = DEFAULT_P2;
    private double radius = DEFAULT_RADIUS;
    
    public static final Dictionary cylinder = new Dictionary("cylinder") {
        {
            add(TYPE, Surface.SEARCHABLE_CYLINDER_KEY);
            add(Surface.POINT1_KEY, "(0 0 0)");
            add(Surface.POINT2_KEY, "(1 0 0)");
            add(Surface.RADIUS_KEY, "1.0");
        }
    };

    /**
	 * @deprecated  Use GeometryFactory!!
	 */
	@Deprecated
	public Cylinder(String name) {
		super(name);
	}

	@Override
	public Type getType() {
		return Type.CYLINDER;
	}

	public double[] getPoint1() {
	    return point1;
	}
	public void setPoint1(double[] point1) {
	    firePropertyChange("point1", this.point1, this.point1 = point1);
    }
    public void setPoint1(double d1, double d2, double d3) {
        setPoint1(new double[]{d1,d2,d3});
    }
	
	public double[] getPoint2() {
	    return point2;
	}
	public void setPoint2(double[] point2) {
	    firePropertyChange("point2", this.point2, this.point2 = point2);
    }
    public void setPoint2(double d1, double d2, double d3) {
        setPoint2(new double[]{d1,d2,d3});
    }
	
	public double getRadius() {
	    return radius;
	}
	public void setRadius(double radius) {
	    firePropertyChange("radius", this.radius, this.radius = radius);
    }
    
    @Override
    public Surface cloneSurface() {
        Cylinder cyl = new Cylinder(name);
        cyl.point1 = ArrayUtils.clone(point1);
        cyl.point2 = ArrayUtils.clone(point2);
        cyl.radius = radius;
        cloneSurface(cyl);
        return cyl;
    }
    
    @Override
    public void copyFrom(Surface delegate, boolean changeGeometry, boolean changeSurface, boolean changeVolume, boolean changeLayer, boolean changeZone) {
        if (delegate instanceof Cylinder) {
            Cylinder cyl = (Cylinder) delegate;
            if (changeGeometry) {
                setPoint1(cyl.getPoint1());
                setPoint2(cyl.getPoint2());
                setRadius(cyl.getRadius());
            }
            super.copyFrom(delegate, changeGeometry, changeSurface, changeVolume, changeLayer, changeZone);
        }
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
    
    @Override
    public Dictionary toGeometryDictionary() {
        Dictionary d = new Dictionary(name, cylinder);
        d.add(POINT1_KEY, point1);
        d.add(POINT2_KEY, point2);
        d.add(RADIUS_KEY, radius);
        return d;
    }
    
    @Override
    public void fromGeometryDictionary(Dictionary g) {
        if (g != null && g.found(TYPE) && g.lookup(TYPE).equals(SEARCHABLE_CYLINDER_KEY) ) {
            if (g.found(POINT1_KEY))
                setPoint1(g.lookupDoubleArray(POINT1_KEY));
            else
                setPoint1(DEFAULT_P1);        

            if (g.found(POINT2_KEY))
                setPoint2(g.lookupDoubleArray(POINT2_KEY));
            else
                setPoint2(DEFAULT_P2);        
            
            if (g.found(RADIUS_KEY))
                setRadius(g.lookupDouble(RADIUS_KEY));
            else
                setRadius(DEFAULT_RADIUS);        
        }
    }
    
    @Override
    public String toString() {
        String string = super.toString();
        
        return string + String.format("[ p1: %s, p2: %s, radius: %s] ", Arrays.toString(point1), Arrays.toString(point2), radius);
    }
    
}
