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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.constant.ThermophysicalProperties;
import eu.engys.core.project.constant.TransportProperties;
import eu.engys.core.project.materials.Materials;
import eu.engys.core.project.materials.MaterialsWriter;

public abstract class AbstractMaterialsWriter implements MaterialsWriter {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMaterialsWriter.class);

	@Override
	public void writeSingle_IncompressibleMaterial(Materials materials, TransportProperties tpp) {
		if (materials.size() == 1) {
			tpp.merge(materials.get(0).getDictionary());
		} else {
			logger.warn("Multiphase solution choosen but '{}' materials found", materials.size());
		}
	}

	@Override
	public void writeSingle_CompressibleMaterial(Materials materials, ThermophysicalProperties tfp) {
		if (materials.size() == 1) {
			tfp.merge(materials.get(0).getDictionary());
		} else {
			logger.warn("Multiphase solution choosen but '{}' materials found", materials.size());
		}
	}

}
