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

package eu.engys.core.project.system;

import java.io.File;

import eu.engys.core.dictionary.BeanToDict;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.project.SolverModel;

public class RunDict extends Dictionary {
	public static final String RUN_DICT = "runDict";
//	public static final String POST_PROC_FILE_MAP = "postProcFileMap";
	public static final String SERVER_STATE = "serverState";
	public static final String REMOTE = "remote";
	public static final String QUEUE = "queue";
	public static final String SERVER_ID = "serverID";
	public static final String LOG_FILE = "logFile";
	public static final String RMI_PORT = "rmiPort";
	public static final String LOG_PORT = "logPort";
	public static final String SSH_PARAMETERS = "sshParameters";
	public static final String QUEUE_PARAMETERS = "queueParameters";
	public static final String MULTI_MACHINE = "multiMachine";
	public static final String HOSTFILE_PATH = "hostfilePath";

	public RunDict() {
		super(RUN_DICT);
	}

	public RunDict(SolverModel solverModel) {
		this();
		merge(BeanToDict.beanToDict(solverModel));
	}

	public RunDict(File runDictFile) {
		this();
		readDictionary(runDictFile);
	}

	@Override
	public void check() throws DictionaryException {
	}

}
