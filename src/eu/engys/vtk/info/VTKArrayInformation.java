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
import java.util.List;

import vtk.vtkAbstractArray;
import vtk.vtkDataArray;
import vtk.vtkObject;

public class VTKArrayInformation {
	String Name;
	double[][] Ranges;
	List<String> ComponentNames;
	String DefaultComponentName;
	List<?> InformationKeys;
	int DataType;
	int NumberOfComponents;
	int NumberOfTuples;
	boolean IsPartial;
	private Object NumberOfInformationKeys;
	  
//	namespace
//	{
//	  typedef std::vector<vtkStdString*> vtkInternalComponentNameBase;
//
//	  struct vtkPVArrayInformationInformationKey
//	  {
//	    vtkStdString Location;
//	    vtkStdString Name;
//	  };
//
//	  typedef std::vector<vtkPVArrayInformationInformationKey> vtkInternalInformationKeysBase;
//	}
//
//	class vtkPVArrayInformation::vtkInternalComponentNames:
//	    public vtkInternalComponentNameBase
//	{
//	};
//
//	class vtkPVArrayInformation::vtkInternalInformationKeys:
//	    public vtkInternalInformationKeysBase
//	{
//	};

	//----------------------------------------------------------------------------
	public VTKArrayInformation()
	{
	  this.Initialize();
	}

	//----------------------------------------------------------------------------
	void Initialize()
	{
		this.Name = null;
		this.DataType = VTKConstants.VTK_VOID;
		this.NumberOfComponents = 0;
		this.NumberOfTuples = 0;

		this.ComponentNames = new ArrayList<>();
		this.DefaultComponentName = null;

		this.Ranges = null;
		this.IsPartial = false;

		this.InformationKeys = new ArrayList<>();
	}

	//----------------------------------------------------------------------------
	void PrintSelf(PrintStream os, String indent)
	{
		if (this.Name != null)
		{
			os.println(indent+"Name: " + this.Name);
		}
		os.println(indent+"DataType: " + this.DataType);
		os.println(indent+"NumberOfComponents: " + this.NumberOfComponents);
		if (this.ComponentNames != null)
		{
			os.println(indent+"ComponentNames:");
			for (int i = 0; i < this.ComponentNames.size(); ++i)
			{
				os.println(indent+indent+this.ComponentNames.get(i));
			}
		}
		os.println(indent+"NumberOfTuples: " + this.NumberOfTuples);
		os.println(indent+"IsPartial: " + this.IsPartial);

		os.println(indent+"Ranges :");
		int num = this.NumberOfComponents;
		if (num > 1)
		{
			++num;
		}
		for (int idx = 0; idx < num; ++idx)
		{
			os.println(indent+indent+this.Ranges[idx][0] + ", " + this.Ranges[idx][1]);
		}

		os.println(indent+"InformationKeys :");
		if(this.InformationKeys != null)
		{
//			num = this.NumberOfInformationKeys;
//			for (idx = 0; idx < num; ++idx)
//			{
//				os + i2 + this.GetInformationKeyLocation(idx) + "::"
//						+ this.GetInformationKeyName(idx));
//			}
		} else
		{
			os.println(indent+indent+"None");
		}
	}

	//----------------------------------------------------------------------------
//	void vtkPVArrayInformation::SetNumberOfComponents(int numComps)
//	{
//	  if (this.NumberOfComponents == numComps)
//	    {
//	    return;
//	    }
//	  if (this.Ranges)
//	    {
//	    delete[] this.Ranges;
//	    this.Ranges = NULL;
//	    }
//	  this.NumberOfComponents = numComps;
//	  if (numComps <= 0)
//	    {
//	    this.NumberOfComponents = 0;
//	    return;
//	    }
//	  if (numComps > 1)
//	    { // Extra range for vector magnitude (first in array).
//	    numComps = numComps + 1;
//	    }
//
//	  int idx;
//	  this.Ranges = new double[numComps * 2];
//	  for (idx = 0; idx < numComps; ++idx)
//	    {
//	    this.Ranges[2 * idx] = VTK_DOUBLE_MAX;
//	    this.Ranges[2 * idx + 1] = -VTK_DOUBLE_MAX;
//	    }
//	}

