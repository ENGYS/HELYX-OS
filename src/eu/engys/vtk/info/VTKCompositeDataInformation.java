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

import vtk.vtkCompositeDataSet;
import vtk.vtkDataObject;
import vtk.vtkInformation;
import vtk.vtkMultiBlockDataSet;
import vtk.vtkMultiPieceDataSet;
import vtk.vtkObject;

public class VTKCompositeDataInformation {

	class DataInformation {
		VTKDataInformation Info;
		String Name;
	}

	private boolean DataIsComposite;
	private boolean DataIsMultiPiece;
	private int NumberOfPieces;
	private List<DataInformation> ChildrenInformation;

	public VTKCompositeDataInformation() {
		// this.Internal = new vtkPVCompositeDataInformationInternals;
		this.DataIsComposite = false;
		this.DataIsMultiPiece = false;
		this.NumberOfPieces = 0;
		// DON'T FORGET TO UPDATE Initialize().
	}

	boolean GetDataIsComposite() {
		return DataIsComposite;
	}

	boolean GetDataIsMultiPiece() {
		return DataIsMultiPiece;
	}
	 void PrintSelf(PrintStream os, String indent)
	 {
	 os.println(indent+"DataIsMultiPiece: " + this.DataIsMultiPiece);
	 os.println(indent+"DataIsComposite: " + this.DataIsComposite);
	 }

//	VTKDataInformation GetDataInformationForCompositeIndex(int index)
//	{
//		if (!this.DataIsComposite)
//		{
//			return null;
//		}
//
//		if (this.DataIsMultiPiece)
//		{
//			if (index < this.NumberOfPieces) {
//				(*index)=-1;
//				return null;
//			}
//
//			(*index) -= this.NumberOfPieces;
//		}
//
//		for ( DataInformation d : ChildrenInformation)
//		{
//			if (d.Info != null)
//			{
//				VTKDataInformation info = d.Info.GetDataInformationForCompositeIndex(index);
//				if ( (*index) == -1)
//				{
//					return info;
//				}
//			} else {
//				(*index)--;
//				if ((*index) < 0)
//				{
//					return null;
//				}
//			}
//		}
//		return null;
//	}

	void Initialize() {
		this.DataIsMultiPiece = false;
		this.NumberOfPieces = 0;
		this.DataIsComposite = false;
		this.ChildrenInformation = new ArrayList<>();
	}

	int GetNumberOfChildren() {
		return this.DataIsMultiPiece ? this.NumberOfPieces : ChildrenInformation.size();
	}

	VTKDataInformation GetDataInformation(int idx) {
		if (this.DataIsMultiPiece) {
			return null;
		}

		if (idx >= ChildrenInformation.size()) {
			return null;
		}

		return this.ChildrenInformation.get(idx).Info;
	}

	String GetName(int idx) {
		if (this.DataIsMultiPiece) {
			return null;
		}

		if (idx >= this.ChildrenInformation.size()) {
			return null;
		}

		return this.ChildrenInformation.get(idx).Name;
	}

