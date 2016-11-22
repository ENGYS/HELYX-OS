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
package eu.engys.core.project.state;

import java.util.Set;

public interface Table15 {

    public static final String BUOYANT_BOUSSINESQ_PIMPLE_FOAM = "buoyantBoussinesqPimpleFoam";
    public static final String BUOYANT_BOUSSINESQ_SIMPLE_FOAM = "buoyantBoussinesqSimpleFoam";
    public static final String BUOYANT_PIMPLE_FOAM = "buoyantPimpleFoam";
    public static final String BUOYANT_SIMPLE_FOAM = "buoyantSimpleFoam";
    public static final String COMPRESSIBLE_INTER_FOAM = "compressibleInterFoam";
    public static final String PIMPLE_FOAM = "pimpleFoam";
    public static final String PISO_FOAM = "pisoFoam";
    public static final String RHO_PIMPLE_FOAM = "rhoPimpleFoam";
    public static final String RHO_SIMPLE_FOAM = "rhoSimpleFoam";
    public static final String SIMPLE_FOAM = "simpleFoam";
    public static final String SONIC_FOAM = "sonicFoam";

    public void updateSolver(State state);

    public void updateSolverFamilies(State state, Set<SolverFamily> families);

}
