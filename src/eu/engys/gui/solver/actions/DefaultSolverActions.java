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
package eu.engys.gui.solver.actions;

import static eu.engys.core.controller.AbstractController.SOLVER_RUN;
import static eu.engys.util.ui.UiUtil.createToolBarButton;

import javax.inject.Inject;
import javax.swing.JToolBar;

import eu.engys.core.controller.Controller;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.gui.Actions;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;

public abstract class DefaultSolverActions implements Actions {

    private JToolBar toolbar;
    protected Model model;

    @Inject
    public DefaultSolverActions(Model model, Controller controller) {
        this.model = model;
    }

    @Override
    public JToolBar toolbar() {
        toolbar = UiUtil.getToolbarWrapped(TOOLBAR_NAME);

        if (ActionManager.getInstance().contains(SOLVER_RUN)) {
            toolbar.add(createToolBarButton(ActionManager.getInstance().get(SOLVER_RUN)));
            toolbar.addSeparator();
        }
        addExtraActions();
//        toolbar.add(Box.createHorizontalGlue());
        return toolbar;
    }

    public JToolBar getToolbar() {
        return toolbar;
    }

    protected abstract void addExtraActions();

    @Override
    public void update() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (ActionManager.getInstance().contains(SOLVER_RUN)) {
                    boolean hasMesh = !model.getPatches().isEmpty();
                    boolean isRunning = model.getSolverModel() != null && model.getSolverModel().getServerState() != null && model.getSolverModel().getServerState().getSolverState().isRunning();
                    boolean isSolutionSet = model.getState().areTimeAndFlowAndTurbulenceChoosen();
                    ActionManager.getInstance().get(SOLVER_RUN).setEnabled(hasMesh && !isRunning && isSolutionSet);
                }
            }
        });
    }

}
