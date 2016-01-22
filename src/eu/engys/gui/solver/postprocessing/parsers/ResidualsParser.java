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

package eu.engys.gui.solver.postprocessing.parsers;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlock;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlockUnit;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.gui.solver.postprocessing.data.DoubleListTimeBlockUnit;

public class ResidualsParser extends AbstractParser {

    private static final Logger logger = LoggerFactory.getLogger(ResidualsParser.class);
    public static final String KEY = "residuals";

    private static final String CHECK_STRING1 = ", Initial residual = ";
    private static final String CHECK_STRING2 = ", Final residual";
    private static final String CHECK_STRING3 = "Solving for ";
    private static final String ILAMBDA = "ILambda";
    public static final String TIME_PREFIX = "Time = ";

    // COUPLED
    private static final String TIME_PREFIX_COUPLED = "PHYSICAL TIME = ";
    private static final String U_MOM = "U-Mom";
    private static final String V_MOM = "V-Mom";
    private static final String W_MOM = "W-Mom";
    private static final String P_MASS = "p-Mass";
    private static final String K_EQN = "K-Eqn";
    private static final String OMEGA_EQN = "Omega-Eqn";
    private static final String EPSILON_EQN = "Epsilon-Eqn";

    private TimeBlock incompleteBlock;// from previous parsing

    private int timeBlockSize = -1;

