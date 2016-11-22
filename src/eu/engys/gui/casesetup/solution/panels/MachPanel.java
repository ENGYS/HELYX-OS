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

import static eu.engys.core.project.state.Mach.HIGH;
import static eu.engys.core.project.state.Mach.LOW;

import eu.engys.core.project.state.Mach;
import eu.engys.core.project.state.State;
import eu.engys.util.ui.ChooserPanel;

public class MachPanel extends ChooserPanel {

    public static final String MACH = "Mach";

    public MachPanel() {
        super(MACH);
        addChoice(LOW.label());
        addChoice(HIGH.label());
    }

    public void updateFromState(State state) {
        if (state.isLowMach())
            select(LOW.label());
        else if (state.isHighMach())
            select(HIGH.label());
        else
            selectNone();

    }

    public Mach getMach() {
        String selectedState = getSelectedState();
        if (selectedState.equals(HIGH.label()))
            return Mach.HIGH;
        else if (selectedState.equals(LOW.label()))
            return Mach.LOW;
        return Mach.NONE;
    }

    public boolean isHighMach() {
        return getSelectedState().equals(HIGH.label());
    }

    public boolean isLowMach() {
        return getSelectedState().equals(LOW.label());
    }

}
