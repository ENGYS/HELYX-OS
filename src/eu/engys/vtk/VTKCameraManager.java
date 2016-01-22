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

import vtk.vtkRenderer;
import eu.engys.gui.view3D.CameraManager;

public class VTKCameraManager implements CameraManager {

    private VTKRenderPanel vtkRenderPanel;

    public VTKCameraManager(VTKRenderPanel vtkRenderPanel) {
        this.vtkRenderPanel = vtkRenderPanel;
    }


    public void setCameraPosition(Position pos) {
        vtkRenderer renderer = vtkRenderPanel.GetRenderer();
        double[] fp = renderer.GetActiveCamera().GetFocalPoint();
        double[] p = renderer.GetActiveCamera().GetPosition();
        double dist = Math.sqrt(Math.pow(p[0] - fp[0], 2) + Math.pow(p[1] - fp[1], 2) + Math.pow(p[2] - fp[2], 2));

        vtkRenderPanel.lock();
        switch (pos) {
        case X_POS:
            renderer.GetActiveCamera().SetPosition(fp[0] - dist, fp[1], fp[2]);
            renderer.GetActiveCamera().SetViewUp(0, 0, 1);
            break;
        case X_NEG:
            renderer.GetActiveCamera().SetPosition(fp[0] + dist, fp[1], fp[2]);
            renderer.GetActiveCamera().SetViewUp(0, 0, 1);
            break;
        case Y_POS:
            renderer.GetActiveCamera().SetPosition(fp[0], fp[1] - dist, fp[2]);
            renderer.GetActiveCamera().SetViewUp(0, 0, 1);
            break;
        case Y_NEG:
            renderer.GetActiveCamera().SetPosition(fp[0], fp[1] + dist, fp[2]);
            renderer.GetActiveCamera().SetViewUp(0, 0, 1);
            break;
        case Z_POS:
            renderer.GetActiveCamera().SetPosition(fp[0], fp[1], fp[2] - dist);
            renderer.GetActiveCamera().SetViewUp(0, 1, 0);
            break;
        case Z_NEG:
            renderer.GetActiveCamera().SetPosition(fp[0], fp[1], fp[2] + dist);
            renderer.GetActiveCamera().SetViewUp(0, 1, 0);
            break;
        }
        vtkRenderPanel.unlock();
        vtkRenderPanel.renderLater();
    }


}
