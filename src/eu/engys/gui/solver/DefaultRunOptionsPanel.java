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
package eu.engys.gui.solver;

import static eu.engys.core.controller.AbstractController.SOLVER_RUN;
import static eu.engys.core.controller.AbstractController.SOLVER_RUN_EDIT;
import static eu.engys.core.project.openFOAMProject.LOG;
import static eu.engys.util.ui.ComponentsFactory.labelField;
import static eu.engys.util.ui.ComponentsFactory.stringField;

import java.awt.Dimension;
import java.io.File;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.gui.DefaultGUIPanel;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.StringField;
import net.java.dev.designgridlayout.Componentizer;

public class DefaultRunOptionsPanel extends DefaultGUIPanel {

    private static final String RUN = "Run Options";
    public static final String SOLVER_LABEL = "Solver";
    public static final String LOG_FILE_LABEL = "Log File";
    public static final String PROPERTIES_LABEL = "Properties";

    private JLabel solverName;
    // private IntegerField nProcessors;
    // private JCheckBox parallel;
    private StringField log;

    @Inject
    public DefaultRunOptionsPanel(Model model) {
        super(RUN, model);
    }

    protected JComponent layoutComponents() {
        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(getActionsPanel());
        builder.addComponent(getPropertiesPanel());
        builder.addComponent(getServerPanel());
        builder.addComponent(getQueuePanel());
        return builder.removeMargins().getPanel();
    }

    protected JComponent getServerPanel() {
        return new JPanel();
    }

    protected JComponent getQueuePanel() {
        return new JPanel();
    }

    protected JComponent getPropertiesPanel() {
        PanelBuilder properties = new PanelBuilder();
        properties.getPanel().setBorder(BorderFactory.createTitledBorder(PROPERTIES_LABEL));
        properties.addComponent(SOLVER_LABEL, solverName);
        // properties.addComponent("Parallel Run", parallel);
        // properties.addComponent("Number Of Processors", nProcessors);
        properties.addComponent(LOG_FILE_LABEL, log);
        return properties.getPanel();
    }

    protected JComponent getActionsPanel() {
        solverName = labelField("");
        // parallel = checkField();
        // nProcessors = intField();
        log = stringField();

        solverName.setEnabled(false);
        // parallel.setEnabled(false);
        // nProcessors.setEnabled(false);

        PanelBuilder actions = new PanelBuilder();
        actions.getPanel().setBorder(BorderFactory.createTitledBorder("Actions"));

        if (ActionManager.getInstance().contains(SOLVER_RUN) && ActionManager.getInstance().contains(SOLVER_RUN_EDIT)) {
            JButton runSolverButton = new JButton(ActionManager.getInstance().get(SOLVER_RUN));
            JButton editRunSolverButton = new JButton(ActionManager.getInstance().get(SOLVER_RUN_EDIT));
            runSolverButton.setPreferredSize(new Dimension(120, runSolverButton.getPreferredSize().height));

            JComponent c1 = Componentizer.create().minToPref(runSolverButton).fixedPref(editRunSolverButton).minAndMore(new JLabel()).component();
            actions.addComponent(c1);
        }

        JComponent[] cs = getExtraButtons();
        for (JComponent c : cs) {
            actions.addComponent(c);
        }
        return actions.getPanel();
    }

    protected JComponent[] getExtraButtons() {
        return new JComponent[0];
    }

    @Override
    public void load() {
        if (model.getSolverModel().getLogFile() != null) {
            File logFile = Paths.get(model.getProject().getBaseDir().getAbsolutePath(), LOG, model.getSolverModel().getLogFile()).toFile();
            if (logFile == null || !logFile.exists() || !logFile.isFile()) {
                setDefaultLogName();
            } else {
                log.setText(logFile.getName());
                solverName.setText(model.getState().getSolver().getName());
            }
        } else {
            setDefaultLogName();
        }
        // parallel.setSelected(model.getProject().isParallel());
        // nProcessors.setValue(model.getProject().getProcessors());
    }

    @Override
    public void save() {
        super.save();
        model.getSolverModel().setLogFile(log.getText());
    }

    @Override
    public void stateChanged() {
        setDefaultLogName();
    }

    @Override
    public void solverChanged() {
        setDefaultLogName();
    }

    public void setDefaultLogName() {
        String application = model.getState().getSolver().getName();
        solverName.setText(application);
        log.setText(application + ".log");
    }

}
