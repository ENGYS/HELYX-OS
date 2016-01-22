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

package eu.engys.core.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorMonitor implements ExecutorListener {

    protected static final Logger logger = LoggerFactory.getLogger(ExecutorMonitor.class);
    private Map<ExecutorState, List<ExecutorHook>> stateHooks = new HashMap<>();

    protected ExecutorService executor;
    private ExecutorState state;

    private int returnValue;
    private String errorMessage;

    @Override
    public void start() {
        logger.info("[EXECUTOR MONITOR] START");
        state = ExecutorState.START;
        runHook();
    }

    @Override
    public void refresh() {
        state = ExecutorState.RUNNING;
        runHook();
    }

    @Override
    public void finish(int returnValue) {
        logger.info("[EXECUTOR MONITOR] FINISH: value = {}", returnValue);
        this.state = ExecutorState.FINISH;
        this.returnValue = returnValue;
        runHook();
    }

    @Override
    public void error(int returnValue, String msg) {
        logger.info("[EXECUTOR MONITOR] ERROR: {}", msg);
        this.returnValue = -1;
        this.errorMessage = decodeError(returnValue, msg);
        this.state = ExecutorState.ERROR;
        runHook();
    }

    public static String decodeError(int returnValue, String msg) {
        switch (returnValue) {
        case 127:
            return "Command not found";
        case 130:
            return "Script Terminated";
        case 137:
            return "Process Killed";
        case 255:
            return "Script Error";

        default:
            return msg;
        }
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void addHook(ExecutorState state, ExecutorHook hook) {
        if (!stateHooks.containsKey(state)) {
            stateHooks.put(state, new ArrayList<ExecutorHook>());
        }
        stateHooks.get(state).add(hook);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getReturnValue() {
        return returnValue;
    }

    public ExecutorState getState() {
        return state;
    }

    private void runHook() {
        if (stateHooks.containsKey(state)) {
            for (ExecutorHook hook : stateHooks.get(state)) {
                hook.run(this);
            }
        }
    }
}
