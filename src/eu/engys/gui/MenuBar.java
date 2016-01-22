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

package eu.engys.gui;

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

    public static final Icon OPEN_ICON = ResourcesUtil.getIcon("application.open.icon");
    public static final Icon FILE_ICON = ResourcesUtil.getIcon("file");

    private JMenu fileMenu;
    private JMenu editMenu;
    // private JMenu dictionariesMenu;
    private JMenu helpMenu;
    // private JMenu viewMenu;
    private JMenu recentCasesMenu;

    private View view;

    public MenuBar(View view) {
        super();
        this.view = view;
        StartUpMonitor.info("Loading Menu Bar");
        fileMenu = new JMenu("File");
        fileMenu.add(ActionManager.getInstance().get("application.create"));
        fileMenu.add(ActionManager.getInstance().get("application.open"));

        recentCasesMenu = new JMenu("Open Recent");
        recentCasesMenu.setIcon(OPEN_ICON);
        fileMenu.add(recentCasesMenu);

        fileMenu.add(ActionManager.getInstance().get("application.save"));
        fileMenu.add(ActionManager.getInstance().get("application.saveAs"));
        fileMenu.addSeparator();
        fileMenu.add(ActionManager.getInstance().get("application.exit"));

        editMenu = new JMenu("Edit");
        editMenu.setName("Application Edit");

        // dictionariesMenu = new JMenu("Dictionaries");

        helpMenu = new JMenu("Help");

        add(fileMenu);
        add(editMenu);
        // add(dictionariesMenu);
        add(helpMenu);
        
        RecentItems.getInstance().addObserver(this);
    }

    public JMenu getFileMenu() {
        return fileMenu;
    }

    public JMenu getEditMenu() {
        return editMenu;
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
                            view.getController().openCase(new File(item));
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
                // dictionariesMenu.removeAll();
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
                        // dictionariesMenu.add(menuItem);
                    }
                }
            }
        });
    }
}
