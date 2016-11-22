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

import static eu.engys.core.project.zero.fields.Fields.T;
import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ADVECTIVE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ALPHA_EFF_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.COMPRESSIBLE_TURBULENT_HEAT_FLUX_TEMPERATURE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.CP_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FIXED_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.GAMMA_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.HEAT_SOURCE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INCOMPRESSIBLE_TURBULENT_HEAT_FLUX_TEMPERATURE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INLET_OUTLET_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INLET_OUTLET_TOTAL_TEMPERATURE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INLET_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.KAPPA_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.KAPPA_NAME_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PHI_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PSI_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.QR_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.Q_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.T0_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TOTAL_TEMPERATURE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ZERO_GRADIENT_KEY;

import eu.engys.core.dictionary.Dictionary;

public class TemperatureFactory {

	public static final Dictionary fixedValue = new Dictionary(T) {
		{
			add(TYPE, FIXED_VALUE_KEY);
			add(VALUE, "uniform 300");
		}
	};

	public static final Dictionary zeroGradient = new Dictionary(T) {
		{
			add(TYPE, ZERO_GRADIENT_KEY);
		}
	};

	public static final Dictionary advectiveTemperature = new Dictionary(T) {
		{
			add(TYPE, ADVECTIVE_KEY);
		}
	};

	public static final Dictionary totalTemperature = new Dictionary(T) {
		{
			add(TYPE, TOTAL_TEMPERATURE_KEY);
			add(VALUE, "uniform 300");
			add(U, U);
			add(PHI_KEY, PHI_KEY);
			add(PSI_KEY, "0");
			add(GAMMA_KEY, "1.4");
			add(T0_KEY, "uniform 300");
		}

	};

	public static final Dictionary inletOutletTotalTemperature = new Dictionary(T) {
		{
			add(TYPE, INLET_OUTLET_TOTAL_TEMPERATURE_KEY);
			add(VALUE, "uniform 300");
			add(U, "U");
			add(PHI_KEY, PHI_KEY);
			add(PSI_KEY, "0");
			add(GAMMA_KEY, "1.4");
			add(T0_KEY, "uniform 300");
		}

	};

	public static final Dictionary inletOutlet = new Dictionary(T) {
		{
			add(TYPE, INLET_OUTLET_KEY);
			add(VALUE, "uniform 300");
			add(INLET_VALUE_KEY, "uniform 300");
		}
	};

	public static final Dictionary turbulentHeatFluxTemperature_FLUX = new Dictionary(T) {
		{
			add(TYPE, INCOMPRESSIBLE_TURBULENT_HEAT_FLUX_TEMPERATURE_KEY);
			add(VALUE, "uniform 300");
			add(Q_KEY, "uniform 10.0");
			add(HEAT_SOURCE_KEY, "flux");
			add(ALPHA_EFF_KEY, "kappaEff");
			add(CP_KEY, "Cp0");
		}
	};

	public static final Dictionary turbulentHeatFluxTemperature_FLUX_COMP = new Dictionary(T) {
		{
			add(TYPE, COMPRESSIBLE_TURBULENT_HEAT_FLUX_TEMPERATURE_KEY);
			add(VALUE, "uniform 300");
			add(Q_KEY, "uniform 10.0");
			add(HEAT_SOURCE_KEY, "flux");
			add(KAPPA_KEY, "fluidThermo");
			add(KAPPA_NAME_KEY, "default");
		}
	};

//	public static final Dictionary turbulentHeatFluxTemperatureOCFD_FLUX = new Dictionary(T) {
//		{
//			add(TYPE, TURBULENT_HEAT_FLUX_TEMPERATURE_KEY);
//			add(VALUE, "uniform 300");
//			add(Q_KEY, "uniform 10.0");
//			add(HEAT_SOURCE_KEY, "flux");
//			add(ALPHA_EFF_KEY, ALPHA_EFF_KEY);
//		}
//	};

	public static final Dictionary turbulentHeatFluxTemperatureOCFD_FLUX_COMP = new Dictionary(T) {
		{
			add(TYPE, COMPRESSIBLE_TURBULENT_HEAT_FLUX_TEMPERATURE_KEY);
			add(VALUE, "uniform 300");
			add(Q_KEY, "uniform 10.0");
			add(HEAT_SOURCE_KEY, "flux");
			add(KAPPA_KEY, "fluidThermo");
			add(KAPPA_NAME_KEY, "default");
			add(QR_KEY, "none");
		}
	};

	public static final Dictionary turbulentHeatFluxTemperature_POWER = new Dictionary(T) {
		{
			add(TYPE, INCOMPRESSIBLE_TURBULENT_HEAT_FLUX_TEMPERATURE_KEY);
			add(VALUE, "uniform 300");
			add(Q_KEY, "uniform 10.0");
			add(HEAT_SOURCE_KEY, "power");
			add(ALPHA_EFF_KEY, "kappaEff");
			add(CP_KEY, "Cp0");
		}
	};

//	public static final Dictionary turbulentHeatFluxTemperatureOCFD_POWER = new Dictionary(T) {
//		{
//			add(TYPE, TURBULENT_HEAT_FLUX_TEMPERATURE_KEY);
//			add(VALUE, "uniform 300");
//			add(Q_KEY, "uniform 10.0");
//			add(HEAT_SOURCE_KEY, "power");
//			add(ALPHA_EFF_KEY, ALPHA_EFF_KEY);
//		}
//	};

	public static final Dictionary turbulentHeatFluxTemperature_POWER_COMP = new Dictionary(T) {
		{
			add(TYPE, COMPRESSIBLE_TURBULENT_HEAT_FLUX_TEMPERATURE_KEY);
			add(VALUE, "uniform 300");
			add(Q_KEY, "uniform 10.0");
			add(HEAT_SOURCE_KEY, "power");
			add(KAPPA_KEY, "fluidThermo");
			add(KAPPA_NAME_KEY, "none");
		}
	};

	public static final Dictionary turbulentHeatFluxTemperatureOCFD_POWER_COMP = new Dictionary(T) {
		{
			add(TYPE, COMPRESSIBLE_TURBULENT_HEAT_FLUX_TEMPERATURE_KEY);
			add(VALUE, "uniform 300");
			add(Q_KEY, "uniform 10.0");
			add(HEAT_SOURCE_KEY, "power");
			add(KAPPA_KEY, "fluidThermo");
			add(KAPPA_NAME_KEY, "default");
			add(QR_KEY, "none");
		}
	};
}
