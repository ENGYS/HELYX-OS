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


package eu.engys.util.progress;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;

public class ProgressDialog extends JDialog {

	private final Action CLOSE_ACTION = new AbstractAction("Close") {
		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	};

	private final Action STOP_ACTION = new AbstractAction("Stop") {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        monitor.stop();
	    }
	};

	private JEditorPane statusArea = new JEditorPane("text/html", null);
	private ProgressBar progressBar;
	private JCheckBox keepOpen;
	private ProgressMonitor monitor;

    private JButton closeButton;
    private JButton stopButton;

	public ProgressDialog(Window window) throws HeadlessException {
		super(window, "Progress", JDialog.DEFAULT_MODALITY_TYPE);
		setSize(500, 300);
		setLocationRelativeTo(null);
	}

	public void init(ProgressMonitor monitor) {
		this.monitor = monitor;

		progressBar = new ProgressBar(0, monitor.getTotal());

		if (monitor.isIndeterminate()) {
			progressBar.setIndeterminate(true);
			progressBar.setStringPainted(false);
		} else {
			progressBar.setStringPainted(monitor.getTotal() > 0);
			progressBar.setValue(monitor.getCurrent() < 0 ? 0 : monitor.getCurrent());
		}

		keepOpen = new JCheckBox("Keep dialog open on errors", true);

		statusArea.setText(monitor.getMessages());

		final JScrollPane statusScrollPane = new JScrollPane(statusArea);
		statusScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			BoundedRangeModel brm = statusScrollPane.getVerticalScrollBar().getModel();
			boolean wasAtBottom = true;

			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (!brm.getValueIsAdjusting()) {
					if (wasAtBottom)
						brm.setValue(brm.getMaximum());
				} else
					wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());

			}
		});

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(statusScrollPane, BorderLayout.CENTER);
		centerPanel.add(keepOpen, BorderLayout.SOUTH);

		closeButton = new JButton(CLOSE_ACTION);
		stopButton = new JButton(STOP_ACTION);
		
		List<JComponent> buttons = new ArrayList<JComponent>();
        buttons.add(closeButton);
        buttons.add(stopButton);
		JComponent buttonsPanel = UiUtil.getCommandRow(buttons);

		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout(UiUtil.STANDARD_BORDER, UiUtil.STANDARD_BORDER));
		contents.setBorder(UiUtil.getStandardBorder());
		contents.add(progressBar, BorderLayout.NORTH);
		contents.add(centerPanel, BorderLayout.CENTER);
		contents.add(buttonsPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		closeButton.setVisible(false);
		stopButton.setVisible(false);
	}

	public void start() {
		ExecUtil.invokeLater(new Runnable() {
			public void run() {
				_start();
			}
		});
	}

	public void startImmediately() {
		ExecUtil.invokeAndWait(new Runnable() {
			public void run() {
				_start();
			}
		});
	}

	private void _start() {
		_update();

		stopButton.setVisible(monitor.canStop());
		
		if (!monitor.isFinished()) {
			setVisible(true);
		}
	}

	public void end() {
		if (isVisible()) {
			ExecUtil.invokeAndWait(new Runnable() {
				public void run() {
					_end();
				}
			});
		} else {
			ExecUtil.invokeLater(new Runnable() {
				public void run() {
					_end();
				}
			});
		}
	}

	private void _end() {
		if (monitor.isFinished()) {
			progressBar.setIndeterminate(false);
			progressBar.setValue(progressBar.getMaximum());
			String messages = monitor.getMessages();
			statusArea.setText(messages);
			
			if (!(keepOpen.isSelected() && monitor.hasErrors())) {
				if (isVisible()) {
					setVisible(false);
				}
			}
			closeButton.setVisible(isVisible());
			stopButton.setVisible(false);
		} else {
		}
	}

	public void update() {
		ExecUtil.invokeLater(new Runnable() {
			public void run() {
				_update();
			}
		});
	}

	private void _update() {
		if (monitor.getCurrent() != monitor.getTotal()) {
			String messages = monitor.getMessages();
			statusArea.setText(messages);

			if (monitor.isIndeterminate() != progressBar.isIndeterminate()) {
				progressBar.setIndeterminate(monitor.isIndeterminate());
				progressBar.setStringPainted(false);
			}

			if (!monitor.isIndeterminate()) {
				if (monitor.getTotal() != progressBar.getMaximum())
					progressBar.setMaximum(monitor.getTotal());

				if (monitor.getTotal() > 0) {
					progressBar.setStringPainted(true);
					progressBar.setValue(monitor.getCurrent());
				} else {
					progressBar.setStringPainted(false);
				}
			}

		} else {
			progressBar.setStringPainted(true);
			progressBar.setValue(progressBar.getMaximum());
//			CLOSE_ACTION.setEnabled(true);
		}
	}

	public static void runOnEDT(final Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread()) {
			// System.out.println("ProgressDialog.runOnEDT() is EDT");
			// Thread.dumpStack();
			runnable.run();
		} else {
			SwingUtilities.invokeLater(runnable);
		}
	}

	public static void waitOnEDT(final Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread()) {
			// System.out.println("ProgressDialog.runOnEDT() is EDT");
			// Thread.dumpStack();
			runnable.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(runnable);
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
