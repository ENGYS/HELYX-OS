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

import eu.engys.core.controller.Controller;
import eu.engys.core.controller.Controller.OpenOptions;
import eu.engys.core.controller.ScriptFactory;
import eu.engys.core.executor.Executor;
import eu.engys.core.executor.ExecutorHook;
import eu.engys.core.executor.ExecutorListener.ExecutorState;
import eu.engys.core.executor.ExecutorMonitor;
import eu.engys.core.executor.ExecutorTerminal;
import eu.engys.core.executor.TerminalExecutorMonitor;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;

public class StandardInitialiseFields extends InitialiseFields {

    private static final Logger logger = LoggerFactory.getLogger(StandardInitialiseFields.class);

    public StandardInitialiseFields(Model model, Controller controller, ScriptFactory scriptFactory) {
        super(model, controller, scriptFactory);
    }

    @Override
    public void executeClient() {
        logger.debug("EXECUTE IN CLIENT");
        File script = scriptFactory.getInitialiseScript(model);
        File baseDir = model.getProject().getBaseDir();
        File logFile = Paths.get(baseDir.getAbsolutePath(), openFOAMProject.LOG, LOG_NAME).toFile();

        ExecutorTerminal terminal = new TerminalExecutorMonitor(logFile);
        ExecutorMonitor monitor = new ExecutorMonitor();
        ExecutorService service = Executor.newExecutor(ACTION_NAME);
        monitor.addHook(ExecutorState.FINISH, new FinishHook());

        this.executor = Executor.script(script).description(ACTION_NAME).inFolder(baseDir).inTerminal(terminal).withMonitors(monitor).inService(service).env(getEnvironment(model, LOG_NAME)).keepFileOnEnd();
        executor.exec();
    }

    private class FinishHook implements ExecutorHook {
        @Override
        public void run(ExecutorMonitor m) {
            controller.reopenCase(OpenOptions.CURRENT_SETTINGS);
        }
    }

}
