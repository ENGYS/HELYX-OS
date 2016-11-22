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
package eu.engys.vtk;

import java.util.ArrayList;
import java.util.List;

import eu.engys.gui.view3D.Interactor;
import vtk.vtkGenericRenderWindowInteractor;
import vtk.vtkInteractorObserver;
import vtk.vtkInteractorStyleHelyx;
import vtk.vtkInteractorStyleRubberBand3D;
import vtk.vtkInteractorStyleRubberBandZoom;
import vtk.vtkRenderWindow;

public class VTKInteractor extends vtkGenericRenderWindowInteractor implements Interactor {

    private List<InteractorListener> listeners = new ArrayList<>();

    public VTKInteractor(vtkRenderWindow rw) {
        SetRenderWindow(rw);
        setStyleToDefault();
        
//      AddObserver("TimerEvent", this, "TimerEvent");
//      AddObserver("CreateTimerEvent", this, "StartTimer");
//      AddObserver("DestroyTimerEvent", this, "DestroyTimer");
//
//     SetDesiredUpdateRate(HIGHEST_RATE);
//     SetStillUpdateRate(LOW_RATE);
        
//        VTKUtil.observe(this, "INTER");
//        style.AddObserver("InteractionEvent", this, "InteractionEvent");
    }
    
//    public void InteractionEvent() {
//        vtkInteractorObserver style = GetInteractorStyle();
//        if (style instanceof vtkInteractorStyleTrackballCamera) {
//            int state = ((vtkInteractorStyleTrackballCamera) style).GetState();
//            for (InteractorListener l : listeners) {
//                switch (state) {
//                    case 1: l.rotate(); break;
//                    case 2: l.pan();    break;
//                    case 3: l.spin();   break;
//                    case 4: l.zoom();   break;
//                        
//                    default: break;
//                }
//            }
//        }
//    }
    
    @Override
    public void setStyleToDefault() {
//        SetInteractorStyle(new vtkInteractorStyleTrackballCamera());
        SetInteractorStyle(new vtkInteractorStyleHelyx());
    }

    @Override
    public void setStyleToArea() {
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
    
    @Override
    public void addListener(InteractorListener l) {
        listeners.add(l);
    }
    
    @Override
    public void removeListener(InteractorListener l) {
        listeners.remove(l);
    }
    
    @Override
    public double[] getCenter() {
        if (GetInteractorStyle() instanceof vtkInteractorStyleHelyx) {
            vtkInteractorStyleHelyx hs = (vtkInteractorStyleHelyx) GetInteractorStyle();
            return hs.GetCenter();
        }
        return new double[3];
    }
    
    @Override
    public void setCenter(double[] center) {
        if (GetInteractorStyle() instanceof vtkInteractorStyleHelyx) {
            vtkInteractorStyleHelyx hs = (vtkInteractorStyleHelyx) GetInteractorStyle();
            hs.SetCenter(center);
        }
    }
}
