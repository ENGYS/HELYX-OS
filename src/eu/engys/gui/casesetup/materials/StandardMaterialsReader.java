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

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardMaterialsReader extends AbstractMaterialsReader {

	private static final Logger logger = LoggerFactory.getLogger(StandardMaterialsReader.class);

	@Inject
	public StandardMaterialsReader() {
	}

//	@Override
//	public void readMultiphase_IncompressibleMaterials(Materials materials, Dictionary transportProperties) {
//		if (transportProperties.found("phases")) {
//			String[] phases = transportProperties.lookupArray("phases");
//			if (phases.length == 2) {
//				
//				Dictionary dict1 = new Dictionary(transportProperties.subDict(phases[0]));
//				if (!dict1.isEmpty()) {
//					if (!dict1.found("materialName")) {
//						dict1.add("materialName", "material1");
//					}
//					String name1 = dict1.lookup("materialName");
//					dict1.setName(name1);
//					materials.add(new Material(name1, dict1));
//				}
//
//				Dictionary dict2 = new Dictionary(transportProperties.subDict(phases[1]));
//				if (!dict2.isEmpty()) {
//					if (!dict2.found("materialName")) {
//						dict2.add("materialName", "material2");
//					}
//					String name2 = dict2.lookup("materialName");
//					dict2.setName(name2);
//					materials.add(new Material(name2, dict2));
//				}
//
//				if (transportProperties.found("sigma")) {
//					String sigmaValue = transportProperties.lookup("sigma");
//					materials.get(0).getDictionary().add("sigma", sigmaValue);
//					materials.get(1).getDictionary().add("sigma", sigmaValue);
//				} else if (dict1.found("sigma")) {
//					String sigmaValue = dict1.lookup("sigma");
//					materials.get(1).getDictionary().add("sigma", sigmaValue);
//				} else if (dict2.found("sigma")) {
//					String sigmaValue = dict2.lookup("sigma");
//					materials.get(0).getDictionary().add("sigma", sigmaValue);
//				}
//			} else {
//				logger.warn("Multiphase case but wrong phases number found in transportProperties: " + phases.length);
//			}
//
//		} else {
//			logger.warn("Multiphase case but no phases found in transportProperties");
//		}
//	}

}
