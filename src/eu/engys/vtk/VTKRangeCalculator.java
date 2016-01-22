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

package eu.engys.vtk;

import vtk.vtkDataArray;
import vtk.vtkMapper;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.core.project.mesh.FieldItem.DataType;
import eu.engys.gui.view3D.Actor;

public class VTKRangeCalculator {

    private FieldItem fieldItem;

    public VTKRangeCalculator(FieldItem fieldItem) {
        this.fieldItem = fieldItem;
    }

    public void calculateRange_Automatically_For(VTKActors actors) {
        if (!actors.getActors().isEmpty()) {
            double[] range = new double[] { Double.MAX_VALUE, -Double.MAX_VALUE };
            for (Actor actor : actors.getActors()) {
                if (actor.getVisibility()) {
                    double[] drange = calculateRangeFor(actor);
                    if (drange != null) {
//                        System.out.println("VTKRangeCalculator.calculateRange_Automatically_For() " + Arrays.toString(drange));
                        range[0] = Math.min(range[0], drange[0]);
                        range[1] = Math.max(range[1], drange[1]);
                    }
                }
            }
            if (range[0] <= range[1] ) {
//                System.out.println("VTKRangeCalculator.calculateRange_Automatically_For() " + actors.getClass() + " => " + Arrays.toString(range));
                fieldItem.setRange(range);
            }
        }
    }

    public void calculateRange_Automatically_For(Actor actor) {
        double[] range = fieldItem.getRange();
        if (actor.getVisibility()) {
            double[] drange = calculateRangeFor(actor);
            if (drange != null) {
                range[0] = drange[0];
                range[1] = drange[1];
            }

        }
        fieldItem.setRange(range);
    }

    private double[] calculateRangeFor(Actor actor) {
        vtkMapper mapper = actor.getMapper();
        mapper.Update();

        vtkDataArray pScalars = null;
        DataType dataType = fieldItem.getDataType();
        String fieldName = fieldItem.getName();

        if (dataType.isCell()) {
            pScalars = mapper.GetInputAsDataSet().GetCellData().GetScalars(fieldName);
        } else if (dataType.isPoint()) {
            pScalars = mapper.GetInputAsDataSet().GetPointData().GetScalars(fieldName);
        }

        if (pScalars != null) {
            return fieldItem.getComponent() >= 0 ? pScalars.GetRange(fieldItem.getComponent() - 1) : pScalars.GetRange();
        }

        return null;
    }

}
