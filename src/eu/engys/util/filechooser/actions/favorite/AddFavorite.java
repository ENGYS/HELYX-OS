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

/*
 * Copyright 2012 Krzysztof Otrebski (krzysztof.otrebski@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.engys.util.filechooser.actions.favorite;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import eu.engys.util.filechooser.favorites.Favorite;
import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;

/**
 */
public class AddFavorite extends AbstractAction {

	private FileChooserController controller;

	public AddFavorite(FileChooserController controller) {
		this.controller = controller;
		putValue(NAME, NAV_ADDTOFAVORITES);
		putValue(SHORT_DESCRIPTION, NAV_ADDTOFAVORITES);
		putValue(SMALL_ICON, STAR_PLUS);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		FileObject currentLocation = controller.getUriPanel().getFileObject();
		if (currentLocation != null) {
			try {
			    String url = currentLocation.getURL().toString();
			    String name = VFSUtils.decode(url, controller.getSshParameters());
				Favorite favorite = new Favorite(name, url, Favorite.Type.USER);
				controller.addFavorite(favorite);
			} catch (FileSystemException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Resources
	 */
	private static final String NAV_ADDTOFAVORITES = ResourcesUtil.getString("nav.AddToFavorites");
	private static final Icon STAR_PLUS = ResourcesUtil.getIcon("starPlus");
}
