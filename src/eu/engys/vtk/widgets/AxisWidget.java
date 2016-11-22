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

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.vtk.VTKRenderPanel;
import vtk.vtkAppendPolyData;
import vtk.vtkArrowSource;
import vtk.vtkHandleWidget;
import vtk.vtkMatrix4x4;
import vtk.vtkPolygonalHandleRepresentation3D;
import vtk.vtkSphereSource;
import vtk.vtkTransform;
import vtk.vtkTransformPolyDataFilter;

public class AxisWidget {

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
    private PropertyChangeListener listener;
    private DoubleField[] currentCenter = null;
    private DoubleField[] currentNormal = null;
    private DoubleField currentAngle1 = null;
    private DoubleField currentAngle2 = null;
    private vtkHandleWidget widget;
    private vtkPolygonalHandleRepresentation3D representation;
    private vtkAppendPolyData append;
    private double magnitude;
    private int sign;

    public AxisWidget(RenderPanel renderPanel) {
        this.renderPanel = renderPanel;

        vtkSphereSource sphereSource = new vtkSphereSource();
        sphereSource.SetCenter(0,0,0);
        sphereSource.SetRadius(0.1);
        sphereSource.SetThetaResolution(24);
        sphereSource.SetPhiResolution(24);
        sphereSource.Update();
        
        vtkArrowSource arrowSource = new vtkArrowSource();
        arrowSource.SetShaftResolution(24); // default = 6
        arrowSource.SetTipResolution(36); // default = 6
        arrowSource.SetTipLength(0.1);
        arrowSource.SetTipRadius(0.1);
        arrowSource.Update();

        append = new vtkAppendPolyData();
        append.AddInputData(arrowSource.GetOutput());
        append.AddInputData(sphereSource.GetOutput());
        append.Update();
        
        vtkTransform transform = new vtkTransform();
        transform.RotateZ(-90);
        transform.Update();

        vtkTransformPolyDataFilter transformPD = new vtkTransformPolyDataFilter();
        transformPD.SetTransform(transform);
        transformPD.SetInputData(append.GetOutput());
        transformPD.Update();

        representation = new vtkPolygonalHandleRepresentation3D();
        representation.SetHandle(transformPD.GetOutput());
        representation.GetProperty().SetColor(0, 1, 1);
        representation.DragableOff();
        representation.PickableOff();
        representation.ActiveRepresentationOff();// MuDeMe!

        widget = new vtkHandleWidget();
        renderPanel.getInteractor().addObserver(widget);
        widget.SetRepresentation(representation);
        widget.ManagesCursorOff();
        widget.ProcessEventsOff();// MuDeMe!

        listener = new PointFieldListener();
    }

    public void clear() {
        removeListener();
        renderPanel.lock();
        widget.EnabledOff();
        widget.Delete();
        currentCenter = null;
        currentAngle1 = null;
        currentAngle2 = null;
        renderPanel.unlock();
        renderPanel.renderLater();
    }

    public void showAxis(DoubleField[] center, DoubleField[] normal, double magnitude, EventActionType action) {
        renderPanel.lock();
        if (action.equals(EventActionType.HIDE)) {
            widget.EnabledOff();
            removeListener();
            this.currentCenter = null;
            this.currentAngle1 = null;
            this.currentAngle2 = null;
            this.currentNormal = null;
        } else if (action.equals(EventActionType.SHOW)) {
            removeListener();
            this.currentCenter = center;
            this.currentAngle1 = null;
            this.currentAngle2 = null;
            this.currentNormal = normal;
            this.magnitude = magnitude;
            changePosition();
            widget.SetCurrentRenderer(((VTKRenderPanel)renderPanel).GetSelectionRenderer());
            widget.EnabledOn();
            addListener();
        }
        renderPanel.unlock();
        renderPanel.renderLater();
    }
    
