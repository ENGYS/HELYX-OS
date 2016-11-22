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
package eu.engys.gui.casesetup.fields;

import static eu.engys.core.project.system.FvSolution.N_NON_ORTHOGONAL_CORRECTORS_KEY;

import java.io.File;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.FvSolution;
import eu.engys.core.project.system.SetFieldsDict;
import eu.engys.core.project.zero.fields.AbstractInitialisations;
import eu.engys.core.project.zero.fields.ArrayInternalField;
import eu.engys.core.project.zero.fields.DefaultInitialisation;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.fields.FixedScalarInitialisation;
import eu.engys.core.project.zero.fields.FixedVectorInitialisation;
import eu.engys.core.project.zero.fields.Initialisation;
import eu.engys.core.project.zero.fields.InternalField;
import eu.engys.core.project.zero.fields.LoadSaveInitialisation;
import eu.engys.core.project.zero.fields.PotentialFlowInitialisation;
import eu.engys.core.project.zero.fields.ScalarInternalField;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.util.progress.ProgressMonitor;

public class StandardInitialisations extends AbstractInitialisations {

    private static final Logger logger = LoggerFactory.getLogger(StandardInitialisations.class);
    private ProgressMonitor monitor;

    @Inject
    public StandardInitialisations(Model model, ProgressMonitor monitor) {
        super(model);
        this.monitor = monitor;
    }

    @Override
    public void loadInitialisation(File zeroDir, Field field, ProgressMonitor monitor) {
        readFieldFromFile(field, zeroDir, monitor);
        loadInitialisations(field);
    }

    // Public for test purpose only
    public void loadInitialisations(Field field) {
        if (map.containsKey(field.getName())) {
            field.setInitialisation(map.get(field.getName()));
        } else {
            boolean isScalar = field.getInternalField() instanceof ScalarInternalField;
            boolean isArray = field.getInternalField() instanceof ArrayInternalField;
            if (isScalar) {
                ScalarInternalField internalField = (ScalarInternalField) field.getInternalField();
                double value = internalField.getValue()[0][0];
                FixedScalarInitialisation i = new FixedScalarInitialisation();
                i.setValue(value);
                field.setInitialisation(i);
            } else if (isArray) {
                ArrayInternalField internalField = (ArrayInternalField) field.getInternalField();
                FixedVectorInitialisation i = new FixedVectorInitialisation();
                i.setValue(internalField.getValue()[0]);
                field.setInitialisation(i);
            } else {
                field.setInitialisation(new DefaultInitialisation());
            }
        }
        logger.info("{} initialised to {}", field.getName(), field.getInitialisation());
    }

    @Override
    public void readInitialisationFromFile(Field field) {
        if (BoundaryConditions.isMomentum(field.getName())) {
            readPotentialFlowInitialisation(field);
        }
        if (field.getName().startsWith(Fields.ALPHA)) {
            readCellSetInitialisation(field);
        }
    }

    private void readPotentialFlowInitialisation(Field field) {
        FvSolution fvSolution = model.getProject().getSystemFolder().getFvSolution();
        if (fvSolution.found(POTENTIAL_FLOW_KEY)) {
            PotentialFlowInitialisation pfInitialisation = new PotentialFlowInitialisation();
            Dictionary potentialFlowDict = fvSolution.subDict(POTENTIAL_FLOW_KEY);
            if (potentialFlowDict.found(INITIALISE_UBCS_KEY)) {
                pfInitialisation.setInitUBCS(potentialFlowDict.lookupBoolean(INITIALISE_UBCS_KEY));
            }
            if (potentialFlowDict.found(N_NON_ORTHOGONAL_CORRECTORS_KEY)) {
                pfInitialisation.setnNonOrthogonalCorrectors(potentialFlowDict.lookupInt(N_NON_ORTHOGONAL_CORRECTORS_KEY));
            }
            field.setInitialisation(pfInitialisation);
            map.put(field.getName(), pfInitialisation);
        }
    }

    private void readCellSetInitialisation(Field field) {
        SetFieldsDict setFieldsDict = model.getProject().getSystemFolder().getSetFieldsDict();
        SetFieldsDictConverter converter = new SetFieldsDictConverter(setFieldsDict, field);
        if (converter.isFieldCellSetInitialized()) {
            Dictionary convertedDict = converter.readFromSetFieldsDict();

            LoadSaveInitialisation loadSaveInitialisation = new LoadSaveInitialisation(model.getProject().getBaseDir(), model.getState(), monitor);
            Initialisation i = loadSaveInitialisation.load(convertedDict, Fields.getFieldTypeByName(field.getName()));
            field.setInitialisation(i);
            map.put(field.getName(), i);
        }
    }

    public void initializeFields() {
        for (Field field : model.getFields().values()) {
            initializeField(field);
        }
    }

    private void initializeField(Field field) {
        String fieldName = field.getName();
        logger.info("Inititalising field: {} as {}", fieldName, field.getInitialisation());

        initializeFixedValue(field);
    }

    private void initializeFixedValue(Field field) {
        Initialisation init = field.getInitialisation();
        InternalField internalField = null;
        if (init instanceof FixedScalarInitialisation) {
            double value = ((FixedScalarInitialisation) init).getValue();
            internalField = new ScalarInternalField(value);
        } else if (init instanceof FixedVectorInitialisation) {
            double[] value = ((FixedVectorInitialisation) init).getValue();
            internalField = new ArrayInternalField(value);
        }
        if (internalField != null) {
            field.setInternalField(internalField);
            Fields[] parallelFields = model.getFields().getParallelFields();
            if (parallelFields != null) {
                for (int i = 0; i < parallelFields.length; i++) {
                    Field subField = parallelFields[i].get(field.getName());
                    subField.setInternalField(internalField);
                }
            }
            if (field.getName().equals(Fields.P) || field.getName().equals(Fields.P_RGH)) {
                writeValueOnProcBoundary(field);
            }
        }
    }

    private void writeValueOnProcBoundary(Field field) {
        Initialisation init = field.getInitialisation();
        if (init instanceof FixedScalarInitialisation) {
            double value = ((FixedScalarInitialisation) init).getValue();
            for (Patch patch : model.getPatches()) {
                if (patch.getPhysicalType().isProcessor()) {
                    Dictionary momentum = patch.getBoundaryConditions().getMomentum();
                    if (momentum.found(Fields.P)) {
                        Dictionary p = momentum.subDict(Fields.P);
                        p.addUniform(Dictionary.VALUE, value);
                    }
                }
            }
        }
    }
}
