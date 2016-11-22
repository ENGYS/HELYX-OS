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
package eu.engys.util.filechooser.table;

import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTable;

import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickSearchKeyAdapter extends KeyAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(QuickSearchKeyAdapter.class);

	private long lastTimeTyped = 0;
	private long typeTimeout = 500;
	private static final String LETTERS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
	private static final String DIGITS = "0123456789";
	private static final String OTHER_CHARS = "!@#$%^&*()()-_=+[];:'\",./ ";
	private static final String ALLOWED_CHARS = LETTERS + DIGITS + OTHER_CHARS;
	private StringBuilder sb;
	private final JTable table;

	public QuickSearchKeyAdapter(JTable table) {
		this.table = table;
		sb = new StringBuilder();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		char keyChar = e.getKeyChar();
		if (ALLOWED_CHARS.indexOf(keyChar) > -1) {
			if (System.currentTimeMillis() > lastTimeTyped + typeTimeout) {
				sb.setLength(0);
			}
			sb.append(keyChar);
			selectNextFileStarting(sb.toString());
			lastTimeTyped = System.currentTimeMillis();
		}

	}

	private void selectNextFileStarting(String string) {
		LOGGER.debug("Looking for file starting with {}", string);
		int selectedRow = table.getSelectedRow();
		selectedRow = selectedRow < 0 ? 0 : selectedRow;
		LOGGER.debug("Starting search with row {}", selectedRow);
		boolean fullLoop;
		int started = selectedRow;
		do {
			LOGGER.debug("Checking table row {}", selectedRow);
			int convertRowIndexToModel = table.convertRowIndexToModel(selectedRow);
			LOGGER.debug("Table row {} is row {} from model", selectedRow, convertRowIndexToModel);
			FileObject fileObject = ((FileSystemTableModel) table.getModel()).get(convertRowIndexToModel);
			LOGGER.debug("Checking {} if begins with {}", fileObject.getName().getBaseName(), string);
			if (fileObject.getName().getBaseName().toLowerCase().startsWith(string.toLowerCase())) {
				table.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
				table.scrollRectToVisible(new Rectangle(table.getCellRect(selectedRow, 0, true)));
				break;
			}
			selectedRow++;
			selectedRow = selectedRow >= table.getRowCount() ? 0 : selectedRow;
			fullLoop = selectedRow == started;
		} while (!fullLoop);
	}
}
