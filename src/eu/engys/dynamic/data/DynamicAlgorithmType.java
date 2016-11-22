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
package eu.engys.dynamic.data;

import static eu.engys.dynamic.DynamicMeshDict.DYNAMIC_MOTION_SOLVER_FV_MESH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DYNAMIC_REFINE_FV_MESH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.MULTI_SOLID_BODY_MOTION_FV_MESH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SOLID_BODY_MOTION_FV_MESH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.STATIC_FV_MESH_KEY;
import static eu.engys.dynamic.DynamicSolutionView.DYNAMIC_REFINE_FV_MESH_LABEL;
import static eu.engys.dynamic.DynamicSolutionView.MULTI_RIGID_BODY_LABEL;
import static eu.engys.dynamic.DynamicSolutionView.OFF_LABEL;
import static eu.engys.dynamic.DynamicSolutionView.SIX_DOF_LABEL;
import static eu.engys.dynamic.DynamicSolutionView.SOLID_RIGID_BODY_LABEL;

public enum DynamicAlgorithmType {

    OFF(STATIC_FV_MESH_KEY, OFF_LABEL), 
    SOLID_RIGID_BODY(SOLID_BODY_MOTION_FV_MESH_KEY, SOLID_RIGID_BODY_LABEL), 
    MULTI_RIGID_BODY(MULTI_SOLID_BODY_MOTION_FV_MESH_KEY, MULTI_RIGID_BODY_LABEL), 
    SIX_DOF(DYNAMIC_MOTION_SOLVER_FV_MESH_KEY, SIX_DOF_LABEL), 
    MESH_REFINE(DYNAMIC_REFINE_FV_MESH_KEY, DYNAMIC_REFINE_FV_MESH_LABEL);

    private String label;
    private String key;

    DynamicAlgorithmType(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public boolean isOff() {
        return this == OFF;
    }

    public boolean isOn() {
        return !isOff();
    }

    public boolean isDomainLevel() {
        return isOn() && !isMultiRigidBody();
    }

    public boolean is6DOF() {
        return this == SIX_DOF;
    }

    public boolean isSolidRigidBody() {
        return this == SOLID_RIGID_BODY;
    }

    public boolean isMultiRigidBody() {
        return this == MULTI_RIGID_BODY;
    }

    public boolean isMeshRefine() {
        return this == MESH_REFINE;
    }

    public static DynamicAlgorithmType byKey(String key) {
        if (key == null)
            return OFF;
        switch (key) {
            case DYNAMIC_MOTION_SOLVER_FV_MESH_KEY:
                return SIX_DOF;
            case SOLID_BODY_MOTION_FV_MESH_KEY:
                return SOLID_RIGID_BODY;
            case MULTI_SOLID_BODY_MOTION_FV_MESH_KEY:
                return MULTI_RIGID_BODY;
            case DYNAMIC_REFINE_FV_MESH_KEY:
                return MESH_REFINE;
            default:
                return OFF;
        }
    }

    public static DynamicAlgorithmType byLabel(String label) {
        if (label == null)
            return OFF;
        switch (label) {
            case SIX_DOF_LABEL:
                return SIX_DOF;
            case SOLID_RIGID_BODY_LABEL:
                return SOLID_RIGID_BODY;
            case MULTI_RIGID_BODY_LABEL:
                return MULTI_RIGID_BODY;
            case DYNAMIC_REFINE_FV_MESH_LABEL:
                return MESH_REFINE;
            default:
                return OFF;
        }
    }

}
