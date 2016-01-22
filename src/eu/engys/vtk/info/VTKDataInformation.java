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


package eu.engys.vtk.info;

import java.io.PrintStream;

import vtk.vtkAlgorithm;
import vtk.vtkAlgorithmOutput;
import vtk.vtkCompositeDataSet;
import vtk.vtkDataObject;
import vtk.vtkDataSet;
import vtk.vtkFieldData;
import vtk.vtkGenericDataSet;
import vtk.vtkImageData;
import vtk.vtkInformation;
import vtk.vtkMultiBlockDataSet;
import vtk.vtkObject;
import vtk.vtkPointSet;
import vtk.vtkRectilinearGrid;
import vtk.vtkStructuredGrid;
import vtk.vtkUniformGrid;
import eu.engys.util.Util;

public class VTKDataInformation {

	private VTKCompositeDataInformation CompositeDataInformation = new VTKCompositeDataInformation();
	private VTKDataSetAttributesInformation PointDataInformation = new VTKDataSetAttributesInformation();
	private VTKDataSetAttributesInformation CellDataInformation = new VTKDataSetAttributesInformation();
	private VTKDataSetAttributesInformation VertexDataInformation = new VTKDataSetAttributesInformation();
	private VTKDataSetAttributesInformation EdgeDataInformation = new VTKDataSetAttributesInformation();
	private VTKDataSetAttributesInformation RowDataInformation = new VTKDataSetAttributesInformation();
	private VTKDataSetAttributesInformation FieldDataInformation = new VTKDataSetAttributesInformation();
	private VTKArrayInformation PointArrayInformation = new VTKArrayInformation();

	private int CompositeDataSetType, DataSetType, NumberOfPoints, NumberOfCells, NumberOfRows, MemorySize, PolygonCount, NumberOfDataSets, HasTime, PortNumber;
	private String DataClassName, TimeLabel, CompositeDataClassName;
	private double[] Bounds;
	private int[] Extent;
	private double Time;
	private boolean SortArrays;

	public VTKDataInformation() {
		Initialize();
	}

	private void Initialize() {
		this.DataSetType = -1;
		this.CompositeDataSetType = -1;
		this.NumberOfPoints = 0;
		this.NumberOfCells = 0;
		this.NumberOfRows = 0;
		this.NumberOfDataSets = 0;
		this.MemorySize = 0;
		this.PolygonCount = 0;
		this.Bounds = new double[6];
		this.Bounds[0] = this.Bounds[2] = this.Bounds[4] = Double.MAX_VALUE;
		this.Bounds[1] = this.Bounds[3] = this.Bounds[5] = -Double.MAX_VALUE;
		this.Extent = new int[6];
		this.Extent[0] = this.Extent[2] = this.Extent[4] = Integer.MAX_VALUE;
		this.Extent[1] = this.Extent[3] = this.Extent[5] = -Integer.MAX_VALUE;
		this.PointDataInformation.Initialize();
		this.CellDataInformation.Initialize();
		this.VertexDataInformation.Initialize();
		this.EdgeDataInformation.Initialize();
		this.RowDataInformation.Initialize();
		this.FieldDataInformation.Initialize();
		this.CompositeDataInformation.Initialize();
		this.PointArrayInformation.Initialize();
		this.DataClassName = "";
		this.CompositeDataClassName = "";
		// this.TimeSpan[0] = VTK_DOUBLE_MAX;
		// this.TimeSpan[1] = -VTK_DOUBLE_MAX;
		this.HasTime = 0;
		this.Time = 0.0;
		// this.SetTimeLabel(NULL);
	}

	private void DeepCopy(VTKDataInformation dataInfo, boolean copyCompositeInformation) {

		this.DataSetType = dataInfo.DataSetType;
		this.CompositeDataSetType = dataInfo.CompositeDataSetType;
		this.DataClassName = dataInfo.DataClassName;
		this.CompositeDataClassName = dataInfo.CompositeDataClassName;

		this.NumberOfDataSets = dataInfo.NumberOfDataSets;

		this.NumberOfPoints = dataInfo.NumberOfPoints;
		this.NumberOfCells = dataInfo.NumberOfCells;
		this.NumberOfRows = dataInfo.NumberOfRows;
		this.MemorySize = dataInfo.MemorySize;
		this.PolygonCount = dataInfo.PolygonCount;

		Util.deepCopy(dataInfo.Bounds, this.Bounds);
		Util.deepCopy(dataInfo.Extent, this.Extent);

		// Copy attribute information.
		this.PointDataInformation.DeepCopy(dataInfo.PointDataInformation);
		this.CellDataInformation.DeepCopy(dataInfo.CellDataInformation);
		this.VertexDataInformation.DeepCopy(dataInfo.VertexDataInformation);
		this.EdgeDataInformation.DeepCopy(dataInfo.EdgeDataInformation);
		this.RowDataInformation.DeepCopy(dataInfo.RowDataInformation);
		this.FieldDataInformation.DeepCopy(dataInfo.FieldDataInformation);
		if (copyCompositeInformation) {
			this.CompositeDataInformation.AddInformation(dataInfo.CompositeDataInformation);
		}
		this.PointArrayInformation.AddInformation(dataInfo.PointArrayInformation);

		// double *timespan;
		// timespan = dataInfo.GetTimeSpan();
		// this.TimeSpan[0] = timespan[0];
		// this.TimeSpan[1] = timespan[1];
		// this.SetTimeLabel(dataInfo.GetTimeLabel());
	}

	public void AddFromMultiBlockDataSet(vtkMultiBlockDataSet data) {
		for (int i = 0; i < data.GetNumberOfBlocks(); i++) {
			vtkDataObject block = data.GetBlock(i);
			if (block != null) {
				VTKDataInformation dinf = new VTKDataInformation();
				dinf.CopyFromObject(block);
				dinf.DataClassName = block.GetClassName();
				dinf.DataSetType = block.GetDataObjectType();
				this.AddInformation(dinf, true);
				block.Delete();
			}
		}

	}

