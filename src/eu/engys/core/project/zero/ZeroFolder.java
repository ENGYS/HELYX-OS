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
package eu.engys.core.project.zero;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.defaults.DefaultsProvider;
import eu.engys.core.project.files.Folder;
import eu.engys.core.project.state.State;
import eu.engys.core.project.zero.cellzones.CellZones;
import eu.engys.core.project.zero.cellzones.CellZonesBuilder;
import eu.engys.core.project.zero.cellzones.CellZonesReader;
import eu.engys.core.project.zero.cellzones.CellZonesWriter;
import eu.engys.core.project.zero.facezones.FaceZones;
import eu.engys.core.project.zero.facezones.FaceZonesReader;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.fields.FieldsDefaults;
import eu.engys.core.project.zero.fields.FieldsReader;
import eu.engys.core.project.zero.fields.FieldsWriter;
import eu.engys.core.project.zero.fields.Initialisations;
import eu.engys.core.project.zero.patches.BoundaryConditionsDefaults;
import eu.engys.core.project.zero.patches.Patches;
import eu.engys.core.project.zero.patches.PatchesReader;
import eu.engys.core.project.zero.patches.PatchesWriter;
import eu.engys.util.progress.ConsoleMonitor;
import eu.engys.util.progress.ProgressMonitor;

public class ZeroFolder implements Folder {

    public static final String ZERO = "0";
    public static final String POLY_MESH = "polyMesh";

    private static final Logger logger = LoggerFactory.getLogger(ZeroFolder.class);

    private ZeroFileManager zeroFileManager;

    private Map<String, MeshRegion> regions = new HashMap<>();

    private String timeValue = "0";

    public ZeroFolder(openFOAMProject prj) {
        zeroFileManager = prj.isParallel() ? new ParallelZeroFileManager(prj.getBaseDir(), prj.getProcessors()) : new SerialZeroFileManager(prj.getBaseDir());
    }

    public ZeroFolder(File baseDir, ZeroFolder zeroFolder) {
        if (zeroFolder.getFileManager() instanceof ParallelZeroFileManager) {
            ParallelZeroFileManager pZero = (ParallelZeroFileManager) zeroFolder.getFileManager();
            zeroFileManager = new ParallelZeroFileManager(baseDir, pZero.getZeroDirs(zeroFolder.getTimeValue()).length);
        } else {
            zeroFileManager = new SerialZeroFileManager(baseDir);
        }
        this.timeValue = zeroFolder.getTimeValue();
    }

    public void read(Model model, CellZonesBuilder builder, Set<ApplicationModule> modules, Initialisations initialisations, ProgressMonitor monitor) {
        try {
            timeValue = zeroFileManager.findTimeValue(model.getProject().getSystemFolder().getControlDict());

            monitor.info("Time: " + timeValue, 1);

            File[] zeroDirs = zeroFileManager.getZeroDirs("0");
            File[] timeDirs = zeroFileManager.getZeroDirs(timeValue);
            File[] polyMeshes = zeroFileManager.getPolyMeshDirs(zeroDirs);
            File[] boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);
            File[] cellZonesFiles = zeroFileManager.getCellZonesFiles(polyMeshes);
            File[] faceZonesFiles = zeroFileManager.getFaceZonesFiles(polyMeshes);
            String[] regionNames = ZeroFolderUtil.getRegions(zeroDirs);

            if (!ZeroFolderUtil.exists(boundaryFiles)) {
                File[] constantDirs = zeroFileManager.getConstantDirs();
                polyMeshes = zeroFileManager.getPolyMeshDirs(constantDirs);
                boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);
                cellZonesFiles = zeroFileManager.getCellZonesFiles(polyMeshes);
                faceZonesFiles = zeroFileManager.getFaceZonesFiles(polyMeshes);
                regionNames = ZeroFolderUtil.getRegions(constantDirs);
            }

            monitor.setIndeterminate(false);

