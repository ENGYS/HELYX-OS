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


package eu.engys.gui.casesetup.boundaryconditions.utils;

import static eu.engys.core.dictionary.Dictionary.TYPE;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import net.java.dev.designgridlayout.Componentizer;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.gui.casesetup.boundaryconditions.TimeVaryingComboBoxController;
import eu.engys.gui.casesetup.boundaryconditions.TimeVaryingInterpolationTable;
import eu.engys.util.Symbols;

public class BoundaryConditionsUtils {

    /*
	 * TYPES
	 */
	public static final String ADVECTIVE_KEY = "advective";
	public static final String ALPHA_CONTACT_ANGLE_KEY = "alphaContactAngle";
	public static final String CYLINDRICAL_INLET_VELOCITY_KEY = "cylindricalInletVelocity";
	public static final String COMPRESSIBLE_TURBULENT_MIXING_LENGTH_DISSIPATION_RATE_INLET_KEY = "compressible::turbulentMixingLengthDissipationRateInlet";
	public static final String COMPRESSIBLE_TURBULENT_MIXING_LENGTH_FREQUENCY_INLET_KEY = "compressible::turbulentMixingLengthFrequencyInlet";
	public static final String CONSTANT_ALPHA_CONTACT_ANGLE_KEY = "constantAlphaContactAngle";

	public static final String COUPLED_TOTAL_VELOCITY_KEY = "coupledTotalVelocity";
	public static final String COUPLED_TOTAL_PRESSURE_KEY = "coupledTotalPressure";
	
