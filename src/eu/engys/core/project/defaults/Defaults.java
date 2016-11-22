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
package eu.engys.core.project.defaults;

import javax.inject.Inject;

import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.system.BlockMeshDict;
import eu.engys.core.project.system.MapFieldsDict;
import eu.engys.core.project.system.SnappyHexMeshDict;
import eu.engys.core.project.system.StretchMeshDict;

public class Defaults extends AbstractDefaultsProvider {

	private DictDataFolder dictDataFolder;
	private Dictionary defaultsDictionary;

	@Inject
	public Defaults(DictDataFolder folder) {
		this.dictDataFolder = folder;
		this.defaultsDictionary = new Dictionary(folder.getFile("caseSetupDict.defaults")).subDict("defaults");
		LoggerFactory.getLogger(Defaults.class).info("-> Defaults");
	}

	@Override
	public String getName() {
		return "Main";
	}

	public DictDataFolder getDictDataFolder() {
		return dictDataFolder;
	}

	private Dictionary getDefaultsDict() {
		return defaultsDictionary;
	}

	@Override
	public Dictionary getStates() {
		return getDefaultsDict().subDict("states");
	}

	@Override
	public Dictionary getDefaultStateData() {
		return getDefaultsDict().subDict("stateData");
	}

	@Override
	public Dictionary getDefaultFieldsData() {
		return getDefaultsDict().subDict("fields");
	}

	public Dictionary getCompressibleMaterials() {
		return new Dictionary(dictDataFolder.getFile("caseSetupDict.materialProperties.compressible")).subDict("materialProperties");
	}

	public Dictionary getIncompressibleMaterials() {
		return new Dictionary(dictDataFolder.getFile("caseSetupDict.materialProperties.incompressible")).subDict("materialProperties");
	}

	@Override
	public Dictionary getDefaultTurbulenceProperties() {
		return getDefaultsDict().subDict("turbulenceProperties");
	}

	public Dictionary getDefaultFunctions() {
		return getDefaultsDict().subDict("functions");
	}

	public Dictionary getDefaultSchemes() {
		return getDefaultsDict().subDict("schemes");
	}

	public SnappyHexMeshDict getDefaultSnappyHexMeshDict() {
		return new SnappyHexMeshDict(dictDataFolder.getFile("createCase.snappyHexMeshDict"));
	}

	public Dictionary getDefaultStretchMeshDict() {
		return new StretchMeshDict(dictDataFolder.getFile("createCase.stretchMeshDict"));
	}

	public BlockMeshDict getDefaultBlockMeshDict() {
		return new BlockMeshDict(dictDataFolder.getFile("createCase.blockMeshDict"));
	}

	public Dictionary getDefaultFvSchemes() {
		return new Dictionary(dictDataFolder.getFile("createCase.fvSchemes"));
	}

	public Dictionary getDefaultFvSolution() {
		return new Dictionary(dictDataFolder.getFile("createCase.fvSolution"));
	}

	public Dictionary getDefaultFvOptions() {
		return new Dictionary("");
	}

	public Dictionary getDefaultControlDict() {
		return new Dictionary(dictDataFolder.getFile("createCase.controlDict"));
	}

	public Dictionary getDefaultRunDict() {
		return new Dictionary(dictDataFolder.getFile("createCase.runDict"));
	}

	public MapFieldsDict getDefaultMapFieldsDict() {
		return new MapFieldsDict(dictDataFolder.getFile("createCase.mapFieldsDict"));
	}

	public Dictionary getDefaultDecomposeParDict() {
		return new Dictionary(dictDataFolder.getFile("createCase.decomposeParDict"));
	}

	public Dictionary getDefaultCustomNodeDict() {
		return new Dictionary(dictDataFolder.getFile("createCase.customNodeDict"));
	}

	public Dictionary getDefaultShapes() {
		return new Dictionary(dictDataFolder.getFile("createCase.shapes"));
	}

}
