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

package eu.engys.util.filechooser.favorites;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.PrefUtil;
import eu.engys.util.Util;
import eu.engys.util.filechooser.favorites.Favorite.Type;

public class FavoritesUtils {

    private static final Logger logger = LoggerFactory.getLogger(FavoritesUtils.class);

    public static final String HOME = "Home";
    public static final String DESKTOP = "Desktop";
    public static final String DOCUMENTS = "Documents";
    private static final String FAVORITES_PREFERENCE_DELIMITER = "@@@";
    private static final String FAVORITE_NAME_URL_DELIMITER = "##";

    public static final File HOME_DIRECTORY = new File(System.getProperty("user.home"));

    public static final File VFS_JFC_CONFIG_DIRECTORY = new File(HOME_DIRECTORY, ".vfsjfilechooser");
    public static final File VFS_JFC_BOOKMARKS_FILE = new File(VFS_JFC_CONFIG_DIRECTORY, "favorites.xml");

    public List<Favorite> loadFavorite() {
        return new ArrayList<Favorite>();
    }

    public static List<Favorite> loadFavorites() {
        List<Favorite> list = new ArrayList<Favorite>();
        String favoritesString = PrefUtil.getString(PrefUtil.FAVORITES_KEY);
        if (!favoritesString.isEmpty()) {
            String[] favorites = favoritesString.split(FAVORITES_PREFERENCE_DELIMITER);
            for (int i = 0; i < favorites.length; i++) {
                String fav = favorites[i];
                if (!fav.isEmpty()) {
                    String[] nameAndURL = fav.split(FAVORITE_NAME_URL_DELIMITER);
                    if (nameAndURL.length == 2) {
                        String name = nameAndURL[0];
                        String url = nameAndURL[1];
                        list.add(new Favorite(name, url, Type.USER));
                    }
                }
            }
        }
        return list;
    }

    public static void saveFavorites(List<Favorite> favoriteList) {
        StringBuffer favoritesString = new StringBuffer();
        for (Favorite favorite : favoriteList) {
            if (favorite.getType().equals(Type.USER)) {
                favoritesString.append(favorite.getName());
                favoritesString.append(FAVORITE_NAME_URL_DELIMITER);
                favoritesString.append(favorite.getUrl());
                favoritesString.append(FAVORITES_PREFERENCE_DELIMITER);
            }
        }
        PrefUtil.putString(PrefUtil.FAVORITES_KEY, favoritesString.toString());
    }

    public static List<Favorite> loadSystemLocations() {
        List<Favorite> list = new ArrayList<Favorite>();
        File[] listRoots = File.listRoots();
        for (File file : listRoots) {
            list.add(new Favorite(file.getAbsolutePath(), file.getAbsolutePath(), Favorite.Type.SYSTEM));
        }
        File userHome = new File(System.getProperty("user.home"));
        File desktop = null;
        File documents = null;
        if (Util.isWindows()) {
            desktop = FileSystemView.getFileSystemView().getHomeDirectory();
            documents = FileSystemView.getFileSystemView().getDefaultDirectory();
        } else {
            desktop = new File(userHome, "Desktop");
            documents = new File(userHome, "Documents");
        }

        list.add(new Favorite(HOME, userHome.getAbsolutePath(), Favorite.Type.SYSTEM));
        if (desktop.exists()) {
            list.add(new Favorite(DESKTOP, desktop.getAbsolutePath(), Favorite.Type.SYSTEM));
        }
        if (documents.exists()) {
            list.add(new Favorite(DOCUMENTS, documents.getAbsolutePath(), Favorite.Type.SYSTEM));
        }
        return list;
    }
}
