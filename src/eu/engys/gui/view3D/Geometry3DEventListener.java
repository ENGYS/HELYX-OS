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


package eu.engys.gui.view3D;

import java.awt.Color;

import javax.swing.SwingUtilities;

import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.gui.events.EventManager.Event;
import eu.engys.gui.events.view3D.AddSurfaceEvent;
import eu.engys.gui.events.view3D.ChangeSurfaceEvent;
import eu.engys.gui.events.view3D.ColorSurfaceEvent;
import eu.engys.gui.events.view3D.RemoveSurfaceEvent;
import eu.engys.gui.events.view3D.RenameSurfaceEvent;
import eu.engys.gui.events.view3D.SelectSurfaceEvent;
import eu.engys.gui.events.view3D.TransformSurfaceEvent;
import eu.engys.gui.events.view3D.VisibleItemEvent;
import eu.engys.util.ui.checkboxtree.VisibleItem;

public class Geometry3DEventListener implements View3DEventListener {

    private Geometry3DController controller;

    public Geometry3DEventListener(Geometry3DController geometryActors) {
        this.controller = geometryActors;
    }

    @Override
    public void eventTriggered(final Object obj, final Event event) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (event instanceof RemoveSurfaceEvent) {
                    handleRemoveSurface((RemoveSurfaceEvent) event);
                } else if (event instanceof RenameSurfaceEvent) {
                    handleRenameSurface((RenameSurfaceEvent) event);
                } else if (event instanceof ChangeSurfaceEvent) {
                    handleChangeSurface((ChangeSurfaceEvent) event);
                } else if (event instanceof AddSurfaceEvent) {
                    handleAddSurface((AddSurfaceEvent) event);
                } else if (event instanceof TransformSurfaceEvent) {
                    handleTransformSurface((TransformSurfaceEvent) event);
                } else if (event instanceof SelectSurfaceEvent) {
                    handleSelectSurface((SelectSurfaceEvent) event);
                } else if (event instanceof VisibleItemEvent) {
                    handleVisibleItem((VisibleItemEvent) event);
                } else if (event instanceof ColorSurfaceEvent) {
                    handleColorSurface((ColorSurfaceEvent) event);
                }       
            }

        });
    }

    private void handleColorSurface(ColorSurfaceEvent event) {
        Surface selection = event.getSelection();
        Color c = event.getColor();
        controller.updateSurfaceColor(c, selection);
    }

    private void handleVisibleItem(VisibleItemEvent event) {
        VisibleItem selection = event.getSelection();
        if (selection instanceof Surface) {
            controller.updateSurfaceVisibility((Surface) selection);
        }
    }
    
    private void handleSelectSurface(SelectSurfaceEvent event) {
        Surface selection[] = event.getSelection();
        controller.updateSurfacesSelection(selection);        
    }

    private void handleAddSurface(AddSurfaceEvent e) {
        Surface[] surfaces = e.getSurfaces();
        controller.addSurfaces(surfaces);
        if (e.isResetZoom()) {
            controller.zoomReset();
        } else {
            controller.render();
        }
    }

    private void handleTransformSurface(TransformSurfaceEvent e) {
        Surface[] surfaces = e.getSurfaces();
        AffineTransform t = e.getTransformation();
        boolean save = e.shouldSave();
        controller.transformSurfaces(t, save, surfaces);
        controller.render();
    }

    private void handleChangeSurface(ChangeSurfaceEvent e) {
        Surface surface = e.getSurface();
        boolean resetZoom = e.isResetZoom();
        controller.changeSurface(surface);
        controller.render();
        if (resetZoom) {
            controller.zoomReset();
        }
    }

    private void handleRemoveSurface(RemoveSurfaceEvent e) {
        Surface[] surfaces = e.getSurfaces();
        controller.removeSurfaces(surfaces);
    }

    private void handleRenameSurface(RenameSurfaceEvent e) {
        /* DO NOTHING */
    }

}
