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
package eu.engys.gui.casesetup.boundaryconditions.utils;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.util.Symbols.CUBE;
import static eu.engys.util.Symbols.DENSITY;
import static eu.engys.util.Symbols.DOT;
import static eu.engys.util.Symbols.EPSILON_SYMBOL;
import static eu.engys.util.Symbols.KELVIN;
import static eu.engys.util.Symbols.K_SYMBOL;
import static eu.engys.util.Symbols.M;
import static eu.engys.util.Symbols.M2_S;
import static eu.engys.util.Symbols.M2_S2;
import static eu.engys.util.Symbols.M_S;
import static eu.engys.util.Symbols.OMEGA_SYMBOL_RAD;
import static eu.engys.util.Symbols.OMEGA_SYMBOL_S;
import static eu.engys.util.Symbols.PASCAL;
import static eu.engys.util.Symbols.SQUARE;
import static eu.engys.util.ui.UiUtil.EDIT_BUTTON_LABEL;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.StartWithFinder;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.gui.casesetup.boundaryconditions.charts.DictionarySparkline;
import eu.engys.gui.casesetup.boundaryconditions.charts.SparklineChart;
import eu.engys.gui.casesetup.boundaryconditions.panels.ScalarTimeVaryingInterpolationTable;
import eu.engys.gui.casesetup.boundaryconditions.panels.TimeVaryingComboBoxController;
import eu.engys.gui.casesetup.boundaryconditions.panels.VectorTimeVaryingInterpolationTable;
import eu.engys.util.Symbols;
import net.java.dev.designgridlayout.Componentizer;

public class BoundaryConditionsUtils {

