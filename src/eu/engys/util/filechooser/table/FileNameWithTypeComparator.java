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

package eu.engys.util.filechooser.table;

import java.util.Comparator;

import javax.swing.SortOrder;

import org.apache.commons.vfs2.FileType;

import eu.engys.util.filechooser.ParentFileObject;

public class FileNameWithTypeComparator implements Comparator<FileNameWithType> {
    private SortOrder sortOrder = SortOrder.ASCENDING;

    @Override
    public int compare(FileNameWithType o1, FileNameWithType o2) {
        return compareTo(o1, o2);
    }

    public int compareTo(FileNameWithType o1, FileNameWithType o2) {
        if (o1 == null || o1.getFileType() == null || o1.getFileName() == null) {
            return -1;
        }
        if (o2 == null || o2.getFileType() == null || o2.getFileName() == null) {
            return 1;
        }
        // folders first first
        boolean folder1 = FileType.FOLDER.equals(o1.getFileType());
        boolean folder2 = FileType.FOLDER.equals(o2.getFileType());
        int result = 0;

        int sortOrderSign = SortOrder.ASCENDING.equals(sortOrder) ? 1 : -1;
        String o1BaseName = o1.getFileName().getBaseName();
        String o2BaseName = o2.getFileName().getBaseName();

        if (o1BaseName.equalsIgnoreCase(ParentFileObject.PARENT_NAME)) {
            result = -1 * sortOrderSign;
        } else {
            if (o2BaseName.equalsIgnoreCase(ParentFileObject.PARENT_NAME)) {
                result = 1 * sortOrderSign;
            } else if (folder1 & !folder2) {
                result = -1 * sortOrderSign;
            } else if (!folder1 & folder2) {
                result = 1 * sortOrderSign;
            } else {
                result = o1BaseName.compareToIgnoreCase(o2BaseName);
            }
        }

        return result;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }
}
