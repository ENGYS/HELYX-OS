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

package eu.engys.gui.casesetup.cellzones;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.project.system.SnappyHexMeshDict.CELL_ZONE_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.ABSOLUTE_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.ACTIVE_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.AXES_ROTATION_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.AXIS_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.C0_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.C1_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.CARTESIAN_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.COEFFS_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.COORDINATE_ROTATION_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.COORDINATE_SYSTEM_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.DARCY_FORCHHEIMER_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.D_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.E1_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.E2_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.EXPLICIT_POROSITY_SOURCE_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.F_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.INJECTION_RATE_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.MRF_SOURCE_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.OMEGA_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.ORIGIN_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.POROUS_DARCY_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.POROUS_POWER_LAW_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.POWER_LAW_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.SCALAR_EXPLICIT_SET_VALUE_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.SELECTION_MODE_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.THERMAL_FIXED_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.T_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.VOLUME_MODE_KEY;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.State;
import eu.engys.core.project.system.FvOptions;
import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.core.project.zero.cellzones.CellZoneType;
import eu.engys.core.project.zero.cellzones.CellZones;
import eu.engys.core.project.zero.cellzones.CellZonesBuilder;

public class StandardCellZonesBuilder implements CellZonesBuilder {

    private static final Logger logger = LoggerFactory.getLogger(StandardCellZonesBuilder.class);

    @Override
    public void loadMRFDictionary(Model model) {
        loadMRFDictionary(model.getCellZones(), model.getProject().getSystemFolder().getFvOptions());
    }

