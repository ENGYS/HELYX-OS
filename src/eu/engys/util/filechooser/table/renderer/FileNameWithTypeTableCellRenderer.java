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

package eu.engys.util.filechooser.table.renderer;

import static eu.engys.util.ui.FileChooserUtils.EXCEL_EXTENSION_NEW;
import static eu.engys.util.ui.FileChooserUtils.EXCEL_EXTENSION_OLD;
import static eu.engys.util.ui.FileChooserUtils.PDF_EXTENSION;

import java.awt.Component;
import java.io.File;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
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

		if (value != null) {
			FileNameWithType fileNameWithType = (FileNameWithType) value;
			FileName fileName = fileNameWithType.getFileName();
			label.setText(fileName.getBaseName());
			label.setToolTipText(fileName.getPath());

			FileType fileType = fileNameWithType.getFileType();
			Icon icon = null;
			if (FileType.FOLDER.equals(fileType)) {
				String decodePath = VFSUtils.decode(fileName.getURI(), null);
				File file = decodePath == null ? null : new File(decodePath);
				if (isSuitableCase(file)) {
					label.setText(label.getText());
					if (ApplicationInfo.getVendor() != null) {
						icon = ResourcesUtil.getIcon(ApplicationInfo.getVendor().toLowerCase() + ".case");
					}
				} else if (isSuitableStudy(file)) {
					label.setText(label.getText());
					if (ApplicationInfo.getVendor() != null) {
						icon = ResourcesUtil.getIcon(ApplicationInfo.getVendor().toLowerCase() + ".study");
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
		}

		return label;
	}

	private boolean isSuitableCase(File file) {
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

	private boolean isSuitableStudy(File file) {
		if (file != null && file.exists() && file.isDirectory()) {
			Collection<File> listFiles = FileUtils.listFiles(file, FileFilterUtils.suffixFileFilter(".std"), null);
			return listFiles.size() == 1;
		}
		return false;
	}

	/**
	 * Resources
	 */

	private static final Icon FILE = ResourcesUtil.getIcon("file.icon");
	private static final Icon JARICON = ResourcesUtil.getIcon("jar.icon");
	private static final Icon SHORTCUT = ResourcesUtil.getIcon("shortcut.icon");
	private static final Icon FOLDEROPEN = ResourcesUtil.getIcon("folder.icon");
	private static final Icon FOLDERZIPPER = ResourcesUtil.getIcon("archive.icon");

	private static final Icon PDF_ICON = ResourcesUtil.getIcon("file.pdf");
	private static final Icon EXCEL_ICON = ResourcesUtil.getIcon("file.excel");

}
