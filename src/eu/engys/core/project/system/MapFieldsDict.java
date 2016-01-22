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


package eu.engys.core.project.system;

import static eu.engys.core.project.system.SystemFolder.SYSTEM;

import java.io.File;
import java.util.Map;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.dictionary.FieldElement;
import eu.engys.core.dictionary.FoamFile;

public class MapFieldsDict extends Dictionary {

    public static final String MAP_FIELDS_DICT = "mapFieldsDict";

    public static final String PATCH_MAP_KEY = "patchMap";
    public static final String CUTTING_PATCHES_KEY = "cuttingPatches";
    public static final String SOURCE_CASE_KEY = "sourceCase";
    public static final String PARALLEL_SOURCE_KEY = "parallelSource";
    public static final String SOURCE_TIME_OPTION_KEY = "sourceTimeOption";
    public static final String SOURCE_TIME_VALUE_KEY = "sourceTimeValue";
    public static final String TARGET_TIME_OPTION_KEY = "targetTimeOption";
    public static final String TARGET_TIME_VALUE_KEY = "targetTimeValue";
    public static final String CONSISTENT_KEY = "consistent";

    public static final String LATEST_TIME_KEY = "latestTime";
    public static final String ALL_TIMES_KEY = "allTimes";
    public static final String[] SOURCE_TIME_OPTION_KEYS = new String[] { LATEST_TIME_KEY, SOURCE_TIME_VALUE_KEY, ALL_TIMES_KEY };
    public static final String[] TARGET_TIME_OPTION_KEYS = new String[] { LATEST_TIME_KEY, TARGET_TIME_VALUE_KEY, SOURCE_TIME_VALUE_KEY };

    public MapFieldsDict() {
        super(MAP_FIELDS_DICT);
        setFoamFile(FoamFile.getDictionaryFoamFile(SYSTEM, MAP_FIELDS_DICT));
    }

    public MapFieldsDict(File file) {
        super(file);
    }

    public MapFieldsDict(Dictionary dict) {
        super(dict);
        setFoamFile(FoamFile.getDictionaryFoamFile(SYSTEM, MAP_FIELDS_DICT));
    }

    @Override
    public void check() throws DictionaryException {
    }

    @Override
    public void merge(Dictionary dict) {
        if (dict instanceof MapFieldsDict) {
            ((MapFieldsDict) dict).functionObjectsToDict();
        }
        functionObjectsToDict();
        super.merge(dict);
        functionObjectsToList();
        if (dict instanceof MapFieldsDict) {
            ((MapFieldsDict) dict).functionObjectsToList();
        }

    }

    private void functionObjectsToDict() {
        if (found(PATCH_MAP_KEY)) {
            // () is recognized as empty list
            boolean isEmptyList = isList(PATCH_MAP_KEY) && getList(PATCH_MAP_KEY).isEmpty();
            boolean isField = isField(PATCH_MAP_KEY);
            if (isEmptyList || isField) {
                Dictionary functionsDict = new Dictionary(PATCH_MAP_KEY);
                if (isField) {
                    String patchMap = lookup(PATCH_MAP_KEY);
                    String[] patches = DictionaryUtils.string2StringArray(patchMap);
                    for (int i = 0; i < patches.length; i += 2) {
                        String el1 = patches[i];
                        String el2 = patches[i + 1];
                        functionsDict.add(new FieldElement(el1, el2));
                    }
                }
                remove(PATCH_MAP_KEY);
                add(functionsDict);
            }
        }
    }

    private void functionObjectsToList() {
        if (found(PATCH_MAP_KEY) && isDictionary(PATCH_MAP_KEY)) {
            Dictionary functionsDict = subDict(PATCH_MAP_KEY);
            remove(PATCH_MAP_KEY);
            Map<String, String> fieldsMap = functionsDict.getFieldsMap();
            StringBuilder sb = new StringBuilder("(");
            for (String key : fieldsMap.keySet()) {
                sb.append(key + " ");
                sb.append(fieldsMap.get(key) + " ");
            }
            sb.append(")");
            add(PATCH_MAP_KEY, sb.toString());
        }
    }

}
