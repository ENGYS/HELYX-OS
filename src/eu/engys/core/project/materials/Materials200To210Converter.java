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


package eu.engys.core.project.materials;

import static eu.engys.core.project.constant.ThermophysicalProperties.AS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.CONSTANT_CP_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.CONST_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.CP_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.ENERGY_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.EQUATION_OF_STATE_KEY;
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
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.constant.ThermophysicalProperties;

public class Materials200To210Converter {

    /*
     * thermoType
     * thermoModel<mixture<transport<specieThermo<thermo<equationOfState>>>>>;
     * thermoType
     * hRhoThermo<pureMixture<constTransport<specieThermo<hConstThermo
     * <perfectGas>>>>>;
     * 
     * mixture { specie { nMoles 1; molWeight 28.9; } thermodynamics { Cp 1000;
     * Hf 0; } transport { mu 1.8e-05; Pr 0.7; } }
     */
    public ThermophysicalProperties convert(Dictionary oldDictionary) {
        ThermophysicalProperties thermophysicalProperties = new ThermophysicalProperties();

        Dictionary materialGUIDict = toGUIFormat(oldDictionary);

        String type = materialGUIDict.lookup(THERMO_MODEL_KEY);
        String energy = SENSIBLE_ENTHALPY_KEY;
        String thermo = materialGUIDict.found(THERMO_KEY) ? materialGUIDict.lookup(THERMO_KEY) : "";
        String transport = materialGUIDict.found(TRANSPORT_KEY) ? materialGUIDict.lookup(TRANSPORT_KEY) : "";

        Dictionary thermoDict = new Dictionary(THERMO_TYPE_KEY);
        thermoDict.add(Dictionary.TYPE, type);
        thermoDict.add(MIXTURE_KEY, PURE_MIXTURE_KEY);
        thermoDict.add(TRANSPORT_KEY, transport);
        thermoDict.add(THERMO_KEY, thermo);
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

        thermophysicalProperties.add(MATERIAL_NAME_KEY, materialGUIDict.lookup(MATERIAL_NAME_KEY));
        thermophysicalProperties.add(thermoDict);
        thermophysicalProperties.add(mixtureDict);

        // System.out.println("MaterialsBuilder.saveCompressible() "+thermophysicalProperties);
        return thermophysicalProperties;

    }

    // @Override
    // public Dictionary loadCompressible(Model model) {
    // return
    // toGUIFormat(model.getProject().getConstantFolder().getThermophysicalProperties());
    // }

    // @Override
    public Dictionary toGUIFormat(Dictionary thermophysicalProperties) {
        Dictionary d = new Dictionary("");

        if (thermophysicalProperties.isField("thermoType")) {
            String thermoType = thermophysicalProperties.lookup("thermoType");
            String[] tokens = thermoType.replace(">", "").trim().split("<");

            String transport = tokens[2];
            String thermo = tokens[4];

            d.add("thermoModel", tokens[0]);
            d.add("transport", transport);
            d.add("thermo", thermo);
            d.add("equationOfState", tokens[5]);
        }

        if (thermophysicalProperties.found("materialName"))
            d.add("materialName", thermophysicalProperties.lookup("materialName"));
        else
            d.add("materialName", "defaultMaterial");

        if (thermophysicalProperties.found("mixture")) {
            Dictionary mixture = thermophysicalProperties.subDict("mixture");

            /* SPECIES */
            Dictionary speciesDict = mixture.subDict("specie");
            d.merge(speciesDict);

            /* THERMODYNAMICS */
            Dictionary thermodynamicsDict = mixture.subDict("thermodynamics");
            d.merge(thermodynamicsDict);

            /* TRANSPORT */
            Dictionary transportDict = mixture.subDict("transport");
            d.merge(transportDict);
        }

        return d;
    }

    // @Override
    // public Dictionary saveIncompressible(Model model, Dictionary
    // materialDict) {
    //
    // Dictionary transportProperties = new Dictionary("transportProperties");
    //
    // transportProperties.add("materialName",
    // materialDict.lookup("materialName"));
    //
    // String transportModel = materialDict.lookup("transportModel");
    // transportProperties.add("transportModel", transportModel);
    //
    // if (materialDict.found(transportModel + "Coeffs")) {
    // transportProperties.add(materialDict.subDict(transportModel + "Coeffs"));
    // }
    //
    // if (materialDict.found("rho")) {
    // transportProperties.add(materialDict.lookupScalar("rho"));
    // }
    //
    // if (materialDict.found("mu")) {
    // transportProperties.add(materialDict.lookupScalar("mu"));
    // }
    //
    // if (materialDict.found("nu")) {
    // transportProperties.add(materialDict.lookupScalar("nu"));
    // } else if (materialDict.found("rho") && materialDict.found("mu")) {
    // DimensionedScalar rho = materialDict.lookupScalar("rho");
    // DimensionedScalar mu = materialDict.lookupScalar("mu");
    //
    // double nuValue = mu.doubleValue() / rho.doubleValue();
    // Dimensions nuDimensions = mu.getDimensions().divide(rho.getDimensions());
    //
    // DimensionedScalar nu = new DimensionedScalar("nu",
    // Double.toString(nuValue), nuDimensions);
    //
    // transportProperties.add(nu);
    // }
    //
    // if (materialDict.found("Cp")) {
    // DimensionedScalar cp = materialDict.lookupScalar("Cp");
    // transportProperties.add(cp);
    // transportProperties.add("Cp0", cp.getValue());
    // }
    // if (materialDict.found("Prt")) {
    // transportProperties.add(materialDict.lookupScalar("Prt"));
    // }
    // if (materialDict.found("Pr")) {
    // transportProperties.add(materialDict.lookupScalar("Pr"));
    // }
    // if (materialDict.found("lambda")) {
    // transportProperties.add(materialDict.lookupScalar("lambda"));
    // }
    //
    // if (materialDict.found("pRef")) {
    // transportProperties.add(materialDict.lookupScalar("pRef"));
    // }
    // if (materialDict.found("beta")) {
    // transportProperties.add(materialDict.lookupScalar("beta"));
    // }
    // if (materialDict.found("TRef")) {
    // transportProperties.add(materialDict.lookupScalar("TRef"));
    // }
    //
    // //
    // System.out.println("MaterialsBuilder.saveIncompressible() "+transportProperties);
    //
    // return transportProperties;
    // }

    // @Override
    // public Dictionary saveSigma(Model model, Dictionary sigmaDict) {
    // Dictionary transportProperties = new Dictionary("sigma");
    // if (model.getState().isMultiphase()) {
    // if (sigmaDict.found("sigma")) {
    // transportProperties.add(sigmaDict.lookupScalar("sigma"));
    // }
    // }
    // return transportProperties;
    // }

}
