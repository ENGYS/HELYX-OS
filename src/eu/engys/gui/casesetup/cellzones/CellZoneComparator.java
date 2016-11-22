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
package eu.engys.gui.casesetup.cellzones;

import static eu.engys.core.project.zero.cellzones.CellZoneType.DYNAMIC_MESH_KEY;
import static eu.engys.core.project.zero.cellzones.CellZoneType.HEAT_EXCHANGER_KEY;
import static eu.engys.core.project.zero.cellzones.CellZoneType.HUMIDITY_KEY;
import static eu.engys.core.project.zero.cellzones.CellZoneType.MRF_KEY;
import static eu.engys.core.project.zero.cellzones.CellZoneType.POROUS_KEY;
import static eu.engys.core.project.zero.cellzones.CellZoneType.THERMAL_KEY;

import java.util.Arrays;
import java.util.Comparator;

import eu.engys.core.project.zero.cellzones.CellZoneType;

public class CellZoneComparator implements Comparator<CellZoneType> {

    public static final String[] ORDERED_KEYS = new String[] { 
            POROUS_KEY, MRF_KEY, 
            DYNAMIC_MESH_KEY, 
            THERMAL_KEY,
            HEAT_EXCHANGER_KEY,
            HUMIDITY_KEY };

    @Override
    public int compare(CellZoneType type1, CellZoneType type2) {
        int indexType1 = Arrays.asList(ORDERED_KEYS).indexOf(type1.getKey());
        int indexType2 = Arrays.asList(ORDERED_KEYS).indexOf(type2.getKey());
        return Integer.compare(indexType1, indexType2);
    }

}
