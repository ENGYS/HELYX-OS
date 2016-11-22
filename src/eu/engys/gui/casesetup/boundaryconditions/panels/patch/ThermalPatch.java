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
package eu.engys.gui.casesetup.boundaryconditions.panels.patch;

import static eu.engys.core.project.zero.fields.Fields.T;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.fixedValue;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.inletOutlet;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.inletOutletTotalTemperature;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.totalTemperature;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.zeroGradient;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.COMPRESSIBILITY_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FIXED_TEMPERATURE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.GAMMA_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INLET_OUTLET_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INLET_OUTLET_TOTAL_TEMPERATURE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INLET_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PSI_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.RATIO_OF_SPECIFIC_HEATS_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.T0_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TEMPERATURE_VALUE_K_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TOTAL_TEMPERATURE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TYPE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ZERO_GRADIENT_LABEL;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.gui.casesetup.boundaryconditions.parameterspanel.ThermalParametersPanel;

public class ThermalPatch extends ThermalParametersPanel {

    private DictionaryModel fixedTemperatureModel;
	private DictionaryModel totalTemperatureModel;
	private DictionaryModel inletOutletModel;
	private DictionaryModel zeroGradientModel;
	private DictionaryModel inletOutletTotalTemperatureModel;

	public ThermalPatch(BoundaryTypePanel parent) {
		super(parent);
	}
	
	@Override
	protected void init() {
		fixedTemperatureModel = new DictionaryModel();
		totalTemperatureModel = new DictionaryModel();
		inletOutletModel = new DictionaryModel();
		zeroGradientModel = new DictionaryModel();
		inletOutletTotalTemperatureModel = new DictionaryModel();
	}

	@Override
	public void resetToDefault(Model model) {
		fixedTemperatureModel.setDictionary(new Dictionary(fixedValue));
		totalTemperatureModel.setDictionary(new Dictionary(totalTemperature));
		inletOutletModel.setDictionary(new Dictionary(inletOutlet));
		zeroGradientModel.setDictionary(new Dictionary(zeroGradient));
		inletOutletTotalTemperatureModel.setDictionary(new Dictionary(inletOutletTotalTemperature));
	}

	@Override
	public void populatePanel() {
		resetToDefault(null);
		
		builder.startChoice(TYPE_LABEL);
		buildFixedTemperaturePanel();
		buildTotalTemperaturePanel();
		buildInletOutlet();
		buildZeroGradient();
		buildInletOutletTotalTemperature();
		builder.endChoice();
	}

	private void buildFixedTemperaturePanel() {
		builder.startDictionary(FIXED_TEMPERATURE_LABEL, fixedTemperatureModel);
		builder.addComponent(TEMPERATURE_VALUE_K_LABEL, fixedTemperatureModel.bindUniformDouble(VALUE_KEY));
		builder.endDictionary();
	}

	private void buildTotalTemperaturePanel() {
		builder.startDictionary(TOTAL_TEMPERATURE_LABEL, totalTemperatureModel);
		builder.addComponent(COMPRESSIBILITY_LABEL, totalTemperatureModel.bindDouble(PSI_KEY));
		builder.addComponent(RATIO_OF_SPECIFIC_HEATS_LABEL, totalTemperatureModel.bindDouble(GAMMA_KEY));
		builder.addComponent(TEMPERATURE_VALUE_K_LABEL, totalTemperatureModel.bindUniformDouble(T0_KEY));
		builder.endDictionary();
	}

	private void buildInletOutlet() {
		builder.startDictionary(INLET_OUTLET_LABEL, inletOutletModel);
		builder.addComponent(TEMPERATURE_VALUE_K_LABEL, inletOutletModel.bindUniformDouble(VALUE_KEY, INLET_VALUE_KEY));
		builder.endDictionary();
	}

	private void buildZeroGradient() {
		builder.startDictionary(ZERO_GRADIENT_LABEL, zeroGradientModel);
		builder.endDictionary();
	}

	private void buildInletOutletTotalTemperature() {
		builder.startDictionary(INLET_OUTLET_TOTAL_TEMPERATURE_LABEL, inletOutletTotalTemperatureModel);
		builder.addComponent(COMPRESSIBILITY_LABEL, inletOutletTotalTemperatureModel.bindDouble(PSI_KEY));
		builder.addComponent(RATIO_OF_SPECIFIC_HEATS_LABEL, inletOutletTotalTemperatureModel.bindDouble(GAMMA_KEY));
		builder.addComponent(TEMPERATURE_VALUE_K_LABEL, inletOutletTotalTemperatureModel.bindUniformDouble(T0_KEY));
		builder.endDictionary();
	}

	@Override
	public void loadFromBoundaryConditions(BoundaryConditions bc) {
		Dictionary dictionary = bc.getThermal();
		Dictionary tDict = dictionary.subDict(T);
		builder.selectDictionary(tDict);
	}

}
