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


package eu.engys.vtk;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.vecmath.Point3d;

import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.gui.events.EventManager.Event;
import eu.engys.gui.events.view3D.AxisEvent;
import eu.engys.gui.events.view3D.BoxEvent;
import eu.engys.gui.events.view3D.LayersCoverageEvent;
import eu.engys.gui.events.view3D.MeshQualityEvent;
import eu.engys.gui.events.view3D.PlaneEvent;
import eu.engys.gui.events.view3D.PointEvent;
import eu.engys.gui.events.view3D.SelectionEvent;
import eu.engys.gui.events.view3D.VolumeReportEvent;
import eu.engys.gui.events.view3D.VolumeReportVisibilityEvent;
import eu.engys.gui.events.view3D.VolumeReportVisibilityEvent.Kind;
import eu.engys.gui.view3D.CanvasPanel;
import eu.engys.gui.view3D.LayerInfo;
import eu.engys.gui.view3D.QualityInfo;
import eu.engys.gui.view3D.Selection;
import eu.engys.gui.view3D.View3DEventListener;
import eu.engys.util.ui.textfields.DoubleField;

public class HelyxView3DEventListener implements View3DEventListener {

	private CanvasPanel view3D;

	public HelyxView3DEventListener(CanvasPanel view3DPanel) {
		this.view3D = view3DPanel;
	}

	@Override
	public void eventTriggered(Object obj, final Event event) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (event instanceof PointEvent) {
					DoubleField[] point = ((PointEvent) event).getPoint();
					String key = ((PointEvent) event).getKey();
					EventActionType action = ((PointEvent) event).getAction();
					Color color = ((PointEvent) event).getColor();
					view3D.showPoint(point, key, action, color);
				} else if (event instanceof AxisEvent) {
					AxisEvent e = (AxisEvent) event;
					DoubleField[] origin = e.getAxisInfo().getCenter();
					DoubleField[] normal = e.getAxisInfo().getAxis();
					EventActionType action = e.getAxisInfo().getAction();
					view3D.showAxis(origin, normal, action);
				} else if (event instanceof PlaneEvent) {
                    DoubleField[] origin = ((PlaneEvent) event).getOrigin();
                    DoubleField[] normal = ((PlaneEvent) event).getNormal();
                    EventActionType action = ((PlaneEvent) event).getAction();
                    boolean interactive = ((PlaneEvent) event).isInteractive();
                    if (interactive) {
                        view3D.showPlane(origin, normal, action);
                    } else {
                        view3D.showPlaneDisplay(origin, normal, action);
                    }
                } else if (event instanceof BoxEvent) {
                    DoubleField[] min = ((BoxEvent) event).getMin();
                    DoubleField[] max = ((BoxEvent) event).getMax();
                    EventActionType action = ((BoxEvent) event).getAction();
                    view3D.showBox(min, max, action);
                } else if (event instanceof SelectionEvent) {
                    Selection selection = ((SelectionEvent) event).getSelection();
                    EventActionType action = ((SelectionEvent) event).getAction();
                    view3D.activateSelection(selection, action);
                } else if (event instanceof MeshQualityEvent) {
                    QualityInfo qualityInfo = ((MeshQualityEvent) event).getQualityInfo();
                    EventActionType action = ((MeshQualityEvent) event).getAction();
                    view3D.showQualityFields(qualityInfo, action);
                }  else if (event instanceof LayersCoverageEvent) {
                    EventActionType action = ((LayersCoverageEvent) event).getAction();
                    JPanel colorBar = ((LayersCoverageEvent) event).getColorBar();
                    LayerInfo layerInfo = ((LayersCoverageEvent) event).getLayerInfo();
                    view3D.showLayersCoverage(layerInfo, colorBar, action);
                } else if (event instanceof VolumeReportEvent) {
					String varName = VolumeReportEvent.class.cast(event).getVarName();
					Point3d min = VolumeReportEvent.class.cast(event).getMinAtLocation();
					Point3d max = VolumeReportEvent.class.cast(event).getMaxAtLocation();
					view3D.updateMinAndMaxForFields(varName, min, max);
				} else if (event instanceof VolumeReportVisibilityEvent) {
					String key = VolumeReportVisibilityEvent.class.cast(event).getKey();
					boolean visible = VolumeReportVisibilityEvent.class.cast(event).isVisible();
					Kind kind = VolumeReportVisibilityEvent.class.cast(event).getKind();
					view3D.showMinMaxFieldPoints(key, kind, visible);
				}
			}
		});
	}
}