	//----------------------------------------------------------------------------
//	void vtkPVArrayInformation::SetComponentName(vtkIdType component,
//	    const char *name)
//	{
//	  if (component < 0 || name == NULL)
//	    {
//	    return;
//	    }
//
//	  unsigned int index = static_cast<unsigned int> (component);
//	  if (this.ComponentNames == NULL)
//	    {
//	    //delayed allocate
//	    this.ComponentNames
//	        = new vtkPVArrayInformation::vtkInternalComponentNames();
//	    }
//
//	  if (index == this.ComponentNames.size())
//	    {
//	    //the array isn't large enough, so we will resize
//	    this.ComponentNames.push_back(new vtkStdString(name));
//	    return;
//	    }
//	  else if (index > this.ComponentNames.size())
//	    {
//	    this.ComponentNames.resize(index + 1, NULL);
//	    }
//
//	  //replace an exisiting element
//	  vtkStdString *compName = this.ComponentNames.at(index);
//	  if (!compName)
//	    {
//	    compName = new vtkStdString(name);
//	    this.ComponentNames.at(index) = compName;
//	    }
//	  else
//	    {
//	    compName.assign(name);
//	    }
//	}

	//----------------------------------------------------------------------------
//	const char* vtkPVArrayInformation::GetComponentName(vtkIdType component)
//	{
//	  unsigned int index = static_cast<unsigned int> (component);
//	  //check signed component for less than zero
//	  if (this.ComponentNames && component >= 0 && index
//	      < this.ComponentNames.size())
//	    {
//	    vtkStdString *compName = this.ComponentNames.at(index);
//	    if (compName)
//	      {
//	      return compName.c_str();
//	      }
//	    }
//	  else if (this.ComponentNames && component == -1
//	      && this.ComponentNames.size() >= 1)
//	    {
//	    //we have a scalar array, and we need the component name
//	    vtkStdString *compName = this.ComponentNames.at(0);
//	    if (compName)
//	      {
//	      return compName.c_str();
//	      }
//	    }
//	  //we have failed to find a user set component name, use the default component name
//	  this.DetermineDefaultComponentName(component, this.GetNumberOfComponents());
//	  return this.DefaultComponentName.c_str();
//	}

	//----------------------------------------------------------------------------
//	void vtkPVArrayInformation::SetComponentRange(int comp, double min, double max)
//	{
//	  if (comp >= this.NumberOfComponents || this.NumberOfComponents <= 0)
//	    {
//	    vtkErrorMacro("Bad component");
//	    }
//	  if (this.NumberOfComponents > 1)
//	    { // Shift over vector mag range.
//	    ++comp;
//	    }
//	  if (comp < 0)
//	    { // anything less than 0 just defaults to the vector mag.
//	    comp = 0;
//	    }
//	  this.Ranges[comp * 2] = min;
//	  this.Ranges[comp * 2 + 1] = max;
//	}

	//----------------------------------------------------------------------------
//	double* vtkPVArrayInformation::GetComponentRange(int comp)
//	{
//	  if (comp >= this.NumberOfComponents || this.NumberOfComponents <= 0)
//	    {
//	    vtkErrorMacro("Bad component");
//	    return NULL;
//	    }
//	  if (this.NumberOfComponents > 1)
//	    { // Shift over vector mag range.
//	    ++comp;
//	    }
//	  if (comp < 0)
//	    { // anything less than 0 just defaults to the vector mag.
//	    comp = 0;
//	    }
//	  return this.Ranges + comp * 2;
//	}

	//----------------------------------------------------------------------------
//	void vtkPVArrayInformation::GetComponentRange(int comp, double *range)
//	{
//	  double *ptr;
//
//	  ptr = this.GetComponentRange(comp);
//
//	  if (ptr == NULL)
//	    {
//	    range[0] = VTK_DOUBLE_MAX;
//	    range[1] = -VTK_DOUBLE_MAX;
//	    return;
//	    }
//
//	  range[0] = ptr[0];
//	  range[1] = ptr[1];
//	}

