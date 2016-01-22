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

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import vtk.vtkAreaPicker;
import vtk.vtkCellPicker;
import vtk.vtkGenericRenderWindowInteractor;
import vtk.vtkWorldPointPicker;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.EventManager.Event;
import eu.engys.gui.events.view3D.ActorPopUpEvent;
import eu.engys.gui.events.view3D.ActorSelectionEvent;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.CellPicker;
import eu.engys.gui.view3D.PickInfo;
import eu.engys.gui.view3D.PickManager;
import eu.engys.gui.view3D.Picker;
import eu.engys.gui.view3D.RenderPanel;

public class VTKPickManager implements PickManager {

	enum PickFor {ACTOR, CELL};
	
	private final RenderPanel renderPanel;
	private PickFor pickFor;
	
	public VTKPickManager(RenderPanel renderPanel) {
		this.renderPanel = renderPanel;
	}
	
	public void pick(int x, int y, boolean control, boolean shift) {
		PickInfo pi  = pickCell(x, y);
		pi.shift = shift;
		pi.control = control;
		
		if (pickFor == PickFor.ACTOR) {
			pickActor(pi);
		} else {
			pickCell(pi);
		}
	}
	
	public void pickArea(int[] startPosition, int[] endPosition, boolean control, boolean shift) {
		PickInfo pi  =  pickArea(startPosition, endPosition);
		pi.shift = shift;
		pi.control = control;
		
		if (pickFor == PickFor.ACTOR) {
			pickActor(pi);
		} else {
			pickCell(pi);
		}
	}
	
	void pickActor(PickInfo pi) {
		if (pi != null && pi.actor != null) {
		    renderPanel.selectActors(pi.control, pi.actor);
			EventManager.triggerEvent(this, getSelectionEvent(pi.control, pi.actor));
		} else {
		    renderPanel.selectActors(false);
			EventManager.triggerEvent(this, getSelectionEvent(false, null));
		}
	}
	
	private Event getSelectionEvent(boolean keep, Actor pickedActor) {
		for (Picker picker : pickersForActors) {
			if (picker.containsActor(pickedActor)) {
                return new ActorSelectionEvent(picker, pickedActor, keep) ;
			}
		}
		return new ActorSelectionEvent(null, null, false);
	}
	
	private void pickCell(final PickInfo pi) {
		for (final CellPicker picker : pickersForCells) {
		    picker.pick(pi);
//		    ExecUtil.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                }
//            });
		}
	}
	
	private List<Picker> pickersForActors = new ArrayList<>();
	private List<CellPicker> pickersForCells = new ArrayList<>();
	
	public void registerPickerForActors(Picker picker) {
		pickersForActors.add(picker);
	}

	public void registerPickerForCells(CellPicker picker) {
		pickersForCells.add(picker);
	}

	public void unregisterPickerForCells(CellPicker picker) {
	    pickersForCells.remove(picker);
	}
	
	public void pickForActors() {
		pickFor = PickFor.ACTOR;
	}

	public void pickForCells() {
		pickActor(null);
		pickFor = PickFor.CELL;
	}
	
	public double[] pickPoint() {
	    int[] position = ((vtkGenericRenderWindowInteractor)((VTKRenderPanel)renderPanel).getInteractor()).GetLastEventPosition();
	    return pickPoint(position);
	}
	
    public double[] pickPoint(int[] eventPosition) {
        renderPanel.lock();
    	vtkWorldPointPicker pick = new vtkWorldPointPicker();
        if (renderPanel instanceof VTKRenderPanel) {
            pick.Pick(eventPosition[0], eventPosition[1], 0, ((VTKRenderPanel)renderPanel).GetRenderer());
        }
    	renderPanel.unlock();
    	return pick.GetPickPosition();
    }
    
    private PickInfo pickCell(int x, int y) {
    	vtkCellPicker picker = new vtkCellPicker();
		picker.SetTolerance(0.0005);
		renderPanel.lock();
		if (renderPanel instanceof VTKRenderPanel) {
		    picker.Pick(x, y, 0, ((VTKRenderPanel)renderPanel).GetRenderer());
		}
		renderPanel.unlock();
		
		PickInfo pi = new PickInfo();
		pi.actor   = (Actor) picker.GetActor();
		pi.dataSet = picker.GetDataSet();
		pi.cellId  = picker.GetCellId();
		pi.cellIJK = picker.GetCellIJK();
		pi.normal = picker.GetPickNormal();
		pi.position = picker.GetPickPosition();
		
		return pi;
    }
    
    public PickInfo pickArea(int[] start, int[] end) {
    	vtkCellPicker centerPicker = new vtkCellPicker();
		centerPicker.SetTolerance(0.0005);
        if (renderPanel instanceof VTKRenderPanel) {
            centerPicker.Pick((start[0]+end[0])/2, (start[1]+end[1])/2, 0, ((VTKRenderPanel)renderPanel).GetRenderer());
        }
    	
    	vtkAreaPicker picker = new vtkAreaPicker();
    	if (renderPanel instanceof VTKRenderPanel) {
    	    picker.AreaPick(start[0], start[1], end[0], end[1], ((VTKRenderPanel)renderPanel).GetRenderer());
    	}
		
		PickInfo pi = new PickInfo();
		pi.actor   = (Actor) picker.GetActor();//centerPicker.GetActor();
		pi.dataSet = picker.GetDataSet();//centerPicker.GetDataSet();
		pi.cellId  = centerPicker.GetCellId();
		pi.cellIJK = null;
		pi.normal = null;
		pi.position = picker.GetPickPosition();
		pi.frustum = picker.GetFrustum();
		
		return pi;
    }

    public void popup(int x, int y, MouseEvent event) {
        PickInfo pi  = pickCell(x, y);
        pi.shift = false;
        pi.control = false;
        
        if (pickFor == PickFor.ACTOR) {
            popUp(event, pi);
        }
    }
    
    void popUp(MouseEvent event, PickInfo pi) {
        if (pi != null && pi.actor != null) {
            EventManager.triggerEvent(this, getPopUpEvent(event, pi.actor));
        } else {
            EventManager.triggerEvent(this, getPopUpEvent(event, null));
        }
    }
    
    private Event getPopUpEvent(MouseEvent event, Actor pickedActor) {
        for (Picker picker : pickersForActors) {
            if (picker.containsActor(pickedActor)) {
                return new ActorPopUpEvent(event, picker, pickedActor) ;
            }
        }
        return new ActorPopUpEvent(event, null, null);
    }
}
