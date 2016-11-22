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
package eu.engys.core.project.state;

public class ThermalState {

    public static final String THERMAL = "Thermal";
    public static final String ENERGY = "Energy";
    public static final String BUOYANCY = "Buoyancy";
    
    private boolean energy;
    private boolean buoyancy;
    private boolean radiation;
    private boolean solar;

    public ThermalState() {
        this.setEnergy(false);
        this.setBuoyancy(false);
        this.setBuoyancy(false);
        this.setSolar(false);
    }

    public ThermalState(State state) {
        this.setEnergy(state.isEnergy());
        this.setBuoyancy(state.isBuoyant());
        this.setRadiation(state.isRadiation());
        this.setSolar(state.isSolar());
    }

    public boolean isEnergy() {
        return energy;
    }

    public void setEnergy(boolean energy) {
        this.energy = energy;
    }

    public boolean isBuoyancy() {
        return buoyancy;
    }

    public void setBuoyancy(boolean buoyancy) {
        this.buoyancy = buoyancy;
    }

    public boolean isRadiation() {
        return radiation;
    }

    public void setRadiation(boolean radiation) {
        this.radiation = radiation;
    }

    public boolean isSolar() {
        return solar;
    }

    public void setSolar(boolean solar) {
        this.solar = solar;
    }

    @Override
    public String toString() {
        return "Energy: " + (isEnergy() ? "ON" : "OFF") + ", Buoyancy: " + (isBuoyancy() ? "ON" : "OFF") + ", Radiation: " + (isRadiation() ? "ON" : "OFF") + ", Solar: " + (isSolar() ? "ON" : "OFF");
    }
}