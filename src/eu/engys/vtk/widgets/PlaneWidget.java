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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.util.ui.textfields.DoubleField;
import vtk.vtkPlaneWidget;

public class PlaneWidget {

    private RenderPanel renderPanel;
	private vtkPlaneWidget widget;
	private PropertyChangeListener listener;
	private DoubleField[] currentOrigin = null;
	private DoubleField[] currentNormal = null;

	public PlaneWidget(RenderPanel renderPanel) {
		this.renderPanel = renderPanel;
		listener = new PlaneFieldListener();
	}

	public void showPlane(DoubleField[] origin, DoubleField[] normal, EventActionType action, double diagonal) {
		renderPanel.lock();
		if (action.equals(EventActionType.HIDE)) {
			if(widget != null){
				widget.Off();
				removeListener();
				currentOrigin = null;
				currentNormal = null;
			}
		} else if (action.equals(EventActionType.SHOW)) {
			removeListener();
			if(widget == null){
				createWidget(diagonal);
			}
			currentOrigin = origin;
			currentNormal = normal;
			changePosition();
			widget.On();
			addListener();
		}
		renderPanel.unlock();
		renderPanel.renderLater();
	}
	
	private void createWidget(double diagonal) {
		widget = new vtkPlaneWidget();
		widget.SetHandleSize(1);
		widget.SetRepresentationToSurface();
		widget.AddObserver("EndInteractionEvent", this, "handleEndInteraction");

		widget.SetOrigin(0, 0, 0);
		widget.SetPoint1(diagonal, 0, 0);
		widget.SetPoint2(0, diagonal, 0);
		
//		widget.SetHandleSize(3);
//		widget.SetPlaceFactor(1);
//		widget.PlaceWidget();
//		widget.SetResolution(1);
//		widget.GetPlaneProperty().SetColor(VTKColors.GREEN);
//		widget.GetPlaneProperty().SetOpacity(0.7);
//		widget.GetPlaneProperty().EdgeVisibilityOn();
		
		renderPanel.getInteractor().addObserver(widget);
	}

	public void clear() {
		removeListener();
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

	private void changePosition() {
		if (currentOrigin != null) {
			widget.SetCenter(new double[] { currentOrigin[0].getDoubleValue(), currentOrigin[1].getDoubleValue(), currentOrigin[2].getDoubleValue() });
		}
		if (currentNormal != null) {
			widget.SetNormal(new double[] { currentNormal[0].getDoubleValue(), currentNormal[1].getDoubleValue(), currentNormal[2].getDoubleValue() });
		}
	}

	private void addListener() {
		if (currentOrigin != null) {
			currentOrigin[0].addPropertyChangeListener(listener);
			currentOrigin[1].addPropertyChangeListener(listener);
			currentOrigin[2].addPropertyChangeListener(listener);
		}
		if (currentNormal != null) {
			currentNormal[0].addPropertyChangeListener(listener);
			currentNormal[1].addPropertyChangeListener(listener);
			currentNormal[2].addPropertyChangeListener(listener);
		}
	}

	private void removeListener() {
		if (currentOrigin != null) {
			currentOrigin[0].removePropertyChangeListener(listener);
			currentOrigin[1].removePropertyChangeListener(listener);
			currentOrigin[2].removePropertyChangeListener(listener);
		}
		if (currentNormal != null) {
			currentNormal[0].removePropertyChangeListener(listener);
			currentNormal[1].removePropertyChangeListener(listener);
			currentNormal[2].removePropertyChangeListener(listener);
		}
	}

	void handleEndInteraction() {
		removeListener();
		if (currentOrigin != null) {
			double[] position = widget.GetCenter();
			currentOrigin[0].setDoubleValue(position[0]);
			currentOrigin[1].setDoubleValue(position[1]);
			currentOrigin[2].setDoubleValue(position[2]);
		}
		if (currentNormal != null) {
			double[] position = widget.GetNormal();
			currentNormal[0].setDoubleValue(position[0]);
			currentNormal[1].setDoubleValue(position[1]);
			currentNormal[2].setDoubleValue(position[2]);
		}
		addListener();
	}
	
	private final class PlaneFieldListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("value")) {
				changePosition();
				renderPanel.Render();
			}
		}
	}

}
