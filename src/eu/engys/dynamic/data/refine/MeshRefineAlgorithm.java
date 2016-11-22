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
package eu.engys.dynamic.data.refine;

import eu.engys.dynamic.data.DynamicAlgorithm;
import eu.engys.dynamic.data.DynamicAlgorithmType;

public class MeshRefineAlgorithm extends DynamicAlgorithm {
    
    public static final String REFINE_INTERVAL = "refineInterval";
    public static final String FIELD = "field";
    public static final String LOWER_REFINE_LEVEL = "lowerRefineLevel";
    public static final String UPPER_REFINE_LEVEL = "upperRefineLevel";
    public static final String UNREFINE_LEVEL = "unrefineLevel";
    public static final String N_BUFFER_LAYERS = "nBufferLayers";
    public static final String MAX_REFINEMENT = "maxRefinement";
    public static final String MAX_CELLS = "maxCells";
    
    private int refineInterval = 1;
    private String field = "";
    private double lowerRefineLevel = 0.001;
    private double upperRefineLevel = 0.999;
    private int unrefineLevel = 10;
    private int nBufferLayers = 1;
    private int maxRefinement = 2;
    private int maxCells = 200_000;

    @Override
    public DynamicAlgorithmType getType() {
        return DynamicAlgorithmType.MESH_REFINE;
    }
    
    @Override
    public DynamicAlgorithm copy() {
        MeshRefineAlgorithm copy = new MeshRefineAlgorithm();
        copy.refineInterval = this.refineInterval;
        copy.field = this.field;
        copy.lowerRefineLevel = this.lowerRefineLevel;
        copy.upperRefineLevel = this.upperRefineLevel;
        copy.unrefineLevel = this.unrefineLevel;
        copy.nBufferLayers = this.nBufferLayers;
        copy.maxRefinement = this.maxRefinement;
        copy.maxCells = this.maxCells;
        return copy;
    }

    public int getRefineInterval() {
        return refineInterval;
    }

    public void setRefineInterval(int refineInterval) {
        firePropertyChange(REFINE_INTERVAL, this.refineInterval, this.refineInterval = refineInterval);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        firePropertyChange(FIELD, this.field, this.field = field);
    }

    public double getLowerRefineLevel() {
        return lowerRefineLevel;
    }

    public void setLowerRefineLevel(double lowerRefineLevel) {
        firePropertyChange(LOWER_REFINE_LEVEL, this.lowerRefineLevel, this.lowerRefineLevel = lowerRefineLevel);
    }

    public double getUpperRefineLevel() {
        return upperRefineLevel;
    }

    public void setUpperRefineLevel(double upperRefineLevel) {
        firePropertyChange(UPPER_REFINE_LEVEL, this.upperRefineLevel, this.upperRefineLevel = upperRefineLevel);
    }

    public int getUnrefineLevel() {
        return unrefineLevel;
    }

    public void setUnrefineLevel(int unrefineLevel) {
        firePropertyChange(UNREFINE_LEVEL, this.unrefineLevel, this.unrefineLevel = unrefineLevel);
    }

    public int getnBufferLayers() {
        return nBufferLayers;
    }

    public void setnBufferLayers(int nBufferLayers) {
        firePropertyChange(N_BUFFER_LAYERS, this.nBufferLayers, this.nBufferLayers = nBufferLayers);
    }

    public int getMaxRefinement() {
        return maxRefinement;
    }

    public void setMaxRefinement(int maxRefinement) {
        firePropertyChange(MAX_REFINEMENT, this.maxRefinement, this.maxRefinement = maxRefinement);
    }

    public int getMaxCells() {
        return maxCells;
    }

    public void setMaxCells(int maxCells) {
        firePropertyChange(MAX_CELLS, this.maxCells, this.maxCells = maxCells);
    }

}
