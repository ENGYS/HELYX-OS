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
package eu.engys.core.project.materials.incompressible;

import eu.engys.core.project.materials.Material;

public class IncompressibleMaterial extends Material {

    public static final String PR_KEY = "pr";
    public static final String RHO_KEY = "rho";
    public static final String NU_KEY = "nu";
    public static final String MU_KEY = "mu";
    public static final String CP_KEY = "cp";
    public static final String PRT_KEY = "prt";
    public static final String LAMBDA_KEY = "lambda";
    public static final String T_REF_KEY = "tRef";
    public static final String P_REF_KEY = "pRef";
    public static final String BETA_KEY = "beta";
    public static final String TRANSPORT_MODEL_KEY = "transportModel";
    
    private double rho = 0;
    private double mu = 0;
    private double nu = 0;
    private double cp = 0;
    private double prt = 0;
    private double lambda = 0;
    private double tRef = 0;
    private double pRef = 0;
    private double beta = 0;
    private double pr = 0;
    
    private TransportModel transportModel = new NewtonianTransportModel();
    
    public IncompressibleMaterial() {
        super();
    }
    
    public IncompressibleMaterial(String name) {
        super(name);
    }
    
    public IncompressibleMaterial(IncompressibleMaterial m) {
        this(m.getName(), m);
    }

    public IncompressibleMaterial(String name, IncompressibleMaterial m) {
        super(name);
        if (m != null) {
            this.rho    = m.rho;
            this.mu     = m.mu;
            this.nu     = m.nu;
            this.cp     = m.cp;
            this.prt    = m.prt;
            this.lambda = m.lambda;
            this.tRef   = m.tRef;
            this.pRef   = m.pRef;
            this.beta   = m.beta;
            this.pr     = m.pr;
            this.transportModel = m.transportModel;
        }
    }

    public double getRho() {
        return rho;
    }
    public void setRho(double rho) {
        firePropertyChange(RHO_KEY, this.rho, this.rho = rho);
    }

    public double getMu() {
        return mu;
    }
    public void setMu(double mu) {
        firePropertyChange(MU_KEY, this.mu, this.mu = mu);
    }

    public double getNu() {
        return nu;
    }
    public void setNu(double nu) {
        firePropertyChange(NU_KEY, this.nu, this.nu = nu);
    }

    public double getCp() {
        return cp;
    }

    public void setCp(double cp) {
        firePropertyChange(CP_KEY, this.cp, this.cp = cp);
    }

    public double getPrt() {
        return prt;
    }

    public void setPrt(double prt) {
        firePropertyChange(PRT_KEY, this.prt, this.prt = prt);
    }

    public double getLambda() {
        return lambda;
    }
    public void setLambda(double lambda) {
        firePropertyChange(LAMBDA_KEY, this.lambda, this.lambda = lambda);
    }

    public double gettRef() {
        return tRef;
    }
    public void settRef(double tRef) {
        firePropertyChange(T_REF_KEY, this.tRef, this.tRef = tRef);
    }

    public double getpRef() {
        return pRef;
    }
    public void setpRef(double pRef) {
        firePropertyChange(P_REF_KEY, this.pRef, this.pRef = pRef);
    }

    public double getBeta() {
        return beta;
    }
    public void setBeta(double beta) {
        firePropertyChange(BETA_KEY, this.beta, this.beta = beta);
    }

    public double getPr() {
        return pr;
    }
    public void setPr(double pr) {
        firePropertyChange(PR_KEY, this.pr, this.pr = pr);
    }

    public TransportModel getTransportModel() {
        return transportModel;
    }
    public void setTransportModel(TransportModel transportModel) {
        firePropertyChange(TRANSPORT_MODEL_KEY, this.transportModel, this.transportModel = transportModel);
    }

    
}
