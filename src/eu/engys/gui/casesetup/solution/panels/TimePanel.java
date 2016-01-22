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

import static eu.engys.core.project.state.SolutionState.STEADY;
import static eu.engys.core.project.state.SolutionState.TRANSIENT;
import eu.engys.core.project.state.State;
import eu.engys.core.project.state.Time;
import eu.engys.util.ui.ChooserPanel;

public class TimePanel extends ChooserPanel {

    public static final String TIME = "Time";

    public TimePanel() {
        super(TimePanel.TIME);
        addChoice(STEADY);
        addChoice(TRANSIENT);
    }

    public void updateFromState(State state) {
        if (state.isSteady())
            select(STEADY);
        else if (state.isTransient())
            select(TRANSIENT);
        else
            selectNone();
    }

    public Time getTime() {
        String selectedState = getSelectedState();
        if (selectedState.equals(STEADY))
            return Time.STEADY;
        else if (selectedState.equals(TRANSIENT))
            return Time.TRANSIENT;
        return Time.NONE;
    }

    public boolean isSteady() {
        return getSelectedState().equals(STEADY);
    }

    public boolean isTransient() {
        return getSelectedState().equals(TRANSIENT);
    }

}
