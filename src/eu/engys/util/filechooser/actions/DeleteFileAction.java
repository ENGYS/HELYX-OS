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

import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;

public final class DeleteFileAction extends AbstractAction {

	private static final Logger logger = LoggerFactory.getLogger(DeleteFileAction.class);
	private FileChooserController controller;

	public DeleteFileAction(FileChooserController controller) {
		super("Delete");
		this.controller = controller;
		putValue(SMALL_ICON, DELETE_FOLDER_ICON);
		putValue(SHORT_DESCRIPTION, DELETE_FOLDER_TEXT);
		setEnabled(!controller.isRemote());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		FileObject[] fileObjects = controller.getFileSystemPanel().getSelectedFileObjects();
		for (FileObject fo : fileObjects) {
			File file = new File(VFSUtils.decode(fo.getName().getURI(), controller.getSshParameters()));
			FileUtils.deleteQuietly(file);
		}
		refresh(controller.getUriPanel().getFileObject());
	}

	private void refresh(FileObject fileObject) {
		try {
			fileObject.refresh();
			controller.goToURL(fileObject);
		} catch (FileSystemException e) {
			logger.error("Can't refresh location", e.getMessage());
		}
	}

	/**
	 * Resources
	 */
	private static final String DELETE_FOLDER_TEXT = ResourcesUtil.getString("delete.file.label");
	private static final Icon DELETE_FOLDER_ICON = ResourcesUtil.getIcon("delete.file.icon");

}
