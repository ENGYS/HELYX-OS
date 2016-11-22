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

import static eu.engys.vtk.VTKColors.WHITE;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.swing.JOptionPane;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import eu.engys.core.controller.GeometryToMesh;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.core.project.mesh.FieldItems;
import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.core.project.zero.facezones.FaceZone;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.ActorVisibilityEvent;
import eu.engys.gui.events.view3D.View3DEvent;
import eu.engys.gui.view.View3DElement;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Context;
import eu.engys.gui.view3D.Mesh3DController;
import eu.engys.gui.view3D.Mesh3DEventListener;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.gui.view3D.Representation;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.UiUtil;
import eu.engys.vtk.actors.SurfaceToActor;
import eu.engys.vtk.actors.SurfaceToActor.ActorMode;
import vtk.vtkPlane;

public class VTKMesh3DController implements Mesh3DController {

    private static final Logger logger = LoggerFactory.getLogger(Mesh3DController.class);

    private final Model model;
    private final ProgressMonitor monitor;
    private final VTKPatches patchActors;
    private final VTKCellZones cellZonesActors;
    private final VTKFaceZones faceZonesActors;
    private final VTKInternalMesh internalMeshActor;
//    private final VTKInternalMesh internalMeshActor;



    private FieldItem currentField;
    private double currentTimeStep = 0;
    private RenderPanel renderPanel;

    @Inject
    public VTKMesh3DController(Model model, ProgressMonitor monitor) {
        this.model = model;
        this.monitor = monitor;

        this.patchActors = new VTKPatches(model);
        this.cellZonesActors = new VTKCellZones(model);
        this.faceZonesActors = new VTKFaceZones(model);
//        this.internalMeshActor = new VTKInternalMesh(monitor);
        this.internalMeshActor = new VTKInternalMesh(monitor);

        this.currentField = FieldItem.indexItem();

        EventManager.registerEventListener(new Mesh3DEventListener(this), View3DEvent.class);
    }

    @Override
    public void setRenderPanel(RenderPanel renderPanel) {
        this.renderPanel = renderPanel;

        patchActors.setRenderPanel(renderPanel);
        cellZonesActors.setRenderPanel(renderPanel);
        faceZonesActors.setRenderPanel(renderPanel);
        internalMeshActor.setRenderPanel(renderPanel);

        if (renderPanel.getPickManager() != null) {
            renderPanel.getPickManager().registerPickerForActors(patchActors);
            renderPanel.getPickManager().registerPickerForActors(cellZonesActors);
            renderPanel.getPickManager().registerPickerForActors(faceZonesActors);
        }
    }

    @Override
    public void geometryToMesh(GeometryToMesh g2m) {
        clear();
        clearContext();
        removeActorsFromRenderer();

        SurfaceToActor surfaceToActor = new SurfaceToActor(ActorMode.VIRTUALISED, computeBoundingBox(), monitor);

        for (Surface surface : g2m.getWillBePatches()) {
            Actor[] actors = surfaceToActor.toActor(surface);
            for (Actor a : actors) {
                String actorName = g2m.getPatchName(surface);
                a.rename(actorName);
                a.setVisibility(true);
                patchActors.addActorToPatches(a);
            }
        }
        for (Surface surface : g2m.getWillBeCellZones()) {
            Actor[] actors = surfaceToActor.toActor(surface);
            if (actors.length == 1) {
                String zoneName = g2m.getCellZoneName(surface);
                actors[0].rename(zoneName);
                actors[0].setVisibility(true);
                cellZonesActors.addActorToZones(actors[0]);
            } else {
                for (Actor a : actors) {
                    a.setVisibility(true);
                    cellZonesActors.addActorToZones(a);
                }
            }
        }
        for (Surface surface : g2m.getWillBeFaceZones()) {
            Actor[] actors = surfaceToActor.toActor(surface);
            if (actors.length == 1) {
                String zoneName = g2m.getFaceZoneName(surface);
                actors[0].rename(zoneName);
                actors[0].setVisibility(true);
                faceZonesActors.addActorToZones(actors[0]);
            } else {
                for (Actor a : actors) {
                    a.setVisibility(true);
                    faceZonesActors.addActorToZones(a);
                }
            }
        }
        VTKColors.indexedColor().to(patchActors).to(cellZonesActors).to(faceZonesActors).apply();
    }

    @Override
    public void loadActors() {
        _loadExternalMesh();
    }

    @Override
    public BoundingBox computeBoundingBox() {
        return VTKUtil.computeBoundingBox(patchActors.getActors());
    }

    @Override
    public boolean isInternalMeshLoaded() {
        return internalMeshActor.isLoaded();
    }

