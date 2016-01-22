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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthTabbedPaneUI;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;

import eu.engys.util.ApplicationInfo;
import eu.engys.util.connection.SshParameters;
import eu.engys.util.connection.SshUtils;
import eu.engys.util.progress.ProgressMonitor;

/** Static convenience methods for GUIs which eliminate code duplication. */
public final class UiUtil {

    public static Window getActiveWindow() {
        for (Window window : Window.getWindows()) {
            if (window.isShowing() && window.isActive())
                return (Window) window;
        }
        return null;
    }

    public static void debugPreferredSize(JComponent component) {
        UiUtil.debugPreferredSize(component, 0, 0);
    }

    public static void debugPreferredSize(JComponent component, int limitWidth, int limitHeight) {
        double width = component.getPreferredSize().getWidth();
        double height = component.getPreferredSize().getHeight();
        if (component.getComponentCount() == 0) {
            StringBuffer out = new StringBuffer("-> LEAF [" + component.getName() + "] - [" + component.getClass().getCanonicalName() + "]");
            if (width >= limitWidth && height >= limitHeight) {
                out.append(" - W:[" + width + "] - H:[" + height + "]");
            }
            System.out.println(out.toString());
        } else {
            StringBuffer out = new StringBuffer("| PARENT [" + component.getName() + "] - [" + component.getClass().getCanonicalName() + "]");
            if (width >= limitWidth && height >= limitHeight) {
                out.append(" - W:[" + width + "] - H:[" + height + "]");
            }
            System.out.println(out.toString());
            for (Component c : component.getComponents()) {
                if (c instanceof JComponent) {
                    debugPreferredSize((JComponent) c, limitWidth, limitHeight);
                }
            }
        }
    }

    public static void showDocumentationNotLoadedWarning(boolean emptyDocumentation) {
        if (emptyDocumentation) {
            JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Missing file.", ApplicationInfo.getName() + " Documentation error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Ambiguous file name.", ApplicationInfo.getName() + " Documentation error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void showEnvironmentNotLoadedWarning(String application) {
        String message = String.format("%s cannot be launched because:\n\t1) %s is not installed on your system.\n\t2) The path to the executable does not exists.\n\t3) The path to the executable is broken.\nPlease, enter %s executable path under: Edit > Preferences.", application, application, application);
        JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), message, application + " executable error", JOptionPane.WARNING_MESSAGE);
    }

    public static void showCoreEnvironmentNotLoadedWarning() {
        showCoreEnvironmentNotLoadedWarning(UiUtil.getActiveWindow());
    }

    public static void showCoreEnvironmentNotLoadedWarning(Component parent) {
        String token = ApplicationInfo.getName() + " Core";
        String message = String.format("%s cannot be launched because:\n\t1) %s is not installed on your system.\n\t2) The path to the executable does not exists.\n\t3) The path to the executable is broken.\nPlease, enter %s executable path under: Edit > Preferences.", token, token, token);
        JOptionPane.showMessageDialog(parent, message, token + " executable error", JOptionPane.WARNING_MESSAGE);
    }

