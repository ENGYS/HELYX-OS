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
package eu.engys.dynamic.data.sixdof.restraint.linear;

import static eu.engys.dynamic.DynamicMeshDict.LINEAR_DAMPER_KEY;
import static eu.engys.dynamic.DynamicMeshDict.LINEAR_SPRING_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY;
import static eu.engys.dynamic.domain.SixDoFDynamicPanel.DAMPER_LABEL;
import static eu.engys.dynamic.domain.SixDoFDynamicPanel.NONE_LABEL;
import static eu.engys.dynamic.domain.SixDoFDynamicPanel.SPRING_LABEL;

import eu.engys.core.dictionary.Dictionary;

public enum LinearRestraintType {

    NONE("", NONE_LABEL), DAMPER(LINEAR_DAMPER_KEY, DAMPER_LABEL), SPRING(LINEAR_SPRING_KEY, SPRING_LABEL);

    // linearRestraint
    // {
    // sixDoFRigidBodyMotionRestraint linearDamper;
    // coeff 0.0;
    // }

    // linearRestraint
    // {
    // sixDoFRigidBodyMotionRestraint linearSpring;
    // stiffness 0.0;
    // damping 0.0;
    // refAttachmentPt (0.0 0.0 0.0);
    // restLength 0.0;
    // anchor (0.0 0.0 0.0);
    // }

    private String label;
    private String key;

    LinearRestraintType(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public boolean isNone() {
        return this == NONE;
    }

    public boolean isDamper() {
        return this == DAMPER;
    }

    public boolean isSpring() {
        return this == SPRING;
    }

    public static LinearRestraintType byKey(String key) {
        switch (key) {
            case LINEAR_DAMPER_KEY:
                return DAMPER;
            case LINEAR_SPRING_KEY:
                return SPRING;
            default:
                return NONE;
        }
    }

    public static boolean isLinearRestraintDict(Dictionary dict) {
        if (dict.found(SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY)) {
            LinearRestraintType type = LinearRestraintType.byKey(dict.lookupString(SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY));
            return !type.isNone();
        }
        return false;
    }

}
