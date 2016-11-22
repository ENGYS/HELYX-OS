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

import static javax.swing.JOptionPane.ERROR_MESSAGE;

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
import eu.engys.core.controller.actions.DecomposeCaseAction;
import eu.engys.core.controller.actions.InitialiseFields;
import eu.engys.core.controller.actions.RunCommand;
import eu.engys.core.controller.actions.StandardInitialiseFields;
import eu.engys.core.controller.actions.StandardRunCase;
import eu.engys.core.controller.actions.StandardRunMesh;
import eu.engys.core.executor.Executor;
import eu.engys.core.executor.TerminalManager;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.parameters.Parameters;
import eu.engys.core.presentation.Action;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.core.project.ProjectReader;
import eu.engys.core.project.ProjectWriter;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.core.project.state.ServerState;
import eu.engys.core.project.system.monitoringfunctionobjects.ParserView;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.core.project.zero.ZeroFileManager;
import eu.engys.core.project.zero.cellzones.CellZonesBuilder;
import eu.engys.gui.casesetup.fields.StandardInitialisations;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.EventManager.Event;
import eu.engys.gui.events.EventManager.GenericEventListener;
import eu.engys.gui.events.application.ApplicationEvent;
import eu.engys.gui.events.application.BaseMeshTypeChangedEvent;
import eu.engys.gui.events.application.OpenMonitorEvent;
import eu.engys.gui.events.view3D.AddSurfaceEvent;
import eu.engys.gui.solver.postprocessing.ParsersHandler;
import eu.engys.gui.solver.postprocessing.ParsersViewHandler;
import eu.engys.gui.solver.postprocessing.panels.residuals.ResidualsView;
import eu.engys.parallelworks.CloudPanel;
import eu.engys.parallelworks.ParallelWorksClient;
import eu.engys.parallelworks.ParallelWorksData;
import eu.engys.parallelworks.actions.ParallelWorksInitialiseFields;
import eu.engys.parallelworks.actions.ParallelWorksRunCase;
import eu.engys.parallelworks.actions.ParallelWorksRunMesh;
import eu.engys.util.ApplicationInfo;
import eu.engys.util.PrefUtil;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.progress.SilentMonitor;
import eu.engys.util.ui.ScriptEditor;
import eu.engys.util.ui.UiUtil;

public class HelyxOSController extends AbstractController implements GenericEventListener, ParsersManager {

    private static final String PARALLEL_WORKS_RESOURCES_ERROR_TITLE = "Remote Error";
    private static final String PARALLEL_WORKS_RESOURCES_ERROR_MESSAGE = "No Compute Resources Available - Please Start your Resources on Parallel Works";
    
    private static final long TIMER_INITIAL_DELAY = 500L;
    private static final long TIMER_REFRESH_RATE = 1000L;

    private static final Logger logger = LoggerFactory.getLogger(HelyxOSController.class);
    private ParserView residualsView;
    private RunCommand command;

    private Timer timer;
    private ParsersHandler parsersHandler;
    private ParsersViewHandler viewHandler;
    private ThreadPoolExecutor executor;
    private TerminalManager terminalManager;

    @Inject
    public HelyxOSController(Model model, CellZonesBuilder cellZonesBuilder, Set<ApplicationModule> modules, ProjectReader reader, ProjectWriter writer, CellZonesBuilder zonesBuilder, ProgressMonitor monitor, ScriptFactory scriptFactory) {
        super(model, modules, reader, writer, zonesBuilder, monitor, scriptFactory);
        this.residualsView = new ResidualsView(model, modules, monitor);
        EventManager.registerEventListener(this, ApplicationEvent.class);
        ActionManager.getInstance().parseActions(this);
        this.terminalManager = new TerminalManager();
    }