            model.setPatches(readPatches(model, monitor, boundaryFiles));
            model.setCellZones(readCellZones(model, builder, monitor, cellZonesFiles, modules));
            model.setFaceZones(readFaceZones(monitor, faceZonesFiles));
            model.setFields(readFields(null, model.getProject(), model.getState(), model.getDefaults(), model.getPatches(), modules, initialisations, monitor, timeDirs, boundaryFiles));

            BoundaryConditionsDefaults.loadBoundaryConditionsFromFields(model.getPatches(), model.getFields());

            regions.clear();
            if (regionNames != null && regionNames.length > 0) {
                logger.info("REGIONS: found regions {}", Arrays.toString(regionNames));
                for (String regionName : regionNames) {
                    zeroDirs = zeroFileManager.getZeroDirs("0");
                    timeDirs = zeroFileManager.getZeroDirs(timeValue);
                    File[] regionTimeDirs = zeroFileManager.getRegionDirs(regionName, timeDirs);

                    ZeroFolderUtil.mkDirs(regionTimeDirs);

                    File[] regionDirs = zeroFileManager.getRegionDirs(regionName, zeroDirs);
                    polyMeshes = zeroFileManager.getPolyMeshDirs(regionDirs);
                    boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);
                    cellZonesFiles = zeroFileManager.getCellZonesFiles(polyMeshes);
                    faceZonesFiles = zeroFileManager.getFaceZonesFiles(polyMeshes);

                    if (!ZeroFolderUtil.exists(boundaryFiles)) {
                        File[] constantDirs = zeroFileManager.getConstantDirs();
                        regionDirs = zeroFileManager.getRegionDirs(regionName, constantDirs);
                        polyMeshes = zeroFileManager.getPolyMeshDirs(regionDirs);
                        boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);
                        cellZonesFiles = zeroFileManager.getCellZonesFiles(polyMeshes);
                        faceZonesFiles = zeroFileManager.getFaceZonesFiles(polyMeshes);
                    }

                    Patches patches = readPatches(model, monitor, boundaryFiles);
                    Fields fields = readFields(regionName, model.getProject(), model.getState(), model.getDefaults(), patches, modules, initialisations, monitor, regionTimeDirs, boundaryFiles);
                    MeshRegion region = new MeshRegion();
                    region.setPatches(patches);
                    region.setFields(fields);

