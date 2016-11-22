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

package eu.engys.gui.casesetup.boundaryconditions.factories;

import static eu.engys.core.project.zero.fields.Fields.EPSILON;
import static eu.engys.core.project.zero.fields.Fields.K;
import static eu.engys.core.project.zero.fields.Fields.MUT;
import static eu.engys.core.project.zero.fields.Fields.MU_SGS;
import static eu.engys.core.project.zero.fields.Fields.NUT;
import static eu.engys.core.project.zero.fields.Fields.NU_SGS;
import static eu.engys.core.project.zero.fields.Fields.NU_TILDA;
import static eu.engys.core.project.zero.fields.Fields.OMEGA;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.CLAMP_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.CS_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FIXED_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INLET_OUTLET_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INLET_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INTENSITY_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.KS_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.LENGTH_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.MIXING_LENGTH_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.MUT_K_ROUGH_WALL_FUNCTION_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.MUT_U_ROUGH_WALL_FUNCTION_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.NUT_K_ATM_ROUGH_WALL_FUNCTION_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.NUT_K_ROUGH_WALL_FUNCTION_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.NUT_TURBULENT_INTENSITY_LENGTH_SCALE_INLET_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.NUT_U_ROUGH_WALL_FUNCTION_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.OUT_OF_BOUNDS_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ROUGHNESS_CONSTANT_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ROUGHNESS_FACTOR_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ROUGHNESS_HEIGHT_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TABLE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TURBULENT_INTENSITY_KINETIC_ENERGY_INLET_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TURBULENT_MIXING_LENGTH_DISSIPATION_RATE_INLET_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TURBULENT_MIXING_LENGTH_FREQUENCY_INLET_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.UNIFORM_FIXED_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.UNIFORM_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.Z0_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ZERO_GRADIENT_KEY;

import eu.engys.core.dictionary.Dictionary;

public class TurbulenceFactory {

    /*
     * Fixed
     */

    public static final Dictionary kFixedValue = new Dictionary(K) {
        {
            add(TYPE, FIXED_VALUE_KEY);
            addUniform(VALUE, 0.01);
        }
    };
    public static final Dictionary omegaFixedValue = new Dictionary(OMEGA) {
        {
            add(TYPE, FIXED_VALUE_KEY);
            addUniform(VALUE, 0.01);
        }
    };
    public static final Dictionary epsilonFixedValue = new Dictionary(EPSILON) {
        {
            add(TYPE, FIXED_VALUE_KEY);
            addUniform(VALUE, 0.01);
        }
    };
    public static final Dictionary nutildaFixedValue = new Dictionary(NU_TILDA) {
        {
            add(TYPE, FIXED_VALUE_KEY);
            addUniform(VALUE, 0.01);
        }
    };

    /*
     * Mixed
     */
    public static final Dictionary kMixingLength = new Dictionary(K) {
        {
            add(TYPE, TURBULENT_INTENSITY_KINETIC_ENERGY_INLET_KEY);
            addUniform(VALUE, 0.01);
            add(INTENSITY_KEY, "0.05");
        }
    };
    public static final Dictionary epsilonMixingLength = new Dictionary(EPSILON) {
        {
            add(TYPE, TURBULENT_MIXING_LENGTH_DISSIPATION_RATE_INLET_KEY);
            addUniform(VALUE, 0.01);
            add(MIXING_LENGTH_KEY, "0.01");
        }
    };
    public static final Dictionary omegaMixingLength = new Dictionary(OMEGA) {
        {
            add(TYPE, TURBULENT_MIXING_LENGTH_FREQUENCY_INLET_KEY);
            addUniform(VALUE, 0.01);
            add(MIXING_LENGTH_KEY, "0.01");
        }
    };
    public static final Dictionary nuTildaMixingLength = new Dictionary(NU_TILDA) {
        {
            add(TYPE, NUT_TURBULENT_INTENSITY_LENGTH_SCALE_INLET_KEY);
            addUniform(VALUE, 0.01);
            add(INTENSITY_KEY, "0.05");
            add(LENGTH_KEY, "0.01");
        }
    };

    /*
     * Time-varying
     */
    public static final Dictionary kTimeVarying = new Dictionary(K) {
        {
            add(TYPE, UNIFORM_FIXED_VALUE_KEY);
            addUniform(VALUE, 0.01);
            add(UNIFORM_VALUE_KEY, TABLE_KEY + " ()");
            add(OUT_OF_BOUNDS_KEY, CLAMP_KEY);
        }
    };
    public static final Dictionary omegaTimeVarying = new Dictionary(OMEGA) {
        {
            add(TYPE, UNIFORM_FIXED_VALUE_KEY);
            addUniform(VALUE, 0.01);
            add(UNIFORM_VALUE_KEY, TABLE_KEY + " ()");
            add(OUT_OF_BOUNDS_KEY, CLAMP_KEY);
        }
    };
    public static final Dictionary epsilonTimeVarying = new Dictionary(EPSILON) {
        {
            add(TYPE, UNIFORM_FIXED_VALUE_KEY);
            addUniform(VALUE, 0.01);
            add(UNIFORM_VALUE_KEY, TABLE_KEY + " ()");
            add(OUT_OF_BOUNDS_KEY, CLAMP_KEY);
        }
    };
    public static final Dictionary nuTildaTimeVarying = new Dictionary(NU_TILDA) {
        {
            add(TYPE, UNIFORM_FIXED_VALUE_KEY);
            addUniform(VALUE, 0.01);
            add(UNIFORM_VALUE_KEY, TABLE_KEY + " ()");
            add(OUT_OF_BOUNDS_KEY, CLAMP_KEY);
        }
    };

