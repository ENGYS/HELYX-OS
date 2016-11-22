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
package eu.engys.core.project.zero;

public class ZeroFolderStructure {

    private boolean boundaryFieldInZero;
    private boolean boundaryFieldInConstant;
    private boolean boundaryFieldInZeroMultiRegion;
    private boolean boundaryFieldInConstantMultiRegion;

    public boolean isBoundaryFieldInZero() {
        return boundaryFieldInZero;
    }

    public void setBoundaryFieldInZero(boolean boundaryFieldInZero) {
        this.boundaryFieldInZero = boundaryFieldInZero;
    }

    public boolean isBoundaryFieldInConstant() {
        return boundaryFieldInConstant;
    }

    public void setBoundaryFieldInConstant(boolean boundaryFieldInConstant) {
        this.boundaryFieldInConstant = boundaryFieldInConstant;
    }

    public boolean isBoundaryFieldInZeroMultiRegion() {
        return boundaryFieldInZeroMultiRegion;
    }

    public void setBoundaryFieldInZeroMultiRegion(boolean boundaryFieldInZeroMultiRegion) {
        this.boundaryFieldInZeroMultiRegion = boundaryFieldInZeroMultiRegion;
    }

    public boolean isBoundaryFieldInConstantMultiRegion() {
        return boundaryFieldInConstantMultiRegion;
    }

    public void setBoundaryFieldInConstantMultiRegion(boolean boundaryFieldInConstantMultiRegion) {
        this.boundaryFieldInConstantMultiRegion = boundaryFieldInConstantMultiRegion;
    }

    

    
}
