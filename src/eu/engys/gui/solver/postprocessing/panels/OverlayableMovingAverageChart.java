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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.ui.TextAnchor;

import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.EventManager.Event;
import eu.engys.gui.events.EventManager.GenericEventListener;
import eu.engys.gui.events.application.ApplicationEvent;
import eu.engys.gui.solver.postprocessing.panels.utils.ChartUtils;
import eu.engys.gui.solver.postprocessing.panels.utils.CrosshairListener;
import eu.engys.gui.solver.postprocessing.panels.utils.MarkersTable;
import eu.engys.gui.solver.postprocessing.panels.utils.events.AddMarkerEvent;
import eu.engys.gui.solver.postprocessing.panels.utils.events.CrosshairChangedEvent;
import eu.engys.gui.solver.postprocessing.panels.utils.events.ShowCrosshairEvent;
import eu.engys.gui.solver.postprocessing.panels.utils.events.ShowMarkersTableEvent;
import eu.engys.gui.solver.postprocessing.panels.utils.markers.Marker;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;

public abstract class OverlayableMovingAverageChart extends MovingAverageChart implements GenericEventListener {

    private static final Color MARKER_COLOR = Color.BLACK;
    private static final BasicStroke MARKER_STROKE = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{1.0f}, 0.0f);

    private CrosshairOverlay overlay;
    private MarkersTable markersTable;
    private CrosshairListener crosshairListener;
    private JSplitPane mainPanel;

    private List<Marker> markers;

    public OverlayableMovingAverageChart() {
        super();
        this.markers = new ArrayList<Marker>();
        EventManager.registerEventListener(this, ApplicationEvent.class);
    }

    @Override
    public void layoutComponents() {
        super.layoutComponents();

        this.overlay = ChartUtils.createOverlay();
        getChartPanel().addOverlay(overlay);

        this.markersTable = new MarkersTable(getChartPanel());
        this.crosshairListener = new CrosshairListener(getChartPanel(), overlay);

        this.mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.mainPanel.setOneTouchExpandable(false);
        this.mainPanel.setTopComponent(super.getPanel());
        this.mainPanel.setBottomComponent(markersTable.getPanel());

        setMarkersTableVisible(false);
    }

    @Override
    public void eventTriggered(Object obj, final Event event) {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (event instanceof ShowCrosshairEvent) {
                    // from crosshair button in toolbar
                    setCrosshairVisible(((ShowCrosshairEvent) event).isActive());
                } else if (event instanceof ShowMarkersTableEvent) {
                    // from X button on table
                    setMarkersTableVisible(((ShowMarkersTableEvent) event).isVisible());
                } else if (event instanceof AddMarkerEvent) {
                    addMarker(((AddMarkerEvent) event).getX());
                } else if (event instanceof CrosshairChangedEvent) {
                    Marker oldValue = ((CrosshairChangedEvent) event).getOldValue();
                    Marker newValue = ((CrosshairChangedEvent) event).getNewValue();
                    markers.remove(oldValue);
                    markers.add(newValue);

                    loadMarkers();
                    markersTable.loadMarkers(markers);
                }
            }
        });
    }

    @Override
    public void _clearData() {
        super._clearData();
        clearMarkers();
    }

    private void clearMarkers() {
        markers.clear();
        loadMarkers();
        markersTable.clear();
    }

    @Override
    public void postAddToDataSet() {
        super.postAddToDataSet();
        markersTable.loadMarkers(markers);
    }

    private void addMarker(Double iteration) {
        markers.add(new Marker(iteration));
        loadMarkers();
        markersTable.loadMarkers(markers);
        setMarkersTableVisible(true);
    }

    private void loadMarkers() {
        getChartPanel().getChart().getXYPlot().clearDomainMarkers();
        for (Marker marker : markers) {
            ValueMarker valueMarker = new ValueMarker(marker.getValue(), MARKER_COLOR, MARKER_STROKE);
            valueMarker.setLabel(ChartUtils.SERIES_DECIMAL_FORMAT.format(marker.getValue()));
            valueMarker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
            getXYPlot().addDomainMarker(valueMarker);
        }
    }

    // public for test purposes only
    public void setCrosshairVisible(boolean visible) {
        if (visible) {
            for (EventListener chartListener : getChartPanel().getListeners(ChartMouseListener.class)) {
                if (chartListener instanceof CrosshairListener) {
                    getChartPanel().removeChartMouseListener((ChartMouseListener) chartListener);
                }
            }
            for (MouseListener mouseListener : getChartPanel().getMouseListeners()) {
                if (mouseListener instanceof CrosshairListener) {
                    getChartPanel().removeMouseListener(mouseListener);
                }
            }
            getChartPanel().addChartMouseListener(crosshairListener);
            getChartPanel().addMouseListener(crosshairListener);
        } else {
            getChartPanel().removeChartMouseListener(crosshairListener);
            getChartPanel().removeMouseListener(crosshairListener);

            ((Crosshair) overlay.getDomainCrosshairs().get(0)).setValue(Double.NaN);
            ((Crosshair) overlay.getRangeCrosshairs().get(0)).setValue(Double.NaN);
        }
    }

    // public for test purposes only
    public void setMarkersTableVisible(boolean visible) {
        if (visible) {
            UiUtil.expandSplitPane(mainPanel, markersTable.getPanel(), 0.9, 100);
        } else {
            clearMarkers();
            UiUtil.collapseSplitPane(mainPanel);
        }

    }

    public List<Marker> getMarkers() {
        return markers;
    }

    @Override
    public JComponent getPanel() {
        return mainPanel;
    }

}
