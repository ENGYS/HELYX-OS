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
package eu.engys.dynamic.data.sixdof.constraint.translation;

public class LineTranslationConstraint extends TranslationConstraint {

    public static final String DIRECTION = "direction";
    private double[] direction = new double[] { 0, 0, 1 };

    public LineTranslationConstraint() {
    }

    public LineTranslationConstraint(double[] direction) {
        this.direction = direction;
    }
    
    @Override
    public TranslationConstraint copy() {
        return new LineTranslationConstraint(new double[]{direction[0], direction[1], direction[2]});
    }

    @Override
    public TranslationConstraintType getType() {
        return TranslationConstraintType.LINE;
    }

    public double[] getDirection() {
        return direction;
    }

    public void setDirection(double[] direction) {
        firePropertyChange(DIRECTION, this.direction, this.direction = direction);
    }
}
