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

package eu.engys.util.filechooser.actions.pathnavigation;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.ui.UiUtil;

public abstract class BaseNavigateAction extends AbstractAction {

	private static final int SWITCH_TO_LOADING_TIME = 120;

	public FileChooserController controller;
	private static Executor executor = Executors.newSingleThreadExecutor();
	private volatile SwingWorker<Void, Void> showLoadingAfterDelayWorker;
	private Component focusOwner;

	public BaseNavigateAction(FileChooserController controller) {
		super();
		this.controller = controller;
	}

	public BaseNavigateAction(FileChooserController controller, String name) {
		this(controller);
		putValue(NAME, name);
	}

	public BaseNavigateAction(FileChooserController controller, String name, Icon icon) {
		this(controller, name);
		putValue(SMALL_ICON, icon);
	}

	protected abstract void performLongOperation(CheckBeforeActionResult checkBeforeActionResult);

	@Override
	public final void actionPerformed(ActionEvent e) {
		final CheckBeforeActionResult checkBeforeActionResult = doInUiThreadBefore();
		if (CheckBeforeActionResult.CANT_GO.equals(checkBeforeActionResult)) {
			return;
		}

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected void done() {
				doInUiThreadAfter();
			}

			@Override
			protected Void doInBackground() throws Exception {
				try {
					performLongOperation(checkBeforeActionResult);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), e.getMessage(), "File Chooser Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				return null;
			}
		};
		executor.execute(worker);

	}

	protected final void doInUiThreadAfter() {
		if (showLoadingAfterDelayWorker != null) {
			showLoadingAfterDelayWorker.cancel(false);
		}
		updateGuiAfter();
		controller.showTable();
		if (focusOwner != null) {
			focusOwner.requestFocus();
		}
	}

	protected void updateGuiAfter() {
	}

	protected final CheckBeforeActionResult doInUiThreadBefore() {
		CheckBeforeActionResult result = CheckBeforeActionResult.CAN_GO;
		if (!canGoUrl()) {
			if (canExecuteDefaultAction()) {
				result = CheckBeforeActionResult.CANT_GO_USE_DEFAULT_ACTION;
			} else {
				result = CheckBeforeActionResult.CANT_GO;
			}
		} else {
			if (canExecuteDefaultAction()) {
				result = CheckBeforeActionResult.CAN_GO_OR_USE_DEFAULT_ACTION;
			} else {
				result = CheckBeforeActionResult.CAN_GO;
			}
        }

		focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		showLoadingAfterDelayWorker = new SwingWorker<Void, Void>() {

			@Override
			protected void done() {
				boolean cancelled = isCancelled();
				if (!cancelled) {
					controller.showLoading();
				}
			}

			@Override
			protected Void doInBackground() throws Exception {
				Thread.sleep(SWITCH_TO_LOADING_TIME);
				return null;
			}
		};
		executor.execute(showLoadingAfterDelayWorker);
		return result;
	}

	protected abstract boolean canExecuteDefaultAction();

	protected abstract boolean canGoUrl();

	protected void updateGuiBefore() {

	}

	public enum CheckBeforeActionResult {
		CAN_GO_OR_USE_DEFAULT_ACTION, CANT_GO, CANT_GO_USE_DEFAULT_ACTION, CAN_GO;
	}
}
