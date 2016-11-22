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
package eu.engys.core.project;

import java.io.File;

import eu.engys.core.project.constant.ConstantFolder;
import eu.engys.core.project.defaults.Defaults;
import eu.engys.core.project.system.SystemFolder;
import eu.engys.core.project.zero.ZeroFolder;
import eu.engys.util.progress.SilentMonitor;

public class openFOAMProject {

    public static final String LOG = "log";
    public static final String HOSTFILE = "hostfile";
    public static final String POST_PROC = "postProcessing";
    public static final String MACHINEFILE = "machinefile";

    private final File baseDir;
    private final boolean parallel;
    private final int processors;

    private final SystemFolder system;
    private final ConstantFolder constant;
    private final ZeroFolder zero;

    public static openFOAMProject createProject(CaseParameters parameters) {
        return parameters.isParallel() ? openFOAMProject.newParallelProject(parameters.getBaseDir(), parameters.getnProcessors()) : openFOAMProject.newSerialProject(parameters.getBaseDir());
    }

    public static openFOAMProject newSerialProject(File baseDir) {
        return new openFOAMProject(baseDir, false, -1);
    }

    public static openFOAMProject newDefaultSerialProject(File baseDir, Defaults defaults) {
        CreateCase createCase = new CreateCase(defaults, new SilentMonitor());
        CaseParameters caseParams = new CaseParameters();
        caseParams.setParallel(false);
        caseParams.setnHierarchy(new int[] { 1, 1, 1 });
        caseParams.setnProcessors(1);
        caseParams.setBaseDir(baseDir);
        return createCase.create(caseParams);
    }

    public static openFOAMProject newParallelProject(File baseDir) {
        return new openFOAMProject(baseDir, true, new ProjectFolderAnalyzer(baseDir, null).findProcessorsFolders());
    }

    public static openFOAMProject newParallelProject(File baseDir, int nProcessors) {
        return new openFOAMProject(baseDir, true, nProcessors);
    }

    public static openFOAMProject newCopy(openFOAMProject project) {
        return new openFOAMProject(project);
    }

    public static openFOAMProject newCopy(File baseDir, openFOAMProject project) {
        return new openFOAMProject(baseDir, project);
    }

    public openFOAMProject(String path) {
        this.baseDir = new File(path);
        this.parallel = false;
        this.processors = 2;
        this.system = new SystemFolder(this);
        this.constant = new ConstantFolder(this);
        this.zero = new ZeroFolder(this);
    }

    private openFOAMProject(File baseDir, boolean parallel, int processors) {
        this.baseDir = baseDir;
        this.parallel = parallel;
        this.processors = processors;

        this.system = new SystemFolder(this);
        this.constant = new ConstantFolder(this);
        this.zero = new ZeroFolder(this);
    }

    private openFOAMProject(File baseDir, openFOAMProject prj) {
        this.baseDir = baseDir;
        this.parallel = prj.parallel;
        this.processors = prj.processors;

        this.system = new SystemFolder(baseDir, prj.getSystemFolder());
        this.constant = new ConstantFolder(baseDir, prj.getConstantFolder());
        this.zero = new ZeroFolder(baseDir, prj.getZeroFolder());
    }

    private openFOAMProject(openFOAMProject prj) {
        this.baseDir = prj.baseDir;
        this.parallel = prj.parallel;
        this.processors = prj.processors;

        this.system = new SystemFolder(baseDir, prj.getSystemFolder());
        this.constant = new ConstantFolder(baseDir, prj.getConstantFolder());
        this.zero = new ZeroFolder(baseDir, prj.getZeroFolder());
    }

    public File getBaseDir() {
        return baseDir;
    }

    public boolean isParallel() {
        return parallel;
    }

    public boolean isSerial() {
        return !isParallel();
    }

    public boolean isMeshOnZero() {
        ProjectFolderAnalyzer analyzer = new ProjectFolderAnalyzer(getBaseDir(), null).checkSerialOrParallel();
        return analyzer.isParallel_Zero() || analyzer.isSerial_Zero();
    }

    public int getProcessors() {
        return processors;
    }

    public ConstantFolder getConstantFolder() {
        return constant;
    }

    public SystemFolder getSystemFolder() {
        return system;
    }

    public ZeroFolder getZeroFolder() {
        return zero;
    }

    @Override
    public String toString() {
        return "PROJECT [ basedir: " + baseDir + " ] - [ parallel: " + parallel + " ] - [ processors: " + processors + "]";
    }
}
