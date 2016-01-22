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

package eu.engys.core.modules.solutionmodelling;

import eu.engys.core.project.state.MultiphaseModel;
import eu.engys.core.project.state.SolutionState;
import eu.engys.core.project.state.State;
import eu.engys.core.project.state.ThermalState;
import eu.engys.util.ui.ChooserPanel;
import eu.engys.util.ui.builder.PanelBuilder;

public interface SolutionView {

    void buildSolution(ChooserPanel solutionPanel);

    void buildThermal(PanelBuilder builder);

    void buildMultiphase(MultiphaseBuilder builder);

    void buildDynamic(PanelBuilder builder);

    void buildScalar(SolutionModellingPanel solutionPanel);

    void updateGUIFromState(State state);

    void updateStateFromGUI();

    boolean hasChanged();

    void fixSolutionState(SolutionState ss);

    void fixMultiphase(MultiphaseModel mm);

    void fixThermal(SolutionState ss, ThermalState ts);

}
