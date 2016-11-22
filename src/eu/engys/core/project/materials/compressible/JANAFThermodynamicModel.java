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

public class JANAFThermodynamicModel extends AbstractBean implements ThermodynamicModel {

    public static final String TLOW_KEY = "tLow";
    public static final String THIGH_KEY = "tHigh";
    public static final String TCOMMON_KEY = "tCommon";

    public static final String LOW_CP_COEFFS_KEY = "lowCpCoefficients";
    public static final String HIGH_CP_COEFFS_KEY = "highCpCoefficients";

    private double tLow = 0;
    private double tHigh = 0;
    private double tCommon = 0;

    private double[] lowCpCoefficients = new double[7];
    private double[] highCpCoefficients = new double[7];

    public double gettLow() {
        return tLow;
    }

    public void settLow(double tLow) {
        firePropertyChange(TLOW_KEY, this.tLow, this.tLow = tLow);
    }

    public double gettHigh() {
        return tHigh;
    }

    public void settHigh(double tHigh) {
        firePropertyChange(THIGH_KEY, this.tHigh, this.tHigh = tHigh);
    }

    public double gettCommon() {
        return tCommon;
    }

    public void settCommon(double tCommon) {
        firePropertyChange(TCOMMON_KEY, this.tCommon, this.tCommon = tCommon);
    }

    public double[] getLowCpCoefficients() {
        return lowCpCoefficients;
    }

    public void setLowCpCoefficients(double[] lowCpCoefficients) {
        firePropertyChange(LOW_CP_COEFFS_KEY, this.lowCpCoefficients, this.lowCpCoefficients = lowCpCoefficients);
    }

    public double[] getHighCpCoefficients() {
        return highCpCoefficients;
    }

    public void setHighCpCoefficients(double[] highCpCoefficients) {
        firePropertyChange(HIGH_CP_COEFFS_KEY, this.highCpCoefficients, this.highCpCoefficients = highCpCoefficients);
    }

}
