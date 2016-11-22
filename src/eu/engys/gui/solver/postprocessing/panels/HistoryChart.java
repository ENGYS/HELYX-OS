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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import eu.engys.core.modules.AbstractChart;
import eu.engys.util.ui.textfields.DoubleField;

public abstract class HistoryChart extends OverlayableMovingAverageChart {

    public HistoryChart() {
        super();
    }

    @Override
    protected JFreeChart createChart() {
        JFreeChart chart = ChartFactory.createXYLineChart("", "", "", new XYSeriesCollection(), PlotOrientation.VERTICAL, true, true, false);

        NumberAxis domainAxis = new NumberAxis("");
        domainAxis.setAutoRangeIncludesZero(false);
        domainAxis.setAutoRange(true);

        NumberAxis rangeAxis = new NumberAxis("");
        rangeAxis.setNumberFormatOverride(DoubleField.getFormatForDISPLAY(10));
        rangeAxis.setAutoRangeIncludesZero(false);
        rangeAxis.setAutoRange(true);

        XYPlot xyplot = chart.getXYPlot();
        xyplot.setDomainAxis(domainAxis);
        xyplot.setRangeAxis(rangeAxis);
        xyplot.setDataset(AbstractChart.MOVING_AVERAGE_DATASET_INDEX, movingAverageDataSet);

        StandardXYItemRenderer movingAverageRenderer = new StandardXYItemRenderer();
        movingAverageRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        chart.getXYPlot().setRenderer(MOVING_AVERAGE_DATASET_INDEX, movingAverageRenderer);
        chart.getXYPlot().setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        return chart;
    }

    @Override
    public XYSeriesCollection getXYDataset(int index) {
        return (XYSeriesCollection) super.getXYDataset(index);
    }

    @Override
    public void _clearData() {
        super._clearData();
        getXYDataset(DATASET_INDEX).removeAllSeries();
    }

    @Override
    protected void notifySeries(boolean notify) {
        super.notifySeries(notify);
        XYSeriesCollection dataset = (XYSeriesCollection) getXYDataset(DATASET_INDEX);
        dataset.setNotify(notify);
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            dataset.getSeries(i).setNotify(notify);
        }
    }

}
