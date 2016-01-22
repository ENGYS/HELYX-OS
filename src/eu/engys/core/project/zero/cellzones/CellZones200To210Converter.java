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

package eu.engys.core.project.zero.cellzones;

import java.util.ArrayList;
import java.util.List;

import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.ListField;
import eu.engys.core.project.Project200To210Converter.MRFZones;
import eu.engys.core.project.Project200To210Converter.PorousZones;

public class CellZones200To210Converter {

	public static List<CellZone> loadMRFDictionary(MRFZones MRFZones) {
		List<CellZone> zones = new ArrayList<>();
		if (MRFZones != null) {
			Dictionary zonesDictionary = null;
			if (MRFZones.isList("")) {
				ListField list = MRFZones.getListFields().get(0);
				zonesDictionary = new Dictionary("");
				for (DefaultElement el : list.getListElements()) {
					if (el instanceof Dictionary) {
						zonesDictionary.add((Dictionary) el);
					}
				}
			} else {
				zonesDictionary = MRFZones;
			}
		
			if (zonesDictionary != null) {
				for (Dictionary d : zonesDictionary.getDictionaries()) {
					String zoneName = d.getName();
					Dictionary encodedDictionary = new Dictionary(d);
					encodedDictionary.add(Dictionary.TYPE, "mrf");
					
					CellZone zone = new CellZone(zoneName);
					zone.setName(zoneName);
                    zone.getTypes().add(CellZoneType.MRF_KEY);
                    zone.setDictionary(CellZoneType.MRF_KEY, encodedDictionary);
					
					zones.add(zone);
				}
			}
		}
		return zones;
	}

//	public static void saveMRFDictionary(ArrayList<CellZone> cellZones, MRFZones MRFZones) {
//		boolean asList = true;
//		if (MRFZones != null) {
//			MRFZones.clear();
//			for (CellZone cellZone : cellZones) {
//				String zoneName = cellZone.getName();
//				
//				if (cellZone.getType() == CellZoneType.MRF) {
//					Dictionary encodedDictionary = cellZone.getDictionary();
//
//					Dictionary toBeDecoded = new Dictionary(zoneName);
//					toBeDecoded.merge(encodedDictionary);
//					toBeDecoded.remove(Dictionary.TYPE);
//
//					if (asList) {
//						MRFZones.addToList(toBeDecoded);
//					} else {
//						MRFZones.add(toBeDecoded);
//					}
//				} else {
//					//System.err.println(zoneName + " NOT A MRF Zone");
//				}
//			}
//		}
//	}

