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

package eu.engys.util.ui.textfields;

import java.io.File;
import java.text.ParseException;

import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

import eu.engys.util.ui.textfields.verifiers.FileVerifier;

public class FileTextField extends PromptTextField {

	public FileTextField() {
		super();
		setFormatterFactory(new DefaultFormatterFactory(new FileFormatter()));
		setInputVerifier(new FileVerifier(this));
		setColumns(STRING_FIELD_COLUMNS);
	}

	@Override
	public File getValue() {
		return (File) super.getValue();
	}
	
	public void setValue(File file) {
		super.setValue(file);
		((FileVerifier) getInputVerifier()).verify(this);
	}
	
	public boolean hasValidFile() {
		return ((FileVerifier) getInputVerifier()).verify(this);
	}

	private static class FileFormatter extends DefaultFormatter {
		public FileFormatter() {
			super();
			setValueClass(File.class);
		}

		@Override
		public Object stringToValue(String string) throws ParseException {
			return string.isEmpty() ? null : new File(string);
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			if (value == null) {
				return "";
			}
			return File.class.cast(value).getPath();
		}
	}

}