    public static void showDemoMessage() {
        String message = "The feature requested is not available in this demo version of " + ApplicationInfo.getName() + ".\nPlease contact " + ApplicationInfo.getVendor() + " at " + ApplicationInfo.getMail();
        JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), message, "Demo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void show(String title, Component panel, int w, int h) {
        UiUtil.centerAndShow(defaultTestFrame(title, panel, w, h));
    }

    public static void show(String title, Component panel) {
        UiUtil.centerAndShow(defaultTestFrame(title, panel));
    }

    public static JFrame defaultTestFrame(String title, Component panel, int w, int h) {
        JFrame frame = defaultTestFrame(title, panel);
        frame.setSize(w, h);
        frame.setPreferredSize(new Dimension(w, h));
        return frame;
    }

    public static JFrame defaultTestFrame(String title, Component panel) {
        JFrame frame = defaultEmptyTestFrame(title);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        return frame;
    }

    public static JFrame defaultEmptyTestFrame(String title) {
        JFrame frame = new JFrame(title);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    public static void center(Window aWindow) {
        Dimension screen = getScreenSize();
        Dimension window = aWindow.getSize();
        // ensure that no parts of aWindow will be off-screen
        if (window.height > screen.height) {
            window.height = screen.height;
        }
        if (window.width > screen.width) {
            window.width = screen.width;
        }
        int xCoord = (screen.width / 2 - window.width / 2);
        int yCoord = (screen.height / 2 - window.height / 2);
        aWindow.setLocation(xCoord, yCoord);
    }

    public static void centerAndShow(Window aWindow) {
        aWindow.pack();
        /*
         * If called from outside the event dispatch thread (as is the case upon startup, in the launch thread), 
         * then in principle this code is not thread-safe: once pack has been called, the component is realized, 
         * and (most) further work on the component should take place in the event-dispatch thread.
         * 
         * In practice, it is exceedingly unlikely that this will lead to an error, since invisible components cannot receive events.
         */
        center(aWindow);
        aWindow.setVisible(true);
    }

    public static Border getStandardBorder() {
        return BorderFactory.createEmptyBorder(UiUtil.STANDARD_BORDER, UiUtil.STANDARD_BORDER, UiUtil.STANDARD_BORDER, UiUtil.STANDARD_BORDER);
    }

    public static JComponent getCommandRow(JComponent... aButtons) {
        List<JComponent> list = Arrays.asList(aButtons);
        return getCommandRow(list);
    }

    public static JComponent getCommandRow(java.util.List<JComponent> aButtons) {
        equalizeSizes(aButtons);
        JPanel panel = new JPanel();
        LayoutManager layout = new BoxLayout(panel, BoxLayout.X_AXIS);
        panel.setLayout(layout);
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(UiUtil.TWO_SPACES, 0, 0, 0));
        panel.add(Box.createHorizontalGlue());
        Iterator<JComponent> buttonsIter = aButtons.iterator();
        while (buttonsIter.hasNext()) {
            panel.add(buttonsIter.next());
            if (buttonsIter.hasNext()) {
                panel.add(Box.createHorizontalStrut(UiUtil.ONE_SPACE));
            }
        }
        return panel;
    }

    public static JComponent getCommandColumn(java.util.List<JComponent> aButtons) {
        equalizeSizes(aButtons);
        JPanel panel = new JPanel();
        LayoutManager layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, UiUtil.TWO_SPACES, 0, 0));
        // (no for-each is used here, because of the 'not-yet-last' check)
        Iterator<JComponent> buttonsIter = aButtons.iterator();
        while (buttonsIter.hasNext()) {
            panel.add(buttonsIter.next());
            if (buttonsIter.hasNext()) {
                panel.add(Box.createVerticalStrut(UiUtil.ONE_SPACE));
            }
        }
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    public static JComponent getCommandColumnToolbar(java.util.List<JComponent> aButtons) {
        equalizeSizes(aButtons);
        JToolBar panel = getToolbar("command.column.toolbar");
        LayoutManager layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);
        panel.setOpaque(false);
        panel.setFloatable(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, UiUtil.TWO_SPACES, 0, 0));

        // (no for-each is used here, because of the 'not-yet-last' check)
        Iterator<JComponent> buttonsIter = aButtons.iterator();
        while (buttonsIter.hasNext()) {
            AbstractButton next = (AbstractButton) buttonsIter.next();
            next.setAlignmentX(Component.LEFT_ALIGNMENT);
            next.setHorizontalAlignment(SwingConstants.LEFT);
            panel.add(next);
            if (buttonsIter.hasNext()) {
                panel.add(Box.createVerticalStrut(UiUtil.ONE_SPACE));
            }
        }
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    public static JMenuItem createMenuItem(Action a) {
        Icon icon = (Icon) a.getValue(Action.SMALL_ICON);
        String text = (String) a.getValue(Action.NAME);
        String desc = (String) a.getValue(Action.SHORT_DESCRIPTION);

        JMenuItem item = new JMenuItem(a);
        item.setName(text != null ? text : desc);
        item.setText(text != null ? text : desc);
        item.setIcon(icon);
        item.setToolTipText(desc);

        return item;
    }

    public static AbstractButton createButton(Action a) {
        Icon icon = (Icon) a.getValue(Action.SMALL_ICON);
        String text = (String) a.getValue(Action.NAME);
        String desc = (String) a.getValue(Action.SHORT_DESCRIPTION);

        JButton b = new JButton(a);
        b.setName(text != null ? text : desc);
        b.setText(((text != null && text.equals("MISSING")) ? null : text));
        b.setIcon(icon);
        b.setToolTipText(desc);
        
        return b;
    }
    
    public static AbstractButton createToolBarIconButton(Action a) {
        return _createToolBarButton(a, false);
    }

    public static AbstractButton createToolBarButton(Action a) {
        return _createToolBarButton(a, true);
    }

    private static AbstractButton _createToolBarButton(Action a, boolean showLabel) {
        Icon icon = (Icon) a.getValue(Action.SMALL_ICON);
        String text = (String) a.getValue(Action.NAME);
        String desc = (String) a.getValue(Action.SHORT_DESCRIPTION);

        JButton b = new JButton(a) {
            public Insets getMargin() {
                if (super.getMargin() != null)
                    return new Insets(super.getMargin().top, 2, super.getMargin().bottom, 2);
                else
                    return null;
            }
        };
        b.setName(text != null ? text : desc);
        b.setText(showLabel ? ((text != null && text.equals("MISSING")) ? null : text) : null);
        b.setIcon(icon);
        b.setToolTipText(desc);
        // b.setEnabled(a.isEnabled());
        b.setFocusable(false);
        return b;
    }

    public static AbstractButton createToolBarMultiButtonBar(String name, Icon icon, String tooltip, final Action... actions) {
        final JButton button = new JButton() {
            public Insets getMargin() {
                if (super.getMargin() != null)
                    return new Insets(super.getMargin().top, 2, super.getMargin().bottom, 2);
                else
                    return null;
            }
        };
        button.setName(name);
        button.setToolTipText(tooltip);
        button.setFocusable(false);

        final JPopupMenu popup = new JPopupMenu();
        for (Action action : actions) {
            JMenuItem item = new JMenuItem(action);
            item.setName((String) action.getValue(Action.NAME));
            item.setToolTipText(String.valueOf(action.getValue(Action.SHORT_DESCRIPTION)));
            popup.add(item);
        }

        button.setAction(new ViewAction(name, icon, tooltip) {

            @Override
            public void actionPerformed(ActionEvent e) {
                popup.show(button, 0, button.getPreferredSize().height);
            }
        });

        return button;
    }

    public static ButtonBar createToolBarButtonBar(Action... actions) {
        ButtonBar bar = new ButtonBar();
        for (Action action : actions) {
            AbstractButton button = createButtonBarButton(action);
            button.setName((String) action.getValue(Action.NAME));
            bar.add(button);
        }
        return bar;
    }

    public static void clearToolbar(JToolBar toolbar) {
        for (Component c : toolbar.getComponents()) {
            if (c instanceof AbstractButton) {
                ((AbstractButton) c).setSelected(false);
            } else if (c instanceof JComboBox) {
                ((JComboBox<?>) c).setSelectedIndex(-1);
            }
        }
    }

    public static ButtonBar createToolBarToggleButtonBar(Action... actions) {
        ButtonGroup viewGroup = new ButtonGroup();
        ButtonBar bar = new ButtonBar();
        for (Action action : actions) {
            bar.add(createButtonBarToggleButton(action, viewGroup));
        }
        return bar;
    }

    public static AbstractButton createToolBarToggleButton(Action a) {
        return createToolBarToggleButton(a, false);
    }

    public static JToggleButton createToolBarToggleButton(Action a, final boolean tooltipOver) {
        Icon icon = (Icon) a.getValue(Action.SMALL_ICON);
        Icon sel_icon = (Icon) a.getValue(Action.SMALL_ICON + Action.SELECTED_KEY);
        String text = (String) a.getValue(Action.NAME);

        final JToggleButton b = new JToggleButton(a) {
            public Insets getMargin() {
                if (super.getMargin() != null)
                    return new Insets(super.getMargin().top, 2, super.getMargin().bottom, 2);
                else
                    return null;
            }
        };
        b.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                b.getAction().putValue(Action.SELECTED_KEY, b.getModel().isSelected());
            }
        });
        b.setName(text);
        b.setText((text != null && text.equals("MISSING")) ? null : text);
        b.setHorizontalTextPosition(0);
        b.setVerticalTextPosition(3);
        b.setIcon(icon);
        b.setRolloverIcon(icon);
        b.setRolloverSelectedIcon(sel_icon);
        b.setSelectedIcon(sel_icon);
        b.setEnabled(a.isEnabled());
        b.setFocusable(false);
        return b;
    }

    public static AbstractButton createButtonBarButton(Action a) {
        JButton b = new JButton(a) {
            public Insets getMargin() {
                if (super.getMargin() != null)
                    return new Insets(super.getMargin().top, 0, super.getMargin().bottom, 0);
                else
                    return null;
            }
        };
        b.setHorizontalTextPosition(SwingConstants.RIGHT);
        // b.setVerticalTextPosition(3);
        b.setName((String) a.getValue(Action.NAME));
        b.setFocusable(false);
        return b;
    }

    public static AbstractButton createButtonBarToggleButton(Action a, ButtonGroup group) {
        Icon icon = (Icon) a.getValue(Action.SMALL_ICON);
        String text = (String) a.getValue(Action.SHORT_DESCRIPTION);

        JToggleButton b = new JToggleButton(a) {
            public Insets getMargin() {
                if (super.getMargin() != null)
                    return new Insets(super.getMargin().top, 0, super.getMargin().bottom, 0);
                else
                    return null;
            }
        };
        b.setHorizontalTextPosition(0);
        b.setVerticalTextPosition(3);
        b.setText("");
        b.setIcon(icon);
        b.setToolTipText(text);
        b.setFocusable(false);
        group.add(b);
        return b;
    }

    public static void updateToolBarComboButton(final JComboBox<Action> combo, final List<Action> actions) {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                _updateToolBarComboButton(combo, actions);
            }
        });
    }

    private static void _updateToolBarComboButton(JComboBox<Action> combo, List<Action> actions) {
        int selectedIndex = -1;
        ActionListener[] actionListeners = combo.getActionListeners();
        for (ActionListener l : actionListeners) {
            combo.removeActionListener(l);
        }
        ItemListener[] itemListeners = combo.getItemListeners();
        for (ItemListener l : itemListeners) {
            combo.removeItemListener(l);
        }
        combo.removeAllItems();
        combo.setPrototypeDisplayValue(getPrototype(actions, combo.getPrototypeDisplayValue()));
        for (int j = 0; j < actions.size(); j++) {
            combo.addItem(actions.get(j));
            if (actions.get(j).getValue("default") != null) {
                selectedIndex = j;
            }
        }
        combo.setSelectedIndex(selectedIndex);
        for (ActionListener l : actionListeners) {
            combo.addActionListener(l);
        }
        for (ItemListener l : itemListeners) {
            combo.addItemListener(l);
        }
    }

    public static JComboBox<Action> createToolBarComboButton(List<Action> actions, String tooltip, String prototype, boolean enabled, final boolean tooltipOver) {
        int selectedIndex = -1;
        final JComboBox<Action> c = new JComboBox<Action>();
        c.setPrototypeDisplayValue(getPrototype(prototype));
        for (int j = 0; j < actions.size(); j++) {
            c.addItem(actions.get(j));
            if (actions.get(j).getValue("default") != null) {
                selectedIndex = j;
            }
        }
        c.setSelectedIndex(selectedIndex);
        c.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (c.getSelectedIndex() > -1) {
                        c.getItemAt(c.getSelectedIndex()).actionPerformed(null);
                    }
                }
            }
        });
        c.setMaximumSize(c.getPreferredSize());
        c.setEnabled(enabled);
        c.setToolTipText(tooltip);
        c.setRenderer(new ActionsComboBoxRenderer(c.getRenderer()));
        return c;
    }

    private static Action getPrototype(List<Action> actions, Action actualPrototype) {
        Action proto = actualPrototype;
        for (Action action : actions) {
            String actionName = (String) action.getValue(Action.NAME);
            String prototypeName = (String) proto.getValue(Action.NAME);
            if (actionName.length() > prototypeName.length()) {
                proto = action;
            }
        }
        return proto;
    }

    private static Action getPrototype(String actionName) {
        return new AbstractAction(actionName) {
            @Override
            public void actionPerformed(ActionEvent arg0) {
            }
        };
    }

    private static class ActionsComboBoxRenderer extends JLabel implements ListCellRenderer<Action> {

        private ListCellRenderer renderer;

        public ActionsComboBoxRenderer(ListCellRenderer renderer) {
            this.renderer = renderer;
        }

        @SuppressWarnings("unchecked")
        public Component getListCellRendererComponent(JList<? extends Action> list, Action value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (c instanceof JLabel && value != null) {
                String text = (String) value.getValue(Action.NAME);
                Icon icon = (Icon) value.getValue(Action.SMALL_ICON);

                JLabel label = (JLabel) renderer;
                label.setIcon(icon);
                label.setText(text);
            }

            return c;
        }

    }

    public static void equalizeSizes(java.util.List<JComponent> aComponents) {
        Dimension targetSize = new Dimension(0, 0);
        for (JComponent comp : aComponents) {
            Dimension compSize = comp.getPreferredSize();
            double width = Math.max(targetSize.getWidth(), compSize.getWidth());
            double height = Math.max(targetSize.getHeight(), compSize.getHeight());
            targetSize.setSize(width, height);
        }
        setSizes(aComponents, targetSize);
    }

    private static void setSizes(java.util.List<JComponent> aComponents, Dimension aDimension) {
        Iterator<JComponent> compsIter = aComponents.iterator();
        while (compsIter.hasNext()) {
            JComponent comp = (JComponent) compsIter.next();
            comp.setPreferredSize((Dimension) aDimension.clone());
            comp.setMaximumSize((Dimension) aDimension.clone());
        }
    }

    private static <T extends JComponent> List<T> getDescendantsOfType(Class<T> clazz, Container container, boolean nested) {
        List<T> tList = new ArrayList<T>();

        for (Component component : container.getComponents()) {
            if (clazz.isAssignableFrom(component.getClass())) {
                tList.add(clazz.cast(component));
            }
            if (nested || !clazz.isAssignableFrom(component.getClass())) {
                if (component instanceof Container) {
                    tList.addAll(getDescendantsOfType(clazz, (Container) component, nested));
                }
            }
        }

        return tList;
    }

    private static Map<Container, List<JComponent>> containers = new HashMap<Container, List<JComponent>>();

    public static void enable(Container container) {
        List<JComponent> enabledComponents = containers.get(container);
        if (enabledComponents != null) {
            for (JComponent component : enabledComponents) {
                if (component instanceof AbstractButton) {
                    AbstractButton b = (AbstractButton) component;
                    if (b.getAction() != null) {
                        b.getAction().setEnabled(true);
                    } else {
                        component.setEnabled(true);
                    }
                } else {
                    component.setEnabled(true);
                }

            }
            containers.remove(container);
        }
    }
    
    public static void disable(Container container) {
        List<JComponent> components = getDescendantsOfType(JComponent.class, container, true);
        List<JComponent> enabledComponents = new ArrayList<JComponent>();
        if (!containers.containsKey(container)) {
            containers.put(container, enabledComponents);
            for (JComponent component : components) {
                if (component.isEnabled()) {
                    enabledComponents.add(component);
                    if (component instanceof AbstractButton) {
                        AbstractButton b = (AbstractButton) component;
                        if (b.getAction() != null) {
                            b.getAction().setEnabled(false);
                        } else {
                            component.setEnabled(false);
                        }
                    } else {
                        component.setEnabled(false);
                    }
                }
            }
        }
    }
    

    public static void expandAll(JTree tree, boolean expand) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();

        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }

    public static void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    public static Boolean testConnection(final SshParameters sshParameters, final ProgressMonitor progressMonitor) {
        progressMonitor.setIndeterminate(true);
        Boolean retVal = progressMonitor.start("Testing connection...", false, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                boolean retVal = SshUtils.testConnection(sshParameters);
                progressMonitor.end();
                return retVal;
            }
        });
        return retVal;
    }

    public static boolean isMainScreen(GraphicsDevice currentScreen) {
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice def = g.getDefaultScreenDevice();
        return def.equals(currentScreen);
    }

    public static boolean isSecondaryScreen(GraphicsDevice currentScreen) {
        return !isMainScreen(currentScreen);
    }

    public static Rectangle getCurrentScreenSize(JFrame frame) {
        GraphicsConfiguration config = frame.getGraphicsConfiguration();
        GraphicsDevice currentScreen = config.getDevice();
        return new Rectangle(currentScreen.getDisplayMode().getWidth(), currentScreen.getDisplayMode().getHeight());
    }

    public static JToolBar getToolbar(String name) {
        JToolBar toolbar = new JToolBar();
        toolbar.setLayout(new WrappedFlowLayout(FlowLayout.LEFT, 0, 0));
        toolbar.putClientProperty("Synthetica.toolBar.buttons.paintBorder", Boolean.TRUE);
        toolbar.putClientProperty("Synthetica.opaque", Boolean.FALSE);
        toolbar.setName(name);
        toolbar.setFloatable(false);
        toolbar.setRollover(false);
        toolbar.setOpaque(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder());

        return toolbar;
    }

    public static void setOneTabHide(final JTabbedPane tabbedPane) {
        try {
            LookAndFeel laf = UIManager.getLookAndFeel();
            TabbedPaneUI ui = null;

            if (laf != null && laf instanceof SynthLookAndFeel) {
                ui = new SynthTabbedPaneUI() {
                    @Override
                    protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                        if (tabbedPane.getTabCount() > 1) {
                            return super.calculateTabAreaHeight(tabPlacement, horizRunCount, maxTabHeight);
                        } else {
                            return 0;
                        }
                    }

                    @Override
                    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                        if (tabbedPane.getTabCount() > 1) {
                            return super.calculateTabWidth(tabPlacement, tabIndex, metrics);
                        } else {
                            return 0;
                        }
                    }

                };

            } else if (laf != null && laf instanceof MetalLookAndFeel) {
                ui = new MetalTabbedPaneUI() {
                    @Override
                    protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                        if (tabbedPane.getTabCount() > 1) {
                            return super.calculateTabAreaHeight(tabPlacement, horizRunCount, maxTabHeight);
                        } else {
                            return 0;
                        }
                    }

                    @Override
                    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                        if (tabbedPane.getTabCount() > 1) {
                            return super.calculateTabWidth(tabPlacement, tabIndex, metrics);
                        } else {
                            return 0;
                        }
                    }

                };
            }
            tabbedPane.setUI(ui);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public static void installExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if (GraphicsEnvironment.isHeadless()) {
                } else {
                    e.printStackTrace();
//                    StringOutputStream stream = new StringOutputStream();
//                    e.printStackTrace(new PrintStream(stream));
//                    String msg = stream.toString();
//                    System.err.println(msg);
//                    JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), msg, "An error occurred", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void renameUIThread() {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                if (SwingUtilities.isEventDispatchThread()) {
                    Thread.currentThread().setName("GUI Dispatch Queue");
                }
            }
        });
    }

    public static Dimension getScreenSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = ge.getScreenDevices();
        // System.out.println("UiUtil.getPreferredDimension() screenDevices.length: "+screenDevices.length);
        if (screenDevices.length > 1) {
            int width = screenSize.width;
            int height = screenSize.height;
            for (int i = 0; i < screenDevices.length; i++) {
                GraphicsDevice gd = screenDevices[i];
                width = Math.min(width, gd.getDisplayMode().getWidth());
                height = Math.min(height, gd.getDisplayMode().getHeight());
            }

            // System.out.println("UiUtil.getPreferredDimension() width: "+width+" height: "+height);
            return new Dimension(width, height);
        } else {
            return screenSize;
        }
    }

    public static Dimension getPreferredScreenSize() {
        Dimension screenSize = getScreenSize();
        int W = screenSize.width * 8 / 10;
        int H = screenSize.height * 8 / 10;

        return new Dimension(W, H);
    }

    public static void printLogOnDesktopFile(String log) {
        printLogOnDesktopFile(log, true);
    }

    public static void printLogOnDesktopFile(String log, boolean append) {
        try {
            File desktopFolder = Paths.get(System.getProperty("user.home"), "Desktop").toFile();
            if (desktopFolder.exists()) {
                File logFile = new File(desktopFolder, "log.txt");
                if (!logFile.exists()) {
                    logFile.createNewFile();
                }
                String date = new SimpleDateFormat("'['HH:mm:ss']'").format(new Date());
                FileUtils.writeStringToFile(logFile, date + " - " + log + "\n", append);
            }
        } catch (Exception e) {

        }

    }

    public static final int ONE_SPACE = 5;
    public static final int TWO_SPACES = 11;
    public static final int THREE_SPACES = 17;
    public static final int STANDARD_BORDER = TWO_SPACES;

}
