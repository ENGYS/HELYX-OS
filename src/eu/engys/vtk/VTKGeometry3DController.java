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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.Arguments;
import eu.engys.core.controller.GeometryToMesh;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.geometry.Geometry;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;
import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.core.project.geometry.surface.Plane;
import eu.engys.core.project.geometry.surface.Region;
import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.View3DEvent;
import eu.engys.gui.view.View3DElement;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Context;
import eu.engys.gui.view3D.Geometry3DController;
import eu.engys.gui.view3D.Geometry3DEventListener;
import eu.engys.gui.view3D.Picker;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.gui.view3D.Representation;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.checkboxtree.VisibleItem;
import eu.engys.vtk.actors.SurfaceToActor;
import eu.engys.vtk.actors.SurfaceToActor.ActorMode;

public class VTKGeometry3DController implements Geometry3DController, Picker {

    private static final Logger logger = LoggerFactory.getLogger(VTKGeometry3DController.class);

    protected RenderPanel renderPanel;
    private final Model model;
    private final ProgressMonitor monitor;
    private final ActorsMap actorsMap = new ActorsMap();

    private boolean isLoading;

    @Inject
    public VTKGeometry3DController(Model model, ProgressMonitor monitor) {
        this.model = model;
        this.monitor = monitor;

        EventManager.registerEventListener(new Geometry3DEventListener(this), View3DEvent.class);
    }

    @Override
    public void setRenderPanel(RenderPanel renderPanel) {
        this.renderPanel = renderPanel;
        if (renderPanel.getPickManager() != null) {
            renderPanel.getPickManager().registerPickerForActors(this);
        }
    }

    @Override
    public void loadActors() {
        if (Arguments.load3Dgeometry) {
            isLoading = true;

            Geometry geometry = model.getGeometry();

            monitor.info("-> Actors");
            _addSurfaces(geometry.getSurfaces());
            _addSurfaces(geometry.getLines().toArray());

            if (geometry.hasBlock()) {
                _addSurfaces(geometry.getBlock());
            }

            isLoading = false;
        }
    }

    @Override
    public void addSurfaces(Surface... surfaces) {
        _addSurfaces(surfaces);
        updatePlaneActors();
    }

    protected void _addSurfaces(Surface... surfaces) {
        BoundingBox bb = computeBoundingBox(new Surface[0]);
        SurfaceToActor surfaceToActor = new SurfaceToActor(ActorMode.DEFAULT, bb, monitor);
        for (Surface surface : surfaces) {
            Actor[] actors = surfaceToActor.toActor(surface);
            for (Actor a : actors) {
                addActor(a);
            }
        }
    }

    private void addActor(Actor actor) {
        Surface surface = (Surface) actor.getVisibleItem();
        logger.debug("[ADD ACTOR] {} ({}) hash: {}", actor.getName(), actor.getVisibility() ? "visible" : "hidden", surface.hashCode());
        actorsMap.put(surface, actor);
        if (!isLoading) {
            renderPanel.addActor(actor);
        }
    }

    @Override
    public void removeSurfaces(Surface... surfaces) {
        _removeSurface(surfaces);
        renderPanel.renderLater();
    }

    protected void _removeSurface(Surface... surfaces) {
        for (Surface surface : surfaces) {
            if (surface.hasRegions()) {
                _removeSurface(surface.getRegions());
                continue;
            }
            logger.info("[REM SURFACE] name: {}, type: {}", surface.getName(), surface.getType());
            removeFromMap(surface);
            removeFromContext(surface);
        }
        // updatePlaneActors();
    }

    private void removeFromMap(Surface surface) {
        if (actorsMap.contains(surface)) {
            Actor oldActor = actorsMap.remove(surface);
            renderPanel.removeActor(oldActor);
        } else {
            // System.err.println("removeFromMap: " + surface.getName() + " NOT FOUND");
        }
    }

    private void removeFromContext(Surface surface) {
        for (GeometryContext context : contextMap.values()) {
            if (context.getActorsMap() != null) {
                if (context.getActorsMap().contains(surface)) {
                    logger.info("[REM SURFACE FROM CONTEXT] name: {}, context: {}", surface.getName(), context);
                    context.getActorsMap().remove(surface);
                } else {
                    // System.err.println("removeFromContext: " + surface.getName() + " NOT FOUND");
                }
            }
        }
    }

    boolean containsSurface(Surface... surfaces) {
        for (Surface surface : surfaces) {
            if (!_containsSurface(surface))
                return false;
        }
        return true;
    }

    private boolean _containsSurface(Surface surface) {
        if (surface.getType() == Type.STL) {
            Stl stl = (Stl) surface;
            for (Region region : stl.getSolids()) {
                if (!actorsMap.contains(region)) {
                    return false;
                }
            }
            return true;
        } else {
            return actorsMap.contains(surface);
        }
    }

