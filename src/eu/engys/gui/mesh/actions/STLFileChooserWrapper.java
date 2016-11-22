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
package eu.engys.gui.mesh.actions;

import static eu.engys.util.ui.FileChooserUtils.STL_FILTER;

import java.awt.HeadlessException;
import java.io.File;

import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.util.PrefUtil;
import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.filechooser.HelyxFileChooser;
import eu.engys.util.filechooser.util.HelyxFileFilter;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.ui.UiUtil;

public class STLFileChooserWrapper {

    private STLAccessory stlAccessory;
    private HelyxFileChooser chooser;
    public static final HelyxFileFilter ARCHIVE_FILTER = new HelyxFileFilter("Archive File (*.zip, *.gz, *.tar.gz, *tar.bz2)", "zip", "gz", "bz2");

    public STLFileChooserWrapper(boolean multiSelectionEnabled) {
        chooser = new HelyxFileChooser(PrefUtil.getWorkDir(PrefUtil.LAST_IMPORT_DIR).getAbsolutePath());
        chooser.setTitle("Open STL");
        chooser.setSelectionMode(SelectionMode.FILES_ONLY);
        chooser.setMultiSelectionEnabled(multiSelectionEnabled);
        stlAccessory = new STLAccessory(chooser);
    }

    public ReturnValue showOpenDialog() throws HeadlessException {
        ReturnValue returnedValue = chooser.showOpenDialog(stlAccessory, UiUtil.getPreferredScreenSize(), STL_FILTER, ARCHIVE_FILTER);
        if (returnedValue.isApprove()) {
            final File[] files = getSelectedFiles();
            if (files != null && files.length > 0) {
                PrefUtil.putFile(PrefUtil.LAST_IMPORT_DIR, files[0].getParentFile());
            }
        }
        return returnedValue;
    }

    public File[] getSelectedFiles() {
        if (chooser == null)
            return new File[0];
        return chooser.getSelectedFiles();
    }

    public File getSelectedFile() {
        if (chooser == null)
            return null;
        return chooser.getSelectedFile();
    }

    public AffineTransform[] getSelectedTransform() {
        return stlAccessory.getTransformations();
    }

    public STLAccessory getStlAccessory() {
        return stlAccessory;
    }
}
