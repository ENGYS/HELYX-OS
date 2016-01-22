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


package eu.engys.gui.casesetup.solver.panels;

import static eu.engys.core.project.system.FvSolution.N_NON_ORTHOGONAL_CORRECTORS_KEY;
import static eu.engys.core.project.system.FvSolution.RELAXATION_FACTORS_KEY;
import static eu.engys.core.project.system.FvSolution.RESIDUAL_CONTROL_KEY;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.SolverFamily;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.gui.casesetup.solver.SolverSettingsBuilder;

public class SimpleSettingsPanel implements SolverPanel {

    private DictionaryPanelBuilder builder;

    private DictionaryModel simpleDictModel;
    private DictionaryModel relaxationFactorsDictModel;
    private DictionaryModel residualControlDict;

    private JPanel relaxationFactorsPanel;
    private JPanel residualControlPanel;

    public SimpleSettingsPanel() {
        relaxationFactorsDictModel = new DictionaryModel(new Dictionary(RELAXATION_FACTORS_KEY));
        residualControlDict = new DictionaryModel(new Dictionary(RESIDUAL_CONTROL_KEY));

        simpleDictModel = new DictionaryModel(new Dictionary(getKey()));

        builder = new DictionaryPanelBuilder();
        builder.addComponent(NON_ORTHOGONAL_CORRECTORS_LABEL, simpleDictModel.bindIntegerPositive(N_NON_ORTHOGONAL_CORRECTORS_KEY));

        residualControlPanel = new JPanel(new BorderLayout());
        residualControlPanel.setOpaque(false);
        residualControlPanel.setBorder(BorderFactory.createTitledBorder(RESIDUAL_CONTROL_LABEL));
        residualControlPanel.setName(RESIDUAL_CONTROL_LABEL);
        builder.addFill(residualControlPanel);

        relaxationFactorsPanel = new JPanel(new BorderLayout());
        relaxationFactorsPanel.setOpaque(false);
        relaxationFactorsPanel.setBorder(BorderFactory.createTitledBorder(RELAXATION_FACTORS_LABEL));
        relaxationFactorsPanel.setName(RELAXATION_FACTORS_LABEL);
        builder.addFill(relaxationFactorsPanel);
    }

    @Override
    public String getKey() {
        return SolverFamily.SIMPLE.getKey();
    }

    @Override
    public JPanel getPanel() {
        return builder.removeMargins().getPanel();
    }

    @Override
    public Dictionary getSolverDictionary() {
        return simpleDictModel.getDictionary();
    }

    @Override
    public Dictionary getRelaxationFactorsDictionary() {
        return relaxationFactorsDictModel.getDictionary();
    }

    @Override
    public Dictionary getResidualControlDictionary() {
        return residualControlDict.getDictionary();
    }

    @Override
    public void load(Model model) {
        residualControlPanel.removeAll();
        DictionaryPanelBuilder residualBuilder = new DictionaryPanelBuilder();
        residualControlPanel.add(residualBuilder.getPanel());

        relaxationFactorsPanel.removeAll();
        DictionaryPanelBuilder relaxationBuilder = new DictionaryPanelBuilder();
        relaxationFactorsPanel.add(relaxationBuilder.getPanel());

        Fields fields = model.getFields();

        Dictionary fvSolution = model.getProject().getSystemFolder().getFvSolution();
        
        
        if (fvSolution != null) {
            Dictionary SIMPLEDict = fvSolution.subDict(getKey());
            simpleDictModel.setDictionary(SIMPLEDict != null ? new Dictionary(SIMPLEDict) : new Dictionary(getKey()));
            
//            System.out.println("SimpleSettingsPanel.load(): " + fields.orderedFieldsExcludingPassiveScalars());

            for (Field field : fields.orderedFieldsExcludingPassiveScalars()) {
                String fieldName = field.getName();
                residualBuilder.addComponent(fieldName, residualControlDict.bindDouble(fieldName, 0.0, 1.0));
            }

            for (Field field : fields.orderedFields()) {
                String fieldName = field.getName();
                relaxationBuilder.addComponent(fieldName, relaxationFactorsDictModel.bindDouble(fieldName, 0.0, 1.0));
            }

            if (SIMPLEDict != null && SIMPLEDict.found(RESIDUAL_CONTROL_KEY)) {
                residualControlDict.setDictionary(SIMPLEDict.subDict(RESIDUAL_CONTROL_KEY));
            }
            if (fvSolution.found(RELAXATION_FACTORS_KEY)) {
                Dictionary relaxationFactors = fvSolution.subDict(RELAXATION_FACTORS_KEY);
                Dictionary relaxationFactorsForGUI = SolverSettingsBuilder.decodeRelaxationFactorsForGUI(model, relaxationFactors);
                relaxationFactorsDictModel.setDictionary(relaxationFactorsForGUI);
            }
        }
    }

}
