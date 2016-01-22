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

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.jfree.data.xy.AbstractIntervalXYDataset;

import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.IntegerField;

public class MovingAveragePanel extends JPanel {

    private JComboBox<String> type;
    private IntegerField periodField;

    private AbstractIntervalXYDataset sourceDataSet;
    private AbstractIntervalXYDataset movingAverageDataset;

    private MovingAverageType movingAverageType = MovingAverageType.TRAILING;
    private int movingAveragePeriod = 1;

    public MovingAveragePanel(AbstractIntervalXYDataset sourceDataSet, AbstractIntervalXYDataset movingAverageDataSet) {
        super(new BorderLayout());
        this.sourceDataSet = sourceDataSet;
        this.movingAverageDataset = movingAverageDataSet;
        layoutComponents();
    }

    private void layoutComponents() {
        PanelBuilder builder = new PanelBuilder();

        type = ComponentsFactory.selectField(new String[] { MovingAverageType.TRAILING.getLabel(), MovingAverageType.CENTERED.getLabel() });
        type.setPrototypeDisplayValue(MovingAverageType.CENTERED.getLabel());
        type.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("value")) {
                    updateMovingAverageType((String) type.getSelectedItem());
                }
            }
        });
        type.setSelectedItem(movingAverageType.getLabel());
        builder.addComponent("Type", type);

        periodField = ComponentsFactory.intField(1, Integer.MAX_VALUE);
        periodField.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("value")) {
                    updateMovingAveragePeriod(periodField.getIntValue());
                }
            }
        });
        builder.addComponent("Period", periodField);

        add(builder.removeMargins().getPanel(), BorderLayout.CENTER);
    }

    private void updateMovingAveragePeriod(int period) {
        this.movingAveragePeriod = period;
        updateMovingAverageDataset();
    }

    private void updateMovingAverageType(String label) {
        this.movingAverageType = MovingAverageType.getTypeByLabel(label);
        updateMovingAverageDataset();
    }

    public void updateMovingAverageDataset() {
        MovingAverageCalculator.calculate(sourceDataSet, movingAverageDataset, movingAverageType, movingAveragePeriod);
    }

}
