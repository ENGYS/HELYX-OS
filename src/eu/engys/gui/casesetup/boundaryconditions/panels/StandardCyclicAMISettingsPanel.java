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


package eu.engys.gui.casesetup.boundaryconditions.panels;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.CYCLIC_AMI_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.CYCLIC_AMI_OS;
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.CYCLIC_AMI_ROTATIONAL_OS;

import javax.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.gui.ListBuilderFactory;
import eu.engys.util.ui.builder.PanelBuilder;

public class StandardCyclicAMISettingsPanel extends AbstractCyclicAMISettingsPanel {

	@Inject
	public StandardCyclicAMISettingsPanel(Model model) {
		super(model);
	}

	@Override
	public void resetToDefault() {
		super.resetToDefault();
		this.cyclicModel.setDictionary(new Dictionary(CYCLIC_AMI_OS));
		this.rotationalModel.setDictionary(new Dictionary(CYCLIC_AMI_ROTATIONAL_OS));
	}

	@Override
	protected void bindAmiParameters(PanelBuilder builder) {
		builder.addComponent(MATCH_TOLERANCE_LABEL, cyclicModel.bindDouble(MATCH_TOLERANCE_KEY));
		builder.addComponent(WEIGHT_CORRECTION_LABEL, cyclicModel.bindDouble(WEIGHT_CORRECTION_KEY));
		builder.addComponent(NEIGHBOUR_PATCH_LABEL, cyclicModel.bindSelection(NEIGHBOUR_PATCH_KEY, ListBuilderFactory.getPatchesListBuilder(model)));
	}

	@Override
	protected void bindRotationalParameters() {
		transformBuilder.startDictionary(ROTATIONAL_LABEL, rotationalModel);
		transformBuilder.addComponent(AXIS_LABEL, rotationalModel.bindPoint(ROTATION_AXIS_KEY));
		transformBuilder.addComponent(CENTRE_LABEL, rotationalModel.bindPoint(ROTATION_CENTRE_KEY));
		transformBuilder.addComponent(ROTATION_ANGLE_LABEL, rotationalModel.bindDoubleAngle_360(ROTATION_ANGLE_KEY));
		transformBuilder.endDictionary();
	}

	@Override
	public void loadFromPatches(Patch... patches) {
		if (patches.length == 1) {
			Dictionary dictionary = patches[0].getDictionary();
			if (dictionary.found(TYPE) && dictionary.lookup(TYPE).equals(CYCLIC_AMI_KEY)) {
				Dictionary cyclicDict = extractCyclicDict(dictionary);
				Dictionary transformDict = extractTransformDict(dictionary);
				cyclicModel.setDictionary(cyclicDict);
				transformBuilder.selectDictionary(transformDict);
			} else {
				cyclicModel.setDictionary(new Dictionary(CYCLIC_AMI_OS));
				transformBuilder.selectDictionary(couplingModel.getDictionary());
			}
		}
	}

	@Override
	protected Dictionary extractCyclicDict(Dictionary dictionary) {
		Dictionary cyclicDict = super.extractCyclicDict(dictionary);
		cyclicDict.remove(ROTATION_ANGLE_KEY);
		return cyclicDict;
	}

	@Override
	protected Dictionary extractTransformDict(Dictionary dictionary) {
		Dictionary transformDict = super.extractTransformDict(dictionary);
		if (dictionary.found(ROTATION_ANGLE_KEY)) {
			transformDict.add(ROTATION_ANGLE_KEY, dictionary.lookup(ROTATION_ANGLE_KEY));
		}
		return transformDict;
	}

	@Override
	public void materialsChanged() {
	}

}
