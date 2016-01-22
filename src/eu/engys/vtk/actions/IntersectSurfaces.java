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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vtk.vtkIntersectionPolyDataFilter;
import vtk.vtkPolyData;
import vtk.vtkTriangleFilter;

public class IntersectSurfaces {

    private static final Logger logger = LoggerFactory.getLogger(IntersectSurfaces.class);
    
    private vtkPolyData input1;
    private vtkPolyData input2;

    private boolean triangulateInput1;
    private boolean triangulateInput2;
    
    public IntersectSurfaces() {
        this.input1 = null;
        this.input2 = null;
    }
    
    public void setInput1(vtkPolyData input1) {
        this.input1 = input1;
    }
    public void setInput2(vtkPolyData input2) {
        this.input2 = input2;
    }
    
    public void setTriangulateInput1(boolean triangulateInput1) {
        this.triangulateInput1 = triangulateInput1;
    }
    
    public void setTriangulateInput2(boolean triangulateInput2) {
        this.triangulateInput2 = triangulateInput2;
    }
    
    public vtkPolyData execute() {
        if (input1 != null && input2 != null) {

            vtkIntersectionPolyDataFilter intersect = new vtkIntersectionPolyDataFilter();

            if (triangulateInput1) {
                vtkTriangleFilter triangle = new vtkTriangleFilter();
                triangle.SetInputData(input1);
                triangle.Update();

                intersect.SetInputData(0, triangle.GetOutput());
            } else {
                intersect.SetInputData(0, input1);
            }

            if (triangulateInput2) {
                vtkTriangleFilter triangle = new vtkTriangleFilter();
                triangle.SetInputData(input2);
                triangle.Update();

                intersect.SetInputData(1, triangle.GetOutput());
            } else {
                intersect.SetInputData(1, input2);
            }

            intersect.SplitFirstOutputOff();
            intersect.SplitSecondOutputOff();
            intersect.Update();

            log("LINE", intersect.GetOutput());
            return intersect.GetOutput();
        } else {
            return null;
        }
    }

    private void log(String title, vtkPolyData data) {
        logger.info(title + "points: {}, cells: {}, lines: {}", data.GetNumberOfPoints(), data.GetNumberOfCells(), data.GetNumberOfLines());
        final double[] bounds = data.GetBounds();
        System.out.println(title + " Z     : [" + bounds[4] + ", " + bounds[5] + "]");
        System.out.println(title + " points: " + data.GetNumberOfPoints());
        System.out.println(title + " cells : " + data.GetNumberOfCells());
        System.out.println(title + " lines : " + data.GetNumberOfLines());

    }

}
