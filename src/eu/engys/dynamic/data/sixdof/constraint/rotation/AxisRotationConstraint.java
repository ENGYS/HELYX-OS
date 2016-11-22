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
package eu.engys.dynamic.data.sixdof.constraint.rotation;

public class AxisRotationConstraint extends RotationConstraint {

    public static final String AXIS = "axis";
    private double[] axis = new double[] { 0, 0, 1 };

    public AxisRotationConstraint() {
    }

    public AxisRotationConstraint(double[] axis) {
        this.axis = axis;
    }
    
    @Override
    public RotationConstraint copy() {
        return new AxisRotationConstraint(new double[]{axis[0], axis[1], axis[2]});
    }

    @Override
    public RotationConstraintType getType() {
        return RotationConstraintType.AXIS;
    }
    
    public double[] getAxis() {
        return axis;
    }
    
    public void setAxis(double[] axis) {
        firePropertyChange(AXIS, this.axis, this.axis = axis);
    }

}
