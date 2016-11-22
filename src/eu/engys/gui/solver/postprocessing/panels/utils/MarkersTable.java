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
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.JTextComponent;

import org.jfree.chart.ChartPanel;

import eu.engys.gui.events.EventManager;
import eu.engys.gui.solver.postprocessing.panels.utils.events.ShowMarkersTableEvent;
import eu.engys.gui.solver.postprocessing.panels.utils.markers.Marker;
import eu.engys.gui.solver.postprocessing.panels.utils.markers.MarkersTableModel;
import eu.engys.gui.table.editors.DoubleCellEditor;
import eu.engys.gui.table.renderers.DoubleCellRender;
import eu.engys.util.ui.CopyPasteSupport;
import eu.engys.util.ui.TitledBorderWithAction;

public class MarkersTable extends JTable {

    private static final String MARKERS = "Markers";
	public static final String MARKERS_TABLE = "markers.table";
    private JComponent mainPanel;

    public MarkersTable(ChartPanel chartPanel) {
        super();

        setName(MARKERS_TABLE);
        setModel(new MarkersTableModel(chartPanel));

        setDefaultEditor(Double.class, new DoubleCellEditor());
        setDefaultRenderer(Double.class, new DoubleCellRender());

        ((DefaultTableCellRenderer) tableHeader.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        CopyPasteSupport.addSupportTo(this);

        layoutComponents();
    }
    
    public void loadMarkers(List<Marker> markers) {
        ((MarkersTableModel) getModel()).load(markers);
    }

    public void clear() {
        ((MarkersTableModel) getModel()).load(Collections.<Marker>emptyList());
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        boolean result = super.editCellAt(row, column, e);
        final Component editor = getEditorComponent();
        if (e instanceof KeyEvent && editor instanceof JTextComponent) {
            ((JTextComponent) editor).selectAll();
        }
        return result;
    }

    private void layoutComponents() {
        JPanel tablePanel = new JPanel(new BorderLayout());

        tablePanel.add(tableHeader, BorderLayout.NORTH);
        tablePanel.add(this, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(tablePanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new TitledBorderWithAction(MARKERS, mainPanel, new Runnable() {
            @Override
            public void run() {
                EventManager.triggerEvent(this, new ShowMarkersTableEvent(false));
            }
        }));
        mainPanel.add(scroll, BorderLayout.CENTER);

    }

    public JComponent getPanel() {
        return mainPanel;
    }

}
