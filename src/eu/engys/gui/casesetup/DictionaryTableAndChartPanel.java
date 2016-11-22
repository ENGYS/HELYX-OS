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

import static eu.engys.core.report.excel.CSVUtils.IMPORT_CSV_ICON;
import static eu.engys.core.report.excel.CSVUtils.IMPORT_CSV_LABEL;
import static eu.engys.core.report.excel.CSVUtils.IMPORT_CSV_TOOLTIP;
import static eu.engys.core.report.excel.ExcelUtils.IMPORT_XLS_ICON;
import static eu.engys.core.report.excel.ExcelUtils.IMPORT_XLS_LABEL;
import static eu.engys.core.report.excel.ExcelUtils.IMPORT_XLS_TOOLTIP;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.report.excel.CSVUtils;
import eu.engys.core.report.excel.ExcelUtils;
import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.filechooser.HelyxFileChooser;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.FileChooserUtils;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public abstract class DictionaryTableAndChartPanel extends TableAndChartPanel {

	private static final Logger logger = LoggerFactory.getLogger(DictionaryTableAndChartPanel.class);

	public static final String IMPORT_OF_LABEL = "import.of.button";
	public static final String IMPORT_OF_TOOLTIP = "Import data from an OpenFoam table data file";
	public static final Icon IMPORT_OF_ICON = ResourcesUtil.getIcon("file.openfoam");

	protected Double[][] data;
	private String[] columnNames;

	public DictionaryTableAndChartPanel(String title, String name, String domainAxisLabel, String rangeAxisLabel, boolean editable, String[] columnNames) {
		super(title, name, domainAxisLabel, rangeAxisLabel, editable);
		this.columnNames = columnNames;
	}

	@Override
	protected JComponent createTableButtons() {
		JToolBar rightPanel = UiUtil.getToolbarWrapped();

		rightPanel.add(Box.createHorizontalGlue());
		AbstractButton importOFButton = UiUtil.createToolBarButton(new ViewAction(IMPORT_OF_ICON, IMPORT_OF_TOOLTIP) {
			@Override
			public void actionPerformed(ActionEvent e) {
				importFromOpenFoam();
			}
		});
		rightPanel.add(importOFButton);
		importOFButton.setName(IMPORT_OF_LABEL);

		AbstractButton importXLSButton = UiUtil.createToolBarButton(new ViewAction(IMPORT_XLS_ICON, IMPORT_XLS_TOOLTIP) {
			@Override
			public void actionPerformed(ActionEvent e) {
				importFromExcel();
			}
		});
		importXLSButton.setName(IMPORT_XLS_LABEL);
		rightPanel.add(importXLSButton);

		AbstractButton importCSVButton = UiUtil.createToolBarButton(new ViewAction(IMPORT_CSV_ICON, IMPORT_CSV_TOOLTIP) {
			@Override
			public void actionPerformed(ActionEvent e) {
				importFromCSV();
			}
		});
		importCSVButton.setName(IMPORT_CSV_LABEL);
		rightPanel.add(importCSVButton);

		JPanel p = new JPanel(new BorderLayout());
		p.add(super.createTableButtons(), BorderLayout.CENTER);
		p.add(rightPanel, BorderLayout.EAST);

		return p;
	}

	private boolean checkDataStructure() {
		String msg = null;
		if (data == null || data.length == 0) {
			msg = "No data read.";
		} else if (data[0] == null || data[0].length == 0) {
			msg = "No data read.";
		} else if (data[0].length <= columnNames.length) {
			msg = "Too few columns of data read.";
		} else if (data[0].length > columnNames.length + 1) {
			msg = "Too much columns of data read: only " + (columnNames.length + 1) + " are taken.";
		}
		if (msg != null) {
			JOptionPane.showMessageDialog(dialog, msg, "Import Error", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	private void importFromOpenFoam() {
		HelyxFileChooser chooser = new HelyxFileChooser();
		chooser.setParent(dialog);
		chooser.setSelectionMode(SelectionMode.FILES_ONLY);

		ReturnValue retVal = chooser.showOpenDialog();
		if (retVal.isApprove()) {
			File file = chooser.getSelectedFile();

			try (FileInputStream is = new FileInputStream(file)) {
				String data = IOUtils.toString(is);
				data = data.trim();
				data = data.replace("\n", Dictionary.SPACER);
				data = data.replace(";", Dictionary.SPACER).trim();
				data = data.replace("(", Dictionary.SPACER + "(" + Dictionary.SPACER);
				data = data.replace(")", Dictionary.SPACER + ")" + Dictionary.SPACER);
				data = data.replaceAll("\\s+", Dictionary.SPACER).trim();

				this.data = toObject(data);

				if (checkDataStructure()) {
					ExecUtil.invokeLater(new Runnable() {
						@Override
						public void run() {
							loadTableAndChart();
						}
					});
				}
			} catch (Exception e) {
				logger.error("Error reading file", e);
			}
		}
	}

	private void importFromExcel() {
		HelyxFileChooser chooser = new HelyxFileChooser();
		chooser.setParent(dialog);
		chooser.setSelectionMode(SelectionMode.FILES_ONLY);

		ReturnValue retVal = chooser.showOpenDialog(FileChooserUtils.XLS_FILTER);
		if (retVal.isApprove()) {
			File file = chooser.getSelectedFile();
			this.data = ExcelUtils.readDoubleMatrix(file, 0,(columnNames.length + 1));
			ExecUtil.invokeLater(new Runnable() {
				@Override
				public void run() {
					loadTableAndChart();
				}
			});
		}
	}

	private void importFromCSV() {
		HelyxFileChooser chooser = new HelyxFileChooser();
		chooser.setParent(dialog);
		chooser.setSelectionMode(SelectionMode.FILES_ONLY);

		ReturnValue retVal = chooser.showOpenDialog(FileChooserUtils.CSV_FILTER);
		if (retVal.isApprove()) {
			File file = chooser.getSelectedFile();
			this.data = CSVUtils.readDoubleMatrix(file, (columnNames.length + 1));
			ExecUtil.invokeLater(new Runnable() {
				@Override
				public void run() {
					loadTableAndChart();
				}
			});
		}
	}

	protected abstract String toPrimitive();

	protected abstract Double[][] toObject(String tableData);
}
