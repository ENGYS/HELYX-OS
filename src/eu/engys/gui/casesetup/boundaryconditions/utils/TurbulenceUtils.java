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

import static eu.engys.util.Symbols.EPSILON_SYMBOL;
import static eu.engys.util.Symbols.K_SYMBOL;
import static eu.engys.util.Symbols.M2_S;
import static eu.engys.util.Symbols.OMEGA_SYMBOL;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;

public class TurbulenceUtils extends BoundaryConditionsUtils {

    public static final String FIXED_VALUES_LABEL = "Fixed Values";
	public static final String INFLOW_FIXED_VALUES_LABEL = "Inflow Fixed Values";
	public static final String INLET_OUTLET_LABEL = "Inlet Outlet";
	public static final String ZERO_GRADIENT_LABEL = "Zero Gradient";
	public static final String BY_TURB_INTENSITY_AND_MIXING_LENGTH_LABEL = "By Turb. Intensity And Mixing Length";
    
	public static final String K_LABEL = "k " + K_SYMBOL;// "Turbulent Kinetik Energy";
	public static final String NU_TILDA_LABEL = "NuTilda " + M2_S;
	public static final String EPSILON_LABEL = "Epsilon " + EPSILON_SYMBOL;
	public static final String OMEGA_LABEL = "Omega " + OMEGA_SYMBOL;
	public static final String KEQUATION_LABEL = K_LABEL;

	private static final String TIMEVARYING_KEY = "timevarying";
	private static final String NONUNIFROM_KEY = "nonunifrom";
	private static final String FIXED_KEY = "fixed";
	private static final String ZERO_KEY = "zero";
	private static final String INOUT_KEY = "inout";
	private static final String MIXING_KEY = "mixing";

	public static final String TURBULENCE_INTENSITY = "Turbulence Intensity";

	public static void buildFixedKnownValuesPanel(DictionaryPanelBuilder builder, DictionaryModel dictK, DictionaryModel dictOmega, DictionaryModel dictEpsilon, DictionaryModel dictNutilda) {
		builder.startGroup(FIXED_VALUES_LABEL);
		builder.startHidable(FIXED_KEY);

		dictOmega.setCompanion(dictK);
		builder.startDictionary(OMEGA_LABEL, dictOmega);
		builder.addComponent(K_LABEL, dictK.bindUniformDouble("value"));
		builder.addComponent(OMEGA_LABEL, dictOmega.bindUniformDouble("value"));
		builder.endDictionary();

		dictEpsilon.setCompanion(dictK);
		builder.startDictionary(EPSILON_LABEL, dictEpsilon);
		builder.addComponent(K_LABEL, dictK.bindUniformDouble("value"));
		builder.addComponent(EPSILON_LABEL, dictEpsilon.bindUniformDouble("value"));
		builder.endDictionary();

		builder.startDictionary(NU_TILDA_LABEL, dictNutilda);
		builder.addComponent(NU_TILDA_LABEL, dictNutilda.bindUniformDouble("value"));
		builder.endDictionary();

		builder.startDictionary(KEQUATION_LABEL, dictK);
		builder.addComponent(K_LABEL, dictK.bindUniformDouble("value"));
		builder.endDictionary();

		builder.endHidable();
		builder.endGroup();
	}

	public static void buildInletOutletPanel(DictionaryPanelBuilder builder, DictionaryModel dictK, DictionaryModel dictOmega, DictionaryModel dictEpsilon, DictionaryModel dictNutilda) {
		builder.startGroup(INFLOW_FIXED_VALUES_LABEL);
		builder.startHidable(INOUT_KEY);

		dictOmega.setCompanion(dictK);
		builder.startDictionary(OMEGA_LABEL, dictOmega);
		builder.addComponent(K_LABEL, dictK.bindUniformDouble("inletValue"));
		builder.addComponent(OMEGA_LABEL, dictOmega.bindUniformDouble("inletValue"));
		builder.endDictionary();

		dictEpsilon.setCompanion(dictK);
		builder.startDictionary(EPSILON_LABEL, dictEpsilon);
		builder.addComponent(K_LABEL, dictK.bindUniformDouble("inletValue"));
		builder.addComponent(EPSILON_LABEL, dictEpsilon.bindUniformDouble("inletValue"));
		builder.endDictionary();

		builder.startDictionary(NU_TILDA_LABEL, dictNutilda);
		builder.addComponent(NU_TILDA_LABEL, dictNutilda.bindUniformDouble("inletValue"));
		builder.endDictionary();

		builder.startDictionary(KEQUATION_LABEL, dictK);
		builder.addComponent(K_LABEL, dictK.bindUniformDouble("inletValue"));
		builder.endDictionary();
		
		builder.endHidable();
		builder.endGroup();
	}

	public static void buildZeroGradientPanel(DictionaryPanelBuilder builder, DictionaryModel dictK, DictionaryModel dictOmega, DictionaryModel dictEpsilon, DictionaryModel dictNutilda) {
		builder.startGroup(ZERO_GRADIENT_LABEL);
		builder.startHidable(ZERO_KEY);

		dictOmega.setCompanion(dictK);
		builder.startDictionary(OMEGA_LABEL, dictOmega);
		builder.endDictionary();

		dictEpsilon.setCompanion(dictK);
		builder.startDictionary(EPSILON_LABEL, dictEpsilon);
		builder.endDictionary();

		builder.startDictionary(NU_TILDA_LABEL, dictNutilda);
		builder.endDictionary();

		builder.startDictionary(KEQUATION_LABEL, dictK);
		builder.endDictionary();
		
		builder.endHidable();
		builder.endGroup();
	}

