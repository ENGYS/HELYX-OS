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


package eu.engys.util.ui;

import java.awt.event.InputEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

public abstract class ViewAction extends AbstractAction {
	
	public ViewAction(String text, String tooltip) {
		super(text, null);
		putValue(SHORT_DESCRIPTION, tooltip);
	}

	public ViewAction(Icon icon, String tooltip) {
		super(null, icon);
		putValue(SHORT_DESCRIPTION, tooltip);
	}
	
	public ViewAction(String text, Icon icon, boolean enabled) {
		super(text, icon);
		setEnabled(enabled);
	}

	public ViewAction(String text, Icon icon, String tooltip) {
		super(text, icon);
		putValue(SHORT_DESCRIPTION, tooltip);
	}

	public ViewAction(String text, Icon icon, String tooltip, boolean enabled) {
		super(text, icon);
		putValue(SHORT_DESCRIPTION, tooltip);
		setEnabled(enabled);
	}

	public ViewAction(String text, Icon icon, String tooltip, int mnemonic) {
		super(text, icon);
		putValue(SHORT_DESCRIPTION, tooltip);
		putValue(MNEMONIC_KEY, mnemonic);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(mnemonic, InputEvent.CTRL_DOWN_MASK));
	}
	
	public boolean isSelected() {
        return Boolean.TRUE.equals(getValue(Action.SELECTED_KEY));
    }

	public void setSelected(boolean b) {
		putValue(Action.SELECTED_KEY, Boolean.valueOf(b));
	}
	
	public String getText() {
	    return (String) getValue(NAME);
	}

	public String getTooltip() {
	    return (String) getValue(SHORT_DESCRIPTION);
	}

	public Icon getIcon() {
	    return (Icon) getValue(SMALL_ICON);
	}
}
