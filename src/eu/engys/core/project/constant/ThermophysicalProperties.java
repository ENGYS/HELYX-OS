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
package eu.engys.core.project.constant;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FoamFile;

public class ThermophysicalProperties extends Dictionary {

    public static final String THERMOPHYSICAL_PROPERTIES = "thermophysicalProperties";
    
    public static final String TYPE_KEY = "type";

    // Thermophysical Model
    public static final String MATERIAL_NAME_KEY = "materialName";
    public static final String DEFAULT_MATERIAL_KEY = "defaultMaterial";
    public static final String MOL_WEIGHT_KEY = "molWeight";
    public static final String N_MOLES_KEY = "nMoles";
    
    public static final String SENSIBLE_INTERNAL_ENERGY_KEY = "sensibleInternalEnergy";
    public static final String SENSIBLE_ENTHALPY_KEY = "sensibleEnthalpy";
    

    // Equation of State
    public static final String P_REF_KEY = "pRef";
    public static final String RHO_COEFFS_KEY = "rhoCoeffs";
    public static final String R_KEY = "R";
    public static final String RHO_KEY = "rho";
    public static final String GAMMA_KEY = "gamma";
    public static final String P0_KEY = "p0";
    public static final String B_KEY = "B";
    public static final String RHO0_KEY = "rho0";

    public static final String EQUATION_OF_STATE_KEY = "equationOfState";
    public static final String ADIABATIC_PERFECT_FLUID_KEY = "adiabaticPerfectFluid";
    public static final String PERFECT_GAS_KEY = "perfectGas";
    public static final String PERFECT_FLUID_KEY = "perfectFluid";
    public static final String EQUATION_CONSTANT_KEY = "rhoConst";
    public static final String INCOMPRESSIBLE_KEY = "incompressiblePerfectGas";
    public static final String EQUATION_POLYNOMIAL_KEY = "icoPolynomial";

    // Transport Properties
    public static final String TS_KEY = "Ts";
    public static final String AS_KEY = "As";
    public static final String PR_KEY = "Pr";
    public static final String PRT_KEY = "Prt";
    public static final String MU_KEY = "mu";
    public static final String NU_KEY = "nu";
    public static final String TRANSPORT_MODEL_KEY = "transportModel";
    public static final String MIXTURE_KEY = "mixture";
    public static final String PURE_MIXTURE_KEY = "pureMixture";
    public static final String THERMODYNAMICS_KEY = "thermodynamics";
    public static final String SPECIE_KEY = "specie";
    public static final String ENERGY_KEY = "energy";
    public static final String MU_COEFFS_KEY = "muCoeffs";
    public static final String KAPPA_COEFFS_KEY = "kappaCoeffs";

    public static final String TRANSPORT_KEY = "transport";
    public static final String TRANSPORT_CONST_KEY = "const";
    public static final String TRANSPORT_SUTHERLAND_KEY = "sutherland";
    public static final String TRANSPORT_POLYNOMIAL_KEY = "polynomial";

    // Thermodynamic Model
    public static final String THERMO_TYPE_KEY = "thermoType";
    public static final String THERMO_MODEL_KEY = "thermoModel";
    public static final String HE_PSI_THERMO_KEY = "hePsiThermo";
    public static final String HE_RHO_THERMO_KEY = "heRhoThermo";
    public static final String LOW_CP_COEFFS_KEY = "lowCpCoeffs";
    public static final String HIGH_CP_COEFFS_KEY = "highCpCoeffs";
    public static final String CP_COEFFS_KEY = "CpCoeffs";
    public static final String TCOMMON_KEY = "Tcommon";
    public static final String THIGH_KEY = "Thigh";
    public static final String TLOW_KEY = "Tlow";
    public static final String HF_KEY = "Hf";
    public static final String SF_KEY = "Sf";
    public static final String CP_KEY = "Cp";
    public static final String T_REF_KEY = "TRef";
    public static final String LAMBDA_KEY = "lambda";
    
    public static final String THERMO_KEY = "thermo";
    public static final String THERMO_CONST_KEY = "hConst";
    public static final String THERMO_JANAF_KEY = "janaf";
    public static final String THERMO_POLYNOMIAL_KEY = "hPolynomial";
    
    
    public static final String[] A_KEYS = new String[] { "a0", "a1", "a2", "a3", "a4", "a5", "a6" };
    public static final int COEFFICIENTS_NUMBER = 8;

    public ThermophysicalProperties() {
        super(THERMOPHYSICAL_PROPERTIES);
        setFoamFile(FoamFile.getDictionaryFoamFile(ConstantFolder.CONSTANT, THERMOPHYSICAL_PROPERTIES));
    }

    public ThermophysicalProperties(Dictionary d) {
        this();
        merge(d);
        setFoamFile(FoamFile.getDictionaryFoamFile(ConstantFolder.CONSTANT, THERMOPHYSICAL_PROPERTIES));
    }
}
