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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Controller3D;
import eu.engys.util.FormatUtil;
import eu.engys.util.Util;
import eu.engys.util.VTKSettings;
import vtk.vtkActor;
import vtk.vtkAppendPolyData;
import vtk.vtkCellArray;
import vtk.vtkCellData;
import vtk.vtkCleanPolyData;
import vtk.vtkCompositeDataPipeline;
import vtk.vtkCompositeDataSet;
import vtk.vtkConeSource;
import vtk.vtkDataArray;
import vtk.vtkDataObject;
import vtk.vtkDataSet;
import vtk.vtkDataSetSurfaceFilter;
import vtk.vtkDoubleArray;
import vtk.vtkExecutive;
import vtk.vtkFieldData;
import vtk.vtkIdList;
import vtk.vtkInformation;
import vtk.vtkInformationDoubleKey;
import vtk.vtkInformationDoubleVectorKey;
import vtk.vtkInteractorStyleTrackballCamera;
import vtk.vtkIntersectionPolyDataFilter;
import vtk.vtkLineSource;
import vtk.vtkMapper;
import vtk.vtkMultiBlockDataSet;
import vtk.vtkObject;
import vtk.vtkOpenFOAMReader;
import vtk.vtkPlaneSource;
import vtk.vtkPointData;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkPolyDataNormals;
import vtk.vtkReferenceInformation;
import vtk.vtkRenderWindow;
import vtk.vtkRenderWindowInteractor;
import vtk.vtkRenderer;
import vtk.vtkStreamingDemandDrivenPipeline;
import vtk.vtkTriangleFilter;
import vtk.vtkTubeFilter;
import vtk.vtkUnstructuredGrid;

public class VTKUtil {

    private static final Logger logger = LoggerFactory.getLogger(VTKUtil.class);

    public static BoundingBox computeBoundingBox(List<Controller3D> controllers, boolean visibleOnly) {

        List<Actor> allVisibleActors = new ArrayList<>();

        for (Controller3D controller : controllers) {
            Collection<Actor> actors = controller.getActorsList();
            for (Actor vtkActor : actors) {
                if (!visibleOnly || (visibleOnly && vtkActor.getVisibility())) {
                    allVisibleActors.add(vtkActor);
                }
            }
        }

        return computeBoundingBox(allVisibleActors);
    }

    public static BoundingBox computeBoundingBox(Surface... surfaces) {
        if (Util.isVarArgsNotNull(surfaces)) {
            double xmin = Double.MAX_VALUE;
            double xmax = -Double.MAX_VALUE;
            double ymin = Double.MAX_VALUE;
            double ymax = -Double.MAX_VALUE;
            double zmin = Double.MAX_VALUE;
            double zmax = -Double.MAX_VALUE;

            for (Surface surface : surfaces) {
                if (surface != null) {
                    if (surface.getType().isSolid()) {
                        Solid solid = (Solid) surface;

                        if (solid.getTransformedDataSet() != null) {
                            double[] bounds = solid.getTransformedDataSet().GetBounds();
                            xmin = Math.min(xmin, bounds[0]);
                            xmax = Math.max(xmax, bounds[1]);
                            ymin = Math.min(ymin, bounds[2]);
                            ymax = Math.max(ymax, bounds[3]);
                            zmin = Math.min(zmin, bounds[4]);
                            zmax = Math.max(zmax, bounds[5]);
                        }
                    }
                }
            }

            return new BoundingBox(xmin, xmax, ymin, ymax, zmin, zmax);
        } else {
            return new BoundingBox(0, 0, 0, 0, 0, 0);
        }
    }

    public static BoundingBox computeBoundingBox(Collection<Actor> actors) {
        if (Util.isVarArgsNotNull(actors.toArray(new Actor[0]))) {
            double xmin = Double.MAX_VALUE;
            double xmax = -Double.MAX_VALUE;
            double ymin = Double.MAX_VALUE;
            double ymax = -Double.MAX_VALUE;
            double zmin = Double.MAX_VALUE;
            double zmax = -Double.MAX_VALUE;

            for (Actor actor : actors) {
                if (actor != null) {
                    double[] bounds = actor.getBounds();
                    xmin = Math.min(xmin, bounds[0]);
                    xmax = Math.max(xmax, bounds[1]);
                    ymin = Math.min(ymin, bounds[2]);
                    ymax = Math.max(ymax, bounds[3]);
                    zmin = Math.min(zmin, bounds[4]);
                    zmax = Math.max(zmax, bounds[5]);
                }
            }

            return new BoundingBox(xmin, xmax, ymin, ymax, zmin, zmax);
        } else {
            return new BoundingBox(0, 0, 0, 0, 0, 0);
        }

    }
    