	public void CopyFromObject(vtkObject object) {
		vtkDataObject dobj = null;
		vtkInformation info = null;
		// Handle the case where the a vtkAlgorithmOutput is passed instead of
		// the data object. vtkSMPart uses vtkAlgorithmOutput.
		if (!(object instanceof vtkDataObject)) {
			if (object instanceof vtkAlgorithmOutput) {
				vtkAlgorithmOutput algOutput = (vtkAlgorithmOutput) object;
				vtkAlgorithm producer = algOutput.GetProducer();
				if (producer != null && producer.GetClassName().equals("vtkPVNullSource")) {
					// Don't gather any data information from the hypothetical
					// null source.
					return;
				}

				if (producer.IsA("vtkPVPostFilter") == 0) {
					algOutput = producer.GetInputConnection(0, 0);
				}
				info = producer.GetOutputPortInformation(this.PortNumber);
				dobj = producer.GetOutputDataObject(algOutput.GetIndex());

				producer.Delete();
				algOutput.Delete();
			} else if (object instanceof vtkAlgorithm) {
				vtkAlgorithm algo = (vtkAlgorithm) object;
				// We don't use vtkAlgorithm::GetOutputDataObject() since that
				// call a UpdateDataObject() pass, which may raise errors if the
				// algo
				// is not fully setup yet.
				if (algo.GetClassName().equals("vtkPVNullSource")) {
					// Don't gather any data information from the hypothetical
					// null source.
					return;
				}
				info = algo.GetExecutive().GetOutputInformation(this.PortNumber);
				// if (!info || vtkDataObject::GetData(info) == NULL)
				// {
				// return;
				// }
				dobj = algo.GetOutputDataObject(this.PortNumber);
				algo.Delete();
			}
		} else {
			dobj = (vtkDataObject) object;
		}

		if (object instanceof vtkCompositeDataSet) {
			vtkCompositeDataSet cds = (vtkCompositeDataSet) dobj;
			this.CopyFromCompositeDataSet(cds);
			this.CopyCommonMetaData(cds, info);
			return;
		}

		if (object instanceof vtkDataSet) {
			vtkDataSet ds = (vtkDataSet) dobj;
			this.CopyFromDataSet(ds);
			this.CopyCommonMetaData(ds, info);
			return;
		}

		if (object instanceof vtkGenericDataSet) {
			vtkGenericDataSet ads = (vtkGenericDataSet) dobj;
			this.CopyFromGenericDataSet(ads);
			this.CopyCommonMetaData(ads, info);
			return;
		}

		// vtkGraph* graph = vtkGraph::SafeDownCast(dobj);
		// if( graph)
		// {
		// this.CopyFromGraph(graph);
		// this.CopyCommonMetaData(dobj, info);
		// return;
		// }
		//
		// vtkTable* table = vtkTable::SafeDownCast(dobj);
		// if (table)
		// {
		// this.CopyFromTable(table);
		// this.CopyCommonMetaData(dobj, info);
		// return;
		// }
		//
		// vtkSelection* selection = vtkSelection::SafeDownCast(dobj);
		// if (selection)
		// {
		// this.CopyFromSelection(selection);
		// this.CopyCommonMetaData(dobj, info);
		// return;
		// }
		//
		// String cname = dobj.GetClassName();
		// vtkPVDataInformationHelper *dhelper =
		// vtkPVDataInformation::FindHelper
		// (cname);
		// if (dhelper)
		// {
		// dhelper.CopyFromDataObject(this, dobj);
		// this.CopyCommonMetaData(dobj, info);
		// dhelper.Delete();
		// return;
		// }

		// Because custom applications may implement their own data
		// object types, this isn't an error condition - just
		// display the name of the data object and return quietly.
		this.DataClassName = dobj.GetClassName();
		this.CopyCommonMetaData(dobj, info);
	}

	private void CopyFromCompositeDataSetInitialize(vtkCompositeDataSet data) {
		this.Initialize();
		this.CompositeDataInformation.CopyFromObject(data);
	}

	private void CopyFromCompositeDataSetFinalize(vtkCompositeDataSet data) {
		this.CompositeDataClassName = data.GetClassName();
		this.CompositeDataSetType = data.GetDataObjectType();

		if (this.DataSetType == -1) {
			// This is a composite dataset with no non-empty leaf node. Set some
			// data type (Look at BUG #7144).
			this.DataClassName = "vtkDataSet";
			this.DataSetType = VTKConstants.VTK_DATA_SET;
		}
	}

	private void CopyFromCompositeDataSet(vtkCompositeDataSet data) {
		this.CopyFromCompositeDataSetInitialize(data);

		int numDataSets = this.CompositeDataInformation.GetNumberOfChildren();
		if (this.CompositeDataInformation.GetDataIsMultiPiece()) {
		} else {
			for (int cc = 0; cc < numDataSets; cc++) {
				VTKDataInformation childInfo = this.CompositeDataInformation.GetDataInformation(cc);
				if (childInfo != null) {
					this.AddInformation(childInfo, true);
				}
			}
		}

		this.CopyFromCompositeDataSetFinalize(data);

		// AddInformation should have updated NumberOfDataSets correctly to
		// count number of non-zero datasets. We don't need to fix it here.
		// this.NumberOfDataSets = numDataSets;
	}