	//----------------------------------------------------------------------------
//	void vtkPVArrayInformation::GetDataTypeRange(double range[2])
//	{
//	  int dataType = this.GetDataType();
//	  switch (dataType)
//	    {
//	    case VTK_BIT:
//	      range[0] = VTK_BIT_MAX;
//	      range[1] = VTK_BIT_MAX;
//	      break;
//	    case VTK_UNSIGNED_CHAR:
//	      range[0] = VTK_UNSIGNED_CHAR_MIN;
//	      range[1] = VTK_UNSIGNED_CHAR_MAX;
//	      break;
//	    case VTK_CHAR:
//	      range[0] = VTK_CHAR_MIN;
//	      range[1] = VTK_CHAR_MAX;
//	      break;
//	    case VTK_UNSIGNED_SHORT:
//	      range[0] = VTK_UNSIGNED_SHORT_MIN;
//	      range[1] = VTK_UNSIGNED_SHORT_MAX;
//	      break;
//	    case VTK_SHORT:
//	      range[0] = VTK_SHORT_MIN;
//	      range[1] = VTK_SHORT_MAX;
//	      break;
//	    case VTK_UNSIGNED_INT:
//	      range[0] = VTK_UNSIGNED_INT_MIN;
//	      range[1] = VTK_UNSIGNED_INT_MAX;
//	      break;
//	    case VTK_INT:
//	      range[0] = VTK_INT_MIN;
//	      range[1] = VTK_INT_MAX;
//	      break;
//	    case VTK_UNSIGNED_LONG:
//	      range[0] = VTK_UNSIGNED_LONG_MIN;
//	      range[1] = VTK_UNSIGNED_LONG_MAX;
//	      break;
//	    case VTK_LONG:
//	      range[0] = VTK_LONG_MIN;
//	      range[1] = VTK_LONG_MAX;
//	      break;
//	    case VTK_FLOAT:
//	      range[0] = VTK_FLOAT_MIN;
//	      range[1] = VTK_FLOAT_MAX;
//	      break;
//	    case VTK_DOUBLE:
//	      range[0] = VTK_DOUBLE_MIN;
//	      range[1] = VTK_DOUBLE_MAX;
//	      break;
//	    default:
//	      // Default value:
//	      range[0] = 0;
//	      range[1] = 1;
//	      break;
//	    }
//	}
	//----------------------------------------------------------------------------
	void AddRanges(VTKArrayInformation info)
	{
		if (this.NumberOfComponents != info.NumberOfComponents)
		{
			System.err.println("Component mismatch.");
		}

		double[] range = info.Ranges[0];
		if (this.NumberOfComponents > 1)
		{
			if (range[0] < this.Ranges[0][0])
			{
				Ranges[0][0] = range[0];
			}
			if (range[1] > Ranges[0][1])
			{
				Ranges[0][1] = range[1];
			}
			for (int idx = 0; idx < this.NumberOfComponents; ++idx)
			{
				range = info.Ranges[idx];
				if (range[0] < Ranges[idx+1][0])
				{
					Ranges[idx+1][0] = range[0];
				}
				if (range[1] > Ranges[idx+1][1])
				{
					Ranges[idx+1][1] = range[1];
				}
			}
		} else {
			if (range[0] < this.Ranges[0][0])
			{
				Ranges[0][0] = range[0];
			}
			if (range[1] > Ranges[0][1])
			{
				Ranges[0][1] = range[1];
			}
		}


		this.NumberOfTuples += info.NumberOfTuples;
	}

	//----------------------------------------------------------------------------
	void DeepCopy(VTKArrayInformation info)
	{
		this.Name = info.Name;
		this.DataType = info.DataType;
		this.NumberOfComponents = info.NumberOfComponents;
		this.NumberOfTuples = info.NumberOfTuples;
		
		if (this.NumberOfComponents > 1)
		{
			this.Ranges = new double[this.NumberOfComponents+1][2];
			for (int idx = 0; idx < this.NumberOfComponents+1; ++idx)
			{
				this.Ranges[idx] = info.Ranges[idx];
			}
		} else {
			this.Ranges = new double[this.NumberOfComponents][2];
			this.Ranges[0] = info.Ranges[0];
		}


		//clear the vector of old data
		if (this.ComponentNames != null)
		{
			this.ComponentNames = null;
		}

		if (info.ComponentNames != null)
		{
//			this.ComponentNames
//			= new vtkPVArrayInformation::vtkInternalComponentNames();
//			//copy the passed in components if they exist
//			this.ComponentNames.reserve(info.ComponentNames.size());
//			const char *name;
//			for (unsigned i = 0; i < info.ComponentNames.size(); ++i)
//			{
//				name = info.GetComponentName(i);
//				if (name)
//				{
//					this.SetComponentName(i, name);
//				}
//			}
		}

		if (this.InformationKeys == null)
		{
			this.InformationKeys = new ArrayList<>();
		}

		//clear the vector of old data
		this.InformationKeys.clear();

		if (info.InformationKeys != null)
		{
//			//copy the passed in components if they exist
//			for (unsigned i = 0; i < info.InformationKeys.size(); ++i)
//			{
//				this.InformationKeys.push_back(info.InformationKeys.at(i));
//			}
		}
	}

	//----------------------------------------------------------------------------
	boolean Compare(VTKArrayInformation info) {
		if (info == null) {
			return false;
		}
		if (info.Name.equals(this.Name) && info.NumberOfComponents == this.NumberOfComponents && this.NumberOfInformationKeys == info.NumberOfInformationKeys) {
			return true;
		}
		return false;
	}

