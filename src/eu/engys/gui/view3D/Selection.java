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
package eu.engys.gui.view3D;

import eu.engys.util.bean.AbstractBean;
import vtk.vtkIdTypeArray;
import vtk.vtkPolyData;

public class Selection extends AbstractBean {

    public static final String SELECTION_DATA = "selectionData";

    public enum SelectionType {
        CELL, AREA, FEATURE
    }

    public enum SelectionTarget {
        POINT, LINE, CELL
    }

    public enum SelectionMode {
        SELECT, DESELECT
    }

    private Selection.SelectionType type = SelectionType.FEATURE;
    private Selection.SelectionMode mode = SelectionMode.SELECT;
    private Selection.SelectionTarget target = SelectionTarget.CELL;

    private double featureAngle = 30.0;
    private boolean keepSelection = true;

    private vtkPolyData selectionData;
    private vtkPolyData inverseSelectionData;
    private vtkIdTypeArray idList;
    private vtkPolyData dataSet;

    public void setType(Selection.SelectionType type) {
        firePropertyChange("type", this.type, this.type = type);
    }

    public Selection.SelectionType getType() {
        return type;
    }

    public void setMode(Selection.SelectionMode mode) {
        this.mode = mode;
    }

    public Selection.SelectionMode getMode() {
        return mode;
    }

    public void setTarget(Selection.SelectionTarget target) {
        this.target = target;
    }

    public Selection.SelectionTarget getTarget() {
        return target;
    }

    public void setFeatureAngle(double featureAngle) {
        this.featureAngle = featureAngle;
    }

    public double getFeatureAngle() {
        return featureAngle;
    }

    public void setKeepSelection(boolean keepSelection) {
        this.keepSelection = keepSelection;
    }

    public boolean isKeepSelection() {
        return keepSelection;
    }

    public void setSelectionData(vtkPolyData selectionData) {
        firePropertyChange(SELECTION_DATA, this.selectionData, this.selectionData = selectionData);
    }

    public vtkPolyData getSelectionData() {
        return selectionData;
    }

    public void setInverseSelectionData(vtkPolyData inverseSelectionData) {
        this.inverseSelectionData = inverseSelectionData;
    }

    public vtkPolyData getInverseSelectionData() {
        return inverseSelectionData;
    }

    public void setIdList(vtkIdTypeArray list) {
        this.idList = list;
    }

    public vtkIdTypeArray getIdList() {
        return idList;
    }

    public void setDataSet(vtkPolyData dataSet) {
        firePropertyChange("dataSet", this.dataSet, this.dataSet = dataSet);
    }

    public vtkPolyData getDataSet() {
        return dataSet;
    }

    public boolean isEmpty() {
        if(selectionData == null) return true;
        if(selectionData.GetPointData() == null) return true;
        if(selectionData.GetCellData() == null) return true;
        int points = selectionData.GetPointData().GetNumberOfArrays();
        int cells = selectionData.GetCellData().GetNumberOfArrays();
        return points == 0 && cells == 0;
    }
}