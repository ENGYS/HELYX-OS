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

package eu.engys.util.filechooser.table;

import java.util.Comparator;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileObjectComparator implements Comparator<FileObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileObjectComparator.class);
    private FileNameWithTypeComparator fileNameWithTypeComparator = new FileNameWithTypeComparator();

    @Override
    public int compare(FileObject o1, FileObject o2) {
        if (o1 != null && o2 != null) {
            try {
                return fileNameWithTypeComparator.compare(new FileNameWithType(o1.getName(), o1.getType()), new FileNameWithType(o2.getName(), o2.getType()));
            } catch (FileSystemException e) {
                return 0;
            }
        }
        return 0;
    }

    private int compareTypes(FileType type1, FileType type2) {
        if (type1.equals(FileType.FILE) && !type2.equals(FileType.FILE)) {
            return 1;
        } else if (!type1.equals(FileType.FILE) && type2.equals(FileType.FILE)) {
            return -1;
        }
        return 0;
    }

}