    public static void printDatasetData(vtkOpenFOAMReader reader) {
        logger.debug("[reader] ---------------" + reader.GetFileName() + "----------------");
        int patchesNumber = reader.GetNumberOfPatchArrays();
        logger.debug("[reader]      Patches Number: " + patchesNumber);
        logger.debug("[reader]      Patches List: ");
        for (int i = 0; i < patchesNumber; i++) {
            String patchName = reader.GetPatchArrayName(i);
            int status = reader.GetPatchArrayStatus(patchName);
            logger.debug("[reader]          (" + i + ") " + patchName + ", status: " + (status == 0 ? "disabled" : "enabled"));
        }

        int cellArraysNumber = reader.GetNumberOfCellArrays();
        logger.debug("[reader]      Cell Arrays Number: " + cellArraysNumber);
        logger.debug("[reader]      Cell Arrays List: ");
        for (int i = 0; i < cellArraysNumber; i++) {
            String cellArrayName = reader.GetCellArrayName(i);
            int status = reader.GetCellArrayStatus(cellArrayName);
            logger.debug("[reader]          (" + i + ") " + cellArrayName + ", status: " + (status == 0 ? "disabled" : "enabled"));
        }

        int pointArraysNumber = reader.GetNumberOfPointArrays();
        logger.debug("[reader]      Point Arrays Number: " + pointArraysNumber);
        logger.debug("[reader]      Point Arrays List: ");
        for (int i = 0; i < pointArraysNumber; i++) {
            String pointArrayName = reader.GetPointArrayName(i);
            int status = reader.GetPointArrayStatus(pointArrayName);
            logger.debug("[reader]          (" + i + ") " + pointArrayName + ", status: " + (status == 0 ? "disabled" : "enabled"));
        }

        logger.debug("[reader]      Decompose Polyhedra: " + reader.GetDecomposePolyhedra());

        int lagrangianArraysNumber = reader.GetNumberOfLagrangianArrays();
        logger.debug("[reader]      Lagrangian Arrays Number: " + lagrangianArraysNumber);
        logger.debug("[reader]      Lagrangian Arrays List: ");
        for (int i = 0; i < lagrangianArraysNumber; i++) {
            String lagrangianArrayName = reader.GetLagrangianArrayName(i);
            int status = reader.GetLagrangianArrayStatus(lagrangianArrayName);
            logger.debug("[reader]          (" + i + ") " + lagrangianArrayName + ", status: " + (status == 0 ? "disabled" : "enabled"));
        }

        logger.debug("[reader]      Read Zones: " + reader.GetReadZones());

        vtkDoubleArray times = reader.GetTimeValues();
        if (times != null) {
            logger.debug("[reader]      Time Values: [");
            for (int i = 0; i < times.GetSize(); i++) {
                logger.debug("[Time]" + times.GetValue(i) + ", ");
            }
            logger.debug("]");
        } else {
            // reader.UpdateInformation(); // Scan time steps and create
            // metadata
            vtkExecutive exe = reader.GetExecutive();
            vtkInformation outInfo = exe.GetOutputInformation(0);
            vtkInformationDoubleVectorKey timeStepsKey = new vtkStreamingDemandDrivenPipeline().TIME_STEPS();
            int nTimeSteps = outInfo.Length(timeStepsKey); // Get the number of
                                                           // time steps
            logger.debug("[reader]      Time Values: " + nTimeSteps);
            for (int i = 0; i < nTimeSteps; i++) {
                double timeValue = outInfo.Get(timeStepsKey, i); // Get the i-th
                                                                 // time value
                logger.debug("[reader]         Step: " + i + ", Value: " + timeValue);
            }
        }

        vtkExecutive exe = reader.GetExecutive();
        vtkCompositeDataPipeline pipeline = (vtkCompositeDataPipeline) exe;
        vtkInformation outInfo = exe.GetOutputInformation(0);
        vtkInformationDoubleVectorKey TIME_STEPS = pipeline.TIME_STEPS();
        vtkInformationDoubleKey UPDATE_TIME_STEP = pipeline.UPDATE_TIME_STEP();

        int nTimeSteps = outInfo.Length(TIME_STEPS); // Get the number of time steps
        logger.debug("[pipeline]    Time Values: " + nTimeSteps);
        logger.debug("[pipeline]    Time Values: current is " + outInfo.Get(UPDATE_TIME_STEP));
        for (int i = 0; i < nTimeSteps; i++) {
            double timeValue = outInfo.Get(TIME_STEPS, i); // Get the i-th time value
            logger.debug("[pipeline]       Step: " + i + ", Value: " + timeValue);

        }

        vtkMultiBlockDataSet dataset = reader.GetOutput();
        int blocksNumber = dataset.GetNumberOfBlocks();
        logger.debug("[dataset]\tBlocks Number: " + blocksNumber);
        for (int i = 0; i < blocksNumber; i++) {
            vtkDataObject block = dataset.GetBlock(i);
            String name = dataset.GetMetaData(i).Get(new vtkCompositeDataSet().NAME());
            readBlock(block, name, i, "\t");
        }
        logger.debug("[reader] ---------------" + reader.GetFileName() + "----------------");
    }

