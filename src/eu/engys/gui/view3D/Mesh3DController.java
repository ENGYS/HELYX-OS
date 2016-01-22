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


package eu.engys.gui.view3D;

import java.util.Collection;

import vtk.vtkPlane;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.core.project.mesh.ScalarBarType;
import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.core.project.zero.patches.Patch;

public interface Mesh3DController extends Controller3D {

	void updatePatchesSelection(Patch[] selection);
	void updatePatchesVisibility(Patch... selection);

	void updateCellZonesSelection(CellZone[] selection);
	void updateCellZonesVisibility(CellZone... selection);

	void clear();

    BoundingBox computeBoundingBox();

	void showTimeStep(double value);
	void showField(FieldItem fieldItem);
	
	void clip(vtkPlane plane);
	void slice(vtkPlane plane);
	void crinkle(vtkPlane plane);
	void disconnectFiltersFromInternalMesh();
	
	void insideOut(boolean selected);
	
	void showExternalMesh();
	
	void showInternalMesh();
	void hideInternalMesh();
	boolean isInternalMeshLoaded();
	
	void readTimeSteps();
	FieldItem getCurrentFieldItem();
	double getCurrentTimeStep();
	
	Collection<Actor> getActorsList();
	
    
	void setAutomaticRangeCalculation(boolean autoRange);
	void setManualRangeCalculation(double[] rangeField);

	void setScalarsActorsResolution(int resolution);

	void resetScalarsActorsRangeAndResolutionAndHue();
    void setScalarsBarType(ScalarBarType hueRangeType);

}
