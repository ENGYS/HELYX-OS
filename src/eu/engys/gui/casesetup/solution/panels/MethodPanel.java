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

import static eu.engys.core.project.state.Method.LES;
import static eu.engys.core.project.state.Method.RANS;

import eu.engys.core.project.state.Method;
import eu.engys.core.project.state.State;
import eu.engys.util.ui.ChooserPanel;

public class MethodPanel extends ChooserPanel {

    public static final String TURBULENCE = "Turbulence";

    public MethodPanel() {
        super(TURBULENCE);
        addChoice(RANS.label());
        addChoice(LES.label());
    }

    public void updateFromState(State state) {
        if (state.isLES())
            select(LES.label());
        else if (state.isRANS())
            select(RANS.label());
        else
            selectNone();

    }

    public Method getMethod() {
        String selectedState = getSelectedState();
        if (selectedState.equals(LES.label()))
            return Method.LES;
        else if (selectedState.equals(RANS.label()))
            return Method.RANS;
        return Method.NONE;
    }

    public boolean isLES() {
        return getSelectedState().equals(LES.label());
    }

    public boolean isRAS() {
        return getSelectedState().equals(RANS.label());
    }

}
