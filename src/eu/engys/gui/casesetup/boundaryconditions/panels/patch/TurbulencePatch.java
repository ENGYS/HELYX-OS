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

import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.epsilonFixedValue;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.epsilonInletOutlet;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.epsilonMixingLength;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.epsilonMixingLength_COMP;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.epsilonZeroGradient;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.kFixedValue;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.kInletOutlet;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.kMixingLength;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.kZeroGradient;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.nuTildaInletOutlet;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.nuTildaZeroGradient;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.nutildaFixedValue;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.omegaFixedValue;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.omegaInletOutlet;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.omegaMixingLength;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.omegaMixingLength_COMP;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TurbulenceFactory.omegaZeroGradient;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.State;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.gui.casesetup.boundaryconditions.panels.TurbulenceParametersPanel;
import eu.engys.gui.casesetup.boundaryconditions.utils.TurbulenceUtils;
import eu.engys.util.ui.builder.JComboBoxController;

public class TurbulencePatch extends TurbulenceParametersPanel {

	private JComboBoxController typeChoice;

	private DictionaryModel dictKFixedModel;
	private DictionaryModel dictOmegaFixedModel;
	private DictionaryModel dictEpsilonFixedModel;
	private DictionaryModel dictNuTildaFixedModel;
	private DictionaryModel dictKInletOutletModel;
	private DictionaryModel dictOmegaInletOutletModel;
	private DictionaryModel dictEpsilonInletOutletModel;
	private DictionaryModel dictNuTildaInletOutletModel;
	private DictionaryModel dictKZeroGradientModel;
	private DictionaryModel dictOmegaZeroGradientModel;
	private DictionaryModel dictEpsilonZeroGradientModel;
	private DictionaryModel dictNuTildaZeroGradientModel;
	private DictionaryModel dictKTurbulentIntensityModel;
	private DictionaryModel dictOmegaTurbulentIntensityModel;
	private DictionaryModel dictEpsilonTurbulentIntensityModel;

	public TurbulencePatch(BoundaryTypePanel parent) {
		super(parent);
	}

	@Override
	protected void init() {
		dictKFixedModel = new DictionaryModel();
		dictOmegaFixedModel = new DictionaryModel();
		dictEpsilonFixedModel = new DictionaryModel();
		dictNuTildaFixedModel = new DictionaryModel();

		dictKInletOutletModel = new DictionaryModel();
		dictOmegaInletOutletModel = new DictionaryModel();
		dictEpsilonInletOutletModel = new DictionaryModel();
		dictNuTildaInletOutletModel = new DictionaryModel();

		dictKZeroGradientModel = new DictionaryModel();
		dictOmegaZeroGradientModel = new DictionaryModel();
		dictEpsilonZeroGradientModel = new DictionaryModel();
		dictNuTildaZeroGradientModel = new DictionaryModel();

		dictKTurbulentIntensityModel = new DictionaryModel();
		dictOmegaTurbulentIntensityModel = new DictionaryModel();
		dictEpsilonTurbulentIntensityModel = new DictionaryModel();
	}

	@Override
	public void resetToDefault(Model model) {
		dictKFixedModel.setDictionary(new Dictionary(kFixedValue));
		dictOmegaFixedModel.setDictionary(new Dictionary(omegaFixedValue));
		dictEpsilonFixedModel.setDictionary(new Dictionary(epsilonFixedValue));
		dictNuTildaFixedModel.setDictionary(new Dictionary(nutildaFixedValue));

		dictKInletOutletModel.setDictionary(new Dictionary(kInletOutlet));
		dictOmegaInletOutletModel.setDictionary(new Dictionary(omegaInletOutlet));
		dictEpsilonInletOutletModel.setDictionary(new Dictionary(epsilonInletOutlet));
		dictNuTildaInletOutletModel.setDictionary(new Dictionary(nuTildaInletOutlet));

		dictKZeroGradientModel.setDictionary(new Dictionary(kZeroGradient));
		dictOmegaZeroGradientModel.setDictionary(new Dictionary(omegaZeroGradient));
		dictEpsilonZeroGradientModel.setDictionary(new Dictionary(epsilonZeroGradient));
		dictNuTildaZeroGradientModel.setDictionary(new Dictionary(nuTildaZeroGradient));

		dictKTurbulentIntensityModel.setDictionary(new Dictionary(kMixingLength));

		if (model != null && model.getState() != null && model.getState().isCompressible()) {
			dictOmegaTurbulentIntensityModel.setDictionary(new Dictionary(omegaMixingLength_COMP));
			dictEpsilonTurbulentIntensityModel.setDictionary(new Dictionary(epsilonMixingLength_COMP));
		} else {
			dictOmegaTurbulentIntensityModel.setDictionary(new Dictionary(omegaMixingLength));
			dictEpsilonTurbulentIntensityModel.setDictionary(new Dictionary(epsilonMixingLength));
		}
	}

