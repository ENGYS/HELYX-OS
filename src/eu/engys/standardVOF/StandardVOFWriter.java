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

import static eu.engys.core.project.constant.TransportProperties.PHASES_KEY;
import static eu.engys.core.project.constant.TransportProperties.SIGMA_KEY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DimensionedScalar;
import eu.engys.core.project.Model;
import eu.engys.core.project.constant.ConstantFolder;
import eu.engys.core.project.constant.TransportProperties;
import eu.engys.core.project.materials.Material;
import eu.engys.core.project.materials.Materials;
import eu.engys.gui.casesetup.materials.StandardMaterialsWriter;

public class StandardVOFWriter {

    private static final Logger logger = LoggerFactory.getLogger(StandardVOFWriter.class);

    private Model model;
    private StandardVOFModule module;

    public StandardVOFWriter(Model model, StandardVOFModule module) {
        this.model = model;
        this.module = module;

    }

    public void write() {
        if (module.isVOF()) {
            if (model.getState().isIncompressible()) {
                writeIncompressibleMaterials();
            } else {
                writeCompressibleMaterials();
            }
        }
    }

    private void writeCompressibleMaterials() {
        logger.error("Multiphase Compressible not supported");
    }

    private void writeIncompressibleMaterials() {
        Materials materials = model.getMaterials();
        ConstantFolder constantFolder = model.getProject().getConstantFolder();
        TransportProperties transportProperties = constantFolder.getTransportProperties();

        if (materials.size() == 2) {
            transportProperties.clear();

            Material mat1 = materials.get(0);
            String mat1Name = mat1.getName();
            Material mat2 = materials.get(1);
            String mat2Name = mat2.getName();

            transportProperties.add(PHASES_KEY, "(" + mat1Name + " " + mat2Name + ")");

            Dictionary dict1 = new StandardMaterialsWriter(model).writeSingle_IncompressibleMaterial(mat1);
            dict1.remove(SIGMA_KEY);
            dict1.setName(mat1Name);
            transportProperties.add(dict1);

            Dictionary dict2 = new StandardMaterialsWriter(model).writeSingle_IncompressibleMaterial(mat2);
            dict2.remove(SIGMA_KEY);
            dict2.setName(mat2Name);
            transportProperties.add(dict2);

            double sigmaValue = module.getSigma();
            transportProperties.add(new DimensionedScalar(SIGMA_KEY, String.valueOf(sigmaValue), "[1 0 -2 0 0 0 0 ]"));

        } else {
            logger.warn("Multiphase solution choosen but '{}' materials found", materials.size());
        }
    }
}
