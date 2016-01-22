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

package eu.engys.gui.mesh.actions;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import eu.engys.core.OpenFOAMEnvironment;
import eu.engys.core.controller.Controller;
import eu.engys.core.controller.actions.RunCommand;
import eu.engys.core.project.Model;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class RunBlockMeshAction extends ViewAction {

    private Model model;
    private Controller controller;

    public RunBlockMeshAction(Model model, Controller controller) {
        super(BLOCK_LABEL, BLOCK_ICON, BLOCK_TOOLTIP);
        this.model = model;
        this.controller = controller;
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
        RunCommand command = new RunBlockMesh(model, controller);
        command.beforeExecute();
        command.executeClient();
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
