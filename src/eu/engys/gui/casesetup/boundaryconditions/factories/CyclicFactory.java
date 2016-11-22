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

import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.BRIDGE_OVERLAP_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.COUPLING_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.COUPLING_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.MATCH_TOLERANCE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.NEIGHBOUR_PATCH_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.ROTATIONAL_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.ROTATIONAL_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.ROTATION_ANGLE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.ROTATION_AXIS_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.ROTATION_CENTRE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.SEPARATION_VECTOR_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.TRANSFORM_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.TRANSLATIONAL_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.TRANSLATIONAL_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.parameterspanel.AbstractCyclicAMISettingsPanel.WEIGHT_CORRECTION_KEY;

import eu.engys.core.dictionary.Dictionary;

public class CyclicFactory {

    public static final String CYCLIC_KEY = "cyclic";
    public static final String CYCLIC_AMI_KEY = "cyclicAMI";

    public static final Dictionary BOUNDARY_CONDITION(final double value) {
        return new Dictionary("patch") {
            {
                add(TYPE, CYCLIC_KEY);
                addUniform(VALUE, value);
            }
        };
    }

    public static final Dictionary BOUNDARY_CONDITION_VECTOR(final double[] value) {
        return new Dictionary("patch") {
            {
                add(TYPE, CYCLIC_KEY);
                addUniform(VALUE, new double[] { 0, 0, 0 });
            }
        };
    }

    public static final Dictionary CYCLIC = new Dictionary("patch") {
        {
            add(TYPE, CYCLIC_KEY);
            add(MATCH_TOLERANCE_KEY, 0.0001);
            add(NEIGHBOUR_PATCH_KEY, "");
        }
    };

    public static final Dictionary AMI_BOUNDARY_CONDITION(final double value) {

        return new Dictionary("patch") {
            {
                add(TYPE, CYCLIC_AMI_KEY);
                addUniform(VALUE, value);
            }
        };
    }

    public static final Dictionary AMI_BOUNDARY_CONDITION_VECTOR(final double[] value) {
        return new Dictionary("patch") {
            {
                add(TYPE, CYCLIC_AMI_KEY);
                addUniform(VALUE, value);
            }
        };
    }

    public static final Dictionary CYCLIC_AMI = new Dictionary("patch") {
        {
            add(TYPE, CYCLIC_AMI_KEY);
            add(MATCH_TOLERANCE_KEY, 0.0001);
            add(NEIGHBOUR_PATCH_KEY, "");
            add(TRANSFORM_KEY, COUPLING_KEY);
            add(BRIDGE_OVERLAP_KEY, true);
        }
    };

    public static final Dictionary CYCLIC_AMI_OS = new Dictionary("patch") {
        {
            add(TYPE, CYCLIC_AMI_KEY);
            add(MATCH_TOLERANCE_KEY, 0.0001);
            add(WEIGHT_CORRECTION_KEY, 0.2);
            add(NEIGHBOUR_PATCH_KEY, "");
            add(TRANSFORM_KEY, COUPLING_KEY);
            add(BRIDGE_OVERLAP_KEY, true);
        }
    };

    public static final Dictionary CYCLIC_AMI_COUPLING = new Dictionary("") {
        {
            add(TYPE, COUPLING_LABEL);
            add(TRANSFORM_KEY, COUPLING_KEY);
        }
    };

    public static final Dictionary CYCLIC_AMI_ROTATIONAL = new Dictionary("") {
        {
            add(TYPE, ROTATIONAL_LABEL);
            add(TRANSFORM_KEY, ROTATIONAL_KEY);
            add(ROTATION_AXIS_KEY, new double[] { 1, 0, 0 });
            add(ROTATION_CENTRE_KEY, new double[] { 0, 0, 0 });
        }
    };

    public static final Dictionary CYCLIC_AMI_ROTATIONAL_OS = new Dictionary("") {
        {
            add(TYPE, ROTATIONAL_LABEL);
            add(TRANSFORM_KEY, ROTATIONAL_KEY);
            add(ROTATION_AXIS_KEY, new double[] { 1, 0, 0 });
            add(ROTATION_CENTRE_KEY, new double[] { 0, 0, 0 });
            add(ROTATION_ANGLE_KEY, 30);
        }
    };

    public static final Dictionary CYCLIC_AMI_TRANSLATIONAL = new Dictionary("") {
        {
            add(TYPE, TRANSLATIONAL_LABEL);
            add(TRANSFORM_KEY, TRANSLATIONAL_KEY);
            add(SEPARATION_VECTOR_KEY, new double[] { 0, 0, 0 });
        }
    };

}
