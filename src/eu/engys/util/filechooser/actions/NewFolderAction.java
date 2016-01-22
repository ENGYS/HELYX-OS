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

package eu.engys.util.filechooser.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.textfields.PromptTextField;

public final class NewFolderAction extends AbstractAction {

    private static final String FOLDER_ALREADY_EXISTS = "Folder already exists";
    private static final String EMPTY_NAME_MESSAGE = "Cannot create folder with emptyName!";
    private static final String NEW_FOLDER_NAME = "New Folder Name";
    private static final Logger logger = LoggerFactory.getLogger(NewFolderAction.class);
    private FileChooserController controller;

    public NewFolderAction(FileChooserController controller) {
        super("New");
        this.controller = controller;
        putValue(SMALL_ICON, NEW_FOLDER_ICON);
        putValue(SHORT_DESCRIPTION, NEW_FOLDER_TEXT);
        setEnabled(!controller.isRemote());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FileObject fileObject = controller.getUriPanel().getFileObject();
        String newFolderName = askNewFolderName();
        if (newFolderName == null) {
            return;
        } else if (newFolderName.isEmpty()) {
            JOptionPane.showMessageDialog(SwingUtilities.getRoot(controller.getUriPanel()), EMPTY_NAME_MESSAGE, NEW_FOLDER_NAME, JOptionPane.ERROR_MESSAGE);
        } else {
            String parentFile = VFSUtils.decode(fileObject.getName().getURI(), controller.getSshParameters());
            File newFile = new File(parentFile, newFolderName);
            if (newFile.exists()) {
                JOptionPane.showMessageDialog(SwingUtilities.getRoot(controller.getUriPanel()), FOLDER_ALREADY_EXISTS, NEW_FOLDER_NAME, JOptionPane.ERROR_MESSAGE);
            } else {
                newFile.mkdirs();
                refresh(fileObject);
            }
        }
    }

    private void refresh(FileObject fileObject) {
        try {
            fileObject.refresh();
            controller.goToURL(fileObject);
        } catch (FileSystemException e) {
            logger.error("Can't refresh location", e.getMessage());
        }
    }

    private String askNewFolderName() {
        final PromptTextField textFieldName = new PromptTextField();
        textFieldName.setName("newFolder.name");
        textFieldName.setPrompt("newFolder");

        Object[] options = { "Create", "Cancel" };
        Component parent = SwingUtilities.getRoot(controller.getUriPanel());
        int response = JOptionPane.showOptionDialog(parent, textFieldName, NEW_FOLDER_NAME, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (response == JOptionPane.YES_OPTION) {
            return textFieldName.getText();
        }
        return null;
    }

    /**
     * Resources
     */
    private static final String NEW_FOLDER_TEXT = ResourcesUtil.getString("new.folder.label");
    private static final Icon NEW_FOLDER_ICON = ResourcesUtil.getIcon("new.folder.icon");

}
