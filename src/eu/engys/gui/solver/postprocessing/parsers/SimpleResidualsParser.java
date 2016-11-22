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
package eu.engys.gui.solver.postprocessing.parsers;

import java.io.File;

public class SimpleResidualsParser extends ResidualsParser {

    public static final String CHECK_STRING1 = ", Initial residual = ";
    public static final String CHECK_STRING2 = ", Final residual";
    public static final String CHECK_STRING3 = "Solving for ";
    public static final String ILAMBDA = "ILambda";
    public static final String TIME_PREFIX = "Time = ";

    public SimpleResidualsParser(File file) {
        super(file);
    }

    @Override
    public boolean isValidTimeRow(String row) {
        return row.startsWith(TIME_PREFIX);
    }

    @Override
    public boolean isValidDataRow(String row) {
        return row.contains(CHECK_STRING1) && row.contains(CHECK_STRING2) && row.contains(CHECK_STRING3) && !row.contains(ILAMBDA);
    }

    @Override
    public String extractTimeValue(String row) {
        if (row.startsWith(TIME_PREFIX)) {
            return row.substring(row.indexOf(TIME_PREFIX) + TIME_PREFIX.length());
        }
        return "";
    }

    @Override
    public String extractVarName(String row) {
        if (isValidDataRow(row)) {
            return row.substring(row.indexOf(CHECK_STRING3) + CHECK_STRING3.length(), row.indexOf(CHECK_STRING1));
        } else {
            return "";
        }
    }

    @Override
    public Double extractInitialResidual(String row) {
        if (isValidDataRow(row)) {
            return Double.parseDouble(row.substring(row.indexOf(CHECK_STRING1) + CHECK_STRING1.length(), row.indexOf(CHECK_STRING2)));
        }
        return 0.0;
    }

}
