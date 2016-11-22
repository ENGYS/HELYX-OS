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
package eu.engys.core.project.zero.patches;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.util.ui.checkboxtree.LoadableItem;
import eu.engys.util.ui.checkboxtree.VisibleItem;
import vtk.vtkPolyData;

public class Patch implements VisibleItem, LoadableItem {
	
    public static final String TYPE_KEY = "type";
    public static final String N_FACES_KEY = "nFaces";
	public static final String OFFSETS_KEY = "offsets";
    public static final String PHYSICAL_TYPE_KEY = "physicalType";
    public static final String START_FACE_KEY = "startFace";

    public static final int NONE = -1;
    private final String originalName;
    private String name;
    private BoundaryType physicalType;
    private String type;
    private boolean visible;
    private boolean loaded;
    private Dictionary dictionary;
    private BoundaryConditions boundaryConditions;
    private boolean empty;
    private vtkPolyData dataSet;

    public Patch(String originalName) {
        this.originalName = originalName;
    }

    public Patch(Patch patch) {
        this.originalName = patch.originalName;
        this.name = patch.name;
        this.physicalType = patch.physicalType;
        this.type = patch.type;
        this.visible = patch.visible;
        this.loaded = patch.loaded;
        this.boundaryConditions = new BoundaryConditions(patch.boundaryConditions);
        this.dictionary = new Dictionary(patch.getDictionary());
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

    public BoundaryType getPhysicalType() {
        return physicalType;
    }

    public void setPhysicalType(BoundaryType type) {
        this.physicalType = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public void setBoundaryConditions(BoundaryConditions boundaryConditions) {
        this.boundaryConditions = boundaryConditions;
    }

    public BoundaryConditions getBoundaryConditions() {
        return boundaryConditions;
    }

    @Override
    public String toString() {
        return name + " [ type: " + physicalType.getLabel() + ", visible: " + visible + ", loaded: " + loaded + "]";
    }

    public void setEmpty(boolean b) {
        this.empty = b;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setDictionary(Dictionary patch) {
        this.dictionary = patch;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public vtkPolyData getDataSet() {
        return dataSet;
    }

    public void setDataSet(vtkPolyData dataSet) {
        this.dataSet = dataSet;
    }

    public void resetToDefault(){
        setPhysicalType(BoundaryType.getDefaultType());
        setDictionary(new Dictionary(""));
        setBoundaryConditions(new BoundaryConditions(BoundaryConditionsDefaults.get(BoundaryType.getDefaultKey())));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Patch)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        Patch patch = (Patch) obj;
        return new EqualsBuilder().append(name, patch.name).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(name).toHashCode();
    }

}
