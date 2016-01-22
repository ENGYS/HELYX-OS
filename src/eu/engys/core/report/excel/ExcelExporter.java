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

package eu.engys.core.report.excel;

import static eu.engys.util.ui.FileChooserUtils.EXCEL_EXTENSION_NEW;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import eu.engys.core.executor.FileManagerSupport;
import eu.engys.core.project.system.monitoringfunctionobjects.Parser;
import eu.engys.util.progress.ProgressMonitor;

public abstract class ExcelExporter {

    protected List<Parser> parsers;
    protected ProgressMonitor monitor;

    public ExcelExporter(List<Parser> parsers, ProgressMonitor monitor) {
        this.parsers = parsers;
        this.monitor = monitor;
    }

    public ExcelExporter(Parser parser) {
        List<Parser> parsers = new ArrayList<>();
        parsers.add(parser);
        this.parsers = parsers;
    }

    public void create(File reportFile) throws Exception {
        Workbook workbook = null;
        if (FilenameUtils.getExtension(reportFile.getAbsolutePath()).equals(EXCEL_EXTENSION_NEW)) {
            workbook = new XSSFWorkbook();
        } else {
            workbook = new HSSFWorkbook();
        }
        populate(workbook);
        end(workbook, reportFile);
    }

    protected abstract void populate(Workbook workbook) throws Exception;

    private void end(Workbook workbook, File reportFile) throws Exception {
        FileOutputStream fileOut = new FileOutputStream(reportFile);
        workbook.write(fileOut);
        fileOut.close();
    }

    public void show(File reportFile) {
        FileManagerSupport.open(reportFile);
    }

}
