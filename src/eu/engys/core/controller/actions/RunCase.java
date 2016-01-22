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

package eu.engys.core.controller.actions;

import static eu.engys.core.project.system.ControlDict.CONTROL_DICT;
import static eu.engys.core.project.system.ControlDict.END_TIME_KEY;
import static eu.engys.core.project.system.ControlDict.STOP_AT_KEY;
import static eu.engys.core.project.system.ControlDict.WRITE_NOW_KEY;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.Command;
import eu.engys.core.controller.Controller;
import eu.engys.core.controller.ScriptFactory;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.executor.ExecutorHook;
import eu.engys.core.executor.ExecutorMonitor;
import eu.engys.core.project.Model;
import eu.engys.core.project.SolverState;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.state.ServerState;
import eu.engys.core.project.system.ControlDict;
import eu.engys.util.PrefUtil;

public class RunCase extends AbstractRunCommand {

    private static final Logger logger = LoggerFactory.getLogger(RunCase.class);

    public static final String ACTION_NAME = "Run Case";
    public static final String RUNNING_LABEL = "Running: ";

    protected final ScriptFactory scriptFactory;

    public RunCase(Model model, Controller controller, ScriptFactory scriptFactory) {
        super(model, controller);
        this.scriptFactory = scriptFactory;
    }

    @Override
    public void beforeExecute() {
        setupLogFolder();
        setupPostProcFolder();
        clearPolyMesh();
        setStopAtVariableToEndTime();
    }

    private void setupLogFolder() {
        File log = new File(model.getProject().getBaseDir(), openFOAMProject.LOG);
        if (!log.exists())
            log.mkdir();
    }

    private void setupPostProcFolder() {
        File log = new File(model.getProject().getBaseDir(), openFOAMProject.POST_PROC);
        if (!log.exists())
            log.mkdir();
    }

    private void clearPolyMesh() {
        model.getProject().getZeroFolder().removeNonZeroTimeFolders_GreaterThanActualTimeStep();
    }

    @Override
    public void stop() throws TimeoutException {
        int stop_refresh_time = PrefUtil.getInt(PrefUtil.SERVER_CONNECTION_REFRESH_TIME, 1000);
        int stop_max_tries = PrefUtil.getInt(PrefUtil.SERVER_CONNECTION_MAX_TRIES, 60);

        int tryIndex = 0;
        while (this.executor.getState().isDoingSomething() && (tryIndex < stop_max_tries)) {
            try {
                Thread.sleep(stop_refresh_time);
            } catch (Exception e) {
            }
            setStopAtVariableToWriteNow();
            tryIndex++;
        }
        if (tryIndex >= stop_max_tries) {
            throw new TimeoutException("Timeout stopping solver");
        }
        setStopAtVariableToEndTime();
    }

    private void setStopAtVariableToWriteNow() {
        File systemFolder = model.getProject().getSystemFolder().getFileManager().getFile();
        ControlDict controlDict = new ControlDict(new File(systemFolder, CONTROL_DICT));
        controlDict.add(STOP_AT_KEY, WRITE_NOW_KEY);
        controlDict.functionObjectsToList();
        DictionaryUtils.writeDictionary(systemFolder, controlDict, null);
    }

    private void setStopAtVariableToEndTime() {
        File systemFolder = model.getProject().getSystemFolder().getFileManager().getFile();
        ControlDict controlDict = new ControlDict(new File(systemFolder, CONTROL_DICT));
        if (controlDict.found(STOP_AT_KEY) && controlDict.lookup(STOP_AT_KEY).equals(WRITE_NOW_KEY)) {
            controlDict.add(STOP_AT_KEY, END_TIME_KEY);
            controlDict.functionObjectsToList();
            DictionaryUtils.writeDictionary(systemFolder, controlDict, null);
        }
    }

    protected class StartHook implements ExecutorHook {
        @Override
        public void run(ExecutorMonitor m) {
            model.getSolverModel().writeState(new ServerState(Command.RUN_CASE, SolverState.RUNNING), model);
        }
    }

}
