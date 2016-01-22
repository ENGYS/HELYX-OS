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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import eu.engys.core.controller.Controller;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.gui.Actions;
import eu.engys.gui.mesh.panels.DefaultMeshAdvancedOptionsPanel;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public abstract class DefaultMeshActions implements Actions {

    public static final String ADVANCED_OPTIONS = "Advanced Options";
    protected Model model;
    protected Controller controller;
    protected ProgressMonitor monitor;
    protected DefaultMeshAdvancedOptionsPanel generalOptionsPanel;

    protected final Action createMesh, checkMesh, deleteMesh, virtualMesh;

    public DefaultMeshActions(Model model, Controller controller, ProgressMonitor monitor, DefaultMeshAdvancedOptionsPanel generalOptionsPanel) {
        this.model = model;
        this.controller = controller;
        this.monitor = monitor;
        this.generalOptionsPanel = generalOptionsPanel;

        createMesh = ActionManager.getInstance().get("mesh.create");
        checkMesh = ActionManager.getInstance().get("mesh.check");
        deleteMesh = ActionManager.getInstance().get("mesh.delete");
        virtualMesh = ActionManager.getInstance().get("mesh.batch");
    }

    @Override
    public void update() {
        createMesh.setEnabled(!model.getGeometry().isEmpty());
        checkMesh.setEnabled(!model.getPatches().isEmpty());
        deleteMesh.setEnabled(!model.getPatches().isEmpty());
    }

    protected final Action openOptionsDialog = new ViewAction(OPEN_OPTIONS_DIALOG_LABEL, OPT_ICON, OPEN_OPTIONS_DIALOG_TOOLTIP) {

        private JDialog dialog;

        public void actionPerformed(ActionEvent e) {
            // Commented because there seems to be no reason to save the case
            // here
            // controller.save(null);
            createGeneralOptionsDialog();
            generalOptionsPanel.load();
            dialog.setVisible(true);
        }

        private void createGeneralOptionsDialog() {
            if (dialog == null) {
                dialog = new JDialog(UiUtil.getActiveWindow());
                dialog.setName("general.options.dialog");

                JPanel mainPanel = new JPanel(new BorderLayout());
                mainPanel.setName("general.options.panel");

                JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
                JPanel leftButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JPanel rightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonsPanel.add(leftButtonsPanel);
                buttonsPanel.add(rightButtonsPanel);

                generalOptionsPanel.layoutPanel();
                mainPanel.add(generalOptionsPanel.getPanel(), BorderLayout.CENTER);

                AbstractAction saveAndCloseDialogAction = new AbstractAction("OK") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        generalOptionsPanel.save();
                        generalOptionsPanel.handleClose();
                        dialog.setVisible(false);
                    }
                };

                AbstractAction cancelAction = new AbstractAction("Cancel") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        generalOptionsPanel.handleClose();
                        dialog.setVisible(false);
                    }
                };

                AbstractAction resetToDefaultsAction = new AbstractAction("Reset") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        generalOptionsPanel.resetToDefaults();
                    }
                };

                JButton okButton = new JButton(saveAndCloseDialogAction);
                okButton.setName("OK");
                rightButtonsPanel.add(okButton);

                JButton cancelButton = new JButton(cancelAction);
                cancelButton.setName("Cancel");
                rightButtonsPanel.add(cancelButton);

                JButton resetButton = new JButton(resetToDefaultsAction);
                resetButton.setName("Reset");
                leftButtonsPanel.add(resetButton);

                mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

                dialog.setTitle(ADVANCED_OPTIONS);
                dialog.add(mainPanel);
                dialog.setSize(600, 420);
                dialog.setLocationRelativeTo(null);
                dialog.setModal(false);
                dialog.getRootPane().setDefaultButton(okButton);
            }
        }
    };

    /**
     * Resources
     */

    public static final String OPEN_OPTIONS_DIALOG_LABEL = ResourcesUtil.getString("mesh.options.label");
    public static final String OPEN_OPTIONS_DIALOG_TOOLTIP = ResourcesUtil.getString("mesh.options.tooltip");
    public static final Icon OPT_ICON = ResourcesUtil.getIcon("general.options.icon");
}