    private static void readBlock(vtkDataObject block, String name, int i, String indent) {
        if (block instanceof vtkMultiBlockDataSet) {
            vtkMultiBlockDataSet multiBlockDataSet = (vtkMultiBlockDataSet) block;
            int nBlocks = multiBlockDataSet.GetNumberOfBlocks();
            logger.debug("[dataset]" + indent + "Block {} '{}': MultiBlockDataset, {} Sub Blocks", i, name, nBlocks);
            for (int j = 0; j < nBlocks; j++) {
                vtkDataObject subBlock = multiBlockDataSet.GetBlock(j);
                String subName = multiBlockDataSet.GetMetaData(j).Get(new vtkCompositeDataSet().NAME());
                readBlock(subBlock, subName, j, indent + indent);
            }
        } else if (block instanceof vtkUnstructuredGrid) {
            logger.debug("[dataset]" + indent + "Block {} '{}': UnstructuredGrid", i, name);
            readFields((vtkDataSet) block, indent);
        } else if (block instanceof vtkPolyData) {
            logger.debug("[dataset]" + indent + "Block {} '{}': PolyData", i, name);
            readFields((vtkDataSet) block, indent);
        } else {
            logger.debug("[dataset]" + indent + "Block {} '{}': OTHER {}", i, name, block);
        }
    }

    private static void readFields(vtkDataSet dataSet, String indent) {
        String[] pointFields = getPointFields(dataSet);
        Map<String, double[][]> pointRange = getPointFieldsRanges(dataSet);
        logger.debug("[dataset]" + indent + "\tPointData Arrays Number: " + pointFields.length);
        for (int i = 0; i < pointFields.length; i++) {
            String fieldName = pointFields[i];
            if (fieldName.equals("U")) {
                logger.debug("[dataset]" + indent + "\t\t array " + i + ": " + fieldName + " [" + pointRange.get(fieldName)[0][0] + ", " + pointRange.get(fieldName)[0][1] + "]" + " [" + pointRange.get(fieldName)[1][0] + ", " + pointRange.get(fieldName)[1][1] + "]" + " [" + pointRange.get(fieldName)[2][0] + ", " + pointRange.get(fieldName)[2][1] + "]" + " [" + pointRange.get(fieldName)[3][0] + ", " + pointRange.get(fieldName)[3][1] + "]");

            } else {
                logger.debug("[dataset]" + indent + "\t\t array " + i + ": " + fieldName + " [" + pointRange.get(fieldName)[0][0] + ", " + pointRange.get(fieldName)[0][1] + "]");
            }
        }
        String[] cellFields = getCellFields(dataSet);
        Map<String, double[][]> cellRange = getCellFieldsRanges(dataSet);
        logger.debug("[dataset]" + indent + "\tCellData Arrays Number: " + cellFields.length);
        for (int i = 0; i < cellFields.length; i++) {
            String fieldName = cellFields[i];
            if (fieldName.equals("U")) {
                logger.debug("[dataset]" + indent + "\t\t array " + i + ": " + fieldName + " [" + cellRange.get(fieldName)[0][0] + ", " + cellRange.get(fieldName)[0][1] + "]" + " [" + cellRange.get(fieldName)[1][0] + ", " + cellRange.get(fieldName)[1][1] + "]" + " [" + cellRange.get(fieldName)[2][0] + ", " + cellRange.get(fieldName)[2][1] + "]" + " [" + cellRange.get(fieldName)[3][0] + ", " + cellRange.get(fieldName)[3][1] + "]");
            } else {
                logger.debug("[dataset]" + indent + "\t\t array " + i + ": " + fieldName + " [" + cellRange.get(fieldName)[0][0] + ", " + cellRange.get(fieldName)[0][1] + "]");
            }
        }

    }

