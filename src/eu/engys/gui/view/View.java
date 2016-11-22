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
package eu.engys.gui.view;

import java.awt.BorderLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import eu.engys.application.AbstractApplication;
import eu.engys.core.controller.Controller;
import eu.engys.core.controller.Controller.OpenMode;
import eu.engys.core.controller.OpenOptions;
import eu.engys.core.executor.FileManagerSupport;
import eu.engys.core.executor.TerminalSupport;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.presentation.Action;
import eu.engys.core.presentation.ActionContainer;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.gui.MenuBar;
import eu.engys.gui.RecentItems;
import eu.engys.gui.StartPanel;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.EventManager.GenericEventListener;
import eu.engys.gui.events.application.ApplicationEvent;
import eu.engys.gui.events.view3D.GlassPaneEvent;
import eu.engys.gui.view3D.CanvasPanel;
import eu.engys.launcher.StartUpMonitor;
import eu.engys.util.ApplicationInfo;
import eu.engys.util.plaf.ILookAndFeel;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;

public class View extends JPanel implements Observer, ActionContainer, GenericEventListener {

    private static final Logger logger = LoggerFactory.getLogger(View.class);

    public static final String NEW_CASE = "application.create";
    public static final String OPEN_CASE = "application.open";
    public static final String RECENT_CASES = "application.recent";
    public static final String SAVE_CASE = "application.save";
    public static final String SAVE_AS_CASE = "application.saveAs";
    public static final String BROWSE_CASE = "application.browse.case";
    public static final String EXIT = "application.exit";
    public static final String OPEN_TERMINAL = "application.open.terminal";
    public static final String SPLIT_PANE_3D = "split.pane.3d";
    public static final String VIEW = "view";
    public static final String ELEMENT = "element";
    public static final String LOADING_VIEW = "Loading View";

    private final Model model;
    private final Controller controller;

    private MainPanel mainPanel;
    private final CanvasPanel canvasPanel;

    private final ILookAndFeel lookAndFeel;
    private final Set<ViewElement> viewElements;
    private final Set<ApplicationModule> modules;
    private final ProgressMonitor monitor;

    private JSplitPane splitPane;
    private MenuBar menuBar;
    private StatusBar statusBar;
    private ApplicationToolBar applicationToolBar;

    private JDialog startupDialog;
    private GlassLayerUI glassPane;

    @Override
    public boolean isDemo() {
        return controller.isDemo();
    }

    @Inject
    public View(Model model, Controller controller, CanvasPanel canvasPanel, ILookAndFeel lookAndFeel, Set<ViewElement> vElements, Set<ApplicationModule> modules, ProgressMonitor monitor) {
        super();
        this.model = model;
        this.controller = controller;
        this.viewElements = vElements;
        this.canvasPanel = canvasPanel;
        this.lookAndFeel = lookAndFeel;
        this.modules = modules;
        this.monitor = monitor;

        for (ViewElement element : viewElements) {
            controller.getReader().registerReader(element.getReader());
            controller.getWriter().registerWriter(element.getWriter());
        }

        controller.addListener(new DefaultControllerListener(model, controller, modules, this));
        model.addObserver(this);

        StartUpMonitor.info(LOADING_VIEW);
        logger.info(LOADING_VIEW);

        ActionManager.getInstance().parseActions(this);
        EventManager.registerEventListener(this, ApplicationEvent.class);
    }

    public void setProgressMonitorParent(JFrame frame) {
        this.monitor.setParent(frame);
    }

