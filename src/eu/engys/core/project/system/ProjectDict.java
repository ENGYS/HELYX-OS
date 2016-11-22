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

import java.io.File;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.FoamFile;

public class ProjectDict extends Dictionary {
	public static final String PROJECT_DICT = "projectDict";
	public static final String MESH_INFO_DICT = "meshInfo";
	
	public ProjectDict() {
		super(PROJECT_DICT);
		setFoamFile(FoamFile.getDictionaryFoamFile(SystemFolder.SYSTEM, PROJECT_DICT));
	}

	public ProjectDict(File projectDictFile) {
		this();
		readDictionary(projectDictFile);
	}

	@Override
	public void check() throws DictionaryException {
	}

    public void setRunDict(Dictionary d) {
        RunDict runDict = new RunDict();
        runDict.merge(d);
        add(runDict);
    }
    
    public Dictionary getRunDict() {
        return subDict(RunDict.RUN_DICT);
    }

    public void setMeshInfoDict(Dictionary d) {
        MeshInfoDict meshInfoDict = new MeshInfoDict();
        meshInfoDict.merge(d);
        add(meshInfoDict);
    }

    public Dictionary getMeshInfoDict() {
        return subDict(MESH_INFO_DICT);
    }
    
    class MeshInfoDict extends Dictionary {
        public MeshInfoDict() {
            super(MESH_INFO_DICT);
        }
    }
}
