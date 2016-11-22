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

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.dictionary.Dictionary.VALUE;
import static eu.engys.core.project.constant.TurbulenceProperties.FIELD_MAPS_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.LES_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.RAS_KEY;
import static eu.engys.core.project.zero.patches.BoundaryType.CYCLIC_AMI_KEY;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FieldElement;
import eu.engys.core.project.defaults.DefaultsProvider;
import eu.engys.core.project.state.State;
import eu.engys.core.project.zero.patches.BoundaryType;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.core.project.zero.patches.Patches;
import eu.engys.util.progress.SilentMonitor;

public class FieldsDefaults {

    private static final Logger logger = LoggerFactory.getLogger(FieldsDefaults.class);
    private static Map<String, Initialisation> oldInitialisations = new HashMap<>();

    private DefaultsProvider defaults;

    private FieldsDefaults(DefaultsProvider defaults) {
        this.defaults = defaults;
    }

    public static void prepareFields(Fields fields) {
        saveInitializations(fields);
        fields.clear();
    }

    private static void saveInitializations(Fields fieldsMap) {
        oldInitialisations.clear();

        for (String key : fieldsMap.keySet()) {
            oldInitialisations.put(key, fieldsMap.get(key).getInitialisation());
        }
    }

    private static void applySavedInitialization(Field field) {
        if (oldInitialisations.containsKey(field.getName())) {
            Initialisation initialisation = oldInitialisations.get(field.getName());
            field.setInitialisation(initialisation);
        }
    }

    public static Fields loadFieldsFromDefaults(File baseDir, State state, DefaultsProvider defaults, Patches patches, String region) {
        return new FieldsDefaults(defaults).loadDefaultFields(baseDir, state, patches, region);
    }

    public static Field loadFieldFromDefaults(File baseDir, State state, String name, DefaultsProvider defaults, Patches patches) {
        return new FieldsDefaults(defaults).loadDefaultField(baseDir, state, name, name, patches);
    }

    private Field loadDefaultField(File baseDir, State state, String name, String value, Patches patches) {
        return newFieldFromDefaults(baseDir, state, name, value, patches);
    }

    private Fields loadDefaultFields(File baseDir, State state, Patches patches, String region) {
        Dictionary defaultFields = getDefaultFieldMaps(state, region);

        Fields fields = addNewFields(baseDir, state, defaultFields, patches);
        if (patches.getParallelPatches() != null) {
            Fields[] parallelFields = new Fields[patches.getParallelPatches().length];
            for (int i = 0; i < parallelFields.length; i++) {
                parallelFields[i] = addNewFields(baseDir, state, defaultFields, patches);
            }
            fields.setParallelFields(parallelFields);
        }

        return fields;
    }

    private Dictionary getDefaultFieldMaps(State state, String region) {
        Dictionary fieldsMap = new Dictionary(FIELD_MAPS_KEY);
        fieldsMap.merge(getTurbulenceFieldMaps(state, defaults, region));
        fieldsMap.merge(getStateFieldMaps(state, defaults, region));
        return fieldsMap;
    }

    private Dictionary getStateFieldMaps(State state, DefaultsProvider defaults, String region) {
        return defaults.getDefaultsFieldMapsFor(state, region);
    }

    private Dictionary getTurbulenceFieldMaps(State state, DefaultsProvider defaults, String region) {
        if (state.getTurbulenceModel() != null && !state.getFlow().isNone() && !state.getMethod().isNone() && !state.getSolverType().isNone()) {
            String modelName = state.getTurbulenceModel().getName();
            String compType = state.getSolverType().isCoupled() ? "coupledIncompressible" : state.isCompressible() ? "compressible" : "incompressible";
            String turbType = state.isLES() ? LES_KEY : RAS_KEY;
            String modelCoeffs = modelName + "Coeffs";

            Dictionary fieldMaps = new Dictionary(FIELD_MAPS_KEY);

            Dictionary tpp = defaults.getDefaultTurbulenceProperties();
            if (tpp != null && tpp.isDictionary(compType + turbType)) {
                if (tpp.subDict(compType + turbType).isDictionary(modelCoeffs)) {
                    Dictionary defCoeff = tpp.subDict(compType + turbType).subDict(modelCoeffs);
                    if (region != null) {
                        if (defCoeff.found(FIELD_MAPS_KEY + "." + region)) {
                            fieldMaps.merge(defCoeff.subDict(FIELD_MAPS_KEY + "." + region));
                        }
                    } else {
                        if (defCoeff.found(FIELD_MAPS_KEY)) {
                            fieldMaps.merge(defCoeff.subDict(FIELD_MAPS_KEY));
                        }
                    }
                }
            }
            return fieldMaps;
        }
        return new Dictionary("");
    }