	public static List<CellZone> loadPorousDictionary(PorousZones porousZones) {
		List<CellZone> zones = new ArrayList<>();
		if (porousZones != null) {
			Dictionary zonesDictionary = null;
			if (porousZones.isList("")) {
				ListField list = porousZones.getListFields().get(0);
				zonesDictionary = new Dictionary("");
				for (DefaultElement el : list.getListElements()) {
					if (el instanceof Dictionary) {
						zonesDictionary.add((Dictionary) el);
					}
				}
			} else {
				zonesDictionary = porousZones;
			}

			if (zonesDictionary != null) {

			}
			for (Dictionary toBeEncoded : zonesDictionary.getDictionaries()) {
				String zoneName = toBeEncoded.getName();

				Dictionary encodedDictionary = new Dictionary("porous");

				if (toBeEncoded.found("Darcy")) {
					Dictionary darcyDict = toBeEncoded.subDict("Darcy");
					//System.out.println("CellZoneBuilder.encodePorousDictionary() darcyDict: "+darcyDict);
					encodedDictionary.add(darcyDict.lookupScalar("d"));
					encodedDictionary.add(darcyDict.lookupScalar("f"));
					encodedDictionary.add("e1", toBeEncoded.lookup("e1"));
					encodedDictionary.add("e2", toBeEncoded.lookup("e2"));
					encodedDictionary.add("porosity", toBeEncoded.lookup("porosity"));
					encodedDictionary.add(Dictionary.TYPE, "porousDarcy");
				} else if (toBeEncoded.found("powerLaw")) {
					Dictionary powerLawDict = toBeEncoded.subDict("powerLaw");
					encodedDictionary.add("C0", powerLawDict.lookup("C0"));
					encodedDictionary.add("C1", powerLawDict.lookup("C1"));
					encodedDictionary.add("porosity", toBeEncoded.lookup("porosity"));
					encodedDictionary.add(Dictionary.TYPE, "porousPowerLaw");
				} else {
					//bad
				}

				CellZone zone = new CellZone(zoneName);
				zone.setName(zoneName);
                zone.getTypes().add(CellZoneType.POROUS_KEY);
                zone.setDictionary(CellZoneType.POROUS_KEY, encodedDictionary);

				if (toBeEncoded.found("thermalModel")) {
					Dictionary thermalDict = new Dictionary("thermalModel");

					if (toBeEncoded.subDict("thermalModel").found("powerLaw")) {
						Dictionary powerLaw = toBeEncoded.subDict("powerLaw");
						thermalDict.merge(powerLaw);
						thermalDict.add(Dictionary.TYPE, "powerLaw");
					} else if (toBeEncoded.subDict("thermalModel").found(Dictionary.TYPE)) {
						thermalDict.merge(toBeEncoded.subDict("thermalModel"));
					}

                    zone.getTypes().add(CellZoneType.THERMAL_KEY);
                    zone.setDictionary(CellZoneType.THERMAL_KEY, thermalDict);
				}

				
				zones.add(zone);
			}
		}
		
		return zones;
	}

//	public static void savePorousDictionary(ArrayList<CellZone> cellZones, PorousZones porousZones) {
//		boolean asList = true;
//		if (porousZones != null) {
//			porousZones.clear();
//			for (CellZone cellZone : cellZones) {
//				String zoneName = cellZone.getName();
//				if (cellZone.getType() == CellZoneType.POROUS || cellZone.getType() == CellZoneType.THERMAL_POROUS || cellZone.getType() == CellZoneType.THERMAL) {
//					Dictionary encodedDictionary = cellZone.getDictionary();
//					String typeString = encodedDictionary.lookup(Dictionary.TYPE);
//
//					Dictionary toBeDecoded = new Dictionary(zoneName);
//					if (typeString.equals("porousDarcy")) {
//						Dictionary darcyDict = new Dictionary("Darcy");
//						darcyDict.add(encodedDictionary.lookupScalar("d"));
//						darcyDict.add(encodedDictionary.lookupScalar("f"));
//
//						toBeDecoded.add(darcyDict);
//						toBeDecoded.add("e1", encodedDictionary.lookup("e1"));
//						toBeDecoded.add("e2", encodedDictionary.lookup("e2"));
//						toBeDecoded.add("porosity", encodedDictionary.lookup("porosity"));
//
//					} else if (typeString.equals("porousPowerLaw")) {
//						Dictionary powerLawDict = new Dictionary("powerLaw");
//						powerLawDict.add("C0", encodedDictionary.lookup("C0"));
//						powerLawDict.add("C1", encodedDictionary.lookup("C1"));
//						toBeDecoded.add(powerLawDict);
//						toBeDecoded.add("porosity", encodedDictionary.lookup("porosity"));
//					}
//					toBeDecoded.remove(Dictionary.TYPE);
//
//					/* in case of thermal porous zone */
//					if (encodedDictionary.found("thermalModel")) {
//						Dictionary thermalDict = new Dictionary("thermalModel");
//
//						Dictionary thermalModel = encodedDictionary.subDict("thermalModel");
//						String thermalType = thermalModel.lookup(Dictionary.TYPE);
//						if (thermalType.equals("powerLaw")) {
//							thermalDict.merge(thermalModel);
//						} else if (thermalType.equals("fixedTemperature")) {
//							thermalDict.merge(thermalModel);
//						}
//						toBeDecoded.add(thermalDict);
//					}
//					
//					if (asList) {
//						porousZones.addToList(toBeDecoded);
//					} else {
//						porousZones.add(toBeDecoded);
//					}
//				} else {
//					//System.err.println(zoneName + " NOT A Thermal Zone");
//				}
//			}
//		}
//	}

}
