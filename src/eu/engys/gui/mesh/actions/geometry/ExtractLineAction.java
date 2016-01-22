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

package eu.engys.gui.mesh.actions.geometry;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.engys.core.controller.Controller;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;
import eu.engys.gui.mesh.panels.DefaultGeometryActions;
import eu.engys.gui.mesh.panels.DefaultGeometryActions.Disable;
import eu.engys.gui.mesh.panels.DefaultGeometryActions.Enable;
import eu.engys.util.ui.UiUtil;

public class ExtractLineAction extends AbstractAction {

    public static final String EXTRACT = "Extract";
    public static final String EXTRACT_NAME = EXTRACT + ".Lines";

    private Model model;
    private Surface[] surfaces;

    private DefaultGeometryActions actions;

    private Controller controller;

    public ExtractLineAction(Model model, Controller controller, DefaultGeometryActions actions) {
        super(EXTRACT);
        this.model = model;
        this.controller = controller;
        this.actions = actions;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (controller.isDemo()) {
            UiUtil.showDemoMessage();
        } else {
            new ExtractLinesDialog(UiUtil.getActiveWindow(), model, null).show(surfaces[0], new Disable(actions), new Enable(actions));
        }
    }

    public void update(boolean enabled, Surface[] surfaces) {
        this.surfaces = surfaces;
        setEnabled(enabled && surfaces.length == 1);
    }
}