	private void CopyCommonMetaData(vtkDataObject data, vtkInformation pinfo) {
		// Gather some common stuff
		// if (pinfo &&
		// pinfo.Has(vtkStreamingDemandDrivenPipeline::TIME_RANGE()))
		// {
		// double *times =
		// pinfo.Get(vtkStreamingDemandDrivenPipeline::TIME_RANGE());
		// this.TimeSpan[0] = times[0];
		// this.TimeSpan[1] = times[1];
		// }
		//
		// this.SetTimeLabel(
		// (pinfo &&
		// pinfo.Has(vtkStreamingDemandDrivenPipeline::TIME_LABEL_ANNOTATION()))
		// ?
		// pinfo.Get(vtkStreamingDemandDrivenPipeline::TIME_LABEL_ANNOTATION())
		// : NULL);
		//
		// vtkInformation *dinfo = data.GetInformation();
		// if (dinfo.Has(vtkDataObject::DATA_TIME_STEP()))
		// {
		// double time = dinfo.Get(vtkDataObject::DATA_TIME_STEP());
		// this.Time = time;
		// this.HasTime = 1;
		// }
	}

	private void CopyFromDataSet(vtkDataSet data) {
		int idx;
		double[] bds = null;
		int[] ext = null;

		this.DataClassName = data.GetClassName();
		this.DataSetType = data.GetDataObjectType();

		this.NumberOfDataSets = 1;

		switch (this.DataSetType) {
		case VTKConstants.VTK_IMAGE_DATA:
			ext = ((vtkImageData) data).GetExtent();
			break;
		case VTKConstants.VTK_STRUCTURED_GRID:
			ext = ((vtkStructuredGrid) data).GetExtent();
			break;
		case VTKConstants.VTK_RECTILINEAR_GRID:
			ext = ((vtkRectilinearGrid) data).GetExtent();
			break;
		case VTKConstants.VTK_UNIFORM_GRID:
			ext = ((vtkUniformGrid) data).GetExtent();
			break;
		case VTKConstants.VTK_UNSTRUCTURED_GRID:
		case VTKConstants.VTK_POLY_DATA:
			this.PolygonCount = data.GetNumberOfCells();
			break;
		}
		if (ext != null) {
			for (idx = 0; idx < 6; ++idx) {
				this.Extent[idx] = ext[idx];
			}
		}

		this.NumberOfPoints = data.GetNumberOfPoints();
		if (this.NumberOfPoints == 0) {
			return;
		}

		// We do not want to get the number of dual cells from an octree
		// because this triggers generation of connectivity arrays.
		if (data.GetDataObjectType() != VTKConstants.VTK_HYPER_OCTREE) {
			this.NumberOfCells = data.GetNumberOfCells();
		}

		bds = data.GetBounds();
		for (idx = 0; idx < 6; ++idx) {
			this.Bounds[idx] = bds[idx];
		}
		this.MemorySize = data.GetActualMemorySize();

		if (data instanceof vtkPointSet) {
			vtkPointSet ps = (vtkPointSet) data;
			if (ps.GetPoints() != null) {
				this.PointArrayInformation.CopyFromObject(ps.GetPoints().GetData());
			}
		}

		// Copy Point Data information
		this.PointDataInformation.CopyFromDataSetAttributes(data.GetPointData());

		// Copy Cell Data information
		this.CellDataInformation.CopyFromDataSetAttributes(data.GetCellData());

		// Copy Field Data information, if any
		vtkFieldData fd = data.GetFieldData();
		if (fd != null && fd.GetNumberOfArrays() > 0) {
			this.FieldDataInformation.CopyFromFieldData(fd);
		}
	}

	private void CopyFromGenericDataSet(vtkGenericDataSet data) {
		this.DataClassName = data.GetClassName();
		this.DataSetType = data.GetDataObjectType();

		this.NumberOfDataSets = 1;
		this.NumberOfPoints = data.GetNumberOfPoints();
		if (this.NumberOfPoints == 0) {
			return;
		}
		// We do not want to get the number of dual cells from an octree
		// because this triggers generation of connectivity arrays.
		if (data.GetDataObjectType() != VTKConstants.VTK_HYPER_OCTREE) {
			this.NumberOfCells = data.GetNumberOfCells(-1);
		}

		data.GetBounds(this.Bounds);

		this.MemorySize = data.GetActualMemorySize();

		switch (this.DataSetType) {
		case VTKConstants.VTK_POLY_DATA:
			this.PolygonCount = data.GetNumberOfCells(2);
			break;
		}

		// Copy Point Data information
		this.PointDataInformation.CopyFromGenericAttributesOnPoints(data.GetAttributes());

		// Copy Cell Data information
		this.CellDataInformation.CopyFromGenericAttributesOnCells(data.GetAttributes());
	}

	public void AddInformation(VTKDataInformation info) {
		this.AddInformation(info, false);
	}

