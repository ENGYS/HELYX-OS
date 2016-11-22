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

import static eu.engys.util.ui.UiUtil.DIALOG_CANCEL_LABEL;
import static eu.engys.util.ui.UiUtil.DIALOG_OK_LABEL;
import static eu.engys.util.ui.UiUtil.RESET_BUTTON_LABEL;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import eu.engys.gui.mesh.panels.DefaultMeshAdvancedOptionsPanel;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class OpenMeshAdvancedOptionsDialog extends ViewAction {

    public static final String OPEN_OPTIONS_DIALOG_LABEL = ResourcesUtil.getString("mesh.options.label");
    public static final Icon OPEN_OPTIONS_ICON = ResourcesUtil.getIcon("mesh.options.icon");
    public static final String OPEN_OPTIONS_DIALOG_TOOLTIP = ResourcesUtil.getString("mesh.options.tooltip");

    public static final String ADVANCED_OPTIONS = "Advanced Options";

    private JDialog dialog;
    private DefaultMeshAdvancedOptionsPanel optionsPanel;

    public OpenMeshAdvancedOptionsDialog(DefaultMeshAdvancedOptionsPanel optionsPanel) {
        super(OPEN_OPTIONS_DIALOG_LABEL, OPEN_OPTIONS_ICON, OPEN_OPTIONS_DIALOG_TOOLTIP);
        this.optionsPanel = optionsPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        createGeneralOptionsDialog();
        optionsPanel.load();
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

            optionsPanel.layoutPanel();
            mainPanel.add(optionsPanel.getPanel(), BorderLayout.CENTER);

            AbstractAction saveAndCloseDialogAction = new AbstractAction(DIALOG_OK_LABEL) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    optionsPanel.save();
                    optionsPanel.handleClose();
                    dialog.dispose();
                }
            };

            AbstractAction cancelAction = new AbstractAction(DIALOG_CANCEL_LABEL) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    optionsPanel.handleClose();
                    dialog.dispose();
                }
            };

            AbstractAction resetToDefaultsAction = new AbstractAction(RESET_BUTTON_LABEL) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    optionsPanel.resetToDefaults();
                }
            };

            JButton okButton = new JButton(saveAndCloseDialogAction);
            okButton.setName(DIALOG_OK_LABEL);
            rightButtonsPanel.add(okButton);

            JButton cancelButton = new JButton(cancelAction);
            cancelButton.setName(DIALOG_CANCEL_LABEL);
            rightButtonsPanel.add(cancelButton);

            JButton resetButton = new JButton(resetToDefaultsAction);
            resetButton.setName(RESET_BUTTON_LABEL);
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

}
