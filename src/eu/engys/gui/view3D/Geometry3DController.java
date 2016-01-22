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


package eu.engys.gui.view3D;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;

import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.core.project.mesh.FieldItem;

public interface Geometry3DController extends Controller3D {

	public void updateSurfacesSelection(Surface... selection);

	void updateSurfaceVisibility(Surface... selection);

	void updateSurfaceColor(Color color, Surface... selection);

	void addSurfaces(Surface... surface);

	void transformSurfaces(AffineTransform t, boolean save, Surface... surfaces);

	void changeSurface(Surface... surface);

	void removeSurfaces(Surface... surfaces);

	public BoundingBox computeBoundingBox(Surface... surfaces);

	void clear();

	Collection<Actor> getActorsList();

	public Map<Surface, Actor> getActorsMap();

	public void showInternalMesh();

	public void hideInternalMesh();

    public void showField(FieldItem fieldItem);


}