    /*
     * TYPES
     */
    public static final String ADVECTIVE_KEY = "advective";
    public static final String ALPHA_CONTACT_ANGLE_KEY = "alphaContactAngle";
    public static final String CALCULATED_KEY = "calculated";
    public static final String CYLINDRICAL_INLET_VELOCITY_KEY = "cylindricalInletVelocity";
    public static final String COMPRESSIBLE_TURBULENT_CONVECTIVE_TEMPERATURE_KEY = "compressible::turbulentConvectiveTemperature";
    public static final String COMPRESSIBLE_TURBULENT_HEAT_FLUX_TEMPERATURE_KEY = "compressible::turbulentHeatFluxTemperature";
    public static final String COMPRESSIBLE_TURBULENT_MIXING_LENGTH_DISSIPATION_RATE_INLET_KEY = "compressible::turbulentMixingLengthDissipationRateInlet";
    public static final String COMPRESSIBLE_TURBULENT_MIXING_LENGTH_FREQUENCY_INLET_KEY = "compressible::turbulentMixingLengthFrequencyInlet";
    public static final String CONSTANT_ALPHA_CONTACT_ANGLE_KEY = "constantAlphaContactAngle";
    public static final String COUPLED_TOTAL_VELOCITY_KEY = "coupledTotalVelocity";
    public static final String COUPLED_TOTAL_PRESSURE_KEY = "coupledTotalPressure";
    public static final String CURVATURE_KEY = "curvature";
    public static final String DYNAMIC_ALPHA_CONTACT_ANGLE_KEY = "dynamicAlphaContactAngle";
    public static final String FIXED_FLUX_PRESSURE_KEY = "fixedFluxPressure";
    public static final String FIXED_MEAN_VALUE_KEY = "fixedMeanValue";
    public static final String FIXED_VALUE_KEY = "fixedValue";
    public static final String FLOW_RATE_INLET_VELOCITY_KEY = "flowRateInletVelocity";
    public static final String FLOW_RATE_OUTLET_VELOCITY_KEY = "flowRateOutletVelocity";
    public static final String FLUX_CORRECTED_VELOCITY_KEY = "fluxCorrectedVelocity";
    public static final String FREESTREAM_PRESSURE_KEY = "freestreamPressure";
    public static final String FREESTREAM_KEY = "freestream";
    public static final String GREY_DIFFUSIVE_RADIATION_KEY = "greyDiffusiveRadiation";
    public static final String GROUP_OF_HUMANS_CO2_KEY = "groupOfHumansCO2";
    public static final String GROUP_OF_HUMANS_HEAT_FLUX_KEY = "groupOfHumansHeatFlux";
    public static final String GROUP_OF_HUMANS_HUMIDITY_FLUX_KEY = "groupOfHumansHumidityFlux";
    public static final String INCOMPRESSIBLE_TURBULENT_CONVECTIVE_TEMPERATURE_KEY = "incompressible::turbulentConvectiveTemperature";
    public static final String INCOMPRESSIBLE_TURBULENT_HEAT_FLUX_TEMPERATURE_KEY = "incompressible::turbulentHeatFluxTemperature";
    public static final String INLET_OUTLET_KEY = "inletOutlet";
    public static final String INLET_OUTLET_TOTAL_TEMPERATURE_KEY = "inletOutletTotalTemperature";
    public static final String INTERPOLATED_CYLINDRICAL_VELOCITY_KEY = "interpolatedCylindricalVelocity";
    public static final String INTERPOLATED_FIXED_VALUE_KEY = "interpolatedFixedValue";
    public static final String INTERPOLATED_INLET_OUTLET_KEY = "interpolatedInletOutlet";
    public static final String MAXWELL_SLIP_U_KEY = "maxwellSlipU";
    public static final String MOVING_WALL_VELOCITY_KEY = "movingWallVelocity";
    public static final String MOVING_WALL_VELOCITY_KEY_COUPLED = "movingNoSlipWall";
    public static final String MUT_K_ROUGH_WALL_FUNCTION_KEY = "mutKRoughWallFunction";
    public static final String MUT_U_ROUGH_WALL_FUNCTION_KEY = "mutURoughWallFunction";
    public static final String NUT_K_ROUGH_WALL_FUNCTION_KEY = "nutkRoughWallFunction";
    public static final String NUT_K_ATM_ROUGH_WALL_FUNCTION_KEY = "nutkAtmRoughWallFunction";
    public static final String NUT_U_ROUGH_WALL_FUNCTION_KEY = "nutURoughWallFunction";
    public static final String NUT_TURBULENT_INTENSITY_LENGTH_SCALE_INLET_KEY = "nutTurbulentIntensityLengthScaleInlet";
    public static final String PHASE_CHANGE_HUMIDITY_KEY = "phaseChangeHumidity";
    public static final String PRESSURE_DIRECTED_INLET_VELOCITY_KEY = "pressureDirectedInletVelocity";
    public static final String PRESSURE_DIRECT_INLET_OUTLET_VELOCITY_KEY = "pressureDirectedInletOutletVelocity";
    public static final String PRESSURE_INLET_OUTLET_VELOCITY_KEY = "pressureInletOutletVelocity";
    public static final String PRESSURE_INLET_VELOCITY_KEY = "pressureInletVelocity";
    public static final String PRESSURE_OUTLET_KEY = "pressureOutlet";
    public static final String RELATIVE_HUMIDITY_INLET_OUTLET_KEY = "relativeHumidityInletOutlet";
    public static final String RESISTIVE_PRESSURE_KEY = "resistivePressure";
    public static final String RESISTIVE_VELOCITY_KEY = "resistiveVelocity";
    public static final String ROTATING_WALL_VELOCITY_KEY = "rotatingWallVelocity";
    public static final String ROTATING_NO_SLIP_WALL_KEY = "rotatingNoSlipWall";
    public static final String SLIP_KEY = "slip";
    public static final String SLIP_WALL_KEY = "slipWall";
    public static final String NO_SLIP_WALL_KEY = "noSlipWall";
    public static final String SUPERSONIC_FREESTREAM_KEY = "supersonicFreestream";
    public static final String SURFACE_NORMAL_FIXED_VALUE_KEY = "surfaceNormalFixedValue";
    public static final String SMOLUCHOWSKI_JUMP_T_KEY = "smoluchowskiJumpT";
    public static final String START_DAMPING_ANGLE_KEY = "startDampingAngle";
    public static final String TURBULENT_ABL_INLET_VELOCITY_KEY = "turbulentAtmBLInletVelocity";
    public static final String TANGENTIAL_VELOCITY_KEY = "tangentialVelocity";
    public static final String THERMAL_CREEP_KEY = "thermalCreep";
    public static final String TOTAL_PRESSURE_KEY = "totalPressure";
    public static final String TOTAL_TEMPERATURE_KEY = "totalTemperature";
    public static final String TRANSLATING_NO_SLIP_WALL_VELOCITY = "translatingNoSlipWallVelocity";
    public static final String TURBULENT_HEAT_FLUX_TEMPERATURE_KEY = "turbulentHeatFluxTemperature";
    public static final String TURBULENT_INTENSITY_KINETIC_ENERGY_INLET_KEY = "turbulentIntensityKineticEnergyInlet";
    public static final String TURBULENT_MIXING_LENGTH_DISSIPATION_RATE_INLET_KEY = "turbulentMixingLengthDissipationRateInlet";
    public static final String TURBULENT_MIXING_LENGTH_FREQUENCY_INLET_KEY = "turbulentMixingLengthFrequencyInlet";
    public static final String UNIFORM_FIXED_VALUE_KEY = "uniformFixedValue";
    public static final String UNIFORM_TOTAL_PRESSURE_KEY = "uniformTotalPressure";
    public static final String VARIABLE_HEIGHT_FLOW_RATE_INLET_VELOCITY_KEY = "variableHeightFlowRateInletVelocity";
    public static final String VELOCITY_GRADIENT_DISSIPATION_INLET_OUTLET_KEY = "velocityGradientDissipationInletOutlet";
    public static final String WALL_PRESSURE_KEY = "wallPressure";
    public static final String WAVE_TRANSMISSIVE_KEY = "waveTransmissive";
    public static final String WHEEL_VELOCITY_KEY = "wheelVelocity";
    public static final String WIND_PROFILE_DIRECTION_VELOCITY_KEY = "windProfileDirectionVelocity";
    public static final String ZERO_GRADIENT_ANGLE_KEY = "zeroGradientAngle";
    public static final String ZERO_GRADIENT_KEY = "zeroGradient";

    /*
     * OTHER KEYS
     */

