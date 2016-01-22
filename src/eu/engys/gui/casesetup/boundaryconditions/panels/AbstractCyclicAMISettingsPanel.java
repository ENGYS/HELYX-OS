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
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.AMI_BOUNDARY_CONDITION;
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.AMI_BOUNDARY_CONDITION_VECTOR;
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.CYCLIC_AMI_COUPLING;
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.CYCLIC_AMI_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.CYCLIC_AMI_TRANSLATIONAL;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.modules.boundaryconditions.ParametersPanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Field.FieldType;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.core.project.zero.patches.BoundaryConditionsDefaults;
import eu.engys.core.project.zero.patches.BoundaryType;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.PanelBuilder;

public abstract class AbstractCyclicAMISettingsPanel extends JPanel implements BoundaryTypePanel {

	public static final String MATCH_TOLERANCE_KEY = "matchTolerance";
	public static final String NEIGHBOUR_PATCH_KEY = "neighbourPatch";
	public static final String SEPARATION_VECTOR_LABEL = "Separation Vector";
	public static final String SEPARATION_VECTOR_KEY = "separationVector";
	public static final String ROTATION_CENTRE_KEY = "rotationCentre";
	public static final String ROTATION_AXIS_KEY = "rotationAxis";
	public static final String TRANSFORM_KEY = "transform";
	public static final String COUPLING_KEY = "noOrdering";
	public static final String TRANSLATIONAL_KEY = "translational";
	public static final String ROTATIONAL_KEY = "rotational";
	public static final String ROTATION_ANGLE_KEY = "rotationAngle";
	public static final String WEIGHT_CORRECTION_KEY = "lowWeightCorrection";
	public static final String BRIDGE_OVERLAP_KEY = "bridgeOverlap";

	public static final String ROTATION_ANGLE_LABEL = "Rotation [deg]";
	public static final String WEIGHT_CORRECTION_LABEL = "Weight Correction";
	public static final String TRANSFORM_LABEL = "Transform";
	public static final String COUPLING_LABEL = "Coupling";
	public static final String TRANSLATIONAL_LABEL = "Translational";
	public static final String ROTATIONAL_LABEL = "Rotational";
	public static final String CENTRE_LABEL = "Centre";
	public static final String AXIS_LABEL = "Axis";
	public static final String NEIGHBOUR_PATCH_LABEL = "Neighbour Patch";
	public static final String MATCH_TOLERANCE_LABEL = "Match Tolerance";

	protected DictionaryModel cyclicModel;
	protected DictionaryModel rotationalModel;
	protected DictionaryModel couplingModel;
	private DictionaryModel translationalModel;

	protected Model model;

	protected DictionaryPanelBuilder transformBuilder;

	public AbstractCyclicAMISettingsPanel(Model model) {
		super(new BorderLayout());
		this.model = model;
		this.cyclicModel = new DictionaryModel();
		this.couplingModel = new DictionaryModel();
		this.rotationalModel = new DictionaryModel();
		this.translationalModel = new DictionaryModel();
	}

	@Override
	public void resetToDefault() {
		this.couplingModel.setDictionary(new Dictionary(CYCLIC_AMI_COUPLING));
		this.translationalModel.setDictionary(new Dictionary(CYCLIC_AMI_TRANSLATIONAL));
	}

	@Override
	public void layoutPanel() {
		resetToDefault();

		PanelBuilder builder = new PanelBuilder();

		bindAmiParameters(builder);

		transformBuilder = new DictionaryPanelBuilder();
		transformBuilder.startChoice(TRANSFORM_LABEL);

		bindCouplingParameters();

		bindRotationalParameters();

		bindTranslationalParameters();

		transformBuilder.endChoice();
		transformBuilder.selectDictionary(couplingModel.getDictionary());

		builder.addFill(transformBuilder.removeMargins().getPanel());

		add(builder.getPanel());
	}

	protected abstract void bindAmiParameters(PanelBuilder builder);

	private void bindCouplingParameters() {
		transformBuilder.startDictionary(COUPLING_LABEL, couplingModel);
		transformBuilder.endDictionary();
	}

	protected abstract void bindRotationalParameters();

