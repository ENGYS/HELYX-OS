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


package eu.engys.core.project.geometry.stl;

import java.io.File;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.constant.TriSurfaceFolder;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.util.progress.ProgressMonitor;

public class STLManager {

	public static final String COPY_OF_PREFIX = "CopyOf";

	private static final Logger logger = LoggerFactory.getLogger(STLManager.class);

	private ProgressMonitor monitor;
	private TriSurfaceFolder triSurface;
	private Model model;

	@Inject
	public STLManager(Model model, ProgressMonitor monitor) {
		this.monitor = monitor;
		this.model = model;
		this.triSurface = model.getProject().getConstantFolder().getTriSurface();
	}

	private Stl loadFromTriSurface(String fileName) {
		Dictionary g = new Dictionary(fileName, Surface.stl);
		g.setName(fileName);
		g.add("name", FilenameUtils.removeExtension(fileName));

		Stl stl = (Stl) model.getGeometry().getFactory().loadSurface(g, model, monitor);
		return stl;
	}

	public Stl copyAndLoadFile(File file, String name, boolean overwrite) {
		triSurface.getFileManager().copyHere(file, name, overwrite);
		return loadFromTriSurface(name);
	}


}
