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
package eu.engys.dynamic.data.singlebody.functions;

import eu.engys.dynamic.data.singlebody.SolidBodyMotionFunction;
import eu.engys.dynamic.data.singlebody.SolidBodyMotionFunctionType;

public class SDAMotionFunction extends SolidBodyMotionFunction {

    public static final String TPN = "tpn";
    public static final String TP = "tp";
    public static final String SWAY_A = "swayA";
    public static final String ROLL_AMIN = "rollAmin";
    public static final String Q = "q";
    public static final String LAMBDA = "lambda";
    public static final String HEAVE_A = "heaveA";
    public static final String DTI = "dti";
    public static final String ROLL_AMAX = "rollAmax";
    public static final String DTP = "dtp";
    public static final String COFG = "cofg";

    private double tpn = 1;
    private double tp = 1;
    private double swayA = 1;
    private double rollAmin = 1;
    private double q = 0;
    private double lambda = 1;
    private double heaveA = 1;
    private double dti = 1;
    private double rollAmax = 1;
    private double dtp = 1;
    private double[] cofg = new double[] { 0, 0, 0 };

    public SDAMotionFunction() {
    }
    
    @Override
    public SolidBodyMotionFunction copy() {
        SDAMotionFunction function = new SDAMotionFunction();
        function.tpn = this.tpn;
        function.tp = this.tp;
        function.swayA = this.swayA;
        function.rollAmin = this.rollAmin;
        function.q = this.q;
        function.lambda = this.lambda;
        function.heaveA = this.heaveA;
        function.dti = this.dti;
        function.rollAmax = this.rollAmax;
        function.dtp = this.dtp;
        function.cofg = new double[] { cofg[0], cofg[1], cofg[2] };
        return function;
    
    }

    @Override
    public SolidBodyMotionFunctionType getFunctionType() {
        return SolidBodyMotionFunctionType.SDA_MOTION;
    }

    public double getTpn() {
        return tpn;
    }

    public void setTpn(double tpn) {
        firePropertyChange(TPN, this.tpn, this.tpn = tpn);
    }

    public double getTp() {
        return tp;
    }

    public void setTp(double tp) {
        firePropertyChange(TP, this.tp, this.tp = tp);
    }

    public double getSwayA() {
        return swayA;
    }

    public void setSwayA(double swayA) {
        firePropertyChange(SWAY_A, this.swayA, this.swayA = swayA);
    }

    public double getRollAmin() {
        return rollAmin;
    }

    public void setRollAmin(double rollAmin) {
        firePropertyChange(ROLL_AMIN, this.rollAmin, this.rollAmin = rollAmin);
    }

    public double getQ() {
        return q;
    }

    public void setQ(double q) {
        firePropertyChange(Q, this.q, this.q = q);
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        firePropertyChange(LAMBDA, this.lambda, this.lambda = lambda);
    }

    public double getHeaveA() {
        return heaveA;
    }

    public void setHeaveA(double heaveA) {
        firePropertyChange(HEAVE_A, this.heaveA, this.heaveA = heaveA);
    }

    public double getDti() {
        return dti;
    }

    public void setDti(double dti) {
        firePropertyChange(DTI, this.dti, this.dti = dti);
    }

    public double getRollAmax() {
        return rollAmax;
    }

    public void setRollAmax(double rollAmax) {
        firePropertyChange(ROLL_AMAX, this.rollAmax, this.rollAmax = rollAmax);
    }

    public double getDtp() {
        return dtp;
    }

    public void setDtp(double dtp) {
        firePropertyChange(DTP, this.dtp, this.dtp = dtp);
    }

    public double[] getCofg() {
        return cofg;
    }

    public void setCofg(double[] cofg) {
        firePropertyChange(COFG, this.cofg, this.cofg = cofg);
    }

}
