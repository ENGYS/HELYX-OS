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


package eu.engys.core.controller;

import java.io.File;
import java.util.List;

import eu.engys.core.project.Model;

public interface ScriptFactory {

    File getMeshScript(Model model);
    List<String> getDefaultMeshScript(Model model);

	File getCheckMeshScript(Model model);
	List<String> getDefaultCheckMeshScript(Model model);

	File getSolverScript(Model model);
	List<String> getDefaultSolverScript(Model model);

	File getInitialiseScript(Model model);
	List<String> getDefaultInitialiseScript(Model model);

	File getExtrudeScript(Model model);
	List<String> getDefaultExtrudeScript(Model model);

	void deleteMeshScripts(Model model);

	File getQueueDriver(Model model);
	List<String> getDefaultQueueDriver(Model model);

	File getQueueLauncher(Model model);
	List<String> getDefaultQueueLauncher(Model model);

	File getExportScript(Model model);
	List<String> getDefaultExportScript(Model model);

}
