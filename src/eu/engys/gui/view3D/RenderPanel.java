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
package eu.engys.gui.view3D;

import java.awt.Color;
import java.awt.event.KeyListener;

import eu.engys.gui.view3D.CameraManager.Position;
import vtk.vtkAssembly;
import vtk.vtkImageData;

public interface RenderPanel {

	void lock();

	void Render();

	void unlock();

	void clear();

	void setCameraPosition(Position xPos);
	void resetCamera();

	void wheelForward();
	void wheelBackward();

	void zoomReset();
	void resetZoomLater();
	void resetZoomAndWait();

	void clearSelection();

	void setRepresentation(Representation r);
	Representation getRepresentation(); 
	
	void changeRepresentation(Representation r);

	void renderLater();
	void renderAndWait();

	void addActor(vtkAssembly cor);
	void addActor(Actor actor);

	void removeActor(Actor actor);
	void removeActor(vtkAssembly cor);

	void filterActors(Actor... actors);
	void selectActors(boolean keepSelected, Actor... actors);

	void setLowRendering();
	void setHighRendering();
	void DestroyTimer();

	void setActorColor(Color c, Actor... actor);

    void addKeyListener(KeyListener listener);

    void removeKeyListener(KeyListener listener);

	void dispose();

	void ParallelProjectionOn();
	void ParallelProjectionOff();

    PickManager getPickManager();
    
    CameraManager getCameraManager();

    Interactor getInteractor();

    vtkImageData toImageData();

    void lowRenderingOff();
    void lowRenderingOn();
}
