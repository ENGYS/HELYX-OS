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
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.filechooser.actions.favorite.AddFavoriteAction;
import eu.engys.util.filechooser.actions.navigation.BaseNavigateAction;
import eu.engys.util.filechooser.actions.navigation.BaseNavigateActionGoUp;
import eu.engys.util.filechooser.actions.navigation.BaseNavigateActionRefresh;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.PanelBuilder;
import net.java.dev.designgridlayout.Componentizer;

public class URIPanel extends JPanel {

	private static final String OPEN_PATH = "OPEN_PATH";

	private static final Logger logger = LoggerFactory.getLogger(URIPanel.class);

	public static final String NAME = "chooser.uripanel";
	public static final String NAME_LABEL = "Name:";
	public static final String LOOK_IN = "Look in:";
	public static final String SAVE_IN = "Save in:";
	private static final String ACTION_FOCUS_ON_TABLE = "FOCUS ON TABLE";

	public static final String ADD_FAVORITE = "add.favorite";
	public static final String UP_FOLDER = "up.folder";
	public static final String REFRESH = "refresh";

	private JTextField pathField;

	private FileChooserController controller;
	private FileObject fileObject;
	private JTextField newFileNameField;
	private BreadCrumbsPanel breadCrumbs;

	public URIPanel(FileChooserController controller) {
		super(new BorderLayout());
		setName(NAME);
		this.controller = controller;
		layoutComponents();
	}

	private void layoutComponents() {
		pathField = createPathField();
		breadCrumbs = new BreadCrumbsPanel(controller);

		PanelBuilder pb = new PanelBuilder();

		String pathLabel = controller.isSaveAs() ? SAVE_IN : LOOK_IN;
		pb.addComponent(pathLabel, Componentizer.create().prefAndMore(pathField).minToPref(createURIActionsBar()).component());
		pathField.setName(pathLabel);

		if (controller.isSaveAs()) {
			newFileNameField = new JTextField(15);
			newFileNameField.getDocument().addDocumentListener(new NotEmptyListener());
			newFileNameField.setText("");
			pb.addComponent(NAME_LABEL, newFileNameField);
		}

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(pb.removeMargins().getPanel(), BorderLayout.NORTH);
		panel.add(breadCrumbs, BorderLayout.CENTER);

		add(panel, BorderLayout.CENTER);
	}

	public String getNewFileName() {
		return newFileNameField == null ? null : newFileNameField.getText();
	}

	private JTextField createPathField() {
		final JTextField field = new JTextField(30);
		field.setToolTipText(NAV_PATHTOOLTIP);
		
		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
			    if(fileObject != null){
			        /*
			         * This will fix the path displayed if no ENTER is pressed
			         */
			        try {
			            pathField.setText(VFSUtils.decode(fileObject.getURL().toString(), controller.getSshParameters()));
			        } catch (FileSystemException e1) {
			            logger.error("Can't get URL", e);
			        }
			    }
			}
		});

		InputMap inputMapPath = field.getInputMap(JComponent.WHEN_FOCUSED);
		inputMapPath.put(KeyStroke.getKeyStroke("ENTER"), OPEN_PATH);
		inputMapPath.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), ACTION_FOCUS_ON_TABLE);

		field.getActionMap().put(OPEN_PATH, new BaseNavigateAction(controller) {

			@Override
			protected void performLongOperation(CheckBeforeActionResult actionResult) {
				controller.goToURL(field.getText().trim(), false);
				controller.updateOkButton();
			}

			@Override
			protected boolean canGoUrl() {
				return true;
			}

			@Override
			protected boolean canExecuteDefaultAction() {
				return false;
			}

		});

		field.getActionMap().put(ACTION_FOCUS_ON_TABLE, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.fixSelection();
			}
		});
		return field;
	}

	private JComponent createURIActionsBar() {
		JButton goUpButton = new JButton(new BaseNavigateActionGoUp(controller));
		goUpButton.setName(UP_FOLDER);

		JButton refreshButton = new JButton(new BaseNavigateActionRefresh(controller));
		refreshButton.setName(REFRESH);

		JButton addCurrentLocationToFavoriteButton = new JButton(new AddFavoriteAction(controller));
		addCurrentLocationToFavoriteButton.setName(ADD_FAVORITE);
		addCurrentLocationToFavoriteButton.setText("");

		JToolBar bar = UiUtil.getToolbar("uri.panel.toolbar");
		bar.add(goUpButton);
		bar.add(refreshButton);
		bar.add(addCurrentLocationToFavoriteButton);

		return bar;
	}

	public void setFileObject(FileObject fileObject) {
		try {
			this.fileObject = fileObject;
			pathField.setText(VFSUtils.decode(fileObject.getURL().toString(), controller.getSshParameters()));
			breadCrumbs.updatePanel(fileObject);
		} catch (FileSystemException e) {
			logger.error("Can't get URL", e);
		}
	}

	public void updateFileName() {
		FileObject fo = controller.getSelectedFileObject();
		if (controller.isSaveAs() && fo != null) {
			newFileNameField.setText(fo.getName().getBaseName());
		}
	}

	public FileObject getFileObject() {
		return fileObject;
	}

	private class NotEmptyListener implements DocumentListener {
		@Override
		public void removeUpdate(DocumentEvent e) {
			checkNotEmpty();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			checkNotEmpty();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			checkNotEmpty();
		}

		private void checkNotEmpty() {
			controller.updateOkButton();
		}
	}

	/**
	 * Resources
	 */

	private static final String NAV_PATHTOOLTIP = ResourcesUtil.getString("nav.pathTooltip");
}
