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
package eu.engys.core.project.system;

import static eu.engys.core.project.system.ControlDict.INCLUDE_KEY;
import static eu.engys.core.project.system.ControlDict.LIBS_KEY;

import java.io.File;
import java.util.Set;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.files.DefaultFileManager;
import eu.engys.core.project.files.FileManager;
import eu.engys.core.project.files.Folder;
import eu.engys.core.project.system.fieldmanipulationfunctionobjects.FieldManipulationFunctionObjectType;
import eu.engys.core.project.system.monitoringfunctionobjects.MonitoringFunctionObjectType;
import eu.engys.util.progress.ProgressMonitor;

public class SystemFolder implements Folder {

    public static final String SYSTEM = "system";

    // ECOMARINE
    public static final String REGION_KEY = "region";
    public static final String PATCHES_KEY = "patches";
    public static final String EXTRUDE_TO_REGION_MESH_DICT = "extrudeToRegionMeshDict";

    private BlockMeshDict blockMeshDict;
    private SnappyHexMeshDict snappyHexMeshDict;
    private StretchMeshDict stretchMeshDict;
    private FvSchemes fvSchemes;
    private FvSolution fvSolution;
    private FvOptions fvOptions;
    private ControlDict controlDict;
    private ProjectDict projectDict;
    private SetFieldsDict setFieldsDict;
    private MapFieldsDict mapFieldsDict;
    private DecomposeParDict decomposeParDict;

    private FileManager fileManager;

    private CustomNodeDict customDict;

    public SystemFolder(openFOAMProject prj) {
        fileManager = new DefaultFileManager(new File(prj.getBaseDir(), SYSTEM));
    }

    public SystemFolder(File baseDir, SystemFolder systemFolder) {
        fileManager = new DefaultFileManager(new File(baseDir, SYSTEM));
        setBlockMeshDict(systemFolder.getBlockMeshDict());
        setSnappyHexMeshDict(systemFolder.getSnappyHexMeshDict());
        setStretchMeshDict(systemFolder.getStretchMeshDict());
        setFvSchemes(systemFolder.getFvSchemes());
        setFvSolution(systemFolder.getFvSolution());
        setFvOptions(systemFolder.getFvOptions());
        setControlDict(systemFolder.getControlDict());
        setProjectDict(systemFolder.getProjectDict());
        setMapFieldsDict(systemFolder.getMapFieldsDict());
        setDecomposeParDict(systemFolder.getDecomposeParDict());
        setCustomNodeDict(systemFolder.getCustomNodeDict());
    }

    @Override
    public FileManager getFileManager() {
        return fileManager;
    }

    public BlockMeshDict getBlockMeshDict() {
        return blockMeshDict;
    }

    public void setBlockMeshDict(BlockMeshDict dict) throws DictionaryException {
        if (dict != null && dict.isFromFile()) {
            this.blockMeshDict = dict;
        } else {
            this.blockMeshDict = new BlockMeshDict();
            blockMeshDict.merge(dict);
        }
    }

    public SnappyHexMeshDict getSnappyHexMeshDict() {
        return snappyHexMeshDict;
    }

    public void setSnappyHexMeshDict(Dictionary dict) throws DictionaryException {
        this.snappyHexMeshDict = new SnappyHexMeshDict();
        snappyHexMeshDict.merge(dict);
    }

    public StretchMeshDict getStretchMeshDict() {
        return stretchMeshDict;
    }

    public void setStretchMeshDict(Dictionary dict) {
        this.stretchMeshDict = new StretchMeshDict();
        stretchMeshDict.merge(dict);
    }

    public FvSchemes getFvSchemes() {
        return fvSchemes;
    }

    public void setFvSchemes(Dictionary dict) throws DictionaryException {
        this.fvSchemes = new FvSchemes();
        fvSchemes.merge(dict);
    }

    public FvSolution getFvSolution() {
        return fvSolution;
    }

    public void setFvSolution(Dictionary dict) throws DictionaryException {
        this.fvSolution = new FvSolution();
        fvSolution.merge(dict);
        fvSolution.check();
    }

