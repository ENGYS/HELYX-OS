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

import static eu.engys.gui.casesetup.boundaryconditions.factories.StandardPhaseFactory.calculated;
import static eu.engys.gui.casesetup.boundaryconditions.factories.StandardPhaseFactory.fixedValueVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.StandardPhaseFactory.inletOutlet;
import static eu.engys.gui.casesetup.boundaryconditions.factories.StandardPhaseFactory.zeroGradient;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.materials.Materials;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.gui.casesetup.boundaryconditions.panels.PhaseParametersPanel;

public class PhasePatch extends PhaseParametersPanel {

    public static final String FIXED_VALUE_LABEL = "Fixed Value";
    public static final String ZERO_GRADIENT_LABEL = "Zero Gradient";
    public static final String INLET_OUTLET_LABEL = "Inlet Outlet";
    public static final String CALCULATED_LABEL = "Calculated";

    private DictionaryModel fixedModel;
    private DictionaryModel inletOutletModel;
    private DictionaryModel zeroGradientModel;
    private DictionaryModel calculatedModel;
    private Model model;

    public PhasePatch(Model model, BoundaryTypePanel parent) {
        super(parent);
        this.model = model;
    }

    @Override
    protected void init() {
        fixedModel = new DictionaryModel();
        inletOutletModel = new DictionaryModel();
        zeroGradientModel = new DictionaryModel();
        calculatedModel = new DictionaryModel();
    }

    @Override
    public void stateChanged(Model model) {
        resetToDefault(model);
    }

    @Override
    public void materialsChanged(Model model) {
        resetToDefault(model);
    }

    @Override
    public void resetToDefault(Model model) {
        String alphaFieldName = "";
        if (model != null) {
            Materials materials = model.getMaterials();
            String mat1Name = (materials != null && materials.size() > 0) ? materials.get(0).getName() : "";
            alphaFieldName = Fields.ALPHA + "." + mat1Name;
        } else {
            alphaFieldName = Fields.ALPHA;
        }

        fixedModel.setDictionary(new Dictionary(alphaFieldName, fixedValueVelocity));
        inletOutletModel.setDictionary(new Dictionary(alphaFieldName, inletOutlet));
        zeroGradientModel.setDictionary(new Dictionary(alphaFieldName, zeroGradient));
        calculatedModel.setDictionary(new Dictionary(alphaFieldName, calculated));
    }

    @Override
    public void populatePanel() {
        resetToDefault(null);
        builder.startChoice("Type");
        buildFixedValues();
        buildInletOutlet();
        buildZeroGradient();
        buildCalculated();
        builder.endChoice();
    }

    private void buildFixedValues() {
        builder.startDictionary(FIXED_VALUE_LABEL, fixedModel);
        builder.addComponent("Value", fixedModel.bindUniformDouble("value", 0.0, 1.0));
        builder.endDictionary();
    }

    private void buildInletOutlet() {
        builder.startDictionary(INLET_OUTLET_LABEL, inletOutletModel);
        builder.addComponent("Inlet Value", inletOutletModel.bindUniformDouble("inletValue", 0.0, 1.0));
        builder.endDictionary();
    }

    private void buildZeroGradient() {
        builder.startDictionary(ZERO_GRADIENT_LABEL, zeroGradientModel);
        builder.endDictionary();
    }

    private void buildCalculated() {
        builder.startDictionary(CALCULATED_LABEL, calculatedModel);
        builder.endDictionary();
    }

    @Override
    public void loadFromBoundaryConditions(String patchName, BoundaryConditions bc) {
        Dictionary dictionary = bc.getPhase();
        if (model.getState().getMultiphaseModel().isMultiphase()) {
            String alphaField = Fields.ALPHA + "." + model.getMaterials().getFirstMaterialName();
            Dictionary alphaDict = dictionary.subDict(alphaField);
            if (alphaDict != null) {
            	builder.selectDictionary(alphaDict);
            }
        }
    }

}
