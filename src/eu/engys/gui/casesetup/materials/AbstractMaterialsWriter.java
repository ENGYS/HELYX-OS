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
import static eu.engys.core.project.constant.ThermophysicalProperties.COEFFICIENTS_NUMBER;
import static eu.engys.core.project.constant.ThermophysicalProperties.CP_COEFFS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.ENERGY_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.EQUATION_CONSTANT_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.EQUATION_OF_STATE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.EQUATION_POLYNOMIAL_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.GAMMA_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.HE_PSI_THERMO_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.HE_RHO_THERMO_KEY;
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
import static eu.engys.core.project.constant.ThermophysicalProperties.PURE_MIXTURE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.RHO0_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.RHO_COEFFS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.R_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.SENSIBLE_ENTHALPY_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.SENSIBLE_INTERNAL_ENERGY_KEY;
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
import static java.lang.String.valueOf;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DimensionedScalar;
import eu.engys.core.project.Model;
import eu.engys.core.project.constant.ThermophysicalProperties;
import eu.engys.core.project.constant.TransportProperties;
import eu.engys.core.project.materials.Material;
import eu.engys.core.project.materials.MaterialsWriter;
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
import eu.engys.util.DimensionalUnits;

public abstract class AbstractMaterialsWriter implements MaterialsWriter {
    
	private Model model;

    public AbstractMaterialsWriter(Model model) {
        this.model = model;
    }