    @Override
    public boolean isInternalMeshVisible() {
        return internalMeshActor.isVisible();
    }

    @Override
    public void clear() {
        deleteActors();
        this.currentField = FieldItem.indexItem();
        this.currentTimeStep = 0;
    }

    private void deleteActors() {
        patchActors.deleteActors();
        cellZonesActors.deleteActors();
        faceZonesActors.deleteActors();
        internalMeshActor.deleteActors();
    }

    private void removeActorsFromRenderer() {
        patchActors.removeActorsFromRenderer();
        cellZonesActors.removeActorsFromRenderer();
        faceZonesActors.removeActorsFromRenderer();
        internalMeshActor.removeActorsFromRenderer();
    }

    @Override
    public void updatePatchesSelection(Patch[] selection) {
        patchActors.updateSelection(selection);
    }

    @Override
    public void updatePatchesVisibility(Patch... selection) {
        patchActors.updateVisibility(selection);
    }

    @Override
    public void updateCellZonesSelection(CellZone[] selection) {
        cellZonesActors.selectActors(selection);
    }

    @Override
    public void updateCellZonesVisibility(CellZone... selection) {
        cellZonesActors.updateVisibility(selection);
    }

    @Override
    public void updateFaceZonesSelection(FaceZone[] selection) {
        faceZonesActors.selectActors(selection);
    }

