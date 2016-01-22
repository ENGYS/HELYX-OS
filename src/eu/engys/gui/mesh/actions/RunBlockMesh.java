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

package eu.engys.gui.mesh.actions;

import static eu.engys.core.OpenFOAMEnvironment.getEnvironment;
import static eu.engys.core.OpenFOAMEnvironment.loadEnvironment;
import static eu.engys.core.OpenFOAMEnvironment.printHeader;
import static eu.engys.core.OpenFOAMEnvironment.printVariables;
import static eu.engys.core.project.constant.ConstantFolder.CONSTANT;
import static eu.engys.core.project.constant.ConstantFolder.POLY_MESH;
import static eu.engys.core.project.openFOAMProject.LOG;
import static eu.engys.util.OpenFOAMCommands.BLOCK_MESH;
import static eu.engys.util.OpenFOAMCommands.DECOMPOSE_PAR;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import eu.engys.core.controller.Controller;
import eu.engys.core.controller.Controller.OpenOptions;
import eu.engys.core.controller.ScriptBuilder;
import eu.engys.core.controller.actions.AbstractRunCommand;
import eu.engys.core.executor.Executor;
import eu.engys.core.executor.ExecutorHook;
import eu.engys.core.executor.ExecutorListener.ExecutorState;
import eu.engys.core.executor.ExecutorMonitor;
import eu.engys.core.executor.ExecutorTerminal;
import eu.engys.core.executor.TerminalExecutorMonitor;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.util.IOUtils;
import eu.engys.util.Util;

public class RunBlockMesh extends AbstractRunCommand {

    public static final String ACTION_NAME = "Run Block Mesh";

    public static final String BLOCK_MESH_LOG = "blockMesh.log";
    public static final String BLOCK_MESH_RUN = "block_mesh.run";
    public static final String BLOCK_MESH_BAT = "block_mesh.bat";

    private File logFile;

    public RunBlockMesh(Model model, Controller controller) {
        super(model, controller);
        this.logFile = Paths.get(model.getProject().getBaseDir().getAbsolutePath(), LOG, BLOCK_MESH_LOG).toFile();
    }

    @Override
    public void beforeExecute() {
        IOUtils.clearFile(logFile);
        clearPolyMesh();
        setupLogFolder();
    }

    private void clearPolyMesh() {
        model.getProject().getZeroFolder().deleteMesh();
    }

    private void setupLogFolder() {
        File log = new File(model.getProject().getBaseDir(), openFOAMProject.LOG);
        if (!log.exists()) {
            log.mkdir();
        }
    }

    @Override
    public void executeClient() {
        File baseDir = model.getProject().getBaseDir();
        File script = getScript();

        ExecutorTerminal terminal = new TerminalExecutorMonitor(logFile);
        ExecutorMonitor monitor = new ExecutorMonitor();
        monitor.addHook(ExecutorState.FINISH, new FinishHook());

        this.executor = Executor.script(script).description(ACTION_NAME).inFolder(baseDir).inTerminal(terminal).withMonitors(monitor).env(getEnvironment(model, BLOCK_MESH_LOG));
        executor.exec();
    }

    private File getScript() {
        File file = new File(model.getProject().getBaseDir(), Util.isWindowsScriptStyle() ? BLOCK_MESH_BAT : BLOCK_MESH_RUN);
        ScriptBuilder sb = new ScriptBuilder();
        writeScript(sb);

        IOUtils.writeLinesToFile(file, sb.getLines());

        file.setExecutable(true);
        return file;
    }

    private void writeScript(ScriptBuilder sb) {
        printHeader(sb, ACTION_NAME.toUpperCase());
        printVariables(sb);
        loadEnvironment(sb);
        writeCommand(sb);
    }

    private void writeCommand(ScriptBuilder sb) {
        sb.append(BLOCK_MESH());
        if (model.getProject().isParallel()) {
            sb.append(DECOMPOSE_PAR());
        }
    }

    private class FinishHook implements ExecutorHook {
        @Override
        public void run(ExecutorMonitor m) {
            if (model.getProject().isParallel()) {
                File polyMesh = Paths.get(model.getProject().getBaseDir().getAbsolutePath()).resolve(CONSTANT).resolve(POLY_MESH).toFile();
                FileUtils.deleteQuietly(polyMesh);
            }
            controller.reopenCase(OpenOptions.CURRENT_SETTINGS);
            if(controller.getListener() != null){
                controller.getListener().afterBlockMesh();
            }
        }
    }

}
