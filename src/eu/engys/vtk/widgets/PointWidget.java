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

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import vtk.vtkHandleWidget;
import vtk.vtkSphereHandleRepresentation;
import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.util.ui.textfields.DoubleField;

public class PointWidget {

    private final class PointFieldListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                changePosition();
                renderPanel.renderLater();
            }
        }
    }

    private RenderPanel renderPanel;
    private vtkSphereHandleRepresentation rep;
    private vtkHandleWidget hwidget;
    private PropertyChangeListener listener;
    private DoubleField[] currentPoint = null;

    public PointWidget(RenderPanel renderPanel, Color color) {
        this.renderPanel = renderPanel;
        hwidget = new vtkHandleWidget();

        renderPanel.getInteractor().addObserver(hwidget);

        rep = new vtkSphereHandleRepresentation();
        hwidget.SetRepresentation(rep);
        hwidget.EnableAxisConstraintOff();
        hwidget.AddObserver("EndInteractionEvent", this, "handleEndInteraction");

        rep.SetWorldPosition(new double[] { 0.0, 0.0, 0.0 });

        float[] colorRGB = new float[3];
        color.getRGBColorComponents(colorRGB);
        rep.GetProperty().SetColor(colorRGB[0], colorRGB[1], colorRGB[2]);

        rep.GetProperty().SetLineWidth(1.0);
        rep.GetSelectedProperty().SetColor(0.1, 0.1, 0.1);

        listener = new PointFieldListener();
    }

    public void clear() {
        removeListener();
        renderPanel.lock();
        hwidget.EnabledOff();
        hwidget.Delete();
        currentPoint = null;
        renderPanel.unlock();
        renderPanel.renderLater();
    }

    public void showPoint(DoubleField[] point, EventActionType action) {
        renderPanel.lock();
        if (action.equals(EventActionType.HIDE)) {
            hwidget.EnabledOff();
            removeListener();
            currentPoint = null;
        } else if (action.equals(EventActionType.SHOW)) {
            removeListener();
            hwidget.EnabledOn();
            currentPoint = point;
            changePosition();
            addListener();
        }
        renderPanel.unlock();
        renderPanel.renderLater();
    }

    private void changePosition() {
        if (currentPoint != null) {
            rep.SetWorldPosition(new double[] { currentPoint[0].getDoubleValue(), currentPoint[1].getDoubleValue(), currentPoint[2].getDoubleValue() });
        }
    }

    private void addListener() {
        if (currentPoint != null) {
            currentPoint[0].addPropertyChangeListener(listener);
            currentPoint[1].addPropertyChangeListener(listener);
            currentPoint[2].addPropertyChangeListener(listener);
        }
    }

    private void removeListener() {
        if (currentPoint != null) {
            currentPoint[0].removePropertyChangeListener(listener);
            currentPoint[1].removePropertyChangeListener(listener);
            currentPoint[2].removePropertyChangeListener(listener);
        }
    }

    void handleEndInteraction() {
        removeListener();
        if (currentPoint != null) {
            double[] position = rep.GetWorldPosition();
            currentPoint[0].setDoubleValue(position[0]);
            currentPoint[1].setDoubleValue(position[1]);
            currentPoint[2].setDoubleValue(position[2]);
        }
        addListener();
    }
}
