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
package eu.engys.gui.casesetup.materials;

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
import eu.engys.core.project.materials.Material;
import eu.engys.core.project.materials.Materials;
import eu.engys.gui.casesetup.materials.panels.MaterialsPanel;
import eu.engys.gui.tree.AbstractSelectionHandler;
import eu.engys.gui.tree.DefaultTreeNodeManager;
import eu.engys.gui.tree.SelectionHandler;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Picker;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.TreeUtil;
import eu.engys.util.ui.checkboxtree.VisibleItem;

public class MaterialsTreeNodeManager extends DefaultTreeNodeManager<Material> {

    private Map<DefaultMutableTreeNode, Material> materialsMap;
    private SelectionHandler selectionHandler;

    public MaterialsTreeNodeManager(Model model, MaterialsPanel materialsPanel) {
        super(model, materialsPanel);
        this.selectionHandler = new MaterialsSelectionHandler(materialsPanel);
        this.materialsMap = new HashMap<>();
    }

    @Override
    public void update(Observable o, final Object arg) {
        if (arg instanceof Materials) {
            ExecUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    loadTree();
                    expandTree();
                }
            });
        }
    }

    private void loadTree() {
        clear();
        for (Material material : model.getMaterials()) {
            addMaterial(material);
        }
        treeChanged(root);
    }

    private void expandTree() {
        if (getTree() != null) {
            getTree().expandNode(getRoot());
        }
    }

    private void addMaterial(Material material) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(material);
        root.add(node);
        nodeMap.put(material, node);
        materialsMap.put(node, material);
    }

    public Material[] getSelectedValues() {
        if (getTree() != null) {
            TreePath[] selectionPaths = getTree().getSelectionPaths();
            if (selectionPaths != null) {
                Material[] materials = new Material[selectionPaths.length];
                for (int i = 0; i < selectionPaths.length; i++) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPaths[i].getLastPathComponent();
                    Material material = materialsMap.get(node);
                    materials[i] = material;
                }
                return materials;
            }
            return new Material[0];
        }
        return new Material[0];
    }

    public void setSelectedValue(Material material) {
        if (getTree() != null) {
            DefaultMutableTreeNode selectedNode = nodeMap.get(material);
            if (selectedNode != null) {
                getTree().setSelectedNode(selectedNode);
            } else {
                getTree().setSelectedNode(getRoot());
            }
        }
    }

    public void clear() {
        // clear node before selection handler!
        clearNode(root);
        selectionHandler.clear();
        nodeMap.clear();
        materialsMap.clear();
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    @Override
    public Class<?> getRendererClass() {
        return Material.class;
    }

    @Override
    public DefaultTreeCellRenderer getRenderer() {
        return new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();

                if (userObject instanceof Material) {
                    Material material = (Material) userObject;
                    if (model.getMaterials().size() > 1) {
                        String phase = "[phase" + (model.getMaterials().indexOf(material) + 1) + "]";
                        setText("<html>" + material.getName() + " " + "<font color='#000000'>" + phase + "</font></html>");
                    } else {
                        setText(material.getName());
                    }
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

    private final class MaterialsSelectionHandler extends AbstractSelectionHandler {

        private MaterialsPanel panel;
        private Material[] currentSelection;

        public MaterialsSelectionHandler(MaterialsPanel panel) {
            this.panel = panel;
        }

        @Override
        public void handleSelection(boolean fire3DEvent, Object... selection) {
            if (currentSelection != null && currentSelection.length > 0) {
                panel.saveMaterials(currentSelection);
            }
            if (TreeUtil.isConsistent(selection, Material.class)) {
                this.currentSelection = Arrays.copyOf(selection, selection.length, Material[].class);
            } else {
                this.currentSelection = new Material[0];
            }
            panel.updateSelection(currentSelection);
            panel.updateButtons(currentSelection.length > 0);
        }

        @Override
        public void handleVisibility(VisibleItem item) {
        }

        @Override
        public void process3DSelectionEvent(Picker picker, Actor actor, boolean keep) {
        }

        @Override
        public void process3DVisibilityEvent(boolean selected) {
        }

        @Override
        public void clear() {
            currentSelection = null;
        }
    }

}
