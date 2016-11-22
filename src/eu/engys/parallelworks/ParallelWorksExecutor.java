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
package eu.engys.parallelworks;

import static eu.engys.parallelworks.ParallelWorksData.PROJECT_JOB_FILE_NAME;
import static eu.engys.parallelworks.ParallelWorksData.PROJECT_URL_FILE_NAME;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FileUtils;

import eu.engys.core.executor.AbstractExecutor;
import eu.engys.parallelworks.json.JobState;

public class ParallelWorksExecutor extends AbstractExecutor {

    int lastLine = 0;

    private File baseDir;
    private String command;
    private File log;

    private String lastStatus;

    public ParallelWorksExecutor(File baseDir, String command, File log) {
        this.baseDir = baseDir;
        this.command = command;
        this.log = log;
    }

    @Override
    protected int _exec() {
        notifyStart();

        try {
            ParallelWorksClient.getInstance().startJob(command);
            FileUtils.writeStringToFile(new File(baseDir, PROJECT_JOB_FILE_NAME), ParallelWorksClient.getInstance().getJobID().getId());
        } catch (IOException e1) {
            notifyError(-1, "Error starting JOB");
            return 0;
        }

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                notifyError(-1, "Killed");
                return 0;
            }

            JobState js;
            try {
                js = ParallelWorksClient.getInstance().getJobState();
            } catch (Exception e) {
                js = new JobState("starting", "");
            }

            String status = js.getStatus();
            if (status != null && !status.equals(lastStatus)) {
                try {
                    FileUtils.writeStringToFile(log, status+"\n", true);
                } catch (IOException e) {
                }
            }
            lastStatus = status;

            switch (js.getState()) {
                case "ok":
                    try {
                        FileUtils.writeStringToFile(new File(baseDir, PROJECT_URL_FILE_NAME), ParallelWorksClient.getInstance().getDownloadUrl());
                    } catch (IOException e1) {
                    }
                    notifyFinish(0);
                    return 0;
                case "deleted":
                case "error":
                    notifyError(-1, "Simulation had an error. Please try again");
                    return 0;
                default:
                    String tail = ParallelWorksClient.getInstance().getJobTail(lastLine);
                    if (!tail.isEmpty()) {
                        try {
                            FileUtils.writeStringToFile(log, tail, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        lastLine += count(tail, '\n');
                    }
                    notifyRefresh();
                    break;
            }
        }
    }

    private int count(String s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (c == s.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    @Override
    protected CommandLine getCommandLine() {
        return null;
    }

}