    public static final String ACCOMMODATION_COEFFICIENT_KEY = "accommodationCoeff";
    public static final String AGE_KEY = "age";
    public static final String AIJ_KEY = "AIJ";
    public static final String ALPHA_KEY = "alpha";
    public static final String ALPHA_EFF_KEY = "alphaEff";
    public static final String ALPHA_WALL_KEY = "alphaWall";
    public static final String AXIAL_VELOCITY_KEY = "axialVelocity";
    public static final String AXIS_KEY = "axis";
    public static final String AVERAGING_DISTANCE_KEY = "averagingDistance";
    public static final String C1_KEY = "C1";
    public static final String C2_KEY = "C2";
    public static final String CENTRE_KEY = "centre";
    public static final String CLAMP_KEY = "clamp";
    public static final String CO2_MASS_FRACTION_LABEL = "CO" + Symbols.SUBSCRIPT_2 + " Mass Fraction";
    public static final String COFG_KEY = "CofG";
    public static final String CONTACT_ANGLE_KEY = "contactAngle";
    public static final String CONTACT_RADIUS_KEY = "contactRadius";
    public static final String CP_KEY = "Cp";
    public static final String CP0_KEY = "Cp0";
    public static final String CN_KEY = "Cn";
    public static final String CS_KEY = "Cs";
    public static final String CT_KEY = "Ct";
    public static final String DATA_KEY = "data";
    public static final String DIRECTION_KEY = "direction";
    public static final String DEFAULT_KEY = "default";
    public static final String DEMOGRAPHICS_KEY = "demographics";
    public static final String DISTANCE_ALONG_VECTOR_KEY = "distanceAlongVector";
    public static final String DISTANCE_TYPE_KEY = "distanceType";
    public static final String DROPLETS_KEY = "droplets";
    public static final String EMISSIVITY_KEY = "emissivity";
    public static final String EMISSIVITY_MODE_KEY = "emissivityMode";
    public static final String ERROR_KEY = "error";
    public static final String FEMALE_ADULT_KEY = "femaleAdult";
    public static final String FEMALE_CHILD_KEY = "femaleChild";
    public static final String FIELD_KEY = "field";
    public static final String FIELD_INF_KEY = "fieldInf";
    public static final String FILE_KEY = "file";
    public static final String FILE_NAME_KEY = "fileName";
    public static final String FILM_MASS_KEY = "filmMass";
    public static final String FIXED_KEY = "fixed";
    public static final String FLOW_RATE_KEY = "flowRate";
    public static final String FLUID_THERMO_KEY = "fluidThermo";
    public static final String FLUX_KEY = "flux";
    public static final String FREESTREAM_VALUE_KEY = "freestreamValue";
    public static final String GAMMA_KEY = "gamma";
    public static final String GRADIENT_KEY = "gradient";
    public static final String HEAT_SOURCE_KEY = "heatSource";
    public static final String HREF_KEY = "Href";
    public static final String HEIGHT_KEY = "height";
    public static final String HUB_SPEED_KEY = "hubSpeed";
    public static final String I_USER_DEFINED_KEY = "iUserDefined";
    public static final String INLET_VALUE_KEY = "inletValue";
    public static final String INLET_DIRECTION_KEY = "inletDirection";
    public static final String INOUT_KEY = "inout";
    public static final String INTENSITY_KEY = "intensity";
    public static final String KAPPA_KEY = "kappa";
    public static final String KAPPA_EFF_KEY = "kappaEff";
    public static final String KAPPA_NAME_KEY = "kappaName";
    public static final String KS_KEY = "Ks";
    public static final String L_USER_DEFINED_KEY = "LUserDefined";
    public static final String LAMBDA_KEY = "lambda";
    public static final String LAYERS_KEY = "layers";
    public static final String LENGTH_KEY = "length";
    public static final String LIMIT_KEY = "limit";
    public static final String LINF_KEY = "lInf";
    public static final String LOOKUP_KEY = "lookup";
    public static final String MAGNITUDE_KEY = "magnitude";
    public static final String MALE_ADULT_KEY = "maleAdult";
    public static final String MALE_CHILD_KEY = "maleChild";
    public static final String MASS_FLOW_RATE_KEY = "massFlowRate";
    public static final String MEAN_VALUE_KEY = "meanValue";
    public static final String MIXING_KEY = "mixing";
    public static final String MIXING_LENGTH_KEY = "mixingLength";
    public static final String N_KEY = "n";
    public static final String NONE_KEY = "none";
    public static final String NONUNIFROM_KEY = "nonunifrom";
    public static final String NORMAL_KEY = "normal";
    public static final String NUMBER_KEY = "number";
    public static final String OMEGA_KEY = "omega";
    public static final String ORIGIN_KEY = "origin";
    public static final String OUT_OF_BOUNDS_KEY = "outOfBounds";
    public static final String P0_KEY = "p0";
    public static final String PHASE_KEY = "phase";
    public static final String PHI_KEY = "phi";
    public static final String PINF_KEY = "pInf";
    public static final String POINT_KEY = "point";
    public static final String POINT_DISTANCE_KEY = "pointDistance";
    public static final String POWER_KEY = "power";
    public static final String PRESSURE_KEY = "pressure";
    public static final String PROFILE_TYPE_KEY = "profileType";
    public static final String PSI_KEY = "psi";
    public static final String PVALUE_KEY = "pValue";
    public static final String Q_KEY = "q";
    public static final String QADD_KEY = "qadd";
    public static final String QR_KEY = "Qr";
    public static final String R_USER_DEFINED_KEY = "RUserDefined";
    public static final String RADIAL_VELOCITY_KEY = "radialVelocity";
    public static final String RAU_KEY = "rAU";
    public static final String REGION0_KEY = "region0";
    public static final String REPEAT_KEY = "repeat";
    public static final String RHO_KEY = "rho";
    public static final String RHOK_KEY = "rhok";
    public static final String RHO_INLET_KEY = "rhoInlet";
    public static final String REF_VALUE_KEY = "refValue";
    public static final String RELATIVE_HUMIDITY_KEY = "relativeHumidity";
    public static final String RICHARDS_HOXEY_KEY = "RichardsHoxey";
    public static final String ROUGHNESS_CONSTANT_KEY = "roughnessConstant";
    public static final String ROUGHNESS_HEIGHT_KEY = "roughnessHeight";
    public static final String ROUGHNESS_FACTOR_KEY = "roughnessFactor";
    public static final String RPM_KEY = "rpm";
    public static final String SOLAR_TRANSMISSIVITY_KEY = "solarTransmissivity";
    public static final String STEADY_EVAPORATION_KEY = "steadyEvaporation";
    public static final String T0_KEY = "T0";
    public static final String TABLE_KEY = "table";
    public static final String TABLE_FILE_KEY = "tableFile";
    public static final String THERMAL_COUPLING_KEY = "thermalCoupling";
    public static final String THERMO_PSI_KEY = "thermo:psi";
    public static final String THETA_0_KEY = "theta0";
    public static final String THETA_A_KEY = "thetaA";
    public static final String THETA_PROPERTIES_KEY = "thetaProperties";
    public static final String THETA_R_KEY = "thetaR";
    public static final String THICKNESS_KEY = "thickness";
    public static final String TIMEVARYING_KEY = "timevarying";
    public static final String TINF_KEY = "Tinf";
    public static final String TRANSMISSIVITY_KEY = "transmissivity";
    public static final String TWALL = "Twall";
    public static final String U_THETA_KEY = "uTheta";
    public static final String U_USER_DEFINED_KEY = "UUserDefined";
    public static final String UINF_KEY = "Uinf";
    public static final String UREF_KEY = "Uref";
    public static final String UNIFORM_VALUE_KEY = "uniformValue";
    public static final String UNIFORM_KEY = "uniform";
    public static final String USE_WALL_DISTANCE_KEY = "useWallDistance";
    public static final String USER_DEFINED_KEY = "userDefined";
    public static final String UWALL_KEY = "Uwall";
    public static final String VELOCITY_KEY = "velocity";
    public static final String VOLUMETRIC_FLOW_RATE_KEY = "volumetricFlowRate";
    public static final String VALUE_KEY = "value";
    public static final String WALL_DISTANCE_KEY = "wallDistance";
    public static final String WARN_KEY = "warn";
    public static final String WATER_VAPOUR_MASS_FRACTION_LABEL = "Water Vapour Mass Fraction";
    public static final String WEIGHT_KEY = "weight";
    public static final String WIND_DIRECTION_KEY = "windDirection";
    public static final String X_KEY = "x";
    public static final String X_OFFSET_KEY = "xoffset";
    public static final String X_SCALE_KEY = "xscale";
    public static final String Y_OFFSET_KEY = "yoffset";
    public static final String Y_KEY = "y";
    public static final String Y_SCALE_KEY = "yscale";
    public static final String Z_KEY = "z";
    public static final String ZERO_KEY = "zero";
    public static final String Z0_KEY = "z0";
    public static final String ZG_KEY = "zG";

