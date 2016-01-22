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

package eu.engys.gui.casesetup.solution;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.modules.solutionmodelling.SolutionModellingPanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.TurbulenceModel;
import eu.engys.core.project.state.BuoyancyBuilder;
import eu.engys.core.project.state.Flow;
import eu.engys.core.project.state.Mach;
import eu.engys.core.project.state.Method;
import eu.engys.core.project.state.MultiphaseModel;
import eu.engys.core.project.state.SolutionState;
import eu.engys.core.project.state.SolverFamily;
import eu.engys.core.project.state.SolverType;
import eu.engys.core.project.state.State;
import eu.engys.core.project.state.StateBuilder;
import eu.engys.core.project.state.Table15;
import eu.engys.core.project.state.ThermalState;
import eu.engys.core.project.state.Time;
import eu.engys.gui.DefaultGUIPanel;
import eu.engys.gui.casesetup.solution.panels.AbstractThermalPanel;
import eu.engys.gui.casesetup.solution.panels.GPanel;
import eu.engys.gui.casesetup.solution.panels.MultiphasePanel;
import eu.engys.gui.casesetup.solution.panels.SolutionStatePanel;
import eu.engys.gui.casesetup.solution.panels.TurbulencePanel;
import eu.engys.util.ui.ChooserPanel;
import eu.engys.util.ui.UiUtil;

