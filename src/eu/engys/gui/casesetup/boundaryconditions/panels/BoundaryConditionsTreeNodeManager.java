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
package eu.engys.gui.casesetup.boundaryconditions.panels;

import static eu.engys.core.dictionary.Dictionary.TYPE;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.patches.BoundaryType;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.core.project.zero.patches.Patches;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.SelectPatchesEvent;
import eu.engys.gui.tree.AbstractSelectionHandler;
import eu.engys.gui.tree.DefaultTreeNodeManager;
import eu.engys.gui.tree.SelectionHandler;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Picker;
import eu.engys.util.Util;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.TreeUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.checkboxtree.RootVisibleLoadableTreeNode;
import eu.engys.util.ui.checkboxtree.VisibleItem;

public class BoundaryConditionsTreeNodeManager extends DefaultTreeNodeManager<Patch> {

    private static final Logger logger = LoggerFactory.getLogger(BoundaryConditionsTreeNodeManager.class);
    public static final String COPY = "Copy";
    public static final String PASTE = "Paste";

    private Map<DefaultMutableTreeNode, Patch> patchesMap;
    private SelectionHandler selectionHandler;

    private CopyAction copyAction;
    private PasteAction pasteAction;

    public BoundaryConditionsTreeNodeManager(Model model, BoundaryConditionsPanel panel) {
        super(model, panel);
        this.root = new RootVisibleLoadableTreeNode(panel.getTitle());
        this.selectionHandler = new BoundaryConditionsSelectionHandler(panel);
        this.copyAction = new CopyAction(panel);
        this.pasteAction = new PasteAction();
        this.patchesMap = new HashMap<>();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Patches) {
            logger.debug("[CHANGE OBSERVERD] arg: " + arg.getClass().getSimpleName());
            ExecUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    selectionHandler.disable();
                    loadTree();
                    makeVisibleItemsChecked();
                    expandTree();
                    selectionHandler.enable();
                }
            });
        }
    }

    private void loadTree() {
        logger.debug("Load 'Patches' tree");
        clear();
        for (Patch patch : model.getPatches().patchesToDisplay()) {
            addPatch(root, patch);
        }
        treeChanged(root);
    }

    private void makeVisibleItemsChecked() {
        logger.debug("Make visible items checked: DO NOTHING!");
    }

    private void expandTree() {
        getTree().expandNode(getRoot());
    }

    private void addPatch(DefaultMutableTreeNode parent, Patch patch) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(patch);
        parent.add(node);
        nodeMap.put(patch, node);
        patchesMap.put(node, patch);
    }

    public Patch[] getSelectedValues() {
        if (getTree() != null) {
            TreePath[] selectionPaths = getTree().getSelectedDescendantOf(getRoot());
            Patch[] patches = new Patch[selectionPaths.length];
            for (int i = 0; i < selectionPaths.length; i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPaths[i].getLastPathComponent();
                Patch patch = patchesMap.get(node);
                patches[i] = patch;
            }
            return patches;
        }
        return new Patch[0];
    }

    public void setSelectedValues(Patch[] patches) {
        if (getTree() != null) {
            TreePath[] selectionPaths = new TreePath[patches.length];
            for (int i = 0; i < patches.length; i++) {
                DefaultMutableTreeNode node = nodeMap.get(patches[i]);
                selectionPaths[i] = new TreePath(getTree().getPathToRoot(node));
            }
            getTree().setSelectionPaths(selectionPaths);
        }
    }

    public void clear() {
        // clear node before selection handler!
        clearNode(root);
        selectionHandler.clear();
        nodeMap.clear();
        patchesMap.clear();
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    @Override
    public Class<?> getRendererClass() {
        return Patch.class;
    }

    @SuppressWarnings("serial")
    public DefaultTreeCellRenderer getRenderer() {
        return new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();

                if (userObject instanceof Patch) {
                    Patch renderedPatch = (Patch) userObject;
                    setText(renderedPatch.getName());
                    setIcon(renderedPatch.getPhysicalType().getIcon());
                } else {
                    setIcon(null);
                }
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
                popUp.add(copyAction).setName(COPY);
                popUp.add(pasteAction).setName(PASTE);
            }
        };
    }

    private final class CopyAction extends AbstractAction {

        private BoundaryConditionsPanel panel;

        public CopyAction(BoundaryConditionsPanel panel) {
            super(COPY);
            this.panel = panel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Patch[] selectedValues = getSelectedValues();
            if (Util.isVarArgsNotNullAndOfSize(1, selectedValues)) {
                Patch patch = selectedValues[0];
                panel.savePatch(patch);
                Dictionary bc = patch.getBoundaryConditions().toDictionary();
                bc.add(TYPE, patch.getPhysicalType().getKey());
                StringSelection contents = new StringSelection(bc.toString());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, contents);
            } else {
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Only Single Selection Allowed", "Copy Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private final class PasteAction extends AbstractAction {

        public PasteAction() {
            super(PASTE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Patch[] selectedValues = getSelectedValues();
            getTree().clearSelection();
            if (Util.isVarArgsNotNull(selectedValues)) {
                Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
                try {
                    String dictionaryString = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    if (dictionaryString.startsWith("\nboundaryConditions")) {
                        Dictionary dictionary = DictionaryUtils.readDictionary(dictionaryString).getDictionaries().get(0);
                        BoundaryType type = BoundaryType.getType(dictionary.lookup("type"));
                        for (Patch patch : selectedValues) {
                            patch.setPhysicalType(type);
                            patch.getBoundaryConditions().fromDictionary(dictionary);
                        }
                        setSelectedValues(selectedValues);
                    } else {
                        JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Invalid Format", "Paste Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ee) {
                    JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "An Error Occurred", "Paste Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Empty Selection", "Paste Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private final class BoundaryConditionsSelectionHandler extends AbstractSelectionHandler {

        private BoundaryConditionsPanel panel;
        private Patch[] currentSelection;

        public BoundaryConditionsSelectionHandler(BoundaryConditionsPanel panel) {
            this.panel = panel;
        }

        @Override
        public void handleSelection(boolean fire3DEvent, Object... selection) {
            if (currentSelection != null && currentSelection.length > 0) {
                panel.savePatches(currentSelection);
            }
            if (TreeUtil.isConsistent(selection, Patch.class)) {
                this.currentSelection = Arrays.copyOf(selection, selection.length, Patch[].class);
            } else {
                this.currentSelection = new Patch[0];
            }
            panel.updateSelection(currentSelection);
            getTree().repaint();

            if (fire3DEvent) {
                EventManager.triggerEvent(this, new SelectPatchesEvent(currentSelection));
            }
        }

        @Override
        public void handleVisibility(VisibleItem item) {
        }

        @Override
        public void process3DSelectionEvent(Picker picker, Actor actor, boolean keep) {
            if (getTree() != null && actor != null && actor.getVisibleItem() instanceof Patch) {
                Patch patch = (Patch) actor.getVisibleItem();
                DefaultMutableTreeNode selectedNode = nodeMap.get(patch);
                if (selectedNode != null) {
                    if (keep) {
                        getTree().addSelectedNode(selectedNode);
                    } else {
                        getTree().setSelectedNode(selectedNode);
                    }
                }
            }
        }

        @Override
        public void process3DVisibilityEvent(boolean selected) {
            if (getTree() != null) {
                for (DefaultMutableTreeNode node : nodeMap.values()) {
                    if (selected) {
                        getTree().getCheckManager().selectNode(node);
                    } else {
                        getTree().getCheckManager().deselectNode(node);
                    }
                }
            }
        }

        public void clear() {
            currentSelection = null;
        }
    }
}
