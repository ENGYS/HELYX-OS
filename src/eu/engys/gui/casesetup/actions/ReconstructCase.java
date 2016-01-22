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

package eu.engys.gui.casesetup.actions;

import static eu.engys.core.OpenFOAMEnvironment.getEnvironment;
import static eu.engys.core.OpenFOAMEnvironment.loadEnvironment;
import static eu.engys.core.OpenFOAMEnvironment.printHeader;
import static eu.engys.core.OpenFOAMEnvironment.printVariables;
import static eu.engys.core.project.openFOAMProject.LOG;
import static eu.engys.util.OpenFOAMCommands.RECONSTRUCT_PAR;
import static eu.engys.util.OpenFOAMCommands.RECONSTRUCT_PAR_ALLREGIONS;
import static eu.engys.util.OpenFOAMCommands.RECONSTRUCT_PAR_MESH;
import static eu.engys.util.OpenFOAMCommands.RECONSTRUCT_PAR_MESH_ALLREGIONS;
import static eu.engys.util.OpenFOAMCommands.RECONSTRUCT_PAR_MESH_CONSTANT;
import static eu.engys.util.OpenFOAMCommands.RECONSTRUCT_PAR_MESH_CONSTANT_ALLREGIONS;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

import eu.engys.core.controller.Controller;
import eu.engys.core.controller.ScriptBuilder;
import eu.engys.core.controller.actions.AbstractRunCommand;
import eu.engys.core.executor.Executor;
import eu.engys.core.executor.TerminalExecutorMonitor;
import eu.engys.core.project.Model;
import eu.engys.util.IOUtils;
import eu.engys.util.Util;

public class ReconstructCase extends AbstractRunCommand {

    private static final String RECONSTRUCT_CASE_RUN = "reconstructCase.run";
    private static final String RECONSTRUCT_CASE_BAT = "reconstructCase.bat";

    private String actionName;
    private String logName;
    private File logFile;
    private boolean shouldUseWithZeroFlag;

    public ReconstructCase(Model model, Controller controller, boolean shouldUseWithZeroFlag, String actionName, String logName) {
        super(model, controller);
        this.shouldUseWithZeroFlag = shouldUseWithZeroFlag;
        this.actionName = actionName;
        this.logName = logName;
        this.logFile = Paths.get(model.getProject().getBaseDir().getAbsolutePath(), LOG, logName).toFile();
    }

    @Override
    public void executeClient() {
        File script = getScript();
        File baseDir = model.getProject().getBaseDir();
        
        if (terminal == null) {
            this.terminal = new TerminalExecutorMonitor(logFile);
        }
        if (service == null) {
            this.service = Executors.newSingleThreadExecutor();
        }

        this.executor = Executor.script(script).description(actionName).inFolder(baseDir).inTerminal(terminal).inService(service).env(getEnvironment(model, logName));
        executor.exec();
    }

    private File getScript() {
        File file = new File(model.getProject().getBaseDir(), Util.isWindows() ? RECONSTRUCT_CASE_BAT : RECONSTRUCT_CASE_RUN);
        ScriptBuilder sb = new ScriptBuilder();
        writeScript(sb);
        IOUtils.writeLinesToFile(file, sb.getLines());
        file.setExecutable(true);
        return file;
    }

    private void writeScript(ScriptBuilder sb) {
        printHeader(sb, actionName.toUpperCase());
        printVariables(sb);
        loadEnvironment(sb);
        writeCommand(sb);
    }

    private void writeCommand(ScriptBuilder sb) {
        boolean meshOnZero = model.getProject().isMeshOnZero();

        if (meshOnZero) {
            if (model.getProject().getZeroFolder().hasRegions()) {
                sb.append(RECONSTRUCT_PAR_MESH_ALLREGIONS());
            } else {
                sb.append(RECONSTRUCT_PAR_MESH());
            }
        } else {
            if (model.getProject().getZeroFolder().hasRegions()) {
                sb.append(RECONSTRUCT_PAR_MESH_CONSTANT_ALLREGIONS());
            } else {
                sb.append(RECONSTRUCT_PAR_MESH_CONSTANT());
            }
        }

        if (model.getProject().getZeroFolder().hasRegions()) {
            sb.append(RECONSTRUCT_PAR_ALLREGIONS(shouldUseWithZeroFlag));
        } else {
            sb.append(RECONSTRUCT_PAR(shouldUseWithZeroFlag));
        }

    }

}
