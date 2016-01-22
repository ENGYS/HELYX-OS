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

package eu.engys.gui.casesetup.boundaryconditions;

import static eu.engys.util.RegexpUtils.CLOSED_BRACKET;
import static eu.engys.util.RegexpUtils.DOUBLE;
import static eu.engys.util.RegexpUtils.OPEN_BRACKET;
import static eu.engys.util.RegexpUtils.POINT;
import static eu.engys.util.RegexpUtils.SPACES;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlock;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.gui.solver.postprocessing.data.DoubleTimeBlockUnit;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.CopyPasteSupport;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.textfields.DoubleField;

public abstract class AbstractInterpolationTable extends JPanel {

    private static final String VALUE_VAR = "Value";
    private static final String Z_VAR = "Z";
    private static final String Y_VAR = "Y";
    private static final String X_VAR = "X";
    private static final String VECTOR_PATTERN = OPEN_BRACKET + SPACES + DOUBLE + SPACES + POINT + SPACES + CLOSED_BRACKET;
    private static final String SCALAR_PATTERN = OPEN_BRACKET + SPACES + DOUBLE + SPACES + DOUBLE + SPACES + CLOSED_BRACKET;

    private JTable table;
    private DefaultTableModel tableModel;
    private InterpolationChartPanel chart;
    protected String[] columnNames;
    private JButton removeButton;
    private JSplitPane splitPane;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JDialog dialog;
    private JButton okButton;
    protected String[] names;

    public AbstractInterpolationTable(String[] names) {
        super(new BorderLayout());
        this.names = names;
        setName("interpolation.table");
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setupColumnNames();
        layoutComponents();
    }

    public abstract void load();

    public boolean isVector() {
        return names.length == 3;
    }
    
    public StringBuilder save() {
        StringBuilder sb = new StringBuilder();
        sb.append("(\n");
        for (int row = 0; row < table.getRowCount(); row++) {
            if (isVector()) {
                Double distance = ((Double) table.getValueAt(row, 0));
                double x = ((Double) table.getValueAt(row, 1));
                double y = ((Double) table.getValueAt(row, 2));
                double z = ((Double) table.getValueAt(row, 3));
                String interpolationRow = "( " + distance + " ( " + x + " " + y + " " + z + " ) )";
                sb.append(interpolationRow + "\n");
            } else {
                Double time = ((Double) table.getValueAt(row, 0));
                double value = ((Double) table.getValueAt(row, 1));
                String interpolationRow = "( " + time + " " + value + " )";
                sb.append(interpolationRow + "\n");
            }
        }
        sb.append(")");
        return sb;
    }

    protected void loadTable(String tableData) {
        if (tableData.startsWith("(") && tableData.endsWith(")")) {
            tableData = tableData.substring(1, tableData.length() - 1).trim();
            try {
                Pattern regex = Pattern.compile(isVector() ? VECTOR_PATTERN : SCALAR_PATTERN);
                Matcher regexMatcher = regex.matcher(tableData);
                while (regexMatcher.find()) {
                    addRow(parseRows(regexMatcher.group(0)));
                }
            } catch (PatternSyntaxException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Double[] parseRows(String row) {
        Pattern innerRegex = Pattern.compile(DOUBLE);
        Matcher innerRegexMatcher = innerRegex.matcher(row);
        Double[] values = isVector() ? new Double[4] : new Double[2];
        int internalCount = 0;
        while (innerRegexMatcher.find()) {
            values[internalCount++] = Double.parseDouble(innerRegexMatcher.group(0));
        }
        return values;
    }

    /*
     * GUI
     */

    protected abstract void setupColumnNames();

    private void layoutComponents() {
        createSplitPane();
        add(splitPane, BorderLayout.CENTER);
        createTableButtons();
        createTable();
        createChart();
        createDialogButtons();
        createDialog();
    }

    private void createDialog() {
        dialog = new JDialog(UiUtil.getActiveWindow(), "Interpolation Table", ModalityType.MODELESS);
        dialog.setName("interpolation.dialog");
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleDialogClose_Cancel();
            }
        });

        dialog.add(this);
        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.getRootPane().setDefaultButton(okButton);
    }

    public void showDialog() {
        load();
        dialog.setVisible(true);
    }

