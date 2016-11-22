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

import java.net.URL;
import java.util.List;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.NameScope;
import org.apache.commons.vfs2.operations.FileOperations;

public class FileObjectWrapper implements FileObject {
    protected FileObject parent;

    public FileObjectWrapper(FileObject parent) {
        super();
        this.parent = parent;
    }

    public FileName getName() {
        return parent.getName();
    }

    public URL getURL() throws FileSystemException {
        return parent.getURL();
    }

    public boolean exists() throws FileSystemException {
        return parent.exists();
    }

    public boolean isHidden() throws FileSystemException {
        return parent.isHidden();
    }

    public boolean isReadable() throws FileSystemException {
        return parent.isReadable();
    }

    public boolean isWriteable() throws FileSystemException {
        return parent.isWriteable();
    }

    public FileType getType() throws FileSystemException {
        return parent.getType();
    }

    public FileObject getParent() throws FileSystemException {
        return parent.getParent();
    }

    public FileSystem getFileSystem() {
        return parent.getFileSystem();
    }

    public FileObject[] getChildren() throws FileSystemException {
        return parent.getChildren();
    }

    public FileObject getChild(String name) throws FileSystemException {
        return parent.getChild(name);
    }

    public FileObject resolveFile(String name, NameScope scope) throws FileSystemException {
        return parent.resolveFile(name, scope);
    }

    public FileObject resolveFile(String path) throws FileSystemException {
        return parent.resolveFile(path);
    }

    public FileObject[] findFiles(FileSelector selector) throws FileSystemException {
        return parent.findFiles(selector);
    }

    public void findFiles(FileSelector selector, boolean depthwise, List<FileObject> selected) throws FileSystemException {
        parent.findFiles(selector, depthwise, selected);
    }

    public boolean delete() throws FileSystemException {
        return parent.delete();
    }

    public int delete(FileSelector selector) throws FileSystemException {
        return parent.delete(selector);
    }

    public void createFolder() throws FileSystemException {
        parent.createFolder();
    }

    public void createFile() throws FileSystemException {
        parent.createFile();
    }

    public void copyFrom(FileObject srcFile, FileSelector selector) throws FileSystemException {
        parent.copyFrom(srcFile, selector);
    }

    public void moveTo(FileObject destFile) throws FileSystemException {
        parent.moveTo(destFile);
    }

    public boolean canRenameTo(FileObject newfile) {
        return parent.canRenameTo(newfile);
    }

    public FileContent getContent() throws FileSystemException {
        return parent.getContent();
    }

    public void close() throws FileSystemException {
        parent.close();
    }

    public void refresh() throws FileSystemException {
        parent.refresh();
    }

    public boolean isAttached() {
        return parent.isAttached();
    }

    public boolean isContentOpen() {
        return parent.isContentOpen();
    }

    public FileOperations getFileOperations() throws FileSystemException {
        return parent.getFileOperations();
    }
}
