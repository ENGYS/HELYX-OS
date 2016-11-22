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

import javax.vecmath.Point3d;

import eu.engys.gui.events.view3D.VolumeReportVisibilityEvent.Kind;
import eu.engys.vtk.VTKRenderPanel;

public class MinMaxPointWidgetManager {

    private Map<String, MinMaxPointWidget> widgetMap = new HashMap<>();
    private VTKRenderPanel vtkRendererPanel;

    public MinMaxPointWidgetManager(VTKRenderPanel vtkRendererPanel) {
        this.vtkRendererPanel = vtkRendererPanel;
    }

    public void clear() {
        for (MinMaxPointWidget w : widgetMap.values()) {
            w.clear();
        }
        widgetMap.clear();
    }

    public void setPointsVisible(String key, Kind kind, boolean visible) {
        if(visible){
            getPointWidget(key).showPoint(kind);
        } else {
            getPointWidget(key).hidePoint(kind);
        }
    }

    public void updateCoordinates(Point3d minPoint, Point3d maxPoint, String key) {
        MinMaxPointWidget pointWidget = getPointWidget(key);
        pointWidget.setPoints(minPoint, maxPoint);
    }

    private MinMaxPointWidget getPointWidget(String key) {
        if (!widgetMap.containsKey(key)) {
            MinMaxPointWidget widget = new MinMaxPointWidget(vtkRendererPanel);
            widgetMap.put(key, widget);
        }
        return widgetMap.get(key);
    }

}
