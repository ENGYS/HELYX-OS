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

package eu.engys.gui.tree;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.MutableTreeNode;

import eu.engys.core.project.Model;
import eu.engys.gui.GUIPanel;

public class DefaultTreeNodeManager<K extends Object> implements TreeNodeManager {

    protected DefaultMutableTreeNode root;
    protected Model model;
    private Tree tree;
    protected Map<K, DefaultMutableTreeNode> nodeMap;

    public DefaultTreeNodeManager(Model model, GUIPanel guiPanel) {
        this.model = model;
        this.nodeMap = new HashMap<>();
        this.root = new DefaultMutableTreeNode(guiPanel);
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    @Override
    public void setTree(Tree tree) {
        this.tree = tree;
    }

    @Override
    public Tree getTree() {
        return tree;
    }

    @Override
    public DefaultTreeCellRenderer getRenderer() {
        return null;
    }
    
    @Override
    public SelectionHandler getSelectionHandler() {
        return null;
    }

    @Override
    public PopUpBuilder getPopUpBuilder() {
    	return null;
    }

    @Override
    public Class<?> getRendererClass() {
        return null;
    }

    protected void treeChanged(DefaultMutableTreeNode node) {
    	if (tree != null) {
    		int[] childIndices = new int[node.getChildCount()];
    		for (int i = 0; i < childIndices.length; i++) {
    			childIndices[i] = node.getIndex(node.getChildAt(i));
    		}
    		getTree().getModel().reload(node);
    	}
    }
    
    public void refreshNode(K key) {
        if(key != null && nodeMap.containsKey(key)){
            getTree().getModel().nodeChanged(nodeMap.get(key));
        }
    }

    protected void clearNode(DefaultMutableTreeNode node) {
    	if (tree != null) {
    		for (int i = node.getChildCount() - 1; i >= 0; i--) {
    			getTree().getModel().removeNodeFromParent((MutableTreeNode) node.getChildAt(i));
    		}
    		getTree().getModel().reload(node);
    	}
    }
}
