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


package eu.engys.core.project.mesh;

import static eu.engys.core.project.mesh.ScalarBarType.BLUE_TO_RED_RAINBOW;

public class FieldItem {

    public static final int DEFAULT_RESOLUTION = 256;
    public static final String SOLID = "Solid Color";
    public static final String INDEXED = "Index";
    public static final String[] COMPONENTS = new String[] { "Magnitude", "X", "Y", "Z" };
    
    private int component;
    private String name;
    private DataType dataType;
    private double[] range;
    private ScalarBarType scalarBarType;
    private int resolution;
    private boolean automaticRange;

    public FieldItem(String fieldName, DataType dataType, int component) {
        this.name = fieldName;
        this.dataType = dataType;
        this.component = component;
        this.range = new double[] { Double.MAX_VALUE, -Double.MAX_VALUE };
        this.scalarBarType = BLUE_TO_RED_RAINBOW;
        this.resolution = DEFAULT_RESOLUTION;
        this.automaticRange = true;
    }

    public int getComponent() {
        return component;
    }

    public String getName() {
        return name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setAutomaticRange(boolean automaticRange) {
        this.automaticRange = automaticRange;
    }

    public boolean isAutomaticRange() {
        return automaticRange;
    }

    public void setRange(double[] range) {
        this.range = range;
    }

    public double[] getRange() {
        return range;
    }

    public ScalarBarType getScalarBarType() {
        return scalarBarType;
    }

    public void setScalarBarType(ScalarBarType scalarBarType) {
        this.scalarBarType = scalarBarType;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
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
        return name;
    }

    public boolean isScalar() {
        return ! SOLID.equals(name) && ! INDEXED.equals(name);
    }

    public boolean isSolid() {
        return SOLID.equals(name);
    }
    
    public boolean isIndexed() {
        return INDEXED.equals(name);
    }
   
}
