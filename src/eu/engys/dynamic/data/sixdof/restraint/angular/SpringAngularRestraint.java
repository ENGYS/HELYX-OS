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
package eu.engys.dynamic.data.sixdof.restraint.angular;

public class SpringAngularRestraint extends AngularRestraint {

    public static final String STIFFNESS = "stiffness";
    public static final String DAMPING = "damping";

    private double stiffness = 0.0;
    private double damping = 0.0;

    public SpringAngularRestraint() {
    }

    public SpringAngularRestraint(double stiffness, double damping) {
        this.stiffness = stiffness;
        this.damping = damping;
    }
    
    @Override
    public AngularRestraint copy() {
        return new SpringAngularRestraint(stiffness, damping);
    }

    @Override
    public AngularRestraintType getType() {
        return AngularRestraintType.SPRING;
    }

    public double getStiffness() {
        return stiffness;
    }

    public void setStiffness(double stiffness) {
        firePropertyChange(STIFFNESS, this.stiffness, this.stiffness = stiffness);
    }

    public double getDamping() {
        return damping;
    }

    public void setDamping(double damping) {
        firePropertyChange(DAMPING, this.damping, this.damping = damping);
    }

}
