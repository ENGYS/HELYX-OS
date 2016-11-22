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
package eu.engys.core.project;

import static eu.engys.core.controller.AbstractScriptFactory.MESH_PARALLEL_BAT;
import static eu.engys.core.controller.AbstractScriptFactory.MESH_PARALLEL_RUN;
import static eu.engys.core.controller.AbstractScriptFactory.MESH_SERIAL_BAT;
import static eu.engys.core.controller.AbstractScriptFactory.MESH_SERIAL_RUN;
import static eu.engys.core.controller.AbstractScriptFactory.SOLVER_PARALLEL_BAT;
import static eu.engys.core.controller.AbstractScriptFactory.SOLVER_PARALLEL_RUN;
import static eu.engys.core.controller.AbstractScriptFactory.SOLVER_SERIAL_BAT;
import static eu.engys.core.controller.AbstractScriptFactory.SOLVER_SERIAL_RUN;
import static eu.engys.core.project.openFOAMProject.HOSTFILE;
import static eu.engys.core.project.openFOAMProject.MACHINEFILE;
import static eu.engys.core.project.system.ControlDict.CONTROL_DICT;
import static eu.engys.core.project.system.ControlDict.FUNCTIONS_KEY;
import static eu.engys.core.project.system.RunDict.HOSTFILE_PATH;
import static eu.engys.core.project.system.RunDict.LOG_FILE;
import static eu.engys.core.project.system.RunDict.RMI_PORT;
import static eu.engys.core.project.system.RunDict.RUN_DICT;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.dictionary.FoamFile;
import eu.engys.core.dictionary.ListField;
import eu.engys.core.project.constant.ConstantFolder;
import eu.engys.core.project.constant.ThermophysicalProperties;
import eu.engys.core.project.materials.Materials200To210Converter;
import eu.engys.core.project.system.CustomNodeDict;
import eu.engys.core.project.system.SnappyHexMeshDict;
import eu.engys.core.project.system.SystemFolder;
import eu.engys.core.project.zero.cellzones.CellZones;
import eu.engys.core.project.zero.cellzones.CellZones200To210Converter;
import eu.engys.core.project.zero.cellzones.CellZonesBuilder;
import eu.engys.util.Util;
import eu.engys.util.progress.SilentMonitor;

public class Project200To210Converter {

    private static final Logger logger = LoggerFactory.getLogger(Project200To210Converter.class);

    private final SilentMonitor monitor = new SilentMonitor();

    public static final String POROUS_ZONES = "porousZones";
    public static final String MRF_ZONES = "MRFZones";

    private openFOAMProject project;
    private CellZonesBuilder cellZonesBuilder;

    public Project200To210Converter(openFOAMProject project, CellZonesBuilder cellZonesBuilder) {
        this.project = project;
        this.cellZonesBuilder = cellZonesBuilder;
    }

    public void convert() {
        ConstantFolder constantFolder = project.getConstantFolder();
        SystemFolder systemFolder = project.getSystemFolder();

        convertCellZones(cellZonesBuilder, constantFolder, systemFolder);
        convertThermophysicalProperties(constantFolder);
        convertSnappyHexMeshDict(systemFolder);
        convertRunDict(project);
        convertHELYXDict(project);
        convertForcesPostProcMapEntry(project);
        convertMeshAndSolverScriptName(project);
        moveFunctionObjectLogs(project);
    }

    private void convertHELYXDict(openFOAMProject project) {
        SystemFolder systemFolder = project.getSystemFolder();
        File helyxDictFile = systemFolder.getFileManager().getFile("HELYXDict");
        if (helyxDictFile.exists()) {
            CustomNodeDict customDict = new CustomNodeDict(helyxDictFile);
            DictionaryUtils.writeDictionary(systemFolder.getFileManager().getFile(), DictionaryUtils.header("system", customDict), monitor);
            FileUtils.deleteQuietly(helyxDictFile);
        }
    }

