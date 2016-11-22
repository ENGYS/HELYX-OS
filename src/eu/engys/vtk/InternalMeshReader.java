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

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.mesh.FieldItem;
import eu.engys.core.project.mesh.FieldItem.DataType;
import eu.engys.util.progress.ProgressMonitor;
import vtk.vtkDataObject;
import vtk.vtkMultiBlockDataSet;
import vtk.vtkUnstructuredGrid;

public class InternalMeshReader extends AbstractMeshReader {

    static final String INTERNAL_MESH = "internalMesh";
    
    private static final Logger logger = LoggerFactory.getLogger(InternalMeshReader.class);

    private vtkUnstructuredGrid internalMeshDataset = null;
    
    public InternalMeshReader(File baseDir, boolean parallel, ProgressMonitor monitor) {
        super(baseDir, parallel, monitor);
    }

    public void read(double timeStep) {
        VTKOpenFOAMReader reader = new VTKOpenFOAMReader(baseDir, parallel, monitor);//, "Internal");
        reader.UpdateInformation();
        reader.ReadPatchesOff();
        reader.ReadInternalMeshOn();
        reader.ReadZonesOff();
        reader.setTimeStep(timeStep);
        reader.Update();

        vtkMultiBlockDataSet dataset = reader.GetOutput();
        try {

            if (VTKUtil.isSingleRegion(dataset)) {
                readSingleRegionInternalMesh(dataset);
            } else {
                readMultiRegionInternalMesh(dataset);
            }

            readFieldItems(reader);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dataset.Delete();
            reader.Delete();
        }
    }

    private void readMultiRegionInternalMesh(vtkMultiBlockDataSet dataset) {
        int numberOfBlocks = dataset.GetNumberOfBlocks();
        if (numberOfBlocks > 0) {
            logger.debug("READ [MULTI] [INTERNAL] blocks are: {}", VTKUtil.logBlockNames(dataset));

            vtkDataObject defaultRegion = VTKUtil.getBlock(VTKUtil.DEFAULT_REGION, dataset);
            if (defaultRegion != null) {
                readSingleRegionInternalMesh((vtkMultiBlockDataSet) defaultRegion);
            } else {
                logger.warn("READ [MULTI] [INTERNAL]: {} NOT FOUND", VTKUtil.DEFAULT_REGION);
            }
        } else {
            logger.warn("READ [MULTI] [INTERNAL]: EMPTY");
        }
    }

    private void readSingleRegionInternalMesh(vtkMultiBlockDataSet dataset) {
        int numberOfBlocks = dataset.GetNumberOfBlocks();
        if (numberOfBlocks > 0) {
            logger.debug("READ [SINGLE] [INTERNAL] blocks are: {}", VTKUtil.logBlockNames(dataset));

            vtkDataObject internalMesh = VTKUtil.getBlock(INTERNAL_MESH, dataset);
            if (internalMesh != null) {
                extractInternalDataset(internalMesh);
            } else {
                logger.warn("READ [SINGLE] [INTERNAL]: {} NOT FOUND", INTERNAL_MESH);
            }
        } else {
            logger.warn("READ [SINGLE] [INTERNAL]: EMPTY!");
        }
    }

    private void extractInternalDataset(vtkDataObject block) {
        this.internalMeshDataset = VTKUtil.shallowCopy((vtkUnstructuredGrid) block);
    }

    private void readFieldItems(VTKOpenFOAMReader reader) {
        readCellFieldItems(reader);
        readPointFieldItems(reader);
    }

    private void readCellFieldItems(VTKOpenFOAMReader reader) {
        Map<String, double[][]> cellRanges = VTKUtil.getCellFieldsRanges(internalMeshDataset);
        for (String newField : reader.getCellArrayNames()) {
            if (newField.startsWith(U)) {
                fieldItems.addCellFieldItem(new FieldItem(newField + " - " + MAGNITUDE, newField, DataType.CELL, 0, cellRanges.get(newField)[0]));
                fieldItems.addCellFieldItem(new FieldItem(newField + " - " + X, newField, DataType.CELL, 1, cellRanges.get(newField)[1]));
                fieldItems.addCellFieldItem(new FieldItem(newField + " - " + Y, newField, DataType.CELL, 2, cellRanges.get(newField)[2]));
                fieldItems.addCellFieldItem(new FieldItem(newField + " - " + Z, newField, DataType.CELL, 3, cellRanges.get(newField)[3]));
            } else {
                fieldItems.addCellFieldItem(new FieldItem(newField, newField, DataType.CELL, -1, cellRanges.get(newField)[0]));
            }
        }
    }

    private void readPointFieldItems(VTKOpenFOAMReader reader) {
        Map<String, double[][]> pointRanges = VTKUtil.getPointFieldsRanges(internalMeshDataset);
        for (String newField : reader.getPointArrayNames()) {
            if (newField.startsWith(U)) {
                fieldItems.addPointFieldItem(new FieldItem(newField + " - " + MAGNITUDE, newField, DataType.POINT, 0, pointRanges.get(newField)[0]));
                fieldItems.addPointFieldItem(new FieldItem(newField + " - " + X, newField, DataType.POINT, 1, pointRanges.get(newField)[1]));
                fieldItems.addPointFieldItem(new FieldItem(newField + " - " + Y, newField, DataType.POINT, 2, pointRanges.get(newField)[2]));
                fieldItems.addPointFieldItem(new FieldItem(newField + " - " + Z, newField, DataType.POINT, 3, pointRanges.get(newField)[3]));
            } else {
                fieldItems.addPointFieldItem(new FieldItem(newField, newField, DataType.POINT, -1, pointRanges.get(newField)[0]));
            }
        }
    }

    public vtkUnstructuredGrid getInternalMeshDataset() {
        return internalMeshDataset;
    }

    public void clear() {
        if (internalMeshDataset != null) {
            internalMeshDataset.Delete();
            internalMeshDataset = null;
        }
    }

}