    public void showAxis(DoubleField[] center, DoubleField angle1, DoubleField angle2, double magnitude, int sign, EventActionType action) {
        renderPanel.lock();
        if (action.equals(EventActionType.HIDE)) {
            widget.EnabledOff();
            removeListener();
            this.currentCenter = null;
            this.currentAngle1 = null;
            this.currentAngle2 = null;
            this.currentNormal = null;
        } else if (action.equals(EventActionType.SHOW)) {
            removeListener();
            this.currentCenter = center;
            this.currentAngle1 = angle1;
            this.currentAngle2 = angle2;
            this.currentNormal = null;
            this.magnitude = magnitude;
            this.sign = sign;
            changePosition();
            widget.SetCurrentRenderer(((VTKRenderPanel)renderPanel).GetSelectionRenderer());
            widget.EnabledOn();
            addListener();
        }
        renderPanel.unlock();
        renderPanel.renderLater();
    }

    private void changePosition() {
        if (isValid()) {
            if (currentNormal != null) {
                double[] p1 = new double[] { 
                        currentCenter[0].getDoubleValue(), 
                        currentCenter[1].getDoubleValue(), 
                        currentCenter[2].getDoubleValue() };
                double[] p2 = new double[] { 
                        currentCenter[0].getDoubleValue() + magnitude * currentNormal[0].getDoubleValue(), 
                        currentCenter[1].getDoubleValue() + magnitude * currentNormal[1].getDoubleValue(), 
                        currentCenter[2].getDoubleValue() + magnitude * currentNormal[2].getDoubleValue() };
                transformArrow(p1, p2);
            } else {
//                transformArrow(
//                        currentCenter[0].getDoubleValue(), 
//                        currentCenter[1].getDoubleValue(), 
//                        currentCenter[2].getDoubleValue(), 
//                        currentAngle1.getDoubleValue(), 
//                        currentAngle2.getDoubleValue(), 
//                        magnitude);
                double[] normal = getAxisFromCamberAndToe(currentAngle1.getDoubleValue(), currentAngle2.getDoubleValue(), sign);
                double[] p1 = new double[] { 
                        currentCenter[0].getDoubleValue(), 
                        currentCenter[1].getDoubleValue(), 
                        currentCenter[2].getDoubleValue() };
                double[] p2 = new double[] { 
                        currentCenter[0].getDoubleValue() + magnitude * normal[0], 
                        currentCenter[1].getDoubleValue() + magnitude * normal[1], 
                        currentCenter[2].getDoubleValue() + magnitude * normal[2] };
                transformArrow(p1, p2);
            }
            
        }
    }
    
    public static double[] getAxisFromCamberAndToe(double camber, double toe, int sign) {
        /* toe: rotation around Z axis */
        Matrix3d X = new Matrix3d();
        X.rotX(Math.toRadians(sign * camber));

        Matrix3d Y = new Matrix3d();
        Y.setIdentity();

        /* camber: rotation around X axis */
        Matrix3d Z = new Matrix3d();
        Z.rotZ(Math.toRadians(sign * toe));
        
        Matrix3d R = new Matrix3d();
        R.mul(Y, X);
        R.mul(Z, R);
        
        Vector3d axis = new Vector3d(0, -1, 0);
        R.transform(axis);
        return new double[] {axis.x, axis.y, axis.z};
    }
    
    private boolean isValid() {
        return currentCenter != null && currentCenter[0].getValue() != null && currentCenter[1].getValue() != null && currentCenter[2].getValue() != null;
    }

