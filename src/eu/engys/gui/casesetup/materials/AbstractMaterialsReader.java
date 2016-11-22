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

import static eu.engys.core.project.constant.ThermophysicalProperties.ADIABATIC_PERFECT_FLUID_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.AS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.B_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.CP_COEFFS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.EQUATION_CONSTANT_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.EQUATION_OF_STATE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.EQUATION_POLYNOMIAL_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.GAMMA_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.HF_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.HIGH_CP_COEFFS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.INCOMPRESSIBLE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.KAPPA_COEFFS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.LOW_CP_COEFFS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MIXTURE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MOL_WEIGHT_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MU_COEFFS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.N_MOLES_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.PERFECT_FLUID_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.PERFECT_GAS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.RHO0_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.RHO_COEFFS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.R_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.SF_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.SPECIE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TCOMMON_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMODYNAMICS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_CONST_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_JANAF_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_POLYNOMIAL_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_TYPE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THIGH_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TLOW_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TRANSPORT_CONST_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TRANSPORT_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TRANSPORT_POLYNOMIAL_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TRANSPORT_SUTHERLAND_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TS_KEY;
import static eu.engys.core.project.constant.TransportProperties.BIRD_CARREAU_KEY;
import static eu.engys.core.project.constant.TransportProperties.CROSS_POWER_LAW_KEY;
import static eu.engys.core.project.constant.TransportProperties.HERSCHEL_BULKLEY_KEY;
import static eu.engys.core.project.constant.TransportProperties.K_KEY;
import static eu.engys.core.project.constant.TransportProperties.M_KEY;
import static eu.engys.core.project.constant.TransportProperties.NEWTONIAN_KEY;
import static eu.engys.core.project.constant.TransportProperties.NU_0_KEY;
import static eu.engys.core.project.constant.TransportProperties.NU_INF_KEY;
import static eu.engys.core.project.constant.TransportProperties.NU_MAX_KEY;
import static eu.engys.core.project.constant.TransportProperties.NU_MIN_KEY;
import static eu.engys.core.project.constant.TransportProperties.N_KEY;
import static eu.engys.core.project.constant.TransportProperties.POWER_LAW_KEY;
import static eu.engys.core.project.constant.TransportProperties.TAU_0_KEY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.StartWithFinder;
import eu.engys.core.project.constant.ThermophysicalProperties;
import eu.engys.core.project.constant.TransportProperties;
import eu.engys.core.project.materials.MaterialsReader;
import eu.engys.core.project.materials.compressible.AdiabaticPerfectFluid;
import eu.engys.core.project.materials.compressible.CompressibleMaterial;
import eu.engys.core.project.materials.compressible.ConstantDensity;
import eu.engys.core.project.materials.compressible.ConstantThermodynamicModel;
import eu.engys.core.project.materials.compressible.ConstantTransport;
import eu.engys.core.project.materials.compressible.EquationOfState;
import eu.engys.core.project.materials.compressible.IncompressiblePerfectGas;
import eu.engys.core.project.materials.compressible.JANAFThermodynamicModel;
import eu.engys.core.project.materials.compressible.PerfectFluid;
import eu.engys.core.project.materials.compressible.PerfectGas;
import eu.engys.core.project.materials.compressible.PolynomialEquation;
import eu.engys.core.project.materials.compressible.PolynomialThermodynamicModel;
import eu.engys.core.project.materials.compressible.PolynomialTransport;
import eu.engys.core.project.materials.compressible.SutherlandTransport;
import eu.engys.core.project.materials.compressible.ThermodynamicModel;
import eu.engys.core.project.materials.compressible.Transport;
import eu.engys.core.project.materials.incompressible.BirdCarreauTransportModel;
import eu.engys.core.project.materials.incompressible.CrossPowerLawTransportModel;
import eu.engys.core.project.materials.incompressible.HerschelBulkleyTransportModel;
import eu.engys.core.project.materials.incompressible.IncompressibleMaterial;
import eu.engys.core.project.materials.incompressible.NewtonianTransportModel;
import eu.engys.core.project.materials.incompressible.PowerLawTransportModel;
import eu.engys.core.project.materials.incompressible.TransportModel;

public abstract class AbstractMaterialsReader implements MaterialsReader {

    public static final String COEFFS = "Coeffs";

    private static final Logger logger = LoggerFactory.getLogger(AbstractMaterialsReader.class);

    public static final String DEFAULT_MATERIAL_NAME = "material";

