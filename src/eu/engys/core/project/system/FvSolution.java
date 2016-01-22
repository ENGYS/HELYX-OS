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


package eu.engys.core.project.system;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.FoamFile;

public class FvSolution extends Dictionary {
	
    public static final String FV_SOLUTION = "fvSolution";
	
    public static final String SIMPLE = "SIMPLE";
    public static final String PIMPLE = "PIMPLE";
    public static final String PISO = "PISO";
    public static final String COUPLED = "COUPLED";

    public static final String N_OUTER_CORRECTORS_KEY = "nOuterCorrectors";
    public static final String N_NON_ORTHOGONAL_CORRECTORS_KEY = "nNonOrthogonalCorrectors";
    public static final String N_CORRECTORS_KEY = "nCorrectors";
    public static final String RHO_MIN_KEY = "rhoMin";
    public static final String RHO_MAX_KEY = "rhoMax";
    public static final String RELAXATION_FACTORS_KEY = "relaxationFactors";
    public static final String RESIDUAL_CONTROL_KEY = "residualControl";
    public static final String REL_TOLERANCE_KEY = "relTol";
    public static final String TOLERANCE_KEY = "tolerance";
    
    public static final String SONIC_KEY = "sonic";
    public static final String HYDRO_KEY = "hydro";

    public static final String FIELDS_KEY = "fields";
    public static final String EQUATIONS_KEY = "equations";
    public static final String SOLVERS_KEY = "solvers";
	
	public FvSolution() {
		super(FV_SOLUTION);
		setFoamFile(FoamFile.getDictionaryFoamFile(SystemFolder.SYSTEM, FV_SOLUTION));
	}
	
	@Override
	public void check() throws DictionaryException {
	}
}
