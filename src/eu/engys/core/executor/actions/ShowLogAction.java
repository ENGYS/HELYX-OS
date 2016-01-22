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

import javax.swing.JOptionPane;

import eu.engys.core.executor.FileManagerSupport;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class ShowLogAction extends ViewAction {

	public static final String OPEN_LOG_FILE = "Open Log File";
	
    private File logFile;

	public ShowLogAction() {
		this(false);
	}

	public ShowLogAction(boolean label) {
		super(label ? OPEN_LOG_FILE : null, ResourcesUtil.getIcon("console.browse.icon"), OPEN_LOG_FILE);
	}
	
	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (logFile != null && logFile.exists()) {
			FileManagerSupport.open(logFile);
		} else {
			JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "No log file has been created.", "File System error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
