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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.util.VTKSettings;
import eu.engys.util.progress.ProgressMonitor;
import vtk.vtkSTLReader;

public class STLAreaReader {

    private static final Logger logger = LoggerFactory.getLogger(STLAreaReader.class);

    private static final long CHAR_PER_ROW = 30;
    private static final int SIZE = 8192;
    private static final int NP = 4;

    private final File sourceFile;
    private final ProgressMonitor monitor;

    private List<Solid> solids;

    public STLAreaReader(File source, ProgressMonitor monitor) {
        this.sourceFile = source;
        this.monitor = monitor;
        this.solids = new ArrayList<Solid>();
    }

    public void run() {
        try {
            initMonitor();
            readFile();
        } catch (Exception e) {
            logAnError(e);
        } finally {
            monitor.setCurrent(null, monitor.getTotal());
        }
    }

    void initMonitor() throws IOException {
        int totalLines = count(sourceFile);
        monitor.setTotal(totalLines);
        monitor.setIndeterminate(false);
    }

    void logAnError(Exception e) {
        monitor.error(e.getMessage(), 1);
        logger.error("Error reading STL file ", e);
        solids.clear();
    }
    
    void readFile() throws Exception {
        try {
            logger.info("Read STL " + sourceFile.getName() + " [ASCII]");
            monitor.info(sourceFile.getName() + " [ASCII]", 2);

            Solid solid = new Solid("region");
            solids.add(solid);
            if (VTKSettings.librariesAreLoaded()) {
                vtkSTLReader reader = new vtkSTLReader();
                reader.SetFileName(sourceFile.getAbsolutePath());
                reader.Update();

                solid.setDataSet(reader.GetOutput());

                reader.Delete();
            } else {
                logger.info("VTK not loaded!");
            }
        } finally {
        }
    }
    
    private int count(File file) throws IOException {
        int i = (int) (file.length() / CHAR_PER_ROW);
        return i;
    }

    public List<Solid> getSolids() {
        return solids;
    }

}
