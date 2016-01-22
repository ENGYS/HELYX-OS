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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import eu.engys.core.project.geometry.Surface;
import eu.engys.gui.view3D.Actor;

public class ActorsMap {
    
    private final Map<Surface, Actor> delegate = new LinkedHashMap<>();
    
    public ActorsMap() {
    }

    public ActorsMap(Map<Surface, Actor> map) {
        this.delegate.putAll(map);
    }

    public void put(Surface key, Actor value) {
        delegate.put(key, value);
    }

    public Actor get(Surface key) {
        return delegate.get(key);
    }

    public Actor remove(Surface key) {
        return delegate.remove(key);
    }

    public boolean contains(Surface surface) {
        return delegate.containsKey(surface);
    }

    public Collection<Actor> values() {
        return delegate.values();
    }

    public Set<Surface> keys() {
        return delegate.keySet();
    }

    public void clear() {
        delegate.clear();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public boolean containsActor(Actor actor) {
        return delegate.containsValue(actor);
    }

    public Map<Surface, Actor> getDelegate() {
        return delegate;
    }

}
