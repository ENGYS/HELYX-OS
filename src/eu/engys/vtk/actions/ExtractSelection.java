/*--------------------------------*- Java -*---------------------------------*\
 |		 o                                                                   |                                                                                     
 |    o     o       | HelyxOS: The Open Source GUI for OpenFOAM              |
 |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 |    o     o       | http://www.engys.com                                   |
 |       o          |                                                        |
 |---------------------------------------------------------------------------|
 |	 License                                                                 |
 |   This file is part of HelyxOS.                                           |
 |                                                                           |
 |   HelyxOS is free software; you can redistribute it and/or modify it      |
 |   under the terms of the GNU General Public License as published by the   |
 |   Free Software Foundation; either version 2 of the License, or (at your  |
 |   option) any later version.                                              |
 |                                                                           |
 |   HelyxOS is distributed in the hope that it will be useful, but WITHOUT  |
 |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 |   for more details.                                                       |
 |                                                                           |
 |   You should have received a copy of the GNU General Public License       |
 |   along with HelyxOS; if not, write to the Free Software Foundation,      |
 |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
\*---------------------------------------------------------------------------*/

package eu.engys.vtk.actions;

import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Vector3d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vtk.vtkCellData;
import vtk.vtkDataArray;
import vtk.vtkDataSet;
import vtk.vtkExtractPolyDataGeometry;
import vtk.vtkExtractSelection;
import vtk.vtkFloatArray;
import vtk.vtkGeometryFilter;
import vtk.vtkIdFilter;
import vtk.vtkIdList;
import vtk.vtkIdTypeArray;
import vtk.vtkPolyData;
import vtk.vtkPolyDataNormals;
import vtk.vtkSelection;
import vtk.vtkSelectionNode;
import eu.engys.gui.view3D.PickInfo;
import eu.engys.gui.view3D.Selection;
import eu.engys.gui.view3D.Selection.SelectionMode;

public class ExtractSelection {

    private static final Logger logger = LoggerFactory.getLogger(ExtractSelection.class);
    
    private Selection selection;
    
    private vtkPolyData selectionData;
    private vtkPolyData inverseSelectionData;
    private vtkIdTypeArray list;

    public void setSelection(Selection selection) {
        this.selection = selection;
    }
    
    public void execute(PickInfo pi) {
        
        String name = (pi.actor != null ? pi.actor.getName() : null);
        
        logger.debug("------------- PICK --------------");
        logger.debug("  ACTOR   : " + name);
        logger.debug("  CELL    : " + pi.cellId);
        logger.debug("  FRUSTUM : " + (pi.frustum != null ? pi.frustum.GetClassName() : "null"));
        if (selection.getDataSet() != null) {
            logger.debug("  DATASET 1 ");
            logger.debug("    hashcode : " + selection.getDataSet().hashCode());
            logger.debug("    cells    : " + selection.getDataSet().GetNumberOfCells());
            logger.debug("    point    : " + selection.getDataSet().GetNumberOfPoints());
        } else {
            logger.debug("  DATASET 1 : " + "null");
        }
        if (pi.dataSet != null) {
            logger.debug("  DATASET 2 ");
            logger.debug("    hashcode : " + pi.dataSet.hashCode());
            logger.debug("    cells    : " + pi.dataSet.GetNumberOfCells());
            logger.debug("    points   : " + pi.dataSet.GetNumberOfPoints());
        } else {
            logger.debug("  DATASET 2 : " + "null");
        }

        boolean keepSelection = selection.isKeepSelection() || pi.control;

//        if (selection.getDataSet() != pi.dataSet) {
//            return;
//        }
        
        if (keepSelection) {
            logger.debug("SELECTION: KEEP");
            this.list = selection.getIdList() != null ? selection.getIdList() : new vtkIdTypeArray();
        } else {
            logger.debug("SELECTION: DISCARD");
            this.list = new vtkIdTypeArray();
        }

        switch (selection.getType()) {
            case CELL:
                logger.debug("PICK BY: CELL");
                pickByCell(pi);
                break;
            case AREA:
                logger.debug("PICK BY: AREA");
                pickByArea(pi);
                break;
            case FEATURE:
                logger.debug("PICK BY: FEATURE");
                pickByFeature(pi);
                break;
    
            default:
                break;
        }
        
        applySelection(selection.getDataSet());

        selection.setSelectionData(selectionData);
        selection.setInverseSelectionData(inverseSelectionData);
        selection.setIdList(list);

        logger.debug("SELECTION cells: "+selectionData.GetNumberOfCells());
        logger.debug("SELECTION points: "+selectionData.GetNumberOfPoints());
        logger.debug("---------------------------");
    }
    
