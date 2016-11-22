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
package eu.engys.gui.casesetup.facezones;

import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import eu.engys.core.project.Model;
import eu.engys.core.project.zero.facezones.FaceZone;
import eu.engys.core.project.zero.facezones.FaceZones;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.SelectFaceZonesEvent;
import eu.engys.gui.tree.AbstractSelectionHandler;
import eu.engys.gui.tree.DefaultTreeNodeManager;
import eu.engys.gui.tree.SelectionHandler;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Picker;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.TreeUtil;
import eu.engys.util.ui.checkboxtree.VisibleItem;

public class FaceZonesTreeNodeManager extends DefaultTreeNodeManager<FaceZone> {

    private Map<DefaultMutableTreeNode, FaceZone> zonesMap;
    private SelectionHandler selectionHandler;

    public FaceZonesTreeNodeManager(Model model, FaceZonesPanel cellZonesPanel) {
        super(model, cellZonesPanel);
        this.selectionHandler = new FaceZonesSelectionHandler(cellZonesPanel);
        this.zonesMap = new HashMap<>();
    }

    @Override
    public void update(Observable o, final Object arg) {
        if (arg instanceof FaceZones) {
            ExecUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    selectionHandler.disable();
                    loadTree();
                    selectVisibleItems();
                    expandTree();
                    selectionHandler.enable();
                }
            });
        }
    }

    private void loadTree() {
        clear();
        for (FaceZone zone : model.getFaceZones()) {
            addFaceZone(root, zone);
        }
        treeChanged(root);
    }

    private void selectVisibleItems() {
        // for(DefaultMutableTreeNode node : nodeMap.values())
        // getTree().getCheckManager().selectNode(node);
    }

    private void expandTree() {
        getTree().expandNode(getRoot());
    }

    private void addFaceZone(DefaultMutableTreeNode parent, FaceZone cellZone) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(cellZone);
        parent.add(node);
        nodeMap.put(cellZone, node);
        zonesMap.put(node, cellZone);
    }

    public FaceZone[] getSelectedValues() {
        if (getTree() != null) {
            TreePath[] selectionPaths = getTree().getSelectedDescendantOf(getRoot());
            FaceZone[] cellzones = new FaceZone[selectionPaths.length];
            for (int i = 0; i < selectionPaths.length; i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPaths[i].getLastPathComponent();
                FaceZone zone = zonesMap.get(node);
                cellzones[i] = zone;
            }
            return cellzones;
        }
        return new FaceZone[0];
    }

    public void clear() {
        // clear node before selection handler!
        clearNode(root);
        selectionHandler.clear();
        nodeMap.clear();
        zonesMap.clear();
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    @Override
    public Class<?> getRendererClass() {
        return FaceZone.class;
    }

    public DefaultTreeCellRenderer getRenderer() {
        return new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();

                if (userObject instanceof FaceZone) {
                    FaceZone patch = (FaceZone) userObject;
                    setText(patch.getName());
                }
                setIcon(null);
                return this;
            }
        };
    }

    @Override
    public SelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    private final class FaceZonesSelectionHandler extends AbstractSelectionHandler {

        private FaceZonesPanel panel;
        private FaceZone[] currentSelection;

        public FaceZonesSelectionHandler(FaceZonesPanel panel) {
            this.panel = panel;
        }

        @Override
        public void handleSelection(boolean fire3DEvent, Object... selection) {
            if (TreeUtil.isConsistent(selection, FaceZone.class)) {
                this.currentSelection = Arrays.copyOf(selection, selection.length, FaceZone[].class);
            } else {
                this.currentSelection = new FaceZone[0];
            }
            panel.updateSelection(currentSelection);
            if (fire3DEvent) {
                EventManager.triggerEvent(this, new SelectFaceZonesEvent(currentSelection));
            }
        }

        @Override
        public void handleVisibility(VisibleItem item) {
        }

        @Override
        public void process3DSelectionEvent(Picker picker, Actor actor, boolean keep) {
            if (getTree() != null && actor.getVisibleItem() instanceof VisibleItem) {
                DefaultMutableTreeNode selectedNode = nodeMap.get(actor.getVisibleItem());
                if (selectedNode != null) {
                    getTree().setSelectedNode(selectedNode);
                }
            }
        }

        @Override
        public void process3DVisibilityEvent(boolean selected) {
            if (getTree() != null) {
                for (DefaultMutableTreeNode node : nodeMap.values()) {
                    // if (selected) {
                    // getTree().getCheckManager().selectNode(node);
                    // } else {
                    getTree().getCheckManager().deselectNode(node);
                    // }
                }
            }
        }

        @Override
        public void clear() {
            currentSelection = null;
        }
    }

}
