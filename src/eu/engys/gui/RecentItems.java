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
package eu.engys.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.engys.util.PrefUtil;

public class RecentItems {

    public interface RecentItemsObserver {
        void onRecentItemChange(RecentItems src);
    }

    public final static String RECENT_ITEM_STRING = "recent.item.";

    static final int MAX_ITEMS = 5;

    public static final String NO_ITEMS = "No recent files";

    private List<String> items = new ArrayList<>();
    private List<RecentItemsObserver> m_observers = new ArrayList<RecentItemsObserver>();

    private static RecentItems instance;

    public static RecentItems getInstance() {
        if (instance == null) {
            instance = new RecentItems();
        }
        return instance;
    }
    
    public RecentItems() {
        loadFromPreferences();
    }

    public void push(File item) {
        items.remove(item.getAbsolutePath());
        items.add(0, item.getAbsolutePath());

        if (items.size() > MAX_ITEMS) {
            items.remove(items.size() - 1);
        }

        update();
        storeToPreferences();
    }

    public void remove(Object item) {
        items.remove(item);
        update();
        storeToPreferences();
    }

    public List<String> getItems() {
        return items;
    }

    public int size() {
        return items.size();
    }

    public void addObserver(RecentItemsObserver observer) {
        m_observers.add(observer);
        update();
    }

    public void removeObserver(RecentItemsObserver observer) {
        m_observers.remove(observer);
    }

    private void update() {
        for (RecentItemsObserver observer : m_observers) {
            observer.onRecentItemChange(this);
        }
    }

    void loadFromPreferences() {
        // load recent files from properties
        String recentItems = PrefUtil.getString(PrefUtil.RECENT_PROJECTS, "");
        for (String item : recentItems.split(File.pathSeparator)) {
            File file = new File(item);
            if (file.exists()) {
                items.add(item);
            }
        }
    }

    void storeToPreferences() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < MAX_ITEMS; i++) {
            if (i < items.size()) {
                list.add(items.get(i));
            }
        }
        PrefUtil.putString(PrefUtil.RECENT_PROJECTS, StringUtils.join(list, File.pathSeparator));
    }

    public void clear() {
        items.clear();
        storeToPreferences();
    }
}
