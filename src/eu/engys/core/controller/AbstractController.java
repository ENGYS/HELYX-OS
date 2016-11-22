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

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_CANCEL_OPTION;

import java.io.File;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.actions.CommandException;
import eu.engys.core.controller.actions.DeleteMesh;
import eu.engys.core.executor.ExecutorTerminal;
import eu.engys.core.executor.TerminalManager;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.presentation.Action;
import eu.engys.core.presentation.ActionContainer;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.CaseParameters;
import eu.engys.core.project.CreateCaseDialog;
import eu.engys.core.project.Model;
import eu.engys.core.project.Project200To210Converter;
import eu.engys.core.project.Project210To240Converter;
import eu.engys.core.project.ProjectFolderAnalyzer;
import eu.engys.core.project.ProjectFolderAnalyzer.WhenInDoubt;
import eu.engys.core.project.ProjectFolderStructure;
import eu.engys.core.project.ProjectReader;
import eu.engys.core.project.ProjectWriter;
import eu.engys.core.project.SolverState;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.mesh.MeshInfo;
import eu.engys.core.project.mesh.MeshInfoWriter;
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

    public static final String SOLVER_RUN = "solver.run";
    public static final String SOLVER_RUN_EDIT = "solver.run.edit";

    public static final String SOLVER_STOP = "solver.stop";
    public static final String REFRESH_ONCE = "solver.refresh.once";
    public static final String RUN_ALL = "solver.run.all";

    public static final String INITIALISE_SCRIPT = "initialise.fields";
    public static final String INITIALISE_SCRIPT_EDIT = "initialise.fields.edit";

    public static final String MESH_CREATE = "mesh.create";
    public static final String MESH_CREATE_EDIT = "mesh.create.edit";
    public static final String MESH_CHECK = "mesh.check";
    public static final String MESH_CHECK_EDIT = "mesh.check.edit";
    public static final String MESH_DELETE = "mesh.delete";
    public static final String MESH_STRETCH = "mesh.stretch";
    public static final String DECOMPOSE = "decompose";

    public static final String OPEN_RUN_MODE = "application.connection.window";
    public static final String OPEN_PARAMETERS_MANAGER = "application.parameters.manager";
    public static final String SAVE_SCREENSHOT = "save.screenshot";

    public static final String STOP_SOLVER_LABEL = "Stop Solver";
    public static final String KILL_SOLVER_LABEL = "Kill Solver";

    public static final String STOP_EXECUTION = "Stop Execution";
    public static final String KILL_PROCESS = "Kill Process";

    public static final String CANCEL = "Cancel";
    public static final String CONTINUE_IN_BATCH = "Continue in Batch";

    public static final String PARALLEL_WORKS = "application.parallel.works";
    
    protected static final Icon EXIT_BIG_ICON = ResourcesUtil.getIcon("application.exit.big.icon");
    protected static final String EXIT_LABEL = ResourcesUtil.getString("application.exit.label");

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    protected final Model model;
    protected final Set<ApplicationModule> modules;
    protected final ProjectReader reader;
    protected final ProjectWriter writer;
    protected final ProgressMonitor monitor;
    protected final ScriptFactory scriptFactory;
    protected ControllerListener listener;
    protected CellZonesBuilder cellZonesBuilder;

    public static boolean isServer = false;

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
    public void createCase(CaseParameters params, OpenOptions oo) {
        if (params == null) {
            params = caseParametersOrNull();
        }

        if (params.getBaseDir() != null) {
            newCaseInAThread(params, oo);
        }
    }

    private CaseParameters caseParametersOrNull() {
        final CaseParameters p = new CaseParameters();

        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                final CreateCaseDialog createCaseDialog = new CreateCaseDialog();
                createCaseDialog.showDialog();
                if (createCaseDialog.isOK()) {
                    ActionManager.getInstance().invoke("application.startup.hide");
                    p.setBaseDir(createCaseDialog.getParameters().getBaseDir());
                    p.setnHierarchy(createCaseDialog.getParameters().getnHierarchy());
                    p.setnProcessors(createCaseDialog.getParameters().getnProcessors());
                    p.setParallel(createCaseDialog.getParameters().isParallel());
                }
            }
        });

        return p;
    }

    private void newCaseInAThread(final CaseParameters params, final OpenOptions oo) {
        monitor.setTotal(10);
        monitor.start(String.format("Creating %s", params), false, new Runnable() {
            @Override
            public void run() {
                create(params, oo);
                PrefUtil.putFile(PrefUtil.WORK_DIR, model.getProject().getBaseDir().getParentFile());
                if (oo != null && oo.getFilesToImport() != null) {
                    importFiles(oo.getFilesToImport());
                }
                monitor.end();
            }
        });
    }

    @Override
    public void create(final CaseParameters params, final OpenOptions oo) {
        if (listener != null) {
            listener.beforeNewCase();
        }
        clearModel();
        writer.create(params);
        if (listener != null) {
            listener.afterNewCase(oo != null ? oo.isLoadMesh() : true);
        }
    }

    /*
     * OPEN
     */

    @Override
    public void openCase(OpenOptions oo) {
        if (oo == null) {
            oo = fileToOpenOrNull();
        }
        if (oo != null && oo.getFile() != null) {
            openInAThread(oo);
        }
    }

    private OpenOptions fileToOpenOrNull() {
        OpenFile r = new OpenFile();
        ExecUtil.invokeAndWait(r);
        return r.getOpenOptions();
    }

    class OpenFile implements Runnable {

        private OpenOptions openOptions;

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
                    openOptions = OpenOptions.file(selectedCase, OpenMode.CHECK_FOLDER_ASK_USER).loadMesh(options.loadMesh());
                    ActionManager.getInstance().invoke("application.startup.hide");
                    return;
                }
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), selectedCase + "\n appears not to be a valid case folder", "File System Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        public OpenOptions getOpenOptions() {
            return openOptions;
        }
    }

    private void openInAThread(final OpenOptions oo) {
        monitor.setTotal(10);
        monitor.start("Open " + oo.getFile().getName(), false, new Runnable() {
            @Override
            public void run() {
                open(oo);
                monitor.end();
            }
        });
    }

    @Override
    public void open(OpenOptions oo) {
        File file = oo.getFile();
        OpenMode mode = oo.getMode();

        final File baseDir = file.isAbsolute() ? file : file.getAbsoluteFile();
        logger.debug("OPEN file {}", baseDir.getAbsolutePath());

        if (listener != null) {
            listener.beforeLoadCase();
        }
        clearModel();

        switch (mode) {
            case SERIAL:
                model.setProject(openFOAMProject.newSerialProject(baseDir));
                break;
            case PARALLEL:
                model.setProject(openFOAMProject.newParallelProject(baseDir));
                break;
            case CURRENT_SETTINGS:
            case CHECK_FOLDER_ASK_USER:
                ProjectFolderStructure structure1 = new ProjectFolderAnalyzer(baseDir, monitor).checkAll(WhenInDoubt.ASK_USER);
                model.setProject(structure1.isParallel() ? openFOAMProject.newParallelProject(baseDir, structure1.getProcessors()) : openFOAMProject.newSerialProject(baseDir));
                break;
            case CHECK_FOLDER_PARALLEL:
                ProjectFolderStructure structure2 = new ProjectFolderAnalyzer(baseDir, monitor).checkAll(WhenInDoubt.READ_PARALLEL);
                model.setProject(structure2.isParallel() ? openFOAMProject.newParallelProject(baseDir, structure2.getProcessors()) : openFOAMProject.newSerialProject(baseDir));
                break;
            case MESH_ONLY:
                break;
            default:
                break;
        }

        new Project200To210Converter(model.getProject(), cellZonesBuilder).convert();
        new Project210To240Converter(model.getProject()).convert();

        reader.read();

        if (listener != null) {
            listener.afterLoadCase(oo.isLoadMesh());
        }

        logger.debug("OPEN file {} done.", baseDir.getName());
    }

    protected abstract void importFiles(final File[] stlFiles);
    
    @Override
    public void reopen(OpenMode mode) {
        File baseDir = model.getProject().getBaseDir();
        int np = model.getProject().getProcessors();
        boolean parallel = model.getProject().isParallel();

        logger.debug("REOPEN file {} in {} mode", baseDir.getAbsolutePath(), mode);
        if (listener != null) {
            listener.beforeReopenCase();
        }

        if (mode == OpenMode.MESH_ONLY) {
            reader.readMesh();
            if (listener != null) {
                listener.afterReopenCase();
            }
            return;
        }
        clearModel();

        switch (mode) {
            case SERIAL:
                model.setProject(openFOAMProject.newSerialProject(baseDir));
                break;
            case PARALLEL:
                model.setProject(openFOAMProject.newParallelProject(baseDir));
                break;
            case CHECK_FOLDER_ASK_USER:
                ProjectFolderStructure structure1 = new ProjectFolderAnalyzer(baseDir, monitor).checkAll(WhenInDoubt.ASK_USER);
                model.setProject(structure1.isParallel() ? openFOAMProject.newParallelProject(baseDir, structure1.getProcessors()) : openFOAMProject.newSerialProject(baseDir));
                break;
            case CHECK_FOLDER_PARALLEL:
                ProjectFolderStructure structure2 = new ProjectFolderAnalyzer(baseDir, monitor).checkAll(WhenInDoubt.READ_PARALLEL);
                model.setProject(structure2.isParallel() ? openFOAMProject.newParallelProject(baseDir, structure2.getProcessors()) : openFOAMProject.newSerialProject(baseDir));
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

    @Override
    public void reopenCase(final OpenMode options) {
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

    private void saveInAThread(final File baseDir) {
        monitor.info("");
        monitor.start("Save: " + baseDir.getAbsolutePath(), false, new Runnable() {
            @Override
            public void run() {
                save(baseDir);
                monitor.end();
            }
        });
        if (ActionManager.getInstance().contains(SAVE_SCREENSHOT)) {
            ActionManager.getInstance().get(SAVE_SCREENSHOT).actionPerformed(null);
        }
    }

    @Override
    public void save(File baseDir) {
        if (baseDir == null) {
            baseDir = model.getProject().getBaseDir();
        }
        logger.debug("Save file {}", baseDir.getAbsolutePath());
        if (listener != null) {
            listener.beforeSaveCase();
        }
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

    @Action(key = MESH_DELETE)
    public void deleteMesh() {
        int retVal = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), "This action will delete the existing mesh.\nContinue?", "Delete Mesh", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (retVal == JOptionPane.YES_OPTION) {
            saveInAThread(model.getProject().getBaseDir());
            new DeleteMesh(model, this).executeClient();
            model.getMesh().setMeshInfo(new MeshInfo());
            new MeshInfoWriter(model).write();
            model.getProject().getSystemFolder().writeProjectDict(null);
            reopenCase(OpenMode.CURRENT_SETTINGS);
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
    public void createReport(ExecutorTerminal terminal) {
    }

    @Override
    public void clearModel() {
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
    public boolean allowActionsOnRunning(boolean shouldAskConfirmation) {
        String exitMessage = EXIT_LABEL + " " + ApplicationInfo.getName() + "?";

        boolean thereIsACaseLoaded = model != null && model.getSolverModel() != null;
        if (thereIsACaseLoaded) {
            SolverState solverState = model.getSolverModel().getServerState().getSolverState();
            if (solverState.isDoingSomething()) {
                return handleExitOnRunning();
            } else {// server not running
                if (shouldAskConfirmation) {
                    int option = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), exitMessage, EXIT_LABEL, OK_CANCEL_OPTION, INFORMATION_MESSAGE, EXIT_BIG_ICON);
                    if (option == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
            }
        } else if (shouldAskConfirmation) {
            int option = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), exitMessage, EXIT_LABEL, OK_CANCEL_OPTION, INFORMATION_MESSAGE, EXIT_BIG_ICON);
            if (option == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }

    protected boolean handleExitOnRunning() {
        Object[] options = new Object[] { KILL_PROCESS, CONTINUE_IN_BATCH, CANCEL };
        String message = ApplicationInfo.getName() + " is running. Select an action to perform.";
        String title = ApplicationInfo.getName();

        int option = JOptionPane.showOptionDialog(UiUtil.getActiveWindow(), message, title, YES_NO_CANCEL_OPTION, QUESTION_MESSAGE, null, options, options[0]);

        if (getClient() != null && option == JOptionPane.YES_OPTION) {
            getClient().killCommand(Command.ANY);
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
    public TerminalManager getTerminalManager() {
        return null;
    }

    @Override
    public ParserView getResidualView() {
        return null;
    }

}
