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

package eu.engys.vtk.actors;

import vtk.vtkLookupTable;
import eu.engys.core.project.geometry.surface.PlaneRegion;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.gui.view3D.Representation;
import eu.engys.vtk.VTKColors;
import eu.engys.vtk.actors.SurfaceToActor.ActorMode;

public class PlaneRegionActor extends SurfaceActor {

    private ActorMode mode;

    public PlaneRegionActor(PlaneRegion plane, ActorMode mode) {
        super(plane);
        this.mode = mode;
        newActor(plane.getDataSet(), plane.isVisible());
        if (mode == ActorMode.DEFAULT) {
            setColor(VTKColors.CYAN, 0.5);
        }
    }
    
    @Override
    public void setRepresentation(Representation representation) {
        if (mode == ActorMode.DEFAULT) {
            super.setRepresentation(Representation.SURFACE_WITH_EDGES);
        } else {
            super.setRepresentation(representation);
        }
    }
    
    @Override
    public void setScalarColors(vtkLookupTable lut, FieldItem field) {
        if (mode == ActorMode.VIRTUALISED) {
            super.setScalarColors(lut, field);
        }
    }
    
    @Override
    public void setSolidColor(double[] color, double opacity) {
        if (mode == ActorMode.VIRTUALISED) {
            super.setSolidColor(color, opacity);
        }
    }
}
