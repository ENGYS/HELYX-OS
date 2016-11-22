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
import java.util.List;
import java.util.Map;

import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlock;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlockUnit;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.gui.solver.postprocessing.data.DoubleListTimeBlockUnit;

public abstract class ResidualsParser extends AbstractParser {

    public static final String KEY = "residuals";

    protected Double time;

    public ResidualsParser(File file) {
        super(null, file);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public TimeBlocks updateNewTimeBlocks(List<String> newFileLines) {
        TimeBlocks newTimeBlocks = new TimeBlocks(blockKey);
        for (String row : newFileLines) {
            parseRow(newTimeBlocks, row);
        }
        return newTimeBlocks;
    }

    protected void parseRow(TimeBlocks newTimeBlocks, String row) {
        if (isValidTimeRow(row)) {
            time = Double.parseDouble(extractTimeValue(row));
            newTimeBlocks.add(new TimeBlock(time));
        } else if (isValidDataRow(row) && time != null) {
            /*
             * It may happen that the previous parse has not completely parsed the last time block
             * If this is the case I should create a time block with the same time as the incomplete block and add the missing data
             */
            if (newTimeBlocks.size() == 0) {
                newTimeBlocks.add(new TimeBlock(time));
            }
            String extractVarName = extractVarName(row);
            Map<String, TimeBlockUnit> unitsMap = newTimeBlocks.getLast().getUnitsMap();
            if (!unitsMap.containsKey(extractVarName)) {
                unitsMap.put(extractVarName, new DoubleListTimeBlockUnit(extractVarName));
            }
            DoubleListTimeBlockUnit unit = (DoubleListTimeBlockUnit) unitsMap.get(extractVarName);
            unit.getValues().add(extractInitialResidual(row));
        }
    }

    protected abstract String extractTimeValue(String row);

    protected abstract String extractVarName(String row);

    protected abstract Double extractInitialResidual(String row);

}
