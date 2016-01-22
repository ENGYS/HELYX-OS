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

import eu.engys.core.project.TurbulenceModel;
import eu.engys.core.project.TurbulenceModelType;

public class StateComposer {

    private State state;

    public static StateComposer newState() {
        StateComposer composer = new StateComposer();
        composer.state = new State();
        composer.state.setSolverType(SolverType.SEGREGATED);
        return composer;
    }

    public StateComposer coupled() {
        state.setSolverType(SolverType.COUPLED);
        state.setSolverFamily(SolverFamily.COUPLED);
        state.setToLowMach();
        return this;
    }

    public StateComposer segregated() {
        state.setSolverType(SolverType.SEGREGATED);
        return this;
    }

    public StateComposer steady() {
        state.setTimeToSteady();
        return this;
    }

    public StateComposer trans() {
        state.setTimeToTransient();
        return this;
    }

    public StateComposer compressible() {
        state.setFlowToCompressible();
        return this;
    }

    public StateComposer incompressible() {
        state.setFlowToIncompressible();
        return this;
    }

    public StateComposer les() {
        state.setMethodToLES();
        state.setToLowMach();
        return this;
    }

    public StateComposer rans() {
        state.setMethodToRANS();
        state.setToLowMach();
        return this;
    }

    public StateComposer hiMach() {
        state.setToHighMach();
        return this;
    }

    public StateComposer buoyant() {
        state.setBuoyant(true);
        return this;
    }

    public StateComposer energy() {
        state.setEnergy(true);
        return this;
    }

    public StateComposer simple() {
        state.setSolverFamily(SolverFamily.SIMPLE);
        return this;
    }

    public StateComposer pimple() {
        state.setSolverFamily(SolverFamily.PIMPLE);
        return this;
    }

    public StateComposer central() {
        state.setSolverFamily(SolverFamily.CENTRAL);
        return this;
    }

    public StateComposer piso() {
        state.setSolverFamily(SolverFamily.PISO);
        return this;
    }

    public StateComposer multiphaseVOF() {
        state.setMultiphaseModel(new MultiphaseModel("VOF", "VOF", true, true));
        return this;
    }

    public StateComposer multiphaseEuler() {
        state.setMultiphaseModel(new MultiphaseModel("Euler-Euler", "MEF", true, false));
        return this;
    }

    public StateComposer multiphaseHydro() {
        state.setMultiphaseModel(new MultiphaseModel("Hydro", "HYDRO", true, false));
        return this;
    }

    public StateComposer multiphaseECOMARINE() {
        state.setMultiphaseModel(new MultiphaseModel("ECOMARINE", "ECOMARINE", false, false));
        return this;
    }

    public StateComposer laminar() {
        state.setTurbulenceModel(new TurbulenceModel("laminar", TurbulenceModelType.LAMINAR));
        return this;
    }

    public StateComposer kEquationEddy() {
        state.setTurbulenceModel(new TurbulenceModel("k-Equation Eddy", TurbulenceModelType.K_Equation_Eddy));
        return this;
    }

    public StateComposer kOmega() {
        state.setTurbulenceModel(new TurbulenceModel("kOmegaSST", TurbulenceModelType.K_Omega));
        return this;
    }

    public StateComposer kEpsilon() {
        state.setTurbulenceModel(new TurbulenceModel("kEpsilon", TurbulenceModelType.K_Epsilon));
        return this;
    }

    public StateComposer spalartAllmaras() {
        state.setTurbulenceModel(new TurbulenceModel("SpalartAllmaras", TurbulenceModelType.Spalart_Allmaras));
        return this;
    }

    public StateComposer phases(int i) {
        state.setPhases(i);
        return this;
    }

    public State getState() {
        return state;
    }

}
