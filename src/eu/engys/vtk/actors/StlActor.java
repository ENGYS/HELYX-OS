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
package eu.engys.vtk.actors;

import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.core.project.geometry.surface.Stl;
import vtk.vtkAppendPolyData;


public class StlActor extends SurfaceActor {
    
    public StlActor(Stl stl) {
        super(stl);
        
        vtkAppendPolyData append = new vtkAppendPolyData();
        for (Solid solid : stl.getSolids()) {
            append.AddInputData(solid.getDataSet());
        }
        append.Update();

        newActor(append.GetOutput(), stl.isVisible());
//        vtkCleanPolyData clean = new vtkCleanPolyData();
//        clean.AddInputData(append.GetOutput());
//        clean.Update();
//
//        newActor(clean.GetOutput(), stl.isVisible());
        append.Delete();
//        clean.Delete();

        transformActor(true, stl.getTransformation());
    }   
}