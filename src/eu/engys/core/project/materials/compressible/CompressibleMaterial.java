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
package eu.engys.core.project.materials.compressible;

import eu.engys.core.project.materials.Material;

public class CompressibleMaterial extends Material {

    public static final String N_MOLES = "nMoles";
    public static final String MOL_WEIGHT = "molWeight";
    public static final String EQ_OF_STATE = "eqOfState";
    public static final String TRANSPORT = "transport";
    public static final String THERMODYNAMIC_MODEL = "thermodynamicModel";
    
    private int nMoles = 0;
    private double molWeight = 0;
    
    private EquationOfState eqOfState = new PerfectGas();
    private Transport transport = new ConstantTransport();
    private ThermodynamicModel thermodynamicModel = new ConstantThermodynamicModel(); 

    public CompressibleMaterial() {
        super();
    }
    
    public CompressibleMaterial(String name) {
        super(name);
    }
    
    public CompressibleMaterial(CompressibleMaterial m) {
        this(m.getName(), m);
    }
    
    public CompressibleMaterial(String name, CompressibleMaterial m) {
        super(name);
        if (m != null) {
            this.nMoles = m.nMoles;
            this.molWeight = m.molWeight;
            this.eqOfState = m.eqOfState;
            this.transport = m.transport;
            this.thermodynamicModel = m.thermodynamicModel;
        }
    }

    public int getnMoles() {
        return nMoles;
    }
    public void setnMoles(int nMoles) {
        firePropertyChange(N_MOLES, this.nMoles, this.nMoles = nMoles);
    }

    public double getMolWeight() {
        return molWeight;
    }
    public void setMolWeight(double molWeight) {
        firePropertyChange(MOL_WEIGHT, this.molWeight, this.molWeight = molWeight);
    }

    public EquationOfState getEqOfState() {
        return eqOfState;
    }
    public void setEqOfState(EquationOfState eqOfState) {
        firePropertyChange(EQ_OF_STATE, this.eqOfState, this.eqOfState = eqOfState);
    }
    
    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        firePropertyChange(TRANSPORT, this.transport, this.transport = transport);
    }

    public ThermodynamicModel getThermodynamicModel() {
        return thermodynamicModel;
    }

    public void setThermodynamicModel(ThermodynamicModel thermodynamicModel) {
        firePropertyChange(THERMODYNAMIC_MODEL, this.thermodynamicModel, this.thermodynamicModel = thermodynamicModel);
    }
}
