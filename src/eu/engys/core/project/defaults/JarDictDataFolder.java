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

package eu.engys.core.project.defaults;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.ArchiveUtils;
import eu.engys.util.TempFolder;

public class JarDictDataFolder implements DictDataFolder {

	private static final Logger logger = LoggerFactory.getLogger(JarDictDataFolder.class);
	
	private File dictDataFolder;
	private String applicationFolder;
	
	@Inject
	public JarDictDataFolder(@Named("Application") String applicationFolder) throws IOException {
		this.applicationFolder = applicationFolder;
	}
    
    @Override
    public File toFile() {
        return dictDataFolder;
    }
	
	private String getRootPath() {
		URL appJarURL = JarDictDataFolder.class.getProtectionDomain().getCodeSource().getLocation();
		File appJarFile;
		try {
			appJarFile = new File(appJarURL.toURI());
		} catch (URISyntaxException e) {
			appJarFile = new File(appJarURL.getPath());
		}
		return appJarFile.getParentFile().getParent();
	}

	public File getFile(String fileName) {
		if (dictDataFolder == null) {
			extractDictData();
		}
		return new File(dictDataFolder, fileName);
	}

	public void extractDictData() {
		extractToTemp();
	}

	public void extractToTemp() {
		this.dictDataFolder = TempFolder.get("dictData", applicationFolder);
		if (dictDataFolder.exists()) {
		    FileUtils.deleteQuietly(dictDataFolder);
		}
		logger.info("Extract to temp");
		dictDataFolder.mkdirs();
		Path pathToLibFile = Paths.get(getRootPath(), "lib", applicationFolder+"-data.jar");
		if (Files.exists(pathToLibFile)) {
		    logger.info("Extract to temp: File is {}", pathToLibFile);
		    ArchiveUtils.unzip(pathToLibFile.toFile(), dictDataFolder.getParentFile());
		} else {
		    logger.info("Extract to temp: File {} not found", pathToLibFile);
		    Path pathToDistFile = Paths.get(getRootPath(), "dist", applicationFolder+"-data.jar");
		    if (Files.exists(pathToDistFile)){ 
		        logger.info("Extract to temp: File is {}", pathToDistFile);
		        ArchiveUtils.unzip(pathToDistFile.toFile(), dictDataFolder.getParentFile());
		    } else {
		        logger.info("Extract to temp: File {} not found", pathToDistFile);
		    }
		}
	}

//	public void extractToRoot() {
//		this.dictDataFolder = new File(new File(getRootPath(), "dictData"), applicationFolder);
//		if (!dictDataFolder.exists()) {
//			logger.warn("Extract to root");
//			dictDataFolder.mkdirs();
//			Path path = Paths.get(getRootPath(), "lib", applicationFolder+"-data.jar");
//			IOUtils.unzip(dictDataFolder.getParentFile(), path.toFile());
//		}
//	}
	
}
