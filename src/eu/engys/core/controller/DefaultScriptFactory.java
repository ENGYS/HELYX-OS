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
package eu.engys.core.controller;

import static eu.engys.core.OpenFOAMEnvironment.loadEnvironment;
import static eu.engys.core.OpenFOAMEnvironment.printHeader;
import static eu.engys.core.OpenFOAMEnvironment.printVariables;
import static eu.engys.util.OpenFOAMCommands.BLOCK_MESH;
import static eu.engys.util.OpenFOAMCommands.CHECK_MESH_PARALLEL;
import static eu.engys.util.OpenFOAMCommands.CHECK_MESH_PARALLEL_CONSTANT;
import static eu.engys.util.OpenFOAMCommands.CHECK_MESH_SERIAL;
import static eu.engys.util.OpenFOAMCommands.CHECK_MESH_SERIAL_CONSTANT;
import static eu.engys.util.OpenFOAMCommands.DECOMPOSE_PAR;
import static eu.engys.util.OpenFOAMCommands.DECOMPOSE_PAR_ALLREGIONS;
import static eu.engys.util.OpenFOAMCommands.DECOMPOSE_PAR_CONSTANT_ALLREGIONS;
import static eu.engys.util.OpenFOAMCommands.EXTRUDE_REGION_TO_MESH;
import static eu.engys.util.OpenFOAMCommands.INITIALISE_FIELDS_PARALLEL;
import static eu.engys.util.OpenFOAMCommands.INITIALISE_FIELDS_SERIAL;
import static eu.engys.util.OpenFOAMCommands.RECONSTRUCT_PAR_MESH;
import static eu.engys.util.OpenFOAMCommands.RECONSTRUCT_PAR_MESH_CONSTANT;
import static eu.engys.util.OpenFOAMCommands.RUN_CASE_PARALLEL;
import static eu.engys.util.OpenFOAMCommands.RUN_CASE_SERIAL;
import static eu.engys.util.OpenFOAMCommands.RUN_MESH_PARALLEL;
import static eu.engys.util.OpenFOAMCommands.RUN_MESH_SERIAL;
import static eu.engys.util.OpenFOAMCommands.SNAPPY_CHECK_MESH_PARALLEL;
import static eu.engys.util.OpenFOAMCommands.SNAPPY_CHECK_MESH_SERIAL;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import eu.engys.core.project.Model;
import eu.engys.util.IOUtils;
import eu.engys.util.Util;
import eu.engys.util.connection.QueueParameters;

public class DefaultScriptFactory extends AbstractScriptFactory {

    private static final String DO_NOT_EDIT_BELOW_THIS_LINE = "# DO NOT EDIT BELOW THIS LINE";
    private static final String DO_NOT_EDIT_ABOVE_THIS_LINE = "# DO NOT EDIT ABOVE THIS LINE";
    protected Model model;

    @Inject
    public DefaultScriptFactory(Model model) {
        this.model = model;
    }

    /*
     * MESH
     */

