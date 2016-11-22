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
package eu.engys.parallelworks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import eu.engys.core.project.system.ControlDict;

public class ParallelWorksCleaner {

    public static void cleanBaseDir(File baseDir) {
        List<File> tempFiles = new ArrayList<File>(FileUtils.listFiles(baseDir, FileFilterUtils.prefixFileFilter("_"), null));
        for (File f : tempFiles) {
            FileUtils.deleteQuietly(f);
        }

        List<File> supportFiles = new ArrayList<File>(FileUtils.listFiles(baseDir, new String[] { "tgz", "gz", "job", "py", "pyc", "sh", "env" }, false));
        for (File f : supportFiles) {
            FileUtils.deleteQuietly(f);
        }
        
        FileUtils.deleteQuietly(new File(baseDir, ControlDict.CONTROL_DICT));
    }

}
