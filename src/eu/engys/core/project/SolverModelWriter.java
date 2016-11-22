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

import eu.engys.core.project.system.ProjectDict;
import eu.engys.core.project.system.RunDict;
import eu.engys.util.Util;
import eu.engys.util.connection.SshParameters;

public class SolverModelWriter {

    private SolverModel solverModel;

    public SolverModelWriter(SolverModel solverModel) {
        this.solverModel = solverModel;
    }

    void save(openFOAMProject project) {
        SshParameters sshParameters = solverModel.getSshParameters();
        encryptPassword(sshParameters);
        RunDict runDict = new RunDict(solverModel);
        if (project.getSystemFolder().getProjectDict() == null) {
            project.getSystemFolder().setProjectDict(new ProjectDict());
        }
        project.getSystemFolder().getProjectDict().setRunDict(runDict);
        decryptPassword(sshParameters);
    }

    public static void encryptPassword(SshParameters sshParameters) {
        if (sshParameters != null) {
            sshParameters.setSshpwd(Util.encrypt(sshParameters.getSshpwd()));
        }
    }

    public static void decryptPassword(SshParameters sshParameters) {
        if (sshParameters != null) {
            sshParameters.setSshpwd(Util.decrypt(sshParameters.getSshpwd()));
        }
    }

}
