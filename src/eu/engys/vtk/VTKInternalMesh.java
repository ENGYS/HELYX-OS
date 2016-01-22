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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import vtk.vtkCutter;
import vtk.vtkDataObject;
import vtk.vtkExtractGeometry;
import vtk.vtkPlane;
import vtk.vtkTableBasedClipDataSet;
import vtk.vtkUnstructuredGrid;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.vtk.actors.InternalMeshActor;

public class VTKInternalMesh implements VTKActors {

    private Actor actor;
    private RenderPanel renderPanel;

    private vtkCutter slicer;
    private vtkExtractGeometry crinkle;
    private vtkTableBasedClipDataSet clipper;

    private vtkUnstructuredGrid internalMeshDataset;

    public VTKInternalMesh(ProgressMonitor monitor) {
    }

    @Override
    public void setRenderPanel(RenderPanel renderPanel) {
        this.renderPanel = renderPanel;
    }
    
    public void update(vtkDataObject dataset) {
        if (actor != null) {
            renderPanel.removeActor(actor);

            load(dataset);

            VisibilityOn();

            if (slicer != null) {
                slicer.SetInputData(internalMeshDataset);
                slicer.Update();
                connectActorToSlicer();
            } else if (clipper != null) {
                clipper.SetInputData(internalMeshDataset);
                clipper.Update();
                connectActorToClipper();
            } else if (crinkle != null) {
                crinkle.SetInputData(internalMeshDataset);
                crinkle.Update();
                connectActorToCrinkler();
            }

            renderPanel.addActor(actor);
        }
    }
    
    public void load(vtkDataObject internalMeshDataset) {

        if (this.internalMeshDataset != null) {
            VTKUtil.deleteDataset(this.internalMeshDataset);
        }

        if (this.actor != null) {
            this.actor.deleteActor();
        }

        this.internalMeshDataset = new vtkUnstructuredGrid();
        this.internalMeshDataset.ShallowCopy((vtkUnstructuredGrid) internalMeshDataset);
        this.actor = new InternalMeshActor(this.internalMeshDataset);
    }
    
    public void deleteActors() {
        if (actor != null) {
            renderPanel.removeActor(actor);
            actor.deleteActor();
            actor = null;

            deleteClipper();
            deleteSlicer();
        }
    }

    public void removeActorsFromRenderer() {
        if (actor != null) {
            renderPanel.removeActor(actor);
        }
        actor = null;
    }

    private void deleteSlicer() {
        if (slicer != null) {
            slicer.RemoveAllInputs();
            slicer.Delete();
            slicer = null;
        }
    }

    private void deleteClipper() {
        if (clipper != null) {
            clipper.RemoveAllInputs();
            clipper.Delete();
            clipper = null;
        }
    }

    private void deleteCrinkle() {
        if (crinkle != null) {
            crinkle.RemoveAllInputs();
            crinkle.Delete();
            crinkle = null;
        }
    }

    public void VisibilityOff() {
        if (actor != null) {
            actor.setVisibility(false);
        }
    }

    public void VisibilityOn() {
        if (actor != null) {
            actor.setVisibility(true);
        }
    }

    public void disconnectFilters() {
        actor.setInput(internalMeshDataset);

        deleteClipper();
        deleteSlicer();
        deleteCrinkle();
    }

    public boolean isLoaded() {
        return actor != null;
    }

    public void show() {
        renderPanel.addActor(actor);
    }

    @Override
    public Collection<Actor> getActors() {
        return actor != null ? Arrays.asList(actor) : Collections.<Actor> emptyList();
    }

    @Override
    public boolean containsActor(Actor pickedActor) {
        return false;
    }

    public void clip(vtkPlane plane) {
        deleteClipper();
        deleteSlicer();
        deleteCrinkle();

        clipper = new vtkTableBasedClipDataSet();
        clipper.SetInputData(internalMeshDataset);
        clipper.SetClipFunction(plane);
        clipper.InsideOutOff();
        clipper.Update();

        connectActorToClipper();

        renderPanel.renderLater();

        VTKUtil.gc(false);
    }

    private void connectActorToClipper() {
        actor.interactiveOff();
        actor.setInput(clipper.GetOutput());
    }

    void crinkle(vtkPlane plane) {
        deleteClipper();
        deleteSlicer();
        deleteCrinkle();

        crinkle = new vtkExtractGeometry();
        crinkle.SetInputData(internalMeshDataset);
        crinkle.ExtractInsideOn();
        crinkle.ExtractOnlyBoundaryCellsOn();
        crinkle.ExtractBoundaryCellsOn();
        crinkle.SetImplicitFunction(plane);
        crinkle.Update();

        connectActorToCrinkler();

        renderPanel.renderLater();
    }

    private void connectActorToCrinkler() {
        actor.interactiveOff();
        actor.setInput(crinkle.GetOutput());
    }

    void slice(vtkPlane plane) {
        deleteClipper();
        deleteSlicer();
        deleteCrinkle();

        slicer = new vtkCutter();
        slicer.SetInputData(internalMeshDataset);
        slicer.GenerateTrianglesOff();
        slicer.SetCutFunction(plane);
        slicer.Update();
        // slicerSetNumberOfContours(nbContours);

        connectActorToSlicer();

        renderPanel.renderLater();
    }

    private void connectActorToSlicer() {
        actor.interactiveOff();
        actor.setInput(slicer.GetOutput());
    }

    void insideOut(boolean selected) {
        if (selected) {
            clipper.InsideOutOn();
        } else {
            clipper.InsideOutOff();
        }
        clipper.Update();

        connectActorToClipper();

        renderPanel.renderLater();
    }

    public Map<String, Actor> getActorsMap() {
        return null;
    }
}
