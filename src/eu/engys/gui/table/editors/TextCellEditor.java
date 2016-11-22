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
package eu.engys.gui.table.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.textfields.PromptTextField;

public class TextCellEditor extends AbstractCellEditor implements ActionListener, TableCellEditor {

    private static final Icon ICON_EDIT = ResourcesUtil.getIcon("edit.icon");
    private JPanel component;
    private PromptTextField textField;
    private JButton button;
    private String label;

    public TextCellEditor() {
        component = new JPanel() {
            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
                return textField.processKeyBinding(ks, e, WHEN_FOCUSED, pressed);
            }

            @Override
            public void validate() {
                super.validate();
                repaint();
            }
        };
        textField = new PromptTextField();
        textField.addActionListener(this);
        textField.setBorder(BorderFactory.createEmptyBorder());
        textField.setBackground(Color.ORANGE);
        button = new JButton(ICON_EDIT);
        button.addActionListener(this);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        component.setLayout(new GridBagLayout());
        component.setBackground(Color.ORANGE);
        component.add(textField, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        component.add(button, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == textField) {
            fireEditingStopped();
        } else if (evt.getSource() == button) {
            TextEditor dialog = new TextEditor();
            int result = dialog.showEditor(component, label, textField.getText());
            if (result == TextEditor.OK_OPTION) {
                textField.setText(dialog.getText());
                fireEditingStopped();
            } 
        }
    }

    public Object getCellEditorValue() {
        return textField.getText();
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.label = table.getColumnName(column);
        
        textField.setText(value == null ? "" : value.toString());
        textField.selectAll();
        component.validate();
        return component;
    }
    
    class TextEditor {
        static final int OK_OPTION = 0;
        static final int CANCEL_OPTION = 0;
        
        private JTextArea area = new JTextArea();
        private JButton okButton = new JButton(new OkAction());
        private JButton cancelButton = new JButton(new CancelAction());
        private int result;
        private JDialog dialog;
        
        public String getText() {
            return area.getText();
        }

        public int showEditor(JComponent parent, String title, String text) {
            dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), JDialog.DEFAULT_MODALITY_TYPE);
            dialog.setTitle(title);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    new CancelAction().actionPerformed(null);
                }
            });
            dialog.getContentPane().add(new JLabel("Enter a description"), BorderLayout.NORTH);
            dialog.getContentPane().add(new JScrollPane(area), BorderLayout.CENTER);
            dialog.getContentPane().add(UiUtil.getCommandRow(okButton, cancelButton), BorderLayout.SOUTH);
            dialog.setSize(300, 200);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            return result;
        }
        
        class OkAction extends AbstractAction {
            public OkAction() {
                super("OK");
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                result = OK_OPTION;
                if (dialog != null) {
                    dialog.dispose();
                }
            }
            
        }
        class CancelAction extends AbstractAction {
            public CancelAction() {
                super("Cancel");
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                result = CANCEL_OPTION;
                if (dialog != null) {
                    dialog.dispose();
                }
            }
            
        }
    }
}