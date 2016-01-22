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

package eu.engys.core.project.state;

import java.io.Serializable;

import eu.engys.core.controller.Command;
import eu.engys.core.executor.ExecutorError;
import eu.engys.core.project.SolverState;

public class ServerState implements Serializable {

    public static final String SOLVER_STATE = "solverState";
    public static final String COMMAND = "command";
    public static final String ERROR = "error";

    private Command command = Command.NONE;
    private SolverState solverState = SolverState.FINISHED;
    private ExecutorError error = null;

    public ServerState() {
    }

    public ServerState(Command command, SolverState solverState) {
        this.command = command;
        this.solverState = solverState;
    }

    public ServerState(Command command, SolverState solverState, ExecutorError error) {
        this.command = command;
        this.solverState = solverState;
        this.error = error;
    }

    public ServerState(ServerState remoteState) {
        this.command = remoteState.getCommand();
        this.solverState = remoteState.getSolverState();
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public SolverState getSolverState() {
        return solverState;
    }

    public void setSolverState(SolverState solverState) {
        this.solverState = solverState;
    }

    public ExecutorError getError() {
        return error;
    }

    public void setError(ExecutorError error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return command + " [State: " + solverState + ", Exit Value: " + (error != null ? error : "") + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServerState) {
            ServerState state = (ServerState) obj;
            return this.command.equals(state.command) && this.solverState.equals(state.solverState);
        }
        return super.equals(obj);
    }
}