	private void AddInformation(VTKDataInformation info, boolean addingParts) {
		if (info == null) {
			System.err.println("Cound not cast object to data information.");
			return;
		}

		// if (!addingParts) {
		// this.SetCompositeDataClassName(info.CompositeDataClassName);
		// this.CompositeDataSetType = info.CompositeDataSetType;
		// this.CompositeDataInformation.AddInformation(info.CompositeDataInformation);
		// }

		if (info.NumberOfDataSets == 0) {
			return;
		}

		if (this.NumberOfPoints == 0 && this.NumberOfCells == 0 && this.NumberOfDataSets == 0) {
			// Just copy the other array information.
			this.DeepCopy(info, !addingParts);
			return;
		}

		// For data set, lets pick the common super class.
		// This supports Heterogeneous collections.
		// We need a new classification: Structured.
		// This would allow extracting grid from mixed structured collections.
		if (this.DataSetType != info.DataSetType) { // IsTypeOf method will not
													// work here. Must be done
													// manually.
			if (this.DataSetType == VTKConstants.VTK_IMAGE_DATA || this.DataSetType == VTKConstants.VTK_RECTILINEAR_GRID || this.DataSetType == VTKConstants.VTK_DATA_SET || info.DataSetType == VTKConstants.VTK_IMAGE_DATA || info.DataSetType == VTKConstants.VTK_RECTILINEAR_GRID || info.DataSetType == VTKConstants.VTK_DATA_SET) {
				this.DataSetType = VTKConstants.VTK_DATA_SET;
				this.DataClassName = "vtkDataSet";
			} else {
				if (this.DataSetType == VTKConstants.VTK_GENERIC_DATA_SET || info.DataSetType == VTKConstants.VTK_GENERIC_DATA_SET) {
					this.DataSetType = VTKConstants.VTK_GENERIC_DATA_SET;
					this.DataClassName = "vtkGenericDataSet";
				} else {
					this.DataSetType = VTKConstants.VTK_POINT_SET;
					this.DataClassName = "vtkPointSet";
				}
			}
		}

		// Empty data set? Ignore bounds, extent and array info.
		if (info.NumberOfCells == 0 && info.NumberOfPoints == 0) {
			return;
		}

		// First the easy stuff.
		this.NumberOfPoints += info.NumberOfPoints;
		this.NumberOfCells += info.NumberOfCells;
		this.MemorySize += info.MemorySize;
		this.NumberOfRows += info.NumberOfRows;

		switch (this.DataSetType) {
		case VTKConstants.VTK_POLY_DATA:
			this.PolygonCount += info.NumberOfCells;
			break;
		}
		if (addingParts) {
			// Adding data information of parts
			this.NumberOfDataSets += info.NumberOfDataSets;
		} else {
			// Adding data information of 1 part across processors
			if (this.CompositeDataClassName != null) {
				// Composite data blocks are not distributed across processors.
				// Simply add their number.
				this.NumberOfDataSets += info.NumberOfDataSets;
			} else {
				// Simple data blocks are distributed across processors, use
				// the largest number (actually, NumberOfDataSets should always
				// be 1 since the data information is for a part)
				if (this.NumberOfDataSets < info.NumberOfDataSets) {
					this.NumberOfDataSets = info.NumberOfDataSets;
				}
			}
		}

		// Bounds are only a little harder.
		double[] bds = info.Bounds;
		for (int i = 0; i < 3; ++i) {
			int j = i * 2;
			if (bds[j] < this.Bounds[j]) {
				this.Bounds[j] = bds[j];
			}
			++j;
			if (bds[j] > this.Bounds[j]) {
				this.Bounds[j] = bds[j];
			}
		}

		// Extents are only a little harder.
		int[] ext = info.Extent;
		for (int i = 0; i < 3; ++i) {
			int j = i * 2;
			if (ext[j] < this.Extent[j]) {
				this.Extent[j] = ext[j];
			}
			++j;
			if (ext[j] > this.Extent[j]) {
				this.Extent[j] = ext[j];
			}
		}

		// Now for the messy part, all of the arrays.
		this.PointArrayInformation.AddInformation(info.PointArrayInformation);
		this.PointDataInformation.AddInformation(info.PointDataInformation);
		this.CellDataInformation.AddInformation(info.CellDataInformation);
		this.VertexDataInformation.AddInformation(info.VertexDataInformation);
		this.EdgeDataInformation.AddInformation(info.EdgeDataInformation);
		this.RowDataInformation.AddInformation(info.RowDataInformation);
		this.FieldDataInformation.AddInformation(info.FieldDataInformation);
		// this.GenericAttributesInformation.AddInformation(info.GetGenericAttributesInformation());

		// double times = info.GetTimeSpan();
		// if (times[0] < this.TimeSpan[0]) {
		// this.TimeSpan[0] = times[0];
		// }
		// if (times[1] > this.TimeSpan[1]) {
		// this.TimeSpan[1] = times[1];
		// }
		//
		// if (!this.HasTime && info.GetHasTime()) {
		// this.Time = info.GetTime();
		// this.HasTime = 1;
		// }
		//
		// this.SetTimeLabel(info.GetTimeLabel());
	}

	String GetPrettyDataTypeString() {
		int dataType = this.DataSetType;
		if (this.CompositeDataSetType >= 0) {
			dataType = this.CompositeDataSetType;
		}

		switch (dataType) {
		case VTKConstants.VTK_POLY_DATA:
			return "Polygonal Mesh";
		case VTKConstants.VTK_STRUCTURED_POINTS:
			return "Image (Uniform Rectilinear Grid)";
		case VTKConstants.VTK_STRUCTURED_GRID:
			return "Structured (Curvilinear) Grid";
		case VTKConstants.VTK_RECTILINEAR_GRID:
			return "Rectilinear Grid";
		case VTKConstants.VTK_UNSTRUCTURED_GRID:
			return "Unstructured Grid";
		case VTKConstants.VTK_PIECEWISE_FUNCTION:
			return "Piecewise function";
		case VTKConstants.VTK_IMAGE_DATA:
			return "Image (Uniform Rectilinear Grid)";
		case VTKConstants.VTK_DATA_OBJECT:
			return "Data Object";
		case VTKConstants.VTK_DATA_SET:
			return "Data Set";
		case VTKConstants.VTK_POINT_SET:
			return "Point Set";
		case VTKConstants.VTK_UNIFORM_GRID:
			return "Image (Uniform Rectilinear Grid) with blanking";
		case VTKConstants.VTK_COMPOSITE_DATA_SET:
			return "Composite Dataset";
		case VTKConstants.VTK_MULTIGROUP_DATA_SET:
			return "Multi-group Dataset";
		case VTKConstants.VTK_MULTIBLOCK_DATA_SET:
			return "Multi-block Dataset";
		case VTKConstants.VTK_HIERARCHICAL_DATA_SET:
			return "Hierarchical DataSet (Deprecated)";
		case VTKConstants.VTK_HIERARCHICAL_BOX_DATA_SET:
			return "AMR Dataset (Deprecated)";
		case VTKConstants.VTK_NON_OVERLAPPING_AMR:
			return "Non-Overlapping AMR Dataset";
		case VTKConstants.VTK_OVERLAPPING_AMR:
			return "Overlapping AMR Dataset";
		case VTKConstants.VTK_GENERIC_DATA_SET:
			return "Generic Dataset";
		case VTKConstants.VTK_HYPER_OCTREE:
			return "Hyper-octree";
		case VTKConstants.VTK_HYPER_TREE_GRID:
			return "Hyper-tree Grid";
		case VTKConstants.VTK_TEMPORAL_DATA_SET:
			return "Temporal Dataset";
		case VTKConstants.VTK_TABLE:
			return "Table";
		case VTKConstants.VTK_GRAPH:
			return "Graph";
		case VTKConstants.VTK_TREE:
			return "Tree";
		case VTKConstants.VTK_SELECTION:
			return "Selection";
		case VTKConstants.VTK_DIRECTED_GRAPH:
			return "Directed Graph";
		case VTKConstants.VTK_UNDIRECTED_GRAPH:
			return "Undirected Graph";
		case VTKConstants.VTK_MULTIPIECE_DATA_SET:
			return "Multi-piece Dataset";
		case VTKConstants.VTK_DIRECTED_ACYCLIC_GRAPH:
			return "Directed Acyclic Graph";
		default:
			// vtkPVDataInformationHelper *dhelper =
			// vtkPVDataInformation::FindHelper
			// (this.DataClassName);
			// if (dhelper)
			// {
			// const char *namestr = dhelper.GetPrettyDataTypeString();
			// dhelper.Delete();
			// return namestr;
			// }
		}

		return "UnknownType";
	}

