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
import vtk.vtkPlaneSource;
import vtk.vtkPolyData;

public class Plane extends BaseSurface {

    public static final double DEFAULT_DIAGONAL = 1;
    public static final double[] DEFAULT_CENTER = {0,0,0};
    public static final double[] DEFAULT_NORMAL = {0,0,1};
    
    private double[] basePoint = DEFAULT_CENTER;
    private double[] normalVector = DEFAULT_NORMAL;
    private double diagonal = DEFAULT_DIAGONAL;
    
    public static final Dictionary planeWithCenter = new Dictionary("plane") {
        {
            add(TYPE, SEARCHABLE_PLANE_KEY);
            add(PLANE_TYPE_KEY, POINT_AND_NORMAL_KEY);
            Dictionary dict = new Dictionary(POINT_AND_NORMAL_DICT_KEY);
            dict.add(BASE_POINT_KEY, "(0 0 0)");
            dict.add(NORMAL_VECTOR_KEY, "(0 0 1)");
            add(dict);
        }
    };

	/**
	 * @deprecated Use GeometryFactory!!
	 */
	@Deprecated
	public Plane(String name) {
		super(name);
	}

	@Override
	public Type getType() {
		return Type.PLANE;
	}

	public double[] getBasePoint() {
	    return basePoint;
	}
    public void setBasePoint(double[] basePoint) {
        firePropertyChange(BASE_POINT_KEY, this.basePoint, this.basePoint = basePoint);
    }

    public double[] getNormalVector() {
        return normalVector;
    }
    public void setNormalVector(double[] normalVector) {
        firePropertyChange(NORMAL_VECTOR_KEY, this.normalVector, this.normalVector = normalVector);
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
        plane.basePoint = ArrayUtils.clone(basePoint);
        plane.normalVector = ArrayUtils.clone(normalVector);
        return plane;
    }
    
    @Override
    public void copyFrom(Surface delegate, boolean changeGeometry, boolean changeSurface, boolean changeVolume, boolean changeLayer, boolean changeZone) {
        if (delegate instanceof Plane) {
            Plane plane = (Plane) delegate;
            if (changeGeometry) {
                setBasePoint(plane.getBasePoint());
                setNormalVector(plane.getNormalVector());
            }
            super.copyFrom(delegate, changeGeometry, changeSurface, changeVolume, changeLayer, changeZone);
        }
    }
    
    @Override
    public vtkPolyData getDataSet() {
        vtkPlaneSource planeSource = new vtkPlaneSource();
        planeSource.SetOrigin(0, 0, 0);
        planeSource.SetPoint1(diagonal, 0, 0);
        planeSource.SetPoint2(0, diagonal, 0);
        planeSource.SetCenter(getBasePoint());
        planeSource.SetNormal(getNormalVector());
        planeSource.Update();

        return planeSource.GetOutput();
    }
    
    @Override
    public Dictionary toGeometryDictionary() {
        Dictionary d = new Dictionary(name, planeWithCenter);
        d.subDict(POINT_AND_NORMAL_DICT_KEY).add(BASE_POINT_KEY, basePoint);
        d.subDict(POINT_AND_NORMAL_DICT_KEY).add(NORMAL_VECTOR_KEY, normalVector);
        return d;
    }
    
    @Override
    public void fromGeometryDictionary(Dictionary g) {
        if (g != null && g.found(TYPE) && g.lookup(TYPE).equals(SEARCHABLE_PLANE_KEY) ) {
            if (g.isDictionary(POINT_AND_NORMAL_DICT_KEY)) {
                Dictionary subDict = g.subDict(POINT_AND_NORMAL_DICT_KEY);
                if (subDict.found(BASE_POINT_KEY))
                    setBasePoint(subDict.lookupDoubleArray(BASE_POINT_KEY));
                else
                    setBasePoint(DEFAULT_CENTER);        
                
                if (subDict.found(NORMAL_VECTOR_KEY))
                    setNormalVector(subDict.lookupDoubleArray(NORMAL_VECTOR_KEY));
                else
                    setNormalVector(DEFAULT_NORMAL);        
            }
        }
    }
}
