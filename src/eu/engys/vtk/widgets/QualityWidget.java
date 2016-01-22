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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vtk.vtkHandleWidget;
import vtk.vtkPolyData;
import vtk.vtkPolygonalHandleRepresentation3D;
import vtk.vtkThreshold;
import vtk.vtkUnstructuredGrid;
import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.core.project.Model;
import eu.engys.gui.view3D.QualityInfo;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.vtk.VTKColors;
import eu.engys.vtk.VTKOpenFOAMDataset;
import eu.engys.vtk.VTKUtil;

public class QualityWidget {

    public class QualityListener implements PropertyChangeListener{
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("threshold")) {
                if (evt.getSource() instanceof QualityInfo) { 
                    updateWidget();
                }
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(QualityWidget.class);

    private static final int FIELD_ASSOCIATION_CELLS = 1;
    
    private final Model model;
    private final RenderPanel renderPanel;
    private final ProgressMonitor monitor;

    private vtkHandleWidget widget;
    private vtkPolygonalHandleRepresentation3D representation;
    private QualityListener listener;

    private QualityInfo currentQualityInfo;

    public QualityWidget(Model model, RenderPanel renderPanel, ProgressMonitor monitor) {
        this.model = model;
        this.renderPanel = renderPanel;
        this.monitor = monitor;
        this.listener = new QualityListener();
    }

    public void activateQualityField(QualityInfo qualityInfo, EventActionType action) {
        renderPanel.lock();
        if (action.equals(EventActionType.HIDE)) {
            hide();
        } else if (action.equals(EventActionType.SHOW)) {
            this.currentQualityInfo = qualityInfo;
            show();
        } else if (action.equals(EventActionType.REMOVE)) {
            clearSelection();
        }
        renderPanel.unlock();
        renderPanel.renderLater();
    }
    
    private void hide() {
        if (widget != null) {
            widget.Off();
            removeListener();
            currentQualityInfo = null;
            representation.SetHandle(new vtkPolyData());
        }
    }

    private void show() {
        if (widget == null) {
            createWidget();
        }
        
        updateWidget();
        
        widget.On();
        addListener();
    }
    
    private void setColor(Color color) {
        double[] d = VTKColors.toVTK(color);
        representation.GetProperty().SetColor(d);
        representation.GetSelectedProperty().SetColor(d);
    }
    
    private void createWidget() {
        representation = new vtkPolygonalHandleRepresentation3D();
//        representation.GetProperty().LightingOff();

        representation.DragableOff();
        representation.PickableOff();
        representation.ActiveRepresentationOff();//MuDeMe!
        
        widget = new vtkHandleWidget();
        renderPanel.getInteractor().addObserver(widget);
        widget.SetRepresentation(representation);
        
//        widget.AllowHandleResizeOff();
//        widget.EnableAxisConstraintOff();
//        widget.EnabledOff();
//        widget.ManagesCursorOff();
        widget.ProcessEventsOff();//MuDeMe!
//        widget.RemoveAllObservers();

    }

    private vtkUnstructuredGrid internalMesh;

    private void updateWidget() {
        if (internalMesh == null) {
            loadInternalMesh();
        }

        setColor(currentQualityInfo.getColor());

        vtkThreshold threshold = new vtkThreshold();
//        threshold.SetAttributeModeToUseCellData();
        threshold.SetInputData(internalMesh);
//        threshold.AllScalarsOff();
        
        switch (currentQualityInfo.getMeasure().getTest()) {
            case MORE_THAN:
                threshold.ThresholdByLower(currentQualityInfo.getThreshold());
                break;
            case LESS_THAN:
                threshold.ThresholdByUpper(currentQualityInfo.getThreshold());
                break;
            default:
                System.err.println("ERROR: Threshold not set!");
                break;
        }
        
        threshold.SetInputArrayToProcess(0, 0, 0, "vtkDataObject::FIELD_ASSOCIATION_CELLS", currentQualityInfo.getMeasure().getFieldName());
        threshold.Update();

        vtkPolyData dataSet = VTKUtil.geometryFilter(threshold.GetOutput());
        
        representation.SetHandle(dataSet);
        renderPanel.renderLater();
    }

    private void loadInternalMesh() {
        monitor.start("Loading internal mesh", false, new Runnable() {
            @Override
            public void run() {
                VTKOpenFOAMDataset dataset = new VTKOpenFOAMDataset(model, monitor);
                dataset.loadInternalMesh(0);
                
                monitor.info("-> Internal Mesh Actor");
                internalMesh = VTKOpenFOAMDataset.shallowCopy(dataset.getInternalMeshDataset());
                
                dataset.clear();
                monitor.end();
            }
        });
    }
    
    private void addListener() {
        if (currentQualityInfo != null && !currentQualityInfo.isListenedBy(listener)) {
            currentQualityInfo.addPropertyChangeListener(listener);
        }
    }

    private void removeListener() {
        if (currentQualityInfo != null && currentQualityInfo.isListenedBy(listener)) {
            currentQualityInfo.removePropertyChangeListener(listener);
        }
    }
    
    public void clear() {
        widget = null;
        representation = null;
    }

    private void clearSelection() {
        if (representation != null) { 
            representation.SetHandle(new vtkPolyData());
            renderPanel.renderLater();
        }
    }

    public QualityInfo getQualityInfo() {
        return currentQualityInfo;
    }

}
