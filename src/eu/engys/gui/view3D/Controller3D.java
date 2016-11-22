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

import java.util.Collection;

import eu.engys.core.controller.GeometryToMesh;
import eu.engys.gui.view.View3DElement;

public interface Controller3D {

	void clearContext();
	void newContext(Class<? extends View3DElement> klass);
	void newEmptyContext(Class<? extends View3DElement> klass);
	void dumpContext(Class<? extends View3DElement> klass);
	void applyContext(Class<? extends View3DElement> klass);
	
	Context getCurrentContext();
	
	void loadActors();
	void clear();
	
	Collection<Actor> getActorsList();
	
	void geometryToMesh(GeometryToMesh g2m);

    void zoomReset();
    void render();
    
    void setRenderPanel(RenderPanel renderPanel);
}
