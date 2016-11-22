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
package eu.engys.util.ui.checkboxtree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.MouseAdapter;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import eu.engys.util.ui.checkboxtree.TristateCheckBox.State;
import eu.engys.util.ui.treetable.tree.TreeTableTreeModel;

public class AddCheckBoxToTree {

    public interface CheckBoxSelectionListener {
        void selectionAdded(DefaultMutableTreeNode userObject);

        void selectionRemoved(DefaultMutableTreeNode userObject);
    }

    public static CheckTreeManager toTree(JTree tree) {
        return new CheckTreeManager(tree);
    }

    public static class CheckTreeManager extends MouseAdapter {

        private final CheckTreeSelectionModel checkSelectionModel;
        private final CheckTreeCellRenderer checkCellRenderer;
        private final SelectionModelProxy selectionModel;
        private final JTree tree;

        public CheckTreeManager(final JTree tree) {
            this.tree = tree;
            this.checkSelectionModel = new CheckTreeSelectionModel(tree.getModel());
            this.checkCellRenderer = new CheckTreeCellRenderer(tree.getCellRenderer(), checkSelectionModel);
            this.selectionModel = new SelectionModelProxy(tree, checkSelectionModel);

            tree.setCellRenderer(checkCellRenderer);
            tree.setSelectionModel(selectionModel);

            tree.getModel().addTreeModelListener(new CheckTreeModelListener(selectionModel));
        }

        public CheckTreeManager withListener(CheckBoxSelectionListener l) {
            checkSelectionModel.setListener(l);
            return this;
        }

        public void selectNode(DefaultMutableTreeNode node) {
            CheckBoxSelectionListener listener = checkSelectionModel.removeListener();
            if (tree.getModel() instanceof DefaultTreeModel) {
                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                checkSelectionModel.addSelectionPath(new TreePath(treeModel.getPathToRoot(node)));
            } else if (tree.getModel() instanceof TreeTableTreeModel) {
                TreeTableTreeModel treeModel = (TreeTableTreeModel) tree.getModel();
                checkSelectionModel.addSelectionPath(new TreePath(treeModel.getPathToRoot(node)));
            }
            checkSelectionModel.setListener(listener);
        }

        public void deselectNode(DefaultMutableTreeNode node) {
            CheckBoxSelectionListener listener = checkSelectionModel.removeListener();
            if (tree.getModel() instanceof DefaultTreeModel) {
                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                checkSelectionModel.removeSelectionPath(new TreePath(treeModel.getPathToRoot(node)));
            } else if (tree.getModel() instanceof TreeTableTreeModel) {
                TreeTableTreeModel treeModel = (TreeTableTreeModel) tree.getModel();
                checkSelectionModel.removeSelectionPath(new TreePath(treeModel.getPathToRoot(node)));
            }
            checkSelectionModel.setListener(listener);
        }

        public void clearSelection() {
            checkSelectionModel.clearSelection();
        }
    }

    private static class CheckTreeModelListener implements TreeModelListener {

        private SelectionModelProxy selectionModel;

        public CheckTreeModelListener(SelectionModelProxy selectionModel) {
            this.selectionModel = selectionModel;
        }

        @Override
        public void treeNodesChanged(TreeModelEvent e) {
        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
            // System.out.println("AddCheckBoxToTree.CheckTreeModelListener.treeStructureChanged()");
            selectionModel.adjustSelection((DefaultMutableTreeNode) new TreePath(e.getPath()).getLastPathComponent());
        }

    }

    private static class SelectionModelProxy extends DefaultTreeSelectionModel {
        private final int hotspot = new JCheckBox().getPreferredSize().width;
        private final TreeSelectionModel delegate;
        private final JTree tree;
        private final CheckTreeSelectionModel checkSelectionModel;

        private SelectionModelProxy(JTree tree, CheckTreeSelectionModel checkSelectionModel) {
            this.tree = tree;
            this.checkSelectionModel = checkSelectionModel;
            this.delegate = tree.getSelectionModel();
        }

