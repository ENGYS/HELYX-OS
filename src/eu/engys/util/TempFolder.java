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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TempFolder {

    private static final Logger logger = LoggerFactory.getLogger(TempFolder.class);
    
    private static final String sessionID;
    
    static {
        sessionID = String.valueOf(System.currentTimeMillis() / 1000);
    }
    
    public static File get(String... folders) {
        File userTemp = new File(ApplicationInfo.getHome(), "tmp");
        if (!userTemp.exists()) {
            userTemp.mkdirs();
        }
        
        final File sessionTemp = new File(userTemp, "tmp_" + sessionID);
        if (!sessionTemp.exists()) {
            if (sessionTemp.mkdirs()) {
                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    public void run() {
                        FileUtils.deleteQuietly(sessionTemp);
                    }
                }));
            } else {
                logger.error("Cannot create session temporary folder: {}");
            }
        }
        
        if (folders.length > 0) {
            Path path = Paths.get(sessionTemp.getAbsolutePath(), folders);
            try {
                Files.createDirectories(path);
                return path.toFile();
            } catch (IOException e) {
                logger.error("Cannot create path: {}", path);
                throw new RuntimeException(e);
            }
        } else {
            return sessionTemp;
        }
    }
    
}
