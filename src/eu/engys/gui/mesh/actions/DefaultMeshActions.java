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

import static eu.engys.core.controller.AbstractController.MESH_CHECK;
import static eu.engys.core.controller.AbstractController.MESH_CREATE;
import static eu.engys.core.controller.AbstractController.MESH_DELETE;
import static eu.engys.core.controller.AbstractController.MESH_STRETCH;

import javax.swing.Action;

import eu.engys.core.controller.Controller;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.gui.Actions;
import eu.engys.gui.mesh.panels.DefaultMeshAdvancedOptionsPanel;
import eu.engys.util.progress.ProgressMonitor;

public abstract class DefaultMeshActions implements Actions {

    protected Model model;
    protected Controller controller;
    protected ProgressMonitor monitor;

    protected Action createMesh, stretchMesh, checkMesh, deleteMesh, advancedMeshOptions;

    public DefaultMeshActions(Model model, Controller controller, ProgressMonitor monitor, DefaultMeshAdvancedOptionsPanel optionsPanel) {
        this.model = model;
        this.controller = controller;
        this.monitor = monitor;
        this.advancedMeshOptions = new OpenMeshAdvancedOptionsDialog(optionsPanel);

        createMesh = ActionManager.getInstance().get(MESH_CREATE);
        if (ActionManager.getInstance().contains(MESH_STRETCH)) {
            stretchMesh = ActionManager.getInstance().get(MESH_STRETCH);
        }
        checkMesh = ActionManager.getInstance().get(MESH_CHECK);
        deleteMesh = ActionManager.getInstance().get(MESH_DELETE);
    }

    @Override
    public void update() {
        boolean geometryNotEmpty = !model.getGeometry().isEmpty();
        boolean meshNotEmpty = !model.getPatches().isEmpty();

        createMesh.setEnabled(geometryNotEmpty);
        if (ActionManager.getInstance().contains(MESH_STRETCH)) {
            stretchMesh.setEnabled(meshNotEmpty);
        }
        checkMesh.setEnabled(meshNotEmpty);
        deleteMesh.setEnabled(meshNotEmpty);
    }

}
