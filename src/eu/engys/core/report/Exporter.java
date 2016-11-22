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
package eu.engys.core.report;

import java.io.File;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import au.com.bytecode.opencsv.CSVWriter;
import eu.engys.core.project.system.monitoringfunctionobjects.Parser;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.core.report.excel.CSVExporter;
import eu.engys.core.report.excel.ExcelExporter;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.progress.SilentMonitor;

public abstract class Exporter {

    protected static final String POPULATING_SHEET = "Populating sheet ";
    protected static final String PARSING_LOG_FILE = "Parsing log file: ";
    protected static final String DOTS = "...";

    protected List<Parser> parsers;
    protected ProgressMonitor monitor;

    public Exporter(List<Parser> parsers, ProgressMonitor monitor) {
        this.parsers = parsers;
        this.monitor = monitor;
    }

    protected TimeBlocks getTimeBlocks() throws Exception {
        monitor.setIndeterminate(false);
        monitor.setTotal(parsers.size());

        TimeBlocks blocks = new TimeBlocks();
        for (Parser parser : parsers) {
            monitor.info(PARSING_LOG_FILE + parser.getFile(), 1);
            parser.init();
            TimeBlocks parsedBlocks = parser.updateParsing();
            blocks.setKey(parsedBlocks.getKey());
            blocks.addAll(parsedBlocks);
            parser.end();
            monitor.setCurrent(null, monitor.getCurrent() + 1);
        }

        blocks.orderAscending();

        return blocks;
    }

    /*
     * Excel
     */

    public void exportToExcel(File reportFile, ProgressMonitor monitor) throws Exception {
        ExcelExporter exporter = new ExcelExporter(parsers, monitor) {
            @Override
            protected void populate(Workbook workbook) throws Exception {
                populateExcelFile(workbook);
            }
        };
        exporter.create(reportFile);
        exporter.show(reportFile);
    }

    protected abstract void populateExcelFile(Workbook workbook) throws Exception;

    /*
     * CSV
     */

    public void exportToCSV(File reportFile, ProgressMonitor monitor) throws Exception {
        CSVExporter exporter = new CSVExporter(parsers, monitor) {

            @Override
            protected void populate(CSVWriter writer) throws Exception {
                populateCSVFile(writer);
            }
        };

        exporter.create(reportFile);
        exporter.show(reportFile);
    }

    protected abstract void populateCSVFile(CSVWriter writer) throws Exception;

    protected void addRow(CSVWriter writer, String string) {
        writer.writeNext(new String[] { string });
    }

    protected void addEmptyRow(CSVWriter writer) {
        writer.writeNext(new String[] { "" });
    }

    /*
     * For test purposes only
     */
    public void exportToExcel_TEST(File reportFile) throws Exception {
        ExcelExporter exporter = new ExcelExporter(parsers, new SilentMonitor()) {

            @Override
            protected void populate(Workbook workbook) throws Exception {
                populateExcelFile(workbook);
            }
        };

        exporter.create(reportFile);
    }

    public void exportToCSV_TEST(File reportFile) throws Exception {
        CSVExporter exporter = new CSVExporter(parsers, new SilentMonitor()) {

            @Override
            protected void populate(CSVWriter writer) throws Exception {
                populateCSVFile(writer);
            }
        };

        exporter.create(reportFile);
    }
}