    @Override
    protected List<String> getSerialMeshScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, RUN_MESH);
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.appendIf(performBlockMesh(), BLOCK_MESH());
        sb.newLine();
        sb.append(RUN_MESH_SERIAL());
        sb.newLine();
        return sb.getLines();
    }

    @Override
    protected List<String> getParallelMeshScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, RUN_MESH);
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.appendIf(performBlockMesh(), BLOCK_MESH());
        sb.newLine();
        sb.appendIf(performBlockMesh(), DECOMPOSE_PAR(Collections.<String> emptySet()));
        sb.newLine();
        sb.append(RUN_MESH_PARALLEL());
        sb.newLine();
        sb.appendIf(performBlockMesh() && Util.isUnixScriptStyle(), "rm -rf constant/polyMesh");
        sb.newLine();
        return sb.getLines();
    }

    @Override
    protected List<String> getSerialCheckMeshScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, CHECK_MESH);
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.append(meshOnZero() ? CHECK_MESH_SERIAL() : CHECK_MESH_SERIAL_CONSTANT());
        sb.newLine();
        return sb.getLines();
    }

    @Override
    protected List<String> getParallelCheckMeshScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, CHECK_MESH);
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.append(meshOnZero() ? CHECK_MESH_PARALLEL() : CHECK_MESH_PARALLEL_CONSTANT());
        sb.newLine();
        return sb.getLines();
    }

    @Override
    protected List<String> getSerialSnappyCheckMeshScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, SNAPPY_CHECK_MESH);
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.append(SNAPPY_CHECK_MESH_SERIAL());
        sb.newLine();
        return sb.getLines();
    }

    @Override
    protected List<String> getParallelSnappyCheckMeshScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, SNAPPY_CHECK_MESH);
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.append(SNAPPY_CHECK_MESH_PARALLEL());
        sb.newLine();
        return sb.getLines();
    }

    /*
     * SOLVER
     */

    @Override
    protected List<String> getSerialSolverScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, RUN_CASE);
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.append(RUN_CASE_SERIAL());
        sb.newLine();
        return sb.getLines();
    }

    @Override
    protected List<String> getParallelSolverScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, RUN_CASE);
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.append(RUN_CASE_PARALLEL());
        sb.newLine();
        return sb.getLines();
    }

    /*
     * INITIALISE
     */

    @Override
    protected List<String> getSerialInitialiseScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, INITIALISE_FIELDS.toUpperCase());
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.append(INITIALISE_FIELDS_SERIAL());
        sb.newLine();
        return sb.getLines();
    }

    @Override
    protected List<String> getParallelInitialiseScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, INITIALISE_FIELDS.toUpperCase());
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.append(INITIALISE_FIELDS_PARALLEL());
        sb.newLine();
        return sb.getLines();
    }

    /*
     * EXTRUDE REGION
     */

    @Override
    protected List<String> getSerialExtrudeScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, EXTRUDEMESH.toUpperCase());
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.append(EXTRUDE_REGION_TO_MESH());
        sb.newLine();
        return sb.getLines();
    }

    @Override
    protected List<String> getParallelExtrudeScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, EXTRUDEMESH.toUpperCase());
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.appendIf(meshOnZero(), RECONSTRUCT_PAR_MESH(), RECONSTRUCT_PAR_MESH_CONSTANT());
        sb.append(EXTRUDE_REGION_TO_MESH());
        sb.appendIf(meshOnZero(), DECOMPOSE_PAR_ALLREGIONS(Collections.<String> emptySet()), DECOMPOSE_PAR_CONSTANT_ALLREGIONS(Collections.<String> emptySet()));
        sb.newLine();
        return sb.getLines();
    }

    private boolean meshOnZero() {
        return model.getProject().isMeshOnZero();
    }

    /*
     * QUEUE
     */

    @Override
    public List<String> getDefaultQueueDriver(Model model) {
        return getLinuxQueueDriver(defaultBody());
    }

    @Override
    public File getQueueDriver(Model model) {
        File file = new File(model.getProject().getBaseDir(), "driver.pbs");
        List<String> lines = null;
        if (file.exists()) {
            lines = getLinuxQueueDriver(extractBody(file));
        } else {
            lines = getLinuxQueueDriver(defaultBody());
        }

        IOUtils.writeLinesToFile(file, lines);
        return file;
    }

    private List<String> extractBody(File file) {
        try {
            return extractLine(file, DO_NOT_EDIT_ABOVE_THIS_LINE, DO_NOT_EDIT_BELOW_THIS_LINE);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static List<String> extractLine(File file, String start, String end) throws IOException {
        List<String> lines = FileUtils.readLines(file);
        List<String> support = new LinkedList<String>();

        boolean copy = false;
        for (String l : lines) {
            if (l.startsWith(end)) {
                break;
            }
            if (copy) {
                support.add(l);
            }
            if (l.startsWith(start)) {
                copy = true;
            }
        }
        return support;
    }

    private List<String> defaultBody() {
        InputStream inputStream = DefaultScriptFactory.class.getClassLoader().getResourceAsStream("eu/engys/resources/driver.pbs");
        try {
            String body = IOUtils.readStringFromStream(inputStream);
            return Arrays.asList(body.split(IOUtils.LNX_EOL));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<String> getLinuxQueueDriver(List<String> body) {

        String name = model.getProject().getBaseDir().getName();
        QueueParameters queueParameters = model.getSolverModel().getQueueParameters();
        int nodes = queueParameters.getNumberOfNodes();
        int cpus = queueParameters.getCpuPerNode();
        int timeout = queueParameters.getTimeout();
        String feature = queueParameters.getFeature();
        String names = queueParameters.getNodeNames();

        ScriptBuilder sb = new ScriptBuilder();
        sb.append("#PBS -o " + name + ".out");
        // sb.append("#PBS -e " + name + ".err");
        sb.append("#PBS -j oe");
        // sb.append("#PBS -k oe");
        sb.append("#PBS -N " + name);
        if (names == null || names.isEmpty()) {
            sb.append("#PBS -l nodes=" + nodes + ":ppn=" + cpus + (feature.isEmpty() ? "" : (":" + feature)));
        } else {
            sb.append("#PBS -l nodes=" + names + ":ppn=" + cpus + (feature.isEmpty() ? "" : (":" + feature)));
        }
        sb.append("#PBS -l walltime=" + timeout + ":00:00");
        sb.append("#PBS -V");
        sb.newLine();
        sb.append(DO_NOT_EDIT_ABOVE_THIS_LINE);
        sb.newLine();
        for (String line : body) {
            sb.append(line);
        }
        sb.newLine();
        sb.append(DO_NOT_EDIT_BELOW_THIS_LINE);
        sb.newLine();
        sb.append("echo \"  Environment\"");
        sb.append("echo \"---------------------------------\"");
        sb.append("echo \"  APPLICATION = $APPLICATION\"");
        sb.append("echo \"  ENV_LOADER  = $ENV_LOADER\"");
        sb.append("echo \"  HOSTFILE    = $HOSTFILE\"");
        sb.append("echo \"  CASE        = $CASE\"");
        sb.append("echo \"  LOG         = $LOG\"");
        sb.append("echo \"  NP          = $NP\"");
        sb.append("echo \"---------------------------------\"");
        sb.append("echo \"  PBS_O_WORKDIR = $PBS_O_WORKDIR\"");
        sb.append("echo \"  PBS_NODEFILE  = $PBS_NODEFILE\"");
        sb.append("echo \"---------------------------------\"");
        sb.append("echo \"  HOSTNAME = `hostname`\"");
        sb.append("echo \"  TIME     = `date`\"");
        sb.append("echo \"  PWD      = `pwd`\"");
        sb.append("echo \"---------------------------------\"");
        sb.newLine();
        sb.append("unset DISPLAY");
        sb.append("export HOSTFILE=$PBS_NODEFILE");
        sb.append("NPROCS=`wc -l < $HOSTFILE`");
        sb.append("NNODES=`uniq $HOSTFILE | wc -l`");
        sb.append("echo \"  Using $NPROCS processors across $NNODES nodes\"");
        sb.newLine();
        sb.append("cd $PBS_O_WORKDIR");
        sb.newLine();
        sb.append("$APPLICATION -case $CASE &> $LOG");
        sb.newLine();
        sb.append("exit $?");

        return sb.getLines();
    }

    @Override
    protected List<String> getLinuxSetupCaseScript() {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    protected List<String> getWindowsSetupCaseScript() {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    protected List<String> getSerialExportScript() {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    protected List<String> getParallelExportScript() {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public List<String> getReportScript() {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    protected List<String> getLinuxQueueLauncher() {
        InputStream inputStream = DefaultScriptFactory.class.getClassLoader().getResourceAsStream("eu/engys/resources/pbs.run");
        String driverString = "";
        try {
            driverString = IOUtils.readStringFromStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Arrays.asList(driverString.split(IOUtils.LNX_EOL));
    }

    @Override
    protected List<String> getWindowsQueueLauncher() {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    /*
     * UTILS
     */

    protected boolean performBlockMesh() {
        return !model.getGeometry().isAutoBoundingBox();
    }
}
