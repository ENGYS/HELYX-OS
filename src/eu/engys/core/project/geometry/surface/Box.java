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
import static eu.engys.util.Util.round;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;
import vtk.vtkCubeSource;
import vtk.vtkPolyData;
import vtk.vtkTransform;
import vtk.vtkTransformFilter;

public class Box extends BaseSurface {

    public static final String MIN_KEY = "min";
    public static final String MAX_KEY = "max";
    public static final String CENTER_KEY = "center";
    public static final String DELTA_KEY = "delta";
    public static final String ROTATION_KEY = "rotation";
    
    public static final double[] DEFAULT_MIN = { 0, 0, 0 };
    public static final double[] DEFAULT_MAX = { 2, 2, 2 };
    public static final double[] DEFAULT_CENTER = { 1, 1, 1 };
    public static final double[] DEFAULT_DELTA = { 2, 2, 2 };
    public static final double[] DEFAULT_ROTATION = { 0, 0, 0 };
    
    private double[] min = DEFAULT_MIN;
    private double[] max = DEFAULT_MAX;
    private double[] center = DEFAULT_CENTER;
    private double[] delta = DEFAULT_DELTA;
    private double[] rotation = DEFAULT_ROTATION;
    
    public static final Dictionary box = new Dictionary("box") {
        {
            add(TYPE, Surface.SEARCHABLE_BOX_KEY);
            add(Surface.MIN_KEY, new double[]{0, 0, 0});
            add(Surface.MAX_KEY, new double[]{2, 2, 1});
        }
    };
    
    
	/**
	 * @deprecated Use GeometryFactory!!
	 */
	@Deprecated
	public Box(String name) {
		super(name);
	}

	@Override
	public Type getType() {
		return Type.BOX;
	}

	public double[] getMin() {
	    return min;
	}
	public void setMin(double[] min) {
	    firePropertyChange(MIN_KEY, this.min, this.min = min);
	    recalculateCenterAndDelta();
    }
	public void setMin(double d1, double d2, double d3) {
	    setMin(new double[]{d1,d2,d3});
	}
	
	public double[] getMax() {
	    return max;
	}
	public void setMax(double[] max) {
	    firePropertyChange(MAX_KEY, this.max, this.max = max);
	    recalculateCenterAndDelta();
	}
	public void setMax(double d1, double d2, double d3) {
	    setMax(new double[]{d1,d2,d3});
	}

	public double[] getCenter() {
        return center;
    }
    public void setCenter(double[] center) {
        firePropertyChange(CENTER_KEY, this.center, this.center = center);
        recalculateMinMax();
    }
    public void setCenter(double d1, double d2, double d3) {
        setCenter(new double[]{d1,d2,d3});
    }

    public double[] getDelta() {
        return delta;
    }
    public void setDelta(double[] delta) {
        firePropertyChange(DELTA_KEY, this.delta, this.delta = delta);
        recalculateMinMax();
    }
    public void setDelta(double d1, double d2, double d3) {
        setDelta(new double[]{d1,d2,d3});
    }

    public double[] getRotation() {
        return rotation;
    }
    public void setRotation(double[] rotation) {
        firePropertyChange(ROTATION_KEY, this.rotation, this.rotation = rotation);
    }
    public void setRotation(double d1, double d2, double d3) {
        setRotation(new double[]{d1,d2,d3});
    }
    
    private void recalculateCenterAndDelta() {
        double minX = min[0];
        double minY = min[1];
        double minZ = min[2];
        
        double maxX = max[0];
        double maxY = max[1];
        double maxZ = max[2];
        
        this.center = new double[] { (minX + maxX)/2, (minY + maxY)/2, (minZ + maxZ)/2 };
        this.delta = new double[] {  maxX - minX, maxY - minY, maxZ - minZ };
        
        round(center);
        round(delta);
    }
    
