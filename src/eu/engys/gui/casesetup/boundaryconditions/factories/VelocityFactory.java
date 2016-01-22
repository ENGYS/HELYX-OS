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


package eu.engys.gui.casesetup.boundaryconditions.factories;

import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ACCOMMODATION_COEFFICIENT_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ADVECTIVE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ALPHA_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.AXIS_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.CYLINDRICAL_INLET_VELOCITY_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FIXED_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FLOW_RATE_INLET_VELOCITY_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FLOW_RATE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FLUX_CORRECTED_VELOCITY_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FREESTREAM_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FREESTREAM_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INLET_DIRECTION_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INLET_OUTLET_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INLET_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.MASS_FLOW_RATE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.MAXWELL_SLIP_U_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.MOVING_WALL_VELOCITY_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.NO_SLIP_WALL_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PHI_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PRESSURE_DIRECT_INLET_OUTLET_VELOCITY_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PRESSURE_DIRECT_INLET_VELOCITY_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PRESSURE_INLET_OUTLET_VELOCITY_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PRESSURE_INLET_VELOCITY_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.REF_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.RHO_INLET_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.RHO_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ROTATING_WALL_VELOCITY_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.SLIP_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.SLIP_WALL_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.SURFACE_NORMAL_FIXED_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.UWALL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.VARIABLE_HEIGHT_FLOW_RATE_INLET_VELOCITY_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.VOLUMETRIC_FLOW_RATE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ZERO_GRADIENT_KEY;
import eu.engys.core.dictionary.Dictionary;

public class VelocityFactory {

    public static final Dictionary fixedValueVelocity = new Dictionary(U) {
        {
            add(TYPE, FIXED_VALUE_KEY);
            add(VALUE, "uniform (0 0 0)");
        }
    };
    public static final Dictionary movingWallVelocity = new Dictionary(U) {
        {
            add(TYPE, MOVING_WALL_VELOCITY_KEY);
            add(VALUE, "uniform (0 0 0)");
        }
    };
    public static final Dictionary zeroGradientVelocity = new Dictionary(U) {
        {
            add(TYPE, ZERO_GRADIENT_KEY);
            add(VALUE, "uniform (0 0 0)");
        }
    };
    public static final Dictionary advectiveVelocity = new Dictionary(U) {
        {
            add(TYPE, ADVECTIVE_KEY);
        }
    };
    public static final Dictionary inletOutletVelocity = new Dictionary(U) {
        {
            add(TYPE, INLET_OUTLET_KEY);
            add(VALUE, "uniform (0 0 0)");
            add(INLET_VALUE_KEY, "uniform (0 0 0)");
        }
    };
    public static final Dictionary cylindricalInletVelocity = new Dictionary(U) {
        {
            add(TYPE, CYLINDRICAL_INLET_VELOCITY_KEY);
            add(VALUE, "uniform (0 0 0)");
            add(AXIS_KEY, "(0 0 1)");
            add("centre", "(0 0 0)");
            add("axialVelocity", "30");
            add("rpm", "100");
            add("radialVelocity", "-10");
        }
    };
    public static final Dictionary surfaceNormalFixedValue = new Dictionary(U) {
        {
            add(TYPE, SURFACE_NORMAL_FIXED_VALUE_KEY);
            add(VALUE, "uniform (0 0 0)");
            add(REF_VALUE_KEY, "uniform -1.5");
        }
    };

    public static final Dictionary volumetricFlowRateInletVelocity = new Dictionary(U) {
        {
            add(TYPE, FLOW_RATE_INLET_VELOCITY_KEY);
            add(VALUE, "uniform (0 0 0)");
            add(VOLUMETRIC_FLOW_RATE_KEY, "0.1");
        }
    };

    public static final Dictionary massFlowRateInletVelocity = new Dictionary(U) {
        {
            add(TYPE, FLOW_RATE_INLET_VELOCITY_KEY);
            add(VALUE, "uniform (0 0 0)");
            add(MASS_FLOW_RATE_KEY, "0.1");
            add(RHO_KEY, "rho");
        }
    };

