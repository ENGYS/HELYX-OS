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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import eu.engys.util.ApplicationInfo;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class TerminalManager {

    private static TerminalManager instance = null;
    private Map<Component, JFrame> frames = new HashMap<>();
    private Map<Component, TerminalExecutorMonitor> monitors = new HashMap<>();

    public static final String TERMINAL_MANAGER = "terminal.manager";

    private CollapseManager collapseManager;
    private JTabbedPane tabbedPane;

    public static TerminalManager getInstance() {
        if (instance == null) {
            instance = new TerminalManager();
        }
        return instance;
    }

    private TerminalManager() {
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setName(TERMINAL_MANAGER);
        this.collapseManager = new CollapseManager(tabbedPane);
    }

    public void toggleVisibility() {
        collapseManager.toggle();
    }

    public void addTerminal(Component component, TerminalExecutorMonitor monitor) {
        if (!contains(component)) {
            tabbedPane.addTab(monitor.getTitle(), component);
            tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new TerminalTabComponent(this));
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

            if (!monitors.containsKey(component)) {
                monitors.put(component, monitor);
            }
        }
    }

    public boolean contains(Component component) {
        return tabbedPane.indexOfComponent(component) >= 0 || frames.containsKey(component);
    }

    public String getTitleFor(TerminalTabComponent tabComponent) {
        int i = tabbedPane.indexOfTabComponent(tabComponent);
        if (i != -1) {
            return tabbedPane.getTitleAt(i);
        }
        return "";
    }

    public void clear() {
        closeAll(true);
        clearFrames();
    }

    private void clearFrames() {
        for (JFrame frame : frames.values()) {
            frame.dispose();
        }
        frames.clear();
    }

    public void closeAll(boolean forced) {
        for (int i = tabbedPane.getTabCount() - 1; i >= 0; i--) {
            close(tabbedPane.getComponentAt(i), forced);
        }
    }

    public void close(Component component, boolean forced) {
        final TerminalExecutorMonitor monitor = monitors.get(component);
        if (monitor != null && (forced || monitor.canClose())) {
            monitor.disconnect();
            _close(component);
        }
    }

    private void _close(final Component component) {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                int i = tabbedPane.indexOfComponent(component);
                if (i >= 0) {
                    tabbedPane.remove(i);
                } else if (frames.containsKey(component)) {
                    frames.remove(component).dispose();
                }
            }
        });
    }

    public void toTab(final Component component) {
        int index = tabbedPane.indexOfComponent(component);
        if (index < 0) {
            final TerminalExecutorMonitor monitor = monitors.get(component);
            _close(component);
            addTerminal(component, monitor);
        }
    }

    public void toDialog(final Component component) {
        int index = tabbedPane.indexOfComponent(component);
        if (index >= 0) {
            final String title = tabbedPane.getTitleAt(index);
            _close(component);
            ExecUtil.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    final JFrame frame = createTerminalFrame(component, title, true);
                    frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                    frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            frame.dispose();
                        }
                    });
                    frame.setVisible(true);
                    frames.put(component, frame);
                }
            });
        }
    }

    private static void removeMinMaxClose(Component comp) {
        if (comp instanceof AbstractButton) {
            comp.getParent().remove(comp);
        }
        if (comp instanceof Container) {
            Component[] comps = ((Container) comp).getComponents();
            for (int x = 0, y = comps.length; x < y; x++) {
                removeMinMaxClose(comps[x]);
            }
        }
    }
    
    public static JFrame createTerminalFrame(final Component component, final String title, boolean removeMinMax) {
        final JFrame frame = new JFrame(title) {
            /**
             * This method fixes the Synthetica laf bug that causes incorrect fullscreen window size on secondary monitor.
             */
            @Override
            public void setMaximizedBounds(Rectangle bounds) {
                GraphicsDevice currentFrame = getGraphicsConfiguration().getDevice();
                if (UiUtil.isSecondaryScreen(currentFrame) && getExtendedState() == JFrame.NORMAL) {
                    super.setMaximizedBounds(UiUtil.getCurrentScreenSize(this));
                } else {
                    super.setMaximizedBounds(bounds);
                }
            }

        };
        
        if (removeMinMax) {
            removeMinMaxClose(frame);
        }
        
        frame.setIconImage(((ImageIcon) ResourcesUtil.getIcon(ApplicationInfo.getVendor().toLowerCase() + ".logo")).getImage());
        frame.setAlwaysOnTop(false);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(component, BorderLayout.CENTER);
        frame.setSize(600, 800);
        return frame;
    }

    public void showPopup(MouseEvent e) {
        Component c = e.getComponent();
        if (c instanceof TerminalTabComponent) {
            int index = tabbedPane.indexOfTabComponent(c);
            tabbedPane.setSelectedIndex(index);
            Component component = tabbedPane.getComponentAt(index);
            if (SwingUtilities.isRightMouseButton(e)) {
                TerminalExecutorMonitor monitor = monitors.get(component);
                JPopupMenu pMenu = monitor.createMenu(component);
                pMenu.addSeparator();
                pMenu.add(new JMenuItem(new CloseAllAction(true)));
                pMenu.show(c, e.getX(), e.getY());
            }
        }
    }

    class CloseAllAction extends ViewAction {

        public CloseAllAction() {
            this(false);
        }

        public CloseAllAction(boolean label) {
            super(label ? "Close All Monitors" : "", ResourcesUtil.getIcon("console.tab.closeall.icon"), "Close All Monitors");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            closeAll(false);
        }
    }

    public Component getComponent() {
        return tabbedPane;
    }
}
