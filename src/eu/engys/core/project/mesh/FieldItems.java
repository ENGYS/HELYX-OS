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
package eu.engys.core.project.mesh;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FieldItems {

    private Map<String, FieldItem> cellFieldMap;
    private Map<String, FieldItem> pointFieldMap;

    public FieldItems() {
        this.cellFieldMap = new LinkedHashMap<>();
        this.pointFieldMap = new LinkedHashMap<>();
    }

    public void clear() {
        cellFieldMap.clear();
        pointFieldMap.clear();
    }

    public Set<String> cellFieldKeys() {
        return cellFieldMap.keySet();
    }

    public Set<String> pointFieldKeys() {
        return pointFieldMap.keySet();
    }

    public FieldItem getCellFieldItem(String key) {
        return cellFieldMap.get(key);
    }

    public FieldItem getPointFieldItem(String key) {
        return pointFieldMap.get(key);
    }

    public void addPointFieldItem(FieldItem item) {
        pointFieldMap.put(item.getName(), item);
    }

    public void addCellFieldItem(FieldItem item) {
        cellFieldMap.put(item.getName(), item);
    }

    public List<FieldItem> getPointFieldItems() {
        List<FieldItem> items = new ArrayList<FieldItem>();
        for (FieldItem fi : pointFieldMap.values()) {
            if (fi.isVisible()) {
                items.add(fi);
            }
        }
        return items;
    }

    public List<FieldItem> getCellFieldItems() {
        List<FieldItem> items = new ArrayList<FieldItem>();
        for (FieldItem fi : cellFieldMap.values()) {
            if (fi.isVisible()) {
                items.add(fi);
            }
        }
        return items;
    }

    public boolean containsCellFieldItem(String key) {
        return cellFieldMap.containsKey(key);
    }

    public boolean containsPointFieldItem(String key) {
        return pointFieldMap.containsKey(key);
    }

    public boolean containsVisibleCellFieldItem(String key) {
        boolean contains = cellFieldMap.containsKey(key);
        if (contains) {
            return cellFieldMap.get(key).isVisible();
        } else {
            return false;
        }
    }

    public boolean containsVisiblePointFieldItem(String key) {
        boolean contains = pointFieldMap.containsKey(key);
        if (contains) {
            return pointFieldMap.get(key).isVisible();
        } else {
            return false;
        }
    }

    // For test purposes only
    public Map<String, FieldItem> getPointFieldMap() {
        return pointFieldMap;
    }

    // For test purposes only
    public Map<String, FieldItem> getCellFieldMap() {
        return cellFieldMap;
    }

    public FieldItem getEquivalentFieldItemOf(FieldItem sample) {
        if (sample.getDataType().isCell()) {
            if (containsVisibleCellFieldItem(sample.getName())) {
                return cellFieldMap.get(sample.getName());
            } else {
                return FieldItem.solidColorItem();
            }
        } else if (sample.getDataType().isPoint()) {
            if (containsVisiblePointFieldItem(sample.getName())) {
                return pointFieldMap.get(sample.getName());
            } else {
                return FieldItem.solidColorItem();
            }
        } else if (sample.getDataType().isNone()) {
            if (sample.isIndexed()) {
                return FieldItem.indexItem();
            } else {
                return FieldItem.solidColorItem();
            }
        } else {
            return FieldItem.solidColorItem();
        }
    }

    public void addFieldItems(FieldItems fieldItems) {
        for (FieldItem fieldItem : fieldItems.getCellFieldItems()) {
            updateCellFieldMap(fieldItem);
        }
        fixCellFieldItemsVisibility(fieldItems.cellFieldKeys());
        
        for (FieldItem fieldItem : fieldItems.getPointFieldItems()) {
            updatePointFieldMap(fieldItem);
        }
        fixPointFieldItemsVisibility(fieldItems.pointFieldKeys());
    }

    private void updateCellFieldMap(FieldItem item) {
        if (containsCellFieldItem(item.getName())) {
            // Just update the range used in automatic calculation
            FieldItem fi = getCellFieldItem(item.getName());
            fi.setOriginalRange(item.getOriginalRange());
            fi.setVisible(true);
        } else {
            addCellFieldItem(item);
        }
    }

    private void fixCellFieldItemsVisibility(Set<String> set) {
        for (String key : cellFieldKeys()) {
            if (!set.contains(key)) {
                getCellFieldItem(key).setVisible(false);
            }
        }
    }

    private void updatePointFieldMap(FieldItem item) {
        if (containsPointFieldItem(item.getName())) {
            // Just update the range used in automatic calculation
            FieldItem fi = getPointFieldItem(item.getName());
            fi.setOriginalRange(item.getOriginalRange());
            fi.setVisible(true);
        } else {
            addPointFieldItem(item);
        }
    }

    private void fixPointFieldItemsVisibility(Set<String> list) {
        for (String key : pointFieldKeys()) {
            if (!list.contains(key)) {
                getPointFieldItem(key).setVisible(false);
            }
        }
    }
}
