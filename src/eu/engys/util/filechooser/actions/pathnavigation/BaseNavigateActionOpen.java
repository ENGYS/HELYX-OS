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

package eu.engys.util.filechooser.actions.pathnavigation;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.filechooser.util.VFSUtils;

public final class BaseNavigateActionOpen extends BaseNavigateAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseNavigateActionOpen.class);
    private final FileChooserController controller;

    public BaseNavigateActionOpen(FileChooserController controller) {
        super(controller);
        this.controller = controller;
    }

    @Override
    public void performLongOperation(CheckBeforeActionResult checkBeforeActionResult) {
        // When double click a file on the table
        FileObject fileObject = controller.getSelectedFileObject();
        if (canExecuteDefaultAction()) {
            controller.closeAndReturn(ReturnValue.Approve);
        } else {
            controller.goToURL(fileObject);
        }
    }

    @Override
    protected boolean canGoUrl() {
        FileObject fileObject = controller.getSelectedFileObject();
        if (fileObject != null) {
            try {
                return VFSUtils.canGoUrl(fileObject);
            } catch (FileSystemException e) {
                LOGGER.error("Can't open location", e.getMessage());
            }
        }
        return false;
    }

    @Override
    protected boolean canExecuteDefaultAction() {
        SelectionMode selectionMode = controller.getSelectionMode();
        if (selectionMode.isFilesOnly() || selectionMode.isDirsAndFiles()) {
            FileObject fileObject = controller.getSelectedFileObject();
            if (fileObject != null) {
                try {
                    return FileType.FILE.equals(fileObject.getType()) || FileType.FILE_OR_FOLDER.equals(fileObject.getType());
                } catch (FileSystemException e) {
                    LOGGER.warn("Cant' get file type", e);
                }
            }
        }
        return false;
    }

}
