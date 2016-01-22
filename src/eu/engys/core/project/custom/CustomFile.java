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

import static eu.engys.core.project.system.ControlDict.CONTROL_DICT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.dictionary.FoamFile;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.ControlDict;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.util.LineSeparator;
import eu.engys.util.Util;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.UiUtil;

public class CustomFile {

    private static final Logger logger = LoggerFactory.getLogger(CustomFile.class);

    private final CustomFile parent;
    private Dictionary dictionary;
    private List<String> rawFileContent;
    private CustomFileType type = CustomFileType.DICTIONARY;
    private List<CustomFile> children = new ArrayList<>();

    private String name;

    private boolean changed;

    public CustomFile(CustomFile parent, CustomFileType type, String name) {
        this.parent = parent;
        this.type = type;
        this.name = name;
        this.rawFileContent = new LinkedList<String>();
        if (CONTROL_DICT.equals(name)) {
            this.dictionary = new ControlDict();
        } else {
            this.dictionary = new Dictionary(name);
            this.dictionary.setFoamFile(getFOAMFile(parent, name));
        }
    }

    private FoamFile getFOAMFile(CustomFile parent, String name) {
        if (type.isField()) {
            return FoamFile.getFieldFoamFile(name);
        } else if (type.isDictionary()) {
            return FoamFile.getDictionaryFoamFile(parent != null ? parent.getName() : "", name);
        } else {
            return null;
        }
    }

    public void add(CustomFile child) {
        children.add(child);
    }

    public void clear() {
        children.clear();
        dictionary.clear();
        rawFileContent.clear();
    }

    public void remove(CustomFile child) {
        children.remove(child);
    }

    public void remove(String childName) {
        CustomFile child = getChildByName(childName);
        if (child != null) {
            children.remove(child);
        }
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public List<String> getRawFileContent() {
        return rawFileContent;
    }

    public CustomFileType getType() {
        return type;
    }

    public CustomFile getParent() {
        return parent;
    }

    public List<CustomFile> getChildren() {
        return children;
    }

    public List<String> getChildrenNames() {
        List<String> names = new ArrayList<>();
        for (CustomFile c : getChildren()) {
            names.add(c.getName());
        }
        return names;
    }

    public CustomFile getChildByName(String name) {
        for (CustomFile c : getChildren()) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
    
    public void setChanged(boolean changed) {
        this.changed = changed;
    }
    
    public boolean hasChanged(){
        return changed;
    }

    public void write(Model model, ProgressMonitor monitor) {
        if (getType().isDirectory()) {
            writeDirectory(model, monitor);
        } else if (getType().isDictionary()) {
            writeDictionary(model, monitor);
        } else if (getType().isField()) {
            writeField(model, monitor);
        } else if (getType().isRaw()) {
            writeRaw(model, monitor);
        }
        changed = false;
    }

    private void writeRaw(Model model, ProgressMonitor monitor) {
        for (File f : CustomUtils.getFiles(model, this)) {
            try {
                if (!f.exists()) {
                    f.createNewFile();
                }
                String lineEnding = Util.isWindowsScriptStyle() ? LineSeparator.DOS.getSeparator() : LineSeparator.UNIX.getSeparator();
                FileUtils.writeLines(f, null, rawFileContent, lineEnding);
            } catch (IOException e) {
                logger.error("Cannot create new raw file: " + f);
                return;
            }
        }
    }

    private void writeDictionary(Model model, ProgressMonitor monitor) {
        for (File f : CustomUtils.getFiles(model, this)) {
            if (f.exists()) {
                Dictionary existingDictionary = null;
                if (CONTROL_DICT.equals(f.getName())) {
                    existingDictionary = new ControlDict(f);
                } else {
                    existingDictionary = new Dictionary(f);
                }
                existingDictionary.merge(dictionary);
                existingDictionary.setFoamFile(dictionary.getFoamFile());
                DictionaryUtils.writeDictionary(f.getParentFile(), existingDictionary, monitor);
            } else {
                DictionaryUtils.writeDictionary(f.getParentFile(), dictionary, monitor);
            }
        }
    }

    private void writeDirectory(Model model, ProgressMonitor monitor) {
        if (!children.isEmpty()) {
            for (File f : CustomUtils.getFiles(model, this)) {
                _writeDirectory(f);
            }
            for (CustomFile child : getChildren()) {
                if (child != null) {
                    child.write(model, monitor);
                } else {
                    logger.error("CustomNodeDict is currupted " + getName() + " has a NULL child!");
                }
            }
        }
    }

    private void _writeDirectory(File f) {
        try {
            f.mkdirs();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "No writing permissions on " + parent, "File System error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void writeField(Model model, ProgressMonitor monitor) {
        monitor.info(getName(), 1);
        for (File f : CustomUtils.getFiles(model, this)) {
            if (f.exists()) {
                writeExistingField(f, monitor);
            } else {
                DictionaryUtils.writeDictionary(f.getParentFile(), getDictionary(), monitor);
            }
        }
    }

    private void writeExistingField(File f, ProgressMonitor monitor) {
        Dictionary existingDictionary = new Dictionary(f);
        if (getDictionary().found(Field.BOUNDARY_FIELD)) {
            Dictionary customBoundaryField = getDictionary().subDict(Field.BOUNDARY_FIELD);
            if (existingDictionary.found(Field.BOUNDARY_FIELD)) {
                Dictionary existingBoundaryField = existingDictionary.subDict(Field.BOUNDARY_FIELD);
                existingBoundaryField.merge(customBoundaryField);
            } else {
                existingDictionary.add(customBoundaryField);
            }

        }
        existingDictionary.setFoamFile(getDictionary().getFoamFile());
        DictionaryUtils.writeDictionary(f.getParentFile(), existingDictionary, null);
    }

}