                    BoundaryConditionsDefaults.loadBoundaryConditionsFromFields(patches, fields);
                    regions.put(regionName, region);
                }
            }
        } catch (Exception e) {
            logger.error("Error in load", e);
            monitor.error(e.getMessage(), 1);
        }
        model.patchesChanged();
        model.cellZonesChanged();
        model.faceZonesChanged();
    }

    public Patches readPatches(Model model) {
        File[] zeroDirs = zeroFileManager.getZeroDirs("0");
        File[] polyMeshes = zeroFileManager.getPolyMeshDirs(zeroDirs);
        File[] boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);

        if (!ZeroFolderUtil.exists(boundaryFiles)) {
            File[] constantDirs = zeroFileManager.getConstantDirs();
            polyMeshes = zeroFileManager.getPolyMeshDirs(constantDirs);
            boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);
        }

        return readPatches(model, new ConsoleMonitor(), boundaryFiles);
    }

    public Patches readPatches(Model model, String regionName) {
        File[] zeroDirs = zeroFileManager.getZeroDirs("0");
        File[] regionDirs = zeroFileManager.getRegionDirs(regionName, zeroDirs);
        File[] polyMeshes = zeroFileManager.getPolyMeshDirs(regionDirs);
        File[] boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);

        if (!ZeroFolderUtil.exists(boundaryFiles)) {
            File[] constantDirs = zeroFileManager.getConstantDirs();
            regionDirs = zeroFileManager.getRegionDirs(regionName, constantDirs);
            polyMeshes = zeroFileManager.getPolyMeshDirs(regionDirs);
            boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);
        }

        return readPatches(model, new ConsoleMonitor(), boundaryFiles);
    }

    private Patches readPatches(Model model, ProgressMonitor monitor, File[] boundaryFiles) {
        if (ZeroFolderUtil.exists(boundaryFiles)) {
            monitor.setCurrent("Reading patches", 0, boundaryFiles.length, 1);
            Patches patches = new PatchesReader(model, monitor).read(boundaryFiles);
            monitor.info("Patches: " + patches.filterProcBoundary().patchesNames().toString(), 1);
            return patches;
        } else {
            monitor.warning("Missing boundary fields", 1);
            logger.warn(Arrays.toString(boundaryFiles) + " does not exist");
            return new Patches();
        }
    }

    private CellZones readCellZones(Model model, CellZonesBuilder builder, ProgressMonitor monitor, File[] cellZonesFiles, Set<ApplicationModule> modules) {
        if (ZeroFolderUtil.exists(cellZonesFiles)) {
            monitor.setCurrent("Reading cell zones", 0, cellZonesFiles.length, 1);
            CellZones cellZones = new CellZonesReader(model, builder, modules, monitor).read(cellZonesFiles);
            monitor.info("Cell Zones: " + cellZones.zonesNames().toString(), 1);
            return cellZones;
        } else {
            monitor.warning("Missing cellZones file", 1);
            logger.warn(Arrays.toString(cellZonesFiles) + " does not exist");
            return new CellZones();
        }
    }

    private FaceZones readFaceZones(ProgressMonitor monitor, File[] faceZonesFiles) {
        if (ZeroFolderUtil.exists(faceZonesFiles)) {
            monitor.setCurrent("Reading face zones", 0, faceZonesFiles.length, 1);
            FaceZones faceZones = new FaceZonesReader(monitor).read(faceZonesFiles);
            monitor.info("Face Zones: " + faceZones.zonesNames().toString(), 1);
            return faceZones;
        } else {
            monitor.warning("Missing faceZones file", 1);
            logger.warn(Arrays.toString(faceZonesFiles) + " does not exist");
            return new FaceZones();
        }
    }

    private Fields readFields(String region, openFOAMProject prj, State state, DefaultsProvider defaults, Patches patches, Set<ApplicationModule> modules, Initialisations initialisations, ProgressMonitor monitor, File[] timeDirs, File[] boundaryFiles) {
        if (initialisations != null) {
            if (ZeroFolderUtil.exists(timeDirs)) {
                if (ZeroFolderUtil.exists(boundaryFiles)) {
                    Fields fields = new Fields();
                    if (prj.isParallel()) {
                        fields.newParallelFields(prj.getProcessors());
                    }
                    logger.debug("Loading fields from defaults");
                    fields.merge(FieldsDefaults.loadFieldsFromDefaults(prj.getBaseDir(), state, defaults, patches, region));
                    logger.debug("Loading fields from MODULE defaults");
                    fields.merge(ModulesUtil.loadFieldsFromDefaults(modules, region));
                    logger.debug("Reading field from case");
                    fields.merge(new FieldsReader(initialisations, monitor).read(fields.keySet(), timeDirs));
                    fields.fixPVisibility(state);

                    monitor.info("Fields: " + fields.fieldNames().toString(), 1);
                    return fields;
                } else {
                    monitor.warning("Missing fields", 1);
                    logger.warn("Boundary Files " + Arrays.toString(boundaryFiles) + " does not exist");
                    return new Fields();
                }
            } else {
                monitor.warning("Missing fields", 1);
                logger.warn("Time Folders " + Arrays.toString(timeDirs) + " does not exist");
                return new Fields();
            }
        } else {
            monitor.warning("Missing fields", 1);
            logger.warn("No initialisation");
            return new Fields();
        }
    }

    /*
     * Write
     */

    public void write(Model model, CellZonesBuilder cellZonesBuilder, Set<ApplicationModule> modules, Initialisations initialisations, ProgressMonitor monitor) {
        if (avoidSave(model))
            return;

        try {
            String currentTimeValue = zeroFileManager.findTimeValue(model.getProject().getSystemFolder().getControlDict());
            boolean timeStepHasChanged = !currentTimeValue.equals(timeValue);
            if (timeStepHasChanged) {
                logger.info("Timestep has changed {} -> {}", timeValue, currentTimeValue);
                this.timeValue = currentTimeValue;
            }

            File[] zeroDirs = zeroFileManager.getZeroDirs("0");
            File[] timeDirs = zeroFileManager.getZeroDirs(timeValue);
            File[] polyMeshes = zeroFileManager.getPolyMeshDirs(zeroDirs);
            File[] boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);
            File[] cellZonesFiles = zeroFileManager.getCellZonesFiles(polyMeshes);

            if (!ZeroFolderUtil.exists(boundaryFiles)) {
                File[] constantDirs = zeroFileManager.getConstantDirs();
                polyMeshes = zeroFileManager.getPolyMeshDirs(constantDirs);
                boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);
                cellZonesFiles = zeroFileManager.getCellZonesFiles(polyMeshes);
            }

            writePatches(model.getPatches(), model, monitor, boundaryFiles);
            writeCellZones(model, cellZonesBuilder, monitor, cellZonesFiles, modules);
            writeFields(model.getFields(), model.getPatches(), modules, initialisations, monitor, timeDirs, boundaryFiles, timeStepHasChanged);

            if (!regions.isEmpty()) {
                for (String regionName : regions.keySet()) {
                    MeshRegion region = regions.get(regionName);

                    zeroDirs = zeroFileManager.getZeroDirs("0");
                    File[] regionTimeDirs = zeroFileManager.getRegionDirs(regionName, timeDirs);
                    File[] regionDirs = zeroFileManager.getRegionDirs(regionName, zeroDirs);
                    polyMeshes = zeroFileManager.getPolyMeshDirs(regionDirs);
                    boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);
                    cellZonesFiles = zeroFileManager.getCellZonesFiles(polyMeshes);

                    if (!ZeroFolderUtil.exists(boundaryFiles)) {
                        File[] constantDirs = zeroFileManager.getConstantDirs();
                        regionDirs = zeroFileManager.getRegionDirs(regionName, constantDirs);
                        polyMeshes = zeroFileManager.getPolyMeshDirs(regionDirs);
                        boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);
                        cellZonesFiles = zeroFileManager.getCellZonesFiles(polyMeshes);
                    }

                    writePatches(region.getPatches(), model, monitor, boundaryFiles);
                    writeFields(region.getFields(), region.getPatches(), modules, initialisations, monitor, regionTimeDirs, boundaryFiles, timeStepHasChanged);
                }
            }
        } catch (Exception e) {
            logger.error("Error in write", e);
            monitor.error("Zero folder error: " + e.getMessage(), 2);
        }
    }

    public void writePatches(Model model, Patches patches) {
        File[] zeroDirs = zeroFileManager.getZeroDirs("0");
        File[] polyMeshes = zeroFileManager.getPolyMeshDirs(zeroDirs);
        File[] boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);

        if (!ZeroFolderUtil.exists(boundaryFiles)) {
            File[] constantDirs = zeroFileManager.getConstantDirs();
            polyMeshes = zeroFileManager.getPolyMeshDirs(constantDirs);
            boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);
        }

        writePatches(patches, model, new ConsoleMonitor(), boundaryFiles);
    }

    public void writePatches(Model model, Patches patches, String regionName) {
        File[] zeroDirs = zeroFileManager.getZeroDirs("0");
        File[] regionDirs = zeroFileManager.getRegionDirs(regionName, zeroDirs);
        File[] polyMeshes = zeroFileManager.getPolyMeshDirs(regionDirs);
        File[] boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);

        if (!ZeroFolderUtil.exists(boundaryFiles)) {
            File[] constantDirs = zeroFileManager.getConstantDirs();
            regionDirs = zeroFileManager.getRegionDirs(regionName, constantDirs);
            polyMeshes = zeroFileManager.getPolyMeshDirs(regionDirs);
            boundaryFiles = zeroFileManager.getBoundaryFiles(polyMeshes);
        }

        writePatches(patches, model, new ConsoleMonitor(), boundaryFiles);
    }

    private void writePatches(Patches patches, Model model, ProgressMonitor monitor, File[] boundaryFiles) {
        if (ZeroFolderUtil.exists(boundaryFiles)) {
            monitor.setCurrent("Saving patches", 0, boundaryFiles.length, 1);
            new PatchesWriter(model, monitor).write(patches, boundaryFiles);
        } else {
            monitor.warning("Missing boundary fields", 1);
            logger.warn(Arrays.toString(boundaryFiles) + " does not exist");
        }
    }

    private void writeCellZones(Model model, CellZonesBuilder builder, ProgressMonitor monitor, File[] cellZonesFiles, Set<ApplicationModule> modules) {
        CellZonesWriter cellZonesWriter = new CellZonesWriter(builder, modules, monitor);
        cellZonesWriter.writeFvOptions(model);
        if (ZeroFolderUtil.exists(cellZonesFiles)) {
            monitor.setCurrent("Saving cell zones", 0, cellZonesFiles.length, 1);
            cellZonesWriter.writeCellZoneFiles(model, cellZonesFiles);
            monitor.info("Cell Zones: " + model.getCellZones().zonesNames().toString(), 1);
        } else {
            monitor.warning("Missing cellZones file", 1);
            logger.warn(Arrays.toString(cellZonesFiles) + " does not exist");
        }
    }

    private void writeFields(Fields fields, Patches patches, Set<ApplicationModule> modules, Initialisations initialisations, ProgressMonitor monitor, File[] timeDirs, File[] boundaryFiles, boolean timeStepHasChanged) {
        if (initialisations != null && ZeroFolderUtil.exists(timeDirs) && ZeroFolderUtil.exists(boundaryFiles)) {
            if (timeStepHasChanged) {
                monitor.setCurrent("Re-Reading fields", 0, timeDirs.length, 1);
                fields = new FieldsReader(initialisations, monitor).read(fields.keySet(), timeDirs);
                BoundaryConditionsDefaults.fieldsToBoundaryConditions(patches, fields);
            }
            monitor.setCurrent("Saving fields", 0, timeDirs.length, 1);
            BoundaryConditionsDefaults.saveBoundaryConditionsToFields(patches, fields);
            new FieldsWriter(monitor).write(fields, timeDirs);
            monitor.info("Fields: " + fields.fieldNames().toString(), 1);
        } else {
            monitor.warning("Missing fields", 1);
            logger.warn(Arrays.toString(timeDirs) + " does not exist");
        }
    }

    String getTimeValue() {
        return timeValue;
    }

    @Override
    public ZeroFileManager getFileManager() {
        return zeroFileManager;
    }

    public void deleteMesh() {
        regions.clear();
        zeroFileManager.deleteAll();
        if (zeroFileManager instanceof ParallelZeroFileManager) {
            new SerialZeroFileManager(zeroFileManager.getFile()).deleteAll();
        }
    }

    public void deleteFields() {
        zeroFileManager.clearZeroDirs("0");
        zeroFileManager.removeNonZeroDirs("0");
    }

    protected boolean avoidSave(Model model) {
        return model.getCellZones().isEmpty() && model.getPatches().isEmpty() && model.getFields().isEmpty();
    }

    public void removeNonZeroTimeFolders_GreaterThanActualTimeStep() {
        zeroFileManager.removeNonZeroDirs(timeValue);
    }

    public boolean hasNonZeroTimeFolders() {
        return zeroFileManager.getNonZeroDirs("0").length > 0;
    }

    public MeshRegion getRegion(String regionName) {
        return regions.get(regionName);
    }

    public boolean hasRegion(String regionName) {
        return regions.containsKey(regionName);
    }

    // For tests purposes only!!!
    public void setTimeValue(String timeValue) {
        this.timeValue = timeValue;
    }

    public boolean hasRegions() {
        return !regions.isEmpty();
    }
}
