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
package eu.engys.core.controller.actions;

import static eu.engys.core.OpenFOAMEnvironment.getEnvironment;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.Controller;
import eu.engys.core.controller.KillCommandOS;
import eu.engys.core.controller.ScriptFactory;
import eu.engys.core.executor.Executor;
import eu.engys.core.executor.ExecutorHook;
import eu.engys.core.executor.ExecutorListener.ExecutorState;
import eu.engys.core.executor.ExecutorMonitor;
import eu.engys.core.executor.TerminalExecutorMonitor;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;

public class StandardRunMesh extends RunMesh {

    private static final Logger logger = LoggerFactory.getLogger(StandardRunMesh.class);

    public StandardRunMesh(Model model, Controller controller, ScriptFactory scriptFactory) {
        super(model, controller, scriptFactory);
    }

    @Override
    public void executeClient() {
        logger.debug("EXECUTE IN CLIENT");
        File baseDir = model.getProject().getBaseDir();
        File logFile = Paths.get(baseDir.getAbsolutePath(), openFOAMProject.LOG, LOG_NAME).toFile();

        ExecutorMonitor monitor = new ExecutorMonitor();
        monitor.addHook(ExecutorState.FINISH, new FinishHook());

        ExecutorService service = Executor.newExecutor(ACTION_NAME);

        TerminalExecutorMonitor terminal = new TerminalExecutorMonitor(controller.getTerminalManager(), logFile);
        terminal.setStopCommand(new KillCommandOS(controller));

        this.executor = Executor.script(scriptFactory.getMeshScript(model)).description(ACTION_NAME).inFolder(baseDir).inService(service).inTerminal(terminal).withMonitors(monitor).env(getEnvironment(model, LOG_NAME)).keepFileOnEnd();
        executor.exec();
    }

    private class FinishHook implements ExecutorHook {
        @Override
        public void run(ExecutorMonitor m) {
            controller.setupCase();
        }
    }

}
