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
package eu.engys.gui.solver.postprocessing;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.Command;
import eu.engys.core.controller.Server;
import eu.engys.core.executor.Executor;
import eu.engys.core.executor.ExecutorError;
import eu.engys.core.project.SolverState;
import eu.engys.core.project.state.ServerState;
import eu.engys.util.PrefUtil;

public class ServerStateMonitor {

    private static final Logger logger = LoggerFactory.getLogger(ServerStateMonitor.class);

    private static final long TIMER_INITIAL_DELAY = 500L;
    private static final long TIMER_REFRESH_RATE = 1000L;

    private Timer timer;
    private Server server;

    private Map<Command, List<ServerListener>> listeners = new HashMap<>();
    private Map<Command, Map<SolverState, List<ServerListener>>> hooks = new HashMap<>();

    private ServerState endState;

    private ThreadPoolExecutor executor;

    public ServerStateMonitor() {
    }

    public void startMonitor(Server server) {
        logger.info(">>> START MONITOR");
        this.server = server;
        startTimer();
    }

    public void waitForFinished() {
        int wait_for_running_refresh_time = PrefUtil.getInt(PrefUtil.SERVER_WAIT_FOR_RUN_REFRESH_TIME, 2000);
        while (timer != null) {
            try {
                Thread.sleep(wait_for_running_refresh_time);
            } catch (InterruptedException e) {
            } finally {
            }
        }
    }

    private void startTimer() {
        executor = Executor.newExecutor("ServerStateMonitor");
        if (timer == null) {
            timer = new Timer("- Server Monitor -", true);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (executor.getQueue().isEmpty()) {
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            runTimer();
                        }
                    });
                } else {
                    logger.debug("Timer running...");
                }
            }
        }, TIMER_INITIAL_DELAY, TIMER_REFRESH_RATE);
    }

    private void runTimer() {
        if (server == null) {
            handleNoServer();
        } else {
            handleServerIsRunning();
        }
    }

    private void handleNoServer() {
        logger.info(">>> ERROR: NO SERVER");
        notifyServerStateChanged(new ServerState(Command.ANY, SolverState.ERROR));
    }

    private void handleServerIsRunning() {
        try {
            List<ServerState> remoteStates = server.getStates();
            if (remoteStates.isEmpty()) {
                logger.info(">>> NO SERVER STATES");
            }

            for (ServerState remoteState : remoteStates) {
                logger.info(">>> SERVER STATE: " + remoteState);

                if (remoteState.getSolverState().isStarted()) {
                    notifyServerStateChanged(remoteState);
                } else if (remoteState.getSolverState().isMeshing()) {
                    notifyServerStateChanged(remoteState);
                } else if (remoteState.getSolverState().isInitialising()) {
                    notifyServerStateChanged(remoteState);
                } else if (remoteState.getSolverState().isRunning()) {
                    notifyServerStateChanged(remoteState);
                } else if (remoteState.getSolverState().isFinished()) {
                    notifyServerStateChanged(remoteState);
                } else if (remoteState.getSolverState().isMeshed()) {
                    notifyServerStateChanged(remoteState);
                } else if (remoteState.getSolverState().isInitialised()) {
                    notifyServerStateChanged(remoteState);
                } else if (remoteState.getSolverState().isError()) {
                    notifyServerStateChanged(remoteState);
                } else {
                    logger.error(">>> UNKOWN STATE {]", remoteState.getSolverState());
                }

                // try { Thread.sleep(1000); } catch (InterruptedException e) {}
            }
        } catch (RemoteException e) {
            logger.info(">>> SERVER: ERROR (SERVER EXITED)");
            ServerState errorState = new ServerState(Command.ANY, SolverState.ERROR, new ExecutorError(-1, "Server Exited"));
            notifyServerStateChanged(errorState);
            stopTimer();
        }
    }

    private void notifyServerStateChanged(ServerState state) {
        // logger.info(">>> NOTIFY: {} ", state);
        Command command = state.getCommand();
        SolverState solverState = state.getSolverState();

        if (listeners.containsKey(command)) {
            for (ServerListener listener : listeners.get(command)) {
                listener.serverChanged(state);
            }
        } else if (command.equals(Command.ANY)) {
            for (Command c : listeners.keySet()) {
                for (ServerListener listener : listeners.get(c)) {
                    listener.serverChanged(state);
                }
            }
        } else if (listeners.containsKey(Command.ANY)) {
            for (ServerListener listener : listeners.get(Command.ANY)) {
                listener.serverChanged(state);
            }
        }

        if (hooks.containsKey(command)) {
            if (hooks.get(command).containsKey(solverState)) {
                for (ServerListener listener : hooks.get(command).get(solverState)) {
                    listener.serverChanged(state);
                }
            }
        } else if (command.equals(Command.ANY)) {
            for (Command c : hooks.keySet()) {
                if (hooks.get(c).containsKey(solverState)) {
                    for (ServerListener listener : hooks.get(c).get(solverState)) {
                        listener.serverChanged(state);
                    }
                }
            }
        } else if (hooks.containsKey(Command.ANY)) {
            if (hooks.get(Command.ANY).containsKey(solverState)) {
                for (ServerListener listener : hooks.get(Command.ANY).get(solverState)) {
                    listener.serverChanged(state);
                }
            }
        }

        if (endState != null) {
            if (endState.equals(state) || (state.getCommand().equals(endState.getCommand()) &&  state.getSolverState().isError())) {
                logger.info("STATE IS {}, END STATE IS {}", state.getSolverState(), endState);
                stopTimer();
            }
        }
    }

    public void stopTimer() {
        logger.debug("STOP TIMER");
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public CommandHook forCommand(Command command) {
        return new CommandHook(command);
    }

    /*
     * For test purposes only
     */
    public boolean isRunning() {
        return timer != null;
    }

    public class CommandHook {
        private Command command;

        public CommandHook(Command command) {
            this.command = command;
            if (!hooks.containsKey(command)) {
                hooks.put(command, new HashMap<SolverState, List<ServerListener>>());
            }
            if (!listeners.containsKey(command)) {
                listeners.put(command, new ArrayList<ServerListener>());
            }
        }

        public SolverHook when(SolverState state) {
            return new SolverHook(command, state);
        }

        public void forEachState(ServerListener listener) {
            listeners.get(command).add(listener);
        }
    }

    public class SolverHook {

        private SolverState state;
        private Command command;

        public SolverHook(Command command, SolverState state) {
            this.command = command;
            this.state = state;
            if (!hooks.get(command).containsKey(state)) {
                hooks.get(command).put(state, new ArrayList<ServerListener>());
            }
        }

        public void execute(ServerListener listener) {
            hooks.get(command).get(state).add(listener);
        }

        public void endTimer() {
            ServerStateMonitor.this.endState = new ServerState(command, state);
        }
    }

}