	private void bindTranslationalParameters() {
		transformBuilder.startDictionary(TRANSLATIONAL_LABEL, translationalModel);
		transformBuilder.addComponent(SEPARATION_VECTOR_LABEL, translationalModel.bindPoint(SEPARATION_VECTOR_KEY));
		transformBuilder.endDictionary();
	}

	@Override
	public BoundaryType getType() {
		return BoundaryType.CYCLIC_AMI;
	}

	@Override
	public Component getPanel() {
		return this;
	}

	@Override
	public void saveToPatch(Patch patch) {
		Dictionary cyclicDict = new Dictionary(cyclicModel.getDictionary());
		Dictionary transformDict = new Dictionary(transformBuilder.getSelectedModel().getDictionary());
		transformDict.remove(TYPE);
		cyclicDict.merge(transformDict);

		patch.setBoundaryConditions(getAMIBoundaryConditions());
		Dictionary oldPatchDict = new Dictionary(patch.getDictionary());
		Dictionary oldPatchCyclicDict = extractCyclicDict(oldPatchDict);
		oldPatchCyclicDict.merge(cyclicDict);
		patch.setDictionary(oldPatchCyclicDict);

		updateNeighbourPatchDictionary(patch.getName(), oldPatchDict, cyclicDict);
	}

	protected Dictionary extractCyclicDict(Dictionary dictionary) {
		Dictionary cyclicDict = new Dictionary(dictionary);
		cyclicDict.remove(TRANSFORM_KEY);
		cyclicDict.remove(SEPARATION_VECTOR_KEY);
		cyclicDict.remove(ROTATION_AXIS_KEY);
		cyclicDict.remove(ROTATION_CENTRE_KEY);
		return cyclicDict;
	}

	protected Dictionary extractTransformDict(Dictionary dictionary) {
		Dictionary transformDict = new Dictionary("");
		if (dictionary.found(TRANSFORM_KEY)) {
			transformDict.add(TRANSFORM_KEY, dictionary.lookup(TRANSFORM_KEY));
		}
		if (dictionary.found(SEPARATION_VECTOR_KEY)) {
			transformDict.add(SEPARATION_VECTOR_KEY, dictionary.lookup(SEPARATION_VECTOR_KEY));
		}
		if (dictionary.found(ROTATION_AXIS_KEY)) {
			transformDict.add(ROTATION_AXIS_KEY, dictionary.lookup(ROTATION_AXIS_KEY));
		}
		if (dictionary.found(ROTATION_CENTRE_KEY)) {
			transformDict.add(ROTATION_CENTRE_KEY, dictionary.lookup(ROTATION_CENTRE_KEY));
		}
		switch (dictionary.lookup(TRANSFORM_KEY)) {
		case COUPLING_KEY:
			transformDict.add(TYPE, COUPLING_LABEL);
			break;
		case ROTATIONAL_KEY:
			transformDict.add(TYPE, ROTATIONAL_LABEL);
			break;
		case TRANSLATIONAL_KEY:
			transformDict.add(TYPE, TRANSLATIONAL_LABEL);
			break;
		default:
			transformDict.add(TYPE, COUPLING_LABEL);
			break;
		}
		return transformDict;
	}

	private BoundaryConditions getAMIBoundaryConditions() {
		BoundaryConditions boundaryConditions = new BoundaryConditions();
		for (Field field : model.getFields().values()) {
			if (field.getFieldType() == FieldType.SCALAR) {
				boundaryConditions.add(field.getName(), new Dictionary(AMI_BOUNDARY_CONDITION));
			} else {
				boundaryConditions.add(field.getName(), new Dictionary(AMI_BOUNDARY_CONDITION_VECTOR));
			}
		}
		return boundaryConditions;
	}

