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

import java.util.LinkedHashMap;
import java.util.Map;

import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Context;

public class MeshContext extends Context {

    private boolean allowSelection;

    private Map<String, Actor> patches;
    private Map<String, Boolean> patchesVisibility;

    private Map<String, Actor> faceZones;
    private Map<String, Boolean> faceZonesVisibility;

    private Map<String, Actor> cellZones;
    private Map<String, Boolean> cellZonesVisibility;
    
    private Map<String, Actor> internalMesh;
    private Map<String, Boolean> internalMeshVisibility;

    public void clear() {
        patches.clear();
        patchesVisibility.clear();
        cellZones.clear();
        cellZonesVisibility.clear();
        faceZones.clear();
        faceZonesVisibility.clear();
    }

    @Override
    public boolean isEmpty() {
        return patches.isEmpty();
    }

    public boolean isAllowSelection() {
        return allowSelection;
    }
    public void setAllowSelection(boolean allowSelection) {
        this.allowSelection = allowSelection;
    }

    public Map<String, Actor> getPatches() {
        return patches;
    }
    public void setPatches(Map<String, Actor> patches) {
        this.patches = new LinkedHashMap<>(patches);
        this.patchesVisibility = initVisibility(patches);
    }
    public Map<String, Boolean> getPatchesVisibility() {
        return patchesVisibility;
    }

    public Map<String, Actor> getFaceZones() {
        return faceZones;
    }
    public void setFaceZones(Map<String, Actor> faceZones) {
        this.faceZones = new LinkedHashMap<>(faceZones);
        this.faceZonesVisibility = initVisibility(faceZones);
    }
    public Map<String, Boolean> getFaceZonesVisibility() {
        return faceZonesVisibility;
    }

    public Map<String, Actor> getCellZones() {
        return cellZones;
    }
    public void setCellZones(Map<String, Actor> cellZones) {
        this.cellZones = new LinkedHashMap<>(cellZones);
        this.cellZonesVisibility = initVisibility(cellZones);
    }
    public Map<String, Boolean> getCellZonesVisibility() {
        return cellZonesVisibility;
    }

    public Map<String, Actor> getInternalMesh() {
        return internalMesh;
    }
    public void setInternalMesh(Map<String, Actor> internalMesh) {
        this.internalMesh = new LinkedHashMap<>(internalMesh);
        this.internalMeshVisibility = initVisibility(internalMesh);
    }
    public Map<String, Boolean> getInternalMeshVisibility() {
        return internalMeshVisibility;
    }
    
    private Map<String, Boolean> initVisibility(Map<String, Actor> actors) {
        Map<String, Boolean> map = new LinkedHashMap<>();
        for (String name : actors.keySet()) {
            Actor actor = actors.get(name);
            map.put(name, actor.getVisibility());
        }
        return map;
    }
    
    @Override
    public String toString() {
        return "MeshContext [ rep = " + representation + ", CellZones = " + cellZones.keySet().size() + ", FaceZones = " + faceZones.keySet().size() + ", Patches = " + patches.keySet().size() + "]";
    }

}