    /*
     * LIST OF KEYS
     */
    public static final String[] LIMIT_KEYS = { NONE_KEY, GRADIENT_KEY, ZERO_GRADIENT_KEY, ALPHA_KEY };
    public static final String[] INTERP_ALGO_TYPE_KEYS = { REPEAT_KEY, CLAMP_KEY, WARN_KEY, ERROR_KEY };

    /*
     * LABELS
     */
    
    public static final String ACCOMMODATION_COEFFICIENT_LABEL = "Accommodation Coefficient";
    public static final String ADIABATIC_LABEL = "Adiabatic";
    public static final String ADVECTIVE_TEMPERATURE_LABEL = "Advective";
    public static final String AGE_LABEL = "Age";
    public static final String AIJ_LABEL = "AIJ";
    public static final String AMBIENT_TEMPERATURE_LABEL = "<html>Ambient Temperature<br>Outside The Wall [K]</html>";
    public static final String ATMOSPHERIC_BOUNDARY_LAYER_LABEL = "Atmospheric Boundary Layer";
    public static final String AVERAGE_DISTANCE_LABEL = "Average Distance";
    public static final String AXIAL_VELOCITY_LABEL = "Axial Velocity";
    public static final String BOUNDARY_LAYER_HEIGHT_LABEL = "Boundary Layer Height " + M;
    public static final String BY_INTENSITY_FROM_WIND_LABEL = "By Intensity from Wind";
    public static final String BY_TURB_INTENSITY_AND_MIXING_LENGTH_LABEL = "By Turb. Intensity And Mixing Length";
    public static final String AXIS_LABEL = "Axis";
    public static final String CENTRE_LABEL = "Centre";
    public static final String CYLINDRICAL_INLET_VELOCITY_LABEL = "Cylindrical Inlet Velocity";
    public static final String CLAMP_LABEL = "Clamp";
    public static final String COFG_LABEL = "CofG";
    public static final String COMPRESSIBILITY_LABEL = "Compressibility";
    public static final String COMPRESSIBLE_LABEL = "Compressible";
    public static final String CONDUCTIVITY_LABEL = "Conductivity [W/K" + DOT + "m]";
    public static final String CONTACT_RADIUS_LABEL = "Contact Radius [m]";
    public static final String CONVECTIVE_HEAT_LABEL = "<html>Convective Heat Transfer<br>Coefficient [W/K" + DOT + "m" + SQUARE + "]</html>";
    public static final String CONVECTIVE_HEAT_TRANSFER_LABEL = "Convective Heat Transfer";
    public static final String COORDINATE_SYSTEM_LABEL = "Coordinate System";
    public static final String COUPLED_LABEL = "Coupled";
    public static final String CURVATURE_LABEL = "Curvature";
    public static final String DEMOGRAPHICS_LABEL = "Demographics";
    public static final String DIRECTION_LABEL = "Direction";
    public static final String DISTANCE_ALONG_VECTOR_LABEL = "Distance Along Vector";
    public static final String DISTANCE_TYPE_LABEL = "Distance Type";
    public static final String DISTRIBUTION_BASED_ON_WIND_PROFILE_LABEL = "Distribution Based On Wind Profile";
    public static final String DOWNSTREAM_PRESSURE_COMP = "Downstream Pressure " + PASCAL;
    public static final String DOWNSTREAM_PRESSURE_INCOMP = "Downstream Pressure " + M2_S2;
    public static final String DROPLETS_CONTACT_ANGLE_LABEL = "Droplets Contact Angle";
    public static final String DROPLETS_LABEL = "Droplets";
    public static final String EDIT_I_LABEL = "Turbulent Intensity";
	public static final String EDIT_L_LABEL = "Turbulent Length Scale";
    public static final String EDIT_VELOCITY_LABEL = "Velocity";
    public static final String EPSILON_LABEL = "Epsilon " + EPSILON_SYMBOL;
    public static final String ERROR_LABEL = "Error";
    public static final String FEMALE_ADULT_LABEL = "Female Adult";
    public static final String FEMALE_CHILD_LABEL = "Female Child";
    public static final String FIXED_FLUX_PRESSURE_LABEL = "Fixed Flux Pressure";
    public static final String FIXED_FLOW_RATE_LABEL = "Fixed Flow Rate";
    public static final String FIXED_PRESSURE_COUPLED_LABEL = "Fixed Pressure (Coupled)";
    public static final String FIXED_PRESSURE_LABEL = "Fixed Pressure";
    public static final String FIXED_PRESSURE_COMP_LABEL = FIXED_PRESSURE_LABEL + " " + PASCAL;
    public static final String FIXED_PRESSURE_INCOMP_LABEL = FIXED_PRESSURE_LABEL + " " + M2_S2;
    public static final String FIXED_TANGENTIAL_VELOCITY_LABEL = "Fixed Tangential Velocity";
    public static final String FIXED_TEMPERATURE_LABEL = "Fixed Temperature";
    public static final String FIXED_VALUE_COUPLED_LABEL = "Fixed Value (Coupled)";
    public static final String FIXED_VALUE_LABEL = "Fixed Value";
    public static final String FIXED_VALUES_LABEL = "Fixed Values";
    public static final String FIXED_VELOCITY_LABEL = "Fixed Velocity";
    public static final String FIXED_VELOCITY_AND_PRESSURE_LABEL = "Fixed Velocity and Pressure";
    public static final String FIXED_WALL_LABEL = "Fixed Wall";
    public static final String FLOW_DIRECTION_LABEL = "Flow Direction";
    public static final String FLOW_RATE_LABEL = "Flow Rate";
    public static final String FLOW_RATE_OUTLET_LABEL = "Flow Rate Outlet";
    public static final String FLOW_VELOCITY_LABEL = "Flow Velocity";
    public static final String FLUX_CORRECTED_LABEL = "Flux Corrected";
    public static final String FREESTREAM_LABEL = "Freestream";
    public static final String FREESTREAM_PRESSURE_LABEL = "Freestream Pressure " + PASCAL;
    public static final String FREESTREAM_VELOCITY_LABEL = "Freestream Velocity " + M_S;
    public static final String FROM_FILE_LABEL = "From File";
    public static final String GAMMA_LABEL = "Gamma";
    public static final String GLOBAL_CARTESIAN_LABEL = "Global Cartesian";
    public static final String GROUP_OF_HUMANS_LABEL = "Group of Humans";
    public static final String HEAT_FLUX_LABEL = "Heat Flux";
    public static final String HEAT_FLUX_LABEL_WM = "Heat Flux [W/m" + SQUARE + "]";
    public static final String HEIGHT_LABEL = "Height";
    public static final String HUB_SPEED_LABEL = "Hub Speed " + M_S;
    public static final String I_LABEL = "I";
    public static final String INCOMPRESSIBLE_LABEL = "Incompressible";
    public static final String INFLOW_FIXED_TEMPERATURE_LABEL = "Inflow Fixed Temperature";
    public static final String INFLOW_FIXED_VALUES_LABEL = "Inflow Fixed Values";
    public static final String INFLOW_PHASE_FRACTION_LABEL = "Inflow Phase Fraction";
    public static final String INFLOW_TOTAL_PRESSURE_LABEL = "Inflow Total Pressure";
    public static final String INFLOW_VELOCITY_LABEL = "Inflow Velocity " + M_S;
    public static final String INITIAL_PHASE_FRACTION_LABEL = "Initial Phase Fraction";
    public static final String INITIAL_PRESSURE_PA_LABEL = "Initial Pressure "  + PASCAL;
    public static final String INITIAL_TEMPERATURE_K_LABEL = "Initial Temperature " + KELVIN;
    public static final String INLET_DIRECTION_LABEL = "Inlet Direction";
    public static final String INLET_OUTLET_LABEL = "Inlet Outlet";
    public static final String INLET_OUTLET_TOTAL_TEMPERATURE_LABEL = "Inlet Outlet Total Temperature";
    public static final String INLET_OUTLET_VELOCITY_LABEL = "Inlet Outlet Velocity";
    public static final String INLET_VALUE_LABEL = "Inlet Value";
    public static final String INTERPOLATION_ALGORITHM_LABEL = "Interpolation Algorithm";
    public static final String INTERPOLATION_PROFILE_LABEL = "Interpolation Profile";
    public static final String INITIAL_VELOCITY_LABEL = "Initial Velocity " + M_S;
    public static final String K_LABEL = "K " + K_SYMBOL;// "Turbulent Kinetik Energy";
    public static final String KEQUATION_LABEL = K_LABEL;
    public static final String L_LABEL = "L";
    public static final String LENGTH_INF_M_LABEL = "Length Inf " + M;
    public static final String LOCAL_CYLINDRICAL_LABEL = "Local Cylindrical";
    public static final String MAGNITUDE_LABEL = "Magnitude " + M_S;
    public static final String MALE_ADULT_LABEL = "Male Adult";
    public static final String MALE_CHILD_LABEL = "Male Child";
    public static final String MASS_FLOW_RATE_INLET_LABEL = "Mass Flow Rate Inlet";
    public static final String MASS_FLOW_RATE_LABEL = "Mass Flow Rate [kg/s]";
    public static final String MAXWELL_SLIP_LABEL = "Maxwell Slip";
    public static final String MEAN_PRESSURE_LABEL = "Mean Pressure";
    public static final String MEAN_PRESSURE_INCOMP = MEAN_PRESSURE_LABEL + " " + M2_S2;
    public static final String MEAN_PRESSURE_COMP = MEAN_PRESSURE_LABEL + " " + PASCAL;
    public static final String MIXING_LENGTH_LABEL = "Mixing Length " + M;
    public static final String MOVING_WALL_LABEL = "Moving Wall";
    public static final String MOVING_WALL_VELOCITY_LABEL = "Moving Wall Velocity";
    public static final String MOVING_WALL_COUPLED_VELOCITY_LABEL = "Moving Wall Velocity (Coupled)";
    public static final String NAME_LABEL = "Name";
    public static final String NO_PHASES_LABEL = "No Phases";
    public static final String NO_SLIP_COUPLED_LABEL = "No-slip (Coupled)";
    public static final String NO_SLIP_LABEL = "No-slip";
    public static final String NON_UNIFORM_PHASE_FRACTION_LABEL = "Non-uniform Phase Fraction";
    public static final String NON_UNIFORM_TEMPERATURE_LABEL = "Non-uniform Temperature";
    public static final String NON_UNIFORM_TURBULENCE_LABEL = "Non-uniform Turbulence";
    public static final String NON_UNIFORM_VELOCITY_LABEL = "Non-uniform Velocity";
    public static final String NORMAL_GRADIENT_COEFFICIENT = "Normal Gradient Coefficient";
    public static final String NORMAL_TO_BOUNDARY_LABEL = "Normal to Boundary";
    public static final String NORMAL_TO_BOUNDARY_PATCH_LABEL = "Normal To Boundary Patch";
    public static final String NUTILDA_LABEL = "NuTilda " + M2_S;
    public static final String NUMBER_LABEL = "Number";
    public static final String OMEGA_TURBULENCE_LABEL = "Omega " + OMEGA_SYMBOL_S;
    public static final String OMEGA_VELOCITY_LABEL = "Omega " + OMEGA_SYMBOL_RAD;
    public static final String ORIGIN_LABEL = "Origin";
    public static final String OUTSIDE_ENVIRONMENTAL_TEMPERATURE_K_LABEL = "Outside Environmental Temperature [K]";
    public static final String PHASE_CHANGE_LABEL = "Phase Change";
    public static final String PHASE_LABEL = "Phase";
    public static final String PHASE_CHANGE_EFFECT_LABEL = "Phase Change Effect";
    public static final String POINT_DISTANCE_LABEL = "Point Distance";
    public static final String POINT_LABEL = "Point";
    public static final String POWER_LAW_COEFFICIENT_LABEL = "Power Law Coefficient";
    public static final String PRESSURE_COMP_LABEL = "Pressure " + PASCAL;
    public static final String PRESSURE_DIRECTED_INLET_OUTLET_VELOCITY_LABEL = "Pressure Directed Inlet Outlet Velocity";
    public static final String PRESSURE_DIRECTED_INLET_VELOCITY_LABEL = "Pressure Directed Inlet Velocity";
    public static final String PRESSURE_INCOMP_LABEL = "Pressure " + M2_S2;
    public static final String PRESSURE_INF_LABEL = "Pressure Inf " + PASCAL;
    public static final String PRESSURE_INLET_OUTLET_VELOCITY_LABEL = "Pressure Inlet Outlet Velocity";
    public static final String PRESSURE_INLET_VELOCITY_LABEL = "Pressure Inlet Velocity";
    public static final String PRESSURE_LABEL = "Pressure";
    public static final String PRESSURE_TYPE_LABEL = "Pressure Type";
    public static final String PROFILE_TYPE_LABEL = "Profile Type";
    public static final String RADIAL_VELOCITY_LABEL = "Radial Velocity";
    public static final String RATIO_OF_SPECIFIC_HEATS_LABEL = "Ratio of Specific Heats";
    public static final String REFERENCE_HEIGHT_LABEL = "Reference Height " + M;
    public static final String REFERENCE_VELOCITY_LABEL = "Reference Velocity " + M_S;
    public static final String REPEAT_LABEL = "Repeat";
    public static final String RESISTIVE_LABEL = "Resistive";
    public static final String REYNOLDS_STRESS_LABEL = "Mean Reynolds Stress";
    public static final String RHO_INLET_LABEL = "Initial Density "+DENSITY;
    public static final String RICHARDS_HOXEY_LABEL = "Richards-Hoxey";
    public static final String ROTATING_WALL_LABEL = "Rotating Wall";
    public static final String ROTATING_WALL_COUPLED_LABEL = "Rotating Wall (Coupled)";
    public static final String ROTATING_WHEEL_LABEL = "Rotating Wheel";
    public static final String ROUGHNESS_HEIGHT_LABEL = "Roughness Height " + M;
    public static final String RPM_LABEL = "RPM";
    public static final String SLIP_COUPLED_LABEL = "Slip (Coupled)";
    public static final String SLIP_LABEL = "Slip";
    public static final String SMOLUCHOWSKI_JUMP_LABEL = "Smoluchowski Jump";
    public static final String SOLAR_RADIATION_LABEL = "Solar Radiation";
    public static final String SPECIFICATION_METHOD_LABEL = "Specification Method";
    public static final String SPECIFY_DIRECTION_LABEL = "Specify Direction";
    public static final String STEADY_EVAPORATION_LABEL = "Steady Evaporation";
    public static final String SUPERSONIC_FREESTREAM_LABEL = "Supersonic Freestream";
    public static final String SURFACE_EMISSIVITY_LABEL = "Surface Emissivity";
    public static final String SURFACE_LIQUID_MASS_LABEL = "Surface Liquid Mass [Kg/m" + Symbols.SQUARE + "]";
    public static final String SURFACE_NORMAL_FIXED_VALUE_LABEL = "Surface Normal Fixed Value";
    public static final String SURFACE_TRANSMISSIVITY_LABEL = "Surface Transmissivity";
    public static final String TABLE_DATA_LABEL = "Table Data";
    public static final String TABLE_FILE_COEFFS_KEY = "tableFileCoeffs";
    public static final String TANGENTIAL_GRADIENT_COEFFICIENT = "Tangential Gradient Coefficient";
    public static final String TEMPERATURE_INF_K_LABEL = "Temperature Inf " + KELVIN;
    public static final String TEMPERATURE_INFLOW_VALUE_LABEL = "Inflow Value " + KELVIN;
    public static final String TEMPERATURE_INITIAL_VALUE_LABEL = "Initial Value " + KELVIN;
    public static final String TEMPERATURE_VALUE_LABEL = "Temperature Value " + KELVIN;
    public static final String TEMPERATURE_VALUE_K_LABEL = "Temperature Value " + KELVIN;
    public static final String THERMAL_CONDUCTION_LABEL = "Thermal Conduction";
    public static final String THERMAL_CREEP_LABEL = "Thermal Creep";
    public static final String THERMAL_RADIATION_LABEL = "Thermal Radiation";
    public static final String THICKNESS_M_LABEL = "Thickness " + M;
    public static final String TIME_VARYING_FLOW_RATE_LABEL = "Time-varying Flow Rate";
    public static final String TIME_VARYING_LABEL = "Time-varying";
    public static final String TIME_VARYING_PHASE_FRACTION_LABEL = "Time-varying Phase Fraction";
    public static final String TIME_VARYING_VELOCITY_LABEL = "Time-varying Velocity";
    public static final String TIME_VARYING_TEMPERATURE_LABEL = "Time-varying Temperature";
    public static final String TIME_VARYING_TURBULENCE_LABEL = "Time-varying Turbulence";
    public static final String TOTAL_HEAT_LOAD_LABEL = "Total Heat Load";
    public static final String TOTAL_HEAT_LOAD_AT_WALL_W_LABEL = "Total Heat Load At Wall [W]";
    public static final String TOTAL_PRESSURE_LABEL = "Total Pressure";
    public static final String TOTAL_PRESSURE_COMP_LABEL = TOTAL_PRESSURE_LABEL + " " + PASCAL;
    public static final String TOTAL_PRESSURE_INCOMP_LABEL = TOTAL_PRESSURE_LABEL + " " + M2_S2;
    public static final String TOTAL_TEMPERATURE_LABEL = "Total Temperature";
    public static final String TRANSLATING_WALL_COUPLED_LABEL = "Tangential Wall (Coupled)";
    public static final String TURBULENCE_INTENSITY_LABEL = "Turbulence Intensity";
    public static final String TYPE_LABEL = "Type";
    public static final String U_LABEL = "U";
    public static final String USE_WALL_DISTANCE_LABEL = "Use Wall Distance";
    public static final String UINF_LABEL = "UInf " + M_S;
    public static final String UNIFORM_VELOCITY_LABEL = "Uniform Velocity";
    public static final String USER_DEFINED_HEAT_LABEL = "<html>User Defined Heat Flux<br>[W/m" + SQUARE + "]</html>";
    public static final String USER_DEFINED_LABEL = "User Defined";
    public static final String VALUE_LABEL = "Value";
    public static final String VARIABLE_HEIGHT_FLOW_RATE_INLET_LABEL = "Variable Height Flow Rate Inlet";
    public static final String VARIABLE_HEIGHT_FLOW_RATE_LABEL = "Variable Height Flow Rate";
    public static final String VELOCITY_COMPONENTS_LABEL = "Velocity Components";
    public static final String VELOCITY_LABEL = "Velocity";
    public static final String VELOCITY_LABEL_MS = "Velocity " + M_S;
    public static final String VELOCITY_MAGNITUDE_LABEL = "Velocity Magnitude " + M_S;
    public static final String VELOCITY_TYPE_LABEL = "Velocity Type";
    public static final String VOLUMETRIC_FLOW_RATE_LABEL = "Volumetric Flow Rate [m" + CUBE + "/s]";
    public static final String VOLUMETRIC_FLOW_RATE_INLET_LABEL = "Volumetric Flow Rate Inlet";
    public static final String WALL_HEAT_LABEL = "Wall Heat Flux [W/m" + SQUARE + "]";
    public static final String WALL_DISTANCE_LABEL = "Wall Distance";
    public static final String WALL_TEMPERATURE_K_LABEL = "Wall Temperature " + KELVIN;
    public static final String WALL_TYPE_LABEL = "Wall Type";
    public static final String WARN_LABEL = "Warn";
    public static final String WAVE_TRANSMISSIVE_LABEL = "Wave Transmissive";
    public static final String WEIGHT_LABEL = "Weight";
    public static final String WIND_DIRECTION_DEG_LABEL = "Wind Direction [deg]";
    public static final String WIND_UPWARD_DIRECTION_LABEL = "Wind Upward Direction";
    public static final String X_LABEL = "X";
    public static final String X_OFFSET_LABEL = "X Offset";
    public static final String X_SCALE_LABEL = "X Scale";
    public static final String Y_LABEL = "Y";
    public static final String Y_OFFSET_LABEL = "Y Offset";
    public static final String Y_SCALE_LABEL = "Y Scale";
    public static final String Z_LABEL = "Z";
    public static final String ZERO_GRADIENT_LABEL = "Zero Gradient";
    
