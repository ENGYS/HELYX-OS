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

import eu.engys.core.controller.actions.CommandException;
import eu.engys.core.executor.ExecutorTerminal;
import eu.engys.core.executor.TerminalManager;
import eu.engys.core.parameters.Parameters;
import eu.engys.core.project.ProjectReader;
import eu.engys.core.project.ProjectWriter;
import eu.engys.core.project.system.monitoringfunctionobjects.ParserView;

public interface Controller extends ApplicationActions, BatchActions {

	public enum CloseOptions {
		EXIT, CONTINUE, NONE
	}

	public enum OpenMode {
		SERIAL, PARALLEL, CURRENT_SETTINGS, CHECK_FOLDER_ASK_USER, CHECK_FOLDER_PARALLEL, MESH_ONLY
	}

	boolean isDemo();

	ProjectWriter getWriter();
	ProjectReader getReader();

	void addListener(ControllerListener l);
	ControllerListener getListener();

	public Client getClient();
	public Server getServer();
	public ParserView getResidualView();

	boolean allowActionsOnRunning(boolean shouldAskConfirmation);

    void createReport(ExecutorTerminal terminal);

    void executeCommand(Command command) throws CommandException;
    
    void executeCommands(Command... commands) throws CommandException;

    String submitCommand(Command command) throws CommandException;

    boolean isRunningCommand();

    void applyParameters(Parameters parameters);

    TerminalManager getTerminalManager();

    void clearModel();

}
