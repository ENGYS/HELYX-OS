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

import static eu.engys.gui.solver.postprocessing.panels.utils.SeriesInfo.SERIES_INFO_PROPERTY;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import eu.engys.util.ui.ComponentsFactory;

public class SeriesPanel extends JPanel {

    public static final String SERIES = "Series";

    private JPanel seriesPanel;

    public SeriesPanel() {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(SERIES));
        this.seriesPanel = new JPanel(new GridBagLayout());
        this.seriesPanel.setName(SERIES);
        add(seriesPanel, BorderLayout.CENTER);
    }

    public void clear() {
        seriesPanel.removeAll();
    }

    public void addSeries(final SeriesInfo info) {
        JCheckBox checkBox = ComponentsFactory.checkField(info.getSeriesKey(), info.isVisible(), info.getColor());
        checkBox.setName(info.getSeriesKey());
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox check = (JCheckBox) e.getSource();
                info.setVisible(check.isSelected());
                firePropertyChange(SERIES_INFO_PROPERTY, null, info);
            }
        });
        int seriesIndex = seriesPanel.getComponentCount();
        seriesPanel.add(checkBox, new GridBagConstraints(0, seriesIndex, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        
        revalidate();
        repaint();
    }

}
