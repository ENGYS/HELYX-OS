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
		surface.setAppendRegionName(true);
		return surface;
	}

	@Override
	protected Surface loadBox(Dictionary g) {
		Surface box = super.loadBox(g);
		box.setAppendRegionName(true);
		return box;
	}

	@Override
	protected Cylinder loadCylinder(Dictionary g) {
		Cylinder cylinder = super.loadCylinder(g);
		cylinder.setAppendRegionName(true);
		return cylinder;
	}

	@Override
	protected Plane loadPlane(Dictionary g) {
		Plane plane = super.loadPlane(g);
		plane.setAppendRegionName(true);
		return plane;
	}

	@Override
	protected Ring loadRing(Dictionary g) {
		Ring ring = super.loadRing(g);
		ring.setAppendRegionName(true);
		return ring;
	}

	@Override
	protected Sphere loadSphere(Dictionary g) {
		Sphere sphere = super.loadSphere(g);
		sphere.setAppendRegionName(true);
		return sphere;
	}

	@Override
	protected Stl loadSTL(Dictionary g, Model model, ProgressMonitor monitor) {
		Stl stl = super.loadSTL(g, model, monitor);
		stl.setAppendRegionName(!stl.isSingleton());
		return stl;
	}
	
	@Override
	public Stl readSTL(File file, ProgressMonitor monitor) {
	    Stl stl = super.readSTL(file, monitor);
	    stl.setAppendRegionName(!stl.isSingleton());
	    return stl;
	}
	
}
