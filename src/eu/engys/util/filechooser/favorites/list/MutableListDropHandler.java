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

package eu.engys.util.filechooser.favorites.list;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

import eu.engys.util.ui.UiUtil;

public class MutableListDropHandler extends TransferHandler {
	private JList<?> list;

	public MutableListDropHandler(final JList<?> list) {
		this.list = list;
	}

	public boolean canImport(final TransferHandler.TransferSupport support) {
		if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return false;
		}
		final JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
		if (dl.getIndex() == -1) {
			return false;
		} else {
			return true;
		}
	}

	public boolean importData(final TransferHandler.TransferSupport support) {
		if (!canImport(support)) {
			return false;
		}

		final Transferable transferable = support.getTransferable();
		String indexString;
		try {
			indexString = (String) transferable.getTransferData(DataFlavor.stringFlavor);
		} catch (final Exception e) {
			JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), e.getMessage(), "File Chooser Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		}

		int index = Integer.parseInt(indexString);
		final JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
		final int dropTargetIndex = dl.getIndex();

		final MutableListModel<?> model = (MutableListModel<?>) list.getModel();
		model.move(index, dropTargetIndex);
		return true;
	}
}