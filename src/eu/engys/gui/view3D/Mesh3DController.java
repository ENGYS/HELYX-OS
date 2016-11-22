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
package eu.engys.gui.view3D;

import java.io.File;
import java.util.Collection;
import java.util.List;

import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.core.project.zero.facezones.FaceZone;
import eu.engys.core.project.zero.patches.Patch;
import vtk.vtkPlane;

public interface Mesh3DController extends Controller3D {

    void updatePatchesSelection(Patch[] selection);

    void updatePatchesVisibility(Patch... selection);

    void updateCellZonesSelection(CellZone[] selection);

    void updateCellZonesVisibility(CellZone... selection);

    void updateFaceZonesSelection(FaceZone[] selection);

    void updateFaceZonesVisibility(FaceZone... selection);

    void clear();

    BoundingBox computeBoundingBox();

    void changeTimeStep(double value);

    void showField(FieldItem fieldItem);

    void clip(vtkPlane plane, boolean insideOut);

    void slice(vtkPlane plane, List<Double> values);

    void crinkle(vtkPlane plane);

    void contour(String field, List<Double> values, int smoothingIterations, double smoothingConvergence);

    void exportContourAsSTL(File stlFile);

    void disconnectFiltersFromInternalMesh();

    void showExternalMesh();

    void showInternalMesh();

    void hideInternalMesh();

    boolean isInternalMeshLoaded();

    boolean isInternalMeshVisible();

    void readTimeSteps();

    FieldItem getCurrentFieldItem();

    double getCurrentTimeStep();

    Collection<Actor> getActorsList();

    void updateActorsColors();

}