    private void pickByCell(PickInfo pi) {
        int cellId = pi.cellId;
        if (cellId < 0)
            return;

        performSelection(cellId);
    }

    private void pickByArea(PickInfo pi) {
        if (pi.frustum != null && pi.dataSet != null) {

            vtkDataSet input = selection.getDataSet();//pi.dataSet;
            vtkIdFilter idFilter = new vtkIdFilter();
            idFilter.CellIdsOn();
//            idFilter.PointIdsOff();
//            idFilter.FieldDataOff();
            idFilter.SetIdsArrayName("originalCellIds");
            idFilter.SetInputData(input);
            idFilter.Update();
            
            vtkExtractPolyDataGeometry extractor = new vtkExtractPolyDataGeometry();
            extractor.SetInputData(idFilter.GetOutput());
            extractor.ExtractInsideOn();
            extractor.ExtractBoundaryCellsOff();
            extractor.SetImplicitFunction(pi.frustum);
            extractor.Update();
            vtkDataSet output = extractor.GetOutput();
            
            vtkIdTypeArray ids = (vtkIdTypeArray) output.GetCellData().GetArray("originalCellIds");
            if (ids != null) {
                for (int i = 0; i < ids.GetNumberOfTuples(); i++) {
                    int id = ids.GetValue(i);
                    performSelection(id);
                }
            }
            
//          vtkExtractSelectedFrustum extractor = new vtkExtractSelectedFrustum();
//          extractor.SetInput(pi.dataSet);
//          extractor.ShowBoundsOff();
//          extractor.PreserveTopologyOff();
//          extractor.SetFrustum(pi.frustum);
//          extractor.Update();
//          vtkDataSet output = (vtkDataSet) extractor.GetOutput();
//
//          vtkIdTypeArray ids = (vtkIdTypeArray) output.GetCellData().GetArray("vtkOriginalCellIds");
//          if (ids != null) {
//              for (int i = 0; i < ids.GetNumberOfTuples(); i++) {
//                  performSelection(ids.GetValue(i));
//              }
//          }
        }
    }

    private void pickByFeature(PickInfo pi) {
        double[] normal = pi.normal;
        vtkDataSet input = pi.dataSet;
        int cellId = pi.cellId;

        if (input == null)
            return;

        vtkPolyData output = getNormalsDataSet(normal, input);
        Set<Integer> cells = new HashSet<>();

        if (output.GetNumberOfCells() > 0 ) {
            analyseNeighbours(cellId, output, cells);
            
            performSelection(cellId);
            
            for (Integer id : cells) {
                performSelection(id);
            }
        }

    }

    private void analyseNeighbours(int cellId, vtkPolyData output, Set<Integer> cells) {
        vtkCellData outputData = output.GetCellData();
        vtkDataArray angles = outputData.GetVectors("Angles");

        vtkIdList cellPointIds = new vtkIdList();
        output.GetCellPoints(cellId, cellPointIds);

        // neighbor cells may be listed multiple times
        // use set instead of list to get a unique list of neighbors
        Set<Integer> neighbors = new HashSet<>();
        /*
         * For each vertice of the cell, we calculate which cells uses that
         * point. So if we make this, for each vertice, we have all the
         * neighbors. In the case we use ''cellPointIds'' as a parameter of
         * ''GeteCellNeighbors'', we will obtain an empty set. Because the only
         * cell that is using that set of points is the current one. That is why
         * we have to make each vertice at time.
         */

        for (int i = 0; i < cellPointIds.GetNumberOfIds(); i++) {
            vtkIdList idList = new vtkIdList();
            idList.InsertNextId(cellPointIds.GetId(i));

            // get the neighbors of the cell
            vtkIdList neighborCellIds = new vtkIdList();

            output.GetCellNeighbors(cellId, idList, neighborCellIds);

            for (int j = 0; j < neighborCellIds.GetNumberOfIds(); j++) {
                int id = neighborCellIds.GetId(j);
                double angle = angles.GetComponent(id, 0);
                if (angle < selection.getFeatureAngle()) {
                    neighbors.add(id);
                }
            }
        }

        Set<Integer> newCells = new HashSet<>();
        for (Integer id : neighbors) {
            if (!cells.contains(id)) {
                newCells.add(id);
                cells.add(id);
            }
        }

        if (!newCells.isEmpty()) {
            for (Integer newCell : newCells) {
                analyseNeighbours(newCell, output, cells);
            }
        }
    }