	public static final String DYNAMIC_ALPHA_CONTACT_ANGLE_KEY = "dynamicAlphaContactAngle";
	public static final String FIXED_FLUX_PRESSURE_KEY = "fixedFluxPressure";
	public static final String FIXED_MEAN_VALUE_KEY = "fixedMeanValue";
	public static final String FIXED_VALUE_KEY = "fixedValue";
	public static final String FLOW_RATE_INLET_VELOCITY_KEY = "flowRateInletVelocity";
	public static final String FLOW_RATE_OUTLET_VELOCITY_KEY = "flowRateOutletVelocity";
	public static final String FLUX_CORRECTED_VELOCITY_KEY = "fluxCorrectedVelocity";
	public static final String FREESTREAM_PRESSURE_KEY = "freestreamPressure";
	public static final String FREESTREAM_KEY = "freestream";
	public static final String INLET_OUTLET_KEY = "inletOutlet";
	public static final String INTERPOLATED_CYLINDRICAL_VELOCITY_KEY = "interpolatedCylindricalVelocity";
	public static final String INTERPOLATED_FIXED_VALUE_KEY = "interpolatedFixedValue";
	public static final String INTERPOLATED_INLET_OUTLET_KEY = "interpolatedInletOutlet";
	public static final String MAXWELL_SLIP_U_KEY = "maxwellSlipU";
	public static final String MOVING_WALL_VELOCITY_KEY = "movingWallVelocity";
	public static final String MOVING_WALL_COUPLED_VELOCITY_KEY = "movingNoSlipWall";
	public static final String MUT_K_ROUGH_WALL_FUNCTION_KEY = "mutKRoughWallFunction";
	public static final String MUT_U_ROUGH_WALL_FUNCTION_KEY = "mutURoughWallFunction";
	public static final String NUT_K_ROUGH_WALL_FUNCTION_KEY = "nutkRoughWallFunction";
	public static final String NUT_K_ATM_ROUGH_WALL_FUNCTION_KEY = "nutkAtmRoughWallFunction";
	public static final String NUT_U_ROUGH_WALL_FUNCTION_KEY = "nutURoughWallFunction";
	public static final String NUT_TURBULENT_INTENSITY_LENGTH_SCALE_INLET_KEY = "nutTurbulentIntensityLengthScaleInlet";
	public static final String PRESSURE_DIRECT_INLET_VELOCITY_KEY = "pressureDirectedInletVelocity";
	public static final String PRESSURE_DIRECT_INLET_OUTLET_VELOCITY_KEY = "pressureDirectedInletOutletVelocity";
	public static final String PRESSURE_INLET_OUTLET_VELOCITY_KEY = "pressureInletOutletVelocity";
	public static final String PRESSURE_INLET_VELOCITY_KEY = "pressureInletVelocity";
	public static final String RESISTIVE_PRESSURE_KEY = "resistivePressure";
	public static final String RESISTIVE_VELOCITY_KEY = "resistiveVelocity";
	public static final String ROTATING_WALL_VELOCITY_KEY = "rotatingWallVelocity";
	public static final String ROTATING_WALL_COUPLED_VELOCITY_KEY = "rotatingNoSlipWall";
	public static final String SLIP_KEY = "slip";
	public static final String SLIP_WALL_KEY = "slipWall";
	public static final String NO_SLIP_WALL_KEY = "noSlipWall";
	public static final String SUPERSONIC_FREESTREAM_KEY = "supersonicFreestream";
	public static final String SURFACE_NORMAL_FIXED_VALUE_KEY = "surfaceNormalFixedValue";
	public static final String TANGENTIAL_VELOCITY_KEY = "tangentialVelocity";
	public static final String TOTAL_PRESSURE_KEY = "totalPressure";
	public static final String TURBULENT_INTENSITY_KINETIC_ENERGY_INLET_KEY = "turbulentIntensityKineticEnergyInlet";
	public static final String TURBULENT_MIXING_LENGTH_DISSIPATION_RATE_INLET_KEY = "turbulentMixingLengthDissipationRateInlet";
	public static final String TURBULENT_MIXING_LENGTH_FREQUENCY_INLET_KEY = "turbulentMixingLengthFrequencyInlet";
	public static final String UNIFORM_FIXED_VALUE_KEY = "uniformFixedValue";
	public static final String UNIFORM_TOTAL_PRESSURE_KEY = "uniformTotalPressure";
	public static final String VARIABLE_HEIGHT_FLOW_RATE_INLET_VELOCITY_KEY = "variableHeightFlowRateInletVelocity";
	public static final String VELOCITY_GRADIENT_DISSIPATION_INLET_OUTLET_KEY = "velocityGradientDissipationInletOutlet";
	public static final String WAVE_TRANSMISSIVE_KEY = "waveTransmissive";
	public static final String WHEEL_VELOCITY_KEY = "wheelVelocity";
	public static final String WIND_PROFILE_DIRECTION_VELOCITY_KEY = "windProfileDirectionVelocity";
	public static final String ZERO_GRADIENT_KEY = "zeroGradient";

