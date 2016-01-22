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

import javax.vecmath.Point3d;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelUtils {

    public static void addHeaderCell(Workbook workbook, Row row, int col, String value) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);

        Font font = workbook.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cell.setCellStyle(cellStyle);
    }

    public static void addEmptyCell(Workbook workbook, Row row, int col) {
        row.createCell(col).setCellValue("");
    }

    public static void addDoubleCell(Sheet sheet, int row, int col, double value) {
        sheet.getRow(row + 1).createCell(col + 1).setCellValue(value);
    }

    public static void addPointCells(Sheet sheet, int row, int col, Point3d point) {
        sheet.getRow(row + 1).createCell(col + 1).setCellValue(point.getX());
        sheet.getRow(row + 1).createCell(col + 2).setCellValue(point.getY());
        sheet.getRow(row + 1).createCell(col + 3).setCellValue(point.getZ());
    }

    public static void mergeColumnsOnRow(Sheet sheet, int firstRow, int firstColumn, int lastColumn) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, firstRow, firstColumn, lastColumn));
    }

    public static void autoSizeColumns(Sheet sheet) {
        // header row
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i, true);
        }

        // table row
        for (int i = 0; i < sheet.getRow(1).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i, true);
        }
    }

}
