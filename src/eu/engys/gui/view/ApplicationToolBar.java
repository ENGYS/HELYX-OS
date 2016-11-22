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
package eu.engys.gui.view;

import static eu.engys.core.controller.AbstractController.OPEN_PARAMETERS_MANAGER;
import static eu.engys.core.controller.AbstractController.OPEN_RUN_MODE;
import static eu.engys.core.controller.AbstractController.PARALLEL_WORKS;
import static eu.engys.gui.view.View.BROWSE_CASE;
import static eu.engys.gui.view.View.EXIT;
import static eu.engys.gui.view.View.NEW_CASE;
import static eu.engys.gui.view.View.OPEN_CASE;
import static eu.engys.gui.view.View.OPEN_TERMINAL;
import static eu.engys.gui.view.View.RECENT_CASES;
import static eu.engys.gui.view.View.SAVE_AS_CASE;
import static eu.engys.gui.view.View.SAVE_CASE;
import static eu.engys.util.ui.UiUtil.createToolBarButton;

import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.launcher.StartUpMonitor;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class ApplicationToolBar {

    public static final String APPLICATION_TOOLBAR = "application.toolbar";

    private Model model;

    private JToolBar toolbar;

    public ApplicationToolBar(Model model) {
        this.model = model;
        this.toolbar = UiUtil.getToolbarWrapped(APPLICATION_TOOLBAR);
        StartUpMonitor.info("Loading Toolbar");
        layoutComponents();
    }

    private void layoutComponents() {
        if (ActionManager.getInstance().contains(NEW_CASE)) {
            toolbar.add(createToolBarButton(ActionManager.getInstance().get(NEW_CASE)));
        }
        toolbar.add(createToolBarButton(ActionManager.getInstance().get(OPEN_CASE)));
        toolbar.add(createToolBarButton(ActionManager.getInstance().get(RECENT_CASES)));
        if (ActionManager.getInstance().contains(SAVE_CASE)) {
            toolbar.add(createToolBarButton(ActionManager.getInstance().get(SAVE_CASE)));
            ActionManager.getInstance().get(SAVE_CASE).setEnabled(false);
        }
        if (ActionManager.getInstance().contains(SAVE_AS_CASE)) {
            toolbar.add(createToolBarButton(ActionManager.getInstance().get(SAVE_AS_CASE)));
            ActionManager.getInstance().get(SAVE_AS_CASE).setEnabled(false);
        }
        toolbar.addSeparator();
        toolbar.add(createToolBarButton(ActionManager.getInstance().get(OPEN_TERMINAL)));
        toolbar.add(createToolBarButton(ActionManager.getInstance().get(BROWSE_CASE)));

        // add the run cloud button
        toolbar.addSeparator();
        if (ActionManager.getInstance().contains(PARALLEL_WORKS)) {
            toolbar.add(createToolBarButton(ActionManager.getInstance().get(PARALLEL_WORKS)));
            ActionManager.getInstance().get(PARALLEL_WORKS).setEnabled(false);
        }

        ViewAction connectionAction = ActionManager.getInstance().get(OPEN_RUN_MODE);
        if (ActionManager.getInstance().contains(OPEN_RUN_MODE)) {
            toolbar.addSeparator();
            toolbar.add(createToolBarButton(connectionAction));
            connectionAction.setEnabled(false);
        }

        if (ActionManager.getInstance().contains(OPEN_PARAMETERS_MANAGER)) {
            toolbar.add(createToolBarButton(ActionManager.getInstance().get(OPEN_PARAMETERS_MANAGER)));
        }
        // if (ActionManager.getInstance().contains(EXIT)) {
        // toolbar.add(Box.createHorizontalGlue());
        // toolbar.add(createToolBarButton(ActionManager.getInstance().get(EXIT)));
        // }

        ActionManager.getInstance().get(OPEN_TERMINAL).setEnabled(false);
        ActionManager.getInstance().get(BROWSE_CASE).setEnabled(false);
    }

    public JToolBar getExitButton() {
        if (ActionManager.getInstance().contains(EXIT)) {
            return UiUtil.createButtonToolbarStyle(createToolBarButton(ActionManager.getInstance().get(EXIT)));
        }
        return null;
    }

    public void refresh() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                boolean hasProject = model.getProject() != null;

                if (ActionManager.getInstance().contains(SAVE_CASE)) {
                    ActionManager.getInstance().get(SAVE_CASE).setEnabled(hasProject);
                }
                if (ActionManager.getInstance().contains(SAVE_AS_CASE)) {
                    ActionManager.getInstance().get(SAVE_AS_CASE).setEnabled(hasProject);
                }
                ActionManager.getInstance().get(OPEN_TERMINAL).setEnabled(hasProject);
                ActionManager.getInstance().get(BROWSE_CASE).setEnabled(hasProject);
                if (ActionManager.getInstance().contains(OPEN_RUN_MODE)) {
                    ActionManager.getInstance().get(OPEN_RUN_MODE).setEnabled(hasProject);
                }
                if (ActionManager.getInstance().contains(PARALLEL_WORKS)) {
                    ActionManager.getInstance().get(PARALLEL_WORKS).setEnabled(model.getProject() != null);
                }
                if (ActionManager.getInstance().contains(OPEN_PARAMETERS_MANAGER)) {
                    ActionManager.getInstance().get(OPEN_PARAMETERS_MANAGER).setEnabled(hasProject);
                }
            }
        });
    }

    public JToolBar getToolbar() {
        return toolbar;
    }

}
