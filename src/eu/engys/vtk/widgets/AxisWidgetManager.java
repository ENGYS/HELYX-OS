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


package eu.engys.vtk.widgets;

import java.util.HashMap;
import java.util.Map;

import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.vtk.VTKRenderPanel;

public class AxisWidgetManager {

	private String key = "pippo";
	
	private Map<String, AxisWidget> widgetMap = new HashMap<>();
	private VTKRenderPanel vtkRendererPanel;

	public AxisWidgetManager(VTKRenderPanel vtkRendererPanel) {
		this.vtkRendererPanel = vtkRendererPanel;
	}

	public void clear() {
		for (AxisWidget w : widgetMap.values()) {
			w.clear();
		}
		widgetMap.clear();
	}

	public void showPoint(DoubleField[] center, DoubleField[] normal, EventActionType action) {
		if (action.equals(EventActionType.REMOVE)) {
			if (widgetMap.containsKey(key)) {
				AxisWidget w = widgetMap.remove(key);
				w.clear();
			}
		} else {
			if (!widgetMap.containsKey(key)) {
				AxisWidget widget = new AxisWidget(vtkRendererPanel);
				widgetMap.put(key, widget);
			}
			AxisWidget axisWidget = widgetMap.get(key);
			axisWidget.showAxis(center, normal, action);
		}
	}

}
