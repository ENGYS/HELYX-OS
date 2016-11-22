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
package eu.engys.core.project.geometry;

import static eu.engys.core.project.system.SnappyHexMeshDict.FILE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.LEVELS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.REFINE_FEATURE_EDGES_ONLY_KEY;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.surface.BaseSurface;
import vtk.vtkPolyData;

public class FeatureLine extends BaseSurface {

    public static class Refinement {
        
        private double distance;
        private int level;

        public Refinement(double distance, int level) {
            this.distance = distance;
            this.level = level;
        }
        public double getDistance() {
            return distance;
        }
        public int getLevel() {
            return level;
        }
    }
    
    private boolean refineOnly;
    private List<Refinement> refinements;
    private vtkPolyData dataSet;
    private Color color;
    private boolean modified;

    public FeatureLine(String name) {
        super(name);
        this.refineOnly = false;
        this.refinements = new ArrayList<>();
        this.refinements.add(new Refinement(0.0, 0));
        this.color = Color.BLUE;
    }
    
    public boolean isRefineOnly() {
        return refineOnly;
    }
    public void setRefineOnly(boolean refineOnly) {
        this.refineOnly = refineOnly;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<Refinement> getRefinements() {
        return refinements;
    }
    public void setRefinements(List<Refinement> refinements) {
        this.refinements = refinements;
    }
    
    @Override
    public Type getType() {
        return Type.LINE;
    }
    @Override
    public Surface cloneSurface() {
        return new FeatureLine(getName());
    }
    @Override
    public vtkPolyData getDataSet() {
        return dataSet;
    }
    public void setDataSet(vtkPolyData dataSet) {
        this.dataSet = dataSet;
    }
    @Override
    public boolean isAppendRegionName() {
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
    public boolean hasLayers() {
        return false;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
    
    @Override
    public Dictionary toDictionary() {
        Dictionary d = new Dictionary("");
        d.add(FILE_KEY, "\"" + getName() + ".eMesh" + "\"");
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Refinement r : refinements) {
            sb.append("(");
            sb.append(r.distance);
            sb.append(" ");
            sb.append(r.level);
            sb.append(")");
        }
        sb.append(")");
        d.add(LEVELS_KEY, sb.toString());
        d.add(REFINE_FEATURE_EDGES_ONLY_KEY, String.valueOf(refineOnly));
        return d;
    }
    
    @Override
    public void fromDictionary(Dictionary d) {
        refinements.clear();
        if (d.found(LEVELS_KEY)) {
            double[][] levels = d.lookupDoubleMatrix(LEVELS_KEY);
            for (int i = 0; i < levels.length; i++) {
                if (levels[i].length == 2) {
                    double distance = levels[i][0];
                    int level = (int) levels[i][1];
                    refinements.add(new Refinement(distance, level));
                }
            }
        }
        if (d.found(REFINE_FEATURE_EDGES_ONLY_KEY) ) {
            this.refineOnly = Boolean.parseBoolean(d.lookup(REFINE_FEATURE_EDGES_ONLY_KEY));
        }
    }
    
    @Override
    public Dictionary toGeometryDictionary() {
        return null;
    }
    
    @Override
    public void fromGeometryDictionary(Dictionary g) {
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
    
    public boolean isModified() {
        return modified;
    }
}
