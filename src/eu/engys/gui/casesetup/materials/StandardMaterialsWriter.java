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

import static eu.engys.core.project.constant.TransportProperties.BETA_OS_KEY;
import static eu.engys.core.project.constant.TransportProperties.CP0_KEY;
import static eu.engys.core.project.constant.TransportProperties.RHO_CP_KEY;
import static java.lang.String.valueOf;

import javax.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DimensionedScalar;
import eu.engys.core.project.Model;
import eu.engys.core.project.materials.Material;
import eu.engys.core.project.materials.incompressible.IncompressibleMaterial;
import eu.engys.util.DimensionalUnits;

public class StandardMaterialsWriter extends AbstractMaterialsWriter {

    @Inject
    public StandardMaterialsWriter(Model model) {
        super(model);
    }

    @Override
    public Dictionary writeSingle_IncompressibleMaterial(Material m) {
        Dictionary d = super.writeSingle_IncompressibleMaterial(m);

        if (m instanceof IncompressibleMaterial) {
            IncompressibleMaterial im = (IncompressibleMaterial) m;
            d.add(new DimensionedScalar(CP0_KEY, valueOf(im.getCp()), DimensionalUnits.K));
            d.add(RHO_CP_KEY, valueOf(im.getRho())); /* NOT A DIMENSIONED SCALAR! */
        }

        return d;
    }

    @Override
    protected String getBetaKey() {
        return BETA_OS_KEY;
    }
}
