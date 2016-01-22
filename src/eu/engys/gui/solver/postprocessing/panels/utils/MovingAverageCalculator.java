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

package eu.engys.gui.solver.postprocessing.panels.utils;

import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalDataItem;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

public class MovingAverageCalculator {

    public static void calculate(AbstractIntervalXYDataset sourceDataset, AbstractIntervalXYDataset movingAverageDataSet, MovingAverageType type, int period) {
        if (type.isTrailing()) {
            if(sourceDataset instanceof XYSeriesCollection){
                calculateTrailing((XYSeriesCollection)sourceDataset, (XYSeriesCollection)movingAverageDataSet, period);
            } else if(sourceDataset instanceof YIntervalSeriesCollection){
                calculateTrailing((YIntervalSeriesCollection)sourceDataset, (XYSeriesCollection)movingAverageDataSet, period);
            }
        } else {
            if(sourceDataset instanceof XYSeriesCollection){
                calculateCentral((XYSeriesCollection)sourceDataset, (XYSeriesCollection)movingAverageDataSet, period);
            } else if(sourceDataset instanceof YIntervalSeriesCollection){
                calculateCentral((YIntervalSeriesCollection)sourceDataset, (XYSeriesCollection)movingAverageDataSet, period);
            }
        }
    }

    /*
     * XY SERIES
     */

    // Sum of the left N-values, divided by N
    private static void calculateTrailing(XYSeriesCollection sourceDataset, XYSeriesCollection movingAverageDataSet, int period) {
        movingAverageDataSet.removeAllSeries();

        for (int i = 0; i < sourceDataset.getSeriesCount(); i++) {
            XYSeries origSeries = sourceDataset.getSeries(i);
            XYSeries maSeries = new XYSeries(origSeries.getKey() + "-MAVG");

            for (int j = 0; j < origSeries.getItemCount(); j++) {
                XYDataItem origItem = origSeries.getDataItem(j);
                if (j - (period - 1) < 0) {
                    // do nothing
                } else {
                    double yValue = 0;
                    for (int k = (j - (period - 1)); k <= j; k++) {
                        yValue += origSeries.getDataItem(k).getYValue();
                    }
                    maSeries.add(origItem.getXValue(), (yValue / period));
                }
            }
            movingAverageDataSet.addSeries(maSeries);
        }
    }

    // Sum of the N/2 left-values and of the N/2 right-values, divided by N
    private static void calculateCentral(XYSeriesCollection sourceDataset, XYSeriesCollection movingAverageDataSet, int period) {
        movingAverageDataSet.removeAllSeries();
        for (int i = 0; i < sourceDataset.getSeriesCount(); i++) {
            XYSeries origSeries = sourceDataset.getSeries(i);
            XYSeries maSeries = new XYSeries(origSeries.getKey() + "-MAVG");

            int limit = (int) Math.floor(period / 2);

            for (int j = 0; j < origSeries.getItemCount(); j++) {
                XYDataItem origItem = origSeries.getDataItem(j);
                if (j - limit < 0) {
                    // do nothing
                } else if (j + limit >= origSeries.getItemCount()) {
                    // do nothing
                } else {
                    double yValue = 0;
                    for (int k = (j - limit); k <= (j + limit); k++) {
                        yValue += origSeries.getDataItem(k).getYValue();
                    }
                    maSeries.add(origItem.getXValue(), (yValue / period));
                }
            }
            movingAverageDataSet.addSeries(maSeries);
        }
    }

    /*
     * YSERIES
     */

    private static void calculateTrailing(YIntervalSeriesCollection sourceDataset, XYSeriesCollection movingAverageDataSet, int period) {
        movingAverageDataSet.removeAllSeries();

        for (int i = 0; i < sourceDataset.getSeriesCount(); i++) {
            YIntervalSeries origSeries = sourceDataset.getSeries(i);
            XYSeries maSeries = new XYSeries(origSeries.getKey() + "-MAVG");

            for (int j = 0; j < origSeries.getItemCount(); j++) {
                YIntervalDataItem origItem = (YIntervalDataItem) origSeries.getDataItem(j);
                if (j - (period - 1) < 0) {
                    // do nothing
                } else {
                    double yValue = 0;
                    for (int k = (j - (period - 1)); k <= j; k++) {
                        yValue += ((YIntervalDataItem) origSeries.getDataItem(k)).getYValue();
                    }
                    maSeries.add(origItem.getX().doubleValue(), (yValue / period));
                }
            }
            movingAverageDataSet.addSeries(maSeries);
        }
    }

    private static void calculateCentral(YIntervalSeriesCollection sourceDataset, XYSeriesCollection movingAverageDataSet, int period) {
        movingAverageDataSet.removeAllSeries();
        for (int i = 0; i < sourceDataset.getSeriesCount(); i++) {
            YIntervalSeries origSeries = sourceDataset.getSeries(i);
            XYSeries maSeries = new XYSeries(origSeries.getKey() + "-MAVG");

            int limit = (int) Math.floor(period / 2);

            for (int j = 0; j < origSeries.getItemCount(); j++) {
                YIntervalDataItem origItem = (YIntervalDataItem) origSeries.getDataItem(j);
                if (j - limit < 0) {
                    // do nothing
                } else if (j + limit >= origSeries.getItemCount()) {
                    // do nothing
                } else {
                    double yValue = 0;
                    for (int k = (j - limit); k <= (j + limit); k++) {
                        yValue += ((YIntervalDataItem) origSeries.getDataItem(k)).getYValue();
                    }
                    maSeries.add(origItem.getX().doubleValue(), (yValue / period));
                }
            }
            movingAverageDataSet.addSeries(maSeries);
        }
    }
}
