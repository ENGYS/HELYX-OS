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

package eu.engys.vtk;

import static eu.engys.core.project.mesh.ScalarBarType.BLUE_TO_RED_RAINBOW;
import static eu.engys.vtk.VTKColors.WHITE;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import javax.inject.Inject;
import javax.swing.JOptionPane;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vtk.vtkPlane;
import eu.engys.core.Arguments;
import eu.engys.core.controller.GeometryToMesh;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.core.project.mesh.ScalarBarType;
import eu.engys.core.project.zero.cellzones.CellZone;
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
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.vtk.actors.SurfaceToActor;
import eu.engys.vtk.actors.SurfaceToActor.ActorMode;

public class VTKMesh3DController implements Mesh3DController {

    private static final Logger logger = LoggerFactory.getLogger(Mesh3DController.class);

    private final Model model;
    private final ProgressMonitor monitor;
    private final VTKPatches patchActors;
    private final VTKCellZones cellZonesActors;
    private final VTKInternalMesh internalMeshActor;

    private FieldItem currentField = null;
    private double currentTimeStep = 0;
    private RenderPanel renderPanel;

    @Inject
    public VTKMesh3DController(Model model, ProgressMonitor monitor) {
        this.model = model;
        this.monitor = monitor;

        this.patchActors = new VTKPatches(model);
        this.cellZonesActors = new VTKCellZones(model, monitor);
        this.internalMeshActor = new VTKInternalMesh(monitor);

        EventManager.registerEventListener(new Mesh3DEventListener(this), View3DEvent.class);
    }

    @Override
    public void setRenderPanel(RenderPanel renderPanel) {
        this.renderPanel = renderPanel;

        patchActors.setRenderPanel(renderPanel);
        cellZonesActors.setRenderPanel(renderPanel);
        internalMeshActor.setRenderPanel(renderPanel);

        if (renderPanel.getPickManager() != null) {
            renderPanel.getPickManager().registerPickerForActors(patchActors);
            renderPanel.getPickManager().registerPickerForActors(cellZonesActors);
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
                patchActors.addActorToPatches(a);
            }
        }
        for (Surface surface : g2m.getWillBeCellZones()) {
            Actor[] actors = surfaceToActor.toActor(surface);
            if (actors.length == 1) {
                String zoneName = g2m.getCellZoneName(surface);
                actors[0].rename(zoneName);
                cellZonesActors.addActorToZones(actors[0]);
            } else {
                for (Actor a : actors) {
                    cellZonesActors.addActorToZones(a);
                }
            }
        }

