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

import static eu.engys.core.project.system.RunDict.RUN_DICT;

import java.io.File;
import java.io.Serializable;
import java.util.Observable;

import eu.engys.core.controller.Command;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.project.state.ServerState;
import eu.engys.util.connection.QueueParameters;
import eu.engys.util.connection.SshParameters;
import eu.engys.util.progress.SilentMonitor;

public class SolverModel extends Observable implements Serializable {

    private String logFile = "";
    private String hostfilePath = "";
//    private int rmiPort = 20001;
//    private int logPort = 21001;
    private boolean multiMachine = false;

    private ServerState serverState = new ServerState(Command.NONE, SolverState.FINISHED);

    private SshParameters sshParameters = new SshParameters();
    private QueueParameters queueParameters = new QueueParameters();
    private boolean remote = false;
    private boolean queue = false;

    private String serverID;

    public String getHostfilePath() {
        return hostfilePath;
    }
    public void setHostfilePath(String hostfilePath) {
        this.hostfilePath = hostfilePath;
    }

    public boolean getMultiMachine() {
        return multiMachine;
    }
    public void setMultiMachine(boolean multiMachine) {
        this.multiMachine = multiMachine;
    }

    public String getServerID() {
        return serverID;
    }
    public void setServerID(String serverID) {
        this.serverID = serverID;
    }
    
    public String getLogFile() {
        return logFile;
    }
    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public ServerState getServerState() {
        return serverState;
    }

    public void setServerState(ServerState serverState) {
        this.serverState = serverState;
        setChanged();
        notifyObservers();
    }

    public SshParameters getSshParameters() {
        return sshParameters;
    }

    public void setSshParameters(SshParameters sshParameters) {
        this.sshParameters = sshParameters;
    }

    public QueueParameters getQueueParameters() {
        return queueParameters;
    }

    public void setQueueParameters(QueueParameters queueParameters) {
        this.queueParameters = queueParameters;
    }

    public void setQueue(boolean queue) {
        this.queue = queue;
    }

    public boolean isQueue() {
        return queue;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }

    public boolean isRemote() {
        return remote;
    }

    private void read(Model model) {
        File file = model.getProject().getSystemFolder().getFileManager().getFile(RUN_DICT);
        new SolverModelReader(model).loadFromRunDict(new Dictionary(file), this);
    }

    // Called from Server, so you need to load runDict from disk
    public void writeState(ServerState state, Model model) {
        read(model);
        setServerState(state);
        write(model);
    }

    public void writeServerID(String serverID, Model model) {
        setServerID(serverID);
        write(model);
    }

    private void write(Model model) {
        new SolverModelWriter(model).save();
        DictionaryUtils.writeDictionary(model.getProject().getSystemFolder().getFileManager().getFile(), model.getProject().getSystemFolder().getRunDict(), new SilentMonitor());
    }

    @Override
    public String toString() {
        return "SolverModel [" + "state=" + serverState + ", " + "logFile=" + logFile + ", " + "serverID=" + serverID + ", " + "sshParameters=" + sshParameters + ", " + "queueParameters=" + queueParameters + "]";
    }

}
