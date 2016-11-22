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
package eu.engys.gui.casesetup.solver;

import static eu.engys.core.project.state.SolverFamily.CENTRAL;
import static eu.engys.core.project.state.SolverFamily.COUPLED;
import static eu.engys.core.project.state.SolverFamily.PIMPLE;
import static eu.engys.core.project.state.SolverFamily.PISO;
import static eu.engys.core.project.state.SolverFamily.SIMPLE;
import static eu.engys.core.project.system.FvSchemes.FV_SCHEMES;
import static eu.engys.core.project.system.FvSolution.FV_SOLUTION;
import static eu.engys.core.project.system.SystemFolder.SYSTEM;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.SolverFamily;
import eu.engys.core.project.state.State;
import eu.engys.core.project.state.Table15;
import eu.engys.core.project.system.SystemFolder;
import eu.engys.gui.DefaultGUIPanel;
import eu.engys.gui.casesetup.solver.panels.CentralSettingsPanel;
import eu.engys.gui.casesetup.solver.panels.CoupledSettingsPanel;
import eu.engys.gui.casesetup.solver.panels.PimpleSettingsPanel;
import eu.engys.gui.casesetup.solver.panels.PisoSettingsPanel;
import eu.engys.gui.casesetup.solver.panels.SimpleSettingsPanel;
import eu.engys.gui.casesetup.solver.panels.SolverPanel;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.PanelBuilder;

public abstract class AbstractSolverSettingsPanel extends DefaultGUIPanel {

    private static final String EMPTY = "EMPTY";
    private static final String STATE_CHANGED_WARNING = "Solution state has been changed.\nAll fields default settings are going to be reset now.\nContinue?";

    public static final String SOLVER_SETTINGS = "Solver Settings";
    public static final String SOLUTION_ALGORITHM_LABEL = "Solution Algorithm";

    protected JComboBox<SolverFamily> algorithmCombo;
    private ActionListener algorithmActionListener;

    private Map<SolverFamily, SolverPanel> solverPanelMap = new LinkedHashMap<>();
    private Table15 solversTable;
    private Set<ApplicationModule> modules;

    public AbstractSolverSettingsPanel(Model model, Table15 solversTable, Set<ApplicationModule> modules) {
        super(SOLVER_SETTINGS, model);
        this.solversTable = solversTable;
        this.modules = modules;
    }

