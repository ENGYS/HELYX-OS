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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.EnumSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableModel;

public class CopyPasteSupport {

    private static final String PASTE_ACTION = "Paste";
    private static final String COPY_ACTION = "Copy";

    public static void addSupportTo(JTable table) {
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
        // Identifying the copy KeyStroke user can modify this
        // to copy on some other Key combination.
        KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
        // Identifying the Paste KeyStroke user can modify this
        // to copy on some other Key combination.

        CopyPasteListener cpl = new CopyPasteListener(table);

        table.registerKeyboardAction(cpl, COPY_ACTION, copy, JComponent.WHEN_FOCUSED);
        table.registerKeyboardAction(cpl, PASTE_ACTION, paste, JComponent.WHEN_FOCUSED);

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
            if (e.getActionCommand().equals(COPY_ACTION)) {
                copy();
            }
            if (e.getActionCommand().equals(PASTE_ACTION)) {
                RowSorter<? extends TableModel> sorter = table.getRowSorter();
                List<? extends SortKey> sortKeys = null;
                if (sorter != null ) {
                    sortKeys = sorter.getSortKeys();
                    sorter.setSortKeys(null);
                }
                paste();
                if (sorter != null && sortKeys != null ) {
                    sorter.setSortKeys(sortKeys);
                }
            }
        }

        private void copy() {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringBuffer sbf = new StringBuffer();
            // Check to ensure we have selected only a contiguous block of cells
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

        private void paste() {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            
            int[] selRows = table.getSelectedRows();
            int[] selCols = table.getSelectedColumns();

            int nRows = selRows.length;
            int nCols = selCols.length;

            try {
                String copiedString = (String) clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor);
//                System.out.println("String is:" + copiedString);

                String[][] data = stringToArray(copiedString);
                Structure copyStructure = decodeStructureOfData(data);
                Structure pasteStructure = decodeStructureOfData(new String[nRows][nCols]);

                if (copyStructure == Structure.ERROR) {
                    JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Invalid Paste Selection", "Invalid Paste Selection", JOptionPane.ERROR_MESSAGE);
                } else {
//                    System.out.println(String.format("Paste %s -> %s (%d x %d -> %d x %d)", copyStructure, pasteStructure, data.length, data[0].length, selRows.length, selCols.length));
                    switch (copyStructure) {
                    case CELL:
                        for (int i = 0; i < selRows.length; i++) {
                            for (int j = 0; j < selCols.length; j++) {
                                table.setValueAt(getEditedValue(data[0][0]), selRows[i], selCols[j]);
                            }
                        }
                        break;

                    case ROW:
                        if (EnumSet.range(Structure.CELL,Structure.CELL).contains(pasteStructure)) {
                            //                          int retVal = JOptionPane.showConfirmDialog(null, "The content of the clipboard is bigger than the range selected.\nDo you want to insert anyway?", "Warning", JOptionPane.YES_NO_OPTION);
                            //                          if (retVal == JOptionPane.YES_OPTION) {
                            int rows = Math.max(selRows.length, data.length);
                            int cols = Math.max(selCols.length, data[0].length);

                            for (int i = 0; i < rows; i++) {
                                for (int j = 0; j < cols; j++) {
                                    int r = selRows[0] + i;
                                    int c = selCols[0] + j;
                                    if (inRange(r, c)) {
                                        Object v = getEditedValue(data[0][j]);
                                        System.out.println("[" + r + ", " + c +"] < " + v);
                                        table.setValueAt(v, r, c);
                                    }
                                }
                                //                              }
                            }
                        } else {
                            for (int i = 0; i < selRows.length; i++) {
                                for (int j = 0; j < selCols.length; j++) {
                                    table.setValueAt(getEditedValue(data[0][j]), selRows[i], selCols[j]);
                                }
                            }
                        }
                        break;

                    case COLUMN:
                        //TODO estendere funzionamento come per la riga
                        for (int i = 0; i < selRows.length; i++) {
                            for (int j = 0; j < selCols.length; j++) {
                                table.setValueAt(getEditedValue(data[j][0]), selRows[i], selCols[j]);
                            }
                        }
                        break;

                    case MATRIX:
                        //TODO estendere funzionamento come per la riga
                        for (int i = 0; i < selRows.length; i++) {
                            for (int j = 0; j < selCols.length; j++) {
                                table.setValueAt(getEditedValue(data[i][j]), selRows[i], selCols[j]);
                            }
                        }
                        break;

                    default:
                        break;
                    }
                    table.repaint();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        public boolean inRange(int r, int c) {
            return r < table.getRowCount() && c < table.getColumnCount();
        }

        private Object getEditedValue(String string) {
            try {
                return Double.valueOf(string);
            } catch (NumberFormatException e) {
                return 0D;
            }
        }

        enum Structure {
            CELL, ROW, COLUMN, MATRIX, ERROR
        }

        private Structure decodeStructureOfData(String[][] data) {
            if (data != null) {
                if (data.length == 1) {
                    if (data[0].length == 1) {
                        return Structure.CELL;
                    } else {
                        return Structure.ROW;
                    }
                } else if (data.length > 1) {
                    if (data[0].length == 1) {
                        return Structure.COLUMN;
                    } else if (data[0].length > 1) {
                        return Structure.MATRIX;
                    } else {
                        return Structure.ERROR;
                    }
                } else {
                    return Structure.ERROR;
                }
            } else {
                return Structure.ERROR;
            }
        }

        private String[][] stringToArray(String copiedString) {
            StringTokenizer rowTokenizer = new StringTokenizer(copiedString, "\n");
            String[][] data = new String[rowTokenizer.countTokens()][];
            for (int i = 0; rowTokenizer.hasMoreTokens(); i++) {
                String rowstring = rowTokenizer.nextToken();
                StringTokenizer colTokenizer;
                if (copiedString.contains(",")) {
                    colTokenizer = new StringTokenizer(rowstring, ",");
                } else {
                    colTokenizer = new StringTokenizer(rowstring, "\t");
                }
                data[i] = new String[colTokenizer.countTokens()];
                for (int j = 0; colTokenizer.hasMoreTokens(); j++) {
                    String value = (String) colTokenizer.nextToken();
                    data[i][j] = value.trim();
                }
            }
            return data;
            
        }
    }
}
