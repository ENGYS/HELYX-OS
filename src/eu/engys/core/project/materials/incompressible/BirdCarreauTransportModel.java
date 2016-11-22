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

import eu.engys.util.bean.AbstractBean;

public class BirdCarreauTransportModel extends AbstractBean implements TransportModel {

    public static final String NU0_KEY = "nu0";
    public static final String NU_INF_KEY = "nuInf";
    public static final String K_KEY = "k";
    public static final String N_KEY = "n";
    
    private double nu0 = 0;
    private double nuInf = 0;
    private double k = 0;
    private double n = 0;
    
    public double getNu0() {
        return nu0;
    }
    public void setNu0(double nu0) {
        firePropertyChange(NU0_KEY, this.nu0, this.nu0 = nu0);
        this.nu0 = nu0;
    }
    public double getNuInf() {
        return nuInf;
    }
    public void setNuInf(double nuInf) {
        firePropertyChange(NU_INF_KEY, this.nuInf, this.nuInf = nuInf);
    }
    public double getK() {
        return k;
    }
    public void setK(double k) {
        firePropertyChange(K_KEY, this.k, this.k = k);
    }
    public double getN() {
        return n;
    }
    public void setN(double n) {
        firePropertyChange(N_KEY, this.n, this.n = n);
    }
    
    
}
