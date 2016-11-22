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
//package eu.engys.vtk;
//
//import java.io.File;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
//import vtk.vtkAlgorithm;
//import vtk.vtkContourFilter;
//import vtk.vtkCutter;
//import vtk.vtkDataObject;
//import vtk.vtkExtractGeometry;
//import vtk.vtkPlane;
//import vtk.vtkSTLWriter;
//import vtk.vtkSmoothPolyDataFilter;
//import vtk.vtkTableBasedClipDataSet;
//import vtk.vtkUnstructuredGrid;
//import eu.engys.gui.view3D.Actor;
//import eu.engys.gui.view3D.RenderPanel;
//import eu.engys.util.progress.ProgressMonitor;
//import eu.engys.vtk.actors.InternalMeshActor;
//
//public class VTKInternalMesh implements VTKActors {
//
//    private static final String UPDATING_CONTOUR_AND_SMOOTH = "Updating contour and smooth";
//    private static final String UPDATING_CRINKLE = "Updating crinkle";
//    private static final String UPDATING_CLIPPER = "Updating clipper";
//    private static final String UPDATING_SLICER = "Updating slicer";
//    private Actor actor;
//    private RenderPanel renderPanel;
//
//    private vtkCutter slicer;
//    private vtkExtractGeometry crinkle;
//    private vtkTableBasedClipDataSet clipper;
//
//    private vtkContourFilter contour;
//    private vtkSmoothPolyDataFilter smooth;
//
//    private vtkUnstructuredGrid internalMeshDataset;
//    private ProgressMonitor monitor;
//
//    public VTKInternalMesh(ProgressMonitor monitor) {
//        this.monitor = monitor;
//    }
//
//    @Override
//    public void setRenderPanel(RenderPanel renderPanel) {
//        this.renderPanel = renderPanel;
//    }
//
//    public void update(final vtkDataObject dataset) {
//        if (actor != null) {
//            if (this.internalMeshDataset != null) {
//                VTKUtil.deleteDataset(this.internalMeshDataset);
//            }
//
//            this.internalMeshDataset = new vtkUnstructuredGrid();
//            this.internalMeshDataset.ShallowCopy((vtkUnstructuredGrid) dataset);
//            if (slicer != null) {
//                slicer.SetInputData(internalMeshDataset);
//                updateInAThread(UPDATING_SLICER, slicer);
//                connectActorToSlicer();
//            } else if (clipper != null) {
//                clipper.SetInputData(internalMeshDataset);
//                updateInAThread(UPDATING_CLIPPER, clipper);
//                connectActorToClipper();
//            } else if (crinkle != null) {
//                crinkle.SetInputData(internalMeshDataset);
//                updateInAThread(UPDATING_CRINKLE, crinkle);
//                connectActorToCrinkler();
//            } else if (contour != null && smooth != null) {
//                contour.SetInputData(internalMeshDataset);
//                updateInAThread(UPDATING_CONTOUR_AND_SMOOTH, contour, smooth);
//                connectActorToSmoothedContour();
//            }
//        }
//    }
//
//    public void load(vtkDataObject internalMeshDataset) {
//        if (this.internalMeshDataset != null) {
//            VTKUtil.deleteDataset(this.internalMeshDataset);
//        }
//
//        if (this.actor != null) {
//            this.actor.deleteActor();
//        }
//
//        this.internalMeshDataset = new vtkUnstructuredGrid();
//        this.internalMeshDataset.ShallowCopy((vtkUnstructuredGrid) internalMeshDataset);
//        this.actor = new InternalMeshActor(this.internalMeshDataset);
//    }
//
//    public void deleteActors() {
//        if (actor != null) {
//            renderPanel.removeActor(actor);
//            actor.deleteActor();
//            actor = null;
//
//            deleteClipper();
//            deleteSlicer();
//            deleteCrinkle();
//            deleteContourAndSmooth();
//        }
//    }
//
//    public void removeActorsFromRenderer() {
//        if (actor != null) {
//            renderPanel.removeActor(actor);
//        }
//        actor = null;
//    }
//
//    private void deleteSlicer() {
//        if (slicer != null) {
//            slicer.RemoveAllInputs();
//            slicer.Delete();
//            slicer = null;
//        }
//    }
//
//    private void deleteClipper() {
//        if (clipper != null) {
//            clipper.RemoveAllInputs();
//            clipper.Delete();
//            clipper = null;
//        }
//    }
//
//    private void deleteCrinkle() {
//        if (crinkle != null) {
//            crinkle.RemoveAllInputs();
//            crinkle.Delete();
//            crinkle = null;
//        }
//    }
//
//    private void deleteContourAndSmooth() {
//        if (contour != null) {
//            contour.RemoveAllInputs();
//            contour.Delete();
//            contour = null;
//        }
//        if (smooth != null) {
//            smooth.RemoveAllInputs();
//            smooth.Delete();
//            smooth = null;
//        }
//    }
//
//    public void VisibilityOff() {
//        if (actor != null) {
//            actor.setVisibility(false);
//        }
//    }
//
//    public boolean isVisible() {
//        return actor != null && actor.getVisibility();
//    }
//
//    public void VisibilityOn() {
//        if (actor != null) {
//            actor.setVisibility(true);
//        }
//    }
//
//    public void disconnectFilters() {
//        actor.setInput(internalMeshDataset);
//
//        deleteClipper();
//        deleteSlicer();
//        deleteCrinkle();
//        deleteContourAndSmooth();
//    }
//
//    public boolean isLoaded() {
//        return actor != null;
//    }
//
//    public void show() {
//        renderPanel.addActor(actor);
//    }
//
//    @Override
//    public Collection<Actor> getActors() {
//        return actor != null ? Arrays.asList(actor) : Collections.<Actor> emptyList();
//    }
//
//    @Override
//    public boolean containsActor(Actor pickedActor) {
//        return false;
//    }
//
//    public void clip(vtkPlane plane) {
//        deleteClipper();
//        deleteSlicer();
//        deleteCrinkle();
//
//        clipper = new vtkTableBasedClipDataSet();
//        clipper.SetInputData(internalMeshDataset);
//        clipper.SetClipFunction(plane);
//        clipper.InsideOutOff();
//
//        updateInAThread(UPDATING_CLIPPER, clipper);
//
//        connectActorToClipper();
//
//        renderPanel.renderLater();
//
//        VTKUtil.gc(false);
//    }
//
//    private void connectActorToClipper() {
//        actor.interactiveOff();
//        actor.setInput(clipper.GetOutput());
//    }
//
//    void smoothedContour(final String field, final List<Double> values, final int smoothingIterations, final double smoothingConvergence) {
//        deleteContourAndSmooth();
//
//        contour = new vtkContourFilter();
//        contour.SetInputData(internalMeshDataset);
//        contour.ComputeScalarsOff();
//        contour.ComputeNormalsOff();
//        contour.GenerateTrianglesOn();
//        contour.SetInputArrayToProcess(0, 0, 0, "vtkDataObject::FIELD_ASSOCIATION_POINTS", field);
//        contour.SetNumberOfContours(values.size());
//        for (int i = 0; i < values.size(); i++) {
//            contour.SetValue(i, values.get(i));
//        }
//
//        smooth = new vtkSmoothPolyDataFilter();
//        smooth.SetInputData(contour.GetOutput());
//        smooth.SetNumberOfIterations(smoothingIterations);
//        smooth.SetConvergence(smoothingConvergence);
//
//        updateInAThread(UPDATING_CONTOUR_AND_SMOOTH, contour, smooth);
//
//        connectActorToSmoothedContour();
//
//        renderPanel.renderLater();
//
//        VTKUtil.gc(false);
//    }
//
//    void exportSmoothedContourAsSTL(final File stlFile) {
//        vtkSTLWriter writer = new vtkSTLWriter();
//        writer.SetFileName(stlFile.getAbsolutePath());
//        writer.SetInputData(smooth.GetOutput());
//
//        writeSTLInAThread("Saving STL file " + stlFile.getAbsolutePath(), writer);
//        writer.Delete();
//    }
//
//    private void writeSTLInAThread(String log, final vtkSTLWriter writer) {
//        writer.RemoveAllObservers();
//        VTKProgressMonitorWrapper progressWrapper = new VTKProgressMonitorWrapper("", writer, monitor);
//        writer.AddObserver("StartEvent", progressWrapper, "onStart");
//        writer.AddObserver("EndEvent", progressWrapper, "onEnd");
//        writer.AddObserver("ProgressEvent", progressWrapper, "onProgress");
//
//        monitor.setIndeterminate(false);
//        monitor.setTotal(100);
//        monitor.start(log, false, new Runnable() {
//            @Override
//            public void run() {
//                writer.Write();
//                monitor.end();
//            }
//        });
//    }
//
//    private void connectActorToSmoothedContour() {
//        actor.interactiveOff();
//        actor.setInput(smooth.GetOutput());
//    }
//
//    void crinkle(vtkPlane plane) {
//        deleteClipper();
//        deleteSlicer();
//        deleteCrinkle();
//
//        crinkle = new vtkExtractGeometry();
//
//        crinkle.SetInputData(internalMeshDataset);
//        crinkle.ExtractInsideOn();
//        crinkle.ExtractOnlyBoundaryCellsOn();
//        crinkle.ExtractBoundaryCellsOn();
//        crinkle.SetImplicitFunction(plane);
//
//        updateInAThread(UPDATING_CRINKLE, crinkle);
//
//        connectActorToCrinkler();
//
//        renderPanel.renderLater();
//    }
//
//    private void connectActorToCrinkler() {
//        actor.interactiveOff();
//        actor.setInput(crinkle.GetOutput());
//    }
//
//    void slice(vtkPlane plane, List<Double> values) {
//        deleteClipper();
//        deleteSlicer();
//        deleteCrinkle();
//
//        slicer = new vtkCutter();
//        slicer.SetInputData(internalMeshDataset);
//        slicer.GenerateTrianglesOff();
//        slicer.SetCutFunction(plane);
//
////        slicer.SetNumberOfContours(values.size());
////        for (int i = 0; i < values.size(); i++) {
////            slicer.SetValue(i, values.get(i));
////        }
//
//        updateInAThread(UPDATING_SLICER, slicer);
//
//        connectActorToSlicer();
//
//        renderPanel.renderLater();
//    }
//
//    private void connectActorToSlicer() {
//        actor.interactiveOff();
//        actor.setInput(slicer.GetOutput());
//    }
//
//    void insideOut(boolean selected) {
//        if (selected) {
//            clipper.InsideOutOn();
//        } else {
//            clipper.InsideOutOff();
//        }
//        updateInAThread(UPDATING_CLIPPER, clipper);
//
//        connectActorToClipper();
//
//        renderPanel.renderLater();
//    }
//
//    private void updateInAThread(String log, final vtkAlgorithm... algo) {
//        for (vtkAlgorithm a : algo) {
//            a.RemoveAllObservers();
//
//            VTKProgressMonitorWrapper progressWrapper = new VTKProgressMonitorWrapper("", a, monitor);
//            a.AddObserver("StartEvent", progressWrapper, "onStart");
//            a.AddObserver("EndEvent", progressWrapper, "onEnd");
//            a.AddObserver("ProgressEvent", progressWrapper, "onProgress");
//        }
//
//        monitor.setIndeterminate(false);
//        monitor.setTotal(100);
//        monitor.start(log, false, new Runnable() {
//            @Override
//            public void run() {
//                for (vtkAlgorithm a : algo) {
//                    a.Update();
//                }
//                monitor.end();
//            }
//        });
//    }
//
//    @Override
//    public Map<String, Actor> getActorsMap() {
//        return null;
//    }
//}
