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
package eu.engys.util.filechooser.actions.favorite;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.filechooser.favorites.Favorite;
import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;

public class AddFavoriteAction extends AbstractAction {

	private static final Logger logger = LoggerFactory.getLogger(AddFavoriteAction.class);
	
	private FileChooserController controller;

	public AddFavoriteAction(FileChooserController controller) {
		super(LABEL, ICON);
		putValue(SHORT_DESCRIPTION, TOOLTIP);
		this.controller = controller;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		FileObject currentLocation = controller.getUriPanel().getFileObject();
		if (currentLocation != null) {
			try {
			    addFavorite(currentLocation);
			} catch (FileSystemException ex) {
				logger.error("Filesystem error: {}", ex.getMessage());
			}
		}
	}

	private void addFavorite(FileObject currentLocation) throws FileSystemException {
		String url = currentLocation.getURL().toString();
		String name = VFSUtils.decode(url, controller.getSshParameters());
		Favorite favorite = new Favorite(name, url, Favorite.Type.USER);
		controller.addFavorite(favorite);
	}

	/**
	 * Resources
	 */
	private static final String LABEL = ResourcesUtil.getString("add.favorite.label");
	private static final String TOOLTIP = ResourcesUtil.getString("add.favorite.tooltip");
	private static final Icon ICON = ResourcesUtil.getIcon("add.favorite.icon");
}