	public static void buildTurbulentIntensityAndMixingLengthPanel(DictionaryPanelBuilder builder, DictionaryModel dictK, DictionaryModel dictOmega, DictionaryModel dictEpsilon, DictionaryModel dictNuTilda) {
		builder.startGroup(BY_TURB_INTENSITY_AND_MIXING_LENGTH_LABEL);
		builder.startHidable(MIXING_KEY);

		dictOmega.setCompanion(dictK);
		builder.startDictionary(OMEGA_LABEL, dictOmega);
//		builder.addComponent(new JLabel(OMEGA_LABEL), new JLabel(""));
		builder.addComponent(TURBULENCE_INTENSITY, dictK.bindDouble("intensity"));
		builder.addComponent("Mixing Length [m]", dictOmega.bindDouble("mixingLength"));
		builder.endDictionary();

		dictEpsilon.setCompanion(dictK);
		builder.startDictionary(EPSILON_LABEL, dictEpsilon);
//		builder.addComponent(new JLabel(EPSILON_LABEL), new JLabel(""));
		builder.addComponent(TURBULENCE_INTENSITY, dictK.bindDouble("intensity"));
		builder.addComponent("Mixing Length [m]", dictEpsilon.bindDouble("mixingLength"));
		builder.endDictionary();

		if (dictNuTilda != null) {
			builder.startDictionary(NU_TILDA_LABEL, dictNuTilda);
//			builder.addComponent(new JLabel(NU_TILDA_LABEL), new JLabel(""));
			builder.addComponent(TURBULENCE_INTENSITY, dictNuTilda.bindDouble("intensity"));
			builder.addComponent("Mixing Length [m]", dictNuTilda.bindDouble("length"));
			builder.endDictionary();
		}

		builder.startDictionary(KEQUATION_LABEL, dictK);
		builder.endDictionary();
		builder.endHidable();
		builder.endGroup();
	}

	public static void setSpalartAllmaras(DictionaryPanelBuilder builder) {
		// Dictionary selectedDict = builder.getSelectedModel().getDictionary();
		builder.setShowing(FIXED_KEY, NU_TILDA_LABEL);
		// builder.setShowing(MIXING_KEY, NU_TILDA_LABEL);
		builder.setShowing(NONUNIFROM_KEY, NU_TILDA_LABEL);
		builder.setShowing(TIMEVARYING_KEY, NU_TILDA_LABEL);
		builder.setShowing(INOUT_KEY, NU_TILDA_LABEL);
		builder.setShowing(ZERO_KEY, NU_TILDA_LABEL);

		// builder.selectDictionary(selectedDict);
	}

	public static void setKEquationEddy(DictionaryPanelBuilder builder) {
		// Dictionary selectedDict = builder.getSelectedModel().getDictionary();
		builder.setShowing(FIXED_KEY, KEQUATION_LABEL);
		// builder.setShowing(MIXING_KEY, KEQUATION_LABEL);
		builder.setShowing(NONUNIFROM_KEY, KEQUATION_LABEL);
		builder.setShowing(TIMEVARYING_KEY, KEQUATION_LABEL);
		builder.setShowing(INOUT_KEY, KEQUATION_LABEL);
		builder.setShowing(ZERO_KEY, KEQUATION_LABEL);
		
		// builder.selectDictionary(selectedDict);
	}

	public static void setKOmega(DictionaryPanelBuilder builder) {
		// Dictionary selectedDict = builder.getSelectedModel().getDictionary();
		// Dictionary selectedCompanion =
		// builder.getSelectedModel().getCompanion().getDictionary();

		builder.setShowing(FIXED_KEY, OMEGA_LABEL);
		builder.setShowing(MIXING_KEY, OMEGA_LABEL);
		builder.setShowing(NONUNIFROM_KEY, OMEGA_LABEL);
		builder.setShowing(TIMEVARYING_KEY, OMEGA_LABEL);
		builder.setShowing(INOUT_KEY, OMEGA_LABEL);
		builder.setShowing(ZERO_KEY, OMEGA_LABEL);

		// builder.selectDictionaries(selectedDict, selectedCompanion);
	}

	public static void setKEpsilon(DictionaryPanelBuilder builder) {
		// Dictionary selectedDict = builder.getSelectedModel().getDictionary();
		// Dictionary selectedCompanion =
		// builder.getSelectedModel().getCompanion().getDictionary();

		builder.setShowing(FIXED_KEY, EPSILON_LABEL);
		builder.setShowing(MIXING_KEY, EPSILON_LABEL);
		builder.setShowing(NONUNIFROM_KEY, EPSILON_LABEL);
		builder.setShowing(TIMEVARYING_KEY, EPSILON_LABEL);
		builder.setShowing(INOUT_KEY, EPSILON_LABEL);
		builder.setShowing(ZERO_KEY, EPSILON_LABEL);

		// builder.selectDictionaries(selectedDict, selectedCompanion);
	}
}
