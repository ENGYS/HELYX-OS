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

package eu.engys.util;

import static eu.engys.util.IOUtils.WIN_EOL;

import java.io.File;

public class OpenFOAMCommands {

    private static final String _ALL_REGIONS = "-allRegions";
    private static final String _NO_FUNCTION_OBJECTS = "-noFunctionObjects";
    private static final String _FORCE = "-force";
    private static final String _CONSTANT = "-constant";
    private static final String _ZERO_TIME = "-zeroTime";
    private static final String _WITH_ZERO = "-withZero";
    private static final String _OVERWRITE = "-overwrite";
    private static final String _SCALE = "-scale";
    private static final String _PARALLEL = "-parallel";

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

    /*
     * MESH Commands
     */
    public static final String BLOCK_MESH() {
        return COMMAND("blockMesh " + _BLOCK_MESH_DICT() + " " + _CASE(), _TEE_LOG());
    }

    public static final String MERGE_MESHES(String filePath) {
        return COMMAND("mergeMeshes " + _OVERWRITE + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + CASE() + " \"" + filePath + "\"", _TEE_LOG());
    }

    public static final String RECONSTRUCT_PAR_MESH() {
        return COMMAND("reconstructParMesh " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String RECONSTRUCT_PAR_MESH_CONSTANT() {
        return COMMAND("reconstructParMesh " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + _CONSTANT, _TEE_LOG());
    }

    public static final String RECONSTRUCT_PAR_MESH_ALLREGIONS() {
        return COMMAND("reconstructParMesh " + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + _ALL_REGIONS, _TEE_LOG());
    }

    public static final String RECONSTRUCT_PAR_MESH_CONSTANT_ALLREGIONS() {
        return COMMAND("reconstructParMesh " + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + _CONSTANT + " " + _ALL_REGIONS, _TEE_LOG());
    }

    public static final String CHECK_MESH_SERIAL() {
        return COMMAND("checkMesh " + _CASE(), _TEE_LOG());
    }

    public static final String CHECK_MESH_PARALLEL() {
        return COMMAND(_MPI_NP() + " checkMesh " + _PARALLEL + " " + _CASE(), _TEE_LOG());
    }

    public static final String SNAPPY_CHECK_MESH_SERIAL() {
        return COMMAND("snappyCheckMesh -writeAllMetrics " + _CASE(), _TEE_LOG());
    }

    public static final String SNAPPY_CHECK_MESH_PARALLEL() {
        return COMMAND(_MPI_NP() + " snappyCheckMesh -writeAllMetrics " + _PARALLEL + " " + _CASE(), _TEE_LOG());
    }

    public static final String RUN_MESH_SERIAL() {
        return COMMAND("snappyHexMesh " + _OVERWRITE + " " + _CASE(), _TEE_LOG());
    }

    public static final String RUN_MESH_PARALLEL() {
        return COMMAND(_MPI_NP() + " snappyHexMesh " + _PARALLEL + " " + _OVERWRITE + " " + _CASE(), _TEE_LOG());
    }

    public static final String EXTRUDE_REGION_TO_MESH() {
        return COMMAND("extrudeToRegionMesh " + _OVERWRITE + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
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

    /*
     * Fields
     */

    public static final String SET_FIELDS_SERIAL() {
        return COMMAND("setFields " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String SET_FIELDS_PARALLEL() {
        return COMMAND(_MPI_NP() + " setFields " + _PARALLEL + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String INITIALISE_FIELDS_SERIAL() {
        return COMMAND("caseSetup " + _CASE(), _TEE_LOG());
    }

    public static final String INITIALISE_FIELDS_PARALLEL() {
        return COMMAND(_MPI_NP() + " caseSetup " + _PARALLEL + " " + _CASE(), _TEE_LOG());
    }

    public static final String PAR_MAP_FIELDS_SERIAL() {
        return COMMAND("parMapFields " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String PAR_MAP_FIELDS_PARALLEL() {
        return COMMAND(_MPI_NP() + " parMapFields " + _PARALLEL + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    /*
     * Other
     */

    public static final String DECOMPOSE_PAR() {
        return COMMAND("decomposePar " + _FORCE + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String DECOMPOSE_PAR_CONSTANT() {
        return COMMAND("decomposePar " + _FORCE + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + _CONSTANT, _TEE_LOG());
    }

    public static final String DECOMPOSE_PAR_ALLREGIONS() {
        return COMMAND("decomposePar " + _FORCE + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + _ALL_REGIONS, _TEE_LOG());
    }

    public static final String DECOMPOSE_PAR_CONSTANT_ALLREGIONS() {
        return COMMAND("decomposePar " + _FORCE + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + _CONSTANT + " " + _ALL_REGIONS, _TEE_LOG());
    }

    public static final String RECONSTRUCT_PAR(boolean useWithZeroFlag) {
        return COMMAND("reconstructPar " + (useWithZeroFlag ? _WITH_ZERO : _ZERO_TIME) + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String RECONSTRUCT_PAR_ALLREGIONS(boolean useWithZeroFlag) {
        return COMMAND("reconstructPar " + (useWithZeroFlag ? _WITH_ZERO : _ZERO_TIME) + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + _ALL_REGIONS, _TEE_LOG());
    }

    public static final String FLUENT_TO_FOAM(Double scale, String fluentFileName) {
        String separator = Util.isWindowsScriptStyle() ? "\\" : "/";
        return COMMAND("fluent3DMeshToFoam " + _SCALE + " " + scale + " " + _NO_FUNCTION_OBJECTS + " " + _CASE() + " " + CASE() + separator + fluentFileName, _TEE_LOG());
    }

    public static final String RENUMBER_SERIAL() {
        return COMMAND("renumberMesh " + _OVERWRITE + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String RENUMBER_PARALLEL() {
        return COMMAND(_MPI_NP() + " renumberMesh " + _PARALLEL + " " + _OVERWRITE + " " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String FOAM_MESH_TO_STAR() {
        return COMMAND("foamToStarMesh " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String FOAM_MESH_TO_FLUENT() {
        return COMMAND("foamMeshToFluent " + _NO_FUNCTION_OBJECTS + " " + _CASE(), _TEE_LOG());
    }

    public static final String CAD_TOOL(boolean split, double precision, File input, File output) {
        String byComponentFlag = split ? " -byComponent" : "";
        String precisionFlag = "-relativeSpacing " + precision;
        String inputFlag = "-inputFile " + input.getName();
        String outputFlag = "-outputFile " + output.getName();

        return COMMAND("CADtoSurface" + byComponentFlag + " " + precisionFlag + " " + inputFlag + " " + outputFlag, _TEE_LOG());
    }

    public static final String FRONTAL_AREA = "frontalArea";
    public static final String MOVE_TO_CASE_FOLDER_WIN = "cd /D \"%CASE%\"";
    public static final String PARA_FOAM = "paraFoam";

}
