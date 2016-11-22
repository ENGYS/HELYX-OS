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
package eu.engys.gui.custom;

import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import eu.engys.core.project.Model;
import eu.engys.core.project.custom.Custom;
import eu.engys.core.project.custom.CustomFile;
import eu.engys.core.project.custom.CustomUtils;
import eu.engys.gui.tree.AbstractSelectionHandler;
import eu.engys.gui.tree.DefaultTreeNodeManager;
import eu.engys.gui.tree.SelectionHandler;
import eu.engys.util.Symbols;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.TreeUtil;

public class CustomTreeNodeManager extends DefaultTreeNodeManager<CustomFile> {

    private static final Icon FILE_ICON = ResourcesUtil.getIcon("file.icon");
    private static final Icon FOLDER_ICON = ResourcesUtil.getIcon("folder.icon");

    private SelectionHandler selectionHandler;
    private Map<DefaultMutableTreeNode, CustomFile> filesMap = new HashMap<>();
    private CustomNodePanel panel;
    private AbstractAction addFileAction;
    private AbstractAction removeFileAction;
    private AbstractAction editFileAction;

    public CustomTreeNodeManager(Model model, final CustomNodePanel panel) {
        super(model, panel);
        this.selectionHandler = new CustomSelectionHandler(panel);
        this.panel = panel;
    }

    @Override
    public void update(Observable o, final Object arg) {
        if (arg instanceof CustomFile || arg instanceof Custom) {
            ExecUtil.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    loadTree();
                    expandTree(arg);
                }
            });
        }
    }

    private void loadTree() {
        clear();
        load(model.getCustom().getRoot());
        treeChanged(root);
    }

    private void load(CustomFile file) {
        if (file != null) {
            _load(file);
        }
    }

    private void _load(CustomFile file) {
        if (file.getName() != null) {
            CustomFile parent = file.getParent();
            if (parent != null && nodeMap.containsKey(parent)) {
                addNode(nodeMap.get(parent), file);
            } else {
                addNode(root, file);
            }
        }
        for (CustomFile child : file.getChildren()) {
            load(child);
        }
    }

    private void addNode(DefaultMutableTreeNode parent, CustomFile file) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
        parent.add(node);
        filesMap.put(node, file);
        nodeMap.put(file, node);
    }

    private void expandTree(final Object arg) {
        if (getTree() != null) {
            getTree().expandNode(getRoot());
            if (arg instanceof CustomFile) {
                CustomFile file = (CustomFile) arg;
                setSelectedValue(file);
            }
        }
    }

    @Override
    public PopUpBuilder getPopUpBuilder() {
        this.addFileAction = panel.getAddFileAction();
        this.removeFileAction = panel.getRemoveFileAction();
        this.editFileAction = panel.getEditFileAction();
        return new PopUpBuilder() {
            @Override
            public void populate(JPopupMenu popUp) {
                popUp.add(addFileAction).setName((String) addFileAction.getValue(Action.NAME));
                popUp.add(removeFileAction).setName((String) removeFileAction.getValue(Action.NAME));
                popUp.add(editFileAction).setName((String) editFileAction.getValue(Action.NAME));
            }
        };
    }

    public CustomFile[] getSelectedValues() {
        if (getTree() != null) {
            TreePath[] selectionPaths = getTree().getSelectionPaths();
            if (selectionPaths != null) {
                CustomFile[] files = new CustomFile[selectionPaths.length];
                for (int i = 0; i < selectionPaths.length; i++) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPaths[i].getLastPathComponent();
                    CustomFile file = filesMap.get(node);
                    files[i] = file;
                }
                return files;
            }
        }
        return new CustomFile[0];
    }

    public CustomFile getSelectedValue() {
        CustomFile[] values = getSelectedValues();
        return values.length > 0 ? values[0] : null;
    }

    private void setSelectedValue(CustomFile file) {
        DefaultMutableTreeNode selectedNode = nodeMap.get(file);
        if (getTree() != null) {
            if (selectedNode != null) {
                getTree().setSelectedNode(selectedNode);
            }
        }
    }

    public void clear() {
        // clear node before selection handler!
        clearNode(root);
        selectionHandler.clear();
        nodeMap.clear();
        filesMap.clear();
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    @Override
    public Class<?> getRendererClass() {
        return CustomFile.class;
    }

    public DefaultTreeCellRenderer getRenderer() {
        return new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();

                if (userObject instanceof CustomFile) {
                    CustomFile item = (CustomFile) userObject;
                    text(item);
                    icon(item);
                } else {
                    setIcon(null);
                }
                return this;
            }

            private void text(CustomFile item) {
                if (model.getProject() != null && model.getProject().isParallel() && CustomUtils.isVirtualFolder(item)) {
                    String hint = "[processor0 .. " + (model.getProject().getProcessors() - 1) + "]";
                    setText(getLabelWithHint(item.getName(), hint));
                } else if (item.getType().isDictionary() || item.getType().isField() || item.getType().isRaw()) {
                    File file = CustomUtils.getFiles(model, item).get(0);
                    if (file.exists()) {
                        if (item.hasChanged()) {
                            setText(getLabelWithHint(item.getName(), "*"));
                        } else {
                            setText(item.getName());
                        }
                    } else {
                        setText(getLabelWithHint(item.getName(), Symbols.PLUS_UPPERCASE));
                    }
                } else {
                    setText(item.getName());
                }
            }

            private String getLabelWithHint(String label, String hint) {
                return "<html>" + label + " " + "<i>" + hint + "</i></html>";
            }

            private void icon(CustomFile item) {
                if (item.getType().isDirectory()) {
                    setIcon(FOLDER_ICON);
                } else {
                    setIcon(FILE_ICON);
                }
            }
        };
    }

    @Override
    public SelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    public final class CustomSelectionHandler extends AbstractSelectionHandler {

        private final CustomNodePanel panel;
        private CustomFile[] currentSelection;

        public CustomSelectionHandler(CustomNodePanel panel) {
            this.panel = panel;
        }

        @Override
        public void handleSelection(boolean fire3DEvent, Object... selection) {
            if (currentSelection != null && currentSelection.length > 0) {
                // panel.saveSurfaces(currentSelection);
            }
            if (TreeUtil.isConsistent(selection, CustomFile.class)) {
                this.currentSelection = Arrays.copyOf(selection, selection.length, CustomFile[].class);
            } else {
                this.currentSelection = new CustomFile[0];
            }

            panel.updateSelection(currentSelection);
            updateActions();
        }

        private void updateActions() {
            addFileAction.setEnabled(panel.canAddCustomFileToSelectedFile(getSelectedValues()));
            removeFileAction.setEnabled(panel.canRemoveSelectedCustomFile(getSelectedValues()));
            editFileAction.setEnabled(panel.canEditSelectedCustomFile(getSelectedValues()));
        }

        public void clear() {
            currentSelection = null;
        }
    }
}
