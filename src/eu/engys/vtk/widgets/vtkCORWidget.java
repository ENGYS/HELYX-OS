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

package eu.engys.vtk.widgets;

import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.gui.view3D.RenderPanel;
import vtk.vtkActor;
import vtk.vtkAppendPolyData;
import vtk.vtkAssembly;
import vtk.vtkLineSource;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;

public class vtkCORWidget {

	private RenderPanel renderPanel;
	private vtkActor lineX;
	private vtkActor lineY;
	private vtkActor lineZ;
	private vtkAssembly cor;

	public vtkCORWidget(RenderPanel renderPanel) {
		this.renderPanel = renderPanel;
		cor = new vtkAssembly();
    	
		lineX = getLine(new double[] {-1, 0, 0}, new double[] {1,0,0}, 1, 1,0,0);
		lineY = getLine(new double[] { 0,-1, 0}, new double[] {0,1,0}, 1, 0,1,0);
		lineZ = getLine(new double[] { 0, 0,-1}, new double[] {0,0,1}, 1, 0,0,1);
		
    	cor.AddPart(lineX);
		cor.AddPart(lineY);
		cor.AddPart(lineZ);
		cor.UseBoundsOff();
	}
	
	public static vtkPolyData createDataSet() {
	    vtkAppendPolyData append = new vtkAppendPolyData();
	    append.AddInputData(createLineDataSet(new double[] {-1, 0, 0}, new double[] {1,0,0}));
	    append.AddInputData(createLineDataSet(new double[] { 0,-1, 0}, new double[] {0,1,0}));
	    append.AddInputData(createLineDataSet(new double[] { 0, 0,-1}, new double[] {0,0,1}));
	    append.Update();
	    
	    return append.GetOutput();
	}
	
	private static vtkPolyData createLineDataSet(double[] p1, double[] p2) {
	    vtkLineSource line = new vtkLineSource();
	    line.SetPoint1(p1[0], p1[1], p1[2]);
	    line.SetPoint2(p2[0], p2[1], p2[2]);
	    line.Update();
	    
	    return line.GetOutput();
	}

	private vtkActor getLine(double[] p1, double[] p2, float gr, int r, int g, int b)	{

	    vtkPolyData lineDataSet = createLineDataSet(p1, p2);

		vtkPolyDataMapper mapper = new vtkPolyDataMapper();
		mapper.SetInputData(lineDataSet);
        mapper.SetResolveCoincidentTopologyToShiftZBuffer();
        mapper.SetResolveCoincidentTopologyZShift(0.0001);

		vtkActor axes = new vtkActor();

		axes.SetMapper(mapper);
		axes.GetProperty().SetColor(r, g, b);
		axes.GetProperty().SetLineWidth(gr);

		lineDataSet.Delete();
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
	    double[] center = renderPanel.getInteractor().getCenter();
//		double[] center = renderPanel.GetRenderer().GetActiveCamera().GetFocalPoint();
//		double[] center = GetDefaultRenderer().GetActiveCamera().GetFocalPoint();
		
//		System.out.println("CORWidget.update() W: " + bb.getWidth() + ", H: "+bb.getHeight());
//		System.out.println("CORWidget.update() CENTER: " + Arrays.toString(center));
		double deltaX = bb != null ? (bb.getXmax() - bb.getXmin()) / 4 : 1;
		double deltaY = bb != null ? (bb.getYmax() - bb.getYmin()) / 4 : 1;
		double deltaZ = bb != null ? (bb.getZmax() - bb.getZmin()) / 4 : 1;
		
		double[] start = new double[] { center[0]-deltaX, center[1], center[2] };
		double[] end   = new double[] { center[0]+deltaX, center[1], center[2] };
		updateLine(lineX, start, end);

		start = new double[] { center[0], center[1]-deltaY, center[2] };
		end   = new double[] { center[0], center[1]+deltaY, center[2] };
		updateLine(lineY, start, end);

		start = new double[] { center[0], center[1], center[2]-deltaZ };
		end   = new double[] { center[0], center[1], center[2]+deltaZ };
		updateLine(lineZ, start, end);

		renderPanel.renderLater();
	}
	
	public void clear() {
		
	}

//    public void activateSelection(EventActionType action) {
//        if (action.equals(EventActionType.HIDE)) {
//            hide();
//        } else if (action.equals(EventActionType.SHOW)) {
//            show();
//        } else if (action.equals(EventActionType.REMOVE)) {
////            clearSelection();
//        }
//        renderPanel.renderLater();
//    }

    public void On() {
        renderPanel.addActor(cor);
    }

    public void Off() {
        renderPanel.removeActor(cor);
    }

}
