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

package eu.engys.core.project.geometry.stl;

import java.util.Arrays;

import vtk.vtkCell;
import vtk.vtkCellArray;
import vtk.vtkIdList;
import vtk.vtkMergePoints;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkTriangle;

public class RemoveDuplicates {

    private vtkPolyData dataset;
    private vtkMergePoints PointsLocator;
    private vtkMergePoints CellsLocator;

    public RemoveDuplicates(vtkPolyData dataset) {
        this.dataset = dataset;
        this.PointsLocator = new vtkMergePoints();
        this.CellsLocator = new vtkMergePoints();
    }

    public vtkPolyData execute() {
        vtkPolyData input = dataset;
        vtkPolyData output = new vtkPolyData();

        if (input.GetNumberOfCells() == 0)
        {
            // set up a ugrid with same data arrays as input, but
            // no points, cells or data.
            output.Allocate(1, 0);
//            output.GetPointData().CopyAllocate(input.GetPointData(), 0, 0);
//            output.GetCellData().CopyAllocate(input.GetCellData(), 0, 0);
            vtkPoints pts = new vtkPoints();
            output.SetPoints(pts);
            pts.Delete();
            return output;
        }

//        output.GetPointData().CopyAllocate(input.GetPointData(), 0, 0);
//        output.GetCellData().PassData(input.GetCellData());

        // First, create a new points array that eliminate duplicate points.
        // Also create a mapping from the old point id to the new.
        vtkPoints newPts = new vtkPoints();
        int num = input.GetNumberOfPoints();
        int newId;
        int[] ptMap = new int[num];
        double[] pt = new double[3];
        
        this.PointsLocator.InitPointInsertion(newPts, input.GetBounds(), num);
        
//        int progressStep = num / 100;
//        if (progressStep == 0)
//        {
//            progressStep = 1;
//        }
        for (int id = 0; id < num; ++id)
        {
//            if (id % progressStep == 0)
//            {
//                this.UpdateProgress(0.8*((float)id/num));
//            }
            input.GetPoint(id, pt);
            if ( (newId = this.PointsLocator.IsInsertedPoint(pt))  < 0) 
            {
                newId = this.PointsLocator.InsertNextPoint(pt);
                output.GetPointData().CopyData(input.GetPointData(),id,newId);
            }
            ptMap[id] = newId;
        }
        output.SetPoints(newPts);
        newPts.Delete();
        
        
        // New copy the cells.
        int newCenterId;
        vtkPoints newCenterPts = new vtkPoints();
        vtkIdList cellPoints = new vtkIdList();
        num = input.GetNumberOfCells();
//        output.Allocate(num, 0);
        
        this.CellsLocator.InitPointInsertion(newCenterPts, input.GetBounds(), num);
        
        vtkCellArray cells = new vtkCellArray();
        output.SetPolys(cells);

        System.out.println("RemoveDuplicates.execute() " + output.GetNumberOfCells());
        
        for (int id = 0; id < num; ++id)
        {
//            if (id % progressStep == 0)
//            {
//                this.UpdateProgress(0.8+0.2*((float)id/num));
//            }
//            input.GetCell(id).GetParametricCenter(id0);
            
            vtkCell cell = input.GetCell(id);
            
            if (cell instanceof vtkTriangle) {
                vtkTriangle tri = (vtkTriangle) cell;
                vtkPoints verices = tri.GetPoints();
                double[] center = new double[3];
                tri.TriangleCenter(verices.GetPoint(0), verices.GetPoint(1), verices.GetPoint(2), center);

                newCenterId = this.CellsLocator.IsInsertedPoint(center);
                System.out.println("RemoveDuplicates.execute() center: " + Arrays.toString(center) + ", id: " + newCenterId);
                if ( newCenterId < 0)
                {
                    newCenterId = this.CellsLocator.InsertNextPoint(center);
                    
                    input.GetCellPoints(id, cellPoints);
                    for (int i=0; i < cellPoints.GetNumberOfIds(); i++)
                    {
                        int cellPtId = cellPoints.GetId(i);
                        newId = ptMap[cellPtId];
                        cellPoints.SetId(i, newId);
                    }
                    cells.InsertNextCell(cell);
                } else {
                    System.err.println("Duplicated!");
                }
            } else {
                System.err.println("Cell isn't a TRIANGLE");
            }
        }
        
        output.SetPolys(cells);
        
//        delete [] ptMap;
        cellPoints.Delete();
        output.Squeeze();
        return output;
    }
}
