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


package eu.engys.vtk;

import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.vtk.VTKUtil.logBlockNames;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vtk.vtkCompositeDataPipeline;
import vtk.vtkCompositeDataSet;
import vtk.vtkDataObject;
import vtk.vtkExecutive;
import vtk.vtkInformation;
import vtk.vtkInformationDoubleVectorKey;
import vtk.vtkMultiBlockDataSet;
import vtk.vtkPolyData;
import vtk.vtkUnstructuredGrid;
import eu.engys.core.project.Model;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.core.project.mesh.FieldItem.DataType;
import eu.engys.core.project.mesh.Mesh;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.vtk.info.VTKDataInformation;

public class VTKOpenFOAMDataset {

    private static final Logger logger = LoggerFactory.getLogger(VTKOpenFOAMDataset.class);

    private static final String DEFAULT_REGION = "defaultRegion";
    private static final String INTERNAL_MESH = "internalMesh";
    private static final String ZONES = "Zones";
    private static final String FACE_ZONES = "faceZones";
    private static final String CELL_ZONES = "cellZones";
    private static final String PATCHES = "Patches";
    
    private Model model;
    private ProgressMonitor monitor;

    private vtkUnstructuredGrid internalMeshDataset = null;
    private List<vtkDataObject> patchesDataset = new ArrayList<>();
    private List<vtkDataObject> cellZonesDataset = new ArrayList<>();

    public VTKOpenFOAMDataset(Model model, ProgressMonitor monitor) {
        this.model = model;
        this.monitor = monitor;
    }

    public void loadInformations(double timeStep) {
        logger.info("Load mesh informations for timestep {}", timeStep);
        VTKOpenFOAMReader reader = new VTKOpenFOAMReader(model, monitor, "Informations of");
        reader.UpdateInformation();
        reader.ReadInternalMeshOff();
        reader.ReadPatchesOff();
        reader.ReadZonesOff();
        reader.setTimeStep(timeStep);
        reader.Update();

        extractInformations(reader, timeStep);

        reader.Delete();
    }

    public List<vtkDataObject> getCellZonesDataset() {
        return cellZonesDataset;
    }

    public List<vtkDataObject> getPatchesDataset() {
        return patchesDataset;
    }

    public vtkUnstructuredGrid getInternalMeshDataset() {
        return internalMeshDataset;
    }

