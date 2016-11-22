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
package eu.engys.vtk;

import static eu.engys.core.project.mesh.FieldItem.MAGNITUDE;
import static eu.engys.core.project.mesh.FieldItem.X;
import static eu.engys.core.project.mesh.FieldItem.Y;
import static eu.engys.core.project.mesh.FieldItem.Z;
import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.vtk.VTKUtil.logBlockNames;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.core.project.mesh.FieldItem.DataType;
import eu.engys.util.progress.ProgressMonitor;
import vtk.vtkCompositeDataPipeline;
import vtk.vtkCompositeDataSet;
import vtk.vtkDataObject;
import vtk.vtkDataSet;
import vtk.vtkExecutive;
import vtk.vtkInformation;
import vtk.vtkInformationDoubleVectorKey;
import vtk.vtkMultiBlockDataSet;
import vtk.vtkPolyData;
import vtk.vtkUnstructuredGrid;

public class ExternalMeshReader extends AbstractMeshReader {

    private static final Logger logger = LoggerFactory.getLogger(ExternalMeshReader.class);

    private static final String FACE_ZONES = "faceZones";
    private static final String CELL_ZONES = "cellZones";
    static final String PATCHES = "Patches";
    static final String ZONES = "Zones";

    private static final boolean readFaceZones = false;
    private static final boolean readCellZones = true;

    private Map<String, vtkPolyData> patchesDataset;
    private List<vtkPolyData> faceZonesDataset;
    private List<vtkUnstructuredGrid> cellZonesDataset;

    private BoundingBox bounds;
    private List<Double> timesteps;
    
    public ExternalMeshReader(File baseDir, boolean parallel, ProgressMonitor monitor) {
        super(baseDir, parallel, monitor);
        
        this.patchesDataset = new HashMap<>();
        this.faceZonesDataset = new ArrayList<>();
        this.cellZonesDataset = new ArrayList<>();
    }

    public void read(double timeStep) {
        VTKOpenFOAMReader reader = new VTKOpenFOAMReader(baseDir, parallel, monitor);//"External");
        reader.UpdateInformation();
        reader.ReadPatchesOn();
        reader.ReadInternalMeshOff();
        reader.ReadZonesOn();
        reader.setTimeStep(timeStep);
        reader.Update();

        vtkMultiBlockDataSet dataset = reader.GetOutput();
        try {
            if (VTKUtil.isSingleRegion(dataset)) {
                readSingleRegion(dataset);
            } else {
                readMultiRegion(dataset);
            }

            readFieldItems(reader);
            readTimeSteps(reader);
            readBounds(reader);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dataset.Delete();
            reader.Delete();
        }
    }

    private void readMultiRegion(vtkMultiBlockDataSet dataset) {
        int numberOfBlocks = dataset.GetNumberOfBlocks();
        if (numberOfBlocks > 0) {
            logger.debug("READ [MULTI] [EXTERNAL] blocks are: {}", logBlockNames(dataset));

            vtkDataObject defaultRegion = VTKUtil.getBlock(VTKUtil.DEFAULT_REGION, dataset);
            if (defaultRegion != null) {
                readSingleRegion((vtkMultiBlockDataSet) defaultRegion);
            } else {
                logger.warn("READ [MULTI] [EXTERNAL]: {} NOT LOADED", VTKUtil.DEFAULT_REGION);
            }
        } else {
            logger.warn("READ [MULTI] [EXTERNAL]: EMPTY!");
        }
    }

    private void readSingleRegion(vtkMultiBlockDataSet dataset) {
        int numberOfBlocks = dataset.GetNumberOfBlocks();
        if (numberOfBlocks > 0) {
            logger.debug("READ [SINGLE] [EXTERNAL] blocks are: {}", logBlockNames(dataset));

            vtkDataObject patches = VTKUtil.getBlock(PATCHES, dataset);
            if (patches != null) {
                extractPatchesDataset(patches);
            } else {
                logger.warn("READ [SINGLE] [PATCHES]: {} NOT FOUND", PATCHES);
            }

            vtkDataObject zones = VTKUtil.getBlock(ZONES, dataset);
            if (zones != null) {
                extractZonesDataset(zones);
            } else {
                logger.warn("READ [SINGLE] [ZONES]: {} NOT FOUND", ZONES);
            }
        }
    }

