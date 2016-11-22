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
package eu.engys.vtk.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import eu.engys.gui.view3D.CanvasPanel;
import eu.engys.gui.view3D.Geometry3DController;
import eu.engys.gui.view3D.Mesh3DController;
import eu.engys.gui.view3D.fallback.FallbackGeometry3DController;
import eu.engys.gui.view3D.fallback.FallbackMesh3DController;
import eu.engys.gui.view3D.fallback.FallbackView3D;
import eu.engys.util.VTKSettings;
import eu.engys.vtk.VTKEmptyView3D;
import eu.engys.vtk.VTKGeometry3DController;
import eu.engys.vtk.VTKMesh3DController;
import eu.engys.vtk.VTKView3D;

public class VTK3DModule extends AbstractModule {

    private boolean no3D;
    
    public VTK3DModule(Boolean no3D) {
        this.no3D = no3D;
    }
    
    @Override
    protected void configure() {
        if (!VTKSettings.librariesAreLoaded()) {
            VTKSettings.LoadAllNativeLibraries();
        }
        if (VTKSettings.librariesAreLoaded()) {
            if (no3D) {
                bind(CanvasPanel.class).to(VTKEmptyView3D.class).in(Singleton.class);
            } else {
                bind(CanvasPanel.class).to(VTKView3D.class).in(Singleton.class);
            }
            bind(Geometry3DController.class).to(VTKGeometry3DController.class).in(Singleton.class);
            bind(Mesh3DController.class).to(VTKMesh3DController.class).in(Singleton.class);
        } else {
            bind(CanvasPanel.class).to(FallbackView3D.class).in(Singleton.class);
            bind(Geometry3DController.class).to(FallbackGeometry3DController.class).in(Singleton.class);
            bind(Mesh3DController.class).to(FallbackMesh3DController.class).in(Singleton.class);
        }
    }

}
