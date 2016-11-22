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
package eu.engys.dynamic;

import eu.engys.core.modules.tree.AbstractTreeViewAdapter;
import eu.engys.core.modules.tree.ModuleElementPanel;
import eu.engys.gui.GUIPanel;

public class DynamicTreeView extends AbstractTreeViewAdapter {

    private DynamicModule module;

    private GUIPanel sixDoFPanel;
    private GUIPanel solidBodyPanel;
    private GUIPanel refinePanel;

    public DynamicTreeView(DynamicModule module, GUIPanel sixDoFPanel, GUIPanel solidBodyPanel, GUIPanel refinePanel) {
        this.module = module;
        this.sixDoFPanel = sixDoFPanel;
        this.solidBodyPanel = solidBodyPanel;
        this.refinePanel = refinePanel;
    }

    @Override
    public void updateCaseSetupTree(ModuleElementPanel viewElementPanel) {
        if (module.getDynamicData().getAlgorithm().getType().isSolidRigidBody()) {
            viewElementPanel.removePanel(sixDoFPanel);
            viewElementPanel.removePanel(refinePanel);
            viewElementPanel.addPanel(solidBodyPanel);
            solidBodyPanel.load();
        } else if (module.getDynamicData().getAlgorithm().getType().is6DOF()) {
            viewElementPanel.removePanel(solidBodyPanel);
            viewElementPanel.removePanel(refinePanel);
            viewElementPanel.addPanel(sixDoFPanel);
            sixDoFPanel.load();
        } else if (module.getDynamicData().getAlgorithm().getType().isMeshRefine()) {
            viewElementPanel.removePanel(solidBodyPanel);
            viewElementPanel.removePanel(sixDoFPanel);
            viewElementPanel.addPanel(refinePanel);
            refinePanel.load();
        } else {
            viewElementPanel.removePanel(solidBodyPanel);
            viewElementPanel.removePanel(sixDoFPanel);
            viewElementPanel.removePanel(refinePanel);
        }
    }

}
