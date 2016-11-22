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

package eu.engys.util.filechooser.favorites.renderer;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

import eu.engys.util.filechooser.favorites.Favorite;
import eu.engys.util.filechooser.favorites.FavoritesUtils;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;


/**
 */
public class FavoriteListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected & cellHasFocus, cellHasFocus);
        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            Favorite f = (Favorite) value;
            label.setText(f.getName());
            label.setToolTipText(VFSUtils.getFriendlyName(f.getUrl()));
            
            if(f.getType().isSystem()){
                if(FavoritesUtils.HOME.equals(f.getName())){
                    label.setIcon(HOME_ICON);
                } else if(FavoritesUtils.DESKTOP.equals(f.getName())){
                    label.setIcon(DESKTOP_ICON);
                } else if(FavoritesUtils.DOCUMENTS.equals(f.getName())){
                    label.setIcon(DOCUMENTS_ICON);
                } else {
                    label.setIcon(VFSUtils.getIconForFileSystem(f.getUrl()));
                }
            } else {
                label.setIcon(VFSUtils.getIconForFileSystem(f.getUrl()));
            }
        }

        return component;
    }
    
    /*
     * RESOURCES
     */
    private static final Icon HOME_ICON = ResourcesUtil.getIcon("folder.user");
    private static final Icon DESKTOP_ICON = ResourcesUtil.getIcon("folder.desktop");
    private static final Icon DOCUMENTS_ICON = ResourcesUtil.getIcon("folder.documents");
}
