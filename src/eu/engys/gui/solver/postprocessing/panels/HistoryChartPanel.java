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

package eu.engys.gui.solver.postprocessing.panels;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import eu.engys.util.ui.textfields.DoubleField;

public abstract class HistoryChartPanel extends MovingAverageChartPanel<XYSeriesCollection> {

    private static final long serialVersionUID = 1L;

    protected List<String> seriesNames;

    public HistoryChartPanel(String title, List<String> seriesNames, String domainAxisLabel, String rangeAxisLabel, boolean showMovingAverage) {
        super(title, domainAxisLabel, rangeAxisLabel, showMovingAverage);
        this.seriesNames = seriesNames;
        this.dataset = new XYSeriesCollection();
    }

    @Override
    public void initSeries() {
        for (String serieName : seriesNames) {
            XYSeries series = new XYSeries(serieName);
            dataset.addSeries(series);
            populateSeriesPanel(dataset.getSeriesIndex(series.getKey()), series.getKey().toString());
        }
    }

    @Override
    protected void createChart() {
        this.chart = ChartFactory.createXYLineChart("", "", "", dataset, PlotOrientation.VERTICAL, true, true, false);

        NumberAxis domainAxis = new NumberAxis(domainAxisLabel);
        domainAxis.setAutoRangeIncludesZero(false);

        NumberAxis rangeAxis = new NumberAxis(rangeAxisLabel);
        rangeAxis.setNumberFormatOverride(DoubleField.getFormatForDISPLAY(10));

        chart.getXYPlot().setDomainAxis(domainAxis);
        chart.getXYPlot().setRangeAxis(rangeAxis);
        chart.getXYPlot().setDataset(1, movingAverageDataSet);

        StandardXYItemRenderer movingAverageRenderer = new StandardXYItemRenderer();
        movingAverageRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        chart.getXYPlot().setRenderer(1, movingAverageRenderer);
    }
    
    @Override
    protected void clearDataset() {
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            dataset.getSeries(i).clear();
        }
    }

}
