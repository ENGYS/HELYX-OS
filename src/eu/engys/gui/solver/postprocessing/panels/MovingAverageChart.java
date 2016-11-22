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
package eu.engys.gui.solver.postprocessing.panels;

import static eu.engys.gui.solver.postprocessing.panels.utils.SeriesInfo.SERIES_INFO_PROPERTY;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.data.xy.XYSeriesCollection;

import eu.engys.core.project.state.State;
import eu.engys.gui.solver.postprocessing.panels.utils.ChartUtils;
import eu.engys.gui.solver.postprocessing.panels.utils.MavgSeriesPanel;
import eu.engys.gui.solver.postprocessing.panels.utils.SeriesInfo;
import eu.engys.gui.solver.postprocessing.panels.utils.SeriesVisibilityListener;

public abstract class MovingAverageChart extends SeriesChart {

    protected XYSeriesCollection movingAverageDataSet;
    private MavgSeriesPanel movingAverageSeriesPanel;
    private Map<String, SeriesInfo> mavgSeriesInfoMap = new HashMap<>();

    public MovingAverageChart() {
        super();
        this.movingAverageDataSet = new XYSeriesCollection();
    }

    @Override
    protected JPanel createRightPanel() {
        this.movingAverageSeriesPanel = new MavgSeriesPanel(getXYDataset(DATASET_INDEX), movingAverageDataSet);
        movingAverageSeriesPanel.addPropertyChangeListener(SERIES_INFO_PROPERTY, new SeriesVisibilityListener());

        JPanel rightPanel = super.createRightPanel();

        JScrollPane movingAverageSeriesScrollPane = new JScrollPane(movingAverageSeriesPanel);
        movingAverageSeriesScrollPane.setBorder(BorderFactory.createEmptyBorder());

        rightPanel.add(movingAverageSeriesScrollPane);

        return rightPanel;
    }

    protected void applyMavgGraphicProperties(String seriesKey) {
        if (!mavgSeriesInfoMap.containsKey(seriesKey)) {
            int mavgSeriesIndex = ChartUtils.getSeriesIndex(movingAverageDataSet, seriesKey);
            Color mavgColor = ChartUtils.coloForMAVGSerie(getXYPlot(), mavgSeriesIndex);
            boolean defaultVisibility = false;
            mavgSeriesInfoMap.put(seriesKey, new SeriesInfo(getXYPlot(), MOVING_AVERAGE_DATASET_INDEX, seriesKey, mavgColor, defaultVisibility));
        }
        ChartUtils.applyGraphicProperties(getXYPlot(), mavgSeriesInfoMap.get(seriesKey));
    }

    protected void populateMovingAverageSeriesPanel(String seriesKey) {
        movingAverageSeriesPanel.addSeries(mavgSeriesInfoMap.get(seriesKey));
    }

    @Override
    public void postAddToDataSet() {
        movingAverageSeriesPanel.updateMovingAverage();
    }

    @Override
    public void _clearData() {
        super._clearData();
        movingAverageSeriesPanel.clear();
        movingAverageDataSet.removeAllSeries();
    }

    @Override
    protected void notifySeries(boolean notify) {
        movingAverageDataSet.setNotify(notify);
        for (int i = 0; i < movingAverageDataSet.getSeriesCount(); i++) {
            movingAverageDataSet.getSeries(i).setNotify(notify);
        }
    }

    @Override
    public void handleStateChanged(State state) {
        ChartUtils.updateDomainAxisLabel(state, getXYPlot());
    }

}
