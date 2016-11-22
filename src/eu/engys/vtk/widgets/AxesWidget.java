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

import eu.engys.gui.view3D.RenderPanel;
import vtk.vtkAxesActor;
import vtk.vtkCaptionActor2D;
import vtk.vtkOrientationMarkerWidget;

public class AxesWidget {

    private vtkAxesActor axes;
	private vtkOrientationMarkerWidget widget;

	public AxesWidget(RenderPanel renderPanel) {
        axes = new vtkAxesActor();
        
        setAxisProperty(axes.GetXAxisCaptionActor2D());
        setAxisProperty(axes.GetYAxisCaptionActor2D());
        setAxisProperty(axes.GetZAxisCaptionActor2D());

        widget = new vtkOrientationMarkerWidget();
        renderPanel.getInteractor().addObserver(widget);
        widget.SetOutlineColor(0.9300, 0.5700, 0.1300);
        widget.SetOrientationMarker(axes);
        widget.SetViewport(0, 0, 0.25, 0.25);
        widget.EnabledOn();
        widget.InteractiveOff();

    }

	private void setAxisProperty(vtkCaptionActor2D axis) {
		axis.GetTextActor().GetTextProperty().ShadowOff();
		axis.GetTextActor().GetTextProperty().SetFontFamilyToArial();
		axis.GetTextActor().GetTextProperty().ItalicOff();
		axis.GetTextActor().GetTextProperty().BoldOff();
		axis.GetTextActor().GetTextProperty().SetColor(0, 0, 0);
	}
	
	public void clear() {
	}

}