    public void layoutComponents() {
        menuBar = new MenuBar(this);
        statusBar = new StatusBar(controller);
        applicationToolBar = new ApplicationToolBar(model);

        mainPanel = new MainPanel(model, viewElements, controller.getTerminalManager(), monitor);

        mainPanel.layoutComponents();
        canvasPanel.layoutComponents();

        setLayout(new BorderLayout());
        setName(VIEW);

        glassPane = new GlassLayerUI();
        splitPane = new JSplitPane();
        splitPane.setName(SPLIT_PANE_3D);
        splitPane.setLeftComponent(new JLayer<>(mainPanel, glassPane));
        splitPane.setRightComponent(canvasPanel.getPanel());
        splitPane.setDividerLocation(lookAndFeel.getMainWidth());
        splitPane.setOneTouchExpandable(false);

        
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.add(applicationToolBar.getToolbar(), BorderLayout.CENTER);
        toolbarPanel.add(applicationToolBar.getExitButton(), BorderLayout.EAST);
        
        add(toolbarPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // updateSplitPosition(elementByIndex(0));

        mainPanel.addPropertyChangeListener(ELEMENT, new PropertyChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Class<? extends ViewElement> oldKlass = (Class<? extends ViewElement>) evt.getOldValue();
                Class<? extends ViewElement> newKlass = (Class<? extends ViewElement>) evt.getNewValue();

                // System.out.println("oldKlass = " + oldKlass);
                // System.out.println("newKlass = " + newKlass);

                if (oldKlass != null && oldKlass != newKlass) {
                    _stop(oldKlass);
                }
                _start(newKlass);
            }
        });
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }

    public CanvasPanel getCanvasPanel() {
        return canvasPanel;
    }

    public void clear(boolean clearTerminalManager) {
        canvasPanel.clear();
        mainPanel.clear();
        if (clearTerminalManager) {
            controller.getTerminalManager().clear();
        }
    }

    public void saveView() {
        monitor.info("Saving GUI");

        mainPanel.save();
        canvasPanel.save();

        ModulesUtil.save(modules);
    }

    public void loadView(boolean loadMesh) {
        loadToolbars();

        if (model.hasProject()) {
            monitor.info("");
            monitor.info("Loading 3D");
            canvasPanel.load(loadMesh);

            monitor.info("");
            monitor.info("Loading GUI");
            mainPanel.load();
        } else {
            canvasPanel.clear();
            mainPanel.disableAll();
        }
    }

    public void loadToolbars() {
        if (model.hasProject()) {
            RecentItems.getInstance().push(model.getProject().getBaseDir());
        }
        menuBar.updateDictionariesList(model);
        statusBar.load(model);
        applicationToolBar.refresh();
    }

    private void _stop(Class<? extends ViewElement> klass) {
        logger.debug("STOP: {}", klass.getSimpleName());
        mainPanel.stop(klass);
        canvasPanel.stop(klass);
    }

    private void _start(Class<? extends ViewElement> klass) {
        logger.debug("START: {}", klass.getSimpleName());
        mainPanel.start(klass);
        canvasPanel.start(klass);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Model) {
            if (arg != null) {
                for (ViewElement element : viewElements) {
                    logger.debug("[CHANGE OBSERVERD] arg: {}, observer: {}", arg.getClass().getSimpleName(), element.getClass().getSimpleName());
                    element.changeObserved(arg);
                }
            }
        }
    }

    @Action(key = NEW_CASE)
    public void createCase() {
        if (controller.allowActionsOnRunning(false)) {
            controller.createCase(null, null);
        }
    }

    @Action(key = OPEN_CASE)
    public void openCase() {
        if (controller.allowActionsOnRunning(false)) {
            controller.openCase(null);
        }
    }

    @Action(key = RECENT_CASES)
    public void open() {
        try {
            Point location = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(location, UiUtil.getActiveWindow());
            createRecentProjectsPopUp().show(this, location.x, (location.y / 2));
        } catch (Exception e) {
            // convertPointFromScreen can throw a Nullpointer exception
            // no need to do anything
        }

    }

    private JPopupMenu createRecentProjectsPopUp() {
        final JPopupMenu popup = new JPopupMenu();
        List<String> items = RecentItems.getInstance().getItems();
        if (items.isEmpty()) {
            JMenuItem menuItem = new JMenuItem(RecentItems.NO_ITEMS);
            menuItem.setEnabled(false);
            popup.add(menuItem);
        } else {
            Icon CASE_ICON = ResourcesUtil.getIcon(ApplicationInfo.getVendor().toLowerCase() + ".case");
            for (final String item : items) {
                popup.add(new AbstractAction(item, CASE_ICON) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (controller.allowActionsOnRunning(false)) {
                            controller.openCase(OpenOptions.file(new File(item), OpenMode.CHECK_FOLDER_ASK_USER));
                        }
                    }
                });
            }
            popup.addSeparator();
            popup.add(new AbstractAction("Clear") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    RecentItems.getInstance().clear();
                }
            });
        }
        return popup;
    }

    @Action(key = SAVE_CASE, checkLicense = true)
    public void save() {
        File baseDir = model.getProject().getBaseDir();
        if (baseDir.exists()) {
            int retVal = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), "Overwrite existing case?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (retVal == JOptionPane.YES_OPTION) {
                controller.saveCase(baseDir);
            }
        }
    }

    @Action(key = SAVE_AS_CASE, checkLicense = true)
    public void saveAs() {
        controller.saveCase(null);
    }

    @Action(key = EXIT)
    public void exit() {
        if (controller.allowActionsOnRunning(true)) {
            _exit();
        }
    }

    @Action(key = BROWSE_CASE)
    public void browseCase() {
        if (model.getProject() != null && model.getProject().getBaseDir() != null && model.getProject().getBaseDir().exists()) {
            FileManagerSupport.open(model.getProject().getBaseDir());
        } else {
            JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "No project directory", "File System error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Action(key = OPEN_TERMINAL, checkEnv = true, checkLicense = true)
    public void openTerminal() {
        if (model.getProject() != null && model.getProject().getBaseDir() != null) {
            TerminalSupport.openTerminal(model);
        }
    }

    private void _exit() {
        System.exit(0);
    }

    public void showStartupDialog(AbstractApplication abstractApplication) {
        startupDialog = new JDialog(abstractApplication.getFrame(), JDialog.DEFAULT_MODALITY_TYPE);
        startupDialog.getContentPane().setLayout(new BorderLayout());
        startupDialog.setName("engys.cfd.dialog");
        startupDialog.getContentPane().add(new StartPanel(abstractApplication, this), BorderLayout.CENTER);
        startupDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        startupDialog.setResizable(true);
        startupDialog.setTitle(controller.isDemo() ? "(Demo mode)" : "");
        abstractApplication.checkVersion();
        // dialog.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        // dialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
        UiUtil.centerAndShow(startupDialog);
    }

    @Action(key = "application.startup.hide")
    public void hideStartupDialog() {
        if (startupDialog != null) {
            startupDialog.dispose();
        }
    }

    public void dump() {
        model.getPatches().print();
    }

    public Controller getController() {
        return controller;
    }

    // Called just by case manager
    public void removeExitButton() {
        ActionManager.getInstance().remove(EXIT);
    }

    @Override
    public void eventTriggered(Object obj, EventManager.Event event) {
        if(event instanceof GlassPaneEvent){
            setGlassPaneActive(((GlassPaneEvent) event).isActive());
        }
    }

    private void setGlassPaneActive(boolean active) {
        glassPane.setActive(active);
    }
}
