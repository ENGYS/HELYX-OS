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
import static eu.engys.core.controller.AbstractScriptFactory.SOLVER_PARALLEL;
import static eu.engys.core.controller.AbstractScriptFactory.SOLVER_SERIAL;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.Command;
import eu.engys.core.controller.Controller;
import eu.engys.core.controller.ScriptFactory;
import eu.engys.core.controller.StopOrKillCommandOS;
import eu.engys.core.controller.actions.RunCase;
import eu.engys.core.executor.Executor;
import eu.engys.core.executor.ExecutorHook;
import eu.engys.core.executor.ExecutorListener.ExecutorState;
import eu.engys.core.executor.ExecutorMonitor;
import eu.engys.core.executor.TerminalExecutorMonitor;
import eu.engys.core.project.Model;
import eu.engys.core.project.SolverState;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.state.ServerState;
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

public class ParallelWorksRunCase extends RunCase {

    private static final Logger logger = LoggerFactory.getLogger(ParallelWorksRunCase.class);
    private ProgressMonitor monitor;

    public ParallelWorksRunCase(Model model, Controller controller, ScriptFactory scriptFactory, ProgressMonitor monitor) {
        super(model, controller, scriptFactory);
        this.monitor = monitor;
    }

    @Override
    public void beforeExecute() {
        super.beforeExecute();
        String command = model.getProject().isSerial() ? SOLVER_SERIAL : SOLVER_PARALLEL;
        Map<String, String> environment = getEnvironment(model, model.getSolverModel().getLogFile());

        Util.setScriptStyle(ScriptStyle.LINUX);
        scriptFactory.getSolverScript(model);
        Util.initScriptStyle();

        ParallelWorksUploader uploader = new ParallelWorksUploader(monitor);
        uploader.uploadCloudResults(command, environment);
    }

    @Override
    public void executeClient() {
        logger.debug("EXECUTE ON PARALLEL WORKS");
        File baseDir = model.getProject().getBaseDir();
        File logFile = Paths.get(baseDir.getAbsolutePath(), openFOAMProject.LOG, model.getSolverModel().getLogFile()).toFile();

        ExecutorMonitor exeMonitor = new ExecutorMonitor();
        exeMonitor.addHook(ExecutorState.START, new StartHook());
        exeMonitor.addHook(ExecutorState.RUNNING, new RunningHook());
        exeMonitor.addHook(ExecutorState.ERROR, new ErrorHook());
        exeMonitor.addHook(ExecutorState.FINISH, new FinishHook());

        ExecutorService service = Executor.newExecutor(ACTION_NAME);

        String command = model.getProject().isSerial() ? SOLVER_SERIAL : SOLVER_PARALLEL;
        TerminalExecutorMonitor terminal = new ParallelWorksExecutorMonitor(controller.getTerminalManager(), logFile);
        terminal.setStopCommand(new StopOrKillCommandOS(controller));

        this.executor = new ParallelWorksExecutor(baseDir, command, logFile).description(ACTION_NAME).inFolder(baseDir).inTerminal(terminal).withMonitors(exeMonitor).inService(service).env(getEnvironment(model, model.getSolverModel().getLogFile()));
        executor.exec();
    }

    @Override
    public void kill() {
        super.kill();
        ParallelWorksClient.getInstance().cancelJob();
        ParallelWorksCleaner.cleanBaseDir(model.getProject().getBaseDir());
    }

    @Override
    public void stop() throws Exception {
        new ParallelWorksStopper(model, executor).stop();
    }

    private class StartHook implements ExecutorHook {
        @Override
        public void run(ExecutorMonitor m) {
            model.getSolverModel().setServerState(new ServerState(Command.RUN_CASE, SolverState.STARTED));
        }
    }

    private class RunningHook implements ExecutorHook {
        @Override
        public void run(ExecutorMonitor m) {
            model.getSolverModel().setServerState(new ServerState(Command.RUN_CASE, SolverState.RUNNING));
        }
    }

    private class FinishHook implements ExecutorHook {
        @Override
        public void run(ExecutorMonitor m) {
            model.getSolverModel().setServerState(new ServerState(Command.RUN_CASE, SolverState.FINISHED));
            if (ParallelWorksData.fromPrefences().isPullResults()) {
                new ParallelWorksDownloader(model, monitor).downloadCloudResults();
            } else {
                ParallelWorksCleaner.cleanBaseDir(model.getProject().getBaseDir());
            }
        }
    }

    private class ErrorHook implements ExecutorHook {
        @Override
        public void run(ExecutorMonitor m) {
            model.getSolverModel().setServerState(new ServerState(Command.RUN_CASE, SolverState.ERROR));
        }
    }

}
