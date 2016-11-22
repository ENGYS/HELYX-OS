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
package eu.engys.dynamic.data.singlebody;

import static eu.engys.dynamic.DynamicMeshDict.AXIS_ROTATION_MOTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.LINEAR_MOTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.OSCILLATING_LINEAR_MOTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.OSCILLATING_ROTATING_MOTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ROTATING_MOTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ROTATING_STEP_MOTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SDA_KEY;
import static eu.engys.dynamic.domain.SolidBodyDynamicPanel.AXIS_ROTATION_MOTION_LABEL;
import static eu.engys.dynamic.domain.SolidBodyDynamicPanel.LINEAR_MOTION_LABEL;
import static eu.engys.dynamic.domain.SolidBodyDynamicPanel.OSCILLATING_LINEAR_MOTION_LABEL;
import static eu.engys.dynamic.domain.SolidBodyDynamicPanel.OSCILLATING_ROTATING_MOTION_LABEL;
import static eu.engys.dynamic.domain.SolidBodyDynamicPanel.ROTATING_MOTION_LABEL;
import static eu.engys.dynamic.domain.SolidBodyDynamicPanel.ROTATING_STEP_MOTION_LABEL;
import static eu.engys.dynamic.domain.SolidBodyDynamicPanel.SDA_LABEL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SolidBodyMotionFunctionType {
    
    LINEAR_MOTION(LINEAR_MOTION_KEY, LINEAR_MOTION_LABEL), 
    ROTATING_MOTION(ROTATING_MOTION_KEY, ROTATING_MOTION_LABEL),
    ROTATING_STEP_MOTION(ROTATING_STEP_MOTION_KEY, ROTATING_STEP_MOTION_LABEL),
    AXIS_ROTATION_MOTION(AXIS_ROTATION_MOTION_KEY, AXIS_ROTATION_MOTION_LABEL), 
    OSCILLATING_LINEAR_MOTION(OSCILLATING_LINEAR_MOTION_KEY, OSCILLATING_LINEAR_MOTION_LABEL), 
    OSCILLATING_ROTATING_MOTION(OSCILLATING_ROTATING_MOTION_KEY, OSCILLATING_ROTATING_MOTION_LABEL), 
    SDA_MOTION(SDA_KEY, SDA_LABEL);
    
    private static final Logger logger = LoggerFactory.getLogger(SolidBodyMotionFunctionType.class);

    private String label;
    private String key;

    SolidBodyMotionFunctionType(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public boolean isLinearMotion() {
        return this == LINEAR_MOTION;
    }

    public boolean isRotatingMotion() {
        return this == ROTATING_MOTION;
    }

    public boolean isRotatingStepMotion() {
        return this == ROTATING_STEP_MOTION;
    }

    public boolean isAxisRotationMotion() {
        return this == AXIS_ROTATION_MOTION;
    }

    public boolean isOscillatingLinearMotion() {
        return this == OSCILLATING_LINEAR_MOTION;
    }

    public boolean isOscillatingRotatingMotion() {
        return this == OSCILLATING_ROTATING_MOTION;
    }

    public boolean isSDAMotion() {
        return this == SDA_MOTION;
    }

    public static SolidBodyMotionFunctionType byKey(String key) {
        switch (key) {
            case LINEAR_MOTION_KEY:
                return LINEAR_MOTION;
            case ROTATING_MOTION_KEY:
                return ROTATING_MOTION;
            case ROTATING_STEP_MOTION_KEY:
                return ROTATING_STEP_MOTION;
            case AXIS_ROTATION_MOTION_KEY:
                return AXIS_ROTATION_MOTION;
            case OSCILLATING_LINEAR_MOTION_KEY:
                return OSCILLATING_LINEAR_MOTION;
            case OSCILLATING_ROTATING_MOTION_KEY:
                return OSCILLATING_ROTATING_MOTION;
            case SDA_KEY:
                return SDA_MOTION;
            default:
                logger.error("Unknown type {}, reset to linear motion", key);
                return LINEAR_MOTION;
        }
    }

}