	void CopyFromObject(vtkObject object) {
		this.Initialize();

		if (!(object instanceof vtkCompositeDataSet)) {
			return;
		}
		
		vtkCompositeDataSet cds = (vtkCompositeDataSet) object;
		this.DataIsComposite = true;

		if (object instanceof vtkMultiPieceDataSet) {
			vtkMultiPieceDataSet mpDS = (vtkMultiPieceDataSet) object;
			this.DataIsMultiPiece = true;
			this.NumberOfPieces = mpDS.GetNumberOfPieces();
			return;
		}

		if(object instanceof vtkMultiBlockDataSet){
			vtkMultiBlockDataSet mbds = (vtkMultiBlockDataSet) object; 
			for (int i = 0; i < mbds.GetNumberOfBlocks(); i++) {
				VTKDataInformation childInfo = null;
				vtkDataObject block = mbds.GetBlock(i);
				if (block != null) {
					if (block != null) {
						childInfo = new VTKDataInformation();
						childInfo.CopyFromObject(block);
					}
					DataInformation d = new DataInformation();
					d.Info = childInfo;
					
					vtkInformation info = block.GetInformation();
					if (info.Has(cds.NAME()) != 0) {
							d.Name = info.Get(cds.NAME());
						}

//					if (iter.HasCurrentMetaData() != 0) {
//						vtkInformation info = iter.GetCurrentMetaData();
//						if (info.Has(cds.NAME()) != 0) {
//							d.Name = info.Get(cds.NAME());
//						}
//					}

					ChildrenInformation.add(d);
				}
			}
		} 
		// vtkTimerLog::MarkEndEvent("Copying information from composite data");
	}
	
	
	// void vtkPVCompositeDataInformation::CopyFromAMR(vtkUniformGridAMR* amr)
	// {
	// unsigned int num_levels = amr.GetNumberOfLevels();
	// if (num_levels == 0)
	// {
	// this.Internal.ChildrenInformation.clear();
	// }
	// else
	// {
	// this.Internal.ChildrenInformation.resize(num_levels);
	// }
	//
	// // we use this to "simulate" a composite tree from AMR
	// vtkNew<vtkMultiPieceDataSet> tempMultiPiece;
	// vtkNew<vtkPVDataInformation> tempDSInfo;
	//
	// for (unsigned int level=0; level < num_levels; level++)
	// {
	// unsigned int num_datasets = amr.GetNumberOfDataSets(level);
	// tempMultiPiece.SetNumberOfPieces(num_datasets);
	//
	// vtkNew<vtkPVDataInformation> levelInfo;
	// levelInfo.CopyFromCompositeDataSetInitialize(tempMultiPiece.GetPointer());
	//
	// // now fill up levelInfo with meta-data about arrays.
	// for (unsigned int idx=0; idx < num_datasets; idx++)
	// {
	// vtkUniformGrid* dataset = amr.GetDataSet(level, idx);
	// if (dataset)
	// {
	// tempDSInfo.CopyFromObject(dataset);
	// levelInfo.AddInformation(tempDSInfo.GetPointer(), 1);
	// }
	// }
	// levelInfo.CopyFromCompositeDataSetFinalize(tempMultiPiece.GetPointer());
	// this.Internal.ChildrenInformation[level].Info = levelInfo.GetPointer();
	// }
	// }

	//Called to merge informations from two processess.
	 void AddInformation(VTKCompositeDataInformation info)
	 {
		 if (info == null)
		 {
			 System.err.println("Cound not cast object to data information.");
			 return;
		 }

		 this.DataIsComposite = info.GetDataIsComposite();
		 this.DataIsMultiPiece = info.GetDataIsMultiPiece();
		 if (this.DataIsMultiPiece)
		 {
			 if (this.NumberOfPieces != info.NumberOfPieces)
			 {
				 // vtkWarningMacro("Mismatch in number of pieces among processes.");
			 }
			 if (info.NumberOfPieces > this.NumberOfPieces)
			 {
				 this.NumberOfPieces = info.NumberOfPieces;
			 }
			 return;
		 }

		 int otherNumChildren = info.ChildrenInformation.size();
		 int numChildren = this.ChildrenInformation.size();
		 if ( otherNumChildren > numChildren)
		 {
			 numChildren = otherNumChildren;
			 //this.ChildrenInformation.resize(numChildren);
		 }

		 for (int i=0; i < otherNumChildren; i++)
		 {
			 VTKDataInformation otherInfo = info.ChildrenInformation.get(i).Info;
			 VTKDataInformation localInfo = this.ChildrenInformation.get(i).Info;
			 if (otherInfo != null)
			 {
				 if (localInfo != null)
				 {
					 localInfo.AddInformation(otherInfo);
				 }
				 else
				 {
					 VTKDataInformation dinf = new VTKDataInformation();
					 dinf.AddInformation(otherInfo);
					 this.ChildrenInformation.get(i).Info = dinf;
				 }
			 }

			 String otherName = info.ChildrenInformation.get(i).Name;
			 String localName = this.ChildrenInformation.get(i).Name;
			 if (!otherName.isEmpty())
			 {
				 if (!localName.isEmpty() && localName != otherName)
				 {
					 //vtkWarningMacro("Same block is named as \'" << localName.c_str()
					 // << "\' as well as \'" << otherName.c_str() << "\'");
				 }
				 localName = otherName;
			 }
		 }
	 }

