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

import static eu.engys.util.FormatUtil.format;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.util.ui.textfields.DoubleField;
import vtk.vtkBoxRepresentation;
import vtk.vtkBoxWidget2;
import vtk.vtkPolyData;
import vtk.vtkTransform;

public class RotatedBoxWidget {

    private RenderPanel renderPanel;
	private vtkBoxWidget2 widget;
	private BoxFieldListener listener;
	private DoubleField[] currentCenter = null;
	private DoubleField[] currentDelta = null;
	private DoubleField[] currentRotation = null;

	public RotatedBoxWidget(RenderPanel renderPanel) {
		this.renderPanel = renderPanel;
		listener = new BoxFieldListener();
	}

	final Runnable callback = new Runnable() {

		public void run() {
		    vtkBoxRepresentation rep = (vtkBoxRepresentation) widget.GetRepresentation();
		    vtkTransform trasform = new vtkTransform();
		    rep.GetTransform(trasform);
		    vtkPolyData polyData = new vtkPolyData();
		    rep.GetPolyData(polyData);
		    
			System.out.println("CALLBACK pos: " + format(trasform.GetPosition()) + ",  rot " + format(trasform.GetOrientation()) + ",  scale: " + format(trasform.GetScale()));

			removeListener();
			
			System.out.println("CALLBACK bounds: " + Arrays.toString(polyData.GetBounds()));
			BoundingBox bb = new BoundingBox(polyData.GetBounds());
			
			if (currentCenter != null) {
			    double[] newCenter = trasform.TransformDoublePoint(bb.getCenter());
			    currentCenter[0].setDoubleValue(newCenter[0]);
			    currentCenter[1].setDoubleValue(newCenter[1]);
			    currentCenter[2].setDoubleValue(newCenter[2]);
			}
			if (currentDelta != null) {
			    double[] scale = trasform.GetScale();
			    currentDelta[0].setDoubleValue(scale[0] * bb.getDeltaX());
			    currentDelta[1].setDoubleValue(scale[1] * bb.getDeltaY());
			    currentDelta[2].setDoubleValue(scale[2] * bb.getDeltaZ());
			}
			if (currentRotation != null) {
			    double[] orientation = trasform.GetOrientation();
			    currentRotation[0].setDoubleValue(orientation[0]);
			    currentRotation[1].setDoubleValue(orientation[1]);
			    currentRotation[2].setDoubleValue(orientation[2]);
			}
			addListener();
		}
	};

	public void showBox(DoubleField[] center, DoubleField[] delta, DoubleField[] rotation, EventActionType action) {
		renderPanel.lock();
		if (action.equals(EventActionType.HIDE)) {
			if (widget != null) {
				widget.Off();
				removeListener();
				this.currentCenter = null;
				this.currentDelta = null;
				this.currentRotation = null;
			}
		} else if (action.equals(EventActionType.SHOW)) {
			removeListener();
			if (widget == null) {
				createWidget();
			}
			widget.On();
			this.currentCenter = center;
			this.currentDelta = delta;
			this.currentRotation = rotation;
			changePosition();
			addListener();
		}
		renderPanel.unlock();
		renderPanel.renderLater();
	}

	private void createWidget() {
		vtkBoxRepresentation representation = new vtkBoxRepresentation();
		representation.SetPlaceFactor(1);

		widget = new vtkBoxWidget2();
		widget.AddObserver("EndInteractionEvent", callback, "run");
		renderPanel.getInteractor().addObserver(widget);

		widget.SetRepresentation(representation);
//		widget.RotationEnabledOff();
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
        this.currentCenter = null;
        this.currentDelta = null;
        this.currentRotation = null;
		renderPanel.unlock();
		renderPanel.renderLater();
	}

	public void hideWidget() {
		clear();
	}

	private void changePosition() {
		if (currentCenter != null && currentDelta != null && currentRotation != null) {
		    
		    double oX = currentCenter[0].getDoubleValue();
		    double oY = currentCenter[1].getDoubleValue();
		    double oZ = currentCenter[2].getDoubleValue();
		    
		    double dX = currentDelta[0].getDoubleValue();
		    double dY = currentDelta[1].getDoubleValue();
		    double dZ = currentDelta[2].getDoubleValue();

		    double minX = oX - dX/2;
			double maxX = oX + dX/2;

			double minY = oY - dY/2;
			double maxY = oY + dY/2;

			double minZ = oZ - dZ/2;
			double maxZ = oZ + dZ/2;
			
            System.out.println(String.format("changePosition() %s %s %s %s %s %s", format(minX).toCents(), format(maxX).toCents(), format(minY).toCents(), format(maxY).toCents(), format(minZ).toCents(), format(maxZ).toCents()));

			vtkBoxRepresentation rep = (vtkBoxRepresentation) widget.GetRepresentation();
            rep.PlaceWidget(new double[] { minX, maxX, minY, maxY, minZ, maxZ });
            if (hasRotation()) {
                double rotX = currentRotation[0].getDoubleValue();
                double rotY = currentRotation[1].getDoubleValue();
                double rotZ = currentRotation[2].getDoubleValue();
                vtkTransform t = new vtkTransform();
                t.PostMultiply();
                t.Translate(-oX, -oY, -oZ);
                t.RotateY(rotY);
                t.RotateX(rotX);
                t.RotateZ(rotZ);
                t.Translate(oX, oY, oZ);
                rep.SetTransform(t);
            } else {
                vtkTransform t = new vtkTransform();
                rep.SetTransform(t);
            }
		}
	}

	private boolean hasRotation() {
        return currentRotation[0].getDoubleValue() != 0 || currentRotation[1].getDoubleValue() != 0 || currentRotation[2].getDoubleValue() != 0;
    }

    private void addListener() {
        addListener(currentCenter);
        addListener(currentDelta);
        addListener(currentRotation);
	}
    
    private void removeListener() {
        removeListener(currentCenter);
        removeListener(currentDelta);
        removeListener(currentRotation);
    }

    private void addListener(DoubleField[] d) {
        if (d != null) {
            d[0].addPropertyChangeListener(listener);
            d[1].addPropertyChangeListener(listener);
            d[2].addPropertyChangeListener(listener);
        }
    }

    private void removeListener(DoubleField[] d) {
        if (d != null) {
            d[0].removePropertyChangeListener(listener);
            d[1].removePropertyChangeListener(listener);
            d[2].removePropertyChangeListener(listener);
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
