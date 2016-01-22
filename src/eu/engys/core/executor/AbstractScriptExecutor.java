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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.apache.commons.io.FileUtils;

import eu.engys.util.PrefUtil;
import eu.engys.util.Util;

public abstract class AbstractScriptExecutor extends AbstractExecutor {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected int _exec() {
        int returnValue = 0;
        CommandLine cmdLine = getCommandLine();

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        ExecuteWatchdog watchdog = new ScriptExecuteWatchdog();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        executor.setWorkingDirectory(currentDir);
        executor.setWatchdog(watchdog);
        if (terminal != null) {
            ExecuteStreamHandler monitorStreamHandler = new PumpStreamHandler(terminal.getOutputStream(), terminal.getErrorStream());
            executor.setStreamHandler(monitorStreamHandler);
        } else {
            ExecuteStreamHandler silentStreamHandler = new PumpStreamHandler();
            executor.setStreamHandler(silentStreamHandler);
        }

        Map procEnvironment = null;
        if (environment != null) {
            try {
                procEnvironment = EnvironmentUtils.getProcEnvironment();
                procEnvironment.putAll(environment);
            } catch (IOException e) {
                logger.error(e.getMessage());
                procEnvironment = null;
            }
        }

        try {
            executor.execute(cmdLine, procEnvironment, resultHandler);
            notifyStart();
        } catch (Exception e) {
            logger.warn("[EXECUTOR] ERROR: {}", e.getMessage());

            notifyError(-1, e.getMessage());

            return 1;
        }

        try {
            if (resultHandler.hasResult()) {
                notifyRefresh();
            } else {
                logger.info("[EXECUTOR] RUNNING");
                while (!resultHandler.hasResult()) {
                    resultHandler.waitFor(PrefUtil.getInt(PrefUtil.SCRIPT_RUN_REFRESH_TIME, 1000));
                    notifyRefresh();
                }
            }
        } catch (InterruptedException e) {
            logger.warn("[EXECUTOR] INTERRUPTED");
            watchdog.destroyProcess();
        } finally {
            if (watchdog.killedProcess()) {
                logger.warn("[EXECUTOR] WAITING FOR KILL");
                try {
                    resultHandler.waitFor(PrefUtil.getInt(PrefUtil.SCRIPT_WAIT_FOR_KILL_REFRESH_TIME, 5000));
                } catch (InterruptedException e) {
                    logger.warn("[EXECUTOR] INTERRUPTED: {}", e.getMessage());
                    notifyError(-1, e.getMessage());
                }
            }
            try {
                ExecuteException exception = resultHandler.getException();
                if (exception != null) {
                    returnValue = resultHandler.getExitValue();
                    logger.warn("[EXECUTOR] ERROR: {}", exception.getMessage());

                    if (terminal != null) {
                        // String error = terminal.getErrorStream().peekLines();
                        // notifyError(returnValue, error);
                        // error is empty
                        notifyError(returnValue, exception.getMessage());
                    } else {
                        notifyError(returnValue, exception.getMessage());
                    }
                    service.shutdownNow();
                } else {
                    returnValue = resultHandler.getExitValue();
                    logger.info("[EXECUTOR] FINISH: {}", returnValue);
                    notifyFinish(resultHandler.getExitValue());
                }
            } catch (IllegalStateException e) {
                returnValue = -1;
                logger.warn("[EXECUTOR] INTERRUPTED: {}", e.getMessage());
                notifyError(returnValue, e.getMessage());
            }
        }
        if (!keepFileOnEnd) {
            internalDeleteOnEnd();
        }

        return returnValue;
    }

    protected abstract void internalDeleteOnEnd();

    private class ScriptExecuteWatchdog extends ExecuteWatchdog {

        public ScriptExecuteWatchdog() {
            super(ExecuteWatchdog.INFINITE_TIMEOUT);
        }

        private Process process;

        @Override
        public synchronized void start(Process process) {
            this.process = process;
            super.start(process);
        }

        @Override
        public synchronized void destroyProcess() {
            if (Util.isWindows()) {
                killWindowsProcess();
            } else {
                killLinuxProcess();
            }
            super.destroyProcess();
        }

        private void killWindowsProcess() {
            int pid = -1;
            try {
                pid = Util.getWindowsProcessId(process);
                if (pid != -1) {
                    CommandLine command = getWindowsKillCommand(pid);
                    DefaultExecutor executor = new DefaultExecutor();
                    executor.setStreamHandler(new PumpStreamHandler(System.out));
                    executor.execute(command);
                }
            } catch (Exception e) {
                logger.error("No process for pid {}", pid);
            }
        }

        private CommandLine getWindowsKillCommand(int pid) {
            CommandLine command = new CommandLine("taskkill");
            command.addArgument("/F");
            command.addArgument("/PID");
            command.addArgument("" + pid);
            command.addArgument("/T");
            return command;
        }

        private void killLinuxProcess() {
            try {
                int pid = Util.getLinuxProcessId(process);
                File file = new File(currentDir, "killer.run");
                FileUtils.write(file, getLinuxKillScript(pid));
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // e.printStackTrace();
                    logger.error(e.getMessage());
                }
                file.setExecutable(true);
                Executor.script(file).inService(Executor.newExecutor("Killer")).execAndWait();
            } catch (Exception e) {
                // e.printStackTrace();
                logger.error(e.getMessage());
            }
        }

        private String getLinuxKillScript(int pid) {
            StringBuilder sb = new StringBuilder();
            sb.append("#!/bin/bash");
            sb.append("\n\n");
            sb.append("for i in `ps h --ppid " + pid + " -o pid`;");
            sb.append("\n");
            sb.append("do");
            sb.append("\n");
            sb.append("kill -9 $i");
            sb.append("\n");
            sb.append("done");
            sb.append("\n");
            return sb.toString();
        }
    };

}
