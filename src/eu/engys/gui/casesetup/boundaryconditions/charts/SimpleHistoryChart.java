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
package eu.engys.gui.casesetup.boundaryconditions.charts;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlock;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlockUnit;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.gui.solver.postprocessing.data.DoubleTimeBlockUnit;
import eu.engys.gui.solver.postprocessing.panels.SeriesChart;
import eu.engys.util.ui.textfields.DoubleField;

public class SimpleHistoryChart extends SeriesChart {

    private static final Logger logger = LoggerFactory.getLogger(SimpleHistoryChart.class);
    private String domainAxisLabel;
    private String rangeAxisLabel;
    private List<String> seriesNames;

    public SimpleHistoryChart(List<String> seriesNames, String domainAxisLabel, String rangeAxisLabel) {
        super();
        this.seriesNames = seriesNames;
        this.domainAxisLabel = domainAxisLabel;
        this.rangeAxisLabel = rangeAxisLabel;
    }

    @Override
    protected void notifySeries(boolean notify) {
        XYSeriesCollection dataset = (XYSeriesCollection) getXYDataset(DATASET_INDEX);
        dataset.setNotify(notify);
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            dataset.getSeries(i).setNotify(notify);
        }
    }

    @Override
    protected JFreeChart createChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        JFreeChart chart = ChartFactory.createXYLineChart("", "", "", dataset, PlotOrientation.VERTICAL, true, true, false);

        NumberAxis domainAxis = new NumberAxis(domainAxisLabel);
        domainAxis.setAutoRangeIncludesZero(false);
        domainAxis.setAutoRange(true);

        NumberAxis rangeAxis = new NumberAxis(rangeAxisLabel);
        rangeAxis.setNumberFormatOverride(DoubleField.getFormatForDISPLAY(10));
        rangeAxis.setAutoRangeIncludesZero(false);
        rangeAxis.setAutoRange(true);

        XYPlot xyplot = chart.getXYPlot();
        xyplot.setDomainAxis(domainAxis);
        xyplot.setRangeAxis(rangeAxis);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);

        return chart;
    }

    @Override
    public void _addToDataSet(TimeBlocks list) {
        for (TimeBlock block : list) {
            for (String serieName : seriesNames) {
                XYSeriesCollection dataset = (XYSeriesCollection) getXYDataset(DATASET_INDEX);
                if (dataset.getSeriesIndex(serieName) == -1) {
                    XYSeries serie = new XYSeries(serieName);
                    serie.setNotify(false);
                    dataset.addSeries(serie);
                    applyGraphicProperties(serieName);
                    populateSeriesPanel(serieName);
                }
                addTimeUnit(block.getTime(), block.getUnitsMap().get(serieName));
            }
        }
    }

    @Override
    public void postAddToDataSet() {
    }

    private void addTimeUnit(double time, TimeBlockUnit unit) {
        if (unit instanceof DoubleTimeBlockUnit) {
            DoubleTimeBlockUnit doubleUnit = (DoubleTimeBlockUnit) unit;
            Double value = doubleUnit.getValue();
            ((XYSeriesCollection) getXYDataset(DATASET_INDEX)).getSeries(unit.getVarName()).add(time, value);
        } else {
            logger.error("Invalid unit type {} in time {}", (unit != null ? unit.getClass().getCanonicalName() : "null"), time);
        }
    }

    @Override
    public void _clearData() {
        super._clearData();
        ((XYSeriesCollection) getXYDataset(DATASET_INDEX)).removeAllSeries();
    }

    @Override
    public void stop() {
    }

}