    @Override
    protected JComponent layoutComponents() {
        solverPanelMap.put(SIMPLE, new SimpleSettingsPanel());
        solverPanelMap.put(PIMPLE, new PimpleSettingsPanel());
        solverPanelMap.put(PISO, new PisoSettingsPanel());
        solverPanelMap.put(CENTRAL, new CentralSettingsPanel());
        solverPanelMap.put(COUPLED, new CoupledSettingsPanel());

        final CardLayout cardLayout = new CardLayout();
        final JPanel cardLayoutPanel = new JPanel(cardLayout);
        cardLayoutPanel.add(new JLabel(""), EMPTY);
        cardLayoutPanel.setOpaque(false);

        algorithmCombo = createAlgorithmsCombo(cardLayout, cardLayoutPanel);
        algorithmActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionevent) {
                SolverFamily selectedType = (SolverFamily) algorithmCombo.getSelectedItem();
                if (selectedType != null) {
                    cardLayout.show(cardLayoutPanel, selectedType.key());
                    fixPIMPLE_PISOSolver(model);
                    fixPIMPLE_CENTRALSolver(model);
                    solverPanelMap.get(selectedType).load(model);
                    solverPanelMap.get(selectedType).getPanel().add(getLimiterPanel(), BorderLayout.CENTER);
                } else {
                    cardLayout.show(cardLayoutPanel, EMPTY);
                }
            }
        };
        algorithmCombo.addActionListener(algorithmActionListener);

        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(SOLUTION_ALGORITHM_LABEL, algorithmCombo);
        builder.addFill(cardLayoutPanel);
        return builder.removeMargins().getPanel();
    }

    protected abstract JPanel getLimiterPanel();

    private JComboBox<SolverFamily> createAlgorithmsCombo(final CardLayout cardLayout, final JPanel cardLayoutPanel) {
        JComboBox<SolverFamily> combo = new JComboBox<SolverFamily>();
        combo.setPrototypeDisplayValue(SolverFamily.CENTRAL);
        final ListCellRenderer<? super SolverFamily> renderer = combo.getRenderer();
        combo.setRenderer(new ListCellRenderer<SolverFamily>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends SolverFamily> list, SolverFamily value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JLabel && value instanceof SolverFamily) {
                    SolverFamily model = (SolverFamily) value;
                    ((JLabel) c).setText(model.label());
                }
                return c;
            }
        });

        combo.setEnabled(false);
        for (SolverFamily solver : solverPanelMap.keySet()) {
            cardLayoutPanel.add(solverPanelMap.get(solver).getPanel(), solver.key());
        }
        combo.setSelectedIndex(-1);
        return combo;
    }

    @Override
    public void stateChanged() {
        loadLater();
    }

    @Override
    public void materialsChanged() {
        loadLater();
    }

    @Override
    public void fieldsChanged() {
        loadLater();
    }

    private void loadLater() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                load();
            }
        });
    }

    @Override
    public void load() {
        if (model.hasProject()) {
            Set<SolverFamily> solverFamilies = new LinkedHashSet<>();
            solversTable.updateSolverFamilies(model.getState(), solverFamilies);
            ModulesUtil.updateSolverFamilies(modules, model.getState(), solverFamilies);

            loadSolverPanels(solverFamilies);
            populateCombo(solverFamilies);
            fixComboSelection();
        }
    }

    private void loadSolverPanels(Set<SolverFamily> solverFamiliesForState) {
        Dictionary fvSolution = model.getProject().getSystemFolder().getFvSolution();
        if (fvSolution != null) {
            leaveOneSolverDictionaryOnFvSolution(fvSolution);
            for (SolverFamily family : solverFamiliesForState) {
                solverPanelMap.get(family).load(model);
            }
        }

    }

    private void leaveOneSolverDictionaryOnFvSolution(Dictionary fvSolution) {
        if (fvSolution.found(SolverFamily.SIMPLE.key())) {
            if (fvSolution.found(SolverFamily.PIMPLE.key()) || fvSolution.found(SolverFamily.PISO.key()) || fvSolution.found(SolverFamily.CENTRAL.key())) {
                fvSolution.remove(SolverFamily.SIMPLE.key());
            }
        }
    }

    private void populateCombo(Set<SolverFamily> solverFamiliesForState) {
        algorithmCombo.removeAllItems();
        algorithmCombo.removeActionListener(algorithmActionListener);
        for (SolverFamily algo : solverFamiliesForState) {
            algorithmCombo.addItem(algo);
        }
        algorithmCombo.addActionListener(algorithmActionListener);
    }

    private void fixComboSelection() {
        algorithmCombo.setEnabled(true);
        SolverFamily solverFamily = model.getState().getSolverFamily();
        if (solverFamily.isNone() || algorithmCombo.getItemCount() == 0) {
            algorithmCombo.setSelectedIndex(-1);
            algorithmCombo.setEnabled(false);
        } else {
            algorithmCombo.setEnabled(true);
            boolean itemNotInComboBox = ((DefaultComboBoxModel<SolverFamily>) algorithmCombo.getModel()).getIndexOf(solverFamily) == -1;
            if (itemNotInComboBox) {
                algorithmCombo.setSelectedIndex(0);
            } else {
                algorithmCombo.setSelectedItem(solverFamily);
            }
            if (algorithmCombo.getItemCount() < 2) {
                algorithmCombo.setEnabled(false);
            }
        }
    }

    @Override
    public boolean canStop() {
        if (stateHasChanged()) {
            if (JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), STATE_CHANGED_WARNING, "State Changed", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void save() {
        fixPIMPLE_PISOSolver(model);
        fixPIMPLE_CENTRALSolver(model);
        if (algorithmCombo.getSelectedIndex() != -1) {
            SolverPanel selectedSolverPanel = solverPanelMap.get(algorithmCombo.getSelectedItem());
            SolverSettingsBuilder.build(model, selectedSolverPanel);
        }
    }

    private boolean stateHasChanged() {
        State state = model.getState();
        SolverFamily family = state.getSolverFamily();
        if (state.isTransient() && state.isIncompressible()) {
            return family.isPiso() && isPIMPLE() || family.isPimple() && isPISO();
        }
        if (state.isTransient() && state.isCompressible() && state.isHighMach()) {
            return family.isCentral() && isPIMPLE() || family.isPimple() && isCENTRAL();
        }
        return false;
    }

    private boolean isCENTRAL() {
        SolverFamily selectedItem = (SolverFamily) algorithmCombo.getSelectedItem();
        return selectedItem.isCentral();
    }

    private boolean isPISO() {
        SolverFamily selectedItem = (SolverFamily) algorithmCombo.getSelectedItem();
        return selectedItem.isPiso();
    }

    private boolean isPIMPLE() {
        SolverFamily selectedItem = (SolverFamily) algorithmCombo.getSelectedItem();
        return selectedItem.isPimple();
    }

    private void fixPIMPLE_PISOSolver(Model model) {
        State state = model.getState();
        if (state.isTransient() && state.isIncompressible()) {
            SystemFolder systemFolder = model.getProject().getSystemFolder();
            Dictionary fvSolution = systemFolder.getFvSolution();

            Dictionary stateData = model.getDefaults().getDefaultStateData();
            Dictionary pisoSolution = stateData.subDict("pisoFoamRAS").subDict(SYSTEM).subDict(FV_SOLUTION);
            Dictionary pimpleSolution = stateData.subDict("pimpleFoamRAS").subDict(SYSTEM).subDict(FV_SOLUTION);

            if (isPISO() && fvSolution.found(SolverFamily.PIMPLE.key())) {
                systemFolder.setFvSolution(pisoSolution);
                state.setSolverFamily(SolverFamily.PISO);
                solversTable.updateSolver(state);
                ModulesUtil.updateSolver(modules, state);
                model.solverChanged();
            } else if (isPIMPLE() && fvSolution.found(SolverFamily.PISO.key())) {
                systemFolder.setFvSolution(pimpleSolution);
                state.setSolverFamily(SolverFamily.PIMPLE);
                solversTable.updateSolver(state);
                ModulesUtil.updateSolver(modules, state);
                model.solverChanged();
            }
        }
    }

    private void fixPIMPLE_CENTRALSolver(Model model) {
        State state = model.getState();
        if (state.isTransient() && state.isCompressible() && state.isHighMach()) {
            SystemFolder systemFolder = model.getProject().getSystemFolder();
            if (isCENTRAL() && state.getSolverFamily().isPimple()) {
                state.setSolverFamily(SolverFamily.CENTRAL);
                solversTable.updateSolver(state);
                ModulesUtil.updateSolver(modules, state);

                Dictionary stateData = model.getDefaults().getDefaultsFor(state);
                Dictionary solutionDict = stateData.subDict(SYSTEM).subDict(FV_SOLUTION);
                Dictionary schemesDict = stateData.subDict(SYSTEM).subDict(FV_SCHEMES);

                systemFolder.setFvSolution(solutionDict);
                systemFolder.setFvSchemes(schemesDict);

                model.solverChanged();
            } else if (isPIMPLE() && state.getSolverFamily().isCentral()) {
                state.setSolverFamily(SolverFamily.PIMPLE);
                solversTable.updateSolver(state);
                ModulesUtil.updateSolver(modules, state);

                Dictionary stateData = model.getDefaults().getDefaultsFor(state);
                Dictionary solutionDict = stateData.subDict(SYSTEM).subDict(FV_SOLUTION);
                Dictionary schemesDict = stateData.subDict(SYSTEM).subDict(FV_SCHEMES);

                systemFolder.setFvSolution(solutionDict);
                systemFolder.setFvSchemes(schemesDict);

                model.solverChanged();
            }
        }
    }

}
