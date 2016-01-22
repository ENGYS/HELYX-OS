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

/*--------------------------------*- Java -*---------------------------------*\
 |o                                                                   |                                                                                     
 |    o     o       | HelyxOS: The Open Source GUI for OpenFOAM              |
 |   o   O   o      | Copyright (C) 2012-2013 ENGYS                          |
 |    o     o       | http://www.engys.com                                   |
 |       o          |                                                        |
 |---------------------------------------------------------------------------|
 |License                                                                    |
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
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.FeatureLine;
import eu.engys.core.project.geometry.Geometry;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;
import eu.engys.core.project.zero.patches.Patches;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.RemoveSurfaceEvent;
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

public class FeatureLinesTreeNodeManager extends DefaultTreeNodeManager<Surface> {

    private static final Logger logger = LoggerFactory.getLogger(FeatureLinesTreeNodeManager.class);

    private final Action removeAction = new RemoveAction();

    private Map<DefaultMutableTreeNode, FeatureLine> linesMap;
    private FeatureLinesSelectionHandler selectionHandler;

    public FeatureLinesTreeNodeManager(Model model, FeatureLinesPanel panel) {
        super(model, panel);
        this.root = new DefaultMutableTreeNode(new RootVisibleItem(panel.getTitle()));
        this.selectionHandler = new FeatureLinesSelectionHandler(panel);
        this.linesMap = new HashMap<>();
    }

    @Override
    public void update(Observable o, final Object arg) {
        if (arg instanceof FeatureLine) {
            logger.debug("Observerd a change: arg is " + arg.getClass());
            ExecUtil.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    loadTree();
                    expandTree(arg);
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

    private void loadTree() {
        logger.debug("Load 'Geometry' tree");
        clear();
        for (FeatureLine line : model.getGeometry().getLines()) {
            addLine(root, line);
        }
        treeChanged(root);
    }

    private void addLine(DefaultMutableTreeNode parent, FeatureLine line) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(line);
        parent.add(node);
        nodeMap.put(line, node);
        linesMap.put(node, line);
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
                if (arg instanceof Surface) {
                    Surface surface = (Surface) arg;
                    setSelectedValue(surface);
                }
            }
        }
    }

    private boolean shouldExpand(final Object arg) {
        return arg instanceof FeatureLine || arg instanceof Geometry;
    }

    private void setSelectedValue(Surface surface) {
        DefaultMutableTreeNode selectedNode = nodeMap.get(surface);
        if (getTree() != null) {
            if (surface.getPatchName() != null) {
                getTree().setSelectedNode(selectedNode);
            }
        }
    }

    public FeatureLine[] getSelectedValues() {
        if (getTree() != null) {
            TreePath[] selectionPaths = getTree().getSelectionPaths();
            if (selectionPaths != null) {
                FeatureLine[] surfaces = new FeatureLine[selectionPaths.length];
                for (int i = 0; i < selectionPaths.length; i++) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPaths[i].getLastPathComponent();
                    FeatureLine line = linesMap.get(node);
                    surfaces[i] = line;
                }
                return surfaces;
            }
        }
        return new FeatureLine[0];
    }

    public void clear() {
        // clear node before selection handler!
        clearNode(root);
        selectionHandler.clear();
        nodeMap.clear();
        linesMap.clear();
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    @Override
    public Class<?> getRendererClass() {
        return FeatureLine.class;
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
        return new PopUpBuilder() {
            @Override
            public void populate(JPopupMenu popUp) {
                popUp.add(removeAction);
            }
        };
    }

    private void updateActions() {
        Surface[] surfaces = selectionHandler.currentSelection;
        if (Util.isVarArgsNotNull(surfaces)) {
            Type type = surfaces[0].getType();
            removeAction.setEnabled(type != Type.SOLID);
        }
    }

    private final class RemoveAction extends AbstractAction {
        public RemoveAction() {
            super("Remove");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            FeatureLine[] surfaces = getSelectedValues();

            model.getGeometry().removeLines(surfaces);
            model.geometryChanged();

            EventManager.triggerEvent(this, new RemoveSurfaceEvent(surfaces));
        }
    }

    public final class FeatureLinesSelectionHandler extends AbstractSelectionHandler {

        private final FeatureLinesPanel panel;
        private FeatureLine[] currentSelection;

        public FeatureLinesSelectionHandler(FeatureLinesPanel panel) {
            this.panel = panel;
        }

        @Override
        public void handleSelection(boolean fire3DEvent, Object... selection) {
            saveCurrentSelection();

            boolean isValidSelection = TreeUtil.isConsistent(selection, FeatureLine.class);
            if (isValidSelection) {
                handleValidSelection(fire3DEvent, selection);
            } else {
                boolean shouldClearSelection = TreeUtil.isConsistent(currentSelection, FeatureLine.class);
                if (shouldClearSelection) {
                    clearSelection(fire3DEvent);
                }
            }

        }

        private void saveCurrentSelection() {
            if (Util.isVarArgsNotNull(currentSelection)) {
                panel.saveLine(currentSelection);
            }
        }

        private void handleValidSelection(boolean fire3DEvent, Object... selection) {
            logger.debug("handleSelection: {} selected, fire3D {} {}", selection.length, fire3DEvent, selection.length == 1 ? ", selection is: " + selection[0] : "");

            this.currentSelection = Arrays.copyOf(selection, selection.length, FeatureLine[].class);
            panel.selectLine(currentSelection);

            if (fire3DEvent) {
                EventManager.triggerEvent(this, new SelectSurfaceEvent(currentSelection));
            }

            updateActions();
        }

        private void clearSelection(boolean fire3DEvent) {
            clear();
            panel.deselectAll();
            if (fire3DEvent) {
                EventManager.triggerEvent(this, new SelectSurfaceEvent(new FeatureLine[0]));
            }
        }

        @Override
        public void handleVisibility(VisibleItem item) {
            logger.debug("handleVisibility: {}", item);
            if (Util.isVarArgsNotNull(currentSelection) && Arrays.asList(currentSelection).contains(item)) {
                panel.selectLine(currentSelection);
                EventManager.triggerEvent(this, new SelectSurfaceEvent(currentSelection));
            }
        }

        @Override
        public void process3DSelectionEvent(Picker picker, Actor actor, boolean keep) {
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

        public void clear() {
            currentSelection = null;
        }
    }

}
