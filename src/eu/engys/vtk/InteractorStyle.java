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

import vtk.vtkInteractorStyleTrackballCamera;

public class InteractorStyle extends vtkInteractorStyleTrackballCamera {

    public InteractorStyle() {
        super();
        AddObserver("MouseMoveEvent", this, "OnMouseMove");
        AddObserver("LeftButtonPressEvent", this, "leftButtonPressed");
        AddObserver("MiddleButtonPressEvent", this, "middleButtonPressed");
        AddObserver("RightButtonPressEvent", this, "rightButtonPressed");
        AddObserver("LeftButtonReleaseEvent", this, "leftButtonReleased");
        AddObserver("MiddleButtonReleaseEvent", this, "middleButtonReleased");
        AddObserver("RightButtonReleaseEvent", this, "rightButtonReleased");
    }

    public void leftButtonPressed() {
        System.out.println("InteractorStyle.leftButtonPressed()");
//        buttonDown(getLastPos(), 0);
    }

    public void middleButtonPressed() {
        System.out.println("InteractorStyle.middleButtonPressed()");
//        buttonDown(getLastPos(), 1);
    }

    public void rightButtonPressed() {
        System.out.println("InteractorStyle.rightButtonPressed()");
//        buttonDown(getLastPos(), 2);
    }

    public void leftButtonReleased() {
        System.out.println("InteractorStyle.leftButtonReleased()");
//        buttonUp(getLastPos(), 0);
    }

    public void middleButtonReleased() {
        System.out.println("InteractorStyle.middleButtonReleased()");
//        buttonUp(getLastPos(), 1);
    }

    public void rightButtonReleased() {
        System.out.println("InteractorStyle.rightButtonReleased()");
//        buttonUp(getLastPos(), 2);
    }
    
    public void OnMouseMove() {
        System.out.println("InteractorStyle.OnMouseMove() " + GetState());
//        int x = GetInteractor().GetEventPosition()[0];
//        int y = GetInteractor().GetEventPosition()[1];
//        switch(GetState())
//        {
//        case 1: // '\001'
//            postText(INTERACTOR_ACTION_ROTATE);
//            Rotate();
//            InvokeEvent("InteractionEvent");
//            break;
//
//        case 2: // '\002'
//            postText(INTERACTOR_ACTION_PAN);
//            Pan();
//            InvokeEvent("InteractionEvent");
//            break;
//
//        case 4: // '\004'
//            postText(INTERACTOR_ACTION_ZOOM);
//            Dolly();
//            InvokeEvent("InteractionEvent");
//            break;
//
//        case 3: // '\003'
//            postText(INTERACTOR_ACTION_ROLL);
//            Spin();
//            InvokeEvent("InteractionEvent");
//            break;
//        }
    }

}
