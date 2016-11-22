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
package eu.engys.util.plaf;

import javax.swing.UIManager;

import com.pagosoft.plaf.PgsLookAndFeel;
import com.pagosoft.plaf.PlafOptions;

public class HelyxOSLookAndFeel implements ILookAndFeel {

    private final double[] BG_COLOR = { 0.8, 0.8, 0.8 };
    private final double[] BG2_COLOR = { 0.2, 0.2, 0.2 };
    private final double[] SELECT_COLOR = { 1.0, 1.0, 1.0 };

    @Override
    public double[] get3DColor1() {
        return BG_COLOR;
    }

    @Override
    public double[] get3DColor2() {
        return BG2_COLOR;
    }

    @Override
    public double[] get3DSelectionColor() {
        return SELECT_COLOR;
    }

    @Override
    public int getMainWidth() {
        return 650;
    }

    @Override
    public int getSecondaryWidth() {
        return 180;
    }

    @Override
    public void init() {
        initPgsLAF();
    }

    private void initPgsLAF() {
        try {
            PlafOptions.setClearBorderEnabled(true);
            PlafOptions.useExtraMargin(false);
            PlafOptions.useShadowBorder(false);
            PlafOptions.setOfficeScrollBarEnabled(true);
            UIManager.setLookAndFeel(new PgsLookAndFeel());
        } catch (Exception e) {
        }
        
    }
}
