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

package eu.engys.util.filechooser.depot;
///*
// * Copyright 2012 Krzysztof Otrebski (krzysztof.otrebski@gmail.com)
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package eu.engys.util.otrosfilechooser.panels;
//
//import java.awt.BorderLayout;
//import java.awt.CardLayout;
//import java.awt.Color;
//import java.awt.FlowLayout;
//import java.text.MessageFormat;
//import java.util.List;
//
//import javax.swing.Action;
//import javax.swing.BorderFactory;
//import javax.swing.Icon;
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JProgressBar;
//import javax.swing.JScrollPane;
//import javax.swing.JSplitPane;
//import javax.swing.JTable;
//import javax.swing.JTextField;
//import javax.swing.ListSelectionModel;
//import javax.swing.SwingWorker;
//import javax.swing.event.ListSelectionEvent;
//import javax.swing.event.ListSelectionListener;
//
//import org.apache.commons.vfs2.FileObject;
//import org.apache.commons.vfs2.FileSystemException;
//import org.apache.commons.vfs2.FileType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import eu.engys.util.otrosfilechooser.FileChooserSelectionChangedListener;
//import eu.engys.util.otrosfilechooser.ParentFileObject;
//import eu.engys.util.otrosfilechooser.favorites.Favorite;
//import eu.engys.util.otrosfilechooser.preview.PreviewListener;
//import eu.engys.util.otrosfilechooser.util.SelectionMode;
//import eu.engys.util.otrosfilechooser.util.SwingUtils;
//import eu.engys.util.otrosfilechooser.util.TaskContext;
//import eu.engys.util.otrosfilechooser.util.VFSUtils;
//import eu.engys.util.ui.ResourcesUtil;
//
//public class OriginalVfsBrowser extends JPanel {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(OriginalVfsBrowser.class);
//	public static final String MULTI_SELECTION_ENABLED_CHANGED_PROPERTY = "MultiSelectionEnabledChangedProperty";
//	public static final String MULTI_SELECTION_MODE_CHANGED_PROPERTY = "SelectionModeChangedProperty";
//
//	private static final String TABLE_KEY = "TABLE";
//	private static final String LOADING_KEY = "LOADING";
//
//	protected JPanel centralPanel;
//
//	private JLabel statusLabel;
//
//	private FileObject currentLocation;
//	private CardLayout cardLayout;
//
//	private SelectionMode selectionMode = SelectionMode.DIRS_AND_FILES;
//	private Action actionApproveDelegate;
//	private Action actionCancelDelegate;
//
//	private boolean multiSelectionEnabled = false;
//	private JButton actionApproveButton;
//	private JButton actionCancelButton;
//	private TaskContext taskContext;
//	private URIPanel uriPanel;
//	private FileSystemPanel fileSystemPanel;
//
//	private FavoritesPanel favoritesPanel;
//
//	private LoadingPanel loadingPanel;
//
//	public OriginalVfsBrowser() {
//		this(System.getProperty("user.home"));
//	}
//
//	public OriginalVfsBrowser(String initialPath) {
//		this(initialPath, null);
//	}
//
//	public OriginalVfsBrowser(String initialPath, JPanel rightPanel) {
//		this(initialPath, rightPanel, false);
//	}
//
//	public OriginalVfsBrowser(String initialPath, JPanel rightPanel, boolean remote) {
//		this(initialPath, rightPanel, new String[0], remote);
//	}
//
//	public OriginalVfsBrowser(String initialPath, JPanel rightPanel, String[] filter, boolean remote) {
//		this(initialPath, rightPanel, new String[0], remote, false, "");
//	}
//
//	public OriginalVfsBrowser(String initialPath, JPanel rightPanel, String[] filter, boolean remote, boolean saveas, String currentProjectName) {
//		super(new BorderLayout());
//		layoutComponents(initialPath, rightPanel, filter, remote, saveas, currentProjectName);
//		VFSUtils.loadAuthStore();
//	}
//
//	private void layoutComponents(final String initialPath, final JPanel rightPanel, String[] filter, boolean remote, boolean saveAs, String currentProjectName) {
//		fileSystemPanel = new FileSystemPanel(this, filter);
//
//		favoritesPanel = new FavoritesPanel(this);
//
//		JSplitPane fileSystemAndPreviewPane = null;
//		if (rightPanel != null) {
//			if (rightPanel instanceof PreviewPanel) {
//				fileSystemPanel.getTable().getSelectionModel().addListSelectionListener(new PreviewListener(this, (PreviewPanel) rightPanel));
//			} else if (rightPanel instanceof FileChooserSelectionChangedListener) {
//				fileSystemPanel.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//
//					@Override
//					public void valueChanged(ListSelectionEvent e) {
//						((FileChooserSelectionChangedListener) rightPanel).onSelectionChanged();
//					}
//				});
//			}
//			fileSystemAndPreviewPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileSystemPanel, rightPanel);
//			fileSystemAndPreviewPane.setOneTouchExpandable(false);
//			fileSystemAndPreviewPane.setDividerLocation(350);
//
//		}
//
//		loadingPanel = new LoadingPanel(taskContext);
//
//		centralPanel = new JPanel(cardLayout = new CardLayout());
//		centralPanel.add(loadingPanel, LOADING_KEY);
//		if (fileSystemAndPreviewPane != null) {
//			centralPanel.add(fileSystemAndPreviewPane, TABLE_KEY);
//		} else {
//			centralPanel.add(fileSystemPanel, TABLE_KEY);
//		}
//
//		showTable();
//
//		uriPanel = new URIPanel(this, remote);
//
//		JSplitPane centralSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(favoritesPanel), centralPanel);
//		centralSplitPane.setOneTouchExpandable(false);
//		centralSplitPane.setDividerLocation(180);
//
//		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
//		mainPanel.add(uriPanel, BorderLayout.NORTH);
//		mainPanel.add(centralSplitPane, BorderLayout.CENTER);
//		mainPanel.add(createSouthPanel(saveAs, currentProjectName), BorderLayout.SOUTH);
//
//		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//		this.add(mainPanel, BorderLayout.CENTER);
//
//		postLayout(initialPath);
//	}
//
//	private void postLayout(final String initialPath) {
//		try {
//			selectionChanged();
//		} catch (FileSystemException e) {
//			LOGGER.error("Can't initialize default selection mode", e);
//		}
//		try {
//			if (initialPath == null) {
//				goToUrl(VFSUtils.getUserHome());
//			} else {
//				goToUrl(initialPath);
//			}
//		} catch (FileSystemException e1) {
//			LOGGER.error("Can't initialize default location", e1);
//		}
//	}
//
//	private JPanel createSouthPanel(boolean saveas, String currentProjectName) {
//		JPanel southPanel = new JPanel(new BorderLayout());
//
//		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//
//		actionApproveButton = new JButton(actionApproveDelegate);
//		actionCancelButton = new JButton(actionCancelDelegate);
//
//		leftPanel.add(statusLabel = new JLabel());
//		if (saveas) {
//			rightPanel.add(new JLabel("Name:"));
//			newFileNameField = new JTextField(15);
//			newFileNameField.setText(currentProjectName + "_copy");
//			rightPanel.add(newFileNameField);
//		}
//		rightPanel.add(actionApproveButton);
//		rightPanel.add(actionCancelButton);
//
//		southPanel.add(leftPanel, BorderLayout.WEST);
//		southPanel.add(rightPanel, BorderLayout.CENTER);
//
//		return southPanel;
//	}
//
//	public String getNewFileName() {
//		if (newFileNameField != null) {
//			return newFileNameField.getText();
//		}
//		return null;
//	}
//
//	public void selectionChanged() throws FileSystemException {
//		LOGGER.debug("Updating selection");
//		boolean acceptEnabled = false;
//		if (getSelectedFilesOnTable().length == 0) {
//			acceptEnabled = false;
//		} else if (isMultiSelectionEnabled()) {
//			boolean filesSelected = false;
//			boolean folderSelected = false;
//
//			for (FileObject fo : getSelectedFilesOnTable()) {
//				FileType fileType = fo.getType();
//				if (fileType == FileType.FILE) {
//					filesSelected = true;
//				} else if (fileType == FileType.FOLDER) {
//					folderSelected = true;
//				}
//			}
//			if (selectionMode == SelectionMode.FILES_ONLY && filesSelected && !folderSelected) {
//				acceptEnabled = true;
//			} else if (selectionMode == SelectionMode.DIRS_ONLY && !filesSelected && folderSelected) {
//				acceptEnabled = true;
//			} else if (selectionMode == SelectionMode.DIRS_AND_FILES) {
//				acceptEnabled = true;
//			}
//		} else {
//			FileObject selectedFileObject = fileSystemPanel.getSelectedFileObject();
//			FileType type = selectedFileObject.getType();
//			if (selectionMode == SelectionMode.FILES_ONLY && type == FileType.FILE || selectionMode == SelectionMode.DIRS_ONLY && type == FileType.FOLDER) {
//				acceptEnabled = true;
//			} else if (SelectionMode.DIRS_AND_FILES == selectionMode) {
//				acceptEnabled = true;
//			}
//		}
//
//		if (actionApproveDelegate != null) {
//			actionApproveDelegate.setEnabled(acceptEnabled);
//		}
//		actionApproveButton.setEnabled(acceptEnabled);
//	}
//
//	public FileSystemPanel getFileSystemPanel() {
//		return fileSystemPanel;
//	}
//
//	public JTable getFileSystemTable() {
//		return fileSystemPanel.getTable();
//	}
//
//	public void fixSelection() {
//		fileSystemPanel.fixSelection();
//	}
//
//	public void goToUrl(String url) {
//		// System.out.println("VfsBrowser.goToUrl(): " + url);
//		LOGGER.info("Going to URL: " + url);
//		try {
//			FileObject resolveFile = VFSUtils.resolveFileObject(url);
//			String type = "?";
//			if (resolveFile != null) {
//				type = resolveFile.getType().toString();
//			}
//			LOGGER.info("URL: " + url + " is resolved " + type);
//			goToUrl(resolveFile);
//		} catch (FileSystemException e) {
//			LOGGER.error("Can't go to URL " + url, e);
//			final String message = getRootCause(e).getClass().getName() + ": " + getRootCause(e).getLocalizedMessage();
//
//			Runnable runnable = new Runnable() {
//				public void run() {
//					JOptionPane.showMessageDialog(OriginalVfsBrowser.this, "Can't open location: " + message);
//				}
//			};
//			SwingUtils.runInEdt(runnable);
//		}
//	}
//
//	public void goToUrl(final FileObject fileObject) {
//		if (taskContext != null) {
//			taskContext.setStop(true);
//		}
//		//
//		final FileObject[] files = VFSUtils.getFiles(fileObject);
//		LOGGER.info("Have {} files in {}", files.length, fileObject.getName().getFriendlyURI());
//		this.currentLocation = fileObject;
//		//
//		taskContext = new TaskContext(BROWSER_CHECKINGSFTPLINKSTASK, files.length);
//		taskContext.setIndeterminate(false);
//		SwingWorker<Void, Void> refreshWorker = new SwingWorker<Void, Void>() {
//			int icon = 0;
//			Icon[] icons = new Icon[] { NETWORKSTATUSONLINE, NETWORKSTATUSAWAY, NETWORKSTATUSOFFLINE };
//
//			@Override
//			protected void process(List<Void> chunks) {
//				JProgressBar loadingProgressBar = loadingPanel.getLoadingProgressBar();
//				loadingProgressBar.setIndeterminate(taskContext.isIndeterminate());
//				loadingProgressBar.setMaximum(taskContext.getMax());
//				loadingProgressBar.setValue(taskContext.getCurrentProgress());
//				loadingProgressBar.setString(String.format("%s [%d of %d]", taskContext.getName(), taskContext.getCurrentProgress(), taskContext.getMax()));
//				loadingPanel.getLoadingIconLabel().setIcon(icons[++icon % icons.length]);
//			}
//
//			@Override
//			protected Void doInBackground() throws Exception {
//				try {
//					while (!taskContext.isStop()) {
//						publish();
//						Thread.sleep(300);
//					}
//				} catch (InterruptedException ignore) {
//					// ignore
//				}
//				return null;
//			}
//		};
//		new Thread(refreshWorker).start();
//
//		if (!loadingPanel.getSkipCheckingLinksButton().isSelected()) {
//			VFSUtils.checkForSftpLinks(files, taskContext);
//		}
//		taskContext.setStop(true);
//		final FileObject[] fileObjectsWithParent = addParentToFiles(files);
//		Runnable r = new Runnable() {
//
//			@Override
//			public void run() {
//				fileSystemPanel.setContent(fileObjectsWithParent);
//				uriPanel.setFileObject(fileObject);
//				int filesCount = files.length;
//				statusLabel.setText(MessageFormat.format(BROWSER_FOLDERCONTAINSXELEMENTS, filesCount));
//				JTable table = fileSystemPanel.getTable();
//				if (table.getRowCount() > 0) {
//					table.getSelectionModel().setSelectionInterval(0, 0);
//				}
//			}
//		};
//		SwingUtils.runInEdt(r);
//	}
//
//	public static Throwable getRootCause(Throwable t) {
//		while (t.getCause() != null) {
//			t = t.getCause();
//		}
//		return t;
//	}
//
//	private FileObject[] addParentToFiles(FileObject[] files) {
//		FileObject[] newFiles = new FileObject[files.length + 1];
//		try {
//			FileObject parent = currentLocation.getParent();
//			if (parent != null) {
//				newFiles[0] = new ParentFileObject(parent);
//				System.arraycopy(files, 0, newFiles, 1, files.length);
//			} else {
//				newFiles = files;
//			}
//		} catch (FileSystemException e) {
//			LOGGER.warn("Can't add parent", e);
//			newFiles = files;
//		}
//		return newFiles;
//	}
//
//	public FileObject getCurrentLocation() {
//		return currentLocation;
//	}
//
//	public void addFavorite(Favorite favorite) {
//		favoritesPanel.getFavoritesUserListModel().add(favorite);
//	}
//
//	public void showLoading() {
//		System.out.println("VfsBrowser.showLoading()----------------------");
//		LOGGER.trace("Showing loading panel");
//		JProgressBar loadingProgressBar = loadingPanel.getLoadingProgressBar();
//		loadingProgressBar.setIndeterminate(true);
//		loadingProgressBar.setString(BROWSER_LOADING);
//		loadingPanel.getSkipCheckingLinksButton().setSelected(false);
//		loadingPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
//		cardLayout.show(centralPanel, LOADING_KEY);
//	}
//
//	public void showTable() {
//		LOGGER.trace("Showing result table");
//		fileSystemPanel.resetScroll();
//		cardLayout.show(centralPanel, TABLE_KEY);
//	}
//
//	public boolean isMultiSelectionEnabled() {
//		return multiSelectionEnabled;
//	}
//
//	public void setMultiSelectionEnabled(boolean b) {
//		int selectionMode = b ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION;
//		fileSystemPanel.getTable().getSelectionModel().setSelectionMode(selectionMode);
//		if (multiSelectionEnabled == b) {
//			return;
//		}
//		boolean oldValue = multiSelectionEnabled;
//		multiSelectionEnabled = b;
//		firePropertyChange(MULTI_SELECTION_ENABLED_CHANGED_PROPERTY, oldValue, multiSelectionEnabled);
//		try {
//			selectionChanged();
//		} catch (FileSystemException e) {
//			LOGGER.error("Error during update state", e);
//		}
//	}
//
//	public SelectionMode getSelectionMode() {
//		return selectionMode;
//	}
//
//	public void setSelectionMode(SelectionMode mode) {
//		if (selectionMode == mode) {
//			return;
//		}
//		SelectionMode oldValue = selectionMode;
//		this.selectionMode = mode;
//		firePropertyChange(MULTI_SELECTION_MODE_CHANGED_PROPERTY, oldValue, selectionMode);
//		try {
//			selectionChanged();
//		} catch (FileSystemException e) {
//			LOGGER.error("Error during update state", e);
//		}
//	}
//
//	public JButton getActionApproveButton() {
//		return actionApproveButton;
//	}
//
//	public Action getActionApproveDelegate() {
//		return actionApproveDelegate;
//	}
//
//	public void setApproveAction(Action action) {
//		actionApproveDelegate = action;
//		actionApproveButton.setAction(actionApproveDelegate);
//		if (action != null) {
//			actionApproveButton.setText((String) actionApproveDelegate.getValue(Action.NAME));
//		}
//		try {
//			selectionChanged();
//		} catch (FileSystemException e) {
//			LOGGER.warn("Problem with checking selection conditions", e);
//		}
//	}
//
//	public void setCancelAction(Action cancelAction) {
//		actionCancelDelegate = cancelAction;
//		actionCancelButton.setAction(actionCancelDelegate);
//		try {
//			selectionChanged();
//		} catch (FileSystemException e) {
//			LOGGER.warn("Problem with checking selection conditions", e);
//		}
//
//	}
//
//	public FileObject[] getSelectedFilesOnTable() {
//		return fileSystemPanel.getSelectedFileObjects();
//	}
//
//	/**
//	 * Resources
//	 */
//
//	private static final String BROWSER_CHECKINGSFTPLINKSTASK = ResourcesUtil.getString("browser.checkingSFtpLinksTask");
//	private static final String BROWSER_FOLDERCONTAINSXELEMENTS = ResourcesUtil.getString("browser.folderContainsXElements");
//	private static final String BROWSER_LOADING = ResourcesUtil.getString("browser.loading...");
//
//	private static final Icon NETWORKSTATUSAWAY = ResourcesUtil.getIcon("networkStatusAway");
//	private static final Icon NETWORKSTATUSONLINE = ResourcesUtil.getIcon("networkStatusOnline");
//	private static final Icon NETWORKSTATUSOFFLINE = ResourcesUtil.getIcon("networkStatusOffline");
//	private JTextField newFileNameField;
//
//}
