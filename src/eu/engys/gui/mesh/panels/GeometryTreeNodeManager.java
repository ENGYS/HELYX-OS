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

import eu.engys.core.controller.Controller;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.FeatureLine;
import eu.engys.core.project.geometry.Geometry;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.surface.BaseSurface;
import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.core.project.zero.patches.Patches;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.SelectSurfaceEvent;
import eu.engys.gui.tree.AbstractSelectionHandler;
import eu.engys.gui.tree.DefaultTreeNodeManager;
import eu.engys.gui.tree.SelectionHandler;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Picker;
import eu.engys.util.Util;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.TreeUtil;
import eu.engys.util.ui.checkboxtree.RootVisibleItem;
import eu.engys.util.ui.checkboxtree.VisibleItem;

public class GeometryTreeNodeManager extends DefaultTreeNodeManager<Surface> {

    private static final Logger logger = LoggerFactory.getLogger(GeometryTreeNodeManager.class);

    private Map<DefaultMutableTreeNode, Surface> surfaceMap;
    private GeometrySelectionHandler selectionHandler;
    private DefaultGeometryActions geometryActions;

    public GeometryTreeNodeManager(Model model, Controller controller, AbstractGeometryPanel panel, DefaultGeometryActions geometryActions) {
        super(model, panel);
        this.root = new DefaultMutableTreeNode(new RootVisibleItem(panel.getTitle()));
        this.selectionHandler = new GeometrySelectionHandler(panel);
        this.geometryActions = geometryActions;
        this.surfaceMap = new HashMap<>();
    }

