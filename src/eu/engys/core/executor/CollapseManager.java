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

package eu.engys.core.executor;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CollapseManager {

	private JTabbedPane tabbedPane;

	public CollapseManager(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
		tabbedPane.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				collapsePanelIfEmpty();
			}
		});
	}

	private void collapsePanelIfEmpty() {
		boolean isEmpty = tabbedPane.getTabCount() == 0;
		if (isEmpty) {
			collapse();
		} else if (canExpand()) {
			expand();
		}
	}

	public boolean canExpand() {
		boolean hasOne = tabbedPane.getTabCount() >= 1;
		boolean isCollapsed = getSplitPane().getResizeWeight() == 1;
		return hasOne && isCollapsed;
	}

	public void collapse() {
		getSplitPane().setDividerLocation(getSplitPane().getHeight());
		getSplitPane().setResizeWeight(1);
	}

	public void expand() {
		getSplitPane().setDividerLocation(getSplitPane().getHeight() - 300);
		getSplitPane().setResizeWeight(0.7);
	}

	public void toggle() {
		if (canExpand()) {
			expand();
		} else {
			collapse();
		}
	}

	private JSplitPane getSplitPane() {
		return (JSplitPane) tabbedPane.getParent();
	}
}
