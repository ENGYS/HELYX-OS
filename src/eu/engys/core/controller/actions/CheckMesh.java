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
import static eu.engys.core.project.openFOAMProject.LOG;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

import eu.engys.core.controller.Controller;
import eu.engys.core.controller.ScriptFactory;
import eu.engys.core.executor.Executor;
import eu.engys.core.executor.ExecutorHook;
import eu.engys.core.executor.ExecutorListener.ExecutorState;
import eu.engys.core.executor.ExecutorMonitor;
import eu.engys.core.executor.TerminalExecutorMonitor;
import eu.engys.core.project.Model;
import eu.engys.core.project.mesh.MeshInfoReader;
import eu.engys.core.project.mesh.MeshInfoWriter;
import eu.engys.util.IOUtils;

public class CheckMesh extends AbstractRunCommand {

    public static final String ACTION_NAME = "Check Mesh";
    public static final String LOG_NAME = "checkMesh.log";

    private ScriptFactory scriptFactory;
    private String logName;
    private String actionName;
    private File logFile;

    public CheckMesh(Model model, Controller controller, ScriptFactory scriptFactory) {
        this(model, controller, scriptFactory, LOG_NAME, ACTION_NAME);
    }

    public CheckMesh(Model model, Controller controller, ScriptFactory scriptFactory, String logName, String actionName) {
        super(model, controller);
        this.scriptFactory = scriptFactory;
        this.logName = logName;
        this.actionName = actionName;
        this.logFile = Paths.get(model.getProject().getBaseDir().getAbsolutePath(), LOG, logName).toFile();
    }

    @Override
    public void beforeExecute() {
        IOUtils.clearFile(logFile);
    }

    @Override
    public void executeClient() {
        if (terminal == null) {
            this.terminal = new TerminalExecutorMonitor(controller.getTerminalManager(), logFile);
        }
        if (service == null) {
            this.service = Executors.newSingleThreadExecutor();
        }
        
        ExecutorMonitor monitor = new ExecutorMonitor();
        monitor.addHook(ExecutorState.FINISH, new FinishHook());

        this.executor = Executor.script(scriptFactory.getCheckMeshScript(model)).description(actionName).inFolder(model.getProject().getBaseDir()).inTerminal(terminal).withMonitors(monitor).inService(service).env(getEnvironment(model, logName)).keepFileOnEnd();
        executor.exec();
    }

    private class FinishHook implements ExecutorHook {
        @Override
        public void run(ExecutorMonitor monitor) {
            if (controller.getListener() != null) {
                new MeshInfoReader(model).read(logFile);
                new MeshInfoWriter(model).write();
                model.getProject().getSystemFolder().writeProjectDict(null);
                controller.getListener().afterCheckMesh();
            }
        }
    }
}
