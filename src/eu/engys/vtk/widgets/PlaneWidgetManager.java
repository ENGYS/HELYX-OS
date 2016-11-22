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
package eu.engys.vtk.widgets;

import java.util.HashMap;
import java.util.Map;

import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.vtk.VTKRenderPanel;

public class PlaneWidgetManager {

	private Map<String, PlaneWidget> widgetMap = new HashMap<>();
	private Map<String, PlaneDisplayWidget> widgetDisplayMap = new HashMap<>();
	private VTKRenderPanel vtkRendererPanel;

	public PlaneWidgetManager(VTKRenderPanel vtkRendererPanel) {
		this.vtkRendererPanel = vtkRendererPanel;
	}

	public void clear() {
		for (PlaneWidget w : widgetMap.values()) {
			w.clear();
		}
		for (PlaneDisplayWidget w : widgetDisplayMap.values()) {
			w.clear();
		}
		widgetMap.clear();
		widgetDisplayMap.clear();
	}

	public void showPlane(String key, DoubleField[] origin, DoubleField[] normal, EventActionType action, double diagonal) {
		if (action.equals(EventActionType.REMOVE)) {
			if (widgetMap.containsKey(key)) {
				PlaneWidget w = widgetMap.remove(key);
				w.clear();
			}
		} else {
			if (!widgetMap.containsKey(key)) {
				PlaneWidget widget = new PlaneWidget(vtkRendererPanel);
				widgetMap.put(key, widget);
			}
			PlaneWidget pointWidget = widgetMap.get(key);
			pointWidget.showPlane(origin, normal, action, diagonal);
		}
	}

	public void showPlaneDisplay(String key, DoubleField[] origin, DoubleField[] normal, EventActionType action, double diagonal) {
		if (action.equals(EventActionType.REMOVE)) {
			if (widgetDisplayMap.containsKey(key)) {
				PlaneDisplayWidget w = widgetDisplayMap.remove(key);
				w.clear();
			}
		} else {
			if (!widgetDisplayMap.containsKey(key)) {
				PlaneDisplayWidget widget = new PlaneDisplayWidget(vtkRendererPanel);
				widgetDisplayMap.put(key, widget);
			}
			PlaneDisplayWidget pointWidget = widgetDisplayMap.get(key);
			pointWidget.showPlane(origin, normal, action, diagonal);
		}
	}

}
