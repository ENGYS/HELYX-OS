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
package eu.engys.core.project;

public enum SolverState {
    
    STARTED, RUNNING, FINISHED, MESHING, MESHED, INITIALISING, INITIALISED, ERROR;

    public boolean isStarted() {
        return this == STARTED;
    }

    public boolean isRunning() {
        return this == RUNNING;
    }

    public boolean isFinished() {
        return this == FINISHED;
    }
    
    public boolean isMeshed() {
        return this == MESHED;
    }

    public boolean isMeshing() {
        return this == MESHING;
    }

    public boolean isInitialising() {
        return this == INITIALISING;
    }

    public boolean isInitialised() {
        return this == INITIALISED;
    }

    public boolean isError() {
        return this == ERROR;
    }

    public boolean isDoingSomething() {
        return this == STARTED || this == RUNNING || this == MESHING || this == INITIALISING;
    }

}