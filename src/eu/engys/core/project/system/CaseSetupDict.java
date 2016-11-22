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

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.FoamFile;

public class CaseSetupDict extends Dictionary {

    public static final String CASE_SETUP_DICT = "caseSetupDict";

    public static final String MATERIALS_KEY = "materials";
    public static final String MATERIAL_PROPERTIES_KEY = "materialProperties";
    public static final String BINARY_PAIR_DATA_KEY = "binaryPairData";

    public static final String REGION0_KEY = "region0";
    public static final String REGION_DEFAULTS_KEY = "regionDefaults";
    public static final String FIELDS_KEY = "fields";
    public static final String GLOBAL_KEY = "global";
    public static final String REGIONS_KEY = "regions";
    public static final String BOUNDARY_MESH_KEY = "boundaryMesh";
    public static final String EXACT_NAMED_KEY = "exactNamed";
    public static final String PARTIAL_NAMED_KEY = "partialNamed";
    public static final String BOUNDARY_CONDITIONS_KEY = "boundaryConditions";

    public static final String MODIFICATION_SWITCHES_KEY = "modificationSwitches";
    public static final String RESET_INTERNAL_FIELDS_KEY = "resetInternalFields";
    public static final String RESET_BOUNDARY_FIELDS_KEY = "resetBoundaryFields";
    public static final String RESET_SYSTEM_DICTS_KEY = "resetSystemDicts";
    public static final String RESET_CONST_DICTS_KEY = "resetConstDicts";
    public static final String RESET_BOUNDARY_MESH_KEY = "resetBoundaryMesh";
    public static final String DELETE_UNUSED_FIELDS_KEY = "deleteUnusedFields";
    public static final String REUSE_EXISTING_DICTS_KEY = "reuseExistingDicts";
    public static final String STRICT_PATCH_NAME_CHECKING_KEY = "strictPatchNameChecking";
    
    public static final String STATE_KEY = "state";
    public static final String TURBULENCE_MODEL_KEY = "turbulenceModel";
    public static final String INITIALISATION_KEY = "initialisation";

    public  static final String FUNCTIONS_KEY = "functions";

    public CaseSetupDict() {
        super(CASE_SETUP_DICT);
        setFoamFile(FoamFile.getDictionaryFoamFile(SystemFolder.SYSTEM, CASE_SETUP_DICT));
    }

    public void check() throws DictionaryException {
    }
}
