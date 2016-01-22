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

package eu.engys.gui.casesetup;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.Solver;
import eu.engys.core.project.state.State;
import eu.engys.core.project.system.ControlDict;
import eu.engys.gui.DefaultGUIPanel;
import eu.engys.util.Symbols;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.SelectionValueConfigurator;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;

public class RuntimeControlsPanel extends DefaultGUIPanel {

    public static final String RUNTIME_CONTROLS = "Runtime Controls";

    public static final String DATA_WRITING_LABEL = "Data Writing";
    public static final String TIME_SETTINGS_LABEL = "Time Settings";
    public static final String GRAPH_FORMAT_LABEL = "Graph Format";
    public static final String TIME_PRECISION_LABEL = "Time Precision";
    public static final String TIME_FORMAT_LABEL = "Time Format";
    public static final String WRITE_COMPRESSION_LABEL = "Write Compression";
    public static final String WRITE_PRECISION_LABEL = "Write Precision";
    public static final String WRITE_FORMAT_LABEL = "Write Format";
    public static final String PURGE_WRITE_LABEL = "Purge Write";
    public static final String WRITE_CONTROL_LABEL = "Write Control";
    public static final String MAX_TIME_STEP_LABEL = "Max Time Step";
    public static final String MAX_COURANT_ALPHA_LABEL = "Max Courant Alpha";
    public static final String MAX_COURANT_NUMBER_LABEL = "Max Courant Number";
    public static final String ADJUSTABLE_TIME_STEP_LABEL = "Adjustable Time Step";
    public static final String DELTA_T_LABEL = Symbols.DELTA_T + "(s)";
    public static final String END_TIME_LABEL = "End Time";
    public static final String START_FROM_LABEL = "Start From";

    // public static final String J_PLOT_LABEL = "JPlot";
    // public static final String GRACE_XMRG_LABEL = "Grace/XMRG";
    // public static final String GNU_PLOT_LABEL = "GNUPlot";
    public static final String RAW_LABEL = "Raw";
    // public static final String SCIENTIFIC_LABEL = "Scientific";
    // public static final String FIXED_LABEL = "Fixed";
    public static final String GENERAL_LABEL = "General";
    public static final String COMPRESSED_LABEL = "Compressed";
    public static final String UNCOMPRESSED_LABEL = "Uncompressed";
    // public static final String BINARY_LABEL = "Binary";
    public static final String ASCII_LABEL = "ASCII";
    public static final String CLOCK_TIME_LABEL = "Clock Time";
    public static final String CPU_TIME_LABEL = "CPU Time";
    public static final String RUN_TIME_LABEL = "Run Time";
    public static final String TIME_STEP_LABEL = "Time Step";
    public static final String START_TIME_LABEL = "Start Time";
    public static final String LATEST_TIME_LABEL = "Latest Time";
    public static final String FIRST_TIME_LABEL = "First Time";
    public static final String[] START_FROM_LABELS = { FIRST_TIME_LABEL, LATEST_TIME_LABEL, START_TIME_LABEL };
    public static final String[] WRITE_CONTROL_LABELS = { TIME_STEP_LABEL, RUN_TIME_LABEL, CPU_TIME_LABEL, CLOCK_TIME_LABEL };
    public static final String[] WRITE_FORMAT_LABELS = { ASCII_LABEL };
    // public static final String[] WRITE_FORMAT_LABELS = { ASCII_LABEL,
    // BINARY_LABEL };
    public static final String[] WRITE_COMPRESSION_LABELS = { UNCOMPRESSED_LABEL, COMPRESSED_LABEL };
    public static final String[] TIME_FORMAT_LABELS = { GENERAL_LABEL };
    // public static final String[] TIME_FORMAT_LABELS = { GENERAL_LABEL,
    // FIXED_LABEL, SCIENTIFIC_LABEL };
    public static final String[] GRAPH_FORMAT_LABELS = { RAW_LABEL };
    // public static final String[] GRAPH_FORMAT_LABELS = { RAW_LABEL,
    // GNU_PLOT_LABEL, GRACE_XMRG_LABEL, J_PLOT_LABEL };

    private DictionaryModel dictionaryModel;
    private JCheckBox adjustableTime;
    private JComponent maxCourantNumber;
    private JComponent maxAlphaCourant;
    private JComponent maxTimeStep;
    private DoubleField deltaT;
    private Solver solver = null;

    private boolean isSaving = false;

    @Inject
    public RuntimeControlsPanel(Model model) {
        super(RUNTIME_CONTROLS, model);
    }

    @Override
    public void start() {
        super.start();
        updatePanel(model.getState());
    }