    /*
     * Inlet outlet
     */

    public static final Dictionary kInletOutlet = new Dictionary(K) {
        {
            add(TYPE, INLET_OUTLET_KEY);
            addUniform(VALUE, 0.01);
            addUniform(INLET_VALUE_KEY, 0.01);
        }
    };
    public static final Dictionary omegaInletOutlet = new Dictionary(OMEGA) {
        {
            add(TYPE, INLET_OUTLET_KEY);
            addUniform(VALUE, 0.01);
            addUniform(INLET_VALUE_KEY, 0.01);
        }
    };
    public static final Dictionary epsilonInletOutlet = new Dictionary(EPSILON) {
        {
            add(TYPE, INLET_OUTLET_KEY);
            addUniform(VALUE, 0.01);
            addUniform(INLET_VALUE_KEY, 0.01);
        }
    };
    public static final Dictionary nuTildaInletOutlet = new Dictionary(NU_TILDA) {
        {
            add(TYPE, INLET_OUTLET_KEY);
            addUniform(VALUE, 0.01);
            addUniform(INLET_VALUE_KEY, 0.01);
        }
    };

    /*
     * Zero Gradient
     */
    public static final Dictionary kZeroGradient = new Dictionary(K) {
        {
            add(TYPE, ZERO_GRADIENT_KEY);
        }
    };
    public static final Dictionary omegaZeroGradient = new Dictionary(OMEGA) {
        {
            add(TYPE, ZERO_GRADIENT_KEY);
        }
    };
    public static final Dictionary epsilonZeroGradient = new Dictionary(EPSILON) {
        {
            add(TYPE, ZERO_GRADIENT_KEY);
        }
    };
    public static final Dictionary nuTildaZeroGradient = new Dictionary(NU_TILDA) {
        {
            add(TYPE, ZERO_GRADIENT_KEY);
        }
    };

    /*
     * Wall
     */
    public static final Dictionary nutkRoughWallFunction = new Dictionary(NUT) {
        {
            add(TYPE, NUT_K_ROUGH_WALL_FUNCTION_KEY);
            addUniform(VALUE, 0.0);
            addUniform(KS_KEY, 0.0);
            addUniform(CS_KEY, 0.5);
        }
    };
    public static final Dictionary nutkAtmRoughWallFunction = new Dictionary(NUT) {
        {
            add(TYPE, NUT_K_ATM_ROUGH_WALL_FUNCTION_KEY);
            addUniform(VALUE, 0.0);
            addUniform(Z0_KEY, 0.0);
        }
    };
    public static final Dictionary nutURoughWallFunction = new Dictionary(NUT) {
        {
            add(TYPE, NUT_U_ROUGH_WALL_FUNCTION_KEY);
            addUniform(VALUE, 0.0);
            add(ROUGHNESS_HEIGHT_KEY, "1e-5");
            add(ROUGHNESS_CONSTANT_KEY, "0.5");
            add(ROUGHNESS_FACTOR_KEY, "1");
        }
    };
    public static final Dictionary nuSgsURoughWallFunction = new Dictionary(NU_SGS) {
        {
            add(TYPE, NUT_U_ROUGH_WALL_FUNCTION_KEY);
            addUniform(VALUE, 0.0);
            add(ROUGHNESS_HEIGHT_KEY, "1e-5");
            add(ROUGHNESS_CONSTANT_KEY, "0.5");
            add(ROUGHNESS_FACTOR_KEY, "1");
        }
    };

    public static final Dictionary mutKRoughWallFunction = new Dictionary(MUT) {
        {
            add(TYPE, MUT_K_ROUGH_WALL_FUNCTION_KEY);
            addUniform(VALUE, 0.0);
            addUniform(KS_KEY, 0.0);
            addUniform(CS_KEY, 0.5);
        }
    };
    public static final Dictionary mutURoughWallFunction = new Dictionary(MUT) {
        {
            add(TYPE, MUT_U_ROUGH_WALL_FUNCTION_KEY);
            addUniform(VALUE, 0.0);
            add(ROUGHNESS_HEIGHT_KEY, "1e-5");
            add(ROUGHNESS_CONSTANT_KEY, "0.5");
            add(ROUGHNESS_FACTOR_KEY, "1");
        }
    };
    public static final Dictionary muSgsURoughWallFunction = new Dictionary(MU_SGS) {
        {
            add(TYPE, MUT_U_ROUGH_WALL_FUNCTION_KEY);
            addUniform(VALUE, 0.0);
            add(ROUGHNESS_HEIGHT_KEY, "1e-5");
            add(ROUGHNESS_CONSTANT_KEY, "0.5");
            add(ROUGHNESS_FACTOR_KEY, "1");
        }
    };

}
