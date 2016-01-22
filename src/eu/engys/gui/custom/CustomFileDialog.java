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

package eu.engys.gui.custom;

import static eu.engys.core.project.constant.ConstantFolder.CONSTANT;
import static eu.engys.core.project.custom.CustomFileType.DICTIONARY;
import static eu.engys.core.project.custom.CustomFileType.DIRECTORY;
import static eu.engys.core.project.custom.CustomFileType.FIELD;
import static eu.engys.core.project.system.SystemFolder.SYSTEM;
import static eu.engys.util.ui.ComponentsFactory.selectField;
import static eu.engys.util.ui.ComponentsFactory.selectFieldWithItemSupport;
import static eu.engys.util.ui.ComponentsFactory.stringField;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import eu.engys.core.project.Model;
import eu.engys.core.project.custom.CustomFile;
import eu.engys.core.project.custom.CustomFileType;
import eu.engys.core.project.custom.CustomUtils;
import eu.engys.core.project.system.CaseSetupDict;
import eu.engys.core.project.system.CustomNodeDict;
import eu.engys.core.project.system.RunDict;
import eu.engys.util.ui.JComboBoxWithItemsSupport;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.StringField;
import eu.engys.util.ui.textfields.verifiers.AbstractVerifier;
import eu.engys.util.ui.textfields.verifiers.AbstractVerifier.ValidationStatusListener;

public class CustomFileDialog {

    public static final String CUSTOM_DIALOG_NAME = "custom.dialog";

    public static final String TITLE = "New Custom File";

    public static final String VALUE_LABEL = "Value";
    public static final String NAME_LABEL = "Name";
    public static final String TYPE_LABEL = "Type";
    public static final String PARENT_LABEL = "Parent";
    public static final String DEFAULT_NAME = "newFile";
    public static final String NEW_NAME = "New ...";

    public static final String CANCEL_LABEL = "Cancel";
    public static final String CREATE_NEW_LABEL = "Create New";

    private static final String[] VETOED_DICT_LIST = new String[] { RunDict.RUN_DICT, CaseSetupDict.CASE_SETUP_DICT, CustomNodeDict.CUSTOM_NODE_DICT };

    private JComboBoxWithItemsSupport typeCombo;
    private JComboBox<CustomFile> parentCombo;
    private JComboBox<String> namesCombo;
    private StringField nameField;
    private JDialog dialog;
    private JButton okButton;
    private Model model;

    private PropertyChangeListener enableOKButtonListener;
    private PropertyChangeListener enableTYPESComboListener;
    private PropertyChangeListener updateNamesCombo;

    private static final int LOAD = 0;
    private static final int NEW = 1;
    private static final int CANCEL = 2;

