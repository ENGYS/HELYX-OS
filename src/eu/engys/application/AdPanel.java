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

package eu.engys.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import eu.engys.util.Symbols;
import eu.engys.util.Util;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;

public class AdPanel extends JPanel {

    private static final String ENGYS = "ENGYS" + Symbols.REGISTERED;

    public static final Icon FAQ = ResourcesUtil.getIcon("helyxos.faq");
    public static final Icon ENGYS_PRODUCTS = ResourcesUtil.getIcon("helyxos.products");
    public static final Icon HELYX_BOX = ResourcesUtil.getIcon("helyxos.helyx.box");
    public static final Icon HELYX_OS_BOX = ResourcesUtil.getIcon("helyxos.helyxos.box");
    public static final Icon ELEMENTS_BOX = ResourcesUtil.getIcon("helyxos.elements.box");
    public static final Icon FULL_LOGO = ResourcesUtil.getIcon("engys.logo.full");

    public AdPanel() {
        super(new BorderLayout());
        layoutComponents();

        setBorder(BorderFactory.createTitledBorder(ENGYS + " products"));
    }

    private void layoutComponents() {
        createCentralPanel();
        createSouthPanel();
    }

    private void createCentralPanel() {
        JPanel productsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        productsPanel.add(new JLabel(HELYX_BOX));
        productsPanel.add(new JLabel(HELYX_OS_BOX));
        productsPanel.add(new JLabel(ELEMENTS_BOX));

        add(productsPanel, BorderLayout.CENTER);
    }

    private void createSouthPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.add(createShowImageButton("FAQ", FAQ));
        buttonsPanel.add(createShowImageButton("Products Comparison", ENGYS_PRODUCTS));
        buttonsPanel.add(createOpenEngysSiteButton());

        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.add(new JLabel(FULL_LOGO, JLabel.CENTER));
        southPanel.add(buttonsPanel);

        add(southPanel, BorderLayout.SOUTH);

    }

    private JButton createShowImageButton(final String title, final Icon image) {
        return new JButton(new AbstractAction(title) {

            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(UiUtil.getActiveWindow(), title);
                dialog.setModal(true);
                dialog.setSize(1000, 600);
                dialog.setLocationRelativeTo(null);
                dialog.getContentPane().setLayout(new BorderLayout());

                JLabel label = new JLabel(image);
                label.setBackground(Color.WHITE);

                JScrollPane jsp = new JScrollPane(label);
                jsp.setOpaque(false);
                jsp.getViewport().setOpaque(false);
                jsp.getVerticalScrollBar().setUnitIncrement(20);
                dialog.getContentPane().add(jsp, BorderLayout.CENTER);

                dialog.setVisible(true);
            }
        });
    }

    private JButton createOpenEngysSiteButton() {
        return new JButton(new AbstractAction(ENGYS + " Website") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Util.openWebpage(new URL("http://www.engys.com"));
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}
