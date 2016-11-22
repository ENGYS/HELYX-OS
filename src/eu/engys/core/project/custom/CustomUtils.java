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
package eu.engys.core.project.custom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.custom.Custom.ConstantDirectory;
import eu.engys.core.project.custom.Custom.RootDirectory;
import eu.engys.core.project.custom.Custom.SystemDirectory;
import eu.engys.core.project.custom.Custom.ZeroDirectory;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.FieldReader;

public class CustomUtils {

    public static List<File> getFiles(Model model, CustomFile customFile) {
        openFOAMProject project = model.getProject();
        List<File> files = new ArrayList<>();
        Path basePath = Paths.get(project.getBaseDir().getAbsolutePath());
        if (isInAVirtualPath(customFile) && project.isParallel()) {
            for (int i = 0; i < project.getProcessors(); i++) {
                Path initialPath = basePath.resolve("processor" + i);
                files.add(getFile(model, customFile, initialPath));
            }
        } else {
            files.add(getFile(model, customFile, basePath));
        }
        return files;
    }

    private static File getFile(Model model, CustomFile customFile, Path initialPath) {
        List<String> path = new ArrayList<>();
        addParentsToList(path, customFile);
        path.add(customFile.getName());

        for (String p : path) {
            initialPath = initialPath.resolve(p);
        }
        return initialPath.toFile();
    }

    private static void addParentsToList(List<String> path, CustomFile file) {
        CustomFile parentFile = file.getParent();
        if (parentFile instanceof RootDirectory) {
            return;
        }
        boolean hasToStop = (parentFile instanceof ConstantDirectory) || (parentFile instanceof SystemDirectory) || (parentFile instanceof ZeroDirectory);
        if (!hasToStop) {
            addParentsToList(path, file.getParent());
        }
        path.add(parentFile.getName());
    }

    private static boolean isInAVirtualPath(CustomFile file) {
        if (file instanceof RootDirectory) {
            return false;
        }
        if (isVirtualFolder(file)) {
            return true;
        }
        return isInAVirtualPath(file.getParent());
    }

    public static boolean isVirtualFolder(CustomFile customFile) {
        boolean isParentZeroDirectory = customFile instanceof ZeroDirectory;
        boolean isParentPolyMeshOfConstantFolder = customFile.getType().isDirectory() && "polyMesh".equals(customFile.getName()) && (customFile.getParent() instanceof ConstantDirectory);
        return isParentZeroDirectory || isParentPolyMeshOfConstantFolder;
    }

    public static void loadFromDisk(String name, CustomFile customFile, File file) {
        if (customFile.getType().isDictionary()) {
            customFile.getDictionary().merge(new Dictionary(file));
        } else if (customFile.getType().isField()) {
            Field field = new Field(name);
            new FieldReader(field).read(file);
            Dictionary dict = new Dictionary(name);
            dict.add(getCleanBoundaryField(field));
            customFile.getDictionary().merge(dict);
        } else if (customFile.getType().isRaw()) {
            customFile.getRawFileContent().clear();
            try {
                customFile.getRawFileContent().addAll(FileUtils.readLines(file));
            } catch (IOException e) {
            }
        }
    }

    public static Dictionary getCleanBoundaryField(Field field) {
        Dictionary boundaryField = new Dictionary(field.getBoundaryField());
        List<Dictionary> toRemoveList = new ArrayList<>();
        if (boundaryField != null) {
            for (Dictionary dict : boundaryField.getDictionaries()) {
                if (dict.getName().startsWith("procBoundary")) {
                    toRemoveList.add(dict);
                } else {
                    dict.clear();
                }
            }
        }
        for (Dictionary d : toRemoveList) {
            boundaryField.remove(d.getName());
        }
        return boundaryField;
    }

}
