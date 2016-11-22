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

package eu.engys.core.project.geometry.stl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.util.TempFolder;
import eu.engys.util.VTKSettings;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ExecUtil;

public class STLWriter implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(STLWriter.class);
    private static final int SIZE = 8192;

    private final File targetFile;
    private final String fileName;
    private final File tmp;
    private final ProgressMonitor monitor;

    private Stl stl;

    public STLWriter(File target, Stl stl, ProgressMonitor monitor) {
        this.targetFile = target;
        this.stl = stl;
        this.fileName = target.getName();
        this.monitor = monitor;
        this.tmp = TempFolder.get(STLWriter.class.getSimpleName());
    }

    @Override
    public void run() {
        try {
            initMonitor();
            writeFile();
        } catch (Exception e) {
            logAnError(e);
        } finally {
            monitor.setCurrent(null, monitor.getTotal());
            FileUtils.deleteQuietly(tmp);
        }
    }

    void initMonitor() throws IOException {
        int totalLines = stl.getSolids().length;
        monitor.setTotal(totalLines);
        monitor.setCurrent(null, 0);
        monitor.setIndeterminate(false);
    }

    void logAnError(Exception e) {
        monitor.error(e.getMessage(), 1);
        logger.error("Error writing STL file ", e);
    }

    void writeFile() throws Exception {
        monitor.info(targetFile.getName() + " [ASCII]", 2);
        logger.info("Write STL " + targetFile.getName() + " [ASCII]");

        if (VTKSettings.librariesAreLoaded()) {
            try (FileWriter writer = new FileWriter(targetFile)) {
                Solid[] solids = stl.getSolids();
                SolidWriter[] solidWriters = new SolidWriter[solids.length];
                for (int i = 0; i < solidWriters.length; i++) {
                    solidWriters[i] = new SolidWriter(tmp, fileName, solids[i]);
                }

                ExecUtil.execSerial(solidWriters);

                for (SolidWriter solidWriter : solidWriters) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(solidWriter.getFile()))) {
                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("solid")) {
                                writer.write("solid " + solidWriter.getSolid().getName() + "\n");
                            } else if (line.startsWith("endsolid")) {
                                writer.write("endsolid " + solidWriter.getSolid().getName() + "\n");
                            } else {
                                writer.write(line + "\n");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    monitor.setCurrent(null, monitor.getCurrent());
                }
                writer.flush();
            } finally {

            }
        } else {
            logger.info("Write STL SKIPPED: no 3D");
        }
            
    }

}
