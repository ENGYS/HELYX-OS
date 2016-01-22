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

import java.awt.Component;
import java.util.List;

import javax.swing.JToolBar;

import eu.engys.core.controller.Controller;
import eu.engys.core.project.Model;
import eu.engys.gui.Actions;
import eu.engys.util.ui.UiUtil;

public abstract class DefaultCaseSetupActions implements Actions {

    protected Model model;
    protected Controller controller;
    protected DecomposeCaseAction decomposeCaseAction;

    public DefaultCaseSetupActions(Model model, Controller controller) {
        this.model = model;
        this.controller = controller;
        this.decomposeCaseAction = new DecomposeCaseAction(model, controller, shouldUseWithZeroFlag());
    }
    
    protected abstract List<Component> getToolbarComponents();
    
    protected abstract boolean shouldUseWithZeroFlag();
    
    @Override
    public JToolBar toolbar() {
        JToolBar toolbar = UiUtil.getToolbar("view.element.toolbar");
        for (Component c : getToolbarComponents()) {
            if (c == null) {
                toolbar.addSeparator();
            } else {
                toolbar.add(c);
            }
        }
        return toolbar;
    }


    @Override
    public void update() {
        decomposeCaseAction.setEnabled(!model.getPatches().isEmpty() && !model.getState().getMultiphaseModel().getKey().equals("ECOMARINE"));
    }
}
