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

package eu.engys.core.project.custom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.CustomNodeDict;
import eu.engys.util.progress.ProgressMonitor;

public class Custom {

    public static final String HELYX_INTERNAL_TYPE = "helyx_type";

    private static final Logger logger = LoggerFactory.getLogger(Custom.class);

    private final RootDirectory root = new RootDirectory();
    private final ZeroDirectory zero = new ZeroDirectory();
    private final ConstantDirectory constant = new ConstantDirectory();
    private final SystemDirectory system = new SystemDirectory();

    private List<CustomFile> files = new ArrayList<>();

    public Custom() {
        clear();
        add(root);
        add(zero);
        add(constant);
        add(system);
    }

    public void add(CustomFile file) {
        CustomFile parent = file.getParent();
        if (parent != null) {
            if (files.contains(parent) && parent.getType().isDirectory()) {
                parent.add(file);
            }
        }
        files.add(file);
    }

    public void clear() {
        zero.clear();
        constant.clear();
        system.clear();
        files.clear();
    }

    public void remove(CustomFile file) {
        if (file.getType().isDirectory()) {
            for (CustomFile child : new ArrayList<CustomFile>(file.getChildren())) {
                remove(child);
            }
        }
        files.remove(file);
        file.getParent().remove(file);
    }

    public RootDirectory getRoot() {
        return root;
    }

    public ZeroDirectory getZero() {
        return zero;
    }

    public ConstantDirectory getConstant() {
        return constant;
    }

    public SystemDirectory getSystem() {
        return system;
    }

    public List<CustomFile> getParentFiles() {
        List<CustomFile> parents = new ArrayList<>();
        for (CustomFile file : files) {
            if (file != root && file.getType().isDirectory())
                parents.add(file);
        }
        return parents;
    }

    public void read(Model model, CustomNodeDict customDict, ProgressMonitor monitor) {
        if (customDict.found("system"))
            readFromCustomDict(model, system, customDict.subDict("system"));
        if (customDict.found("0"))
            readFromCustomDict(model, zero, customDict.subDict("0"));
        if (customDict.found("constant"))
            readFromCustomDict(model, constant, customDict.subDict("constant"));
    }

    private void readFromCustomDict(Model model, CustomFile parentFile, Dictionary dict) {
        for (Dictionary d : dict.getDictionaries()) {
            CustomFile customFile = null;
            Dictionary copyDict = new Dictionary(d);
            String type = copyDict.lookup(HELYX_INTERNAL_TYPE);
            copyDict.remove(HELYX_INTERNAL_TYPE);
            if (CustomFileType.DIRECTORY.getKey().equals(type)) {
                customFile = new CustomFile(parentFile, CustomFileType.DIRECTORY, copyDict.getName());
                readFromCustomDict(model, customFile, copyDict);
            } else if (CustomFileType.DICTIONARY.getKey().equals(type)) {
                customFile = new CustomFile(parentFile, CustomFileType.DICTIONARY, copyDict.getName());
                customFile.getDictionary().merge(copyDict);
            } else if (CustomFileType.FIELD.getKey().equals(type)) {
                customFile = new CustomFile(parentFile, CustomFileType.FIELD, copyDict.getName());
                customFile.getDictionary().merge(copyDict);
            } else if (CustomFileType.RAW.getKey().equals(type)) {
                customFile = new CustomFile(parentFile, CustomFileType.RAW, copyDict.getName());
                File file = CustomUtils.getFiles(model, customFile).get(0);
                customFile.getRawFileContent().clear();
                try {
                    customFile.getRawFileContent().addAll(FileUtils.readLines(file));
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            } else {
                logger.error("Wrong dictionary type found: " + type);
            }
            if (customFile != null) {
                parentFile.add(customFile);
            }
        }
    }

    public void write(Model model, ProgressMonitor monitor) {
        logger.info("--- Customise ---");
        system.write(model, monitor);
        constant.write(model, monitor);
        zero.write(model, monitor);
        logger.info("-----------------");
    }

    public void saveCustomDict(Model model) {
        CustomNodeDict customDict = new CustomNodeDict();
        saveCustomDict(root, customDict);
        model.getProject().getSystemFolder().setCustomNodeDict(customDict);
    }

    private void saveCustomDict(CustomFile file, Dictionary customDict) {
        if (file.getType().isDirectory()) {
            if (file instanceof RootDirectory) {
                saveChildrenOf(file, customDict);
            } else {
                Dictionary subdict = new Dictionary(file.getName());
                subdict.add(HELYX_INTERNAL_TYPE, CustomFileType.DIRECTORY.getKey());
                customDict.add(subdict);
                saveChildrenOf(file, subdict);
            }
        } else if (file.getType().isRaw()) {
            Dictionary subdict = new Dictionary(file.getName());
            subdict.add(Custom.HELYX_INTERNAL_TYPE, file.getType().getKey());
            customDict.add(subdict);
        } else {
            Dictionary subdict = new Dictionary(file.getName(), file.getDictionary());
            subdict.add(Custom.HELYX_INTERNAL_TYPE, file.getType().getKey());
            customDict.add(subdict);
        }
    }

    private void saveChildrenOf(CustomFile file, Dictionary customDict) {
        for (CustomFile child : file.getChildren()) {
            if (child != null) {
                saveCustomDict(child, customDict);
            } else {
                logger.error("NULL CHILD FOR: " + file.getName());
            }
        }
    }

    public class RootDirectory extends CustomFile {
        public RootDirectory() {
            super(null, CustomFileType.DIRECTORY, null);
        }
    }

    public class ZeroDirectory extends CustomFile {
        public ZeroDirectory() {
            super(root, CustomFileType.DIRECTORY, "0");
        }
    }

    public class ConstantDirectory extends CustomFile {
        public ConstantDirectory() {
            super(root, CustomFileType.DIRECTORY, "constant");
        }
    }

    public class SystemDirectory extends CustomFile {
        public SystemDirectory() {
            super(root, CustomFileType.DIRECTORY, "system");
        }
    }
}
