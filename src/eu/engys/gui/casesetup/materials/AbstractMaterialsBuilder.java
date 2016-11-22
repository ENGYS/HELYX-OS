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

package eu.engys.gui.casesetup.materials;

import eu.engys.core.modules.materials.MaterialsBuilder;

public abstract class AbstractMaterialsBuilder implements MaterialsBuilder {

//    @Override
//    public Dictionary saveIncompressible(Model model, Dictionary materialDict) {
//        String materialName = materialDict.lookup(MATERIAL_NAME_KEY);
//
//        Dictionary transportProperties = new Dictionary(materialName);
//        transportProperties.add(MATERIAL_NAME_KEY, materialName);
//        
//        String transportModel = materialDict.lookup(TRANSPORT_MODEL_KEY);
//        transportProperties.add(TRANSPORT_MODEL_KEY, transportModel);
//
//        if (materialDict.found(transportModel + "Coeffs")) {
//            transportProperties.add(materialDict.subDict(transportModel + "Coeffs"));
//        }
//
//        if (materialDict.found(RHO_KEY)) {
//            transportProperties.add(materialDict.lookupScalar(RHO_KEY));
//        }
//
//        if (materialDict.found(MU_KEY)) {
//            transportProperties.add(materialDict.lookupScalar(MU_KEY));
//        }
//
//        if (materialDict.found(NU_KEY)) {
//            transportProperties.add(materialDict.lookupScalar(NU_KEY));
//        } else if (materialDict.found(RHO_KEY) && materialDict.found(MU_KEY)) {
//            DimensionedScalar rho = materialDict.lookupScalar(RHO_KEY);
//            DimensionedScalar mu = materialDict.lookupScalar(MU_KEY);
//
//            double nuValue = mu.doubleValue() / rho.doubleValue();
//            Dimensions nuDimensions = mu.getDimensions().divide(rho.getDimensions());
//
//            DimensionedScalar nu = new DimensionedScalar(NU_KEY, Double.toString(nuValue), nuDimensions);
//
//            transportProperties.add(nu);
//        }
//
//        if (materialDict.found(CP_KEY)) {
//            transportProperties.add(materialDict.lookupScalar(CP_KEY));
//        }
//        if (materialDict.found(PRT_KEY)) {
//            transportProperties.add(materialDict.lookupScalar(PRT_KEY));
//        }
//        if (materialDict.found(PR_KEY)) {
//            transportProperties.add(materialDict.lookupScalar(PR_KEY));
//        }
//        if (materialDict.found(LAMBDA_KEY)) {
//            transportProperties.add(materialDict.lookupScalar(LAMBDA_KEY));
//        }
//
//        if (materialDict.found(P_REF_KEY)) {
//            transportProperties.add(materialDict.lookupScalar(P_REF_KEY));
//        }
//        if (materialDict.found(getBetaKey())) {
//            transportProperties.add(materialDict.lookupScalar(getBetaKey()));
//        }
//        if (materialDict.found(T_REF_KEY)) {
//            transportProperties.add(materialDict.lookupScalar(T_REF_KEY));
//        }
//
//        // System.out.println("MaterialsBuilder.saveIncompressible() "+transportProperties);
//
//        return transportProperties;
//    }

}
