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

package eu.engys.util.filechooser;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.connection.SshParameters;
import eu.engys.util.filechooser.gui.BrowserFactory;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.UiUtil;

public class RemoteFileChooser extends AbstractFileChooser {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteFileChooser.class);

    private SshParameters sshParameters;

    public RemoteFileChooser(SshParameters sshParameters) {
        super();
        this.sshParameters = sshParameters;
    }

    public RemoteFileChooser(SshParameters sshParameters, String initialPath) {
        this.initialPath = encodePath(initialPath);
    }

    public ReturnValue showOpenRemoteDialogConnectionTested(final ProgressMonitor progressMonitor) {
        this.panel = BrowserFactory.createOpenRemoteBrowser(this, sshParameters);
        return initializeAndShow(getDimension(null));
    }

    public ReturnValue showOpenRemoteDialog(final ProgressMonitor progressMonitor) {
        Boolean retVal = UiUtil.testConnection(sshParameters, progressMonitor);
        if (!retVal) {
            return ReturnValue.Cancelled;
        }
        this.panel = BrowserFactory.createOpenRemoteBrowser(this, sshParameters);
        return initializeAndShow(getDimension(null));
    }

    public FileObject getSelectedFileObject() {
        FileObject[] files = getSelectedFileObjects();
        if (files != null && files.length > 0) {
            return files[0];
        }
        return null;
    }

    public FileObject[] getSelectedFileObjects() {
        return panel.getFileObjects();
    }

    private String encodePath(String filePath) {
        try {
            String encodePath = VFSUtils.encode(filePath, sshParameters);
            FileObject fileObject = VFSUtils.resolveFileObject(encodePath, sshParameters);
            if (fileObject != null) {
                return fileObject.getName().getPath();
            } else {
                return "";
            }
        } catch (FileSystemException e1) {
            LOGGER.error("Cannot resolve: " + filePath + ". " + e1.getMessage());
            return "";
        }
    }

}
