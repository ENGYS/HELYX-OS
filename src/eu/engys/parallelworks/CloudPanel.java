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
package eu.engys.parallelworks;

import static eu.engys.parallelworks.ParallelWorksData.LOCALHOST;
import static eu.engys.parallelworks.ParallelWorksData.PARALLEL_WORKS;
import static eu.engys.util.ui.ComponentsFactory.selectField;
import static eu.engys.util.ui.ComponentsFactory.stringField;
import static eu.engys.util.ui.UiUtil.DIALOG_CANCEL_LABEL;
import static eu.engys.util.ui.UiUtil.DIALOG_OK_LABEL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.Controller;
import eu.engys.core.controller.Controller.OpenMode;
import eu.engys.core.project.Model;
import eu.engys.util.Util;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.StringField;

public class CloudPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(CloudPanel.class);

    public static final String GET_A_PARALLEL_WORKS_API_KEY = "Get a Parallel Works API key";
    private static final String WEBSITE = "https://eval.parallel.works/signup?starter=helyxos";

    private static final String CLOUD_SETTINGS = "Cloud Settings";

    private static final String[] DRIVERS = { LOCALHOST, PARALLEL_WORKS };

    public static final String EXECUTION_DRIVER = "Execution Driver";
    public static final String API_KEY = "API Key";
    public static final String RUN_WORKSPACE = "Run Workspace";
    public static final String RUN_WORKFLOW = "Run Workflow";
    public static final String PULL_RESULTS = "Pull Results from Cloud";

    public static final String DOWNLOAD_LABEL = "Download Last Cloud Result";

    private JDialog dialog;

    private JComboBox<String> driverCombo;
    private StringField driverAPIKEY;
    private StringField driverWorkflow;
    private StringField driverWorkspace;
    private JCheckBox driverPullResults;

    private JButton downloadButton;
    private JButton websiteButton;

    private DriverComboListener listener;

    private Model model;
    private ProgressMonitor monitor;

    private Controller controller;

    private JTextArea infoArea;

    public CloudPanel(Model model, Controller controller, ProgressMonitor monitor) {
        super(new BorderLayout());
        this.model = model;
        this.controller = controller;
        this.monitor = monitor;
        layoutComponents();
    }

    private void layoutComponents() {
        JScrollPane scrollPane = new JScrollPane(createMainPanel());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        add(scrollPane, BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);

        dialog = new JDialog(UiUtil.getActiveWindow(), CLOUD_SETTINGS, ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(this);
        dialog.setSize(520, 320);
        dialog.setLocationRelativeTo(null);

        this.listener = new DriverComboListener();
    }

    private JPanel createMainPanel() {
        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(EXECUTION_DRIVER, driverCombo = selectField(DRIVERS));
        builder.addComponent(API_KEY, driverAPIKEY = stringField(""));
        builder.addComponent(RUN_WORKFLOW, driverWorkflow = stringField("", false, false));
        builder.addComponent(RUN_WORKSPACE, driverWorkspace = stringField("", false, false));
        builder.addComponent(PULL_RESULTS, driverPullResults = ComponentsFactory.checkField(true));

        infoArea = new JTextArea("Mesh, initialise fields and solver scripts, will be executed on the Parallel Works cloud using OpenFOAM v4.0.");
        infoArea.setLineWrap(true);
        infoArea.setEditable(false);
        infoArea.setOpaque(false);
        infoArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.WHITE), BorderFactory.createEmptyBorder(10, 0, 10, 0)));

        websiteButton = UiUtil.createURLOpenerButton(new WebSiteAction());
        websiteButton.setForeground(Color.BLUE);
        websiteButton.setBorderPainted(false);
        websiteButton.setName(GET_A_PARALLEL_WORKS_API_KEY);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(builder.removeMargins().getPanel(), BorderLayout.NORTH);
        mainPanel.add(infoArea, BorderLayout.CENTER);
        mainPanel.add(websiteButton, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(downloadButton = new JButton(new DownloadAction()));
        downloadButton.setName(DOWNLOAD_LABEL);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton(new OkAction());
        okButton.setName(UiUtil.DIALOG_OK_LABEL);
        JButton cancelButton = new JButton(new CancelAction());
        cancelButton.setName(UiUtil.DIALOG_CANCEL_LABEL);
        rightPanel.add(okButton);
        rightPanel.add(cancelButton);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel.add(leftPanel);
        buttonsPanel.add(rightPanel);
        return buttonsPanel;
    }

    public void showDialog(ParallelWorksData data) {
        load(data);
        dialog.setVisible(true);
    }

    private void closeDialog() {
        dialog.dispose();
    }

    void load(ParallelWorksData data) {
        driverCombo.removeActionListener(listener);

        driverCombo.setSelectedItem(data.getType());
        driverAPIKEY.setStringValue(data.getKey());
        driverWorkflow.setStringValue(data.getWorkflow());
        driverWorkspace.setStringValue(data.getWorkspace());
        driverPullResults.setSelected(data.isPullResults());

        enableEditing(data.isLocalhost());

        driverCombo.addActionListener(listener);
    }

    private void enableEditing(boolean isLocalhost) {
        driverAPIKEY.setEnabled(!isLocalhost);
        driverWorkflow.setEnabled(!isLocalhost);
        driverWorkspace.setEnabled(!isLocalhost);
        driverPullResults.setEnabled(!isLocalhost);
        websiteButton.setVisible(!isLocalhost);
        downloadButton.setVisible(!isLocalhost);
        infoArea.setVisible(!isLocalhost);
    }

    ParallelWorksData save() {
        ParallelWorksData data = new ParallelWorksData();
        data.setType(driverCombo.getItemAt(driverCombo.getSelectedIndex()));
        data.setKey(driverAPIKEY.getStringValue());
        data.setWorkflow(driverWorkflow.getStringValue());
        data.setWorkspace(driverWorkspace.getStringValue());
        data.setPullResults(driverPullResults.isSelected());
        return data;
    }

    private class DriverComboListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            enableEditing(driverCombo.getItemAt(driverCombo.getSelectedIndex()).equals(ParallelWorksData.LOCALHOST));
        }

    }

    private class OkAction extends AbstractAction {

        public OkAction() {
            super(DIALOG_OK_LABEL);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ParallelWorksData data = save();
            ParallelWorksData.toPreferences(data);
            closeDialog();
        }

    }

    private class CancelAction extends AbstractAction {

        public CancelAction() {
            super(DIALOG_CANCEL_LABEL);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            closeDialog();
        }

    }

    private class DownloadAction extends AbstractAction {

        public DownloadAction() {
            super(DOWNLOAD_LABEL);
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            int retVal = showWarningMessage("This action will override current data. Continue?");
            if (retVal == JOptionPane.OK_OPTION) {
                closeDialog();
                new ParallelWorksDownloader(model, monitor).downloadCloudResults();
                controller.reopenCase(OpenMode.CURRENT_SETTINGS);
            }
        }
    }

    private int showWarningMessage(String message) {
        return JOptionPane.showConfirmDialog(dialog, message, "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    private class WebSiteAction extends AbstractAction {

        public WebSiteAction() {
            super(GET_A_PARALLEL_WORKS_API_KEY);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Util.openWebpage(new URI(WEBSITE).toURL());
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }

    }

}