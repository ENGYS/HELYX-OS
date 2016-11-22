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

import static eu.engys.core.project.system.ControlDict.ADJUSTABLE_RUN_TIME_KEY;
import static eu.engys.core.project.system.ControlDict.ADJUST_TIME_STEP_KEY;
import static eu.engys.core.project.system.ControlDict.DELTA_T_KEY;
import static eu.engys.core.project.system.ControlDict.END_TIME_KEY;
import static eu.engys.core.project.system.ControlDict.FUNCTIONS_KEY;
import static eu.engys.core.project.system.ControlDict.GRAPH_FORMAT_KEY;
import static eu.engys.core.project.system.ControlDict.GRAPH_FORMAT_VALUE;
import static eu.engys.core.project.system.ControlDict.MAX_ALPHA_CO_KEY;
import static eu.engys.core.project.system.ControlDict.MAX_CO_KEY;
import static eu.engys.core.project.system.ControlDict.MAX_DELTA_T_KEY;
import static eu.engys.core.project.system.ControlDict.PURGE_WRITE_KEY;
import static eu.engys.core.project.system.ControlDict.RUN_TIME_VALUE;
import static eu.engys.core.project.system.ControlDict.START_FROM_KEY;
import static eu.engys.core.project.system.ControlDict.START_FROM_VALUES;
import static eu.engys.core.project.system.ControlDict.START_TIME_KEY;
import static eu.engys.core.project.system.ControlDict.START_TIME_VALUE;
import static eu.engys.core.project.system.ControlDict.STOP_AT_KEY;
import static eu.engys.core.project.system.ControlDict.TIME_FORMAT_KEY;
import static eu.engys.core.project.system.ControlDict.TIME_FORMAT_VALUES;
import static eu.engys.core.project.system.ControlDict.TIME_PRECISION_KEY;
import static eu.engys.core.project.system.ControlDict.WRITE_COMPRESSION_KEY;
import static eu.engys.core.project.system.ControlDict.WRITE_COMPRESSION_VALUES;
import static eu.engys.core.project.system.ControlDict.WRITE_CONTROL_KEY;
import static eu.engys.core.project.system.ControlDict.WRITE_CONTROL_VALUES;
import static eu.engys.core.project.system.ControlDict.WRITE_FORMAT_KEY;
import static eu.engys.core.project.system.ControlDict.WRITE_FORMAT_VALUES;
import static eu.engys.core.project.system.ControlDict.WRITE_INTERVAL_KEY;
import static eu.engys.core.project.system.ControlDict.WRITE_PRECISION_KEY;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.ADJUSTABLE_TIME_STEP_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.DATA_WRITING_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.DELTA_T_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.END_TIME_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.GRAPH_FORMAT_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.GRAPH_FORMAT_LABELS;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.MAX_COURANT_ALPHA_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.MAX_COURANT_NUMBER_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.MAX_TIME_STEP_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.PURGE_WRITE_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.RUNTIME_CONTROLS;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.START_FROM_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.START_FROM_LABELS;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.TIME_FORMAT_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.TIME_FORMAT_LABELS;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.TIME_PRECISION_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.TIME_SETTINGS_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.WRITE_COMPRESSION_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.WRITE_COMPRESSION_LABELS;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.WRITE_CONTROL_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.WRITE_CONTROL_LABELS;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.WRITE_FORMAT_LABEL;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.WRITE_FORMAT_LABELS;
import static eu.engys.gui.casesetup.RuntimeControlsPanel.WRITE_PRECISION_LABEL;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.State;
import eu.engys.core.project.state.Time;
import eu.engys.core.project.system.ControlDict;
import eu.engys.gui.DefaultGUIPanel;
import eu.engys.util.progress.SilentMonitor;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.SelectionValueConfigurator;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;

public class SolverRuntimeControlsPanel extends DefaultGUIPanel {

    private static final ImageIcon APPLY_ICON = new ImageIcon(SolverRuntimeControlsPanel.class.getClassLoader().getResource("eu/engys/resources/images/tick16.png"));

    private DictionaryModel dictionaryModel;
    private JCheckBox adjustableTime;
    private JComponent maxCourantNumber;
    private JComponent maxAlphaCourant;
    private JComponent maxTimeStep;
    private DoubleField deltaT;
    private Time time = null;

    private JComboBox<?> startFrom;
    private DoubleField startTime;
    private PropertyChangeListener startFromListener;

    private boolean isSaving = false;

    private ActionListener adjustableTimeListener;

    @Inject
    public SolverRuntimeControlsPanel(Model model) {
        super(RUNTIME_CONTROLS, model);
    }

    @Override
    public String getName() {
        return "Solver " + RUNTIME_CONTROLS;
    }

    @Override
    public void start() {
        super.start();
        if (model.getSolverModel().getServerState().getSolverState().isRunning()) {
            fixGUI();
            UiUtil.enable(this);
        } else {
            UiUtil.disable(this);
        }
    }

