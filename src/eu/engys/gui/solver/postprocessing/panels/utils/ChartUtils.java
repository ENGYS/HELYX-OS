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
package eu.engys.gui.solver.postprocessing.panels.utils;

import static eu.engys.core.modules.AbstractChart.DATASET_INDEX;
import static eu.engys.core.modules.AbstractChart.WEIGHTING_DATASET_INDEX;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;

import org.jfree.chart.labels.CrosshairLabelGenerator;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.SeriesDataset;
import org.jfree.data.xy.XYDataset;

import eu.engys.core.project.state.State;

public class ChartUtils {

    public static final Color SERIES_RED = new Color(255, 85, 85);
    public static final Color SERIES_BLUE = new Color(85, 85, 255);
    public static final Color SERIES_GREEN = new Color(85, 255, 85);

    private static final String STEADY_DOMAIN_AXIS_LABEL = "Iteration [-]";
    private static final String TRANSIENT_DOMAIN_AXIS_LABEL = "Time [s]";

    private static final Color CROSSHAIR_COLOR = Color.BLACK;
    private static final BasicStroke CROSSHAIR_STROKE = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 0.0f);

    public static final DecimalFormat SERIES_DECIMAL_FORMAT = new DecimalFormat("#,###,##0.0###");

    public static int getSeriesIndex(SeriesDataset dataset, String key) {
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            String seriesKey = (String) dataset.getSeriesKey(i);
            if (key.equals(seriesKey)) {
                return i;
            }
        }
        return -1;
    }

    public static Color colorForSerie(XYPlot plot, int seriesIndex) {
        XYItemRenderer baseRenderer = plot.getRenderer(DATASET_INDEX);
        Color colorWithTransparency = (Color) baseRenderer.getItemPaint(seriesIndex, 0);
        return new Color(colorWithTransparency.getRed(), colorWithTransparency.getGreen(), colorWithTransparency.getBlue());
    }

    public static Color coloForMAVGSerie(XYPlot plot, int seriesIndex) {
        XYItemRenderer baseRenderer = plot.getRenderer(DATASET_INDEX);
        Color colorWithTransparency = ((Color) baseRenderer.getItemPaint(seriesIndex, 0)).darker().darker();
        return new Color(colorWithTransparency.getRed(), colorWithTransparency.getGreen(), colorWithTransparency.getBlue());
    }

    public static Color coloForWeightedSerie(XYPlot plot, int seriesIndex) {
        XYItemRenderer baseRenderer = plot.getRenderer(WEIGHTING_DATASET_INDEX);
        Color colorWithTransparency = ((Color) baseRenderer.getItemPaint(seriesIndex, 0));
        return new Color(colorWithTransparency.getRed(), colorWithTransparency.getGreen(), colorWithTransparency.getBlue());
    }
    
    public static void applyGraphicProperties(XYPlot plot, SeriesInfo info){
        setSeriesVisible(plot, info.getDatasetIndex(), info.getSeriesKey(), info.isVisible());
        setSeriesColor(plot, info.getDatasetIndex(), info.getSeriesKey(), info.getColor(), 1);
    }

    public static void applyWeightedGraphicProperties(XYPlot plot, SeriesInfo info){
        setSeriesVisible(plot, info.getDatasetIndex(), info.getSeriesKey(), info.isVisible());
//        setSeriesColor(plot, info.getDatasetIndex(), info.getSeriesKey(), info.getColor(), 1);
    }

    public static void setSeriesVisible(XYPlot plot, int datasetIndex, String seriesKey, boolean visible) {
        XYDataset dataset = plot.getDataset(datasetIndex);
        XYItemRenderer renderer = plot.getRenderer(datasetIndex);
        if (dataset instanceof NormalizedWeightDataSet) {
            if (visible) {
                ((NormalizedWeightDataSet) dataset).restoreSerie();
            } else {
                ((NormalizedWeightDataSet) dataset).backupAndRemoveSerie();
            }
        } else {
            renderer.setSeriesVisible(getSeriesIndex(dataset, seriesKey), visible, true);
        }
    }

    private static void setSeriesColor(XYPlot plot, int datasetIndex, String seriesKey, Color color, int strokeWidth) {
        int seriesIndex = getSeriesIndex(plot.getDataset(datasetIndex), seriesKey);
        plot.getRenderer(datasetIndex).setSeriesPaint(seriesIndex, color);
        plot.getRenderer(datasetIndex).setSeriesStroke(seriesIndex, new BasicStroke(strokeWidth));
    }

    public static void updateDomainAxisLabel(State state, XYPlot plot) {
        plot.getDomainAxis().setLabel(state.isSteady() ? STEADY_DOMAIN_AXIS_LABEL : TRANSIENT_DOMAIN_AXIS_LABEL);
    }

    public static CrosshairOverlay createOverlay() {
        CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
        Crosshair xCrosshair = new Crosshair(Double.NaN, CROSSHAIR_COLOR, CROSSHAIR_STROKE);
        xCrosshair.setLabelGenerator(new CrosshairLabelGenerator() {
            @Override
            public String generateLabel(Crosshair crosshair) {
                return ChartUtils.SERIES_DECIMAL_FORMAT.format(crosshair.getValue());
            }
        });
        xCrosshair.setLabelBackgroundPaint(Color.WHITE);
        xCrosshair.setLabelOutlineVisible(false);
        xCrosshair.setLabelVisible(true);

        Crosshair yCrosshair = new Crosshair(Double.NaN, CROSSHAIR_COLOR, CROSSHAIR_STROKE);
        yCrosshair.setLabelGenerator(new CrosshairLabelGenerator() {
            @Override
            public String generateLabel(Crosshair crosshair) {
                return ChartUtils.SERIES_DECIMAL_FORMAT.format(crosshair.getValue());
            }
        });
        yCrosshair.setLabelBackgroundPaint(Color.WHITE);
        yCrosshair.setLabelOutlineVisible(false);
        yCrosshair.setLabelVisible(true);

        crosshairOverlay.addDomainCrosshair(xCrosshair);
        crosshairOverlay.addRangeCrosshair(yCrosshair);
        return crosshairOverlay;
    }

}
