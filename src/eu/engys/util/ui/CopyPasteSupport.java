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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

public class CopyPasteSupport {

    public static void addSupportTo(JTable table) {
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
        // Identifying the copy KeyStroke user can modify this
        // to copy on some other Key combination.
        KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
        // Identifying the Paste KeyStroke user can modify this
        // to copy on some other Key combination.

        CopyPasteListener cpl = new CopyPasteListener(table);

        table.registerKeyboardAction(cpl, "Copy", copy, JComponent.WHEN_FOCUSED);
        table.registerKeyboardAction(cpl, "Paste", paste, JComponent.WHEN_FOCUSED);

        table.setColumnSelectionAllowed(true);
        table.setRowSelectionAllowed(true);
    }

    static class CopyPasteListener implements ActionListener {

        private JTable table;

        public CopyPasteListener(JTable table) {
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            if (e.getActionCommand().equals("Copy")) {
                StringBuffer sbf = new StringBuffer();
                // Check to ensure we have selected only a contiguous block of
                // cells
                int numcols = table.getSelectedColumnCount();
                int numrows = table.getSelectedRowCount();

                int[] rowsselected = table.getSelectedRows();
                int[] colsselected = table.getSelectedColumns();
                if (!((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0] && numrows == rowsselected.length) && (numcols - 1 == colsselected[colsselected.length - 1]
                        - colsselected[0] && numcols == colsselected.length))) {
                    JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                for (int i = 0; i < numrows; i++) {
                    for (int j = 0; j < numcols; j++) {
                        sbf.append(table.getValueAt(rowsselected[i], colsselected[j]));
                        if (j < numcols - 1)
                            sbf.append("\t");
                    }
                    sbf.append("\n");
                }
                StringSelection stsel = new StringSelection(sbf.toString());

                clipboard.setContents(stsel, stsel);
            }
            if (e.getActionCommand().equals("Paste")) {
                System.out.println("Trying to Paste");
                int[] selRows = table.getSelectedRows();
                int[] selCols = table.getSelectedColumns();

                int startRow = selRows[0];
                int startCol = selCols[0];

                try {
                    String copiedString = (String) clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor);
                    System.out.println("String is:" + copiedString);

                    String[][] data = stringToArray(copiedString);
                    TypeOfCopy type = decodeTypeOfCopy(data, selRows, selCols);

                    if (type == TypeOfCopy.ERROR) {
                        JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Invalid Paste Selection", "Invalid Paste Selection", JOptionPane.ERROR_MESSAGE);
                    } else {
                        System.out.println(String.format("Paste %d x %d -> %d x %d", data.length, data[0].length, selRows.length, selCols.length));
                        switch (type) {
                        case CELL:
                            for (int i = 0; i < selRows.length; i++) {
                                for (int j = 0; j < selCols.length; j++) {
                                    table.setValueAt(getEditedValue(data[0][0]), selRows[i], selCols[j]);
                                }
                            }
                            break;

                        case ROW:
                            for (int i = 0; i < selRows.length; i++) {
                                for (int j = 0; j < selCols.length; j++) {
                                    table.setValueAt(getEditedValue(data[0][j]), selRows[i], selCols[j]);
                                }
                            }
                            break;

                        case COLUMN:
                            for (int i = 0; i < selRows.length; i++) {
                                for (int j = 0; j < selCols.length; j++) {
                                    table.setValueAt(getEditedValue(data[j][0]), selRows[i], selCols[j]);
                                }
                            }
                            break;

                        case MATRIX:
                            for (int i = 0; i < selRows.length; i++) {
                                for (int j = 0; j < selCols.length; j++) {
                                    table.setValueAt(getEditedValue(data[i][j]), selRows[i], selCols[j]);
                                }
                            }
                            break;

                        default:
                            break;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        private Object getEditedValue(String string) {
            // Class klass = table.getModel().getColumnClass(col);
            // System.out.println("CopyPasteSupport.CopyPasteListener.getEditedValue() "+klass);
            try {
                return Double.valueOf(string);
            } catch (NumberFormatException e) {
                return 0;
            }
            // Object value;

            // if (klass.isInstance(Double.class)) {
            // value = Double.valueOf(string);
            // } else {
            // value = string;
            // }
        }

        enum TypeOfCopy {
            CELL, ROW, COLUMN, MATRIX, ERROR
        }

        private TypeOfCopy decodeTypeOfCopy(String[][] data, int[] selRows, int[] selCols) {
            if (data != null) {
                if (data.length == 1) {
                    if (data[0].length == 1) {
                        return TypeOfCopy.CELL;
                    } else if (data[0].length == selCols.length) {
                        return TypeOfCopy.ROW;
                    } else {
                        return TypeOfCopy.ERROR;
                    }
                } else if (data.length == selRows.length) {
                    if (data[0].length == 1) {
                        return TypeOfCopy.COLUMN;
                    } else if (data[0].length == selCols.length) {
                        return TypeOfCopy.MATRIX;
                    } else {
                        return TypeOfCopy.ERROR;
                    }
                } else {
                    return TypeOfCopy.ERROR;
                }
            } else {
                return TypeOfCopy.ERROR;
            }
        }

        private String[][] stringToArray(String copiedString) {
            StringTokenizer rowTokenizer = new StringTokenizer(copiedString, "\n");
            String[][] data = new String[rowTokenizer.countTokens()][];
            for (int i = 0; rowTokenizer.hasMoreTokens(); i++) {
                String rowstring = rowTokenizer.nextToken();
                StringTokenizer colTokenizer = new StringTokenizer(rowstring, "\t");
                data[i] = new String[colTokenizer.countTokens()];
                for (int j = 0; colTokenizer.hasMoreTokens(); j++) {
                    String value = (String) colTokenizer.nextToken();
                    data[i][j] = value;
                }
            }
            return data;
        }
    }
}
