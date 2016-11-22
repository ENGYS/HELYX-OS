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

package eu.engys.vtk.widgets.shapes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.util.ui.textfields.DoubleField;
import vtk.vtkActor;
import vtk.vtkBoxRepresentation;
import vtk.vtkBoxWidget2;
import vtk.vtkLineSource;
import vtk.vtkTransform;
import vtk.vtkTubeFilter;

public class CylinderWidget {

    private RenderPanel renderPanel;

    private vtkBoxWidget2 widget;
	private vtkActor actor;
	private BoxFieldListener listener;
	private DoubleField[] currentPoint1 = null;
	private DoubleField[] currentPoint2 = null;
	private DoubleField currentRadius = null;

	public CylinderWidget(RenderPanel renderPanel) {
		this.renderPanel = renderPanel;
		listener = new BoxFieldListener();
	}

	final Runnable callback = new Runnable() {
		vtkTransform transform = new vtkTransform();

		public void run() {
			if (actor != null) {
				vtkBoxRepresentation rep = (vtkBoxRepresentation) widget.GetRepresentation();
				rep.GetTransform(transform);
				actor.SetUserTransform(transform);

				vtkTubeFilter tubeFilter = (vtkTubeFilter) actor.GetMapper().GetInputConnection(0, 0).GetProducer();
				vtkLineSource cyl = (vtkLineSource) tubeFilter.GetInputConnection(0, 0).GetProducer();

				double[] tPoint1 = transform.TransformVector(cyl.GetPoint1());
				double[] tPoint2 = transform.TransformVector(cyl.GetPoint2());

				double[] center = transform.GetPosition();

				double radius = 0;
				// How to find it???

				removeListener();
				if (currentPoint1 != null) {
					currentPoint1[0].setDoubleValue(tPoint1[0] + center[0]);
					currentPoint1[1].setDoubleValue(tPoint1[1] + center[1]);
					currentPoint1[2].setDoubleValue(tPoint1[2] + center[2]);
				}
				if (currentPoint2 != null) {
					currentPoint2[0].setDoubleValue(tPoint2[0] + center[0]);
					currentPoint2[1].setDoubleValue(tPoint2[1] + center[1]);
					currentPoint2[2].setDoubleValue(tPoint2[2] + center[2]);
				}
				if (currentRadius != null) {
					currentRadius.setDoubleValue(radius);
				}

				addListener();
			}
		}
	};

	public void showWidget(vtkActor actor, DoubleField[] point1, DoubleField[] point2, DoubleField radius, EventActionType action) {
		this.actor = actor;
		renderPanel.lock();
		if (action.equals(EventActionType.HIDE)) {
			if (widget != null) {
				widget.Off();
				removeListener();
				currentPoint1 = null;
				currentPoint2 = null;
				currentRadius = null;
			}
		} else if (action.equals(EventActionType.SHOW)) {
			removeListener();
			if (widget == null) {
				createWidget();
			}
			widget.On();
			currentPoint1 = point1;
			currentPoint2 = point2;
			currentRadius = radius;
			changePosition();
			addListener();
		}
		renderPanel.unlock();
		renderPanel.renderLater();
	}

	private void createWidget() {
		final vtkBoxRepresentation representation = new vtkBoxRepresentation();
		representation.SetPlaceFactor(1);
		representation.PlaceWidget(actor.GetBounds());

		widget = new vtkBoxWidget2();
		widget.RemoveAllObservers();
		widget.AddObserver("EndInteractionEvent", callback, "run");

        renderPanel.getInteractor().addObserver(widget);

		widget.SetRepresentation(representation);
	}

	public void clear() {
		removeListener();
		renderPanel.lock();
		if (widget != null) {
			widget.EnabledOff();
			widget.Delete();
			widget = null;
		}
		currentPoint1 = null;
		currentPoint2 = null;
		currentRadius = null;
		renderPanel.unlock();
		renderPanel.renderLater();
	}

	public void hideWidget() {
		clear();
	}

	private void changePosition() {
		// If you change the coordinates from the GeometriesPanelBuilder it
		// doesn't work great
		((vtkBoxRepresentation) widget.GetRepresentation()).PlaceWidget(actor.GetBounds());
		renderPanel.Render();
	}

	private void addListener() {
		if (currentPoint1 != null) {
			currentPoint1[0].addPropertyChangeListener(listener);
			currentPoint1[1].addPropertyChangeListener(listener);
			currentPoint1[2].addPropertyChangeListener(listener);
		}
		if (currentPoint2 != null) {
			currentPoint2[0].addPropertyChangeListener(listener);
			currentPoint2[1].addPropertyChangeListener(listener);
			currentPoint2[2].addPropertyChangeListener(listener);
		}
		if (currentRadius != null) {
			currentRadius.addPropertyChangeListener(listener);
		}
	}

	private void removeListener() {
		if (currentPoint1 != null) {
			currentPoint1[0].removePropertyChangeListener(listener);
			currentPoint1[1].removePropertyChangeListener(listener);
			currentPoint1[2].removePropertyChangeListener(listener);
		}
		if (currentPoint2 != null) {
			currentPoint2[0].removePropertyChangeListener(listener);
			currentPoint2[1].removePropertyChangeListener(listener);
			currentPoint2[2].removePropertyChangeListener(listener);
		}
		if (currentRadius != null) {
			currentRadius.removePropertyChangeListener(listener);
		}
	}

	private final class BoxFieldListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("value")) {
				changePosition();
				renderPanel.Render();
			}
		}
	}

}
