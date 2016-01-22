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

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eu.engys.util.ui.ResourcesUtil;

public class TerminalTabComponent extends JPanel {
    
    private static final Icon CONSOLE_ICON = ResourcesUtil.getIcon("console.tab.icon");
	private final TerminalManager manager;

	public TerminalTabComponent(final TerminalManager terminalManager) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		if (terminalManager == null) {
			throw new NullPointerException("TabbedPane is null");
		}
		this.manager = terminalManager;
		setOpaque(false);

		JLabel label = createTabComponent();
		add(label);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		addMouseListener(showPopupMenuListener);
	}

	private JLabel createTabComponent() {
		JLabel label = new JLabel() {
		    @Override
			public String getText() {
				return manager.getTitleFor(TerminalTabComponent.this);
			}

			@Override
			public Icon getIcon() {
				return CONSOLE_ICON;
			}
		};
		return label;
	}

	private final MouseListener showPopupMenuListener = new MouseAdapter() {
	    @Override
		public void mouseReleased(MouseEvent e) {
			manager.showPopup(e);
		};
	};
}
