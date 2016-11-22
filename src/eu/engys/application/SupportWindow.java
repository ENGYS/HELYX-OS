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

package eu.engys.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import eu.engys.util.ApplicationInfo;
import eu.engys.util.Symbols;
import eu.engys.util.ui.UiUtil;

public class SupportWindow {

    private static final String MESSAGE = "If you require technical assistance with HELYX-OS and OPENFOAM" + Symbols.COPYRIGHT + ", ENGYS offers a dedicated user support package. For more information please contact ";
    private static final String SUBJECT = "HELYX-OS%20Info%20Request";
    private final String disclaimer;

    private JWindow window;
    private final Icon vendorIcon;
    private final Icon applicationIcon;

    public SupportWindow(Icon vendorIcon, Icon applicationIcon, String disclaimer) {
        this.vendorIcon = vendorIcon;
        this.applicationIcon = applicationIcon;
        this.disclaimer = disclaimer;
        createWindow();
    }

    private void createWindow() {
        window = new JWindow(UiUtil.getActiveWindow());
        window.getContentPane().setLayout(new BorderLayout());
        window.getContentPane().add(createMainPanel(), BorderLayout.CENTER);
        window.setSize(420, 320);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        mainPanel.add(createNorthPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createCloseButtonPanel(), BorderLayout.SOUTH);
        return mainPanel;
    }

    private JPanel createNorthPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(getImage(vendorIcon), BorderLayout.WEST);
        panel.add(getImage(applicationIcon), BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JLabel getImage(Icon imageIcon) {
        JLabel label = new JLabel(imageIcon);
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        return label;
    }

    private JPanel createCloseButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.WHITE);
        panel.add(new JButton(new AbstractAction("Close") {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.dispose();
            }
        }));
        return panel;
    }

    private JPanel createCenterPanel() {
        JLabel vers = center("<html>" + ApplicationInfo.getVersion() + "</html>", 20f, Color.BLACK);
        JLabel copy = left("<html>" + ApplicationInfo.getCopyright() + "</html>", 10f, Color.BLACK);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(vers, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(30, 10, 0, 10), 0, 0));
        infoPanel.add(copy, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 30, 10), 0, 0));

        JTextPane textPane = createTextPane();
        JScrollPane scrollPane = new JScrollPane(textPane);

        JPanel textPanePanel = new JPanel(new BorderLayout());
        textPanePanel.setBackground(Color.WHITE);
        textPanePanel.add(scrollPane, BorderLayout.CENTER);
        textPanePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(textPanePanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private JTextPane createTextPane() {
        JTextPane textPane = new JTextPane();
        textPane.setText(MESSAGE + ApplicationInfo.getMail() + ".\n\n" + disclaimer);
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_LEFT);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        textPane.setCaretPosition(0);
        return textPane;
    }

    private JButton createMailButton() {
        final JButton button = new JButton(new AbstractAction(ApplicationInfo.getMail()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                // MailManagerSupport.mail(TO, SUBJECT);
            }
        });
        button.setForeground(Color.BLUE);
        return button;
    }

    private JLabel center(String text, float size, Color color) {
        JLabel label = new JLabel(text) {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D graphics2d = (Graphics2D) g;
                graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(label.getFont().deriveFont(size));
        label.setForeground(color);
        return label;
    }

    private JLabel left(String text, float size, Color color) {
        JLabel label = new JLabel(text) {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D graphics2d = (Graphics2D) g;
                graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setFont(label.getFont().deriveFont(size));
        label.setForeground(color);
        return label;
    }
}