    private void convertRunDict(openFOAMProject project) {
        SystemFolder systemFolder = project.getSystemFolder();
        File runDictFile = systemFolder.getFileManager().getFile(RUN_DICT);
        if (runDictFile.exists()) {
            Dictionary runDict = DictionaryUtils.readDictionary(runDictFile, monitor);

            boolean writeNeed = false;
            // BAD STRUCTURE
            if (runDict.isDictionary(RUN_DICT)) {
                runDict = new Dictionary(runDict.subDict(RUN_DICT));
                writeNeed = true;
            }
            // DELETE OLD FUNCTION OBJECTS MAP
            if (runDict.found("logFileMap")) {
                runDict.remove("logFileMap");
                writeNeed = true;
            }
            if (runDict.found("postProcFileMap")) {
                runDict.remove("postProcFileMap");
                writeNeed = true;
            }
            // RMI PORT FIX
            if (runDict.found(RMI_PORT)) {
                int rmiPort = runDict.lookupInt(RMI_PORT);
                if (rmiPort < 20000) {
                    runDict.add(RMI_PORT, String.valueOf(20001));
                    writeNeed = true;
                }
            }
            // LOG FILE NAME FIX
            if (runDict.found(RMI_PORT)) {
                String logName = runDict.lookup(LOG_FILE);
                if (logName != null) {
                    File logFile = new File(logName);
                    if (!logName.isEmpty() && logFile.isAbsolute()) {
                        runDict.add(LOG_FILE, logFile.getName());
                        writeNeed = true;
                    }
                }
            }
            String hostfilePath = runDict.lookup(HOSTFILE_PATH);
            if (hostfilePath == null || hostfilePath.isEmpty()) {
                fixHostFilePath(project, runDict);
                writeNeed = true;
            }
            if (writeNeed) {
                DictionaryUtils.writeDictionary(systemFolder.getFileManager().getFile(), DictionaryUtils.header("system", runDict), monitor);
            }
        }
    }

    private void fixHostFilePath(openFOAMProject project, Dictionary runDict) {
        File baseDir = project.getBaseDir();
        if (new File(baseDir, HOSTFILE).exists()) {
            runDict.add(HOSTFILE_PATH, HOSTFILE);
        } else if (new File(baseDir, MACHINEFILE).exists()) {
            runDict.add(HOSTFILE_PATH, MACHINEFILE);
        } else if (new File(project.getSystemFolder().getFileManager().getFile(), HOSTFILE).exists()) {
            runDict.add(HOSTFILE_PATH, SystemFolder.SYSTEM + "/" + HOSTFILE);
        } else if (new File(project.getSystemFolder().getFileManager().getFile(), MACHINEFILE).exists()) {
            runDict.add(HOSTFILE_PATH, SystemFolder.SYSTEM + "/" + MACHINEFILE);
        } else {
            runDict.add(HOSTFILE_PATH, HOSTFILE);
        }
    }

    private void convertForcesPostProcMapEntry(openFOAMProject project) {
        File runDictFile = project.getSystemFolder().getFileManager().getFile(RUN_DICT);
        if (runDictFile.exists()) {
            Dictionary runDict = DictionaryUtils.readDictionary(runDictFile, monitor);
            boolean writeNeed = false;

            if (writeNeed) {
                DictionaryUtils.writeDictionary(project.getSystemFolder().getFileManager().getFile(), DictionaryUtils.header("system", runDict), monitor);
            }
        }
    }

    private void convertMeshAndSolverScriptName(openFOAMProject project) {
        File baseDir = project.getBaseDir();

        String parallelIdentifier = Util.isWindows() ? "mpiexec -n" : "mpirun -np";

        try {
            File solverScript = new File(baseDir, Util.isWindows() ? "solver.bat" : "solver.run");
            if (solverScript.exists()) {
                if (FileUtils.readFileToString(solverScript).contains(parallelIdentifier)) {
                    solverScript.renameTo(new File(baseDir, Util.isWindows() ? SOLVER_PARALLEL_BAT : SOLVER_PARALLEL_RUN));
                } else {
                    solverScript.renameTo(new File(baseDir, Util.isWindows() ? SOLVER_SERIAL_BAT : SOLVER_SERIAL_RUN));
                }
            }

            File meshScript = new File(baseDir, Util.isWindows() ? "mesh.bat" : "mesh.run");
            if (meshScript.exists()) {
                if (FileUtils.readFileToString(meshScript).contains(parallelIdentifier)) {
                    meshScript.renameTo(new File(baseDir, Util.isWindows() ? MESH_PARALLEL_BAT : MESH_PARALLEL_RUN));
                } else {
                    meshScript.renameTo(new File(baseDir, Util.isWindows() ? MESH_SERIAL_BAT : MESH_SERIAL_RUN));
                }
            }
        } catch (IOException e) {
            logger.error("Could not rename script files");
        }
    }

