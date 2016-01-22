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

package eu.engys.util.filechooser.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import eu.engys.util.filechooser.actions.favorite.EditFavorite;
import eu.engys.util.filechooser.actions.favorite.OpenFavorite;
import eu.engys.util.filechooser.favorites.Favorite;
import eu.engys.util.filechooser.favorites.FavoritesUtils;
import eu.engys.util.filechooser.favorites.PopupListener;
import eu.engys.util.filechooser.favorites.list.MutableListDragListener;
import eu.engys.util.filechooser.favorites.list.MutableListDropHandler;
import eu.engys.util.filechooser.favorites.list.MutableListModel;
import eu.engys.util.filechooser.favorites.list.SelectFirstElementFocusAdapter;
import eu.engys.util.filechooser.favorites.renderer.FavoriteListCellRenderer;
import eu.engys.util.filechooser.util.VFSUtils.LocationType;
import eu.engys.util.ui.FileChooserUtils;
import eu.engys.util.ui.ResourcesUtil;

public class FavoritesPanel extends JPanel {

    public static final String NAME = "chooser.favoritespanel";
    public static final String FAVORITES_PANEL = "favoritesPanel";
    public static final String SYSTEM_PANEL = "systemPanel";
    public static final String FAVORITES_USER_LIST = "favoritesUserList";
    public static final String FAVORITES_SYSTEM_LIST = "favoriteSystemList";

    private static final Color BG_COLOR = new Color(250, 250, 250);

    private static final String ACTION_OPEN = "OPEN";
    private static final String ACTION_DELETE = "DELETE";
    private static final String ACTION_EDIT = "EDIT";

    private final FileChooserController controller;

    private List<Favorite> userFavorites;

    private MutableListModel<Favorite> systemListModel;
    private MutableListModel<Favorite> userListModel;

    public FavoritesPanel(FileChooserController controller) {
        super(new GridLayout(controller.isRemote() ? 1 : 2, 1, 0, 10));
        setName(NAME);
        this.controller = controller;
        
        layoutComponents();
        load();
    }


    private void layoutComponents() {
        addSystemLocationPanel();
        addUserFavoritesPanel();
    }

    private void load() {
        loadSystemLocations();
        loadFavorites();
    }
    
    @SuppressWarnings("unchecked")
    private void addSystemLocationPanel() {
        systemListModel = new MutableListModel<Favorite>();

        JList<Favorite> favoriteSystemList = new JList<Favorite>(systemListModel);
        favoriteSystemList.setName(FAVORITES_SYSTEM_LIST);
        favoriteSystemList.setCellRenderer(new FavoriteListCellRenderer());
        favoriteSystemList.addFocusListener(new SelectFirstElementFocusAdapter());

        addOpenActionToList(favoriteSystemList);
        addPopupMenu(favoriteSystemList, ACTION_OPEN);

        JLabel systemLocationLabel = createLabelWithIcon(FAVORITES_SYSTEMLOCATIONS, COMPUTER_ICON);

        if (!controller.isRemote()) {
            JPanel systemPanel = new JPanel(new BorderLayout());
            systemPanel.setName(SYSTEM_PANEL);
            systemPanel.add(systemLocationLabel, BorderLayout.NORTH);
            systemPanel.add(favoriteSystemList, BorderLayout.CENTER);
            JScrollPane comp = new JScrollPane(systemPanel);
            add(comp);
        }
    }

    private void addUserFavoritesPanel() {
        userListModel = createListModel();

        JList<Favorite> favoritesUserList = createList();
        favoritesUserList.setName(FAVORITES_USER_LIST);
        favoritesUserList.setCellRenderer(new FavoriteListCellRenderer());
        favoritesUserList.addFocusListener(new SelectFirstElementFocusAdapter());

        addOpenActionToList(favoritesUserList);
        addEditActionToList(favoritesUserList, userListModel);
        addPopupMenu(favoritesUserList, ACTION_OPEN, ACTION_EDIT, ACTION_DELETE);

        JLabel userFavouritesLabel = createLabelWithIcon(FAVORITES_FAVORITES, STAR);

        JPanel favoritesPanel = new JPanel(new BorderLayout());
        favoritesPanel.setName(FAVORITES_PANEL);
        favoritesPanel.add(userFavouritesLabel, BorderLayout.NORTH);
        favoritesPanel.add(favoritesUserList, BorderLayout.CENTER);

        add(new JScrollPane(favoritesPanel));
    }
    
    private void loadSystemLocations() {
        List<Favorite> systemLocations = FavoritesUtils.loadSystemLocations();
        for (Favorite favorite : systemLocations) {
            systemListModel.add(favorite);
        }
    }

    private void loadFavorites() {
        this.userFavorites = FavoritesUtils.loadFavorites();
        for (Favorite favorite : userFavorites) {
            if (isValidFavorite(favorite.getUrl(), controller)) {
                userListModel.add(favorite);
            }
        }
    }