    @Override
	public Dictionary writeSingle_IncompressibleMaterial(Material m) {
	    Dictionary d = new Dictionary(m.getName());
	    d.add(TransportProperties.MATERIAL_NAME_KEY, m.getName());
	    
	    if (m instanceof IncompressibleMaterial) {
	        IncompressibleMaterial im = (IncompressibleMaterial) m;
	        
	        TransportModel transport = im.getTransportModel();
            String transportKey = getTransportModelKey(transport);
	        d.add(TransportProperties.TRANSPORT_MODEL_KEY, transportKey);
	        
	        Dictionary coeffs = new Dictionary(transportKey + "Coeffs");
	        d.add(coeffs);
	        
	        switch (transportKey) {
            case NEWTONIAN_KEY:
                break;
            case CROSS_POWER_LAW_KEY:
                CrossPowerLawTransportModel cpl = (CrossPowerLawTransportModel) transport;
                coeffs.add(new DimensionedScalar(NU_0_KEY, valueOf(cpl.getNu0()), DimensionalUnits.M2_S));
                coeffs.add(new DimensionedScalar(NU_INF_KEY, valueOf(cpl.getNuInf()), DimensionalUnits.M2_S));
                coeffs.add(new DimensionedScalar(M_KEY, valueOf(cpl.getM()), DimensionalUnits.S));
                coeffs.add(new DimensionedScalar(N_KEY, valueOf(cpl.getN()), DimensionalUnits.NONE));
                break;
            case BIRD_CARREAU_KEY:
                BirdCarreauTransportModel bc = (BirdCarreauTransportModel) transport;
                coeffs.add(new DimensionedScalar(NU_0_KEY, valueOf(bc.getNu0()), DimensionalUnits.M2_S));
                coeffs.add(new DimensionedScalar(NU_INF_KEY, valueOf(bc.getNuInf()), DimensionalUnits.M2_S));
                coeffs.add(new DimensionedScalar(K_KEY, valueOf(bc.getK()), DimensionalUnits.S));
                coeffs.add(new DimensionedScalar(N_KEY, valueOf(bc.getN()), DimensionalUnits.NONE));
                break;
            case HERSCHEL_BULKLEY_KEY: 
                HerschelBulkleyTransportModel hb = (HerschelBulkleyTransportModel) transport;
                coeffs.add(new DimensionedScalar(NU_0_KEY, valueOf(hb.getNu0()), DimensionalUnits.M2_S));
                coeffs.add(new DimensionedScalar(TAU_0_KEY, valueOf(hb.getTau0()), DimensionalUnits.M2_S2));
                coeffs.add(new DimensionedScalar(K_KEY, valueOf(hb.getK()), DimensionalUnits.M2_S));
                coeffs.add(new DimensionedScalar(N_KEY, valueOf(hb.getN()), DimensionalUnits.NONE));
                break;
            case POWER_LAW_KEY: 
                PowerLawTransportModel pl = (PowerLawTransportModel) transport;
                coeffs.add(new DimensionedScalar(NU_MIN_KEY, valueOf(pl.getNuMin()), DimensionalUnits.M2_S));
                coeffs.add(new DimensionedScalar(NU_MAX_KEY, valueOf(pl.getNuMax()), DimensionalUnits.M2_S));
                coeffs.add(new DimensionedScalar(K_KEY, valueOf(pl.getK()), DimensionalUnits.M2_S));
                coeffs.add(new DimensionedScalar(N_KEY, valueOf(pl.getN()), DimensionalUnits.NONE));
                break;
            default:
                break;
            }
	        
	        d.add(new DimensionedScalar(TransportProperties.RHO_KEY, valueOf(im.getRho()), DimensionalUnits.KG_M3));
	        d.add(new DimensionedScalar(TransportProperties.MU_KEY, valueOf(im.getMu()), DimensionalUnits.KG_MS));
	        
	        if (im.getNu() != 0 && !Double.isNaN(im.getNu())) {
	            double nuValue = im.getMu() / im.getRho();
	            d.add(new DimensionedScalar(TransportProperties.NU_KEY, valueOf(nuValue), DimensionalUnits.M2_S));
	        } else {
	            d.add(new DimensionedScalar(TransportProperties.NU_KEY, valueOf(im.getNu()), DimensionalUnits.M2_S));
	        }
	        
	        
	        d.add(new DimensionedScalar(TransportProperties.CP_KEY, valueOf(im.getCp()), DimensionalUnits.M2_S2K));
	        d.add(new DimensionedScalar(TransportProperties.PRT_KEY, valueOf(im.getPrt()), DimensionalUnits.NONE));
	        d.add(new DimensionedScalar(TransportProperties.LAMBDA_KEY, valueOf(im.getLambda()), DimensionalUnits.KGM_S3K));
	        d.add(new DimensionedScalar(TransportProperties.P_REF_KEY, valueOf(im.getpRef()), DimensionalUnits.KG_MS2));
	        d.add(new DimensionedScalar(TransportProperties.T_REF_KEY, valueOf(im.gettRef()), DimensionalUnits.K));
	        d.add(new DimensionedScalar(getBetaKey(), valueOf(im.getBeta()), DimensionalUnits._K));
	        d.add(new DimensionedScalar(TransportProperties.PR_KEY, valueOf(im.getPr()), DimensionalUnits.NONE));
	    }
	    return d;
	}

    protected abstract String getBetaKey();
    
	private String getTransportModelKey(TransportModel transport) {
	    if (transport instanceof NewtonianTransportModel) {
            return NEWTONIAN_KEY;
        } else if (transport instanceof CrossPowerLawTransportModel) {
            return CROSS_POWER_LAW_KEY;
        } else if (transport instanceof BirdCarreauTransportModel) {
            return BIRD_CARREAU_KEY;
        } else if (transport instanceof HerschelBulkleyTransportModel) {
            return HERSCHEL_BULKLEY_KEY;
        } else if (transport instanceof PowerLawTransportModel) {
            return POWER_LAW_KEY;
        }
        return "";
    }

    @Override
	public Dictionary writeSingle_CompressibleMaterial(Material m) {
        Dictionary d = new Dictionary(m.getName());
        if (m instanceof CompressibleMaterial) {
            CompressibleMaterial cm = (CompressibleMaterial) m;
            d.add(ThermophysicalProperties.MATERIAL_NAME_KEY, cm.getName());
            d.add(saveThermoDict(cm));
            d.add(saveMixtureDict(cm));
        }
        return d;
	}

