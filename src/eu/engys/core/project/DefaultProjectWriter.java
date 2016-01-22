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


package eu.engys.core.project;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.project.materials.MaterialsWriter;
import eu.engys.core.project.zero.cellzones.CellZonesBuilder;
import eu.engys.core.project.zero.fields.Initialisations;
import eu.engys.util.progress.ProgressMonitor;

public class DefaultProjectWriter extends AbstractProjectWriter {

    protected static final Logger logger = LoggerFactory.getLogger(ProjectWriter.class);
    private MaterialsWriter materialsWriter;
    private Initialisations initialisations;
    private CellZonesBuilder cellZoneBuilder;

    @Inject
    public DefaultProjectWriter(Model model, MaterialsWriter materialsWriter, CellZonesBuilder cellZoneBuilder, Set<ApplicationModule> modules, Initialisations initialisations, ProgressMonitor monitor) {
        super(model, modules, monitor);
        this.materialsWriter = materialsWriter;
        this.cellZoneBuilder = cellZoneBuilder;
        this.initialisations = initialisations;
    }

    @Override
    public void create(CaseParameters params) {
        openFOAMProject project = new CreateCase(model.getDefaults(), monitor).create(params);
        model.setProject(project);
        if (params.isParallel()) {
            model.getFields().newParallelFields(project.getProcessors());
            model.getPatches().newParallelPatches(project.getProcessors());
        }

        write(project.getBaseDir());
        for (ProjectWriter writer : writers) {
            writer.create(params);
        }
    }

    @Override
    public void write(File baseDir) {
        logger.info("################## Write '{}' ################## ", baseDir.getName());
        monitor.info("");
        monitor.info("Saving Project");
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        openFOAMProject oldProject = model.getProject();
        boolean isSaveAs = !baseDir.getAbsoluteFile().equals(oldProject.getBaseDir().getAbsoluteFile());
        if (isSaveAs) {
            CreateCase.deleteAll(baseDir, oldProject.isParallel(), oldProject.getProcessors());
            makeACopy(baseDir);
            setNewProject(baseDir);
        }

        writeFoamFile(baseDir);

        model.getGeometry().writeGeometry(model, monitor);
        
        model.getCustom().saveCustomDict(model);

        new SolverModelWriter(model).save();

        openFOAMProject project = model.getProject();

        monitor.info("-> Saving Zero Folder");
        project.getZeroFolder().write(model, cellZoneBuilder, modules, initialisations, monitor);

        monitor.info("-> Saving Constant Folder");
        project.getConstantFolder().write(model, materialsWriter, monitor);

        monitor.info("-> Saving System Folder");
        project.getSystemFolder().write(model, monitor);
        
        monitor.info("-> Saving Modules");
        for (ApplicationModule m : modules) {
            monitor.info(m.getName(), 1);
            m.write();
        }

        for (ProjectWriter writer : writers) {
            writer.write(baseDir);
        }

        File logFolder = new File(baseDir, "log");
        if (!logFolder.exists()) {
            logFolder.mkdir();
        }

        monitor.info("-> Saving Custom");
        model.getCustom().write(model, monitor);

        logger.info("################## End Write ############################## ");
    }

    private void makeACopy(File baseDir) {
        File srcDir = model.getProject().getBaseDir();

        boolean indeterminate = monitor.isIndeterminate();
        monitor.setIndeterminate(true);

        monitor.info(String.format("Copy: %s -> %s", srcDir.getName(), baseDir.getName()));
        logger.info("Copy: {} -> {}", srcDir.getName(), baseDir.getName());

        try {
            FileUtils.copyDirectory(srcDir, baseDir);
        } catch (IOException e) {
            monitor.error("Error copying folder");
            monitor.error(e.getMessage());
        }

        if (!indeterminate) {
            monitor.setIndeterminate(false);
        }

    }

    private void setNewProject(File baseDir) {
        openFOAMProject prj = openFOAMProject.newCopy(baseDir, model.getProject());
        model.setProject(prj);
    }

    private void writeFoamFile(File baseDir) {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".foam");
            }
        };
        File[] foamFiles = baseDir.listFiles(filter);

        if (foamFiles.length > 0) {
            for (int i = 0; i < foamFiles.length; i++) {
                FileUtils.deleteQuietly(foamFiles[i]);
            }
        }

        File foamFile = new File(baseDir, baseDir.getName() + ".foam");
        try {
            foamFile.createNewFile();
        } catch (IOException e) {
        }
    }

}
