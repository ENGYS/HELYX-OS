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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vtk.vtkAbstractArray;
import vtk.vtkDataSetAttributes;
import vtk.vtkFieldData;
import vtk.vtkGenericAttribute;
import vtk.vtkGenericAttributeCollection;

public class VTKDataSetAttributesInformation {

	private static final int NUM_ATTRIBUTES = 8;
	private List<VTKArrayInformation> ArrayInformation;
	private boolean SortArrays;
	private int[] AttributeIndices = new int[NUM_ATTRIBUTES];

	// ----------------------------------------------------------------------------
	public VTKDataSetAttributesInformation() {
		this.ArrayInformation = new ArrayList<>();
		for (int idx = 0; idx < NUM_ATTRIBUTES; ++idx) {
			this.AttributeIndices[idx] = -1;
		}
		this.SortArrays = true;
	}

	// ----------------------------------------------------------------------------
	void PrintSelf(PrintStream os, String indent) {
		
		int num = this.GetNumberOfArrays();
		os.println(indent+"ArrayInformation, number of arrays: " + num);
		for (int idx = 0; idx < num; ++idx) {
			this.ArrayInformation.get(idx).PrintSelf(os, indent+indent);
		}
		os.println(indent+"SortArrays: " + this.SortArrays);
	}

	// ----------------------------------------------------------------------------
	void Initialize() {
		this.ArrayInformation.clear();
		for (int idx = 0; idx < NUM_ATTRIBUTES; ++idx) {
			this.AttributeIndices[idx] = -1;
		}
	}

	// ----------------------------------------------------------------------------
	void DeepCopy(VTKDataSetAttributesInformation dataInfo) {

		// Copy array information.
		this.ArrayInformation.clear();
		int num = dataInfo.GetNumberOfArrays();
		for (int idx = 0; idx < num; ++idx) {
			VTKArrayInformation arrayInfo = dataInfo.ArrayInformation.get(idx);
			VTKArrayInformation newArrayInfo = new VTKArrayInformation();
			newArrayInfo.DeepCopy(arrayInfo);
			this.ArrayInformation.add(newArrayInfo);
		}
		// Now the default attributes.
		for (int idx = 0; idx < NUM_ATTRIBUTES; ++idx) {
			this.AttributeIndices[idx] = dataInfo.AttributeIndices[idx];
		}
	}

	// ----------------------------------------------------------------------------
	void CopyFromFieldData(vtkFieldData da) {
		// Clear array information.
		this.ArrayInformation.clear();
		for (int idx = 0; idx < NUM_ATTRIBUTES; ++idx) {
			this.AttributeIndices[idx] = -1;
		}

		// Copy Field Data
		int num = da.GetNumberOfArrays();
		for (int idx = 0; idx < num; ++idx) {
			vtkAbstractArray array = da.GetAbstractArray(idx);
			if (array.GetName() != null) {
				VTKArrayInformation info = new VTKArrayInformation();
				info.CopyFromObject(array);
				this.ArrayInformation.add(info);
			}
		}
	}

	class SortedArray implements Comparable<SortedArray> {

		public int arrayIndx;
		public String arrayName;

		@Override
		public int compareTo(SortedArray o) {
			return arrayName.compareTo(o.arrayName);
		}

	}

