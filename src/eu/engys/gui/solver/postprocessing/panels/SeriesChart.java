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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import eu.engys.core.modules.AbstractChart;
import eu.engys.core.project.state.State;
import eu.engys.gui.solver.postprocessing.panels.utils.ChartUtils;
import eu.engys.gui.solver.postprocessing.panels.utils.SeriesInfo;
import eu.engys.gui.solver.postprocessing.panels.utils.SeriesPanel;
import eu.engys.gui.solver.postprocessing.panels.utils.SeriesVisibilityListener;

public abstract class SeriesChart extends AbstractChart {

    private JSplitPane mainPanel;
    private SeriesPanel seriesPanel;
    protected Map<String, SeriesInfo> seriesInfoMap = new HashMap<>();

    public SeriesChart() {
        super();
    }

    @Override
    public void layoutComponents() {
        super.layoutComponents();

        JPanel chartPanel = new JPanel(new BorderLayout());

        JComponent accessory = createChartAccessory();
        if (accessory != null) {
            JSplitPane accessorySplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            accessorySplit.setTopComponent(getChartPanel());

            JScrollPane scroll = new JScrollPane(accessory);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            accessorySplit.setBottomComponent(scroll);
            accessorySplit.setResizeWeight(0.9);
            accessorySplit.setOneTouchExpandable(false);
            chartPanel.add(accessorySplit, BorderLayout.CENTER);
        } else {
            chartPanel.add(getChartPanel(), BorderLayout.CENTER);
        }

        this.mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainPanel.setOneTouchExpandable(false);
        mainPanel.setLeftComponent(chartPanel);
        mainPanel.setRightComponent(createRightPanel());
        mainPanel.setResizeWeight(0.8);
    }

    protected JPanel createRightPanel() {
        this.seriesPanel = new SeriesPanel();
        seriesPanel.addPropertyChangeListener(SERIES_INFO_PROPERTY, new SeriesVisibilityListener());

        JScrollPane seriesScrollPane = new JScrollPane(seriesPanel);
        seriesScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel rightPanel = new JPanel(new GridLayout(0, 1));
        rightPanel.add(seriesScrollPane);

        return rightPanel;
    }

    protected JComponent createChartAccessory() {
        return null;
    }

    @Override
    public JComponent getPanel() {
        return mainPanel;
    }

    protected void applyGraphicProperties(String seriesKey) {
        if (!seriesInfoMap.containsKey(seriesKey)) {
            int seriesIndex = ChartUtils.getSeriesIndex(getXYDataset(DATASET_INDEX), seriesKey);
            Color color = ChartUtils.colorForSerie(getXYPlot(), seriesIndex);
            boolean defaultVisibility = true;
            seriesInfoMap.put(seriesKey, new SeriesInfo(getXYPlot(), DATASET_INDEX, seriesKey, color, defaultVisibility));
        }
        ChartUtils.applyGraphicProperties(getXYPlot(), seriesInfoMap.get(seriesKey));
    }

    protected void populateSeriesPanel(String seriesKey) {
        seriesPanel.addSeries(seriesInfoMap.get(seriesKey));
    }

    @Override
    public void handleStateChanged(State state) {
    }

    @Override
    public void _clearData() {
        seriesPanel.clear();
    }

    public SeriesPanel getSeriesPanel() {
        return seriesPanel;
    }

}
