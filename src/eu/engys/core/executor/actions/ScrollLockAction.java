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
package eu.engys.core.executor.actions;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.ViewAction;

public class ScrollLockAction extends ViewAction {

	private JTextArea area;

	public ScrollLockAction(JTextArea area) {
		this(area, false);
	}

	public ScrollLockAction(JTextArea area, boolean label) {
		super(label ? "Scroll Lock" : null, ResourcesUtil.getIcon("console.scroll.icon"), "Scroll Lock");
		this.area = area;
		putValue(SMALL_ICON + SELECTED_KEY, ResourcesUtil.getIcon("console.scroll.lock.icon"));
		putValue(SHORT_DESCRIPTION + SELECTED_KEY, "Scroll Lock");

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (isSelected()) {
			DefaultCaret caret = (DefaultCaret) area.getCaret();
			caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		} else {
			DefaultCaret caret = (DefaultCaret) area.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		}
	}

}