    private vtkPolyData getNormalsDataSet(double[] normal, vtkDataSet dataSet) {
        vtkPolyDataNormals normalsFilter = new vtkPolyDataNormals();
        normalsFilter.SetInputData(dataSet);
        normalsFilter.SplittingOff();
        normalsFilter.SetFeatureAngle(60);
        normalsFilter.ComputeCellNormalsOn();
        // normalsFilter.AutoOrientNormalsOn();
        normalsFilter.Update();

        vtkPolyData output = normalsFilter.GetOutput();
        vtkCellData outputData = output.GetCellData();
        vtkDataArray normals = outputData.GetVectors("Normals");

        vtkFloatArray angles = new vtkFloatArray();
        angles.SetName("Angles");

        if (normals != null) {
            for (int i = 0; i < normals.GetNumberOfTuples(); i++) {
                double[] t = normals.GetTuple3(i);
                double a = computeAngle(t, normal);
                angles.InsertNextValue(a);
            }
        }
        outputData.AddArray(angles);
        return output;
    }

    private double computeAngle(double[] t, double[] normal) {
        Vector3d v = new Vector3d(t);
        Vector3d n = new Vector3d(normal);
        double angle = n.angle(v);

        return Math.toDegrees(angle);
    }

    private void performSelection(int cellId) {
        if (selection.getMode() == SelectionMode.SELECT) { 
            select(cellId);
        } else {
            deselect(cellId);
        }
    }
    
    private void select(int cellId) {
        list.InsertNextValue(cellId);
    }

    private void deselect(int cellId) {
        int index = -1;
        if ((index = alreadySelected(cellId)) >= 0) {
            list.RemoveTuple(index);
        }
    }

    private int alreadySelected(int cellId) {
        for (int i = 0; i < list.GetNumberOfTuples(); i++) {
            if (list.GetValue(i) == cellId) {
                return i;
            }
        }
        return -1;
    }
    
    private void applySelection(vtkDataSet dataSet) {
        vtkSelection selection = new vtkSelection();
        vtkSelectionNode node = new vtkSelectionNode();
        node.SetContentType(4); //INDICES
        node.SetFieldType(0); //CELL
        node.SetSelectionList(list);
        selection.AddNode(node);
        
        vtkExtractSelection filter = new vtkExtractSelection();
        filter.SetInputData(0, dataSet);
        filter.SetInputData(1, selection);
        filter.Update();
        
        vtkGeometryFilter geom = new vtkGeometryFilter();
        geom.SetInputData(filter.GetOutput());
        geom.Update();

        selectionData = geom.GetOutput();
        selection.Delete();
        node.Delete();
        filter.Delete();
        geom.Delete();

        selection = new vtkSelection();
        node = new vtkSelectionNode();
        node.SetContentType(4); // INDICES
        node.SetFieldType(0); // CELL
        node.SetSelectionList(list);
        node.GetProperties().Set(node.INVERSE(), 1);
        selection.AddNode(node);

        filter = new vtkExtractSelection();
        filter.SetInputData(0, dataSet);
        filter.SetInputData(1, selection);
//        VTKProgressConsoleWrapper progressWrapper = new VTKProgressConsoleWrapper("", filter, monitor);
//        filter.AddObserver("StartEvent", progressWrapper, "onStart");
//        filter.AddObserver("EndEvent", progressWrapper, "onEnd");
//        filter.AddObserver("ProgressEvent", progressWrapper, "onProgress");
        filter.Update();

        geom = new vtkGeometryFilter();
        geom.SetInputData(filter.GetOutput());
        geom.Update();

        inverseSelectionData = geom.GetOutput();
        selection.Delete();
        node.Delete();
        filter.Delete();
        geom.Delete();
    }
}
