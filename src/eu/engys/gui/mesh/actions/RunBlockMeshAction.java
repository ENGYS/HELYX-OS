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
package eu.engys.gui.mesh.actions;

import static eu.engys.gui.mesh.actions.RunBlockMesh.ACTION_NAME;
import static eu.engys.gui.mesh.actions.RunBlockMesh.BLOCK_MESH_LOG;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Icon;

import eu.engys.core.OpenFOAMEnvironment;
import eu.engys.core.controller.Controller;
import eu.engys.core.controller.ScriptFactory;
import eu.engys.core.controller.actions.CheckMesh;
import eu.engys.core.controller.actions.RunCommand;
import eu.engys.core.executor.TerminalExecutorMonitor;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class RunBlockMeshAction extends ViewAction {

    private Model model;
    private Controller controller;
    private ScriptFactory scriptFactory;

    public RunBlockMeshAction(Model model, Controller controller, ScriptFactory scriptFactory) {
        super(BLOCK_LABEL, BLOCK_ICON, BLOCK_TOOLTIP);
        this.model = model;
        this.controller = controller;
        this.scriptFactory = scriptFactory;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (controller.isDemo()) {
            UiUtil.showDemoMessage();
        } else {
            if (OpenFOAMEnvironment.isEnvironementLoaded()) {
                fixDecomposeParDict();
                controller.saveCase(model.getProject().getBaseDir());
                blockMesh();
            } else {
                UiUtil.showCoreEnvironmentNotLoadedWarning();
            }
        }
    }

    private void blockMesh() {
        File logFile = Paths.get(model.getProject().getBaseDir().getAbsolutePath(), openFOAMProject.LOG, BLOCK_MESH_LOG).toFile();
        
        TerminalExecutorMonitor terminal = new TerminalExecutorMonitor(controller.getTerminalManager(), logFile);
        ExecutorService service = Executors.newSingleThreadExecutor();
        
        RunCommand blockMeshCommand = new RunBlockMesh(model, controller, BLOCK_MESH_LOG, ACTION_NAME);
        blockMeshCommand.inService(service);
        blockMeshCommand.inTerminal(terminal);

        RunCommand checkCommand = new CheckMesh(model, controller, scriptFactory, BLOCK_MESH_LOG, ACTION_NAME);
        checkCommand.inService(service);
        checkCommand.inTerminal(terminal);

        blockMeshCommand.beforeExecute();
        checkCommand.beforeExecute();

        blockMeshCommand.executeClient();
        checkCommand.executeClient();
    }
    

    private void fixDecomposeParDict() {
        model.getProject().getSystemFolder().getDecomposeParDict().toHierarchical(model);
    }

    /*
     * Resources
     */

    private static final Icon BLOCK_ICON = ResourcesUtil.getIcon("block.mesh.create.icon");

    private static final String BLOCK_LABEL = ResourcesUtil.getString("block.mesh.create.label");
    private static final String BLOCK_TOOLTIP = ResourcesUtil.getString("block.mesh.create.tooltip");

}
