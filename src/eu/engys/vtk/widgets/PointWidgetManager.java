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

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.vtk.VTKRenderPanel;

public class PointWidgetManager {

	private Map<String, PointWidget> widgetMap = new HashMap<>();
	private VTKRenderPanel vtkRendererPanel;

	public PointWidgetManager(VTKRenderPanel vtkRendererPanel) {
		this.vtkRendererPanel = vtkRendererPanel;
	}

	public void clear() {
		for (PointWidget w : widgetMap.values()) {
			w.clear();
		}
		widgetMap.clear();
	}

	public void showPoint(String key, DoubleField[] point, EventActionType action, Color color) {
		if (action.equals(EventActionType.REMOVE)) {
			if (widgetMap.containsKey(key)) {
				PointWidget w = widgetMap.remove(key);
				w.clear();
			}
		} else {
			if (!widgetMap.containsKey(key)) {
				PointWidget widget = new PointWidget(vtkRendererPanel, color);
				widgetMap.put(key, widget);
			}
			PointWidget pointWidget = widgetMap.get(key);
			pointWidget.showPoint(point, action);
		}
	}

}
