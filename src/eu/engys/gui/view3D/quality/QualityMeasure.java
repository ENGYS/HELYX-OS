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
package eu.engys.gui.view3D.quality;

import static eu.engys.gui.view3D.quality.QualityThresholdType.LESS_THAN;
import static eu.engys.gui.view3D.quality.QualityThresholdType.MORE_THAN;

public enum QualityMeasure {
    
    NON_ORTHOGONALITY("nonOrthogonality", MORE_THAN), 
    PYRAMIDS("pyramids", MORE_THAN), 
    SKEWNESS("skewness", LESS_THAN), 
    WEIGHTS("weights", MORE_THAN), 
    VOLUME_RATIO("volumeRatio", MORE_THAN), 
    DETERMINANT("determinant", MORE_THAN);

    private String fieldName;
    private QualityThresholdType type;
    
    private QualityMeasure(String fieldName, QualityThresholdType type) {
        this.fieldName = fieldName;
        this.type = type;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public QualityThresholdType getType() {
        return type;
    }

}
