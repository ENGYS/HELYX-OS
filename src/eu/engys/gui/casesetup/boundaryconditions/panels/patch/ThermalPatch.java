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


package eu.engys.gui.casesetup.boundaryconditions.panels.patch;

import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.fixedValue;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.inletOutlet;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.inletOutletTotalTemperature;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.totalTemperature;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.zeroGradient;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.gui.casesetup.boundaryconditions.panels.ThermalParametersPanel;

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
		
		builder.startChoice("Type");
		buildFixedTemperaturePanel();
		buildTotalTemperaturePanel();
		buildInletOutlet();
		buildZeroGradient();
		buildInletOutletTotalTemperature();
		builder.endChoice();
	}

	private void buildFixedTemperaturePanel() {
		builder.startDictionary("Fixed Temperature", fixedTemperatureModel);
		builder.addComponent("Temperature Value [K]", fixedTemperatureModel.bindUniformDouble("value"));
		builder.endDictionary();
	}

	private void buildTotalTemperaturePanel() {
		builder.startDictionary("Total Temperature", totalTemperatureModel);
		builder.addComponent("Compressibility", totalTemperatureModel.bindDouble("psi"));
		builder.addComponent("Ratio Of Specific Heats", totalTemperatureModel.bindDouble("gamma"));
		builder.addComponent("Temperature Value [K]", totalTemperatureModel.bindUniformDouble("T0"));
		builder.endDictionary();
	}

	private void buildInletOutlet() {
		builder.startDictionary("Inlet Outlet", inletOutletModel);
		builder.addComponent("Temperature Value [K]", inletOutletModel.bindUniformDouble("value", "inletValue"));
		builder.endDictionary();
	}

	private void buildZeroGradient() {
		builder.startDictionary("Zero Gradient", zeroGradientModel);
		builder.endDictionary();
	}

	private void buildInletOutletTotalTemperature() {
		builder.startDictionary("Inlet Outlet Total Temperature", inletOutletTotalTemperatureModel);
		builder.addComponent("Compressibility", inletOutletTotalTemperatureModel.bindDouble("psi"));
		builder.addComponent("Ratio Of Specific Heats", inletOutletTotalTemperatureModel.bindDouble("gamma"));
		builder.addComponent("Temperature Value [K]", inletOutletTotalTemperatureModel.bindUniformDouble("T0"));
		builder.endDictionary();
	}

	@Override
	public void loadFromBoundaryConditions(String patchName, BoundaryConditions bc) {
		Dictionary dictionary = bc.getThermal();
		Dictionary T = dictionary.subDict("T");
		builder.selectDictionary(T);
	}

}