    @Override
    public void changeSurface(Surface... surfaces) {
        _removeSurface(surfaces);
        _addSurfaces(surfaces);
    }

    @Override
    public void render() {
        renderPanel.renderLater();
    }

    @Override
    public void zoomReset() {
        renderPanel.resetZoomLater();
    }

    @Override
    public void transformSurfaces(AffineTransform t, boolean save, Surface... surfaces) {
        _transform(t, save, surfaces);
    }

    private void _transform(AffineTransform t, boolean save, Surface[] surfaces) {
        for (Surface surface : surfaces) {
            if (surface.hasRegions()) {
                _transform(t, save, surface.getRegions());
                if (save) {
                    Region region = surface.getRegions()[0];
                    surface.setTransformation(new AffineTransform(region.getTransformation()));
                }
                continue;
            }
            if (actorsMap.contains(surface)) {
                Actor actor = actorsMap.get(surface);
                actor.transformActor(save, t);
                if (save) {
                    surface.setTransformation(AffineTransform.fromVTK(actor.getUserTransform()));
                }
            }
        }
    }

    public void updateSurfacesSelection(Surface... surfaces) {
        logger.debug("[SELECTION] {} surfaces selected {}", surfaces.length, surfaces.length == 1 ? ", selection is: " + surfaces[0].getName() : "");

        List<Actor> selection = new ArrayList<Actor>();

        _updateSurfaceSelection(surfaces, selection);

        // if (selection.isEmpty()) {
        // logger.debug("updateSurfacesSelection: NONE selected!");
        // } else {
        if (!actorsMap.isEmpty()) {
            renderPanel.selectActors(false, selection.toArray(new Actor[0]));
        }
        // }
    }

    private void _updateSurfaceSelection(Surface[] surfaces, List<Actor> selection) {
        for (Surface surface : surfaces) {
            if (surface.hasRegions()) {
                _updateSurfaceSelection(surface.getRegions(), selection);
                continue;
            }
            if (surface.isVisible() && actorsMap.contains(surface)) {
                selection.add(actorsMap.get(surface));
            }
        }
    }

    @Override
    public void updateSurfaceVisibility(Surface... surfaces) {
        logger.debug("[VISIBILITY] {} selected", surfaces.length == 1 ? surfaces[0] : surfaces.length);
        for (Surface surface : surfaces) {
            if (surface.hasRegions()) {
                _updateSurfaceVisibility(surface.getRegions());
                continue;
            }
            _updateSurfaceVisibility(surface);
        }
        render();
    }

    private void _updateSurfaceVisibility(Surface... selection) {
        for (Surface surface : selection) {
            if (actorsMap.contains(surface)) {
                Actor actor = actorsMap.get(surface);
                actor.setVisibility(surface.isVisible());
            }
        }
    }

    @Override
    public void updateSurfaceColor(Color color, Surface... selection) {
        for (Surface surface : selection) {
            if (surface.hasRegions()) {
                updateSurfaceColor(color, surface.getRegions());
                continue;
            }
            _updateSurfaceColor(color, surface);
        }
    }

    private void _updateSurfaceColor(Color color, Surface surface) {
        if (getActorsMap().containsKey(surface)) {
            Actor actor = getActorsMap().get(surface);
            renderPanel.setActorColor(color, actor);
        }
    }

    @Override
    public void geometryToMesh(GeometryToMesh g2m) {
        clear();
        clearContext();
        removeActorsFromRenderer();

        loadActors();

        for (Actor actor : getActorsList()) {
            actor.setVisibility(false);
        }
    }

    @Override
    public void clear() {
        deleteActors();
    }

    private void deleteActors() {
        for (Actor actor : actorsMap.values()) {
            renderPanel.removeActor(actor);
            actor.deleteActor();
        }
        actorsMap.clear();
    }

    private void removeActorsFromRenderer() {
        for (Actor actor : actorsMap.values()) {
            renderPanel.removeActor(actor);
        }
        actorsMap.clear();
    }

    private void updatePlaneActors() {
        Surface[] surfaces = model.getGeometry().getSurfaces();
        if (hasPlanes()) {
            BoundingBox bb = VTKUtil.computeBoundingBox(getNonPlaneActorsList());
            for (Surface surface : surfaces) {
                if (surface.getType().isPlane()) {
                    double diagonal = bb.getDiagonal();
                    double value = Double.isInfinite(diagonal) ? 1 : diagonal > 0 ? diagonal : 1;

                    Plane plane = (Plane) surface;
                    plane.setDiagonal(2 * value);
                    changeSurface(plane);
                    render();
                }
            }
        }
    }

    private List<Actor> getNonPlaneActorsList() {
        List<Actor> list = new ArrayList<>();
        for (Surface surface : actorsMap.keys()) {
            if (!surface.getType().isPlane()) {
                list.add(actorsMap.get(surface));
            }
        }
        return list;
    }

