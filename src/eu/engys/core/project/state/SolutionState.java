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
package eu.engys.core.project.state;

import static eu.engys.util.ui.UiUtil.NONE_LABEL;

public class SolutionState {

    public String solverType;
    public String time;
    public String flow;
    public String method;
    public String mach;

    public SolutionState() {
        this.solverType = SolverType.NONE.label();
        this.time = Time.NONE.label();
        this.flow = Flow.NONE.label();
        this.method = Method.NONE.label();
        this.mach = Mach.NONE.label();
    }

    public SolutionState(State state) {
        this.solverType = state.isCoupled() ? SolverType.COUPLED.label() : state.isSegregated() ? SolverType.SEGREGATED.label() : SolverType.NONE.label();
        this.time = state.isSteady() ? Time.STEADY.label() : state.isTransient() ? Time.TRANSIENT.label() : Time.NONE.label();
        this.flow = state.isCompressible() ? Flow.COMPRESSIBLE.label() : state.isIncompressible() ? Flow.INCOMPRESSIBLE.label() : Flow.NONE.label();
        this.method = state.isRANS() ? Method.RANS.label() : state.isLES() ? Method.LES.label() : Method.NONE.label();
        this.mach = state.isHighMach() ? Mach.HIGH.label() : state.isLowMach() ? Mach.LOW.label() : Mach.NONE.label();
    }

    public boolean areSolverTypeAndTimeAndFlowAndTurbulenceChoosen() {
        boolean solverTypeChoosen = solverType != NONE_LABEL;
        boolean timeChoosen = time != NONE_LABEL;
        boolean flowChoosen = flow != NONE_LABEL;
        boolean turbulenceChoosen = method != NONE_LABEL;
        return solverTypeChoosen && timeChoosen && flowChoosen && turbulenceChoosen;
    }

    public boolean isLowMach() {
        return mach.equals(Mach.LOW.label());
    }

    public boolean isHighMach() {
        return mach.equals(Mach.HIGH.label());
    }

    public boolean isMachNone() {
        return mach.equals(Mach.NONE.label());
    }

    public boolean isLES() {
        return method.equals(Method.LES.label());
    }

    public boolean isRANS() {
        return method.equals(Method.RANS.label());
    }

    public boolean isTransient() {
        return time.equals(Time.TRANSIENT.label());
    }

    public boolean isSteady() {
        return time.equals(Time.STEADY.label());
    }

    public boolean isTimeNone() {
        return time.equals(Time.NONE.label());
    }

    public boolean isSegregated() {
        return solverType.equals(SolverType.SEGREGATED.label());
    }

    public boolean isCoupled() {
        return solverType.equals(SolverType.COUPLED.label());
    }

    public boolean isSolverNone() {
        return solverType.equals(SolverType.NONE.label());
    }

    public boolean isIncompressible() {
        return flow.equals(Flow.INCOMPRESSIBLE.label());
    }

    public boolean isCompressible() {
        return flow.equals(Flow.COMPRESSIBLE.label());
    }

    public boolean isFlowNone() {
        return flow.equals(Flow.NONE.label());
    }

    @Override
    public String toString() {
        return "SolverType: " + solverType + ", Time: " + time + ", Flow: " + flow + " Method: " + method + ", Mach: " + mach;
    }

}