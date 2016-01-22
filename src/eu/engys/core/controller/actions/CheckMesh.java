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
import static eu.engys.core.project.openFOAMProject.LOG;

import java.io.File;
import java.nio.file.Paths;

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
import eu.engys.util.IOUtils;

public class CheckMesh extends AbstractRunCommand {

    public static final String ACTION_NAME = "Check Mesh";
    public static final String LOG_NAME = "checkMesh.log";

    private File logFile;
    private ScriptFactory scriptFactory;

    public CheckMesh(Model model, Controller controller, ScriptFactory scriptFactory) {
        super(model, controller);
        this.scriptFactory = scriptFactory;
        this.logFile = Paths.get(model.getProject().getBaseDir().getAbsolutePath(), LOG, LOG_NAME).toFile();
    }

    @Override
    public void beforeExecute() {
        IOUtils.clearFile(logFile);
        if (controller.getListener() != null) {
            controller.getListener().beforeCheckMesh();
        }
    }

    @Override
    public void executeClient() {
        ExecutorTerminal terminal = new TerminalExecutorMonitor(logFile);
        ExecutorMonitor monitor = new ExecutorMonitor();
        monitor.addHook(ExecutorState.FINISH, new FinishHook());

        this.executor = Executor.script(scriptFactory.getCheckMeshScript(model)).description(ACTION_NAME).inFolder(model.getProject().getBaseDir()).inTerminal(terminal).withMonitors(monitor).env(getEnvironment(model, LOG_NAME)).keepFileOnEnd();
        executor.exec();
    }

    private class FinishHook implements ExecutorHook {

        @Override
        public void run(ExecutorMonitor monitor) {
            if (controller.getListener() != null) {
                controller.reopenCase(OpenOptions.MESH_ONLY);
                controller.getListener().afterCheckMesh();
            }
        }
    }
}
