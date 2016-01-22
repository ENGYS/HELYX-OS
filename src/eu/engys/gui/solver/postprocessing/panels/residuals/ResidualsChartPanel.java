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

package eu.engys.gui.solver.postprocessing.panels.residuals;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;

import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlock;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlockUnit;
import eu.engys.gui.solver.postprocessing.data.DoubleListTimeBlockUnit;
import eu.engys.gui.solver.postprocessing.panels.HistoryChartPanel;

public class ResidualsChartPanel extends HistoryChartPanel {

    public ResidualsChartPanel() {
        super("Residuals", null, TIME_LABEL, "", false);
    }

    @Override
    protected void createChart() {
        this.chart = ChartFactory.createXYLineChart("", "", "", dataset, PlotOrientation.VERTICAL, true, true, false);
        NumberAxis domainAxis = new NumberAxis(domainAxisLabel);
        domainAxis.setAutoRangeIncludesZero(false);

        LogarithmicAxis rangeAxis = new LogarithmicAxis(rangeAxisLabel);
        rangeAxis.setExpTickLabelsFlag(true);

        XYPlot xyPlot = chart.getXYPlot();
        xyPlot.setDomainAxis(domainAxis);
        xyPlot.setRangeAxis(rangeAxis);
    }

    @Override
    protected void addTimeBlock(TimeBlock block) {
        for (TimeBlockUnit unit : block.getUnitsMap().values()) {
            if (unit instanceof DoubleListTimeBlockUnit) {
                addTimeUnit(block.getTime(), (DoubleListTimeBlockUnit) unit);
            }
        }
    }

    private void addTimeUnit(double time, DoubleListTimeBlockUnit unit) {
        String varName = unit.getVarName();
        if (dataset.getSeriesIndex(varName) == -1) {
            XYSeries series = new XYSeries(varName);
            dataset.addSeries(series);
            populateSeriesPanel(dataset.getSeriesIndex(series.getKey()), series.getKey().toString());
        }
        XYSeries xyserie = dataset.getSeries(varName);
        DoubleListTimeBlockUnit doubleListUnit = (DoubleListTimeBlockUnit) unit;
        for (Double value : doubleListUnit.getValues()) {
            if (value > 0) {
                xyserie.add(time, value);
            }
        }
    }

    @Override
    public void clearData() {
        // Chart is recreated everytime so we remove all
        dataset.removeAllSeries();
        chartPanel.restoreAutoDomainBounds();
        seriesPanel.clear();
        refreshGUI();
    }

}
