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

public class TransportProperties extends Dictionary {
    
    public static final String TRANSPORT_PROPERTIES = "transportProperties";
    
    public static final String MATERIAL_NAME_KEY = "materialName";
    
    public static final String NEWTONIAN_KEY = "Newtonian";
    public static final String CROSS_POWER_LAW_KEY = "CrossPowerLaw";
    public static final String BIRD_CARREAU_KEY = "BirdCarreau";
    public static final String HERSCHEL_BULKLEY_KEY = "HerschelBulkley";
    public static final String POWER_LAW_KEY = "powerLaw";
    
    public static final String NEWTONIAN_COEFFS_KEY = "NewtonianCoeffs";
    public static final String CROSS_POWER_LAW_COEFFS_KEY = "CrossPowerLawCoeffs";
    public static final String BIRD_CARREAU_COEFFS_KEY = "BirdCarreauCoeffs";
    public static final String HERSCHEL_BULKLEY_COEFFS_KEY = "HerschelBulkleyCoeffs";
    public static final String POWER_LAW_COEFFS_KEY = "powerLawCoeffs";
    
    public static final String SIGMAS_KEY = "sigmas";
    public static final String INTERFACE_COMPRESSION_KEY = "interfaceCompression";
    public static final String DRAG_KEY = "drag";
    public static final String VIRTUAL_MASS_KEY = "virtualMass";
    
    public static final String PHASES_KEY = "phases";
	public static final String PHASE1_KEY = "phase1";
	public static final String PHASE2_KEY = "phase2";
	public static final String TRANSPORT_MODEL_KEY = "transportModel";
	public static final String SIGMA_KEY = "sigma";
	public static final String MU_KEY = "mu";
    public static final String NU_KEY = "nu";
    public static final String CP_KEY = "Cp";
    public static final String CP0_KEY = "Cp0";
    public static final String RHO_KEY = "rho";
    public static final String RHO_CP_KEY = "rhoCp0";
    public static final String KAPPA_KEY = "kappa";
    public static final String PR_KEY = "Pr";
    public static final String PRT_KEY = "Prt";
    public static final String LAMBDA_KEY = "lambda";
    public static final String T_REF_KEY = "TRef";
    public static final String BETA_OS_KEY = "beta";
    public static final String BETA_KEY = "Beta";
    public static final String P_REF_KEY = "pRef";
    
    //Non newtonian coeffs
    public static final String NU_0_KEY = "nu0";
    public static final String NU_INF_KEY = "nuInf";
    public static final String M_KEY = "m";
    public static final String N_KEY = "n";
    public static final String K_KEY = "k";
    public static final String TAU_0_KEY = "tau0";
    public static final String NU_MIN_KEY = "nuMin";
    public static final String NU_MAX_KEY = "nuMax";
    

    //Phases Euler
    public static final String DIAMETER_MODEL_KEY = "diameterModel";
    public static final String CONSTANT_KEY = "constant";
    public static final String CONSTANT_COEFFS_KEY = "constantCoeffs";
    public static final String ISOTHERMAL_KEY = "isothermal";
    public static final String ISOTHERMAL_COEFFS_KEY = "isothermalCoeffs";
    public static final String P0_KEY = "p0";
    public static final String D0_KEY = "d0";
    public static final String D_KEY = "d";
    
    public static final String ERGUN_KEY = "Ergun";
    public static final String GIBILARO_KEY = "Gibilaro";
    public static final String GIDASPOW_EEGUNWENYU_KEY = "GidasporEegunWenYu";
    public static final String GIDASPOW_SCHILLERNAUMANN_KEY = "GidaspowSchillerNaumann";
    public static final String SCHILLERNAUMANN_KEY = "SchillerNaumann";
    public static final String SYAMLAL_OBRIEN_KEY = "SyamlalOBrien";
    public static final String WENYU_KEY = "WenYu";
    public static final String BLENDED_KEY = "blended";
    public static final String INTERFACE_KEY = "interface";

    public static final String RESIDUAL_PHASE_FRACTION_KEY = "residualPhaseFraction";
    public static final String RESIDUAL_SLIP_KEY = "residualSlip";

	public TransportProperties() {
		super(TRANSPORT_PROPERTIES);
		setFoamFile(FoamFile.getDictionaryFoamFile(ConstantFolder.CONSTANT, TRANSPORT_PROPERTIES));
	}
	
	public TransportProperties(Dictionary d) {
		this();
		merge(d);
		setFoamFile(FoamFile.getDictionaryFoamFile(ConstantFolder.CONSTANT, TRANSPORT_PROPERTIES));
	}
}