	int IsDataStructured() {
		switch (this.DataSetType) {
		case VTKConstants.VTK_IMAGE_DATA:
		case VTKConstants.VTK_STRUCTURED_GRID:
		case VTKConstants.VTK_RECTILINEAR_GRID:
		case VTKConstants.VTK_UNIFORM_GRID:
		case VTKConstants.VTK_GENERIC_DATA_SET:
			return 1;
		}
		return 0;
	}

	public void PrintSelf(PrintStream os) {

		os.println("PortNumber:           " + this.PortNumber);
		os.println("DataSetType:          " + this.DataSetType);
		os.println("CompositeDataSetType: " + this.CompositeDataSetType);
		os.println("NumberOfPoints:       " + this.NumberOfPoints);
		os.println("NumberOfRows:         " + this.NumberOfRows);
		os.println("NumberOfCells: 	   " + this.NumberOfCells);
		os.println("NumberOfDataSets:     " + this.NumberOfDataSets);
		os.println("MemorySize:           " + this.MemorySize);
		os.println("PolygonCount:         " + this.PolygonCount);
		os.println("Bounds:               " + this.Bounds[0] + ", " + this.Bounds[1] + ", " + this.Bounds[2] + ", " + this.Bounds[3] + ", " + this.Bounds[4] + ", " + this.Bounds[5]);
		os.println("Extent:               " + this.Extent[0] + ", " + this.Extent[1] + ", " + this.Extent[2] + ", " + this.Extent[3] + ", " + this.Extent[4] + ", " + this.Extent[5]);

		String indent = "    ";
		os.println("PointDataInformation ");
		this.PointDataInformation.PrintSelf(os, indent);
		os.println("CellDataInformation ");
		this.CellDataInformation.PrintSelf(os, indent);
		os.println("VertexDataInformation");
		this.VertexDataInformation.PrintSelf(os, indent);
		os.println("EdgeDataInformation");
		this.EdgeDataInformation.PrintSelf(os, indent);
		os.println("RowDataInformation");
		this.RowDataInformation.PrintSelf(os, indent);
		os.println("FieldDataInformation ");
		this.FieldDataInformation.PrintSelf(os, indent);
		os.println("CompositeDataInformation ");
		this.CompositeDataInformation.PrintSelf(os, indent);
		os.println("PointArrayInformation ");
		this.PointArrayInformation.PrintSelf(os, indent);

		os.println("DataClassName: " + (this.DataClassName != null ? this.DataClassName : "(none)"));
		os.println("CompositeDataClassName: " + (this.CompositeDataClassName != null ? this.CompositeDataClassName : "(none)"));

		// os.println("TimeSpan: " + this.TimeSpan[0] + ", " + this.TimeSpan[1]
		// );

		if (this.TimeLabel != null) {
			os.println("TimeLabel: " + this.TimeLabel);
		}
	}

	public int getCompositeDataSetType() {
		return CompositeDataSetType;
	}

	public int getDataSetType() {
		return DataSetType;
	}

	public int getNumberOfPoints() {
		return NumberOfPoints;
	}

	public int getNumberOfCells() {
		return NumberOfCells;
	}

	public int getNumberOfRows() {
		return NumberOfRows;
	}

	public int getMemorySize() {
		return MemorySize;
	}

	public int getPolygonCount() {
		return PolygonCount;
	}

	public double[] getBounds() {
		return Bounds;
	}

	public int[] getExtent() {
		return Extent;
	}

	public String getDataClassName() {
		return DataClassName;
	}

	public String getCompositeDataClassName() {
		return CompositeDataClassName;
	}

	public int getNumberOfDataSets() {
		return NumberOfDataSets;
	}

	// String GetDataSetTypeAsString()
	// {
	// if(this.DataSetType == -1)
	// {
	// return "UnknownType";
	// }
	// else
	// {
	// return vtkDataObjectTypes::GetClassNameFromTypeId(this.DataSetType);
	// }
	// }

