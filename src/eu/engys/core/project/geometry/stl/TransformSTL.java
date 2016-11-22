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
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.util.progress.ProgressMonitor;
import vtk.vtkSTLReader;
import vtk.vtkSTLWriter;
import vtk.vtkTransform;
import vtk.vtkTransformFilter;

public class TransformSTL implements Callable<File> {

    public static final String COPY_OF_PREFIX = "CopyOf";
    private static final Logger logger = LoggerFactory.getLogger(TransformSTL.class);

    private ProgressMonitor monitor;

    private AffineTransform t;
    private File toTransformFile;

    private File sourceFile;
    private File targetFile;

    public TransformSTL(File toTransformFile, AffineTransform t, ProgressMonitor monitor) {
        this.toTransformFile = toTransformFile;
        this.t = t;
        this.monitor = monitor;
    }

    @Override
    public File call() {
        try {
            initFiles();
        } catch (IOException e2) {
            logger.error("[Transform STL] ", e2);
            return toTransformFile;
        }

        logger.info("[Transform STL] START: {} -> {}", sourceFile, targetFile);
        monitor.info("Transforming " + sourceFile + " -> " + targetFile);
        monitor.setIndeterminate(true);
        
        try {
            BufferedWriter targetWriter = Files.newBufferedWriter(targetFile.toPath(), Charset.defaultCharset());
            STLReader reader = new STLReader(sourceFile, monitor);
            reader.run();
            List<Solid> solids = reader.getSolids();
            
            for (Solid solid : solids) {
                logger.info("[Transform STL] solid: {}", solid.getName());
//              File solidFile = solid.getFile();
//              Path transformedSolidPath = transformSolid(solidFile.toPath());
//              appendToMainWriter(targetWriter, transformedSolidPath, solid.getName());
            }
            targetWriter.close();
        } catch (Exception e) {
            try {
                // If something goes wrong
                FileUtils.copyFile(sourceFile, targetFile);
            } catch (IOException e1) {
                logger.error("[Transform STL] ", e1);
            }
            logger.error("[Transform STL] ", e);
        } finally {
            FileUtils.deleteQuietly(sourceFile);
            logger.info("[Transform STL] FINISH: {}", toTransformFile.getName());
        }

        monitor.setIndeterminate(false);
        return targetFile;
    }

    private void initFiles() throws IOException {
        // Better not use the original file name
        String  newNameSource= COPY_OF_PREFIX + "_source_" + toTransformFile.getName();
        String  newNameTarget= COPY_OF_PREFIX + "_target_" + toTransformFile.getName();

        FileUtils.copyFile(toTransformFile, new File(toTransformFile.getParent(), newNameSource));
        FileUtils.copyFile(toTransformFile, new File(toTransformFile.getParent(), newNameTarget));
        
        this.sourceFile = new File(toTransformFile.getParentFile(), newNameSource);
        this.targetFile = new File(toTransformFile.getParentFile(), newNameTarget);

        Files.deleteIfExists(targetFile.toPath());
        Files.createFile(targetFile.toPath());
    }

    private void appendToMainWriter(BufferedWriter targetWriter, Path transformedSolidPath, String solidName) throws Exception {
        logger.debug("[Transform STL] Append solid {}", solidName);
        BufferedReader br = Files.newBufferedReader(transformedSolidPath, Charset.defaultCharset());
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("solid")) {
                line = "solid " + solidName;
            }
            targetWriter.write(line);
            targetWriter.newLine();
        }
        br.close();
    }

    private Path transformSolid(Path solidPath) {
        Path transformedSolidPath = solidPath.resolveSibling(solidPath.getFileName() + ".buffer");

        vtkSTLReader sr = new vtkSTLReader();
        // sr.AddObserver("ProgressEvent", new ReaderProgress(sr, new
        // SilentMonitor()), "progress");
        sr.SetFileName(solidPath.toString());
        sr.Update();

        vtkTransform transform = new vtkTransform();
        transform.Translate(t.getTranslation());
        transform.Scale(t.getScale());
        transform.RotateX(t.getRotationX());
        transform.RotateY(t.getRotationY());
        transform.RotateZ(t.getRotationZ());

        vtkTransformFilter tFilter = new vtkTransformFilter();
        tFilter.SetTransform(transform);
        tFilter.SetInputData(sr.GetOutput());
        // tFilter.AddObserver("ProgressEvent", new ReaderProgress(tFilter, monitor), "progress");
        tFilter.Update();

        vtkSTLWriter write = new vtkSTLWriter();
        // write.SetFileTypeToASCII();
        write.SetFileName(transformedSolidPath.toString());
        write.SetInputData(tFilter.GetOutput());
        // write.AddObserver("ProgressEvent", new ReaderProgress(write, monitor), "progress");
        write.Write();

        return transformedSolidPath;
    }
}
