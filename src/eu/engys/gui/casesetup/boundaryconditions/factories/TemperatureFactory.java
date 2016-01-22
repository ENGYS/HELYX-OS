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

import eu.engys.core.dictionary.Dictionary;

public class TemperatureFactory {

	public static final Dictionary fixedValue = new Dictionary("T") {
		{
			add(TYPE, "fixedValue");
			add("value", "uniform 300");
		}
	};

	public static final Dictionary zeroGradient = new Dictionary("T") {
		{
			add(TYPE, "zeroGradient");
		}
	};

	public static final Dictionary advectiveTemperature = new Dictionary("T") {
		{
			add(TYPE, "advective");
		}
	};

	public static final Dictionary totalTemperature = new Dictionary("T") {
		{
			add(TYPE, "totalTemperature");
			add("value", "uniform 300");
			add("U", "U");
			add("phi", "phi");
			add("psi", "0");
			add("gamma", "1.4");
			add("T0", "uniform 300");
		}

	};

	public static final Dictionary inletOutletTotalTemperature = new Dictionary("T") {
		{
			add(TYPE, "inletOutletTotalTemperature");
			add("value", "uniform 300");
			add("U", "U");
			add("phi", "phi");
			add("psi", "0");
			add("gamma", "1.4");
			add("T0", "uniform 300");
		}

	};

	public static final Dictionary inletOutlet = new Dictionary("T") {
		{
			add(TYPE, "inletOutlet");
			add("value", "uniform 300");
			add("inletValue", "uniform 300");
		}
	};

	public static final Dictionary turbulentHeatFluxTemperature_FLUX = new Dictionary("T") {
		{
			add(TYPE, "incompressible::turbulentHeatFluxTemperature");
			add("value", "uniform 300");
			add("q", "uniform 10.0");
			add("heatSource", "flux");
			add("alphaEff", "kappaEff");
			add("Cp", "Cp0");
		}
	};

	public static final Dictionary turbulentHeatFluxTemperature_FLUX_COMP = new Dictionary("T") {
		{
			add(TYPE, "compressible::turbulentHeatFluxTemperature");
			add("value", "uniform 300");
			add("q", "uniform 10.0");
			add("heatSource", "flux");
			add("kappa", "fluidThermo");
			add("kappaName", "default");
		}
	};

	public static final Dictionary turbulentHeatFluxTemperatureOCFD_FLUX = new Dictionary("T") {
		{
			add(TYPE, "turbulentHeatFluxTemperature");
			add("value", "uniform 300");
			add("q", "uniform 10.0");
			add("heatSource", "flux");
			add("alphaEff", "alphaEff");
		}
	};

	public static final Dictionary turbulentHeatFluxTemperatureOCFD_FLUX_COMP = new Dictionary("T") {
		{
			add(TYPE, "compressible::turbulentHeatFluxTemperature");
			add("value", "uniform 300");
			add("q", "uniform 10.0");
			add("heatSource", "flux");
			add("kappa", "fluidThermo");
			add("kappaName", "default");
			add("Qr", "none");
		}
	};

	public static final Dictionary turbulentHeatFluxTemperature_POWER = new Dictionary("T") {
		{
			add(TYPE, "incompressible::turbulentHeatFluxTemperature");
			add("value", "uniform 300");
			add("q", "uniform 10.0");
			add("heatSource", "power");
			add("alphaEff", "kappaEff");
			add("Cp", "Cp0");
		}
	};

	public static final Dictionary turbulentHeatFluxTemperatureOCFD_POWER = new Dictionary("T") {
		{
			add(TYPE, "turbulentHeatFluxTemperature");
			add("value", "uniform 300");
			add("q", "uniform 10.0");
			add("heatSource", "power");
			add("alphaEff", "alphaEff");
		}
	};

	public static final Dictionary turbulentHeatFluxTemperature_POWER_COMP = new Dictionary("T") {
		{
			add(TYPE, "compressible::turbulentHeatFluxTemperature");
			add("value", "uniform 300");
			add("q", "uniform 10.0");
			add("heatSource", "power");
			add("kappa", "fluidThermo");
			add("kappaName", "none");
		}
	};

	public static final Dictionary turbulentHeatFluxTemperatureOCFD_POWER_COMP = new Dictionary("T") {
		{
			add(TYPE, "compressible::turbulentHeatFluxTemperature");
			add("value", "uniform 300");
			add("q", "uniform 10.0");
			add("heatSource", "power");
			add("kappa", "fluidThermo");
			add("kappaName", "default");
			add("Qr", "none");
		}
	};
}