	// ----------------------------------------------------------------------------
	void CopyFromDataSetAttributes(vtkDataSetAttributes da) {
		// Clear array information.
		this.ArrayInformation.clear();
		for (int idx = 0; idx < NUM_ATTRIBUTES; ++idx) {
			this.AttributeIndices[idx] = -1;
		}

		// Copy Point Data
		int num = da.GetNumberOfArrays();

		// sort the arrays alphabetically
		List<SortedArray> sortArrays = new ArrayList<>();
		sortArrays.clear();

		if (num > 0) {
			for (int i = 0; i < num; i++) {
				SortedArray sa = new SortedArray();
				sa.arrayIndx = i;
				sa.arrayName = da.GetArrayName(i) != null ? da.GetArrayName(i) : "";

				sortArrays.add(i, sa);
			}

			if (this.SortArrays) {
				Collections.sort(sortArrays);
			}
		}

		int infoArrayIndex = 0;
		for (SortedArray sa : sortArrays) {
			int arrayIndx = sa.arrayIndx;
			vtkAbstractArray array = da.GetAbstractArray(arrayIndx);

			if (array.GetName() != null && !array.GetName().equals("vtkGhostLevels") && !array.GetName().equals("vtkOriginalCellIds") && !array.GetName().equals("vtkOriginalPointIds")) {
				int attribute = da.IsArrayAnAttribute(arrayIndx);
				VTKArrayInformation info = new VTKArrayInformation();
				info.CopyFromObject(array);
				this.ArrayInformation.add(info);
				// Record default attributes.
				if (attribute > -1) {
					this.AttributeIndices[attribute] = infoArrayIndex;
				}
				++infoArrayIndex;
			}
		}

		sortArrays.clear();
	}

	private static final int vtkPointCentered = 0;
	private static final int vtkCellCentered = 1;
	private static final int vtkBoundaryCentered = 2;

	// ----------------------------------------------------------------------------
	void CopyFromGenericAttributesOnPoints(vtkGenericAttributeCollection da) {

		// Clear array information.
		this.ArrayInformation.clear();
		for (int idx = 0; idx < 5; ++idx) {
			this.AttributeIndices[idx] = -1;
		}

		// Copy Point Data
		int num = da.GetNumberOfAttributes();
		for (int idx = 0; idx < num; ++idx) {
			vtkGenericAttribute array = da.GetAttribute(idx);
			if (array.GetCentering() == vtkPointCentered) {
				if (array.GetName() != null && (!array.GetName().equals("vtkGhostLevels"))) {
					VTKGenericAttributeInformation info = new VTKGenericAttributeInformation();
					info.CopyFromObject(array);
					this.ArrayInformation.add(info);
				}
			}
		}
	}

	// ----------------------------------------------------------------------------
	void CopyFromGenericAttributesOnCells(vtkGenericAttributeCollection da) {

		// Clear array information.
		this.ArrayInformation.clear();
		for (int idx = 0; idx < 5; ++idx) {
			this.AttributeIndices[idx] = -1;
		}

		// Copy Cell Data
		int num = da.GetNumberOfAttributes();
		for (int idx = 0; idx < num; ++idx) {
			vtkGenericAttribute array = da.GetAttribute(idx);
			if (array.GetCentering() == vtkCellCentered) {
				if (array.GetName() != null && (!array.GetName().equals("vtkGhostLevels"))) {
					VTKGenericAttributeInformation info = new VTKGenericAttributeInformation();
					info.CopyFromObject(array);
					this.ArrayInformation.add(info);
				}
			}
		}
	}

