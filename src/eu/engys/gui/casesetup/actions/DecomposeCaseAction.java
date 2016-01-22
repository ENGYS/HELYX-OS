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

package eu.engys.gui.casesetup.actions;

import static eu.engys.core.project.openFOAMProject.LOG;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Icon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.OpenFOAMEnvironment;
import eu.engys.core.controller.Controller;
import eu.engys.core.controller.actions.RunCommand;
import eu.engys.core.executor.TerminalExecutorMonitor;
import eu.engys.core.project.Model;
import eu.engys.util.IOUtils;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class DecomposeCaseAction extends ViewAction {

    private static final Logger logger = LoggerFactory.getLogger(DecomposeCaseAction.class);

    private static final Icon DECOMPOSE_ICON = ResourcesUtil.getIcon("decompose.icon");
    private static final String DECOMPOSE_LABEL = ResourcesUtil.getString("casesetup.decompose.label");
    private static final String DECOMPOSE_TOOLTIP = ResourcesUtil.getString("casesetup.decompose.tooltip");

    public static final String ACTION_NAME = "Decompose";
    public static final String LOG_NAME = "decomposeCase.log";

    private Model model;
    private Controller controller;

    private boolean shouldUseWithZeroFlag;

    public DecomposeCaseAction(Model model, Controller controller, boolean shouldUseWithZeroFlag) {
        super(DECOMPOSE_LABEL, DECOMPOSE_ICON, DECOMPOSE_TOOLTIP);
        this.model = model;
        this.controller = controller;
        this.shouldUseWithZeroFlag = shouldUseWithZeroFlag;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (controller.isDemo()) {
            UiUtil.showDemoMessage();
        } else {
            if (OpenFOAMEnvironment.isEnvironementLoaded()) {
                _actionPerformed();
            } else {
                UiUtil.showCoreEnvironmentNotLoadedWarning();
            }
        }
    }

    private void _actionPerformed() {
        DecomposeCasePanel panel = new DecomposeCasePanel(model);
        panel.showDialog();
        if (panel.getStatus().isOK()) {
            controller.saveCase(model.getProject().getBaseDir());
            decompose();
        }
    }

    private void decompose() {
        if (model.getProject().isParallel()) {
            decomposeParallelCase();
        } else {
            decomposeSerialCase();
        }
    }

    private void decomposeSerialCase() {
        RunCommand command = new DecomposeCase(model, controller, ACTION_NAME, LOG_NAME);
        command.beforeExecute();
        command.executeClient();
    }

    private void decomposeParallelCase() {
        File logFile = Paths.get(model.getProject().getBaseDir().getAbsolutePath(), LOG, LOG_NAME).toFile();

        IOUtils.clearFile(logFile);

        TerminalExecutorMonitor terminal = new TerminalExecutorMonitor(logFile);
        ExecutorService service = Executors.newSingleThreadExecutor();

        RunCommand reconstructCase = new ReconstructCase(model, controller, shouldUseWithZeroFlag, ACTION_NAME, LOG_NAME);
        reconstructCase.inService(service);
        reconstructCase.inTerminal(terminal);
        reconstructCase.executeClient();

        RunCommand decomposeCase = new DecomposeCase(model, controller, ACTION_NAME, LOG_NAME);
        decomposeCase.inService(service);
        decomposeCase.inTerminal(terminal);
        decomposeCase.executeClient();
    }

}
