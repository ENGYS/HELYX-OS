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

package eu.engys.core.project.state;

import java.util.Set;

import javax.inject.Inject;

public class EngysTable15 implements Table15 {

    public static final String RHO_CENTRAL_FOAM = "rhoCentralFoam";

    @Inject
    public EngysTable15() {
    }

    @Override
    public void updateSolverFamilies(State state, Set<SolverFamily> families) {
        if (state.getSolverType().isSegregated()) {
            if (state.isLowMach()) {
                if (state.isSteady()) {
                    if (state.getMultiphaseModel().isMultiphase()) {
                        // NONE
                    } else {
                        families.add(SolverFamily.SIMPLE);
                    }
                } else if (state.isTransient()) {
                    if (state.isCompressible()) {
                        families.add(SolverFamily.PIMPLE);
                    } else if (state.isIncompressible()) {
                        if (state.isEnergy() || state.getMultiphaseModel().isMultiphase()) {
                            families.add(SolverFamily.PIMPLE);
                        } else {
                            families.add(SolverFamily.PIMPLE);
                            families.add(SolverFamily.PISO);
                        }
                    } else {
                        // NONE
                    }
                } else {
                    // NONE
                }
            } else if (state.isHighMach()) {
                families.add(SolverFamily.PIMPLE);
                families.add(SolverFamily.CENTRAL);
            } else {
                // NONE
            }
        } else {
            // NONE
        }
    }

    @Override
    public void updateSolver(State state) {
        if (state.getSolverType().isSegregated()) {
            String solverName = "";

            if (state.getMultiphaseModel().isMultiphase()) {
                /* in modules */
            } else if (state.getSolverFamily().isSimple() && state.isRANS()) {
                if (state.isCompressible()) {
                    if (state.isBuoyant()) {
                        solverName = BUOYANT_SIMPLE_FOAM;
                    } else {
                        solverName = RHO_SIMPLE_FOAM;
                    }
                } else if (state.isIncompressible()) {
                    if (state.isEnergy()) {
                        solverName = BUOYANT_BOUSSINESQ_SIMPLE_FOAM;
                    } else {
                        solverName = SIMPLE_FOAM;
                    }
                }
            } else if (state.getSolverFamily().isPiso()) {
                if (state.isIncompressible()) {
                    solverName = PISO_FOAM;
                }
            } else if (state.getSolverFamily().isPimple()) {
                if (state.isCompressible()) {
                    if (state.isBuoyant()) {
                        if (state.isRANS()) {
                            solverName = BUOYANT_PIMPLE_FOAM;
                        }
                    } else {
                        if (state.isHighMach()) {
                            solverName = SONIC_FOAM;
                        } else {
                            solverName = RHO_PIMPLE_FOAM;
                        }
                    }
                } else if (state.isIncompressible()) {
                    if (state.isEnergy()) {
                        solverName = BUOYANT_BOUSSINESQ_PIMPLE_FOAM;
                    } else {
                        solverName = PIMPLE_FOAM;
                    }
                }
            } else if (state.getSolverFamily().isCentral()) {
                solverName = RHO_CENTRAL_FOAM;
            }
            state.setSolver(new Solver(solverName));
        }
    }

}
