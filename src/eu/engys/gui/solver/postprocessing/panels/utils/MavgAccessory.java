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

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.jfree.data.xy.XYDataset;

import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.IntegerField;

public class MavgAccessory extends JPanel {

    public static final String MOVING_AVERAGE_PANEL = "moving.average.panel";

    public static final String TYPE_LABEL = "Type";
    public static final String PERIOD_LABEL = "Period";

    private JComboBox<String> type;
    private IntegerField periodField;

    private XYDataset sourceDataSet;
    private XYDataset movingAverageDataset;

    private MavgType movingAverageType = MavgType.TRAILING;
    private int movingAveragePeriod = 1;

    public MavgAccessory(XYDataset sourceDataSet, XYDataset movingAverageDataSet) {
        super(new BorderLayout());
        setName(MOVING_AVERAGE_PANEL);
        this.sourceDataSet = sourceDataSet;
        this.movingAverageDataset = movingAverageDataSet;
        layoutComponents();
    }

    private void layoutComponents() {
        PanelBuilder builder = new PanelBuilder();

        type = ComponentsFactory.selectField(new String[] { MavgType.TRAILING.getLabel(), MavgType.CENTERED.getLabel() });
        type.setPrototypeDisplayValue(MavgType.CENTERED.getLabel());
        type.setSelectedItem(movingAverageType.getLabel());
        builder.addComponent(TYPE_LABEL, type);

        periodField = ComponentsFactory.intField(1, Integer.MAX_VALUE);
        builder.addComponent(PERIOD_LABEL, periodField);
        add(builder.removeMargins().getPanel(), BorderLayout.CENTER);
        
        type.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("value")) {
                    updateMovingAverageType((String) type.getSelectedItem());
                }
            }
        });

        periodField.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("value")) {
                    updateMovingAveragePeriod(periodField.getIntValue());
                }
            }
        });

    }

    private void updateMovingAveragePeriod(int period) {
        this.movingAveragePeriod = period;
        updateMovingAverageDataset();
    }

    private void updateMovingAverageType(String label) {
        this.movingAverageType = MavgType.getTypeByLabel(label);
        updateMovingAverageDataset();
    }

    public void updateMovingAverageDataset() {
        MavgCalculator.calculate(sourceDataSet, movingAverageDataset, movingAverageType, movingAveragePeriod);
    }

}
