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

import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.BOUNDARY_CONDITION;
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.BOUNDARY_CONDITION_VECTOR;
import static eu.engys.gui.casesetup.boundaryconditions.factories.CyclicFactory.CYCLIC;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.inject.Inject;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.modules.boundaryconditions.ParametersPanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Field.FieldType;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.core.project.zero.patches.BoundaryType;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.gui.ListBuilderFactory;
import eu.engys.util.ui.builder.PanelBuilder;

public class CyclicSettingsPanel extends JPanel implements BoundaryTypePanel {

    private DictionaryModel cyclicModel;

    private Model model;

    @Inject
    public CyclicSettingsPanel(Model model) {
        super(new BorderLayout());
        this.model = model;
        this.cyclicModel = new DictionaryModel();
    }

    @Override
    public void resetToDefault() {
        cyclicModel.setDictionary(new Dictionary(CYCLIC));
    }

    @Override
    public void layoutPanel() {
        PanelBuilder builder = new PanelBuilder();
        cyclicModel.setDictionary(new Dictionary(CYCLIC));
        builder.addComponent("Match Tolerance", cyclicModel.bindDouble("matchTolerance"));
        builder.addComponent("Neighbour Patch", cyclicModel.bindSelection("neighbourPatch", ListBuilderFactory.getPatchesListBuilder(model)));
        add(builder.getPanel());
    }

    @Override
    public BoundaryType getType() {
        return BoundaryType.CYCLIC;
    }

    @Override
    public Component getPanel() {
        return this;
    }

    @Override
    public void loadFromPatches(Patch... patches) {
        if (patches.length == 1) {
            Dictionary dictionary = patches[0].getDictionary();
            if (dictionary.found(Dictionary.TYPE) && dictionary.lookup(Dictionary.TYPE).equals("cyclic")) {
                cyclicModel.setDictionary(dictionary);
            } else {
                cyclicModel.setDictionary(new Dictionary(CYCLIC));
            }
        }
    }

    @Override
    public void saveToPatch(Patch patch) {
        Dictionary newDict = cyclicModel.getDictionary();

        patch.setBoundaryConditions(getBoundaryConditions());
        patch.getDictionary().merge(newDict);
    }

    private BoundaryConditions getBoundaryConditions() {
        BoundaryConditions boundaryConditions = new BoundaryConditions();
        for (Field field : model.getFields().values()) {
            if (field.getFieldType() == FieldType.SCALAR) {
                boundaryConditions.add(field.getName(), new Dictionary(BOUNDARY_CONDITION));
            } else {
                boundaryConditions.add(field.getName(), new Dictionary(BOUNDARY_CONDITION_VECTOR));
            }
        }
        return boundaryConditions;
    }

    @Override
    public void stateChanged() {
    }

    @Override
    public void materialsChanged() {
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

    @Override
    public void addPanel(String name, ParametersPanel pPanel) {
    }

	@Override
	public void addPanel(String name, ParametersPanel pPanel, int index) {
	}
}
