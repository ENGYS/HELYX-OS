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
package eu.engys.gui.solver.postprocessing.parsers;

import java.io.File;
import java.nio.file.Paths;

import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.util.IOUtils;

public class ResidualsUtils {

    public static void clearLogFile(Model model) {
        File logFile = fileToParse(model);
        if (logFile != null && logFile.exists()) {
            IOUtils.clearFile(logFile);
        }
    }

    public static File fileToParse(Model model) {
        String logFile = model.getSolverModel().getLogFile();
        if (logFile.isEmpty()) {
            logFile = guessLogFile(model);
        }
        return Paths.get(model.getProject().getBaseDir().getAbsolutePath(), openFOAMProject.LOG, logFile).toFile();

    }

    private static String guessLogFile(Model model) {
        String application = model.getState().getSolver().getName();
        if (application != null && !application.isEmpty()) {
            return application + ".log";
        }
        return "none.log";
    }

}
