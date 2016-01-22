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

import java.util.LinkedHashMap;
import java.util.Map;

import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Context;
import eu.engys.gui.view3D.Representation;

public class MeshContext extends Context {
    
    private boolean allowSelection;

    private Map<String, Actor> patches;
    private Map<String, Boolean> patchesVisibility;

    private Map<String, Actor> cellzones;
    private Map<String, Boolean> zonesVisibility;

    public MeshContext(Representation representation, boolean allowSelection, Map<String, Actor> cellzones, Map<String, Actor> patches) {
        super(representation);
        this.allowSelection = allowSelection;

        this.patches = new LinkedHashMap<>(patches);
        this.patchesVisibility = initPatchesVisibility(patches);

        this.cellzones = new LinkedHashMap<>(cellzones);
        this.zonesVisibility = initZonesVisibility(cellzones);
    }

    public void clear() {
        patches.clear();
        patchesVisibility.clear();
        zonesVisibility.clear();
        cellzones.clear();
    }

    private Map<String, Boolean> initPatchesVisibility(Map<String, Actor> patches) {
        Map<String, Boolean> map = new LinkedHashMap<>();
        for (String name : patches.keySet()) {
            Actor actor = patches.get(name);
            map.put(name, actor.getVisibility());
        }
        return map;
    }

    private Map<String, Boolean> initZonesVisibility(Map<String, Actor> cellzones) {
        Map<String, Boolean> map = new LinkedHashMap<>();
        for (String name : cellzones.keySet()) {
            Actor actor = cellzones.get(name);
            map.put(name, actor.getVisibility());
        }
        return map;
    }
    
    @Override
    public boolean isEmpty() {
        return patches.isEmpty();
    }
    
    public boolean isAllowSelection(){
        return allowSelection;
    }
    
    public Map<String, Actor> getPatches() {
        return patches;
    }
    
    public Map<String, Boolean> getPatchesVisibility() {
        return patchesVisibility;
    }
    
    public Map<String, Actor> getCellzones() {
        return cellzones;
    }
    
    public Map<String, Boolean> getZonesVisibility() {
        return zonesVisibility;
    }

    @Override
    public String toString() {
        return "MeshContext [ repres = " + representation + ", zones are " + cellzones.keySet().size() + ", patches are " + patches.keySet().size() + "]";
    }

}
