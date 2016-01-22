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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vtk.vtkAlgorithm;
import vtk.vtkAlgorithmOutput;
import vtk.vtkCellData;
import vtk.vtkCleanPolyData;
import vtk.vtkCompositeDataPipeline;
import vtk.vtkCompositeDataSet;
import vtk.vtkDataArray;
import vtk.vtkDataObject;
import vtk.vtkDataSet;
import vtk.vtkDataSetSurfaceFilter;
import vtk.vtkDoubleArray;
import vtk.vtkExecutive;
import vtk.vtkInformation;
import vtk.vtkInformationDoubleKey;
import vtk.vtkInformationDoubleVectorKey;
import vtk.vtkMapper;
import vtk.vtkMultiBlockDataSet;
import vtk.vtkObject;
import vtk.vtkOpenFOAMReader;
import vtk.vtkPointData;
import vtk.vtkPolyData;
import vtk.vtkPolyDataAlgorithm;
import vtk.vtkPolyDataMapper;
import vtk.vtkReferenceInformation;
import vtk.vtkStreamingDemandDrivenPipeline;
import vtk.vtkTrivialProducer;
import vtk.vtkUnstructuredGrid;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Controller3D;
import eu.engys.util.Util;
import eu.engys.util.VTKSettings;

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

	public static BoundingBox computeBoundingBox(Collection<Actor> actors) {
	    if(Util.isVarArgsNotNull(actors.toArray(new Actor[0]))){
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
			logger.debug("[dataset]" + indent + "Block {} '{}': OTHER {}", i,  name, block);
		}
	}

	private static void readFields(vtkDataSet dataSet, String indent) {
		String[] pointFields = getPointFields(dataSet);
		logger.debug("[dataset]" + indent + "\tPointData Arrays Number: " + pointFields.length);
		for (int i = 0; i < pointFields.length; i++) {
			logger.debug("[dataset]" + indent + "\t\t array " + i + ": " + pointFields[i]);
		}
		String[] cellFields = getCellFields(dataSet);
		logger.debug("[dataset]" + indent + "\tCellData Arrays Number: " + cellFields.length);
		for (int i = 0; i < cellFields.length; i++) {
			logger.debug("[dataset]" + indent + "\t\t array " + i + ": " + cellFields[i]);
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

	public static String[] getCellFields(vtkDataSet dataSet) {
		vtkCellData cellData = dataSet.GetCellData();
		String[] fields = new String[cellData.GetNumberOfArrays()];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = cellData.GetArrayName(i);
		}
		return fields;
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

	public static vtkPolyData geometryFilter(vtkUnstructuredGrid dataset) {
	    vtkUnstructuredGrid input = new vtkUnstructuredGrid();
	    input.ShallowCopy(dataset);
	    
	    vtkDataSetSurfaceFilter filter = new vtkDataSetSurfaceFilter();
	    filter.SetInputData(input);
	    filter.PassThroughCellIdsOn();
	    filter.PassThroughPointIdsOn();
	    filter.Update();
	    input.Delete();
	    
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

	public static void changeDataset(Actor actor, vtkPolyData subset) {
		vtkMapper mapper = actor.getMapper();
		((vtkPolyDataMapper) mapper).SetInputData((vtkPolyData) subset);
	}

	public static void changeDataset(Actor actor, vtkUnstructuredGrid subset) {
		vtkMapper mapper = actor.getMapper();
		vtkAlgorithmOutput filterOutput = mapper.GetInputConnection(0, 0);
		
		if (filterOutput.GetProducer() instanceof vtkPolyDataAlgorithm ) {
		    vtkPolyDataAlgorithm filter = (vtkPolyDataAlgorithm) filterOutput.GetProducer();
		    
		    VTKUtil.deleteDataset(filter.GetInput());
		    filter.SetInputData(subset);
		    mapper.Update();
		} else if (filterOutput.GetProducer() instanceof vtkTrivialProducer ) {
		    vtkAlgorithm filter = (vtkAlgorithm) filterOutput.GetProducer();
		    filter.SetInputDataObject(subset);
		    mapper.Update();
		} else {
		    logger.warn("CANNOT CHANGE DATASET FOR: {}", "unstructured grid");
		}
	}

	public static void deleteDataset(vtkDataObject dataObject) {
		if (dataObject instanceof vtkDataSet) {
			vtkDataSet dataSet = (vtkDataSet) dataObject;
			vtkPointData pointData = dataSet.GetPointData();
			vtkDataArray pScalars = pointData.GetScalars();
			if (pScalars != null)
				pScalars.Delete();
			pointData.Delete();

			vtkCellData cellData = dataSet.GetCellData();
			vtkDataArray cScalars = cellData.GetScalars();
			if (cScalars != null)
				cScalars.Delete();
			cellData.Delete();

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
    }   
    
	static class ConsoleObserver {
	    private vtkObject obj;
        private String label;
        private long time = 0L;
        public ConsoleObserver(vtkObject obj, String label) {
            this.obj = obj;
            this.label = label;
        }
        public void AbortCheckEvent() { System.err.println("+++ AbortCheckEvent +++" + obj.GetClassName() + " - " + label); }
	    public void StartEvent()      { System.err.println("StartEvent             " + obj.GetClassName() + " - " + label); this.time = System.currentTimeMillis();}
	    public void EndEvent()        { System.err.println("EndEvent               " + obj.GetClassName() + " - " + label + " - ET: " + (System.currentTimeMillis() - time)/1000D + " sec");}
	    public void ProgressEvent()   { System.err.println("ProgressEvent          " + obj.GetClassName() + " - " + label);}
	    public void ConfigureEvent()  { System.err.println("ConfigureEvent         " + obj.GetClassName() + " - " + label);}
	    public void TimerEvent()      { System.err.println("TimerEvent             " + obj.GetClassName() + " - " + label);}
	    public void ErrorEvent()      { System.err.println("ErrorEvent             " + obj.GetClassName() + " - " + label);}
	    public void WarningEvent()    { System.err.println("WarningEvent           " + obj.GetClassName() + " - " + label);}
	}

    public static  String logBlockNames(vtkMultiBlockDataSet dataset) {
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
}
