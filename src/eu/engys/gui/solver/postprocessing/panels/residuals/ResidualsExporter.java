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

package eu.engys.gui.solver.postprocessing.panels.residuals;

import static eu.engys.core.report.excel.ExcelUtils.addDoubleCell;
import static eu.engys.core.report.excel.ExcelUtils.addHeaderCell;
import static eu.engys.core.report.excel.ExcelUtils.autoSizeColumns;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import au.com.bytecode.opencsv.CSVWriter;
import eu.engys.core.project.system.monitoringfunctionobjects.Parser;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlock;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlockUnit;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.core.report.Exporter;
import eu.engys.gui.solver.postprocessing.data.DoubleListTimeBlockUnit;
import eu.engys.util.progress.ProgressMonitor;

public class ResidualsExporter extends Exporter {

    public ResidualsExporter(List<Parser> parsers, ProgressMonitor monitor) {
        super(parsers, monitor);
    }

    @Override
    protected void populateExcelFile(Workbook workbook) throws Exception {
        monitor.setIndeterminate(false);

        Parser parser = parsers.get(0);
        monitor.info(PARSING_LOG_FILE + parser.getFile(), 1);
        parser.init();
        TimeBlocks blocks = parser.updateParsing();
        parser.end();

        monitor.setTotal(blocks.size());
        monitor.info(POPULATING_SHEET + "Residuals" + DOTS, 1);
        addExcelSheet(workbook, blocks);
    }

    @Override
    protected void populateCSVFile(CSVWriter writer) throws Exception {
        monitor.setIndeterminate(false);

        Parser parser = parsers.get(0);
        monitor.info(PARSING_LOG_FILE + parser.getFile(), 1);
        parser.init();
        TimeBlocks blocks = parser.updateParsing();
        parser.end();

        monitor.setTotal(blocks.size());
        monitor.info(POPULATING_SHEET + "Residuals" + DOTS, 1);
        addCSVSheet(writer, blocks);
    }

    /*
     * CSV
     */

    private void addCSVSheet(CSVWriter writer, TimeBlocks blocks) {
        if (!blocks.isEmpty()) {
            addCSVHeaderRow(writer, blocks.get(0));
            addCSVTableRows(writer, blocks);
        }
    }

    private void addCSVHeaderRow(CSVWriter writer, TimeBlock firstTimeBlock) {
        List<String> headerRow = new LinkedList<>();
        headerRow.add("Time");

        Map<String, TimeBlockUnit> unitsMap = firstTimeBlock.getUnitsMap();
        for (String var : unitsMap.keySet()) {
            DoubleListTimeBlockUnit unit = (DoubleListTimeBlockUnit) unitsMap.get(var);
            if (unit.getValues().size() == 1) {
                headerRow.add(var);
            } else {
                for (int i = 0; i < unit.getValues().size(); i++) {
                    headerRow.add(var + i);
                }
            }
        }

        writer.writeNext(headerRow.toArray(new String[0]));
    }

    private void addCSVTableRows(CSVWriter writer, TimeBlocks blocks) {
        List<String[]> tableRows = new LinkedList<>();

        for (int i = 0; i < blocks.size(); i++) {
            TimeBlock block = blocks.get(i);

            List<String> row = new LinkedList<>();
            row.add(String.valueOf(block.getTime()));

            Map<String, TimeBlockUnit> unitsMap = block.getUnitsMap();
            for (String var : unitsMap.keySet()) {
                DoubleListTimeBlockUnit unit = (DoubleListTimeBlockUnit) unitsMap.get(var);
                for (int j = 0; j < unit.getValues().size(); j++) {
                    row.add(String.valueOf(unit.getValues().get(j)));
                }
            }
            tableRows.add(row.toArray(new String[0]));

            monitor.setCurrent(null, monitor.getCurrent() + 1);
        }
        writer.writeAll(tableRows);
    }

    /*
     * Excel
     */

    private void addExcelSheet(Workbook workbook, TimeBlocks blocks) {
        Sheet sheet = workbook.createSheet("Residuals");
        if (!blocks.isEmpty()) {
            addExcelHeaderRow(workbook, sheet, blocks.get(0));
            addExcelTableRows(sheet, blocks);
            autoSizeColumns(sheet);
        }
    }

    private void addExcelHeaderRow(Workbook workbook, Sheet sheet, TimeBlock firstTimeBlock) {
        Row headerRow = sheet.createRow(0);
        addHeaderCell(workbook, headerRow, 0, "Time");

        Map<String, TimeBlockUnit> unitsMap = firstTimeBlock.getUnitsMap();
        int counter = 1;
        for (String var : unitsMap.keySet()) {
            DoubleListTimeBlockUnit unit = (DoubleListTimeBlockUnit) unitsMap.get(var);
            if (unit.getValues().size() == 1) {
                addHeaderCell(workbook, headerRow, counter, var);
                counter++;
            } else {
                for (int i = 0; i < unit.getValues().size(); i++) {
                    addHeaderCell(workbook, headerRow, counter, var + i);
                    counter++;
                }
            }
        }
    }

    private void addExcelTableRows(Sheet sheet, TimeBlocks blocks) {
        for (int i = 0; i < blocks.size(); i++) {
            TimeBlock block = blocks.get(i);

            // i+1 because row 0 is for the header
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(block.getTime());

            Map<String, TimeBlockUnit> unitsMap = block.getUnitsMap();
            int counter = 0;
            for (String var : unitsMap.keySet()) {
                DoubleListTimeBlockUnit unit = (DoubleListTimeBlockUnit) unitsMap.get(var);
                for (int j = 0; j < unit.getValues().size(); j++) {
                    addDoubleCell(sheet, i, counter, unit.getValues().get(j));
                    counter++;
                }
            }

            monitor.setCurrent(null, monitor.getCurrent() + 1);
        }
    }

}