	/*
	 * OTHER KEYS
	 */
	public static final String ACCOMMODATION_COEFFICIENT_KEY = "accommodationCoeff";
	public static final String ALPHA_KEY = "alpha";
	public static final String AXIS_KEY = "axis";
	public static final String CENTRE_KEY = "centre";
	public static final String CLAMP_KEY = "clamp";
	public static final String CS_KEY = "Cs";
	public static final String DATA_KEY = "data";
	public static final String DIRECTION_KEY = "direction";
	public static final String DISTANCE_ALONG_VECTOR_KEY = "distanceAlongVector";
	public static final String DISTANCE_TYPE_KEY = "distanceType";
	public static final String FIELD_KEY = "field";
	public static final String FILE_KEY = "file";
	public static final String FILE_NAME_KEY = "fileName";
	public static final String FLOW_RATE_KEY = "flowRate";
	public static final String FREESTREAM_VALUE_KEY = "freestreamValue";
	public static final String GAMMA_KEY = "gamma";
	public static final String GRADIENT_KEY = "gradient";
	public static final String INLET_VALUE_KEY = "inletValue";
	public static final String INLET_DIRECTION_KEY = "inletDirection";
	public static final String INTENSITY_KEY = "intensity";
	public static final String KS_KEY = "Ks";
	public static final String LENGTH_KEY = "length";
	public static final String LIMIT_KEY = "limit";
	public static final String MASS_FLOW_RATE_KEY = "massFlowRate";
	public static final String MEAN_VALUE_KEY = "meanValue";
	public static final String MIXING_LENGTH_KEY = "mixingLength";
	public static final String NONE_KEY = "none";
	public static final String NORMAL_KEY = "normal";
	public static final String OMEGA_KEY = "omega";
	public static final String ORIGIN_KEY = "origin";
	public static final String OUT_OF_BOUNDS_KEY = "outOfBounds";
	public static final String P0_KEY = "p0";
	public static final String PHASE_KEY = "phase";
	public static final String PHI_KEY = "phi";
	public static final String POINT_KEY = "point";
	public static final String POINT_DISTANCE_KEY = "pointDistance";
	public static final String PRESSURE_KEY = "pressure";
	public static final String RHO_KEY = "rho";
	public static final String RHO_INLET_KEY = "rhoInlet";
	public static final String REF_VALUE_KEY = "refValue";
	public static final String ROUGHNESS_CONSTANT_KEY = "roughnessConstant";
	public static final String ROUGHNESS_HEIGHT_KEY = "roughnessHeight";
	public static final String ROUGHNESS_FACTOR_KEY = "roughnessFactor";
	public static final String TABLE_KEY = "table";
	public static final String TABLE_FILE_KEY = "tableFile";
	public static final String THETA_0_KEY = "theta0";
	public static final String THETA_A_KEY = "thetaA";
	public static final String THETA_PROPERTIES_KEY = "thetaProperties";
	public static final String THETA_R_KEY = "thetaR";
	public static final String U_THETA_KEY = "uTheta";
	public static final String UNIFORM_VALUE_KEY = "uniformValue";
	public static final String UNIFORM_KEY = "uniform";
	public static final String USE_WALL_DISTANCE_KEY = "useWallDistance";
	public static final String UWALL = "Uwall";
	public static final String VOLUMETRIC_FLOW_RATE_KEY = "volumetricFlowRate";
	public static final String VALUE_KEY = "value";
	public static final String WALL_DISTANCE_KEY = "wallDistance";
	public static final String WIND_DIRECTION_KEY = "windDirection";
	public static final String X_KEY = "x";
	public static final String XOFFSET_KEY = "xoffset";
	public static final String XSCALE_KEY = "xscale";
	public static final String YOFFSET_KEY = "yoffset";
	public static final String Y_KEY = "y";
	public static final String YSCALE_KEY = "yscale";
	public static final String Z_KEY = "z";
	public static final String Z0_KEY = "z0";

	/*
	 * LISTS
	 */
	public static final String[] LIMIT_KEYS = { NONE_KEY, GRADIENT_KEY, "zeroGradient", ALPHA_KEY };

	// TO ORDER

	public static final String[] INTERP_ALGO_TYPE_KEYS = { "repeat", "clamp", "warn", "error" };
	public static final String TABLE_FILE_COEFFS_KEY = "tableFileCoeffs";

	public static final String[] INTERP_ALGO_TYPE_LABELS = { "Repeat", "Clamp", "Warn", "Error" };

	public static final String ZERO_GRADIENT_LABEL = "Zero Gradient";
	public static final String FIXED_VALUE_LABEL = "Fixed Value";

	public static final String NON_UNIFORM_TEMPERATURE_LABEL = "Non-uniform Temperature";
	public static final String NON_UNIFORM_TURBULENCE_LABEL = "Non-uniform Turbulence";
	public static final String NON_UNIFORM_PHASE_FRACTION_LABEL = "Non-uniform Phase Fraction";

