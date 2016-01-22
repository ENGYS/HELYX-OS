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

package eu.engys.gui.casesetup.cellzones;

import static eu.engys.core.project.zero.cellzones.CellZoneType.HUMIDITY_KEY;
import static eu.engys.core.project.zero.cellzones.CellZoneType.MRF_KEY;
import static eu.engys.core.project.zero.cellzones.CellZoneType.POROUS_KEY;
import static eu.engys.core.project.zero.cellzones.CellZoneType.SLIDING_MESH_KEY;
import static eu.engys.core.project.zero.cellzones.CellZoneType.THERMAL_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.ABSOLUTE_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.ATTACHED_PATCHES_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.AXIS_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.C0_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.C1_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.CE_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.CM_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.D_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.E1_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.E2_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.F_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.NON_ROTATING_PATCHES_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.OMEGA_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.ORIGIN_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.PERIOD_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.PLACE_HOLDER_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.POROUS_DARCY_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.POROUS_POWER_LAW_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.SPECIFIC_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.T0_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.TEMPERATURE_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.THERMAL_EXPONENTIAL_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.THERMAL_FIXED_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.THERMAL_SCALAR_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.THETA_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.T_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.VOLUME_MODE_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.W_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.t0_KEY;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DimensionedScalar;
import eu.engys.util.DimensionalUnits;

public class CellZonesFactory {

    /*
     * MRF
     */
    public static Dictionary mrf = new Dictionary(MRF_KEY) {
        {
            add(TYPE, MRF_KEY);
            add(ORIGIN_KEY, "(0 0 0)");
            add(AXIS_KEY, "(0 0 1)");
            add(OMEGA_KEY, "constant 104.72");
            add(NON_ROTATING_PATCHES_KEY, "()");
            add(ATTACHED_PATCHES_KEY, "()");
        }
    };

    /*
     * Porous
     */
    public static Dictionary porousDarcyForchheimer = new Dictionary(POROUS_KEY) {
        {
            add(TYPE, POROUS_DARCY_KEY);
            add(E1_KEY, "(1 0 0)");
            add(E2_KEY, "(0 1 0)");
            add(new DimensionedScalar(D_KEY, "(100 1000 1000)", DimensionalUnits._M2));
            add(new DimensionedScalar(F_KEY, "(100 1000 1000)", DimensionalUnits._M));
        }
    };

    public static Dictionary porousPowerLaw = new Dictionary(POROUS_KEY) {
        {
            add(TYPE, POROUS_POWER_LAW_KEY);
            add(C0_KEY, "1e-14");
            add(C1_KEY, "1");
        }
    };

    /*
     * Thermal
     */
    public static Dictionary thermalFixedTemperature = new Dictionary(THERMAL_KEY) {
        {
            add(TYPE, THERMAL_FIXED_KEY);
            add(TEMPERATURE_KEY, "constant 350");
        }
    };

    public static Dictionary thermalFixedTemperature_OS = new Dictionary(THERMAL_KEY) {
        {
            add(TYPE, THERMAL_FIXED_KEY);
            add(T_KEY, "350");
        }
    };

    public static Dictionary thermalExponential = new Dictionary(THERMAL_KEY) {
        {
            add(TYPE, THERMAL_EXPONENTIAL_KEY);
            add(CM_KEY, "0.291");
            add(CE_KEY, "1.369");
            add(T0_KEY, "350");
        }
    };

    public static Dictionary thermalScalarSemiImplicit = new Dictionary(THERMAL_KEY) {
        {
            add(TYPE, THERMAL_SCALAR_KEY);
            add(VOLUME_MODE_KEY, ABSOLUTE_KEY);
            add(PLACE_HOLDER_KEY, "(65 0)");
        }
    };

    /*
     * Humidity
     */

    public static Dictionary humidity = new Dictionary(HUMIDITY_KEY) {
        {
            add(TYPE, HUMIDITY_KEY);
            add(VOLUME_MODE_KEY, ABSOLUTE_KEY);
            add(W_KEY, "(100 0)");
        }
    };

    /*
     * Rotating
     */
    public static Dictionary slidingMesh = new Dictionary(SLIDING_MESH_KEY) {
        {
            add(ORIGIN_KEY, "(0 0 0)");
            add(AXIS_KEY, "(0 0 1)");
            add(OMEGA_KEY, "1");

        }
    };

    public static Dictionary coupledSlidingMesh_steady = new Dictionary(SLIDING_MESH_KEY) {
        {
            add(ORIGIN_KEY, "(0 0 0)");
            add(AXIS_KEY, "(0 0 1)");
            add(THETA_KEY, "60");
            add(PERIOD_KEY, "150");
        }
    };
    
    public static Dictionary coupledSlidingMesh_transient = new Dictionary(SLIDING_MESH_KEY) {
        {
            add(ORIGIN_KEY, "(0 0 0)");
            add(AXIS_KEY, "(0 0 1)");
            add(OMEGA_KEY, "-308.92");
            add(t0_KEY, "2");
        }
    };

    // For tests purposes only
    public static Dictionary thermalScalarSemiImplicit_Specific = new Dictionary(THERMAL_KEY) {
        {
            add(TYPE, THERMAL_SCALAR_KEY);
            add(VOLUME_MODE_KEY, SPECIFIC_KEY);
            add(PLACE_HOLDER_KEY, "(1 2)");
        }
    };

}