    protected JComponent layoutComponents() {
        dictionaryModel = new DictionaryModel(new Dictionary(""));
        PanelBuilder timeBuilder = new PanelBuilder();

        JComboBox<?> startFrom = dictionaryModel.bindSelection(START_FROM_KEY, START_FROM_VALUES, START_FROM_LABELS);
        final DoubleField startTime = dictionaryModel.bindDouble(START_TIME_KEY);
        DoubleField endTime = dictionaryModel.bindDouble(END_TIME_KEY);

        timeBuilder.addComponent(START_FROM_LABEL, startFrom, startTime);
        timeBuilder.addComponent(END_TIME_LABEL, endTime);

        startTime.setEnabled(false);
        startFrom.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                startTime.setEnabled(START_TIME_VALUE.equals(evt.getNewValue()));
            }
        });

        deltaT = dictionaryModel.bindDouble(DELTA_T_KEY);
        timeBuilder.addComponent(DELTA_T_LABEL, deltaT);

        adjustableTime = dictionaryModel.bindBoolean(ADJUST_TIME_STEP_KEY);
        maxCourantNumber = dictionaryModel.bindDouble(MAX_CO_KEY);
        maxAlphaCourant = dictionaryModel.bindDouble(MAX_ALPHA_CO_KEY);
        maxTimeStep = dictionaryModel.bindDouble(MAX_DELTA_T_KEY);

        timeBuilder.addComponent(ADJUSTABLE_TIME_STEP_LABEL, adjustableTime);
        timeBuilder.addComponent(MAX_COURANT_NUMBER_LABEL, maxCourantNumber);
        timeBuilder.addComponent(MAX_COURANT_ALPHA_LABEL, maxAlphaCourant);
        timeBuilder.addComponent(MAX_TIME_STEP_LABEL, maxTimeStep);

        adjustableTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maxCourantNumber.setEnabled(adjustableTime.isSelected());
                maxAlphaCourant.setEnabled(adjustableTime.isSelected() && model.getState().getMultiphaseModel().isMultiphase());
                maxTimeStep.setEnabled(adjustableTime.isSelected());
            }
        });
        adjustableTime.setSelected(false);
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

        JComboBox<String> writeFormat = dictionaryModel.bindSelection(WRITE_FORMAT_KEY, WRITE_FORMAT_VALUES, WRITE_FORMAT_LABELS);
        writeFormat.setEnabled(false);
        dataWriteBuilder.addComponent(WRITE_FORMAT_LABEL, writeFormat);

        dataWriteBuilder.addComponent(WRITE_PRECISION_LABEL, dictionaryModel.bindIntegerPositive(WRITE_PRECISION_KEY));
        dataWriteBuilder.addComponent(WRITE_COMPRESSION_LABEL, dictionaryModel.bindSelection(WRITE_COMPRESSION_KEY, WRITE_COMPRESSION_VALUES, WRITE_COMPRESSION_LABELS));

        JComboBox<String> timeFormat = dictionaryModel.bindSelection(TIME_FORMAT_KEY, TIME_FORMAT_VALUES, TIME_FORMAT_LABELS);
        timeFormat.setEnabled(false);
        dataWriteBuilder.addComponent(TIME_FORMAT_LABEL, timeFormat);

        dataWriteBuilder.addComponent(TIME_PRECISION_LABEL, dictionaryModel.bindIntegerPositive(TIME_PRECISION_KEY));

        JComboBox<String> graphFormat = dictionaryModel.bindSelection(GRAPH_FORMAT_KEY, GRAPH_FORMAT_VALUE, GRAPH_FORMAT_LABELS);
        graphFormat.setEnabled(false);
        dataWriteBuilder.addComponent(GRAPH_FORMAT_LABEL, graphFormat);

        JPanel timePanel = timeBuilder.margins(.5, .5, .5, .5).getPanel();
        timePanel.setBorder(BorderFactory.createTitledBorder(TIME_SETTINGS_LABEL));
        timePanel.setName(TIME_SETTINGS_LABEL);

        JPanel dataWritePanel = dataWriteBuilder.margins(.5, .5, .5, .5).getPanel();
        dataWritePanel.setBorder(BorderFactory.createTitledBorder(DATA_WRITING_LABEL));
        dataWritePanel.setName(DATA_WRITING_LABEL);

        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(timePanel);
        builder.addComponent(dataWritePanel);

        return builder.removeMargins().getPanel();
    }

    @Override
    public void load() {
        loadControlDict();
        updatePanel(model.getState());
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

        if (this.solver == null || !(state.getSolver().equals(this.solver))) {
            this.solver = state.getSolver();
            loadControlDict();
            updatePanel(state);
        } else {
            /*
             * Entro qua se ho cambiato solo turbulence model e quindi non serve
             * svrazzare via quello che ce nella GUI Il file controlDict ora
             * contiene i valori di default. Lo mergio con i valori della GUI
             * per non perdere i cambiamenti fatti. Ovviamente questo significa
             * che quello che ce nella GUI...rimane!
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
        ControlDict controlDict = model.getProject().getSystemFolder().getControlDict();
        if (controlDict != null) {
            Dictionary dictionary = new Dictionary(controlDict);
            dictionary.remove(FUNCTIONS_KEY);
            dictionaryModel.setDictionary(dictionary);
        }
    }

    private void updatePanel(final State state) {
        this.solver = state.getSolver();
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    private boolean isSonic(State state) {
        return state.isHighMach() && state.getSolverFamily().isPimple();
    }
}