	@Override
	public void populatePanel() {
		resetToDefault(null);
		typeChoice = (JComboBoxController) builder.startChoice("Type");
		TurbulenceUtils.buildFixedKnownValuesPanel(builder, dictKFixedModel, dictOmegaFixedModel, dictEpsilonFixedModel, dictNuTildaFixedModel);
		TurbulenceUtils.buildInletOutletPanel(builder, dictKInletOutletModel, dictOmegaInletOutletModel, dictEpsilonInletOutletModel, dictNuTildaInletOutletModel);
		TurbulenceUtils.buildTurbulentIntensityAndMixingLengthPanel(builder, dictKTurbulentIntensityModel, dictOmegaTurbulentIntensityModel, dictEpsilonTurbulentIntensityModel, null);
		TurbulenceUtils.buildZeroGradientPanel(builder, dictKZeroGradientModel, dictOmegaZeroGradientModel, dictEpsilonZeroGradientModel, dictNuTildaZeroGradientModel);
		builder.endChoice();
	}

	public void loadFromBoundaryConditions(String patchName, BoundaryConditions bc) {
		Dictionary dictionary = bc.getTurbulence();
		Dictionary k = dictionary.subDict(Fields.K);
		Dictionary omega = dictionary.subDict(Fields.OMEGA);
		Dictionary epsilon = dictionary.subDict(Fields.EPSILON);
		Dictionary nutilda = dictionary.subDict(Fields.NU_TILDA);

		if (nutilda != null) {
			builder.selectDictionary(nutilda);
		} else if (k != null) {
			if (omega != null) {
				builder.selectDictionaries(omega, k);
			} else if (epsilon != null) {
				builder.selectDictionaries(epsilon, k);
			} else {
				builder.selectDictionary(k);
			}
		}
	}

	@Override
	public void tabChanged(Model model) {
		super.tabChanged(model);
		
		fixIntensityAndMixingVisibility(model);
	}

	@Override
	public void stateChanged(Model model) {
		super.stateChanged(model);
		fixIntensityAndMixingVisibility(model);

		State state = model.getState();
		if (state.isCompressible()) {
			dictEpsilonTurbulentIntensityModel.setDictionary(new Dictionary(epsilonMixingLength_COMP));
			dictOmegaTurbulentIntensityModel.setDictionary(new Dictionary(omegaMixingLength_COMP));
		} else if (state.isIncompressible()) {
			dictEpsilonTurbulentIntensityModel.setDictionary(new Dictionary(epsilonMixingLength));
			dictOmegaTurbulentIntensityModel.setDictionary(new Dictionary(omegaMixingLength));
		}

	}

	private void fixIntensityAndMixingVisibility(Model model) {
		State state = model.getState();
		typeChoice.clearDisabledIndexes();
		if (state.getTurbulenceModel().getType().isSpalartAllmaras()) {
			typeChoice.addDisabledItem(TurbulenceUtils.BY_TURB_INTENSITY_AND_MIXING_LENGTH_LABEL);
		}
		
		if (state.getTurbulenceModel().getType().isKEquationeddy()) {
			typeChoice.addDisabledItem(TurbulenceUtils.BY_TURB_INTENSITY_AND_MIXING_LENGTH_LABEL);
		}
	}
	
}
