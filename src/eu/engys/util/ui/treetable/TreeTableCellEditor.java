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

package eu.engys.util.ui.treetable;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class TreeTableCellEditor extends DefaultCellEditor {

    private final JTreeTable jTreeTable;

    public TreeTableCellEditor(JTreeTable jTreeTable) {
        super(new TreeTableTextField());
        this.jTreeTable = jTreeTable;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
        Component component = super.getTableCellEditorComponent(table, value, isSelected, r, c);
        JTree t = this.jTreeTable.getTree();
        boolean rv = t.isRootVisible();
        int offsetRow = rv ? r : r - 1;
        Rectangle bounds = t.getRowBounds(offsetRow);
        int offset = bounds.x;
        TreeCellRenderer tcr = t.getCellRenderer();
        if (tcr instanceof DefaultTreeCellRenderer) {
            Object node = t.getPathForRow(offsetRow).getLastPathComponent();
            Icon icon;
            if (t.getModel().isLeaf(node))
                icon = ((DefaultTreeCellRenderer) tcr).getLeafIcon();
            else if (this.jTreeTable.getTree().isExpanded(offsetRow))
                icon = ((DefaultTreeCellRenderer) tcr).getOpenIcon();
            else
                icon = ((DefaultTreeCellRenderer) tcr).getClosedIcon();
            if (icon != null) {
                offset += ((DefaultTreeCellRenderer) tcr).getIconTextGap() + icon.getIconWidth();
            }
        }
        ((TreeTableTextField) getComponent()).offset = offset;
        return component;
    }

    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) e;
            if (me.getModifiers() == 0 || me.getModifiers() == InputEvent.BUTTON1_MASK) {
                for (int counter = jTreeTable.getColumnCount() - 1; counter >= 0; counter--) {
                    if (jTreeTable.getColumnClass(counter) == TreeTableModel.class) {
                        MouseEvent newME = new MouseEvent(jTreeTable.getTree(), me.getID(), me.getWhen(), me.getModifiers(), me.getX() - jTreeTable.getCellRect(0, counter, true).x, me.getY(), me.getClickCount(), me.isPopupTrigger());
                        this.jTreeTable.getTree().dispatchEvent(newME);
                        break;
                    }
                }
            }
            if (me.getClickCount() >= 3) {
                return this.jTreeTable.getTreeEditable();
            }
            return false;
        }
        if (e == null) {
            return this.jTreeTable.getTreeEditable();
        }
        return false;
    }

    public static class TreeTableTextField extends JTextField {
        public int offset;

        public void setBounds(int x, int y, int w, int h) {
            int newX = Math.max(x, offset);
            super.setBounds(newX, y, w - (newX - x), h);
        }
    }
}