    @Override
    public void loadMRFDictionary(CellZones cellZones, FvOptions fvOptions) {
        if (fvOptions != null) {
            for (CellZone cellZone : cellZones) {
                String zoneName = cellZone.getName();
                List<Dictionary> zonesDict = fvOptions.getDictionaries();
                for (Dictionary zoneDict : zonesDict) {
                    if (zoneDict.found(CELL_ZONE_KEY) && zoneDict.lookup(CELL_ZONE_KEY).equals(zoneName)) {
                        if (zoneDict.found(TYPE) && zoneDict.lookup(TYPE).equals(MRF_SOURCE_KEY)) {
                            Dictionary encodedDictionary = new Dictionary("");
                            encodedDictionary.add(TYPE, CellZoneType.MRF_KEY);
                            if (zoneDict.isDictionary(MRF_SOURCE_KEY + COEFFS_KEY)) {
                                Dictionary coeffs = zoneDict.subDict(MRF_SOURCE_KEY + COEFFS_KEY);
                                encodedDictionary.add(ORIGIN_KEY, coeffs.lookup(ORIGIN_KEY));
                                encodedDictionary.add(AXIS_KEY, coeffs.lookup(AXIS_KEY));
                                encodedDictionary.add(OMEGA_KEY, coeffs.lookup(OMEGA_KEY));
                            }

                            cellZone.setDictionary(CellZoneType.MRF_KEY, encodedDictionary);
                            cellZone.getTypes().add(CellZoneType.MRF_KEY);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void saveMRFDictionary(Model model) {
        saveMRFDictionary(model.getCellZones(), model.getProject().getSystemFolder().getFvOptions());
    }

    @Override
    public void saveMRFDictionary(CellZones cellZones, FvOptions fvOptions) {
        if (fvOptions != null) {
            for (CellZone cellZone : cellZones) {
                String zoneName = cellZone.getName();

                if (cellZone.hasType(CellZoneType.MRF_KEY)) {
                    Dictionary encodedDictionary = cellZone.getDictionary(CellZoneType.MRF_KEY);

                    Dictionary toBeDecoded = new Dictionary(zoneName + "_" + CellZoneType.MRF_KEY);
                    toBeDecoded.add(TYPE, MRF_SOURCE_KEY);
                    toBeDecoded.add(ACTIVE_KEY, "true");
                    toBeDecoded.add(SELECTION_MODE_KEY, CELL_ZONE_KEY);
                    toBeDecoded.add(CELL_ZONE_KEY, zoneName);

                    Dictionary MRFSourceCoeffs = new Dictionary(MRF_SOURCE_KEY + COEFFS_KEY);
                    MRFSourceCoeffs.add(ORIGIN_KEY, encodedDictionary.lookup(ORIGIN_KEY));
                    MRFSourceCoeffs.add(AXIS_KEY, encodedDictionary.lookup(AXIS_KEY));
                    MRFSourceCoeffs.add(OMEGA_KEY, encodedDictionary.lookup(OMEGA_KEY));

                    toBeDecoded.add(MRFSourceCoeffs);

                    fvOptions.add(toBeDecoded);
                } else {
                    logger.debug(zoneName + " NOT A MRF Zone " + cellZone.getTypes());
                }
            }
        }
    }

    @Override
    public void loadPorousDictionary(Model model) {
        loadPorousDictionary(model.getCellZones(), model.getProject().getSystemFolder().getFvOptions());
    }

    @Override
    public void loadPorousDictionary(CellZones cellZones, FvOptions fvOptions) {
        if (fvOptions != null) {
            for (CellZone cellZone : cellZones) {
                String zoneName = cellZone.getName();
                List<Dictionary> zonesDict = fvOptions.getDictionaries();
                for (Dictionary zoneDict : zonesDict) {
                    if (zoneDict.found(CELL_ZONE_KEY) && zoneDict.lookup(CELL_ZONE_KEY).equals(zoneName)) {
                        if (zoneDict.lookup(TYPE).equals(EXPLICIT_POROSITY_SOURCE_KEY)) {
                            Dictionary encodedDictionary = new Dictionary("");
                            if (zoneDict.isDictionary(EXPLICIT_POROSITY_SOURCE_KEY + COEFFS_KEY)) {
                                Dictionary coeffDict = zoneDict.subDict(EXPLICIT_POROSITY_SOURCE_KEY + COEFFS_KEY);
                                String type = coeffDict.lookup(TYPE);

                                if (type.equals(DARCY_FORCHHEIMER_KEY)) {
                                    encodedDictionary.add(TYPE, POROUS_DARCY_KEY);

                                    Dictionary darcyDict = coeffDict.subDict(DARCY_FORCHHEIMER_KEY + COEFFS_KEY);
                                    encodedDictionary.add(darcyDict.lookupScalar(D_KEY));
                                    encodedDictionary.add(darcyDict.lookupScalar(F_KEY));
                                    if (darcyDict.isDictionary(COORDINATE_SYSTEM_KEY)) {
                                        Dictionary coordSys = darcyDict.subDict(COORDINATE_SYSTEM_KEY);
                                        if (coordSys.isDictionary(COORDINATE_ROTATION_KEY)) {
                                            Dictionary rotation = coordSys.subDict(COORDINATE_ROTATION_KEY);
                                            encodedDictionary.add(E1_KEY, rotation.lookup(E1_KEY));
                                            encodedDictionary.add(E2_KEY, rotation.lookup(E2_KEY));
                                        }
                                    }
                                } else if (type.equals(POWER_LAW_KEY)) {
                                    encodedDictionary.add(TYPE, POROUS_POWER_LAW_KEY);
                                    Dictionary powerLawDict = coeffDict.subDict(POWER_LAW_KEY + COEFFS_KEY);
                                    encodedDictionary.add(C0_KEY, powerLawDict.lookup(C0_KEY));
                                    encodedDictionary.add(C1_KEY, powerLawDict.lookup(C1_KEY));
                                } else {
                                    System.err.println("Unknown type");
                                }
                            }

                            cellZone.getTypes().add(CellZoneType.POROUS_KEY);
                            cellZone.setDictionary(CellZoneType.POROUS_KEY, encodedDictionary);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void savePorousDictionary(Model model) {
        savePorousDictionary(model.getCellZones(), model.getProject().getSystemFolder().getFvOptions());
    }

    @Override
    public void savePorousDictionary(CellZones cellZones, FvOptions fvOptions) {
        if (fvOptions != null) {
            for (CellZone cellZone : cellZones) {
                String zoneName = cellZone.getName();
                if (cellZone.hasType(CellZoneType.POROUS_KEY)) {
                    Dictionary encodedDictionary = cellZone.getDictionary(CellZoneType.POROUS_KEY);
                    String typeString = encodedDictionary.lookup(TYPE);

                    Dictionary toBeDecoded = new Dictionary(zoneName + "_" + CellZoneType.POROUS_KEY);
                    toBeDecoded.add(TYPE, EXPLICIT_POROSITY_SOURCE_KEY);
                    toBeDecoded.add(ACTIVE_KEY, "true");
                    toBeDecoded.add(SELECTION_MODE_KEY, CELL_ZONE_KEY);
                    toBeDecoded.add(CELL_ZONE_KEY, zoneName);

                    Dictionary porousCoeffs = new Dictionary(EXPLICIT_POROSITY_SOURCE_KEY + COEFFS_KEY);
                    toBeDecoded.add(porousCoeffs);

                    if (typeString != null && typeString.equals(POROUS_DARCY_KEY)) {
                        porousCoeffs.add(TYPE, DARCY_FORCHHEIMER_KEY);

                        Dictionary darcyDict = new Dictionary(DARCY_FORCHHEIMER_KEY + COEFFS_KEY);
                        if (encodedDictionary.found(D_KEY))
                            darcyDict.add(encodedDictionary.lookupScalar(D_KEY));
                        if (encodedDictionary.found(F_KEY))
                            darcyDict.add(encodedDictionary.lookupScalar(F_KEY));

                        Dictionary coordSys = new Dictionary(COORDINATE_SYSTEM_KEY);
                        coordSys.add(TYPE, CARTESIAN_KEY);
                        coordSys.add(ORIGIN_KEY, "(0 0 0)");

                        Dictionary rotation = new Dictionary(COORDINATE_ROTATION_KEY);
                        rotation.add(TYPE, AXES_ROTATION_KEY);
                        rotation.add(E1_KEY, encodedDictionary.lookup(E1_KEY));
                        rotation.add(E2_KEY, encodedDictionary.lookup(E2_KEY));
                        coordSys.add(rotation);

                        darcyDict.add(coordSys);

                        porousCoeffs.add(darcyDict);
                    } else if (typeString != null && typeString.equals(POROUS_POWER_LAW_KEY)) {
                        porousCoeffs.add(TYPE, POWER_LAW_KEY);

                        Dictionary powerLawDict = new Dictionary(POWER_LAW_KEY + COEFFS_KEY);
                        powerLawDict.add(C0_KEY, encodedDictionary.lookup(C0_KEY));
                        powerLawDict.add(C1_KEY, encodedDictionary.lookup(C1_KEY));
                        porousCoeffs.add(powerLawDict);
                    }

                    fvOptions.add(toBeDecoded);
                } else {
                    logger.debug(zoneName + " NOT A Porous Zone " + cellZone.getTypes());
                }
            }
        }
    }

    @Override
    public void loadThermalDictionary(Model model) {
        loadThermalDictionary(model.getCellZones(), model.getProject().getSystemFolder().getFvOptions(), model.getState());
    }

    @Override
    public void loadThermalDictionary(CellZones cellZones, FvOptions fvOptions, State state) {
        if (fvOptions != null) {
            for (CellZone cellZone : cellZones) {
                String zoneName = cellZone.getName();
                List<Dictionary> zonesDict = fvOptions.getDictionaries();
                for (Dictionary zoneDict : zonesDict) {
                    if (zoneDict.found(CELL_ZONE_KEY) && zoneDict.lookup(CELL_ZONE_KEY).equals(zoneName)) {
                        String type = zoneDict.lookup(TYPE);
                        if (type != null && (type.equals(SCALAR_EXPLICIT_SET_VALUE_KEY))) {
                            Dictionary encodedDictionary = new Dictionary("");
                            if (type.equals(SCALAR_EXPLICIT_SET_VALUE_KEY)) {
                                if (zoneDict.isDictionary(SCALAR_EXPLICIT_SET_VALUE_KEY + COEFFS_KEY)) {
                                    Dictionary coeffDict = zoneDict.subDict(SCALAR_EXPLICIT_SET_VALUE_KEY + COEFFS_KEY);
                                    Dictionary injectionDict = coeffDict.subDict(INJECTION_RATE_KEY);
                                    
                                    encodedDictionary.add(TYPE, THERMAL_FIXED_KEY);
                                    encodedDictionary.add(T_KEY, injectionDict.lookup(T_KEY));
                                }
                            }
                            cellZone.getTypes().add(CellZoneType.THERMAL_KEY);
                            cellZone.setDictionary(CellZoneType.THERMAL_KEY, encodedDictionary);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void saveThermalDictionary(Model model) {
        saveThermalDictionary(model.getCellZones(), model.getProject().getSystemFolder().getFvOptions(), model.getState());
    }

    @Override
    public void saveThermalDictionary(CellZones cellZones, FvOptions fvOptions, State state) {
        if (fvOptions != null) {
            for (CellZone cellZone : cellZones) {
                String zoneName = cellZone.getName();
                if (cellZone.hasType(CellZoneType.THERMAL_KEY)) {
                    Dictionary encodedDictionary = cellZone.getDictionary(CellZoneType.THERMAL_KEY);

                    Dictionary toBeDecoded = new Dictionary(zoneName + "_" + CellZoneType.THERMAL_KEY);
                    toBeDecoded.add(TYPE, SCALAR_EXPLICIT_SET_VALUE_KEY);
                    toBeDecoded.add(ACTIVE_KEY, "true");
                    toBeDecoded.add(SELECTION_MODE_KEY, CELL_ZONE_KEY);
                    toBeDecoded.add(CELL_ZONE_KEY, zoneName);

                    Dictionary coeffsDict = new Dictionary(SCALAR_EXPLICIT_SET_VALUE_KEY + COEFFS_KEY);
                    coeffsDict.add(VOLUME_MODE_KEY, ABSOLUTE_KEY);

                    Dictionary injectionDict = new Dictionary(INJECTION_RATE_KEY);
                    injectionDict.add(T_KEY, encodedDictionary.lookup(T_KEY));
                    coeffsDict.add(injectionDict);

                    toBeDecoded.add(coeffsDict);

                    fvOptions.add(toBeDecoded);
                } else {
                    logger.debug(zoneName + " NOT A Thermal Zone " + cellZone.getTypes());
                }
            }
        }
    }

}
