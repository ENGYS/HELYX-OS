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
package eu.engys.vtk.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.vtk.VTKUtil;
import vtk.vtkPolyData;

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

            vtkPolyData output = VTKUtil.intersect(input1, input2, triangulateInput1, triangulateInput2);

            log("LINE", output);
            return output;
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
