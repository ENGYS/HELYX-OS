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
package eu.engys.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtils {

    private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);
    
    public static final String LNX_EOL = "\n";
    public static final String WIN_EOL = "\r\n";

    /*
     * Write File
     */

    public static void clearFile(File file) {
        try {
            if(file.exists()){
                FileUtils.writeStringToFile(file, "");
            }
        } catch (IOException e) {
            logger.error("Error writing file {}: {} ", file, e.getMessage());
        }
    }

    public static void writeStringToFile(File file, String text) {
        String lineEnding = Util.isWindowsScriptStyle() ? WIN_EOL : LNX_EOL;
        List<String> lines = Arrays.asList(text.split(lineEnding));
        writeLinesToFile(file, lines);
    }

    public static void writeLinesToFile(File file, List<String> lines) {
        String lineEnding = Util.isWindowsScriptStyle() ? WIN_EOL : LNX_EOL;
        try {
            FileUtils.writeLines(file, null, lines, lineEnding);
        } catch (IOException e) {
            logger.error("Error writing file {}: {} ", file, e.getMessage());
        }
    }

    /*
     * Read File
     */

    public static List<String> readLinesFromFile(File file) {
        try {
            return FileUtils.readLines(file, (Charset) null);
        } catch (IOException e) {
            logger.warn("Error reading file {}: {} ", file, e.getMessage());
        }
        return Collections.emptyList();
    }

    public static String readStringFromFile(File file) {
        try {
            return FileUtils.readFileToString(file, (Charset) null);
        } catch (IOException e) {
            logger.warn("Error reading file {}: {} ", file, e.getMessage());
        }
        return "";
    }

    public static String readStringFromStream(InputStream input) throws IOException {
        return org.apache.commons.io.IOUtils.toString(input);
    }

    public static File getSupportFile(File pwd) {
        String extension = Util.isWindows() ? ".bat" : ".run";
        String name = "temp" + System.currentTimeMillis() + extension;
        return new File(pwd, name);
    }

}
