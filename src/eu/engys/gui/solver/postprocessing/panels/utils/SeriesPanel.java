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
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.renderer.xy.XYItemRenderer;

import eu.engys.util.ui.ComponentsFactory;

public class SeriesPanel extends JPanel {

    private Map<String, Boolean> seriesVisibility = new HashMap<>();
    private Map<String, Boolean> movingAverageSeriesVisibility = new HashMap<>();
    private JPanel seriesPanel;
    private JPanel seriesAveragePanel;

    public SeriesPanel(JPanel movingAveragePanel) {
        super();
        seriesPanel = new JPanel(new GridBagLayout());
        seriesAveragePanel = new JPanel(new GridBagLayout());

        if (movingAveragePanel != null) {
            setLayout(new GridLayout(2, 1));

            JPanel movingAveragepanel = new JPanel(new BorderLayout());
            movingAveragepanel.setBorder(BorderFactory.createTitledBorder("Moving Average"));
            movingAveragepanel.add(movingAveragePanel, BorderLayout.NORTH);
            movingAveragepanel.add(seriesAveragePanel, BorderLayout.CENTER);

            seriesPanel.setBorder(BorderFactory.createTitledBorder("Series"));
            add(seriesPanel);
            add(movingAveragepanel);
        } else {
            setLayout(new BorderLayout());
            add(seriesPanel, BorderLayout.CENTER);
        }
    }

    public void clear() {
        // do not clear visibility maps
        seriesPanel.removeAll();
        seriesAveragePanel.removeAll();
    }

    public void addSeries(final XYItemRenderer renderer, final int seriesIndex, final String label) {
        boolean visible = true;
        if (seriesVisibility.containsKey(label)) {
            visible = seriesVisibility.get(label);
        } else {
            seriesVisibility.put(label, visible);
        }
        final JCheckBox checkBox = ComponentsFactory.checkField(label, visible, getColorOfSeries(renderer, seriesIndex));
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox.isSelected()) {
                    seriesVisibility.put(label, true);
                    renderer.setSeriesVisible(seriesIndex, true, true);
                } else {
                    seriesVisibility.put(label, false);
                    renderer.setSeriesVisible(seriesIndex, false, true);
                }
            }
        });
        seriesPanel.add(checkBox, new GridBagConstraints(0, seriesIndex, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    public void addMovingAverageSeries(final XYItemRenderer renderer, final int seriesIndex, final String label) {
        boolean visible = false;
        if (movingAverageSeriesVisibility.containsKey(label)) {
            visible = movingAverageSeriesVisibility.get(label);
        } else {
            movingAverageSeriesVisibility.put(label, visible);
        }
        final JCheckBox checkBox = ComponentsFactory.checkField(label, visible, getColorOfSeries(renderer, seriesIndex));
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox.isSelected()) {
                    seriesVisibility.put(label, true);
                    renderer.setSeriesVisible(seriesIndex, true, true);
                } else {
                    seriesVisibility.put(label, false);
                    renderer.setSeriesVisible(seriesIndex, false, true);
                }
            }
        });

        seriesAveragePanel.add(checkBox, new GridBagConstraints(0, seriesIndex, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        renderer.setSeriesVisible(seriesIndex, visible, true);
    }

    // Used to colour points in the 3D
    public static Color getColorOfSeries(XYItemRenderer renderer, int seriesIndex) {
        Color colorWithTransparency = (Color) renderer.getItemPaint(seriesIndex, 0);
        return new Color(colorWithTransparency.getRed(), colorWithTransparency.getGreen(), colorWithTransparency.getBlue());
    }

}
