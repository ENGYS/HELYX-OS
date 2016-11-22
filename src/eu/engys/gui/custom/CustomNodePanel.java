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

import static eu.engys.util.ui.ComponentsFactory.stringField;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.custom.Custom.ConstantDirectory;
import eu.engys.core.project.custom.Custom.SystemDirectory;
import eu.engys.core.project.custom.Custom.ZeroDirectory;
import eu.engys.core.project.custom.CustomFile;
import eu.engys.gui.AbstractGUIPanel;
import eu.engys.gui.DictionaryEditor;
import eu.engys.gui.FileEditor;
import eu.engys.gui.tree.TreeNodeManager;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.StringField;
import net.java.dev.designgridlayout.Componentizer;

public class CustomNodePanel extends AbstractGUIPanel {

    public static final String ADD_LABEL = "Add";
    public static final String REMOVE_LABEL = "Remove";

    public static final String CUSTOM = "Custom";
    public static final String FILE_NAME_LABEL = "File Name";
    public static final String FILE_TYPE_LABEL = "File Type";
    public static final String FILE_PARENT_LABEL = "File Parent";
    public static final String EDIT_LABEL = "Edit";

    private CustomTreeNodeManager treeNodeManager;
    private JButton newButton;
    private JButton removeButton;
    private StringField typeField;
    private StringField nameField;
    private StringField parentField;
    private JButton editButton;

    @Inject
    public CustomNodePanel(Model model) {
        super(CUSTOM, model);
        this.treeNodeManager = new CustomTreeNodeManager(model, this);
        model.addObserver(treeNodeManager);
    }

    @Override
    protected JComponent layoutComponents() {
        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(createButtonsPanel());
        builder.addComponent(createNameTypePanel());

        return builder.removeMargins().getPanel();
    }

