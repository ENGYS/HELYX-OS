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
package eu.engys.core.project.state;

import static eu.engys.core.project.constant.TurbulenceProperties.DELTA1_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.DELTA_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.FIELD_MAPS_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.LAMINAR_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.LES_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.PRINT_COEFFS_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.RAS_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.SIMULATION_TYPE_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.TURBULENCE_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.TURBULENCE_PROPERTIES;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FieldElement;
import eu.engys.core.project.Model;
import eu.engys.core.project.TurbulenceModel;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.defaults.DefaultsProvider;

public class TurbulenceBuilder {

    private static final Logger logger = LoggerFactory.getLogger(TurbulenceBuilder.class);

    public static void saveDefaultsToProject(Model model, DefaultsProvider defaults) {
        State state = model.getState();
        openFOAMProject project = model.getProject();

        Dictionary turbulenceProperties = new Dictionary(TURBULENCE_PROPERTIES);
        if (state.getTurbulenceModel() != null) {
            String simulationType = state.isLES() ? LES_KEY : RAS_KEY;
            if (state.getTurbulenceModel().getName().equals(LAMINAR_KEY)) {
                turbulenceProperties.add(SIMULATION_TYPE_KEY, LAMINAR_KEY);
            } else {
                turbulenceProperties.add(SIMULATION_TYPE_KEY, simulationType);
            }
            Dictionary simulationDict = saveSimulationDict(defaults, state, simulationType);
            turbulenceProperties.add(simulationDict);
        }
        project.getConstantFolder().setTurbulenceProperties(turbulenceProperties);

    }

    private static Dictionary saveSimulationDict(DefaultsProvider defaults, State state, String simulationType) {
        TurbulenceModel turbulenceModel = state.getTurbulenceModel();
        String modelName = turbulenceModel.getName();

        Dictionary simulationDict = new Dictionary(simulationType);
        simulationDict.add(simulationType + "Model", modelName);

        if (turbulenceModel.getType().isLaminar()) {
            // do nothing
        } else {
            simulationDict.add(TURBULENCE_KEY, "on");
            simulationDict.add(PRINT_COEFFS_KEY, "on");

            String dictName = "";
            if (state.getSolverType().isCoupled()) {
                dictName = (state.isRANS() ? "coupledIncompressibleRAS" : "coupledIncompressibleLES");
            } else if (state.getSolverType().isSegregated()) {
                dictName = (state.isCompressible() ? "compressible" : "incompressible") + simulationType;
            }

            Dictionary tpp = defaults.getDefaultTurbulenceProperties();
            if (tpp != null && tpp.isDictionary(dictName)) {
                logger.info("[ {} provider ]: FOUND {} dictionary", defaults.getName(), dictName);
                Dictionary subDict = tpp.subDict(dictName);

                if (subDict.isDictionary(modelName + "Coeffs")) {
                    // prendo i coefficienti del model dal file dei defaults
                    logger.info("[ {} provider ]: FOUND {} dictionary", defaults.getName(), modelName);
                    Dictionary defCoeff = subDict.subDict(modelName + "Coeffs");

                    simulationDict.add(new Dictionary(defCoeff));
                    simulationDict.remove(FIELD_MAPS_KEY);
                } else {
                    logger.warn("[ {} provider ]: Cannot find {} dictionary", defaults.getName(), modelName);
                }
            } else {
                logger.warn("[ {} provider ]: Cannot find {} dictionary", defaults.getName(), dictName);
            }

            if (state.isLES() && simulationDict.found(modelName + "Coeffs")) {
                Dictionary coeffsDict = simulationDict.subDict(modelName + "Coeffs");
                if(coeffsDict.found(DELTA_KEY) && coeffsDict.found(DELTA1_KEY)){
                    FieldElement deltaType = (FieldElement) coeffsDict.remove(DELTA_KEY);
                    FieldElement delta1Type = (FieldElement) coeffsDict.remove(DELTA1_KEY);
                    simulationDict.add(DELTA_KEY, delta1Type.getValue());
                    simulationDict.add(tpp.subDict(deltaType.getValue() + "Coeffs"));
                    simulationDict.add(tpp.subDict(delta1Type.getValue() + "Coeffs"));
                } else if(coeffsDict.found(DELTA_KEY)){
                    FieldElement deltaType = (FieldElement) coeffsDict.remove(DELTA_KEY);
                    simulationDict.add(DELTA_KEY, deltaType.getValue());
                    simulationDict.add(tpp.subDict(deltaType.getValue() + "Coeffs"));
                }
            }
        }
        return simulationDict;

    }
}
