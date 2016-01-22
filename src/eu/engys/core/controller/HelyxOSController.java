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

package eu.engys.core.controller;

import static eu.engys.core.project.system.SetFieldsDict.CELL_SET_KEY;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import eu.engys.core.controller.actions.CheckMesh;
import eu.engys.core.controller.actions.InitialiseFields;
import eu.engys.core.controller.actions.RunCommand;
import eu.engys.core.controller.actions.StandardInitialiseFields;
import eu.engys.core.controller.actions.StandardRunCase;
import eu.engys.core.controller.actions.StandardRunMesh;
import eu.engys.core.controller.actions.TimeoutException;
import eu.engys.core.executor.Executor;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.presentation.Action;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.core.project.ProjectReader;
import eu.engys.core.project.ProjectWriter;
import eu.engys.core.project.state.ServerState;
import eu.engys.core.project.system.monitoringfunctionobjects.ParserView;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.core.project.zero.cellzones.CellZonesBuilder;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.gui.casesetup.fields.StandardInitialisations;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.EventManager.Event;
import eu.engys.gui.events.EventManager.GenericEventListener;
import eu.engys.gui.events.application.ApplicationEvent;
import eu.engys.gui.events.application.BaseMeshTypeChangedEvent;
import eu.engys.gui.events.application.OpenMonitorEvent;
import eu.engys.gui.solver.postprocessing.ParsersHandler;
import eu.engys.gui.solver.postprocessing.ParsersViewHandler;
import eu.engys.gui.solver.postprocessing.panels.residuals.ResidualsView;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ScriptEditor;
import eu.engys.util.ui.UiUtil;

public class HelyxOSController extends AbstractController implements GenericEventListener, ParsersManager {

    private static final long TIMER_INITIAL_DELAY = 500L;
    private static final long TIMER_REFRESH_RATE = 1000L;

    private static final Logger logger = LoggerFactory.getLogger(HelyxOSController.class);
    private ResidualsView residualsView;
    private RunCommand command;

    private Timer timer;
    private ParsersHandler parsersHandler;
    private ParsersViewHandler viewHandler;
    private ThreadPoolExecutor executor;

    @Inject
    public HelyxOSController(Model model, CellZonesBuilder cellZonesBuilder, Set<ApplicationModule> modules, ProjectReader reader, ProjectWriter writer, CellZonesBuilder zonesBuilder, ProgressMonitor monitor, ScriptFactory scriptFactory) {
        super(model, modules, reader, writer, zonesBuilder, monitor, scriptFactory);
        this.residualsView = new ResidualsView(model, monitor);
        EventManager.registerEventListener(this, ApplicationEvent.class);
        ActionManager.getInstance().parseActions(this);
    }

    @Override
    public void eventTriggered(Object obj, Event e) {
        logger.info("Event triggered {}", e.getClass().getName());
        if (e instanceof OpenMonitorEvent) {
            monitor.start(null);
        } else if (e instanceof BaseMeshTypeChangedEvent) {
            scriptFactory.getMeshScript(model).delete();
        }
    }

    /*
     * MESH
     */

    @Action(key = "mesh.create", checkEnv = true)
    public void runMesh() {
        save(model.getProject().getBaseDir());

        command = new StandardRunMesh(model, this, scriptFactory);
        command.beforeExecute();
        command.executeClient();
    }

    @Action(key = "mesh.check", checkEnv = true)
    public void checkMesh() {
        model.getProject().getSystemFolder().write(model, null);
        RunCommand checkMesh = new CheckMesh(model, this, scriptFactory);
        checkMesh.beforeExecute();
        checkMesh.executeClient();
    }

    /*
     * CASE SETUP
     */
    @Override
    public void setupCase() {
        /* JUST REOPEN IN THIS CASE */
        monitor.start("Open " + model.getProject().getBaseDir(), false, new Runnable() {
            @Override
            public void run() {
                reopen(OpenOptions.CURRENT_SETTINGS);
                monitor.end();
            }
        });
    }