	public static final String TIME_VARYING_LABEL = "Time-varying";
	public static final String TIME_VARYING_VELOCITY_LABEL = "Time-varying Velocity";
	public static final String TIME_VARYING_FLOW_RATE_LABEL = "Time-varying Flow Rate";
	public static final String TIME_VARYING_TEMPERATURE_LABEL = "Time-varying Temperature";
	public static final String TIME_VARYING_TURBULENCE_LABEL = "Time-varying Turbulence";
	public static final String TIME_VARYING_PHASE_FRACTION_LABEL = "Time-varying Phase Fraction";

	public static final String TABLE_DATA_LABEL = "Table Data";
	public static final String INTERPOLATION_PROFILE_LABEL = "Interpolation Profile";
	public static final String FROM_FILE_LABEL = "From File";

	public static final String INTERPOLATION_ALGORITHM_LABEL = "Interpolation Algorithm";
	
	public static final String VELOCITY_LABEL = "Velocity " + Symbols.M_S;

	public static void buildSimpleFixedVelocityPanel(DictionaryPanelBuilder builder, DictionaryModel model) {
		builder.addComponent(VELOCITY_LABEL, model.bindUniformPoint("value"));
	}

	public static void buildFreestreamVelocityPanel(DictionaryPanelBuilder builder, DictionaryModel model) {
		builder.addComponent(VELOCITY_LABEL, model.bindUniformPoint("value", "freestreamValue"));
	}

	public static void buildFixedCylindricalVelocityPanel(DictionaryPanelBuilder builder, DictionaryModel model) {
		builder.addComponent("Axis", model.bindPoint("axis"));
		builder.addComponent("Centre", model.bindPoint("centre"));
		builder.addComponent("Axial Velocity", model.bindDouble("axialVelocity"));
		builder.addComponent("RPM", model.bindDouble("rpm"));
		builder.addComponent("Radial Velocity", model.bindDouble("radialVelocity"));
	}

	public static void buildTimeVaryingScalarPanel(DictionaryPanelBuilder builder, DictionaryModel model, String dictionaryKey, String name) {
		buildTimeVaryingInterpolationTablePanel(builder, model, dictionaryKey, new String[]{name});
	}

	public static void buildTimeVaryingVectorPanel(DictionaryPanelBuilder builder, DictionaryModel model, String dictionaryKey, String X, String Y, String Z) {
		buildTimeVaryingInterpolationTablePanel(builder, model, dictionaryKey, new String[]{X,Y,Z});
	}

	/*
	 * Utils
	 */

