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

import javax.vecmath.Point3d;

import eu.engys.gui.events.view3D.VolumeReportVisibilityEvent.Kind;
import eu.engys.gui.view3D.RenderPanel;
import vtk.vtkHandleWidget;
import vtk.vtkSphereHandleRepresentation;

public class MinMaxPointWidget {

    private RenderPanel renderPanel;
	private vtkHandleWidget minWidget;
	private vtkHandleWidget maxWidget;
	private Point3d minPoint;
	private Point3d maxPoint;

	public MinMaxPointWidget(RenderPanel renderPanel) {
		this.renderPanel = renderPanel;
		minWidget = createHandleWidget(new double[] { 0, 0, 1 });
		maxWidget = createHandleWidget(new double[] { 1, 0, 0 });
	}

	private vtkHandleWidget createHandleWidget(double[] color) {

		vtkSphereHandleRepresentation rep = new vtkSphereHandleRepresentation();
		rep.GetProperty().SetColor(color);
		rep.GetProperty().SetLineWidth(1.0);
		rep.GetSelectedProperty().SetColor(0.1, 0.1, 0.1);
		rep.PickableOff();
		rep.DragableOff();
		// rep.ConstrainedOff();
		// rep.TranslationModeOff();

		vtkHandleWidget widget = new vtkHandleWidget();
		widget.EnableAxisConstraintOff();
		widget.SetRepresentation(rep);
		widget.EnabledOff();

        renderPanel.getInteractor().addObserver(widget);

		return widget;
	}

	public void clear() {
		minWidget.EnabledOff();
		maxWidget.EnabledOff();
		minWidget.Delete();
		maxWidget.Delete();
		renderPanel.renderLater();
	}

	public void showPoint(Kind kind) {
		if (kind.isMin() && minWidget.GetEnabled() == 0) {
			minWidget.EnabledOn();
			renderPanel.renderLater();
		} else if (kind.isMax() && maxWidget.GetEnabled() == 0) {
			maxWidget.EnabledOn();
			renderPanel.renderLater();
		}
		updateCoordinates();
	}

	public void hidePoint(Kind kind) {
		if (kind.isMin() && minWidget.GetEnabled() == 1) {
			minWidget.EnabledOff();
			renderPanel.renderLater();
		} else if (kind.isMax() && maxWidget.GetEnabled() == 1) {
			maxWidget.EnabledOff();
			renderPanel.renderLater();
		}
	}

	public void setPoints(Point3d minPoint, Point3d maxPoint) {
		this.minPoint = minPoint;
		this.maxPoint = maxPoint;
		updateCoordinates();
	}

	private void updateCoordinates() {
		if (minWidget.GetEnabled() == 1 && minPoint != null) {
			((vtkSphereHandleRepresentation) minWidget.GetRepresentation()).SetWorldPosition(new double[] { minPoint.getX(), minPoint.getY(), minPoint.getZ() });
			renderPanel.renderLater();
		}
		if (maxWidget.GetEnabled() == 1 && maxPoint != null) {
			((vtkSphereHandleRepresentation) maxWidget.GetRepresentation()).SetWorldPosition(new double[] { maxPoint.getX(), maxPoint.getY(), maxPoint.getZ() });
			renderPanel.renderLater();
		}
	}

}
