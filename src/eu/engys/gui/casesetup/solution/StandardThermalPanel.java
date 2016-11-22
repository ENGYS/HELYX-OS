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
package eu.engys.gui.casesetup.solution;

import java.util.Set;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.project.state.MultiphaseModel;
import eu.engys.core.project.state.SolutionState;
import eu.engys.gui.casesetup.solution.panels.AbstractThermalPanel;

public class StandardThermalPanel extends AbstractThermalPanel {

    public StandardThermalPanel(Set<ApplicationModule> modules) {
        super(modules);
    }

    @Override
    public void fixEnergy(SolutionState ss, MultiphaseModel mm) {
        if (ss.areSolverTypeAndTimeAndFlowAndTurbulenceChoosen()) {
            energy.setEnabled(true);
            boolean isMultiphaseOn = mm.isOn();
            boolean isMultiphaseOff = mm.isOff();
            if (ss.isCompressible()) {
                if ((isMultiphaseOn && energy.isSelected()) || (isMultiphaseOff && !energy.isSelected())) {
                    energy.doClick();
                }
                energy.setEnabled(false);
            } else if (ss.isIncompressible()) {
                if (energy.isSelected() && isMultiphaseOn) {
                    energy.doClick();
                }
                energy.setEnabled(isMultiphaseOff);
            }
        } else {
            energy.setEnabled(false);
        }

        if (ss.areSolverTypeAndTimeAndFlowAndTurbulenceChoosen()) {
            if (ss.isTransient() && ss.isLES()) {
                if (energy.isSelected()) {
                    energy.doClick();
                }
                energy.setEnabled(false);
            }
        }
    }

}
