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

package eu.engys.core.executor.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;

import eu.engys.util.PrefUtil;
import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.filechooser.HelyxFileChooser;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class SaveLogFileAction extends ViewAction {

	private JTextArea area;

	public SaveLogFileAction(JTextArea area) {
		this(area, false);
	}

	public SaveLogFileAction(JTextArea area, boolean label) {
		super(label ? "Save Log to File" : "", ResourcesUtil.getIcon("save.log.file"), "Save Log to File");
		this.area = area;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		File workDir = PrefUtil.getWorkDir(PrefUtil.LAST_OPEN_EXPORT_DIR);
		HelyxFileChooser fc = new HelyxFileChooser(workDir.getAbsolutePath());
		fc.setTitle("Save Log File");
		fc.setSelectionMode(SelectionMode.FILES_ONLY);
		ReturnValue retVal = fc.showSaveAsDialog();
		if (retVal.isApprove()) {
			File file = fc.getSelectedFile();
			if (file != null) {
				if (file.exists()) {
					int answer = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), "File already exists. Overwrite?", "File Overwrite", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (answer == JOptionPane.YES_OPTION) {
						writeToFile(file);
					}
				} else {
					createFile(file);
					writeToFile(file);
				}
				PrefUtil.putFile(PrefUtil.LAST_OPEN_EXPORT_DIR, file.getParentFile());
			}
		}
	}

	private void createFile(File file) {
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void writeToFile(File file) {
		try {
			FileUtils.writeStringToFile(file, area.getText());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
