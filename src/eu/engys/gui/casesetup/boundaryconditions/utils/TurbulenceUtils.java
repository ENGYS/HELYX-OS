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

import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;

public class TurbulenceUtils extends BoundaryConditionsUtils {

	public static void buildFixedKnownValuesPanel(DictionaryPanelBuilder builder, DictionaryModel dictK, DictionaryModel dictOmega, DictionaryModel dictEpsilon, DictionaryModel dictNutilda) {
		builder.startGroup(FIXED_VALUES_LABEL);
		builder.startHidable(FIXED_KEY);

		dictOmega.setCompanion(dictK);
		builder.startDictionary(OMEGA_TURBULENCE_LABEL, dictOmega);
		builder.addComponent(K_LABEL, dictK.bindUniformDouble(VALUE_KEY));
		builder.addComponent(OMEGA_TURBULENCE_LABEL, dictOmega.bindUniformDouble(VALUE_KEY));
		builder.endDictionary();

		dictEpsilon.setCompanion(dictK);
		builder.startDictionary(EPSILON_LABEL, dictEpsilon);
		builder.addComponent(K_LABEL, dictK.bindUniformDouble(VALUE_KEY));
		builder.addComponent(EPSILON_LABEL, dictEpsilon.bindUniformDouble(VALUE_KEY));
		builder.endDictionary();

		builder.startDictionary(NUTILDA_LABEL, dictNutilda);
		builder.addComponent(NUTILDA_LABEL, dictNutilda.bindUniformDouble(VALUE_KEY));
		builder.endDictionary();

		builder.startDictionary(KEQUATION_LABEL, dictK);
		builder.addComponent(K_LABEL, dictK.bindUniformDouble(VALUE_KEY));
		builder.endDictionary();

		builder.endHidable();
		builder.endGroup();
	}

	public static void buildInletOutletPanel(DictionaryPanelBuilder builder, DictionaryModel dictK, DictionaryModel dictOmega, DictionaryModel dictEpsilon, DictionaryModel dictNutilda) {
		builder.startGroup(INFLOW_FIXED_VALUES_LABEL);
		builder.startHidable(INOUT_KEY);

		dictOmega.setCompanion(dictK);
		builder.startDictionary(OMEGA_TURBULENCE_LABEL, dictOmega);
		builder.addComponent(K_LABEL, dictK.bindUniformDouble(INLET_VALUE_KEY));
		builder.addComponent(OMEGA_TURBULENCE_LABEL, dictOmega.bindUniformDouble(INLET_VALUE_KEY));
		builder.endDictionary();

		dictEpsilon.setCompanion(dictK);
		builder.startDictionary(EPSILON_LABEL, dictEpsilon);
		builder.addComponent(K_LABEL, dictK.bindUniformDouble(INLET_VALUE_KEY));
		builder.addComponent(EPSILON_LABEL, dictEpsilon.bindUniformDouble(INLET_VALUE_KEY));
		builder.endDictionary();

		builder.startDictionary(NUTILDA_LABEL, dictNutilda);
		builder.addComponent(NUTILDA_LABEL, dictNutilda.bindUniformDouble(INLET_VALUE_KEY));
		builder.endDictionary();

		builder.startDictionary(KEQUATION_LABEL, dictK);
		builder.addComponent(K_LABEL, dictK.bindUniformDouble(INLET_VALUE_KEY));
		builder.endDictionary();
		
		builder.endHidable();
		builder.endGroup();
	}

	public static void buildZeroGradientPanel(DictionaryPanelBuilder builder, DictionaryModel dictK, DictionaryModel dictOmega, DictionaryModel dictEpsilon, DictionaryModel dictNutilda) {
		builder.startGroup(ZERO_GRADIENT_LABEL);
		builder.startHidable(ZERO_KEY);

		dictOmega.setCompanion(dictK);
		builder.startDictionary(OMEGA_TURBULENCE_LABEL, dictOmega);
		builder.endDictionary();

		dictEpsilon.setCompanion(dictK);
		builder.startDictionary(EPSILON_LABEL, dictEpsilon);
		builder.endDictionary();

		builder.startDictionary(NUTILDA_LABEL, dictNutilda);
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
		builder.startDictionary(OMEGA_TURBULENCE_LABEL, dictOmega);
//		builder.addComponent(new JLabel(OMEGA_LABEL), new JLabel(""));
		builder.addComponent(TURBULENCE_INTENSITY_LABEL, dictK.bindDouble(INTENSITY_KEY));
		builder.addComponent(MIXING_LENGTH_LABEL, dictOmega.bindDouble(MIXING_LENGTH_KEY));
		builder.endDictionary();

		dictEpsilon.setCompanion(dictK);
		builder.startDictionary(EPSILON_LABEL, dictEpsilon);
//		builder.addComponent(new JLabel(EPSILON_LABEL), new JLabel(""));
		builder.addComponent(TURBULENCE_INTENSITY_LABEL, dictK.bindDouble(INTENSITY_KEY));
		builder.addComponent(MIXING_LENGTH_LABEL, dictEpsilon.bindDouble(MIXING_LENGTH_KEY));
		builder.endDictionary();

		if (dictNuTilda != null) {
			builder.startDictionary(NUTILDA_LABEL, dictNuTilda);
//			builder.addComponent(new JLabel(NU_TILDA_LABEL), new JLabel(""));
			builder.addComponent(TURBULENCE_INTENSITY_LABEL, dictNuTilda.bindDouble(INTENSITY_KEY));
			builder.addComponent(MIXING_LENGTH_LABEL, dictNuTilda.bindDouble(LENGTH_KEY));
			builder.endDictionary();
		}

		builder.startDictionary(KEQUATION_LABEL, dictK);
		builder.endDictionary();
		builder.endHidable();
		builder.endGroup();
	}

	public static void setSpalartAllmaras(DictionaryPanelBuilder builder) {
		// Dictionary selectedDict = builder.getSelectedModel().getDictionary();
		builder.setShowing(FIXED_KEY, NUTILDA_LABEL);
		// builder.setShowing(MIXING_KEY, NU_TILDA_LABEL);
		builder.setShowing(NONUNIFROM_KEY, NUTILDA_LABEL);
		builder.setShowing(TIMEVARYING_KEY, NUTILDA_LABEL);
		builder.setShowing(INOUT_KEY, NUTILDA_LABEL);
		builder.setShowing(ZERO_KEY, NUTILDA_LABEL);

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

		builder.setShowing(FIXED_KEY, OMEGA_TURBULENCE_LABEL);
		builder.setShowing(MIXING_KEY, OMEGA_TURBULENCE_LABEL);
		builder.setShowing(NONUNIFROM_KEY, OMEGA_TURBULENCE_LABEL);
		builder.setShowing(TIMEVARYING_KEY, OMEGA_TURBULENCE_LABEL);
		builder.setShowing(INOUT_KEY, OMEGA_TURBULENCE_LABEL);
		builder.setShowing(ZERO_KEY, OMEGA_TURBULENCE_LABEL);

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
