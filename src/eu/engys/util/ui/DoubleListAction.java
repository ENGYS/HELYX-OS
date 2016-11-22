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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import eu.engys.util.Symbols;

public class DoubleListAction extends AbstractAction {

    private final ListFieldPanel listField;
    private final ListBuilder listBuilder;

    private JDialog dialog;
    private DualList dual;

    DoubleListAction(ListFieldPanel listField, ListBuilder listBuilder) {
        super(Symbols.DOTS);
        this.listField = listField;
        this.listBuilder = listBuilder;
    }

    public void actionPerformed(ActionEvent e) {
        if (dialog == null) {
            dialog = new JDialog(SwingUtilities.getWindowAncestor(listField), listBuilder.getTitle());
            dialog.setName("dual.list.dialog");
            dialog.setModal(true);
            dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(listField);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(UiUtil.getStandardBorder());

            dual = new DualList();

            ArrayList<JComponent> buttons = new ArrayList<JComponent>();
            JButton okButton = new JButton(new AbstractAction("OK") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listField.setValues(dual.getDestinationElements());
                    dialog.setVisible(false);
                }
            });
            okButton.setName("OK");
            buttons.add(okButton);

            JButton cancelButton = new JButton(new AbstractAction("Cancel") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });
            cancelButton.setName("Cancel");
            buttons.add(cancelButton);

            JComponent buttonsPanel = UiUtil.getCommandRow(buttons);

            mainPanel.add(dual, BorderLayout.CENTER);
            mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

            dialog.getContentPane().add(mainPanel);
            dialog.getRootPane().setDefaultButton(okButton);
        }

        String[] destination = listField.getValues();
        String[] source = listBuilder.getSourceElements();

        List<String> destinationList = Arrays.asList(destination);
        List<String> sourceList = new ArrayList<>();
        
        for (String el : source) {
            if (!destinationList.contains(el)) {
                sourceList.add(el);
            }
        }
        
        dual.setSourceElements(sourceList.toArray());
        dual.setDestinationElements(destination);

        dialog.setVisible(true);
    }
}
