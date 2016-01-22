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

package eu.engys.util.filechooser.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import net.java.dev.designgridlayout.Componentizer;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;

import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.filechooser.util.HelyxFileFilter;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.builder.PanelBuilder;

public class ButtonsPanel extends JPanel {

    public static final String NAME = "chooser.buttonspanel";
    public static final String FILTER_COMBO = "filter.combo";
    
	private FileChooserController controller;
	private HelyxFileFilter[] filters;
	private JComboBox<HelyxFileFilter> filterCombo;
	private JButton okButton;

	public ButtonsPanel(FileChooserController controller, HelyxFileFilter[] filters) {
		super(new BorderLayout());
		this.filters = filters;
		setName(NAME);
		this.controller = controller;
		layoutComponents();
	}

	private void layoutComponents() {
		PanelBuilder pb = new PanelBuilder();
		okButton = new JButton(new OkAction());
		okButton.setName("OK");
		okButton.setEnabled(false);
		JButton cancelButton = new JButton(new CancelAction());
		cancelButton.setName("Cancel");
		if (filters != null) {
			filterCombo = createFilterCombo();
			pb.addComponent("Type", Componentizer.create().prefAndMore(filterCombo).minToPref(okButton, cancelButton).component());
		} else {
			pb.addComponent(Componentizer.create().prefAndMore(new JLabel()).minToPref(okButton, cancelButton).component());
		}
		add(pb.getPanel(), BorderLayout.CENTER);
	}

	public void updateOkButton() {
		ExecUtil.invokeAndWait(new Runnable() {

			@Override
			public void run() {
				_updateOkButton_OnEDT();
			}
		});
	}

	private void _updateOkButton_OnEDT() {
		FileObject fileSystemPanelFileObject = controller.getFileSystemPanel().getSelectedFileObject();
		FileObject uriPanelFileObject = controller.getUriPanel().getFileObject();

		if (controller.isSaveAs()) {
			String uriPanelNewFileName = controller.getUriPanel().getNewFileName();
			okButton.setEnabled(uriPanelFileObject != null && !uriPanelNewFileName.isEmpty());
		} else {
			if (fileSystemPanelFileObject == null) {
				if (controller.getSelectionMode().isDirsOnly() || controller.getSelectionMode().isDirsAndArchives()) {
					okButton.setEnabled(uriPanelFileObject != null);
				} else {
					okButton.setEnabled(false);
				}
			} else {
				okButton.setEnabled(checkTypeAndExtension(fileSystemPanelFileObject));
			}
		}
	}

	private boolean checkTypeAndExtension(FileObject selectedFileObject) {
		try {
			boolean rightType1 = (selectedFileObject.getType() == FileType.FILE) && controller.getSelectionMode().isFilesOnly();
			boolean rightType2 = (selectedFileObject.getType() == FileType.FOLDER) && controller.getSelectionMode().isDirsOnly();
			boolean rightType3 = (selectedFileObject.getType() == FileType.FOLDER) && controller.getSelectionMode().isDirsAndArchives();
			boolean rightType4 = controller.getSelectionMode().isDirsAndFiles();
			boolean rightType = rightType1 || rightType2 || rightType3 || rightType4;

			boolean rightExtension = true;
			HelyxFileFilter filter = getSelectedFilter();
			if (filter != null && !filter.isAllFilesFilter()) {
				String fileName = selectedFileObject.getName().getBaseName();
				String fileExtension = selectedFileObject.getName().getExtension();
				rightExtension = fileExtension.isEmpty() ? filter.isValidExtension(fileName) : filter.isValidExtension(fileExtension);
			}
			return rightExtension && (rightType);
		} catch (FileSystemException e) {
			return false;
		}
	}

	private JComboBox<HelyxFileFilter> createFilterCombo() {
		JComboBox<HelyxFileFilter> filterCombo = new JComboBox<HelyxFileFilter>();
		filterCombo.setName(FILTER_COMBO);
		filterCombo.addItem(HelyxFileFilter.getAllFilesFilter());
		for (HelyxFileFilter filter : filters) {
			filterCombo.addItem(filter);
		}
		filterCombo.setSelectedIndex(filterCombo.getItemCount() > 1 ? 1 : 0);
		final ListCellRenderer<? super HelyxFileFilter> renderer = filterCombo.getRenderer();
		filterCombo.setRenderer(new ListCellRenderer<HelyxFileFilter>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends HelyxFileFilter> list, HelyxFileFilter value, int index, boolean isSelected, boolean cellHasFocus) {
				Component c = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (c instanceof JLabel && value instanceof HelyxFileFilter) {
					HelyxFileFilter model = (HelyxFileFilter) value;
					((JLabel) c).setText(model.getDescription());
				}
				return c;
			}
		});
		filterCombo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() instanceof JComboBox && e.getStateChange() == ItemEvent.SELECTED) {
					controller.applyFilter();
				}
			}
		});
		return filterCombo;
	}

	public void resetFileFilter() {
		filterCombo.setSelectedItem(HelyxFileFilter.getAllFilesFilter());
		controller.applyFilter();
	}

	public HelyxFileFilter getSelectedFilter() {
		if (filterCombo != null) {
			return (HelyxFileFilter) filterCombo.getSelectedItem();
		}
		return null;
	}
	
	public JButton getOkButton() {
        return okButton;
    }

	private class OkAction extends AbstractAction {
		public OkAction() {
			super(controller.isSaveAs() ? "Save" : filters != null ? "Open" : "Select");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.closeAndReturn(ReturnValue.Approve);
		}
	}

	private class CancelAction extends AbstractAction {
		public CancelAction() {
			super("Cancel");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.closeAndReturn(ReturnValue.Cancelled);
		}
	}

}