    @Override
    public TerminalManager getTerminalManager() {
        return terminalManager;
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

    @Action(key = MESH_CREATE, checkEnv = true)
    public void runMesh() {
        save(model.getProject().getBaseDir());

        if (PrefUtil.isRunningOnCloud()) {
            if (ParallelWorksClient.getInstance().isPoolOn()) {
                command = new ParallelWorksRunMesh(model, this, scriptFactory, monitor);
            } else {
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), PARALLEL_WORKS_RESOURCES_ERROR_MESSAGE, PARALLEL_WORKS_RESOURCES_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            command = new StandardRunMesh(model, this, scriptFactory);
        }
        command.beforeExecute();
        command.executeClient();
    }

    @Action(key = MESH_CHECK, checkEnv = true)
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
                reopen(OpenMode.CURRENT_SETTINGS);
                monitor.end();
            }
        });
    }

    @Action(key = INITIALISE_SCRIPT, checkEnv = true)
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

        StandardInitialisations initialisations = new StandardInitialisations(model, monitor);
        initialisations.initializeFields();

        model.getProject().getZeroFolder().write(model, cellZonesBuilder, modules, initialisations, new SilentMonitor());
        if (model.getFields().hasCellSetInitialisationField() || model.getFields().hasPotentialFlowInitialisationField()) {
            save(model.getProject().getBaseDir());

            if (PrefUtil.isRunningOnCloud()) {
                if (ParallelWorksClient.getInstance().isPoolOn()) {
                    JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), PARALLEL_WORKS_RESOURCES_ERROR_MESSAGE, PARALLEL_WORKS_RESOURCES_ERROR_TITLE, ERROR_MESSAGE);
                    command = new ParallelWorksInitialiseFields(model, this, scriptFactory, monitor);
                } else {
                    return;
                }
            } else {
                command = new StandardInitialiseFields(model, this, scriptFactory);
            }
            command.beforeExecute();
            command.executeClient();
        } else {
            // just clear polymesh
            ((ZeroFileManager) model.getProject().getZeroFolder().getFileManager()).removeNonZeroDirs("0");
        }
    }

    /*
     * DECOMPOSE
     */
    @Action(key = DECOMPOSE)
    public void decomposeCase() {
        new DecomposeCaseAction(model, this, true).executeClient();
    }

    /*
     * Parallel Works
     */
    @Action(key = PARALLEL_WORKS)
    public void openCloud() {
        new CloudPanel(model, this, monitor).showDialog(ParallelWorksData.fromPrefences());

    }

    /*
     * SOLVER
     */

    private void startTimer() {
        startParsers();
        viewHandler = new ParsersViewHandler(model, modules, this, residualsView);

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

        if (PrefUtil.isRunningOnCloud()) {
            if (ParallelWorksClient.getInstance().isPoolOn()) {
                command = new ParallelWorksRunCase(model, this, scriptFactory, monitor);
            } else {
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), PARALLEL_WORKS_RESOURCES_ERROR_MESSAGE, PARALLEL_WORKS_RESOURCES_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            command = new StandardRunCase(model, this, scriptFactory);
        }

        command.beforeExecute();
        command.executeClient();

        startTimer();
    }

    @Action(key = "solver.refresh.once")
    public void refreshOnce() {
        ParsersViewHandler viewHandler = new ParsersViewHandler(model, modules, this, residualsView);
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
        } catch (Exception e) {
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
     * IMPORT FILES
     */
    @Override
    protected void importFiles(final File[] stlFiles) {
        if (stlFiles != null) {
            monitor.setIndeterminate(false);
            monitor.start("Loading STL Files", false, new Runnable() {
                @Override
                public void run() {
                    for (File file : stlFiles) {
                        Stl stl = model.getGeometry().getFactory().readSTL(file, monitor);
                        model.getGeometry().addSurface(stl);
                        model.geometryChanged(stl);

                        EventManager.triggerEvent(this, new AddSurfaceEvent(stl));
                    }
                    monitor.end();
                }
            });
        }
    }

    /*
     * EDIT SCRIPTS
     */

    @Action(key = SOLVER_RUN_EDIT)
    public void editRunCaseScript() {
        ScriptEditor.getInstance().show(scriptFactory.getSolverScript(model).toPath(), scriptFactory.getDefaultSolverScript(model));
    }

    @Action(key = MESH_CREATE_EDIT)
    public void editRunMeshScript() {
        ScriptEditor.getInstance().show(scriptFactory.getMeshScript(model).toPath(), scriptFactory.getDefaultMeshScript(model));
    }

    @Action(key = MESH_CHECK_EDIT)
    public void editCheckMeshScript() {
        ScriptEditor.getInstance().show(scriptFactory.getCheckMeshScript(model).toPath(), scriptFactory.getDefaultCheckMeshScript(model));
    }

    @Action(key = INITIALISE_SCRIPT_EDIT)
    public void editInitialiseScript() {
        ScriptEditor.getInstance().show(scriptFactory.getInitialiseScript(model).toPath(), scriptFactory.getDefaultInitialiseScript(model));
    }

    /*
     * Parsers
     */
    @Override
    public void startParsers() {
        logger.info("[SERVER] START PARSERS");
        parsersHandler = new ParsersHandler(model, modules);
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
        ParsersHandler ph = new ParsersHandler(model, modules);
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
    public void open(OpenOptions oo) {
        super.open(oo);
        if (listener != null) {
            // client.loadState();
            listener.selectDestinationAndGo();
        }
    }

    @Override
    public void reopen(OpenMode options) {
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
    protected boolean handleExitOnRunning() {
        Object[] options = new Object[] { KILL_PROCESS, CANCEL };
        String message = ApplicationInfo.getName() + " is running. Select an action to perform.";
        String title = ApplicationInfo.getName();

        int option = JOptionPane.showOptionDialog(UiUtil.getActiveWindow(), message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (option == JOptionPane.YES_OPTION) {
            kill();
            return true;
        }
        return false;
    }

    @Override
    public void applyParameters(Parameters parameters) {
    }

}
