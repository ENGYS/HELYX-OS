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

import static eu.engys.core.project.constant.ThermophysicalProperties.AS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.BETA_OS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.CONSTANT_CP_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.CONST_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.DEFAULT_MATERIAL_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.ENERGY_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.EQUATION_OF_STATE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.HE_PSI_THERMO_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.HE_RHO_THERMO_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.HF_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.HIGH_CP_COEFFS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.JANAF_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.LOW_CP_COEFFS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MATERIAL_NAME_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MIXTURE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MOL_WEIGHT_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MU_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.N_MOLES_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.PR_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.PURE_MIXTURE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.SENSIBLE_ENTHALPY_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.SENSIBLE_INTERNAL_ENERGY_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.SPECIE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.SUTHERLAND_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TCOMMON_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMODYNAMICS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_MODEL_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_TYPE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THIGH_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TLOW_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TRANSPORT_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TS_KEY;
import static eu.engys.core.project.constant.TransportProperties.CP0_KEY;
import static eu.engys.core.project.constant.TransportProperties.CP_KEY;
import static eu.engys.core.project.constant.TransportProperties.RHO_CP0_KEY;
import static eu.engys.core.project.constant.TransportProperties.RHO_KEY;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;

public class StandardMaterialsBuilder extends AbstractMaterialsBuilder {

    @Override
    protected String getBetaKey() {
        return BETA_OS_KEY;
    }

    @Override
    public Dictionary saveIncompressible(Model model, Dictionary materialDict) {
        Dictionary transportProperties = super.saveIncompressible(model, materialDict);
        if (materialDict.found(CP_KEY)) {
            transportProperties.add(CP0_KEY, materialDict.lookupScalar(CP_KEY).getValue());
        }
        if (materialDict.found(RHO_KEY)) {
            transportProperties.add(RHO_CP0_KEY, materialDict.lookupScalar(RHO_KEY).getValue());
        }
        return transportProperties;
    }

    @Override
    public Dictionary saveCompressible(Model model, Dictionary materialGUIDict) {
        String type = model != null ? (model.getState().isBuoyant() ? HE_RHO_THERMO_KEY : HE_PSI_THERMO_KEY) : materialGUIDict.lookup(THERMO_MODEL_KEY);
        String energy = model != null && model.getState().isHighMach() && model.getState().getSolverFamily().isPimple() ? SENSIBLE_INTERNAL_ENERGY_KEY : SENSIBLE_ENTHALPY_KEY;
        String thermo = materialGUIDict.found(THERMO_KEY) ? materialGUIDict.lookup(THERMO_KEY) : "";
        String transport = materialGUIDict.found(TRANSPORT_KEY) ? materialGUIDict.lookup(TRANSPORT_KEY) : "";

        Dictionary thermoDict = new Dictionary(THERMO_TYPE_KEY);
        thermoDict.add(Dictionary.TYPE, type);
        thermoDict.add(MIXTURE_KEY, PURE_MIXTURE_KEY);
        thermoDict.add(TRANSPORT_KEY, transport.equals(CONST_KEY) ? CONST_KEY : SUTHERLAND_KEY);
        thermoDict.add(THERMO_KEY, materialGUIDict.lookup(THERMO_KEY));
        thermoDict.add(EQUATION_OF_STATE_KEY, materialGUIDict.lookup(EQUATION_OF_STATE_KEY));
        thermoDict.add(SPECIE_KEY, SPECIE_KEY);
        thermoDict.add(ENERGY_KEY, energy);

        /* SPECIES */
        Dictionary specieDict = new Dictionary(SPECIE_KEY);
        specieDict.add(N_MOLES_KEY, materialGUIDict.lookup(N_MOLES_KEY));
        specieDict.add(MOL_WEIGHT_KEY, materialGUIDict.lookup(MOL_WEIGHT_KEY));

        /* THERMODYNAMICS */
        Dictionary thermodynamicsDict = new Dictionary(THERMODYNAMICS_KEY);
        if (thermo.equals(CONSTANT_CP_KEY)) {
            thermodynamicsDict.add(CP_KEY, materialGUIDict.lookup(CP_KEY));
            thermodynamicsDict.add(HF_KEY, materialGUIDict.lookup(HF_KEY));
        } else if (thermo.equals(JANAF_KEY)) {
            thermodynamicsDict.add(TLOW_KEY, materialGUIDict.lookup(TLOW_KEY));
            thermodynamicsDict.add(THIGH_KEY, materialGUIDict.lookup(THIGH_KEY));
            thermodynamicsDict.add(TCOMMON_KEY, materialGUIDict.lookup(TCOMMON_KEY));
            thermodynamicsDict.add(HIGH_CP_COEFFS_KEY, materialGUIDict.lookup(HIGH_CP_COEFFS_KEY));
            thermodynamicsDict.add(LOW_CP_COEFFS_KEY, materialGUIDict.lookup(LOW_CP_COEFFS_KEY));
        }

        /* TRANSPORT */
        Dictionary transportDict = new Dictionary(TRANSPORT_KEY);
        if (transport.equals(CONST_KEY)) {
            transportDict.add(MU_KEY, materialGUIDict.lookup(MU_KEY));
            transportDict.add(PR_KEY, materialGUIDict.lookup(PR_KEY));
        } else if (transport.equals(SUTHERLAND_KEY)) {
            transportDict.add(AS_KEY, materialGUIDict.lookup(AS_KEY));
            transportDict.add(TS_KEY, materialGUIDict.lookup(TS_KEY));
        }

        Dictionary mixtureDict = new Dictionary(MIXTURE_KEY);
        mixtureDict.add(specieDict);
        mixtureDict.add(thermodynamicsDict);
        mixtureDict.add(transportDict);

        String materialName = materialGUIDict.lookup(MATERIAL_NAME_KEY);
        Dictionary thermophysicalProperties = new Dictionary(materialName);
        thermophysicalProperties.add(MATERIAL_NAME_KEY, materialName);
        thermophysicalProperties.add(thermoDict);
        thermophysicalProperties.add(mixtureDict);

        // System.out.println("MaterialsBuilder.saveCompressible() "+thermophysicalProperties);
        return thermophysicalProperties;
    }

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
