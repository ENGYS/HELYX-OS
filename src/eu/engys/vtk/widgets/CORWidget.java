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

import vtk.vtkActor;
import vtk.vtkAssembly;
import vtk.vtkLineSource;
import vtk.vtkPolyDataMapper;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.vtk.VTKRenderPanel;

public class CORWidget {

	private VTKRenderPanel vtkRendererPanel;
	private vtkActor lineX;
	private vtkActor lineY;
	private vtkActor lineZ;
	private vtkAssembly cor;

	public CORWidget(VTKRenderPanel vtkRendererPanel) {
		this.vtkRendererPanel = vtkRendererPanel;
		cor = new vtkAssembly();
    	
		lineX = getLine(new double[] {-1, 0, 0}, new double[] {1,0,0}, 1, 1,0,0);
		lineY = getLine(new double[] { 0,-1, 0}, new double[] {0,1,0}, 1, 0,1,0);
		lineZ = getLine(new double[] { 0, 0,-1}, new double[] {0,0,1}, 1, 0,0,1);
		
    	cor.AddPart(lineX);
		cor.AddPart(lineY);
		cor.AddPart(lineZ);
		cor.UseBoundsOff();
	}

	private vtkActor getLine(double[] p1, double[] p2, float gr, int r, int g, int b)	{

		vtkLineSource line = new vtkLineSource();

		line.SetPoint1(p1[0], p1[1], p1[2]);
		line.SetPoint2(p2[0], p2[1], p2[2]);


		vtkPolyDataMapper mapper = new vtkPolyDataMapper();
		mapper.SetInputData(line.GetOutput());

		vtkActor axes = new vtkActor();

		axes.SetMapper(mapper);
		axes.GetProperty().SetColor(r, g, b);
		axes.GetProperty().SetLineWidth(gr);

		line.Delete();
		mapper.Delete();
		
		return axes;
	}
	
	private void updateLine(vtkActor line, double[] start, double[] end)	{
		//System.out.println("CORWidget.updateLine() start: "+Arrays.toString(start)+", end: "+Arrays.toString(end));
		vtkLineSource source = new vtkLineSource();
		source.SetPoint1(start);
		source.SetPoint2(end);
		source.Update();
		
		vtkPolyDataMapper mapper = (vtkPolyDataMapper) line.GetMapper();
		mapper.SetInputData(source.GetOutput());
		mapper.Update();
	}
	
	public void update(BoundingBox bb) {
		double[] center = vtkRendererPanel.GetRenderer().GetActiveCamera().GetFocalPoint();
		
//		System.out.println("CORWidget.update() W: " + bb.getWidth() + ", H: "+bb.getHeight());
//		System.out.println("CORWidget.update() CENTER: " + Arrays.toString(center));
		double deltaX = (bb.getXmax() - bb.getXmin()) / 4;
		double deltaY = (bb.getYmax() - bb.getYmin()) / 4;
		double deltaZ = (bb.getZmax() - bb.getZmin()) / 4;
		
		double[] start = new double[] { center[0]-deltaX, center[1], center[2] };
		double[] end   = new double[] { center[0]+deltaX, center[1], center[2] };
		updateLine(lineX, start, end);

		start = new double[] { center[0], center[1]-deltaY, center[2] };
		end   = new double[] { center[0], center[1]+deltaY, center[2] };
		updateLine(lineY, start, end);

		start = new double[] { center[0], center[1], center[2]-deltaZ };
		end   = new double[] { center[0], center[1], center[2]+deltaZ };
		updateLine(lineZ, start, end);
		
		cor.Modified();
	}
	
	public void clear() {
		
	}

    public void on() {
//        vtkRendererPanel.addActor(cor);
    }

}
