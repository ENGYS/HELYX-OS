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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;

import eu.engys.util.ui.checkboxtree.AddCheckBoxToTree.CheckTreeCellRenderer;
import eu.engys.util.ui.treetable.JTreeTable;

public class TreeTableCellRenderer extends JTree implements TableCellRenderer {

    private final JTreeTable jTreeTable;
    private int visibleRow;
    private TableCellRenderer tableDefaultRenderer;

    public TreeTableCellRenderer(JTreeTable jTreeTable, TreeModel model) {
        super(model);
        this.jTreeTable = jTreeTable;
        this.tableDefaultRenderer = jTreeTable.getDefaultRenderer(Object.class);
        setCellRenderer(new JTreeTableCellRenderer(jTreeTable));
    }

    public void setBounds(int x, int y, int w, int h) {
        if (x < 0) return; 
        super.setBounds(x + 10, 0, w, this.jTreeTable.getHeight());
    }

    public void paint(Graphics g) {
        g.translate(0, -visibleRow * getRowHeight() - 3);
        super.paint(g);
    }

    @Override
    public Container getParent() {
        return jTreeTable;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        visibleRow = table.convertRowIndexToModel(row);

        JComponent c = (JComponent) tableDefaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        Color background = c.getBackground();
        Color foreground = c.getForeground();
        Border border = c.getBorder();

        setBackground(background);
        setForeground(foreground);
        setBorder(border);
        
        if (getCellRenderer() instanceof CheckTreeCellRenderer) {
            ((CheckTreeCellRenderer) getCellRenderer()).setBackgroundSelectionColor(background);
            ((CheckTreeCellRenderer) getCellRenderer()).setBackgroundNonSelectionColor(background);
        } else {
            ((JLabel) getCellRenderer()).setBackground(background);
            ((JLabel) getCellRenderer()).setForeground(foreground);
        }

        return this;
    }
}
