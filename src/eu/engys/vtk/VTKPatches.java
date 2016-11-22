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
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.PickInfo;
import eu.engys.gui.view3D.Picker;
import eu.engys.gui.view3D.RenderPanel;
import vtk.vtkDataSet;
import vtk.vtkPolyData;

public class VTKPatches implements VTKActors, Picker {

    private static final Logger logger = LoggerFactory.getLogger(VTKPatches.class);

    private Map<String, Actor> actors = new LinkedHashMap<>();
    private Map<Actor, String> names = new LinkedHashMap<>();

    private RenderPanel renderPanel;
    private Model model;

    public VTKPatches(Model model) {
        this.model = model;
    }

    @Override
    public void setRenderPanel(RenderPanel renderPanel) {
        this.renderPanel = renderPanel;
    }

    public void load(Map<String, vtkPolyData> patchesDataset) {
        Map<String, Patch> patchMap = model.getPatches().patchesToDisplay().toMap();
        for (String patchName : patchMap.keySet()) {
            Patch patch = patchMap.get(patchName);
            patch.setLoaded(true);
            patch.setDataSet(patchesDataset.get(patchName));
            addActorToPatches(new PatchActor(patch));
        }
    }

    void addActorToPatches(Actor actor) {
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

    public void VisibilityOn() {
        for (Actor actor : actors.values()) {
            actor.setVisibility(true);
        }
    }

    public void VisibilityOff() {
        for (Actor actor : actors.values()) {
            actor.setVisibility(false);
        }
    }

    @Override
    public Collection<Actor> getActors() {
        return actors.values();
    }

    @Override
    public boolean containsActor(Actor actor) {
        return names.containsKey(actor);
    }

    @Override
    public boolean canPickMesh() {
        return true;
    }

    public void updateSelection(Patch[] patches) {
        logger.debug("updateSurfaceVisibility: {} patches selected {}", patches.length, patches.length == 1 ? ", selection is: " + patches[0] : "");

        List<Actor> selection = new ArrayList<Actor>();

        for (Patch patch : patches) {
            String name = patch.getName();
            if (patch.isVisible() && actors.containsKey(name)) {
                selection.add(actors.get(name));
            }
        }
        renderPanel.setLowRendering();
        renderPanel.selectActors(false, selection.toArray(new Actor[0]));
        renderPanel.setHighRendering();
    }

    public void updateVisibility(Patch[] selection) {
        for (Patch patch : selection) {
            String name = patch.getName();
            if (actors.containsKey(name)) {
                Actor actor = actors.get(name);
                actor.setVisibility(patch.isVisible());
            }
        }
        renderPanel.renderLater();
    }

    public void update(List<vtkPolyData> patchesDataset) {
        VTKUtil.printTotalMemory(patchesDataset.toArray(new vtkDataSet[0]));

        List<Actor> actorsList = new ArrayList<>(getActors());
        for (int i = 0; i < patchesDataset.size(); i++) {
            vtkPolyData subset = patchesDataset.get(i);
            Actor actor = actorsList.get(i);
            logger.debug("Update actor input {}", names.get(actor));
            actor.setInput(subset);
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
            addActorToPatches(actor);
        }
        addActorsToRenderer();
    }

    public boolean isLoaded() {
        return !actors.isEmpty();
    }

    @Override
    public void pickActor(PickInfo pi) {
    }
}
