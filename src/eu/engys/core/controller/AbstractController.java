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

import java.io.File;
import java.rmi.RemoteException;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.Arguments;
import eu.engys.core.controller.actions.CommandException;
import eu.engys.core.controller.actions.DeleteMesh;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.presentation.Action;
import eu.engys.core.presentation.ActionContainer;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.CaseParameters;
import eu.engys.core.project.InvalidProjectException;
import eu.engys.core.project.Model;
import eu.engys.core.project.Project200To210Converter;
import eu.engys.core.project.ProjectFolderAnalyzer;
import eu.engys.core.project.ProjectReader;
import eu.engys.core.project.ProjectWriter;
import eu.engys.core.project.SolverState;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.system.monitoringfunctionobjects.ParserView;
import eu.engys.core.project.zero.cellzones.CellZonesBuilder;
import eu.engys.util.ApplicationInfo;
import eu.engys.util.PrefUtil;
import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.filechooser.HelyxFileChooser;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;

public abstract class AbstractController implements Controller, ActionContainer {

    public static final String STOP_SOLVER = "Stop Solver";
    public static final String KILL_SOLVER = "Kill Solver";

    public static final String STOP_EXECUTION = "Stop Execution";
    public static final String KILL_PROCESS = "Kill Process";
    
    public static final String STOP_FIELDS_INITIALISATION = "Stop Fields Initialisation";
    public static final String STOP_MESH_GENERATOR = "Stop Mesh Generator";
    
    public static final String CANCEL = "Cancel";
    public static final String CONTINUE_IN_BATCH = "Continue in Batch";

    private static final Icon EXIT_BIG_ICON = ResourcesUtil.getIcon("application.exit.big.icon");
    private static final String EXIT_LABEL = ResourcesUtil.getString("application.exit.label");
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    protected final Model model;
    protected final Set<ApplicationModule> modules;
    protected final ProjectReader reader;
    protected final ProjectWriter writer;
    protected final ProgressMonitor monitor;
    protected final ScriptFactory scriptFactory;
    protected ControllerListener listener;
    protected CellZonesBuilder cellZonesBuilder;

    public AbstractController(Model model, Set<ApplicationModule> modules, ProjectReader reader, ProjectWriter writer, CellZonesBuilder cellZonesBuilder, ProgressMonitor monitor, ScriptFactory scriptFactory) {
        this.cellZonesBuilder = cellZonesBuilder;
        logger.info("Loading {}", getClass().getSimpleName());
        this.model = model;
        this.modules = modules;
        this.reader = reader;
        this.writer = writer;
        this.monitor = monitor;
        this.scriptFactory = scriptFactory;
    }

    /*
     * NEW CASE
     */

    @Override
    public void createCase(CaseParameters params) {
        if (params != null) {
            newCaseInAThread(params);
        }
    }

    private void newCaseInAThread(final CaseParameters params) {
        monitor.setTotal(10);
        monitor.start(String.format("Creating %s", params), false, new Runnable() {
            @Override
            public void run() {
                create(params);
                PrefUtil.putFile(PrefUtil.WORK_DIR, model.getProject().getBaseDir().getParentFile());
                monitor.end();
            }
        });
    }

    @Override
    public void create(final CaseParameters params) {
        if (listener != null) {
            listener.beforeNewCase();
        }
        clearModel();
        writer.create(params);
        if (listener != null) {
            listener.afterNewCase();
        }
    }

    /*
     * OPEN
     */

    @Override
    public void openCase(File file) {
        if (file == null) {
            file = fileToOpenOrNull();
        }
        if (file != null) {
            ActionManager.getInstance().invoke("application.startup.hide");
            openInAThread(file);
        }
        Arguments.load3Dgeometry = true;
        Arguments.load3Dmesh = true;
    }

