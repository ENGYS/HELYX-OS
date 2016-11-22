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

import java.util.List;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlock;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlockUnit;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;

public abstract class PredefinedSeriesChart extends HistoryChart {

    protected List<String> seriesNames;

    public PredefinedSeriesChart(List<String> seriesNames) {
        super();
        this.seriesNames = seriesNames;
    }

    @Override
    public void _addToDataSet(TimeBlocks list) {
        for (TimeBlock block : list) {
            XYSeriesCollection dataset = getXYDataset(DATASET_INDEX);
            for (String serieName : seriesNames) {
                if (dataset.getSeriesIndex(serieName) == -1) {
                    XYSeries serie = new XYSeries(serieName);
                    serie.setNotify(false);
                    dataset.addSeries(serie);
                    applyGraphicProperties(serieName);
                    populateSeriesPanel(serieName);
                }
                addTimeUnit(block.getTime(), block.getUnitsMap().get(serieName));
            }
            for (String varName : seriesNames) {
                if (movingAverageDataSet.getSeriesIndex(varName) == -1) {
                    XYSeries serie = new XYSeries(varName);
                    serie.setNotify(false);
                    movingAverageDataSet.addSeries(serie);
                    applyMavgGraphicProperties(varName);
                    populateMovingAverageSeriesPanel(varName);
                }
            }
        }
    }

    protected abstract void addTimeUnit(double time, TimeBlockUnit unit);

    public List<String> getSeriesNames() {
        return seriesNames;
    }

}