    private void moveFunctionObjectLogs(openFOAMProject project) {
        setupPostProcFolder(project);
        File controlDictFile = project.getSystemFolder().getFileManager().getFile(CONTROL_DICT);
        if (controlDictFile.exists()) {
            Dictionary controlDict = DictionaryUtils.readDictionary(controlDictFile, monitor);
            boolean hasFunctionObjects = controlDict.isList(FUNCTIONS_KEY);
            if (hasFunctionObjects) {
                ListField fos = controlDict.getList(FUNCTIONS_KEY);
                String baseDirPath = project.getBaseDir().getAbsolutePath();
                for (DefaultElement element : fos.getListElements()) {
                    if (element instanceof Dictionary) {
                        Dictionary foDict = (Dictionary) element;
                        if (foDict.found(Dictionary.TYPE)) {
                            String type = foDict.lookup(Dictionary.TYPE);
                            switch (type) {
                                case "liftDrag":
                                    moveLiftDragFunctionObject(baseDirPath, foDict);
                                    break;
                                case "volumeReport":
                                    moveVolumeReportFunctionObject(baseDirPath, foDict);
                                    break;
                                case "forces":
                                    moveForcesFunctionObject(baseDirPath, foDict);
                                    break;
                                default:
                                    break;
                            }
                        }

                    }
                }
            }
        }
    }

    private void moveLiftDragFunctionObject(String baseDirPath, Dictionary foDict) {
        String foName = foDict.getName();
        File logFolder = Paths.get(baseDirPath, openFOAMProject.LOG).toFile();
        if (logFolder.isDirectory() && logFolder.exists()) {
            for (File child : logFolder.listFiles()) {
                String fileName = child.getName();
                if (fileName.startsWith(foName) && fileName.endsWith(".dat")) {
                    File postProcFolder = Paths.get(baseDirPath, openFOAMProject.POST_PROC).toFile();
                    File functionObjectFolder = new File(postProcFolder, foName);
                    try {
                        FileUtils.moveFileToDirectory(child, functionObjectFolder, true);
                    } catch (IOException e) {
                        logger.error("Could not move {} to {}", child, functionObjectFolder);
                    }
                }
            }
        }
    }

    private void moveVolumeReportFunctionObject(String baseDirPath, Dictionary foDict) {
        String foName = foDict.getName();
        File logFolder = Paths.get(baseDirPath, openFOAMProject.LOG).toFile();
        if (logFolder.isDirectory() && logFolder.exists()) {
            for (File child : logFolder.listFiles()) {
                String fileName = child.getName();
                if (fileName.startsWith(foName + "_volumeStatistics.")) {
                    File postProcFolder = Paths.get(baseDirPath, openFOAMProject.POST_PROC).toFile();
                    File functionObjectFolder = new File(postProcFolder, foName);
                    try {
                        FileUtils.moveFileToDirectory(child, functionObjectFolder, true);
                    } catch (IOException e) {
                        logger.error("Could not move " + child + " to " + functionObjectFolder);
                    }
                }
            }
        }
    }

    private void moveForcesFunctionObject(String baseDirPath, Dictionary foDict) {
        String foName = foDict.getName();
        File foFolder = Paths.get(baseDirPath, foName).toFile();
        if (foFolder.exists() && foFolder.isDirectory()) {
            File postProcFolder = Paths.get(baseDirPath, openFOAMProject.POST_PROC).toFile();
            try {
                FileUtils.moveDirectoryToDirectory(foFolder, postProcFolder, true);
            } catch (IOException e) {
                logger.error("Could not move " + foFolder + " to " + postProcFolder);
            }
        }
    }

    private void setupPostProcFolder(openFOAMProject project) {
        File postProcFolder = Paths.get(project.getBaseDir().getAbsolutePath(), openFOAMProject.POST_PROC).toFile();
        if (!postProcFolder.exists()) {
            postProcFolder.mkdir();
        }
    }

