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

package eu.engys.core.dictionary;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.parser.DictionaryReader2;
import eu.engys.util.progress.ProgressMonitor;

public final class DictionaryUtils {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryUtils.class);

    public static Dictionary readDictionary(String text) {
        Dictionary dictionary = new Dictionary("");
        dictionary.readDictionaryFromString(text);
        return dictionary;
    }

    public static Dictionary readDictionary(File file, ProgressMonitor monitor) {
        String name = file.getName();
        if (file.exists()) {
            if (monitor != null) {
                monitor.info(file.getName(), 1);
            }
            return new Dictionary(file);
        }
        if (monitor != null)
            monitor.warning(file.getName() + " NOT FOUND", 1);

        return new Dictionary(name);
    }

    public static Dictionary readDictionary2(File file) {
        String name = file.getName();
        String parent = file.getParent();
        if (file.exists()) {
            Dictionary d = new Dictionary(name);
            d.setFoamFile(FoamFile.getDictionaryFoamFile(parent, name));
            DictionaryReader2 reader = new DictionaryReader2(d);
            reader.read(file);

            return d;
        }

        return new Dictionary(name);
    }

    public static Dictionary readDictionary(File file, Dictionary linkDestination) {
        String name = file.getName();
        if (file.exists()) {
            Dictionary d = new Dictionary(name);
            DictionaryReader reader = new DictionaryReader(d, new DictionaryLinkResolver(linkDestination));
            reader.read(file);

            return d;
        }
        return new Dictionary(name);
    }

    public static void removeDictionary(File parent, Dictionary dict, ProgressMonitor monitor) {
        if (dict != null) {
            File dictFile = new File(parent, dict.getName());
            if (dictFile.exists()) {
                logger.info("REMOVE: " + dict.getName() + " -> " + dictFile.getAbsolutePath());
                if (!dictFile.delete())
                    logger.warn("REMOVE: Cannot remove {}", dictFile);
            } else {
                logger.warn("REMOVE: File {} does not exist", dictFile);
            }
        }
    }

    public static void writeDictionary(File parent, Dictionary dict, ProgressMonitor monitor) {
        if (dict != null) {
            File dictFile = new File(parent, dict.getName());
            writeDictionaryFile(dictFile, dict);
            String msg = "WRITE: " + dict.getName() + " -> " + dictFile.getAbsolutePath();
            if (monitor != null) {
                monitor.info(dict.getName(), 1);
            }
            logger.info(msg);
        } else {
            logger.warn("Dictionary NOT FOUND in parent " + parent, 1);
        }
    }

    public static void writeDictionaryFile(File file, Dictionary dictionary) {
        String text = dictionary.write();
        try {
            FileUtils.writeStringToFile(file, text);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static Dictionary header(String parent, Dictionary dict) {
        if (dict == null)
            return null;
        dict.setFoamFile(FoamFile.getDictionaryFoamFile(parent, dict.getName()));
        return dict;
    }

    public static String[] string2StringArray(String list) {
        String trimmedString = list.trim();
        String stringWithoutParentheses = trimmedString.substring(1, trimmedString.length() - 1).trim();
        if(stringWithoutParentheses.isEmpty()){
            return new String[0];
        } else {
            return stringWithoutParentheses.split("\\s+");
        }
    }

    public static String stringArray2String(String[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (String value : values) {
            sb.append(value);
            sb.append(" ");
        }
        sb.append(")");
        return sb.toString();
    }

    public static void copyIfFound(Dictionary dest, Dictionary source, String key) {
        if (source.found(key)) {
            dest.add(key, source.lookup(key));
        }
    }

}