        public void adjustSelection(DefaultMutableTreeNode node) {
            if (node.isLeaf()) {
                if (checkBoxIsVisible(node) && nodeToVisibleItm(node).isVisible()) {
                    // System.out.println("AddCheckBoxToTree.SelectionModelProxy.adjustSelection() -> "+nodeToVisibleItm(node));
                    if (tree.getModel() instanceof DefaultTreeModel) {
                        checkSelectionModel.addSelectionPath(new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(node)));
                    } else {

                    }
                }
            } else {
                for (int i = 0; i < node.getChildCount(); i++) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                    adjustSelection(child);
                }
            }
        }

        @Override
        public void addSelectionPaths(TreePath[] paths) {
            // System.out.println("AddCheckBoxToTree.SelectionModelProxy.addSelectionPaths()");
            Point mousePosition = tree.getMousePosition();
            if (mousePosition != null && paths != null && paths.length > 0) {
                if (checkBoxIsVisible(paths[0]) && clickIsInsideCheckBox(mousePosition.x, paths[0])) {
                    if (isAMultipleSelection(paths[0])) {
                        setCheckSelectionPaths(paths[0], delegate.getSelectionPaths());
                    } else {
                        setCheckSelectionPath(paths[0]);
                    }
                } else {
                    delegate.addSelectionPaths(paths);
                    super.addSelectionPaths(delegate.getSelectionPaths());
                }
            } else {
                delegate.addSelectionPaths(paths);
                super.addSelectionPaths(delegate.getSelectionPaths());
            }
        }

        private boolean checkBoxIsVisible(TreePath path) {
            return checkBoxIsVisible((DefaultMutableTreeNode) path.getLastPathComponent());
        }

        private boolean checkBoxIsVisible(DefaultMutableTreeNode node) {
            return node.getUserObject() instanceof VisibleItem;
        }

        private VisibleItem nodeToVisibleItm(DefaultMutableTreeNode node) {
            return (VisibleItem) node.getUserObject();
        }

        @Override
        public void setSelectionPaths(TreePath[] paths) {
            Point mousePosition = getMousePosition();
            if (paths != null && paths.length > 0) {
                if (mousePosition != null && checkBoxIsVisible(paths[0]) && clickIsInsideCheckBox(mousePosition.x, paths[0])) {
                    if (isAMultipleSelection(paths[0])) {
                        setCheckSelectionPaths(paths[0], delegate.getSelectionPaths());
                    } else {
                        setCheckSelectionPath(paths[0]);
                    }
                } else {
                    delegate.setSelectionPaths(paths);
                    super.setSelectionPaths(delegate.getSelectionPaths());
                }
            }
        }

        private Point getMousePosition() {
            Point mousePosition = tree.getMousePosition();
            if (tree.getParent() != null && mousePosition != null) {
                return mousePosition;
            } else {
                PointerInfo pointerInfo = MouseInfo.getPointerInfo();
                Point point = new Point(pointerInfo.getLocation());
                SwingUtilities.convertPointFromScreen(point, tree);
                return point;
            }
        }

        private boolean isAMultipleSelection(TreePath treePath) {
            TreePath[] selectionPaths = delegate.getSelectionPaths();
            if (selectionPaths != null && selectionPaths.length > 1) {
                for (TreePath tp : selectionPaths) {
                    if (tp.equals(treePath))
                        return true;
                }
            }
            return false;
        }

        public boolean clickIsInsideCheckBox(int x, TreePath path) {
            return x < tree.getPathBounds(path).x + hotspot;
        }

        public void setCheckSelectionPath(TreePath path) {
            // System.out.println("AddCheckBoxToTree.SelectionModelProxy.setCheckSelectionPath() >>>>>>>>>>>>> ");
            if (path == null) {
                return;
            }

            boolean selected = checkSelectionModel.isPathSelected(path, true);

            try {
                if (selected) {
                    checkSelectionModel.removeSelectionPath(path);
                } else {
                    checkSelectionModel.addSelectionPath(path);
                }
            } finally {
                tree.treeDidChange();
                tree.getParent().revalidate();
                tree.getParent().repaint();
            }
        }

        public void setCheckSelectionPaths(TreePath path, TreePath[] paths) {
            if (paths == null || path == null) {
                return;
            }

            boolean selected = checkSelectionModel.isPathSelected(path, true);

            try {
                if (selected) {
                    checkSelectionModel.removeSelectionPath(path);
                    for (TreePath treePath : paths) {
                        checkSelectionModel.removeSelectionPath(treePath);
                    }
                } else {
                    checkSelectionModel.addSelectionPath(path);
                    for (TreePath treePath : paths) {
                        checkSelectionModel.addSelectionPath(treePath);
                    }
                }
            } finally {
                tree.treeDidChange();
            }
        }
    }

    public static class CheckTreeSelectionModel extends DefaultTreeSelectionModel {
        static final long serialVersionUID = 0;
        private TreeModel model;
        private CheckBoxSelectionListener listener;

        public CheckTreeSelectionModel(TreeModel model) {
            this.model = model;
            setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        }

        public CheckBoxSelectionListener removeListener() {
            CheckBoxSelectionListener l = this.listener;
            this.listener = null;
            return l;
        }

        public void setListener(CheckBoxSelectionListener l) {
            this.listener = l;
        }

        @Override
        public void addSelectionPath(TreePath path) {
            // System.out.println("AddCheckBoxToTree.CheckTreeSelectionModel.addSelectionPath() "+path);
            super.addSelectionPath(path);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (listener != null)
                listener.selectionAdded(node);
        }

        @Override
        public void removeSelectionPath(TreePath path) {
            // System.out.println("AddCheckBoxToTree.CheckTreeSelectionModel.removeSelectionPath() "+path);
            super.removeSelectionPath(path);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (listener != null)
                listener.selectionRemoved(node);
        }

        // tests whether there is any unselected node in the subtree of given path (DONT_CARE)
        public boolean isPartiallySelected(TreePath path) {
            if (isPathSelected(path, true)) {
                return false;
            }

            TreePath[] selectionPaths = getSelectionPaths();

            if (selectionPaths == null) {
                return false;
            }

            for (int j = 0; j < selectionPaths.length; j++) {
                if (isDescendant(selectionPaths[j], path)) {
                    return true;
                }
            }

            return false;
        }

        // tells whether given path is selected.
        // if dig is true, then a path is assumed to be selected, if
        // one of its ancestor is selected.
        public boolean isPathSelected(TreePath path, boolean dig) {
            if (!dig) {
                return super.isPathSelected(path);
            }

            while (path != null && !super.isPathSelected(path)) {
                path = path.getParentPath();
            }

            return path != null;
        }

        // is path1 descendant of path2
        private boolean isDescendant(TreePath path1, TreePath path2) {
            return path1 != path2 && path2.isDescendant(path1);
        }

        public void setSelectionPaths(TreePath[] pPaths) {
            throw new UnsupportedOperationException("not implemented yet!!!");
        }

        public void addSelectionPaths(TreePath[] paths) {

            // unselect all descendants of paths[]
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];

                TreePath[] selectionPaths = getSelectionPaths();

                if (selectionPaths == null) {
                    break;
                }

                ArrayList<TreePath> toBeRemoved = new ArrayList<TreePath>();

                for (int j = 0; j < selectionPaths.length; j++) {
                    if (isDescendant(selectionPaths[j], path)) {
                        toBeRemoved.add(selectionPaths[j]);
                    }
                }
                // System.out.println("AddCheckBoxToTree.CheckTreeSelectionModel.addSelectionPaths() -> removeSelectionPaths "+toBeRemoved);
                super.removeSelectionPaths((TreePath[]) toBeRemoved.toArray(new TreePath[0]));
            }

            // if all siblings are selected then unselect them and select parent
            // recursively
            // otherwise just select that path.
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];

                TreePath temp = null;

                while (areSiblingsSelected(path)) {
                    temp = path;

                    if (path.getParentPath() == null) {
                        break;
                    }

                    path = path.getParentPath();
                }

                if (temp != null) {
                    if (temp.getParentPath() != null) {
                        addSelectionPath(temp.getParentPath());
                    } else {
                        if (!isSelectionEmpty()) {
                            removeSelectionPaths(getSelectionPaths());
                        }
                        // System.out.println("AddCheckBoxToTree.CheckTreeSelectionModel.addSelectionPaths() -> addSelectionPaths temp: "+temp);
                        super.addSelectionPaths(new TreePath[] { temp });
                    }
                } else {
                    // System.out.println("AddCheckBoxToTree.CheckTreeSelectionModel.addSelectionPaths() -> addSelectionPaths path: "+path);
                    super.addSelectionPaths(new TreePath[] { path });
                }
            }
        }

        // tells whether all siblings of given path are selected.
        private boolean areSiblingsSelected(TreePath path) {
            TreePath parent = path.getParentPath();

            if (parent == null) {
                return true;
            }

            Object node = path.getLastPathComponent();

            Object parentNode = parent.getLastPathComponent();

            int childCount = model.getChildCount(parentNode);

            for (int i = 0; i < childCount; i++) {

                Object childNode = model.getChild(parentNode, i);

                if (childNode == node) {
                    continue;
                }

                if (!isPathSelected(parent.pathByAddingChild(childNode))) {
                    return false;
                }
            }

            return true;
        }

        public void removeSelectionPaths(TreePath[] paths) {
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                if (path.getPathCount() == 1) {
                    // System.out.println("AddCheckBoxToTree.CheckTreeSelectionModel.removeSelectionPaths() -> removeSelectionPaths "+path);
                    super.removeSelectionPaths(new TreePath[] { path });
                } else {
                    toggleRemoveSelection(path);
                }
            }
        }

        /**
         * if any ancestor node of given path is selected then unselect it and selection all its descendants except given path and descendants. otherwise just unselect the given path
         */
        private void toggleRemoveSelection(TreePath path) {
            // System.out.println("AddCheckBoxToTree.CheckTreeSelectionModel.toggleRemoveSelection() path: " + path);
            Stack<TreePath> stack = new Stack<TreePath>();
            TreePath parent = path.getParentPath();

            // System.out.println("AddCheckBoxToTree.CheckTreeSelectionModel.toggleRemoveSelection() parent: " + parent);

            // System.out.println("AddCheckBoxToTree.CheckTreeSelectionModel.toggleRemoveSelection() is parent selected: " + isPathSelected(parent));
            while (parent != null && !isPathSelected(parent)) {
                stack.push(parent);
                parent = parent.getParentPath();
            }
            if (parent != null)
                stack.push(parent);
            else {
                // System.out.println("AddCheckBoxToTree.CheckTreeSelectionModel.toggleRemoveSelection() -> removeSelectionPaths path: "+path);
                super.removeSelectionPaths(new TreePath[] { path });
                return;
            }

            while (!stack.isEmpty()) {
                TreePath temp = (TreePath) stack.pop();

                TreePath peekPath = stack.isEmpty() ? path : (TreePath) stack.peek();

                Object node = temp.getLastPathComponent();
                Object peekNode = peekPath.getLastPathComponent();
                int childCount = model.getChildCount(node);

                for (int i = 0; i < childCount; i++) {
                    Object childNode = model.getChild(node, i);

                    if (childNode != peekNode) {
                        TreePath pathByAddingChild = temp.pathByAddingChild(childNode);
                        // System.out.println("AddCheckBoxToTree.CheckTreeSelectionModel.toggleRemoveSelection() -> addSelectionPAth pathByAddingChild: "+pathByAddingChild);
                        super.addSelectionPaths(new TreePath[] { pathByAddingChild });
                    }
                }
            }

            // System.out.println("AddCheckBoxToTree.CheckTreeSelectionModel.toggleRemoveSelection() -> removeSelectionPaths parent: "+parent);
            super.removeSelectionPaths(new TreePath[] { parent });
        }

        public TreeModel getModel() {
            return model;
        }
    }

    public static class CheckTreeCellRenderer extends JPanel implements TreeCellRenderer {

        static final long serialVersionUID = 0;

        CheckTreeSelectionModel selectionModel;
        private TreeCellRenderer delegate;
        private TristateCheckBox checkBox;
        private JCheckBox fakeBox;

        public CheckTreeCellRenderer(TreeCellRenderer delegate, CheckTreeSelectionModel selectionModel) {
            this.delegate = delegate;
            this.selectionModel = selectionModel;

            setLayout(new BorderLayout());
            setOpaque(false);

            checkBox = new TristateCheckBox();
            checkBox.setOpaque(false);

            fakeBox = new JCheckBox() {
                protected void paintComponent(java.awt.Graphics g) {
                };
            };
            fakeBox.setOpaque(false);
        }

        public void setBackgroundSelectionColor(Color c) {
            if (delegate instanceof DefaultTreeCellRenderer) {
                ((DefaultTreeCellRenderer) delegate).setBackgroundSelectionColor(c);
            }
        }

        public void setBackgroundNonSelectionColor(Color c) {
            if (delegate instanceof DefaultTreeCellRenderer) {
                ((DefaultTreeCellRenderer) delegate).setBackgroundNonSelectionColor(c);
            }
        }

        public void setTextSelectionColor(Color c) {
            if (delegate instanceof DefaultTreeCellRenderer) {
                ((DefaultTreeCellRenderer) delegate).setTextSelectionColor(c);
            }
        }

        public void setTextNonSelectionColor(Color c) {
            if (delegate instanceof DefaultTreeCellRenderer) {
                ((DefaultTreeCellRenderer) delegate).setTextNonSelectionColor(c);
            }
        }

        @Override
        @Transient
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.width = d.width + 20;
            return d;
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component renderer = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            TreePath path = tree.getPathForRow(row);

            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                // int level = node.getLevel();

                if (node.getLevel() == 1) {
                    setFont(tree.getFont().deriveFont(Font.BOLD));
                } else {
                    setFont(tree.getFont().deriveFont(Font.PLAIN));
                }

                if (node.isRoot() || !(node.getUserObject() instanceof VisibleItem)) {
                    removeAll();
                    add(renderer, BorderLayout.CENTER);
                    return this;
                }

                if ((node.getUserObject() instanceof LoadableItem && !((LoadableItem) node.getUserObject()).isLoaded())) {
                    removeAll();
                    add(renderer, BorderLayout.CENTER);
                    return this;
                }

                if (selectionModel.isPathSelected(path, true)) {
                    checkBox.setState(State.SELECTED);
                    // System.out.println(">>>>>>     selected: " + path);
                } else {
                    checkBox.setState(State.NOT_SELECTED);
                    // System.out.println(">>>>>>     not selected: " + path);
                }

                if (selectionModel.isPartiallySelected(path)) {
                    checkBox.setState(State.DONT_CARE);
                }
            }

            removeAll();

            add(checkBox, BorderLayout.WEST);
            add(renderer, BorderLayout.CENTER);

            return this;
        }
    }
}