	// void vtkPVCompositeDataInformation::CopyToStream(
	// vtkClientServerStream* css)
	// {
	// // vtkTimerLog::MarkStartEvent("Copying composite information to stream");
	// css.Reset();
	// *css << vtkClientServerStream::Reply
	// << this.DataIsComposite
	// << this.DataIsMultiPiece
	// << this.NumberOfPieces;
	//
	// unsigned int numChildren = static_cast<unsigned int>(
	// this.Internal.ChildrenInformation.size());
	// *css << numChildren;
	//
	// for(unsigned i=0; i<numChildren; i++)
	// {
	// vtkPVDataInformation* dataInf = this.Internal.ChildrenInformation[i].Info;
	// if (dataInf)
	// {
	// *css << i
	// << this.Internal.ChildrenInformation[i].Name.c_str();
	//
	// vtkClientServerStream dcss;
	// dataInf.CopyToStream(&dcss);
	//
	// size_t length;
	// const unsigned char* data;
	// dcss.GetData(&data, &length);
	// *css << vtkClientServerStream::InsertArray(data,
	// static_cast<int>(length));
	// }
	// }
	// *css << numChildren; // DONE marker
	// *css << vtkClientServerStream::End;
	// // vtkTimerLog::MarkEndEvent("Copying composite information to stream");
	// }
	//
	// //----------------------------------------------------------------------------
	// void vtkPVCompositeDataInformation::CopyFromStream(
	// const vtkClientServerStream* css)
	// {
	// this.Initialize();
	//
	// if(!css.GetArgument(0, 0, &this.DataIsComposite))
	// {
	// vtkErrorMacro("Error parsing data set type.");
	// return;
	// }
	//
	// if(!css.GetArgument(0, 1, &this.DataIsMultiPiece))
	// {
	// vtkErrorMacro("Error parsing data set type.");
	// return;
	// }
	//
	// if(!css.GetArgument(0, 2, &this.NumberOfPieces))
	// {
	// vtkErrorMacro("Error parsing number of pieces.");
	// return;
	// }
	//
	// unsigned int numChildren;
	// if(!css.GetArgument(0, 3, &numChildren))
	// {
	// vtkErrorMacro("Error parsing number of children.");
	// return;
	// }
	// int msgIdx = 3;
	// this.Internal.ChildrenInformation.resize(numChildren);
	//
	// while (1)
	// {
	// msgIdx++;
	// unsigned int childIdx;
	// if(!css.GetArgument(0, msgIdx, &childIdx))
	// {
	// vtkErrorMacro("Error parsing data set type.");
	// return;
	// }
	// if (childIdx >= numChildren) //receiver DONE marker.
	// {
	// break;
	// }
	// msgIdx++;
	//
	// const char* name = 0;
	// if (!css.GetArgument(0, msgIdx, &name))
	// {
	// vtkErrorMacro("Error parsing the name for the block.");
	// return;
	// }
	//
	// vtkTypeUInt32 length;
	// std::vector<unsigned char> data;
	// vtkClientServerStream dcss;
	//
	// msgIdx++;
	// // Data information.
	// vtkPVDataInformation* dataInf = vtkPVDataInformation::New();
	// if(!css.GetArgumentLength(0, msgIdx, &length))
	// {
	// vtkErrorMacro("Error parsing length of cell data information.");
	// dataInf.Delete();
	// return;
	// }
	// data.resize(length);
	// if(!css.GetArgument(0, msgIdx, &*data.begin(), length))
	// {
	// vtkErrorMacro("Error parsing cell data information.");
	// dataInf.Delete();
	// return;
	// }
	// dcss.SetData(&*data.begin(), length);
	// dataInf.CopyFromStream(&dcss);
	// this.Internal.ChildrenInformation[childIdx].Info = dataInf;
	// this.Internal.ChildrenInformation[childIdx].Name = name;
	// dataInf.Delete();
	// }
	//
	// }

}
