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

import vtk.vtkGenericAttribute;

public class VTKGenericAttributeInformation extends VTKArrayInformation {
	
	//----------------------------------------------------------------------------
	void CopyFromObject(vtkGenericAttribute array)
	{

	  this.Name = array.GetName();
	  this.DataType = array.GetComponentType();
	  this.NumberOfComponents = array.GetNumberOfComponents();
	  
	  if (this.NumberOfComponents > 1) {
		  this.Ranges = new double[this.NumberOfComponents+1][2];
		  // First store range of vector magnitude.
		  array.GetRange(-1,this.Ranges[0]);
		  for (int idx = 0; idx < this.NumberOfComponents; idx++) {
			  array.GetRange(idx, this.Ranges[idx + 1]);
		  }
	  } else {
		  this.Ranges = new double[1][2];
		  array.GetRange(0,this.Ranges[0]);
	  }
	}

}
