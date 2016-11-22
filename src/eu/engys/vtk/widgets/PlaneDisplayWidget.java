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

import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.util.ui.textfields.DoubleField;
import vtk.vtkHandleWidget;
import vtk.vtkPlaneSource;
import vtk.vtkPolygonalHandleRepresentation3D;

public class PlaneDisplayWidget {

    private RenderPanel renderPanel;
    private vtkHandleWidget widget;
    private vtkPolygonalHandleRepresentation3D representation;
    private DoubleField[] currentOrigin = null;
    private DoubleField[] currentNormal = null;

    public PlaneDisplayWidget(RenderPanel renderPanel) {
        this.renderPanel = renderPanel;
    }

    public void showPlane(DoubleField[] origin, DoubleField[] normal, EventActionType action, double diagonal) {
        renderPanel.lock();
        if (action.equals(EventActionType.HIDE)) {
            if(widget != null){
                widget.Off();
                currentOrigin = null;
                currentNormal = null;
            }
        } else if (action.equals(EventActionType.SHOW)) {
            if(widget == null){
                createWidget();
            }
            currentOrigin = origin;
            currentNormal = normal;
            changePosition(diagonal);
            widget.On();
        }
        renderPanel.unlock();
        renderPanel.renderLater();
    }

    private void createWidget() {
        widget = new vtkHandleWidget();

        representation = new vtkPolygonalHandleRepresentation3D();
        representation.GetProperty().SetColor(1, 1, 1);
        representation.GetSelectedProperty().SetColor(1, 1, 1);
        representation.DragableOff();
        representation.PickableOff();
        representation.ActiveRepresentationOff();
//        rep.SetWorldPosition(new double[] { 0.0, 0.0, 0.0 });

        widget.SetRepresentation(representation);
        widget.EnableAxisConstraintOff();      
        widget.ProcessEventsOff();
        
        renderPanel.getInteractor().addObserver(widget);  
    }


    private void changePosition(double diagonal) {
        double value = Double.isInfinite(diagonal) ? 1 : diagonal > 0 ? diagonal : 1;

        vtkPlaneSource planeSource = new vtkPlaneSource();
        planeSource.SetOrigin(0, 0, 0);
        planeSource.SetPoint1(value, 0, 0);
        planeSource.SetPoint2(0, value, 0);
        planeSource.SetCenter(currentOrigin[0].getDoubleValue(), currentOrigin[1].getDoubleValue(), currentOrigin[2].getDoubleValue());
        planeSource.SetNormal(currentNormal[0].getDoubleValue(), currentNormal[1].getDoubleValue(), currentNormal[2].getDoubleValue());
        planeSource.Update();
        
        representation.SetHandle(planeSource.GetOutput());
    }

    public void clear() {
        renderPanel.lock();
        if(widget != null){
            widget.EnabledOff();
            widget.Delete();
            widget = null;
        }
        currentOrigin = null;
        currentNormal = null;
        renderPanel.unlock();
        renderPanel.renderLater();
    }
}
