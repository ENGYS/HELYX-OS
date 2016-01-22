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

package eu.engys.gui.casesetup.materials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardMaterialsWriter extends AbstractMaterialsWriter {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMaterialsWriter.class);

//    @Override
//    public void writeMultiphase_IncompressibleMaterials(Materials materials, Dictionary tpp) {
//        if (materials.size() == 2) {
//            Material mat1 = materials.get(0);
//            Material mat2 = materials.get(1);
//
//            String mat1Name = mat1.getName();
//            String mat2Name = mat2.getName();
//
//            tpp.add(PHASES_KEY, "(" + mat1Name + " " + mat2Name + ")");
//
//            Dictionary dict1 = new Dictionary(mat1.getDictionary());
//            dict1.remove(SIGMA_KEY);
//            dict1.setName(mat1Name);
//            tpp.add(dict1);
//
//            Dictionary dict2 = new Dictionary(mat2.getDictionary());
//            dict2.remove(SIGMA_KEY);
//            dict2.setName(mat2Name);
//            tpp.add(dict2);
//
//            String sigmaValue = "0.0";
//            if (mat1.getDictionary().found(SIGMA_KEY)) {
//                sigmaValue = mat1.getDictionary().lookup(SIGMA_KEY);
//            } else if (mat2.getDictionary().found(SIGMA_KEY)) {
//                sigmaValue = mat2.getDictionary().lookup(SIGMA_KEY);
//            }
//            tpp.add(new DimensionedScalar(SIGMA_KEY, sigmaValue, "[1 0 -2 0 0 0 0 ]"));
//
//        } else {
//            logger.warn("Multiphase solution choosen but '{}' materials found", materials.size());
//        }
//    }

}
