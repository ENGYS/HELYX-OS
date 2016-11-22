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

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.engys.core.project.geometry.Surface;
import eu.engys.gui.mesh.panels.AbstractGeometryPanel;
import eu.engys.util.Util;

public class CopySurfaceAction extends AbstractAction {

    public static final String COPY = "Copy";

    private Surface[] surfaces;
    private AbstractGeometryPanel panel;

    public CopySurfaceAction(AbstractGeometryPanel panel) {
        super(COPY);
        this.panel = panel;
    }

    public void update(boolean enabled, Surface[] surfaces) {
        this.surfaces = surfaces;
        // Type type = surfaces[0].getType();
        setEnabled(enabled);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        panel.saveSurfaces(surfaces);
        new CopySurface(surfaces).execute();
    }

    private class CopySurface {
        private Surface[] surfaces;

        public CopySurface(Surface[] surfaces) {
            this.surfaces = surfaces;
        }

        public void execute() {
            if (Util.isVarArgsNotNull(surfaces)) {
                Surface surface = surfaces[0];
                StringSelection contents = new StringSelection(surface.toDictionary().toString());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, contents);
            } else {
                // error
            }
        }
    }
}