	private static void buildTimeVaryingInterpolationTablePanel(DictionaryPanelBuilder builder, final DictionaryModel model, final String dictionaryKey, final String[] names) {
		builder.startChoice(INTERPOLATION_PROFILE_LABEL, new TimeVaryingComboBoxController(model, dictionaryKey));

		builder.startGroup(DATA_KEY, TABLE_DATA_LABEL);
		JButton editButton = new JButton(new AbstractAction("Edit") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new TimeVaryingInterpolationTable(model, dictionaryKey + " " + TABLE_KEY, names).showDialog();
			}
		});
		editButton.setName("Edit");
		builder.addComponent("", Componentizer.create().minToPref(editButton).component());
		builder.endGroup();

		builder.startGroup(FILE_KEY, FROM_FILE_LABEL);
		builder.addComponent("", model.bindFile(FILE_NAME_KEY));
		builder.endGroup();

		builder.endChoice();

		builder.addComponent(INTERPOLATION_ALGORITHM_LABEL, model.bindSelection(OUT_OF_BOUNDS_KEY, INTERP_ALGO_TYPE_KEYS, INTERP_ALGO_TYPE_LABELS));
	}

	/*
	 * Time varying fix
	 */

	public static void fixTimeVaryingLoad(Dictionary dict, String dictionaryKey) {
		String type = dict.lookup(TYPE);
		if (type.equals(UNIFORM_FIXED_VALUE_KEY) || type.equals(ROTATING_WALL_VELOCITY_KEY) || type.equals(FLOW_RATE_INLET_VELOCITY_KEY) || type.equals(UNIFORM_TOTAL_PRESSURE_KEY)) {
			if (isTableFile(dict, dictionaryKey)) {
				if (dict.found(TABLE_FILE_COEFFS_KEY)) {
					Dictionary tableFileCoeffs = dict.subDict(TABLE_FILE_COEFFS_KEY);
					dict.add(OUT_OF_BOUNDS_KEY, tableFileCoeffs.lookup(OUT_OF_BOUNDS_KEY));
					dict.add(FILE_NAME_KEY, tableFileCoeffs.lookup(FILE_NAME_KEY));
					dict.remove(TABLE_FILE_COEFFS_KEY);
				}
			}
		}
	}

	public static void fixTimeVaryingSave(Dictionary dict, String dictionaryKey) {
		String type = dict.lookup(TYPE);
		if (type.equals(UNIFORM_FIXED_VALUE_KEY) || type.equals(ROTATING_WALL_VELOCITY_KEY) || type.equals(FLOW_RATE_INLET_VELOCITY_KEY) || type.equals(UNIFORM_TOTAL_PRESSURE_KEY)) {
			if (isTableFile(dict, dictionaryKey)) {
				Dictionary tableFileCoeffs = new Dictionary(TABLE_FILE_COEFFS_KEY);
				tableFileCoeffs.add(OUT_OF_BOUNDS_KEY, dict.lookup(OUT_OF_BOUNDS_KEY));
				tableFileCoeffs.add(FILE_NAME_KEY, dict.lookup(FILE_NAME_KEY));
				dict.remove(OUT_OF_BOUNDS_KEY);
				dict.remove(FILE_NAME_KEY);
				dict.add(tableFileCoeffs);
			} else {
				dict.remove(FILE_NAME_KEY);
			}
		}
	}

	public static boolean isTableFile(Dictionary dict, String dictionaryKey) {
		if (dict == null) {
			return false;
		}
		if (!dict.found(dictionaryKey)) {
			return false;
		}
		if (!dict.isField(dictionaryKey)) {
			return false;
		}
		return dict.lookup(dictionaryKey).equals(TABLE_FILE_KEY);
	}

	public static void loadFlowRate(Dictionary U, DictionaryPanelBuilder builder) {
        if (U.found(MASS_FLOW_RATE_KEY)) {
            if (U.lookup(MASS_FLOW_RATE_KEY).startsWith(TABLE_KEY)) {
                fixTimeVaryingLoad(U, MASS_FLOW_RATE_KEY);
                builder.selectDictionaryByKey(BoundaryConditionsUtils.getTimeVaryingMassFlowRate(), U);
            } else {
                builder.selectDictionaryByKey(BoundaryConditionsUtils.getMassFlowRate(), U);
            }
        } else if (U.found(VOLUMETRIC_FLOW_RATE_KEY)) {
            if (U.lookup(VOLUMETRIC_FLOW_RATE_KEY).startsWith(TABLE_KEY)) {
                fixTimeVaryingLoad(U, VOLUMETRIC_FLOW_RATE_KEY);
                builder.selectDictionaryByKey(BoundaryConditionsUtils.getTimeVaryingVolumetricFlowRate(), U);
            } else {
                builder.selectDictionaryByKey(BoundaryConditionsUtils.getVolumetricFlowRate(), U);
            }
        } else if (U.found(FLOW_RATE_KEY)) {
            builder.selectDictionaryByKey(BoundaryConditionsUtils.getVariableHeightFlowRate(), U);
        }
    }

    public static String getMassFlowRate() {
        return "massFlowRate";
    }

    public static String getTimeVaryingMassFlowRate() {
        return "timeVaryingMassFlowRate";
    }

    public static String getTimeVaryingVolumetricFlowRate() {
        return "timeVaryingVolumetricFlowRate";
    }

    public static String getVolumetricFlowRate() {
        return "volumetricFlowRate";
    }

    public static String getVariableHeightFlowRate() {
        return "variableHeightFlowRate";
    }

}
