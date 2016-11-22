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

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class NormalizedWeightDataSet extends XYSeriesCollection {

    /*
     * This class fixes a bug of JFreeChart that causes a strange rendering behaviour when using the method "setPlotArea(true|false)" or even worst the method setSeriesVisible(true | false) 
     * The fix consists in adding/removing the series when toggling its visibility
     */

    private XYSeries backupSeries;

    public void backupAndRemoveSerie() {
        if (getSeriesCount() > 0) {
            removeSeries(backupSeries = getSeries(0));
        }

    }

    public void restoreSerie() {
        if (backupSeries != null) {
            addSeries(backupSeries);
        }
    }

    public boolean isCompletelyRemoved(Comparable key){
        if(getSeriesIndex(key) == -1 && backupSeries == null){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public XYSeries getSeries(Comparable key) {
        if(backupSeries == null)
            return super.getSeries(key);
        else{
            return backupSeries;
        }
    }

    @Override
    public XYSeries getSeries(int series) {
        if(super.getSeries(series) != null){
            return super.getSeries(series);
        }else {
            return backupSeries;
        }
    }

    @Override
    public void removeAllSeries() {
        this.backupSeries = null;
        super.removeAllSeries();
    }
}
