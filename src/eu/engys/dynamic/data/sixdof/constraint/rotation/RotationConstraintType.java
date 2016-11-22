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
package eu.engys.dynamic.data.sixdof.constraint.rotation;

import static eu.engys.dynamic.DynamicMeshDict.AXIS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ORIENTATION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SIX_DOF_RIGID_BODY_MOTION_CONSTRAINT_KEY;
import static eu.engys.dynamic.domain.SixDoFDynamicPanel.AXIS_LABEL;
import static eu.engys.dynamic.domain.SixDoFDynamicPanel.FIX_ORIENTATION_LABEL;
import static eu.engys.dynamic.domain.SixDoFDynamicPanel.NONE_LABEL;

import eu.engys.core.dictionary.Dictionary;

public enum RotationConstraintType {
    
    NONE("", NONE_LABEL), 
    AXIS(AXIS_KEY, AXIS_LABEL), 
    FIX_ORIENTATION(ORIENTATION_KEY, FIX_ORIENTATION_LABEL); 
    
//    sixDoFRigidBodyMotionConstraint axis;
//    axis    (0.0 0.0 1.0);
    
//    sixDoFRigidBodyMotionConstraint orientation;

    private String label;
    private String key;

    RotationConstraintType(String key, String label) {
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

    public boolean isAxis() {
        return this == AXIS;
    }

    public boolean isFixOrientation() {
        return this == FIX_ORIENTATION;
    }

    public static RotationConstraintType byKey(String key) {
        switch (key) {
            case AXIS_KEY:
                return AXIS;
            case ORIENTATION_KEY:
                return FIX_ORIENTATION;
            default:
                return NONE;
        }
    }
    
    public static boolean isRotationContraintDict(Dictionary dict){
        if(dict.found(SIX_DOF_RIGID_BODY_MOTION_CONSTRAINT_KEY)){
            RotationConstraintType type = RotationConstraintType.byKey(dict.lookupString(SIX_DOF_RIGID_BODY_MOTION_CONSTRAINT_KEY));
            return !type.isNone();
        }
        return false;
    }

}
