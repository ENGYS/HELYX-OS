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
import vtk.vtkPolyData;
import vtk.vtkSphereSource;

public class Sphere extends BaseSurface {

	public static final double[] DEFAULT_CENTER = {0,0,0};
    public static final double DEFAULT_RADIUS = 1;
	
    private double[] centre = DEFAULT_CENTER;
    private double radius = DEFAULT_RADIUS;
    public static final Dictionary sphere = new Dictionary("sphere") {
        {
            add(TYPE, Surface.SEARCHABLE_SPHERE_KEY);
            add(Surface.CENTRE_KEY, "(0 0 0)");
            add(Surface.RADIUS_KEY, "1.0");
        }
    };
    
    /**
	 * @deprecated  Use GeometryFactory!!
	 */
	@Deprecated
	public Sphere(String name) {
		super(name);
	}

	@Override
	public Type getType() {
		return Type.SPHERE;
	}

	public double[] getCentre() {
	    return centre;
	}
	public void setCentre(double[] centre) {
	    firePropertyChange(CENTRE_KEY, this.centre, this.centre = centre);
    }
	public void setCentre(double d1, double d2, double d3) {
	    setCentre(new double[]{d1,d2,d3});
	}

	public double getRadius() {
	    return radius;
	}
	public void setRadius(double radius) {
	    firePropertyChange(RADIUS_KEY, this.radius, this.radius = radius);
	}
    
    @Override
    public Surface cloneSurface() {
        Sphere sphere = new Sphere(name);
        sphere.centre = ArrayUtils.clone(centre);
        sphere.radius = radius;
        cloneSurface(sphere);
        return sphere;
    }
    
    @Override
    public void copyFrom(Surface delegate, boolean changeGeometry, boolean changeSurface, boolean changeVolume, boolean changeLayer, boolean changeZone) {
        if (delegate instanceof Sphere) {
            Sphere sphere = (Sphere) delegate;
            if (changeGeometry) {
                setCentre(sphere.getCentre());
                setRadius(sphere.getRadius());
            }
            super.copyFrom(delegate, changeGeometry, changeSurface, changeVolume, changeLayer, changeZone);
        }
    }
    
    @Override
    public vtkPolyData getDataSet() {
        double[] center = getCentre();
        double   radius = getRadius();

        vtkSphereSource sphereSource = new vtkSphereSource();
        sphereSource.SetCenter(center);
        sphereSource.SetRadius(radius);
        sphereSource.SetPhiResolution(20);
        sphereSource.SetThetaResolution(20);
        sphereSource.Update();
        
        return sphereSource.GetOutput();
    }
    
    @Override
    public Dictionary toGeometryDictionary() {
        Dictionary d = new Dictionary(name, sphere);
        d.add(CENTRE_KEY, centre);
        d.add(RADIUS_KEY, radius);
        return d;
    }
    
    @Override
    public void fromGeometryDictionary(Dictionary g) {
        if (g != null && g.found(TYPE) && g.lookup(TYPE).equals(SEARCHABLE_SPHERE_KEY) ) {
            if (g.found(CENTRE_KEY))
                setCentre(g.lookupDoubleArray(CENTRE_KEY));
            else
                setCentre(DEFAULT_CENTER);        

            if (g.found(RADIUS_KEY))
                setRadius(g.lookupDouble(RADIUS_KEY));
            else
                setRadius(DEFAULT_RADIUS);        
        }
    }
    
    @Override
    public String toString() {
        String string = super.toString();
        
        return string + String.format("[ centre: %s, radius: %s] ", Arrays.toString(centre), radius);
    }
}