    public static String[] getPointFields(vtkDataSet dataSet) {
        vtkPointData pointData = dataSet.GetPointData();
        String[] fields = new String[pointData.GetNumberOfArrays()];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = pointData.GetArrayName(i);
        }
        return fields;
    }

    public static Map<String, double[][]> getPointFieldsRanges(vtkDataSet dataSet) {
        List<vtkDataSet> ds = new ArrayList<vtkDataSet>();
        ds.add(dataSet);
        return getPointFieldsRanges(ds);
    }

    public static Map<String, double[][]> getPointFieldsRanges(List<vtkDataSet> dataSet) {
        Map<String, double[][]> rMap = new HashMap<>();
        for (vtkDataSet ds : dataSet) {
            vtkPointData pointData = ds.GetPointData();
            for (int i = 0; i < pointData.GetNumberOfArrays(); i++) {
                String fieldName = pointData.GetArrayName(i);

                if (!rMap.containsKey(fieldName)) {
                    double[][] defaultRange = new double[4][2];
                    defaultRange[0] = new double[] { Double.MAX_VALUE, -Double.MAX_VALUE };
                    defaultRange[1] = new double[] { Double.MAX_VALUE, -Double.MAX_VALUE };
                    defaultRange[2] = new double[] { Double.MAX_VALUE, -Double.MAX_VALUE };
                    defaultRange[3] = new double[] { Double.MAX_VALUE, -Double.MAX_VALUE };
                    rMap.put(fieldName, defaultRange);
                }

                double[][] newRanges = new double[4][2];
                newRanges[0] = pointData.GetScalars(fieldName).GetRange(-1);
                newRanges[1] = pointData.GetScalars(fieldName).GetRange(0);
                newRanges[2] = pointData.GetScalars(fieldName).GetRange(1);
                newRanges[3] = pointData.GetScalars(fieldName).GetRange(2);

                double[][] currentRanges = rMap.get(fieldName);
                for (int j = 0; j < 4; j++) {
                    double curMin = currentRanges[j][0];
                    double newMin = newRanges[j][0];
                    if (newMin < curMin) {
                        currentRanges[j][0] = newMin;
                    }

                    double curMax = currentRanges[j][1];
                    double newMax = newRanges[j][1];
                    if (newMax > curMax) {
                        currentRanges[j][1] = newMax;
                    }
                }
            }
        }
        return rMap;
    }

    public static String[] getCellFields(vtkDataSet dataSet) {
        vtkCellData cellData = dataSet.GetCellData();
        String[] fields = new String[cellData.GetNumberOfArrays()];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = cellData.GetArrayName(i);
        }
        return fields;
    }

    public static Map<String, double[][]> getCellFieldsRanges(vtkDataSet dataSet) {
        List<vtkDataSet> ds = new ArrayList<vtkDataSet>();
        ds.add(dataSet);
        return getCellFieldsRanges(ds);
    }

    public static Map<String, double[][]> getCellFieldsRanges(List<vtkDataSet> dataSet) {
        Map<String, double[][]> rMap = new HashMap<>();
        for (vtkDataSet ds : dataSet) {
            vtkCellData cellData = ds.GetCellData();
            for (int i = 0; i < cellData.GetNumberOfArrays(); i++) {
                String fieldName = cellData.GetArrayName(i);

                if (!rMap.containsKey(fieldName)) {
                    double[][] defaultRange = new double[4][2];
                    defaultRange[0] = new double[] { Double.MAX_VALUE, -Double.MAX_VALUE };
                    defaultRange[1] = new double[] { Double.MAX_VALUE, -Double.MAX_VALUE };
                    defaultRange[2] = new double[] { Double.MAX_VALUE, -Double.MAX_VALUE };
                    defaultRange[3] = new double[] { Double.MAX_VALUE, -Double.MAX_VALUE };
                    rMap.put(fieldName, defaultRange);
                }

                double[][] newRanges = new double[4][2];
                newRanges[0] = cellData.GetScalars(fieldName).GetRange(-1);
                newRanges[1] = cellData.GetScalars(fieldName).GetRange(0);
                newRanges[2] = cellData.GetScalars(fieldName).GetRange(1);
                newRanges[3] = cellData.GetScalars(fieldName).GetRange(2);

                if (rMap.containsKey(fieldName)) {
                    double[][] currentRanges = rMap.get(fieldName);
                    for (int j = 0; j < 4; j++) {
                        double curMin = currentRanges[j][0];
                        double newMin = newRanges[j][0];
                        if (newMin < curMin) {
                            currentRanges[j][0] = newMin;
                        }

                        double curMax = currentRanges[j][1];
                        double newMax = newRanges[j][1];
                        if (newMax > curMax) {
                            currentRanges[j][1] = newMax;
                        }
                    }
                }

            }
        }
        return rMap;
    }

    public static BoundingBox calculateBounds(List<vtkDataSet> dataSet) {
        BoundingBox bounds = new BoundingBox(Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE);
        for (vtkDataSet ds : dataSet) {
            double[] dsBounds = ds.GetBounds();
            double dsXmin = dsBounds[0];
            double dsXmax = dsBounds[1];
            double dsYmin = dsBounds[2];
            double dsYmax = dsBounds[3];
            double dsZmin = dsBounds[4];
            double dsZmax = dsBounds[5];

            if (dsXmin < bounds.getXmin()) {
                bounds.setXmin(dsXmin);
            }
            if (dsXmax > bounds.getXmax()) {
                bounds.setXmax(dsXmax);
            }
            if (dsYmin < bounds.getYmin()) {
                bounds.setYmin(dsYmin);
            }
            if (dsYmax > bounds.getYmax()) {
                bounds.setYmax(dsYmax);
            }
            if (dsZmin < bounds.getZmin()) {
                bounds.setZmin(dsZmin);
            }
            if (dsZmax > bounds.getZmax()) {
                bounds.setZmax(dsZmax);
            }
        }
        return bounds;
    }

    public static void gc(boolean debug) {
        if (VTKSettings.librariesAreLoaded()) {
            vtkReferenceInformation info = vtkObject.JAVA_OBJECT_MANAGER.gc(debug);
            if (debug) {
                logger.debug("K: " + info.listKeptReferenceToString());
                logger.debug("R: " + info.listRemovedReferenceToString());
            }
        }
    }

    public static vtkPolyData triangleFilter(vtkDataObject input) {
        vtkTriangleFilter filter = new vtkTriangleFilter();
        filter.SetInputData(input);
        // filter.PassVertsOff();
        // filter.PassLinesOff();
        filter.Update();

        return filter.GetOutput();
    }

    public static vtkPolyData geometryFilter(vtkUnstructuredGrid dataset) {
        vtkDataSetSurfaceFilter filter = new vtkDataSetSurfaceFilter();
        filter.SetInputData(dataset);
        filter.PassThroughCellIdsOn();
        filter.PassThroughPointIdsOn();
        filter.Update();

        vtkPolyData output = filter.GetOutput();
        filter.Delete();

        return output;
    }

    public static vtkPolyData repairDataSet(vtkPolyData dataset) {
        vtkCleanPolyData clean = new vtkCleanPolyData();
        // clean.ConvertLinesToPointsOff(); //def: on
        // clean.ConvertPolysToLinesOff(); //def: on
        // clean.ConvertStripsToPolysOff(); //def: on
        // clean.PieceInvariantOff(); //def: on
        // clean.PointMergingOff(); //def: on
        // clean.SetAbsoluteTolerance(0); //def: 1.0
        // clean.SetTolerance(0);//def: 0.0
        // clean.ToleranceIsAbsoluteOn(); //def: off
        clean.SetInputData(dataset);
        clean.Update();

        return clean.GetOutput();
    }

    public static void exit() {
        vtkObject.JAVA_OBJECT_MANAGER.deleteAll();
    }

