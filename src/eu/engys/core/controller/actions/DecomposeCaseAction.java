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

import static eu.engys.core.controller.actions.DecomposeCase.ACTION_NAME;
import static eu.engys.core.project.openFOAMProject.LOG;

import java.io.File;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.engys.core.controller.Controller;
import eu.engys.core.executor.TerminalExecutorMonitor;
import eu.engys.core.project.Model;
import eu.engys.util.IOUtils;

public class DecomposeCaseAction extends AbstractRunCommand {

    public static final String LOG_NAME = "decomposeCase.log";

    private Model model;
    private Controller controller;

    private boolean shouldUseWithZeroFlag;

    public DecomposeCaseAction(Model model, Controller controller, boolean shouldUseWithZeroFlag) {
        super(model, controller);
        this.model = model;
        this.controller = controller;
        this.shouldUseWithZeroFlag = shouldUseWithZeroFlag;
    }

    @Override
    public void executeClient() {
        DecomposeCasePanel panel = new DecomposeCasePanel(model);
        panel.showDialog();
        if (panel.getStatus().isOK()) {
            controller.saveCase(model.getProject().getBaseDir());
            decompose(panel.getTimeSteps());
        }
    }

    private void decompose(Set<String> timeSteps) {
        if (model.getProject().isParallel()) {
            decomposeParallelCase(timeSteps);
        } else {
            decomposeSerialCase(timeSteps);
        }
    }

    private void decomposeSerialCase(Set<String> timeSteps) {
        RunCommand command = new DecomposeCase(model, controller, LOG_NAME, ACTION_NAME, timeSteps);
        command.beforeExecute();
        command.executeClient();
    }

    private void decomposeParallelCase(Set<String> timeSteps) {
        File logFile = Paths.get(model.getProject().getBaseDir().getAbsolutePath(), LOG, LOG_NAME).toFile();

        IOUtils.clearFile(logFile);

        TerminalExecutorMonitor terminal = new TerminalExecutorMonitor(controller.getTerminalManager(), logFile);
        ExecutorService service = Executors.newSingleThreadExecutor();

        RunCommand reconstructMesh = new ReconstructMesh(model, controller, ACTION_NAME, LOG_NAME);
        reconstructMesh.inService(service);
        reconstructMesh.inTerminal(terminal);
        reconstructMesh.executeClient();
        
        RunCommand reconstructCase = new ReconstructCase(model, controller, shouldUseWithZeroFlag, ACTION_NAME, LOG_NAME);
        reconstructCase.inService(service);
        reconstructCase.inTerminal(terminal);
        reconstructCase.executeClient();
        
        RunCommand decomposeCase = new DecomposeCase(model, controller, LOG_NAME, ACTION_NAME, timeSteps);
        decomposeCase.inService(service);
        decomposeCase.inTerminal(terminal);
        decomposeCase.executeClient();
    }

}
