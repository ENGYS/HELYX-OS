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

package eu.engys.util.filechooser.favorites;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

public class PopupListener extends MouseAdapter implements KeyListener {

	private JPopupMenu popupMenu;

	public PopupListener(JPopupMenu popupMenu) {
		super();
		this.popupMenu = popupMenu;
	}

	public void mousePressed(MouseEvent e) {
		checkPopup(e);
	}

	public void mouseClicked(MouseEvent e) {
		checkPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		checkPopup(e);
	}

	private void checkPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			show((Component) e.getSource(), e.getX(), e.getY());
		}
	}

	public void show(Component invoker, int x, int y) {
		popupMenu.show(invoker, x, y);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		Point p = new Point(e.getComponent().getLocation());

		if (e.getKeyCode() == KeyEvent.VK_CONTEXT_MENU) {
			if (e.getComponent() instanceof JTable) {
				JTable table = (JTable) e.getComponent();
				int selectedRow = table.getSelectedRow();
				Rectangle cellRect = table.getCellRect(selectedRow, 0, true);
				p.setLocation(cellRect.getCenterX(), cellRect.getCenterY());
			} else if (e.getComponent() instanceof JList) {
				JList<?> list = (JList<?>) e.getComponent();
				int selectedIndex = list.getSelectedIndex();
				Rectangle cellRect = list.getCellBounds(selectedIndex, selectedIndex);
				p.setLocation(cellRect.getCenterX(), cellRect.getCenterY());
			}
			show(e.getComponent(), (int) p.getX(), (int) p.getY());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}