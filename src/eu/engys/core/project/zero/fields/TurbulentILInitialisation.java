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
package eu.engys.core.project.zero.fields;

import eu.engys.util.bean.AbstractBean;

public class TurbulentILInitialisation extends AbstractBean implements Initialisation {

    public static final String I_KEY = "i";
    public static final String UREF_KEY = "uref";
    public static final String L_KEY = "l";
    
    private double i = 0.0;
    private double l = 0.0;
    private double uref = 0.0;

    public TurbulentILInitialisation() {}

    public TurbulentILInitialisation(double I, double L, double Uref) {
        this.i = I;
        this.l = L;
        this.uref = Uref;
    }
    
    public double getI() {
        return i;
    }
    public void setI(double i) {
        firePropertyChange(I_KEY, this.i, this.i = i);
    }

    public double getL() {
        return l;
    }
    public void setL(double l) {
        firePropertyChange(L_KEY, this.l, this.l = l);
    }

    public double getUref() {
        return uref;
    }

    public void setUref(double uref) {
        firePropertyChange(UREF_KEY, this.uref, this.uref = uref);
    }
    
    public String toString() {
        return "Turbulent IL";
    }
}
