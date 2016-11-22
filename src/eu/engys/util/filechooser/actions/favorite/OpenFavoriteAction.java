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

import javax.swing.JList;

import eu.engys.util.filechooser.actions.navigation.BaseNavigateAction;
import eu.engys.util.filechooser.favorites.Favorite;
import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.ui.ResourcesUtil;

public class OpenFavoriteAction extends BaseNavigateAction {

    private JList<Favorite> favoriteList;

    public OpenFavoriteAction(FileChooserController controller, JList<Favorite> favoriteList) {
        super(controller, LABEL);
        putValue(SHORT_DESCRIPTION, TOOLTIP);
        this.favoriteList = favoriteList;
    }

    @Override
    protected void performLongOperation(CheckBeforeActionResult checkBeforeActionResult) {
        if (favoriteList.getSelectedValue() != null) {
            Favorite favorite = favoriteList.getSelectedValue();
            controller.goToURL(favorite.getUrl(), true);
            controller.updateOkButton();
        }
    }

    @Override
    protected boolean canGoUrl() {
        return favoriteList.getSelectedValue() != null;
    }

    @Override
    protected boolean canExecuteDefaultAction() {
        return true;
    }
    
    /**
	 * Resources
	 */
	private static final String LABEL = ResourcesUtil.getString("open.favorite.label");
	private static final String TOOLTIP = ResourcesUtil.getString("open.favorite.tooltip");

}