    private void handleDialogClose_Cancel() {
        dialog.setVisible(false);
    }

    private void handleDialogClose_OK() {
        save();
        dialog.setVisible(false);
    }

    private void createSplitPane() {
        this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.splitPane.setOneTouchExpandable(false);
        this.topPanel = new JPanel(new BorderLayout());
        this.bottomPanel = new JPanel(new BorderLayout());
        this.splitPane.setTopComponent(topPanel);
        this.splitPane.setBottomComponent(bottomPanel);
        this.splitPane.setResizeWeight(0.5);
    }

    private void createTableButtons() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton(new AbstractAction("+") {

            @Override
            public void actionPerformed(ActionEvent e) {
                addRow();
            }
        });
        addButton.setName("add.row.button");
        buttonsPanel.add(addButton);
        removeButton = new JButton(new AbstractAction("-") {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeRows();
            }
        });
        buttonsPanel.add(removeButton);
        removeButton.setName("rem.row.button");
        removeButton.setEnabled(false);

        topPanel.add(buttonsPanel, BorderLayout.NORTH);
    }

    private void createDialogButtons() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton(new AbstractAction("OK") {

            @Override
            public void actionPerformed(ActionEvent e) {
                handleDialogClose_OK();
            }
        });
        okButton.setName("OK");
        JButton cancelButton = new JButton(new AbstractAction("Cancel") {

            @Override
            public void actionPerformed(ActionEvent e) {
                handleDialogClose_Cancel();
            }
        });
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void createTable() {
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Double.class;
            }
        };
        table = new JTable() {
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
        table.setModel(tableModel);
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    updateChart();
                }
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                removeButton.setEnabled(table.getSelectedRowCount() > 0);
            }
        });
        setupEditors(table);

        CopyPasteSupport.addSupportTo(table);
        topPanel.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void createChart() {
        this.chart = new InterpolationChartPanel(getVariablesList(), columnNames[0]);
        chart.layoutComponents();
        chart.initSeries();
        bottomPanel.add(chart, BorderLayout.CENTER);
    }

    private List<String> getVariablesList() {
        List<String> variables = new ArrayList<>();
        if (isVector()) {
            variables.add(X_VAR);
            variables.add(Y_VAR);
            variables.add(Z_VAR);
        } else {
            variables.add(VALUE_VAR);
        }
        return variables;
    }

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
                formatter = NumberFormat.getInstance();
                formatter.setMaximumFractionDigits(10);
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

    private void addRow() {
        Object[] scalarRow = new Object[] { 0.0, 0.0 };
        Object[] vectorRow = new Object[] { 0.0, 0.0, 0.0, 0.0 };
        tableModel.addRow(isVector() ? vectorRow : scalarRow);
        updateChart();
    }

    private void addRow(Object[] row) {
        tableModel.addRow(row);
        updateChart();
    }

    private void removeRows() {
        int[] selectedRow = table.getSelectedRows();
        for (int i = selectedRow.length - 1; i >= 0; i--) {
            tableModel.removeRow(selectedRow[i]);
            updateChart();
        }
    }

    private void updateChart() {
        chart.clearData();
        TimeBlocks tbs = new TimeBlocks();
        for (int row = 0; row < table.getRowCount(); row++) {
            TimeBlock timeBlock = new TimeBlock(((Double) table.getValueAt(row, 0)));
            if (isVector()) {
                timeBlock.getUnitsMap().put(X_VAR, new DoubleTimeBlockUnit(X_VAR, ((Double) table.getValueAt(row, 1))));
                timeBlock.getUnitsMap().put(Y_VAR, new DoubleTimeBlockUnit(Y_VAR, ((Double) table.getValueAt(row, 2))));
                timeBlock.getUnitsMap().put(Z_VAR, new DoubleTimeBlockUnit(Z_VAR, ((Double) table.getValueAt(row, 3))));
            } else {
                timeBlock.getUnitsMap().put(VALUE_VAR, new DoubleTimeBlockUnit(VALUE_VAR, ((Double) table.getValueAt(row, 1))));
            }
            tbs.add(timeBlock);
        }
        chart.addToDataSet(tbs);
    }

    protected void clear() {
        tableModel.setRowCount(0);
        chart.clearData();
    }

}
