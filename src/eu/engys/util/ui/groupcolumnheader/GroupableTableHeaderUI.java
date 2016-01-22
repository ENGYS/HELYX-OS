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


package eu.engys.util.ui.groupcolumnheader;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class GroupableTableHeaderUI extends BasicTableHeaderUI {

    protected Vector<ColumnGroup> paintedGroups = new Vector<ColumnGroup>();

    @Override
    public void paint(Graphics g, JComponent c) {
        Rectangle clipBounds = g.getClipBounds();
        GroupableTableColumnModel cm = (GroupableTableColumnModel) header.getColumnModel();
        if (cm == null)
            return;
        ((GroupableTableHeader) header).setColumnMargin();
        int column = 0;
        Dimension size = header.getSize();
        Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
        Hashtable<ColumnGroup, Rectangle> h = new Hashtable<ColumnGroup, Rectangle>();

        Enumeration<TableColumn> columns = cm.getColumns();
        while (columns.hasMoreElements()) {
            cellRect.height = size.height;
            cellRect.y = 0;
            TableColumn aColumn = (TableColumn) columns.nextElement();
            Iterator<Object> colGrpIter = cm.getColumnGroups(aColumn);
            if (colGrpIter != null) {
                int groupHeight = 0;
                while (colGrpIter.hasNext()) {
                    ColumnGroup cGroup = (ColumnGroup) colGrpIter.next();
                    Rectangle groupRect = (Rectangle) h.get(cGroup);
                    if (groupRect == null) {
                        groupRect = new Rectangle(cellRect);
                        Dimension d = cGroup.getSize(header.getTable());
                        groupRect.width = d.width;
                        groupRect.height = d.height;
                        h.put(cGroup, groupRect);
                    }
                    if (!paintedGroups.contains(cGroup)) {
                        paintCell(g, groupRect, cGroup);
                        paintedGroups.add(cGroup);
                    }
                    groupHeight += groupRect.height;
                    cellRect.height = size.height - groupHeight;
                    cellRect.y = groupHeight;
                }
            }
            cellRect.width = aColumn.getWidth();
            if (cellRect.intersects(clipBounds)) {
                paintCell(g, cellRect, column);
            }
            cellRect.x += cellRect.width;
            column++;
        }
        paintedGroups.clear();
    }

    private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
        TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
        TableCellRenderer renderer = aColumn.getHeaderRenderer();
        if (renderer == null) {
            renderer = header.getDefaultRenderer();
        }
        Component component = renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(), false, false, -1, columnIndex);
        rendererPane.add(component);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }

    private void paintCell(Graphics g, Rectangle cellRect, ColumnGroup cGroup) {
        TableCellRenderer renderer = cGroup.getHeaderRenderer();
        Component component = renderer.getTableCellRendererComponent(header.getTable(), cGroup.getHeaderValue(), false, false, -1, -1);
        rendererPane.add(component);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }

    private int getHeaderHeight() {
        int height = 0;
        GroupableTableColumnModel columnModel = (GroupableTableColumnModel) header.getColumnModel();
        for (int column = 0; column < columnModel.getColumnCount(); column++) {
            TableColumn aColumn = columnModel.getColumn(column);
            TableCellRenderer renderer = aColumn.getHeaderRenderer();
            if (renderer == null) {
                renderer = header.getDefaultRenderer();
            }
            Component comp = renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(), false, false, -1, column);
            int cHeight = comp.getPreferredSize().height;
            Iterator<Object> iter = columnModel.getColumnGroups(aColumn);
            if (iter != null) {
                while (iter.hasNext()) {
                    ColumnGroup cGroup = (ColumnGroup) iter.next();
                    cHeight += cGroup.getSize(header.getTable()).height;
                }
            }
            height = Math.max(height, cHeight);
        }
        return height;
    }

    private Dimension createHeaderSize(long width) {
        TableColumnModel columnModel = header.getColumnModel();
        width += columnModel.getColumnMargin() * columnModel.getColumnCount();
        if (width > Integer.MAX_VALUE) {
            width = Integer.MAX_VALUE;
        }
        return new Dimension((int) width, getHeaderHeight());
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        long width = 0;
        Enumeration<TableColumn> columns = header.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn aColumn = (TableColumn) columns.nextElement();
            width = width + aColumn.getPreferredWidth();
        }
        return createHeaderSize(width);
    }
}
