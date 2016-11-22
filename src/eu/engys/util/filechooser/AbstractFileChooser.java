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

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JDialog;

import eu.engys.util.filechooser.gui.FileChooserPanel;
import eu.engys.util.filechooser.util.HelyxFileFilter;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.ui.UiUtil;

public class AbstractFileChooser {

	public enum ReturnValue {
		Approve, Cancelled;

		public boolean isApprove() {
			return equals(Approve);
		}

		public boolean isCancelled() {
			return equals(Cancelled);
		}
	}

	protected FileChooserPanel panel;
	private ReturnValue returnValue = ReturnValue.Cancelled;
	protected String initialPath;
	private JDialog dialog;
	private SelectionMode selectionMode = SelectionMode.DIRS_AND_FILES;
	private boolean multiSelectionEnabled;
	private String title;
	private Window parent;
	private File fileToSelect;

	public AbstractFileChooser() {
		this(null);
	}

	public AbstractFileChooser(String initialPath) {
		this.initialPath = initialPath;
	}

	protected ReturnValue initializeAndShow(Dimension d) {
		this.panel.layoutComponents();
		this.panel.setSelectionMode(selectionMode);
		this.panel.setSelectedFile(ensureValidFileToSelect(fileToSelect));
		this.panel.setMultiSelectionEnabled(multiSelectionEnabled);
		this.panel.initialize(ensureValidInitialPath(initialPath));
		return showDialog(d);
	}

	protected String ensureValidInitialPath(String pathToCheck) {
		return pathToCheck;
	}

	protected File ensureValidFileToSelect(File fileToCheck) {
		return fileToCheck;
	}

	private ReturnValue showDialog(Dimension d) throws HeadlessException {
		dialog = new JDialog(parent != null ? parent : UiUtil.getActiveWindow());
		dialog.setName("helyx.chooser.dialog");
		dialog.setTitle(title != null ? title : createTitle());
		dialog.getContentPane().add(panel);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				returnValue = ReturnValue.Cancelled;
			}
		});
		dialog.setSize(d);
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.getRootPane().setDefaultButton(panel.getOkButton());
		return returnValue;
	}

	private String createTitle() {
		switch (selectionMode) {
		case DIRS_ONLY:
			return "Select Folder";
		case FILES_ONLY:
			return "Select File";
		case DIRS_AND_FILES:
			return "Select File or Folder";
		default:
			return "Select File or Folder";
		}
	}
	
	public HelyxFileFilter getSelectedFileFilter(){
	    return panel.getSelectedFilter();
	}

	public void setMultiSelectionEnabled(boolean multiSelectionEnabled) {
		this.multiSelectionEnabled = multiSelectionEnabled;
	}

	public void setSelectionMode(SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}

	public void selectFile(File fileToSelect) {
		this.fileToSelect = fileToSelect;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setReturnValue(ReturnValue returnValue) {
		this.returnValue = returnValue;
	}

	public void disposeDialog() {
		if (dialog != null) {// FOR TESTS
			dialog.setVisible(false);
			dialog.dispose();
		}
	}

	public void setParent(Window parent) {
		this.parent = parent;
	}

	/*
	 * For test purpose only
	 */
	public void setPanel(FileChooserPanel panel) {
		this.panel = panel;
	}

	public FileChooserPanel getPanel() {
		return panel;
	}
	
	private static final int WIDTH = 750;
	private static final int HELIGHT = 500;

	protected Dimension getDimension(Dimension d) {
		return d != null ? d : new Dimension(WIDTH, HELIGHT);
	}
}
