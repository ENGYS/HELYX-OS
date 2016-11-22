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

package eu.engys.gui.casesetup.solver.panels;

import static eu.engys.core.project.system.FvSolution.FV_SOLUTION;
import static eu.engys.core.project.system.FvSolution.N_CORRECTORS_KEY;
import static eu.engys.core.project.system.FvSolution.N_NON_ORTHOGONAL_CORRECTORS_KEY;
import static eu.engys.core.project.system.FvSolution.RELAXATION_FACTORS_KEY;
import static eu.engys.core.project.system.FvSolution.RESIDUAL_CONTROL_KEY;
import static eu.engys.core.project.system.FvSolution.RHO_MAX_KEY;
import static eu.engys.core.project.system.FvSolution.RHO_MIN_KEY;
import static eu.engys.core.project.system.SystemFolder.SYSTEM;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.SolverFamily;
import eu.engys.core.project.state.State;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.gui.casesetup.solver.SolverSettingsBuilder;
import eu.engys.util.DimensionalUnits;
import eu.engys.util.ui.textfields.DoubleField;

public class PisoSettingsPanel implements SolverPanel {

    private DictionaryModel pisoDictModel;

    private DictionaryModel relaxationFactorsDictModel;
    private JPanel relaxationFactorsPanel;

    private DictionaryPanelBuilder builder;

    private JComponent pisoRhoMin;
    private JComponent pisoRhoMax;

    private JPanel mainPanel;

    public PisoSettingsPanel() {
        relaxationFactorsDictModel = new DictionaryModel(new Dictionary(RELAXATION_FACTORS_KEY));

        pisoDictModel = new DictionaryModel(new Dictionary(getKey()));

        builder = new DictionaryPanelBuilder();
        builder.addComponent(CORRECTORS_LABEL, pisoDictModel.bindIntegerPositive(N_CORRECTORS_KEY));
        builder.addComponent(NON_ORTHOGONAL_CORRECTORS_LABEL, pisoDictModel.bindIntegerPositive(N_NON_ORTHOGONAL_CORRECTORS_KEY));
        pisoRhoMin = builder.addComponent(RHO_MIN_LABEL, pisoDictModel.bindDimensionedDouble(RHO_MIN_KEY, DimensionalUnits.KG_M3));
        pisoRhoMax = builder.addComponent(RHO_MAX_LABEL, pisoDictModel.bindDimensionedDouble(RHO_MAX_KEY, DimensionalUnits.KG_M3));

        relaxationFactorsPanel = new JPanel(new BorderLayout());
        relaxationFactorsPanel.setOpaque(false);
        relaxationFactorsPanel.setBorder(BorderFactory.createTitledBorder(RELAXATION_FACTORS_LABEL));
        relaxationFactorsPanel.setName(RELAXATION_FACTORS_LABEL);
        builder.addFill(relaxationFactorsPanel);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(builder.removeMargins().getPanel(), BorderLayout.NORTH);
    }

    @Override
    public String getKey() {
        return SolverFamily.PISO.key();
    }

    @Override
    public JPanel getPanel() {
        return mainPanel;
    }

    @Override
    public Dictionary getSolverDictionary() {
        return pisoDictModel.getDictionary();
    }

    @Override
    public Dictionary getRelaxationFactorsDictionary() {
        return relaxationFactorsDictModel.getDictionary();
    }

    @Override
    public Dictionary getResidualControlDictionary() {
        return new Dictionary(RESIDUAL_CONTROL_KEY);
    }

    @Override
    public void load(Model model) {
        relaxationFactorsPanel.removeAll();
        DictionaryPanelBuilder relaxationBuilder = new DictionaryPanelBuilder();
        relaxationFactorsPanel.add(relaxationBuilder.getPanel());

        Fields fields = model.getFields();

        Dictionary fvSolution = model.getProject().getSystemFolder().getFvSolution();
        if (fvSolution != null) {
            Dictionary PISODict = fvSolution.subDict(getKey());
            pisoDictModel.setDictionary(PISODict != null ? new Dictionary(PISODict) : getDictionaryFromDefaults(model));

            for (Field field : fields.orderedFields()) {
                String fieldName = field.getName();
                DoubleField textField = relaxationFactorsDictModel.bindDouble(fieldName, 0.0, 1.0);
                textField.setEnabled(false);
                relaxationBuilder.addComponent(fieldName, textField);
            }

            if (fvSolution.found(RELAXATION_FACTORS_KEY)) {
                Dictionary relaxationFactors = fvSolution.subDict(RELAXATION_FACTORS_KEY);
                Dictionary relaxationFactorsForGUI = SolverSettingsBuilder.decodeRelaxationFactorsForGUI(model, relaxationFactors);
                relaxationFactorsDictModel.setDictionary(relaxationFactorsForGUI);
            }

            updatePanel(model);
        }
    }

    private Dictionary getDictionaryFromDefaults(Model model) {
        Dictionary stateData = model.getDefaults().getDefaultStateData();
        Dictionary pisoSolution = stateData.subDict("pisoFoamRAS").subDict(SYSTEM).subDict(FV_SOLUTION);
        return pisoSolution.subDict(getKey());
    }

    protected void updatePanel(Model model) {
        State state = model.getState();
        boolean isCompressible = state.isCompressible();
        pisoRhoMin.setEnabled(isCompressible);
        pisoRhoMax.setEnabled(isCompressible);
    }

}
