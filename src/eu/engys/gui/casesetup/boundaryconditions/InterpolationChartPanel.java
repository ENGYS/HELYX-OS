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

package eu.engys.gui.casesetup.boundaryconditions;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlock;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlockUnit;
import eu.engys.gui.solver.postprocessing.data.DoubleTimeBlockUnit;
import eu.engys.gui.solver.postprocessing.panels.HistoryChartPanel;
import eu.engys.util.ui.textfields.DoubleField;

public class InterpolationChartPanel extends HistoryChartPanel {

    private static final Logger logger = LoggerFactory.getLogger(InterpolationChartPanel.class);

    public InterpolationChartPanel(List<String> seriesNames, String domainAxisLabel) {
        super("", seriesNames, domainAxisLabel, "", false);
    }

    @Override
    protected void createChart() {
        this.chart = ChartFactory.createXYLineChart("", "", "", dataset, PlotOrientation.VERTICAL, true, true, false);

        NumberAxis domainAxis = new NumberAxis(domainAxisLabel);
        domainAxis.setAutoRangeStickyZero(false);
        domainAxis.setAutoRangeIncludesZero(true);

        NumberAxis rangeAxis = new NumberAxis(rangeAxisLabel);
        rangeAxis.setNumberFormatOverride(DoubleField.getFormatForDISPLAY(10));

        chart.getXYPlot().setDomainAxis(domainAxis);
        chart.getXYPlot().setRangeAxis(rangeAxis);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
    }

    @Override
    protected void addTimeBlock(final TimeBlock block) {
        for (TimeBlockUnit unit : block.getUnitsMap().values()) {
            if (unit instanceof DoubleTimeBlockUnit) {
                addTimeUnit(block.getTime(), unit);
            }
        }
    }

    private void addTimeUnit(double time, TimeBlockUnit unit) {
        String varName = unit.getVarName();
        if (dataset.getSeriesIndex(varName) != -1) {
            XYSeries xyserie = dataset.getSeries(varName);
            DoubleTimeBlockUnit doubleUnit = (DoubleTimeBlockUnit) unit;
            xyserie.add(time, doubleUnit.getValue());
        } else {
            logger.error("Series not found for {}", varName);
        }
    }

    @Override
    public void initSeries() {
        super.initSeries();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        for (String varName : seriesNames) {
            renderer.setSeriesShapesVisible(dataset.getSeriesIndex(varName), true);
        }
    }

}