    private Dictionary saveThermoDict(CompressibleMaterial cm) {
        String type = model != null ? (model.getState().isBuoyant() ? HE_RHO_THERMO_KEY : HE_PSI_THERMO_KEY) : HE_PSI_THERMO_KEY;
        String energy = model != null && model.getState().isHighMach() && model.getState().getSolverFamily().isPimple() ? SENSIBLE_INTERNAL_ENERGY_KEY : SENSIBLE_ENTHALPY_KEY;
        
        Dictionary thermoDict = new Dictionary(THERMO_TYPE_KEY);
        thermoDict.add(Dictionary.TYPE, type);
        thermoDict.add(MIXTURE_KEY, PURE_MIXTURE_KEY);
        thermoDict.add(TRANSPORT_KEY, getTransportKey(cm.getTransport()));
        thermoDict.add(THERMO_KEY, getThermoKey(cm.getThermodynamicModel()));
        thermoDict.add(EQUATION_OF_STATE_KEY, getEquationKey(cm.getEqOfState()));
        thermoDict.add(SPECIE_KEY, SPECIE_KEY);
        thermoDict.add(ENERGY_KEY, energy);
        return thermoDict;
    }
    
    private String getEquationKey(EquationOfState equation) {
        if (equation instanceof AdiabaticPerfectFluid) {
            return ADIABATIC_PERFECT_FLUID_KEY;
        } else if (equation instanceof ConstantDensity) {
            return EQUATION_CONSTANT_KEY;
        } else if (equation instanceof PerfectGas) {
            return PERFECT_GAS_KEY;
        } else if (equation instanceof PerfectFluid) {
            return PERFECT_FLUID_KEY;
        } else if (equation instanceof IncompressiblePerfectGas) {
            return INCOMPRESSIBLE_KEY;
        } else if (equation instanceof PolynomialEquation) {
            return EQUATION_POLYNOMIAL_KEY;
        }
        return "";
    }

    private String getThermoKey(ThermodynamicModel thermo) {
        if (thermo instanceof ConstantThermodynamicModel) {
            return THERMO_CONST_KEY;
        } else if (thermo instanceof JANAFThermodynamicModel) {
            return THERMO_JANAF_KEY;
        } else if (thermo instanceof PolynomialThermodynamicModel) {
            return THERMO_POLYNOMIAL_KEY;
        }
        return "";
    }

    private String getTransportKey(Transport transport) {
        if (transport instanceof ConstantTransport) {
            return TRANSPORT_CONST_KEY;
        } else if (transport instanceof SutherlandTransport) {
            return TRANSPORT_SUTHERLAND_KEY;
        } else if (transport instanceof PolynomialTransport) {
            return TRANSPORT_POLYNOMIAL_KEY;
        }
        return "";
    }

    private Dictionary saveMixtureDict(CompressibleMaterial cm) {
        Dictionary mixtureDict = new Dictionary(MIXTURE_KEY);
        mixtureDict.add(saveSpecieDict(cm));
        mixtureDict.add(saveThermodynamicsDict(cm.getThermodynamicModel()));
        mixtureDict.add(saveTransportPropertiesDict(cm.getTransport()));
        mixtureDict.add(saveEquationOfStateDict(cm.getEqOfState()));
        
        return mixtureDict;
    }

    private Dictionary saveSpecieDict(CompressibleMaterial material) {
        Dictionary specieDict = new Dictionary(SPECIE_KEY);
        specieDict.add(N_MOLES_KEY, material.getnMoles());
        specieDict.add(MOL_WEIGHT_KEY, material.getMolWeight());
        return specieDict;
    }