    public CustomFileDialog(Model model) {
        this.model = model;

        dialog = new JDialog(UiUtil.getActiveWindow(), ModalityType.APPLICATION_MODAL);
        dialog.setTitle(TITLE);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setName(CUSTOM_DIALOG_NAME);

        PanelBuilder builder = new PanelBuilder();
        namesCombo = createNamesCombo();
        nameField = createNameField();
        parentCombo = createParentCombo();
        typeCombo = createTypeCombo();

        builder.addComponent(PARENT_LABEL, parentCombo);
        builder.addComponent(TYPE_LABEL, typeCombo);
        builder.addComponent(NAME_LABEL, namesCombo);
        builder.addComponent(VALUE_LABEL, nameField);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(builder.getPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.getRootPane().setDefaultButton(okButton);

        enableOKButtonListener = new EnableOkButtonListener();
        enableTYPESComboListener = new EnableTypesComboListener();
        updateNamesCombo = new UpdateNamesCombo();
    }

    private StringField createNameField() {
        StringField newFoNameField = stringField(DEFAULT_NAME);
        ((AbstractVerifier) newFoNameField.getInputVerifier()).setValidationStatusListener(new ValidationStatusListener() {
            @Override
            public void validatePassed() {
                okButton.setEnabled(true);
            }

            @Override
            public void validateFailed() {
                okButton.setEnabled(false);
            }
        });
        return newFoNameField;
    }

    private JComboBox<String> createNamesCombo() {
        JComboBox<String> namesCombo = selectField();
        namesCombo.addPropertyChangeListener(new EnableNameFieldListener());
        return namesCombo;
    }

    private JComboBoxWithItemsSupport createTypeCombo() {
        JComboBoxWithItemsSupport typeCombo = selectFieldWithItemSupport(CustomFileType.keys(), CustomFileType.labels());
        typeCombo.setSelectedIndex(-1);
        typeCombo.addPropertyChangeListener(enableOKButtonListener);
        typeCombo.addPropertyChangeListener(updateNamesCombo);
        return typeCombo;
    }

    private JComboBox<CustomFile> createParentCombo() {
        JComboBox<CustomFile> parentCombo = selectField();
        parentCombo.setSelectedIndex(-1);
        parentCombo.addPropertyChangeListener(enableTYPESComboListener);
        parentCombo.addPropertyChangeListener(updateNamesCombo);
        return parentCombo;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton(new AddCustomFile());
        okButton.setEnabled(false);
        okButton.setName("ok");
        panel.add(okButton);

        JButton cancelButton = new JButton(new AbstractAction("Cancel") {

            @Override
            public void actionPerformed(ActionEvent e) {
                resetAndClose();
            }

        });
        cancelButton.setName("cancel");
        panel.add(cancelButton);
        return panel;
    }

    private void resetAndClose() {
        nameField.setText(DEFAULT_NAME);
        typeCombo.setSelectedIndex(-1);
        disposeDialog();
    }

    private void disposeDialog() {
        dialog.setVisible(false);
        dialog.dispose();
        dialog = null;
    }

    public void show(CustomFile parent) {
        removeListeners();
        updateNamesCombo(parent);
        updateParentCombo(parent);
        fixTypeCombo(parentCombo.getSelectedItem());
        addListeners();
        dialog.setVisible(true);
    }

    private void updateParentCombo(CustomFile selected) {
        List<CustomFile> parentFiles = model.getCustom().getParentFiles();
        for (CustomFile customFile : parentFiles) {
            parentCombo.addItem(customFile);
        }
        parentCombo.setSelectedItem(selected);
    }

    private void updateNamesCombo(CustomFile parent) {
        namesCombo.removeAllItems();
        namesCombo.addItem(NEW_NAME);

        if (parent != null && typeCombo.getSelectedItem() != null) {
            _updateNamesCombo(parent);
        }
        namesCombo.setSelectedItem(0);
    }

    private void _updateNamesCombo(CustomFile parent) {
        List<String> children = parent.getChildrenNames();
        File parentFile = CustomUtils.getFiles(model, parent).get(0);
        if (parentFile.exists()) {
            addNames(children, parentFile);
        }
    }

    private void addNames(List<String> children, File parentFile) {
        if (isTypeSelected(DIRECTORY)) {
            addDirectoryNames(children, parentFile);
        } else if (isTypeSelected(DICTIONARY)) {
            addDictionaryNames(children, parentFile);
        } else if (isTypeSelected(FIELD)) {
            addFieldsNames(children, parentFile);
        }
    }

    private void addDictionaryNames(final List<String> children, File parentFile) {
        for (File f : parentFile.listFiles(new ValidDictionaryFileFilter(children))) {
            namesCombo.addItem(f.getName());
        }
    }

    private void addFieldsNames(final List<String> children, File parentFile) {
        for (File f : parentFile.listFiles(new ValidFieldFileFilter(children))) {
            namesCombo.addItem(f.getName());
        }
    }

    private void addDirectoryNames(final List<String> children, File parentFile) {
        for (File f : parentFile.listFiles(new ValidDirectoryFileFilter(children))) {
            namesCombo.addItem(f.getName());
        }
    }

    private boolean isTypeSelected(CustomFileType type) {
        return typeCombo.getSelectedItem().equals(type.getKey());
    }

    private boolean isNewFile() {
        return namesCombo.getSelectedIndex() == 0;
    }

    private void removeListeners() {
        typeCombo.removePropertyChangeListener(enableOKButtonListener);
        typeCombo.removePropertyChangeListener(updateNamesCombo);
        parentCombo.removePropertyChangeListener(enableTYPESComboListener);
        parentCombo.removePropertyChangeListener(updateNamesCombo);
    }

    private void addListeners() {
        typeCombo.addPropertyChangeListener(enableOKButtonListener);
        typeCombo.addPropertyChangeListener(updateNamesCombo);
        parentCombo.addPropertyChangeListener(enableTYPESComboListener);
        parentCombo.addPropertyChangeListener(updateNamesCombo);
    }

    private class AddCustomFile extends AbstractAction {

        public AddCustomFile() {
            super("OK");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            addFile();
        }

        private void addFile() {
            String type = typeCombo.getItemAt(typeCombo.getSelectedIndex());
            CustomFile parent = parentCombo.getItemAt(parentCombo.getSelectedIndex());

            if (parent == null) {
                JOptionPane.showMessageDialog(dialog, "Please, specify a parent for the file", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String fileName = getValidName(parent, nameField.getText());
            CustomFileType fileType = CustomFileType.valueOf(type.toUpperCase());
            CustomFile customFile = new CustomFile(parent, fileType, fileName);

            File file = CustomUtils.getFiles(model, customFile).get(0);
            boolean exists = file.exists();

            if (exists && !customFile.getType().isDirectory()) {
                if (isNewFile()) {
                    boolean parallel = model.getProject().isParallel();
                    String suffix = (customFile.getType().isField() && parallel) ? "Template" : "from File";
                    Object[] options = { "Load " + suffix, CREATE_NEW_LABEL, CANCEL_LABEL };
                    String message = customFile.getName() + " already exists, please select an action.";
                    int retVal = JOptionPane.showOptionDialog(dialog, message, "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                    switch (retVal) {
                    case CANCEL:
                        return;
                    case NEW:
                        break;
                    case LOAD:
                        CustomUtils.loadFromDisk(fileName, customFile, file);
                        break;
                    default:
                        break;
                    }
                } else {
                    CustomUtils.loadFromDisk(fileName, customFile, file);
                }

            }
            model.getCustom().add(customFile);
            model.customFileChanged(customFile);
            resetAndClose();
        }

        private String getValidName(CustomFile parent, String text) {
            if (!isValidName(parent, text)) {
                return getValidName(parent, text + "_copy");
            }
            return text;
        }

        private boolean isValidName(CustomFile parent, String text) {
            for (CustomFile cf : parent.getChildren()) {
                if (cf.getName().equals(text)) {
                    return false;
                }
            }
            return true;
        }

    }

    private class EnableOkButtonListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                okButton.setEnabled(evt.getNewValue() != null);
            }
        }

    }

