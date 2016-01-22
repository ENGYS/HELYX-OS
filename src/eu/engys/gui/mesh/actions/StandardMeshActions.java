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

import static eu.engys.util.ui.UiUtil.createToolBarButton;
import static eu.engys.util.ui.UiUtil.createToolBarButtonBar;

import javax.inject.Inject;
import javax.swing.Box;
import javax.swing.JToolBar;

import eu.engys.core.controller.Controller;
import eu.engys.core.project.Model;
import eu.engys.gui.mesh.panels.DefaultMeshAdvancedOptionsPanel;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.UiUtil;

public class StandardMeshActions extends DefaultMeshActions {

	@Inject
	public StandardMeshActions(Model model, Controller controller, ProgressMonitor monitor, DefaultMeshAdvancedOptionsPanel generalOptionsPanel) {
		super(model, controller, monitor, generalOptionsPanel);
	}

	@Override
	public JToolBar toolbar() {
		JToolBar toolbar = UiUtil.getToolbar("view.element.toolbar");

		toolbar.add(createToolBarButton(createMesh));
		toolbar.add(Box.createHorizontalStrut(2));
		toolbar.add(createToolBarButton(checkMesh));
		toolbar.add(createToolBarButton(deleteMesh));
		toolbar.addSeparator();
		toolbar.add(createToolBarButton(openOptionsDialog));
		toolbar.add(Box.createHorizontalGlue());
		
		return toolbar;
	}
}
