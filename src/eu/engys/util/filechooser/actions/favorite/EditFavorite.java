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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import eu.engys.util.filechooser.favorites.Favorite;
import eu.engys.util.filechooser.favorites.list.MutableListModel;
import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;

/**
 */
public class EditFavorite extends AbstractAction {

    private JList<Favorite> favoriteList;
    private MutableListModel<Favorite> listModel;
    private FileChooserController controller;

    public EditFavorite(FileChooserController controller, JList<Favorite> favoriteList, MutableListModel<Favorite> listModel) {
        super(EDITFAVORITES_ACTIONNAME, EDITSIGNATURE);
        super.putValue(SHORT_DESCRIPTION, EDITFAVORITES_TOOLTIP);
        this.controller = controller;
        this.favoriteList = favoriteList;
        this.listModel = listModel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (favoriteList.getSelectedValue() != null) {
            Favorite favorite = favoriteList.getSelectedValue();
            JPanel panel = new JPanel(new GridLayout(4, 1));

            JTextField nameField = new JTextField(favorite.getName());
            addNameListeners(nameField);
            nameField.setName("favorite.name");
            panel.add(new JLabel(EDITFAVORITES_NAME));
            panel.add(nameField);

            JTextField urlField = new JTextField(decodedURL(favorite), 20);
            urlField.setName("favorite.url");
            panel.add(new JLabel(EDITFAVORITES_URL));
            panel.add(urlField);

            int response = JOptionPane.showConfirmDialog(SwingUtilities.getRoot(favoriteList), panel, EDITFAVORITES_TITLE, JOptionPane.YES_NO_OPTION);
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

    private void addNameListeners(final JTextField text) {
        text.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        text.selectAll();
                    }
                });
            }

            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        text.select(0, 0);
                    }
                });
            }
        });
        text.addAncestorListener(new AncestorListener() {

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }

            @Override
            public void ancestorAdded(AncestorEvent event) {
                // doesn't work because the "yes" button grabs the focus
                text.requestFocusInWindow();
            }
        });
        text.setFocusable(true);
    }

    /**
     * Resources
     */

    private static final String EDITFAVORITES_ACTIONNAME = ResourcesUtil.getString("favorites.action");
    private static final String EDITFAVORITES_TOOLTIP = ResourcesUtil.getString("favorites.tooltip");
    private static final String EDITFAVORITES_NAME = ResourcesUtil.getString("favorites.name");
    private static final String EDITFAVORITES_URL = ResourcesUtil.getString("favorites.url");
    private static final String EDITFAVORITES_TITLE = ResourcesUtil.getString("favorites.title");

    private static final Icon EDITSIGNATURE = ResourcesUtil.getIcon("favorites.edit");
}
