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

package eu.engys.gui.casesetup.materials;

import static eu.engys.core.project.constant.TransportProperties.MATERIAL_NAME_KEY;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.constant.ThermophysicalProperties;
import eu.engys.core.project.constant.TransportProperties;
import eu.engys.core.project.materials.Material;
import eu.engys.core.project.materials.Materials;
import eu.engys.core.project.materials.MaterialsReader;
import eu.engys.util.progress.ProgressMonitor;

public abstract class AbstractMaterialsReader implements MaterialsReader {
	
	@Override
	public void readSingle_Material(Materials materials, TransportProperties tpp, ProgressMonitor monitor) {
		readMaterial(materials, tpp, monitor);
	}

	@Override
	public void readSingle_Material(Materials materials, ThermophysicalProperties tfp, ProgressMonitor monitor) {
		readMaterial(materials, tfp, monitor);
	}

	private void readMaterial(Materials materials, Dictionary tfp, ProgressMonitor monitor) {
		Dictionary dict = new Dictionary(tfp);
		dict.setFoamFile(null);
		if (dict.isEmpty()) {
			monitor.warning("No material found", 1);
			return;
		} else {
			if (!dict.found(MATERIAL_NAME_KEY)) {
				dict.add(MATERIAL_NAME_KEY, "material");
			}
			String name = dict.lookup(MATERIAL_NAME_KEY);
			dict.setName(name);
			materials.add(new Material(name, dict));
			monitor.info(name, 1);
		}
	}

}
