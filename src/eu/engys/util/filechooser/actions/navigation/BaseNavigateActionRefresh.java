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
package eu.engys.util.filechooser.actions.navigation;

import javax.swing.Icon;

import org.apache.commons.vfs2.FileObject;

import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.ui.ResourcesUtil;

public final class BaseNavigateActionRefresh extends BaseNavigateAction {

	public BaseNavigateActionRefresh(FileChooserController controller) {
		super(controller, LABEL, ICON);
		putValue(SHORT_DESCRIPTION, TOOLTIP);
	}

	@Override
	public void performLongOperation(CheckBeforeActionResult checkBeforeActionResult) {
		FileObject fileObject = controller.getUriPanel().getFileObject();
		controller.refreshLocation(fileObject);
	}

	@Override
	protected boolean canGoUrl() {
		return true;
	}

	@Override
	protected boolean canExecuteDefaultAction() {
		return false;
	}

	/**
	 * Resources
	 */

	private static final String LABEL = ResourcesUtil.getString("path.refresh.label");
	private static final String TOOLTIP = ResourcesUtil.getString("path.refresh.tooltip");
	private static final Icon ICON = ResourcesUtil.getIcon("path.refresh.icon");

}
