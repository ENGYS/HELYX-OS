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

package eu.engys.util.ui.treetable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.RowSorter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import eu.engys.util.ui.TableUtil;
import eu.engys.util.ui.treetable.tree.JTreeTableCellRenderer;
import eu.engys.util.ui.treetable.tree.ListToTreeSelectionModelWrapper;
import eu.engys.util.ui.treetable.tree.TreeTableCellRenderer;

public class JTreeTable extends JTable {

    private TreeTableCellRenderer tree;

    private boolean treeEditable = true;
    private boolean showsIcons = false;

    private TableRowSorter<TreeTableModelAdapter> sorter;
    private TableFilter<TreeTableModelAdapter> filter;

    public JTreeTable(TreeTableModel treeTableModel) {
        super();

        this.tree = new TreeTableCellRenderer(this, treeTableModel);

        TreeTableModelAdapter dataModel = new TreeTableModelAdapter(treeTableModel, tree);
        super.setModel(dataModel);

        this.sorter = new TableRowSorter<TreeTableModelAdapter>(dataModel);
        setRowSorter(sorter);

        TableUtil.disableSorting(this);

        this.filter = new TableFilter<TreeTableModelAdapter>("");
        sorter.setRowFilter(filter);

        ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper(tree);
        tree.setSelectionModel(selectionWrapper);
        setSelectionModel(selectionWrapper.getListSelectionModel());

        setDefaultRenderer(TreeTableModel.class, tree);
        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor(this));

        setShowGrid(false);

        setIntercellSpacing(new Dimension(0, 0));

        if (tree.getRowHeight() < 1) {
            setRowHeight(20);
        }
    }

    public void filterOn() {
        setRowSorter(sorter);
    }
    
    public void filterOff() {
        setRowSorter(null);
    }

    public void filter(int i, String filterText) {
        filter.setFilterText(i, filterText);
        sorter.sort();
    }

    public void resetFilter() {
        filter.reset();
        sorter.sort();
    }

    public void removeRow(int row) {
        ((TreeTableModelAdapter) getModel()).removeRow(row);
    }

    public Object[] getNodes(int[] index) {
        Object[] nodes = new Object[index.length];
        for (int i = 0; i < index.length; i++) {
            nodes[i] = ((TreeTableModelAdapter) getModel()).nodeForRow(index[i]);
        }
        return nodes;
    }

    public Object getNode(int index) {
        RowSorter<? extends TableModel> rowSorter = getRowSorter();
        int cIndex = rowSorter != null ? rowSorter.convertRowIndexToModel(index) : index;
        return ((TreeTableModelAdapter) getModel()).nodeForRow(cIndex);
    }
    
    public boolean isExpanded(int row) {
        return tree.isExpanded(row);
    }

    public boolean isCollapsed(int row) {
        return tree.isCollapsed(row);
    }

    public void updateUI() {
        super.updateUI();
        if (tree != null) {
            tree.updateUI();
        }
    }

    public int getEditingRow() {
        return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;
    }

    private int realEditingRow() {
        return editingRow;
    }

    public void sizeColumnsToFit(int resizingColumn) {
        super.sizeColumnsToFit(resizingColumn);
        if (getEditingColumn() != -1 && getColumnClass(editingColumn) == TreeTableModel.class) {
            Rectangle cellRect = getCellRect(realEditingRow(), getEditingColumn(), false);
            Component component = getEditorComponent();
            component.setBounds(cellRect);
            component.validate();
        }
    }

    public void setRowHeight(int rowHeight) {
        super.setRowHeight(rowHeight);
        if (tree != null && tree.getRowHeight() != rowHeight) {
            tree.setRowHeight(getRowHeight());
        }
    }

    public JTree getTree() {
        return tree;
    }

    public void setTreeRenderer(JTreeTableCellRenderer renderer) {
        tree.setCellRenderer(renderer);
    }

    public boolean editCellAt(int row, int column, EventObject e) {
        boolean retValue = super.editCellAt(row, column, e);
        if (retValue && getColumnClass(column) == TreeTableModel.class) {
            repaint(getCellRect(row, column, false));
        }
        return retValue;
    }

    public void expandAll() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    public void collapseAll() {
        tree.collapsePath(tree.getPathForRow(0));
    }

    public void setTreeEditable(boolean treeEditable) {
        this.treeEditable = treeEditable;
    }
    public boolean getTreeEditable() {
        return treeEditable;
    }

    public boolean getShowsIcons() {
        return showsIcons;
    }

    public void setShowsIcons(boolean showsIcons) {
        this.showsIcons = showsIcons;
    }

    public void setRootVisible(boolean visible) {
        tree.setRootVisible(visible);
    }

    public boolean getShowsRootHandles() {
        return tree.getShowsRootHandles();
    }

    public void setShowsRootHandles(boolean newValue) {
        tree.setShowsRootHandles(newValue);
    }
}
