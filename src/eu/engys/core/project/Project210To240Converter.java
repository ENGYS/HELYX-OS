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
package eu.engys.core.project;

import static eu.engys.core.project.system.ProjectDict.PROJECT_DICT;
import static eu.engys.core.project.system.RunDict.RUN_DICT;

import java.io.File;

import org.apache.commons.io.FileUtils;

import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.project.system.ProjectDict;
import eu.engys.core.project.system.RunDict;

public class Project210To240Converter {

    private openFOAMProject project;

    public Project210To240Converter(openFOAMProject project) {
        this.project = project;
    }

    public void convert() {
        fixLogFolder();
        fixRunDict();
    }

    private void fixRunDict() {
        File runDictFile = project.getSystemFolder().getFileManager().getFile(RUN_DICT);
        if (runDictFile.exists()) {
            RunDict runDict = new RunDict(runDictFile);
            
            File projectDictFile = project.getSystemFolder().getFileManager().getFile(PROJECT_DICT);
            if (!projectDictFile.exists()) {
                ProjectDict projectDict = new ProjectDict();
                projectDict.setRunDict(runDict);
                DictionaryUtils.writeDictionary(project.getSystemFolder().getFileManager().getFile(), projectDict, null);
            }
            FileUtils.deleteQuietly(runDictFile);
        }
    }

    public void fixLogFolder() {
        File logFolder = new File(project.getBaseDir(), openFOAMProject.LOG);
        if (!logFolder.exists()) {
            logFolder.mkdir();
        } else if (logFolder.isFile()) {
            logFolder.renameTo(new File(project.getBaseDir(), openFOAMProject.LOG + ".old"));
            logFolder.mkdir();
        }        
    }
}
