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

package eu.engys.core.controller.actions;

import static eu.engys.core.OpenFOAMEnvironment.getEnvironment;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.Command;
import eu.engys.core.controller.Controller;
import eu.engys.core.controller.ScriptFactory;
import eu.engys.core.controller.StopOrKillCommandOS;
import eu.engys.core.executor.Executor;
import eu.engys.core.executor.ExecutorHook;
import eu.engys.core.executor.ExecutorListener.ExecutorState;
import eu.engys.core.executor.ExecutorMonitor;
import eu.engys.core.executor.TerminalExecutorMonitor;
import eu.engys.core.project.Model;
import eu.engys.core.project.SolverState;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.state.ServerState;

public class StandardRunCase extends RunCase {

    private static final Logger logger = LoggerFactory.getLogger(StandardRunCase.class);

    public StandardRunCase(Model model, Controller controller, ScriptFactory scriptFactory) {
        super(model, controller, scriptFactory);
    }

    @Override
    public void executeClient() {
        logger.debug("EXECUTE IN CLIENT");
        File logFile = Paths.get(model.getProject().getBaseDir().getAbsolutePath(), openFOAMProject.LOG, model.getSolverModel().getLogFile()).toFile();
        TerminalExecutorMonitor terminal = new TerminalExecutorMonitor(logFile);
        terminal.setStopCommand(new StopOrKillCommandOS(controller));
        
        ExecutorMonitor monitor = new ExecutorMonitor();
        monitor.addHook(ExecutorState.START, new StartHook());
        monitor.addHook(ExecutorState.RUNNING, new RunningHook());
        monitor.addHook(ExecutorState.FINISH, new FinishHook());
        monitor.addHook(ExecutorState.ERROR, new ErrorHook());

        ExecutorService service = Executor.newExecutor(ACTION_NAME);

        File baseDir = model.getProject().getBaseDir();

        this.executor = Executor.script(scriptFactory.getSolverScript(model)).description(ACTION_NAME).inFolder(baseDir).inTerminal(terminal).withMonitors(monitor).inService(service).env(getEnvironment(model, model.getSolverModel().getLogFile())).keepFileOnEnd();
        executor.exec();
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

    protected class FinishHook implements ExecutorHook {
        @Override
        public void run(ExecutorMonitor m) {
            model.getSolverModel().setServerState(new ServerState(Command.RUN_CASE, SolverState.FINISHED));
        }
    }

    protected class ErrorHook implements ExecutorHook {
        @Override
        public void run(ExecutorMonitor m) {
            model.getSolverModel().setServerState(new ServerState(Command.RUN_CASE, SolverState.ERROR));
        }
    }

}
