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

public class RotatingStepMotionFunction extends SolidBodyMotionFunction {

    public static final String ORIGIN = "origin";
    public static final String AXIS = "axis";
    public static final String THETA = "theta";
    public static final String PERIOD = "period";

    private double[] origin = new double[] { 0, 0, 0 };
    private double[] axis = new double[] { 0, 0, 1 };
    private double theta = -60;
    private int period = 150;

    public RotatingStepMotionFunction() {
    }
    
    @Override
    public SolidBodyMotionFunction copy() {
        RotatingStepMotionFunction function = new RotatingStepMotionFunction();
        function.origin = new double[] { origin[0], origin[1], origin[2] };
        function.axis = new double[] { axis[0], axis[1], axis[2] };
        function.theta = this.theta;
        function.period = this.period;
        return function;
    
    }

    @Override
    public SolidBodyMotionFunctionType getFunctionType() {
        return SolidBodyMotionFunctionType.ROTATING_STEP_MOTION;
    }

    public double[] getOrigin() {
        return origin;
    }

    public void setOrigin(double[] origin) {
        firePropertyChange(ORIGIN, this.origin, this.origin = origin);
    }

    public double[] getAxis() {
        return axis;
    }

    public void setAxis(double[] axis) {
        firePropertyChange(AXIS, this.axis, this.axis = axis);
    }

    public double getTheta() {
        return theta;
    }
    
    public void setTheta(double theta) {
        firePropertyChange(THETA, this.theta, this.theta = theta);
    }
    
    public int getPeriod() {
        return period;
    }
    
    public void setPeriod(int period) {
        firePropertyChange(PERIOD, this.period, this.period = period);
    }

}
