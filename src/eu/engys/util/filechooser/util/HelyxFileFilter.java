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
package eu.engys.util.filechooser.util;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.FilenameUtils;

public class HelyxFileFilter {

	private static final String[] ALL_FILES_FILTER_EXTENSIONS = new String[] { "*" };
	public static final String ALL_FILES_FILTER_DESCRIPTION = "All Files (*.*)";
	private final String description;
	private String[] extensions;
	private static HelyxFileFilter allFilesFilter = new HelyxFileFilter(ALL_FILES_FILTER_DESCRIPTION, ALL_FILES_FILTER_EXTENSIONS);

	/**
	 * Example: Fluent File (*.msh, *.cas)", "msh", "cas"
	 */
	public HelyxFileFilter(String description, String... extensions) {
		this.description = description;
		this.extensions = extensions;
	}

	public static HelyxFileFilter getAllFilesFilter() {
		return allFilesFilter;
	}

	public boolean isAllFilesFilter() {
		return ALL_FILES_FILTER_DESCRIPTION.equals(description) && Arrays.equals(ALL_FILES_FILTER_EXTENSIONS, extensions);
	}

	public String getDescription() {
		return description;
	}
	
	public String[] getExtensions() {
		return extensions;
	}

	public boolean isValidExtension(String extensionToCheck) {
		for (String ext : extensions) {
			if (ext.equalsIgnoreCase(extensionToCheck)) {
				return true;
			}
		}
		return false;
	}

	public boolean accepts(File file) {
		if (extensions == null) {
			return false;
		}
		if (isAllFilesFilter()) {
			return true;
		}
		String fileExtension = FilenameUtils.getExtension(file.getName());
		return isValidExtension(fileExtension);
	}

}
