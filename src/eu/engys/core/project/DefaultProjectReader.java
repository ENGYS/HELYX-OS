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
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.project.geometry.factory.DefaultGeometryFactory;
import eu.engys.core.project.materials.MaterialsReader;
import eu.engys.core.project.state.StateBuilder;
import eu.engys.core.project.state.Table15;
import eu.engys.core.project.system.ControlDict;
import eu.engys.core.project.system.fieldmanipulationfunctionobjects.FieldManipulationFunctionObjectType;
import eu.engys.core.project.system.monitoringfunctionobjects.MonitoringFunctionObjectType;
import eu.engys.core.project.zero.cellzones.CellZonesBuilder;
import eu.engys.core.project.zero.fields.Initialisations;
import eu.engys.util.progress.ProgressMonitor;

public class DefaultProjectReader extends AbstractProjectReader {

    private static final Logger logger = LoggerFactory.getLogger(ProjectReader.class);
    private Set<ApplicationModule> modules;
    private Set<FieldManipulationFunctionObjectType> ffoTypes;
    private Set<MonitoringFunctionObjectType> mfoTypes;
    private final Initialisations initialisation;
    private MaterialsReader materialsReader;
    private Table15 solversTable;
    private CellZonesBuilder cellZoneBuilder;

    @Inject
    public DefaultProjectReader(Model model, Table15 solversTable, MaterialsReader materialsReader, CellZonesBuilder cellZoneBuilder, Set<ApplicationModule> modules, Set<FieldManipulationFunctionObjectType> ffoTypes, Set<MonitoringFunctionObjectType> mfoTypes, Initialisations initialisation, ProgressMonitor monitor) {
        super(model, monitor);
        this.solversTable = solversTable;
        this.materialsReader = materialsReader;
        this.cellZoneBuilder = cellZoneBuilder;
        this.modules = modules;
        this.initialisation = initialisation;
        this.ffoTypes = ffoTypes;
        this.mfoTypes = mfoTypes;
    }

    @Override
    public void read() throws InvalidProjectException {
        File baseDir = model.getProject().getBaseDir();
        logger.info("################## Read '{}' ################## ", baseDir.getName());
        if (baseDir.exists() && baseDir.isDirectory()) {
            defaultRead();
            for (ProjectReader reader : readers) {
                reader.read();
            }
            DefaultGeometryFactory.clearSTLCache();
        } else {
            monitor.error(baseDir + " not found");
        }
        logger.info("################## End Read ################## ");
    }

    @Override
    public void readMesh() {
        File baseDir = model.getProject().getBaseDir();
        if (baseDir.exists() && baseDir.isDirectory()) {
            openFOAMProject prj = model.getProject();
            ControlDict controlDict = prj.getSystemFolder().getControlDict();
            if (controlDict != null) {
                if (controlDict.isBinary()) {
                    monitor.error("Binary fields format not supported");
                } else {
                    logger.info("### Read mesh: '{}' ### ", prj.getZeroFolder().getFileManager().getFile());
                    prj.getZeroFolder().read(model, cellZoneBuilder, modules, initialisation, monitor);
                }
            }

            if (!model.getPatches().isEmpty()) {
                model.getGeometry().hideSurfaces();
            }
        } else {
            monitor.error(baseDir + " not found");
        }
    }

    protected void defaultRead() throws InvalidProjectException {
        monitor.info("");
        monitor.info("Reading Project");
        openFOAMProject project = model.getProject();

        monitor.info("-> Reading Constant Folder");
        project.getConstantFolder().load(model, monitor);

        monitor.info("-> Reading System Folder");
        project.getSystemFolder().read(model, ffoTypes, mfoTypes, monitor);

        new SolverModelReader(model).load();

        monitor.info("-> Reading Geometry");
        model.getGeometry().loadGeometry(model, monitor);

        monitor.info("-> Reading State");
        StateBuilder.loadState(model, solversTable, monitor);
        solversTable.updateSolver(model.getState());

        /*
         * Call updateSolver after loadState because some module may need some other module state in order to select the correct solver (e.g. Dynamic and VOF)
         */
        monitor.info("-> Reading Modules State");
        ModulesUtil.loadState(modules);
        ModulesUtil.updateSolver(modules, model.getState());

        monitor.info("-> Reading Materials");
        model.getMaterials().loadMaterials(model, materialsReader, monitor);
        ModulesUtil.loadMaterials(modules);

        monitor.info("-> Reading Zero Folder");
        ControlDict controlDict = project.getSystemFolder().getControlDict();
        if (controlDict != null) {
            if (controlDict.isBinary()) {
                monitor.error("Binary fields format not supported", 1);
            } else {
                project.getZeroFolder().read(model, cellZoneBuilder, modules, initialisation, monitor);
            }
        } else {
            monitor.error("No control dict found", 1);
        }

        if (!model.getPatches().isEmpty()) {
            model.getGeometry().hideSurfaces();
        }
    }

}
