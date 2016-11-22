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
package eu.engys.gui.view;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;

public class SurfacesCombo extends JComboBox<Surface> {

    public SurfacesCombo() {
        super();
        setRenderer(new SurfacesComboRenderer(getRenderer()));
    }

    public void loadAll(Model model) {
        for (Surface surface : model.getGeometry().getSurfaces()) {
            if (surface.getType().isStl()) {
                if (surface.isSingleton()) {
                    addItem(surface);
                } else {
                    addItem(surface);
                    for (Surface region : surface.getRegions()) {
                        addItem(region);
                    }
                }
            } else {
                addItem(surface);
            }
        }
    }

    public void loadSTLs(Model model) {
        for (Surface surface : model.getGeometry().getSurfaces()) {
            if (surface.getType().isStl()) {
                addItem(surface);
            }
        }
    }

    public void loadParents(Model model) {
        for (Surface surface : model.getGeometry().getSurfaces()) {
            if (surface.getType().isStl() || surface.getType().isBaseShape()) {
                addItem(surface);
            }
        }
    }
    
    public Surface getSelectedSurface() {
        return (Surface) getSelectedItem();
    }
    
    class SurfacesComboRenderer implements ListCellRenderer<Surface> {

        private ListCellRenderer<? super Surface> renderer;
        
        public SurfacesComboRenderer(ListCellRenderer<? super Surface> renderer) {
            this.renderer = renderer;
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Surface> list, Surface value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (c instanceof JLabel) {
                ((JLabel) c).setText(value.getName());
            }
            return c;
        }
        
        
    }

}