    @Override
    public void update(Observable o, final Object arg) {
        if (!(arg instanceof FeatureLine) && (arg instanceof Stl || arg instanceof BaseSurface)) {
            logger.debug("Observerd a change: arg is " + arg.getClass());
            ExecUtil.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    selectionHandler.disable();
                    loadTree();
                    expandTree(arg);
                    selectionHandler.enable();
                    selectArgument(arg);
                }
            });
        } else if (arg instanceof Geometry) {
            logger.debug("Observerd a change: arg is Geometry");
            ExecUtil.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    selectionHandler.disable();
                    loadTree();
                    makeVisibleItemsChecked();
                    expandTree(arg);
                    selectionHandler.enable();
                }
            });
        } else if (arg instanceof Patches) {
            logger.debug("Observerd a change: arg is Patches");
            ExecUtil.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    makeVisibleItemsChecked();
                }
            });
        }
    }

    private void selectArgument(Object arg) {
        if (arg instanceof Surface) {
            Surface surface = (Surface) arg;
            setSelectedValue(surface);
        }
    }

    private void loadTree() {
        logger.debug("Load 'Geometry' tree");
        clear();
        for (Surface surface : model.getGeometry().getSurfaces()) {
            addSurface(root, surface);
            if (surface.getType().isStl()) {
                if (surface.isSingleton()) {
                    // do nothing
                } else {
                    Stl stl = (Stl) surface;
                    DefaultMutableTreeNode parentNode = nodeMap.get(stl);
                    for (Surface region : stl.getRegions()) {
                        addSurface(parentNode, region);
                    }
                }
            }
        }
        treeChanged(root);
    }

    private void addSurface(DefaultMutableTreeNode parent, Surface surface) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(surface);
        parent.add(node);
        nodeMap.put(surface, node);
        surfaceMap.put(node, surface);
    }

    private void makeVisibleItemsChecked() {
        logger.debug("Make visible items checked");
        if (getTree() != null) {
            if (model.getPatches() == null || model.getPatches().isEmpty())
                getTree().getCheckManager().selectNode(getRoot());
            else
                getTree().getCheckManager().deselectNode(getRoot());
        }
    }

    private void expandTree(final Object arg) {
        if (getTree() != null) {
            if (shouldExpand(arg)) {
                logger.debug("Expand the tree");
                getTree().expandNode(getRoot());
            }
        }
    }

    private boolean shouldExpand(final Object arg) {
        return arg instanceof Stl || arg instanceof BaseSurface || arg instanceof Geometry;
    }

    private void setSelectedValue(Surface surface) {
        DefaultMutableTreeNode selectedNode = nodeMap.get(surface);
        if (getTree() != null) {
            if (selectedNode != null) {
                getTree().setSelectedNode(selectedNode);
            } else {
                getTree().setSelectedNode(getRoot());
            }
        }
    }

    public Surface[] getSelectedValues() {
        if (getTree() != null) {
            TreePath[] selectionPaths = getTree().getSelectionPaths();
            if (selectionPaths != null) {
                Surface[] surfaces = new Surface[selectionPaths.length];
                for (int i = 0; i < selectionPaths.length; i++) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPaths[i].getLastPathComponent();
                    Surface surface = surfaceMap.get(node);
                    surfaces[i] = surface;
                }
                return surfaces;
            }
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
        return Surface.class;
    }

    @Override
    public DefaultTreeCellRenderer getRenderer() {
        return new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();

                if (userObject instanceof Surface) {
                    Surface surface = (Surface) userObject;
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

    @Override
    public PopUpBuilder getPopUpBuilder() {
        return geometryActions;
    }

    public final class GeometrySelectionHandler extends AbstractSelectionHandler {

        private final AbstractGeometryPanel panel;
        private Surface[] currentSelection;
        
        public GeometrySelectionHandler(AbstractGeometryPanel panel) {
            this.panel = panel;
        }

        @Override
        public void handleSelection(boolean fire3DEvent, Object... selection) {
            saveCurrentSelection();

            boolean isValidSelection = TreeUtil.isConsistent(selection, Surface.class);
            if (isValidSelection) {
                handleValidSelection(fire3DEvent, selection);
            } else {
                boolean shouldClearSelection = TreeUtil.isConsistent(currentSelection, Surface.class);
                if (shouldClearSelection) {
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

            this.currentSelection = Arrays.copyOf(selection, selection.length, Surface[].class);
            panel.selectSurface(currentSelection);

            if (fire3DEvent) {
                EventManager.triggerEvent(this, new SelectSurfaceEvent(currentSelection));
            }

            geometryActions.updateActions(currentSelection);
        }
        
        private void clearSelection(boolean fire3DEvent) {
            clear();
            panel.deselectAll();
            if (fire3DEvent) {
                EventManager.triggerEvent(this, new SelectSurfaceEvent(new Surface[0]));
            }
        }

        @Override
        public void handleVisibility(VisibleItem item) {
            if (Util.isVarArgsNotNull(currentSelection) && Arrays.asList(currentSelection).contains(item)) {
                logger.debug("handleVisibility: {}", item);
                panel.selectSurface(currentSelection);
                EventManager.triggerEvent(this, new SelectSurfaceEvent(currentSelection));
            }
        }

        @Override
        public void process3DSelectionEvent(Picker picker, Actor actor, boolean keep) {
            if (getTree() != null && actor != null && actor.getVisibleItem() instanceof Surface) {
                Surface surface = (Surface) actor.getVisibleItem();
                if (surface instanceof Solid && ((Solid) surface).getParent().isSingleton()) {
                    surface = ((Solid) surface).getParent();
                }
                DefaultMutableTreeNode selectedNode = nodeMap.get(surface);
                logger.debug("Handle selection from 3D {}", surface);
                if (selectedNode != null) {
                    boolean alreadySelected = getTree().isAlreadySelected(selectedNode);
                    if(alreadySelected){
                        logger.debug("Handle selection from 3D REM");
                        getTree().removeSelectedNode(selectedNode);
                    } else {
                        if (keep) {
                            logger.debug("Handle selection from 3D ADD");
                            getTree().addSelectedNode(selectedNode);
                        } else {
                            logger.debug("Handle selection from 3D SET");
                            getTree().setSelectedNode(selectedNode);
                        }
                    }
                    
                }
            }
        }

        @Override
        public void process3DVisibilityEvent(boolean selected) {
            logger.debug("handle selection from check box");
            if (getTree() != null) {
                if (selected) {
                    // getTree().getCheckManager().selectNode(getRoot());
                } else {
                    getTree().getCheckManager().deselectNode(getRoot());
                }
            }
        }

        @Override
        public void clear() {
            currentSelection = null;
        }
    }

}
