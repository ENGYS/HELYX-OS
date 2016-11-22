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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.vtk.actors.InternalMeshActor;
import vtk.vtkAlgorithm;
import vtk.vtkContourFilter;
import vtk.vtkCutter;
import vtk.vtkExtractGeometry;
import vtk.vtkPlane;
import vtk.vtkPolyData;
import vtk.vtkSTLWriter;
import vtk.vtkSmoothPolyDataFilter;
import vtk.vtkTableBasedClipDataSet;
import vtk.vtkUnstructuredGrid;

public class VTKInternalMesh implements VTKActors {

    class Clip {

        private vtkPlane plane;
        private boolean insideOut;

        public Clip(vtkPlane plane, boolean insideOut) {
            this.plane = plane;
            this.insideOut = insideOut;
        }
        
        public void update(vtkUnstructuredGrid internalMeshDataset) {
            vtkTableBasedClipDataSet clipper = new vtkTableBasedClipDataSet();
            clipper.SetInputData(internalMeshDataset);
            clipper.SetClipFunction(plane);
            if (insideOut) {
                clipper.InsideOutOn();
            } else {
                clipper.InsideOutOff();
            }
            
            updateInAThread(UPDATING_CLIPPER, clipper);

            vtkUnstructuredGrid output = clipper.GetOutput();
            VTKUtil.printTotalMemory(output);
            
//            deleteActor();
//            VTKUtil.gc(false);
            
//            VTKInternalMeshNew.this.actor = new InternalMeshActor(output);

            actor.interactiveOff();
            actor.setInput(output);
            
            output.Delete();
            clipper.Delete();
        }

        public void delete() {
//            clipper.RemoveAllInputs();
//            clipper.Delete();            
        }
    }

    class Slice {

        private vtkPlane plane;
        private List<Double> values;

        public Slice(vtkPlane plane, List<Double> values) {
            this.plane = plane;
            this.values = values;
        }
        
        public void update(vtkUnstructuredGrid internalMeshDataset) {
            vtkCutter slicer = new vtkCutter();
            slicer.SetInputData(internalMeshDataset);
            slicer.GenerateTrianglesOff();
            slicer.SetCutFunction(plane);

//            slicer.SetNumberOfContours(values.size());
//            for (int i = 0; i < values.size(); i++) {
//                slicer.SetValue(i, values.get(i));
//            }

            updateInAThread(UPDATING_SLICER, slicer);

            vtkPolyData output = slicer.GetOutput();
            VTKUtil.printTotalMemory(output);

            actor.interactiveOff();
            actor.setInput(slicer.GetOutput());
            
            output.Delete();
            slicer.Delete();
        }

        public void delete() {
//
//            slicer.RemoveAllInputs();
//            slicer.Delete();            
        }
        
    }
    class Crinkle {
        
        private vtkPlane plane;

        public Crinkle(vtkPlane plane) {
            this.plane = plane;
        }
        
        public void update(vtkUnstructuredGrid internalMeshDataset) {
            vtkExtractGeometry crinkle = new vtkExtractGeometry();
            crinkle.SetInputData(internalMeshDataset);
            crinkle.ExtractInsideOn();
            crinkle.ExtractOnlyBoundaryCellsOn();
            crinkle.ExtractBoundaryCellsOn();
            crinkle.SetImplicitFunction(plane);

            updateInAThread(UPDATING_CRINKLE, crinkle);

            vtkUnstructuredGrid output = crinkle.GetOutput();
            VTKUtil.printTotalMemory(output);
            
            actor.interactiveOff();
            actor.setInput(output);
            
            output.Delete();
            crinkle.Delete();
        }

        public void delete() {
//            crinkle.RemoveAllInputs();
//            crinkle.Delete();
        }
    }
    
    private static final Logger logger = LoggerFactory.getLogger(VTKInternalMesh.class);

    private static final String UPDATING_CONTOUR_AND_SMOOTH = "Updating contour and smooth";
    private static final String UPDATING_CRINKLE = "Updating crinkle";
    private static final String UPDATING_CLIPPER = "Updating clipper";
    private static final String UPDATING_SLICER = "Updating slicer";
    private InternalMeshActor actor;
    private RenderPanel renderPanel;

