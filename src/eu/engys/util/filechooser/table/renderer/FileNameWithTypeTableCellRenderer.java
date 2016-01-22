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

package eu.engys.util.filechooser.table.renderer;

import static eu.engys.util.ui.FileChooserUtils.EXCEL_EXTENSION_NEW;
import static eu.engys.util.ui.FileChooserUtils.EXCEL_EXTENSION_OLD;
import static eu.engys.util.ui.FileChooserUtils.PDF_EXTENSION;

import java.awt.Component;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileType;

import eu.engys.util.ApplicationInfo;
import eu.engys.util.filechooser.table.FileNameWithType;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;

public class FileNameWithTypeTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        FileNameWithType fileNameWithType = (FileNameWithType) value;
        FileName fileName = fileNameWithType.getFileName();
        label.setText(fileName.getBaseName());
        label.setToolTipText(fileName.getPath());

        FileType fileType = fileNameWithType.getFileType();
        Icon icon = null;
        if (FileType.FOLDER.equals(fileType)) {
            String decodePath = VFSUtils.decode(fileName.getURI(), null);
            File file = decodePath == null ? null : new File(decodePath);
            if (isSuitable(file)) {
                label.setText(label.getText());
                if (ApplicationInfo.getVendor() != null) {
                    icon = ResourcesUtil.getIcon(ApplicationInfo.getVendor().toLowerCase() + ".case");
                }
            } else {
                icon = FOLDEROPEN;
            }
        } else if (VFSUtils.isArchive(fileName)) {
            if ("jar".equalsIgnoreCase(fileName.getExtension())) {
                icon = JARICON;
            } else {
                icon = FOLDERZIPPER;
            }
        } else if (FileType.FILE.equals(fileType)) {
            if (PDF_EXTENSION.equalsIgnoreCase(fileName.getExtension())) {
                icon = PDF_ICON;
            } else if (EXCEL_EXTENSION_OLD.equalsIgnoreCase(fileName.getExtension()) || EXCEL_EXTENSION_NEW.equalsIgnoreCase(fileName.getExtension())) {
                icon = EXCEL_ICON;
            } else {
                icon = FILE;
            }
        } else if (FileType.IMAGINARY.equals(fileType)) {
            icon = SHORTCUT;
        }
        label.setIcon(icon);
        return label;
    }

    private boolean isSuitable(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            File constant = new File(file, "constant");
            File system = new File(file, "system");
            if (constant.exists() && constant.isDirectory() && system.exists() && system.isDirectory()) {
                File controlDict = new File(system, "controlDict");
                return controlDict.exists();
            }
            return false;
        }
        return false;
    }

    /**
     * Resources
     */

    private static final Icon FILE = ResourcesUtil.getIcon("file");
    private static final Icon JARICON = ResourcesUtil.getIcon("jarIcon");
    private static final Icon SHORTCUT = ResourcesUtil.getIcon("shortCut");
    private static final Icon FOLDEROPEN = ResourcesUtil.getIcon("folderOpen");
    private static final Icon FOLDERZIPPER = ResourcesUtil.getIcon("folderZipper");

    private static final Icon PDF_ICON = ResourcesUtil.getIcon("file.pdf");
    private static final Icon EXCEL_ICON = ResourcesUtil.getIcon("file.excel");

}
