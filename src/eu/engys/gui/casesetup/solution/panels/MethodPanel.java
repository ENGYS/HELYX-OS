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

import eu.engys.core.project.state.Method;
import eu.engys.core.project.state.SolutionState;
import eu.engys.core.project.state.State;
import eu.engys.util.ui.ChooserPanel;

public class MethodPanel extends ChooserPanel {

    public static final String TURBULENCE = "Turbulence";

    public MethodPanel() {
        super(TURBULENCE);
        addChoice(SolutionState.RANS);
        addChoice(SolutionState.LES_DES);
    }

    public void updateFromState(State state) {
        if (state.isLES())
            select(SolutionState.LES_DES);
        else if (state.isRANS())
            select(SolutionState.RANS);
        else
            selectNone();

    }

    public Method getMethod() {
        String selectedState = getSelectedState();
        if (selectedState.equals(SolutionState.LES_DES))
            return Method.LES;
        else if (selectedState.equals(SolutionState.RANS))
            return Method.RANS;
        return Method.NONE;
    }

    public boolean isLES() {
        return getSelectedState().equals(SolutionState.LES_DES);
    }

    public boolean isRAS() {
        return getSelectedState().equals(SolutionState.RANS);
    }

}
