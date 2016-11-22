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
package eu.engys.gui.casesetup.boundaryconditions.panels;

import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.DATA_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FILE_NAME_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INTERPOLATION_TABLE_NAME;
import static eu.engys.util.RegexpUtils.DOUBLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.parser.ListField2;
import eu.engys.core.modules.AbstractChart;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlock;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.gui.casesetup.DictionaryTableAndChartPanel;
import eu.engys.gui.casesetup.boundaryconditions.charts.SimpleHistoryChart;
import eu.engys.gui.solver.postprocessing.data.DoubleTimeBlockUnit;
import eu.engys.util.RegexpUtils;

public class VectorNonUniformInterpolationTable extends DictionaryTableAndChartPanel {

    private static final Logger logger = LoggerFactory.getLogger(VectorNonUniformInterpolationTable.class);

    public static final String DISTANCE_M_LABEL = "Distance [m]";
    public static final String X_VAR = "X";
    public static final String Y_VAR = "Y";
    public static final String Z_VAR = "Z";

    public static final String TITLE = "Interpolation Table";

    private String[] columnNames;
    public VectorNonUniformInterpolationTable(String[] columnNames) {
        super(TITLE, INTERPOLATION_TABLE_NAME, DISTANCE_M_LABEL, "", true, columnNames);
        this.columnNames = columnNames;
    }

    @Override
    protected AbstractChart createChart(String domainAxisLabel, String rangeAxisLabel) {
        AbstractChart chart = new SimpleHistoryChart(Arrays.asList(new String[] { X_VAR, Y_VAR, Z_VAR }), domainAxisLabel, rangeAxisLabel);
        chart.layoutComponents();
        return chart;
    }

    public void load(Dictionary dictionary) {
        if (dictionary.found(DATA_KEY)) {
            if (dictionary.isList2(DATA_KEY)) {
                // Fix for alpha1 which is read with DictionaryReader2
                this.data = toObject(ListField2.convertToString(dictionary.getList2(DATA_KEY)));
            } else {
                this.data = toObject(dictionary.lookup(DATA_KEY).trim());
            }
        }
        loadTableAndChart();
    }

    @Override
    protected Object[] getEmptyRowData() {
        return new Object[] { 0.0, 0.0, 0.0, 0.0 };
    }

    @Override
    protected TableModel createTableModel() {
        DefaultTableModel tableModel = new DefaultTableModel(data, new String[] { DISTANCE_M_LABEL, columnNames[0], columnNames[1], columnNames[2] }) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Double.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        return tableModel;
    }

    @Override
    protected TimeBlocks convertTableDataToTimeBlocks() {
        TimeBlocks tbs = new TimeBlocks();
        for (int row = 0; row < table.getRowCount(); row++) {
            TimeBlock timeBlock = new TimeBlock(((Double) table.getValueAt(row, 0)));
            timeBlock.getUnitsMap().put(X_VAR, new DoubleTimeBlockUnit(X_VAR, ((Double) table.getValueAt(row, 1))));
            timeBlock.getUnitsMap().put(Y_VAR, new DoubleTimeBlockUnit(Y_VAR, ((Double) table.getValueAt(row, 2))));
            timeBlock.getUnitsMap().put(Z_VAR, new DoubleTimeBlockUnit(Z_VAR, ((Double) table.getValueAt(row, 3))));
            tbs.add(timeBlock);
        }
        return tbs;
    }

    @Override
    public void saveOnDialogClose() {
        Double[][] data = new Double[table.getRowCount()][table.getColumnCount()];
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getColumnCount(); col++) {
                // use table here because it is ordered
                data[row][col] = (Double) table.getValueAt(row, col);
            }
        }
        this.data = data;
    }

    public void save(Dictionary dictionary) {
        String value = toPrimitive();
        dictionary.add(DATA_KEY, value);
        dictionary.remove(FILE_NAME_KEY);
    }

    @Override
    protected String toPrimitive() {
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        for (int i = 0; i < data.length; i++) {
            Double[] row = data[i];
            Double distance = row[0].doubleValue();
            Double x = row[1].doubleValue();
            Double y = row[2].doubleValue();
            Double z = row[3].doubleValue();
            sb.append("( " + distance + " ( " + x + " " + y + " " + z + " ) ) ");
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    protected Double[][] toObject(String tableData) {
        List<Double[]> rows = new ArrayList<>();
        if (tableData.startsWith("(") && tableData.endsWith(")")) {
            tableData = tableData.substring(1, tableData.length() - 1).trim();
            if (!tableData.isEmpty()) {
                try {
                    Pattern regex = Pattern.compile(RegexpUtils.VECTOR_PATTERN);
                    Matcher regexMatcher = regex.matcher(tableData);
                    if (regexMatcher.find()) {
                        regexMatcher.reset();
                        while (regexMatcher.find()) {
                            rows.add(parseRows(regexMatcher.group(0)));
                        }
                    } else {
                        logger.error("Parsing error: pattern not found for {}", tableData);
                    }
                } catch (PatternSyntaxException ex) {
                    logger.error("Parsing error: {}", ex.getMessage());
                }
            }
        } else {
            logger.error("Parsing error: {} does not start with '('", tableData);
        }
        return rows.toArray(new Double[0][0]);
    }

    private Double[] parseRows(String row) {
        Pattern innerRegex = Pattern.compile(DOUBLE);
        Matcher innerRegexMatcher = innerRegex.matcher(row);
        Double[] values = new Double[4];
        int internalCount = 0;
        while (innerRegexMatcher.find()) {
            values[internalCount++] = Double.parseDouble(innerRegexMatcher.group(0));
        }
        return values;
    }

}
