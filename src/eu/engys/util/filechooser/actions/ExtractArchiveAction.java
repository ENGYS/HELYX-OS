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
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.ArchiveUtils;
import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;

public final class ExtractArchiveAction extends AbstractAction {
	
	private static final Logger logger = LoggerFactory.getLogger(ExtractArchiveAction.class);

	private static final String TITLE = "Archive Error";
	private static final String NOT_AN_ARCHIVE_MESSAGE = "The selected file is not a known archive file";

	private FileChooserController controller;

    public ExtractArchiveAction(FileChooserController controller) {
        super(LABEL, ICON);
        putValue(SHORT_DESCRIPTION, TOOLTIP);
        this.controller = controller;
        setEnabled(!controller.isRemote());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controller.showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                extractArchives();
                controller.showTable();
            }
        }).start();
    }

    private void extractArchives() {
        FileObject[] fileObjects = controller.getFileSystemPanel().getSelectedFileObjects();
        for (FileObject fileObject : fileObjects) {
            extractFileObject(fileObject);
        }
    }

    private void extractFileObject(FileObject archivedFileObject) {
        try {
            File selectedFile = new File(VFSUtils.decode(archivedFileObject.getName().getURI(), controller.getSshParameters()));
            if (ArchiveUtils.isArchive(selectedFile)) {
                File parentFile = new File(VFSUtils.decode(archivedFileObject.getParent().getName().getURI(), controller.getSshParameters()));
                ArchiveUtils.unarchive(selectedFile, parentFile);

                controller.resetFileFilter();
                controller.refreshLocation(archivedFileObject.getParent());
                controller.getFileSystemPanel().selectFileByName(removeExtension(selectedFile.getAbsolutePath()));
            } else {
                showMessageDialog(SwingUtilities.getRoot(controller.getFileSystemPanel()), NOT_AN_ARCHIVE_MESSAGE, TITLE, ERROR_MESSAGE);
            }

        } catch (Exception e) {
            logger.error(TITLE, e);
        }
    }

    public String removeExtension(String fileName) {
        String noFirstExtension = FilenameUtils.getBaseName(fileName);
        String noEventualSecondExtension = FilenameUtils.getBaseName(noFirstExtension);
        return noEventualSecondExtension;
    }

    /**
     * Resources
     */
    private static final String LABEL = ResourcesUtil.getString("extract.archive.label");
    private static final String TOOLTIP = ResourcesUtil.getString("extract.archive.tooltip");
    private static final Icon ICON = ResourcesUtil.getIcon("extract.archive.icon");

}
