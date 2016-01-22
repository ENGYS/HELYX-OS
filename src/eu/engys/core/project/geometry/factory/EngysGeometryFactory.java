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

package eu.engys.core.project.geometry.factory;

import java.io.File;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.surface.Cylinder;
import eu.engys.core.project.geometry.surface.Plane;
import eu.engys.core.project.geometry.surface.Ring;
import eu.engys.core.project.geometry.surface.Sphere;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.util.progress.ProgressMonitor;

public class EngysGeometryFactory extends DefaultGeometryFactory {

	@Override
	public <S extends Surface> S newSurface(Class<S> type, String name) {
		S surface = super.newSurface(type, name);
		setAppendRegionName(surface, true);
		return surface;
	}

	public void setAppendRegionName(Surface surface, boolean b) {
	    if (surface.getGeometryDictionary() != null) {
	        surface.getGeometryDictionary().add("appendRegionName", Boolean.toString(b));
	    }
	}

	@Override
	protected Surface loadBox(Dictionary g) {
		Surface box = super.loadBox(g);
		setAppendRegionName(box, true);
		return box;
	}

	@Override
	protected Cylinder loadCylinder(Dictionary g) {
		Cylinder cylinder = super.loadCylinder(g);
		setAppendRegionName(cylinder, true);
		return cylinder;
	}

	@Override
	protected Plane loadPlane(Dictionary g) {
		Plane plane = super.loadPlane(g);
		setAppendRegionName(plane, true);
		return plane;
	}

	@Override
	protected Ring loadRing(Dictionary g) {
		Ring ring = super.loadRing(g);
		setAppendRegionName(ring, true);
		return ring;
	}

	@Override
	protected Sphere loadSphere(Dictionary g) {
		Sphere sphere = super.loadSphere(g);
		setAppendRegionName(sphere, true);
		return sphere;
	}

	@Override
	protected Stl loadSTL(Dictionary g, Model model, ProgressMonitor monitor) {
		Stl stl = super.loadSTL(g, model, monitor);
		setAppendRegionName(stl, !stl.isSingleton());
		return stl;
	}
	
	@Override
	public Stl readSTL(File file, ProgressMonitor monitor) {
	    Stl stl = super.readSTL(file, monitor);
	    setAppendRegionName(stl, !stl.isSingleton());
	    return stl;
	}
	
}
