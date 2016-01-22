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

package eu.engys.gui.casesetup.fields;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.dictionary.Dictionary.VALUE;

import java.io.File;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryBuilder;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.SetFieldsDict;
import eu.engys.core.project.zero.cellzones.CellZonesBuilder;
import eu.engys.core.project.zero.fields.AbstractInitialisations;
import eu.engys.core.project.zero.fields.ArrayInternalField;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.fields.ScalarInternalField;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.progress.SilentMonitor;

public class StandardInitialisations extends AbstractInitialisations {

    private static final Logger logger = LoggerFactory.getLogger(StandardInitialisations.class);
    private Set<ApplicationModule> modules;
    private CellZonesBuilder cellZonesBuilder;

    @Inject
    public StandardInitialisations(Model model, CellZonesBuilder cellZonesBuilder, Set<ApplicationModule> modules) {
        super(model);
        this.cellZonesBuilder = cellZonesBuilder;
        this.modules = modules;
    }

    @Override
    public void loadInitialisation(File zeroDir, Field field, ProgressMonitor monitor) {
        readFieldFromFile(field, zeroDir, monitor);
        loadInitialisations(field);
    }

    // Public for test purpouse only
    public void loadInitialisations(Field field) {
        if (map.containsKey(field.getName())) {
            field.setInitialisation(map.get(field.getName()));
        } else {
            boolean isScalar = field.getInternalField() instanceof ScalarInternalField;
            boolean isArray = field.getInternalField() instanceof ArrayInternalField;
            if (isScalar) {
                ScalarInternalField internalField = (ScalarInternalField) field.getInternalField();
                double value = internalField.getValue()[0][0];
                field.setInitialisation(DictionaryBuilder.newDictionary("initialisation").field(TYPE, "fixedValue").field(VALUE, String.valueOf(value)).done());
            } else if (isArray) {
                ArrayInternalField internalField = (ArrayInternalField) field.getInternalField();
                double valueX = internalField.getValue()[0][0];
                double valueY = internalField.getValue()[0][1];
                double valueZ = internalField.getValue()[0][2];
                String value = "(" + valueX + " " + valueY + " " + valueZ + ")";
                field.setInitialisation(DictionaryBuilder.newDictionary("initialisation").field(TYPE, "fixedValue").field(VALUE, value).done());
            } else {
                field.setInitialisation(DictionaryBuilder.newDictionary("initialisation").field(TYPE, "default").done());
            }
        }
        logger.info("{} initialised to {}", field.getName(), field.getInitialisationType());
    }

    @Override
    public void readInitialisationFromFile(Field field) {
        if (field.getName().startsWith(Fields.ALPHA)) {
            SetFieldsDict setFieldsDict = model.getProject().getSystemFolder().getSetFieldsDict();
            if (setFieldsDict != null && !setFieldsDict.isEmpty()) {
                readInitialisationFromSetFieldsDict(setFieldsDict, field);
                map.put(field.getName(), field.getInitialisation());
            }
        }
    }

    private void readInitialisationFromSetFieldsDict(SetFieldsDict setFieldsDict, Field field) {
        Dictionary convertedInitialisation = new SetFieldsDictConverter(field).convertForRead(setFieldsDict);
        field.setInitialisation(convertedInitialisation);
    }

    public void initializeFields() {
        for (Field field : model.getFields().values()) {
            initializeField(field);
        }
        model.getProject().getZeroFolder().write(model, cellZonesBuilder, modules, this, new SilentMonitor());
    }

    private void initializeField(Field field) {
        String fieldName = field.getName();
        String initMethod = field.getInitialisationType();
        logger.info("Inititalising field: {} as {}", fieldName, initMethod);

        if (initMethod.equals("fixedValue")) {
            initializeFixedValue(field);
        } else if (initMethod.equals("default")) {
        } else {
            logger.warn("'{}': Invalid initialisation: set to 'default'", initMethod);
        }
    }

    private void initializeFixedValue(Field field) {
        String value = field.getInitialisation().lookup("value");
        if (value != null && value.startsWith("uniform")) {
            field.setInternalField("internalField " + value);
            Fields[] parallelFields = model.getFields().getParallelFields();
            if (parallelFields != null) {
                for (int i = 0; i < parallelFields.length; i++) {
                    Field subField = parallelFields[i].get(field.getName());
                    subField.setInternalField("internalField " + value);
                }
            }
        }
        if (field.getName().equals(Fields.P) || field.getName().equals(Fields.P_RGH)) {
            writeValueOnProcBoundary(field);
        }
    }

    private void writeValueOnProcBoundary(Field field) {
        for (Patch patch : model.getPatches()) {
            if (patch.getPhisicalType().isProcessor()) {
                Dictionary momentum = patch.getBoundaryConditions().getMomentum();
                if (momentum.found(Fields.P)) {
                    Dictionary p = momentum.subDict(Fields.P);
                    p.add(Dictionary.VALUE, field.getInitialisation().lookup("value"));
                }
            }
        }
    }
}
