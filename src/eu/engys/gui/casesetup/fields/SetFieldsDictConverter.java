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


package eu.engys.gui.casesetup.fields;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.dictionary.Dictionary.VALUE;
import static eu.engys.core.project.system.SetFieldsDict.CELL_SET_KEY;
import static eu.engys.core.project.system.SetFieldsDict.DEFAULT_FIELD_VALUES_KEY;
import static eu.engys.core.project.system.SetFieldsDict.DEFAULT_VALUE_KEY;
import static eu.engys.core.project.system.SetFieldsDict.FIELD_VALUES_KEY;
import static eu.engys.core.project.system.SetFieldsDict.REGIONS_KEY;
import static eu.engys.core.project.system.SetFieldsDict.SET_SOURCES_KEY;
import static eu.engys.core.project.system.SetFieldsDict.VOL_SCALAR_FIELD_VALUE_KEY;
import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.system.SetFieldsDict;
import eu.engys.core.project.zero.fields.Field;

public class SetFieldsDictConverter {

    private Field field;

    public SetFieldsDictConverter(Field field) {
        this.field = field;
    }

    public Dictionary convertForRead(SetFieldsDict dictToFix) {
        Dictionary fixedDict = new Dictionary(dictToFix);
        fixHeaderForRead(fixedDict);
        fixRegionsForRead(fixedDict);
        fixedDict.setName("initialisation");
        return fixedDict;
    }

    public SetFieldsDict convertForWrite(Dictionary dictToFix) {
        SetFieldsDict fixedDict = new SetFieldsDict(dictToFix);
        fixHeaderForWrite(fixedDict);
        fixRegionsForWrite(fixedDict);
        fixedDict.setName(SetFieldsDict.SET_FIELDS_DICT);
        return fixedDict;
    }

    private void fixHeaderForRead(Dictionary dictionary) {
        if (!dictionary.found(TYPE)) {
            dictionary.add(TYPE, CELL_SET_KEY);
        }
        if (!dictionary.found(DEFAULT_VALUE_KEY) && dictionary.found(DEFAULT_FIELD_VALUES_KEY)) {
            String fixedValue = dictionary.lookup(DEFAULT_FIELD_VALUES_KEY).replace("(", "").replace(")", "").replace(VOL_SCALAR_FIELD_VALUE_KEY, "").replace(field.getName(), "").trim();
            dictionary.add(DEFAULT_VALUE_KEY, "uniform " + fixedValue);
            dictionary.remove(DEFAULT_FIELD_VALUES_KEY);
        }
    }

    private void fixRegionsForRead(Dictionary dictionary) {
        if (!dictionary.found(SET_SOURCES_KEY) && dictionary.found(REGIONS_KEY)) {
            for (DefaultElement element : dictionary.getList(REGIONS_KEY).getListElements()) {
                if (element instanceof Dictionary) {
                    Dictionary dict = (Dictionary) element;
                    if (!dict.found(VALUE) && dict.found(FIELD_VALUES_KEY)) {
                        String fixedValue = dict.lookup(FIELD_VALUES_KEY).replace("(", "").replace(")", "").replace(VOL_SCALAR_FIELD_VALUE_KEY, "").replace(field.getName(), "").trim();
                        dict.add(VALUE, fixedValue);
                        dict.remove(FIELD_VALUES_KEY);
                    }
                    dictionary.addToList(SET_SOURCES_KEY, dict);
                }
            }
            dictionary.remove(REGIONS_KEY);
        }
    }

    private void fixHeaderForWrite(Dictionary dictionary) {
        if (dictionary.found(TYPE)) {
            dictionary.remove(TYPE);
        }
        if (dictionary.found(DEFAULT_VALUE_KEY)) {
            String fixedValue = "( " + VOL_SCALAR_FIELD_VALUE_KEY + " " + field.getName() + " " + dictionary.lookup(DEFAULT_VALUE_KEY).replace("uniform", "").trim() + " )";
            dictionary.add(DEFAULT_FIELD_VALUES_KEY, fixedValue);
            dictionary.remove(DEFAULT_VALUE_KEY);
        }
    }

    private void fixRegionsForWrite(Dictionary dictionary) {
        if (dictionary.found(SET_SOURCES_KEY)) {
            for (DefaultElement element : dictionary.getList(SET_SOURCES_KEY).getListElements()) {
                if (element instanceof Dictionary) {
                    Dictionary dict = (Dictionary) element;
                    if (dict.found(VALUE)) {
                        String value = dict.lookup(VALUE);
                        dict.add(FIELD_VALUES_KEY, "( " + VOL_SCALAR_FIELD_VALUE_KEY + " " + field.getName() + " " + value + " )");
                        dict.remove(VALUE);
                    }
                    dictionary.addToList(REGIONS_KEY, dict);
                }
            }
            dictionary.remove(SET_SOURCES_KEY);
        }
    }

}