    private File fileToOpenOrNull() {
        final File[] openFile = new File[1];
        Runnable r = new Runnable() {
            @Override
            public void run() {
                File workDir = PrefUtil.getWorkDir(PrefUtil.WORK_DIR);
                HelyxFileChooser fileChooser = new HelyxFileChooser(workDir.getAbsolutePath());
                fileChooser.setTitle("Open");
                fileChooser.setSelectionMode(SelectionMode.DIRS_AND_ARCHIVES);

                View3DOptions options = new View3DOptions();
                ReturnValue returnValue = fileChooser.showOpenDialog(options);
                if (returnValue.isApprove()) {
                    File selectedCase = fileChooser.getSelectedFile();
                    if (isSuitable(selectedCase)) {
                        PrefUtil.putFile(PrefUtil.WORK_DIR, selectedCase.getParentFile());
                        Arguments.load3Dgeometry = true;
                        Arguments.load3Dmesh = options.loadMesh();
                        openFile[0] = selectedCase;
                        return;
                    }
                    JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), selectedCase + "\n appears not to be a valid case folder", "File System Error", JOptionPane.ERROR_MESSAGE);
                }
                openFile[0] = null;
                return;
            }
        };
        ExecUtil.invokeAndWait(r);
        return openFile[0];
    }

    private void openInAThread(final File file) {
        monitor.setTotal(10);
        monitor.start("Open " + file.getName(), false, new Runnable() {
            @Override
            public void run() {
                open(file);
                monitor.end();
            }
        });
    }

    @Override
    public void open(File file) {
        final File baseDir = file.isAbsolute() ? file : file.getAbsoluteFile();
        logger.debug("OPEN file {}", baseDir.getAbsolutePath());
        if (listener != null) {
            listener.beforeLoadCase();
        }
        clearModel();

        model.setProject(openFOAMProject.createProject(baseDir, monitor));

        new Project200To210Converter(model.getProject(), cellZonesBuilder).convert();

        _read();

        if (listener != null) {
            listener.afterLoadCase();
        }

        logger.debug("OPEN file {} done.", baseDir.getName());
    }

    @Override
    public void reopen(OpenOptions options) {
        File baseDir = model.getProject().getBaseDir();
        int np = model.getProject().getProcessors();
        boolean parallel = model.getProject().isParallel();

        logger.debug("REOPEN file {} with option {}", baseDir.getAbsolutePath(), options);
        if (listener != null) {
            listener.beforeReopenCase();
        }

        if (options == OpenOptions.MESH_ONLY) {
            reader.readMesh();
            if (listener != null) {
                listener.afterReopenCase();
            }
            return;
        }
        clearModel();

        switch (options) {
        case SERIAL:
            model.setProject(openFOAMProject.newSerialProject(baseDir));
            break;
        case PARALLEL:
            model.setProject(openFOAMProject.newParallelProject(baseDir));
            break;
        case CHECK_FOLDER:
            model.setProject(openFOAMProject.createProject(baseDir, monitor));
            break;
        case CURRENT_SETTINGS:
            model.setProject(parallel ? openFOAMProject.newParallelProject(baseDir, np) : openFOAMProject.newSerialProject(baseDir));
            break;
        case MESH_ONLY:
            break;
        default:
            break;
        }

        reader.read();

        if (listener != null) {
            listener.afterReopenCase();
        }
        logger.debug("Open file {} done.", baseDir.getName());
    }

    private void _read() {
        try {
            reader.read();
        } catch (InvalidProjectException e) {
            logger.error(e.getMessage());
            JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), e.getMessage(), "Project error", JOptionPane.ERROR_MESSAGE);
            clearModel();
        }
    }

    @Override
    public void reopenCase(final OpenOptions options) {
        monitor.start("Reopen " + model.getProject().getBaseDir(), false, new Runnable() {
            @Override
            public void run() {
                reopen(options);
                monitor.end();
            }
        });
    }

    /*
     * SAVE
     */

    @Override
    public void saveCase(File file) {
        if (file == null) {
            file = fileToSaveOrNull();
        }
        if (file != null) {
            saveInAThread(file);
        }
    }

    protected void saveInAThread(final File baseDir) {
        monitor.info("");
        monitor.start("Save: " + baseDir.getAbsolutePath(), false, new Runnable() {
            @Override
            public void run() {
                save(baseDir);
                monitor.end();
            }
        });
    }

    @Override
    public void save(File baseDir) {
        if (baseDir == null) {
            baseDir = model.getProject().getBaseDir();
        }
        logger.debug("Save file {}", baseDir.getAbsolutePath());
        if (listener != null)
            listener.beforeSaveCase();
        writer.write(baseDir);
        writeScripts();
        if (listener != null) {
            listener.afterSaveCase();
        }
        logger.debug("Save file {} done.", baseDir.getName());
    }

    private File fileToSaveOrNull() {
        final File[] file = new File[1];
        Runnable r = new Runnable() {
            @Override
            public void run() {
                File workDir = PrefUtil.getWorkDir(PrefUtil.WORK_DIR);

                HelyxFileChooser fileChooser = new HelyxFileChooser(workDir.getAbsolutePath());
                if (model.getProject().getBaseDir().getAbsoluteFile().getParentFile().equals(workDir)) {
                    fileChooser.selectFile(model.getProject().getBaseDir());
                }
                fileChooser.setTitle("Save As");
                fileChooser.setSelectionMode(SelectionMode.DIRS_ONLY);

                ReturnValue returnValue = fileChooser.showSaveAsDialog();
                if (returnValue.isApprove()) {
                    File baseDir = fileChooser.getSelectedFile();
                    if (baseDir != null) {
                        if (baseDir.exists() && !baseDir.equals(model.getProject().getBaseDir())) {
                            int retVal = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), "Folder already exists. Continue anyway?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                            if (retVal == JOptionPane.NO_OPTION) {
                                file[0] = fileToSaveOrNull();
                                return;
                            }
                        }
                        PrefUtil.putFile(PrefUtil.WORK_DIR, baseDir.getParentFile());
                    }
                    file[0] = baseDir;
                    return;
                }
                file[0] = null;
                return;
            }
        };
        ExecUtil.invokeAndWait(r);
        return file[0];
    }

    /*
     * MESH
     */

    @Action(key = "mesh.delete")
    public void deleteMesh() {
        int retVal = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), "This action will delete the existing mesh.\nContinue?", "Delete Mesh", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (retVal == JOptionPane.YES_OPTION) {
            saveInAThread(model.getProject().getBaseDir());
            new DeleteMesh(model, this).executeClient();
            reopenCase(OpenOptions.CURRENT_SETTINGS);
        }
    }

    private void writeScripts() {
        scriptFactory.getMeshScript(model);
        scriptFactory.getInitialiseScript(model);
        scriptFactory.getSolverScript(model);
        if (model.getSolverModel().isQueue()) {
            scriptFactory.getQueueDriver(model);
            scriptFactory.getQueueLauncher(model);
        }
    }

    /*
     * OTHER
     */

    @Override
    public void createReport() {
    }

    protected void clearModel() {
        logger.info("--- CLEAR MODEL ---");
        model.init();
        model.setProject(null);
        System.gc();
    }

    @Override
    public ProjectReader getReader() {
        return reader;
    }

    @Override
    public ProjectWriter getWriter() {
        return writer;
    }

    public boolean isSuitable(File file) {
        return ProjectFolderAnalyzer.isSuitable(file);
    }

    @Override
    public boolean allowActionsOnRunning(boolean exit) {
        if (model != null && model.getSolverModel() != null) {
            SolverState solverState = model.getSolverModel().getServerState().getSolverState();
            if (solverState.isMeshing()) {
                return handleExitOnMeshRunning();
            } else if (solverState.isInitialising()) {
                return handleExitOnFieldsInitialising();
            } else if (solverState.isRunning()) {
                return handleExitOnSolverRunning();
            } else if (exit) {
                int option = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), EXIT_LABEL + " " + ApplicationInfo.getName() + "?", EXIT_LABEL, JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, EXIT_BIG_ICON);
                if (option == JOptionPane.CANCEL_OPTION) {
                    return false;
                } else {
                    if (getClient() != null && getClient().getServer() != null) {
                        shutdownServer();
                    }
                    return true;
                }
            } else if (getClient() != null && getClient().getServer() != null) {
                shutdownServer();
                return true;
            }
        } else if (exit) {// If no case has been loaded yet
            int option = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), EXIT_LABEL + " " + ApplicationInfo.getName() + "?", EXIT_LABEL, JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, EXIT_BIG_ICON);
            if (option == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }

    private void shutdownServer() {
        try {
            getClient().getServer().shutdown();
        } catch (RemoteException e) {
            logger.error("Error shutting down server: {}" + e.getMessage());
        } catch (Exception e) {
            logger.error("Error shutting down server: {}" + e.getMessage());
        }
    }

    protected boolean handleExitOnMeshRunning() {
        Object[] options = new Object[] { STOP_MESH_GENERATOR, CONTINUE_IN_BATCH, CANCEL };
        int option = JOptionPane.showOptionDialog(UiUtil.getActiveWindow(), "Mesh Generator is Running. Select an action to perform.", "Mesh Generator Running", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (getClient() != null && option == JOptionPane.YES_OPTION) {
            kill();
            return true;
        } else if (getClient() != null && option == JOptionPane.NO_OPTION) {
            getClient().goToBatch();
            return true;
        } else {
            return false;
        }
    }

    protected boolean handleExitOnFieldsInitialising() {
        Object[] options = new Object[] { STOP_FIELDS_INITIALISATION, CONTINUE_IN_BATCH, CANCEL };
        int option = JOptionPane.showOptionDialog(UiUtil.getActiveWindow(), "Fields Initialisation is Running. Select an action to perform.", "Fields Initialisation Running", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (getClient() != null && option == JOptionPane.YES_OPTION) {
            kill();
            return true;
        } else if (getClient() != null && option == JOptionPane.NO_OPTION) {
            getClient().goToBatch();
            return true;
        } else {
            return false;
        }
    }

    protected boolean handleExitOnSolverRunning() {
        Object[] options = new Object[] { STOP_SOLVER, CONTINUE_IN_BATCH, CANCEL };
        int option = JOptionPane.showOptionDialog(UiUtil.getActiveWindow(), "Solver is Running. Select an action to perform.", "Solver Running", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (getClient() != null && option == JOptionPane.YES_OPTION) {
            getClient().stopCommand(Command.ANY);
            return true;
        } else if (getClient() != null && option == JOptionPane.NO_OPTION) {
            getClient().goToBatch();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addListener(ControllerListener listener) {
        this.listener = listener;
    }

    @Override
    public ControllerListener getListener() {
        return listener;
    }

    @Override
    public void executeCommand(Command command) throws CommandException {
    }

    @Override
    public void executeCommands(Command... commands) throws CommandException {
    }

    @Override
    public boolean isRunningCommand() {
        return false;
    }

    @Override
    public String submitCommand(Command command) throws CommandException {
        return null;
    }

    @Override
    public ParserView getResidualView() {
        return null;
    }

}