        VTKColors.indexedColor().to(patchActors).to(cellZonesActors).apply();
    }

    @Override
    public void loadActors() {
        if (Arguments.load3Dmesh) {
            _loadExternalMesh();
        }
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
    public void clear() {
        deleteActors();
        this.currentField = null;
        this.currentTimeStep = 0;
    }

    private void deleteActors() {
        patchActors.deleteActors();
        cellZonesActors.deleteActors();
        internalMeshActor.deleteActors();
    }

    private void removeActorsFromRenderer() {
        patchActors.removeActorsFromRenderer();
        cellZonesActors.removeActorsFromRenderer();
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
    @SuppressWarnings("unchecked")
    public Collection<Actor> getActorsList() {
        return CollectionUtils.union(patchActors.getActors(), CollectionUtils.union(cellZonesActors.getActors(), internalMeshActor.getActors()));
    }

    @Override
    public void readTimeSteps() {
        VTKOpenFOAMDataset dataset = new VTKOpenFOAMDataset(model, null);
        dataset.loadInformations(currentTimeStep);
        dataset.clear();
    }

    @Override
    public void showField(FieldItem fieldItem) {
        this.currentField = fieldItem;
        if (FieldItem.SOLID.equals(fieldItem.getName())) {
            setColorsToSolid();
        } else if (FieldItem.INDEXED.equals(fieldItem.getName())) {
            setColorsToIndexed();
        } else {
            setColorsToScalar();
        }
    }

    private void setColorsToSolid() {
        VTKColors.solidColor(WHITE).to(patchActors).to(cellZonesActors).to(internalMeshActor).apply();
        renderPanel.renderLater();
    }

    private void setColorsToIndexed() {
        VTKColors.indexedColor().to(patchActors).to(cellZonesActors).apply();
        VTKColors.solidColor(WHITE).to(internalMeshActor).apply();
        renderPanel.renderLater();
    }

    private void setColorsToScalar() {
        VTKColors.scalarsColor(currentField).to(patchActors).to(cellZonesActors).to(internalMeshActor).apply();
        renderPanel.renderLater();
    }

    @Override
    public void showTimeStep(final double currentTimeStep) {
        this.currentTimeStep = currentTimeStep;
        monitor.start("Loading values for timestep " + currentTimeStep, false, new Runnable() {
            @Override
            public void run() {
                final VTKOpenFOAMDataset dataset = new VTKOpenFOAMDataset(model, monitor);
                dataset.loadTimeStep(currentTimeStep);

                ExecUtil.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        patchActors.update(dataset.getPatchesDataset());
                        cellZonesActors.update(dataset.getCellZonesDataset());
                        internalMeshActor.update(dataset.getInternalMeshDataset());
                        dataset.clear();
                    }
                });
                monitor.end();
            }
        });

        if (currentField != null && currentField.isScalar()) {
            setColorsToScalar();
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

    // RESOLUTION
    @Override
    public void setScalarsActorsResolution(int resolution) {
        currentField.setResolution(resolution);
        setColorsToScalar();
    }

    // SCALARBAR TYPE
    @Override
    public void setScalarsBarType(ScalarBarType scalarBarType) {
        currentField.setScalarBarType(scalarBarType);
        setColorsToScalar();
    }

    // RANGE
    @Override
    public void setAutomaticRangeCalculation(boolean autoRange) {
        currentField.setAutomaticRange(autoRange);
        setColorsToScalar();
    }

    @Override
    public void setManualRangeCalculation(double[] range) {
        currentField.setRange(range);
        setColorsToScalar();
    }

    // RESET
    @Override
    public void resetScalarsActorsRangeAndResolutionAndHue() {
        currentField.setAutomaticRange(true);
        currentField.setResolution(FieldItem.DEFAULT_RESOLUTION);
        currentField.setScalarBarType(BLUE_TO_RED_RAINBOW);

        setColorsToScalar();
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
                _loadExternalMesh();
                monitor.end();
            }
        });
    }

    private void _loadExternalMesh() {
        if (!patchActors.isLoaded()) {
            VTKOpenFOAMDataset dataset = new VTKOpenFOAMDataset(model, monitor);
            dataset.loadExternalMesh(0);

            monitor.info("-> Patches");
            patchActors.load(dataset.getPatchesDataset());

            monitor.info("-> Cell Zones");
            cellZonesActors.load(dataset.getCellZonesDataset());

            VTKColors.indexedColor().to(patchActors).to(cellZonesActors).apply();

            dataset.clear();
        }
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
            if (currentField != null) {
                showField(currentField);
            } else {
                VTKColors.solidColor(WHITE).to(internalMeshActor).apply();
            }
        }
    }

    private void loadInternalMesh() {
        if (!internalMeshActor.isLoaded()) {
            monitor.start("Loading internal mesh", false, new Runnable() {
                @Override
                public void run() {
                    VTKOpenFOAMDataset dataset = new VTKOpenFOAMDataset(model, monitor);
                    dataset.loadInternalMesh(currentTimeStep);

                    monitor.info("-> Internal Mesh Actor");
                    internalMeshActor.load(dataset.getInternalMeshDataset());

                    dataset.clear();
                    monitor.end();
                }
            });
        }
    }

    private void hideAllActorsShowInternalMesh() {
        cellZonesActors.VisibilityOff();
        patchActors.VisibilityOff();
        internalMeshActor.VisibilityOn();

        EventManager.triggerEvent(this, new ActorVisibilityEvent(false));
    }

    @Override
    public void hideInternalMesh() {
        if (internalMeshActor.isLoaded()) {
            internalMeshActor.disconnectFilters();
            showAllActorsHideInternalMesh();
        }
    }

    private void showAllActorsHideInternalMesh() {
        cellZonesActors.VisibilityOff();
        patchActors.VisibilityOn();
        internalMeshActor.VisibilityOff();

        EventManager.triggerEvent(this, new ActorVisibilityEvent(true));
    }

    @Override
    public void clip(vtkPlane plane) {
        if (internalMeshActor.isLoaded()) {
            internalMeshActor.clip(plane);
        }
    }

    @Override
    public void crinkle(vtkPlane plane) {
        if (internalMeshActor.isLoaded()) {
            internalMeshActor.crinkle(plane);
        }
    }

    @Override
    public void slice(vtkPlane plane) {
        if (internalMeshActor.isLoaded()) {
            internalMeshActor.slice(plane);
        }
    }

    @Override
    public void insideOut(boolean selected) {
        if (internalMeshActor.isLoaded()) {
            internalMeshActor.insideOut(selected);
        }
    }

    @Override
    public void disconnectFiltersFromInternalMesh() {
        if (internalMeshActor.isLoaded()) {
            internalMeshActor.disconnectFilters();
            render();
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
            cellZonesActors.addCellZonesMap(context.getCellzones(), context.getZonesVisibility());
            patchActors.addPatchMap(context.getPatches(), context.getPatchesVisibility());
        } else {
            logger.info("[APPLY CONTEXT] for {} is NOT FOUND", klass.getSimpleName());
        }
    }

    @Override
    public void newContext(Class<? extends View3DElement> klass) {
        MeshContext context = new MeshContext(Representation.SURFACE, true, cellZonesActors.getActorsMap(), patchActors.getActorsMap());
        contextMap.put(klass, context);
        logger.info("[NEW CONTEXT] for {} is {}", klass.getSimpleName(), context);
    }

    @Override
    public void newEmptyContext(Class<? extends View3DElement> klass) {
        MeshContext context = new MeshContext(Representation.SURFACE, true, Collections.<String, Actor> emptyMap(), Collections.<String, Actor> emptyMap());
        contextMap.put(klass, context);
        logger.info("[EMPTY CONTEXT] for {} is {}", klass.getSimpleName(), context);
    }

    @Override
    public void dumpContext(Class<? extends View3DElement> klass) {
        if (contextMap.containsKey(klass)) {
            contextMap.remove(klass).clear();
        }
        MeshContext context = new MeshContext(renderPanel.getRepresentation(), allowSelection, cellZonesActors.getActorsMap(), patchActors.getActorsMap());
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
    }

}
