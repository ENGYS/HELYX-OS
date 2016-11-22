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

import eu.engys.util.bean.AbstractBean;

public class PolynomialThermodynamicModel extends AbstractBean implements ThermodynamicModel {
    
    public static final String SF_KEY = "sf";
    public static final String HF_KEY = "hf";
    public static final String CP_COEFFS_KEY = "cpCoefficients";
    
    private double hf = 0;
    private double sf = 0;
    private double[] cpCoefficients = new double[8];
    
    public double[] getCpCoefficients() {
        return cpCoefficients;
    }
    public void setCpCoefficients(double[] cpCoefficients) {
        firePropertyChange(CP_COEFFS_KEY, this.cpCoefficients, this.cpCoefficients = cpCoefficients);
    }
    public double getHf() {
        return hf;
    }
    public void setHf(double hf) {
        firePropertyChange(HF_KEY, this.hf, this.hf = hf);
    }
    public double getSf() {
        return sf;
    }
    public void setSf(double sf) {
        firePropertyChange(SF_KEY, this.sf, this.sf = sf);
    }
    
    
}
