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

package eu.engys.core.project.geometry;

import java.util.Arrays;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class BoundingBox {

    private double xmin = Double.MAX_VALUE;
    private double xmax = -Double.MAX_VALUE;
    private double ymin = Double.MAX_VALUE;
    private double ymax = -Double.MAX_VALUE;
    private double zmin = Double.MAX_VALUE;
    private double zmax = -Double.MAX_VALUE;

    public BoundingBox() {
    }

    public BoundingBox(double[] bounds) {
        this.xmin = bounds[0];
        this.xmax = bounds[1];
        this.ymin = bounds[2];
        this.ymax = bounds[3];
        this.zmin = bounds[4];
        this.zmax = bounds[5];
    }
    
    public BoundingBox(double[] min, double[] max) {
        this.xmin = min[0];
        this.xmax = max[0];
        this.ymin = min[1];
        this.ymax = max[1];
        this.zmin = min[2];
        this.zmax = max[2];
    }
    
    public BoundingBox(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.zmin = zmin;
        this.zmax = zmax;
    }

    public double getXmin() {
        return xmin;
    }

    public void setXmin(double xmin) {
        this.xmin = xmin;
    }

    public double getXmax() {
        return xmax;
    }

    public void setXmax(double xmax) {
        this.xmax = xmax;
    }

    public double getYmin() {
        return ymin;
    }

    public void setYmin(double ymin) {
        this.ymin = ymin;
    }

    public double getYmax() {
        return ymax;
    }

    public void setYmax(double ymax) {
        this.ymax = ymax;
    }

    public double getZmin() {
        return zmin;
    }

    public void setZmin(double zmin) {
        this.zmin = zmin;
    }

    public double getZmax() {
        return zmax;
    }

    public void setZmax(double zmax) {
        this.zmax = zmax;
    }

    public double[] getCenter() {
        double centerX = (xmin + xmax) / 2;
        double centerY = (ymin + ymax) / 2;
        double centerZ = (zmin + zmax) / 2;
        return new double[]{centerX, centerY, centerZ};
    }

    public double getDeltaX() {
        return xmax - xmin;
    }
    
    public double getDeltaY() {
        return ymax - ymin;
    }
    
    public double getDeltaZ() {
        return zmax - zmin;
    }
    
    public double getDiagonal() {
    	return Math.sqrt(Math.pow(xmax - xmin, 2) + Math.pow(ymax - ymin, 2) + Math.pow(zmax - zmin, 2));
    }
    
    @Override
    public String toString() {
        return "Bounding Box: xmin (" + getXmin() + "), xmax (" + getXmax()  + "), ymin (" + getYmin()+ "), ymax (" + getYmax()+ "), zmin (" + getZmin()+ "), zmax (" + getZmax() + "), centre " + Arrays.toString(getCenter());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BoundingBox)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        BoundingBox fz = (BoundingBox) obj;
        return new EqualsBuilder().append(getDeltaX(), fz.getDeltaX()).append(getDeltaY(), fz.getDeltaY()).append(getDeltaZ(), fz.getDeltaZ()).append(getCenter(), fz.getCenter()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(getDeltaX()).append(getDeltaY()).append(getDeltaZ()).append(getCenter()).toHashCode();
    }

}
