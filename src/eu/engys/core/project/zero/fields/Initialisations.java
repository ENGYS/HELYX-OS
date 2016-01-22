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


package eu.engys.core.project.zero.fields;

import java.io.File;

import eu.engys.util.progress.ProgressMonitor;

public interface Initialisations {

	public static final String DEFAULT_KEY = "default";
    public static final String FIXED_VALUE_KEY = "fixedValue";
    public static final String POTENTIAL_FLOW_KEY = "potentialFlow";
    public static final String PRANDTL_KEY = "Prandtl";
    public static final String TURBULENT_IL_KEY = "turbulentIL";
    public static final String BOUNDARY_VALUE_KEY = "boundaryValue";
    public static final String CELL_SET_KEY = "cellSet";
    public static final String INITIALISE_UBCS_KEY = "initialiseUBCs";
    public static final String PATCH_KEY = "patch";

    public void readInitialisationFromFile(Field field);
	
	public void loadInitialisation(File zeroDir, Field field, ProgressMonitor monitor);

}
