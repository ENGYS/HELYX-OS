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

package eu.engys.core.project.geometry.stl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vtk.vtkSTLWriter;
import vtk.vtkTransform;
import vtk.vtkTransformFilter;
import eu.engys.core.project.geometry.TransfromMode;
import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.util.Util;

public class SolidWriter implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SolidWriter.class);

    private Solid solid;
    private File file;
    private String fileName;
    private File tmp;

    public SolidWriter(File tmp, String fileName, Solid solid) {
        this.tmp = tmp;
        this.fileName = fileName;
        this.solid = solid;
    }
    
    public File getFile() {
        return file;
    }
    
    public Solid getSolid() {
        return solid;
    }
    
    @Override
    public void run() {
        String regionName = solid.getName();
        logger.info("- " + regionName + " written", 1);

        this.file = new File(tmp, fileName + "_" + Util.generateID());

        if (solid.getTransformMode() == TransfromMode.TO_FILE) {
            vtkTransform transform = solid.getTransformation().toVTK(new vtkTransform());

            vtkTransformFilter tFilter = new vtkTransformFilter();
            tFilter.SetTransform(transform);
            tFilter.SetInputData(solid.getDataSet());
            tFilter.Update();

            vtkSTLWriter write = new vtkSTLWriter();
            // write.SetFileTypeToASCII();
            write.SetFileName(file.getAbsolutePath());
            write.SetInputData(tFilter.GetOutput());
            write.Write();
        } else {
            vtkSTLWriter write = new vtkSTLWriter();
            // write.SetFileTypeToASCII();
            write.SetFileName(file.getAbsolutePath());
            write.SetInputData(solid.getDataSet());
            write.Write();
        }
    }
}
