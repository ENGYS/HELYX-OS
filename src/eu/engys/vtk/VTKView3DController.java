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
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.gui.view3D.CameraManager.Position;
import eu.engys.gui.view3D.Representation;

public class VTKView3DController implements ActionContainer {

	private VTKRenderPanel vtkRendererPanel;
	private VTKView3D view3D;

	public VTKView3DController(VTKView3D vtkView3D) {
		this.view3D = vtkView3D;
		this.vtkRendererPanel = vtkView3D.getVTKRendererPanel();
		ActionManager.getInstance().parseActions(this);
	}

	@Override
	public boolean isDemo() {
	    return false;
	}
	
	@Action(key=_3D_LOAD_MESH)
	public void loadMesh() {
	    view3D.getMeshController().showExternalMesh();
	    view3D.load();
	    view3D.start(null);
	}

	@Action(key=_3D_AXIS_XPOS)
	public void viewXPos() {
	    vtkRendererPanel.setCameraPosition(Position.X_POS);
	}

	@Action(key=_3D_AXIS_XNEG)
	public void viewXNeg() {
		vtkRendererPanel.setCameraPosition(Position.X_NEG);
	}

	@Action(key=_3D_AXIS_YPOS)
	public void viewYPos() {
		vtkRendererPanel.setCameraPosition(Position.Y_POS);
	}

	@Action(key=_3D_AXIS_YNEG)
	public void viewYNeg() {
		vtkRendererPanel.setCameraPosition(Position.Y_NEG);
	}

	@Action(key=_3D_AXIS_ZPOS)
	public void viewZPos() {
		vtkRendererPanel.setCameraPosition(Position.Z_POS);
	}

	@Action(key=_3D_AXIS_ZNEG)
	public void viewZNeg() {
		vtkRendererPanel.setCameraPosition(Position.Z_NEG);
	}

	@Action(key=_3D_ZOOM_IN)
	public void zoomIn() {
		vtkRendererPanel.wheelForward();
	}

	@Action(key=_3D_ZOOM_OUT)
	public void zoomOut() {
		vtkRendererPanel.wheelBackward();
	}

	@Action(key=_3D_ZOOM_TOBOX)
	public void zoomToBox() {
	    vtkRendererPanel.getInteractor().setStyleToZoom();
	}

	@Action(key=_3D_ZOOM_RESET)
	public void zoomReset() {
		vtkRendererPanel.zoomReset();
	}

	@Action(key=_3D_VIEW_EDGES)
	public void setRepresentationToSurfaceWithEdges() {
		vtkRendererPanel.clearSelection();
		vtkRendererPanel.changeRepresentation(Representation.SURFACE_WITH_EDGES);
	}

	@Action(key=_3D_VIEW_PROFILE)
	public void setRepresentationToProfile() {
	    vtkRendererPanel.clearSelection();
	    vtkRendererPanel.changeRepresentation(Representation.PROFILE);
	}

	@Action(key=_3D_VIEW_SURFACE)
	public void setRepresentationToSurface() {
		vtkRendererPanel.clearSelection();
		vtkRendererPanel.changeRepresentation(Representation.SURFACE);
	}

	@Action(key=_3D_VIEW_WIREFRAME)
	public void setRepresentationToWireframe() {
		vtkRendererPanel.clearSelection();
		vtkRendererPanel.changeRepresentation(Representation.WIREFRAME);
	}

	@Action(key=_3D_VIEW_OUTLINE)
	public void setRepresentationToOutline() {
		vtkRendererPanel.clearSelection();
		vtkRendererPanel.changeRepresentation(Representation.OUTLINE);
	}

	@ActionToggle(key=_3D_VIEW_PROJECTIONS, normal="perspective", selected="parallel")
	public void setProjection(boolean parallel) {
		if (parallel) {
			vtkRendererPanel.ParallelProjectionOn();
		} else {
			vtkRendererPanel.ParallelProjectionOff();
		}
	}

	public void showScalarsForField(FieldItem fieldItem) {
		view3D.getMeshController().showField(fieldItem);
		view3D.getGeometryController().showField(fieldItem);
		view3D.updateWidgets_fieldChanged();
	}

	public void showTimeStep(double time) {
		view3D.getMeshController().showTimeStep(time);
		view3D.updateWidgets_timeStepChanged();
	}

	public void readTimeSteps() {
		view3D.getMeshController().readTimeSteps();
		view3D.updateWidgets_newTimeStep();
	}
}
