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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.beans.Transient;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

public abstract class SparklineChart extends JComponent {

    private static final int SPARK_H = new JButton("-").getPreferredSize().height;
    private static final int SPARK_W = 60;

    private JFreeChart chart;
    private BufferedImage image;
    private boolean vector;
    private XYSeriesCollection dataset;

    public SparklineChart(boolean vector) {
        this.vector = vector;
        dataset = createDataset();
        XYPlot plot = createPlot();
        chart = createChart(plot);
    }

    private XYSeriesCollection createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        if (vector) {
            XYSeries xseries = new XYSeries("SparklineX");
            XYSeries yseries = new XYSeries("SparklineY");
            XYSeries zseries = new XYSeries("SparklineZ");
            dataset.addSeries(xseries);
            dataset.addSeries(yseries);
            dataset.addSeries(zseries);
        } else {
            XYSeries series = new XYSeries("Sparkline");
            dataset.addSeries(series);
        }
        return dataset;
    }

    private XYPlot createPlot() {
        NumberAxis x = new NumberAxis();
        x.setTickLabelsVisible(false);
        x.setTickMarksVisible(false);
        x.setAxisLineVisible(false);
        x.setNegativeArrowVisible(false);
        x.setPositiveArrowVisible(false);
        x.setVisible(false);

        NumberAxis y = new NumberAxis();
        y.setTickLabelsVisible(false);
        y.setTickMarksVisible(false);
        y.setAxisLineVisible(false);
        y.setNegativeArrowVisible(false);
        y.setPositiveArrowVisible(false);
        y.setVisible(false);

        XYPlot plot = new XYPlot();
        plot.setInsets(new RectangleInsets());
        plot.setDataset(dataset);
        plot.setDomainAxis(x);
        plot.setDomainGridlinesVisible(false);
        plot.setDomainCrosshairVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setRangeCrosshairVisible(false);
        plot.setRangeAxis(y);
        plot.setOutlinePaint(null);

        StandardXYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
        plot.setRenderer(renderer);

        if (vector) {
            // This "get" method will force to generate the color for each series
            // If you do not call it, the colors are generated randomly later
            // (i.e. sometimes series 0 is red, sometimes is blue...) and you loose the match with the real chart
            plot.getRenderer().getItemPaint(0, 0);
            plot.getRenderer().getItemPaint(1, 0);
            plot.getRenderer().getItemPaint(2, 0);
        }

        return plot;
    }

    private JFreeChart createChart(XYPlot plot) {
        JFreeChart c = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        c.setAntiAlias(true);
        c.setBorderVisible(true);
        c.setBorderPaint(Color.LIGHT_GRAY);
        c.setPadding(RectangleInsets.ZERO_INSETS);
        c.setBackgroundPaint(Color.WHITE);
        return c;
    }

    @Override
    @Transient
    public Dimension getPreferredSize() {
        return new Dimension(SPARK_W, SPARK_H);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }
    }

    public void updateChart() {
        clearSeries();
        addToDataSet(parseData());
        refreshImage();
    }

    protected abstract Double[][] parseData();

    private void addToDataSet(Double[][] d) {
        for (int i = 0; i < d.length; i++) {
            Double[] row = d[i];
            for (int j = 0; j < row.length - 1; j++) {
                dataset.getSeries(j).add(row[0], row[j + 1]);
            }
        }
    }

    private void clearSeries() {
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            dataset.getSeries(i).clear();
        }
    }

    private void refreshImage() {
        if (dataset.getSeries(0).isEmpty()) {
            image = null;
        } else {
            image = chart.createBufferedImage(SPARK_W, SPARK_H);
        }
    }

}