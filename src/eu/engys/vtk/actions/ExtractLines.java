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

import vtk.vtkBox;
import vtk.vtkExtractGeometry;
import vtk.vtkFeatureEdges;
import vtk.vtkGeometryFilter;
import vtk.vtkPolyData;

public class ExtractLines {

    private static final Logger logger = LoggerFactory.getLogger(ExtractLines.class);
    
    private vtkPolyData input;
    
    private double[] insideMin;
    private double[] insideMax;

    private double[] outsideMin;
    private double[] outsideMax;

    private double angle;

    private boolean boundary;

    private boolean manifold;

    private boolean nonmanifold;
    
    public ExtractLines() {
        this.input = null;
        this.insideMin = null;
        this.insideMax = null;
        this.outsideMin = null;
        this.outsideMax = null;
    }
    
    public void setInput(vtkPolyData input) {
        this.input = input;
    }
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public void setInsideMin(double[] insideMin) {
        this.insideMin = insideMin;
    }
    public void setInsideMax(double[] insideMax) {
        this.insideMax = insideMax;
    }
    public void setOutsideMin(double[] outsideMin) {
        this.outsideMin = outsideMin;
    }
    public void setOutsideMax(double[] outsideMax) {
        this.outsideMax = outsideMax;
    }
    public void setBoundary(boolean boundary) {
        this.boundary = boundary;
    }
    public void setManifold(boolean manifold) {
        this.manifold = manifold;
    }
    public void setNonmanifold(boolean nonmanifold) {
        this.nonmanifold = nonmanifold;
    }
    
    public vtkPolyData execute() {
        vtkFeatureEdges edges = new vtkFeatureEdges();
        if (boundary) {
            edges.BoundaryEdgesOn();
        } else {
            edges.BoundaryEdgesOff();
        }
        if (manifold) {
            edges.ManifoldEdgesOn();
        } else {
            edges.ManifoldEdgesOff();
        }
        if (nonmanifold) {
            edges.NonManifoldEdgesOn();
        } else {
            edges.NonManifoldEdgesOff();
        }
        edges.FeatureEdgesOn();
        edges.SetFeatureAngle(angle);
        edges.ColoringOff();   
        edges.SetInputData(input);
        edges.Update();

        vtkPolyData output = edges.GetOutput();
        log("EDGES", output);
        
        if (insideMin != null && insideMax != null) {
            vtkBox box = new vtkBox();
            box.SetXMin(insideMin);
            box.SetXMax(insideMax);
            
            vtkExtractGeometry extract = new vtkExtractGeometry();
            extract.ExtractInsideOn();
            extract.ExtractBoundaryCellsOff();
            extract.SetImplicitFunction(box);
            extract.SetInputData(output);
            extract.Update();
            
            vtkGeometryFilter geometry = new vtkGeometryFilter();
            geometry.SetInputData(extract.GetOutput());
            geometry.Update();
            
            output = geometry.GetOutput();
            log("INSIDE", output);
        }

        if (outsideMin != null && outsideMax != null) {
            vtkBox box = new vtkBox();
            box.SetXMin(outsideMin);
            box.SetXMax(outsideMax);
            
            vtkExtractGeometry extract = new vtkExtractGeometry();
            extract.ExtractInsideOff();
            extract.ExtractBoundaryCellsOff();
            extract.SetImplicitFunction(box);
            extract.SetInputData(output);
            extract.Update();
            
            vtkGeometryFilter geometry = new vtkGeometryFilter();
            geometry.SetInputData(extract.GetOutput());
            geometry.Update();
            
            output = geometry.GetOutput();
            log("OUTSIDE", output);
        }
        
        return output;
    }

    private void log(String title, vtkPolyData data) {
        logger.info(title + "points: {}, cells: {}, lines: {}", data.GetNumberOfPoints(), data.GetNumberOfCells(), data.GetNumberOfLines());
//        final double[] bounds = data.GetBounds();
//        System.out.println(title + " Z     : [" + bounds[4] + ", " + bounds[5] + "]");
//        System.out.println(title + " points: " + data.GetNumberOfPoints());
//        System.out.println(title + " cells : " + data.GetNumberOfCells());
//        System.out.println(title + " lines : " + data.GetNumberOfLines());

    }

}
