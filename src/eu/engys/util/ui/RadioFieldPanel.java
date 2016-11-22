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
package eu.engys.util.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class RadioFieldPanel extends JPanel implements ActionListener {

    public static final String PROPERTY_NAME = "value";
    
    private ButtonGroup bg = new ButtonGroup();

    public RadioFieldPanel() {
        super(new GridLayout(0, 1));
        setOpaque(false);
    }

    public void addButton(String string) {
        JRadioButton button = new JRadioButton(string);
        button.setName(string);
        button.setActionCommand(string);
        button.addActionListener(this);
        bg.add(button);
        add(button);
    }

    public void addButton(String string, String actionCommand) {
        JRadioButton button = new JRadioButton(string);
        button.setName(string);
        button.setActionCommand(actionCommand);
        button.addActionListener(this);
        bg.add(button);
        add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        firePropertyChange(PROPERTY_NAME, "", e.getActionCommand());
    }

    public String getSelectedKey() {
        return bg.getSelection() != null ? bg.getSelection().getActionCommand() : null;
    }

    public String getSelectedItem() {
        return bg.getSelection() != null ? getButton(bg.getSelection()).getText() : null;
    }

    private AbstractButton getButton(ButtonModel bm) {
        for (Enumeration<AbstractButton> e = bg.getElements(); e.hasMoreElements();) {
            AbstractButton b = e.nextElement();
            if (b.getModel().equals(bm)) {
                return b;
            }
        }
        return null;
    }

    public void select(String actionCommand) {
        for (Enumeration<AbstractButton> e = bg.getElements(); e.hasMoreElements();) {
            AbstractButton b = e.nextElement();
            if (b.getActionCommand().equals(actionCommand)) {
                bg.setSelected(b.getModel(), true);
                return;
            }
        }
    }

    public int getButtonCount() {
        return bg.getButtonCount();
    }

    public void doClick(String actionCommand) {
        for (Enumeration<AbstractButton> e = bg.getElements(); e.hasMoreElements();) {
            AbstractButton b = e.nextElement();
            if (b.getActionCommand().equals(actionCommand)) {
                b.doClick();
                return;
            }
        }
    }
}
