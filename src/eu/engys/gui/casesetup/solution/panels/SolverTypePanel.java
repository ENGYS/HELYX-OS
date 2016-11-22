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

import static eu.engys.core.project.state.SolverType.COUPLED;
import static eu.engys.core.project.state.SolverType.SEGREGATED;

import eu.engys.core.project.state.SolverType;
import eu.engys.core.project.state.State;
import eu.engys.util.ui.ChooserPanel;

public class SolverTypePanel extends ChooserPanel {

    public static final String SOLVER_TYPE = "Solver Type";

    public SolverTypePanel() {
        super(SOLVER_TYPE, false);
        addChoice(SEGREGATED.label());
        addChoice(COUPLED.label());
        getButton(COUPLED.label()).setEnabled(false);
    }

    public void updateFromState(State state) {
        if (state.getSolverType().isSegregated()) {
            select(SEGREGATED.label());
        } else if (state.getSolverType().isCoupled()) {
            select(COUPLED.label());
        } else {
            selectNone();
        }
    }

    public SolverType getSolverType() {
        String selectedState = getSelectedState();
        if (selectedState.equals(SEGREGATED.label()))
            return SolverType.SEGREGATED;
        if (selectedState.equals(COUPLED.label()))
            return SolverType.COUPLED;
        return SolverType.NONE;
    }

    public boolean isCoupled() {
        return getSelectedState().equals(COUPLED.label());
    }

    public boolean isSegregated() {
        return getSelectedState().equals(SEGREGATED.label());
    }

}
