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

package eu.engys.util.ui.treetable.tree;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import eu.engys.util.ui.treetable.JTreeTable;

public class JTreeTableCellRenderer extends DefaultTreeCellRenderer {

    private final boolean showsIcons;

    public JTreeTableCellRenderer(JTreeTable jTreeTable) {
        super();
        this.showsIcons = jTreeTable.getShowsIcons();

        setTextSelectionColor(jTreeTable.getSelectionForeground());
        setTextNonSelectionColor(jTreeTable.getForeground());
        setBackgroundSelectionColor(jTreeTable.getSelectionBackground());
        setBackgroundNonSelectionColor(jTreeTable.getBackground());
    }

    public Icon getClosedIcon() {
        return (showsIcons ? super.getClosedIcon() : null);
    }

    public Icon getDefaultClosedIcon() {
        return (showsIcons ? super.getDefaultClosedIcon() : null);
    }

    public Icon getDefaultLeafIcon() {
        return (showsIcons ? super.getDefaultLeafIcon() : null);
    }

    public Icon getDefaultOpenIcon() {
        return (showsIcons ? super.getDefaultOpenIcon() : null);
    }

    public Icon getLeafIcon() {
        return (showsIcons ? super.getLeafIcon() : null);
    }

    public Icon getOpenIcon() {
        return (showsIcons ? super.getOpenIcon() : null);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, false, expanded, leaf, row, hasFocus);
        if (row == 0) {
            setIcon(super.getDefaultOpenIcon());
        }
        return this;
    }
}
