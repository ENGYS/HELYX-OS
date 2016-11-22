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
package eu.engys.core.executor;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.PDFFileFilter;
import eu.engys.util.PrefUtil;
import eu.engys.util.TempFolder;
import eu.engys.util.Util;
import eu.engys.util.ui.UiUtil;

public class FileManagerSupport {

    private static final Logger logger = LoggerFactory.getLogger(FileManagerSupport.class);

    private static final String ACTION_NAME = "Open File Manager";

    public static void openPDF(File parent, String key) {
        logger.debug("Looking for PDF file with key {} inside {}", key, parent.getAbsolutePath());
        Collection<File> files = FileUtils.listFiles(parent, new PDFFileFilter(key), null);
        if (files.size() == 1) {
            open(files.iterator().next());
        } else {
            boolean emptyDocumentation = (files.size() == 0);
            UiUtil.showDocumentationNotLoadedWarning(emptyDocumentation);
        }
    }

    public static void open(File file) {
        if (Util.isWindows()) {
            // Desktop.getDesktop().open(file) on windows may hang
        	_openWindows(file);
        } else {
            openOnLinux(file);
        }
    }

    private static void openOnLinux(File file) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.OPEN)) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    _openLinux(file);
                }
            } else {
                _openLinux(file);
            }
        } else {
            _openLinux(file);
        }
    }

    private static void _openLinux(File file) {
        File inFolder = TempFolder.get();
        Executor.command(getOpenCommand(file), getOpenCommandArguments(file)).inFolder(inFolder).description(ACTION_NAME).exec();
    }

    /*
     * If you try to open multiple files (e.g. parser log files) just one of the files is opened.
     * The command rundll32.exe is not blocking therefore I can call execAndWait
     */
    private static void _openWindows(File file) {
    	File inFolder = TempFolder.get();
    	Executor.command(getOpenCommand(file), getOpenCommandArguments(file)).inFolder(inFolder).description(ACTION_NAME).execAndWait();
    }

    private static String getOpenCommand(File file) {
        String command;
        if (Util.isWindows()) {
            if (file.isDirectory()) {
                command = "explorer";
            } else {
                command = "rundll32.exe";
            }
        } else {
            if (file.isDirectory()) {
                command = getLinuxFileManager();
            } else {
                command = getLinuxFileOpener();
            }
        }
        return command;
    }

    private static String[] getOpenCommandArguments(File file) {
        List<String> command = new LinkedList<>();
        if (Util.isWindows() && !file.isDirectory()) {
            command.add("url.dll,FileProtocolHandler");
        }
        command.add(file.getAbsolutePath());
        return command.toArray(new String[0]);
    }

    private static String getLinuxFileManager() {
        String preferredFileManager = PrefUtil.getString(PrefUtil.HELYX_DEFAULT_FILE_MANAGER);
        if (preferredFileManager.isEmpty()) {
            if (checkTerminal("nautilus")) {
                return "nautilus";
            } else if (checkTerminal("dolphin")) {
                return "dolphin";
            } else if (checkTerminal("konqueror")) {
                return "konqueror";
            } else if (checkTerminal("thunar")) {
                return "thunar";
            } else {
                logger.error("No file manager found");
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "No File Manager Found", "File System error", JOptionPane.ERROR_MESSAGE);
                return "";
            }
        } else {
            return preferredFileManager;
        }
    }

    private static String getLinuxFileOpener() {
        String preferredFileOpener = PrefUtil.getString(PrefUtil.HELYX_DEFAULT_FILE_OPENER);
        if (preferredFileOpener.isEmpty()) {
            if (checkTerminal("gnome-open")) {
                return "gnome-open";
            } else if (checkTerminal("xdg-open")) {
                return "xdg-open";
            } else if (checkTerminal("kde-open")) {
                return "kde-open";
            } else {
                logger.error("No command available to open the default application, containing folder will be opened instead");
                return getLinuxFileManager();
            }
        } else {
            return preferredFileOpener;
        }
    }

    private static boolean checkTerminal(String terminal) {
        try {
            new ProcessBuilder(terminal, "--help").start().waitFor();
            // System.out.println("RunScript.checkTerminal() "+terminal+" OK");
            return true;
        } catch (InterruptedException | IOException e) {
            // System.out.println("RunScript.checkTerminal() "+terminal+" NO");
            return false;
        }
    }

}
