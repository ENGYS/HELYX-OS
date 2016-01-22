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

package eu.engys.util.ui;

import java.io.File;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;

import eu.engys.util.PrefUtil;
import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.filechooser.HelyxFileChooser;
import eu.engys.util.filechooser.util.HelyxFileFilter;
import eu.engys.util.filechooser.util.SelectionMode;

public class FileChooserUtils {
	
    public static final String PDF_EXTENSION = "pdf";
	public static final String PNG_EXTENSION = "png";
	public static final String CSV_EXTENSION = "csv";
	public static final String EXCEL_EXTENSION_OLD = "xls";
	public static final String EXCEL_EXTENSION_NEW = "xlsx";
    
	public static final String DEFAULT_SSH_PORT = "22";
	
	public static File getPNGFile() {
		File lastDir = PrefUtil.getWorkDir(PrefUtil.LAST_OPEN_EXPORT_DIR);

		HelyxFileChooser fc = new HelyxFileChooser(lastDir.getAbsolutePath());
		fc.setSelectionMode(SelectionMode.FILES_ONLY);
		HelyxFileFilter filter = new HelyxFileFilter("PNG File (*.png)", PNG_EXTENSION);
		File file = null;
		ReturnValue retVal = fc.showSaveAsDialog(filter);

		if (retVal.isApprove()) {
			file = fc.getSelectedFile();
			String extension = FilenameUtils.getExtension(file.getAbsolutePath());
			
			if (!extension.equalsIgnoreCase(PNG_EXTENSION)) {
			    file = new File(file.getParent(), file.getName() + "." + PNG_EXTENSION);
			}

			if (file.exists()) {
				int confirm = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), "File already exists. Overwrite?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (confirm == JOptionPane.NO_OPTION) {
					return getPNGFile();
				}
			}
			PrefUtil.putFile(PrefUtil.LAST_OPEN_EXPORT_DIR, file.getParentFile());
		}
		return file;
	}

	public static File getExcelFile() {
	    File lastDir = PrefUtil.getWorkDir(PrefUtil.LAST_OPEN_EXPORT_DIR);
	    
	    HelyxFileChooser fc = new HelyxFileChooser(lastDir.getAbsolutePath());
	    fc.setSelectionMode(SelectionMode.FILES_ONLY);
	    HelyxFileFilter filter = new HelyxFileFilter("Excel File (*.xls, *.xlsx)", EXCEL_EXTENSION_OLD, EXCEL_EXTENSION_NEW);
	    File file = null;
	    ReturnValue retVal = fc.showSaveAsDialog(filter);
	    
	    if (retVal.isApprove()) {
	        file = fc.getSelectedFile();
	        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
	        
	        if (!extension.equalsIgnoreCase(EXCEL_EXTENSION_OLD) && !extension.equalsIgnoreCase(EXCEL_EXTENSION_NEW)) {
	            file = new File(file.getParent(), file.getName() + "." + EXCEL_EXTENSION_OLD);
	        }
	        
	        if (file.exists()) {
	            int confirm = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), "File already exists. Overwrite?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	            if (confirm == JOptionPane.NO_OPTION) {
	                return getPNGFile();
	            }
	        }
	        PrefUtil.putFile(PrefUtil.LAST_OPEN_EXPORT_DIR, file.getParentFile());
	    }
	    return file;
	}

	public static File getCSVFile() {
	    File lastDir = PrefUtil.getWorkDir(PrefUtil.LAST_OPEN_EXPORT_DIR);
	    
	    HelyxFileChooser fc = new HelyxFileChooser(lastDir.getAbsolutePath());
	    fc.setSelectionMode(SelectionMode.FILES_ONLY);
	    HelyxFileFilter filter = new HelyxFileFilter("CSV File (*.csv)", CSV_EXTENSION);
	    File file = null;
	    ReturnValue retVal = fc.showSaveAsDialog(filter);
	    
	    if (retVal.isApprove()) {
	        file = fc.getSelectedFile();
	        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
	        
	        if (!extension.equalsIgnoreCase(CSV_EXTENSION)) {
	            file = new File(file.getParent(), file.getName() + "." + CSV_EXTENSION);
	        }
	        
	        if (file.exists()) {
	            int confirm = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), "File already exists. Overwrite?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	            if (confirm == JOptionPane.NO_OPTION) {
	                return getPNGFile();
	            }
	        }
	        PrefUtil.putFile(PrefUtil.LAST_OPEN_EXPORT_DIR, file.getParentFile());
	    }
	    return file;
	}

}