    private Fields addNewFields(File baseDir, State state, Dictionary defaultFieldsMap, Patches patches) {
        Fields fields = new Fields();
        for (FieldElement element : defaultFieldsMap.getFields()) {
            String name = element.getName();
            String value = element.getValue();

            Field field = newFieldFromDefaults(baseDir, state, name, value, patches);

            applySavedInitialization(field);

            fields.put(name, field);
        }
        return fields;
    }

    private Field newFieldFromDefaults(File baseDir, State state, String name, String value, Patches patches) {
        Dictionary defaultsDictionary = getDefaultsDictionary(value);

        Field field = new Field(name);
        if (defaultsDictionary.found("allowedFieldInitialisationMethods")) {
            field.setInitialisationMethods(defaultsDictionary.lookupArray("allowedFieldInitialisationMethods"));
        }
        if (defaultsDictionary.found("fieldDefinition")) {
            Dictionary definition = new Dictionary(defaultsDictionary.subDict("fieldDefinition"));
            field.setDefinition(definition);
            field.setDimensions(definition.lookup("dimensions"));
            field.setInternalField("internalField " + definition.lookup("internalField"));
            field.setBoundaryField(getBoundaryConditionsFromDefaults(Fields.getFieldTypeByName(field.getName()), patches, definition));
        }
        if (defaultsDictionary.found("initialisation")) {
            LoadSaveInitialisation loader = new LoadSaveInitialisation(baseDir, state, new SilentMonitor());
            Initialisation initialisation = loader.load(defaultsDictionary.subDict("initialisation"), Fields.getFieldTypeByName(field.getName()));
            field.setInitialisation(initialisation);
        }

        return field;
    }

    private Dictionary getDefaultsDictionary(String value) {
        Dictionary defaultsDictionary = defaults.getDefaultFieldsData().subDict(value);
        if (defaultsDictionary == null) {
            logger.warn("Cannot find {} into defaults", value);
            // JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Cannot find " + value + " into defaults", "Warning", JOptionPane.WARNING_MESSAGE);
            return new Dictionary("");
        }
        return defaultsDictionary;
    }

    private Dictionary getBoundaryConditionsFromDefaults(FieldType fieldType, Patches patches, Dictionary definition) {
        Dictionary regionDefaults = definition.subDict("boundaryConditions").subDict("regionDefaults");
        Dictionary boundaryField = new Dictionary("boundaryField");
        for (Patch patch : patches) {
            boundaryField.add(getDefaultBoundaryCondition(fieldType, regionDefaults, patch));
        }
        return boundaryField;
    }

    private static Dictionary getDefaultBoundaryCondition(FieldType fieldType, Dictionary regionDefaults, Patch patch) {
        String patchType = patch.getPhysicalType() == BoundaryType.OPENING ? BoundaryType.PATCH_KEY : patch.getPhysicalType().getKey();
        String patchName = patch.getName();

        if (regionDefaults.found(patchType)) {
            Dictionary defaultDictionary = regionDefaults.subDict(patchType);
            Dictionary fieldPatch = new Dictionary(patchName);
            fieldPatch.merge(defaultDictionary);
            return fieldPatch;
        } else if (patch.getPhysicalType().isCyclicAMI()) {
            Dictionary fieldPatch = new Dictionary(patchName);
            fieldPatch.add(TYPE, CYCLIC_AMI_KEY);
            fieldPatch.add(VALUE, fieldType == FieldType.SCALAR ? "uniform 0" : "uniform (0 0 0)");
            return fieldPatch;
        } else {
            Dictionary fieldPatch = new Dictionary(patchName);
            fieldPatch.add(TYPE, patchType);
            return fieldPatch;
        }
    }

    public static void setAsDefault(Field field, Patch patch) {
        String patchName = patch.getName();
        String fieldName = field.getName();

        try {
            if (field.getDefinition() != null && !field.getDefinition().isEmpty()) {
                Dictionary boundaryField = field.getBoundaryField();
                Dictionary regionDefaults = field.getDefinition().subDict("boundaryConditions").subDict("regionDefaults");

                Dictionary defaultDictionary = getDefaultBoundaryCondition(Fields.getFieldTypeByName(field.getName()), regionDefaults, patch);
                if (boundaryField.found(patchName) && !fieldName.equals("U")) {
                    Dictionary fieldPatch = boundaryField.subDict(patchName);
                    fieldPatch.clear();
                    fieldPatch.merge(defaultDictionary);
                } else {
                    Dictionary fieldPatch = new Dictionary(patchName);
                    fieldPatch.merge(defaultDictionary);
                    boundaryField.add(fieldPatch);
                }
            }
        } catch (Exception e) {
            logger.error(patchName + " " + fieldName, e);
        }
    }

}