    private Clip clipper;
    private Slice slicer;
    private Crinkle crinkle;
    
//    private vtkCutter slicer;
//    private vtkExtractGeometry crinkle;
    private vtkContourFilter contour;
    private vtkSmoothPolyDataFilter smooth;

    private vtkUnstructuredGrid internalMeshDataset;
    private ProgressMonitor monitor;

    public VTKInternalMesh(ProgressMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void setRenderPanel(RenderPanel renderPanel) {
        this.renderPanel = renderPanel;
    }

    public void update(final vtkUnstructuredGrid dataSet) {
        VTKUtil.printTotalMemory(dataSet);
        if (actor != null) {
//            hide();
            
//            deleteActor();
            deleteDataSet();
            
            VTKUtil.gc(false);
            
            this.internalMeshDataset = VTKUtil.shallowCopy(dataSet);
//            this.actor = new InternalMeshActor(new vtkUnstructuredGrid());
            
            if (slicer != null) {
                slicer.update(internalMeshDataset);
            } else if (clipper != null) {
                clipper.update(internalMeshDataset);
            } else if (crinkle != null) {
                crinkle.update(internalMeshDataset);
            } else if (contour != null && smooth != null) {
                contour.SetInputData(internalMeshDataset);
                updateInAThread(UPDATING_CONTOUR_AND_SMOOTH, contour, smooth);
                connectActorToSmoothedContour();
            } else {
                actor.setInput(internalMeshDataset);
            }
            
//            show();
            
            logger.debug("Update internalMesh");
        }
    }

    public void load(vtkUnstructuredGrid dataSet) {
        VTKUtil.printTotalMemory(dataSet);

        deleteActor();
        deleteDataSet();
        
        VTKUtil.gc(false);
        
        this.internalMeshDataset = VTKUtil.shallowCopy(dataSet);
        this.actor = new InternalMeshActor(internalMeshDataset);
    }

    private void deleteDataSet() {
        if (this.internalMeshDataset != null) {
            VTKUtil.deleteDataset(internalMeshDataset);
            this.internalMeshDataset = null;
        }
    }

    private void deleteActor() {
        if (this.actor != null) {
            this.actor.deleteActor();
            this.actor = null;
        }
    }
    
    public void deleteActors() {
        if (actor != null) {
            renderPanel.removeActor(actor);
            actor.deleteActor();
            actor = null;

            deleteClipper();
            deleteSlicer();
            deleteCrinkle();
            deleteContourAndSmooth();
        }
    }

    public void removeActorsFromRenderer() {
        if (actor != null) {
            renderPanel.removeActor(actor);
        }
    }

    private void deleteSlicer() {
        if (slicer != null) {
            slicer.delete();
            slicer = null;
        }
    }

    private void deleteClipper() {
        if (clipper != null) {
            clipper.delete();
            clipper = null;
        }
    }

    private void deleteCrinkle() {
        if (crinkle != null) {
            crinkle.delete();
            crinkle = null;
        }
    }

    private void deleteContourAndSmooth() {
        if (contour != null) {
            contour.RemoveAllInputs();
            contour.Delete();
            contour = null;
        }
        if (smooth != null) {
            smooth.RemoveAllInputs();
            smooth.Delete();
            smooth = null;
        }
    }

    public void VisibilityOff() {
        if (actor != null) {
            actor.setVisibility(false);
        }
    }

    public boolean isVisible() {
        return actor != null && actor.getVisibility();
    }

    public void VisibilityOn() {
        if (actor != null) {
            actor.setVisibility(true);
        }
    }

    public void disconnectFilters() {
        deleteClipper();
        deleteSlicer();
        deleteCrinkle();
        deleteContourAndSmooth();

        if (actor != null) {
            actor.setInput(internalMeshDataset);
        }
    }

    public boolean isLoaded() {
        return actor != null;
    }

    public void hide() {
        renderPanel.removeActor(actor);
    }

    public void show() {
        actor.VisibilityOn();
        renderPanel.addActor(actor);
    }

    @Override
    public Collection<Actor> getActors() {
        return actor != null ? Arrays.<Actor>asList(actor) : Collections.<Actor> emptyList();
    }

    @Override
    public boolean containsActor(Actor pickedActor) {
        return false;
    }

    public void clip(vtkPlane plane, boolean insideOut) {
//        hide();

        deleteClipper();
        deleteSlicer();
        deleteCrinkle();

        clipper = new Clip(plane, insideOut);
        clipper.update(internalMeshDataset);
        
//        show();
        renderPanel.renderLater();
    }

    public void slice(vtkPlane plane, List<Double> values) {
        deleteClipper();
        deleteSlicer();
        deleteCrinkle();

        slicer = new Slice(plane, values);
        slicer.update(internalMeshDataset);
        
        renderPanel.renderLater();
    }

    void crinkle(vtkPlane plane) {
        deleteClipper();
        deleteSlicer();
        deleteCrinkle();

        crinkle = new Crinkle(plane);
        crinkle.update(internalMeshDataset);
        
        renderPanel.renderLater();
    }

    void smoothedContour(final String field, final List<Double> values, final int smoothingIterations, final double smoothingConvergence) {
        deleteContourAndSmooth();

        contour = new vtkContourFilter();
        contour.SetInputData(internalMeshDataset);
        contour.ComputeScalarsOff();
        contour.ComputeNormalsOff();
        contour.GenerateTrianglesOn();
        contour.SetInputArrayToProcess(0, 0, 0, "vtkDataObject::FIELD_ASSOCIATION_POINTS", field);
        contour.SetNumberOfContours(values.size());
        for (int i = 0; i < values.size(); i++) {
            contour.SetValue(i, values.get(i));
        }

        smooth = new vtkSmoothPolyDataFilter();
        smooth.SetInputData(contour.GetOutput());
        smooth.SetNumberOfIterations(smoothingIterations);
        smooth.SetConvergence(smoothingConvergence);

        updateInAThread(UPDATING_CONTOUR_AND_SMOOTH, contour, smooth);

        connectActorToSmoothedContour();

        renderPanel.renderLater();

        VTKUtil.gc(false);
    }

    void exportSmoothedContourAsSTL(final File stlFile) {
        vtkSTLWriter writer = new vtkSTLWriter();
        writer.SetFileName(stlFile.getAbsolutePath());
        writer.SetInputData(smooth.GetOutput());

        writeSTLInAThread("Saving STL file " + stlFile.getAbsolutePath(), writer);
        writer.Delete();
    }

    private void writeSTLInAThread(String log, final vtkSTLWriter writer) {
        writer.RemoveAllObservers();
        VTKProgressMonitorWrapper progressWrapper = new VTKProgressMonitorWrapper("", writer, monitor);
        writer.AddObserver("StartEvent", progressWrapper, "onStart");
        writer.AddObserver("EndEvent", progressWrapper, "onEnd");
        writer.AddObserver("ProgressEvent", progressWrapper, "onProgress");

        monitor.setIndeterminate(false);
        monitor.setTotal(100);
        monitor.start(log, false, new Runnable() {
            @Override
            public void run() {
                writer.Write();
                monitor.end();
            }
        });
    }

    private void connectActorToSmoothedContour() {
        actor.interactiveOff();
        actor.setInput(smooth.GetOutput());
    }

    private void updateInAThread(String log, final vtkAlgorithm... algo) {
        for (vtkAlgorithm a : algo) {
            a.RemoveAllObservers();

            VTKProgressMonitorWrapper progressWrapper = new VTKProgressMonitorWrapper("", a, monitor);
            a.AddObserver("StartEvent", progressWrapper, "onStart");
            a.AddObserver("EndEvent", progressWrapper, "onEnd");
            a.AddObserver("ProgressEvent", progressWrapper, "onProgress");
        }

        if (monitor.isFinished()) {
            monitor.setIndeterminate(false);
            monitor.setTotal(100);
            monitor.start(log, false, new Runnable() {
                @Override
                public void run() {
                    for (vtkAlgorithm a : algo) {
                        a.Update();
                    }
                    monitor.end();
                }
            });
        } else {
            for (vtkAlgorithm a : algo) {
                a.Update();
            }
        }
    }

    @Override
    public Map<String, Actor> getActorsMap() {
        Map<String, Actor> map = new HashMap<>();
        if (isLoaded()) {
            map.put("internalMesh", actor);
        }
        return map;
    }

    @Override
    public void addActorsMap(Map<String, Actor> map, Map<String, Boolean> visibility) {
        if (map.size() > 0) {
            this.actor = (InternalMeshActor) map.get("internalMesh");
            actor.setVisibility(visibility.get("internalMesh"));
            renderPanel.addActor(actor);
        }
    }
}
