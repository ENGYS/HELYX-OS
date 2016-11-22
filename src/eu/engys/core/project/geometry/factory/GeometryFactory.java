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
import eu.engys.core.project.geometry.FeatureLine;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.core.project.geometry.surface.StlArea;
import eu.engys.util.progress.ProgressMonitor;

public interface GeometryFactory {

	Surface loadSurface(Dictionary g, Model model, ProgressMonitor monitor);

	void writeSurface(Surface surface, Model model, ProgressMonitor monitor);

	<S extends Surface> S newSurface(Class<S> type, String name);

	Stl readSTL(File file, ProgressMonitor monitor);
	StlArea readSTLArea(File file, ProgressMonitor monitor);

    FeatureLine readLine(File file);

    void deleteSurface(Model model, Surface surface);

}