	// Need to do this manually.
	// int DataSetTypeIsA(String type) {
	// if (strcmp(type, "vtkDataObject") == 0) { // Every type is of type
	// vtkDataObject.
	// return 1;
	// }
	// if (strcmp(type, "vtkDataSet") == 0) { // Every type is of type
	// vtkDataObject.
	// if (this.DataSetType == VTK_POLY_DATA || this.DataSetType ==
	// VTK_STRUCTURED_GRID || this.DataSetType == VTK_UNSTRUCTURED_GRID ||
	// this.DataSetType == VTK_IMAGE_DATA || this.DataSetType ==
	// VTK_RECTILINEAR_GRID || this.DataSetType == VTK_UNSTRUCTURED_GRID ||
	// this.DataSetType == VTK_HYPER_TREE_GRID || this.DataSetType ==
	// VTK_STRUCTURED_POINTS) {
	// return 1;
	// }
	// }
	// if (strcmp(type, this.GetDataSetTypeAsString()) == 0) { // If class names
	// are the same, then they are of the same type.
	// return 1;
	// }
	// if (strcmp(type, "vtkPointSet") == 0) {
	// if (this.DataSetType == VTK_POLY_DATA || this.DataSetType ==
	// VTK_STRUCTURED_GRID || this.DataSetType == VTK_UNSTRUCTURED_GRID) {
	// return 1;
	// }
	// }
	// if (strcmp(type, "vtkStructuredData") == 0) {
	// if (this.DataSetType == VTK_IMAGE_DATA || this.DataSetType ==
	// VTK_STRUCTURED_GRID || this.DataSetType == VTK_RECTILINEAR_GRID) {
	// return 1;
	// }
	// }
	//
	// return 0;
	// }

	// VTKDataInformation GetDataInformationForCompositeIndex(int index)
	// {
	// if (index == 0)
	// {
	// (*index)--;
	// return this;
	// }
	//
	// (*index)--;
	// return
	// this.CompositeDataInformation.GetDataInformationForCompositeIndex(index);
	// }

	// void CopyToStream(vtkClientServerStream* css)
	// {
	// css.Reset();
	// *css << vtkClientServerStream::Reply;
	// *css << this.DataClassName
	// << this.DataSetType
	// << this.NumberOfDataSets
	// << this.NumberOfPoints
	// << this.NumberOfCells
	// << this.NumberOfRows
	// << this.MemorySize
	// << this.PolygonCount
	// << this.Time
	// << this.HasTime
	// << this.TimeLabel
	// << vtkClientServerStream::InsertArray(this.Bounds, 6)
	// << vtkClientServerStream::InsertArray(this.Extent, 6);
	//
	// size_t length;
	// const unsigned char* data;
	// vtkClientServerStream dcss;
	//
	// this.PointArrayInformation.CopyToStream(&dcss);
	// dcss.GetData(&data, &length);
	// *css << vtkClientServerStream::InsertArray(data,
	// static_cast<int>(length));
	//
	// dcss.Reset();
	//
	// this.PointDataInformation.CopyToStream(&dcss);
	// dcss.GetData(&data, &length);
	// *css << vtkClientServerStream::InsertArray(data,
	// static_cast<int>(length));
	//
	// dcss.Reset();
	//
	// this.CellDataInformation.CopyToStream(&dcss);
	// dcss.GetData(&data, &length);
	// *css << vtkClientServerStream::InsertArray(data,
	// static_cast<int>(length));
	//
	// dcss.Reset();
	//
	// this.VertexDataInformation.CopyToStream(&dcss);
	// dcss.GetData(&data, &length);
	// *css << vtkClientServerStream::InsertArray(data,
	// static_cast<int>(length));
	//
	// dcss.Reset();
	//
	// this.EdgeDataInformation.CopyToStream(&dcss);
	// dcss.GetData(&data, &length);
	// *css << vtkClientServerStream::InsertArray(data,
	// static_cast<int>(length));
	//
	// dcss.Reset();
	//
	// this.RowDataInformation.CopyToStream(&dcss);
	// dcss.GetData(&data, &length);
	// *css << vtkClientServerStream::InsertArray(data,
	// static_cast<int>(length));
	//
	// *css << this.CompositeDataClassName;
	// *css << this.CompositeDataSetType;
	//
	// dcss.Reset();
	//
	// this.CompositeDataInformation.CopyToStream(&dcss);
	// dcss.GetData(&data, &length);
	// *css << vtkClientServerStream::InsertArray(data,
	// static_cast<int>(length));
	//
	// dcss.Reset();
	//
	// this.FieldDataInformation.CopyToStream(&dcss);
	// dcss.GetData(&data, &length);
	// *css << vtkClientServerStream::InsertArray(data,
	// static_cast<int>(length));
	//
	// *css << vtkClientServerStream::InsertArray(this.TimeSpan, 2);
	//
	// *css << vtkClientServerStream::End;
	// }

