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
package eu.engys.core.modules;

import static org.jfree.chart.ChartPanel.DEFAULT_HEIGHT;
import static org.jfree.chart.ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT;
import static org.jfree.chart.ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH;
import static org.jfree.chart.ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT;
import static org.jfree.chart.ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH;
import static org.jfree.chart.ChartPanel.DEFAULT_WIDTH;

import java.awt.Color;

import javax.swing.JComponent;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import eu.engys.core.project.state.State;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.util.ui.ExecUtil;

public abstract class AbstractChart {

    public static final int DATASET_INDEX = 0;
    public static final int MOVING_AVERAGE_DATASET_INDEX = 1;
    public static final int WEIGHTING_DATASET_INDEX = 2;

    private ChartPanel chartPanel;

    public AbstractChart() {
    }

    public void layoutComponents() {
        boolean useBuffer = true;
        boolean showPropertiesMenu = true;
        boolean showCopyMenu = true;
        boolean showSaveMenu = false;
        boolean showPrintMenu = true;
        boolean showZoomMenu = true;
        boolean showTooltipsMenu = true;

        JFreeChart chart = createChart();
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setFrame(BlockBorder.NONE);
            legend.setBackgroundPaint(null);
            legend.setPosition(RectangleEdge.RIGHT);
            legend.setVisible(false);
        }
        chart.setBackgroundPaint(new Color(0, 0, 0, 0));
        this.chartPanel = new ChartPanel(chart, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_MINIMUM_DRAW_WIDTH, DEFAULT_MINIMUM_DRAW_HEIGHT, DEFAULT_MAXIMUM_DRAW_WIDTH, DEFAULT_MAXIMUM_DRAW_HEIGHT, useBuffer, showPropertiesMenu, showCopyMenu, showSaveMenu, showPrintMenu, showZoomMenu, showTooltipsMenu);
        this.chartPanel.setName(getClass().getCanonicalName());
    }

    public abstract JComponent getPanel();

    protected abstract JFreeChart createChart();

    public abstract void stop();

    public void addToDataSet(final TimeBlocks list) {
        synchronized (list) {
            if (list.size() > 0) {
                ExecUtil.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        notifySeries(false);
                        _addToDataSet(list);
                        notifySeries(true);
                        postAddToDataSet();
                    }
                });
            }
        }
    }

    public abstract void postAddToDataSet();

    public void clear() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                notifySeries(false);
                _clearData();
                notifySeries(true);
                chartPanel.restoreAutoDomainBounds();
            }
        });
    }

    protected abstract void notifySeries(boolean notify);

    public abstract void _addToDataSet(TimeBlocks list);

    public abstract void _clearData();

    public abstract void handleStateChanged(State state);

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public JFreeChart getChart() {
        return chartPanel.getChart();
    }

    public XYPlot getXYPlot() {
        return chartPanel.getChart().getXYPlot();
    }

    public CategoryPlot getCategoryPlot() {
        return chartPanel.getChart().getCategoryPlot();
    }

    public CategoryDataset getCategoryDataset(int index) {
        return chartPanel.getChart().getCategoryPlot().getDataset(index);
    }

    public XYDataset getXYDataset(int index) {
        return chartPanel.getChart().getXYPlot().getDataset(index);
    }

}
