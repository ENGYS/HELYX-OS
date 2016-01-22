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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.util.progress.ProgressMonitor;

public class FieldsReader {
    
    private static final Logger logger = LoggerFactory.getLogger(FieldsReader.class);

    private Initialisations initialisations;
    private ProgressMonitor monitor;

    public FieldsReader(Initialisations initialisations, ProgressMonitor monitor) {
        this.monitor = monitor;
        this.initialisations = initialisations;
    }

    public Fields read(final Set<String> fieldNames, File... zeroDirs) {
        Fields fields = creteNewFields(zeroDirs);

        Fields[] parallelFields = fields.getParallelFields();

        int total = zeroDirs.length * fieldNames.size();
        monitor.setCurrent("Reading fields", 0, total, 1);

        for (int i = 0; i < parallelFields.length; i++) {
            final Fields pFields = parallelFields[i];
            final File zeroDir = zeroDirs[i];
            readFields(pFields, fieldNames, zeroDir);
        }
        copyFields(parallelFields[0], fields);

        return fields;
    }

    private Fields creteNewFields(File... zeroDirs) {
        Fields fields = new Fields();
        Fields[] parallelFields = new Fields[zeroDirs.length];
        for (int i = 0; i < parallelFields.length; i++) {
            parallelFields[i] = new Fields();
        }
        fields.setParallelFields(parallelFields);

        return fields;
    }

    private void readFields(Fields fields, Set<String> fieldNames, File zeroDir) {
        logger.debug("----------Reading fields from {}", zeroDir);
        
        for (String fieldName : fieldNames) {
            fields.put(fieldName, new Field(fieldName));
        }

        if (initialisations != null) {
            for (int i = 0; i < fields.size(); i++) {
                Field field = new ArrayList<>(fields.values()).get(i);
                initialisations.readInitialisationFromFile(field);
                initialisations.loadInitialisation(zeroDir, field, monitor);
                monitor.setCurrent(null, monitor.getCurrent() + 1, 2);
            }
        }
    }

    private void copyFields(Fields sources, Fields targets) {
        for (Field source : sources.values()) {
            Field target = new Field(source.getName());
            target.setDefinition(new Dictionary(source.getDefinition()));
            target.setDimensions(source.getDimensions());
            target.setInitialisation(new Dictionary(source.getInitialisation()));
            target.setInitialisationMethods(source.getInitialisationMethods());
            target.setFieldType(source.getFieldType());
            target.setInternalField(source.getInternalField());

            targets.put(target.getName(), target);
        }
    }
}
