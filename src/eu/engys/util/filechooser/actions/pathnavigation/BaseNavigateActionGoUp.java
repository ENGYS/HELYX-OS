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

import javax.swing.Icon;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;

public final class BaseNavigateActionGoUp extends BaseNavigateAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseNavigateActionGoUp.class);

	public BaseNavigateActionGoUp(FileChooserController controller) {
		super(controller);
		putValue(SMALL_ICON, ARROWTURN90);
		putValue(SHORT_DESCRIPTION, NAV_GOFOLDERUP);
	}

	@Override
	public void performLongOperation(CheckBeforeActionResult actionResult) {
		LOGGER.debug("Executing going up");
		try {
		    FileObject parent = controller.getUriPanel().getFileObject().getParent();
			controller.goToURL(parent);
		} catch (FileSystemException e) {
			LOGGER.error("Error go UP", e);
		}
	}

	@Override
	protected boolean canGoUrl() {
		try {
			FileObject parent = controller.getUriPanel().getFileObject().getParent();
			return parent != null && VFSUtils.canGoUrl(parent);
		} catch (FileSystemException e) {
			LOGGER.error("Can't get parent of current location", e);
		}
		return false;
	}

	@Override
	protected boolean canExecuteDefaultAction() {
		return false;
	}

	/**
	 * Resources
	 */

	private static final String NAV_GOFOLDERUP = ResourcesUtil.getString("nav.goFolderUp");
	private static final Icon ARROWTURN90 = ResourcesUtil.getIcon("arrowTurn90");
}
