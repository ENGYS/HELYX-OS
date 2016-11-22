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

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlock;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlockUnit;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;

public abstract class NonPredefinedSeriesChart extends HistoryChart {

    public NonPredefinedSeriesChart() {
        super();
    }

    @Override
    public void _addToDataSet(TimeBlocks list) {
        for (TimeBlock block : list) {
            for (TimeBlockUnit unit : block.getUnitsMap().values()) {
                addTimeUnit(block.getTime(), unit);
            }
        }
        for (TimeBlock block : list) {
            for (TimeBlockUnit unit : block.getUnitsMap().values()) {
                addMovingAverageSerie(unit);
            }
        }
    }

    protected abstract void addTimeUnit(double time, TimeBlockUnit unit);

    protected abstract void addMovingAverageSerie(TimeBlockUnit unit);
    
    protected XYSeriesCollection addSerie(TimeBlockUnit unit) {
        XYSeriesCollection dataset = getXYDataset(DATASET_INDEX);
        if (dataset.getSeriesIndex(unit.getVarName()) == -1) {
            XYSeries serie = new XYSeries(unit.getVarName());
            serie.setNotify(false);
            dataset.addSeries(serie);
            
            applyGraphicProperties(unit.getVarName());
            populateSeriesPanel(unit.getVarName());
        }
        return dataset;
    }
    
    protected void _addMovingAverageSerie(TimeBlockUnit unit) {
        String varName = unit.getVarName();
        XYSeriesCollection mavgDataset = getXYDataset(MOVING_AVERAGE_DATASET_INDEX);
        if (mavgDataset.getSeriesIndex(varName) == -1) {
            XYSeries serie = new XYSeries(varName);
            serie.setNotify(false);
            movingAverageDataSet.addSeries(serie);
            
            applyMavgGraphicProperties(varName);
            populateMovingAverageSeriesPanel(varName);
        }
    }

}