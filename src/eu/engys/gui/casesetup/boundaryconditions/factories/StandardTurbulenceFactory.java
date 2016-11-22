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
package eu.engys.gui.casesetup.boundaryconditions.factories;

import static eu.engys.core.project.zero.fields.Fields.EPSILON;
import static eu.engys.core.project.zero.fields.Fields.OMEGA;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.MIXING_LENGTH_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TURBULENT_MIXING_LENGTH_DISSIPATION_RATE_INLET_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TURBULENT_MIXING_LENGTH_FREQUENCY_INLET_KEY;

import eu.engys.core.dictionary.Dictionary;

public class StandardTurbulenceFactory {
    
    public static final Dictionary omegaMixingLength_COMP = new Dictionary(OMEGA) {
        {
            add(TYPE, TURBULENT_MIXING_LENGTH_FREQUENCY_INLET_KEY);
            addUniform(VALUE, 0.01);
            add(MIXING_LENGTH_KEY, "0.01");
        }
    };
    
    public static final Dictionary epsilonMixingLength_COMP = new Dictionary(EPSILON) {
        {
            add(TYPE, TURBULENT_MIXING_LENGTH_DISSIPATION_RATE_INLET_KEY);
            addUniform(VALUE, 0.01);
            add(MIXING_LENGTH_KEY, "0.01");
        }
    };
    
    

}
