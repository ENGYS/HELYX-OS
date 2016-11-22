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
package eu.engys.gui.casesetup;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;

import eu.engys.core.modules.AbstractChart;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.CopyPasteSupport;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;
import eu.engys.util.ui.textfields.DoubleField;

public abstract class TableAndChartPanel extends JPanel {

    private static final int SIZE = 600;
    
    private static final String ADD_TOOLTIP = "Add a row to the table";
    private static final String REM_TOOLTIP = "Remove selected rows";
    private static final Icon ADD_ICON = ResourcesUtil.getResourceIcon("eu/engys/resources/images/table_add16.png");
    private static final Icon REM_ICON = ResourcesUtil.getResourceIcon("eu/engys/resources/images/table_delete16.png");
    public static final String ADD_ROW_BUTTON = "add.row.button";
    public static final String REM_ROW_BUTTON = "rem.row.button";
    public static final String OK_BUTTON_LABEL = "OK";
    public static final String CANCEL_BUTTON_LABEL = "Cancel";

    private JButton okButton;

    private String title;
    private String name;
    protected JDialog dialog;

    protected JTable table;
    protected AbstractChart chart;

    private TableRowSorter<TableModel> sorter;

    private JButton removeButton;

    public TableAndChartPanel(String title, String name, String domainAxisLabel, String rangeAxisLabel, boolean editable) {
        super(new BorderLayout());
        this.title = title;
        this.name = name;
        setName(name);
        layoutComponents(domainAxisLabel, rangeAxisLabel, editable);
    }

    private void layoutComponents(String domainAxisLabel, String rangeAxisLabel, boolean editable) {
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = createSplitPane();
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());

        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(bottomPanel);

        table = createTable();

        if (editable) {
            JComponent tableButtons = createTableButtons();
            topPanel.add(tableButtons, BorderLayout.NORTH);
        }

        chart = createChart(domainAxisLabel, rangeAxisLabel);

        topPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        if (chart != null) {
            bottomPanel.add(chart.getPanel(), BorderLayout.CENTER);
        }

        add(splitPane, BorderLayout.CENTER);

        JPanel buttonsPanel = createDialogButtons();
        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);

        dialog = createDialog();
    }

    private JDialog createDialog() {
        JDialog dialog = new JDialog(UiUtil.getActiveWindow(), title, ModalityType.APPLICATION_MODAL);
        dialog.setName(name);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleDialogClose_Cancel();
            }
        });

        dialog.add(this);
        dialog.setSize(SIZE, SIZE);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.getRootPane().setDefaultButton(okButton);
        return dialog;

    }

    private JSplitPane createSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setOneTouchExpandable(false);
        splitPane.setDividerLocation(SIZE/2);
        splitPane.setResizeWeight(0.5);
        return splitPane;
    }

    private JPanel createDialogButtons() {
        JPanel rightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton(new AbstractAction("OK") {

            @Override
            public void actionPerformed(ActionEvent e) {
                handleDialogClose_OK();
            }
        });
        JButton cancelButton = new JButton(new AbstractAction(CANCEL_BUTTON_LABEL) {

            @Override
            public void actionPerformed(ActionEvent e) {
                handleDialogClose_Cancel();
            }
        });
        okButton.setName(OK_BUTTON_LABEL);
        rightButtonsPanel.add(okButton);
        rightButtonsPanel.add(cancelButton);

        JPanel leftButtonsPanel = createLeftButtonsPanel();

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel.add(leftButtonsPanel);
        buttonsPanel.add(rightButtonsPanel);

        return buttonsPanel;
    }

    protected JPanel createLeftButtonsPanel() {
        return new JPanel();
    }

    private JTable createTable() {
        JTable table = new JTable() {
            public boolean editCellAt(int row, int column, EventObject e) {
                boolean result = super.editCellAt(row, column, e);
                final Component editor = getEditorComponent();
                if (e instanceof KeyEvent && editor instanceof JTextComponent) {
                    ((JTextComponent) editor).selectAll();
                }

                return result;
            }

        };
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        setupEditors(table);

        CopyPasteSupport.addSupportTo(table);
        return table;
    }

    protected JComponent createTableButtons() {
        JToolBar buttonsPanel = UiUtil.getToolbarWrapped();
        JButton addButton = new JButton(new ViewAction(ADD_ICON, ADD_TOOLTIP) {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRow();
            }
        });
        addButton.setName(ADD_ROW_BUTTON);
        buttonsPanel.add(addButton);
        removeButton = new JButton(new ViewAction(REM_ICON, REM_TOOLTIP) {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeRows();
            }
        });
        buttonsPanel.add(removeButton);
        removeButton.setName(REM_ROW_BUTTON);
        removeButton.setEnabled(false);

        return buttonsPanel;
    }

    private void addRow() {
        ((DefaultTableModel) table.getModel()).addRow(getEmptyRowData());
        updateChart();
    }

    protected Object[] getEmptyRowData() {
        return new Object[0];
    }

    private void removeRows() {
        int[] selectedRow = table.getSelectedRows();
        int[] selectedModelRow = new int[selectedRow.length];

        for (int i = 0; i < selectedModelRow.length; i++) {
            selectedModelRow[i] = table.convertRowIndexToModel(selectedRow[i]);
        }

        Arrays.sort(selectedModelRow);

        for (int i = selectedModelRow.length - 1; i >= 0; i--) {
            ((DefaultTableModel) table.getModel()).removeRow(selectedModelRow[i]);
            updateChart();
        }
    }

    protected abstract AbstractChart createChart(String domainAxisLabel, String rangeAxisLabel);

    protected abstract TableModel createTableModel();

    private void setupEditors(JTable table) {
        final DoubleField doubleTextField = ComponentsFactory.doubleField();
        doubleTextField.setMargin(new Insets(0, 0, 0, 0));

        DefaultCellEditor doubleEditor = new DefaultCellEditor(doubleTextField) {
            @Override
            public Object getCellEditorValue() {
                try {
                    return Double.valueOf(Double.parseDouble((String) super.getCellEditorValue()));
                } catch (NumberFormatException e) {
                    return Double.valueOf(doubleTextField.getDoubleValue());
                }
            }
        };
        table.setDefaultEditor(Double.class, doubleEditor);

        final TableCellRenderer r = table.getDefaultRenderer(Double.class);
        table.setDefaultRenderer(Double.class, new TableCellRenderer() {
            private NumberFormat formatter;

            {
                formatter = DoubleField.getFormatForDISPLAY(10);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) r.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Double) {
                    label.setText(formatter.format(value));
                }
                return label;
            }
        });

    }

    protected void updateChart() {
        if (chart != null) { 
            chart.clear();
            chart.addToDataSet(convertTableDataToTimeBlocks());
        }
    };

    protected abstract TimeBlocks convertTableDataToTimeBlocks();

    protected void loadTableAndChart() {
        TableModel tableModel = createTableModel();
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    updateChart();
                    sorter.sort();
                }
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (removeButton != null) {
                    removeButton.setEnabled(table.getSelectedRowCount() > 0);
                }
            }
        });
        table.setModel(tableModel);

        table.setAutoCreateRowSorter(true);

        sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        updateChart();
    }

    /**
     * public for test purposes only do not call it directly
     */
    public abstract void saveOnDialogClose();

    public void showDialog() {
        dialog.setVisible(true);
    }

    private void handleDialogClose_Cancel() {
        dialog.setVisible(false);
    }

    private void handleDialogClose_OK() {
        saveOnDialogClose();
        dialog.setVisible(false);
    }

}
