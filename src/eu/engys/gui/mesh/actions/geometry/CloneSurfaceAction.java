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
import javax.swing.JOptionPane;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.AddSurfaceEvent;
import eu.engys.gui.mesh.panels.AbstractGeometryPanel;
import eu.engys.util.Util;
import eu.engys.util.ui.UiUtil;

public class CloneSurfaceAction extends AbstractAction {

    public static final String CLONE = "Clone";

    private Model model;
    private Surface[] surfaces;
    private AbstractGeometryPanel panel;

    public CloneSurfaceAction(Model model, AbstractGeometryPanel panel) {
        super(CLONE);
        this.model = model;
        this.panel = panel;
    }

    public void update(boolean enabled, Surface[] surfaces) {
        this.surfaces = surfaces;
        Type type = surfaces[0].getType();
        setEnabled(enabled && type != Type.STL && type != Type.SOLID);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        panel.getTreeNodeManager().getTree().clearSelection();
        new CloneSurface(model, surfaces).execute();
    }

    private class CloneSurface {
        private Surface[] surfaces;
        private Model model;

        public CloneSurface(Model model, Surface[] surfaces) {
            this.model = model;
            this.surfaces = surfaces;
        }

        public void execute() {
            if (Util.isVarArgsNotNull(surfaces)) {
                Surface original = surfaces[0];
                Dictionary dictionary = original.toDictionary();
                if (dictionary.isDictionary("surface") && dictionary.isDictionary("volume")) {
                    Surface surface = original.cloneSurface();
                    surface.rename("CopyOf" + original.getName());
                    
                    model.getGeometry().addSurface(surface);
                    model.geometryChanged(surface);

                    EventManager.triggerEvent(this, new AddSurfaceEvent(surface));
                } else {
                    JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Cannot Clone: Invalid Format", "Clone Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
