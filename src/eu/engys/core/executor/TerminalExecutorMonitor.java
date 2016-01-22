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

import static eu.engys.util.ui.UiUtil.createToolBarButton;
import static eu.engys.util.ui.UiUtil.createToolBarToggleButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.executor.actions.CloseMonitorAction;
import eu.engys.core.executor.actions.CopyMonitorToClipboardAction;
import eu.engys.core.executor.actions.MaximiseMonitorAction;
import eu.engys.core.executor.actions.SaveLogFileAction;
import eu.engys.core.executor.actions.ScrollLockAction;
import eu.engys.core.executor.actions.ShowLogAction;
import eu.engys.core.executor.actions.StopCommandAction;
import eu.engys.util.PrefUtil;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class TerminalExecutorMonitor extends ExecutorTerminal {

    private static final Logger logger = LoggerFactory.getLogger(TerminalExecutorMonitor.class);

    private static final String RUNNING_LABEL = "Running...";
    public static final String TERMINAL_PANEL_AREA = "terminal.panel.area";

    protected JTextArea area;
    private JScrollPane scroll;
    private JToolBar toolbar;
    private JPanel panel;

    private Date startTime;

    private JLabel stateLabel;
    private JLabel debugLabel;

    private ViewAction copyAction;
    private ViewAction saveAsAction;
    protected ViewAction closeAction;
    protected ViewAction maxAction;

    private ShowLogAction logAction;
    private ScrollLockAction scrollAction;
    private StopCommandAction stopAction;

    private Runnable stopCommand = new Runnable() {
        public void run() {
            stopExecutor();
        }
    };

    public TerminalExecutorMonitor() {
        super();
        layoutComponents();
    }

    public TerminalExecutorMonitor(File logFile) {
        this();
        setLogFile(logFile);
    }

    private void layoutComponents() {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                area = new JTextArea();
                scroll = new JScrollPane(area);
                toolbar = new JToolBar();
                panel = new JPanel(new BorderLayout());

                scroll.setName("terminal.panel.scroll");
                area.setName(TERMINAL_PANEL_AREA);

                stateLabel = new JLabel();
                stateLabel.setFont(new Font(stateLabel.getFont().getName(), Font.ITALIC, stateLabel.getFont().getSize()));

                debugLabel = new JLabel();

                stopAction = new StopCommandAction();
                stopAction.setStopCommand(stopCommand);
                stopAction.setEnabled(false);

                copyAction = new CopyMonitorToClipboardAction(area);
                logAction = new ShowLogAction();
                logAction.setEnabled(false);
                saveAsAction = new SaveLogFileAction(area);

                scrollAction = new ScrollLockAction(area);

                toolbar.setFloatable(false);
                toolbar.setRollover(false);
                toolbar.add(createToolBarButton(stopAction));
                toolbar.addSeparator();
                toolbar.add(createToolBarButton(logAction));
                toolbar.add(createToolBarButton(saveAsAction));
                toolbar.add(createToolBarButton(copyAction));
                toolbar.addSeparator();
                toolbar.add(createToolBarToggleButton(scrollAction));
                toolbar.addSeparator();
                toolbar.add(stateLabel);
                toolbar.add(debugLabel);
                toolbar.add(Box.createHorizontalGlue());

                configureFrameActions(toolbar);

                panel.add(toolbar, BorderLayout.NORTH);
                panel.add(scroll, BorderLayout.CENTER);
                setupFont();
            }
        });
    }

    protected void configureFrameActions(JToolBar toolbar) {
        maxAction = new MaximiseMonitorAction(panel);
        closeAction = new CloseMonitorAction(panel);
        closeAction.setEnabled(false);
        toolbar.add(createToolBarToggleButton(maxAction));
        toolbar.add(createToolBarButton(closeAction));

    }

    public void setLogFile(File logFile) {
        this.logAction.setLogFile(logFile);
        this.logAction.setEnabled(logFile != null);
    }

    public void setStopCommand(Runnable stopCommand) {
        this.stopAction.setStopCommand(stopCommand);
    }

    @Override
    public void start() {
        startTime = new Date();
        ExecUtil.invokeLater(new Runnable() {

            @Override
            public void run() {
                show();
                _refresh();

                stateLabel.setText(RUNNING_LABEL);
                stopAction.setEnabled(true);
                if (closeAction != null)
                    closeAction.setEnabled(false);
            }
        });
    }

    public void show() {
        TerminalManager.getInstance().addTerminal(panel, TerminalExecutorMonitor.this);
    }

    @Override
    public String getTitle() {
        return super.getTitle() + " " + getTime();
    }

    private String getTime() {
        return new SimpleDateFormat("'['HH:mm:ss']'").format(startTime);
    }

    private void setupFont() {
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 10);
        area.setFont(font);
        area.setBackground(Color.BLACK);
        area.setForeground(Color.LIGHT_GRAY);
    }

    @Override
    public void error(final int returnValue, final String msg) {
        super.error(returnValue, msg);
        refresh();
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                stateLabel.setText("");
                stopAction.setEnabled(false);
                if (closeAction != null) {
                    closeAction.setEnabled(true);
                }
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(area), ExecutorMonitor.decodeError(returnValue, msg), "Execution Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @Override
    public void finish(int returnValue) {
        super.finish(returnValue);
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                _refresh();
                stateLabel.setText("");
                stopAction.setEnabled(false);
                if (closeAction != null)
                    closeAction.setEnabled(true);
            }
        });
    }

    @Override
    public void refresh() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                _refresh();
            }
        });
    }

    protected void _refresh() {
        int maxLines = PrefUtil.getInt(PrefUtil.BATCH_MONITOR_DIALOG_MAX_ROW, 10000);

        String lines = getOutputStream().flushLinesBuffer();
        if (!lines.isEmpty()) {
            area.append(lines);
        }

        String errors = getErrorStream().flushLinesBuffer();
        if (!errors.isEmpty()) {
            area.append(errors);
        }

        int lineCount = area.getLineCount();
        if (lineCount > maxLines) {
            try {
                area.replaceRange("", 0, area.getLineEndOffset(lineCount - maxLines));
            } catch (BadLocationException e) {
                logger.warn("Error cleaning text area, {}", e.getMessage());
            }
        }

        if (!scrollAction.isSelected()) {
            area.setCaretPosition(area.getText().length());
        }
    }

    private boolean stopExecutor() {
        // if (getState() == ExecutorState.START || getState() == ExecutorState.RUNNING) {
        if (executor != null) {
            int retVal = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), "Stop execution?", "Close Monitor", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (retVal == JOptionPane.YES_OPTION) {
                executor.shutdownNow();
                return true;
            }
            return false;
        } else {
            return true;
        }
        // } else {
        // return true;
        // }
    }

    public JPopupMenu createMenu(Component component) {
        JPopupMenu pMenu = new JPopupMenu();
        pMenu.add(UiUtil.createMenuItem(new StopCommandAction(true)));
        pMenu.add(UiUtil.createMenuItem(new CloseMonitorAction(panel, true)));
        return pMenu;
    }

    public boolean canClose() {
        return closeAction != null && closeAction.isEnabled();
    }

    public void disconnect() {
    }

    public JPanel getPanel() {
        return panel;
    }

}
