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

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.FoamFile;

public class SetFieldsDict extends Dictionary {
    
    public static final String SET_FIELDS_DICT = "setFieldsDict";
    
    public static final String CELL_SET_KEY = "cellSet";
    public static final String VOL_SCALAR_FIELD_VALUE_KEY = "volScalarFieldValue";
    public static final String FIELD_VALUES_KEY = "fieldValues";
    public static final String REGIONS_KEY = "regions";
    public static final String SET_SOURCES_KEY = "setSources";
    public static final String DEFAULT_FIELD_VALUES_KEY = "defaultFieldValues";
    public static final String DEFAULT_VALUE_KEY = "defaultValue";
    
    public static final String BOX_TO_CELL_KEY ="boxToCell";
    public static final String SPHERE_TO_CELL_KEY ="sphereToCell";
    public static final String CYLINDER_TO_CELL_KEY ="cylinderToCell";
    public static final String RING_TO_CELL_KEY ="ringToCell";

    public SetFieldsDict() {
        super(SET_FIELDS_DICT);
        setFoamFile(FoamFile.getDictionaryFoamFile(SYSTEM, SET_FIELDS_DICT));
    }

    public SetFieldsDict(File file) {
        super(file);
    }

    public SetFieldsDict(Dictionary dict) {
        super(dict);
        setFoamFile(FoamFile.getDictionaryFoamFile(SYSTEM, SET_FIELDS_DICT));
    }

    @Override
    public void check() throws DictionaryException {
    }

}
