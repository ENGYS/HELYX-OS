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

import java.awt.Dimension;
import java.nio.file.Path;
import java.nio.file.Paths;

import vtk.vtkGenericRenderWindowInteractor;
import vtk.vtkImageData;
import vtk.vtkLogoRepresentation;
import vtk.vtkLogoWidget;
import vtk.vtkPNGReader;
import eu.engys.gui.view3D.Interactor;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.util.ApplicationInfo;

public class LogoWidget {

    private RenderPanel renderPanel;
    private vtkLogoWidget logoWidget;
    private vtkLogoRepresentation logoRepresentation;
    private vtkImageData imageData;

    public LogoWidget(RenderPanel renderPanel) {
        this.renderPanel = renderPanel;
        this.imageData = createImageData();
        vtkLogoRepresentation logoRepresentation = createRepresentation();
        createWidget(logoRepresentation);
        logoWidget.On();
    }

    private void createWidget(vtkLogoRepresentation logoRepresentation) {
        logoWidget = new vtkLogoWidget();
        logoWidget.SetRepresentation(logoRepresentation);
        logoWidget.ResizableOff();
        logoWidget.ProcessEventsOff();
        logoWidget.SelectableOff();

        renderPanel.getInteractor().addObserver(logoWidget);
    }

    private vtkLogoRepresentation createRepresentation() {
        logoRepresentation = new vtkLogoRepresentation();
        logoRepresentation.SetImage(imageData);

        logoRepresentation.DragableOff();
        logoRepresentation.PickableOff();
        logoRepresentation.SetShowBorderToOff();
        logoRepresentation.ProportionalResizeOff();
        logoRepresentation.GetImageProperty().SetDisplayLocationToBackground();
        logoRepresentation.GetImageProperty().SetOpacity(0.7);
        logoRepresentation.GetBorderProperty().SetOpacity(0);
        logoRepresentation.VisibilityOn();

        return logoRepresentation;
    }

    private void placeRepresentation(Dimension size) {
        logoRepresentation.GetPositionCoordinate().SetCoordinateSystemToDisplay();
        logoRepresentation.GetPosition2Coordinate().SetCoordinateSystemToDisplay();
        double imageWidth = imageData.GetBounds()[1];
        double imageHeight = imageData.GetBounds()[3];

        double bottomLeftCornerX = size.width - imageWidth - 40;
        double bottomLeftCornerY = 40;
        double topRightCornerX = imageWidth + 1;
        double topRightCornerY = imageHeight + 1;

        logoRepresentation.SetPosition(bottomLeftCornerX, bottomLeftCornerY);
        logoRepresentation.SetPosition2(topRightCornerX, topRightCornerY);

        renderPanel.lock();
        final Interactor interactor = renderPanel.getInteractor();
        if (interactor instanceof vtkGenericRenderWindowInteractor) {
            vtkGenericRenderWindowInteractor iren = (vtkGenericRenderWindowInteractor) interactor;
            iren.LeftButtonPressEvent();
            iren.LeftButtonReleaseEvent();
        }
        renderPanel.unlock();
    }

    private vtkImageData createImageData() {
        vtkPNGReader reader = new vtkPNGReader();
        Path fileName = Paths.get(ApplicationInfo.getRootPath(), "img", ApplicationInfo.getVendor() + ".png");
        reader.SetFileName(fileName.toString());
        reader.Update();
        return reader.GetOutput();
    }

    public void update(Dimension size) {
        placeRepresentation(size);
    }

}
