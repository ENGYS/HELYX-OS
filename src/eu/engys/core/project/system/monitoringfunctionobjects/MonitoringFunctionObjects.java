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


package eu.engys.core.project.system.monitoringfunctionobjects;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.project.system.ControlDict.FUNCTIONS_KEY;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.system.ControlDict;
import eu.engys.util.progress.ProgressMonitor;

public class MonitoringFunctionObjects extends ArrayList<MonitoringFunctionObject> {

    private static Logger logger = LoggerFactory.getLogger(MonitoringFunctionObjects.class);

    public MonitoringFunctionObjects() {
        super();
    }

    /*
     * Save
     */

    public void save(Model model, Set<? extends MonitoringFunctionObjectType> types, ProgressMonitor monitor) {
        ControlDict controlDict = model.getProject().getSystemFolder().getControlDict();
        controlDict.functionObjectsToDict();
        Dictionary newFunctions = new Dictionary(FUNCTIONS_KEY);

        if (controlDict.found(FUNCTIONS_KEY)) {

            Dictionary oldFunctions = controlDict.subDict(FUNCTIONS_KEY);

            keepOldFunctionObjects_UnknownType(types, oldFunctions, newFunctions);

            deleteOldFunctionObjects_KnownType_Removed(model, types, oldFunctions);
        }

        for (MonitoringFunctionObject fo : this) {
            newFunctions.add(fo.getDictionary());
        }

        addFunctionObjectLibs(model, newFunctions);

        controlDict.add(newFunctions);
        controlDict.functionObjectsToList();
    }

    public void keepOldFunctionObjects_UnknownType(Set<? extends MonitoringFunctionObjectType> types, Dictionary oldFunctions, Dictionary newFunctions) {
        for (Dictionary d : oldFunctions.getDictionaries()) {
            if (d.found(TYPE)) {
                String type = d.lookup(TYPE);
                if (!isKnownFunctionObjectType(types, type)) {
                    newFunctions.add(new Dictionary(d));
                }
            }
        }
    }

    public void deleteOldFunctionObjects_KnownType_Removed(Model model, Set<? extends MonitoringFunctionObjectType> types, Dictionary oldFunctions) {
        for (Dictionary d : oldFunctions.getDictionaries()) {
            if (d.found(TYPE)) {
                String type = d.lookup(TYPE);
                if (isKnownFunctionObjectType(types, type)) {
                    if (!contains(d.getName())) {
                        File postProcFolder = new File(model.getProject().getBaseDir(), openFOAMProject.POST_PROC);
                        File foFolder = new File(postProcFolder, d.getName());
                        if (foFolder.exists()) {
                            logger.warn("{} function object deleted", d.getName());
                            FileUtils.deleteQuietly(foFolder);
                        }
                    }
                }
            }
        }
    }

    private boolean contains(String foName) {
        for (MonitoringFunctionObject fo : this) {
            if (fo.getName().equals(foName)) {
                return true;
            }
        }
        return false;
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

    public void load(ControlDict controlDict, Set<MonitoringFunctionObjectType> types, ProgressMonitor monitor) {
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

    private void dictionaryToFunction(Dictionary dictionary, Set<MonitoringFunctionObjectType> types) {
        if (dictionary.found(TYPE)) {
            String type = dictionary.lookup(TYPE);
            if (isKnownFunctionObjectType(types, type)) {
                add(createFunctionObject(dictionary, types, type));
            } else {
                logger.warn("Unknown Function Object TYPE {}", type);
            }
        }
    }

    private MonitoringFunctionObject createFunctionObject(Dictionary dictionary, Set<MonitoringFunctionObjectType> types, String type) {
        MonitoringFunctionObjectType foType = getFunctionObjectTypeByKey(types, type);
        MonitoringFunctionObject fo = new MonitoringFunctionObject(foType, dictionary);
        return fo;
    }

    public Map<String, MonitoringFunctionObject> toMap() {
        Map<String, MonitoringFunctionObject> map = new HashMap<String, MonitoringFunctionObject>();
        for (MonitoringFunctionObject zone : this) {
            map.put(zone.getName(), zone);
        }
        return Collections.unmodifiableMap(map);
    }

    public boolean isKnownFunctionObjectType(Set<? extends MonitoringFunctionObjectType> types, String typeKey) {
        for (MonitoringFunctionObjectType type : types) {
            if (type.getKey().equals(typeKey)) {
                return true;
            }
        }
        return false;
    }

    private MonitoringFunctionObjectType getFunctionObjectTypeByKey(Set<MonitoringFunctionObjectType> types, String typeKey) {
        for (MonitoringFunctionObjectType type : types) {
            if (type.getKey().equals(typeKey)) {
                return type;
            }
        }
        return null;
    }
}
