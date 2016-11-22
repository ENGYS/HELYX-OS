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
package eu.engys.standardVOF;

import static eu.engys.core.project.constant.ThermophysicalProperties.MATERIAL_NAME_KEY;
import static eu.engys.core.project.constant.TransportProperties.PHASES_KEY;
import static eu.engys.core.project.constant.TransportProperties.SIGMA_KEY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.constant.ConstantFolder;
import eu.engys.core.project.constant.TransportProperties;
import eu.engys.core.project.materials.Material;
import eu.engys.core.project.materials.Materials;
import eu.engys.gui.casesetup.materials.StandardMaterialsReader;

public class StandardVOFReader {

    private static final Logger logger = LoggerFactory.getLogger(StandardVOFReader.class);

    private Model model;
    private StandardVOFModule module;

    private TransportProperties transportProperties;

    public StandardVOFReader(Model model, StandardVOFModule module) {
        this.model = model;
        this.module = module;
    }

    public void loadState() {
        if (isMultiphaseVOF()) {
            model.getState().setMultiphaseModel(StandardVOFModule.VOF_MODEL);
            model.getState().setPhases(2);
        }
    }

    public void loadMaterials() {
        if (isMultiphaseVOF()) {
            if (model.getState().isIncompressible()) {
                readIncompressibleMaterials();
            } else {
                readCompressibleMaterials();
            }
        }
    }

    private boolean isMultiphaseVOF() {
        ConstantFolder constantFolder = model.getProject().getConstantFolder();
        TransportProperties transportProperties = constantFolder.getTransportProperties();
        if (transportProperties != null) {
            if ((transportProperties.found(PHASES_KEY))) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void readIncompressibleMaterials() {
        Materials materials = model.getMaterials();
        ConstantFolder constantFolder = model.getProject().getConstantFolder();
        transportProperties = constantFolder.getTransportProperties();

        if (transportProperties.found(PHASES_KEY)) {
            model.getState().setPhases(2);

            String phases = transportProperties.lookup(PHASES_KEY).replaceAll("\\(", "").replaceAll("\\)", "").trim();

            Dictionary dict1 = new Dictionary(transportProperties.subDict(phases.split(" ")[0]));
            if (!dict1.isEmpty()) {
                if (!dict1.found(MATERIAL_NAME_KEY)) {
                    dict1.add(MATERIAL_NAME_KEY, "material1");
                }
                String name1 = dict1.lookup(MATERIAL_NAME_KEY);
                dict1.setName(name1);
                Material m1 = new StandardMaterialsReader().readIncompressibleMaterial(dict1);
                materials.add(m1);
            }

            Dictionary dict2 = new Dictionary(transportProperties.subDict(phases.split(" ")[1]));
            if (!dict2.isEmpty()) {
                if (!dict2.found(MATERIAL_NAME_KEY)) {
                    dict2.add(MATERIAL_NAME_KEY, "material2");
                }
                String name2 = dict2.lookup(MATERIAL_NAME_KEY);
                dict2.setName(name2);
                Material m2 = new StandardMaterialsReader().readIncompressibleMaterial(dict2);
                materials.add(m2);
            }

            if (transportProperties.found(SIGMA_KEY)) {
                double sigma = transportProperties.lookupScalar(SIGMA_KEY).doubleValue();
                module.setSigma(sigma);
            } else if (dict1.found(SIGMA_KEY)) {
                double sigma = dict1.lookupScalar(SIGMA_KEY).doubleValue();
                module.setSigma(sigma);
            } else if (dict2.found(SIGMA_KEY)) {
                double sigma = dict2.lookupScalar(SIGMA_KEY).doubleValue();
                module.setSigma(sigma);
            }

            model.materialsChanged();

        } else {
            logger.warn("Multiphase case but no phases found in transportProperties");
        }
    }

    public void readCompressibleMaterials() {
        logger.error("Multiphase Compressible not supported");
    }

}