    @Override
    public IncompressibleMaterial readIncompressibleMaterial(Dictionary dictionary) {
        Dictionary materialDict = readMaterialDict(dictionary);
        IncompressibleMaterial im = new IncompressibleMaterial(materialDict.getName());

        im.setRho(getAValue(materialDict, TransportProperties.RHO_KEY));
        im.setMu(getAValue(materialDict, TransportProperties.MU_KEY));
        im.setNu(getAValue(materialDict, TransportProperties.NU_KEY));
        if (im.getNu() == 0 && im.getRho() != 0) {
            im.setNu(im.getMu() / im.getRho());
        }
        im.setCp(getAValue(materialDict, TransportProperties.CP_KEY));
        im.setPrt(getAValue(materialDict, TransportProperties.PRT_KEY));
        im.setLambda(getAValue(materialDict, TransportProperties.LAMBDA_KEY));
        im.setpRef(getAValue(materialDict, TransportProperties.P_REF_KEY));
        im.settRef(getAValue(materialDict, TransportProperties.T_REF_KEY));
        im.setBeta(getAValue(materialDict, getBetaKey()));
        im.setPr(getAValue(materialDict, TransportProperties.PR_KEY));

        if (materialDict.found(TransportProperties.TRANSPORT_MODEL_KEY)) {
            readTransportModel(im, materialDict);
        }

        return im;
    }

    void readTransportModel(IncompressibleMaterial im, Dictionary materialDict) {
        String transport = materialDict.lookup(TransportProperties.TRANSPORT_MODEL_KEY);
        TransportModel transportModel = getTransportModel(transport);
        im.setTransportModel(transportModel);

        if (materialDict.isDictionary(transport + COEFFS)) {
            Dictionary transportDict = materialDict.subDict(transport + COEFFS);
            loadTransportModel(im, transportDict);
        }
    }

    private double getAValue(Dictionary materialDict, String key) {
        if (materialDict.isScalar(key)) {
            return materialDict.lookupScalar(key).doubleValue();
        } else if (materialDict.isField(key)) {
            return materialDict.lookupDouble(key);
        } else {
            return 0;
        }
    }

    private TransportModel getTransportModel(String transport) {
        switch (transport) {
        case NEWTONIAN_KEY:
            return new NewtonianTransportModel();
        case CROSS_POWER_LAW_KEY:
            return new CrossPowerLawTransportModel();
        case BIRD_CARREAU_KEY:
            return new BirdCarreauTransportModel();
        case HERSCHEL_BULKLEY_KEY:
            return new HerschelBulkleyTransportModel();
        case POWER_LAW_KEY:
            return new PowerLawTransportModel();
        default:
            return new NewtonianTransportModel();
        }
    }

    void loadTransportModel(IncompressibleMaterial im, Dictionary coeffsDict) {
        TransportModel transportModel = im.getTransportModel();
        if (transportModel instanceof CrossPowerLawTransportModel) {
            CrossPowerLawTransportModel cpl = (CrossPowerLawTransportModel) transportModel;
            cpl.setNu0(coeffsDict.lookupScalar(NU_0_KEY).doubleValue());
            cpl.setNuInf(coeffsDict.lookupScalar(NU_INF_KEY).doubleValue());
            cpl.setM(coeffsDict.lookupScalar(M_KEY).doubleValue());
            cpl.setN(coeffsDict.lookupScalar(N_KEY).doubleValue());
        } else if (transportModel instanceof BirdCarreauTransportModel) {
            BirdCarreauTransportModel bc = (BirdCarreauTransportModel) transportModel;
            bc.setNu0(coeffsDict.lookupScalar(NU_0_KEY).doubleValue());
            bc.setNuInf(coeffsDict.lookupScalar(NU_INF_KEY).doubleValue());
            bc.setK(coeffsDict.lookupScalar(K_KEY).doubleValue());
            bc.setN(coeffsDict.lookupScalar(N_KEY).doubleValue());
        } else if (transportModel instanceof HerschelBulkleyTransportModel) {
            HerschelBulkleyTransportModel hb = (HerschelBulkleyTransportModel) transportModel;
            hb.setNu0(coeffsDict.lookupScalar(NU_0_KEY).doubleValue());
            hb.setTau0(coeffsDict.lookupScalar(TAU_0_KEY).doubleValue());
            hb.setK(coeffsDict.lookupScalar(K_KEY).doubleValue());
            hb.setN(coeffsDict.lookupScalar(N_KEY).doubleValue());
        } else if (transportModel instanceof PowerLawTransportModel) {
            PowerLawTransportModel pl = (PowerLawTransportModel) transportModel;
            pl.setNuMin(coeffsDict.lookupScalar(NU_MIN_KEY).doubleValue());
            pl.setNuMax(coeffsDict.lookupScalar(NU_MAX_KEY).doubleValue());
            pl.setK(coeffsDict.lookupScalar(K_KEY).doubleValue());
            pl.setN(coeffsDict.lookupScalar(N_KEY).doubleValue());
        }
    }