	private void updateNeighbourPatchDictionary(String patchName, Dictionary oldDict, Dictionary newDict) {
		Patch neighbourPatch = model.getPatches().patchesToDisplay().toMap().get(newDict.lookup(NEIGHBOUR_PATCH_KEY));
		if (neighbourPatch != null) {
			fixPreviousRelatedNeighbourPatches(patchName, neighbourPatch.getName());
			setNeighbourPatchToAMI(patchName, newDict, neighbourPatch);
		} else if (isAlreadyCyclicAMIWithoutNeighbour(oldDict)) {
			JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "No Neighbour Patch selected!\n", "Cyclic AMI Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void fixPreviousRelatedNeighbourPatches(String patchName, String neighbourPatchName) {
		for (Patch p : model.getPatches().patchesToDisplay()) {
			if (isRelatedPatch(p, patchName, neighbourPatchName)) {
				setToDefaultType(p);
			}
		}
	}

	private void setToDefaultType(Patch p) {
		p.setPhisicalType(BoundaryType.getDefaultType());
		p.setBoundaryConditions(new BoundaryConditions(BoundaryConditionsDefaults.get(BoundaryType.getDefaultKey())));
		p.setDictionary(new Dictionary(""));
	}

	private void setNeighbourPatchToAMI(String patchName, Dictionary newDict, Patch neighbourPatch) {
		neighbourPatch.setBoundaryConditions(getAMIBoundaryConditions());
		neighbourPatch.setPhisicalType(BoundaryType.CYCLIC_AMI);
		if (newDict.found(SEPARATION_VECTOR_KEY)) {
			invertSeparationVector(newDict);
		}
		neighbourPatch.getDictionary().merge(newDict);
		neighbourPatch.getDictionary().add(NEIGHBOUR_PATCH_KEY, patchName);
	}

	private boolean isAlreadyCyclicAMIWithoutNeighbour(Dictionary dict) {
		boolean notNull = dict != null;
		boolean isCyclicAMI = dict.found(TYPE) && dict.lookup(TYPE).equals(CYCLIC_AMI_KEY);
		boolean withoutNeighbour = dict.found(NEIGHBOUR_PATCH_KEY) && dict.lookup(NEIGHBOUR_PATCH_KEY).equals("");
		return notNull && isCyclicAMI && withoutNeighbour;
	}

	private boolean isRelatedPatch(Patch p, String patchName, String neighbourPatchName) {
		boolean isNotCurrentOrNeighbourPatch = !p.getName().equals(patchName) && !p.getName().equals(neighbourPatchName);
		boolean isAMI = p.getPhisicalType().isCyclicAMI();
		boolean hasNeighbour = p.getDictionary() != null && p.getDictionary().found(NEIGHBOUR_PATCH_KEY);
		if (hasNeighbour) {
			boolean neighbourEqualsCurrentPatch = p.getDictionary().lookup(NEIGHBOUR_PATCH_KEY).equals(patchName);
			boolean neighbourEqualsNeighbourPatch = p.getDictionary().lookup(NEIGHBOUR_PATCH_KEY).equals(neighbourPatchName);
			return (isNotCurrentOrNeighbourPatch && isAMI && hasNeighbour && (neighbourEqualsCurrentPatch || neighbourEqualsNeighbourPatch));
		}
		return false;
	}

	private void invertSeparationVector(Dictionary d) {
		String[] sepVector = d.lookupArray(SEPARATION_VECTOR_KEY);
		StringBuilder sb = new StringBuilder("(");
		for (String value : sepVector) {
			double doubleValue = Double.parseDouble(value);
			sb.append(-doubleValue + " ");
		}
		sb.append(")");
		d.add(SEPARATION_VECTOR_KEY, sb.toString());
	}

	@Override
	public void stateChanged() {
	}

	@Override
	public void addMomentumPanel(ParametersPanel momentumPanel) {
	}

	@Override
	public void addTurbulencePanel(ParametersPanel momentumPanel) {
	}

	@Override
	public void addThermalPanel(ParametersPanel momentumPanel) {
	}

	@Override
	public void addPanel(String name, ParametersPanel pPanel) {
	}

	@Override
	public void addPanel(String name, ParametersPanel pPanel, int index) {
	}
	
	@Override
	public ParametersPanel getMomentumPanel() {
		return null;
	}

	@Override
	public ParametersPanel getTurbulencePanel() {
		return null;
	}

	@Override
	public ParametersPanel getThermalPanel() {
		return null;
	}
	
	@Override
	public ParametersPanel getPanel(String name) {
	    return null;
	}
}
