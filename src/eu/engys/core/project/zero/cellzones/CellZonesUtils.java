/*--------------------------------*- Java -*---------------------------------*\
 |		 o                                                                   |                                                                                     
 |    o     o       | HelyxOS: The Open Source GUI for OpenFOAM              |
 |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 |    o     o       | http://www.engys.com                                   |
 |       o          |                                                        |
 |---------------------------------------------------------------------------|
 |	 License                                                                 |
 |   This file is part of HelyxOS.                                           |
 |                                                                           |
 |   HelyxOS is free software; you can redistribute it and/or modify it      |
 |   under the terms of the GNU General Public License as published by the   |
 |   Free Software Foundation; either version 2 of the License, or (at your  |
 |   option) any later version.                                              |
 |                                                                           |
 |   HelyxOS is distributed in the hope that it will be useful, but WITHOUT  |
 |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 |   for more details.                                                       |
 |                                                                           |
 |   You should have received a copy of the GNU General Public License       |
 |   along with HelyxOS; if not, write to the Free Software Foundation,      |
 |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
\*---------------------------------------------------------------------------*/

package eu.engys.core.project.zero.cellzones;

public class CellZonesUtils {

    /*
     * MRF
     */
    public static final String MRF_SOURCE_KEY = "MRFSource";
    public static final String ORIGIN_KEY = "origin";
    public static final String OMEGA_KEY = "omega";
    public static final String AXIS_KEY = "axis";
    public static final String ATTACHED_PATCHES_KEY = "attachedPatches";
    public static final String ROTATING_PATCHES_KEY = "rotatingPatches";
    public static final String NON_ROTATING_PATCHES_KEY = "nonRotatingPatches";
    
    /*
     * POROUS
     */
    public static final String POROUS_DARCY_KEY = "porousDarcy";
    public static final String POROUS_POWER_LAW_KEY = "porousPowerLaw";
    public static final String EXPLICIT_POROSITY_SOURCE_KEY = "explicitPorositySource";
    public static final String E1_KEY = "e1";
    public static final String E2_KEY = "e2";
    public static final String D_KEY = "d";
    public static final String F_KEY = "f";
    public static final String C0_KEY = "C0";
    public static final String C1_KEY = "C1";
    public static final String POWER_LAW_KEY = "powerLaw";
    public static final String DARCY_FORCHHEIMER_KEY = "DarcyForchheimer";
    public static final String COORDINATE_SYSTEM_KEY = "coordinateSystem";
    public static final String COORDINATE_ROTATION_KEY = "coordinateRotation";
    public static final String CARTESIAN_KEY = "cartesian";
    public static final String AXES_ROTATION_KEY = "axesRotation";
    
    /*
     * Thermal
     */
    
    public static final String THERMAL_FIXED_KEY = "thermalFixed";
    public static final String THERMAL_SCALAR_KEY = "thermalScalar";
    public static final String THERMAL_EXPONENTIAL_KEY = "thermalExponential";
    public static final String FIXED_TEMPERATURE_CONSTRAINT_KEY = "fixedTemperatureConstraint";
    public static final String EXPONENTIAL_THERMAL_SOURCE_KEY = "exponentialThermalSource";
    public static final String SCALAR_SEMI_IMPLICT_SOURCE_KEY = "scalarSemiImplicitSource";
    public static final String SCALAR_EXPLICIT_SET_VALUE_KEY = "scalarExplicitSetValue";
    public static final String TEMPERATURE_KEY = "temperature";
    public static final String CE_KEY = "Ce";
    public static final String CM_KEY = "Cm";
    public static final String T0_KEY = "T0";
    public static final String MODE_KEY = "mode";
    public static final String VOLUME_MODE_KEY = "volumeMode";
    public static final String PLACE_HOLDER_KEY = "placeHolder";
    public static final String H_KEY = "h";
    public static final String T_KEY = "T";
    public static final String INJECTION_RATE_KEY = "injectionRate";
    public static final String INJECTION_RATE_SU_SP_KEY = "injectionRateSuSp";
    public static final String SPECIFIC_KEY = "specific";
    public static final String UNIFORM_KEY = "uniform";
    
    /*
     * Humidity
     */
    public static final String W_KEY = "w";
    
    /*
     * Sliding
     */
    public static final String t0_KEY = "t0";
    public static final String THETA_KEY = "theta";
    public static final String PERIOD_KEY = "period";
    public static final String ABSOLUTE_KEY = "absolute";
    public static final String[] VOLUME_MODE_KEYS = new String[] { ABSOLUTE_KEY, SPECIFIC_KEY };

    /*
     * OTHER KEYS
     */
    public static final String COEFFS_KEY = "Coeffs";
    public static final String ACTIVE_KEY = "active";
    public static final String SELECTION_MODE_KEY = "selectionMode";
    public static final String CELL_ZONE_KEY = "cellZone";
    

}
