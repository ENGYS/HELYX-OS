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

package eu.engys.gui.solver.postprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.Model;
import eu.engys.core.project.system.monitoringfunctionobjects.MonitoringFunctionObject;
import eu.engys.core.project.system.monitoringfunctionobjects.Parser;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.gui.solver.postprocessing.parsers.ResidualsParser;
import eu.engys.gui.solver.postprocessing.parsers.ResidualsUtils;

public class ParsersHandler {

    private static final Logger logger = LoggerFactory.getLogger(ParsersHandler.class);

    private final Model model;

    private Map<String, List<Parser>> parsersMap;

    public ParsersHandler(Model model) {
        this.model = model;
        this.parsersMap = Collections.synchronizedMap(new HashMap<String, List<Parser>>());
        // this will syncro add/remove/indexof/size BUT iterating should be
        // sync'd manually!
    }

    public void deleteUselessLogFiles() {
        ResidualsUtils.clearLogFile(model);
        for (MonitoringFunctionObject fo : model.getMonitoringFunctionObjects()) {
            fo.getType().getFactory().deleteUselessLogFiles(fo);
        }
    }

    /*
     * Register
     */
    private void registerParsersForFunctionObject(String foName) {
        if (foName.equals(ResidualsParser.KEY)) {
            ResidualsParser residualsParser = new ResidualsParser(ResidualsUtils.fileToParse(model));
            register(residualsParser);
        }
        for (MonitoringFunctionObject fo : model.getMonitoringFunctionObjects()) {
            if (fo.getName().equals(foName)) {
                List<Parser> parsers = fo.getType().getFactory().createParsers(fo);
                for (Parser parser : parsers) {
                    register(parser);
                }
            }
        }
    }

    private void register(Parser parser) {
        if (parser != null && !isAlreadyRegistered(parser)) {
            logger.debug("ADDING PARSER FOR {}", parser.getFile());
            if (!parsersMap.containsKey(parser.getKey())) {
                parsersMap.put(parser.getKey(), new ArrayList<Parser>());
            }
            parsersMap.get(parser.getKey()).add(parser);
            parser.init();
            parser.clear();
        }
    }

    private boolean isAlreadyRegistered(Parser parser) {
        if (parsersMap.containsKey(parser.getKey())) {
            List<Parser> parsersForKey = parsersMap.get(parser.getKey());
            for (Parser p : parsersForKey) {
                if (p.getFile().getAbsolutePath().equals(parser.getFile().getAbsolutePath())) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Refresh
     */

    public List<TimeBlocks> refreshOnceForFunctionObject(String foName) {
        logger.info("REFRESH ONCE {}", foName);
        List<TimeBlocks> list = refreshParsersForFunctionObject(foName);
        endParsersForFunctionObject(foName);
        return list;
    }

    public List<TimeBlocks> refreshParsersForFunctionObject(String foName) {
        // If new files are created at runtime
        registerParsersForFunctionObject(foName);

        List<TimeBlocks> blocks = new ArrayList<>();
        if (parsersMap.containsKey(foName)) {
            for (Parser parser : parsersMap.get(foName)) {
                try {
                    TimeBlocks newTimeBlocks = parser.updateParsing();
                    if (!newTimeBlocks.isEmpty()) {
                        logger.debug("{} ADDED {} BLOCKS [{} - {}] FROM FILE {}", foName, newTimeBlocks.size(), newTimeBlocks.get(0).getTime(), newTimeBlocks.getLast().getTime(), parser.getFile());
                    } else {
                        logger.debug("{} ADDED {} BLOCKS FROM FILE {}", foName, newTimeBlocks.size(), parser.getFile());
                    }
                    blocks.add(newTimeBlocks);
                } catch (Exception e) {
                    logger.error("ERROR WHILE PARSING", e);
                }
            }

            printTimeBlocksInfo(blocks, foName);

        } else {
            logger.warn("CANNOT FIND PARSER {} AMONG {}", foName, parsersMap.keySet());
        }

        return blocks;
    }

    private void printTimeBlocksInfo(List<TimeBlocks> blocks, String functionObjectName) {
        if (!blocks.isEmpty()) {
            TimeBlocks allTimeBlocks = new TimeBlocks();
            for (TimeBlocks bs : blocks) {
                allTimeBlocks.addAll(bs);
            }
            if (!allTimeBlocks.isEmpty()) {
                logger.debug("{} TOTAL BLOCKS: [{} - {}]", functionObjectName, allTimeBlocks.get(0).getTime(), allTimeBlocks.getLast().getTime());
            } else {
                logger.debug("{} PARSER BLOCKS: NO TIMES", functionObjectName);
            }
        } else {
            logger.debug("{} PARSER BLOCKS: NO BLOCKS", functionObjectName);
        }
    }

    /*
     * End
     */

    public void endParsers() {
        for (String parserName : parsersMap.keySet()) {
            endParsersForFunctionObject(parserName);
        }
    }

    private void endParsersForFunctionObject(String functionObjectName) {
        if(parsersMap.containsKey(functionObjectName)){
            for (Parser parser : parsersMap.get(functionObjectName)) {
                parser.end();
            }
        }
    }

}