    public void loadInternalMesh(double timeStep) {
        VTKOpenFOAMReader reader = new VTKOpenFOAMReader(model, monitor, "Internal");
        reader.UpdateInformation();
        reader.ReadInternalMeshOn();
        reader.ReadPatchesOff();
        reader.ReadZonesOff();
        reader.setTimeStep(timeStep);
        reader.Update();

        vtkMultiBlockDataSet dataset = reader.GetOutput();
        try {
            
            if (isSingleRegion(dataset)) { 
                readSingleRegionInternalMesh(dataset);
            } else {
                readMultiRegionInternalMesh(dataset);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dataset.Delete();
            reader.Delete();
        }
    }

    private void readSingleRegionInternalMesh(vtkMultiBlockDataSet dataset) {
        int numberOfBlocks = dataset.GetNumberOfBlocks();
        if (numberOfBlocks > 0) {
            logger.debug("READ [SINGLE] [INTERNAL] blocks are: {}", VTKUtil.logBlockNames(dataset));

            vtkDataObject internalMesh = getBlock(INTERNAL_MESH, dataset);
            if (internalMesh != null) {
                extractInternalDataset(internalMesh);
            } else {
                logger.warn("READ [SINGLE] [INTERNAL]: {} NOT FOUND", INTERNAL_MESH);
            }
        } else {
            logger.warn("READ [SINGLE] [INTERNAL]: EMPTY!");
        }
    }

    private void readMultiRegionInternalMesh(vtkMultiBlockDataSet dataset) {
        int numberOfBlocks = dataset.GetNumberOfBlocks();
        if (numberOfBlocks > 0) {
            logger.debug("READ [MULTI] [INTERNAL] blocks are: {}", VTKUtil.logBlockNames(dataset));
            
            vtkDataObject defaultRegion = getBlock(DEFAULT_REGION, dataset);
            if (defaultRegion != null) {
                readSingleRegionInternalMesh((vtkMultiBlockDataSet) defaultRegion);
            } else {
                logger.warn("READ [MULTI] [INTERNAL]: {} NOT FOUND", DEFAULT_REGION);
            }
        } else {
            logger.warn("READ [MULTI] [INTERNAL]: EMPTY");
        }
    }

    public void loadExternalMesh(double timeStep) {
        VTKOpenFOAMReader reader = new VTKOpenFOAMReader(model, monitor, "External");
        reader.ReadInternalMeshOff();
        reader.ReadPatchesOn();
        reader.ReadZonesOn();
        reader.setTimeStep(timeStep);
        reader.Update();

        vtkMultiBlockDataSet dataset = reader.GetOutput();
        
        try {
            if (isSingleRegion(dataset)) { 
                readSingleRegion(dataset);
            } else {
                readMultiRegion(dataset);
            }

            extractInformations(reader, timeStep);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dataset.Delete();
            reader.Delete();
        }
    }

    private boolean isSingleRegion(vtkMultiBlockDataSet dataset) {
        int numberOfBlocks = dataset.GetNumberOfBlocks();
        if (numberOfBlocks > 0) {
            String blockName = dataset.GetMetaData(0).Get(new vtkCompositeDataSet().NAME());
            return blockName.equals(PATCHES) || blockName.equals(ZONES) || blockName.equals(INTERNAL_MESH) ;
        }
        return false;
    }

    private void readSingleRegion(vtkMultiBlockDataSet dataset) {
        int numberOfBlocks = dataset.GetNumberOfBlocks();
        if (numberOfBlocks > 0) {
            logger.debug("READ [SINGLE] [EXTERNAL] blocks are: {}", logBlockNames(dataset));

            vtkDataObject patches = getBlock(PATCHES, dataset);
            if (patches != null) {
                extractPatchesDataset(patches);
            } else {
                logger.warn("READ [SINGLE] [PATCHES]: {} NOT FOUND", PATCHES);
            }
        
            vtkDataObject zones = getBlock(ZONES, dataset);
            if (zones != null) {
                extractZonesDataset(zones);
            } else {
                logger.warn("READ [SINGLE] [ZONES]: {} NOT FOUND", ZONES);
            }
        }
    }

    private void readMultiRegion(vtkMultiBlockDataSet dataset) {
        int numberOfBlocks = dataset.GetNumberOfBlocks();
        if (numberOfBlocks > 0) {
            logger.debug("READ [MULTI] [EXTERNAL] blocks are: {}", logBlockNames(dataset));
            
            vtkDataObject defaultRegion = getBlock(DEFAULT_REGION, dataset);
            if (defaultRegion != null) {
                readSingleRegion((vtkMultiBlockDataSet) defaultRegion);
            } else {
                logger.warn("READ [MULTI] [EXTERNAL]: {} NOT LOADED", DEFAULT_REGION);
            }
        } else {
            logger.warn("READ [MULTI] [EXTERNAL]: EMPTY!");
        }
    }
    
    private vtkDataObject getBlock(String blockName, vtkMultiBlockDataSet dataset) {
        int numberOfBlocks = dataset.GetNumberOfBlocks();
        for (int i = 0; i < numberOfBlocks; i++) {
            String name = dataset.GetMetaData(i).Get(new vtkCompositeDataSet().NAME());
            if (blockName.equals(name)) {
                return dataset.GetBlock(i);
            }
        }
        return null;
    }

    private void extractPatchesDataset(vtkDataObject block) {
        if (block != null && block instanceof vtkMultiBlockDataSet) {
            vtkMultiBlockDataSet dataset = (vtkMultiBlockDataSet) block;
            int subblockNumbers = dataset.GetNumberOfBlocks();
            for (int i = 0; i < subblockNumbers; i++) {
                vtkDataObject subBlock = dataset.GetBlock(i);
                if (subBlock instanceof vtkPolyData) {
                    this.patchesDataset.add(shallowCopy((vtkPolyData) subBlock));
                }
            }
        }
    }

    public static vtkDataObject shallowCopy(vtkPolyData data) {
        vtkPolyData copy = new vtkPolyData();
        copy.ShallowCopy(data);
        return copy;
    }

    public static  vtkUnstructuredGrid shallowCopy(vtkUnstructuredGrid data) {
        vtkUnstructuredGrid copy = new vtkUnstructuredGrid();
        copy.ShallowCopy(data);
        return copy;
    }

    private void extractZonesDataset(vtkDataObject block) {
        if (block != null && block instanceof vtkMultiBlockDataSet) {
            
            vtkMultiBlockDataSet zones = (vtkMultiBlockDataSet) block;
            
            
            vtkDataObject faceZones = getBlock(FACE_ZONES, zones);
            if (faceZones != null) {
//                extractFaceZonesDataset(zones);
            } else {
                logger.warn("READ [SINGLE] [FACE ZONES]: {} NOT FOUND", FACE_ZONES);
            }

            vtkDataObject cellZones = getBlock(CELL_ZONES, zones);
            if (cellZones != null) {
                extractCellZonesDataset(cellZones);
            } else {
                logger.warn("READ [SINGLE] [ZONES]: {} NOT FOUND", ZONES);
            }
            
            
            
            zones.Delete();
        }
    }

    private void extractCellZonesDataset(vtkDataObject block) {
        if (block != null && block instanceof vtkMultiBlockDataSet) {
            vtkMultiBlockDataSet cellZones = (vtkMultiBlockDataSet) block;
            int cellZonesNumber = cellZones.GetNumberOfBlocks();
            for (int i = 0; i < cellZonesNumber; i++) {
                vtkDataObject cellZone = cellZones.GetBlock(i);
                if (cellZone instanceof vtkUnstructuredGrid) {
                    logger.warn("Load as a Cell Zone");
                    cellZonesDataset.add(shallowCopy((vtkUnstructuredGrid) cellZone));
                }
            }
        }
    }

    private void extractInternalDataset(vtkDataObject block) {
        this.internalMeshDataset = shallowCopy((vtkUnstructuredGrid) block);
    }

    public void loadTimeStep(double timeStep) {
        VTKOpenFOAMReader reader = new VTKOpenFOAMReader(model, monitor, "Time steps of");
        reader.UpdateInformation();
        reader.ReadInternalMeshOn();
        reader.ReadPatchesOn();
        reader.ReadZonesOn();
        reader.setTimeStep(timeStep);
        reader.Update();

        vtkMultiBlockDataSet dataset = reader.GetOutput();
        try {
            if (isSingleRegion(dataset)) {
                readSingleRegionInternalMesh(dataset);
                readSingleRegion(dataset);
            } else {
                readMultiRegionInternalMesh(dataset);
                readMultiRegion(dataset);
            }

            extractInformations(reader, timeStep);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dataset.Delete();
            reader.Delete();
        }
    }

    private void extractInformations(VTKOpenFOAMReader reader, double timeStep) {
        VTKDataInformation info = new VTKDataInformation();
        info.AddFromMultiBlockDataSet(reader.GetOutput());

        Mesh mesh = model.getMesh();
        mesh.readStatistics(model);
        mesh.setBounds(info.getBounds());
        mesh.setMemorySize(info.getMemorySize());

        mesh.setTimeSteps(getTimeSteps(reader));
        
        readFieldItems(reader, mesh);
        
        mesh.getTimeStepPointFieldsMap().put(Double.valueOf(timeStep), reader.getPointArrayNames());
        mesh.getTimeStepCellFieldsMap().put(Double.valueOf(timeStep), reader.getCellArrayNames());

        logger.info("Timesteps = {}", mesh.getTimeSteps());
        logger.info("CellFieldsItems = {}", mesh.getCellFieldMap().keySet());
        logger.info("PointFieldsItems = {}", mesh.getPointFieldMap().keySet());
        
        mesh.setRegions(getRegions(reader));
    }

    private List<String> getRegions(VTKOpenFOAMReader reader) {
        return null;
    }

    private void readFieldItems(VTKOpenFOAMReader reader, Mesh mesh) {
        for (String newField : reader.getCellArrayNames()) {
            Map<String, FieldItem> cellFieldMap = mesh.getCellFieldMap();
            if (newField.startsWith(U)) {
                for (int i = 0; i < FieldItem.COMPONENTS.length; i++) {
                    FieldItem fieldItem = new FieldItem(newField, DataType.CELL, i);
                    if (!cellFieldMap.containsKey(newField + "_" + i)) {
                        cellFieldMap.put(newField + "_" + i, fieldItem);
                    }
                }
            } else {
                FieldItem fieldItem = new FieldItem(newField, DataType.CELL, -1);
                if (!cellFieldMap.containsKey(newField)) {
                    cellFieldMap.put(newField, fieldItem);
                }
            }
        }
        for (String newField : reader.getPointArrayNames()) {
            Map<String, FieldItem> pointFieldMap = mesh.getPointFieldMap();
            if (newField.startsWith(U)) {
                for (int i = 0; i < FieldItem.COMPONENTS.length; i++) {
                    FieldItem fieldItem = new FieldItem(newField, DataType.POINT, i);
                    if (!pointFieldMap.containsKey(newField + "_" + i)) {
                        pointFieldMap.put(newField + "_" + i, fieldItem);
                    }
                }
            } else {
                FieldItem fieldItem = new FieldItem(newField, DataType.POINT, -1);
                if (!pointFieldMap.containsKey(newField)) {
                    pointFieldMap.put(newField, fieldItem);
                }
            }
        }
    }

    private List<Double> getTimeSteps(VTKOpenFOAMReader reader) {
        vtkExecutive exe = reader.GetExecutive();
        vtkCompositeDataPipeline pipeline = (vtkCompositeDataPipeline) exe;
        vtkInformation outInfo = exe.GetOutputInformation(0);

        vtkInformationDoubleVectorKey timeStepsKey = pipeline.TIME_STEPS();
        int nTimeSteps = outInfo.Length(timeStepsKey); // Get the number of time
                                                       // steps
        List<Double> timesteps = new ArrayList<>();
        for (int i = 0; i < nTimeSteps; i++) {
            double timeValue = outInfo.Get(timeStepsKey, i);
            timesteps.add(Double.valueOf(timeValue));
        }
        return timesteps;
    }

    public void clear() {
        for (vtkDataObject obj : patchesDataset) {
            obj.Delete();
        }
        for (vtkDataObject obj : cellZonesDataset) {
            obj.Delete();
        }
        if (internalMeshDataset != null) {
            internalMeshDataset.Delete();
            internalMeshDataset = null;
        }
        patchesDataset.clear();
        cellZonesDataset.clear();
    }
}
