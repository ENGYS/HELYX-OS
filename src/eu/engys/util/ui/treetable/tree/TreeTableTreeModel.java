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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class TreeTableTreeModel implements TreeModel {

    private TreeTableTreeNode root;

    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

    public TreeTableTreeModel(TreeTableTreeNode root) {
        this.root = root;
    }

    @Override
    public Object getRoot() {
        return root;
    }
    
    @Override
    public Object getChild(Object parent, int index) {
        return ((TreeTableTreeNode) parent).getChild(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((TreeTableTreeNode) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        if (node == null) {
            return true;
        }
        return ((TreeTableTreeNode) node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((TreeTableTreeNode) parent).getIndexOfChild((TreeTableTreeNode) child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public void fireTreeStructureChanged() {
        TreeModelEvent e = new TreeModelEvent(getRoot(), new Object[] { getRoot() }, null, null);
        for (Iterator<TreeModelListener> iter = listeners.iterator(); iter.hasNext();) {
            TreeModelListener l = iter.next();
            l.treeStructureChanged(e);
        }
    }

    public TreeNode[] getPathToRoot(TreeNode aNode) {
        return getPathToRoot(aNode, 0);
    }

    private TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
        TreeNode[] retNodes;
        if (aNode == null) {
            if (depth == 0)
                return null;
            else
                retNodes = new TreeNode[depth];
        } else {
            depth++;
            if (aNode == root)
                retNodes = new TreeNode[depth];
            else
                retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }
}
