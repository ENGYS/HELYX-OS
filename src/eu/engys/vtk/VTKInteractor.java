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

package eu.engys.vtk;

import vtk.vtkGenericRenderWindowInteractor;
import vtk.vtkInteractorObserver;
import vtk.vtkInteractorStyle;
import vtk.vtkInteractorStyleRubberBand3D;
import vtk.vtkInteractorStyleRubberBandZoom;
import vtk.vtkInteractorStyleTrackballCamera;
import vtk.vtkRenderWindow;
import eu.engys.gui.view3D.Interactor;

public class VTKInteractor extends vtkGenericRenderWindowInteractor implements Interactor {

    public VTKInteractor(vtkRenderWindow rw) {
        vtkInteractorStyle style = new vtkInteractorStyleTrackballCamera();
        SetRenderWindow(rw);
        SetInteractorStyle(style);
        
//      iren.AddObserver("TimerEvent", this, "TimerEvent");
//      iren.AddObserver("CreateTimerEvent", this, "StartTimer");
//      iren.AddObserver("DestroyTimerEvent", this, "DestroyTimer");
//
//     iren.SetDesiredUpdateRate(HIGHEST_RATE);
//     iren.SetStillUpdateRate(LOW_RATE);
        
    }
    
    @Override
    public void setStyleToDefault() {
        System.out.println("VTKInteractor.setStyleToDefault()<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        SetInteractorStyle(new vtkInteractorStyleTrackballCamera());
    }

    @Override
    public void setStyleToArea() {
        System.out.println("VTKInteractor.setStyleToArea() >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        SetInteractorStyle(new vtkInteractorStyleRubberBand3D());        
    }
    
    @Override
    public void setStyleToZoom() {
        SetInteractorStyle(new vtkInteractorStyleRubberBandZoom());   
    }
    
    @Override
    public void start() {
        Start();
    }
    
    @Override
    public void dispose() {
        SetRenderWindow(null);        
    }
    
    @Override
    public void updateSize(int w, int h) {
        SetSize(w, h);
        // rw.SetSize(w, h);
        ConfigureEvent();        
    }
    
    @Override
    public void wheelForwardEvent() {
        MouseWheelForwardEvent();
    }
    
    @Override
    public void wheelBackwardEvent() {
        MouseWheelBackwardEvent();
    }
    
    @Override
    public void addObserver(vtkInteractorObserver widget) {
        widget.SetInteractor(this);
    }
}
