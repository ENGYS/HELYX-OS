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
import vtk.vtkPolyData;

public class MultiPlane extends MultiRegion {

    public static final double[] DEFAULT_MIN = {0,0,0};
    public static final double[] DEFAULT_MAX = {1,1,1};
    public static final int[] DEFAULT_ELEMENTS = {10,10,10};
    
    private double[] min = DEFAULT_MIN;
    private double[] max = DEFAULT_MAX;
    private int[] elements = DEFAULT_ELEMENTS;
    
    public MultiPlane(String name) {
        super(name);
    }

    public double[] getMin() {
        return min;
    }
    public void setMin(double[] min) {
        firePropertyChange(MIN_KEY, this.min, this.min = min);
    }
    
    public double[] getMax() {
        return max;
    }
    public void setMax(double[] max) {
        firePropertyChange(MAX_KEY, this.max, this.max = max);
    }
    
    public int[] getElements() {
        return elements;
    }
    public void setElements(int[] elements) {
        firePropertyChange(ELEMENTS_KEY, this.elements, this.elements = elements);
    }
    
    public void addPlane(String name) {
        if (!regionsMap.containsKey(name)) {
            PlaneRegion newPlane = new PlaneRegion(name);
            super.addRegion(newPlane);
        }
    }

    public void setPlane(String name, double[] origin, double[] point1, double[] point2, int resX, int resY) {
        if (regionsMap.containsKey(name)) {
            PlaneRegion plane = (PlaneRegion) regionsMap.get(name);
            plane.origin = origin;
            plane.point1 = point1;
            plane.point2 = point2;
            plane.resolutionX = resX;
            plane.resolutionY = resY;
        } else {
            System.err.println(String.format("Plane %s not found.", name));
        }
    }

    public PlaneRegion[] getPlanes() {
        return regions.toArray(new PlaneRegion[0]);
    }

    @Override
    public Type getType() {
        return Type.MULTI;
    }

    @Override
    public void fromGeometryDictionary(Dictionary g) {
    }
    @Override
    public Dictionary toGeometryDictionary() {
        return null;
    }

    @Override
    public boolean hasLayers() {
        return false;
    }

    @Override
    public boolean hasSurfaceRefinement() {
        return false;
    }

    @Override
    public boolean hasVolumeRefinement() {
        return false;
    }

    @Override
    public boolean hasZones() {
        return false;
    }

    public double[] getDelta() {
        double[] delta = new double[3];
        for (int i = 0; i < delta.length; i++) {
            delta[i] = (max[i] - min[i]) / elements[i];
        }
        return delta;
    }
    
    @Override
    public Surface cloneSurface() {
        Surface s = new MultiPlane(name);
        cloneSurface(s);
        return s;
    }
    
    @Override
    public vtkPolyData getDataSet() {
        return null;
    }

    public void updatePlanes() {
        if (regions.size() == 6) {
            setPlane(regions.get(0).getName(), new double[] { min[0], min[1], min[2] }, new double[] { min[0], max[1], min[2] }, new double[] { min[0], min[1], max[2] }, elements[1], elements[2]);
            setPlane(regions.get(1).getName(), new double[] { max[0], min[1], min[2] }, new double[] { max[0], max[1], min[2] }, new double[] { max[0], min[1], max[2] }, elements[1], elements[2]);

            setPlane(regions.get(2).getName(), new double[] { min[0], min[1], min[2] }, new double[] { max[0], min[1], min[2] }, new double[] { min[0], min[1], max[2] }, elements[0], elements[2]);
            setPlane(regions.get(3).getName(), new double[] { min[0], max[1], min[2] }, new double[] { max[0], max[1], min[2] }, new double[] { min[0], max[1], max[2] }, elements[0], elements[2]);

            setPlane(regions.get(4).getName(), new double[] { min[0], min[1], min[2] }, new double[] { max[0], min[1], min[2] }, new double[] { min[0], max[1], min[2] }, elements[0], elements[1]);
            setPlane(regions.get(5).getName(), new double[] { min[0], min[1], max[2] }, new double[] { max[0], min[1], max[2] }, new double[] { min[0], max[1], max[2] }, elements[0], elements[1]);
        }        
    }
}