    private boolean isValidFavorite(String url, FileChooserController controller) {
        if (controller.isRemote()) {
            String host = controller.getSshParameters().getHost();
            String port = String.valueOf(controller.getSshParameters().getPort());
            String typePrefix = LocationType.sftp.toString();
            if(port.equals(FileChooserUtils.DEFAULT_SSH_PORT)){
                return url.startsWith(typePrefix + host);
            } else {
                return url.startsWith(typePrefix + host + ":" + port);
            }
        } else {
            return url.startsWith(LocationType.file.toString());
        }
    }

    public void addFavorite(Favorite favorite) {
        userFavorites.add(favorite);
        userListModel.add(favorite);
    }

    @SuppressWarnings("unchecked")
    private JList<Favorite> createList() {
        final JList<Favorite> favoritesUserList = new JList<Favorite>(userListModel);
        favoritesUserList.setTransferHandler(new MutableListDropHandler(favoritesUserList));
        new MutableListDragListener(favoritesUserList);
        favoritesUserList.getActionMap().put(ACTION_DELETE, new AbstractAction("Delete", MINUSBUTTON) {

            @Override
            public void actionPerformed(ActionEvent e) {
                Favorite favorite = userListModel.getElementAt(favoritesUserList.getSelectedIndex());
                if (!Favorite.Type.USER.equals(favorite.getType())) {
                    return;
                }
                userFavorites.remove(favoritesUserList.getSelectedValue());
                userListModel.remove(favoritesUserList.getSelectedIndex());
            }
        });
        InputMap favoritesListInputMap = favoritesUserList.getInputMap(JComponent.WHEN_FOCUSED);
        favoritesListInputMap.put(KeyStroke.getKeyStroke("DELETE"), ACTION_DELETE);
        return favoritesUserList;
    }

    private MutableListModel<Favorite> createListModel() {
        final MutableListModel<Favorite> favoritesUserListModel = new MutableListModel<Favorite>();
        favoritesUserListModel.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                saveFavorites();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                saveFavorites();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                saveFavorites();
            }

            protected void saveFavorites() {
                FavoritesUtils.saveFavorites(userFavorites);
            }
        });
        return favoritesUserListModel;
    }

    private JPopupMenu addPopupMenu(JList<?> list, String... actions) {
        JPopupMenu favoritesPopupMenu = new JPopupMenu();
        for (String action : actions) {
            JMenuItem item = favoritesPopupMenu.add(list.getActionMap().get(action));
            item.setName(action);
        }
        list.addKeyListener(new PopupListener(favoritesPopupMenu));
        list.addMouseListener(new PopupListener(favoritesPopupMenu));
        return favoritesPopupMenu;
    }

    private JLabel createLabelWithIcon(String text, Icon icon) {
        JLabel label = new JLabel(text, icon, SwingConstants.CENTER);
        Font font = label.getFont();
        label.setFont(font.deriveFont(Font.BOLD));
        Border lineBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, BG_COLOR.darker());
        Border emptyBorder = BorderFactory.createEmptyBorder(2, 0, 2, 0);
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(lineBorder, emptyBorder);

        label.setBorder(compoundBorder);
        return label;
    }

    private void addOpenActionToList(final JList<Favorite> favoritesList) {
        favoritesList.getActionMap().put(ACTION_OPEN, new OpenFavorite(controller, favoritesList));
        favoritesList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                boolean isLeftButton = e.getButton() == MouseEvent.BUTTON1;
                boolean isSingleClick = e.getClickCount() == 1;
                if (isSingleClick && isLeftButton) {
                    favoritesList.getActionMap().get(ACTION_OPEN).actionPerformed(null);
                }
            }
        });
        InputMap favoritesListInputMap = favoritesList.getInputMap(JComponent.WHEN_FOCUSED);
        favoritesListInputMap.put(KeyStroke.getKeyStroke("ENTER"), ACTION_OPEN);
    }

    private void addEditActionToList(JList<Favorite> favoritesList, MutableListModel<Favorite> listModel) {
        favoritesList.getActionMap().put(ACTION_EDIT, new EditFavorite(controller, favoritesList, listModel));

        InputMap favoritesListInputMap = favoritesList.getInputMap(JComponent.WHEN_FOCUSED);
        favoritesListInputMap.put(KeyStroke.getKeyStroke("F2"), ACTION_EDIT);
    }

    /**
     * Resources
     */

    private static final String FAVORITES_SYSTEMLOCATIONS = ResourcesUtil.getString("favorites.systemLocations");
    private static final String FAVORITES_FAVORITES = ResourcesUtil.getString("favorites.favorites");

    private static final Icon COMPUTER_ICON = ResourcesUtil.getIcon("computer");
    private static final Icon STAR = ResourcesUtil.getIcon("star");
    private static final Icon MINUSBUTTON = ResourcesUtil.getIcon("minusButton");
}
