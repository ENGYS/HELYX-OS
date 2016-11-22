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
import java.io.IOException;

public class ArrayInternalField implements InternalField {

    private String buffer;
    private double[] value;

    public ArrayInternalField(double[] value) {
        this.value = value;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public double[][] getValue() {
        return new double[][] { value };
    }

    @Override
    public void write(FileWriter writer) throws IOException {
        writer.write(buffer);
    }

    @Override
    public void buffer(File file) {
        buffer = Field.INTERNAL_FIELD + " uniform (" + String.valueOf(value[0]) + " " + String.valueOf(value[1]) + " " + String.valueOf(value[2]) + ");";
    }
}
