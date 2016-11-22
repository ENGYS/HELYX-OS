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

import static eu.engys.util.connection.SshParameters.APPLICATION_DIR;
import static eu.engys.util.connection.SshParameters.AUTHENTICATION;
import static eu.engys.util.connection.SshParameters.HOST;
import static eu.engys.util.connection.SshParameters.OPENFOAM_DIR;
import static eu.engys.util.connection.SshParameters.PARAVIEW_DIR;
import static eu.engys.util.connection.SshParameters.PORT;
import static eu.engys.util.connection.SshParameters.REMOTE_BASEDIR;
import static eu.engys.util.connection.SshParameters.REMOTE_BASEDIR_PARENT;
import static eu.engys.util.connection.SshParameters.SSH_KEY;
import static eu.engys.util.connection.SshParameters.SSH_PWD;
import static eu.engys.util.connection.SshParameters.USER;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.BeanToDict;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.system.ProjectDict;
import eu.engys.util.Util;
import eu.engys.util.connection.SshParameters;
import eu.engys.util.connection.SshParameters.AuthType;

public class SolverModelReader {

    private static final Logger logger = LoggerFactory.getLogger(SolverModelReader.class);
    private SolverModel solverModel;

    public SolverModelReader(SolverModel solverModel) {
        this.solverModel = solverModel;
    }

    void load(ProjectDict prjDict) {
        if (prjDict != null) {
            Dictionary runDict = prjDict.getRunDict();
            if (runDict != null) {
                loadFromRunDict(runDict, solverModel);
            }
        }
    }

    private void loadFromRunDict(Dictionary runDict, SolverModel solverModel) {
        BeanToDict.dictToBean(runDict, solverModel);
        SolverModelWriter.decryptPassword(solverModel.getSshParameters());
    }

    public static SshParameters readSshParametersFromDictionary(Dictionary sshParametersDict) {
        SshParameters parameters = new SshParameters();
        if (sshParametersDict.found(USER)) {
            parameters.setUser(sshParametersDict.lookup(USER));
        }
        if (sshParametersDict.found(SSH_PWD)) {
            parameters.setSshpwd(Util.decrypt(sshParametersDict.lookup(SSH_PWD)));
        }
        if (sshParametersDict.found(SSH_KEY)) {
            parameters.setSshkey(sshParametersDict.lookup(SSH_KEY));
        }
        if (sshParametersDict.found(HOST)) {
            parameters.setHost(sshParametersDict.lookup(HOST));
        }
        if (sshParametersDict.found(PORT)) {
            parameters.setPort(Integer.parseInt(sshParametersDict.lookup(PORT)));
        }
        if (sshParametersDict.found(AUTHENTICATION)) {
            parameters.setSshauth(AuthType.valueOf(sshParametersDict.lookup(AUTHENTICATION)));
        }
        if (sshParametersDict.found(REMOTE_BASEDIR)) {
            parameters.setRemoteBaseDir(sshParametersDict.lookup(REMOTE_BASEDIR));
        }
        if (sshParametersDict.found(REMOTE_BASEDIR_PARENT)) {
            parameters.setRemoteBaseDirParent(sshParametersDict.lookup(REMOTE_BASEDIR_PARENT));
        }
        if (sshParametersDict.found(APPLICATION_DIR)) {
            parameters.setApplicationDir(sshParametersDict.lookup(APPLICATION_DIR));
        }
        if (sshParametersDict.found(OPENFOAM_DIR)) {
            parameters.setOpenFoamDir(sshParametersDict.lookup(OPENFOAM_DIR));
        }
        if (sshParametersDict.found(PARAVIEW_DIR)) {
            parameters.setParaviewDir(sshParametersDict.lookup(PARAVIEW_DIR));
        }
        return parameters;
    }

}