    protected JComponent layoutComponents() {
        dictionaryModel = new DictionaryModel(new Dictionary(""));
        PanelBuilder timeBuilder = new PanelBuilder();

        startFrom = dictionaryModel.bindSelection(START_FROM_KEY, START_FROM_VALUES, START_FROM_LABELS);
        startTime = dictionaryModel.bindDouble(START_TIME_KEY);
        DoubleField endTime = dictionaryModel.bindDouble(END_TIME_KEY);

        timeBuilder.addComponent(START_FROM_LABEL, startFrom, startTime);
        timeBuilder.addComponent(END_TIME_LABEL, endTime);

        startTime.setEnabled(false);
        startFromListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                startTime.setEnabled(START_TIME_VALUE.equals(evt.getNewValue()));
            }
        };
        startFrom.addPropertyChangeListener("value", startFromListener);

        timeBuilder.addComponent(DELTA_T_LABEL, deltaT = dictionaryModel.bindDouble(DELTA_T_KEY));

        timeBuilder.addComponent(ADJUSTABLE_TIME_STEP_LABEL, adjustableTime = dictionaryModel.bindBoolean(ADJUST_TIME_STEP_KEY));
        timeBuilder.addComponent(MAX_COURANT_NUMBER_LABEL, maxCourantNumber = dictionaryModel.bindDouble(MAX_CO_KEY));
        timeBuilder.addComponent(MAX_COURANT_ALPHA_LABEL, maxAlphaCourant = dictionaryModel.bindDouble(MAX_ALPHA_CO_KEY));
        timeBuilder.addComponent(MAX_TIME_STEP_LABEL, maxTimeStep = dictionaryModel.bindDouble(MAX_DELTA_T_KEY));

        adjustableTimeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maxCourantNumber.setEnabled(adjustableTime.isSelected());
                maxAlphaCourant.setEnabled(adjustableTime.isSelected() && model.getState().getMultiphaseModel().isMultiphase());
                maxTimeStep.setEnabled(adjustableTime.isSelected());
            }
        };
        adjustableTime.setSelected(false);
        adjustableTime.addActionListener(adjustableTimeListener);

        maxCourantNumber.setEnabled(false);
        maxAlphaCourant.setEnabled(false);
        maxTimeStep.setEnabled(false);

        PanelBuilder dataWriteBuilder = new PanelBuilder();
        SelectionValueConfigurator conf = new SelectionValueConfigurator() {
            @Override
            public String write(String value) {
                if (value != null && value.equals(RUN_TIME_VALUE) && adjustableTime.isSelected())
                    return ADJUSTABLE_RUN_TIME_KEY;
                return value;
            }

            @Override
            public String read(String value) {
                if (value != null && value.equals(ADJUSTABLE_RUN_TIME_KEY))
                    return RUN_TIME_VALUE;
                return value;
            }
        };
        dataWriteBuilder.addComponent(WRITE_CONTROL_LABEL, dictionaryModel.bindSelection(WRITE_CONTROL_KEY, WRITE_CONTROL_VALUES, WRITE_CONTROL_LABELS, conf), dictionaryModel.bindDouble(WRITE_INTERVAL_KEY));
        dataWriteBuilder.addComponent(PURGE_WRITE_LABEL, dictionaryModel.bindIntegerPositive(PURGE_WRITE_KEY));
        dataWriteBuilder.addComponent(WRITE_FORMAT_LABEL, dictionaryModel.bindSelection(WRITE_FORMAT_KEY, WRITE_FORMAT_VALUES, WRITE_FORMAT_LABELS));
        dataWriteBuilder.addComponent(WRITE_PRECISION_LABEL, dictionaryModel.bindIntegerPositive(WRITE_PRECISION_KEY));
        dataWriteBuilder.addComponent(WRITE_COMPRESSION_LABEL, dictionaryModel.bindSelection(WRITE_COMPRESSION_KEY, WRITE_COMPRESSION_VALUES, WRITE_COMPRESSION_LABELS));
        dataWriteBuilder.addComponent(TIME_FORMAT_LABEL, dictionaryModel.bindSelection(TIME_FORMAT_KEY, TIME_FORMAT_VALUES, TIME_FORMAT_LABELS));
        dataWriteBuilder.addComponent(TIME_PRECISION_LABEL, dictionaryModel.bindIntegerPositive(TIME_PRECISION_KEY));
        dataWriteBuilder.addComponent(GRAPH_FORMAT_LABEL, dictionaryModel.bindSelection(GRAPH_FORMAT_KEY, GRAPH_FORMAT_VALUE, GRAPH_FORMAT_LABELS));

        JPanel timePanel = timeBuilder.margins(.5, .5, .5, .5).getPanel();
        timePanel.setBorder(BorderFactory.createTitledBorder(TIME_SETTINGS_LABEL));
        timePanel.setName(TIME_SETTINGS_LABEL);

        JPanel dataWritePanel = dataWriteBuilder.margins(.5, .5, .5, .5).getPanel();
        dataWritePanel.setBorder(BorderFactory.createTitledBorder(DATA_WRITING_LABEL));
        dataWritePanel.setName(DATA_WRITING_LABEL);

        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(timePanel);
        builder.addComponent(dataWritePanel);

        List<JComponent> actionsList = new ArrayList<JComponent>();
        JButton applyButton = new JButton(new WriteControlDictAction());
        applyButton.setName("Apply");
        actionsList.add(applyButton);

        JComponent buttonsPanel = UiUtil.getCommandRow(actionsList);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(buttonsPanel, BorderLayout.NORTH);
        mainPanel.add(builder.removeMargins().getPanel(), BorderLayout.CENTER);

        return mainPanel;
    }

    @Override
    public void load() {
        this.time = model.getState().getTime();
        loadControlDict();
        fixGUI();
    }

    @Override
    public void save() {
        ControlDict controlDict = getModel().getProject().getSystemFolder().getControlDict();
        if (controlDict != null) {
            boolean changed = hasControlDictChanged(controlDict);
            controlDict.merge(dictionaryModel.getDictionary());
            controlDict.add(STOP_AT_KEY, END_TIME_KEY);
            if (changed) {
                isSaving = true;
                model.projectChanged();
                isSaving = false;
            }
        }
    }

    private boolean hasControlDictChanged(ControlDict controlDict) {
        ControlDict d = new ControlDict(controlDict);
        d.remove(ControlDict.FUNCTIONS_KEY);
        return !d.toString().equals(dictionaryModel.getDictionary().toString());
    }

    @Override
    public void stateChanged() {
        super.stateChanged();
        State state = model.getState();
        if (this.time == null || state.getTime() != this.time) {
            this.time = state.getTime();
            loadControlDict();
        } else {
            /*
             * Il file controlDict ora contiene i valori di default. Lo mergio con i valori della GUI per non perdere i cambiamenti fatti. Ovviamente questo significa che quello che ce nella GUI...rimane!
             */
            Dictionary controlDict = model.getProject().getSystemFolder().getControlDict();
            if (controlDict != null) {
                controlDict.merge(dictionaryModel.getDictionary());
            }
        }
    }

    @Override
    public void projectChanged() {
        if (!isSaving) {
            loadControlDict();
        }
    }

    private void loadControlDict() {
        removeListeners();

        ControlDict controlDict = getModel().getProject().getSystemFolder().getControlDict();
        if (controlDict != null) {
            Dictionary dictionary = new Dictionary(controlDict);
            dictionary.remove(FUNCTIONS_KEY);
            dictionaryModel.setDictionary(dictionary);
        }

        addListeners();
    }

    private void removeListeners() {
        startFrom.removePropertyChangeListener("value", startFromListener);
        adjustableTime.removeActionListener(adjustableTimeListener);
    }

    private void addListeners() {
        startFrom.addPropertyChangeListener("value", startFromListener);
        adjustableTime.addActionListener(adjustableTimeListener);
    }

    public void fixGUI() {
        if (model.getSolverModel().getServerState().getSolverState().isRunning()) {
            ExecUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    _fixGUI();
                }
            });
        }
    }

    private void _fixGUI() {
        startTime.setEnabled(START_TIME_VALUE.equals(startFrom.getSelectedItem()));
        
        State state = model.getState();
        boolean isTransient = state.isTransient();
        boolean isSteadyMultiphase = state.isSteady() && state.getMultiphaseModel().isMultiphase();
        boolean isSteadyCoupled = state.isSteady() && state.getSolverType().isCoupled();

        if (isTransient || isSteadyMultiphase || isSteadyCoupled) {
            deltaT.setEnabled(true);
        } else {
            deltaT.setEnabled(false);
            deltaT.setDoubleValue(1);
        }

        adjustableTime.setEnabled((isTransient || isSteadyMultiphase) && !isSonic(state));
        maxCourantNumber.setEnabled((isTransient || isSteadyMultiphase) && adjustableTime.isSelected());
        maxAlphaCourant.setEnabled((isTransient || isSteadyMultiphase) && adjustableTime.isSelected() && state.getMultiphaseModel().isMultiphase());
        maxTimeStep.setEnabled((isTransient || isSteadyMultiphase) && adjustableTime.isSelected());
    }

    private boolean isSonic(State state) {
        return state.isHighMach() && state.getSolverFamily().isPimple();
    }

    private class WriteControlDictAction extends AbstractAction {

        public WriteControlDictAction() {
            super("Apply", APPLY_ICON);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            save();
            model.getProject().getSystemFolder().writeControlDict(model, new SilentMonitor());
        }
    }
}