    private void transformArrow(double originX, double originY, double originZ, double camber, double toe, double magnitude) {
        // Apply the transforms
        vtkTransform transform = new vtkTransform();
        transform.PostMultiply();
        transform.Scale(magnitude, magnitude, magnitude);
        transform.RotateZ(-90);

        transform.Translate(-originX, -originY-magnitude/2, -originZ);
        transform.RotateY(0);
        transform.RotateX(sign * camber);
        transform.RotateZ(sign * toe);
        transform.Translate(originX, originY+magnitude/2, originZ);
        
        transform.Translate(originX, originY, originZ);
        transform.Update();

        // Transform the polydata
        vtkTransformPolyDataFilter transformPD = new vtkTransformPolyDataFilter();
        transformPD.SetTransform(transform);
        transformPD.SetInputData(append.GetOutput());
        transformPD.Update();

        representation.SetHandle(transformPD.GetOutput());
    }
    
    private void transformArrow(double[] startPoint, double[] endPoint) {
        // Compute a basis
        Vector3d normalizedX = new Vector3d();
        Vector3d normalizedY = new Vector3d();
        Vector3d normalizedZ = new Vector3d();

        // The X axis is a vector from start to end
        normalizedX.setX(endPoint[0] - startPoint[0]);
        normalizedX.setY(endPoint[1] - startPoint[1]);
        normalizedX.setZ(endPoint[2] - startPoint[2]);

        double length = normalizedX.length();
        normalizedX.normalize();

        // The Z axis is an arbitrary vector cross X
        Vector3d arbitrary = new Vector3d(1, 1, 1);

        normalizedZ.cross(normalizedX, arbitrary);
        normalizedZ.normalize();

        // The Y axis is Z cross X
        normalizedY.cross(normalizedZ, normalizedX);

        vtkMatrix4x4 matrix = new vtkMatrix4x4();

        // Create the direction cosine matrix
        matrix.Identity();
        matrix.SetElement(0, 0, normalizedX.getX());
        matrix.SetElement(0, 1, normalizedY.getX());
        matrix.SetElement(0, 2, normalizedZ.getX());
        matrix.SetElement(1, 0, normalizedX.getY());
        matrix.SetElement(1, 1, normalizedY.getY());
        matrix.SetElement(1, 2, normalizedZ.getY());
        matrix.SetElement(2, 0, normalizedX.getZ());
        matrix.SetElement(2, 1, normalizedY.getZ());
        matrix.SetElement(2, 2, normalizedZ.getZ());

        // Apply the transforms
        vtkTransform transform = new vtkTransform();
        transform.Translate(startPoint);
        transform.Concatenate(matrix);
        transform.Scale(length, length, length);
        transform.Update();

        // Transform the polydata
        vtkTransformPolyDataFilter transformPD = new vtkTransformPolyDataFilter();
        transformPD.SetTransform(transform);
        transformPD.SetInputData(append.GetOutput());
        transformPD.Update();

        representation.SetHandle(transformPD.GetOutput());
    }

    private void addListener() {
        if (isValid()) {
            currentCenter[0].addPropertyChangeListener(listener);
            currentCenter[1].addPropertyChangeListener(listener);
            currentCenter[2].addPropertyChangeListener(listener);
        }
        if (currentNormal != null) {
            currentNormal[0].addPropertyChangeListener(listener);
            currentNormal[1].addPropertyChangeListener(listener);
            currentNormal[2].addPropertyChangeListener(listener);
        }
        if (currentAngle1 != null) {
            currentAngle1.addPropertyChangeListener(listener);
        }
        if (currentAngle2 != null) {
            currentAngle2.addPropertyChangeListener(listener);
        }
    }

    private void removeListener() {
        if (isValid()) {
            currentCenter[0].removePropertyChangeListener(listener);
            currentCenter[1].removePropertyChangeListener(listener);
            currentCenter[2].removePropertyChangeListener(listener);
        }
        if (currentNormal != null) {
            currentNormal[0].removePropertyChangeListener(listener);
            currentNormal[1].removePropertyChangeListener(listener);
            currentNormal[2].removePropertyChangeListener(listener);
        }
        if (currentAngle1 != null) {
            currentAngle1.removePropertyChangeListener(listener);
        }
        if (currentAngle2 != null) {
            currentAngle2.removePropertyChangeListener(listener);
        }
    }
}
