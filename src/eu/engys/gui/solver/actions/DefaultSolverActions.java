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

package eu.engys.gui.solver.actions;

import static eu.engys.util.ui.UiUtil.createToolBarButton;

import javax.inject.Inject;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JToolBar;

import eu.engys.core.controller.Controller;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.gui.Actions;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;

public abstract class DefaultSolverActions implements Actions {

    private final Action launchAction;

    private JToolBar toolbar;
    protected Model model;

    @Inject
    public DefaultSolverActions(Model model, Controller controller) {
        this.model = model;
        this.launchAction = ActionManager.getInstance().get("solver.run");
    }

    @Override
    public JToolBar toolbar() {
        toolbar = UiUtil.getToolbar("view.element.toolbar");

        toolbar.add(createToolBarButton(launchAction));
        toolbar.addSeparator();
        addExtraActions();
        toolbar.add(Box.createHorizontalGlue());
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
                boolean isRemote = model.getSolverModel().isRemote();
                boolean hasMesh = !model.getPatches().isEmpty();
                boolean isRunning = model.getSolverModel() != null && model.getSolverModel().getServerState() != null && model.getSolverModel().getServerState().getSolverState().isRunning();
                boolean isSolutionSet = model.getState().areTimeAndFlowAndTurbulenceChoosen();
                launchAction.setEnabled(hasMesh && !isRunning && isSolutionSet);
            }
        });
    }

}
