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


package eu.engys.gui.casesetup.boundaryconditions.factories;

import static eu.engys.core.project.zero.fields.Fields.P;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.CLAMP_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FILE_NAME_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FIXED_FLUX_PRESSURE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FIXED_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FREESTREAM_PRESSURE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.OUT_OF_BOUNDS_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PRESSURE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.RHO_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TABLE_FILE_COEFFS_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TABLE_FILE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TABLE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TOTAL_PRESSURE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.UNIFORM_TOTAL_PRESSURE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ZERO_GRADIENT_KEY;
import eu.engys.core.dictionary.Dictionary;

public class PressureFactory {

    public static final Dictionary totalPressure = new Dictionary(P) {
        {
            add(TYPE, TOTAL_PRESSURE_KEY);
            add(VALUE, "uniform 0");
            add("p0", "uniform 0");
            add("gamma", "1.4");
        }
    };

    public static final Dictionary uniformTotalPressure = new Dictionary(P) {
        {
            add(TYPE, UNIFORM_TOTAL_PRESSURE_KEY);
            add(VALUE, "uniform 0");
            add(RHO_KEY, RHO_KEY);
            add("gamma", "1.4");
            add("p0", "0");
            add(PRESSURE_KEY, TABLE_KEY + " ()");
            add(OUT_OF_BOUNDS_KEY, CLAMP_KEY);
        }
    };

    public static final Dictionary fixedValuePressure_COMP = new Dictionary(P) {
        {
            add(TYPE, FIXED_VALUE_KEY);
            add(VALUE, "uniform 1e5");
        }
    };

    public static final Dictionary fixedValuePressure = new Dictionary(P) {
        {
            add(TYPE, FIXED_VALUE_KEY);
            add(VALUE, "uniform 0");
        }
    };

    public static final Dictionary staticValuePressure_COMP = new Dictionary(P) {
        {
            add(TYPE, FIXED_VALUE_KEY);
            add(VALUE, "uniform 1e5");
        }
    };

    public static final Dictionary staticValuePressure = new Dictionary(P) {
        {
            add(TYPE, FIXED_VALUE_KEY);
            add(VALUE, "uniform 0");
        }
    };

    public static final Dictionary zeroGradientPressure = new Dictionary(P) {
        {
            add(TYPE, ZERO_GRADIENT_KEY);
        }
    };

    public static final Dictionary fixedFluxPressure = new Dictionary(P) {
        {
            add(TYPE, FIXED_FLUX_PRESSURE_KEY);
            add(VALUE, "uniform 0");
            add(RHO_KEY, "rhok");
        }
    };

    public static final Dictionary freestreamPressure = new Dictionary(P) {
        {
            add(TYPE, FREESTREAM_PRESSURE_KEY);
        }
    };

    // For Tests only
    public static final Dictionary uniformTotalPressure_FILE = new Dictionary(P) {
        {
            add(TYPE, UNIFORM_TOTAL_PRESSURE_KEY);
            add(VALUE, "uniform 0");
            add(RHO_KEY, RHO_KEY);
            add("gamma", "1.4");
            add("p0", "0");
            add(PRESSURE_KEY, TABLE_FILE_KEY);
            add(new Dictionary(TABLE_FILE_COEFFS_KEY) {
                {
                    add(FILE_NAME_KEY, "\"\"");
                    add(OUT_OF_BOUNDS_KEY, CLAMP_KEY);
                }
            });
        }
    };

}