	//----------------------------------------------------------------------------
	void CopyFromObject(vtkObject obj)
	{
		this.Initialize();

		vtkAbstractArray array = (vtkAbstractArray) obj;
//		if (!array)
//		{
//			vtkErrorMacro("Cannot downcast to abstract array.");
//			this.Initialize();
//			return;
//		}

		this.Name = array.GetName();
		this.DataType = array.GetDataType();
		this.NumberOfComponents = array.GetNumberOfComponents();
		this.NumberOfTuples = array.GetNumberOfTuples();

		if (array.HasAComponentName())
		{
			//copy the component names over
			for (int i = 0; i < this.NumberOfComponents; ++i)
			{
				String name = array.GetComponentName(i);
				if (name != null)
				{
					//each component doesn't have to be named
					this.ComponentNames.add(i, name);
				}
			}
		}

		if (obj instanceof vtkDataArray)
		{
			vtkDataArray data_array = (vtkDataArray) obj;
			
			if (this.NumberOfComponents > 1)
			{
				this.Ranges = new double[this.NumberOfComponents+1][2];
				// First store range of vector magnitude.
				data_array.GetRange(this.Ranges[0], -1);
				for (int idx = 0; idx < this.NumberOfComponents; ++idx)
				{
					data_array.GetRange(this.Ranges[idx+1], idx);
				}
			} else {
				this.Ranges = new double[this.NumberOfComponents][2];
				data_array.GetRange(this.Ranges[0], 0);
			}
		}

//		if(this.InformationKeys)
//		{
//			this.InformationKeys.clear();
//			delete this.InformationKeys;
//			this.InformationKeys = 0;
//		}
		if (array.HasInformation())
		{
//			vtkInformation info = array.GetInformation();
//			vtkInformationIterator it = new vtkInformationIterator();
//			it.SetInformationWeak(info);
//			it.GoToFirstItem();
//			while (!it.IsDoneWithTraversal())
//			{
//				vtkInformationKey key = it.GetCurrentKey();
//				this.AddInformationKey(key.GetLocation(), key.GetName());
//				it.GoToNextItem();
//			}
//			it.Delete();
		}
	}

	//----------------------------------------------------------------------------
	void AddInformation(VTKArrayInformation info)
	{
		if (info == null) {
			return;
		}

		if (info.NumberOfComponents > 0)
		{
			if (this.NumberOfComponents == 0)
			{
				// If this object is uninitialized, copy.
				this.DeepCopy(info);
			}
			else
			{
				// Leave everything but ranges and unique values as original, add ranges and unique values.
				this.AddRanges(info);
				//this.AddInformationKeys(info);
			}
		}
	}

	//-----------------------------------------------------------------------------
//	void DetermineDefaultComponentName(
//	    const int &component_no, const int &num_components)
//	{
//	  if (!this.DefaultComponentName)
//	    {
//	    this.DefaultComponentName = new vtkStdString();
//	    }
//
//	  this.DefaultComponentName.assign(vtkPVPostFilter::DefaultComponentName(component_no, num_components));
//	}

//	void AddInformationKeys(VTKArrayInformation info)
//	{
//	  for (int k = 0; k < info.NumberOfInformationKeys; k++)
//	    {
//	    this.AddUniqueInformationKey(info.GetInformationKeyLocation(k),
//	        info.GetInformationKeyName(k));
//	    }
//	}

//	void AddInformationKey(String location, String name)
//	{
//	  if(this.InformationKeys == null)
//	    {
//	    this.InformationKeys = new vtkInternalInformationKeys();
//	    }
//	  vtkPVArrayInformationInformationKey info = vtkPVArrayInformationInformationKey();
//	  info.Location = location;
//	  info.Name = name;
//	  this.InformationKeys.push_back(info);
//	}

//	void AddUniqueInformationKey(String location, String name)
//	{
//	  if (!this.HasInformationKey(location, name))
//	    {
//	    this.AddInformationKey(location, name);
//	    }
//	}

//	int GetNumberOfInformationKeys()
//	{
//	  return static_cast<int>(this.InformationKeys ? this.InformationKeys.size() : 0);
//	}

//	String GetInformationKeyLocation(int index)
//	{
//	  if (index < 0 || index >= this.GetNumberOfInformationKeys())
//	    return NULL;
//
//	  return this.InformationKeys.at(index).Location;
//	}

//	String GetInformationKeyName(int index)
//	{
//	  if (index < 0 || index >= this.GetNumberOfInformationKeys())
//	    return NULL;
//
//	  return this.InformationKeys.at(index).Name;
//	}

//	int HasInformationKey(String location, String name)
//	{
//	  for (int k = 0; k < this.GetNumberOfInformationKeys(); k++)
//	    {
//		  String key_location = this.GetInformationKeyLocation(k);
//		  String key_name = this.GetInformationKeyName(k);
//	    if (strcmp(location, key_location) == 0 && strcmp(name, key_name) == 0)
//	      {
//	      return 1;
//	      }
//	    }
//	  return 0;
//	}

}
