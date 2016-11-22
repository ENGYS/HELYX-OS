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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jfree.data.function.PolynomialFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import eu.engys.util.bean.AbstractBean;
import eu.engys.util.bean.BeanModel;

public class BeanSparkline extends SparklineChart implements PropertyChangeListener {

    private BeanModel<? extends AbstractBean> model;
    private String propertyName;

    public BeanSparkline(BeanModel<? extends AbstractBean> model, String propertyName) {
        super(false);
        setName(propertyName);
        this.model = model;
        this.propertyName = propertyName;
        model.addBeanPropertyChangeListener(propertyName, this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        updateChart();
    }

    @Override
    protected Double[][] parseData() {
        Object value = model.getValue(propertyName);

        Double[][] data = new Double[0][0];
        if (value instanceof double[]) {
            double[] d = (double[]) value;
            data = toPolynomialObject(d);
        } else if (value instanceof double[][]) {
            double[][] d = (double[][]) value;
            data = toObject(d);
        }
        return data;
    }

    private Double[][] toObject(double[][] d) {
        Double[][] data = new Double[d.length][d.length > 0 ? d[0].length : 0];
        for (int i = 0; i < data.length; i++) {
            Double[] row = data[i];
            for (int j = 0; j < row.length; j++) {
                data[i][j] = Double.valueOf(d[i][j]);
            }
        }
        return data;
    }

    private Double[][] toPolynomialObject(double[] d) {
        PolynomialFunction2D function = new PolynomialFunction2D(d);
        XYSeries series = DatasetUtilities.sampleFunction2DToSeries(function, 0, 1000, 100, "");
        Double[][] data = new Double[series.getItemCount()][2];
        for (int i = 0; i < series.getItemCount(); i++) {
            XYDataItem item = series.getDataItem(i);
            data[i][0] = item.getXValue();
            data[i][1] = item.getYValue();
        }
        return data;
    }

}