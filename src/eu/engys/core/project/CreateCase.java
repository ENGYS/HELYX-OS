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

package eu.engys.core.project;

import static eu.engys.core.project.system.DecomposeParDict.HIERARCHICAL_COEFFS_KEY;
import static eu.engys.core.project.system.DecomposeParDict.NUMBER_OF_SUBDOMAINS_KEY;
import static eu.engys.core.project.system.DecomposeParDict.N_KEY;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FileUtils;

import eu.engys.core.controller.AbstractScriptFactory;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.project.constant.ConstantFolder;
import eu.engys.core.project.defaults.Defaults;
import eu.engys.core.project.system.SystemFolder;
import eu.engys.util.Util;
import eu.engys.util.progress.ProgressMonitor;

public class CreateCase {

    public static final int OK = 0;
    public static final int CANCEL = 1;

    private ProgressMonitor monitor;
    private Defaults defaults;

    public CreateCase(Defaults defaults, ProgressMonitor monitor) {
        this.defaults = defaults;
        this.monitor = monitor;
    }

    public openFOAMProject create(CaseParameters params) {
        File baseDir = params.getBaseDir();
        boolean isParallel = params.isParallel();
        int nProcessors = params.getnProcessors();
        int[] nHierarchy = params.getnHierarchy();

        if (baseDir.exists()) {
            deleteAll(baseDir, isParallel, nProcessors);
        } else {
            baseDir.mkdirs();
        }

        openFOAMProject prj = isParallel ? openFOAMProject.newParallelProject(baseDir, nProcessors) : openFOAMProject.newSerialProject(baseDir);

        SystemFolder systemFolder = prj.getSystemFolder();
        try {
            systemFolder.setBlockMeshDict(defaults.getDefaultBlockMeshDict());
            systemFolder.setSnappyHexMeshDict(defaults.getDefaultSnappyHexMeshDict());
            systemFolder.setControlDict(defaults.getDefaultControlDict());
            systemFolder.setFvSchemes(defaults.getDefaultFvSchemes());
            systemFolder.setFvSolution(defaults.getDefaultFvSolution());
            systemFolder.setFvOptions(defaults.getDefaultFvOptions());
            // systemFolder.setRunDict(defaults.getDefaultRunDict());
            systemFolder.setMapFieldsDict(defaults.getDefaultMapFieldsDict());
            systemFolder.setDecomposeParDict(defaults.getDefaultDecomposeParDict());
            systemFolder.setCustomNodeDict(defaults.getDefaultCustomNodeDict());
            systemFolder.getDecomposeParDict().add(NUMBER_OF_SUBDOMAINS_KEY, Integer.toString(nProcessors));
            if (systemFolder.getDecomposeParDict().found(HIERARCHICAL_COEFFS_KEY)) {
                String x = Integer.toString(nHierarchy[0]);
                String y = Integer.toString(nHierarchy[1]);
                String z = Integer.toString(nHierarchy[2]);
                // Y X Z
                systemFolder.getDecomposeParDict().subDict(HIERARCHICAL_COEFFS_KEY).add(N_KEY, "(" + y + " " + x + " " + z + ")");
            }
        } catch (DictionaryException e) {
            e.printStackTrace();
            monitor.error(e.getMessage());
        }

        return prj;
    }

    public static void deleteAll(File baseDir, boolean isParallel, int nProcessors) {
        deleteFile(baseDir, ConstantFolder.CONSTANT);
        deleteFile(baseDir, SystemFolder.SYSTEM);
        deleteFile(baseDir, "0");
        deleteFile(baseDir, openFOAMProject.LOG);
        deleteFile(baseDir, openFOAMProject.POST_PROC);
        deleteFile(baseDir, openFOAMProject.HOSTFILE);
        deleteFile(baseDir, openFOAMProject.MACHINEFILE);

        if (isParallel) {
            for (File processorDir : baseDir.listFiles(new ProcessorDirectoryFileFilter())) {
                FileUtils.deleteQuietly(processorDir);
            }
        }

        if (Util.isWindows()) {
            deleteFile(baseDir, AbstractScriptFactory.MESH_SERIAL_BAT);
            deleteFile(baseDir, AbstractScriptFactory.MESH_PARALLEL_BAT);

            deleteFile(baseDir, AbstractScriptFactory.CHECK_MESH_SERIAL_BAT);
            deleteFile(baseDir, AbstractScriptFactory.CHECK_MESH_PARALLEL_BAT);

            deleteFile(baseDir, AbstractScriptFactory.SOLVER_SERIAL_BAT);
            deleteFile(baseDir, AbstractScriptFactory.SOLVER_PARALLEL_BAT);

            deleteFile(baseDir, AbstractScriptFactory.INITIALISE_FIELDS_SERIAL_BAT);
            deleteFile(baseDir, AbstractScriptFactory.INITIALISE_FIELDS_PARALLEL_BAT);

        } else {
            deleteFile(baseDir, AbstractScriptFactory.MESH_SERIAL_RUN);
            deleteFile(baseDir, AbstractScriptFactory.MESH_PARALLEL_RUN);

            deleteFile(baseDir, AbstractScriptFactory.CHECK_MESH_SERIAL_RUN);
            deleteFile(baseDir, AbstractScriptFactory.CHECK_MESH_PARALLEL_RUN);

            deleteFile(baseDir, AbstractScriptFactory.SOLVER_SERIAL_RUN);
            deleteFile(baseDir, AbstractScriptFactory.SOLVER_PARALLEL_RUN);

            deleteFile(baseDir, AbstractScriptFactory.INITIALISE_FIELDS_SERIAL_RUN);
            deleteFile(baseDir, AbstractScriptFactory.INITIALISE_FIELDS_PARALLEL_RUN);

        }

    }

    private static void deleteFile(File baseDir, String name) {
        File file = new File(baseDir, name);
        if (file.exists())
            FileUtils.deleteQuietly(file);
    }

    private static class ProcessorDirectoryFileFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            boolean isDir = pathname.isDirectory();
            boolean isProcessor = pathname.getName().startsWith("processor");
            return isDir && isProcessor;
        }
    }
}