    private JComponent createButtonsPanel() {
        List<JComponent> actionsList = new ArrayList<JComponent>();

        newButton = new JButton(getAddFileAction());
        newButton.setName(ADD_LABEL);

        removeButton = new JButton(getRemoveFileAction());
        removeButton.setName(REMOVE_LABEL);
        removeButton.setEnabled(false);

        actionsList.add(newButton);
        actionsList.add(removeButton);

        JComponent buttonsPanel = UiUtil.getCommandRow(actionsList);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder());
        return buttonsPanel;
    }

    private JComponent createNameTypePanel() {
        PanelBuilder nameTypeBuider = new PanelBuilder();

        initNameField();
        initParentField();
        initTypeField();
        initEditButton();

        nameTypeBuider.addComponent(FILE_NAME_LABEL, nameField);
        nameTypeBuider.addComponent(FILE_TYPE_LABEL, typeField);
        nameTypeBuider.addComponent(FILE_PARENT_LABEL, parentField);
        nameTypeBuider.addComponent(EDIT_LABEL, Componentizer.create().minToPref(editButton).prefAndMore(new JLabel()).component());

        return nameTypeBuider.removeMargins().getPanel();
    }

    private void initEditButton() {
        editButton = new JButton(getEditFileAction());
        editButton.setName(EDIT_LABEL);
        editButton.setEnabled(false);
    }

    private void initTypeField() {
        typeField = stringField();
        typeField.setEnabled(false);
    }

    private void initParentField() {
        parentField = stringField();
        parentField.setEnabled(false);
    }

    private void initNameField() {
        nameField = stringField();
        nameField.setEnabled(false);
    }

    public boolean canRemoveSelectedCustomFile(CustomFile[] currentSelection) {
        if (!isSomethingSelected(currentSelection)) {
            return false;
        } else {
            boolean notZero = !(currentSelection[0] instanceof ZeroDirectory);
            boolean notConstant = !(currentSelection[0] instanceof ConstantDirectory);
            boolean notSystem = !(currentSelection[0] instanceof SystemDirectory);
            return notZero && notConstant && notSystem;
        }
    }

    public boolean canEditSelectedCustomFile(CustomFile[] currentSelection) {
        if (!isSomethingSelected(currentSelection)) {
            return false;
        } else {
            boolean notDirectory = !(currentSelection[0].getType().isDirectory());
            return notDirectory;
        }
    }

    public boolean canAddCustomFileToSelectedFile(CustomFile[] currentSelection) {
        if (isSomethingSelected(currentSelection)) {
            return currentSelection[0].getType().isDirectory();
        } else {
            return false;
        }
    }

    private boolean isSomethingSelected(CustomFile[] selection) {
        if (selection == null) {
            return false;
        }
        if (selection.length == 0) {
            return false;
        }
        if (selection.length == 1 && selection[0] == null) {
            return false;
        }
        return true;
    }

    public void updateSelection(CustomFile[] currentSelection) {
        updateTypeField(currentSelection);
        updateParentField(currentSelection);
        updateNameField(currentSelection);
        updateAddButton(currentSelection);
        updateEditButton(currentSelection);
        updateRemoveButton(currentSelection);
    }

    private void updateAddButton(CustomFile[] currentSelection) {
        newButton.setEnabled(canAddCustomFileToSelectedFile(currentSelection));
    }

    private void updateRemoveButton(CustomFile[] currentSelection) {
        removeButton.setEnabled(canRemoveSelectedCustomFile(currentSelection));
    }

    private void updateEditButton(CustomFile[] currentSelection) {
        if (currentSelection != null && currentSelection.length > 0) {
            if (currentSelection.length == 1 && currentSelection[0] != null) {
                editButton.setEnabled(!currentSelection[0].getType().isDirectory());
            } else {
                editButton.setEnabled(false);
            }
        }
    }

    private void updateTypeField(CustomFile... currentSelection) {
        if (currentSelection != null && currentSelection.length > 0) {
            if (currentSelection.length == 1 && currentSelection[0] != null) {
                typeField.setText(currentSelection[0].getType() != null ? currentSelection[0].getType().getLabel() : "");
            } else {
                StringBuilder sb = new StringBuilder();
                for (CustomFile file : currentSelection) {
                    sb.append(file.getType().getLabel() + " ");
                }
                typeField.setText(sb.toString());
            }
        }
    }

    private void updateParentField(CustomFile... currentSelection) {
        if (currentSelection != null && currentSelection.length > 0) {
            if (currentSelection.length == 1 && currentSelection[0] != null) {
                parentField.setText(currentSelection[0].getParent().getName());
            } else {
                StringBuilder sb = new StringBuilder();
                for (CustomFile file : currentSelection) {
                    sb.append(file.getParent().getName() + " ");
                }
                parentField.setText(sb.toString());
                parentField.setEnabled(false);
            }
        }
    }

    private void updateNameField(CustomFile... currentSelection) {
        if (currentSelection != null && currentSelection.length > 0) {
            if (currentSelection.length == 1 && currentSelection[0] != null) {
                nameField.setText(currentSelection[0].getName());
            } else {
                StringBuilder sb = new StringBuilder();
                for (CustomFile file : currentSelection) {
                    sb.append(file.getName() + " ");
                }
                nameField.setText(sb.toString());
            }
        }
    }

    @Override
    public TreeNodeManager getTreeNodeManager() {
        return treeNodeManager;
    }

    public AbstractAction getAddFileAction() {
        return new AddAction();
    }

    public AbstractAction getRemoveFileAction() {
        return new RemoveAction();
    }

    public AbstractAction getEditFileAction() {
        return new EditAction();
    }

    private class AddAction extends AbstractAction {

        public AddAction() {
            super(ADD_LABEL);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            addFile();

        }
    }

    private class RemoveAction extends AbstractAction {

        public RemoveAction() {
            super(REMOVE_LABEL);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            removeSelectedFiles();
        }
    }

    private class EditAction extends AbstractAction {

        public EditAction() {
            super(EDIT_LABEL);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            editSelectedFile();
        }
    }

    private void addFile() {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                CustomFileDialog dialog = new CustomFileDialog(model);
                dialog.show(treeNodeManager.getSelectedValue());
                updateRemoveButton(treeNodeManager.getSelectedValues());
                editSelectedFile();
            }
        });
    }

    private void removeSelectedFiles() {
        CustomFile[] selectedValue = treeNodeManager.getSelectedValues();
        for (CustomFile customFile : selectedValue) {
            model.getCustom().remove(customFile);
        }
        model.customChanged();
    }

    private void editSelectedFile() {
        final CustomFile customFile = treeNodeManager.getSelectedValue();
        Runnable onShowRunnable = new Runnable() {
            @Override
            public void run() {
                editButton.setEnabled(false);
            }
        };
        Runnable onDisposeRunnable = new Runnable() {
            @Override
            public void run() {
                editButton.setEnabled(true);
                model.customFileChanged(customFile);
            }
        };
        Runnable onOKRunnable = new Runnable() {
            @Override
            public void run() {
                customFile.setChanged(true);
            }
        };
        if (customFile != null) {
            if (customFile.getType().isDictionary() || customFile.getType().isField()) {
                Dictionary dictionaryToEdit = customFile.getDictionary();
                DictionaryEditor.getInstance().show(SwingUtilities.getWindowAncestor(this), dictionaryToEdit, onShowRunnable, onDisposeRunnable, onOKRunnable);
            } else if (customFile.getType().isRaw()) {
                List<String> rawContent = customFile.getRawFileContent();
                FileEditor.getInstance().show(SwingUtilities.getWindowAncestor(this), rawContent, customFile.getName(), onShowRunnable, onDisposeRunnable, onOKRunnable);
            } else {
                // do nothing
            }
        }
    }
}
