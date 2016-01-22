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

import java.awt.Color;
import java.awt.event.KeyListener;

import vtk.vtkAssembly;
import vtk.vtkImageData;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.CameraManager.Position;
import eu.engys.gui.view3D.Interactor;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.gui.view3D.Representation;

public class RenderPanelAdapter implements RenderPanel {

	@Override
	public void lock() {
	}

	@Override
	public void Render() {
	}

	@Override
	public void unlock() {
	}

	@Override
	public void clear() {
	}

	@Override
	public void setCameraPosition(Position xPos) {
	}

	@Override
	public void resetCamera() {
	}

	@Override
	public void wheelForward() {
	}

	@Override
	public void wheelBackward() {
	}

	@Override
	public void zoomReset() {
	}

	@Override
	public void resetZoomLater() {
	}

	@Override
	public void resetZoomAndWait() {
	}

	@Override
	public void clearSelection() {
	}

	@Override
	public void setRepresentation(Representation r) {
	}

	@Override
	public Representation getRepresentation() {
		return null;
	}

	@Override
	public void changeRepresentation(Representation r) {
	}

	@Override
	public void renderLater() {
	}

	@Override
	public void renderAndWait() {
	}

	@Override
	public void addActor(vtkAssembly cor) {
	}

	@Override
	public void addActor(Actor actor) {
	}

	@Override
	public void removeActor(Actor actor) {
	}

	@Override
	public void selectActors(boolean keep, Actor... pickedActor) {
	}

	@Override
	public void setLowRendering() {

	}

	@Override
	public void setHighRendering() {
	}

	@Override
	public void setActorColor(Color c, Actor... actor) {
	}

	@Override
	public void addKeyListener(KeyListener listener) {
	}

	@Override
	public void removeKeyListener(KeyListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void ParallelProjectionOn() {
	}

	@Override
	public void ParallelProjectionOff() {
	}
	
	@Override
	public VTKPickManager getPickManager() {
	    return null;
	}
	
	@Override
	public Interactor getInteractor() {
	    return null;
	}
	
	@Override
	public vtkImageData toImageData() {
	    return null;
	}
	@Override
	public void lowRenderingOff() {
	}
	@Override
	public void lowRenderingOn() {
	}
}
