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


package eu.engys.suite;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.google.inject.Inject;

public class SuitePanel extends JPanel {

    public static final ImageIcon BANNER = new ImageIcon(SuitePanel.class.getClassLoader().getResource("eu/engys/resources/elements_banner.png"));
    public static final ImageIcon BG_IMAGE = new ImageIcon(SuitePanel.class.getClassLoader().getResource("eu/engys/resources/elements_startup.png"));

    private List<Action> actions;
	private String title;

    @Inject
    public SuitePanel(List<Action> actions, String title) {
    	super(new BorderLayout());
        this.actions = actions;
        this.title = title;
        layoutComponents();
    }

    public void layoutComponents() {
        JPanel topPanel = createTopPanel();
        JPanel centerPanel = createCenterPanel();
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                setOpaque(false);
                g.drawImage(BANNER.getImage(), (getWidth() - BANNER.getImage().getWidth(null)) / 2, 0, null);
                super.paintComponent(g);
            }
        };
        panel.setPreferredSize(new Dimension(BANNER.getImage().getWidth(null), BANNER.getImage().getHeight(null)));
        return panel;
    }

    protected JPanel createCenterPanel() {
        JPanel containerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                setOpaque(false);
                g.drawImage(BG_IMAGE.getImage(), (getWidth() - BG_IMAGE.getImage().getWidth(null)) - 10, getHeight() - BG_IMAGE.getImage().getHeight(null), null);
                super.paintComponent(g);
            }
        };
        containerPanel.setLayout(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        int width = actions.size() >= 3 ? 700 : BG_IMAGE.getImage().getWidth(null);
        int height= 260 * (((actions.size() - 1) / 3) + 1);
        containerPanel.setPreferredSize(new Dimension(width, height));

        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBorder(BorderFactory.createTitledBorder(title));
        titlePanel.setOpaque(false);
        containerPanel.add(titlePanel, BorderLayout.CENTER);

        JPanel buttonsPanel = createButtonsPanel();
        titlePanel.add(buttonsPanel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

        return containerPanel;
    }

    private JPanel createButtonsPanel() {
        List<JButton> buttons = createButtons();

        int colNumber = 3;
        int rows = ((buttons.size() - 1) / colNumber) + 1;
        int cols = Math.min(buttons.size(), colNumber);

        JPanel panel = new JPanel(new GridLayout(rows, cols, 30, 30));
        panel.setOpaque(false);

        for (JButton button : buttons) {
            button.setName("suite." + button.getText());
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setVerticalTextPosition(JButton.BOTTOM);
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
            //button.setOpaque(true);
            buttons.add(button);
        }
        return buttons;
    }

}
