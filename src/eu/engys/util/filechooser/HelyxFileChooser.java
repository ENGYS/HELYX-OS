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

package eu.engys.util.filechooser;

import java.awt.Dimension;
import java.io.File;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;

import eu.engys.util.filechooser.gui.Accessory;
import eu.engys.util.filechooser.gui.BrowserFactory;
import eu.engys.util.filechooser.gui.Options;
import eu.engys.util.filechooser.util.HelyxFileFilter;
import eu.engys.util.filechooser.util.VFSUtils;

public class HelyxFileChooser extends AbstractFileChooser {

    public HelyxFileChooser() {
        super();
    }

    public HelyxFileChooser(String initialPath) {
        super(initialPath);
    }

    public ReturnValue showOpenDialog() {
        this.panel = BrowserFactory.createOpenBrowser(this);
        return initializeAndShow(getDimension(null));
    }

    public ReturnValue showOpenDialog(Dimension d) {
        this.panel = BrowserFactory.createOpenBrowser(this);
        return initializeAndShow(getDimension(d));
    }

    public ReturnValue showOpenDialog(HelyxFileFilter... filters) {
        this.panel = BrowserFactory.createOpenBrowser(this, filters);
        return initializeAndShow(getDimension(null));
    }

    public ReturnValue showOpenDialog(Accessory accessory) {
        this.panel = BrowserFactory.createOpenBrowser(this, accessory);
        return initializeAndShow(getDimension(null));
    }

    public ReturnValue showOpenDialog(Options options) {
        this.panel = BrowserFactory.createOpenBrowser(this, options);
        return initializeAndShow(getDimension(null));
    }

    public ReturnValue showOpenDialog(Accessory accessory, Dimension d, HelyxFileFilter... filters) {
        this.panel = BrowserFactory.createOpenBrowser(this, accessory, filters);
        return initializeAndShow(getDimension(d));
    }

    public ReturnValue showSaveAsDialog() {
        this.panel = BrowserFactory.createSaveAsBrowser(this);
        return initializeAndShow(getDimension(null));
    }

    public ReturnValue showSaveAsDialog(HelyxFileFilter... filters) {
        this.panel = BrowserFactory.createSaveAsBrowser(this, filters);
        return initializeAndShow(getDimension(null));
    }

    public File getSelectedFile() {
        File[] files = getSelectedFiles();
        if (files != null && files.length > 0) {
            return files[0];
        }
        return null;
    }

    public File[] getSelectedFiles() {
        FileObject[] selectedFileObjects = panel.getFileObjects();
        if (selectedFileObjects == null) {
            return null;
        } else {
            File[] selectedFiles = new File[selectedFileObjects.length];
            for (int i = 0; i < selectedFileObjects.length; i++) {
                FileName name = selectedFileObjects[i].getName();
                selectedFiles[i] = new File(VFSUtils.decode(name.getURI(), null));
            }
            return selectedFiles;
        }
    }

    @Override
    protected String ensureValidInitialPath(String pathToCheck) {
        if (pathToCheck == null || new File(pathToCheck).exists()) {
            return pathToCheck;
        } else {
            File parentFile = new File(pathToCheck).getParentFile();
            if (parentFile == null) {
                return null;
            } else if (parentFile.exists()) {
                return parentFile.getAbsolutePath();
            } else {
                return ensureValidInitialPath(parentFile.getAbsolutePath());
            }
        }
    }

    @Override
    protected File ensureValidFileToSelect(File fileToCheck) {
        if (fileToCheck == null)
            return null;
        String path = ensureValidInitialPath(fileToCheck.getAbsolutePath());
        return path != null ? new File(path) : null;
    }

    // public static void main(String[] args) {
    // JFrame f = UiUtil.defaultTestFrame("a", new JButton(new AbstractAction("open") {
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // HelyxFileChooser fileChooser = new HelyxFileChooser("C:\\");
    // fileChooser.showOpenDialog();
    // }
    // }));
    // f.setSize(200, 200);
    // f.setVisible(true);
    // }
}
