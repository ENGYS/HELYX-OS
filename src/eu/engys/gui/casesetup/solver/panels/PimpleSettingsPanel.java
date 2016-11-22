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

import static eu.engys.core.project.system.ControlDict.MAX_ALPHA_CO_KEY;
import static eu.engys.core.project.system.ControlDict.MAX_CO_KEY;
import static eu.engys.core.project.system.FvSolution.FV_SOLUTION;
import static eu.engys.core.project.system.FvSolution.N_CORRECTORS_KEY;
import static eu.engys.core.project.system.FvSolution.N_NON_ORTHOGONAL_CORRECTORS_KEY;
import static eu.engys.core.project.system.FvSolution.N_OUTER_CORRECTORS_KEY;
import static eu.engys.core.project.system.FvSolution.RELAXATION_FACTORS_KEY;
import static eu.engys.core.project.system.FvSolution.REL_TOLERANCE_KEY;
import static eu.engys.core.project.system.FvSolution.RESIDUAL_CONTROL_KEY;
import static eu.engys.core.project.system.FvSolution.RHO_MAX_KEY;
import static eu.engys.core.project.system.FvSolution.RHO_MIN_KEY;
import static eu.engys.core.project.system.FvSolution.TOLERANCE_KEY;
import static eu.engys.core.project.system.SystemFolder.SYSTEM;
import static eu.engys.core.project.zero.fields.Fields.FINAL;
import static eu.engys.core.project.zero.fields.Fields.P;
import static eu.engys.core.project.zero.fields.Fields.P_RGH;
import static eu.engys.core.project.zero.fields.Fields.RHO;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.SolverFamily;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.gui.casesetup.solver.SolverSettingsBuilder;
import eu.engys.util.DimensionalUnits;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;

public class PimpleSettingsPanel implements SolverPanel {

    public static final String COURANT_NUMBER_LABEL = "Courant Number";
    public static final String MAX_COURANT_ALPHA_LABEL = "Max Courant Alpha";
    public static final String MAX_COURANT_NUMBER_LABEL = "Max Courant Number";

    private DictionaryModel pimpleDictModel;

    private DictionaryModel relaxationFactorsDictModel;
    private Map<String, DictionaryModel> pimpleResidualMap;

    private JPanel relaxationFactorsPanel;
    private JPanel residualControlPanel;

    private PanelBuilder builder;

    private JComponent pimpleRhoMin;
    private JComponent pimpleRhoMax;

    private JComponent maxCourantNumber;
    private JComponent maxAlphaCourant;
    private JPanel mainPanel;

