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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.system.monitoringfunctionobjects.MonitoringFunctionObject;
import eu.engys.core.project.system.monitoringfunctionobjects.Parser;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.util.Util;

public abstract class AbstractParser implements Parser {

    public static final int MAX_LINES_PARSED_AT_A_TIME = 100_000;

    private static final Logger logger = LoggerFactory.getLogger(AbstractParser.class);

    private boolean isParserInitialised;
    private BufferedReader in;

    protected MonitoringFunctionObject functionObject;

    protected File file;

    protected String blockKey;

    private boolean needUpdate;

    public AbstractParser(MonitoringFunctionObject functionObject, File file, String blockKey) {
        this.functionObject = functionObject;
        this.file = file;
        this.blockKey = blockKey;
    }

    public AbstractParser(MonitoringFunctionObject functionObject, File file) {
        this(functionObject, file, file.getName());
    }

    @Override
    public void clear() {
    }

    @Override
    public void init() {
        logger.debug("INIT: {} [{}]", getClass().getSimpleName(), getFile());
        try {
            File file = getFile();
            if (file.exists()) {
                Reader reader = new InputStreamReader(new FileInputStream(file), Util.UTF_8);
//                in = new BufferedReader(new FileReader(file), 2048);
                in = new BufferedReader(reader, 2048);
                logger.info("{} Parsing file {}", getClass().getCanonicalName(), file);
            }
        } catch (Exception e) {
            logger.warn("INIT PROBLEM: {}", e.getMessage());
        } finally {
            isParserInitialised = true;
        }
    }

    @Override
    public TimeBlocks updateParsing() throws Exception {
        logger.debug("UPDATE: {} [{}] ", getClass().getSimpleName(), getFile());
        if (in == null && isParserInitialised) {
            init();
        }

        if (in != null) {
            return parse();
        }

        return new TimeBlocks(blockKey);
    }

    @Override
    public void end() {
        if (in != null) {
            logger.debug("END: {} [{}]", getClass().getSimpleName(), getFile());
            try {
                in.close();
            } catch (Exception e) {
                logger.warn("END PROBLEM: {}", e.getMessage());
            } finally {
                in = null;
                isParserInitialised = false;
            }
        }
    }

    private TimeBlocks parse() throws IOException {
        needUpdate = false;
        List<String> newFileLines = updateNewFileLines();
        if (newFileLines.size() > 0) {
            TimeBlocks timeBlocks = updateNewTimeBlocks(newFileLines);
            newFileLines = null;
            System.gc();

            if (needUpdate) {
                timeBlocks.addAll(parse());
            }

            return timeBlocks;
        }

        return new TimeBlocks(blockKey);
    }

    public List<String> updateNewFileLines() throws IOException {
        List<String> newFileLines = new ArrayList<>();
        String s = null;
        int i = 0;
        while ((s = in.readLine()) != null) {
            String line = Util.getTrimmedSingleSpaceLine(s);
            if (!line.isEmpty()) {
                newFileLines.add(line);
                i++;
            }
            if (i == MAX_LINES_PARSED_AT_A_TIME) {
                needUpdate = true;
                break;
            }
        }
        return newFileLines;
    }

    protected abstract TimeBlocks updateNewTimeBlocks(List<String> newFileLines);

    @Override
    public File getFile() {
        return file;
    }

}