    public abstract String getBetaKey();

    Dictionary readMaterialDict(Dictionary d) {
        Dictionary dict = new Dictionary(d);
        dict.setFoamFile(null);
        String name = DEFAULT_MATERIAL_NAME;

        if (dict.isEmpty()) {
            logger.warn("No material found");
        } else {
            if (!dict.found(TransportProperties.MATERIAL_NAME_KEY)) {
                dict.add(TransportProperties.MATERIAL_NAME_KEY, DEFAULT_MATERIAL_NAME);
            }
            name = dict.lookup(TransportProperties.MATERIAL_NAME_KEY);
        }
        dict.setName(name);
        return dict;
    }

    @Override
    public CompressibleMaterial readCompressibleMaterial(Dictionary dictionary) {
        Dictionary materialDict = readMaterialDict(dictionary);
        CompressibleMaterial cm = new CompressibleMaterial(materialDict.getName());

        if (dictionary.found(THERMO_TYPE_KEY)) {
            Dictionary thermoType = dictionary.subDict(THERMO_TYPE_KEY);
            readThermoDict(cm, thermoType);
        }

        if (dictionary.found(MIXTURE_KEY)) {
            Dictionary mixture = dictionary.subDict(MIXTURE_KEY);
            readMixtureDict(cm, mixture);
        }
        return cm;
    }

    void readThermoDict(CompressibleMaterial cm, Dictionary thermoType) {
        if (thermoType.found(TRANSPORT_KEY)) {
            String transport = thermoType.lookup(TRANSPORT_KEY);
            cm.setTransport(getTransport(transport));
        }
        if (thermoType.found(THERMO_KEY)) {
            String thermo = thermoType.lookup(THERMO_KEY);
            cm.setThermodynamicModel(getThermo(thermo));
        }
        if (thermoType.found(EQUATION_OF_STATE_KEY)) {
            String equation = thermoType.lookup(EQUATION_OF_STATE_KEY);
            cm.setEqOfState(getEquation(equation));
        }
    }

    private Transport getTransport(String transport) {
        switch (transport) {
        case TRANSPORT_CONST_KEY:
            return new ConstantTransport();
        case TRANSPORT_SUTHERLAND_KEY:
            return new SutherlandTransport();
        case TRANSPORT_POLYNOMIAL_KEY:
            return new PolynomialTransport();
        default:
            return new ConstantTransport();
        }
    }

    private ThermodynamicModel getThermo(String thermo) {
        switch (thermo) {
        case THERMO_CONST_KEY:
            return new ConstantThermodynamicModel();
        case THERMO_JANAF_KEY:
            return new JANAFThermodynamicModel();
        case THERMO_POLYNOMIAL_KEY:
            return new PolynomialThermodynamicModel();
        default:
            return new ConstantThermodynamicModel();
        }
    }

    private EquationOfState getEquation(String equation) {
        switch (equation) {
        case ADIABATIC_PERFECT_FLUID_KEY:
            return new AdiabaticPerfectFluid();
        case EQUATION_CONSTANT_KEY:
            return new ConstantDensity();
        case PERFECT_GAS_KEY:
            return new PerfectGas();
        case PERFECT_FLUID_KEY:
            return new PerfectFluid();
        case INCOMPRESSIBLE_KEY:
            return new IncompressiblePerfectGas();
        case EQUATION_POLYNOMIAL_KEY:
            return new PolynomialEquation();
        default:
            return new PerfectGas();
        }
    }

    void readMixtureDict(CompressibleMaterial cm, Dictionary mixture) {
        if(mixture.found(SPECIE_KEY)){
            Dictionary specieDict = mixture.subDict(SPECIE_KEY);
            loadSpecie(cm, specieDict);
        }
        if(mixture.found(THERMODYNAMICS_KEY)){
            Dictionary thermoDict = mixture.subDict(THERMODYNAMICS_KEY);
            loadThermo(cm, thermoDict);
        }
        if(mixture.found(TRANSPORT_KEY)){
            Dictionary transportDict = mixture.subDict(TRANSPORT_KEY);
            loadTransport(cm, transportDict);
        }
        if(mixture.found(EQUATION_OF_STATE_KEY)){
            Dictionary equationDict = mixture.subDict(EQUATION_OF_STATE_KEY);
            loadEquationOfState(cm, equationDict);
        }
    }

