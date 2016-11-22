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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.cache.SoftRefFilesCache;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.connection.SshParameters;
import eu.engys.util.filechooser.AbstractFileChooser;
import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.filechooser.authentication.MemoryAuthStore;
import eu.engys.util.filechooser.authentication.UserAuthenticatorFactory;
import eu.engys.util.filechooser.authentication.authenticator.UseCentralsFromSessionUserAuthenticator;
import eu.engys.util.filechooser.util.EngysFileSystemManager;
import eu.engys.util.filechooser.util.HelyxFileFilter;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ExecUtil;

public class FileChooserPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(FileChooserPanel.class);
    public static final String MULTI_SELECTION_ENABLED_CHANGED_PROPERTY = "MultiSelectionEnabledChangedProperty";
    public static final String MULTI_SELECTION_MODE_CHANGED_PROPERTY = "SelectionModeChangedProperty";

    private static final String TABLE_KEY = "TABLE";
    private static final String LOADING_KEY = "LOADING";

    private CardLayout cardLayout;

    private JPanel cardLayoutPanel;
    private URIPanel uriPanel;
    private FileSystemPanel fileSystemPanel;
    private FavoritesPanel favoritesPanel;
    private LoadingPanel loadingPanel;
    private ButtonsPanel buttonsPanel;

    private FileChooserController controller;
    private final AbstractFileChooser chooser;

    private Accessory accessory;
    private Options options;
    private SshParameters sshParameters;
    private HelyxFileFilter[] filters;
    private boolean enableSaveAs;
    private File fileToSelect;

    FileChooserPanel(AbstractFileChooser chooser, Accessory accessory, Options options, SshParameters sshParameters, boolean enableSaveAs, HelyxFileFilter... filters) {
        super(new BorderLayout());
        setName("filechooser.panel");
        this.chooser = chooser;
        this.accessory = accessory;
        this.options = options;
        this.sshParameters = sshParameters;
        this.enableSaveAs = enableSaveAs;
        this.filters = filters;

        setLogLevelToWarning(SoftRefFilesCache.class);
        setLogLevelToWarning(EngysFileSystemManager.class);
        setLogLevelToWarning(MemoryAuthStore.class);
        setLogLevelToWarning(UseCentralsFromSessionUserAuthenticator.class);
        setLogLevelToWarning(UserAuthenticatorFactory.class);
    }

    public void layoutComponents() {
        this.controller = new FileChooserController(sshParameters);
        this.controller.setSaveAs(enableSaveAs);
        this.uriPanel = new URIPanel(controller);
        this.favoritesPanel = new FavoritesPanel(controller);
        this.fileSystemPanel = new FileSystemPanel(controller, accessory);
        this.loadingPanel = new LoadingPanel();
        this.buttonsPanel = new ButtonsPanel(controller, filters);
        this.controller.setBrowser(this);

        this.cardLayoutPanel = new JPanel(cardLayout = new CardLayout());
        cardLayoutPanel.add(new JScrollPane(loadingPanel), LOADING_KEY);

        if (accessory != null) {
            JSplitPane fileSystemAndPreviewPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileSystemPanel, accessory.getPanel());
            fileSystemAndPreviewPane.setOneTouchExpandable(false);
            fileSystemAndPreviewPane.setDividerLocation(450);
            cardLayoutPanel.add(fileSystemAndPreviewPane, TABLE_KEY);
        } else {
            cardLayoutPanel.add(fileSystemPanel, TABLE_KEY);
        }

        JPanel centralPanel = new JPanel(new BorderLayout());
        centralPanel.add(cardLayoutPanel, BorderLayout.CENTER);

        if (options != null) {
            centralPanel.add(options.getPanel(), BorderLayout.SOUTH);
        }

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, favoritesPanel, centralPanel);
        mainSplitPane.setOneTouchExpandable(false);
        mainSplitPane.setDividerLocation(180);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(uriPanel, BorderLayout.NORTH);
        mainPanel.add(mainSplitPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(mainPanel, BorderLayout.CENTER);
    }

    public void initialize(final String initialPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                showLoading();
                initializeInAThread(initialPath);
            }
        }).start();
    }

    private void initializeInAThread(final String initialPath) {
        try {
            boolean thereIsAValidFileToSelect = fileToSelect != null && fileToSelect.getParent() != null;
            if (thereIsAValidFileToSelect) {
                goToURL(fileToSelect.getParent());
            } else {
                goToURL(initialPath);
            }

            showTable();

            controller.applyFilter();

            if (thereIsAValidFileToSelect) {
                selectFileOnTable();
            }

        } catch (FileSystemException e1) {
            logger.error("Can't initialize default location", e1.getMessage());
        }
    }

    private void goToURL(final String initialPath) throws FileSystemException {
        if (initialPath != null && !initialPath.isEmpty()) {
            controller.goToURL(initialPath, false);
        } else {
            if (controller.isRemote()) {
                controller.goToURL(VFSUtils.getRemoteUserHome(sshParameters), false);
            } else {
                controller.goToURL(VFSUtils.getUserHome());
            }
        }
    }

    private void selectFileOnTable() {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                fileSystemPanel.selectFileByName(fileToSelect.getName());
            }
        });
    }

    public void showTable() {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                loadingPanel.stop();
                fileSystemPanel.resetScroll();
                cardLayout.show(cardLayoutPanel, TABLE_KEY);
            }
        });
    }

    public void showLoading() {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                cardLayout.show(cardLayoutPanel, LOADING_KEY);
                loadingPanel.start();
            }
        });
    }

    // Called from outside
    public FileObject[] getFileObjects() {
        if (controller.isSaveAs()) {
            FileObject[] fos = new FileObject[1];
            try {
                String newFileName = uriPanel.getNewFileName();
                boolean hasFilter = controller.getFilter() != null && !controller.getFilter().isAllFilesFilter();
                if (hasFilter) {
                    newFileName = fixFileExtension(newFileName);
                }
                fos[0] = uriPanel.getFileObject().resolveFile(newFileName);
                return fos;
            } catch (FileSystemException e) {
                return null;
            }
        } else {
            FileObject[] selectedFileObjects = getSelectedFileObjects();
            if (selectedFileObjects == null) {
                selectedFileObjects = new FileObject[] { uriPanel.getFileObject() };
            }
            return selectedFileObjects;
        }
    }

    private String fixFileExtension(String newFileName) {
        String extension = FilenameUtils.getExtension(newFileName);
        if (!controller.getFilter().isValidExtension(extension)) {
            return newFileName += "." + controller.getFilter().getExtensions()[0];
        }
        return newFileName;
    }

    FileObject[] getSelectedFileObjects() {
        FileObject[] selectedFileObjects = fileSystemPanel.getSelectedFileObjects();
        if (selectedFileObjects != null && selectedFileObjects.length > 0) {
            return selectedFileObjects;
        } else {
            return null;
        }
    }

    public static void setLogLevelToWarning(Class klass) {
        org.apache.log4j.Logger.getLogger(klass).setLevel(Level.WARN);
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        controller.setSelectionMode(selectionMode);
    }

    public void setMultiSelectionEnabled(boolean b) {
        fileSystemPanel.setMultiSelection(b);
    }

    public void setSelectedFile(File fileToSelect) {
        this.fileToSelect = fileToSelect;
    }

    public FileChooserController getController() {
        return controller;
    }

    public URIPanel getUriPanel() {
        return uriPanel;
    }

    public FavoritesPanel getFavoritesPanel() {
        return favoritesPanel;
    }

    public ButtonsPanel getButtonsPanel() {
        return buttonsPanel;
    }

    public FileSystemPanel getFileSystemPanel() {
        return fileSystemPanel;
    }

    public LoadingPanel getLoadingPanel() {
        return loadingPanel;
    }

    public ButtonsPanel getStatusPanel() {
        return buttonsPanel;
    }

    public JButton getOkButton() {
        return buttonsPanel.getOkButton();
    }

    public HelyxFileFilter getSelectedFilter() {
        return buttonsPanel.getSelectedFilter();
    }

    public Accessory getAccessory() {
        return accessory;
    }

    public void closeAndReturn(ReturnValue retVal) {
        chooser.setReturnValue(retVal);
        chooser.disposeDialog();
    }

}
