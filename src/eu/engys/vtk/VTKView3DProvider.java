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


//public class VTKView3DProvider implements Provider<CanvasPanel>, Runnable {
//
//    private CanvasPanel panel1;
//    private ILookAndFeel laf;
//	private Model model;
//	private Set<ViewElement> elements;
//	private Set<Widget> widgets;
//	private ProgressMonitor monitor;
//    private Set<Controller3D> controllers;
//
//    @Inject
//    public VTKView3DProvider(Model model, ILookAndFeel laf, Set<ViewElement> elements, Set<Controller3D> controllers, Set<Widget> widgets, ProgressMonitor monitor) {
//    	this.model = model;
//		this.laf = laf;
//        this.elements = elements;
//        this.controllers = controllers;
//		this.widgets = widgets;
//		this.monitor = monitor;
//    }
//    
//    @Override
//	public void run() {
//        if (!VTKSettings.librariesAreLoaded()) {
//            VTKSettings.LoadAllNativeLibraries();
//        }
//		if (VTKSettings.librariesAreLoaded()) {
//			if (Arguments.no3D) {
//				panel1 = new VTKEmptyView3D(model, controllers, monitor);
//			} else {
//				panel1 = new VTKView3D(model, laf, elements, controllers, widgets, monitor);
//			}
//		} else {
//			panel1 = new FallbackView3D();
//		}
//	}
//    
//    @Override
//    public CanvasPanel get() {
//        try {
//        	if (EventQueue.isDispatchThread()) { 
//        		run();
//        	} else {
//        		EventQueue.invokeAndWait(this);
//        	}
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException(e); // should not happen
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//        
//        return panel1;
//    }
//}
