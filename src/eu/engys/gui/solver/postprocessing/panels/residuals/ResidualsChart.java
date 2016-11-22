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
package eu.engys.gui.solver.postprocessing.panels.residuals;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlockUnit;
import eu.engys.gui.solver.postprocessing.data.DoubleListTimeBlockUnit;
import eu.engys.gui.solver.postprocessing.panels.NonPredefinedSeriesChart;
import eu.engys.util.ui.textfields.DoubleField;

public class ResidualsChart extends NonPredefinedSeriesChart {

    private static final Logger logger = LoggerFactory.getLogger(ResidualsChart.class);

    @Override
    protected JFreeChart createChart() {
        JFreeChart chart = super.createChart();

        LogarithmicAxis rangeAxis = new LogarithmicAxis("");
        rangeAxis.setNumberFormatOverride(DoubleField.getFormatForDISPLAY(10));
        rangeAxis.setAutoRangeIncludesZero(false);
        rangeAxis.setAutoRange(true);
        rangeAxis.setExpTickLabelsFlag(true);

        chart.getXYPlot().setRangeAxis(rangeAxis);
        return chart;
    }

    @Override
    protected void addTimeUnit(double time, TimeBlockUnit unit) {
        if (unit instanceof DoubleListTimeBlockUnit) {
            XYSeriesCollection dataset = addSerie(unit);

            DoubleListTimeBlockUnit doubleListUnit = (DoubleListTimeBlockUnit) unit;
            for (Double value : doubleListUnit.getValues()) {
                if (value > 0) {
                    dataset.getSeries(unit.getVarName()).add(time, value);
                }
            }
        }
    }

    @Override
    protected void addMovingAverageSerie(TimeBlockUnit unit) {
        if (unit instanceof DoubleListTimeBlockUnit) {
            _addMovingAverageSerie(unit);
        } else {
            logger.error("Invalid unit type {}", (unit != null ? unit.getClass().getCanonicalName() : "null"));
        }

    }

    @Override
    public void stop() {
    }

}
