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
package eu.engys.gui.casesetup.solution.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.util.Set;

import javax.swing.JPanel;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.project.state.Flow;
import eu.engys.core.project.state.Mach;
import eu.engys.core.project.state.Method;
import eu.engys.core.project.state.SolutionState;
import eu.engys.core.project.state.SolverType;
import eu.engys.core.project.state.State;
import eu.engys.core.project.state.Time;

public class SolutionStatePanel extends JPanel {

    protected SolverTypePanel solverTypePanel;
    protected TimePanel timePanel;
    protected FlowPanel flowPanel;
    protected MethodPanel methodPanel;
    protected MachPanel machPanel;

    private PropertyChangeListener listener;
    private Set<ApplicationModule> modules;

    public SolutionStatePanel(Set<ApplicationModule> modules, boolean visibleSolverType) {
        super(new GridBagLayout());
        this.modules = modules;
        layoutComponents(visibleSolverType);
    }

    private void layoutComponents(boolean visibleSolverType) {
        solverTypePanel = new SolverTypePanel();
        timePanel = new TimePanel();
        flowPanel = new FlowPanel();
        methodPanel = new MethodPanel();
        machPanel = new MachPanel();

        if (visibleSolverType) {
            add(solverTypePanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            add(timePanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            add(flowPanel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            add(methodPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            add(machPanel, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        } else {
            add(timePanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            add(flowPanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            add(methodPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            add(machPanel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }

        for (ApplicationModule m : modules) {
            m.getSolutionView().buildSolution(solverTypePanel);
        }
    }

    public void updateFromState(State state) {
        solverTypePanel.updateFromState(state);
        timePanel.updateFromState(state);
        flowPanel.updateFromState(state);
        methodPanel.updateFromState(state);
        machPanel.updateFromState(state);
    }

    public void fix(SolutionState ss) {
        if (ss.isSolverNone()) {
            timePanel.setEnabled(false);
            methodPanel.setEnabled(false);
            flowPanel.setEnabled(false);
            machPanel.setEnabled(false);
        } else {
            timePanel.setEnabled(true);
            if (ss.isSegregated()) {
                if (ss.isTimeNone()) {
                    methodPanel.setEnabled(false);
                    flowPanel.setEnabled(false);
                    machPanel.setEnabled(false);
                } else {
                    methodPanel.setEnabled(true);
                    flowPanel.setEnabled(true);
                    machPanel.setEnabled(true);
                    if (ss.isSteady()) {
                        methodPanel.setEnabled(false);
                        methodPanel.select(Method.RANS.label());
                        machPanel.setEnabled(false);
                        machPanel.select(Mach.LOW.label());
                    } else if (ss.isTransient()) {
                        if (ss.isFlowNone()) {
                            methodPanel.setEnabled(false);
                            machPanel.setEnabled(false);
                        } else {
                            methodPanel.setEnabled(true);
                            if (ss.isCompressible()) {
                                machPanel.setEnabled(true);
                                if (ss.isMachNone()) {
                                    machPanel.select(Mach.LOW.label());
                                }
                            } else if (ss.isIncompressible()) {
                                machPanel.setEnabled(false);
                                machPanel.select(Mach.LOW.label());
                            }
                        }
                    }
                }
            } else if (ss.isCoupled()) {
                flowPanel.select(Flow.INCOMPRESSIBLE.label());
                flowPanel.setEnabled(false);
                if (ss.isSteady()) {
                    methodPanel.select(Method.RANS.label());
                    methodPanel.setEnabled(false);
                } else if(ss.isTransient()){
                    methodPanel.setEnabled(true);
                }
                machPanel.select(Mach.LOW.label());
                machPanel.setEnabled(false);
            }
        }
    }

    public SolutionState getSolutionState() {
        SolutionState ss = new SolutionState();
        ss.time = timePanel.getSelectedState();
        ss.flow = flowPanel.getSelectedState();
        ss.method = methodPanel.getSelectedState();
        ss.solverType = solverTypePanel.getSelectedState();
        ss.mach = machPanel.getSelectedState();
        return ss;
    }

    public void setListener(PropertyChangeListener listener) {
        this.listener = listener;
    }

    public void removeListeners() {
        solverTypePanel.removePropertyChangeListener(listener);
        timePanel.removePropertyChangeListener(listener);
        flowPanel.removePropertyChangeListener(listener);
        methodPanel.removePropertyChangeListener(listener);
        machPanel.removePropertyChangeListener(listener);
    }

    public void addListeners() {
        solverTypePanel.addPropertyChangeListener(listener);
        timePanel.addPropertyChangeListener(listener);
        flowPanel.addPropertyChangeListener(listener);
        methodPanel.addPropertyChangeListener(listener);
        machPanel.addPropertyChangeListener(listener);
    }

    public SolverTypePanel getSolverTypePanel() {
        return solverTypePanel;
    }

    public SolverType getSolverType() {
        return solverTypePanel.getSolverType();
    }

    public Time getTime() {
        return timePanel.getTime();
    }

    public Flow getFlow() {
        return flowPanel.getFlow();
    }

    public Method getMethod() {
        return methodPanel.getMethod();
    }

    public Mach getMach() {
        return machPanel.getMach();
    }

    public boolean isCoupled() {
        return solverTypePanel.isCoupled();
    }

    public boolean isSegregated() {
        return solverTypePanel.isSegregated();
    }

    public boolean isCompressible() {
        return flowPanel.isCompressible();
    }

    public boolean isIncompressible() {
        return flowPanel.isIncompressible();
    }

    public boolean isLES() {
        return methodPanel.isLES();
    }

    public boolean isRAS() {
        return methodPanel.isRAS();
    }

}
