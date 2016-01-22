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

package eu.engys.core.project.state;

import eu.engys.util.ui.ChooserPanel;

public class SolutionState {
    
    public static final String NONE = "NONE";
    public static final String TRANSIENT = "Transient";
    public static final String STEADY = "Steady";
    public static final String INCOMPRESSIBLE = "Incompressible";
    public static final String COMPRESSIBLE = "Compressible";
    public static final String SEGREGATED = "Segregated";
    public static final String COUPLED = "Coupled";
    public static final String LES_DES = "LES/DES";
    public static final String RANS = "RANS";
    public static final String HI_MACH = "High";
    public static final String LO_MACH = "Low";
    
    public String time;
    public String flow;
    public String turbulence;
    public String solver;
    public String mach;
    
    public SolutionState() {
        this.time = NONE;
        this.flow = NONE;
        this.turbulence = NONE;
        this.solver = NONE;
        this.mach = NONE;
    }
    
    public SolutionState(State state) {
        this.time = state.isSteady() ? STEADY : state.isTransient() ? TRANSIENT : NONE;
        this.flow = state.isCompressible() ? COMPRESSIBLE : state.isIncompressible() ? INCOMPRESSIBLE : NONE;
        this.turbulence = state.isRANS() ? RANS : state.isLES() ? LES_DES : NONE;
        this.solver = state.isCoupled() ? COUPLED : state.isSegregated() ? SEGREGATED : NONE;
        this.mach = state.isHighMach() ? SolutionState.HI_MACH : state.isLowMach() ? SolutionState.LO_MACH : NONE;
    }

    public boolean areSolverTypeAndTimeAndFlowAndTurbulenceChoosen() {
        boolean timeChoosen = time != ChooserPanel.NONE;
        boolean flowChoosen = flow != ChooserPanel.NONE;
        boolean turbulenceChoosen = turbulence != ChooserPanel.NONE;
        boolean solverTypeChoosen = solver != ChooserPanel.NONE;
        return solverTypeChoosen && timeChoosen && flowChoosen && turbulenceChoosen;
    }

    public boolean isLowMach() {
        return mach.equals(SolutionState.LO_MACH);
    }
    public boolean isHighMach() {
        return mach.equals(SolutionState.HI_MACH);
    }
    public boolean isMachNone() {
        return mach.equals(NONE);
    }

    public boolean isLES() {
        return turbulence.equals(SolutionState.LES_DES);
    }
    public boolean isRANS() {
        return turbulence.equals(SolutionState.RANS);
    }

    public boolean isTransient() {
        return time.equals(SolutionState.TRANSIENT);
    }
    public boolean isSteady() {
        return time.equals(SolutionState.STEADY);
    }
    public boolean isTimeNone() {
        return time.equals(NONE);
    }
    

    public boolean isSegregated() {
        return solver == SolutionState.SEGREGATED;
    }
    public boolean isCoupled() {
        return solver == SolutionState.COUPLED;
    }
    public boolean isSolverNone() {
        return solver == NONE;
    }
    
    public boolean isIncompressible() {
        return flow == SolutionState.INCOMPRESSIBLE;
    }
    public boolean isCompressible() {
        return flow == SolutionState.COMPRESSIBLE;
    }
    public boolean isFlowNone() {
        return flow == NONE;
    }

}
