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
package eu.engys.gui.mesh.actions.geometry;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.surface.MultiRegion;
import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.RemoveSurfaceEvent;

public class RemoveSurfaceAction extends AbstractAction {

    public static final String REMOVE = "Remove";

    private Model model;
    private Surface[] surfaces;

    public RemoveSurfaceAction(Model model) {
        super(REMOVE);
        this.model = model;
    }

    public void update(boolean enabled, Surface[] surfaces) {
        this.surfaces = surfaces;
        // Type type = surfaces[0].getType();
        // setEnabled(type != Type.SOLID);
        setEnabled(enabled);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new RemoveSurface(model, surfaces).execute();
    }

    private class RemoveSurface {
        private Surface[] surfaces;
        private Model model;

        public RemoveSurface(Model model, Surface[] surfaces) {
            this.model = model;
            this.surfaces = surfaces;
        }

        public void execute() {
            if (surfaces[0].getType().isSolid()) {
                MultiRegion parent = ((Solid) surfaces[0]).getParent();
                for (Surface surface : surfaces) {
                    Solid solid = (Solid) surface;
                    parent.removeRegion(solid.getName());
                    EventManager.triggerEvent(this, new RemoveSurfaceEvent(solid));
                }
                if (parent.getRegions().length > 0) {
                    parent.setModified(true);
                    model.geometryChanged(parent);
                } else {
                    model.getGeometry().removeSurfaces(model, parent);
                    model.geometryChanged(parent);

                    EventManager.triggerEvent(this, new RemoveSurfaceEvent(parent));
                }
            } else {
                model.getGeometry().removeSurfaces(model, surfaces);
                model.geometryChanged(surfaces[0]);

                EventManager.triggerEvent(this, new RemoveSurfaceEvent(surfaces));
            }
        }
    }
}
