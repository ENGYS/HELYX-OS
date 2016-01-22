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

package eu.engys.gui.tree;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import eu.engys.core.modules.ModulePanel;
import eu.engys.gui.GUIPanel;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.EventManager.Event;
import eu.engys.gui.events.EventManager.GenericEventListener;
import eu.engys.gui.events.view3D.ActorPopUpEvent;
import eu.engys.gui.events.view3D.ActorSelectionEvent;
import eu.engys.gui.events.view3D.ActorVisibilityEvent;
import eu.engys.gui.events.view3D.VisibleItemEvent;
import eu.engys.gui.tree.TreeNodeManager.PopUpBuilder;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Picker;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.TreeUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.checkboxtree.AddCheckBoxToTree;
import eu.engys.util.ui.checkboxtree.AddCheckBoxToTree.CheckBoxSelectionListener;
import eu.engys.util.ui.checkboxtree.AddCheckBoxToTree.CheckTreeManager;
import eu.engys.util.ui.checkboxtree.RootVisibleItem;
import eu.engys.util.ui.checkboxtree.VisibleItem;

public class Tree extends JScrollPane {

    private final GUIPanelHandler panelsHandler;

    private DefaultTreeModel treeModel;
    private Map<GUIPanel, DefaultMutableTreeNode> nodesMap = new HashMap<GUIPanel, DefaultMutableTreeNode>();
    private Map<Class<?>, DefaultTreeCellRenderer> renderersMap = new HashMap<>();
    private Map<DefaultMutableTreeNode, SelectionHandler> selectionHandlersMap = new HashMap<>();
    private Map<Class<?>, PopUpBuilder> popupBuildersMap = new HashMap<>();
    private JTree tree;
    private DefaultMutableTreeNode root;
    private CheckTreeManager checkManager;

    private boolean fire3DEvent = true;

    private PopUpMenuListener popUp;

    public Tree(GUIPanelHandler panelsHandler) {
        super();
        this.panelsHandler = panelsHandler;
        layoutComponents();
    }

    private void layoutComponents() {
        root = new DefaultMutableTreeNode();
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.setToggleClickCount(0);
        tree.setSelectionModel(new TreeSelectionModel());
        tree.setRootVisible(false);
        tree.setCellRenderer(new TreeRenderer());
        tree.setRowHeight(20);
        tree.setLargeModel(true);

        setViewportView(tree);

        Set<GUIPanel> panels = panelsHandler.getPanels();
        for (GUIPanel guiPanel : panels) {
            addPanel(guiPanel);
        }

        treeModel.nodeStructureChanged(root);

        checkManager = AddCheckBoxToTree.toTree(tree).withListener(new CheckBoxTreeToPanels());
        tree.getSelectionModel().addTreeSelectionListener(new TreeToPanels());

        popUp = new PopUpMenuListener();
        tree.addMouseListener(popUp);
    }

    public CheckTreeManager getCheckManager() {
        return checkManager;
    }

    public void addListener() {
        EventManager.registerEventListener(new ActorPopUpListener(), ActorPopUpEvent.class);
        EventManager.registerEventListener(new ActorSelectionListener(), ActorSelectionEvent.class);
        EventManager.registerEventListener(new ActorVisibilityListener(), ActorVisibilityEvent.class);
    }

    public void removeListener() {
        EventManager.unregisterEventSubscriptions(ActorPopUpEvent.class);
        EventManager.unregisterEventSubscriptions(ActorSelectionEvent.class);
        EventManager.unregisterEventSubscriptions(ActorVisibilityEvent.class);
    }

    public void addPanel(GUIPanel guiPanel) {
        TreeNodeManager treeNodeManager = guiPanel.getTreeNodeManager();
        treeNodeManager.setTree(this);
        DefaultMutableTreeNode node = treeNodeManager.getRoot();

        installRenderer(treeNodeManager);
        installListener(treeNodeManager);
        installPopUpActions(treeNodeManager);

        nodesMap.put(guiPanel, node);

        int childIndex = guiPanel.getIndex();
        if (childIndex >= 0 && childIndex <= root.getChildCount()) {
            root.insert(node, childIndex);
        } else {
            root.add(node);
        }

        getModel().reload();
        expandNode();
    }