    public static final Dictionary massFlowRateInletVelocity_INCOMPRESSIBLE = new Dictionary(U) {
        {
            add(TYPE, FLOW_RATE_INLET_VELOCITY_KEY);
            add(VALUE, "uniform (0 0 0)");
            add(MASS_FLOW_RATE_KEY, "0.1");
            add(RHO_KEY, "rho");
            add(RHO_INLET_KEY, "1.0");
        }
    };

    public static final Dictionary variableHeightFlowRateInletVelocity = new Dictionary(U) {
        {
            add(TYPE, VARIABLE_HEIGHT_FLOW_RATE_INLET_VELOCITY_KEY);
            add(VALUE, "uniform (0 0 0)");
            add(FLOW_RATE_KEY, "0.2");
            add(ALPHA_KEY, "alpha.water");
        }
    };

    public static final Dictionary pressureInletVelocity = new Dictionary(U) {
        {
            add(TYPE, PRESSURE_INLET_VELOCITY_KEY);
            add(VALUE, "uniform (0 0 0)");
        }
    };

    public static final Dictionary fluxCorrectedVelocity = new Dictionary(U) {
        {
            add(TYPE, FLUX_CORRECTED_VELOCITY_KEY);
        }
    };

    public static final Dictionary pressureDirectedInletVelocity = new Dictionary(U) {
        {
            add(TYPE, PRESSURE_DIRECT_INLET_VELOCITY_KEY);
            add(VALUE, "uniform (0 0 0)");
            add(INLET_DIRECTION_KEY, "(1 0 0)");
        }
    };

    public static final Dictionary pressureInletOutletVelocity = new Dictionary(U) {
        {
            add(TYPE, PRESSURE_INLET_OUTLET_VELOCITY_KEY);
            add(VALUE, "uniform (0 0 0)");
        }
    };

    public static final Dictionary pressureDirectedInletOutletVelocity = new Dictionary(U) {
        {
            add(TYPE, PRESSURE_DIRECT_INLET_OUTLET_VELOCITY_KEY);
            add(VALUE, "uniform (0 0 0)");
            add(INLET_DIRECTION_KEY, "(1 0 0)");
        }
    };

    public static final Dictionary freestreamVelocity = new Dictionary(U) {
        {
            add(TYPE, FREESTREAM_KEY);
            add(VALUE, "uniform (0 0 0)");
            add(PHI_KEY, PHI_KEY);
            add(FREESTREAM_VALUE_KEY, "uniform (0 0 0)");
        }
    };

    public static final Dictionary slipWall = new Dictionary(U) {
        {
            add(TYPE, SLIP_KEY);
        }
    };

    public static final Dictionary slipWallCoupled = new Dictionary(U) {
        {
            add(TYPE, SLIP_WALL_KEY);
        }
    };

    public static final Dictionary maxwellSlipWall = new Dictionary(U) {
        {
            add(TYPE, MAXWELL_SLIP_U_KEY);
            add(VALUE, "uniform (0 0 0)");
            add(ACCOMMODATION_COEFFICIENT_KEY, "0.85");
            add("thermalCreep", "on");
            add("curvature", "on");
            add(UWALL, "uniform (0 0 0)");
        }
    };

    public static final Dictionary noSlipWall = new Dictionary(U) {
        {
            add(TYPE, FIXED_VALUE_KEY);
            add(VALUE, "uniform (0 0 0)");
        }
    };

    public static final Dictionary noSlipWallCoupled = new Dictionary(U) {
        {
            add(TYPE, NO_SLIP_WALL_KEY);
            add(VALUE, "uniform (0 0 0)");
        }
    };

    public static final Dictionary standardRotatingWallVelocity = new Dictionary(U) {
        {
            add(TYPE, ROTATING_WALL_VELOCITY_KEY);
            add(VALUE, "uniform (0 0 0)");
            add("origin", "(0 0 0)");
            add("axis", "(1 0 0)");
            add("omega", "5");
        }
    };

}
