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

package eu.engys.util.ui.treetable;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.apache.commons.lang.ArrayUtils;

public class TableFilter<T extends TableModel> extends RowFilter<T, Object> {

    private String filterText;
    private int[] columnsWhereToSearch;

    public TableFilter(String initialFilter) {
        this.filterText = initialFilter;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    public void setColumnsWhereToSearch(int... columnsWhereToSearch) {
        this.columnsWhereToSearch = columnsWhereToSearch;
    }

    @Override
    public boolean include(RowFilter.Entry<? extends T, ? extends Object> entry) {
        if (columnsWhereToSearch == null || columnsWhereToSearch.length == 0) {
            return true;
        }

        String regexpFilter = filterText.replace(".", "\\.").replace("*", ".*").replace("?", ".?").replace("+", ".+").concat(".*");

        int numberOfTableColumns = entry.getValueCount();
        for (int i = 0; i < numberOfTableColumns; i++) {
            if (ArrayUtils.contains(columnsWhereToSearch, i)) {
                if (entry.getStringValue(i).matches(regexpFilter)) {
                    return true;
                }
            }
        }
        return false;

    }

}
