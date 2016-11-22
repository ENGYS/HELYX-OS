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
package eu.engys.core.project.constant;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FoamFile;

public class TurbulenceProperties extends Dictionary {

    public static final String TURBULENCE_PROPERTIES = "turbulenceProperties";

    public static final String SIMULATION_TYPE_KEY = "simulationType";
    public static final String RAS_KEY = "RAS";
    public static final String LES_KEY = "LES";
    public static final String RAS_MODEL_KEY = "RASModel";
    public static final String LES_MODEL_KEY = "LESModel";
    public static final String LAMINAR_KEY = "laminar";
    
    public static final String PRINT_COEFFS_KEY = "printCoeffs";
    public static final String TURBULENCE_KEY = "turbulence";
    public static final String DELTA_KEY = "delta";
    public static final String DELTA1_KEY = "delta1";
    
    public static final String FIELD_MAPS_KEY = "fieldMaps";

    public TurbulenceProperties() {
        super(TURBULENCE_PROPERTIES);
        setFoamFile(FoamFile.getDictionaryFoamFile(ConstantFolder.CONSTANT, TURBULENCE_PROPERTIES));
    }

    public TurbulenceProperties(Dictionary turbulenceProperties) {
        this();
        merge(turbulenceProperties);
        setFoamFile(FoamFile.getDictionaryFoamFile(ConstantFolder.CONSTANT, TURBULENCE_PROPERTIES));
    }
}