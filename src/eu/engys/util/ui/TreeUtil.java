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


package eu.engys.util.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

public class TreeUtil {

    public static boolean areSiblings(TreePath[] selectionPath) {
        if (selectionPath.length == 0)
            return true;
        return areSiblings(selectionPath, selectionPath[0].getParentPath());
    }

    public static boolean areSiblings(TreePath[] selectionPath, TreePath parent) {
        if (selectionPath.length == 0)
            return true;
        for (TreePath path : selectionPath) {
            if (!parent.isDescendant(path)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isConsistent(List<Object> selection, Class<?> klass) {
        return isConsistent(selection.toArray(), klass);
    }

    public static boolean isConsistent(Object[] selection, Class<?> klass) {
        if (selection == null || selection.length == 0)
            return false;
        for (Object object : selection) {
            if (!(klass.isInstance(object))) {
                return false;
            }
        }
        return true;
    }

    public static List<Object> toUserObjects(TreePath[] selectionPath) {
        List<Object> selection = new ArrayList<>();
        for (TreePath treePath : selectionPath) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            Object userObject = node.getUserObject();
            selection.add(userObject);
        }
        return selection;
    }

    public static Object[] toUserObjectsArray(TreePath[] selectionPath) {
        return toUserObjects(selectionPath).toArray();
    }

    public static TreePath[] getAConsistentSelection(TreePath[] selectionPath) {
        List<TreePath> consistentPath = new ArrayList<>();
        Object[] selection = TreeUtil.toUserObjectsArray(selectionPath);
        Class<?> firstClass = selection[0].getClass();

        for (int i = 0; i < selection.length; i++) {
            if (firstClass.isInstance(selection[i])) {
                // System.out.println("TreeUtil.getAConsistentSelection() "+firstClass+" == "+selection[i].getClass());
                consistentPath.add(selectionPath[i]);
            } else {
                // System.out.println("TreeUtil.getAConsistentSelection() "+firstClass+" != "+selection[i].getClass());

            }
        }

        return consistentPath.toArray(new TreePath[0]);
    }

    public static DefaultMutableTreeNode getFirstLevelParent(DefaultMutableTreeNode node) {
        if (node.getLevel() == 1) {
            return node;
        }
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        if (parent != null && parent.getLevel() == 1)
            return parent;
        else
            return getFirstLevelParent(parent);
    }

    private static TreeNodeComparator tnc = new TreeNodeComparator();

    public static void sortTree(DefaultMutableTreeNode root) {
        Enumeration e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (!node.isLeaf()) {
                sortChildren(node);
            }
        }
    }

    public static void sortChildren(DefaultMutableTreeNode parent) {
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = parent.children();
        List<DefaultMutableTreeNode> children = Collections.list(e);

        Collections.sort(children, tnc);
        parent.removeAllChildren();
        for (MutableTreeNode node : children) {
            parent.add(node);
        }
    }

    private static class TreeNodeComparator implements Comparator<DefaultMutableTreeNode> {
        @Override
        public int compare(DefaultMutableTreeNode a, DefaultMutableTreeNode b) {
            if (a.getLevel() > b.getLevel()) {
                return 1;
            } else if (a.getLevel() < b.getLevel()) {
                return -1;
            } else {
                String sa = a.getUserObject().toString();
                String sb = b.getUserObject().toString();
                return sa.compareToIgnoreCase(sb);
            }
        }
    }
}
