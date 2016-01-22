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

package eu.engys.gui.mesh.panels;

import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Geometry;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.surface.MultiPlane;
import eu.engys.core.project.geometry.surface.PlaneRegion;
import eu.engys.core.project.zero.patches.Patches;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.SelectSurfaceEvent;
import eu.engys.gui.tree.AbstractSelectionHandler;
import eu.engys.gui.tree.DefaultTreeNodeManager;
import eu.engys.gui.tree.SelectionHandler;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Geometry3DController;
import eu.engys.gui.view3D.Picker;
import eu.engys.util.Util;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.TreeUtil;
import eu.engys.util.ui.checkboxtree.RootVisibleItem;
import eu.engys.util.ui.checkboxtree.VisibleItem;

public class BaseMeshTreeNodeManager extends DefaultTreeNodeManager<Surface> {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseMeshTreeNodeManager.class);

    private Map<DefaultMutableTreeNode, Surface> surfaceMap;
    private SelectionHandler selectionHandler;

    public BaseMeshTreeNodeManager(Model model, AbstractBaseMeshPanel panel) {
        super(model, panel);
        this.selectionHandler = new BlockMeshSelectionHandler(panel);
        this.surfaceMap = new HashMap<>();
    }

    @Override
    public void update(Observable o, final Object arg) {
        if (arg instanceof MultiPlane || arg instanceof Geometry) {
            ExecUtil.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    loadTree();
                    selectVisibleItems();
                    expandTree();
                }
            });
        } else if (arg instanceof Patches) {
            selectVisibleItems();
        }
    }

    private void loadTree() {
        clear();

        if (model.getGeometry().hasBlock()) {
            MultiPlane block = model.getGeometry().getBlock();
            DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(new RootVisibleItem(block.getName()));
            root.add(parentNode);

            for (Surface region : block.getRegions()) {
                addSurface(parentNode, region);
            }
            treeChanged(root);
        }
    }

    private void selectVisibleItems() {
        if (model.getPatches().isEmpty())
            getTree().getCheckManager().selectNode(getRoot());
        else
            getTree().getCheckManager().deselectNode(getRoot());
    }

    private void expandTree() {
        if (getTree() != null) {
            if (model.getGeometry().hasBlock()) {
                getTree().expandNode(getRoot());
            }
        }
    }

    private void addSurface(DefaultMutableTreeNode parent, Surface surface) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(surface);
        parent.add(node);
        nodeMap.put(surface, node);
        surfaceMap.put(node, surface);
    }

    public Surface[] getSelectedValues() {
        if (getTree() != null) {
            TreePath[] selectionPaths = getTree().getSelectionPaths();
            Surface[] surfaces = new Surface[selectionPaths.length];
            for (int i = 0; i < selectionPaths.length; i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPaths[i].getLastPathComponent();
                Surface surface = surfaceMap.get(node);
                surfaces[i] = surface;
            }
            return surfaces;
        }
        return new Surface[0];
    }

    public void clear() {
        // clear node before selection handler!
        clearNode(root);
        selectionHandler.clear();
        nodeMap.clear();
        surfaceMap.clear();
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    @Override
    public Class<?> getRendererClass() {
        return PlaneRegion.class;
    }

    public DefaultTreeCellRenderer getRenderer() {
        return new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();

                if (userObject instanceof PlaneRegion) {
                    PlaneRegion surface = (PlaneRegion) userObject;
                    setText(surface.getName());
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
    
    private final class BlockMeshSelectionHandler extends AbstractSelectionHandler {
        private PlaneRegion[] currentSelection;
        private AbstractBaseMeshPanel panel;

        public BlockMeshSelectionHandler(AbstractBaseMeshPanel panel) {
            this.panel = panel;
        }

        @Override
        public void handleSelection(boolean fire3DEvent, Object... selection) {
            saveCurrentSelection();
            
            boolean isValidSelection = TreeUtil.isConsistent(selection, PlaneRegion.class);
            if (isValidSelection) {
                handleValidSelection(fire3DEvent, selection);
            } else {
                boolean shouldClearSelection = TreeUtil.isConsistent(currentSelection, PlaneRegion.class);
                if(shouldClearSelection){
                    clearSelection(fire3DEvent);
                }
            }
        }
        
        private void saveCurrentSelection() {
            if (Util.isVarArgsNotNull(currentSelection)) {
                panel.saveSurfaces(currentSelection);
            }
        }
        
        private void handleValidSelection(boolean fire3DEvent, Object... selection) {
            logger.debug("handleSelection: {} selected, fire3D {} {}", selection.length, fire3DEvent, selection.length == 1 ? ", selection is: " + selection[0] : "");

            // update current selection
            this.currentSelection = Arrays.copyOf(selection, selection.length, PlaneRegion[].class);
            panel.selectPlane(currentSelection);

            if (fire3DEvent) {
                EventManager.triggerEvent(this, new SelectSurfaceEvent(currentSelection));
            }
        }
        
        private void clearSelection(boolean fire3DEvent) {
            clear();
            panel.clear();
            if (fire3DEvent) {
                EventManager.triggerEvent(this, new SelectSurfaceEvent(new PlaneRegion[0]));
            }
        }

        @Override
        public void handleVisibility(VisibleItem item) {
            if (Util.isVarArgsNotNull(currentSelection) && Arrays.asList(currentSelection).contains(item)) {
                panel.selectPlane(currentSelection);
                EventManager.triggerEvent(this, new SelectSurfaceEvent(currentSelection));
            }

        }

        @Override
        public void process3DSelectionEvent(Picker picker, Actor actor, boolean keep) {
            if (getTree() != null && picker instanceof Geometry3DController && actor.getVisibleItem() instanceof Surface) {
                Surface surface = (Surface) actor.getVisibleItem();
                DefaultMutableTreeNode selectedNode = nodeMap.get(surface);
                if (selectedNode != null) {
                    getTree().setSelectedNode(selectedNode);
                }
            }
        }

        @Override
        public void process3DVisibilityEvent(boolean selected) {
            if (getTree() != null) {
                if (selected) {
                    // getTree().getCheckManager().selectNode(getRoot());
                } else {
                    getTree().getCheckManager().deselectNode(getRoot());
                }
            }
        }

        public void clear() {
            currentSelection = null;
        }
    }

}
