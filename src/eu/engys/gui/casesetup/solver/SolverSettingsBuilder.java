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
package eu.engys.gui.casesetup.solver;

import static eu.engys.core.project.system.ControlDict.MAX_ALPHA_CO_KEY;
import static eu.engys.core.project.system.ControlDict.MAX_CO_KEY;
import static eu.engys.core.project.system.FvSolution.EQUATIONS_KEY;
import static eu.engys.core.project.system.FvSolution.FIELDS_KEY;
import static eu.engys.core.project.system.FvSolution.N_CORRECTORS_KEY;
import static eu.engys.core.project.system.FvSolution.N_NON_ORTHOGONAL_CORRECTORS_KEY;
import static eu.engys.core.project.system.FvSolution.N_OUTER_CORRECTORS_KEY;
import static eu.engys.core.project.system.FvSolution.RELAXATION_FACTORS_KEY;
import static eu.engys.core.project.system.FvSolution.RESIDUAL_CONTROL_KEY;
import static eu.engys.core.project.system.FvSolution.RHO_MAX_KEY;
import static eu.engys.core.project.system.FvSolution.RHO_MIN_KEY;
import static eu.engys.core.project.system.FvSolution.SONIC_KEY;
import static eu.engys.core.project.zero.fields.Fields.FINAL;
import static eu.engys.core.project.zero.fields.Fields.P;
import static eu.engys.core.project.zero.fields.Fields.P_RGH;
import static eu.engys.core.project.zero.fields.Fields.RHO;

import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FieldElement;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.gui.casesetup.solver.panels.SolverPanel;

public class SolverSettingsBuilder {

    public static void build(Model model, SolverPanel solverPanel) {
        Dictionary fvSolution = model.getProject().getSystemFolder().getFvSolution();
        Dictionary solverDictionary = solverPanel.getSolverDictionary();
        Dictionary relaxationDictionary = solverPanel.getRelaxationFactorsDictionary();
        Dictionary residualDictionary = solverPanel.getResidualControlDictionary();

        buildSolverSection(fvSolution, solverDictionary, residualDictionary);
        buildRelaxationFactorsSection(fvSolution, relaxationDictionary);

        if (model.getState().isHighMach()) {
            fvSolution.add(SONIC_KEY, "true");
        } else if (fvSolution.found(SONIC_KEY)) {
            fvSolution.remove(SONIC_KEY);
        }
    }

    private static void buildSolverSection(Dictionary fvSolution, Dictionary solverDictionary, Dictionary residualControl) {
        Dictionary solver = fvSolution.subDict(solverDictionary.getName()); // SIMPLE-PIMPLEorPISO
        if (solver != null) {
            if (solverDictionary.found(N_NON_ORTHOGONAL_CORRECTORS_KEY)) {
                solver.add(N_NON_ORTHOGONAL_CORRECTORS_KEY, solverDictionary.lookup(N_NON_ORTHOGONAL_CORRECTORS_KEY));
            }
            if (solverDictionary.found(N_CORRECTORS_KEY)) {
                solver.add(N_CORRECTORS_KEY, solverDictionary.lookup(N_CORRECTORS_KEY));
            }
            if (solverDictionary.found(N_OUTER_CORRECTORS_KEY)) {
                solver.add(N_OUTER_CORRECTORS_KEY, solverDictionary.lookup(N_OUTER_CORRECTORS_KEY));
            }
            if (solverDictionary.found(RHO_MIN_KEY)) {
                solver.add(solverDictionary.lookupScalar(RHO_MIN_KEY));
            }
            if (solverDictionary.found(RHO_MAX_KEY)) {
                solver.add(solverDictionary.lookupScalar(RHO_MAX_KEY));
            }
            if (solverDictionary.found(MAX_CO_KEY)) {
                solver.add(MAX_CO_KEY, solverDictionary.lookupString(MAX_CO_KEY));
            }
            if (solverDictionary.found(MAX_ALPHA_CO_KEY)) {
                solver.add(MAX_ALPHA_CO_KEY, solverDictionary.lookupString(MAX_ALPHA_CO_KEY));
            }
            if (residualControl != null) {
                if (solver.found(RESIDUAL_CONTROL_KEY)) {
                    solver.subDict(RESIDUAL_CONTROL_KEY).merge(residualControl);
                } else {
                    solver.add(residualControl);
                }
            } else {
                if (solver.found(RESIDUAL_CONTROL_KEY)) {
                    solver.remove(RESIDUAL_CONTROL_KEY);
                }
            }
        }
    }