    public void removePanel(GUIPanel guiPanel) {
        if (nodesMap.containsKey(guiPanel)) {
            DefaultMutableTreeNode node = nodesMap.remove(guiPanel);

            TreeNodeManager treeNodeManager = guiPanel.getTreeNodeManager();
            removeRenderer(treeNodeManager);
            removeListener(treeNodeManager);
            removePopUpActions(treeNodeManager);

            root.remove(node);
            getModel().reload();
            expandNode();
        }
    }

    private void installRenderer(TreeNodeManager treeNodeManager) {
        DefaultTreeCellRenderer renderer = treeNodeManager.getRenderer();
        Class<?> rendererClass = treeNodeManager.getRendererClass();
        if (renderer != null) {
            renderersMap.put(rendererClass, renderer);
        }
    }

    private void removeRenderer(TreeNodeManager treeNodeManager) {
        Class<?> rendererClass = treeNodeManager.getRendererClass();
        if (renderersMap.containsKey(rendererClass)) {
            renderersMap.remove(rendererClass);
        }
    }

    private void installListener(TreeNodeManager treeNodeManager) {
        SelectionHandler handler = treeNodeManager.getSelectionHandler();
        DefaultMutableTreeNode root = treeNodeManager.getRoot();
        if (handler != null) {
            selectionHandlersMap.put(root, handler);
        }
    }

    private void removeListener(TreeNodeManager treeNodeManager) {
        DefaultMutableTreeNode root = treeNodeManager.getRoot();
        if (selectionHandlersMap.containsKey(root)) {
            selectionHandlersMap.remove(root);
        }
    }

    private void installPopUpActions(TreeNodeManager treeNodeManager) {
        PopUpBuilder builder = treeNodeManager.getPopUpBuilder();
        Class<?> rendererClass = treeNodeManager.getRendererClass();
        if (builder != null) {
            popupBuildersMap.put(rendererClass, builder);
        }
    }

    private void removePopUpActions(TreeNodeManager treeNodeManager) {
        Class<?> rendererClass = treeNodeManager.getRendererClass();
        if (popupBuildersMap.containsKey(rendererClass)) {
            popupBuildersMap.remove(rendererClass);
        }
    }

    public void selectPanel(GUIPanel panel) {
        if (nodesMap.containsKey(panel)) {
            TreeNode node = nodesMap.get(panel);
            tree.setSelectionPath(new TreePath(getModel().getPathToRoot(node)));
        }
    }

