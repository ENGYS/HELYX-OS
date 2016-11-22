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
package eu.engys.parallelworks.actions;

import static eu.engys.core.OpenFOAMEnvironment.getEnvironment;
import static eu.engys.core.controller.AbstractScriptFactory.MESH_PARALLEL;
import static eu.engys.core.controller.AbstractScriptFactory.MESH_SERIAL;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.Controller;
import eu.engys.core.controller.KillCommandOS;
import eu.engys.core.controller.ScriptFactory;
import eu.engys.core.controller.actions.RunMesh;
import eu.engys.core.executor.Executor;
import eu.engys.core.executor.ExecutorHook;
import eu.engys.core.executor.ExecutorListener.ExecutorState;
import eu.engys.core.executor.ExecutorMonitor;
import eu.engys.core.executor.TerminalExecutorMonitor;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.parallelworks.ParallelWorksCleaner;
import eu.engys.parallelworks.ParallelWorksClient;
import eu.engys.parallelworks.ParallelWorksData;
import eu.engys.parallelworks.ParallelWorksDownloader;
import eu.engys.parallelworks.ParallelWorksExecutor;
import eu.engys.parallelworks.ParallelWorksExecutorMonitor;
import eu.engys.parallelworks.ParallelWorksUploader;
import eu.engys.util.Util;
import eu.engys.util.Util.ScriptStyle;
import eu.engys.util.progress.ProgressMonitor;

public class ParallelWorksRunMesh extends RunMesh {

    private static final Logger logger = LoggerFactory.getLogger(ParallelWorksRunMesh.class);
    private ProgressMonitor monitor;

    public ParallelWorksRunMesh(Model model, Controller controller, ScriptFactory scriptFactory, ProgressMonitor monitor) {
        super(model, controller, scriptFactory);
        this.monitor = monitor;
    }

    @Override
    public void beforeExecute() {
        super.beforeExecute();

        String command = model.getProject().isSerial() ? MESH_SERIAL : MESH_PARALLEL;
        Map<String, String> environment = getEnvironment(model, LOG_NAME);

        Util.setScriptStyle(ScriptStyle.LINUX);
        scriptFactory.getMeshScript(model);
        Util.initScriptStyle();

        ParallelWorksUploader uploader = new ParallelWorksUploader(monitor);
        uploader.uploadCloudResults(command, environment);
    }

    @Override
    public void executeClient() {
        logger.debug("EXECUTE ON PARALLEL WORKS");
        File baseDir = model.getProject().getBaseDir();
        File logFile = Paths.get(baseDir.getAbsolutePath(), openFOAMProject.LOG, LOG_NAME).toFile();

        ExecutorMonitor exeMonitor = new ExecutorMonitor();
        exeMonitor.addHook(ExecutorState.FINISH, new FinishHook());

        ExecutorService service = Executor.newExecutor(ACTION_NAME);

        String command = model.getProject().isSerial() ? MESH_SERIAL : MESH_PARALLEL;
        TerminalExecutorMonitor terminal = new ParallelWorksExecutorMonitor(controller.getTerminalManager(), logFile);
        terminal.setStopCommand(new KillCommandOS(controller));

        this.executor = new ParallelWorksExecutor(baseDir, command, logFile).description(ACTION_NAME).inFolder(baseDir).inTerminal(terminal).withMonitors(exeMonitor).inService(service).env(getEnvironment(model, LOG_NAME));
        executor.exec();
    }

    @Override
    public void kill() {
        super.kill();
        ParallelWorksClient.getInstance().cancelJob();
        ParallelWorksCleaner.cleanBaseDir(model.getProject().getBaseDir());
    }

    private class FinishHook implements ExecutorHook {
        @Override
        public void run(ExecutorMonitor m) {
            if (ParallelWorksData.fromPrefences().isPullResults()) {
                new ParallelWorksDownloader(model, monitor).downloadCloudResults();
                controller.setupCase();
            } else {
                ParallelWorksCleaner.cleanBaseDir(model.getProject().getBaseDir());
            }
        }
    }

}
