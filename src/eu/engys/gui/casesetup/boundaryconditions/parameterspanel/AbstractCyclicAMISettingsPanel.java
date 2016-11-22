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
package eu.engys.gui.casesetup.boundaryconditions.parameterspanel;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.AMI_BOUNDARY_CONDITION;
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.AMI_BOUNDARY_CONDITION_VECTOR;
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.CYCLIC_AMI_COUPLING;
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.CYCLIC_AMI_TRANSLATIONAL;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.modules.boundaryconditions.ParametersPanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.FieldType;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.core.project.zero.patches.BoundaryType;
import eu.engys.core.project.zero.patches.Patch;
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
    protected Model model;
    protected DictionaryPanelBuilder transformBuilder;
    private DictionaryModel translationalModel;

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
        Dictionary oldDict = patch.getDictionary();
        Dictionary newDict = getNewDict();

        resetOldNeighbourPatch(oldDict, newDict);

        patch.setDictionary(newDict);
        patch.setBoundaryConditions(getAMIBoundaryConditions());

        setNeighbourPatchToAMI(newDict, patch.getName());
    }

    /*
     * Scenario:
     * 1) you set patch A to AMI and you set patch B as its neighbour
     * 2) patch A may already be an AMI patch with another neighbour (let say patch C)
     * 3) patch C should be reset to default type
     */
    private void resetOldNeighbourPatch(Dictionary oldDict, Dictionary newDict) {
        if (oldDict.found(NEIGHBOUR_PATCH_KEY)) {
            String oldNeighbourPatchName = oldDict.lookup(NEIGHBOUR_PATCH_KEY);
            if (newDict.found(NEIGHBOUR_PATCH_KEY)) {
                String newNeighbourPatchName = newDict.lookup(NEIGHBOUR_PATCH_KEY);
                if (oldNeighbourPatchName.equals(newNeighbourPatchName)) {
                    // Same neighbour = nothing to do
                    return;
                } else {
                    Patch neighbourPatch = model.getPatches().toMap().get(oldNeighbourPatchName);
                    if (neighbourPatch != null && neighbourPatch.getPhysicalType().isCyclicAMI()) {
                        neighbourPatch.resetToDefault();
                    }
                }
            } else {
                Patch neighbourPatch = model.getPatches().toMap().get(oldNeighbourPatchName);
                if (neighbourPatch != null && neighbourPatch.getPhysicalType().isCyclicAMI()) {
                    neighbourPatch.resetToDefault();
                }
            }
        }
    }

    private void setNeighbourPatchToAMI(Dictionary newDict, String patchName) {
        if (newDict.found(NEIGHBOUR_PATCH_KEY)) {
            String neighbourPatchName = newDict.lookup(NEIGHBOUR_PATCH_KEY);
            Patch neighbourPatch = model.getPatches().toMap().get(neighbourPatchName);
            if (neighbourPatch != null) {
                resetNeighbourOfNeighbourIfAny(neighbourPatch, patchName);
                neighbourPatch.setPhysicalType(BoundaryType.CYCLIC_AMI);
                neighbourPatch.setDictionary(createDictionaryForNeighbourPatch(newDict, patchName));
                neighbourPatch.setBoundaryConditions(getAMIBoundaryConditions());
            }
        }
    }

    /*
     * Scenario:
     * 1) you set patch A to AMI and you set patch B as its neighbour
     * 2) patch B may already be an AMI patch with its own neighbour (let say patch C)
     * 3) patch C should be reset to default type because you cannot have 2 patches with the same neighbour (patch B)
     * 4) of course only if patch C != patch A
     */
    private void resetNeighbourOfNeighbourIfAny(Patch neighbourPatch, String patchName) {
        if (neighbourPatch.getPhysicalType().isCyclicAMI() && neighbourPatch.getDictionary().found(NEIGHBOUR_PATCH_KEY)) {
            String neighbour2PatchName = neighbourPatch.getDictionary().lookup(NEIGHBOUR_PATCH_KEY);
            if (neighbour2PatchName.equals(patchName)) {
                // nothing do do
                return;
            } else {
                Patch neighbour2Patch = model.getPatches().toMap().get(neighbour2PatchName);
                if (neighbour2Patch != null) {
                    neighbour2Patch.resetToDefault();
                }
            }

        }
    }

    private Dictionary createDictionaryForNeighbourPatch(Dictionary newDict, String patchName) {
        Dictionary neighbourPatchDict = new Dictionary(newDict);
        neighbourPatchDict.add(NEIGHBOUR_PATCH_KEY, patchName);
        if (neighbourPatchDict.found(SEPARATION_VECTOR_KEY)) {
            invertSeparationVector(neighbourPatchDict);
        }
        return neighbourPatchDict;
    }

    private void invertSeparationVector(Dictionary d) {
        String[] sepVector = d.lookupArray(SEPARATION_VECTOR_KEY);
        StringBuilder sb = new StringBuilder("( ");
        for (String value : sepVector) {
            double doubleValue = Double.parseDouble(value);
            sb.append(-doubleValue + " ");
        }
        sb.append(")");
        d.add(SEPARATION_VECTOR_KEY, sb.toString());
    }

    private Dictionary getNewDict() {
        Dictionary cyclicDict = new Dictionary(cyclicModel.getDictionary());
        Dictionary transformDict = new Dictionary(transformBuilder.getSelectedModel().getDictionary());
        transformDict.remove(TYPE);
        cyclicDict.merge(transformDict);
        return cyclicDict;
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
            if (Fields.getFieldTypeByName(field.getName()) == FieldType.SCALAR) {
                boundaryConditions.add(field.getName(), new Dictionary(AMI_BOUNDARY_CONDITION(field.getInternalFieldScalarValue(model.getState()))));
            } else {
                boundaryConditions.add(field.getName(), new Dictionary(AMI_BOUNDARY_CONDITION_VECTOR(field.getInternalFieldArrayValue(model.getState()))));
            }
        }
        return boundaryConditions;
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