    private class UpdateNamesCombo implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                updateNamesCombo((CustomFile) parentCombo.getSelectedItem());
            }
        }
    }

    private class EnableNameFieldListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                if (isNewFile()) {
                    nameField.setText(DEFAULT_NAME);
                    nameField.setEnabled(true);
                } else {
                    Object selectedItem = namesCombo.getSelectedItem();
                    nameField.setText(selectedItem != null ? selectedItem.toString() : DEFAULT_NAME);
                    nameField.setEnabled(false);
                }
            }
        }

    }

    private class EnableTypesComboListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                fixTypeCombo(evt.getNewValue());
            }
        }

    }

    public void fixTypeCombo(Object selectedItem) {
        if (selectedItem != null) {
            switch (selectedItem.toString()) {
            case "0":
                typeCombo.clearDisabledIndexes();
                if (typeCombo.getSelectedItem() == DICTIONARY.getKey()) {
                    typeCombo.setSelectedItem(FIELD.getKey());
                }
                // typeCombo.addDisabledIndex(directoryIndex);
                typeCombo.addDisabledItem(DICTIONARY.getKey());
                break;
            case SYSTEM:
            case CONSTANT:
            default:
                typeCombo.clearDisabledIndexes();
                if (typeCombo.getSelectedItem() == FIELD.getKey()) {
                    typeCombo.setSelectedItem(DICTIONARY.getKey());
                }
                typeCombo.addDisabledItem(FIELD.getKey());
                break;
            }
        } else {
            typeCombo.setSelectedIndex(-1);
            typeCombo.clearDisabledIndexes();
        }
    }

    private class ValidDictionaryFileFilter implements FileFilter {

        private List<String> children;

        public ValidDictionaryFileFilter(List<String> children) {
            this.children = children;
        }

        @Override
        public boolean accept(File pathname) {
            boolean isFile = pathname.isFile();
            boolean isVisible = !pathname.isHidden();
            boolean isValidName = !pathname.getName().endsWith("~");
            boolean isNotAlreadyUsed = !children.contains(pathname.getName());
            boolean isNotVetoed = !Arrays.asList(VETOED_DICT_LIST).contains(pathname.getName());
            return isFile && isVisible && isValidName && isNotAlreadyUsed && isNotVetoed;
        }

    }

    private class ValidFieldFileFilter implements FileFilter {

        private List<String> children;

        public ValidFieldFileFilter(List<String> children) {
            this.children = children;
        }

        @Override
        public boolean accept(File pathname) {
            boolean isFile = pathname.isFile();
            boolean isVisible = !pathname.isHidden();
            boolean isValidName = !pathname.getName().endsWith("~");
            boolean isNotAlreadyUsed = !children.contains(pathname.getName());
            boolean isFieldName = model.getFields().orderedFieldsNames().contains(pathname.getName()) || isDynamic(pathname.getName());

            return isFile && isVisible && isValidName && isNotAlreadyUsed && isFieldName;
        }

        private boolean isDynamic(String name) {
            return name.equals("pointMotionU") || name.equals("pointDisplacement");
        }
    }

    private class ValidDirectoryFileFilter implements FileFilter {

        private List<String> children;

        public ValidDirectoryFileFilter(List<String> children) {
            this.children = children;
        }

        @Override
        public boolean accept(File pathname) {
            boolean isDir = pathname.isDirectory();
            boolean isVisible = !pathname.isHidden();
            boolean isNotAlreadyUsed = !children.contains(pathname.getName());
            return isDir && isVisible && isNotAlreadyUsed && isNotAlreadyUsed;
        }
    }

}
