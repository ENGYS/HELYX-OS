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
package eu.engys.vtk.widgets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.engys.core.project.Model;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.gui.view3D.Representation;
import eu.engys.util.ui.checkboxtree.VisibleItem;
import eu.engys.vtk.VTKColors;
import eu.engys.vtk.VTKUtil;
import eu.engys.vtk.actors.DefaultActor;
import vtk.vtkDataSet;
import vtk.vtkLookupTable;
import vtk.vtkPolyData;
import vtk.vtkPolyDataReader;

public class vtkPolyDataWidget {

    private static final String LAYER_INFO_VTK = "layerInfo.vtk";

    private RenderPanel renderPanel;
    private Actor actor;
    private vtkLookupTable lut;

    private vtkPolyData layerInfo;

    public vtkPolyDataWidget(Model model, RenderPanel renderPanel) {
        this.renderPanel = renderPanel;

        vtkPolyDataReader reader = new vtkPolyDataReader();
        reader.ReadAllFieldsOn();
        reader.ReadAllScalarsOn();
        reader.SetFileName(new File(model.getProject().getBaseDir(), LAYER_INFO_VTK).getAbsolutePath());
        reader.Update();

        layerInfo = reader.GetOutput();

        this.actor = new DefaultActor("LayersInfo") {
            {
                newActor(layerInfo, true);
            }

            @Override
            public VisibleItem getVisibleItem() {
                return null;
            }

            @Override
            public void setRepresentation(Representation representation) {
                super.setRepresentation(Representation.SURFACE);
            }
        };
    }

    public void update(FieldItem field) {
        lut = new vtkLookupTable();
        VTKColors.applyTypeToLookupTable(field, lut);
        this.actor.setScalarColors(lut, field);
    }

    public void On() {
        renderPanel.addActor(actor);
    }

    public void Off() {
        renderPanel.removeActor(actor);
    }

    public vtkLookupTable getLut() {
        return lut;
    }

    public void Delete() {
        renderPanel.removeActor(actor);
        actor.deleteActor();
        lut.Delete();
    }

    public double[] getRange(String fieldName) {
        List<vtkDataSet> dataset = new ArrayList<>();
        dataset.add(layerInfo);
        return VTKUtil.getCellFieldsRanges(dataset).get(fieldName)[0];
    }
}
