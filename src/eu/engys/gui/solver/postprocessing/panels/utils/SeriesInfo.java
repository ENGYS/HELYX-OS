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

import java.awt.Color;

import org.jfree.chart.plot.XYPlot;

public class SeriesInfo {
    
    public static final String SERIES_INFO_PROPERTY = "series.info";
    
    private XYPlot plot;
    private Integer datasetIndex;
    private String seriesKey;
    private boolean visible;
    private Color color;
    
    public SeriesInfo(XYPlot plot, Integer datasetIndex, String seriesKey, Color color, boolean visible) {
        this.plot = plot;
        this.datasetIndex = datasetIndex;
        this.seriesKey = seriesKey;
        this.color = color;
        this.visible = visible;
    }

    public XYPlot getPlot() {
        return plot;
    }
    
    public Integer getDatasetIndex() {
        return datasetIndex;
    }
    
    public String getSeriesKey() {
        return seriesKey;
    }   

    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    
    public Color getColor() {
        return color;
    }
    
}
