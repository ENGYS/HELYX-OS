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
package eu.engys.gui.solver.postprocessing.panels.utils.markers;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DatasetUtilities;

import com.google.common.collect.Lists;

import eu.engys.gui.events.EventManager;
import eu.engys.gui.solver.postprocessing.panels.utils.events.CrosshairChangedEvent;

public class MarkersTableModel extends AbstractTableModel {

    public static final String NO_DATA = "-";

    private ChartPanel chartPanel;
    private SortedMap<Marker, List<MarkersTableCell>> seriesValues;

    public MarkersTableModel(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
        this.seriesValues = new TreeMap<>();
    }

    public void load(List<Marker> markers) {
        seriesValues.clear();
        for (Marker marker : markers) {
            addRow(marker);
        }
    }

    private void addRow(Marker marker) {
        XYPlot xyPlot = chartPanel.getChart().getXYPlot();

        List<MarkersTableCell> values = new LinkedList<>();
        for (int i = 0; i < xyPlot.getDataset().getSeriesCount(); i++) {
            String seriesName = (String) xyPlot.getDataset().getSeriesKey(i);
            Double value = DatasetUtilities.findYValue(xyPlot.getDataset(), i, marker.getValue());
            values.add(new MarkersTableCell(seriesName, value));
        }
        seriesValues.put(marker, values);
        fireTableStructureChanged();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (seriesValues.isEmpty()) {
            return Double.NaN;
        } else {
            if (columnIndex == 0) {
                return Lists.newArrayList(seriesValues.keySet()).get(rowIndex).getValue();
            } else {
                Marker key = Lists.newArrayList(seriesValues.keySet()).get(rowIndex);
                return seriesValues.get(key).get(columnIndex - 1).getValue();
            }
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            Marker existingKey = Lists.newArrayList(seriesValues.keySet()).get(rowIndex);
            Marker newKey = new Marker((Double) aValue);
            EventManager.triggerEvent(this, new CrosshairChangedEvent(existingKey, newKey));
        }
    }

    @Override
    public int getRowCount() {
        return seriesValues.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (seriesValues.isEmpty()) {
            return NO_DATA;
        } else {
            if (columnIndex == 0) {
                return chartPanel.getChart().getXYPlot().getDomainAxis().getLabel();
            } else {
                Marker firstKey = seriesValues.keySet().iterator().next();
                List<MarkersTableCell> firstItem = seriesValues.get(firstKey);
                return firstItem.get(columnIndex - 1).getSerieName();
            }
        }
    }

    @Override
    public int getColumnCount() {
        return chartPanel.getChart().getXYPlot().getDataset().getSeriesCount() + 1;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return Double.class;
    }

}