    public FvOptions getFvOptions() {
        return fvOptions;
    }

    public void setFvOptions(Dictionary dict) {
        this.fvOptions = new FvOptions();
        fvOptions.merge(dict);
        fvOptions.check();
    }

    public ControlDict getControlDict() {
        return controlDict;
    }

    public void setControlDict(Dictionary dict) throws DictionaryException {
        this.controlDict = new ControlDict();
        controlDict.merge(dict);
        controlDict.check();
    }

    public ProjectDict getProjectDict() {
        return projectDict;
    }

    public void setProjectDict(Dictionary dict) {
        this.projectDict = new ProjectDict();
        projectDict.merge(dict);
        projectDict.check();
    }

    public SetFieldsDict getSetFieldsDict() {
        return setFieldsDict;
    }

    public void setSetFieldsDict(Dictionary dict) {
        this.setFieldsDict = new SetFieldsDict(dict);
        setFieldsDict.check();
    }

    public MapFieldsDict getMapFieldsDict() {
        return mapFieldsDict;
    }

    public void setMapFieldsDict(Dictionary dict) {
        this.mapFieldsDict = new MapFieldsDict();
        mapFieldsDict.merge(dict);
        mapFieldsDict.check();
    }

    public DecomposeParDict getDecomposeParDict() {
        return decomposeParDict;
    }

    public void setDecomposeParDict(Dictionary dict) throws DictionaryException {
        this.decomposeParDict = new DecomposeParDict();
        decomposeParDict.merge(dict);
        decomposeParDict.check();
    }

    public CustomNodeDict getCustomNodeDict() {
        return customDict;
    }

    public void setCustomNodeDict(Dictionary dict) {
        this.customDict = new CustomNodeDict();
        customDict.merge(dict);
        customDict.check();
    }

    public void write(Model model, ProgressMonitor monitor) {
        File systemDir = fileManager.getFile();
        writeBlockMeshDict(monitor);
        DictionaryUtils.writeDictionary(systemDir, snappyHexMeshDict, monitor);
        DictionaryUtils.writeDictionary(systemDir, decomposeParDict, monitor);
        DictionaryUtils.writeDictionary(systemDir, stretchMeshDict, monitor);
        writeControlDict(model, monitor);
        DictionaryUtils.writeDictionary(systemDir, fvSolution, monitor);
        DictionaryUtils.writeDictionary(systemDir, fvSchemes, monitor);
        DictionaryUtils.writeDictionary(systemDir, fvOptions, monitor);
        DictionaryUtils.writeDictionary(systemDir, setFieldsDict, monitor);
        writeProjectDict(monitor);
        DictionaryUtils.writeDictionary(systemDir, customDict, monitor);
        DictionaryUtils.writeDictionary(systemDir, mapFieldsDict, monitor);
    }

    public void writeControlDict(Model model, ProgressMonitor monitor) {
        // applyHELYXFixes(model);NOT FOR HELYXOS
        DictionaryUtils.writeDictionary(fileManager.getFile(), controlDict, monitor);
    }

