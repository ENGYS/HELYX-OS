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
package eu.engys.util.filechooser.gui;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.connection.SshParameters;
import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.filechooser.favorites.Favorite;
import eu.engys.util.filechooser.util.HelyxFileFilter;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.filechooser.util.TaskContext;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;

public class FileChooserController {

    private static final Logger logger = LoggerFactory.getLogger(FileChooserController.class);
    public static final String MULTI_SELECTION_ENABLED_CHANGED_PROPERTY = "MultiSelectionEnabledChangedProperty";
    public static final String MULTI_SELECTION_MODE_CHANGED_PROPERTY = "SelectionModeChangedProperty";

    private URIPanel uriPanel;
    private FavoritesPanel favoritesPanel;
    private FileSystemPanel fileSystemPanel;

    private TaskContext taskContext;

    private FileChooserPanel chooserPanel;
    private SelectionMode selectionMode = SelectionMode.DIRS_AND_FILES;
    private SshParameters sshParameters;
    private boolean saveAs;
    private ButtonsPanel buttonsPanel;

    public FileChooserController(SshParameters sshParameters) {
        this.sshParameters = sshParameters;
    }

    public void setBrowser(FileChooserPanel vfsBrowser) {
        this.chooserPanel = vfsBrowser;
        this.uriPanel = chooserPanel.getUriPanel();
        this.favoritesPanel = chooserPanel.getFavoritesPanel();
        this.fileSystemPanel = chooserPanel.getFileSystemPanel();
        this.buttonsPanel = chooserPanel.getButtonsPanel();
        // this.breadCrumbsPanel = chooserPanel.getBreadCrumbsPanel();
    }

    public void refreshLocation(FileObject fileObject) {
        try {
            fileObject.refresh();
            goToURL(fileObject);
        } catch (FileSystemException e) {
            logger.error("Can't refresh location", e.getMessage());
        }
    }

    public void goToURL(final String url, boolean encoded) {
        String encodedURL = encoded ? url : VFSUtils.encode(url, sshParameters);
        try {
            FileObject resolveFileObject = VFSUtils.resolveFileObject(encodedURL, sshParameters);
            goToURL(resolveFileObject);
        } catch (FileSystemException e) {
            VFSUtils.showErrorMessage(chooserPanel, encodedURL, e);
        }
    }

    public void goToURL(final FileObject fileObject) {
        try {
            fileObject.refresh();
        } catch (FileSystemException e) {
            logger.error("Could not refresh " + fileObject.getName().getFriendlyURI());
        }
        if (taskContext != null) {
            taskContext.setStop(true);
        }
        final FileObject[] files = VFSUtils.getFiles(chooserPanel, fileObject);
        taskContext = new TaskContext(BROWSER_CHECKINGSFTPLINKSTASK, files.length);
        taskContext.setIndeterminate(false);
        VFSUtils.checkForSftpLinks(files, taskContext);
        taskContext.setStop(true);

        ExecUtil.invokeLater(new Runnable() {

            @Override
            public void run() {
                FileObject[] fileObjectsWithParent = files;
                fileSystemPanel.setContent(fileObjectsWithParent);
                uriPanel.setFileObject(fileObject);
                fileSystemPanel.resetFilter();
            }
        });
        updateOkButton();
    }

    public void addFavorite(Favorite favorite) {
        favoritesPanel.addFavorite(favorite);
    }

    public FileObject getSelectedFileObject() {
        FileObject[] fos = getSelectedFileObjects();
        if (fos != null && fos.length > 0) {
            return fos[0];
        } else {
            return null;
        }
    }

    public FileObject[] getSelectedFileObjects() {
        return chooserPanel.getSelectedFileObjects();
    }

    public FileSystemPanel getFileSystemPanel() {
        return fileSystemPanel;
    }

    public void resetFileFilter() {
        if (buttonsPanel != null) {
            buttonsPanel.resetFileFilter();
        }
    }

    public URIPanel getUriPanel() {
        return uriPanel;
    }

    public static Throwable getRootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

    public void applyFilter() {
        HelyxFileFilter filter = buttonsPanel.getSelectedFilter();
        if (filter != null) {
            fileSystemPanel.applyFilter(filter);
        }
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode mode) {
        this.selectionMode = mode;
    }

    public void showTable() {
        if (chooserPanel != null) {
            chooserPanel.showTable();
        }
    }

    public void showLoading() {
        if (chooserPanel != null) {
            chooserPanel.showLoading();
        }
    }

    public void updateNewFileName() {
        if (uriPanel != null) {
            uriPanel.updateFileName();
        }
    }

    public void updateOkButton() {
        if (buttonsPanel != null) {
            buttonsPanel.updateOkButton();
        }
    }

    public void closeAndReturn(ReturnValue retVal) {
        chooserPanel.closeAndReturn(retVal);
    }

    public HelyxFileFilter getFilter() {
        return buttonsPanel.getSelectedFilter();
    }

    public void fixSelection() {
        // fileSystemPanel.fixSelection();
    }

    public SshParameters getSshParameters() {
        return sshParameters;
    }

    public boolean isRemote() {
        return getSshParameters() != null;
    }

    public boolean isSaveAs() {
        return saveAs;
    }

    public void setSaveAs(boolean saveAs) {
        this.saveAs = saveAs;
    }

    /*
     * For tests purposes only
     */
    public FavoritesPanel getFavoritesPanel() {
        return favoritesPanel;
    }

    public void setFavoritesPanel(FavoritesPanel favoritesPanel) {
        this.favoritesPanel = favoritesPanel;
    }

    public void setFileSystemPanel(FileSystemPanel fileSystemPanel) {
        this.fileSystemPanel = fileSystemPanel;
    }

    public void setUriPanel(URIPanel uriPanel) {
        this.uriPanel = uriPanel;
    }

    /*
     * Resources
     */

    private static final String BROWSER_CHECKINGSFTPLINKSTASK = ResourcesUtil.getString("browser.checkingSFtpLinksTask");

}
