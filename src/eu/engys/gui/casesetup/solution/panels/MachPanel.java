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

package eu.engys.gui.casesetup.solution.panels;

import eu.engys.core.project.state.Mach;
import eu.engys.core.project.state.SolutionState;
import eu.engys.core.project.state.State;
import eu.engys.util.ui.ChooserPanel;

public class MachPanel extends ChooserPanel {

    public static final String MACH = "Mach";

    public MachPanel() {
        super(MACH);
        addChoice(SolutionState.LO_MACH);
        addChoice(SolutionState.HI_MACH);
    }

    public void updateFromState(State state) {
        if (state.isLowMach())
            select(SolutionState.LO_MACH);
        else if (state.isHighMach())
            select(SolutionState.HI_MACH);
        else
            selectNone();

    }

    public Mach getMach() {
        String selectedState = getSelectedState();
        if (selectedState.equals(SolutionState.HI_MACH))
            return Mach.HIGH;
        else if (selectedState.equals(SolutionState.LO_MACH))
            return Mach.LOW;
        return Mach.NONE;
    }

    public boolean isHighMach() {
        return getSelectedState().equals(SolutionState.HI_MACH);
    }

    public boolean isLowMach() {
        return getSelectedState().equals(SolutionState.LO_MACH);
    }

}
