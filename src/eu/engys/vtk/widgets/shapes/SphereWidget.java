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
import vtk.vtkTransform;

public class SphereWidget {

    private RenderPanel renderPanel;
	private vtkBoxWidget2 widget;
	private vtkActor actor;
	private BoxFieldListener listener;
	private DoubleField[] currentCenter = null;
	private DoubleField currentRadius = null;

	public SphereWidget(RenderPanel renderPanel) {
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
			}

			removeListener();
			double[] position = widget.GetRepresentation().GetBounds();

			double x = (position[0] + position[1]) / 2;
			double y = (position[2] + position[3]) / 2;
			double z = (position[4] + position[5]) / 2;

			double diffX = Math.abs(position[0] - position[1]);
			double diffY = Math.abs(position[2] - position[3]);
			double diffZ = Math.abs(position[4] - position[5]);

			double radius = Math.min(diffX, Math.min(diffY, diffZ)) / 2;

			if (currentCenter != null) {
				currentCenter[0].setDoubleValue(x);
				currentCenter[1].setDoubleValue(y);
				currentCenter[2].setDoubleValue(z);
			}
			if (currentRadius != null) {
				currentRadius.setDoubleValue(radius);
			}
			addListener();
		}
	};

	final Runnable callback2 = new Runnable() {
		vtkTransform transform = new vtkTransform();
		
		public void run() {
			if (actor != null) {
				vtkBoxRepresentation rep = (vtkBoxRepresentation) widget.GetRepresentation();
				rep.GetTransform(transform);

				double[] scale = transform.GetScale();
				double[] position = transform.GetPosition();

				double scaleX = scale[0];
				double scaleY = scale[1];
				double scaleZ = scale[2];

				double max = Math.max(scaleX, Math.max(scaleY, scaleZ));

				vtkTransform newT = new vtkTransform();
//				newT.
				newT.Scale(max, max, max);

				rep.SetTransform(newT);
			}

		}
	};

	public void showWidget(vtkActor actor, DoubleField[] center, DoubleField radius, EventActionType action) {
		this.actor = actor;
		renderPanel.lock();
		if (action.equals(EventActionType.HIDE)) {
			if (widget != null) {
				widget.Off();
				removeListener();
				currentCenter = null;
				currentRadius = null;
			}
		} else if (action.equals(EventActionType.SHOW)) {
			removeListener();
			if (widget == null) {
				createWidget();
			}
			widget.On();
			currentCenter = center;
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
		widget.AddObserver("EndInteractionEvent", callback, "run");
		widget.AddObserver("InteractionEvent", callback2, "run");

		widget.SetRepresentation(representation);
		widget.RotationEnabledOff();

        renderPanel.getInteractor().addObserver(widget);
	}

	public void clear() {
		removeListener();
		renderPanel.lock();
		if (widget != null) {
			// widget.RemoveAllObservers();
			widget.EnabledOff();
			widget.Delete();
			widget = null;
		}
		currentCenter = null;
		currentRadius = null;
		renderPanel.unlock();
		renderPanel.renderLater();
	}

	public void hideWidget() {
		clear();
	}

	private void changePosition() {
		if (currentCenter != null && currentRadius != null) {
			double x = currentCenter[0].getDoubleValue();
			double y = currentCenter[1].getDoubleValue();
			double z = currentCenter[2].getDoubleValue();

			double radius = currentRadius.getDoubleValue();

			double minX = x - radius;
			double maxX = x + radius;

			double minY = y - radius;
			double maxY = y + radius;

			double minZ = z - radius;
			double maxZ = z + radius;

			((vtkBoxRepresentation) widget.GetRepresentation()).PlaceWidget(new double[] { minX, maxX, minY, maxY, minZ, maxZ });
		}
	}

	private void addListener() {
		if (currentCenter != null) {
			currentCenter[0].addPropertyChangeListener(listener);
			currentCenter[1].addPropertyChangeListener(listener);
			currentCenter[2].addPropertyChangeListener(listener);
		}
		if (currentRadius != null) {
			currentRadius.addPropertyChangeListener(listener);
		}
	}

	private void removeListener() {
		if (currentCenter != null) {
			currentCenter[0].removePropertyChangeListener(listener);
			currentCenter[1].removePropertyChangeListener(listener);
			currentCenter[2].removePropertyChangeListener(listener);
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
