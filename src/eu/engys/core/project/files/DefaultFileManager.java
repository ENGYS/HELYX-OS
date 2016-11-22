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

package eu.engys.core.project.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.Util;

public class DefaultFileManager implements FileManager {

	private static final Logger logger = LoggerFactory.getLogger(DefaultFileManager.class);

	private File file;

	public DefaultFileManager(File file) {
		setFile(file);
	}

	private void setFile(File file) {
		this.file = file;
		if (!file.exists()) {
			logger.warn("-> New Folder {}", file);
			file.mkdirs();
		}
	}

	public File getFile() {
		return file;
	}

	public File getFile(String fileName) {
		return new File(file, fileName);
	}

	public File newFile(String fileName) {
		return new File(file, fileName);
	}

	public File copyHere(File source, String newName, boolean overwrite) {
		String name = Util.replaceForbiddenCharacters(newName);
		File target;
		if (overwrite) {
			target = newFile(name);
		} else {
			target = getACopy(name);
		}

		if (target.equals(source))
			return target;

		try {
		    FileUtils.copyFile(source, target);
			logger.info("File {} copied to {}", source, target);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Error copying", e);
		}

		return target;
	}

	// public File moveHere(File file, boolean overwrite) {
	// Path source = file.toPath();
	// Path target = file.toPath().resolve(file.getName());
	// try {
	// Files.move(source, target);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return target.toFile();
	// }

	public void rename(String oldFileName, String newFileName) {
		Path source = file.toPath().resolve(oldFileName);
		try {
			Files.move(source, source.resolveSibling(newFileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String[] list() {
		return file.list();
	}

	private File getACopy(String name) {
		String finalName = name;
		String finalNameNoExtension = FilenameUtils.removeExtension(finalName);

		int counter = 0;
		File file;
		while ((file = newFile(finalName)).exists()) {
			finalName = finalNameNoExtension + (counter++) + ".stl";
		}

		return file;
	}

	public void deleteAll() {
		FileUtils.deleteQuietly(file);
		file.mkdir();
	}
}
