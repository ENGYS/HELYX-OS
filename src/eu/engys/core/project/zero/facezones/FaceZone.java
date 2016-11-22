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
package eu.engys.core.project.zero.facezones;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.util.ui.checkboxtree.LoadableItem;
import eu.engys.util.ui.checkboxtree.VisibleItem;

public class FaceZone implements VisibleItem, LoadableItem {

    private final String originalName;
    private String name;
    private boolean visible;
    private boolean loaded;
    private Dictionary dictionary;

    public FaceZone(String originalName) {
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

    public void setDictionary(Dictionary d) {
        this.dictionary = d;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    @Override
    public String toString() {
        return name + " [" + getName() + ", " + visible + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FaceZone)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        FaceZone fz = (FaceZone) obj;
        return new EqualsBuilder().append(name, fz.name).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(name).toHashCode();
    }

}
