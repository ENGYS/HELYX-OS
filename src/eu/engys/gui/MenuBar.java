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

import static eu.engys.gui.view.View.EXIT;
import static eu.engys.gui.view.View.NEW_CASE;
import static eu.engys.gui.view.View.OPEN_CASE;
import static eu.engys.gui.view.View.SAVE_AS_CASE;
import static eu.engys.gui.view.View.SAVE_CASE;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import eu.engys.core.controller.Controller.OpenMode;
import eu.engys.core.controller.OpenOptions;
import eu.engys.core.executor.FileManagerSupport;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.gui.RecentItems.RecentItemsObserver;
import eu.engys.gui.view.View;
import eu.engys.launcher.StartUpMonitor;
import eu.engys.util.ApplicationInfo;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;

public class MenuBar extends JMenuBar implements RecentItemsObserver {

    public static final String FILE_MENU = "File";
    public static final String EDIT_MENU = "Edit";
    public static final String DICTIONARIES_MENU = "Dictionaries";
    public static final String HELP_MENU = "Help";

    private static final String OPEN_RECENT_MENU = "Open Recent";

    public static final Icon OPEN_ICON = ResourcesUtil.getIcon("application.open.icon");
    public static final Icon FILE_ICON = ResourcesUtil.getIcon("file.icon");

    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu dictionariesMenu;
    private JMenu helpMenu;
    // private JMenu viewMenu;
    private JMenu recentCasesMenu;

    private View view;

    public MenuBar(View view) {
        super();
        this.view = view;
        StartUpMonitor.info("Loading Menu Bar");
        fileMenu = new JMenu(FILE_MENU);

        if (ActionManager.getInstance().contains(NEW_CASE)) {
            fileMenu.add(ActionManager.getInstance().get(NEW_CASE));
        }
        fileMenu.add(ActionManager.getInstance().get(OPEN_CASE));

        recentCasesMenu = new JMenu(OPEN_RECENT_MENU);
        recentCasesMenu.setIcon(OPEN_ICON);
        fileMenu.add(recentCasesMenu);

        if (ActionManager.getInstance().contains(SAVE_CASE)) {
            fileMenu.add(ActionManager.getInstance().get(SAVE_CASE));
        }
        if (ActionManager.getInstance().contains(SAVE_AS_CASE)) {
            fileMenu.add(ActionManager.getInstance().get(SAVE_AS_CASE));
        }
        if (ActionManager.getInstance().contains(EXIT)) {
            fileMenu.addSeparator();
            fileMenu.add(ActionManager.getInstance().get(EXIT));
        }

        editMenu = new JMenu(EDIT_MENU);
        editMenu.setName("Application Edit");

        dictionariesMenu = new JMenu(DICTIONARIES_MENU);

        helpMenu = new JMenu(HELP_MENU);

        add(fileMenu);
        add(editMenu);
//        add(dictionariesMenu);
        add(helpMenu);
        
        RecentItems.getInstance().addObserver(this);
    }

    public JMenu getFileMenu() {
        return fileMenu;
    }

    public JMenu getEditMenu() {
        return editMenu;
    }

    public JMenu getDictionariesMenu() {
        return dictionariesMenu;
    }

    public JMenu getHelpMenu() {
        return helpMenu;
    }

    @Override
    public void onRecentItemChange(RecentItems src) {
        recentCasesMenu.removeAll();
        List<String> items = RecentItems.getInstance().getItems();
        if (items.isEmpty()) {
            JMenuItem menuItem = new JMenuItem(RecentItems.NO_ITEMS);
            menuItem.setEnabled(false);
            recentCasesMenu.add(menuItem);
        } else {
            Icon CASE_ICON = ResourcesUtil.getIcon(ApplicationInfo.getVendor().toLowerCase() + ".case");
            for (final String item : items) {
                recentCasesMenu.add(new AbstractAction(item, CASE_ICON) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (view.getController().allowActionsOnRunning(false)) {
                            view.getController().openCase(OpenOptions.file(new File(item), OpenMode.CHECK_FOLDER_ASK_USER));
                        }
                    }
                });
            }
            recentCasesMenu.addSeparator();
            recentCasesMenu.add(new AbstractAction("Clear") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    RecentItems.getInstance().clear();
                }
            });
        }
    }

    public void updateDictionariesList(final Model model) {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                try{
                    view.getMenuBar().getDictionariesMenu().removeAll();
                    if (model.hasProject()) {
                        File baseDir = model.getProject().getBaseDir();
                        IOFileFilter fileFilter = FileFilterUtils.or(FileFilterUtils.suffixFileFilter("Dict"), FileFilterUtils.suffixFileFilter("Properties"), FileFilterUtils.prefixFileFilter("fv"));
                        List<File> files = new ArrayList<File>(FileUtils.listFiles(baseDir, fileFilter, FileFilterUtils.directoryFileFilter()));
                        Collections.sort(files, NameFileComparator.NAME_INSENSITIVE_COMPARATOR);
                        for (final File file : files) {
                            JMenuItem menuItem = new JMenuItem(new AbstractAction(file.getName(), FILE_ICON) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    FileManagerSupport.open(file);
                                }
                            });
                            menuItem.setToolTipText(file.getAbsolutePath());
                            view.getMenuBar().getDictionariesMenu().add(menuItem);
                        }
                    }
                } catch(Exception e){
                }
            }
        });
    }
}