    private final class ActorSelectionListener implements GenericEventListener {
        @Override
        public void eventTriggered(Object obj, Event event) {
            ActorSelectionEvent selectionEvent = ActorSelectionEvent.class.cast(event);
            final Actor selection = selectionEvent.getActor();
            final boolean keep = selectionEvent.isKeep();
            final Picker picker = selectionEvent.getPicker();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    processSelectionEvent(picker, selection, keep);
                }
            });
        }

        private void processSelectionEvent(Picker picker, Actor actor, boolean keep) {
            if (actor == null) {
                TreePath[] selectionPath = tree.getSelectionPaths();
                if (selectionPath != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath[0].getLastPathComponent();
                    DefaultMutableTreeNode parent = TreeUtil.getFirstLevelParent(node);
                    tree.setSelectionPath(new TreePath(getModel().getPathToRoot(parent)));
                }
            } else {
                for (SelectionHandler handler : selectionHandlersMap.values()) {
                    if (handler.isEnabled()) {
                        handler.process3DSelectionEvent(picker, actor, keep);
                    }
                }
            }
        }

    }

    private final class ActorPopUpListener implements GenericEventListener {
        @Override
        public void eventTriggered(Object obj, Event event) {
            ActorPopUpEvent popUpEvent = ActorPopUpEvent.class.cast(event);
            Actor selection = popUpEvent.getActor();
            Picker picker = popUpEvent.getPicker();
            if (selection != null) {
                popUp.mouseReleased(popUpEvent.getMouseEvent());
            }
        }
    }

    private final class ActorVisibilityListener implements GenericEventListener {
        @Override
        public void eventTriggered(Object obj, Event event) {
            ActorVisibilityEvent selectionEvent = ActorVisibilityEvent.class.cast(event);
            final boolean select = selectionEvent.isSelect();

            ExecUtil.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    processTreeCheckBoxSelectionEvent(select);
                }
            });
        }

        private void processTreeCheckBoxSelectionEvent(boolean selected) {
            for (SelectionHandler handler : selectionHandlersMap.values()) {
                if (handler.isEnabled()) {
                    handler.process3DVisibilityEvent(selected);
                }
            }
            tree.repaint();
        }

    }

    private final class TreeToPanels implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            TreePath[] selectionPath = tree.getSelectionPaths();
            if (selectionPath != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath[0].getLastPathComponent();
                List<Object> selection = TreeUtil.toUserObjects(selectionPath);

                if (node.getLevel() != 1) {
                    DefaultMutableTreeNode firstLevelParent = TreeUtil.getFirstLevelParent(node);
                    selectPanelByNode(firstLevelParent);

                    SelectionHandler selectionHandler = selectionHandlersMap.get(firstLevelParent);
                    if (selectionHandler.isEnabled()) {
                        selectionHandler.handleSelection(fire3DEvent, selection.toArray());
                    }
                } else {
                    selectAndClerPanelByNode(node);
                }
            } else {
                for (SelectionHandler handler : selectionHandlersMap.values()) {
                    if (handler.isEnabled()) {
                        handler.handleSelection(fire3DEvent, new Object[0]);
                    }
                }
            }
        }

        private void selectAndClerPanelByNode(DefaultMutableTreeNode node) {
            if (node.getLevel() == 1) {
                selectAndClearPanelByNode(node);
            } else {
                selectAndClearPanelByFirstLevelParent(node);
            }
        }

        private void selectAndClearPanelByNode(DefaultMutableTreeNode node) {
            panelsHandler.selectAndClearPanel(getNodeLabel(node));
        }

        private void selectAndClearPanelByFirstLevelParent(DefaultMutableTreeNode node) {
            DefaultMutableTreeNode firstLevelParent = TreeUtil.getFirstLevelParent(node);
            selectAndClearPanelByNode(firstLevelParent);
        }

        private void selectPanelByNode(DefaultMutableTreeNode node) {
            panelsHandler.selectPanel(getNodeLabel(node));
        }

        private String getNodeLabel(DefaultMutableTreeNode node) {
            Object userObject = node.getUserObject();
            if (userObject instanceof String) {
                return (String) userObject;
            } else if (userObject instanceof ModulePanel) {
                return ((ModulePanel) userObject).getKey();
            } else if (userObject instanceof RootVisibleItem) {
                return ((RootVisibleItem) userObject).getName();
            } else {
                return "";
            }
        }

    }

    private final class CheckBoxTreeToPanels implements CheckBoxSelectionListener {

        @Override
        public void selectionAdded(DefaultMutableTreeNode node) {
            // System.out.println("Tree.CheckBoxTreeToPanels.selectionAdded()");
            Object userObject = node.getUserObject();
            if (userObject instanceof RootVisibleItem) {
                if (node.getChildCount() > 0) {
                    for (int i = 0; i < node.getChildCount(); i++) {
                        DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                        selectionAdded(child);
                    }
                }
            } else if (userObject instanceof VisibleItem) {
                selectNode(node, true);
            }
        }

        private void selectNode(DefaultMutableTreeNode node, boolean selected) {
            Object userObject = node.getUserObject();
            VisibleItem item = (VisibleItem) userObject;
            item.setVisible(selected);

            if (node.getLevel() != 1) {
                DefaultMutableTreeNode firstLevelParent = TreeUtil.getFirstLevelParent(node);
                SelectionHandler handler = selectionHandlersMap.get(firstLevelParent);
                if (handler.isEnabled()) {
                    handler.handleVisibility(item);
                    EventManager.triggerEvent(this, new VisibleItemEvent(this, (VisibleItem) userObject));
                }
            }
        }

        @Override
        public void selectionRemoved(DefaultMutableTreeNode node) {
            // System.out.println("Tree.CheckBoxTreeToPanels.selectionRemoved() "+node);
            Object userObject = node.getUserObject();
            if (userObject instanceof RootVisibleItem) {
                if (node.getChildCount() > 0) {
                    for (int i = 0; i < node.getChildCount(); i++) {
                        DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                        selectionRemoved(child);
                    }
                }
            } else if (userObject instanceof VisibleItem) {
                selectNode(node, false);
            }
        }
    }

    public class TreeRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

            Object userObject = node.getUserObject();
            if (userObject != null) {
                Class<?> klass = containsClass(userObject.getClass());
                if (klass != Object.class) {
                    return renderersMap.get(klass).getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                }
            }
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (userObject instanceof ModulePanel) {
                setText(((ModulePanel) userObject).getTitle());
            }
            if (node.getLevel() == 1) {
                setFont(tree.getFont().deriveFont(Font.BOLD));
            } else {
                setFont(tree.getFont().deriveFont(Font.PLAIN));
            }
            setIcon(null);
            return this;
        }
    }

    private Class<?> containsClass(Class<?> klass) {
        if (klass == null) {
            return Object.class;
        }
        if (renderersMap.containsKey(klass)) {
            return klass;
        }

        if (klass.getSuperclass() != null && klass.getSuperclass() != Object.class) {
            return containsClass(klass.getSuperclass());
        }

        if (klass.getInterfaces().length != 0) {
            for (Class<?> c : klass.getInterfaces()) {
                Class<?> k = containsClass(c);
                if (k != Object.class) {
                    return k;
                }
            }
        }
        return Object.class;
    }

    public void clearAllSelections() {
        tree.clearSelection();
        checkManager.clearSelection();
    }

    public void clearSelection() {
        tree.clearSelection();
    }

    public void clearCheckSelection() {
        checkManager.clearSelection();
    }

    public boolean isAlreadySelected(DefaultMutableTreeNode selectedNode) {
        TreePath treePath = new TreePath(getPathToRoot(selectedNode));
        return tree.getSelectionModel().isPathSelected(treePath);
    }

    public void addSelectedNode(DefaultMutableTreeNode selectedNode) {
        this.fire3DEvent = false;
        TreePath treePath = new TreePath(getPathToRoot(selectedNode));
        tree.getSelectionModel().addSelectionPath(treePath);
        tree.repaint();
        this.fire3DEvent = true;
    }

    public void removeSelectedNode(DefaultMutableTreeNode selectedNode) {
        this.fire3DEvent = false;
        TreePath treePath = new TreePath(getPathToRoot(selectedNode));
        tree.getSelectionModel().removeSelectionPath(treePath);
        tree.repaint();
        this.fire3DEvent = true;
    }

    public void setSelectedNode(DefaultMutableTreeNode selectedNode) {
        this.fire3DEvent = false;
        TreePath treePath = new TreePath(getPathToRoot(selectedNode));
        tree.getSelectionModel().setSelectionPath(treePath);
        tree.scrollPathToVisible(treePath);
        tree.repaint();
        this.fire3DEvent = true;
    }

    public Object[] getPathToRoot(DefaultMutableTreeNode selectedNode) {
        return ((DefaultTreeModel) tree.getModel()).getPathToRoot(selectedNode);
    }

    public TreePath[] getSelectedDescendantOf(DefaultMutableTreeNode parentNode) {
        TreePath parentPath = new TreePath(getPathToRoot(parentNode));
        List<TreePath> selection = new ArrayList<>();
        TreePath[] selectionPaths = getSelectionPaths();
        if (selectionPaths != null) {
            for (TreePath path : selectionPaths) {
                if (path != parentPath && parentPath.isDescendant(path)) {
                    selection.add(path);
                }
            }
        }
        return selection.toArray(new TreePath[0]);
    }

    public TreePath[] getSelectionPaths() {
        return tree.getSelectionPaths();
    }

    public void expandNode() {
        UiUtil.expandAll(tree, true);
    }

    public void expandNode(DefaultMutableTreeNode node) {
        TreePath treePath = new TreePath(node.getPath());
        UiUtil.expandAll(tree, treePath, true);
    }

    public void setSelectionPaths(TreePath[] selPaths) {
        tree.setSelectionPaths(selPaths);
    }

    public DefaultTreeModel getModel() {
        return (DefaultTreeModel) tree.getModel();
    }

    private final class PopUpMenuListener extends MouseAdapter {
        private JPopupMenu popUp;

        public PopUpMenuListener() {
            popUp = new JPopupMenu();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {

                // Select row if not selected
                // tree.setSelectionRow(tree.getClosestRowForLocation(e.getX(), e.getY()));

                TreePath[] selectionPath = tree.getSelectionPaths();
                if (selectionPath != null) {
                    List<Object> selection = TreeUtil.toUserObjects(selectionPath);
                    Class<?> klass = containsClass(selection.get(0).getClass());
                    if (klass != Object.class) {
                        if (popupBuildersMap.containsKey(klass)) {
                            popUp.removeAll();

                            PopUpBuilder builder = popupBuildersMap.get(klass);
                            builder.populate(popUp);
                            popUp.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }
        }
    }

    public void selectPanelIfNeeded() {
        if (tree.getSelectionCount() == 0) {
            tree.setSelectionRow(0);
        }
    }
}
