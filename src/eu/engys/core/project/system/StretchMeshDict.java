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
package eu.engys.core.project.system;

import static eu.engys.core.project.system.SystemFolder.SYSTEM;

import java.io.File;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.FoamFile;

public class StretchMeshDict extends Dictionary {
    
    public static final String STRETCH_MESH_DICT = "stretchMeshDict";
    
    public static final String BASE_POINT_KEY = "basePoint";
    public static final String STRETCH_DIRECTION_KEY = "stretchDirection";
    public static final String INITIAL_LENGTH_KEY = "initialLength";
    public static final String STRETCH_LENGTH_KEY = "stretchLength";
    public static final String EXPANSION_RATIO_KEY = "expansionRatio";
    public static final String DELTA_KEY = "delta";
    public static final String SYMMETRIC_KEY = "symmetric";

    public StretchMeshDict() {
        super(STRETCH_MESH_DICT);
        setFoamFile(FoamFile.getDictionaryFoamFile(SYSTEM, STRETCH_MESH_DICT));
    }

    public StretchMeshDict(File file) {
        super(file);
    }

    public StretchMeshDict(Dictionary dict) {
        super(dict);
        setFoamFile(FoamFile.getDictionaryFoamFile(SYSTEM, STRETCH_MESH_DICT));
    }

    @Override
    public void check() throws DictionaryException {
    }

}
