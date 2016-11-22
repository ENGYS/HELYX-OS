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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import eu.engys.gui.events.EventManager;
import eu.engys.gui.solver.postprocessing.panels.utils.events.AddMarkerEvent;

public class CrosshairListener extends MouseAdapter implements ChartMouseListener {

    private ChartPanel chartPanel;
    private CrosshairOverlay overlay;

    public CrosshairListener(ChartPanel chartPanel, CrosshairOverlay overlay) {
        this.chartPanel = chartPanel;
        this.overlay = overlay;
    }
    
    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        XYPlot xyPlot = chartPanel.getChart().getXYPlot();
        if(xyPlot.getDataset().getSeriesCount() == 0){
            // do nothing
        } else {
            double x = xyPlot.getDomainAxis().java2DToValue(event.getTrigger().getX(), chartPanel.getScreenDataArea(), RectangleEdge.BOTTOM);
            EventManager.triggerEvent(this, new AddMarkerEvent(x));
        }
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        if (plot.getDataset().getSeriesCount() > 0) {
            double x = plot.getDomainAxis().java2DToValue(event.getTrigger().getX(), chartPanel.getScreenDataArea(), RectangleEdge.BOTTOM);
            // make the crosshairs disappear if the mouse is out of range
            if (!plot.getDomainAxis().getRange().contains(x)) {
                x = Double.NaN;
            }
            ((Crosshair) overlay.getDomainCrosshairs().get(0)).setValue(x);

            double y = getClosestY(x, event.getTrigger().getY());
            ((Crosshair) overlay.getRangeCrosshairs().get(0)).setValue(y);
        }
    }

    private double getClosestY(double x, int compare) {
        XYPlot xyPlot = chartPanel.getChart().getXYPlot();

        int series = 0;
        XYDataset closestDataset = xyPlot.getDataset();

        double distance = Double.MAX_VALUE;

        for (int i = 0; i < xyPlot.getDatasetCount(); i++) {
            XYDataset dataset = xyPlot.getDataset(i);
            for (int j = 0; j < dataset.getSeriesCount(); j++) {
                double y = DatasetUtilities.findYValue(dataset, j, x);
                double toCompare = xyPlot.getRangeAxis().java2DToValue(compare, chartPanel.getScreenDataArea(), xyPlot.getRangeAxisEdge());

                if (Math.abs(y - toCompare) < distance) {
                    distance = Math.abs(y - toCompare);
                    closestDataset = dataset;
                    series = j;
                }
            }
        }
        return DatasetUtilities.findYValue(closestDataset, series, x);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ((Crosshair) overlay.getDomainCrosshairs().get(0)).setValue(Double.NaN);
        ((Crosshair) overlay.getRangeCrosshairs().get(0)).setValue(Double.NaN);
    }

}