    private void extractPatchesDataset(vtkDataObject block) {
        if (block != null && block instanceof vtkMultiBlockDataSet) {
            vtkMultiBlockDataSet dataset = (vtkMultiBlockDataSet) block;
            int subblockNumbers = dataset.GetNumberOfBlocks();
            for (int i = 0; i < subblockNumbers; i++) {
                vtkDataObject subBlock = dataset.GetBlock(i);
                if (subBlock instanceof vtkPolyData) {
                    String name = dataset.GetMetaData(i).Get(new vtkCompositeDataSet().NAME());
                    this.patchesDataset.put(name, VTKUtil.shallowCopy((vtkPolyData) subBlock));
                }
            }
        }
    }

    private void extractZonesDataset(vtkDataObject block) {
        if (block != null && block instanceof vtkMultiBlockDataSet) {

            vtkMultiBlockDataSet zones = (vtkMultiBlockDataSet) block;

            if (readFaceZones) {
                vtkDataObject faceZones = VTKUtil.getBlock(FACE_ZONES, zones);
                if (faceZones != null) {
                    extractFaceZonesDataset(faceZones);
                } else {
                    logger.warn("READ [SINGLE] [FACE ZONES]: {} NOT FOUND", FACE_ZONES);
                }
            } else {
                logger.debug("READ [SINGLE] [FACE ZONES]: programmatically DISABLED");
            }

            if (readCellZones) {
                vtkDataObject cellZones = VTKUtil.getBlock(CELL_ZONES, zones);
                if (cellZones != null) {
                    extractCellZonesDataset(cellZones);
                } else {
                    logger.warn("READ [SINGLE] [ZONES]: {} NOT FOUND", ZONES);
                }
            } else {
                logger.debug("READ [SINGLE] [ZONES]: programmatically DISABLED");
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
                    this.cellZonesDataset.add(VTKUtil.shallowCopy((vtkUnstructuredGrid) cellZone));
                }
            }
        }
    }

    private void extractFaceZonesDataset(vtkDataObject block) {
        if (block != null && block instanceof vtkMultiBlockDataSet) {
            vtkMultiBlockDataSet dataset = (vtkMultiBlockDataSet) block;
            int subblockNumbers = dataset.GetNumberOfBlocks();
            for (int i = 0; i < subblockNumbers; i++) {
                vtkDataObject subBlock = dataset.GetBlock(i);
                if (subBlock instanceof vtkPolyData) {
                    this.faceZonesDataset.add(VTKUtil.shallowCopy((vtkPolyData) subBlock));
                }
            }
        }
    }

    private void readFieldItems(VTKOpenFOAMReader reader) {
        readCellFieldItems(reader);
        readPointFieldItems(reader);
    }

    private void readCellFieldItems(VTKOpenFOAMReader reader) {
        List<vtkDataSet> allDataSets = new ArrayList<>();
        allDataSets.addAll(patchesDataset.values());
        allDataSets.addAll(faceZonesDataset);
        allDataSets.addAll(cellZonesDataset);

        Map<String, double[][]> cellRanges = VTKUtil.getCellFieldsRanges(allDataSets);
        for (String newField : reader.getCellArrayNames()) {
            if (cellRanges.containsKey(newField)) {
                if (newField.startsWith(U)) {
                    fieldItems.addCellFieldItem(new FieldItem(newField + " - " + MAGNITUDE, newField, DataType.CELL, 0, cellRanges.get(newField)[0]));
                    fieldItems.addCellFieldItem(new FieldItem(newField + " - " + X, newField, DataType.CELL, 1, cellRanges.get(newField)[1]));
                    fieldItems.addCellFieldItem(new FieldItem(newField + " - " + Y, newField, DataType.CELL, 2, cellRanges.get(newField)[2]));
                    fieldItems.addCellFieldItem(new FieldItem(newField + " - " + Z, newField, DataType.CELL, 3, cellRanges.get(newField)[3]));
                } else {
                    fieldItems.addCellFieldItem(new FieldItem(newField, newField, DataType.CELL, -1, cellRanges.get(newField)[0]));
                }
            } else {
                logger.warn("No cell range for {}", newField);
            }
        }
    }

    private void readPointFieldItems(VTKOpenFOAMReader reader) {
        List<vtkDataSet> allDataSets = new ArrayList<>();
        allDataSets.addAll(patchesDataset.values());
        allDataSets.addAll(faceZonesDataset);
        allDataSets.addAll(cellZonesDataset);

        Map<String, double[][]> pointRanges = VTKUtil.getPointFieldsRanges(allDataSets);
        for (String newField : reader.getPointArrayNames()) {
            if (pointRanges.containsKey(newField)) {
                if (newField.startsWith(U)) {
                    fieldItems.addPointFieldItem(new FieldItem(newField + " - " + MAGNITUDE, newField, DataType.POINT, 0, pointRanges.get(newField)[0]));
                    fieldItems.addPointFieldItem(new FieldItem(newField + " - " + X, newField, DataType.POINT, 1, pointRanges.get(newField)[1]));
                    fieldItems.addPointFieldItem(new FieldItem(newField + " - " + Y, newField, DataType.POINT, 2, pointRanges.get(newField)[2]));
                    fieldItems.addPointFieldItem(new FieldItem(newField + " - " + Z, newField, DataType.POINT, 3, pointRanges.get(newField)[3]));
                } else {
                    fieldItems.addPointFieldItem(new FieldItem(newField, newField, DataType.POINT, -1, pointRanges.get(newField)[0]));
                }
            } else {
                logger.warn("No point range for {}", newField);
            }
        }
    }

    private void readTimeSteps(VTKOpenFOAMReader reader) {
        vtkExecutive exe = reader.GetExecutive();
        vtkCompositeDataPipeline pipeline = (vtkCompositeDataPipeline) exe;
        vtkInformation outInfo = exe.GetOutputInformation(0);

        vtkInformationDoubleVectorKey timeStepsKey = pipeline.TIME_STEPS();
        int nTimeSteps = outInfo.Length(timeStepsKey); // Get the number of time steps
        List<Double> ts = new ArrayList<>();
        for (int i = 0; i < nTimeSteps; i++) {
            double timeValue = outInfo.Get(timeStepsKey, i);
            ts.add(Double.valueOf(timeValue));
        }
        this.timesteps = ts;
    }

    private void readBounds(VTKOpenFOAMReader reader) {
        List<vtkDataSet> allDataSets = new ArrayList<>();
        allDataSets.addAll(patchesDataset.values());
        allDataSets.addAll(faceZonesDataset);
        allDataSets.addAll(cellZonesDataset);
        this.bounds = VTKUtil.calculateBounds(allDataSets);
    }

    public BoundingBox getBounds() {
        return bounds;
    }

    public List<Double> getTimesteps() {
        return timesteps;
    }

    public List<vtkUnstructuredGrid> getCellZonesDataset() {
        return cellZonesDataset;
    }

    public Map<String, vtkPolyData> getPatchesDataset() {
        return patchesDataset;
    }

    public List<vtkPolyData> getFaceZonesDataset() {
        return faceZonesDataset;
    }

    public void clear() {
        for (vtkDataObject obj : patchesDataset.values()) {
            obj.Delete();
        }
        for (vtkDataObject obj : faceZonesDataset) {
            obj.Delete();
        }
        for (vtkDataObject obj : cellZonesDataset) {
            obj.Delete();
        }
    }

}