    /*
     * LIST OF LABELS
     */
    public static final String[] INTERP_ALGO_TYPE_LABELS = { REPEAT_LABEL, CLAMP_LABEL, WARN_LABEL, ERROR_LABEL };

    /*
     * Names
     */
    public static final String INTERPOLATION_TABLE_NAME = "interpolation.table";
    public static final String MEAN_PRESSURE_NAME = "pressure.mean";
    public static final String PRESSURE_FIXED_NAME = "pressure.fixed";
    public static final String RESISTIVE_PRESSURE_NAME = "resistive.pressure";
    public static final String TOTAL_PRESSURE_NAME = "total.pressure";

    public static void buildSimpleFixedVelocityPanel(DictionaryPanelBuilder builder, DictionaryModel model) {
        builder.addComponent(VELOCITY_LABEL_MS, model.bindUniformPoint(VALUE_KEY));
    }

    public static void buildFreestreamVelocityPanel(DictionaryPanelBuilder builder, DictionaryModel model) {
        builder.addComponent(VELOCITY_LABEL_MS, model.bindUniformPoint(VALUE_KEY, FREESTREAM_VALUE_KEY));
    }

    public static void buildFixedCylindricalVelocityPanel(DictionaryPanelBuilder builder, DictionaryModel model) {
        builder.addComponent(AXIS_LABEL, model.bindPoint(AXIS_KEY));
        builder.addComponent(CENTRE_LABEL, model.bindPoint(CENTRE_KEY));
        builder.addComponent(AXIAL_VELOCITY_LABEL, model.bindDouble(AXIAL_VELOCITY_KEY));
        builder.addComponent(RPM_LABEL, model.bindDouble(RPM_KEY));
        builder.addComponent(RADIAL_VELOCITY_LABEL, model.bindDouble(RADIAL_VELOCITY_KEY));
    }

