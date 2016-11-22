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

package eu.engys.core.modules.cellzones;

import static eu.engys.util.Symbols.KELVIN;
import static eu.engys.util.Symbols.SQUARE;

import javax.swing.JComponent;

import eu.engys.core.dictionary.Dictionary;

public interface CellZonePanel {
    
    public static final String MODEL_LABEL = "Model";
    
    /*
     * MRF
     */
    public static final String ORIGIN_LABEL = "Origin";
    public static final String AXIS_LABEL = "Axis";
    public static final String OMEGA_RAD_S_LABEL = "Omega [rad/s]";
    public static final String NON_ROTATING_PATCHES_INSIDE_MRF_ZONE_LABEL = "Non-rotating patches inside MRF zone";
    
    /*
     * Porous
     */
    public static final String C1_LABEL = "C1";
    public static final String C0_LABEL = "C0";
    public static final String POWER_LAW = "Power-law";
    public static final String DARCY_FORCHHEIMER = "Darcy-Forchheimer";
    public static final String INERTIAL_LOSS_LABEL = "Inertial Loss Coefficient [1/m]";
    public static final String VISCOUS_LOSS_LABEL = "Viscous Loss Coefficient [1/m" + SQUARE + "]";
    public static final String E2_LABEL = "e2 [m]";
    public static final String E1_LABEL = "e1 [m]";
    
    /*
     * Thermal
     */
    public static final String FIXED_TEMPERATURE_LABEL = "Fixed Temperature";
    public static final String FIXED_TEMPERATURE_K_LABEL = "Fixed Temperature " + KELVIN;

    // CellZoneType getType();

    JComponent getPanel();

    void stateChanged();

    void loadFromDictionary(String cellZoneName, Dictionary cellZoneDictionary);

    Dictionary saveToDictionary(String czName);

    // Dictionary getDefault();

    void layoutPanel();

    // boolean isEnabled();

}
