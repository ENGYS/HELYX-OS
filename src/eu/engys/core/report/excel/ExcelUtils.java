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
package eu.engys.core.report.excel;

import static eu.engys.util.ui.FileChooserUtils.EXCEL_EXTENSION_NEW;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.Icon;
import javax.vecmath.Point3d;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.ui.ResourcesUtil;

public class ExcelUtils {

	public static final String IMPORT_XLS_LABEL = "import.xls.button";
	public static final String IMPORT_XLS_TOOLTIP = "Import data from an Excel file";
	public static final Icon IMPORT_XLS_ICON = ResourcesUtil.getIcon("file.excel");

	private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

	public static Workbook getWoorkBook(File excelFile) {
		if (FilenameUtils.getExtension(excelFile.getAbsolutePath()).equals(EXCEL_EXTENSION_NEW)) {
			return new XSSFWorkbook();
		} else {
			return new HSSFWorkbook();
		}
	}

	public static Workbook getWoorkBook(File excelFile, FileInputStream inputStream) throws IOException {
		if (FilenameUtils.getExtension(excelFile.getAbsolutePath()).equals(EXCEL_EXTENSION_NEW)) {
			return new XSSFWorkbook(inputStream);
		} else {
			return new HSSFWorkbook(inputStream);
		}
	}

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

	public static void writeDoubleMatrix(File file, double[][] data) {
		Workbook wb = getWoorkBook(file);
		Sheet sheet = wb.createSheet();

		int rows = data.length;
		for (int r = 0; r < rows; r++) {
			Row row = sheet.createRow(r);
			int cells = data[r].length;
			for (int c = 0; c < cells; c++) {
				Cell cell = row.createCell(c, HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(data[r][c]);
			}
		}

		try (FileOutputStream os = new FileOutputStream(file)) {
			wb.write(os);
		} catch (Exception e) {
			logger.error("Error reading file", e);
		}
	}

	public static void writeObjectMatrix(File file, Object[][] data) {
		Workbook wb = getWoorkBook(file);
		Sheet sheet = wb.createSheet();

		int rows = data.length;
		for (int r = 0; r < rows; r++) {
			Row row = sheet.createRow(r);
			int cells = data[r].length;
			for (int c = 0; c < cells; c++) {
				Cell cell = row.createCell(c, HSSFCell.CELL_TYPE_NUMERIC);
				Object d = data[r][c];
				if (d instanceof Double) {
					cell.setCellValue((Double) d);
				} else {
					cell.setCellValue(String.valueOf(d));
				}
			}
		}

		try (FileOutputStream os = new FileOutputStream(file)) {
			wb.write(os);
		} catch (Exception e) {
			logger.error("Error reading file", e);
		}
	}

	public static Double[][] readDoubleMatrix(File file, int sheetIndex, int colSize) {
		try (FileInputStream is = new FileInputStream(file)) {
			Workbook wb = getWoorkBook(file, is);

			Sheet sheet = wb.getSheetAt(sheetIndex);
			int rows = sheet.getPhysicalNumberOfRows();

			logger.debug("Sheet {} \"{}\" has {} row(s).", sheetIndex, wb.getSheetName(sheetIndex), rows);
			Double[][] matrix = new Double[rows][colSize];
			for (int r = 0; r < rows; r++) {

				Double[] matrixRow = getEmptyRow(colSize);

				Row row = sheet.getRow(r);
				if (row == null) {
					continue;
				}

				int cells = row.getPhysicalNumberOfCells();
				logger.debug("ROW {} has {} cell(s).", row.getRowNum(), cells);

				for (int c = 0; c < colSize; c++) {
					if (cells < colSize && c >= cells) {
						matrixRow[c] = 0.0;
					} else {
						Cell cell = row.getCell(c);
						if(cell != null){
							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_FORMULA:
								matrixRow[c] = 0.0;
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								matrixRow[c] = cell.getNumericCellValue();
								break;
							case HSSFCell.CELL_TYPE_STRING:
								matrixRow[c] = 0.0;
								break;
							default:
								matrixRow[c] = 0.0;
							}
						} else {
							matrixRow[c] = 0.0;
						}
					}
				}
				matrix[r] = matrixRow;
			}

			return matrix;
		} catch (Exception e) {
			logger.error("Error reading file", e);
		}

		return new Double[0][0];
	}

	private static Double[] getEmptyRow(int colSize) {
		Double[] emptyArray = new Double[colSize];
		for (int i = 0; i < emptyArray.length; i++) {
			emptyArray[i] = 0.0;
		}
		return emptyArray;
	}

}
