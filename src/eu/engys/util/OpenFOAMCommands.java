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
package eu.engys.util;

import static eu.engys.util.IOUtils.WIN_EOL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OpenFOAMCommands {

    public static final String FRONTAL_AREA = "frontalArea";
    public static final String MOVE_TO_CASE_FOLDER_WIN = "cd /D \"%CASE%\"";
    public static final String PARA_FOAM = "paraFoam";
    private static final String _ALL_REGIONS = "-allRegions";
    private static final String _NO_FUNCTION_OBJECTS = "-noFunctionObjects";
    private static final String _WRITE_P = "-writep";
    private static final String _FORCE = "-force";
    private static final String _CONSTANT = "-constant";
    private static final String _ZERO_TIME = "-zeroTime";
    private static final String _WITH_ZERO = "-withZero";
    private static final String _OVERWRITE = "-overwrite";
    private static final String _SCALE = "-scale";
    private static final String _PARALLEL = "-parallel";
    private static final String _TIME = "-time";
    // Flag to solve a problem with mpirun on OpenSUSE 12.3 where a default file is not installed correctly
    private static final String _DEFAULT_HOST_FILE = PrefUtil.getBoolean(PrefUtil.DEFAULT_HOSTFILE_NONE) ? "--default-hostfile none" : "";
    private static final String GENVLIST = "-genvlist HOME,PATH,USERNAME,WM_PROJECT_DIR,WM_PROJECT_INST_DIR,WM_OPTIONS,FOAM_LIBBIN,FOAM_APPBIN,FOAM_USER_APPBIN,FOAM_CONFIG,MPI_BUFFER_SIZE";

    private static final String CASE() {
        return Util.isWindowsScriptStyle() ? "\"%CASE%\"" : "$CASE";
    }

    private static final String SOLVER() {
        return Util.isWindowsScriptStyle() ? "\"%SOLVER%\"" : "$SOLVER";
    }

    private static final String _CASE() {
        return "-case " + CASE();
    }

    private static final String _TEE_LOG() {
        return "2>&1 | " + (Util.isWindowsScriptStyle() ? "wtee -a \"%LOG%\"" : "tee -a $LOG");
    }

    private static final String _MPI_NP() {
        return Util.isWindowsScriptStyle() ? "mpiexec -n %NP% %MACHINEFILE% %MPI_ACCESSORY_OPTIONS% " + GENVLIST : "mpirun " + _DEFAULT_HOST_FILE + " -np $NP $MACHINEFILE";
    }

    private static final String _BLOCK_MESH_DICT() {
        return "-dict " + (Util.isWindowsScriptStyle() ? "system\\blockMeshDict" : "system/blockMeshDict");
    }

    private static final String COMMAND(String command, String log) {
        if (Util.isWindowsScriptStyle()) {
            String errorFile = "errorcode.txt";
            StringBuilder sb = new StringBuilder();
            sb.append("set COMMAND=" + command + WIN_EOL);
            sb.append(WIN_EOL);
            sb.append("set ERROR_HANDLER=call echo %%^^errorlevel%% ^>" + errorFile + WIN_EOL);
            sb.append(WIN_EOL);
            sb.append("(%COMMAND% & %%ERROR_HANDLER%%) " + log + WIN_EOL);
            sb.append(WIN_EOL);
            sb.append("set /p ERR=<" + errorFile + WIN_EOL);
            sb.append("del " + errorFile + WIN_EOL);
            sb.append(WIN_EOL);
            sb.append("IF %ERR% NEQ 0 exit %ERR%" + WIN_EOL);
            return sb.toString();
        } else {
            return command + " " + log;
        }
    }

    public static final String BLOCK_MESH_COMMAND = "blockMesh";
    public static final String CHECK_MESH_COMMAND = "checkMesh";
    public static final String CASE_SETUP_COMMAND = "caseSetup";
    public static final String DECOMPOSE_PAR_COMMAND = "decomposePar";
    public static final String EXTRUDE_TO_REGION_MESH_COMMAND = "extrudeToRegionMesh";
    public static final String FOAM_MESH_TO_FLUENT_COMMAND = "foamMeshToFluent";
    public static final String FOAM_TO_STAR_MESH_COMMAND = "foamToStarMesh";
    public static final String FLUENT_3D_MESH_TO_FOAM_COMMAND = "fluent3DMeshToFoam";
    public static final String MERGE_MESHES_COMMAND = "mergeMeshes";
    public static final String PAR_MAP_FIELDS_COMMAND = "parMapFields";
    public static final String POTENTIAL_FOAM_COMMAND = "potentialFoam";
    public static final String RECONSTRUCT_PAR_COMMAND = "reconstructPar";
    public static final String RECONSTRUCT_PAR_MESH_COMMAND = "reconstructParMesh";
    public static final String RENUMBER_MESH_COMMAND = "renumberMesh";
    public static final String STRETCH_MESH_COMMAND = "stretchMesh";
    public static final String SET_FIELDS_COMMAND = "setFields";
    public static final String SNAPPY_CHECK_MESH_COMMAND = "snappyCheckMesh";
    public static final String SNAPPY_HEX_MESH_COMMAND = "snappyHexMesh";

    /*
     * MESH Commands
     */
    public static final String BLOCK_MESH() {
        return COMMAND(BLOCK_MESH_COMMAND + " " + _BLOCK_MESH_DICT() + " " + _CASE(), _TEE_LOG());
    }

    public static final String MERGE_MESHES(String filePath) {
        return COMMAND(MERGE_MESHES_COMMAND + " " + _OVERWRITE + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + CASE() + " \"" + filePath + "\"", _TEE_LOG());
    }

    public static final String RECONSTRUCT_PAR_MESH() {
        return COMMAND(RECONSTRUCT_PAR_MESH_COMMAND + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String RECONSTRUCT_PAR_MESH_CONSTANT() {
        return COMMAND(RECONSTRUCT_PAR_MESH_COMMAND + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + _CONSTANT, _TEE_LOG());
    }

    public static final String RECONSTRUCT_PAR_MESH_ALLREGIONS() {
        return COMMAND(RECONSTRUCT_PAR_MESH_COMMAND + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + _ALL_REGIONS, _TEE_LOG());
    }

    public static final String RECONSTRUCT_PAR_MESH_CONSTANT_ALLREGIONS() {
        return COMMAND(RECONSTRUCT_PAR_MESH_COMMAND + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + _CONSTANT + " " + _ALL_REGIONS, _TEE_LOG());
    }

    public static final String CHECK_MESH_SERIAL() {
        return COMMAND(CHECK_MESH_COMMAND + " " + _CASE(), _TEE_LOG());
    }

    public static final String CHECK_MESH_SERIAL_CONSTANT() {
        return COMMAND(CHECK_MESH_COMMAND + " " + _CONSTANT + " " + _CASE(), _TEE_LOG());
    }

    public static final String CHECK_MESH_PARALLEL() {
        return COMMAND(_MPI_NP() + " " + CHECK_MESH_COMMAND + " " + _PARALLEL + " " + _CASE(), _TEE_LOG());
    }

    public static final String CHECK_MESH_PARALLEL_CONSTANT() {
        return COMMAND(_MPI_NP() + " " + CHECK_MESH_COMMAND + " " + _PARALLEL + " " + _CONSTANT + " " + _CASE(), _TEE_LOG());
    }

    public static final String STRETCH_MESH_SERIAL() {
        return COMMAND(STRETCH_MESH_COMMAND + " " + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String STRETCH_MESH_PARALLEL() {
        return COMMAND(_MPI_NP() + " " + STRETCH_MESH_COMMAND + " " + _NO_FUNCTION_OBJECTS + " " + _PARALLEL + " " + _CASE(), _TEE_LOG());
    }

    public static final String SNAPPY_CHECK_MESH_SERIAL() {
        return COMMAND(SNAPPY_CHECK_MESH_COMMAND + " -writeAllMetrics " + _CASE(), _TEE_LOG());
    }

    public static final String SNAPPY_CHECK_MESH_PARALLEL() {
        return COMMAND(_MPI_NP() + " " + SNAPPY_CHECK_MESH_COMMAND + " -writeAllMetrics " + _PARALLEL + " " + _CASE(), _TEE_LOG());
    }

    public static final String RUN_MESH_SERIAL() {
        return COMMAND(SNAPPY_HEX_MESH_COMMAND + " " + _OVERWRITE + " " + _CASE(), _TEE_LOG());
    }

    public static final String RUN_MESH_PARALLEL() {
        return COMMAND(_MPI_NP() + " " + SNAPPY_HEX_MESH_COMMAND + " " + _PARALLEL + " " + _OVERWRITE + " " + _CASE(), _TEE_LOG());
    }

    /*
     * Fields
     */

    public static final String EXTRUDE_REGION_TO_MESH() {
        return COMMAND(EXTRUDE_TO_REGION_MESH_COMMAND + " " + _OVERWRITE + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    /*
     * Solver
     */
    public static final String RUN_CASE_SERIAL() {
        return COMMAND(SOLVER() + " " + _CASE(), _TEE_LOG());
    }

    public static final String RUN_CASE_PARALLEL() {
        return COMMAND(_MPI_NP() + " " + SOLVER() + " " + _PARALLEL + " " + _CASE(), _TEE_LOG());
    }

    public static final String SET_FIELDS_SERIAL() {
        return COMMAND(SET_FIELDS_COMMAND + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String SET_FIELDS_PARALLEL() {
        return COMMAND(_MPI_NP() + " " + SET_FIELDS_COMMAND + " " + _PARALLEL + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String POTENTIAL_FOAM_SERIAL(boolean initialiseUBCs) {
        return COMMAND(POTENTIAL_FOAM_COMMAND + " " + (initialiseUBCs ? "-initialiseUBCs " : "") + _WRITE_P + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }
    
    public static final String POTENTIAL_FOAM_PARALLEL(boolean initialiseUBCs) {
        return COMMAND(_MPI_NP() + " " + POTENTIAL_FOAM_COMMAND + " " + (initialiseUBCs ? "-initialiseUBCs " : "") + _WRITE_P + " "+ _PARALLEL + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String INITIALISE_FIELDS_SERIAL() {
        return COMMAND(CASE_SETUP_COMMAND + " " + _CASE(), _TEE_LOG());
    }

    /*
     * Other
     */

    public static final String INITIALISE_FIELDS_PARALLEL() {
        return COMMAND(_MPI_NP() + " " + CASE_SETUP_COMMAND + " " + _PARALLEL + " " + _CASE(), _TEE_LOG());
    }

    public static final String PAR_MAP_FIELDS_SERIAL() {
        return COMMAND(PAR_MAP_FIELDS_COMMAND + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String PAR_MAP_FIELDS_PARALLEL() {
        return COMMAND(_MPI_NP() + " " + PAR_MAP_FIELDS_COMMAND + " " + _PARALLEL + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String DECOMPOSE_PAR(Set<String> timeSteps) {
        return COMMAND(DECOMPOSE_PAR_COMMAND + " " + _FORCE + " " + _NO_FUNCTION_OBJECTS + " " + createTimeFlag(timeSteps) + " " + _CASE(), _TEE_LOG());
    }

    public static final String DECOMPOSE_PAR_CONSTANT(Set<String> timeSteps) {
        return COMMAND(DECOMPOSE_PAR_COMMAND + " " + _FORCE + " " + _NO_FUNCTION_OBJECTS + " " + createTimeFlag(timeSteps) + " " + _CASE() + " " + _CONSTANT, _TEE_LOG());
    }

    public static final String DECOMPOSE_PAR_ALLREGIONS(Set<String> timeSteps) {
        return COMMAND(DECOMPOSE_PAR_COMMAND + " " + _FORCE + " " + _NO_FUNCTION_OBJECTS + " " + createTimeFlag(timeSteps) + " " + _CASE() + " " + _ALL_REGIONS, _TEE_LOG());
    }

    public static final String DECOMPOSE_PAR_CONSTANT_ALLREGIONS(Set<String> timeSteps) {
        return COMMAND(DECOMPOSE_PAR_COMMAND + " " + _FORCE + " " + _NO_FUNCTION_OBJECTS + " " + createTimeFlag(timeSteps) + " " + _CASE() + " " + _CONSTANT + " " + _ALL_REGIONS, _TEE_LOG());
    }

    public static final String RECONSTRUCT_PAR(boolean useWithZeroFlag) {
        return COMMAND(RECONSTRUCT_PAR_COMMAND + " " + (useWithZeroFlag ? _WITH_ZERO : _ZERO_TIME) + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String RECONSTRUCT_PAR_ALLREGIONS(boolean useWithZeroFlag) {
        return COMMAND(RECONSTRUCT_PAR_COMMAND + " " + (useWithZeroFlag ? _WITH_ZERO : _ZERO_TIME) + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + _ALL_REGIONS, _TEE_LOG());
    }

    public static final String FLUENT_TO_FOAM(Double scale, String fluentFileName) {
        String separator = Util.isWindowsScriptStyle() ? "\\" : "/";
        return COMMAND(FLUENT_3D_MESH_TO_FOAM_COMMAND + " " + _SCALE + " " + scale + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + CASE() + separator + fluentFileName, _TEE_LOG());
    }

    public static final String RENUMBER_MESH_SERIAL() {
        return COMMAND(RENUMBER_MESH_COMMAND + " " + _OVERWRITE + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String RENUMBER_MESH_PARALLEL() {
        return COMMAND(_MPI_NP() + " " + RENUMBER_MESH_COMMAND + " " + _PARALLEL + " " + _OVERWRITE + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String FOAM_MESH_TO_STAR() {
        return COMMAND(FOAM_TO_STAR_MESH_COMMAND + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String FOAM_MESH_TO_FLUENT() {
        return COMMAND(FOAM_MESH_TO_FLUENT_COMMAND + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String CAD_TOOL(boolean split, double precision, File input, File output) {
        String byComponentFlag = split ? " -byComponent" : "";
        String precisionFlag = "-relativeSpacing " + precision;
        String inputFlag = "-inputFile " + input.getName();
        String outputFlag = "-outputFile " + output.getName();

        return COMMAND("CADtoSurface" + byComponentFlag + " " + precisionFlag + " " + inputFlag + " " + outputFlag, _TEE_LOG());
    }

    public static final String PVBATCH_SERIAL() {
        return COMMAND("pvbatch --use-offscreen-rendering export.py", _TEE_LOG());
    }

    public static final String PVBATCH_PARALLEL() {
        return COMMAND(_MPI_NP() + " pvbatch --use-offscreen-rendering export.py", _TEE_LOG());
    }

    /*
     * Utils
     */
    private static String createTimeFlag(Set<String> timeSteps) {
        if (timeSteps.isEmpty()) {
            return "";
        } else {
            String times = _TIME + " ";
            List<String> list = new ArrayList<>(timeSteps);
            for (int i = 0; i < timeSteps.size() - 1; i++) {
                times += list.get(i) + ",";
            }
            times += list.get(timeSteps.size() - 1);
            return times;
        }
    }

}
