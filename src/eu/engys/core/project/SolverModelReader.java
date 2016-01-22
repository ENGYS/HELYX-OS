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
import eu.engys.core.project.system.RunDict;
import eu.engys.util.Util;
import eu.engys.util.connection.SshParameters;
import eu.engys.util.connection.SshParameters.AuthType;

public class SolverModelReader {

    private static final Logger logger = LoggerFactory.getLogger(SolverModelReader.class);
    private Model model;

    public SolverModelReader(Model model) {
        this.model = model;
    }

    void load() {
        RunDict runDict = model.getProject().getSystemFolder().getRunDict();
        populateSolverModel(runDict);
    }

    private void populateSolverModel(Dictionary runDict) {
        SolverModel solverModel = model.getSolverModel();
        if (runDict != null) {
            loadFromRunDict(runDict, solverModel);
        }
    }

    void loadFromRunDict(Dictionary runDict, SolverModel solverModel) {
        BeanToDict.dictToBean(runDict, solverModel);
        SolverModelWriter.decryptPassword(solverModel.getSshParameters());
////        readRMIPort(runDict, solverModel);
////        readLOGPort(runDict, solverModel);
//        readServerID(runDict, solverModel);
//        readRemote(runDict, solverModel);
//        readQueue(runDict, solverModel);
//        readLogFile(runDict, solverModel);
//        readState(runDict, solverModel);
//        readSSHParameters(runDict, solverModel);
//        readQueueParameters(runDict, solverModel);
//        readMultiMachine(runDict, solverModel);
//        readHostfilePath(runDict, solverModel);
    }

//    private void readRemote(Dictionary runDict, SolverModel solverModel) {
//        if (runDict.found(REMOTE)) {
//            solverModel.setRemote(Boolean.valueOf(runDict.lookup(REMOTE)));
//        }
//    }
//
//    private void readQueue(Dictionary runDict, SolverModel solverModel) {
//        if (runDict.found(QUEUE)) {
//            solverModel.setQueue(Boolean.valueOf(runDict.lookup(QUEUE)));
//        }
//    }
//
//    private void readMultiMachine(Dictionary runDict, SolverModel solverModel) {
//        if (runDict.found(MULTI_MACHINE)) {
//            Boolean value = Boolean.valueOf(runDict.lookup(MULTI_MACHINE));
//            solverModel.setMultiMachine(value && !Util.isWindows());
//        }
//    }
//
//    private void readHostfilePath(Dictionary runDict, SolverModel solverModel) {
//        if (runDict.found(HOSTFILE_PATH)) {
//            solverModel.setHostfilePath(runDict.lookup(HOSTFILE_PATH));
//        }
//    }
//
//    private void readLogFile(Dictionary runDict, SolverModel solverModel) {
//        if (runDict.found(LOG_FILE)) {
//            solverModel.setLogFile(runDict.lookup(LOG_FILE));
//        }
//    }

//    private void readRMIPort(Dictionary runDict, SolverModel solverModel) {
//        if (runDict.found(RMI_PORT)) {
//            solverModel.setRmiPort(runDict.lookupInt(RMI_PORT));
//        }
//    }
//
//    private void readLOGPort(Dictionary runDict, SolverModel solverModel) {
//        if (runDict.found(LOG_PORT)) {
//            solverModel.setLogPort(runDict.lookupInt(LOG_PORT));
//        }
//    }

//    private void readServerID(Dictionary runDict, SolverModel solverModel) {
//        if (runDict.found(SERVER_ID)) {
//            solverModel.setServerID(runDict.lookup(SERVER_ID));
//        }
//    }
//
//    private void readState(Dictionary runDict, SolverModel solverModel) {
//        Dictionary serverStateDict = runDict.found(SERVER_STATE) ? runDict.subDict(SERVER_STATE) : new Dictionary(SERVER_STATE);
//        
//        ServerState serverState = readServerStateFromDictionary(serverStateDict);
//        solverModel.setServerState(serverState);
//    }

//    public static ServerState readServerStateFromDictionary(Dictionary serverStateDict) {
//        ServerState serverState = new ServerState();
//        
//        if (serverStateDict.found(ServerState.COMMAND)) {
//            serverState.setCommand(Command.valueOf(serverStateDict.lookup(ServerState.COMMAND)));
//        }
//        if (serverStateDict.found(ServerState.SOLVER_STATE)) {
//            serverState.setSolverState(SolverState.valueOf(serverStateDict.lookup(ServerState.SOLVER_STATE)));
//        }
//        if (serverStateDict.found(ServerState.ERROR)) {
//            serverState.setError(new ServerError(1, "message"));
//        }
//        return serverState;
//    }

//    private void readSSHParameters(Dictionary runDict, SolverModel solverModel) {
//        Dictionary sshParametersDict = runDict.found(SSH_PARAMETERS) ? runDict.subDict(SSH_PARAMETERS) : new Dictionary(SSH_PARAMETERS);
//
//        SshParameters parameters = readSshParametersFromDictionary(sshParametersDict);
//        solverModel.setSshParameters(parameters);
//    }

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

//    private void readQueueParameters(Dictionary runDict, SolverModel solverModel) {
//        QueueParameters parameters = new QueueParameters();
//        Dictionary queueParametersDict = runDict.found(QUEUE_PARAMETERS) ? runDict.subDict(QUEUE_PARAMETERS) : new Dictionary(QUEUE_PARAMETERS);
//
//        if (queueParametersDict.found(QUEUE_NODES)) {
//            String nodes = queueParametersDict.lookup(QUEUE_NODES);
//            parameters.setNumberOfNodes(Integer.parseInt(nodes));
//        }
//        if (queueParametersDict.found(QUEUE_CPUS)) {
//            String cpus = queueParametersDict.lookup(QUEUE_CPUS);
//            parameters.setCpuPerNode(Integer.parseInt(cpus));
//        }
//        if (queueParametersDict.found(QUEUE_TIMEOUT)) {
//            String timeout = queueParametersDict.lookup(QUEUE_TIMEOUT);
//            parameters.setTimeout(Integer.parseInt(timeout));
//        }
//        if (queueParametersDict.found(QUEUE_FEATURE)) {
//            String feature = queueParametersDict.lookup(QUEUE_FEATURE);
//            parameters.setFeature(feature);
//        }
//        if (queueParametersDict.found(QUEUE_NAMES)) {
//            String names = queueParametersDict.lookup(QUEUE_NAMES);
//            parameters.setNodeNames(names);
//        }
//
//        solverModel.setQueueParameters(parameters);
//    }
}