    public static void buildTimeVaryingScalarPanel(DictionaryPanelBuilder builder, DictionaryModel model, String dictionaryKey, String name) {
        buildTimeVaryingInterpolationTablePanel(builder, model, dictionaryKey, new String[] { name });
    }

    public static void buildTimeVaryingVectorPanel(DictionaryPanelBuilder builder, DictionaryModel model, String dictionaryKey, String X, String Y, String Z) {
        buildTimeVaryingInterpolationTablePanel(builder, model, dictionaryKey, new String[] { X, Y, Z });
    }

    /*
     * Utils
     */

    private static void buildTimeVaryingInterpolationTablePanel(DictionaryPanelBuilder builder, final DictionaryModel model, final String dictionaryKey, final String[] names) {
        builder.startChoice(INTERPOLATION_PROFILE_LABEL, new TimeVaryingComboBoxController(model, dictionaryKey));

        builder.startGroup(DATA_KEY, TABLE_DATA_LABEL);
        final boolean isVector = names.length == 3;
        SparklineChart sparkline = new DictionarySparkline(model, dictionaryKey, isVector);
        JButton editButton = new JButton(new AbstractAction(EDIT_BUTTON_LABEL) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isVector) {
                    VectorTimeVaryingInterpolationTable table = new VectorTimeVaryingInterpolationTable(names);
                    table.load(model.getDictionary(), dictionaryKey);
                    table.showDialog();
                    table.save(model.getDictionary(), dictionaryKey);
                } else {
                    ScalarTimeVaryingInterpolationTable table = new ScalarTimeVaryingInterpolationTable(names);
                    table.load(model.getDictionary(), dictionaryKey);
                    table.showDialog();
                    table.save(model.getDictionary(), dictionaryKey);
                }
                model.refresh();
            }
        });
        editButton.setName(EDIT_BUTTON_LABEL);
        builder.addComponent("", Componentizer.create().minToPref(editButton).minToPref(sparkline).component());
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
        if (type.equals(UNIFORM_FIXED_VALUE_KEY) || type.equals(ROTATING_WALL_VELOCITY_KEY) || type.equals(FLOW_RATE_INLET_VELOCITY_KEY) || type.equals(FLOW_RATE_OUTLET_VELOCITY_KEY) || type.equals(UNIFORM_TOTAL_PRESSURE_KEY)) {
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
        if (type.equals(UNIFORM_FIXED_VALUE_KEY) || type.equals(ROTATING_WALL_VELOCITY_KEY) || type.equals(FLOW_RATE_INLET_VELOCITY_KEY) || type.equals(FLOW_RATE_OUTLET_VELOCITY_KEY) || type.equals(UNIFORM_TOTAL_PRESSURE_KEY)) {
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
        if (dict.found(TABLE_FILE_COEFFS_KEY)) {
            return true;
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
            String value = U.lookupString(MASS_FLOW_RATE_KEY);
            if (value.equals(TABLE_FILE_KEY) || value.startsWith(TABLE_KEY)) {
                fixTimeVaryingLoad(U, MASS_FLOW_RATE_KEY);
                builder.selectDictionaryByKey(BoundaryConditionsUtils.getTimeVaryingMassFlowRate(), U);
            } else {
                builder.selectDictionaryByKey(BoundaryConditionsUtils.getMassFlowRate(), U);
            }
        } else if (U.found(new StartWithFinder(MASS_FLOW_RATE_KEY))) {
            builder.selectDictionaryByKey(BoundaryConditionsUtils.getTimeVaryingMassFlowRate(), U);
        } else if (U.found(VOLUMETRIC_FLOW_RATE_KEY)) {
            String value = U.lookupString(VOLUMETRIC_FLOW_RATE_KEY);
            if (value.equals(TABLE_FILE_KEY) || value.startsWith(TABLE_KEY)) {
                fixTimeVaryingLoad(U, VOLUMETRIC_FLOW_RATE_KEY);
                builder.selectDictionaryByKey(BoundaryConditionsUtils.getTimeVaryingVolumetricFlowRate(), U);
            } else {
                builder.selectDictionaryByKey(BoundaryConditionsUtils.getVolumetricFlowRate(), U);
            }
        } else if (U.found(new StartWithFinder(VOLUMETRIC_FLOW_RATE_KEY))) {
            builder.selectDictionaryByKey(BoundaryConditionsUtils.getTimeVaryingVolumetricFlowRate(), U);

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
