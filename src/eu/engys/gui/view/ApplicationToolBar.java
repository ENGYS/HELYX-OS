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

package eu.engys.gui.view;

import static eu.engys.util.ui.UiUtil.createToolBarButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.launcher.StartUpMonitor;
import eu.engys.util.ui.ViewAction;

public class ApplicationToolBar extends JToolBar {

    private Model model;

    public ApplicationToolBar(Model model) {
        super(JToolBar.HORIZONTAL);
        this.model = model;
        StartUpMonitor.info("Loading Toolbar");
        setFloatable(false);
        setRollover(true);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
        
        layoutComponents();
    }

    private void layoutComponents() {
        add(createToolBarButton(ActionManager.getInstance().get("application.create")));
        add(createToolBarButton(ActionManager.getInstance().get("application.open")));
        add(createToolBarButton(ActionManager.getInstance().get("application.recent")));
        add(createToolBarButton(ActionManager.getInstance().get("application.save")));
        add(createToolBarButton(ActionManager.getInstance().get("application.saveAs")));
        addSeparator();
        add(createToolBarButton(ActionManager.getInstance().get("application.open.terminal")));
        add(createToolBarButton(ActionManager.getInstance().get("application.browse.case")));

        if (ActionManager.getInstance().contains("application.support.window")) {
            addSeparator();
            add(createToolBarButton(ActionManager.getInstance().get("application.support.window")));
        }

        ViewAction connectionAction = ActionManager.getInstance().get("application.connection.window");
        if (ActionManager.getInstance().contains("application.connection.window")) {
            addSeparator();
            add(createToolBarButton(connectionAction));
            connectionAction.setEnabled(false);
        }

        add(Box.createHorizontalGlue());
        add(createToolBarButton(ActionManager.getInstance().get("application.exit")));

        ActionManager.getInstance().get("application.save").setEnabled(false);
        ActionManager.getInstance().get("application.saveAs").setEnabled(false);
        ActionManager.getInstance().get("application.open.terminal").setEnabled(false);
        ActionManager.getInstance().get("application.browse.case").setEnabled(false);
    }

    public void refresh() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ActionManager.getInstance().get("application.save").setEnabled(model.getProject() != null);
                ActionManager.getInstance().get("application.saveAs").setEnabled(model.getProject() != null);
                ActionManager.getInstance().get("application.open.terminal").setEnabled(model.getProject() != null);
                ActionManager.getInstance().get("application.browse.case").setEnabled(model.getProject() != null);
                if (ActionManager.getInstance().contains("application.connection.window")) {
                    ActionManager.getInstance().get("application.connection.window").setEnabled(model.getProject() != null);
                }
            }
        });
    }

}
