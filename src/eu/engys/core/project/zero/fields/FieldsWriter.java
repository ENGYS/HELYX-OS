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

package eu.engys.core.project.zero.fields;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.zero.ZeroFolderUtil;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ExecUtil;

public class FieldsWriter {

    private static Logger logger = LoggerFactory.getLogger(Fields.class);

    private ProgressMonitor monitor;

    public FieldsWriter(ProgressMonitor monitor) {
        this.monitor = monitor;
    }

    public void write(Fields fields, File... zeroDirs) {
        if (zeroDirs.length == 1) {
            _write(fields, zeroDirs[0]);
        } else {
            writeParallel(fields, zeroDirs);
        }
    }

    private void writeParallel(Fields fields, File... zeroDirs) {
        Runnable[] runnables = new Runnable[zeroDirs.length];
        for (int i = 0; i < zeroDirs.length; i++) {
            final Fields fieldsForProc = fields.getFieldsForProcessor(i);

            if (fieldsForProc.size() != fields.size()) {
                logger.warn("Fields for processor {} are {}!", i, fieldsForProc.keySet());
            }

            final File zeroDir = zeroDirs[i];
            runnables[i] = new Runnable() {
                @Override
                public void run() {
                    _write(fieldsForProc, zeroDir);
                    monitor.setCurrent(null, monitor.getCurrent() + 1, 2);
                }
            };
        }
        ExecUtil.execSerial(runnables);
        // ExecUtil.execInParallelAndWait(runnables);
    }

    private void _write(Fields fields, final File zeroDir) {
        if (fields.isEmpty()) {
            logger.info("WRITE: Fields -> none", 2);
        } else {
            bufferInternalFields(fields, zeroDir);
            ZeroFolderUtil.clearFiles(zeroDir);
            writeFields(zeroDir, fields);
        }
    }

    private void bufferInternalFields(Fields fields, final File zeroDir) {
        Runnable[] runnablesForBuffer = new Runnable[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            final Field field = new ArrayList<>(fields.values()).get(i);
            runnablesForBuffer[i] = new Runnable() {
                @Override
                public void run() {
                    field.bufferInternalField(zeroDir, monitor);
                }
            };
        }
        ExecUtil.execSerial(runnablesForBuffer);
        // ExecUtil.execInParallelAndWait(runnablesForBuffer);
    }

    private void writeFields(final File zeroDir, Fields fields) {
        Runnable[] runnablesForWrite = new Runnable[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            final Field field = new ArrayList<>(fields.values()).get(i);
            runnablesForWrite[i] = new Runnable() {
                @Override
                public void run() {
                    field.write(zeroDir, monitor);
                }
            };
        }
        ExecUtil.execSerial(runnablesForWrite);
        // ExecUtil.execInParallelAndWait(runnablesForWrite);
    }
}
