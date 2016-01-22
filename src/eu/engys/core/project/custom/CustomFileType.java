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

public enum CustomFileType {

    DICTIONARY("dictionary", "Dictionary"), FIELD("field", "Field"), DIRECTORY("directory", "Directory"), RAW("raw", "Raw File");

    private String key;
    private String label;

    private CustomFileType(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getKey() {
        return key;
    }

    public boolean isDirectory() {
        return key.equals(DIRECTORY.getKey());
    }

    public boolean isField() {
        return key.equals(FIELD.getKey());
    }

    public boolean isDictionary() {
        return key.equals(DICTIONARY.getKey());
    }

    public boolean isRaw() {
        return key.equals(RAW.getKey());
    }

    public static String[] keys() {
        CustomFileType[] all = values();
        String[] keys = new String[all.length];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = all[i].getKey();
        }
        return keys;
    }

    public static String[] labels() {
        CustomFileType[] all = values();
        String[] labels = new String[all.length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = all[i].getLabel();
        }
        return labels;
    }

}
