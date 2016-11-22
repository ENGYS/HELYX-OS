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
package eu.engys.core.controller.actions;

import static eu.engys.core.project.system.ControlDict.CONTROL_DICT;
import static eu.engys.core.project.system.ControlDict.END_TIME_KEY;
import static eu.engys.core.project.system.ControlDict.STOP_AT_KEY;
import static eu.engys.core.project.system.ControlDict.WRITE_NOW_KEY;

import java.io.File;

import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.executor.Executor;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.ControlDict;
import eu.engys.util.PrefUtil;

public class Stopper {

    protected Executor executor;
    protected Model model;

    public Stopper(Model model, Executor executor) {
        this.model = model;
        this.executor = executor;
    }

    public void stop() throws Exception {
        setStopAtVariableToWriteNow();

        waitForExecutorToStop();
        
        setStopAtVariableToEndTime();
    }

    protected void waitForExecutorToStop() throws Exception {
        int stop_refresh_time = PrefUtil.getInt(PrefUtil.SERVER_CONNECTION_REFRESH_TIME, 1000);
        int stop_max_tries = PrefUtil.getInt(PrefUtil.SERVER_CONNECTION_MAX_TRIES, 60);

        int tryIndex = 0;
        while (this.executor.getState().isDoingSomething() && (tryIndex < stop_max_tries)) {
            try {
                Thread.sleep(stop_refresh_time);
            } catch (Exception e) {
            }
            tryIndex++;
        }
        if (tryIndex >= stop_max_tries) {
            throw new TimeoutException("Timeout stopping solver");
        }
    }

    private void setStopAtVariableToWriteNow() {
        File systemFolder = model.getProject().getSystemFolder().getFileManager().getFile();
        ControlDict controlDict = new ControlDict(new File(systemFolder, CONTROL_DICT));
        controlDict.add(STOP_AT_KEY, WRITE_NOW_KEY);
        DictionaryUtils.writeDictionary(systemFolder, controlDict, null);
        
        ControlDict readControlDict = new ControlDict(new File(systemFolder, CONTROL_DICT));
        if(!readControlDict.found(STOP_AT_KEY) || !readControlDict.lookup(STOP_AT_KEY).equals(WRITE_NOW_KEY)){
            try {
                Thread.sleep(500L);
            } catch (Exception e) {
            }
            setStopAtVariableToWriteNow();
        }
    }

    void setStopAtVariableToEndTime() {
        File systemFolder = model.getProject().getSystemFolder().getFileManager().getFile();
        ControlDict controlDict = new ControlDict(new File(systemFolder, CONTROL_DICT));
        if (controlDict.found(STOP_AT_KEY) && controlDict.lookup(STOP_AT_KEY).equals(WRITE_NOW_KEY)) {
            controlDict.add(STOP_AT_KEY, END_TIME_KEY);
            DictionaryUtils.writeDictionary(systemFolder, controlDict, null);
        }
    }

}