	// ----------------------------------------------------------------------------
	void AddInformation(VTKDataSetAttributesInformation info) {
		int num1 = this.GetNumberOfArrays();
		int num2 = info.GetNumberOfArrays();
		int[] newAttributeIndices = new int[NUM_ATTRIBUTES];

		for (int idx1 = 0; idx1 < NUM_ATTRIBUTES; idx1++) {
			newAttributeIndices[idx1] = -1;
		}

		// First add ranges from all common arrays
		for (int idx1 = 0; idx1 < num1; idx1++) {
			boolean found = false;
			VTKArrayInformation ai1 = this.ArrayInformation.get(idx1);
			for (int idx2 = 0; idx2 < num2; idx2++) {
				VTKArrayInformation ai2 = info.ArrayInformation.get(idx2);
				if (ai1.Compare(ai2)) {
					// Take union of range.
					ai1.AddRanges(ai2);
					found = true;
					// Record default attributes.
					int attribute1 = this.IsArrayAnAttribute(idx1);
					int attribute2 = info.IsArrayAnAttribute(idx2);
					if (attribute1 > -1 && attribute1 == attribute2) {
						newAttributeIndices[attribute1] = idx1;
					}
					break;
				}
			}
			if (!found) {
				ai1.IsPartial = true;
			}
		}

		for (int idx1 = 0; idx1 < NUM_ATTRIBUTES; idx1++) {
			this.AttributeIndices[idx1] = newAttributeIndices[idx1];
		}

		// Now add arrays that don't exist
		for (int idx2 = 0; idx2 < num2; idx2++) {
			VTKArrayInformation ai2 = info.ArrayInformation.get(idx2);
			boolean found = false;
			for (int idx1 = 0; idx1 < this.GetNumberOfArrays(); idx1++) {
				VTKArrayInformation ai1 = this.ArrayInformation.get(idx1);
				if (ai1.Compare(ai2)) {
					found = true;
					break;
				}
			}
			if (!found) {
				ai2.IsPartial = true;
				this.ArrayInformation.add(ai2);
				int attribute = info.IsArrayAnAttribute(idx2);
				if (attribute > -1 && this.AttributeIndices[attribute] == -1) {
					this.AttributeIndices[attribute] = idx2;
				}
			}
		}
	}

	// ----------------------------------------------------------------------------
	// void AddInformation(vtkPVInformation info)
	// {
	// vtkPVDataSetAttributesInformation* p =
	// vtkPVDataSetAttributesInformation::SafeDownCast(info);
	// if(p)
	// {
	// this.AddInformation(p);
	// }
	// else
	// {
	// vtkErrorMacro("AddInformation called with object of type "
	// << (info? info.GetClassName():"<unknown>"));
	// }
	// }

	// ----------------------------------------------------------------------------
	void AddInformation(vtkDataSetAttributes da) {
		VTKDataSetAttributesInformation info = new VTKDataSetAttributesInformation();
		info.CopyFromDataSetAttributes(da);
		this.AddInformation(info);
	}

	// ----------------------------------------------------------------------------
	int IsArrayAnAttribute(int arrayIndex) {
		int i;

		for (i = 0; i < NUM_ATTRIBUTES; ++i) {
			if (this.AttributeIndices[i] == arrayIndex) {
				return i;
			}
		}
		return -1;
	}

	// ----------------------------------------------------------------------------
	VTKArrayInformation GetAttributeInformation(int attributeType) {
		int arrayIdx = this.AttributeIndices[attributeType];

		if (arrayIdx < 0) {
			return null;
		}
		return this.ArrayInformation.get(arrayIdx);
	}

	// ----------------------------------------------------------------------------
	int GetNumberOfArrays() {
		return this.ArrayInformation.size();
	}

	// ----------------------------------------------------------------------------
	// int GetMaximumNumberOfTuples()
	// {
	// VTKArrayInformation info;
	// int maxNumVals = 0;
	//
	// this.ArrayInformation.InitTraversal();
	// while ( (info = static_cast<vtkPVArrayInformation*>(this.ArrayInformation.GetNextItemAsObject())) )
	// {
	// maxNumVals = info.GetNumberOfTuples() > maxNumVals ? info.GetNumberOfTuples() : maxNumVals;
	// }
	//
	// return maxNumVals;
	// }

	// ----------------------------------------------------------------------------
	// vtkPVArrayInformation GetArrayInformation(String name)
	// {
	// vtkPVArrayInformation info;
	//
	// if (name == NULL)
	// {
	// return NULL;
	// }
	//
	// this.ArrayInformation.InitTraversal();
	// while ( (info = static_cast<vtkPVArrayInformation*>(this.ArrayInformation.GetNextItemAsObject())) )
	// {
	// if (strcmp(info.GetName(), name) == 0)
	// {
	// return info;
	// }
	// }
	// return NULL;
	// }

}