    private static void buildRelaxationFactorsSection(Dictionary fvSolution, Dictionary relFactorsDictionaryForGUI) {
        Dictionary relaxationFactors = fvSolution.subDict(RELAXATION_FACTORS_KEY);
        if (relaxationFactors != null) {
            relaxationFactors.merge(encodeRelaxactionFactorsForSaving(relFactorsDictionaryForGUI));
        }
    }

    private static Dictionary encodeRelaxactionFactorsForSaving(Dictionary relFactorsDictionaryForGUI) {
        Dictionary relFactorsDict = new Dictionary(RELAXATION_FACTORS_KEY);
        Dictionary fieldsDict = new Dictionary(FIELDS_KEY);
        Dictionary equationsDict = new Dictionary(EQUATIONS_KEY);
        relFactorsDict.add(fieldsDict);
        relFactorsDict.add(equationsDict);

        for (FieldElement field : relFactorsDictionaryForGUI.getFields()) {
            String name = field.getName();
            if (goesToFieldSection(name)) {
                fieldsDict.add(name, relFactorsDictionaryForGUI.lookup(name));
            } else {
                equationsDict.add(name, relFactorsDictionaryForGUI.lookup(name));
            }
        }
        return relFactorsDict;
    }

    private static boolean goesToFieldSection(String name) {
        boolean isP = name.equals(P) || name.equals(P + FINAL);
        boolean isPrgh = name.equals(P_RGH) || name.equals(P_RGH + FINAL);
        boolean isRho = name.equals(RHO) || name.equals(RHO + FINAL);
        return isP || isPrgh || isRho;
    }

    /**
     * li mette tutti in un unico dictionary
     */
    public static Dictionary decodeRelaxationFactorsForGUI(Model model, Dictionary relaxationFactorsDict) {
        if (relaxationFactorsDict.found(FIELDS_KEY)) { // formato nuovo
            Dictionary fieldsDict = relaxationFactorsDict.subDict(FIELDS_KEY);
            Dictionary equationsDict = relaxationFactorsDict.subDict(EQUATIONS_KEY);
            Dictionary relFactorsDictionaryForGUI = new Dictionary(RELAXATION_FACTORS_KEY);
            for (FieldElement field : fieldsDict.getFields()) {
                String name = field.getName();
                relFactorsDictionaryForGUI.add(name, fieldsDict.lookup(name));
            }
            for (FieldElement field : equationsDict.getFields()) {
                String name = field.getName();
                relFactorsDictionaryForGUI.add(name, equationsDict.lookup(name));
            }
            fixAlphas(model, relFactorsDictionaryForGUI);
            return relFactorsDictionaryForGUI;
        } else { // ho il vecchio formato
            return new Dictionary(relaxationFactorsDict);
        }
    }

    private static void fixAlphas(Model model, Dictionary relFactorsDictionaryForGUI) {
        for (FieldElement field : relFactorsDictionaryForGUI.getFields()) {
            String name = field.getName();
            if (model.getState().getMultiphaseModel().isMultiphase()) {
                if (model.getFields().containsKey(Fields.ALPHA_1)) {
                    fixAlphasForVOF(model, relFactorsDictionaryForGUI, name);
                } else {
                    fixAlphasForEuler(model, relFactorsDictionaryForGUI, name);
                }
            }

        }
    }

    private static void fixAlphasForVOF(Model model, Dictionary relFactorsDictionaryForGUI, String name) {
        if (name.equals("\"" + Fields.ALPHA + ".*\"")) {
            DefaultElement alpha = relFactorsDictionaryForGUI.remove(name);
            alpha.setName(Fields.ALPHA_1);
            relFactorsDictionaryForGUI.add(alpha);
        } else if (name.equals("\"" + Fields.ALPHA + ".*Final\"")) {
            DefaultElement alpha = relFactorsDictionaryForGUI.remove(name);
            alpha.setName(Fields.ALPHA_1 + "Final");
            relFactorsDictionaryForGUI.add(alpha);
        }
    }

    private static void fixAlphasForEuler(Model model, Dictionary relFactorsDictionaryForGUI, String name) {
        if (name.equals("\"" + Fields.ALPHA + ".*\"")) {
            DefaultElement alpha = relFactorsDictionaryForGUI.remove(name);
            alpha.setName(Fields.ALPHA + "." + model.getMaterials().getFirstMaterialName());
            relFactorsDictionaryForGUI.add(alpha);
        } else if (name.equals("\"" + Fields.ALPHA + ".*Final\"")) {
            DefaultElement alpha = relFactorsDictionaryForGUI.remove(name);
            alpha.setName(Fields.ALPHA + "." + model.getMaterials().getFirstMaterialName() + "Final");
            relFactorsDictionaryForGUI.add(alpha);
        }
    }

}