    private void loadSpecie(CompressibleMaterial cm, Dictionary specieDict) {
        cm.setnMoles(specieDict.lookupInt(N_MOLES_KEY));
        cm.setMolWeight(specieDict.lookupDouble(MOL_WEIGHT_KEY));
    }

    private void loadThermo(CompressibleMaterial cm, Dictionary thermoDict) {
        ThermodynamicModel thermo = cm.getThermodynamicModel();
        if (thermo instanceof ConstantThermodynamicModel) {
            ConstantThermodynamicModel ctm = (ConstantThermodynamicModel) thermo;
            ctm.setCp(thermoDict.lookupDouble(ThermophysicalProperties.CP_KEY));
            ctm.setHf(thermoDict.lookupDouble(HF_KEY));
        } else if (thermo instanceof JANAFThermodynamicModel) {
            JANAFThermodynamicModel jtm = (JANAFThermodynamicModel) thermo;
            jtm.settLow(thermoDict.lookupDouble(TLOW_KEY));
            jtm.settHigh(thermoDict.lookupDouble(THIGH_KEY));
            jtm.settCommon(thermoDict.lookupDouble(TCOMMON_KEY));
            jtm.setLowCpCoefficients(thermoDict.lookupDoubleArray(new StartWithFinder(LOW_CP_COEFFS_KEY)));
            jtm.setHighCpCoefficients(thermoDict.lookupDoubleArray(new StartWithFinder(HIGH_CP_COEFFS_KEY)));
        } else if (thermo instanceof PolynomialThermodynamicModel) {
            PolynomialThermodynamicModel ptm = (PolynomialThermodynamicModel) thermo;
            ptm.setHf(thermoDict.lookupDouble(HF_KEY));
            ptm.setSf(thermoDict.lookupDouble(SF_KEY));
            ptm.setCpCoefficients(thermoDict.lookupDoubleArray(new StartWithFinder(CP_COEFFS_KEY)));
        }
    }

    private void loadTransport(CompressibleMaterial cm, Dictionary transportDict) {
        Transport transport = cm.getTransport();
        if (transport instanceof ConstantTransport) {
            ConstantTransport ct = (ConstantTransport) transport;
            ct.setMu(transportDict.lookupDouble(ThermophysicalProperties.MU_KEY));
            ct.setPr(transportDict.lookupDouble(ThermophysicalProperties.PR_KEY));
        } else if (transport instanceof SutherlandTransport) {
            SutherlandTransport st = (SutherlandTransport) transport;
            st.setAs(transportDict.lookupDouble(AS_KEY));
            st.setTs(transportDict.lookupDouble(TS_KEY));
        } else if (transport instanceof PolynomialTransport) {
            PolynomialTransport pt = (PolynomialTransport) transport;
            pt.setMuCoefficients(transportDict.lookupDoubleArray(new StartWithFinder(MU_COEFFS_KEY)));
            pt.setKappaCoefficients(transportDict.lookupDoubleArray(new StartWithFinder(KAPPA_COEFFS_KEY)));
        }
    }

    private void loadEquationOfState(CompressibleMaterial cm, Dictionary equationDict) {
        EquationOfState equation = cm.getEqOfState();
        if (equation instanceof AdiabaticPerfectFluid) {
            AdiabaticPerfectFluid apf = (AdiabaticPerfectFluid) equation;
            apf.setRho0(equationDict.lookupDouble(RHO0_KEY));
            apf.setB(equationDict.lookupDouble(B_KEY));
            apf.setP0(equationDict.lookupDouble(ThermophysicalProperties.P0_KEY));
            apf.setGamma(equationDict.lookupDouble(GAMMA_KEY));
        } else if (equation instanceof ConstantDensity) {
            ConstantDensity cd = (ConstantDensity) equation;
            cd.setRho(equationDict.lookupDouble(ThermophysicalProperties.RHO_KEY));
        } else if (equation instanceof PerfectGas) {
//            PerfectGas pg = (PerfectGas) equation;
        } else if (equation instanceof PerfectFluid) {
            PerfectFluid pf = (PerfectFluid) equation;
            pf.setRho0(equationDict.lookupDouble(RHO0_KEY));
            pf.setR(equationDict.lookupDouble(R_KEY));
        } else if (equation instanceof IncompressiblePerfectGas) {
            IncompressiblePerfectGas ipg = (IncompressiblePerfectGas) equation;
            ipg.setpRef(equationDict.lookupDouble(ThermophysicalProperties.P_REF_KEY));
        } else if (equation instanceof PolynomialEquation) {
             PolynomialEquation pe = (PolynomialEquation) equation;
             pe.setRhoCoefficients(equationDict.lookupDoubleArray(new StartWithFinder(RHO_COEFFS_KEY)));
        }
    }

}