	// void CopyFromStream(const vtkClientServerStream* css)
	// {
	// CSS_ARGUMENT_BEGIN();
	//
	// const char* dataclassname = 0;
	// if (!CSS_GET_NEXT_ARGUMENT(css, 0, &dataclassname))
	// {
	// vtkErrorMacro("Error parsing class name of data.");
	// return;
	// }
	// this.SetDataClassName(dataclassname);
	//
	// if (!CSS_GET_NEXT_ARGUMENT(css, 0, &this.DataSetType))
	// {
	// vtkErrorMacro("Error parsing data set type.");
	// return;
	// }
	// if (!CSS_GET_NEXT_ARGUMENT(css, 0, &this.NumberOfDataSets))
	// {
	// vtkErrorMacro("Error parsing number of datasets.");
	// return;
	// }
	// if (!CSS_GET_NEXT_ARGUMENT(css, 0, &this.NumberOfPoints))
	// {
	// vtkErrorMacro("Error parsing number of points.");
	// return;
	// }
	// if (!CSS_GET_NEXT_ARGUMENT(css, 0, &this.NumberOfCells))
	// {
	// vtkErrorMacro("Error parsing number of cells.");
	// return;
	// }
	// if (!CSS_GET_NEXT_ARGUMENT(css, 0, &this.NumberOfRows))
	// {
	// vtkErrorMacro("Error parsing number of cells.");
	// return;
	// }
	// if(!CSS_GET_NEXT_ARGUMENT(css, 0, &this.MemorySize))
	// {
	// vtkErrorMacro("Error parsing memory size.");
	// return;
	// }
	// if(!CSS_GET_NEXT_ARGUMENT(css, 0, &this.PolygonCount))
	// {
	// vtkErrorMacro("Error parsing memory size.");
	// return;
	// }
	// if(!CSS_GET_NEXT_ARGUMENT(css, 0, &this.Time))
	// {
	// vtkErrorMacro("Error parsing Time.");
	// return;
	// }
	// if(!CSS_GET_NEXT_ARGUMENT(css, 0, &this.HasTime))
	// {
	// vtkErrorMacro("Error parsing has-time.");
	// return;
	// }
	// const char* timeLabel = 0;
	// if (!CSS_GET_NEXT_ARGUMENT(css, 0, &timeLabel))
	// {
	// vtkErrorMacro("Error parsing time label.");
	// return;
	// }
	// this.SetTimeLabel(timeLabel);
	// if(!CSS_GET_NEXT_ARGUMENT2(css, 0, this.Bounds, 6))
	// {
	// vtkErrorMacro("Error parsing bounds.");
	// return;
	// }
	// if(!CSS_GET_NEXT_ARGUMENT2(css, 0, this.Extent, 6))
	// {
	// vtkErrorMacro("Error parsing extent.");
	// return;
	// }
	//
	// vtkTypeUInt32 length;
	// std::vector<unsigned char> data;
	// vtkClientServerStream dcss;
	//
	// // Point array information.
	// if(!css.GetArgumentLength(0, CSS_GET_CUR_INDEX(), &length))
	// {
	// vtkErrorMacro("Error parsing length of point data information.");
	// return;
	// }
	// data.resize(length);
	// if(!css.GetArgument(0, CSS_GET_CUR_INDEX(), &*data.begin(), length))
	// {
	// vtkErrorMacro("Error parsing point data information.");
	// return;
	// }
	// dcss.SetData(&*data.begin(), length);
	// this.PointArrayInformation.CopyFromStream(&dcss);
	// CSS_GET_CUR_INDEX()++;
	//
	// // Point data array information.
	// if(!css.GetArgumentLength(0, CSS_GET_CUR_INDEX(), &length))
	// {
	// vtkErrorMacro("Error parsing length of point data information.");
	// return;
	// }
	// data.resize(length);
	// if(!css.GetArgument(0, CSS_GET_CUR_INDEX(), &*data.begin(), length))
	// {
	// vtkErrorMacro("Error parsing point data information.");
	// return;
	// }
	// dcss.SetData(&*data.begin(), length);
	// this.PointDataInformation.CopyFromStream(&dcss);
	// CSS_GET_CUR_INDEX()++;
	//
	// // Cell data array information.
	// if(!css.GetArgumentLength(0, CSS_GET_CUR_INDEX(), &length))
	// {
	// vtkErrorMacro("Error parsing length of cell data information.");
	// return;
	// }
	// data.resize(length);
	// if(!css.GetArgument(0, CSS_GET_CUR_INDEX(), &*data.begin(), length))
	// {
	// vtkErrorMacro("Error parsing cell data information.");
	// return;
	// }
	// dcss.SetData(&*data.begin(), length);
	// this.CellDataInformation.CopyFromStream(&dcss);
	// CSS_GET_CUR_INDEX()++;
	//
	// // Vertex data array information.
	// if(!css.GetArgumentLength(0, CSS_GET_CUR_INDEX(), &length))
	// {
	// vtkErrorMacro("Error parsing length of cell data information.");
	// return;
	// }
	// data.resize(length);
	// if(!css.GetArgument(0, CSS_GET_CUR_INDEX(), &*data.begin(), length))
	// {
	// vtkErrorMacro("Error parsing cell data information.");
	// return;
	// }
	// dcss.SetData(&*data.begin(), length);
	// this.VertexDataInformation.CopyFromStream(&dcss);
	// CSS_GET_CUR_INDEX()++;
	//
	// // Edge data array information.
	// if(!css.GetArgumentLength(0, CSS_GET_CUR_INDEX(), &length))
	// {
	// vtkErrorMacro("Error parsing length of cell data information.");
	// return;
	// }
	// data.resize(length);
	// if(!css.GetArgument(0, CSS_GET_CUR_INDEX(), &*data.begin(), length))
	// {
	// vtkErrorMacro("Error parsing cell data information.");
	// return;
	// }
	// dcss.SetData(&*data.begin(), length);
	// this.EdgeDataInformation.CopyFromStream(&dcss);
	// CSS_GET_CUR_INDEX()++;
	//
	// // Row data array information.
	// if(!css.GetArgumentLength(0, CSS_GET_CUR_INDEX(), &length))
	// {
	// vtkErrorMacro("Error parsing length of cell data information.");
	// return;
	// }
	// data.resize(length);
	// if(!css.GetArgument(0, CSS_GET_CUR_INDEX(), &*data.begin(), length))
	// {
	// vtkErrorMacro("Error parsing cell data information.");
	// return;
	// }
	// dcss.SetData(&*data.begin(), length);
	// this.RowDataInformation.CopyFromStream(&dcss);
	// CSS_GET_CUR_INDEX()++;
	//
	// const char* compositedataclassname = 0;
	// if(!CSS_GET_NEXT_ARGUMENT(css, 0, &compositedataclassname))
	// {
	// vtkErrorMacro("Error parsing class name of data.");
	// return;
	// }
	// this.SetCompositeDataClassName(compositedataclassname);
	//
	// if(!CSS_GET_NEXT_ARGUMENT(css, 0, &this.CompositeDataSetType))
	// {
	// vtkErrorMacro("Error parsing data set type.");
	// return;
	// }
	//
	// // Composite data information.
	// if(!css.GetArgumentLength(0, CSS_GET_CUR_INDEX(), &length))
	// {
	// vtkErrorMacro("Error parsing length of cell data information.");
	// return;
	// }
	// data.resize(length);
	// if(!css.GetArgument(0, CSS_GET_CUR_INDEX(), &*data.begin(), length))
	// {
	// vtkErrorMacro("Error parsing cell data information.");
	// return;
	// }
	// dcss.SetData(&*data.begin(), length);
	// if (dcss.GetNumberOfMessages() > 0)
	// {
	// this.CompositeDataInformation.CopyFromStream(&dcss);
	// }
	// else
	// {
	// this.CompositeDataInformation.Initialize();
	// }
	// CSS_GET_CUR_INDEX()++;
	//
	// // Field data array information.
	// if(!css.GetArgumentLength(0, CSS_GET_CUR_INDEX(), &length))
	// {
	// vtkErrorMacro("Error parsing length of field data information.");
	// return;
	// }
	//
	// data.resize(length);
	// if(!css.GetArgument(0, CSS_GET_CUR_INDEX(), &*data.begin(), length))
	// {
	// vtkErrorMacro("Error parsing field data information.");
	// return;
	// }
	// dcss.SetData(&*data.begin(), length);
	// this.FieldDataInformation.CopyFromStream(&dcss);
	// CSS_GET_CUR_INDEX()++;
	//
	// if(!CSS_GET_NEXT_ARGUMENT2(css, 0, this.TimeSpan, 2))
	// {
	// vtkErrorMacro("Error parsing timespan.");
	// return;
	// }
	//
	// CSS_ARGUMENT_END();
	// }