public abstract class AbstractSolutionModellingPanel extends DefaultGUIPanel implements SolutionModellingPanel {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSolutionModellingPanel.class);

    private static final String STATE_CHANGED_WARNING = "Solution state has been changed.\nAll fields default settings are going to be reset now.\nContinue?";

    public static final String SOLUTION_MODELLING = "Solution Modelling";

    protected Set<ApplicationModule> modules;
    private Table15 solversTable;

    private SolutionStatePanel solutionStatePanel;
    private MultiphasePanel multiphasePanel;
    private AbstractThermalPanel thermalPanel;
    private GPanel gPanel;
    private TurbulencePanel turbulencePanel;

    public AbstractSolutionModellingPanel(Model model, Table15 solversTable, Set<ApplicationModule> modules) {
        super(SOLUTION_MODELLING, model);
        this.solversTable = solversTable;
        this.modules = modules;
    }

    @Override
    protected JComponent layoutComponents() {
        JPanel topPanel = new JPanel(new GridBagLayout());

        solutionStatePanel = new SolutionStatePanel(modules, isSolutionStatePanelVisible());

        turbulencePanel = new TurbulencePanel();

        multiphasePanel = createMultiphasePanel();
        final PropertyChangeListener multiphaseListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("selection")) {
                    fix();
                }
            }
        };
        multiphasePanel.setListener(multiphaseListener);

        thermalPanel = createThermalPanel();
        ActionListener thermalListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fix();
            }
        };
        thermalPanel.setThermalListener(thermalListener);

        gPanel = new GPanel();

        PropertyChangeListener solutionStateListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("selection")) {
                    multiphasePanel.removeListener();
                    thermalPanel.removeListeners();

                    fix();

                    thermalPanel.addListeners();
                    multiphasePanel.addListener();
                }
            }
        };
        solutionStatePanel.setListener(solutionStateListener);

        for (ApplicationModule m : modules) {
            m.getSolutionView().buildDynamic(getDynamicBuilder());
        }

        for (ApplicationModule m : modules) {
            m.getSolutionView().buildScalar(this);
        }

        topPanel.add(solutionStatePanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.add(turbulencePanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        topPanel.add(multiphasePanel, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.add(thermalPanel, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.add(gPanel, new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.add(getDynamicPanel(), new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.add(getScalarsPanel(), new GridBagConstraints(0, 6, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        topPanel.add(new JLabel(), new GridBagConstraints(0, 7, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        fix();

        return topPanel;
    }
    
    protected abstract boolean isSolutionStatePanelVisible();

    @Override
    public void load() {
        removeListeners();
        updateGUIFromState();
        addListeners();
    }

    @Override
    public void save() {
        if (stateHasChanged()) {
            if (JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), STATE_CHANGED_WARNING, "State Changed", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
                _save();
            } else {
                load();
            }
        }
        BuoyancyBuilder.save(model, gPanel.getGValue());
    }

    public void saveAnyway() {
        _save();
        BuoyancyBuilder.save(model, gPanel.getGValue());
    }

    private void _save() {
        updateStateFromGUI();
        StateBuilder.changeState(model, modules);
    }

    private void updateStateFromGUI() {
        State state = getStateFromGUI();
        model.setState(state);
    }

    private boolean stateHasChanged() {
        if (model.getPatches().isEmpty())
            return false;

        State state = model.getState();

        SolverType solverType = solutionStatePanel.getSolverType();
        Time time = solutionStatePanel.getTime();
        Flow flow = solutionStatePanel.getFlow();
        Method method = solutionStatePanel.getMethod();
        Mach mach = solutionStatePanel.getMach();

        boolean energy = thermalPanel.isEnergySelected();
        boolean buoyant = thermalPanel.isBuoyancySelected();

        MultiphaseModel multiphase = this.multiphasePanel.getSelectedModel();
        int phases = this.multiphasePanel.getPhasesNumber();

        TurbulenceModel turbulenceModel = turbulencePanel.getSelectedTurbulenceModel();

        if (state.getSolverType() != solverType) {
            logger.info("SOLVER TYPE [{} -> {}]", state.getSolverType(), solverType);
            return true;
        }
        if (state.getFlow() != flow) {
            logger.info("FLOW [{} -> {}]", state.getFlow(), flow);
            return true;
        }
        if (state.getTime() != time) {
            logger.info("TIME [{} -> {}]", state.getTime(), time);
            return true;
        }
        if (state.getMethod() != method) {
            logger.info("METHOD [{} -> {}]", state.getMethod(), method);
            return true;
        }
        if (state.getMach() != mach) {
            logger.info("MACH [{} -> {}]", state.getMach(), mach);
            return true;
        }
        if (state.isEnergy() != energy) {
            logger.info("ENERGY [{} -> {}]", state.isEnergy(), energy);
            return true;
        }
        if (state.isBuoyant() != buoyant) {
            logger.info("BUOYANT [{} -> {}]", state.isBuoyant(), buoyant);
            return true;
        }
        if (turbulenceModel != null && !turbulenceModel.equals(state.getTurbulenceModel())) {
            logger.info("TURBULENCE [{} -> {}]", state.getTurbulenceModel(), turbulenceModel);
            return true;
        }
        if (state.getMultiphaseModel() != multiphase) {
            logger.info("MULTIPHASE [{} -> {}]", state.getMultiphaseModel().getLabel(), multiphase.getLabel());
            return true;
        }
        if (state.getPhases() != phases) {
            logger.info("PHASEs [{} -> {}]", state.getPhases(), phases);
            return true;
        }

        for (ApplicationModule m : modules) {
            if (m.getSolutionView().hasChanged()) {
                return true;
            }
        }

        return false;
    }

    private State getStateFromGUI() {
        State state = new State();

        SolverType solverType = solutionStatePanel.getSolverType();
        Time time = solutionStatePanel.getTime();
        Flow flow = solutionStatePanel.getFlow();
        Method method = solutionStatePanel.getMethod();
        Mach mach = solutionStatePanel.getMach();

        boolean energy = thermalPanel.isEnergySelected();
        boolean buoyant = thermalPanel.isBuoyancySelected();

        MultiphaseModel multiphase = this.multiphasePanel.getSelectedModel();
        int phases = this.multiphasePanel.getPhasesNumber();

        TurbulenceModel turbulenceModel = turbulencePanel.getSelectedTurbulenceModel();

        if (state.getSolverType() != solverType) {
            state.setSolverType(solverType);
        }
        if (state.getFlow() != flow) {
            state.setFlow(flow);
        }
        if (state.getTime() != time) {
            state.setTime(time);
        }
        if (state.getMethod() != method) {
            state.setMethod(method);
        }
        if (state.getMach() != mach) {
            state.setMach(mach);
        }
        if (state.isEnergy() != energy) {
            state.setEnergy(energy);
        }
        if (state.isBuoyant() != buoyant) {
            state.setBuoyant(buoyant);
        }
        if (turbulenceModel != null && !turbulenceModel.equals(state.getTurbulenceModel())) {
            state.setTurbulenceModel(turbulenceModel);
        }
        if (state.getMultiphaseModel() != multiphase) {
            state.setMultiphaseModel(multiphase);
        }
        if (state.getPhases() != phases) {
            state.setPhases(phases);
        }

        ModulesUtil.updateStateFromGUI(modules);

        // Solver Families
        Set<SolverFamily> solverFamilies = new LinkedHashSet<SolverFamily>();
        solversTable.updateSolverFamilies(state, solverFamilies);
        ModulesUtil.updateSolverFamilies(modules, state, solverFamilies);
        
        if (solverFamilies.isEmpty()) {
            state.setSolverFamily(SolverFamily.NONE);
        } else {
            state.setSolverFamily(solverFamilies.iterator().next());
        }

        // Solver
        solversTable.updateSolver(state);
        ModulesUtil.updateSolver(modules, state);

        return state;
    }

    private void updateGUIFromState() {
        State state = model.getState();

        solutionStatePanel.updateFromState(state);
        solutionStatePanel.fix(new SolutionState(state));

        multiphasePanel.updateFromState(state);

        thermalPanel.updateEnergyFromState(state);
        thermalPanel.updateBuoyancyFromState(state);
        gPanel.updateFromState(model, state);

        turbulencePanel.updateFromState(model, state);

        for (ApplicationModule m : modules) {
            m.getSolutionView().updateGUIFromState(state);
        }
    }

    private void fix() {
        SolutionState ss = solutionStatePanel.getSolutionState();
        solutionStatePanel.fix(ss);
        multiphasePanel.fixSolutionState(ss);
        for (ApplicationModule m : modules) {
            m.getSolutionView().fixSolutionState(ss);
        }

        MultiphaseModel mm = multiphasePanel.getSelectedModel();
        multiphasePanel.fixMultiphase(mm);
        for (ApplicationModule m : modules) {
            m.getSolutionView().fixMultiphase(mm);
        }

        thermalPanel.fixEnergy(ss, mm);
        thermalPanel.fixBuoyancy(ss, mm);

        ThermalState ts = thermalPanel.getThermalState();
        gPanel.fix(ss, mm, ts);
        for (ApplicationModule m : modules) {
            m.getSolutionView().fixThermal(ss, ts);
        }

        turbulencePanel.fixSolutionState(model, ss);
    }

    private void removeListeners() {
        multiphasePanel.removeListener();
        thermalPanel.removeListeners();
        solutionStatePanel.removeListeners();
    }

    private void addListeners() {
        multiphasePanel.addListener();
        thermalPanel.addListeners();
        solutionStatePanel.addListeners();
    }

    @Override
    public ChooserPanel getSolverTypePanel() {
        return solutionStatePanel.getSolverTypePanel();
    }

    @Override
    public MultiphasePanel getMultiphasePanel() {
        return multiphasePanel;
    }

    protected abstract AbstractThermalPanel createThermalPanel();

    protected abstract MultiphasePanel createMultiphasePanel();

    protected abstract JComponent getDynamicPanel();

    protected abstract JComponent getScalarsPanel();

    // For test purpose only
    public void setModules(Set<ApplicationModule> modules) {
        this.modules = modules;
    }
}
