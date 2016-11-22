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
package eu.engys.gui.view;

import static eu.engys.util.SystemEnv.LICENSE_STATUS;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.commons.lang.StringUtils;

import eu.engys.core.controller.Controller;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.application.OpenMonitorEvent;
import eu.engys.launcher.StartUpMonitor;
import eu.engys.util.ApplicationInfo;
import eu.engys.util.MemoryWidget;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class StatusBar extends JPanel {

    private static final Icon consoleIcon = ResourcesUtil.getIcon("console.tab.icon");
    private static final Icon monitorIcon = ResourcesUtil.getIcon("application.icon");
    
    private static final String SERIAL = "Serial";
    private static final String PARALLEL = "Parallel";
    private static final String REMOTE = "REMOTE";
    private static final String LOCAL = "LOCAL";
    private static final String OPEN_STATUS_DIALOG = "Open Status Dialog";
    private static final String OPEN_TERMINAL_DIALOG = "Hide/Open Terminal Bar";
    private static final String TEXT_OFFSET = " ";
    private static final String DOTS = "...";

    private static final String DEFAULT_TEXT = StringUtils.repeat(" ", 10);
    private static final int MAX_PROJECT_PATH_WIDTH = 600;
    
    private JButton terminalButton;
    private JLabel nameLabel;
    private JLabel versionLabel;
    private JLabel caseLabel;
    private JLabel typeLabel;
    private JLabel licenseLabel;
    private JButton monitorButton;
    private MemoryWidget memoryWidget;
    private Controller controller;

    public StatusBar(Controller controller) {
        super(new BorderLayout());
        StartUpMonitor.info("Loading Status Bar");
        this.controller = controller;
        layoutComponents();
    }
    
    private void layoutComponents() {
        setOpaque(false);

        terminalButton = createTerminalButton();
        nameLabel = createLabel(ApplicationInfo.getName());
        versionLabel = createLabel(ApplicationInfo.getVersion() + " " + "[" + ApplicationInfo.getBuildDate() + "]");
        caseLabel = createLabel(DEFAULT_TEXT);
        typeLabel = createLabel(DEFAULT_TEXT);
        licenseLabel = createLabel("License: " + System.getProperty(LICENSE_STATUS));
        
        monitorButton = createMonitorButton();
        memoryWidget = createMemoryWidget();
        
        JToolBar leftPanel = UiUtil.getToolbarWrapped();
        leftPanel.add(terminalButton);
        leftPanel.add(nameLabel);
        leftPanel.addSeparator();
        leftPanel.add(versionLabel);
        if (System.getProperty(LICENSE_STATUS) != null) {
            leftPanel.addSeparator();
            leftPanel.add(licenseLabel);
        }
        leftPanel.addSeparator();
        leftPanel.add(caseLabel);
        leftPanel.addSeparator();
        leftPanel.add(typeLabel);

        JToolBar rightPanel = UiUtil.getToolbarWrapped();
        rightPanel.addSeparator();
        rightPanel.add(monitorButton);
        rightPanel.addSeparator();
        rightPanel.add(memoryWidget);

        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private MemoryWidget createMemoryWidget() {
        MemoryWidget mem = new MemoryWidget();
        setupDimensions(mem, MemoryWidget.PROTOTYPE_STRING);
        return mem;
    }

    private JButton createMonitorButton() {
        ViewAction monitorAction = new ViewAction(monitorIcon, OPEN_STATUS_DIALOG) {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventManager.triggerEvent(this, new OpenMonitorEvent());
            }

        };
        JButton monitorButton = new JButton(monitorAction) {
            public java.awt.Point getToolTipLocation(MouseEvent event) {
                return new java.awt.Point(0, -20);
            }
        };
        return monitorButton;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(TEXT_OFFSET + text + TEXT_OFFSET);
        label.setBorder(BorderFactory.createEmptyBorder());
        
        setupDimensions(label, label.getText());
        
        return label;
    }

    private void setupDimensions(JComponent c, String text) {
        Dimension d = new Dimension(c.getFontMetrics(c.getFont()).stringWidth(text), 20);
        c.setPreferredSize(d);
        c.setMaximumSize(d);
    }

    private JButton createTerminalButton() {
        ViewAction terminalAction = new ViewAction(consoleIcon, OPEN_TERMINAL_DIALOG) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.getTerminalManager().toggleVisibility();
                }
            }

        };
        JButton button = new JButton(terminalAction) {
            public java.awt.Point getToolTipLocation(MouseEvent event) {
                return new java.awt.Point(0, -20);
            }
        };
        return button;
    }

    public void load(final Model model) {
        ExecUtil.invokeLater(new Runnable() {
            public void run() {
                openFOAMProject project = model.getProject();
                
                if (project != null) {
                    String parallel = project.isParallel() ? PARALLEL + " (" + project.getProcessors() + ")" : SERIAL;
                    String remote = model.getSolverModel().isRemote() ? REMOTE : LOCAL;

                    typeLabel.setText(TEXT_OFFSET + parallel + TEXT_OFFSET + remote + TEXT_OFFSET);
                    caseLabel.setText(TEXT_OFFSET + getProjectPath(project.getBaseDir().getAbsolutePath()) + TEXT_OFFSET);
                    caseLabel.setToolTipText(project.getBaseDir().getAbsolutePath());
                } else {
                    typeLabel.setText("");
                    caseLabel.setText("");
                    caseLabel.setToolTipText("");
                }
                
                setupDimensions(caseLabel, caseLabel.getText());
                setupDimensions(typeLabel, typeLabel.getText());

                revalidate();
                repaint();
            }
        });
    }

    private String getProjectPath(String projectPath) {
        int projectPathWidth = getCasePathWidth(projectPath);
        if (projectPathWidth < MAX_PROJECT_PATH_WIDTH - 5) {
            return projectPath;
        } else {
            return truncateCasePath(projectPath);
        }
    }

    private String truncateCasePath(String path) {
        int width = getCasePathWidth(DOTS + path);
        if (width < MAX_PROJECT_PATH_WIDTH - 5) {
            return DOTS + path;
        } else {
            return truncateCasePath(path.substring(1));
        }
    }

    private int getCasePathWidth(String projectPath) {
        return caseLabel.getFontMetrics(caseLabel.getFont()).stringWidth(TEXT_OFFSET + projectPath + TEXT_OFFSET);
    }

    public void updateLicenseField() {
        licenseLabel.setText(TEXT_OFFSET + "License: " + System.getProperty(LICENSE_STATUS) + TEXT_OFFSET);
    }

}
