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

import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.swing.Icon;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import eu.engys.util.ui.ResourcesUtil;

public class CSVUtils {

	public static final String IMPORT_CSV_LABEL = "import.csv.button";
	public static final String IMPORT_CSV_TOOLTIP = "Import data from a comma-separated values file";
	public static final Icon IMPORT_CSV_ICON = ResourcesUtil.getIcon("file.icon");

	private static final Logger logger = LoggerFactory.getLogger(CSVUtils.class);

	public static Double[][] readDoubleMatrix(File file, int colSize) {
		try (CSVReader reader = new CSVReader(new FileReader(file))) {
			List<String[]> dataRead = reader.readAll();
			Double[][] data = new Double[dataRead.size()][colSize];
			for (int i = 0; i < dataRead.size(); i++) {
				String[] stringRow = dataRead.get(i);
				Double[] doubleRow = new Double[colSize];
				for (int j = 0; j < colSize; j++) {
					if (stringRow.length < colSize && j >= stringRow.length) {
						doubleRow[j] = 0.0;
					} else {
						if (NumberUtils.isNumber(stringRow[j])) {
							doubleRow[j] = Double.valueOf(stringRow[j]);
						} else {
							doubleRow[j] = 0.0;
						}
					}
				}
				data[i] = doubleRow;
			}
			return data;
		} catch (Exception e) {
			logger.error("Error reading file", e);
		}
		return new Double[0][0];
	}

}