    private void applyHELYXFixes(Model model) {
        if (model.getState().getSolverType().isCoupled()) {
            if (controlDict.found(INCLUDE_KEY)) {
                controlDict.remove(INCLUDE_KEY);
            }
        } else {
            if (!controlDict.found(INCLUDE_KEY)) {
                controlDict.add(INCLUDE_KEY, "\"$FOAM_CONFIG/controlDict.libs\"");
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("( \"libLEMOS-2.2.x.so\" ");
        if (model.getState().isAdjoint() && model.getState().isTransient()) {
            sb.append("\"libHelyxAdjointPlus.so\"");
        } else if (model.getState().isAdjoint() && model.getState().isSteady()) {
            sb.append("\"libHelyxAdjoint.so\"");
        }
        sb.append(" )");

        if (controlDict.found(LIBS_KEY)) {
            controlDict.remove(LIBS_KEY);
        }
        controlDict.add(LIBS_KEY, sb.toString());
    }

    public void writeProjectDict(ProgressMonitor monitor) {
        DictionaryUtils.writeDictionary(fileManager.getFile(), projectDict, monitor);
    }

    public void writeBlockMeshDict(ProgressMonitor monitor) {
        if (!blockMeshDict.isFromFile()) {
            DictionaryUtils.writeDictionary(fileManager.getFile(), blockMeshDict, monitor);
        }
    }

    public void read(Model model, Set<FieldManipulationFunctionObjectType> ffoTypes, Set<MonitoringFunctionObjectType> mfoTypes, ProgressMonitor monitor) {
        File systemFolder = new File(model.getProject().getBaseDir(), SYSTEM);
        if (systemFolder.exists() && systemFolder.isDirectory()) {

            readControlDict(model, monitor, systemFolder);

            readFvSolution(model, monitor, systemFolder);

            readFvSchemes(model, monitor, systemFolder);

            readFvOptions(model, monitor, systemFolder);

            readSnappyHexMeshDict(model, monitor, systemFolder);

            readStretchMeshDict(model, monitor, systemFolder);

            readBlockMeshDict(model, monitor, systemFolder);

            readDecomposeParDict(model, monitor, systemFolder);

            readSetFieldsDict(model, monitor, systemFolder);

            readMapFiedsDict(model, monitor, systemFolder);

            readProjectDict(model, monitor, systemFolder);

            readCustomDict(model, monitor, systemFolder);

            model.getRuntimeFields().load(controlDict, monitor);
            model.runtimeFieldsChanged();

            model.getFieldManipulationFunctionObjects().load(controlDict, ffoTypes, monitor);
            model.fieldManipulationFunctionObjectsChanged();

            model.getMonitoringFunctionObjects().load(controlDict, mfoTypes, monitor);
            model.monitoringFunctionObjectsChanged();

            if (customDict != null) {
                model.getCustom().read(model, customDict, monitor);
                model.customChanged();
            }
        }
    }

    private void readControlDict(Model model, ProgressMonitor monitor, File systemFolder) {
        File controlDictFile = new File(systemFolder, ControlDict.CONTROL_DICT);
        if (controlDictFile.exists()) {
            ControlDict controlDict = new ControlDict(controlDictFile);
            try {
                setControlDict(controlDict);
                controlDict.check();
                monitor.info(ControlDict.CONTROL_DICT, 1);
            } catch (DictionaryException e) {
                monitor.warning(e.getMessage(), 1);
            }
        } else {
            setControlDict(model.getDefaults().getDefaultControlDict());
            monitor.warning(ControlDict.CONTROL_DICT + " not found, the default one will be used", 1);
        }
    }

    private void readFvSolution(Model model, ProgressMonitor monitor, File systemFolder) {
        File fvSolutionFile = new File(systemFolder, FvSolution.FV_SOLUTION);
        if (fvSolutionFile.exists()) {
            Dictionary fvSolution = new Dictionary(fvSolutionFile);
            try {
                setFvSolution(fvSolution);
                fvSolution.check();
                monitor.info(FvSolution.FV_SOLUTION, 1);
            } catch (DictionaryException e) {
                monitor.warning(e.getMessage(), 1);
            }
        } else {
            setFvSolution(model.getDefaults().getDefaultFvSolution());
            monitor.warning(FvSolution.FV_SOLUTION + " not found, the default one will be used", 1);
        }
    }

    private void readFvSchemes(Model model, ProgressMonitor monitor, File systemFolder) {
        File fvSchemesFile = new File(systemFolder, FvSchemes.FV_SCHEMES);
        if (fvSchemesFile.exists()) {
            Dictionary fvSchemes = new Dictionary(fvSchemesFile);
            try {
                setFvSchemes(fvSchemes);
                fvSchemes.check();
                monitor.info(FvSchemes.FV_SCHEMES, 1);
            } catch (DictionaryException e) {
                monitor.warning(e.getMessage(), 1);
            }
        } else {
            setFvSchemes(model.getDefaults().getDefaultFvSchemes());
            monitor.warning(FvSchemes.FV_SCHEMES + " not found, the default one will be used", 1);
        }
    }

    private void readFvOptions(Model model, ProgressMonitor monitor, File systemFolder) {
        File fvOptionsFile = new File(systemFolder, FvOptions.FV_OPTIONS);
        if (fvOptionsFile.exists()) {
            Dictionary fvOptions = new Dictionary(fvOptionsFile);
            try {
                setFvOptions(fvOptions);
                fvOptions.check();
                monitor.info(FvOptions.FV_OPTIONS, 1);
            } catch (DictionaryException e) {
                monitor.warning(e.getMessage(), 1);
            }
        } else {
            setFvOptions(new Dictionary(FvOptions.FV_OPTIONS));
            monitor.warning(FvOptions.FV_OPTIONS + " not found, the default one will be used", 1);
        }
    }

    private void readSnappyHexMeshDict(Model model, ProgressMonitor monitor, File systemFolder) {
        File snappyHexMeshFile = new File(systemFolder, SnappyHexMeshDict.SNAPPY_DICT);
        SnappyHexMeshDict snappy = model.getDefaults().getDefaultSnappyHexMeshDict();

        if (snappyHexMeshFile.exists()) {
            SnappyHexMeshDict snappyFromFile = new SnappyHexMeshDict(snappyHexMeshFile);
            snappy.merge(snappyFromFile);
            setSnappyHexMeshDict(snappy);
            try {
                snappyHexMeshDict.check();
                monitor.info(SnappyHexMeshDict.SNAPPY_DICT, 1);
            } catch (DictionaryException e) {
                monitor.warning(e.getMessage(), 1);
            }
        } else {
            setSnappyHexMeshDict(snappy);
            monitor.warning(SnappyHexMeshDict.SNAPPY_DICT + " not found, the default one will be used", 1);
        }
    }

    private void readStretchMeshDict(Model model, ProgressMonitor monitor, File systemFolder) {
        File stretchMeshDictFile = new File(systemFolder, StretchMeshDict.STRETCH_MESH_DICT);
        if (stretchMeshDictFile.exists()) {
            Dictionary dict = new StretchMeshDict(stretchMeshDictFile);
            try {
                setStretchMeshDict(dict);
                monitor.info(StretchMeshDict.STRETCH_MESH_DICT, 1);
            } catch (DictionaryException e) {
                monitor.warning(e.getMessage(), 1);
            }
        } else {
            setStretchMeshDict(model.getDefaults().getDefaultStretchMeshDict());
            monitor.warning(StretchMeshDict.STRETCH_MESH_DICT + " not found, the default one will be used", 1);
        }
    }

    private void readBlockMeshDict(Model model, ProgressMonitor monitor, File systemFolder) {
        File blockMeshFile = new File(systemFolder, BlockMeshDict.BLOCK_DICT);
        if (blockMeshFile.exists()) {
            BlockMeshDict dict = null;
            if (BlockMeshDict.containsFromFileLine(blockMeshFile)) {
                dict = new BlockMeshDict();
                dict.setFromFile(true);
            } else {
                dict = new BlockMeshDict(blockMeshFile);
                dict.setFromFile(false);
            }

            try {
                setBlockMeshDict(dict);
                blockMeshDict.check();
                monitor.info(BlockMeshDict.BLOCK_DICT, 1);
            } catch (DictionaryException e) {
                monitor.warning(e.getMessage(), 1);
            }
        } else {
            setBlockMeshDict(model.getDefaults().getDefaultBlockMeshDict());
            monitor.warning(BlockMeshDict.BLOCK_DICT + " not found, the default one will be used", 1);
        }
    }

    private void readDecomposeParDict(Model model, ProgressMonitor monitor, File systemFolder) {
        File decomposeParFile = new File(systemFolder, DecomposeParDict.DECOMPOSE_PAR_DICT);
        if (decomposeParFile.exists()) {
            Dictionary dict = new DecomposeParDict(decomposeParFile);
            try {
                setDecomposeParDict(dict);
                monitor.info(DecomposeParDict.DECOMPOSE_PAR_DICT, 1);
            } catch (DictionaryException e) {
                monitor.warning(e.getMessage(), 1);
            }
        } else {
            setDecomposeParDict(model.getDefaults().getDefaultDecomposeParDict());
            monitor.warning(DecomposeParDict.DECOMPOSE_PAR_DICT + " not found, the default one will be used", 1);
        }
    }

    private void readSetFieldsDict(Model model, ProgressMonitor monitor, File systemFolder) {
        File setFieldsDictFile = new File(systemFolder, SetFieldsDict.SET_FIELDS_DICT);
        if (setFieldsDictFile.exists()) {
            Dictionary dict = new SetFieldsDict(setFieldsDictFile);
            try {
                setSetFieldsDict(dict);
                monitor.info(SetFieldsDict.SET_FIELDS_DICT, 1);
            } catch (DictionaryException e) {
                monitor.warning(e.getMessage(), 1);
            }
        } else {
            monitor.warning(SetFieldsDict.SET_FIELDS_DICT + " NOT FOUND", 1);
        }
    }

    private void readMapFiedsDict(Model model, ProgressMonitor monitor, File systemFolder) {
        File mapFieldsDictFile = new File(systemFolder, MapFieldsDict.MAP_FIELDS_DICT);
        if (mapFieldsDictFile.exists()) {
            Dictionary dict = new MapFieldsDict(mapFieldsDictFile);
            try {
                setMapFieldsDict(dict);
                monitor.info(MapFieldsDict.MAP_FIELDS_DICT, 1);
            } catch (DictionaryException e) {
                monitor.warning(e.getMessage(), 1);
            }
        } else {
            setMapFieldsDict(model.getDefaults().getDefaultMapFieldsDict());
            monitor.warning(MapFieldsDict.MAP_FIELDS_DICT + " not found, the default one will be used", 1);
        }
    }

    // private void readRunDict(Model model, ProgressMonitor monitor, File systemFolder) {
    // File runDictFile = new File(systemFolder, RunDict.RUN_DICT);
    // if (runDictFile.exists()) {
    // RunDict dict = new RunDict(runDictFile);
    // try {
    //// setRunDict(dict);
    // monitor.info(RunDict.RUN_DICT, 1);
    // } catch (DictionaryException e) {
    // monitor.warning(e.getMessage(), 1);
    // }
    // } else {
    // monitor.warning(RunDict.RUN_DICT + " NOT FOUND", 1);
    // }
    // }

    public void readProjectDict(Model model, ProgressMonitor monitor, File systemFolder) {
        File runDictFile = new File(systemFolder, ProjectDict.PROJECT_DICT);
        if (runDictFile.exists()) {
            ProjectDict dict = new ProjectDict(runDictFile);
            try {
                setProjectDict(dict);
                monitor.info(ProjectDict.PROJECT_DICT, 1);
            } catch (DictionaryException e) {
                monitor.warning(e.getMessage(), 1);
            }
        } else {
            monitor.warning(ProjectDict.PROJECT_DICT + " NOT FOUND", 1);
        }
    }

    private void readCustomDict(Model model, ProgressMonitor monitor, File systemFolder) {
        File customNodeDictFile = new File(systemFolder, CustomNodeDict.CUSTOM_NODE_DICT);
        if (customNodeDictFile.exists()) {
            try {
                Dictionary dict = new CustomNodeDict(customNodeDictFile);
                setCustomNodeDict(dict);
                customDict.check();
                monitor.info(CustomNodeDict.CUSTOM_NODE_DICT, 1);
            } catch (DictionaryException e) {
                monitor.warning(e.getMessage(), 1);
            }
        } else {
            monitor.warning(CustomNodeDict.CUSTOM_NODE_DICT + " NOT FOUND", 1);
        }
    }

}
