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
package eu.engys.gui.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.SwingConstants;

import org.apache.commons.lang.StringUtils;

import eu.engys.core.executor.TerminalManager;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.application.OpenMonitorEvent;
import eu.engys.util.ApplicationInfo;
import eu.engys.util.MemoryWidget;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;

public class StatusBar extends JPanel {

    private static final Icon consoleIcon = ResourcesUtil.getIcon("console.tab.icon");
    private static final Icon monitorIcon = ResourcesUtil.getIcon("console.tab.icon");
    
    private static final String DEFAULT_TEXT = StringUtils.repeat(" ", 10);
    
    private JButton terminalButton;
    private JLabel nameLabel;
    private JLabel versionLabel;
    private JLabel caseLabel;
    private JLabel typeLabel;
    private JLabel licenseLabel;
    private JButton monitorButton;
    private MemoryWidget memoryWidget;

    public StatusBar() {
        super();
        layoutComponents();
    }
    
    private void layoutComponents() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        terminalButton = createTerminalButton();
        nameLabel = createLabel(ApplicationInfo.getName());
        versionLabel = createLabel(ApplicationInfo.getVersion() + " " + "[" + ApplicationInfo.getBuildDate() + "]");
        caseLabel = createLabel(DEFAULT_TEXT);
        typeLabel = createLabel(DEFAULT_TEXT);
        licenseLabel = createLabel(DEFAULT_TEXT);
        
        monitorButton = createMonitorButton();
        memoryWidget = createMemoryWidget();
        
        add(terminalButton);
        addSeparator();
        add(nameLabel);
        addSeparator();
        add(versionLabel);
        addSeparator();
        add(caseLabel);
        addSeparator();
        add(typeLabel);
        addSeparator();
        add(licenseLabel);
        add(Box.createHorizontalGlue());
        add(monitorButton);
        addSeparator();
        add(memoryWidget);
    }

    private void addSeparator() {
        Separator separator = new JToolBar.Separator();
        separator.setOrientation(SwingConstants.VERTICAL);
        add(separator);
    }

    private MemoryWidget createMemoryWidget() {
        MemoryWidget mem = new MemoryWidget();
        setupDimensions(mem, MemoryWidget.PROTOTYPE_STRING);
        return mem;
    }

    private JButton createMonitorButton() {
        JButton button = new JButton(monitorIcon);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventManager.triggerEvent(this, new OpenMonitorEvent());
            }
        });
        return button;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
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
        JButton button = new JButton(consoleIcon);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TerminalManager.getInstance().toggleVisibility();
            }
        });
        return button;
    }

    public void load(final Model model) {
        ExecUtil.invokeLater(new Runnable() {
            public void run() {
                openFOAMProject project = model.getProject();
                if (project != null) {
                    caseLabel.setText(project.getBaseDir().getAbsolutePath());
                    typeLabel.setText(project.isParallel() ? "Parallel" : "Serial");
                } else {
                    caseLabel.setText("");
                    typeLabel.setText("");
                }
                
                setupDimensions(caseLabel, caseLabel.getText());
                setupDimensions(typeLabel, typeLabel.getText());

                revalidate();
                repaint();
            }
        });
        
    }

    public void updateLicenseField() {
    }

}
