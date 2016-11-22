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

import static eu.engys.core.project.constant.ThermophysicalProperties.DEFAULT_MATERIAL_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.EQUATION_OF_STATE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MATERIAL_NAME_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MIXTURE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.SPECIE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMODYNAMICS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_MODEL_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_TYPE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TRANSPORT_KEY;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;

public class StandardMaterialsBuilder extends AbstractMaterialsBuilder {

    @Override
    public Dictionary loadCompressible(Model model) {
        Dictionary thermophysicalProperties = model.getProject().getConstantFolder().getThermophysicalProperties();
        // System.out.println("MaterialsBuilder.decodeCompressible()"+thermophysicalProperties);

        Dictionary d = new Dictionary("");
        if (thermophysicalProperties.found(THERMO_TYPE_KEY)) {
            Dictionary thermoType = thermophysicalProperties.subDict(THERMO_TYPE_KEY);

            d.add(THERMO_MODEL_KEY, thermoType.lookup(Dictionary.TYPE));
            d.add(TRANSPORT_KEY, thermoType.lookup(TRANSPORT_KEY));
            d.add(THERMO_KEY, thermoType.lookup(THERMO_KEY));
            d.add(EQUATION_OF_STATE_KEY, thermoType.lookup(EQUATION_OF_STATE_KEY));

            if (thermophysicalProperties.found(MATERIAL_NAME_KEY))
                d.add(MATERIAL_NAME_KEY, thermophysicalProperties.lookup(MATERIAL_NAME_KEY));
            else
                d.add(MATERIAL_NAME_KEY, DEFAULT_MATERIAL_KEY);

            if (thermophysicalProperties.found(MIXTURE_KEY)) {
                Dictionary mixture = thermophysicalProperties.subDict(MIXTURE_KEY);

                /* SPECIES */
                Dictionary speciesDict = mixture.subDict(SPECIE_KEY);
                d.merge(speciesDict);

                /* THERMODYNAMICS */
                Dictionary thermodynamicsDict = mixture.subDict(THERMODYNAMICS_KEY);
                d.merge(thermodynamicsDict);

                /* TRANSPORT */
                Dictionary transportDict = mixture.subDict(TRANSPORT_KEY);
                d.merge(transportDict);
            }
        }

        return d;
    }

    @Override
    public Dictionary toGUIFormat(Dictionary thermophysicalProperties) {
        // System.out.println("MaterialsBuilder.decodeCompressible()"+thermophysicalProperties);
        Dictionary d = new Dictionary("");

        if (thermophysicalProperties.found(THERMO_TYPE_KEY)) {
            Dictionary thermoType = thermophysicalProperties.subDict(THERMO_TYPE_KEY);

            d.add(THERMO_MODEL_KEY, thermoType.lookup(Dictionary.TYPE));
            d.add(TRANSPORT_KEY, thermoType.lookup(TRANSPORT_KEY));
            d.add(THERMO_KEY, thermoType.lookup(THERMO_KEY));
            d.add(EQUATION_OF_STATE_KEY, thermoType.lookup(EQUATION_OF_STATE_KEY));

        }
        if (thermophysicalProperties.found(MATERIAL_NAME_KEY))
            d.add(MATERIAL_NAME_KEY, thermophysicalProperties.lookup(MATERIAL_NAME_KEY));
        else
            d.add(MATERIAL_NAME_KEY, thermophysicalProperties.getName());

        if (thermophysicalProperties.found(MIXTURE_KEY)) {
            Dictionary mixture = thermophysicalProperties.subDict(MIXTURE_KEY);

            /* SPECIES */
            if (mixture.found(SPECIE_KEY))
                d.merge(mixture.subDict(SPECIE_KEY));

            /* THERMODYNAMICS */
            if (mixture.found(THERMODYNAMICS_KEY))
                d.merge(mixture.subDict(THERMODYNAMICS_KEY));

            /* TRANSPORT */
            if (mixture.found(TRANSPORT_KEY))
                d.merge(mixture.subDict(TRANSPORT_KEY));
        }

        return d;
    }
}
