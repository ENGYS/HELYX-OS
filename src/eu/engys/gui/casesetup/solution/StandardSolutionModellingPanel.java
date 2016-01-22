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

import java.util.Set;

import javax.inject.Inject;
import javax.swing.JComponent;
import javax.swing.JLabel;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.SolutionState;
import eu.engys.core.project.state.Table15;
import eu.engys.gui.casesetup.solution.panels.AbstractThermalPanel;
import eu.engys.gui.casesetup.solution.panels.MultiphasePanel;
import eu.engys.util.ui.builder.PanelBuilder;

public class StandardSolutionModellingPanel extends AbstractSolutionModellingPanel {

    @Inject
    public StandardSolutionModellingPanel(Model model, Table15 solversTable, Set<ApplicationModule> modules) {
        super(model, solversTable, modules);
    }

    @Override
    protected MultiphasePanel createMultiphasePanel() {
        return new MultiphasePanel(modules) {
            public void fixSolutionState(SolutionState ss) {
                if (ss.areSolverTypeAndTimeAndFlowAndTurbulenceChoosen()) {
                    if (ss.isCoupled() || ss.isSteady() || ss.isCompressible()) {
                        if (isMultiphaseOn()) {
                            setMultiphaseOff();
                        }
                        multiphaseBuilder.setEnabled(false);
                        phasesNumber.setIntValue(1);
                        phasesNumber.setEnabled(false);
                    } else {
                        multiphaseBuilder.setEnabled(true);
                        // MODULES
                    }
                } else {
                    multiphaseBuilder.setEnabled(false);
                    phasesNumber.setIntValue(1);
                    phasesNumber.setEnabled(false);
                }
            }
        };
    }
    
    @Override
    protected boolean isSolutionStatePanelVisible() {
        return false;
    }

    @Override
    protected AbstractThermalPanel createThermalPanel() {
        return new StandardThermalPanel(modules);
    }

    @Override
    public PanelBuilder getDynamicBuilder() {
        return new PanelBuilder();
    }

    @Override
    public PanelBuilder getScalarsBuilderLeft() {
        return new PanelBuilder();
    }

    @Override
    public PanelBuilder getScalarsBuilderRight() {
        return new PanelBuilder();
    }

    @Override
    protected JComponent getDynamicPanel() {
        return new JLabel();
    }

    @Override
    protected JComponent getScalarsPanel() {
        return new JLabel();
    }

}
