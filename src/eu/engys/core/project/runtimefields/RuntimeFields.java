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
package eu.engys.core.project.runtimefields;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.project.system.ControlDict.FUNCTIONS_KEY;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.ListField;
import eu.engys.core.project.system.ControlDict;
import eu.engys.util.progress.ProgressMonitor;

public class RuntimeFields extends LinkedHashMap<String, RuntimeField> {

    public RuntimeFields() {
        super();
    }

    public List<RuntimeField> fields() {
        return new ArrayList<>(values());
    }

    public void load(ControlDict controlDict, ProgressMonitor monitor) {
        if (controlDict != null && controlDict.isDictionary(FUNCTIONS_KEY)) {
            List<Dictionary> functions = controlDict.subDict(FUNCTIONS_KEY).getDictionaries();
            for (Dictionary dictionary : functions) {
                loadRuntimeField(dictionary);
            }
        } else if (controlDict != null && controlDict.isList(FUNCTIONS_KEY)) {
            List<DefaultElement> functions = controlDict.getList(FUNCTIONS_KEY).getListElements();
            for (DefaultElement el : functions) {
                if (el instanceof Dictionary) {
                    Dictionary dictionary = (Dictionary) el;
                    loadRuntimeField(dictionary);
                }
            }
        }
    }

    private void loadRuntimeField(Dictionary dictionary) {
        if (dictionary.found(TYPE)) {
            String type = dictionary.lookup(TYPE);
            if (type.equals("fieldProcess")) {
                ListField operations = dictionary.getList("operations");
                for (DefaultElement de : operations.getListElements()) {
                    if (de instanceof Dictionary) {
                        Dictionary opDict = (Dictionary) de;
                        String fieldName = opDict.lookup("fieldName");
                        put(fieldName, new RuntimeField(fieldName));
                    }
                }
            }
        }
    }

    public void removeFields(Dictionary dictionary) {
        ListField operations = dictionary.getList("operations");
        for (DefaultElement de : operations.getListElements()) {
            if (de instanceof Dictionary) {
                Dictionary opDict = (Dictionary) de;
                String fieldName = opDict.lookup("fieldName");
                remove(fieldName);
            }
        }
    }

}
