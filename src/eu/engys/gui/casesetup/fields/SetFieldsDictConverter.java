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

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.dictionary.Dictionary.UNIFORM;
import static eu.engys.core.dictionary.Dictionary.VALUE;
import static eu.engys.core.project.system.SetFieldsDict.CELL_SET_KEY;
import static eu.engys.core.project.system.SetFieldsDict.DEFAULT_FIELD_VALUES_KEY;
import static eu.engys.core.project.system.SetFieldsDict.DEFAULT_VALUE_KEY;
import static eu.engys.core.project.system.SetFieldsDict.FIELD_VALUES_KEY;
import static eu.engys.core.project.system.SetFieldsDict.REGIONS_KEY;
import static eu.engys.core.project.system.SetFieldsDict.SET_SOURCES_KEY;
import static eu.engys.core.project.system.SetFieldsDict.VOL_SCALAR_FIELD_VALUE_KEY;
import static eu.engys.core.project.system.SetFieldsDict.VOL_VECTOR_FIELD_VALUE_KEY;
import static eu.engys.core.project.zero.fields.Field.INITIALISATION_KEY;
import static eu.engys.core.project.zero.fields.Fields.U;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.system.SetFieldsDict;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;

public class SetFieldsDictConverter {

    private final String SCALAR_REGEXP(String fieldName) {
        return VOL_SCALAR_FIELD_VALUE_KEY + "\\s*" + fieldName + "\\s*(\\-?\\d+\\.?\\d*(?:[eE][+-]?\\d+)?)";
    }

    private final String VECTOR_REGEXP(String fieldName) {
        return VOL_VECTOR_FIELD_VALUE_KEY + "\\s*" + fieldName + "\\s+(\\(.*\\))";
    }

    private SetFieldsDict setFieldsDict;
    private Field field;

    public SetFieldsDictConverter(SetFieldsDict setFieldsDict, Field field) {
        this.setFieldsDict = setFieldsDict;
        this.field = field;
    }

    public boolean isFieldCellSetInitialized() {
        if (setFieldsDict.found(DEFAULT_FIELD_VALUES_KEY)) {
            String defaultFieldValues = setFieldsDict.lookupString(DEFAULT_FIELD_VALUES_KEY);
            Pattern pattern;
            if (field.getName().equals(U)) {
                pattern = Pattern.compile(VECTOR_REGEXP(field.getName()));
            } else {
                pattern = Pattern.compile(SCALAR_REGEXP(field.getName()));
            }
            Matcher matcher = pattern.matcher(defaultFieldValues);
            if (matcher.find() && matcher.groupCount() == 1) {
                return true;
            }
        }
        return false;
    }

    public Dictionary readFromSetFieldsDict() {
        Dictionary convertedDict = new Dictionary(INITIALISATION_KEY);
        readHeaderFromSetFieldsDict(convertedDict);
        readRegionsFromSetFieldsDict(convertedDict);
        return convertedDict;
    }

    private void readHeaderFromSetFieldsDict(Dictionary d) {
        d.add(TYPE, CELL_SET_KEY);
        if (setFieldsDict.found(DEFAULT_FIELD_VALUES_KEY)) {
            String defaultFieldValues = setFieldsDict.lookupString(DEFAULT_FIELD_VALUES_KEY);
            Pattern pattern;
            if (field.getName().equals(U)) {
                pattern = Pattern.compile(VECTOR_REGEXP(field.getName()));
            } else {
                pattern = Pattern.compile(SCALAR_REGEXP(field.getName()));
            }
            Matcher matcher = pattern.matcher(defaultFieldValues);
            if (matcher.find() && matcher.groupCount() == 1) {
                String value = matcher.group(1);
                d.add(DEFAULT_VALUE_KEY, UNIFORM + " " + value);
            }
        }
    }

    private void readRegionsFromSetFieldsDict(Dictionary d) {
        if (setFieldsDict.found(REGIONS_KEY)) {
            for (Dictionary dict : setFieldsDict.getList(REGIONS_KEY).getDictionaries()) {
                Dictionary cloneDict = new Dictionary(dict);
                if (cloneDict.found(FIELD_VALUES_KEY)) {
                    String fieldValues = cloneDict.lookup(FIELD_VALUES_KEY);
                    Pattern pattern;
                    if (field.getName().equals(U)) {
                        pattern = Pattern.compile(VECTOR_REGEXP(field.getName()));
                    } else {
                        pattern = Pattern.compile(SCALAR_REGEXP(field.getName()));
                    }
                    Matcher matcher = pattern.matcher(fieldValues);
                    if (matcher.find() && matcher.groupCount() == 1) {
                        String value = matcher.group(1);
                        cloneDict.add(VALUE, value);
                        cloneDict.remove(FIELD_VALUES_KEY);
                        d.addToList(SET_SOURCES_KEY, cloneDict);
                    }
                }
            }
        }
    }

    public void writeToSetFieldsDict(Dictionary toConvertDict) {
        if (!setFieldsDict.found(DEFAULT_FIELD_VALUES_KEY)) {
            setFieldsDict.add(DEFAULT_FIELD_VALUES_KEY, "");
        }
        
        String trimmed = setFieldsDict.lookupString(DEFAULT_FIELD_VALUES_KEY).trim();
        String defaultFieldValuesString = trimmed.isEmpty() ? trimmed : trimmed.substring(1, trimmed.length() - 1);
        defaultFieldValuesString += (" " + getDefaultFieldValue(toConvertDict));
        setFieldsDict.add(DEFAULT_FIELD_VALUES_KEY, "(" + defaultFieldValuesString.trim() + ")");

        if (toConvertDict.found(SET_SOURCES_KEY)) {
            for (Dictionary dict : toConvertDict.getList(SET_SOURCES_KEY).getDictionaries()) {
                Dictionary regionDict = getRegionDict(dict);
                if (regionDict != null) {
                    setFieldsDict.addToList(REGIONS_KEY, regionDict);
                }
            }
        }
    }

    private String getDefaultFieldValue(Dictionary d) {
        if (d.found(DEFAULT_VALUE_KEY)) {
            if (field.getName().equals(Fields.U)) {
                return VOL_VECTOR_FIELD_VALUE_KEY + " " + field.getName() + " " + doubleArrayToString(d.lookupDoubleArray(DEFAULT_VALUE_KEY));
            } else {
                return VOL_SCALAR_FIELD_VALUE_KEY + " " + field.getName() + " " + d.lookupDoubleUniform(DEFAULT_VALUE_KEY);
            }
        }
        return "";
    }

    private Dictionary getRegionDict(Dictionary dict) {
        if (dict.found(VALUE)) {
            Dictionary copy = new Dictionary(dict);
            if (field.getName().equals(Fields.U)) {
                copy.add(FIELD_VALUES_KEY, "( " + VOL_VECTOR_FIELD_VALUE_KEY + " " + field.getName() + " " + doubleArrayToString(copy.lookupDoubleArray(VALUE)) + " )");
                copy.remove(VALUE);
            } else {
                copy.add(FIELD_VALUES_KEY, "( " + VOL_SCALAR_FIELD_VALUE_KEY + " " + field.getName() + " " + copy.lookupDouble(VALUE) + " )");
                copy.remove(VALUE);
            }
            return copy;
        }
        return null;
    }

    private String doubleArrayToString(double[] array) {
        return "(" + array[0] + " " + array[1] + " " + array[2] + ")";
    }

}
