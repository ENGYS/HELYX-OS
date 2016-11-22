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
import vtk.vtkBoxRepresentation;
import vtk.vtkBoxWidget2;
import vtk.vtkTransform;

public class BoxWidget {

    private RenderPanel renderPanel;
	private vtkBoxWidget2 widget;
	private BoxFieldListener listener;
	private DoubleField[] currentPoint1 = null;
	private DoubleField[] currentPoint2 = null;

	public BoxWidget(RenderPanel renderPanel) {
		this.renderPanel = renderPanel;
		listener = new BoxFieldListener();
	}

	final Runnable callback = new Runnable() {
		vtkTransform trasform = new vtkTransform();

		public void run() {
			removeListener();
			double[] position = widget.GetRepresentation().GetBounds();
			if (currentPoint1 != null) {
				currentPoint1[0].setDoubleValue(position[0]);
				currentPoint1[1].setDoubleValue(position[2]);
				currentPoint1[2].setDoubleValue(position[4]);
			}
			if (currentPoint2 != null) {
				currentPoint2[0].setDoubleValue(position[1]);
				currentPoint2[1].setDoubleValue(position[3]);
				currentPoint2[2].setDoubleValue(position[5]);
			}
			addListener();
		}
	};

	public void showBox(DoubleField[] point1, DoubleField[] point2, EventActionType action) {
		renderPanel.lock();
		if (action.equals(EventActionType.HIDE)) {
			if (widget != null) {
				widget.Off();
				removeListener();
				currentPoint1 = null;
				currentPoint2 = null;
			}
		} else if (action.equals(EventActionType.SHOW)) {
			removeListener();
			if (widget == null) {
				createWidget();
			}
			widget.On();
			currentPoint1 = point1;
			currentPoint2 = point2;
			changePosition();
			addListener();
		}
		renderPanel.unlock();
		renderPanel.renderLater();
	}

	private void createWidget() {
		final vtkBoxRepresentation representation = new vtkBoxRepresentation();
		representation.SetPlaceFactor(1);
		representation.PlaceWidget(new double[]{ 0.0,1.0,0.0,1.0,0.0,1.0});

		widget = new vtkBoxWidget2();
		renderPanel.getInteractor().addObserver(widget);
		widget.AddObserver("EndInteractionEvent", callback, "run");

		widget.SetRepresentation(representation);
		widget.RotationEnabledOff();
	}

	public void clear() {
		removeListener();
		renderPanel.lock();
		if (widget != null) {
//			widget.RemoveAllObservers();
			widget.EnabledOff();
			widget.Delete();
			widget = null;
		}
		currentPoint1 = null;
		currentPoint2 = null;
		renderPanel.unlock();
		renderPanel.renderLater();
	}

	public void hideWidget() {
		clear();
	}

	private void changePosition() {
		if (currentPoint1 != null && currentPoint2 != null) {
			double minX = currentPoint1[0].getDoubleValue();
			double maxX = currentPoint2[0].getDoubleValue();

			double minY = currentPoint1[1].getDoubleValue();
			double maxY = currentPoint2[1].getDoubleValue();

			double minZ = currentPoint1[2].getDoubleValue();
			double maxZ = currentPoint2[2].getDoubleValue();

			((vtkBoxRepresentation) widget.GetRepresentation()).PlaceWidget(new double[] { minX, maxX, minY, maxY, minZ, maxZ });
		}
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
	}

	private final class BoxFieldListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("value")) {
				changePosition();
				renderPanel.renderLater();
			}
		}
	}

}
