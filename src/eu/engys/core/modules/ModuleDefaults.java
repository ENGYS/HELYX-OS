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
package eu.engys.core.modules;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.defaults.AbstractDefaultsProvider;
import eu.engys.core.project.defaults.DefaultsProvider;

public class ModuleDefaults extends AbstractDefaultsProvider {
	
	private ApplicationModule module;
	private DefaultsProvider parent;
	private Dictionary fieldsData;
	private Dictionary stateData;
	private Dictionary turbulenceProperties;

	public ModuleDefaults(ApplicationModule module, DefaultsProvider parent, Dictionary linkResolver) {
		this.module = module;
		this.parent = parent;
		this.fieldsData = ModulesUtil.readDictionary(module, linkResolver, module.getName() + ".fields");
		this.stateData = ModulesUtil.readDictionary(module, linkResolver, module.getName() + ".stateData");
		this.turbulenceProperties = ModulesUtil.readDictionary(module, linkResolver, module.getName() + ".turbulenceProperties");
	}

	@Override
	public String getName() {
		return module.getName() + " module";
	}
	
	@Override
	public Dictionary getDefaultFieldsData() {
		return fieldsData;
	}

	@Override
	public Dictionary getDefaultStateData() {
		return stateData;
	}
	
	@Override
	public Dictionary getDefaultTurbulenceProperties() {
	    return turbulenceProperties;
	}

	@Override
	public Dictionary getStates() {
		return stateData.found("states") ? stateData.subDict("states") : parent.getStates();
	}
}