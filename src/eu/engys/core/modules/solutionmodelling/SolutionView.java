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
package eu.engys.core.modules.solutionmodelling;

import java.awt.event.ActionListener;

import eu.engys.core.project.state.AdjointState;
import eu.engys.core.project.state.MultiphaseModel;
import eu.engys.core.project.state.SolutionState;
import eu.engys.core.project.state.State;
import eu.engys.core.project.state.ThermalState;
import eu.engys.util.ui.ChooserPanel;
import eu.engys.util.ui.builder.PanelBuilder;

public interface SolutionView {
    
    void handleLicenseChanged(SolutionState ss);

    void updateGUIFromState(State state);

    void updateStateFromGUI(State state);

    boolean hasChanged(State state);

    
    /*
     * Solution
     */
    void buildSolution(ChooserPanel solutionPanel);

    void fixSolutionState(SolutionState ss);
    
    /*
     * Multiphase
     */
    void buildMultiphase(MultiphaseBuilder builder);
    
    void fixMultiphase(MultiphaseModel mm);
    
    /*
     * Thermal
     */
    void buildThermal(PanelBuilder builder);

    void fixThermal(SolutionState ss, ThermalState ts);

    void setThermalListener(ActionListener listener);

    void addThermalListener();

    void removeThermalListener();

    void updateThermalState(ThermalState ts);
    
    /*
     * Dynamic
     */
    void buildDynamic(PanelBuilder builder);
    
    /*
     * Adjoint
     */
    void buildAdjoint(PanelBuilder builder);
    
    void fixAdjoint(AdjointState as);

    void setAdjointListener(ActionListener listener);
    
    void addAdjointListener();
    
    void removeAdjointListener();

    void updateAdjointState(AdjointState as);
    
    /*
     * Scalar
     */
    void buildScalar(SolutionModellingPanel solutionPanel);


}
