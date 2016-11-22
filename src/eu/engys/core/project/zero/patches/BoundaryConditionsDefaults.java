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

package eu.engys.core.project.zero.patches;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.fields.FieldsDefaults;

public class BoundaryConditionsDefaults {
    
    private static final Logger logger = LoggerFactory.getLogger(BoundaryConditionsDefaults.class);

    private static Map<String, BoundaryConditions> defaultBoundaryConditions = new HashMap<String, BoundaryConditions>();

    public static void updateBoundaryConditionsDefaultsByFields(Model model) {
        defaultBoundaryConditions.clear();

        Fields fields = model.getFields();
        Map<String, BoundaryType> typesMap = BoundaryType.getRegisteredBoundaryTypes();

        for (String type : typesMap.keySet()) {
            BoundaryConditions bc = extractDefaultBoundaryConditionsForType(fields.values(), type);
            defaultBoundaryConditions.put(type, bc);
        }
    }

    private static BoundaryConditions extractDefaultBoundaryConditionsForType(Collection<Field> fields, String patchType) {
        BoundaryConditions bc = new BoundaryConditions();
        for (Field field : fields) {
            if (field.getDefinition() != null && field.getDefinition().subDict("boundaryConditions") != null) {
                Dictionary regionDefaults = field.getDefinition().subDict("boundaryConditions").subDict("regionDefaults");
                String internalType = BoundaryType.isOpening(patchType) ? BoundaryType.PATCH_KEY : patchType;
                if (regionDefaults.found(internalType)) {
                    Dictionary regionDefaultsForType = regionDefaults.subDict(internalType);
                    bc.add(field.getName(), regionDefaultsForType);
                } else {
                    Dictionary fieldPatch = new Dictionary(field.getName());
                    fieldPatch.add(Dictionary.TYPE, patchType);
                    bc.add(field.getName(), fieldPatch);
                }
            } else {
                logger.error("No definition for field " + field.getName() + ": " + field.getDefinition());
            }
        }
        return bc;
    }

    public static BoundaryConditions get(String key) {
        return defaultBoundaryConditions.get(key);
    }

    public static Dictionary getPressureFor(BoundaryType type,  Dictionary def) {
		BoundaryConditions boundaryConditions = BoundaryConditionsDefaults.get(type.getKey());
		if (boundaryConditions != null) {
			Dictionary momentum = boundaryConditions.getMomentum();
			if (momentum != null) {
				if (momentum.isDictionary(Fields.P)) {
					return new Dictionary(momentum.subDict(Fields.P));
				}
			}
		}
		return new Dictionary(def);
	}

    public static Dictionary getRoughness() {
        BoundaryConditions boundaryConditions = BoundaryConditionsDefaults.get(BoundaryType.WALL_KEY);
        if (boundaryConditions != null) {
            Dictionary roughness = boundaryConditions.getRoughness();
            if (roughness != null) {
                return new Dictionary(roughness);
            }
        }
        return null;
    }

    public static Dictionary getDisplacement() {
        BoundaryConditions boundaryConditions = BoundaryConditionsDefaults.get(BoundaryType.WALL_KEY);
        if (boundaryConditions != null) {
            Dictionary displacement = boundaryConditions.getDisplacement();
            if (displacement != null) {
                return new Dictionary(displacement);
            }
        }
        return null;
    }

    public static void loadBoundaryConditionsFromFields(Patches patches, Fields fields) {
        patches.clearBoundaryConditions();
        
        fieldsToBoundaryConditions(patches, fields);
        
        new MergeBoundaryConditions(patches, fields).execute();
    }

    public static void fieldsToBoundaryConditions(Patches patches, Fields fields) {
        Fields[] parallelFields = fields.getParallelFields();
        Patches[] parallelPatches = patches.getParallelPatches();
        
        if (parallelFields != null && parallelPatches != null) {
            for (int i = 0; i < parallelFields.length; i++) {
                Fields map = parallelFields[i];
                Patches patchesOfProcessor = parallelPatches[i];
                for (Field field : map.values()) {
                    fieldToBoundaryConditions(field, patchesOfProcessor);
                }
            }
        }
    }

    private static void fieldToBoundaryConditions(Field field, Patches patches) {
        Map<String, Patch> patchesMap = patches.toMap();

        Dictionary boundaryField = field.getBoundaryField();

        for (Dictionary patchInField : boundaryField.getDictionaries()) {
            String patchName = patchInField.getName();
            Patch patch = patchesMap.get(patchName);

            if (patch != null) {
                patch.getBoundaryConditions().add(field.getName(), patchInField);
            }
        }
    }

    public static void saveBoundaryConditionsToFields(Patches patches, Fields fields) {
        new SplitBoundaryConditions(patches, fields).execute();

        boundaryConditionsToFields(patches, fields);
    }

    public static void boundaryConditionsToFields(Patches patches, Fields fieldsMap) {
        
        /* questa roba serve veramente ? */
        for (Field field : fieldsMap.values()) {
            boundaryConditionsToField(patches, field);
        }
        
        Fields[] parallelFields = fieldsMap.getParallelFields();
        if (parallelFields != null) {
            for (int i = 0; i < parallelFields.length; i++) {
                Fields processorFields = parallelFields[i];
                for (Field field : processorFields.values()) {
                    boundaryConditionsToField(patches.getPatchesOfProcessor(i), field);
                }
            }
        }
    }

    public static void boundaryConditionsToField(Patches patches, Field field) {
        Dictionary boundaryField = field.getBoundaryField();
        String fieldName = field.getName();
        for (Patch patch : patches) {
            String patchName = patch.getName();
            if (patch.getBoundaryConditions() != null) {
                Dictionary boundaryConditions = patch.getBoundaryConditions().toDictionary();
                if (boundaryConditions.found(fieldName)) {
                    Dictionary bcDictionary = boundaryConditions.subDict(fieldName);
                    if (boundaryField.found(patchName)) {
                        Dictionary fieldPatch = boundaryField.subDict(patchName);
                        fieldPatch.clear();
                        fieldPatch.merge(bcDictionary);
                    } else {
                        Dictionary fieldPatch = new Dictionary(patchName);
                        boundaryField.add(fieldPatch);
                        fieldPatch.merge(bcDictionary);
                    }

                } else if (fieldName.equals(Fields.P_RGH) && boundaryConditions.found(Fields.P)) {
                    Dictionary bcDictionary = boundaryConditions.subDict(Fields.P);
                    if (boundaryField.found(patchName)) {
                        Dictionary fieldPatch = boundaryField.subDict(patchName);
                        fieldPatch.clear();
                        fieldPatch.merge(bcDictionary);
                    } else {
                        Dictionary fieldPatch = new Dictionary(patchName);
                        boundaryField.add(fieldPatch);
                        fieldPatch.merge(bcDictionary);
                    }

                } else {
                    FieldsDefaults.setAsDefault(field, patch);
                }
            } else {
                FieldsDefaults.setAsDefault(field, patch);
            }
        }
    }
}
