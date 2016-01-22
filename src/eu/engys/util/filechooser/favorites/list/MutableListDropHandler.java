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

/*
 * Copyright 2012 Krzysztof Otrebski (krzysztof.otrebski@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
