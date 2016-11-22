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
package eu.engys.core.project.zero.fields;

import java.io.File;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FoamFile;
import eu.engys.util.progress.ProgressMonitor;

public class FieldWriter {
	
	private static final Logger logger = LoggerFactory.getLogger(FieldWriter.class);

	private Field field;

	private ProgressMonitor monitor;

	public FieldWriter(Field field, ProgressMonitor monitor) {
		this.field = field;
		this.monitor = monitor;
	}

	public void bufferInternalField(File zeroDir) {
		String name = field.getName();
		InternalField internalField = field.getInternalField();
		File file = new File(zeroDir, name);
		internalField.buffer(file);
	}

	public void write(File zeroDir) {
		String name = field.getName();
		String dimensions = field.getDimensions();
		Dictionary boundaryField = field.getBoundaryField();
		InternalField internalField = field.getInternalField();
		File file = new File(zeroDir, name);
//		monitor.info(name, 2);
		logger.info("WRITE : {}", file);

		try (FileWriter writer = new FileWriter(file)) {
			writer.write(FoamFile.HEADER);
			writer.write(FoamFile.getFieldFoamFile(name).toString());
			writer.write(Field.DIMENSIONS + " " + dimensions + ";\n");
			internalField.write(writer);
			writer.write(boundaryField.toString());
		} catch (Exception e) {
		    monitor.error("Error writing " + file);
		    logger.error("Error writing  " + file, e);
		}
	}
}
