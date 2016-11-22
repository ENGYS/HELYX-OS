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
package eu.engys.gui;

import static eu.engys.gui.view.View.EXIT;
import static eu.engys.gui.view.View.NEW_CASE;
import static eu.engys.gui.view.View.OPEN_CASE;
import static eu.engys.util.SystemEnv.LICENSE_ERROR_MESSAGE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import eu.engys.application.Application;
import eu.engys.core.controller.Controller.OpenMode;
import eu.engys.core.controller.OpenOptions;
import eu.engys.core.presentation.ActionManager;
import eu.engys.gui.view.View;
import eu.engys.util.ApplicationInfo;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;

public class StartPanel extends JPanel {

    private static final String DEMO_START = "Go under Help > License Manager to provide a valid license.";

    private List<Action> actions;
    private Application application;
    private View view;

    public StartPanel(Application application, View view) {
        super(new BorderLayout());
        this.application = application;
        this.view = view;
        this.actions = createActions();
        layoutComponents();
        setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
    }

    private List<Action> createActions() {
        List<Action> actions = new ArrayList<Action>();
        if(ActionManager.getInstance().contains(NEW_CASE)){
            actions.add(ActionManager.getInstance().get(NEW_CASE));
        }
        actions.add(ActionManager.getInstance().get(OPEN_CASE));
        
        if(ActionManager.getInstance().contains(EXIT)){
            actions.add(ActionManager.getInstance().get(EXIT));
        }

        return actions;
    }

    public void layoutComponents() {
        JPanel topPanel = new JPanel(new BorderLayout()); 
        JPanel bottomPanel = new JPanel(new BorderLayout()); 
        
        topPanel.add(createBannerPanel(), BorderLayout.NORTH);
        topPanel.add(application.createAdPanel(), BorderLayout.CENTER);

        bottomPanel.add(createProjectActionsPanel(), BorderLayout.CENTER);
        bottomPanel.add(application.createVersionPanel(), BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
    }

    private JComponent createBannerPanel() {
        JLabel banner = new JLabel(application.getBannerIcon());
        return banner;
    }

    protected JPanel createProjectActionsPanel() {
        final ImageIcon BG_IMAGE = (ImageIcon) application.getBgIcon();
        JPanel containerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                setOpaque(false);
                g.drawImage(BG_IMAGE.getImage(), (getWidth() - BG_IMAGE.getImage().getWidth(null)) - 10, getHeight() - BG_IMAGE.getImage().getHeight(null), null);
                super.paintComponent(g);
            }
        };

        int width = actions.size() >= 4 ? 700 : BG_IMAGE.getImage().getWidth(null);
        int height = 260 * (((actions.size() - 1) / 3) + 1);
        containerPanel.setPreferredSize(new Dimension(width, height));

        JPanel buttonsPanel = createButtonsPanel();

        JPanel recentPanel = createRecentPanel();

        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBorder(BorderFactory.createTitledBorder("Select an action"));
        titlePanel.setOpaque(false);
        titlePanel.add(buttonsPanel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        titlePanel.add(recentPanel, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(10, 10, 10, 10), 0, 0));

        containerPanel.add(titlePanel, BorderLayout.CENTER);
        containerPanel.add(getDemoLabel(), BorderLayout.SOUTH);
        return containerPanel;
    }

    private JPanel createRecentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        List<String> items = RecentItems.getInstance().getItems();
        if (items.isEmpty()) {
            JLabel label = new JLabel(RecentItems.NO_ITEMS);
            panel.add(label);
        } else {
            List<JComponent> buttons = new ArrayList<>();
            Icon CASE_ICON = ResourcesUtil.getIcon(ApplicationInfo.getVendor().toLowerCase() + ".case");
            for (final String item : items) {
                JButton button = new JButton((new AbstractAction(truncateItem(item), CASE_ICON) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (view.getController().allowActionsOnRunning(false)) {
                            SwingUtilities.getWindowAncestor((Component) e.getSource()).dispose();
                            view.getController().openCase(OpenOptions.file(new File(item), OpenMode.CHECK_FOLDER_ASK_USER));
                        }
                    }
                }));
                buttons.add(button);
            }
            panel.add(UiUtil.getCommandColumnToolbar(buttons));
        }
        
        return panel;
    }

    private String truncateItem(String projectName) {
        int MAX_LEN = 30;
        String path = projectName;
        if (path.length() <= MAX_LEN) {
            return path;
        } else {
            return "..." + path.substring(path.length() - MAX_LEN, path.length());
        }
    }

    private JLabel getDemoLabel() {
        String licenseErrorMessage = System.getProperty(LICENSE_ERROR_MESSAGE, null);
        if (licenseErrorMessage != null && !licenseErrorMessage.isEmpty()) {
            JLabel label = new JLabel("<html>" + licenseErrorMessage + "<br>" + DEMO_START + "</html>");
            label.setForeground(new Color(0xff0000));
            return label;
        } else {
            return new JLabel();
        }
    }

    private JPanel createButtonsPanel() {
        List<JButton> buttons = createButtons();

        int colNumber = 1;
        int rows = ((buttons.size() - 1) / colNumber) + 1;
        int cols = Math.min(buttons.size(), colNumber);

        JPanel panel = new JPanel(new GridLayout(rows, cols, 0, 10));
        panel.setOpaque(false);

        for (JButton button : buttons) {
            button.setName("suite." + button.getText());
            // button.setHorizontalTextPosition(JButton.CENTER);
            // button.setVerticalTextPosition(JButton.BOTTOM);
            button.setFocusable(false);
            panel.add(button);
        }
        return panel;
    }

    private List<JButton> createButtons() {
        List<JButton> buttons = new ArrayList<JButton>();
        for (Action action : actions) {
            final JButton button = new JButton();
            button.setAction(action);
            // button.setOpaque(true);
            buttons.add(button);
        }
        return buttons;
    }

}
