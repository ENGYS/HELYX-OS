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

package eu.engys.core.project.state;

import static eu.engys.core.project.constant.TurbulenceProperties.FIELD_MAPS_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.LES;
import static eu.engys.core.project.constant.TurbulenceProperties.RAS;
import static eu.engys.core.project.constant.TurbulenceProperties.TURBULENCE_PROPERTIES;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.constant.ConstantFolder;
import eu.engys.core.project.defaults.DefaultsProvider;

public class TurbulenceBuilder {

    private static final Logger logger = LoggerFactory.getLogger(TurbulenceBuilder.class);

    public static void saveDefaultsToProject(Model model, DefaultsProvider defaults) {
        State state = model.getState();
        openFOAMProject project = model.getProject();

        Dictionary turbPropDict = new Dictionary(TURBULENCE_PROPERTIES);
        String turbType = RAS;
        if (state.isLES()) {
            turbType = LES;
        }
        turbPropDict.add("simulationType", turbType + "Model");

        if (state.getTurbulenceModel() != null) {
            String modelName = state.getTurbulenceModel().getName();

            Dictionary turbTypeProp = new Dictionary(turbType + "Properties");
            turbTypeProp.add(turbType + "Model", modelName);
            turbTypeProp.add("turbulence", "on");
            turbTypeProp.add("printCoeffs", "on");

            String dictName = "";
            if (state.getSolverType().isCoupled()) {
                dictName = "coupledIncompressibleRAS";
            } else if (state.getSolverType().isSegregated()) {
                dictName = (state.isCompressible() ? "compressible" : "incompressible") + turbType;
            }

            Dictionary tpp = defaults.getDefaultTurbulenceProperties();
            if (tpp != null && tpp.isDictionary(dictName)) {
                logger.info("[ {} provider ]: FOUND {} dictionary",  defaults.getName(), dictName);
                Dictionary subDict = tpp.subDict(dictName);

                if (subDict.isDictionary(modelName + "Coeffs")) {
                    // prendo i coefficienti del model dal file dei defaults
                    logger.info("[ {} provider ]: FOUND {} dictionary",  defaults.getName(), modelName);
                    Dictionary defCoeff = subDict.subDict(modelName + "Coeffs");

                    turbTypeProp.add(new Dictionary(defCoeff));
                    turbTypeProp.remove(FIELD_MAPS_KEY);
                } else {
                    logger.warn("[ {} provider ]: Cannot find {} dictionary",  defaults.getName(), modelName);
                }
            } else {
                logger.warn("[ {} provider ]: Cannot find {} dictionary",  defaults.getName(), dictName);
            }

            if (state.isLES()) {
                String deltaType = turbTypeProp.subDict(modelName + "Coeffs").lookup("delta");
                // System.out.println("TurbulenceBuilder.build() delta: "+deltaType);
                turbTypeProp.add("delta", deltaType);
                turbTypeProp.subDict(modelName + "Coeffs").remove("delta");

                turbTypeProp.add(tpp.subDict(deltaType + "Coeffs"));
            }

            ConstantFolder constantFolder = project.getConstantFolder();
            constantFolder.setTurbulenceProperties(turbPropDict);

            if (state.isLES()) {
                constantFolder.setLESProperties(turbTypeProp);
            } else {
                constantFolder.setRASProperties(turbTypeProp);
            }
        } else {
            logger.error("Turbulence Model is NULL!");
        }

    }
}
