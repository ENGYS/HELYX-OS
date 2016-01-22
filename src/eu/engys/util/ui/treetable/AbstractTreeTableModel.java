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

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

public abstract class AbstractTreeTableModel implements TreeTableModel {
    protected Object root;
    protected EventListenerList listenerList = new EventListenerList();

    public AbstractTreeTableModel(Object root) {
        this.root = root;
    }

    public Object getRoot() {
        return root;
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public int getIndexOfChild(Object parent, Object child) {
        for (int i = 0; i < getChildCount(parent); i++) {
            if (getChild(parent, i).equals(child)) {
                return i;
            }
        }
        return -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
            }
        }
    }

    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
            }
        }
    }

    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
            }
        }
    }

    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }

    public Class<?> getColumnClass(int column) {
        return Object.class;
    }

    public boolean isCellEditable(Object node, int column) {
        return getColumnClass(column) == TreeTableModel.class;
    }

    public void setValueAt(Object aValue, Object node, int column) {
    }

}
