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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Transient;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ChooserPanel extends JPanel implements ActionListener {

    public static final String NONE = "NONE";

    private ButtonGroup buttonGroup = new ButtonGroup();
    private Map<String, AbstractButton> buttons = new HashMap<String, AbstractButton>();
    boolean propagateEnable;

    public ChooserPanel(String title, boolean propagateEnable) {
        super(new GridLayout(0, 1, 2, 2));
        this.propagateEnable = propagateEnable;
        if (title != null && !title.isEmpty()) {
            setBorder(BorderFactory.createTitledBorder(title));
        }
    }

    public ChooserPanel(String title) {
        this(title, true);
    }

    @Override
    @Transient
    public Dimension getPreferredSize() {
        if (buttons.isEmpty()) {
            return new Dimension(4, 4);
        }
        return super.getPreferredSize();
    }

    public String getSelectedState() {
        if (buttonGroup.getSelection() != null)
            return buttonGroup.getSelection().getActionCommand();
        else
            return NONE;
    }

    public boolean hasSelection() {
        return buttonGroup.getSelection() != null;
    }

    public void selectFirst() {
        if (!buttons.isEmpty()) {
            buttonGroup.getElements().nextElement().setSelected(true);
        }
    }

    public void select(String targetField) {
        if (buttons.containsKey(targetField)) {
            buttons.get(targetField).setSelected(true);
        }
    }

    public void unselect(String targetField) {
        if (!buttons.isEmpty()) {
            for (String key : buttons.keySet()) {
                if (!key.equals(targetField)) {
                    buttons.get(key).setSelected(true);
                    return;
                }
            }
        }
    }

    public void selectNone() {
        if (!buttons.isEmpty()) {
            buttonGroup.clearSelection();
        }
    }

    public void reset() {
        buttonGroup.clearSelection();
        setEnabled(true);
    }

    private JRadioButton createChoice(String choice, int offset) {
        JRadioButton radio = new JRadioButton(choice);
        radio.setName(choice);
        radio.setActionCommand(choice);
        radio.addActionListener(this);
        radio.setBorder(BorderFactory.createEmptyBorder(0, offset, 0, 0));
        return radio;
    }

    public void addChoice(String choice, int offset) {
        JRadioButton radio = createChoice(choice, offset);
        add(radio);
        buttonGroup.add(radio);
        buttons.put(choice, radio);
    }

    public void addChoice(String choice) {
        addChoice(choice, UiUtil.ONE_SPACE);
    }

    public void addChoices(int offset, String... choices) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (String choice : choices) {
            JRadioButton radio = createChoice(choice, offset);
            p.add(radio);
            buttonGroup.add(radio);
            buttons.put(choice, radio);
        }
        add(p);
    }

    public AbstractButton getButton(String key) {
        return buttons.get(key);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof AbstractButton) {
            firePropertyChange("selection", false, true);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (propagateEnable) {
            for (String key : buttons.keySet()) {
                buttons.get(key).setEnabled(enabled);
            }
        }
    }
}
