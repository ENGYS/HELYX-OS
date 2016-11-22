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
package eu.engys.gui.view3D.fallback;

import java.io.File;
import java.util.Collection;
import java.util.List;

import eu.engys.core.controller.GeometryToMesh;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.core.project.zero.facezones.FaceZone;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.gui.view.View3DElement;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Context;
import eu.engys.gui.view3D.Mesh3DController;
import eu.engys.gui.view3D.RenderPanel;
import vtk.vtkPlane;

public class FallbackMesh3DController implements Mesh3DController {

    @Override
    public void setRenderPanel(RenderPanel renderPanel) {
    }

    @Override
    public void updatePatchesSelection(Patch[] selection) {
    }

    @Override
    public void updateCellZonesVisibility(CellZone... selection) {
    }

    @Override
    public void updatePatchesVisibility(Patch... selection) {
    }

    @Override
    public void updateCellZonesSelection(CellZone[] selection) {
    }

    @Override
    public void updateFaceZonesSelection(FaceZone[] selection) {
    }

    @Override
    public void updateFaceZonesVisibility(FaceZone... selection) {
    }

    @Override
    public void loadActors() {
    }

    @Override
    public Context getCurrentContext() {
        return null;
    }

    @Override
    public void clearContext() {
    }

    @Override
    public void clear() {
    }

    @Override
    public void exportContourAsSTL(File stlFile) {
    }

    @Override
    public BoundingBox computeBoundingBox() {
        return null;
    }

    @Override
    public void updateActorsColors() {
    }

    @Override
    public void dumpContext(Class<? extends View3DElement> klass) {
    }

    @Override
    public void applyContext(Class<? extends View3DElement> klass) {
    }

    @Override
    public void newContext(Class<? extends View3DElement> klass) {
    }

    @Override
    public void newEmptyContext(Class<? extends View3DElement> klass) {
    }

    @Override
    public void geometryToMesh(GeometryToMesh g2m) {
    }

    @Override
    public void changeTimeStep(double value) {
    }

    @Override
    public FieldItem getCurrentFieldItem() {
        return null;
    }

    @Override
    public double getCurrentTimeStep() {
        return 0;
    }

    @Override
    public void showField(FieldItem fieldItem) {
    }

    @Override
    public void clip(vtkPlane plane, boolean insideOut) {
    }

    @Override
    public void slice(vtkPlane plane, List<Double> values) {
    }

    @Override
    public void contour(String field, List<Double> values, int smoothingIterations, double smoothingConvergence) {
    }

    @Override
    public void crinkle(vtkPlane plane) {
    }

    @Override
    public void showInternalMesh() {
    }

    @Override
    public void hideInternalMesh() {
    }

    @Override
    public void readTimeSteps() {
    }

    @Override
    public void showExternalMesh() {
    }

    @Override
    public Collection<Actor> getActorsList() {
        return null;
    }

    @Override
    public boolean isInternalMeshLoaded() {
        return false;
    }

    @Override
    public boolean isInternalMeshVisible() {
        return false;
    }

    @Override
    public void render() {
    }

    @Override
    public void zoomReset() {
    }

    @Override
    public void disconnectFiltersFromInternalMesh() {
    }

}