    private boolean hasPlanes() {
        for (Surface surface : model.getGeometry().getSurfaces()) {
            if (surface.getType().isPlane())
                return true;
        }
        return false;
    }

    @Override
    public BoundingBox computeBoundingBox(Surface... surfaces) {
        Collection<Actor> actors;
        if (surfaces.length == 0) {
            actors = getListOfSurfaceActors(model.getGeometry().getSurfaces());
        } else {
            actors = getListOfSurfaceActors(surfaces);
        }
        return VTKUtil.computeBoundingBox(actors);
    }

    private Collection<Actor> getListOfSurfaceActors(Surface[] surfaces) {
        Collection<Actor> list = new ArrayList<>();
        for (Surface surface : surfaces) {
            if (surface.getType().isStl()) {
                Collection<Actor> l = getListOfSurfaceActors(((Stl) surface).getSolids());
                list.addAll(l);
                continue;
            } else if (surface.getType().isPlane()) {
                continue;
            }
            list.add(actorsMap.get(surface));
        }
        return list;
    }

    @Override
    public void showInternalMesh() {
        hideAllActorsShowInternalMesh();
    }

    @Override
    public void hideInternalMesh() {
        showAllActorsHideInternalMesh();
    }

    private void hideAllActorsShowInternalMesh() {
        for (Actor actor : getActorsList()) {
            actor.setVisibility(false);
        }
    }

    private void showAllActorsHideInternalMesh() {
        for (Actor actor : getActorsList()) {
            actor.setVisibility(false);
        }
    }

    @Override
    public void showField(FieldItem fieldItem) {
    }

    private Map<Class<?>, GeometryContext> contextMap = new HashMap<>();
    private GeometryContext context;

    @Override
    public Context getCurrentContext() {
        return context;
    }

    @Override
    public void applyContext(Class<? extends View3DElement> klass) {
        removeActorsFromRenderer();
        context = contextMap.get(klass);
        if (context != null) {
            logger.info("[APPLY CONTEXT] for {} is {}", klass.getSimpleName(), context);
            setRepresentationFromContext(context);
            addActorsFromContext(context);
        } else {
            logger.info("[APPLY CONTEXT] for {} is NOT FOUND", klass.getSimpleName());
        }
    }

    private void setRepresentationFromContext(GeometryContext context) {
        renderPanel.setRepresentation(context.getRepresentation());
    }

    private void addActorsFromContext(GeometryContext context) {
        ActorsMap map = context.getActorsMap();
        Map<Surface, Boolean> visibility = context.getActorsVisibility();

        if (map != null) {
            for (Surface name : map.keys()) {
                Actor actor = map.get(name);
                if (visibility.get(name).booleanValue()) {
                    actor.setVisibility(true);
                } else {
                    actor.setVisibility(false);
                }
                addActor(actor);
            }
        }
    }

    @Override
    public void newContext(Class<? extends View3DElement> klass) {
        GeometryContext context = new GeometryContext(Representation.SURFACE, getActorsMap());
        contextMap.put(klass, context);
        logger.info("[NEW CONTEXT] for {} is {}", klass.getSimpleName(), context);
    }

    @Override
    public void newEmptyContext(Class<? extends View3DElement> klass) {
        GeometryContext context = new GeometryContext(Representation.SURFACE, Collections.<Surface, Actor> emptyMap());
        contextMap.put(klass, context);
        logger.info("[EMPTY CONTEXT] for {} is {}", klass.getSimpleName(), context);
    }

    @Override
    public void dumpContext(Class<? extends View3DElement> klass) {
        if (contextMap.containsKey(klass)) {
            contextMap.remove(klass).clear();
        }
        GeometryContext context = new GeometryContext(renderPanel.getRepresentation(), actorsMap.getDelegate());
        logger.info("[DUMP CONTEXT] for {} is {}", klass.getSimpleName(), context);
        contextMap.put(klass, context);
    }

    @Override
    public void clearContext() {
        logger.info("[CLEAR CONTEXT]");
        for (GeometryContext context : contextMap.values()) {
            context.clear();
        }
        contextMap.clear();
    }

    @Override
    public boolean containsActor(Actor pickedActor) {
        return actorsMap.containsActor(pickedActor);
    }

    @Override
    public Collection<Actor> getActorsList() {
        return actorsMap.values();
    }

    @Override
    public Map<Surface, Actor> getActorsMap() {
        return actorsMap.getDelegate();
    }

    @Override
    public boolean canPickCells(Actor pickedActor) {
        VisibleItem surface = pickedActor.getVisibleItem();
        return (surface instanceof Stl || surface instanceof Solid);
    }

    @Override
    public boolean canPickMesh() {
        return false;
    }

}
