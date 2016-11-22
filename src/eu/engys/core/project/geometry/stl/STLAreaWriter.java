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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.geometry.TransfromMode;
import eu.engys.core.project.geometry.surface.StlArea;
import eu.engys.util.VTKSettings;
import eu.engys.util.progress.ProgressMonitor;
import vtk.vtkSTLWriter;
import vtk.vtkTransform;
import vtk.vtkTransformFilter;

public class STLAreaWriter implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(STLAreaWriter.class);

    private final File targetFile;
    private final ProgressMonitor monitor;
    private StlArea stlArea;

    public STLAreaWriter(File target, StlArea stlArea, ProgressMonitor monitor) {
        this.targetFile = target;
        this.stlArea = stlArea;
        this.monitor = monitor;
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
        }
    }

    void initMonitor() throws IOException {
        monitor.setTotal(10);
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
            if (stlArea.getTransformMode() == TransfromMode.TO_FILE) {
                vtkTransform transform = stlArea.getTransformation().toVTK(new vtkTransform());

                vtkTransformFilter tFilter = new vtkTransformFilter();
                tFilter.SetTransform(transform);
                tFilter.SetInputData(stlArea.getDataSet());
                tFilter.Update();

                vtkSTLWriter write = new vtkSTLWriter();
                // write.SetFileTypeToASCII();
                write.SetFileName(targetFile.getAbsolutePath());
                write.SetInputData(tFilter.GetOutput());
                write.Write();
            } else {
                vtkSTLWriter write = new vtkSTLWriter();
                // write.SetFileTypeToASCII();
                write.SetFileName(targetFile.getAbsolutePath());
                write.SetInputData(stlArea.getDataSet());
                write.Write();
            }
        } else {
            logger.warn("Write STL SKIPPED: no 3D");
        }
            
    }

}