    @Override
    public void updateFaceZonesVisibility(FaceZone... selection) {
        faceZonesActors.updateVisibility(selection);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Actor> getActorsList() {
        Collection<Actor> pActors = patchActors.getActors();
        Collection<Actor> cActors = cellZonesActors.getActors();
        Collection<Actor> fActors = faceZonesActors.getActors();
        Collection<Actor> iActors = internalMeshActor.getActors();
        return CollectionUtils.union(pActors, CollectionUtils.union(cActors, CollectionUtils.union(fActors, iActors)));
    }

    @Override
    public void readTimeSteps() {
        TimeStepsReader reader = new TimeStepsReader(model, monitor);
        reader.read(currentTimeStep);
        model.getMesh().setTimeSteps(reader.getTimesteps());
    }

    @Override
    public void showField(FieldItem fieldItem) {
        this.currentField = fieldItem;
        updateActorsColors();
    }

    @Override
    public void updateActorsColors() {
        if (FieldItem.SOLID.equals(currentField.getName())) {
            setColorsToSolid();
        } else if (FieldItem.INDEXED.equals(currentField.getName())) {
            setColorsToIndexed();
        } else {
            setColorsToScalar();
        }
    }

    private void setColorsToSolid() {
        VTKColors.solidColor(WHITE).to(patchActors).to(cellZonesActors).to(faceZonesActors).to(internalMeshActor).apply();
        renderPanel.renderLater();
    }

    private void setColorsToIndexed() {
        VTKColors.indexedColor().to(patchActors).to(cellZonesActors).to(faceZonesActors).to(internalMeshActor).apply();
        VTKColors.solidColor(WHITE).to(internalMeshActor).apply();
        renderPanel.renderLater();
    }

    private void setColorsToScalar() {
        VTKColors.scalarsColor(currentField).to(patchActors).to(cellZonesActors).to(faceZonesActors).to(internalMeshActor).apply();
        renderPanel.renderLater();
    }

    @Override
    public void changeTimeStep(final double currentTimeStep) {
        this.currentTimeStep = currentTimeStep;
        monitor.start("Loading values for timestep " + currentTimeStep, false, new Runnable() {
            @Override
            public void run() {
                _changeTimeStep(currentTimeStep);
                fixFieldItemSelection();
                monitor.end();
            }
        });
    }

    private void _changeTimeStep(double currentTimeStep) {
        renderPanel.DestroyTimer();
        
        File baseDir = model.getProject().getBaseDir();
        boolean parallel = model.getProject().isParallel();
        
        ExternalMeshReader eReader = new ExternalMeshReader(baseDir, parallel, monitor);
        eReader.read(currentTimeStep);

        renderPanel.lock();
        patchActors.update(Lists.newArrayList(eReader.getPatchesDataset().values()));
        cellZonesActors.update(eReader.getCellZonesDataset());
//        faceZonesActors.update(eReader.getFaceZonesDataset());
        renderPanel.unlock();

        model.getMesh().getExternalMesh().setBounds(eReader.getBounds());
        model.getMesh().setTimeSteps(eReader.getTimesteps());
        
        model.getMesh().getExternalMesh().getFieldItems().addFieldItems(eReader.getFieldItems());

        eReader.clear();

        if (internalMeshActor.isLoaded()) {
            InternalMeshReader iReader = new InternalMeshReader(baseDir, parallel, monitor);
            iReader.read(currentTimeStep);

            renderPanel.lock();
            internalMeshActor.update(iReader.getInternalMeshDataset());
            renderPanel.unlock();
            model.getMesh().getInternalMesh().getFieldItems().addFieldItems(iReader.getFieldItems());
            iReader.clear();
        }
        
        VTKUtil.gc(true);
    }

    @Override
    public void showExternalMesh() {
        loadExternalMesh();
        if (patchActors.isLoaded()) {
            showAllActors();
        }
    }

    private void loadExternalMesh() {
        monitor.start("Loading mesh", false, new Runnable() {
            @Override
            public void run() {
                if (!patchActors.isLoaded()) {
                    _loadExternalMesh();
                    monitor.end();
                }
            }
        });
    }

    private void _loadExternalMesh() {
        File baseDir = model.getProject().getBaseDir();
        boolean parallel = model.getProject().isParallel();
        
        ExternalMeshReader reader = new ExternalMeshReader(baseDir, parallel, monitor);
        reader.read(0);

        monitor.info("-> Patches");
        patchActors.load(reader.getPatchesDataset());

        monitor.info("-> Cell Zones");
        cellZonesActors.load(reader.getCellZonesDataset());

//        monitor.info("-> Face Zones");
//        faceZonesActors.load(reader.getFaceZonesDataset());
        
        cellZonesActors.VisibilityOff();
        faceZonesActors.VisibilityOff();

        model.getMesh().getExternalMesh().setBounds(reader.getBounds());
        model.getMesh().setTimeSteps(reader.getTimesteps());
        model.getMesh().getExternalMesh().getFieldItems().addFieldItems(reader.getFieldItems());

        VTKColors.indexedColor().to(patchActors).to(cellZonesActors).to(faceZonesActors).apply();

        reader.clear();
    }

    @Override
    public void showInternalMesh() {
        if (model.getPatches().isEmpty()) {
            JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "No mesh", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        loadInternalMesh();
        if (internalMeshActor.isLoaded()) {
            hideAllActorsShowInternalMesh();
            internalMeshActor.show();

            fixFieldItemSelection();
        }
    }

    private void loadInternalMesh() {
        if (!internalMeshActor.isLoaded()) {
            monitor.start("Loading internal mesh", false, new Runnable() {
                @Override
                public void run() {
                    _loadInternalMesh();
                    monitor.end();
                }
            });
        }
    }

    private void _loadInternalMesh() {
        File baseDir = model.getProject().getBaseDir();
        boolean parallel = model.getProject().isParallel();
        
        InternalMeshReader reader = new InternalMeshReader(baseDir, parallel, monitor);
        reader.read(currentTimeStep);

        monitor.info("-> Internal Mesh Actor");
        internalMeshActor.load(reader.getInternalMeshDataset());
        model.getMesh().getInternalMesh().getFieldItems().addFieldItems(reader.getFieldItems());

        reader.clear();

        VTKUtil.gc(true);
    }

    private void hideAllActorsShowInternalMesh() {
        patchActors.VisibilityOff();
        cellZonesActors.VisibilityOff();
        faceZonesActors.VisibilityOff();
        internalMeshActor.VisibilityOn();

        EventManager.triggerEvent(this, new ActorVisibilityEvent(false));
    }

    @Override
    public void hideInternalMesh() {
        if (internalMeshActor.isLoaded()) {
            internalMeshActor.disconnectFilters();
            showAllActorsHideInternalMesh();

            fixFieldItemSelection();
        }
    }

    private void showAllActorsHideInternalMesh() {
        patchActors.VisibilityOn();
//        faceZonesActors.VisibilityOn();
//        cellZonesActors.VisibilityOn();
        faceZonesActors.VisibilityOff();
        cellZonesActors.VisibilityOff();
        internalMeshActor.VisibilityOff();

        EventManager.triggerEvent(this, new ActorVisibilityEvent(true));
    }

    private void fixFieldItemSelection() {
        if (currentField != null) {
            if (isInternalMeshVisible()) {
                FieldItems items = model.getMesh().getInternalMesh().getFieldItems();
                showField(items.getEquivalentFieldItemOf(currentField));
            } else {
                FieldItems items = model.getMesh().getExternalMesh().getFieldItems();
                showField(items.getEquivalentFieldItemOf(currentField));
            }
        } else {
            setColorsToSolid();
        }
    }
    
    @Override
    public void disconnectFiltersFromInternalMesh() {
        if (internalMeshActor.isLoaded()) {
            internalMeshActor.disconnectFilters();
        }
    }

    @Override
    public FieldItem getCurrentFieldItem() {
        return currentField;
    }

    @Override
    public double getCurrentTimeStep() {
        return currentTimeStep;
    }

    @Override
    public void clip(vtkPlane plane, boolean insideOut) {
        if (internalMeshActor.isLoaded()) {
            internalMeshActor.clip(plane, insideOut);
        }
    }

    @Override
    public void crinkle(vtkPlane plane) {
        if (internalMeshActor.isLoaded()) {
            internalMeshActor.crinkle(plane);
        }
    }

    @Override
    public void slice(vtkPlane plane, List<Double> values) {
        if (internalMeshActor.isLoaded()) {
            internalMeshActor.slice(plane, values);
        }
    }

    @Override
    public void contour(String field, List<Double> values, int smoothingIterations, double smoothingConvergence) {
        if (internalMeshActor.isLoaded()) {
            internalMeshActor.smoothedContour(field, values, smoothingIterations, smoothingConvergence);
        }
    }

    @Override
    public void exportContourAsSTL(File stlFile) {
        if (internalMeshActor.isLoaded()) {
            internalMeshActor.exportSmoothedContourAsSTL(stlFile);
        }
    }

    @Override
    public void render() {
        renderPanel.renderLater();
    }

    @Override
    public void zoomReset() {
        renderPanel.resetZoomLater();
    }

    /*
     * CONTEXT
     */
    private HashMap<Class<? extends View3DElement>, MeshContext> contextMap = new HashMap<>();
    private MeshContext context;

    private boolean allowSelection;

    @Override
    public Context getCurrentContext() {
        return context;
    }

    @Override
    public void applyContext(Class<? extends View3DElement> klass) {
        context = contextMap.get(klass);
        removeActorsFromRenderer();
        if (context != null) {
            logger.info("[APPLY CONTEXT] for {} is {}", klass.getSimpleName(), context);
            renderPanel.setRepresentation(context.getRepresentation());
            this.allowSelection = context.isAllowSelection();
            patchActors.addActorsMap(context.getPatches(), context.getPatchesVisibility());
            faceZonesActors.addActorsMap(context.getFaceZones(), context.getFaceZonesVisibility());
            cellZonesActors.addActorsMap(context.getCellZones(), context.getCellZonesVisibility());
            internalMeshActor.addActorsMap(context.getInternalMesh(), context.getInternalMeshVisibility());
        } else {
            logger.info("[APPLY CONTEXT] for {} is NOT FOUND", klass.getSimpleName());
        }
    }

    @Override
    public void newContext(Class<? extends View3DElement> klass) {
        MeshContext context = new MeshContext();
        context.setRepresentation(Representation.SURFACE);
        context.setAllowSelection(true);
        context.setPatches(patchActors.getActorsMap());
        context.setCellZones(cellZonesActors.getActorsMap());
        context.setFaceZones(faceZonesActors.getActorsMap());
        context.setInternalMesh(internalMeshActor.getActorsMap());
        
        contextMap.put(klass, context);
        logger.info("[NEW CONTEXT] for {} is {}", klass.getSimpleName(), context);
    }

    @Override
    public void newEmptyContext(Class<? extends View3DElement> klass) {
        MeshContext context = new MeshContext();
        context.setRepresentation(Representation.SURFACE);
        context.setAllowSelection(true);
        context.setPatches(Collections.<String, Actor> emptyMap());
        context.setCellZones(Collections.<String, Actor> emptyMap());
        context.setFaceZones(Collections.<String, Actor> emptyMap());
        context.setInternalMesh(Collections.<String, Actor> emptyMap());
        contextMap.put(klass, context);
        logger.info("[EMPTY CONTEXT] for {} is {}", klass.getSimpleName(), context);
    }

    @Override
    public void dumpContext(Class<? extends View3DElement> klass) {
        if (contextMap.containsKey(klass)) {
            contextMap.remove(klass).clear();
        }
        MeshContext context = new MeshContext();
        context.setRepresentation(renderPanel.getRepresentation());
        context.setAllowSelection(allowSelection);
        context.setPatches(patchActors.getActorsMap());
        context.setCellZones(cellZonesActors.getActorsMap());
        context.setFaceZones(faceZonesActors.getActorsMap());
        context.setInternalMesh(internalMeshActor.getActorsMap());
        contextMap.put(klass, context);
        logger.info("[DUMP CONTEXT] for {} is {}", klass.getSimpleName(), context);
    }

    @Override
    public void clearContext() {
        logger.info("[CLEAR CONTEXT]");
        for (MeshContext context : contextMap.values()) {
            context.clear();
        }
        contextMap.clear();
    }

    public void showAllActors() {
        patchActors.addActorsToRenderer();
        cellZonesActors.addActorsToRenderer();
        faceZonesActors.addActorsToRenderer();
    }

}
