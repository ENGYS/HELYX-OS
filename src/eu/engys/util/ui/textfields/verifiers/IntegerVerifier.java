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

package eu.engys.util.ui.textfields.verifiers;

import javax.swing.JComponent;
import javax.swing.JTextField;

import eu.engys.util.ui.textfields.IntegerField;

public class IntegerVerifier extends AbstractVerifier {

    private boolean checkEmptyStrings = true;

    protected int minValue = -Integer.MAX_VALUE;
    protected int maxValue = Integer.MAX_VALUE;

    public IntegerVerifier(IntegerField c, int min, int max, boolean checkEmptyStrings) {
        super(c);
        this.checkEmptyStrings = checkEmptyStrings;
        this.minValue = min;
        this.maxValue = max;
    }

    @Override
    protected boolean validationCriteria(JComponent jc) {
        try {
            String text = ((JTextField) jc).getText();
            if (text == null || text.isEmpty()) {
                if (checkEmptyStrings) {
                    setMessage("Empty value");
                    return false;
                }
                return true;
            }

            double d = Double.parseDouble(text);
            if (d < minValue || d > maxValue) {
                setMessage("Value outside range [" + minValue + ", " + maxValue + "]");
                return false;
            }

            Integer.parseInt(text);
        } catch (Exception e) {
            setMessage("Invalid number format");
            return false;
        }
        return true;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }
    
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

}