    @Action(key = "initialise.fields", checkEnv = true)
    public void initialiseFields() {
        if (model.getProject().getZeroFolder().hasNonZeroTimeFolders()) {
            int retVal = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), "This action will DELETE all the time folders. Continue?", "Initialise Fields", JOptionPane.YES_NO_OPTION);
            if (retVal == JOptionPane.YES_OPTION) {
                model.getProject().getSystemFolder().getControlDict().startFromZero();
                model.projectChanged();
            } else {
                return;
            }
        }
        monitor.start(InitialiseFields.ACTION_NAME, false, new Runnable() {
            @Override
            public void run() {
                save(null);
                monitor.end();
            }
        });

        new StandardInitialisations(model, cellZonesBuilder, modules).initializeFields();
        if (cellSetFieldPresent()) {
            save(model.getProject().getBaseDir());

            command = new StandardInitialiseFields(model, this, scriptFactory);
            command.beforeExecute();
            command.executeClient();
        }
    }

    public boolean cellSetFieldPresent() {
        for (Field f : model.getFields().values()) {
            if (f.getInitialisationType().equals(CELL_SET_KEY)) {
                return true;
            }
        }
        return false;
    }

    /*
     * SOLVER
     */

    private void startTimer() {
        startParsers();
        viewHandler = new ParsersViewHandler(model, this, residualsView);

        executor = Executor.newExecutor("ServerStateMonitor");
        if (timer == null) {
            timer = new Timer("- Client Monitor -", true);
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
        ServerState state = model.getSolverModel().getServerState();
        Command c = state.getCommand();
        logger.debug("RUN TIMER, STATE IS {}", state.getSolverState());
        if (c.isRunCase()) {
            viewHandler.serverChanged(state);
            if (state.getSolverState().isFinished() || state.getSolverState().isError()) {
                stopTimer();
            }
        }
    }

    private void stopTimer() {
        logger.debug("STOP TIMER");
        if (viewHandler != null) {
            viewHandler = null;
        }
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

    @Action(key = "solver.run", checkEnv = true)
    public void runCase() {
        save(model.getProject().getBaseDir());

        listener.beforeRunCase();
        command = new StandardRunCase(model, this, scriptFactory);
        command.beforeExecute();
        command.executeClient();

        startTimer();
    }

    @Action(key = "solver.refresh.once")
    public void refreshOnce() {
        ParsersViewHandler viewHandler = new ParsersViewHandler(model, this, residualsView);
        viewHandler.refreshOnce();
    }

    @Override
    @Action(key = "solver.stop")
    public void stopCase() {
        monitor.setIndeterminate(true);
        monitor.start("Stop Solver", false, new Runnable() {
            @Override
            public void run() {
                _stopCase();
                monitor.info("DONE");
                monitor.end();
            }

        });
    }

    private void _stopCase() {
        try {
            command.stop();
        } catch (TimeoutException e) {
            Object[] options = new Object[] { "Wait", "Kill Process" };
            int option = JOptionPane.showOptionDialog(monitor.getDialog(), "Solver has not finished yet.\nSelect an action to perform.", "Stop Execution", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (timer != null) {
                if (option == JOptionPane.YES_OPTION) {
                    _stopCase();
                } else {
                    _kill();
                }

            }
        }
    }

    @Override
    public void kill() {
        monitor.setIndeterminate(true);
        monitor.start("Killing Command", false, new Runnable() {
            @Override
            public void run() {
                _kill();
                monitor.info("DONE");
                monitor.end();
            }

        });
    }

    private void _kill() {
        command.kill();
    }

    /*
     * EDIT SCRIPTS
     */

    @Action(key = "solver.run.edit")
    public void editRunCaseScript() {
        ScriptEditor.getInstance().show(scriptFactory.getSolverScript(model).toPath(), scriptFactory.getDefaultSolverScript(model));
    }

    @Action(key = "mesh.create.edit")
    public void editRunMeshScript() {
        ScriptEditor.getInstance().show(scriptFactory.getMeshScript(model).toPath(), scriptFactory.getDefaultMeshScript(model));
    }

    @Action(key = "mesh.check.edit")
    public void editCheckMeshScript() {
        ScriptEditor.getInstance().show(scriptFactory.getCheckMeshScript(model).toPath(), scriptFactory.getDefaultCheckMeshScript(model));
    }

    @Action(key = "initialise.fields.edit")
    public void editInitialiseScript() {
        ScriptEditor.getInstance().show(scriptFactory.getInitialiseScript(model).toPath(), scriptFactory.getDefaultInitialiseScript(model));
    }

    /*
     * Parsers
     */
    @Override
    public void startParsers() {
        logger.info("[SERVER] START PARSERS");
        parsersHandler = new ParsersHandler(model);
        parsersHandler.deleteUselessLogFiles();
    }

    @Override
    public void endParsers() {
        logger.info("[SERVER] END PARSERS");
        if (parsersHandler != null) {
            parsersHandler.endParsers();
            parsersHandler = null;
        }
    }

    @Override
    public List<TimeBlocks> updateParser(String foName) {
        logger.info("[SERVER] UPDATE PARSERS");
        return parsersHandler.refreshParsersForFunctionObject(foName);
    }

    @Override
    public List<TimeBlocks> updateParserOnce(String foName) {
        logger.info("[SERVER] UPDATE PARSERS ONCE");
        ParsersHandler ph = new ParsersHandler(model);
        return ph.refreshOnceForFunctionObject(foName);
    }

    /*
     * OTHER
     */

    @Override
    public ParserView getResidualView() {
        return residualsView;
    }

    @Override
    public void open(File baseDir) {
        super.open(baseDir);
        if (listener != null) {
            // client.loadState();
            listener.selectDestinationAndGo();
        }
    }

    @Override
    public void reopen(OpenOptions options) {
        if (listener != null) {
            listener.saveLocation();
        }

        super.reopen(options);

        if (listener != null) {
            // client.loadState();
            listener.goToLocation();
        }

    }

    @Override
    public Client getClient() {
        return null;
    }

    @Override
    public Server getServer() {
        return null;
    }

    @Override
    public boolean isDemo() {
        return false;
    }

    @Override
    protected boolean handleExitOnSolverRunning() {
        Object[] options = new Object[] { STOP_SOLVER, KILL_SOLVER, CANCEL };
        int option = JOptionPane.showOptionDialog(UiUtil.getActiveWindow(), "Solver is Running. Select an action to perform.", STOP_SOLVER, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (option == JOptionPane.YES_OPTION) {
            stopCase();
            return true;
        } else if (option == JOptionPane.NO_OPTION) {
            kill();
            return true;
        } else {
            return false;
        }
    }
}
