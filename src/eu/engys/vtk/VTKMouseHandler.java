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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

import eu.engys.gui.view3D.Interactor;
import eu.engys.gui.view3D.Representation;
import vtk.vtkGenericRenderWindowInteractor;
import vtk.vtkInteractorObserver;
import vtk.vtkInteractorStyleHelyx;
import vtk.vtkInteractorStyleRubberBand3D;
import vtk.vtkInteractorStyleRubberBandZoom;
import vtk.vtkInteractorStyleTrackballCamera;

public class VTKMouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private boolean isDragging = false;
    private VTKRenderPanel vtkRenderPanel;

    public VTKMouseHandler(VTKRenderPanel vtkRenderPanel) {
        this.vtkRenderPanel = vtkRenderPanel;
    }

    private int ctrlPressed(InputEvent e) {
        return (e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK ? 1 : 0;
    }

    private int shiftPressed(InputEvent e) {
        return (e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK ? 1 : 0;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        System.out.println("MOUSE CLICKED");
        VTKPickManager pickManager = vtkRenderPanel.getPickManager();
        Interactor interactor = vtkRenderPanel.getInteractor();
        vtkGenericRenderWindowInteractor iren = (vtkGenericRenderWindowInteractor) interactor;
        
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (!isDragging) {
                int[] pos = iren.GetEventPosition();
                
                if (iren.GetInteractorStyle() instanceof vtkInteractorStyleTrackballCamera) {
                    pickManager.pick(pos[0], pos[1], e.isControlDown(), e.isShiftDown());
                } else if (iren.GetInteractorStyle() instanceof vtkInteractorStyleHelyx) {
                    pickManager.pick(pos[0], pos[1], e.isControlDown(), e.isShiftDown());
                }
            } else if(iren.GetInteractorStyle() instanceof vtkInteractorStyleRubberBand3D) {
                vtkInteractorStyleRubberBand3D style = (vtkInteractorStyleRubberBand3D) iren.GetInteractorStyle();
                int[] startPosition = style.GetStartPosition();
                int[] endPosition = style.GetEndPosition();
                pickManager.pickArea(startPosition, endPosition, e.isControlDown(), e.isShiftDown());
            } else if(iren.GetInteractorStyle() instanceof vtkInteractorStyleRubberBandZoom) {
                interactor.setStyleToDefault();
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (!isDragging) {
                int[] pos = iren.GetEventPosition();
                
                if (iren.GetInteractorStyle() instanceof vtkInteractorStyleTrackballCamera) {
                    pickManager.popup(pos[0], pos[1], e);
                } else if (iren.GetInteractorStyle() instanceof vtkInteractorStyleHelyx) {
                    pickManager.popup(pos[0], pos[1], e);
                } 
            } 
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        System.out.println("MOUSE_PRESSED");
        vtkRenderPanel.lock();
        vtkGenericRenderWindowInteractor iren = (vtkGenericRenderWindowInteractor) vtkRenderPanel.getInteractor();
        vtkInteractorObserver style = iren.GetInteractorStyle();

        if (SwingUtilities.isLeftMouseButton(e)) {
            iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");
            iren.LeftButtonPressEvent();
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");
            iren.MiddleButtonPressEvent();
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (style instanceof vtkInteractorStyleRubberBand3D) {
                iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");
            } else if (style instanceof vtkInteractorStyleTrackballCamera) {
                iren.SetEventInformation(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");
            } else if (style instanceof vtkInteractorStyleHelyx) {
                iren.SetEventInformation(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");
            }
            iren.RightButtonPressEvent();
        }

        vtkRenderPanel.unlock();

        isDragging = false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        System.out.println("MOUSE RELEASED");
        VTKPickManager pickManager = vtkRenderPanel.getPickManager();
    	Interactor interactor = vtkRenderPanel.getInteractor();
        vtkGenericRenderWindowInteractor iren = (vtkGenericRenderWindowInteractor) interactor;
        iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");

        vtkRenderPanel.lock();

        if (SwingUtilities.isLeftMouseButton(e)) {
            iren.LeftButtonReleaseEvent();
            if(iren.GetInteractorStyle() instanceof vtkInteractorStyleRubberBandZoom) {
                interactor.setStyleToDefault();
            } else if(iren.GetInteractorStyle() instanceof vtkInteractorStyleRubberBand3D) {
                vtkInteractorStyleRubberBand3D style = (vtkInteractorStyleRubberBand3D) iren.GetInteractorStyle();
                int[] startPosition = style.GetStartPosition();
                int[] endPosition = style.GetEndPosition();
                pickManager.pickArea(startPosition, endPosition, e.isControlDown(), e.isShiftDown());
            }
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            iren.MiddleButtonReleaseEvent();
        } else if (SwingUtilities.isRightMouseButton(e)) {
            iren.RightButtonReleaseEvent();
        }

        vtkRenderPanel.unlock();

        if (isDragging) { 
            vtkRenderPanel.setHighRendering();
        }

        isDragging = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        System.out.println("MOUSE_ENTERED");
    	vtkGenericRenderWindowInteractor iren = (vtkGenericRenderWindowInteractor) vtkRenderPanel.getInteractor();
        iren.SetEventInformationFlipY(e.getX(), e.getY(), 0, 0, '0', 0, "0");
        
        vtkRenderPanel.lock();
        iren.EnterEvent();
        vtkRenderPanel.unlock();
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        System.out.println("MOUSE_EXITED");
    	vtkGenericRenderWindowInteractor iren = (vtkGenericRenderWindowInteractor) vtkRenderPanel.getInteractor();
        iren.SetEventInformationFlipY(e.getX(), e.getY(), 0, 0, '0', 0, "0");

        vtkRenderPanel.lock();
        iren.LeaveEvent();
        vtkRenderPanel.unlock();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //System.out.println("MOUSE_MOVED");
    	vtkGenericRenderWindowInteractor iren = (vtkGenericRenderWindowInteractor) vtkRenderPanel.getInteractor();
        iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");

        vtkRenderPanel.lock();
        iren.MouseMoveEvent();
        vtkRenderPanel.unlock();
        isDragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("MOUSE_DRAGGED");
        vtkRenderPanel.setLowRendering();
        
    	vtkGenericRenderWindowInteractor iren = (vtkGenericRenderWindowInteractor) vtkRenderPanel.getInteractor();
    	vtkInteractorObserver style = iren.GetInteractorStyle();

        if (style instanceof vtkInteractorStyleRubberBand3D) {
            iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");
        } else if (style instanceof vtkInteractorStyleRubberBandZoom) {
            iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");
    	} else if (style instanceof vtkInteractorStyleTrackballCamera) {
    	    if (SwingUtilities.isRightMouseButton(e)) {
    	        iren.SetEventInformation(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");
    	    } else {
    	        iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");
    	    }
    	} else if (style instanceof vtkInteractorStyleHelyx) {
            if (SwingUtilities.isRightMouseButton(e)) {
                iren.SetEventInformation(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");
            } else {
                iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");
            }
        }

        vtkRenderPanel.lock();
        iren.MouseMoveEvent();
        vtkRenderPanel.unlock();

        isDragging = true;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // System.out.println("MOUSE_WHEEL_MOVED");
        vtkRenderPanel.setLowRendering();
        vtkGenericRenderWindowInteractor iren = (vtkGenericRenderWindowInteractor) vtkRenderPanel.getInteractor();
        iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed(e), shiftPressed(e), '0', 0, "0");

        vtkRenderPanel.lock();
        if (e.getWheelRotation() < 0)
            iren.MouseWheelForwardEvent();
        else
            iren.MouseWheelBackwardEvent();
        vtkRenderPanel.unlock();
        vtkRenderPanel.setHighRendering();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
        case KeyEvent.VK_W:
            vtkRenderPanel.changeRepresentation(Representation.WIREFRAME);
            break;
        case KeyEvent.VK_O:
            vtkRenderPanel.changeRepresentation(Representation.OUTLINE);
            break;
        case KeyEvent.VK_E:
            vtkRenderPanel.changeRepresentation(Representation.SURFACE_WITH_EDGES);
            break;
        case KeyEvent.VK_S:
            vtkRenderPanel.changeRepresentation(Representation.SURFACE);
            break;
        case KeyEvent.VK_R:
            vtkRenderPanel.resetZoomLater();
            break;

        default:
            break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
