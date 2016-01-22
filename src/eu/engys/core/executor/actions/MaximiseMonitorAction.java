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

import javax.swing.JPanel;

import eu.engys.core.executor.TerminalManager;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.ViewAction;

public class MaximiseMonitorAction extends ViewAction {

	private JPanel panel;

	public MaximiseMonitorAction(JPanel panel) {
        this(panel, false);
    }

    public MaximiseMonitorAction(JPanel panel, boolean label) {
        super(label ? "Maximise Current Monitor" : null, ResourcesUtil.getIcon("console.tab.max.icon"), "Maximise Current Monitor");
		this.panel = panel;
        putValue(SMALL_ICON + SELECTED_KEY, ResourcesUtil.getIcon("console.tab.restore.icon"));
        putValue(SHORT_DESCRIPTION + SELECTED_KEY, "Restore");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isSelected()) {
            TerminalManager.getInstance().toDialog(panel);
        } else {
            TerminalManager.getInstance().toTab(panel);
        }
    }
}
