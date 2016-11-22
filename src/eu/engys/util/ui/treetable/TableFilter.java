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
package eu.engys.util.ui.treetable;

import java.util.HashMap;
import java.util.Map;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

public class TableFilter<T extends TableModel> extends RowFilter<T, Object> {

    private Map<Integer, String> filterStrings = new HashMap<>();

    public TableFilter(String initialFilter) {
        setFilterText(0, "");
    }

    public void setFilterText(int i, String filterText) {
        filterStrings.put(i, filterText);
    }

    @Override
    public boolean include(RowFilter.Entry<? extends T, ? extends Object> entry) {
        if (filterStrings == null || filterStrings.isEmpty()) {
            return true;
        }

        int numberOfTableColumns = entry.getValueCount();
        for (int i = 0; i < numberOfTableColumns; i++) {
            if (filterStrings.containsKey(i)) {
                String filterText = filterStrings.get(i);
                String regexpFilter = filterText.replace(".", "\\.").replace("*", ".*").replace("?", ".?").replace("+", ".+").concat(".*");
                if (regexpFilter.isEmpty() || entry.getStringValue(i).matches(regexpFilter)) {
                    continue;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public void reset() {
        filterStrings.clear();
        setFilterText(0, "");
    }

}