	// void SetSortArrays(boolean sort) {
	// this.PointDataInformation.SetSortArrays(sort);
	// this.CellDataInformation.SetSortArrays(sort);
	// this.FieldDataInformation.SetSortArrays(sort);
	// }

	// vtkPVDataSetAttributesInformation GetAttributeInformation(int
	// fieldAssociation)
	// {
	// switch (fieldAssociation)
	// {
	// case vtkDataObject::FIELD_ASSOCIATION_POINTS:
	// return this.PointDataInformation;
	//
	// case vtkDataObject::FIELD_ASSOCIATION_CELLS:
	// return this.CellDataInformation;
	//
	// case vtkDataObject::FIELD_ASSOCIATION_VERTICES:
	// return this.VertexDataInformation;
	//
	// case vtkDataObject::FIELD_ASSOCIATION_EDGES:
	// return this.EdgeDataInformation;
	//
	// case vtkDataObject::FIELD_ASSOCIATION_ROWS:
	// return this.RowDataInformation;
	//
	// case vtkDataObject::FIELD_ASSOCIATION_NONE:
	// return this.FieldDataInformation;
	// }
	//
	// return 0;
	// }

	// DO NOT USE THIS METHOD, THE ITERATOR HAS MEMORY LEAK PROBLEMS
	// public void AddFromCompositeDataSet(vtkCompositeDataSet data) {
	// vtkCompositeDataIterator iter = data.NewIterator();
	// int counter = 0;
	// for (iter.InitTraversal(); iter.IsDoneWithTraversal() == 0;
	// iter.GoToNextItem()) {
	// System.out.println("VTKDataInformation.AddFromCompositeDataSet() " +
	// counter++);
	// // vtkDataObject dobj = iter.GetCurrentDataObject();
	// // if (dobj != null) {
	// // VTKDataInformation dinf = new VTKDataInformation();
	// // dinf.CopyFromObject(dobj);
	// // dinf.DataClassName = dobj.GetClassName();
	// // dinf.DataSetType = dobj.GetDataObjectType();
	// // this.AddInformation(dinf, true);
	// // }
	// // dobj.Delete();
	// }
	// iter.Delete();
	// }
	// void CopyFromSelection(vtkSelection data) {
	// this.SetDataClassName(data.GetClassName());
	// this.DataSetType = data.GetDataObjectType();
	// this.NumberOfDataSets = 1;
	//
	// this.Bounds[0] = this.Bounds[2] = this.Bounds[4] = VTK_DOUBLE_MAX;
	// this.Bounds[1] = this.Bounds[3] = this.Bounds[5] = -VTK_DOUBLE_MAX;
	//
	// this.MemorySize = data.GetActualMemorySize();
	// this.NumberOfCells = 0;
	// this.NumberOfPoints = 0;
	//
	// // Copy Point Data information
	// this.PointDataInformation.CopyFromFieldData(data.GetFieldData());
	// }

	// void CopyFromGraph(vtkGraph data) {
	// this.SetDataClassName(data.GetClassName());
	// this.DataSetType = data.GetDataObjectType();
	// this.NumberOfDataSets = 1;
	//
	// this.Bounds[0] = this.Bounds[2] = this.Bounds[4] = VTK_DOUBLE_MAX;
	// this.Bounds[1] = this.Bounds[3] = this.Bounds[5] = -VTK_DOUBLE_MAX;
	//
	// if (data.GetPoints())
	// data.GetPoints().GetBounds(this.Bounds);
	//
	// this.MemorySize = data.GetActualMemorySize();
	// this.NumberOfCells = data.GetNumberOfEdges();
	// this.NumberOfPoints = data.GetNumberOfVertices();
	// this.NumberOfRows = 0;
	//
	// this.VertexDataInformation.CopyFromFieldData(data.GetVertexData());
	// this.EdgeDataInformation.CopyFromFieldData(data.GetEdgeData());
	// }

	// void CopyFromTable(vtkTable data) {
	// this.SetDataClassName(data.GetClassName());
	// this.DataSetType = data.GetDataObjectType();
	// this.NumberOfDataSets = 1;
	//
	// this.Bounds[0] = this.Bounds[2] = this.Bounds[4] = VTK_DOUBLE_MAX;
	// this.Bounds[1] = this.Bounds[3] = this.Bounds[5] = -VTK_DOUBLE_MAX;
	//
	// this.MemorySize = data.GetActualMemorySize();
	// this.NumberOfCells = data.GetNumberOfRows() * data.GetNumberOfColumns();
	// this.NumberOfPoints = 0;
	// this.NumberOfRows = data.GetNumberOfRows();
	//
	// this.RowDataInformation.CopyFromFieldData(data.GetRowData());
	// }

}
