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

package eu.engys.core.project.zero.cellzones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellZones extends ArrayList<CellZone> {

    public CellZones() {
        super();
    }

    public List<String> zonesNames() {
        List<String> names = new ArrayList<>();
        for (CellZone zone : this) {
            names.add(zone.getName());
        }
        return names;
    }

    public Map<String, CellZone> toMap() {
        Map<String, CellZone> zonesMap = new HashMap<String, CellZone>();
        for (CellZone zone : this) {
            zonesMap.put(zone.getName(), zone);
        }
        return Collections.unmodifiableMap(zonesMap);
    }

    public void addZones(List<CellZone> cellZones) {
        addAll(cellZones);
    }

    public boolean hasPorous() {
        for (CellZone zone : this) {
            if (zone.getTypes().contains(CellZoneType.POROUS_KEY)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMRF() {
        for (CellZone zone : this) {
            if (zone.getTypes().contains(CellZoneType.MRF_KEY)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDynamic() {
        for (CellZone zone : this) {
            if (zone.getTypes().contains(CellZoneType.DYNAMIC_MESH_KEY)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasThermal() {
        for (CellZone zone : this) {
            if (zone.getTypes().contains(CellZoneType.THERMAL_KEY)) {
                return true;
            }
        }
        return false;
    }
}
