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

package eu.engys.core.project.system.monitoringfunctionobjects;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class TimeBlock implements Serializable {

    private Double time;
    private Map<String, TimeBlockUnit> unitsMap;

    public TimeBlock(Double time) {
        this.time = time;
        this.unitsMap = new LinkedHashMap<String, TimeBlockUnit>();
    }

    public Map<String, TimeBlockUnit> getUnitsMap() {
        return unitsMap;
    }

    public int getSize() {
        return unitsMap.size();
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    @Override
    public String toString() {
        StringBuffer rowstring = new StringBuffer();
        for (String var : getUnitsMap().keySet()) {
            rowstring.append(var + " ");
        }
        return "TIME BLOCK " + getTime() + " ROW KEYS: " + rowstring.toString();
    }
}