    private void recalculateMinMax() {
        double oX = center[0];
        double oY = center[1];
        double oZ = center[2];
        
        double dX = delta[0];
        double dY = delta[1];
        double dZ = delta[2];

        this.min = new double[] { oX - dX/2, oY - dY/2, oZ - dZ/2 };
        this.max = new double[] { oX + dX/2, oY + dY/2, oZ + dZ/2 };
        round(min);
        round(max);
    }
    
    @Override
	public Surface cloneSurface() {
	    Box box = new Box(name);
        box.min = ArrayUtils.clone(min);
        box.max = ArrayUtils.clone(max);
	    cloneSurface(box);
	    return box;
	}
	
	@Override
	public void copyFrom(Surface delegate, boolean changeGeometry, boolean changeSurface, boolean changeVolume, boolean changeLayer, boolean changeZone) {
	    if (delegate instanceof Box) {
	        Box box = (Box) delegate;
	        if (changeGeometry) {
	            setMin(box.getMin());
	            setMax(box.getMax());
	        }
	        super.copyFrom(delegate, changeGeometry, changeSurface, changeVolume, changeLayer, changeZone);
	    }
	}
	
	@Override
	public vtkPolyData getDataSet() {
	    if (hasRotation()) {
	        double oX = center[0];
	        double oY = center[1];
	        double oZ = center[2];
	        
	        double deltaX = delta[0];
	        double deltaY = delta[1];
	        double deltaZ = delta[2];
	        
	        double rotX = rotation[0];
	        double rotY = rotation[1];
	        double rotZ = rotation[2];
	        
	        vtkCubeSource cubeSource = new vtkCubeSource();
	        cubeSource.SetCenter(oX, oY, oZ);
	        cubeSource.SetXLength(deltaX);
	        cubeSource.SetYLength(deltaY);
	        cubeSource.SetZLength(deltaZ);
	        cubeSource.Update();
	        
            vtkTransform t = new vtkTransform();
            t.PostMultiply();
            t.Translate(-oX, -oY, -oZ);
            t.RotateY(rotY);
            t.RotateX(rotX);
            t.RotateZ(rotZ);
            t.Translate(oX, oY, oZ);
            
            vtkTransformFilter filter = new vtkTransformFilter();
            filter.SetInputData(cubeSource.GetOutput());
            filter.SetTransform(t);
            filter.Update();
	        
            return (vtkPolyData) filter.GetOutput();
	    } else {
	        double[] min = getMin();
	        double[] max = getMax();
	        
	        vtkCubeSource cubeSource = new vtkCubeSource();
	        cubeSource.SetBounds(min[0], max[0], min[1], max[1], min[2], max[2]);
	        cubeSource.Update();
	        return cubeSource.GetOutput();
	    }
	}

    private boolean hasRotation() {
        return rotation != null && (rotation[0] != 0 || rotation[1] != 0 || rotation[2] != 0);
    }
	
	@Override
	public Dictionary toGeometryDictionary() {
	    Dictionary d = new Dictionary(name, box);
	    d.add(Surface.MIN_KEY, min);
	    d.add(Surface.MAX_KEY, max);
	    return d;
	}
	
	@Override
	public void fromGeometryDictionary(Dictionary g) {
	    if (g != null && g.found(TYPE) && g.lookup(TYPE).equals(SEARCHABLE_BOX_KEY) ) {
	        if (g.found(MIN_KEY))
	            setMin(g.lookupDoubleArray(MIN_KEY));
	        else
	            setMin(DEFAULT_MIN);	    
	        
	        if (g.found(MAX_KEY))
	            setMax(g.lookupDoubleArray(MAX_KEY));
	        else
	            setMax(DEFAULT_MAX);	    
	    }
	}
	
	@Override
	public String toString() {
	    String string = super.toString();
	    
        return string + String.format("[ min: %s, max: %s] ", Arrays.toString(min), Arrays.toString(max));
	}
}