//    public static void changeDataset(Actor actor, vtkPolyData subset) {
//        vtkMapper mapper = actor.getMapper();
//        ((vtkPolyDataMapper) mapper).SetInputData((vtkPolyData) subset);
//    }
//
//    public static void changeDataset(Actor actor, vtkUnstructuredGrid subset) {
//        vtkMapper mapper = actor.getMapper();
//        vtkAlgorithmOutput filterOutput = mapper.GetInputConnection(0, 0);
//
//        if (filterOutput.GetProducer() instanceof vtkPolyDataAlgorithm) {
//            vtkPolyDataAlgorithm filter = (vtkPolyDataAlgorithm) filterOutput.GetProducer();
//
//            VTKUtil.deleteDataset(filter.GetInput());
//            filter.SetInputData(subset);
//            mapper.Update();
//        } else if (filterOutput.GetProducer() instanceof vtkTrivialProducer) {
//            vtkAlgorithm filter = (vtkAlgorithm) filterOutput.GetProducer();
//            filter.SetInputDataObject(subset);
//            mapper.Update();
//        } else {
//            logger.warn("CANNOT CHANGE DATASET FOR: {}", "unstructured grid");
//        }
//    }

    public static void deleteDataset(vtkDataObject dataObject) {
        if (dataObject instanceof vtkDataSet) {
            vtkDataSet dataSet = (vtkDataSet) dataObject;
            vtkPointData pointData = dataSet.GetPointData();
            if (pointData != null) {
                vtkDataArray pScalars  = pointData.GetScalars();
                if (pScalars != null) {
                    pScalars.Delete();
                }
                pointData.Delete();
            }

            vtkCellData cellData = dataSet.GetCellData();
            if (cellData != null) {
                vtkDataArray cScalars = cellData.GetScalars();
                if (cScalars != null) {
                    cScalars.Delete();
                }
                cellData.Delete();
            }

            vtkFieldData fieldData = dataSet.GetFieldData();
            if (fieldData != null) {
                fieldData.Delete();
            }
            
            dataSet.Delete();
        } else {
            System.err.println("NOT A DATASET");
        }
    }

    public static void observe(vtkObject obj, String label) {
        ConsoleObserver o = new ConsoleObserver(obj, label);

        obj.AddObserver("AbortCheckEvent", o, "AbortCheckEvent");
        obj.AddObserver("StartEvent", o, "StartEvent");
        obj.AddObserver("EndEvent", o, "EndEvent");
        obj.AddObserver("ProgressEvent", o, "ProgressEvent");
        obj.AddObserver("TimerEvent", o, "TimerEvent");
        obj.AddObserver("ConfigureEvent", o, "ConfigureEvent");
        obj.AddObserver("ErrorEvent", o, "ErrorEvent");
        obj.AddObserver("WarningEvent", o, "WarningEvent");
        obj.AddObserver("MouseMoveEvent", o, "MouseMoveEvent");
    }

    static class ConsoleObserver {
        private vtkObject obj;
        private String label;
        private long time = 0L;

        public ConsoleObserver(vtkObject obj, String label) {
            this.obj = obj;
            this.label = label;
        }

        public void AbortCheckEvent() {
            System.err.println("+++ AbortCheckEvent +++" + obj.GetClassName() + " - " + label);
        }

        public void StartEvent() {
            System.err.println("StartEvent             " + obj.GetClassName() + " - " + label);
            this.time = System.currentTimeMillis();
        }

        public void EndEvent() {
            System.err.println("EndEvent               " + obj.GetClassName() + " - " + label + " - ET: " + (System.currentTimeMillis() - time) / 1000D + " sec");
        }

        public void ProgressEvent() {
            System.err.println("ProgressEvent          " + obj.GetClassName() + " - " + label);
        }

        public void ConfigureEvent() {
            System.err.println("ConfigureEvent         " + obj.GetClassName() + " - " + label);
        }

        public void TimerEvent() {
            System.err.println("TimerEvent             " + obj.GetClassName() + " - " + label);
        }

        public void ErrorEvent() {
            System.err.println("ErrorEvent             " + obj.GetClassName() + " - " + label);
        }

        public void WarningEvent() {
            System.err.println("WarningEvent           " + obj.GetClassName() + " - " + label);
        }

        public void MouseMoveEvent() {
            System.err.println("MouseMoveEvent           " + obj.GetClassName() + " - " + label);
        }
    }

    public static String logBlockNames(vtkMultiBlockDataSet dataset) {
        StringBuilder sb = new StringBuilder();
        int numberOfBlocks = dataset.GetNumberOfBlocks();
        for (int i = 0; i < numberOfBlocks; i++) {
            String name = dataset.GetMetaData(i).Get(new vtkCompositeDataSet().NAME());
            sb.append("(" + i + ") ");
            sb.append(name);
            sb.append(" ");
        }

        return sb.toString();
    }

    public static vtkPolyData intersect(vtkPolyData input1, vtkPolyData input2, boolean triangulateInput1, boolean triangulateInput2) {
        vtkIntersectionPolyDataFilter intersect = new vtkIntersectionPolyDataFilter();

        if (triangulateInput1) {
            vtkTriangleFilter triangle = new vtkTriangleFilter();
            triangle.SetInputData(input1);
            triangle.Update();

            intersect.SetInputData(0, triangle.GetOutput());
        } else {
            intersect.SetInputData(0, input1);
        }

        if (triangulateInput2) {
            vtkTriangleFilter triangle = new vtkTriangleFilter();
            triangle.SetInputData(input2);
            triangle.Update();

            intersect.SetInputData(1, triangle.GetOutput());
        } else {
            intersect.SetInputData(1, input2);
        }

        intersect.SplitFirstOutputOff();
        intersect.SplitSecondOutputOff();
        intersect.Update();
        
        return intersect.GetOutput();
    }
    
    public static vtkPolyData getPlane(BoundingBox bb, double[] normal) { 
        vtkPlaneSource planeSource = new vtkPlaneSource();
        planeSource.SetOrigin(0, 0, 0);
        planeSource.SetPoint1(bb.getDiagonal(), 0, 0);
        planeSource.SetPoint2(0, bb.getDiagonal(), 0);
        planeSource.SetCenter(bb.getCenter());
        planeSource.SetNormal(normal);
        planeSource.Update();

        return planeSource.GetOutput();
    }
    
    public static boolean areTouching(vtkPolyData dataSet1, vtkPolyData dataSet2) {
        vtkIntersectionPolyDataFilter intersect = new vtkIntersectionPolyDataFilter();
        intersect.SetInputData(0, dataSet1);
        intersect.SetInputData(1, dataSet2);

        intersect.SplitFirstOutputOff();
        intersect.SplitSecondOutputOff();
        intersect.Update();

        vtkPolyData output = intersect.GetOutput();

        return output.GetNumberOfPoints() != 0;
    }

    public static vtkActor createTestActor(vtkPolyData input, double[] color, double[] lineColor) {
        vtkMapper mapper = new vtkPolyDataMapper();
        mapper.SetInputDataObject(input);
        mapper.ScalarVisibilityOff();

        vtkActor actor = new vtkActor();
        actor.SetMapper(mapper);
        actor.GetProperty().SetRepresentationToWireframe();
        actor.GetProperty().SetEdgeColor(lineColor);
        actor.GetProperty().SetColor(color);
        actor.GetProperty().EdgeVisibilityOn();
        actor.GetProperty().SetLineWidth(1);

        mapper.Update();

        return actor;
    }

    public static vtkRenderWindow showRenderPanel(vtkActor... actors) {
        vtkRenderer ren = new vtkRenderer();
        ren.GradientBackgroundOn();
        ren.SetBackground(0.1D, 0.1D, 0.1D);
        ren.SetBackground2(0.4D, 0.4D, 0.4D);

        vtkRenderWindow renWin = new vtkRenderWindow();
        renWin.AddRenderer(ren);
        renWin.SetSize(1024, 768);

        for (vtkActor actor : actors) {
            ren.AddActor(actor);
        }

        vtkRenderWindowInteractor iren = new vtkRenderWindowInteractor();
        iren.SetInteractorStyle(new vtkInteractorStyleTrackballCamera());
        iren.SetRenderWindow(renWin);

        iren.Start();

        return renWin;
    }

    public static boolean isSingleRegion(vtkMultiBlockDataSet dataset) {
        int numberOfBlocks = dataset.GetNumberOfBlocks();
        if (numberOfBlocks > 0) {
            String blockName = dataset.GetMetaData(0).Get(new vtkCompositeDataSet().NAME());
            return blockName.equals(ExternalMeshReader.PATCHES) || blockName.equals(ExternalMeshReader.ZONES) || blockName.equals(InternalMeshReader.INTERNAL_MESH);
        }
        return false;
    }

    public static vtkDataObject getBlock(String blockName, vtkMultiBlockDataSet dataset) {
        int numberOfBlocks = dataset.GetNumberOfBlocks();
        for (int i = 0; i < numberOfBlocks; i++) {
            String name = dataset.GetMetaData(i).Get(new vtkCompositeDataSet().NAME());
            if (blockName.equals(name)) {
                return dataset.GetBlock(i);
            }
        }
        return null;
    }

    public static vtkUnstructuredGrid shallowCopy(vtkUnstructuredGrid data) {
        vtkUnstructuredGrid copy = new vtkUnstructuredGrid();
        copy.ShallowCopy(data);
        return copy;
    }

    public static vtkPolyData shallowCopy(vtkPolyData data) {
        vtkPolyData copy = new vtkPolyData();
        copy.ShallowCopy(data);
        return copy;
    }

    public static final String DEFAULT_REGION = "defaultRegion";
    
    
    public static vtkPolyData createSimplePointMarkDataset() {
        double L = 0.2;
        double Z = 3.9*L;

        vtkConeSource coneSource = new vtkConeSource();
        coneSource.SetHeight(2*L);
        coneSource.SetRadius(L/2);
        coneSource.CappingOn();
        coneSource.SetCenter(0, 0, L);
        coneSource.SetDirection(0, 0, -1);
        coneSource.SetResolution(30);
        coneSource.Update();

        vtkPolyData outputMesh = coneSource.GetOutput();
        
        vtkPolyDataNormals normals = new vtkPolyDataNormals();
        normals.SetInputData(outputMesh);
        normals.Update();

        return normals.GetOutput();
    }
    
    public static vtkPolyData createPointMarkDataset() {
        double L = 0.2;
        double Z = 3.9*L;
        
        vtkLineSource lineSource = new vtkLineSource();
        lineSource.SetPoint1(-L/4,0,Z);
        lineSource.SetPoint2(L/4,0,Z);
        
        vtkTubeFilter labelTubeFilter = new vtkTubeFilter();
        labelTubeFilter.SetInputConnection(lineSource.GetOutputPort());
        labelTubeFilter.CappingOn();
        labelTubeFilter.SetRadius(L);
        labelTubeFilter.SetNumberOfSides(50);
        labelTubeFilter.Update();

        lineSource = new vtkLineSource();
        lineSource.SetPoint1(-L/2,0,Z);
        lineSource.SetPoint2(L/2,0,Z);
        
        vtkTubeFilter internalTubeFilter = new vtkTubeFilter();
        internalTubeFilter.SetInputConnection(lineSource.GetOutputPort());
        internalTubeFilter.CappingOff();
        internalTubeFilter.SetRadius(L);
        internalTubeFilter.SetNumberOfSides(50);
        internalTubeFilter.Update();

        vtkTubeFilter externalTubeFilter = new vtkTubeFilter();
        externalTubeFilter.SetInputConnection(lineSource.GetOutputPort());
        externalTubeFilter.CappingOff();
        externalTubeFilter.SetRadius(2*L);
        externalTubeFilter.SetNumberOfSides(50);
        externalTubeFilter.Update();
        
        vtkConeSource coneSource = new vtkConeSource();
        coneSource.SetHeight(2*L);
        coneSource.SetRadius(L/2);
        coneSource.CappingOff();
        coneSource.SetCenter(0, 0, L);
        coneSource.SetDirection(0, 0, -1);
        coneSource.SetResolution(30);
        coneSource.Update();

        vtkAppendPolyData append = new vtkAppendPolyData();
        append.AddInputData(internalTubeFilter.GetOutput());
        append.AddInputData(externalTubeFilter.GetOutput());
        append.AddInputData(coneSource.GetOutput());
        append.AddInputData(labelTubeFilter.GetOutput());
        append.Update();

        vtkPolyData outputMesh = new vtkPolyData();
        outputMesh.DeepCopy(append.GetOutput());
        vtkCellArray outputTriangles = outputMesh.GetPolys();

        int length = internalTubeFilter.GetOutput().GetNumberOfPoints();
        for (int ptId = 0; ptId < 50; ptId++) {
            // Triangle one extremity
            vtkIdList triangle = new vtkIdList();
            triangle.InsertNextId(ptId);
            triangle.InsertNextId(ptId + length);
            triangle.InsertNextId((ptId + 1) % 50 + length);
            outputTriangles.InsertNextCell(triangle);

            triangle = new vtkIdList();
            triangle.InsertNextId(ptId);
            triangle.InsertNextId((ptId + 1) % 50 + length);
            triangle.InsertNextId((ptId + 1) % 50);
            outputTriangles.InsertNextCell(triangle);

            // Triangle the other extremity
            int offset = length - 50;
            triangle = new vtkIdList();
            triangle.InsertNextId(ptId + offset);
            triangle.InsertNextId(ptId + +offset + length);
            triangle.InsertNextId((ptId + 1) % 50 + offset + length);
            outputTriangles.InsertNextCell(triangle);

            triangle = new vtkIdList();
            triangle.InsertNextId((ptId + 1) % 50 + length + offset);
            triangle.InsertNextId((ptId + 1) % 50 + offset);
            triangle.InsertNextId(ptId + offset);
            outputTriangles.InsertNextCell(triangle);
        }

        vtkPolyDataNormals normals = new vtkPolyDataNormals();
        normals.SetInputData(outputMesh);
        normals.Update();

        return normals.GetOutput();
    }

    public static void printTotalMemory(vtkDataSet... dataSets) {
        int total = 0;
        for (vtkDataSet dataSet : dataSets) {
            total += dataSet.GetActualMemorySize();
        }
        logger.debug("TOTAL 3D MEMORY: {}", FormatUtil.format(total/1024D).toCents() + " MB");
    }
    
}
