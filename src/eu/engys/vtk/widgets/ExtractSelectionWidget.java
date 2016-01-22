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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vtk.vtkHandleWidget;
import vtk.vtkPolyData;
import vtk.vtkPolygonalHandleRepresentation3D;
import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.gui.view3D.CellPicker;
import eu.engys.gui.view3D.PickInfo;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.gui.view3D.Selection;
import eu.engys.gui.view3D.Selection.SelectionType;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.vtk.actions.ExtractSelection;

public class ExtractSelectionWidget implements CellPicker {

    private static final Logger logger = LoggerFactory.getLogger(ExtractSelectionWidget.class);

    private static final double[] COLOR = new double[] { 0.8, 0.0, 0.0 };

    private RenderPanel renderPanel;
    private vtkHandleWidget widget;

    private vtkPolygonalHandleRepresentation3D representation;

    private Selection currentSelection;
    private SelectionListener listener;

    public ExtractSelectionWidget(RenderPanel renderPanel, ProgressMonitor monitor) {
        this.renderPanel = renderPanel;
        this.listener = new SelectionListener();
    }

    public void activateSelection(Selection selection, EventActionType action) {
        renderPanel.lock();
        if (action.equals(EventActionType.HIDE)) {
            hide();
        } else if (action.equals(EventActionType.SHOW)) {
            this.currentSelection = selection;
            show();
        } else if (action.equals(EventActionType.REMOVE)) {
            clearSelection();
        }
        renderPanel.unlock();
        renderPanel.renderLater();
    }

    private void hide() {
        if (widget != null) {
            renderPanel.lowRenderingOn();
            widget.Off();
            removeListener();
            currentSelection = null;
            representation.SetHandle(new vtkPolyData());
            selectionBoxOff();
            renderPanel.getPickManager().pickForActors();
            renderPanel.getPickManager().unregisterPickerForCells(this);
        }
    }

    private void show() {
        renderPanel.lowRenderingOff();
        renderPanel.getPickManager().pickForCells();
        renderPanel.getPickManager().registerPickerForCells(this);
        if (widget == null) {
            createWidget();
        }

        if (currentSelection.getType() == SelectionType.AREA) {
            selectionBoxOn();
        } else {
            selectionBoxOff();
        }

        widget.On();
        addListener();
    }

    private void createWidget() {
        representation = new vtkPolygonalHandleRepresentation3D();
        representation.GetProperty().SetColor(COLOR);
        // representation.GetProperty().LightingOff();

        representation.GetSelectedProperty().SetColor(COLOR);
        representation.DragableOff();
        representation.PickableOff();
        representation.ActiveRepresentationOff();// MuDeMe!

        widget = new vtkHandleWidget();
        renderPanel.getInteractor().addObserver(widget);
        widget.SetRepresentation(representation);

        // widget.AllowHandleResizeOff();
        // widget.EnableAxisConstraintOff();
        // widget.EnabledOff();
        // widget.ManagesCursorOff();
        widget.ProcessEventsOff();// MuDeMe!
        // widget.RemoveAllObservers();

    }

    private void addListener() {
        if (currentSelection != null) {
            currentSelection.addPropertyChangeListener(listener);
        }
    }

    private void removeListener() {
        if (currentSelection != null) {
            currentSelection.removePropertyChangeListener(listener);
        }
    }

    @Override
    public void pick(PickInfo pi) {
        removeListener();

        ExtractSelection extract = new ExtractSelection();
        extract.setSelection(currentSelection);
        extract.execute(pi);

        if (representation.GetHandle() != null && representation.GetHandle().GetNumberOfCells() == 0) {
            moveSelectionOnTop();
        }
        representation.SetHandle(currentSelection.getSelectionData());
        renderPanel.renderLater();

        addListener();
    }

    public void clear() {
        widget = null;
        representation = null;
    }

    public void selectionBoxOn() {
        renderPanel.getInteractor().setStyleToArea();
    }

    public void selectionBoxOff() {
        renderPanel.getInteractor().setStyleToDefault();
    }

    private void moveSelectionOnTop() {
        renderPanel.lock();
        widget.Off();
        widget.On();
        renderPanel.unlock();
    }

    private void clearSelection() {
        if (currentSelection != null) {
            currentSelection.setIdList(null);
        }
        if (representation != null) {
            representation.SetHandle(new vtkPolyData());
            renderPanel.renderLater();
        }
    }

    public Selection getSelection() {
        return currentSelection;
    }

    public class SelectionListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() instanceof Selection) {
                Selection selection = (Selection) evt.getSource();
                if (evt.getPropertyName().equals("type")) {
                    switch (selection.getType()) {
                    case CELL:
                        selectionBoxOff();
                        break;
                    case AREA:
                        selectionBoxOn();
                        break;
                    case FEATURE:
                        selectionBoxOff();
                        break;

                    default:
                        break;
                    }
                } else if (evt.getPropertyName().equals("dataSet")) {
                    currentSelection.setDataSet(selection.getDataSet());
                }
            }
        }
    }

}
