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

import eu.engys.core.project.geometry.Surface;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Context;

public class GeometryContext extends Context {

    private ActorsMap actorsMap;
    private Map<Surface, Boolean> actorsVisibility;

    @Override
    public boolean isEmpty() {
        return actorsMap.isEmpty();
    }

    public void clear() {
        actorsMap.clear();
        actorsVisibility.clear();
    }

    public ActorsMap getActorsMap() {
        return actorsMap;
    }
    public void setActorsMap(Map<Surface, Actor> actorsMap) {
        this.actorsMap = new ActorsMap(actorsMap);
        this.actorsVisibility = initActorsVisibility(actorsMap);
    }
    private Map<Surface, Boolean> initActorsVisibility(Map<Surface, Actor> actorsMap) {
        Map<Surface, Boolean> map = new LinkedHashMap<>();
        for (Surface surface : actorsMap.keySet()) {
            Actor actor = actorsMap.get(surface);
            map.put(surface, actor.getVisibility());
        }
        return map;
    }
    public Map<Surface, Boolean> getActorsVisibility() {
        return actorsVisibility;
    }

    @Override
    public String toString() {
        return "GeometryContext [ repres = " + getRepresentation() + ", actors are " + actorsMap.getDelegate().size() + "]";
    }
}
