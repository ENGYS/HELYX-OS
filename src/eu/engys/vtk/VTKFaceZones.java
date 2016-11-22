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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.Model;
import eu.engys.core.project.zero.facezones.FaceZone;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.PickInfo;
import eu.engys.gui.view3D.Picker;
import eu.engys.gui.view3D.RenderPanel;
import vtk.vtkPolyData;

public class VTKFaceZones implements VTKActors, Picker {

    private static final Logger logger = LoggerFactory.getLogger(VTKFaceZones.class);

    private Map<String, Actor> actors = new LinkedHashMap<>();
    private Map<Actor, String> names = new LinkedHashMap<>();

    private RenderPanel renderPanel;
    private Model model;

    public VTKFaceZones(Model model) {
        this.model = model;
    }

    @Override
    public void setRenderPanel(RenderPanel renderPanel) {
        this.renderPanel = renderPanel;
    }

    public void load(List<vtkPolyData> faceZonesDataset) {
        for (int i = 0; i < faceZonesDataset.size(); i++) {
            vtkPolyData dataset = faceZonesDataset.get(i);

            FaceZone zone = model.getFaceZones().get(i);
            zone.setLoaded(true);
            addActorToZones(new FaceZoneActor(zone, dataset));
        }
    }

    void addActorToZones(Actor actor) {
        logger.debug("[ADD ACTOR] {} ({})", actor.getName(), actor.getVisibility() ? "visible" : "hidden");
        actors.put(actor.getName(), actor);
        names.put(actor, actor.getName());
    }

    void addActorsToRenderer() {
        for (String name : actors.keySet()) {
            Actor actor = actors.get(name);
            renderPanel.addActor(actor);
        }
    }

    public void deleteActors() {
        for (Actor actor : actors.values()) {
            renderPanel.removeActor(actor);
            actor.deleteActor();
        }
        actors.clear();
        names.clear();
    }

    public void removeActorsFromRenderer() {
        for (Actor actor : actors.values()) {
            renderPanel.removeActor(actor);
        }
        actors.clear();
        names.clear();
    }

    public void VisibilityOff() {
        for (Actor actor : actors.values()) {
            actor.setVisibility(false);
        }
    }
    
    public void VisibilityOn() {
        for (Actor actor : actors.values()) {
            actor.setVisibility(true);
        }
    }

    public Collection<Actor> getActors() {
        return actors.values();
    }

    public boolean containsActor(Actor pickedActor) {
        return names.containsKey(pickedActor);
    }

    @Override
    public boolean canPickMesh() {
        return true;
    }

    public void selectActors(FaceZone[] zones) {
        logger.debug("updateSurfaceVisibility: {} zones selected {}", zones.length, zones.length == 1 ? ", selection is: " + zones[0] : "");

        List<Actor> selection = new ArrayList<Actor>();
        for (FaceZone zone : zones) {
            String name = zone.getName();
            if (zone.isVisible() && actors.containsKey(name)) {
                selection.add(actors.get(name));
            }
        }
        renderPanel.setLowRendering();
        renderPanel.selectActors(false, selection.toArray(new Actor[0]));
        renderPanel.setHighRendering();
    }

    public void updateVisibility(FaceZone[] selection) {
        for (FaceZone cellZone : selection) {
            Actor actor = actors.get(cellZone.getName());
            actor.setVisibility(cellZone.isVisible());
        }
        renderPanel.renderLater();
    }

    public void update(List<vtkPolyData> cellZonesDataset) {
        List<Actor> actorsList = new ArrayList<>(getActors());
        for (int i = 0; i < cellZonesDataset.size(); i++) {
            vtkPolyData obj = cellZonesDataset.get(i);
            Actor actor = actorsList.get(i);
            logger.debug("Update polydata {}", names.get(actor));
//            VTKUtil.changeDataset(actor, obj);
            actor.setInput(obj);
        }
    }

    @Override
    public Map<String, Actor> getActorsMap() {
        return Collections.unmodifiableMap(actors);
    }
    
    @Override
    public void addActorsMap(Map<String, Actor> map, Map<String, Boolean> visibility) {
        for (String name : map.keySet()) {
            Actor actor = map.get(name);
            actor.setVisibility(visibility.get(name));
            addActorToZones(actor);
            renderPanel.addActor(actor);
        }
    }

    public boolean isLoaded() {
        return !actors.isEmpty();
    }

    @Override
    public void pickActor(PickInfo pi) {
    }

}
