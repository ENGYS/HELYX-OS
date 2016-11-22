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

public class StlArea extends BaseSurface {

    public static final String CURVATURE = "curvature";
    public static final String NEAR_DISTANCE = "nearDistance";
    public static final String INCLUDE_OUTSIDE = "includeOutside";
    public static final String INCLUDE_INSIDE = "includeInside";
    public static final String INCLUDE_CUT = "includeCut";
    public static final String FILE = "file";
    public static final String OUTSIDE_POINT = "outsidePoint";

    private String file;
    private boolean includeCut = true;
    private boolean includeInside = true;
    private boolean includeOutside = false;
    private double[] outsidePoint = { 0, 0, 0 };

    private double nearDistance = -1;
    private double curvature = -100;
    private vtkPolyData dataSet;

    /**
     * @deprecated Use GeometryFactory!!
     */
    @Deprecated
    public StlArea(String name) {
        super(name);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        firePropertyChange(FILE, this.file, this.file = file);
    }

    public double[] getOutsidePoint() {
        return outsidePoint;
    }

    public void setOutsidePoint(double[] outsidePoint) {
        firePropertyChange(OUTSIDE_POINT, this.outsidePoint, this.outsidePoint = outsidePoint);
    }

    public boolean isIncludeCut() {
        return includeCut;
    }

    public void setIncludeCut(boolean includeCut) {
        firePropertyChange(INCLUDE_CUT, this.includeCut, this.includeCut = includeCut);
    }

    public boolean isIncludeInside() {
        return includeInside;
    }

    public void setIncludeInside(boolean includeInside) {
        firePropertyChange(INCLUDE_INSIDE, this.includeInside, this.includeInside = includeInside);
    }

    public boolean isIncludeOutside() {
        return includeOutside;
    }

    public void setIncludeOutside(boolean includeOutside) {
        firePropertyChange(INCLUDE_OUTSIDE, this.includeOutside, this.includeOutside = includeOutside);
    }

    public double getNearDistance() {
        return nearDistance;
    }

    public void setNearDistance(double nearDistance) {
        firePropertyChange(NEAR_DISTANCE, this.nearDistance, this.nearDistance = nearDistance);
    }

    public double getCurvature() {
        return curvature;
    }

    public void setCurvature(double curvature) {
        firePropertyChange(CURVATURE, this.curvature, this.curvature = curvature);
    }

    @Override
    public Type getType() {
        return Type.STL_AREA;
    }

    @Override
    public void rename(String newName) {
        super.rename(newName);
        // if (getTransformMode() == TransfromMode.TO_FILE) {
        // if (fileName.endsWith(".stl")) {
        // this.fileName = newName + ".stl";
        // } else if (fileName.endsWith(".STL")) {
        // this.fileName = newName + ".STL";
        // }
        // setModified(true);
        // }
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

    @Override
    public String getPatchName() {
        return name;
    }

    @Override
    public Surface cloneSurface() {
        StlArea s = new StlArea(name);
        s.file = this.file;
        cloneSurface(s);
        return s;
    }

    public void setDataSet(vtkPolyData dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public vtkPolyData getDataSet() {
        return dataSet;
    }

    @Override
    public Dictionary toGeometryDictionary() {
        return new Dictionary(file);
    }

    @Override
    public void fromGeometryDictionary(Dictionary g) {
    }

}
