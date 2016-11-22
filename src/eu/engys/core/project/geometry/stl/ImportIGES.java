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
package eu.engys.core.project.geometry.stl;

import static eu.engys.core.OpenFOAMEnvironment.getEnvironment;
import static eu.engys.core.OpenFOAMEnvironment.loadEnvironment;
import static eu.engys.core.OpenFOAMEnvironment.printHeader;
import static eu.engys.core.OpenFOAMEnvironment.printVariables;
import static eu.engys.core.project.openFOAMProject.LOG;
import static eu.engys.util.OpenFOAMCommands.CAD_TOOL;

import java.io.File;
import java.nio.file.Paths;

import eu.engys.core.controller.Controller;
import eu.engys.core.controller.ScriptBuilder;
import eu.engys.core.controller.actions.AbstractRunCommand;
import eu.engys.core.executor.Executor;
import eu.engys.core.executor.ExecutorHook;
import eu.engys.core.executor.ExecutorListener.ExecutorState;
import eu.engys.core.executor.ExecutorMonitor;
import eu.engys.core.executor.ExecutorTerminal;
import eu.engys.core.executor.TerminalExecutorMonitor;
import eu.engys.core.project.Model;
import eu.engys.util.IOUtils;
import eu.engys.util.Util;

public class ImportIGES extends AbstractRunCommand {

    public static final String ACTION_NAME = "Import IGES";
    public static final String LOG_NAME = "importIGES.log";

    private static final String IMPORT_IGES_RUN = "importIGES.run";
    private static final String IMPORT_IGES_BAT = "importIGES.bat";

    private File[] input;
    private File[] output;

    private boolean split;
    private double precision;
    private File logFile;
    private Runnable loadSTLRunnable;

    public ImportIGES(Model model, Controller controller, Runnable loadSTLRunnable, File[] input, File[] output, boolean split, double precision) {
        super(model, controller);
        this.input = input;
        this.output = output;
        this.split = split;
        this.precision = precision;
        this.loadSTLRunnable = loadSTLRunnable;
        this.logFile = Paths.get(model.getProject().getBaseDir().getAbsolutePath(), LOG, LOG_NAME).toFile();
    }

    @Override
    public void beforeExecute() {
        IOUtils.clearFile(logFile);
    }

    @Override
    public void executeClient() {
        File script = getScript();

        ExecutorTerminal terminal = new TerminalExecutorMonitor(controller.getTerminalManager(), logFile);
        ExecutorMonitor monitor = new ExecutorMonitor();
        monitor.addHook(ExecutorState.FINISH, new FinishHook());

        this.executor = Executor.script(script).description(ACTION_NAME).inFolder(input[0].getParentFile()).env(getEnvironment(model, LOG_NAME)).inTerminal(terminal).withMonitors(monitor);
        executor.exec();
    }

    private File getScript() {
        File file = new File(model.getProject().getBaseDir(), Util.isWindows() ? IMPORT_IGES_BAT : IMPORT_IGES_RUN);
        ScriptBuilder sb = new ScriptBuilder();
        writeScript(sb);

        IOUtils.writeLinesToFile(file, sb.getLines());
        file.setExecutable(true);
        return file;
    }

    private void writeScript(ScriptBuilder sb) {
        printHeader(sb, ACTION_NAME);
        printVariables(sb);
        loadEnvironment(sb);
        writeCommand(sb);
    }

    private void writeCommand(ScriptBuilder sb) {
        for (int i = 0; i < input.length; i++) {
            sb.append(CAD_TOOL(split, precision, input[i], output[i]));
        }
    }

    private class FinishHook implements ExecutorHook {

        @Override
        public void run(ExecutorMonitor monitor) {
            loadSTLRunnable.run();
        }
    }

}