    public PimpleSettingsPanel() {
        relaxationFactorsDictModel = new DictionaryModel(new Dictionary(RELAXATION_FACTORS_KEY));
        pimpleResidualMap = new HashMap<>();

        pimpleDictModel = new DictionaryModel(new Dictionary(getKey()));

        builder = new PanelBuilder();
        builder.addComponent(OUTER_CORRECTORS_LABEL, pimpleDictModel.bindIntegerPositive(N_OUTER_CORRECTORS_KEY));
        builder.addComponent(CORRECTORS_LABEL, pimpleDictModel.bindIntegerPositive(N_CORRECTORS_KEY));
        builder.addComponent(NON_ORTHOGONAL_CORRECTORS_LABEL, pimpleDictModel.bindIntegerPositive(N_NON_ORTHOGONAL_CORRECTORS_KEY));
        pimpleRhoMin = builder.addComponent(RHO_MIN_LABEL, pimpleDictModel.bindDimensionedDouble(RHO_MIN_KEY, DimensionalUnits.KG_M3));
        pimpleRhoMax = builder.addComponent(RHO_MAX_LABEL, pimpleDictModel.bindDimensionedDouble(RHO_MAX_KEY, DimensionalUnits.KG_M3));
        pimpleRhoMin.setEnabled(false);
        pimpleRhoMax.setEnabled(false);

        PanelBuilder courantBuilder = new PanelBuilder();
        maxCourantNumber = courantBuilder.addComponent(MAX_COURANT_NUMBER_LABEL, pimpleDictModel.bindDouble(MAX_CO_KEY));
        maxAlphaCourant = courantBuilder.addComponent(MAX_COURANT_ALPHA_LABEL, pimpleDictModel.bindDouble(MAX_ALPHA_CO_KEY));
        maxCourantNumber.setEnabled(false);
        maxAlphaCourant.setEnabled(false);
        courantBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(COURANT_NUMBER_LABEL));
        courantBuilder.getPanel().setName(COURANT_NUMBER_LABEL);
        builder.addFill(courantBuilder.getPanel());
        
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
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(builder.removeMargins().getPanel(), BorderLayout.NORTH);
    }

    @Override
    public String getKey() {
        return SolverFamily.PIMPLE.key();
    }

    @Override
    public Dictionary getSolverDictionary() {
        return pimpleDictModel.getDictionary();
    }

    @Override
    public Dictionary getRelaxationFactorsDictionary() {
        return relaxationFactorsDictModel.getDictionary();
    }

    @Override
    public Dictionary getResidualControlDictionary() {
        return getPimpleResidualDictionary();
    }

    @Override
    public JPanel getPanel() {
        return mainPanel;
    }

    @Override
    public void load(Model model) {
        residualControlPanel.removeAll();
        DictionaryPanelBuilder residualBuilder = new DictionaryPanelBuilder();
        residualControlPanel.add(residualBuilder.getPanel());

        relaxationFactorsPanel.removeAll();
        DictionaryPanelBuilder relaxationBuilder = new DictionaryPanelBuilder();
        relaxationFactorsPanel.add(relaxationBuilder.getPanel());

        pimpleResidualMap.clear();

        Fields fields = model.getFields();

        Dictionary fvSolution = model.getProject().getSystemFolder().getFvSolution();
        if (fvSolution != null) {
            Dictionary PIMPLEDict = fvSolution.subDict(getKey());
            pimpleDictModel.setDictionary(PIMPLEDict != null ? new Dictionary(PIMPLEDict) : getDictionaryFromDefaults(model));

            residualBuilder.addComponent("", new JLabel(RELATIVE_TOLERANCE_LABEL), new JLabel(TOLERANCE_LABEL));
            for (Field field : fields.orderedFieldsExcludingPassiveScalars()) {
                String fieldName = field.getName();
                DictionaryModel fieldModel = new DictionaryModel(new Dictionary(fieldName));
                DoubleField relativeTolerance = fieldModel.bindDouble(REL_TOLERANCE_KEY, 0.0, 1.0);
                DoubleField tolerance = fieldModel.bindDouble(TOLERANCE_KEY, 0.0, 1.0);
                pimpleResidualMap.put(fieldName, fieldModel);
                residualBuilder.addComponent(fieldName, relativeTolerance, tolerance);
            }
            for (Field field : fields.orderedFields()) {
                String fieldName = field.getName();

                DoubleField normalField = relaxationFactorsDictModel.bindDouble(fieldName, 0.0, 1.0);
                normalField.setName(fieldName);

                JComponent finalField = null;
                JLabel finalLabel = null;
                if (P_RGH.equals(fieldName) || P.equals(fieldName) || RHO.equals(fieldName)) {
                    finalLabel = new JLabel("");
                    finalField = new JLabel("");
                } else {
                    finalLabel = new JLabel(fieldName + FINAL);
                    finalField = relaxationFactorsDictModel.bindDouble(fieldName + FINAL, 0.0, 1.0);
                }
                finalField.setName(fieldName + FINAL);
                relaxationBuilder.addComponent(fieldName, normalField, finalLabel, finalField);
            }

            if (PIMPLEDict != null && PIMPLEDict.found(RESIDUAL_CONTROL_KEY)) {
                Dictionary residualControlDict = PIMPLEDict.subDict(RESIDUAL_CONTROL_KEY);
                for (String key : pimpleResidualMap.keySet()) {
                    pimpleResidualMap.get(key).setDictionary(residualControlDict.subDict(key));
                }
            }

            if (fvSolution.found(RELAXATION_FACTORS_KEY)) {
                Dictionary relaxationFactors = fvSolution.subDict(RELAXATION_FACTORS_KEY);
                Dictionary relaxationFactorsForGUI = SolverSettingsBuilder.decodeRelaxationFactorsForGUI(model, relaxationFactors);
                relaxationFactorsDictModel.setDictionary(relaxationFactorsForGUI);
            }

        }
        updatePanel(model);
    }

    private Dictionary getDictionaryFromDefaults(Model model) {
        Dictionary stateData = model.getDefaults().getDefaultStateData();
        Dictionary pimpleSolution = stateData.subDict("pimpleFoamRAS").subDict(SYSTEM).subDict(FV_SOLUTION);
        return pimpleSolution.subDict(getKey());
    }

    private void updatePanel(Model model) {
        boolean isLTS = model.getState().isSteady() && model.getState().getMultiphaseModel().isMultiphase();
        maxAlphaCourant.setEnabled(isLTS);
        maxCourantNumber.setEnabled(isLTS);

        pimpleRhoMin.setEnabled(model.getState().isCompressible());
        pimpleRhoMax.setEnabled(model.getState().isCompressible());
    }

    private Dictionary getPimpleResidualDictionary() {
        Dictionary dictionary = new Dictionary(RESIDUAL_CONTROL_KEY);
        for (DictionaryModel dm : pimpleResidualMap.values()) {
            dictionary.add(dm.getDictionary());
        }
        return dictionary;
    }

}
