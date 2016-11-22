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
package eu.engys.vtk;

import static eu.engys.vtk.VTK3DActionsToolBar._3D_AXIS_XNEG;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_AXIS_XPOS;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_AXIS_YNEG;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_AXIS_YPOS;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_AXIS_ZNEG;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_AXIS_ZPOS;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_LOAD_MESH;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_VIEW_EDGES;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_VIEW_OUTLINE;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_VIEW_PROFILE;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_VIEW_PROJECTIONS;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_VIEW_SURFACE;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_VIEW_WIREFRAME;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_ZOOM_IN;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_ZOOM_OUT;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_ZOOM_RESET;
import static eu.engys.vtk.VTK3DActionsToolBar._3D_ZOOM_TOBOX;

import eu.engys.core.presentation.Action;
import eu.engys.core.presentation.ActionContainer;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.presentation.ActionToggle;
import eu.engys.gui.view3D.CameraManager.Position;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.gui.view3D.Representation;

public class VTKView3DController implements ActionContainer {

    private RenderPanel renderPanel;
    private VTKView3D view3D;

    public VTKView3DController(VTKView3D vtkView3D) {
        this.view3D = vtkView3D;
        this.renderPanel = vtkView3D.getRenderPanel();
        ActionManager.getInstance().parseActions(this);
    }

    @Override
    public boolean isDemo() {
        return false;
    }

    @Action(key = _3D_LOAD_MESH)
    public void loadMesh() {
        view3D.getMeshController().showExternalMesh();
        // view3D.load();
        view3D.start(null);
    }

    @Action(key = _3D_AXIS_XPOS)
    public void viewXPos() {
        renderPanel.setCameraPosition(Position.X_POS);
    }

    @Action(key = _3D_AXIS_XNEG)
    public void viewXNeg() {
        renderPanel.setCameraPosition(Position.X_NEG);
    }

    @Action(key = _3D_AXIS_YPOS)
    public void viewYPos() {
        renderPanel.setCameraPosition(Position.Y_POS);
    }

    @Action(key = _3D_AXIS_YNEG)
    public void viewYNeg() {
        renderPanel.setCameraPosition(Position.Y_NEG);
    }

    @Action(key = _3D_AXIS_ZPOS)
    public void viewZPos() {
        renderPanel.setCameraPosition(Position.Z_POS);
    }

    @Action(key = _3D_AXIS_ZNEG)
    public void viewZNeg() {
        renderPanel.setCameraPosition(Position.Z_NEG);
    }

    @Action(key = _3D_ZOOM_IN)
    public void zoomIn() {
        renderPanel.wheelForward();
    }

    @Action(key = _3D_ZOOM_OUT)
    public void zoomOut() {
        renderPanel.wheelBackward();
    }

    @Action(key = _3D_ZOOM_TOBOX)
    public void zoomToBox() {
        renderPanel.getInteractor().setStyleToZoom();
    }

    @Action(key = _3D_ZOOM_RESET)
    public void zoomReset() {
        renderPanel.zoomReset();
        view3D.notifyWidgets_ZoomReset();
    }

    @Action(key = _3D_VIEW_EDGES)
    public void setRepresentationToSurfaceWithEdges() {
        renderPanel.clearSelection();
        renderPanel.changeRepresentation(Representation.SURFACE_WITH_EDGES);
    }

    @Action(key = _3D_VIEW_PROFILE)
    public void setRepresentationToProfile() {
        renderPanel.clearSelection();
        renderPanel.changeRepresentation(Representation.PROFILE);
    }

    @Action(key = _3D_VIEW_SURFACE)
    public void setRepresentationToSurface() {
        renderPanel.clearSelection();
        renderPanel.changeRepresentation(Representation.SURFACE);
    }

    @Action(key = _3D_VIEW_WIREFRAME)
    public void setRepresentationToWireframe() {
        renderPanel.clearSelection();
        renderPanel.changeRepresentation(Representation.WIREFRAME);
    }

    @Action(key = _3D_VIEW_OUTLINE)
    public void setRepresentationToOutline() {
        renderPanel.clearSelection();
        renderPanel.changeRepresentation(Representation.OUTLINE);
    }

    @ActionToggle(key = _3D_VIEW_PROJECTIONS, normal = "perspective", selected = "parallel")
    public void setProjection(boolean parallel) {
        if (parallel) {
            renderPanel.ParallelProjectionOn();
        } else {
            renderPanel.ParallelProjectionOff();
        }
    }

//    @ActionToggle(key = _3D_LOCATION, normal = "off", selected = "on")
//    public void showLocation(boolean on) {
//        if (on) {
//            view3D.activateLocation(EventActionType.SHOW);
//        } else {
//            view3D.activateLocation(EventActionType.HIDE);
//        }
//    }
//
//    @ActionToggle(key = _3D_COR, normal = "off", selected = "on")
//    public void showCOR(boolean on) {
//        if (on) {
//            view3D.activateCOR(EventActionType.SHOW);
//        } else {
//            view3D.activateCOR(EventActionType.HIDE);
//        }
//    }
}
