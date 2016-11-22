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
package eu.engys.dynamic.data.sixdof.restraint.angular;

import static eu.engys.dynamic.DynamicMeshDict.SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SPHERICAL_ANGULAR_DAMPER_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SPHERICAL_ANGULAR_SPRING_KEY;

import eu.engys.core.dictionary.Dictionary;

public enum AngularRestraintType {
    
    NONE("", ""), 
    DAMPER(SPHERICAL_ANGULAR_DAMPER_KEY, ""), 
    SPRING(SPHERICAL_ANGULAR_SPRING_KEY, "");


    
//    angularRestraint
//    {
//        sixDoFRigidBodyMotionRestraint  sphericalAngularSpring;
//        stiffness   0.0;
//        damping 0.0;
//    }
    
//    angularRestraint
//    {
//        sixDoFRigidBodyMotionRestraint  sphericalAngularDamper;
//        coeff   0.0;
//    }
    
    

    private String label;
    private String key;

    AngularRestraintType(String key, String label) {
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

    public static AngularRestraintType byKey(String key) {
        switch (key) {
            case SPHERICAL_ANGULAR_DAMPER_KEY:
                return DAMPER;
            case SPHERICAL_ANGULAR_SPRING_KEY:
                return SPRING;
            default:
                return NONE;
        }
    }
    
    public static boolean isAngularRestraintDict(Dictionary dict){
        if(dict.found(SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY)){
            AngularRestraintType type = AngularRestraintType.byKey(dict.lookupString(SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY));
            return !type.isNone();
        }
        return false;
    }

}