    private Dictionary saveThermodynamicsDict(ThermodynamicModel thermo) {
        Dictionary thermodynamicsDict = new Dictionary(THERMODYNAMICS_KEY);
        if (thermo instanceof ConstantThermodynamicModel) {
            ConstantThermodynamicModel ctm = (ConstantThermodynamicModel) thermo;
            thermodynamicsDict.add(ThermophysicalProperties.CP_KEY, ctm.getCp());
            thermodynamicsDict.add(HF_KEY, ctm.getHf());
        } else if (thermo instanceof JANAFThermodynamicModel) {
            JANAFThermodynamicModel jtm = (JANAFThermodynamicModel) thermo;
            thermodynamicsDict.add(TLOW_KEY, jtm.gettLow());
            thermodynamicsDict.add(THIGH_KEY, jtm.gettHigh());
            thermodynamicsDict.add(TCOMMON_KEY, jtm.gettCommon());
            thermodynamicsDict.add(HIGH_CP_COEFFS_KEY, jtm.getHighCpCoefficients());
            thermodynamicsDict.add(LOW_CP_COEFFS_KEY, jtm.getLowCpCoefficients());
        } else if (thermo instanceof PolynomialThermodynamicModel) {
            PolynomialThermodynamicModel ptm = (PolynomialThermodynamicModel) thermo;
            thermodynamicsDict.add(HF_KEY, ptm.getHf());
            thermodynamicsDict.add(SF_KEY, ptm.getSf());
            thermodynamicsDict.add(CP_COEFFS_KEY + "<" + COEFFICIENTS_NUMBER + ">", ptm.getCpCoefficients());
        }
        return thermodynamicsDict;
    }

    private Dictionary saveTransportPropertiesDict(Transport transport) {
        Dictionary transportDict = new Dictionary(TRANSPORT_KEY);
        if (transport instanceof ConstantTransport) {
            ConstantTransport ct = (ConstantTransport) transport;
            transportDict.add(ThermophysicalProperties.MU_KEY, ct.getMu());
            transportDict.add(ThermophysicalProperties.PR_KEY, ct.getPr());
        } else if (transport instanceof SutherlandTransport) {
            SutherlandTransport st = (SutherlandTransport) transport;
            transportDict.add(AS_KEY, st.getAs());
            transportDict.add(TS_KEY, st.getTs());
        } else if (transport instanceof PolynomialTransport) {
            PolynomialTransport pt = (PolynomialTransport) transport;
            transportDict.add(MU_COEFFS_KEY + "<" + COEFFICIENTS_NUMBER + ">", pt.getMuCoefficients());
            transportDict.add(KAPPA_COEFFS_KEY + "<" + COEFFICIENTS_NUMBER + ">", pt.getKappaCoefficients());
        }
        return transportDict;
    }

    private Dictionary saveEquationOfStateDict(EquationOfState equation) {
        Dictionary equationOfStateDict = new Dictionary(EQUATION_OF_STATE_KEY);
        if (equation instanceof AdiabaticPerfectFluid) {
            AdiabaticPerfectFluid apf = (AdiabaticPerfectFluid) equation;
            equationOfStateDict.add(RHO0_KEY, apf.getRho0());
            equationOfStateDict.add(B_KEY, apf.getB());
            equationOfStateDict.add(ThermophysicalProperties.P0_KEY, apf.getP0());
            equationOfStateDict.add(GAMMA_KEY, apf.getGamma());
        } else if (equation instanceof ConstantDensity) {
            ConstantDensity cd = (ConstantDensity) equation;
            equationOfStateDict.add(ThermophysicalProperties.RHO_KEY, cd.getRho());
        } else if (equation instanceof PerfectGas) {
//            PerfectGas pg = (PerfectGas) equation;
        } else if (equation instanceof PerfectFluid) {
            PerfectFluid pf = (PerfectFluid) equation;
            equationOfStateDict.add(RHO0_KEY, pf.getRho0());
            equationOfStateDict.add(R_KEY, pf.getR());
        } else if (equation instanceof IncompressiblePerfectGas) {
            IncompressiblePerfectGas ipg = (IncompressiblePerfectGas) equation;
            equationOfStateDict.add(ThermophysicalProperties.P_REF_KEY, ipg.getpRef());
        } else if (equation instanceof PolynomialEquation) {
            PolynomialEquation pe = (PolynomialEquation) equation;
            equationOfStateDict.add(RHO_COEFFS_KEY + "<" + COEFFICIENTS_NUMBER + ">", pe.getRhoCoefficients());
        }
        return equationOfStateDict;
    }
}
