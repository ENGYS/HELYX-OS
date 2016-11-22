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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import eu.engys.util.filechooser.favorites.Favorite;
import eu.engys.util.filechooser.favorites.list.MutableListModel;
import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.textfields.StringField;

public class RenameFavoriteAction extends AbstractAction {
	
	private static final String TITLE = "Rename favorite";
	private static final String NAME_LABEL = "Name:";
	private static final String URL_LABEL = "URL:";

    private JList<Favorite> favoriteList;
    private MutableListModel<Favorite> listModel;
    private FileChooserController controller;

    public RenameFavoriteAction(FileChooserController controller, JList<Favorite> favoriteList, MutableListModel<Favorite> listModel) {
        super(LABEL, ICON);
        putValue(SHORT_DESCRIPTION, TOOLTIP);
        this.controller = controller;
        this.favoriteList = favoriteList;
        this.listModel = listModel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (favoriteList.getSelectedValue() != null) {
            Favorite favorite = favoriteList.getSelectedValue();
            JPanel panel = new JPanel(new GridLayout(4, 1));

            StringField nameField = new StringField(favorite.getName(), true, false);
            nameField.setName("favorite.name");
            panel.add(new JLabel(NAME_LABEL));
            panel.add(nameField);

            StringField urlField = new StringField(decodedURL(favorite), true, false);
            urlField.setName("favorite.url");
            panel.add(new JLabel(URL_LABEL));
            panel.add(urlField);

            int response = JOptionPane.showConfirmDialog(SwingUtilities.getRoot(favoriteList), panel, TITLE, JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                favorite.setName(nameField.getText());
                favorite.setUrl(encodedURL(urlField));
                listModel.change(favoriteList.getSelectedIndex(), favorite);
            }
        }
    }

    private String encodedURL(JTextField urlField) {
        return VFSUtils.encode(urlField.getText(), controller.getSshParameters());
    }

    private String decodedURL(Favorite favorite) {
        return VFSUtils.decode(favorite.getUrl(), controller.getSshParameters());
    }

    /**
     * Resources
     */

    private static final String LABEL = ResourcesUtil.getString("rename.favorite.label");
    private static final String TOOLTIP = ResourcesUtil.getString("rename.favorite.name");
    private static final Icon ICON = ResourcesUtil.getIcon("rename.favorite.icon");
}
