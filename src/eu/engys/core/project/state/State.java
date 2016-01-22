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

public class State {

    public State() {

    }

    private Time time = Time.NONE;
    private Flow flow = Flow.NONE;
    private Method method = Method.NONE;
    private Mach mach = Mach.NONE;
    private Solver solver = Solver.NONE;
    private SolverType solverType = SolverType.NONE;
    private SolverFamily solverFamily = SolverFamily.NONE;

    private boolean energy;
    private boolean buoyant;
    private MultiphaseModel multiphaseModel = MultiphaseModel.OFF;

    private int phases = 1;

    private TurbulenceModel turbulenceModel;

    @Override
    public String toString() {
        return solver + " - " + solverFamily + " - " + time + " - " + flow + " - " + method + (energy ? " - energy" : "") + (buoyant ? " - buoyant" : "") + " - multiphase: "+ multiphaseModel.getLabel() + " with " + phases + " phases" + " - " + mach + "_MACH";
    }

    public Mach getMach() {
        return mach;
    }

    public void setMach(Mach mach) {
        this.mach = mach;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    public void setTimeToSteady() {
        this.time = Time.STEADY;
    }

    public void setTimeToTransient() {
        this.time = Time.TRANSIENT;
    }

    public void setTimeToNone() {
        this.time = Time.NONE;
    }

    public boolean isSteady() {
        return time.isSteady();
    }

    public boolean isTransient() {
        return time.isTransient();
    }

    public void setFlowToCompressible() {
        this.flow = Flow.COMPRESSIBLE;
    }

    public void setFlowToIncompressible() {
        this.flow = Flow.INCOMPRESSIBLE;
    }

    public void setFlowToNONE() {
        this.flow = Flow.NONE;
    }

    public boolean isCompressible() {
        return flow.isCompressible();
    }

    public boolean isIncompressible() {
        return flow.isIncompressible();
    }

    public void setMethodToLES() {
        this.method = Method.LES;
    }

    public void setMethodToRANS() {
        this.method = Method.RANS;
    }

    public void setMethodToNONE() {
        this.method = Method.NONE;
    }

    public boolean isLES() {
        return method.isLes();
    }

    public boolean isRANS() {
        return method.isRans();
    }

    public void setEnergy(boolean energy) {
        this.energy = energy;
    }

    public boolean isEnergy() {
        return energy;
    }

    public void setToHighMach() {
        this.mach = Mach.HIGH;
    }

    public void setToLowMach() {
        this.mach = Mach.LOW;
    }

    public boolean isLowMach() {
        return mach.isLow();
    }

    public boolean isHighMach() {
        return mach.isHigh();
    }

    public void setBuoyant(boolean buoyant) {
        this.buoyant = buoyant;
    }

    public boolean isBuoyant() {
        return buoyant;
    }

    public TurbulenceModel getTurbulenceModel() {
        return turbulenceModel;
    }

    public void setTurbulenceModel(TurbulenceModel turbulenceModel) {
        this.turbulenceModel = turbulenceModel;
    }

    public MultiphaseModel getMultiphaseModel() {
        return multiphaseModel;
    }

    public void setMultiphaseModel(MultiphaseModel multiphase) {
        this.multiphaseModel = multiphase;
    }

    public int getPhases() {
        return phases;
    }

    public void setPhases(int phases) {
        this.phases = phases;
    }

    public boolean areTimeAndFlowAndTurbulenceChoosen() {
        boolean timeChoosen = !time.isNone();
        boolean flowChoosen = !flow.isNone();
        boolean turbulenceChoosen = !method.isNone();
        return timeChoosen && flowChoosen && turbulenceChoosen;
    }

    public Solver getSolver() {
        return solver;
    }

    public void setSolver(Solver solver) {
        this.solver = solver;
    }

    public SolverType getSolverType() {
        return solverType;
    }

    public void setSolverType(SolverType solverType) {
        this.solverType = solverType;
    }
    
    public boolean isCoupled() {
        return solverType.isCoupled();
    }

    public boolean isSegregated() {
        return solverType.isSegregated();
    }

    public SolverFamily getSolverFamily() {
        return solverFamily;
    }

    public void setSolverFamily(SolverFamily solverFamily) {
        this.solverFamily = solverFamily;
    }

    public void stringToState(String string) {
        String[] tokens = string.replace("(", "").replace(")", "").split("\\s+");

        for (String token : tokens) {
            switch (token.trim()) {
            case "COUPLED":
                setSolverType(SolverType.COUPLED);
                setSolverFamily(SolverFamily.COUPLED);
                break;
            case "SIMPLE":
                setTimeToSteady();
                setSolverType(SolverType.SEGREGATED);
                setSolverFamily(SolverFamily.SIMPLE);
                break;
            case "PISO":
                setTimeToTransient();
                setSolverType(SolverType.SEGREGATED);
                setSolverFamily(SolverFamily.PISO);
                break;
            case "PIMPLE":
                setTimeToTransient();
                setSolverType(SolverType.SEGREGATED);
                setSolverFamily(SolverFamily.PIMPLE);
                break;
            case "CENTRAL":
                setTimeToTransient();
                setSolverType(SolverType.SEGREGATED);
                setSolverFamily(SolverFamily.CENTRAL);
                break;

            case "steady":
                setTimeToSteady();
                setSolverType(SolverType.SEGREGATED);
                setSolverFamily(SolverFamily.SIMPLE);
                break;
            case "transient":
                setTimeToTransient();
                setSolverType(SolverType.SEGREGATED);
                setSolverFamily(SolverFamily.PIMPLE);
                break;

            case "compressible":
                setEnergy(true);
                setFlowToCompressible();
                break;
            case "incompressible":
                setFlowToIncompressible();
                break;

            case "hiMach":
                setToHighMach();
                break;

            case "buoyant":
                setBuoyant(true);
                setEnergy(true);
                break;

//            case "multiphase":
//                setMultiphase(true);
//                setBuoyant(true);
//                break;

            case "ras":
                setMethodToRANS();
                setToLowMach();
                break;
            case "les":
                setMethodToLES();
                setToLowMach();
                break;

            default:
                break;
            }
        }
        Solver solver = new Solver("");
        setSolver(solver);
    }

    private static final String SPACE = " ";

    /**
     * 
     * @param state
     * 
     * @return (steady incompressible ras)
     */
    public String state2String() {
        StringBuffer sb = new StringBuffer();

        sb.append("( ");

        appendState(sb);

        sb.append(")");

        return sb.toString();
    }

    public void appendState(StringBuffer sb) {
        if (isSteady()) {
            if (solverType.isCoupled()) {
                sb.append("steady");
                sb.append(SPACE);
                sb.append("COUPLED");
            } else {
                sb.append("SIMPLE");
            }
        } else if (isTransient()) {
            if (solverType.isCoupled()) {
                sb.append("transient");
                sb.append(SPACE);
                sb.append("COUPLED");
            } else {
                if (solverFamily.isPimple()) {
                    sb.append("PIMPLE");
                } else if (solverFamily.isCentral()) {
                    sb.append("CENTRAL");
                } else if (solverFamily.isPiso()) {
                    sb.append("PISO");
                }
            }
        }

        sb.append(SPACE);

        if (isCompressible())
            sb.append("compressible");
        else if (isIncompressible())
            sb.append("incompressible");

        sb.append(SPACE);

        if (isLES())
            sb.append("les");
        else if (isRANS())
            sb.append("ras");

        if (isHighMach()) {
            sb.append(SPACE);
            sb.append("hiMach");
        }

        if (getMultiphaseModel().isOn()) {
            sb.append(SPACE);
            sb.append(getMultiphaseModel().getKey());
        } else {
            if (isCompressible()) {
                if (isBuoyant()) {
                    sb.append(SPACE);
                    sb.append("buoyant");
                }
            } else {
                if (isEnergy()) {
                    sb.append(SPACE);
                    sb.append("buoyant");
                }
            }
        }
    }
}
