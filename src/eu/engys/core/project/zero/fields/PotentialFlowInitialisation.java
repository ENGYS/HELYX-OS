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
package eu.engys.core.project.zero.fields;

import eu.engys.util.bean.AbstractBean;

public class PotentialFlowInitialisation extends AbstractBean implements Initialisation {

    public static final String INIT_UBCS = "initUBCS";
    public static final String RHO_REF = "rhoRef";
    public static final String N_NON_ORTHOGONAL_CORRECTORS = "nNonOrthogonalCorrectors";

    private boolean initUBCS = true;
    // EE
    private double rhoRef = 1;
    // OS
    private int nNonOrthogonalCorrectors = 10;

    public PotentialFlowInitialisation() {
    }

    public PotentialFlowInitialisation(boolean initUBCS) {
        this.initUBCS = initUBCS;
    }

    public PotentialFlowInitialisation(boolean initUBCS, double rhoRef) {
        this.initUBCS = initUBCS;
        this.rhoRef = rhoRef;
    }

    public PotentialFlowInitialisation(boolean initUBCS, int nNonOrthogonalCorrectors) {
        this.initUBCS = initUBCS;
        this.nNonOrthogonalCorrectors = nNonOrthogonalCorrectors;
    }

    public boolean isInitUBCS() {
        return initUBCS;
    }

    public void setInitUBCS(boolean initUBCS) {
        firePropertyChange(INIT_UBCS, this.initUBCS, this.initUBCS = initUBCS);
    }

    public double getRhoRef() {
        return rhoRef;
    }

    public void setRhoRef(double rhoRef) {
        firePropertyChange(RHO_REF, this.rhoRef, this.rhoRef = rhoRef);
    }
    
    public int getnNonOrthogonalCorrectors() {
        return nNonOrthogonalCorrectors;
    }
    
    public void setnNonOrthogonalCorrectors(int nNonOrthogonalCorrectors) {
        firePropertyChange(N_NON_ORTHOGONAL_CORRECTORS, this.nNonOrthogonalCorrectors, this.nNonOrthogonalCorrectors = nNonOrthogonalCorrectors);
    }

    public String toString() {
        return "Potential Flow";
    }

}