    private void convertSnappyHexMeshDict(SystemFolder systemFolder) {
        File snappyFile = systemFolder.getFileManager().getFile(SnappyHexMeshDict.SNAPPY_DICT);
        if (snappyFile.exists()) {
            Dictionary snappy = DictionaryUtils.readDictionary(snappyFile, monitor);
            if (snappy.isDictionary("castellatedMeshControls")) {
                Dictionary castellated = snappy.subDict("castellatedMeshControls");
                Dictionary layers = snappy.subDict("addLayersControls");

                boolean needWrite = false;
                /* LOCATIONS IN MESH */
                if (castellated.isField("locationsInMesh")) {
                    try {
                        String[][] matrix = castellated.lookupMatrix("locationsInMesh");
                        castellated.remove("locationsInMesh");
                        castellated.add("locationInMesh", "(" + matrix[0][0] + " " + matrix[0][1] + " " + matrix[0][2] + ")");
                    } catch (DictionaryException e) {
                        castellated.remove("locationsInMesh");
                        if (!castellated.isField("locationInMesh")) {
                            castellated.add("locationInMesh", "(0 0 0)");
                        }
                    }
                    needWrite = true;
                }

                /* FEATURE LINES */
                if (castellated.isList("features")) {
                    for (DefaultElement el : castellated.getList("features").getListElements()) {
                        if (el instanceof Dictionary) {
                            Dictionary d = (Dictionary) el;
                            // System.out.println("Project200To210Converter.Project200To210Converter() "+d);
                            if (d.isField("level")) {
                                d.add("levels", "( 0.0 " + d.lookup("level") + ")");
                                d.remove("level");
                                needWrite = true;
                            }
                        }
                    }
                }

                /* LAYERS OPTIONS */
                if (layers != null) {
                    if (!layers.found("writeVTK")) {
                        layers.add("writeVTK", "false");
                        needWrite = true;
                    }
                    if (!layers.found("noErrors")) {
                        layers.add("noErrors", "false");
                        needWrite = true;
                    }
                    if (!layers.found("layerRecovery")) {
                        layers.add("layerRecovery", "1");
                        needWrite = true;
                    }
                    if (!layers.found("growZoneLayers")) {
                        layers.add("growZoneLayers", "false");
                        needWrite = true;
                    }
                    if (!layers.found("projectGrownUp")) {
                        layers.add("projectGrownUp", "0.0");
                        needWrite = true;
                    }
                }

                if (needWrite) {
                    DictionaryUtils.writeDictionary(systemFolder.getFileManager().getFile(), DictionaryUtils.header("system", snappy), monitor);
                }
            }
        }
    }

    private void convertThermophysicalProperties(ConstantFolder constantFolder) {
        File thermoPhysicalPropertiesFile = constantFolder.getFileManager().getFile(ThermophysicalProperties.THERMOPHYSICAL_PROPERTIES);
        if (thermoPhysicalPropertiesFile.exists()) {
            Dictionary thermophysicalPropertiesOLD = DictionaryUtils.readDictionary(thermoPhysicalPropertiesFile, monitor);
            if (thermophysicalPropertiesOLD.isField("thermoType")) {
                Materials200To210Converter converter = new Materials200To210Converter();
                Dictionary thermophysicalPropertiesNEW = converter.convert(thermophysicalPropertiesOLD);
                DictionaryUtils.writeDictionary(constantFolder.getFileManager().getFile(), DictionaryUtils.header("constant", thermophysicalPropertiesNEW), monitor);
            }
        }
    }

    private void convertCellZones(CellZonesBuilder cellZonesBuilder, ConstantFolder constantFolder, SystemFolder systemFolder) {
        CellZones zones = new CellZones();

        File MRFZonesFile = constantFolder.getFileManager().getFile(MRF_ZONES);
        if (MRFZonesFile.exists()) {
            MRFZones mrfZones = getMRFZones(DictionaryUtils.readDictionary(MRFZonesFile, monitor));
            zones.addAll(CellZones200To210Converter.loadMRFDictionary(mrfZones));
        }
        File porousZonesFile = constantFolder.getFileManager().getFile(POROUS_ZONES);
        if (porousZonesFile.exists()) {
            PorousZones porousZones = getPorousZones(DictionaryUtils.readDictionary(porousZonesFile, monitor));
            zones.addAll(CellZones200To210Converter.loadPorousDictionary(porousZones));
        }

        cellZonesBuilder.saveMRFDictionary(zones, systemFolder.getFvOptions(), null);
        cellZonesBuilder.savePorousDictionary(zones, systemFolder.getFvOptions());

        DictionaryUtils.writeDictionary(systemFolder.getFileManager().getFile(), systemFolder.getFvOptions(), monitor);
    }

    private PorousZones getPorousZones(Dictionary dict) {
        PorousZones porousZones = new PorousZones();
        porousZones.merge(dict);
        return porousZones;
    }

    private MRFZones getMRFZones(Dictionary dict) {
        MRFZones MRFZones = new MRFZones();
        MRFZones.merge(dict);
        return MRFZones;
    }

    public class PorousZones extends Dictionary {
        public PorousZones() {
            super(POROUS_ZONES);
            setFoamFile(FoamFile.getDictionaryFoamFile(ConstantFolder.CONSTANT, POROUS_ZONES));
        }
    }

    public class MRFZones extends Dictionary {
        public MRFZones() {
            super(MRF_ZONES);
            setFoamFile(FoamFile.getDictionaryFoamFile(ConstantFolder.CONSTANT, MRF_ZONES));
        }
    }
}
