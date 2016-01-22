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

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.ArchiveUtils;
import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;

public final class ExtractArchiveAction extends AbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(ExtractArchiveAction.class);
    private FileChooserController controller;

    public ExtractArchiveAction(FileChooserController controller) {
        super("Extract");
        this.controller = controller;
        putValue(SMALL_ICON, EXTRACT_ARCHIVE_ICON);
        putValue(SHORT_DESCRIPTION, EXTRACT_ARCHIVE_TEXT);
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
            final File selectedFile = new File(VFSUtils.decode(archivedFileObject.getName().getURI(), controller.getSshParameters()));
            if (ArchiveUtils.isArchive(selectedFile)) {
                File parentFile = new File(VFSUtils.decode(archivedFileObject.getParent().getName().getURI(), controller.getSshParameters()));
                ArchiveUtils.unarchive(selectedFile, parentFile);

                controller.resetFileFilter();
                refresh(archivedFileObject.getParent());

                controller.getFileSystemPanel().selectFileByName(removeExtension(selectedFile.getAbsolutePath()));
            } else {
                JOptionPane.showMessageDialog(SwingUtilities.getRoot(controller.getFileSystemPanel()), "The selected file is not a known archive file", "Archive Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            logger.error("Can't extract file", e);
        }
    }

    public String removeExtension(String fileName) {
        String noFirstExtension = FilenameUtils.getBaseName(fileName);
        String noEventualSecondExtension = FilenameUtils.getBaseName(noFirstExtension);
        return noEventualSecondExtension;
    }

    private void refresh(FileObject fileObject) {
        try {
            fileObject.refresh();
            controller.goToURL(fileObject);
        } catch (FileSystemException e) {
            logger.error("Can't refresh location", e);
        }
    }

    /**
     * Resources
     */
    private static final String EXTRACT_ARCHIVE_TEXT = ResourcesUtil.getString("extract.archive.label");
    private static final Icon EXTRACT_ARCHIVE_ICON = ResourcesUtil.getIcon("extract.archive.icon");

}
