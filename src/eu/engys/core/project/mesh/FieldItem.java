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
package eu.engys.core.project.mesh;

import static eu.engys.core.project.mesh.ScalarBarType.BLUE_TO_RED_HSV;

public class FieldItem {

    public static final int DEFAULT_RESOLUTION = 256;
    public static final String SOLID = "Solid Color";
    public static final String INDEXED = "Index";
    public static final String MAGNITUDE = "Magnitude";
    public static final String X = "X";
    public static final String Y = "Y";
    public static final String Z = "Z";

    private int componentIndex;
    private String name;
    private DataType dataType;
    private ScalarBarType scalarBarType;
    private int resolution;

    private boolean automaticRange;
    private double[] originalRange;
    private double[] userDefinedRange;
    private String fieldName;

    private boolean inverted;

    private boolean visible;

    public FieldItem(String name, String fieldName, DataType dataType, int componentIndex, double[] originalRange) {
        this.name = name;
        this.fieldName = fieldName;
        this.dataType = dataType;
        this.componentIndex = componentIndex;
        this.originalRange = originalRange;
        this.userDefinedRange = new double[]{Double.NaN, Double.NaN};
        this.scalarBarType = BLUE_TO_RED_HSV;
        this.resolution = DEFAULT_RESOLUTION;
        this.automaticRange = true;
        this.visible = true;
        this.inverted = false;
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public DataType getDataType() {
        return dataType;
    }
    
    public int getComponentIndex() {
        return componentIndex;
    }
    
    public ScalarBarType getScalarBarType() {
        return scalarBarType;
    }
    
    public void setScalarBarType(ScalarBarType scalarBarType) {
        this.scalarBarType = scalarBarType;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isVisible(){
        return visible;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public boolean isInverted() {
        return inverted;
    }

    public int getResolution() {
        return resolution;
    }
    
    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    /*
     * Range
     */
    public void setAutomaticRange(boolean automaticRange) {
        this.automaticRange = automaticRange;
        if(isUserDefinedRange() && Double.isNaN(userDefinedRange[0])){
            this.userDefinedRange = new double[]{originalRange[0], originalRange[1]};
        }
    }

    public boolean isAutomaticRange() {
        return automaticRange;
    }

    public boolean isUserDefinedRange() {
        return !automaticRange;
    }

    public double[] getUserDefinedRange() {
        return userDefinedRange;
    }

    public void setUserDefinedRange(double[] userDefinedRange) {
        this.userDefinedRange = userDefinedRange;
    }
    
    public void setOriginalRange(double[] originalRange) {
        this.originalRange = originalRange;
    }

    public double[] range() {
        return automaticRange ? originalRange : userDefinedRange;
    }
    
    public static FieldItem solidColorItem(){
        return new FieldItem(FieldItem.SOLID, null, DataType.NONE, -1, null);
    }

    public static FieldItem indexItem(){
        return new FieldItem(FieldItem.INDEXED, null, DataType.NONE, -1, null);
    }

    public enum DataType {
        POINT, CELL, NONE;

        public boolean isPoint() {
            return this.equals(POINT);
        }

        public boolean isCell() {
            return this.equals(CELL);
        }

        public boolean isNone() {
            return this.equals(NONE);
        }
    }

    @Override
    public String toString() {
        String range = isScalar() ? "[ " + range()[0] + ", " + range()[1] + "]" : "[ - , - ]";
        return name + " fieldName: " + fieldName + " componentIndex: " + componentIndex + " range " + range + " automatic [ " + automaticRange + " ] - resolution [" + resolution + "] - data type [" + dataType + "] - scalarbar type [" + scalarBarType + " ] - inverted [" + inverted + "]";
    }

    public boolean isScalar() {
        return !isSolid() && !isIndexed();
    }

    public boolean isSolid() {
        return SOLID.equals(name);
    }

    public boolean isIndexed() {
        return INDEXED.equals(name);
    }
    
    public double[] getOriginalRange() {
        return originalRange;
    }

}
