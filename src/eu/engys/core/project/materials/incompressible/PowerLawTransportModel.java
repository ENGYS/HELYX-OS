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

public class PowerLawTransportModel extends AbstractBean implements TransportModel {

    public static final String NU_MIN_KEY = "nuMin";
    public static final String NU_MAX_KEY = "nuMax";
    public static final String K_KEY = "k";
    public static final String N_KEY = "n";
    
    private double nuMin = 0;
    private double nuMax = 0;
    private double k = 0;
    private double n = 0;
    
    public double getNuMin() {
        return nuMin;
    }
    public void setNuMin(double nuMin) {
        firePropertyChange("nuMin", this.nuMin, this.nuMin = nuMin);
    }
    public double getNuMax() {
        return nuMax;
    }
    public void setNuMax(double nuMax) {
        firePropertyChange("nuMax", this.nuMax, this.nuMax = nuMax);
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
