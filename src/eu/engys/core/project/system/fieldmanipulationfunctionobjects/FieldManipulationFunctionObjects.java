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

package eu.engys.core.project.system.fieldmanipulationfunctionobjects;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.project.system.ControlDict.FUNCTIONS_KEY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.ControlDict;
import eu.engys.util.progress.ProgressMonitor;

public class FieldManipulationFunctionObjects extends ArrayList<FieldManipulationFunctionObject> {

    private static Logger logger = LoggerFactory.getLogger(FieldManipulationFunctionObjects.class);

    public FieldManipulationFunctionObjects() {
        super();
    }

    /*
     * Save
     */

    public void save(Model model, Set<? extends FieldManipulationFunctionObjectType> types, ProgressMonitor monitor) {
        ControlDict controlDict = model.getProject().getSystemFolder().getControlDict();
        controlDict.functionObjectsToDict();
        Dictionary newFunctions = new Dictionary(FUNCTIONS_KEY);

        if (controlDict.found(FUNCTIONS_KEY)) {

            Dictionary oldFunctions = controlDict.subDict(FUNCTIONS_KEY);

            keepOldFunctionObjects_UnknownType(types, oldFunctions, newFunctions);

        }

        for (FieldManipulationFunctionObject fo : this) {
            newFunctions.add(fo.getDictionary());
        }

        addFunctionObjectLibs(model, newFunctions);

        controlDict.add(newFunctions);
        controlDict.functionObjectsToList();
    }

    public void keepOldFunctionObjects_UnknownType(Set<? extends FieldManipulationFunctionObjectType> types, Dictionary oldFunctions, Dictionary newFunctions) {
        for (Dictionary d : oldFunctions.getDictionaries()) {
            if (d.found(TYPE)) {
                String type = d.lookup(TYPE);
                if (!isKnownFunctionObjectType(types, type)) {
                    newFunctions.add(new Dictionary(d));
                }
            }
        }
    }

    private void addFunctionObjectLibs(Model model, Dictionary newFunctions) {
        Dictionary defaultFunctions = model.getDefaults().getDefaultFunctions();
        for (Dictionary function : newFunctions.getDictionaries()) {
            String type = function.lookup(TYPE);
            if (defaultFunctions.found(type)) {
                function.add("functionObjectLibs", defaultFunctions.subDict(type).lookup("functionObjectLibs"));
            }
        }
    }

    /*
     * LOAD
     */

    public void load(ControlDict controlDict, Set<FieldManipulationFunctionObjectType> types, ProgressMonitor monitor) {
        if (controlDict != null && controlDict.isDictionary(FUNCTIONS_KEY)) {
            List<Dictionary> functions = controlDict.subDict(FUNCTIONS_KEY).getDictionaries();
            for (Dictionary dictionary : functions) {
                dictionaryToFunction(dictionary, types);
            }
        } else if (controlDict != null && controlDict.isList(FUNCTIONS_KEY)) {
            List<DefaultElement> functions = controlDict.getList(FUNCTIONS_KEY).getListElements();
            for (DefaultElement el : functions) {
                if (el instanceof Dictionary) {
                    Dictionary dictionary = (Dictionary) el;
                    dictionaryToFunction(dictionary, types);
                }
            }
        }
    }

    private void dictionaryToFunction(Dictionary dictionary, Set<FieldManipulationFunctionObjectType> types) {
        if (dictionary.found(TYPE)) {
            String type = dictionary.lookup(TYPE);
            if (isKnownFunctionObjectType(types, type)) {
                add(createFunctionObject(dictionary, types, type));
            } else {
                logger.warn("Unknown Function Object TYPE {}", type);
            }
        }
    }

    private FieldManipulationFunctionObject createFunctionObject(Dictionary dictionary, Set<FieldManipulationFunctionObjectType> types, String type) {
        FieldManipulationFunctionObjectType foType = getFunctionObjectTypeByKey(types, type);
        return new FieldManipulationFunctionObject(foType, dictionary);
    }

    public Map<String, FieldManipulationFunctionObject> toMap() {
        Map<String, FieldManipulationFunctionObject> map = new HashMap<String, FieldManipulationFunctionObject>();
        for (FieldManipulationFunctionObject zone : this) {
            map.put(zone.getName(), zone);
        }
        return Collections.unmodifiableMap(map);
    }

    public boolean isKnownFunctionObjectType(Set<? extends FieldManipulationFunctionObjectType> types, String typeKey) {
        for (FieldManipulationFunctionObjectType type : types) {
            if (type.getKey().equals(typeKey)) {
                return true;
            }
        }
        return false;
    }

    private FieldManipulationFunctionObjectType getFunctionObjectTypeByKey(Set<FieldManipulationFunctionObjectType> types, String typeKey) {
        for (FieldManipulationFunctionObjectType type : types) {
            if (type.getKey().equals(typeKey)) {
                return type;
            }
        }
        return null;
    }
}