    public ResidualsParser(File file) {
        super(null, file);
        this.incompleteBlock = null;
        this.timeBlockSize = -1;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void clear() {
        super.clear();
        this.timeBlockSize = -1;
        this.incompleteBlock = null;
    }

    @Override
    public TimeBlocks updateNewTimeBlocks(List<String> newFileLines) {
        // newTimeBlocks.clear();
        TimeBlocks newTimeBlocks = new TimeBlocks(blockKey);

        if (incompleteBlock != null) {
            newTimeBlocks.add(incompleteBlock);
            incompleteBlock = null;
        }
        for (String row : newFileLines) {
            if (isValidTimeRow(row)) {
                TimeBlock timeBlock = new TimeBlock(Double.parseDouble(extractTimeValue(row)));
                newTimeBlocks.add(timeBlock);
            } else if (newTimeBlocks.size() > 0 && isValidDataRow(row)) {
                String extractVarName = extractVarName(row);
                TimeBlock lastTimeBlock = newTimeBlocks.getLast();
                Map<String, TimeBlockUnit> unitsMap = lastTimeBlock.getUnitsMap();
                if (!unitsMap.containsKey(extractVarName)) {
                    unitsMap.put(extractVarName, new DoubleListTimeBlockUnit(extractVarName));
                }
                DoubleListTimeBlockUnit unit = (DoubleListTimeBlockUnit) unitsMap.get(extractVarName);
                unit.getValues().add(extractInitialResidual(row));
            }
        }

        return newTimeBlocks;
    }

    public boolean checkTimeBlockConsistency(TimeBlocks newTimeBlocks) {
        return ParserUtils.checkTimeBlockConsistency(newTimeBlocks, timeBlockSize, logger);
    }

    @Override
    public void removeInconsistentBlocks(TimeBlocks newTimeBlocks) {
        boolean timeBlockSizeNotSet = timeBlockSize == -1;
        if (newTimeBlocks.size() == 0) {
            return;
        } else {
            if (newTimeBlocks.size() == 1 && timeBlockSizeNotSet) {
                // E' il primo blocco in assoluto e non so se e' completo
                this.incompleteBlock = newTimeBlocks.removeLast();
                return;
            } else {
                // Ho piu' di un blocco (quindi almeno il primo e' completo)
                // Oppure ho un blocco solo ma non e' il primo (lo capisco dal
                // fatto che timeBlockSize e' settata)

                if (timeBlockSizeNotSet) {
                    timeBlockSize = newTimeBlocks.get(0).getUnitsMap().size();
                }

                if (lastBlockIsIncomplete(newTimeBlocks)) {
                    this.incompleteBlock = newTimeBlocks.removeLast();
                    logger.debug("Block {} is smaller than it should: {} < {} and will be reprocessed", incompleteBlock.getTime(), incompleteBlock.getUnitsMap().size(), timeBlockSize);
                } else if (lastBlockIsOK(newTimeBlocks)) {
                    /* is OK */
                } else {
                    logger.debug("Block {} is bigger than it should: {} > {}", newTimeBlocks.getLast().getTime(), newTimeBlocks.getLast().getUnitsMap().size(), timeBlockSize);
                }
            }
        }
    }

    private boolean lastBlockIsIncomplete(TimeBlocks newTimeBlocks) {
        TimeBlock lastBlock = newTimeBlocks.getLast();
        return lastBlock.getUnitsMap().size() < timeBlockSize;
    }

    private boolean lastBlockIsOK(TimeBlocks newTimeBlocks) {
        TimeBlock lastBlock = newTimeBlocks.getLast();
        return lastBlock.getUnitsMap().size() == timeBlockSize;
    }

    @Override
    public boolean isValidTimeRow(String row) {
        boolean isResidualsTimeRow = row.startsWith(TIME_PREFIX);
        boolean isCoupledResidualsTimeRow = row.startsWith(TIME_PREFIX_COUPLED);
        return isResidualsTimeRow || isCoupledResidualsTimeRow;
    }

    @Override
    public boolean isValidDataRow(String row) {
        return isValidResidualsDataRow(row) || isValidCoupledResidualsDataRow(row);
    }

    private boolean isValidResidualsDataRow(String row) {
        return row.contains(CHECK_STRING1) && row.contains(CHECK_STRING2) && row.contains(CHECK_STRING3) && !row.contains(ILAMBDA);
    }

    private boolean isValidCoupledResidualsDataRow(String row) {
        return row.contains(U_MOM) || row.contains(V_MOM) || row.contains(W_MOM) || row.contains(P_MASS) || row.contains(K_EQN) || row.contains(OMEGA_EQN) || row.contains(EPSILON_EQN);
    }

    public String extractTimeValue(String row) {
        if (row.startsWith(TIME_PREFIX)) {
            return row.substring(row.indexOf(TIME_PREFIX) + TIME_PREFIX.length());
        } else if (row.startsWith(TIME_PREFIX_COUPLED)) {
            return row.substring(row.indexOf(TIME_PREFIX_COUPLED) + TIME_PREFIX_COUPLED.length());
        }
        return "";
    }

    public String extractVarName(String row) {
        if (isValidResidualsDataRow(row)) {
            return row.substring(row.indexOf(CHECK_STRING3) + CHECK_STRING3.length(), row.indexOf(CHECK_STRING1));
        } else if (isValidCoupledResidualsDataRow(row)) {
            if (row.contains(U_MOM)) {
                return "Ux";
            } else if (row.contains(V_MOM)) {
                return "Uy";
            } else if (row.contains(W_MOM)) {
                return "Uz";
            } else if (row.contains(P_MASS)) {
                return "p";
            } else if (row.contains(K_EQN)) {
                return "k";
            } else if (row.contains(OMEGA_EQN)) {
                return "omega";
            } else if (row.contains(EPSILON_EQN)) {
                return "epsilon";
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public Double extractInitialResidual(String row) {
        if (isValidResidualsDataRow(row)) {
            return Double.parseDouble(row.substring(row.indexOf(CHECK_STRING1) + CHECK_STRING1.length(), row.indexOf(CHECK_STRING2)));
        } else if (isValidCoupledResidualsDataRow(row)) {
            // the value is beetween the 4th and 5th "|"
            int initialDelimiter = StringUtils.ordinalIndexOf(row, "|", 4);
            int finalDelimiter = StringUtils.ordinalIndexOf(row, "|", 5);
            String stringValue = row.substring(initialDelimiter + 1, finalDelimiter).trim();
            return Double.parseDouble(stringValue);
        }
        return 0.0;
    }

    // For test purpose only
    public void setIncompleteBlock(TimeBlock incompleteBlock) {
        this.incompleteBlock = incompleteBlock;
    }
    
    public void setTimeBlockSize(int timeBlockSize) {
        this.timeBlockSize = timeBlockSize;
    }

}
