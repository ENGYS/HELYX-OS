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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceZones extends ArrayList<FaceZone> {

    public FaceZones() {
        super();
    }

    public List<String> zonesNames() {
        List<String> names = new ArrayList<>();
        for (FaceZone zone : this) {
            names.add(zone.getName());
        }
        return names;
    }

    public Map<String, FaceZone> toMap() {
        HashMap<String, FaceZone> zonesMap = new HashMap<String, FaceZone>();
        for (FaceZone zone : this) {
            zonesMap.put(zone.getName(), zone);
        }
        return Collections.unmodifiableMap(zonesMap);
    }

    public void addZones(List<FaceZone> faceZones) {
        addAll(faceZones);
    }
}
