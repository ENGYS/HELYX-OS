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


package eu.engys.core.project.zero.cellzones;

import java.util.HashSet;
import java.util.Set;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.util.ui.checkboxtree.LoadableItem;
import eu.engys.util.ui.checkboxtree.VisibleItem;

public class CellZone implements VisibleItem, LoadableItem {

    private final String originalName;
    private String name;
    private Set<String> types = new HashSet<>();
    private boolean visible;
    private boolean loaded;
    private Dictionary dictionary;

    public CellZone(String originalName) {
        this.originalName = originalName;
        this.name = originalName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypes(Set<String> types) {
        this.types = types;
    }

    public Set<String> getTypes() {
        return types;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean selected) {
        this.visible = selected;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
    
    @Override
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void setDictionary(String key, Dictionary d) {
        if (dictionary == null) {
            this.dictionary = new Dictionary(originalName);
        }
        this.dictionary.add(new Dictionary(key, d));
    }

    public Dictionary getDictionary(String key) {
        return dictionary.subDict(key);
    }

    public void removeDictionary(String key) {
        if (hasDictionary(key)) {
            dictionary.remove(key);
        }
    }

    public boolean hasDictionary(String key) {
        return dictionary != null && dictionary.found(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" [");
        for (String type : types) {
            sb.append(type);
            sb.append("\n");
        }
        sb.append("] ");
        sb.append(visible);

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CellZone) {
            return ((CellZone) obj).getName().equals(name);
        }
        return super.equals(obj);
    }

    public boolean hasType(String key) {
        return types.contains(key);
    }
}
