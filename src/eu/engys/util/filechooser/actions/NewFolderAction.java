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
package eu.engys.util.filechooser.actions;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.showOptionDialog;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.SwingUtilities;

import org.apache.commons.vfs2.FileObject;

import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.textfields.StringField;

public final class NewFolderAction extends AbstractAction {

    private static final String TITLE = "New Folder";
    private static final String PROMPT = "Folder name";
    public static final String TEXTFIELD_NAME = "newFolder.name";
    public static final String CANCEL_LABEL = "Cancel";
    public static final String CREATE_LABEL = "Create";
    private static final String FOLDER_ALREADY_EXISTS = "Folder already exists";
    private static final String EMPTY_NAME_MESSAGE = "Cannot create folder with empty name";

    private FileChooserController controller;

    public NewFolderAction(FileChooserController controller) {
        super(LABEL, ICON);
        putValue(SHORT_DESCRIPTION, TOOLTIP);
        this.controller = controller;
        setEnabled(!controller.isRemote());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FileObject parentFO = controller.getUriPanel().getFileObject();
        if (parentFO != null) {
            createNewFolder(parentFO);
        }
    }

    private void createNewFolder(FileObject parentFO) {
        String newFolderName = promptName();
        if (newFolderName == null) {
            return;
        } else if (newFolderName.isEmpty()) {
            showMessageDialog(SwingUtilities.getRoot(controller.getUriPanel()), EMPTY_NAME_MESSAGE, TITLE, ERROR_MESSAGE);
            createNewFolder(parentFO);
        } else {
            create(parentFO, newFolderName);
        }
    }

    private String promptName() {
        StringField nameField = new StringField();
        nameField.setName(TEXTFIELD_NAME);
        nameField.setPrompt(PROMPT);

        Object[] options = { CREATE_LABEL, CANCEL_LABEL };
        Component parent = SwingUtilities.getRoot(controller.getUriPanel());
        int response = showOptionDialog(parent, nameField, TITLE, YES_NO_OPTION, QUESTION_MESSAGE, null, options, options[0]);
        if (response == YES_OPTION) {
            return nameField.getText();
        }
        return null;
    }

    private void create(FileObject parentFO, String newFolderName) {
        String parentFile = VFSUtils.decode(parentFO.getName().getURI(), controller.getSshParameters());
        File newFile = new File(parentFile, newFolderName);
        if (newFile.exists()) {
            showMessageDialog(SwingUtilities.getRoot(controller.getUriPanel()), FOLDER_ALREADY_EXISTS, TITLE, ERROR_MESSAGE);
            createNewFolder(parentFO);
        } else {
            newFile.mkdirs();
            controller.refreshLocation(parentFO);
        }
    }

    /**
     * Resources
     */
    private static final String LABEL = ResourcesUtil.getString("new.folder.label");
    private static final String TOOLTIP = ResourcesUtil.getString("new.folder.tooltip");
    private static final Icon ICON = ResourcesUtil.getIcon("new.folder.icon");

}
