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

public class OscillatingRotatingMotionFunction extends SolidBodyMotionFunction {

    public static final String ORIGIN = "origin";
    public static final String AMPLITUDE = "amplitude";
    public static final String OMEGA = "omega";

    private double[] origin = new double[] { 0, 0, 0 };
    private double[] amplitude = new double[] { 0, 0, 30 };
    private double omega = 100;

    public OscillatingRotatingMotionFunction() {
    }

    public OscillatingRotatingMotionFunction(double[] origin, double[]amplitude, double omega) {
        this.origin = origin;
        this.amplitude = amplitude;
        this.omega = omega;
    }

    @Override
    public SolidBodyMotionFunction copy() {
        OscillatingRotatingMotionFunction function = new OscillatingRotatingMotionFunction();
        function.origin = new double[] { origin[0], origin[1], origin[2] };
        function.amplitude = new double[] { amplitude[0], amplitude[1], amplitude[2] };
        function.omega = this.omega;
        return function;

    }

    @Override
    public SolidBodyMotionFunctionType getFunctionType() {
        return SolidBodyMotionFunctionType.OSCILLATING_ROTATING_MOTION;
    }

    public double[] getOrigin() {
        return origin;
    }

    public void setOrigin(double[] origin) {
        firePropertyChange(ORIGIN, this.origin, this.origin = origin);
    }

    public double[] getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double[] amplitude) {
        firePropertyChange(AMPLITUDE, this.amplitude, this.amplitude = amplitude);
    }

    public double getOmega() {
        return omega;
    }

    public void setOmega(double omega) {
        firePropertyChange(OMEGA, this.omega, this.omega = omega);
    }

}
