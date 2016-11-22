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

import java.util.Arrays;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.ui.ResourcesUtil;

public class FileSystemTableModel extends AbstractTableModel {

    private static final String MODEL_NAME = ResourcesUtil.getString("model.name");
    private static final String MODEL_SIZE = ResourcesUtil.getString("model.size");
    private static final String MODEL_TYPE = ResourcesUtil.getString("model.type");
    private static final String MODEL_DATELASTMOD = ResourcesUtil.getString("model.dateLastMod");

    public static final int COLUMN_NAME = 0;
    protected static final int COLUMN_SIZE = 1;
    protected static final int COLUMN_TYPE = 2;
    protected static final int COLUMN_LAST_MOD_DATE = 3;
    private static final String[] COLUMN_NAMES = new String[] { MODEL_NAME, MODEL_SIZE, MODEL_TYPE, MODEL_DATELASTMOD };
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemTableModel.class);

    private FileObject[] fileObjects = new FileObject[0];
    private FileObjectComparator fileObjectComparator = new FileObjectComparator();

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public int getRowCount() {
        return fileObjects.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FileObject fileObject = fileObjects[rowIndex];
        boolean isFile = false;
        try {
            isFile = FileType.FILE.equals(fileObject.getType());
        } catch (FileSystemException e1) {
            LOGGER.warn("Can't check file type " + fileObject.getName().getBaseName(), e1);
        }
        if (columnIndex == COLUMN_NAME) {
            try {
                return new FileNameWithType(fileObject.getName(), fileObject.getType());
            } catch (FileSystemException e) {
                return new FileNameWithType(fileObject.getName(), null);
            }
        } else if (columnIndex == COLUMN_TYPE) {
            try {
                return fileObject.getType().getName();
            } catch (FileSystemException e) {
                LOGGER.warn("Can't get file type " + fileObject.getName().getBaseName(), e);
                return "?";
            }
        } else if (columnIndex == COLUMN_SIZE) {
            try {
                long size = -1;
                if (isFile) {
                    size = fileObject.getContent().getSize();
                }
                return new FileSize(size);
            } catch (FileSystemException e) {
                LOGGER.warn("Can't get size " + fileObject.getName().getBaseName(), e);
                return new FileSize(-1);
            }
        } else if (columnIndex == COLUMN_LAST_MOD_DATE) {
            try {

                long lastModifiedTime = fileObject.getContent().getLastModifiedTime();
                return new Date(lastModifiedTime);
            } catch (FileSystemException e) {
                LOGGER.warn("Can't get last mod date " + fileObject.getName().getBaseName(), e);
                return null;
            }
        }
        return "?";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COLUMN_NAME) {
            return FileNameWithType.class;
        } else if (columnIndex == COLUMN_TYPE) {
            return FileType.class;
        } else if (columnIndex == COLUMN_SIZE) {
            return FileSize.class;
        } else if (columnIndex == COLUMN_LAST_MOD_DATE) {
            return Date.class;
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    public void setContent(FileObject... fileObjects) {
        this.fileObjects = fileObjects;
        Arrays.sort(fileObjects, fileObjectComparator);
        fireTableDataChanged();
    }

    public FileObject[] getContent() {
        return fileObjects;
    }

    public FileObject get(int row) {
        return fileObjects[row];
    }

    public int getIndexByName(String nameToSelect) {
        for (int i = 0; i < fileObjects.length; i++) {
            String foName = fileObjects[i].getName().getBaseName();
            if (nameToSelect.equals(foName)) {
                return i;
            }
        }
        return -1;
    }

}
