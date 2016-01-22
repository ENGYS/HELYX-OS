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


package eu.engys.application;

import static eu.engys.launcher.StartUpMonitor.close;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.Arguments;
import eu.engys.core.OpenFOAMEnvironment;
import eu.engys.core.controller.Controller;
import eu.engys.core.presentation.ActionContainer;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.gui.AboutWindow;
import eu.engys.gui.GlassPane;
import eu.engys.gui.PreferencesDialog;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.view.View;
import eu.engys.gui.view3D.View3DEventListener;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;

public abstract class AbstractApplication implements Application, ActionContainer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractApplication.class);

    protected JFrame frame;
    public View view;
    protected Model model;
    public Controller controller;
    protected View3DEventListener view3dListener;

    public AbstractApplication(Model model, View view, Controller controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }
    
    @Override
    public boolean isDemo() {
        return false;
    }
    
    @Override
    public void checkVersion() {
    }

    @Override
    public void run() {
        initFrame();
        frame.setVisible(true);
        trySettingOpenFoamFolder();

        if (Arguments.stlFiles != null) {
            if (Arguments.baseDir != null) {
                ActionManager.getInstance().invoke("application.open");
            } else {
                ActionManager.getInstance().invoke("application.create");
            }
        } else {
            if (Arguments.baseDir != null) {
                controller.openCase(Arguments.baseDir);
            } else {
                view.showStartupDialog(this);
            }
        }
        close();
    }

    protected void trySettingOpenFoamFolder() {
        OpenFOAMEnvironment.trySettingOpenFoamFolder(frame);
    }

    @Override
    public void initFrame() {
        view.layoutComponents();
        frame = new JFrame(getTitle()) {
            @Override
            public void dispose() {
                EventManager.unregisterAllEventSubscriptions();
                super.dispose();
            }

            /**
             * This method fixes the Synthetica laf bug that causes incorrect
             * fullscreen window size on secondary monitor.
             */
            @Override
            public void setMaximizedBounds(Rectangle bounds) {
                GraphicsDevice currentFrame = getGraphicsConfiguration().getDevice();
                if (UiUtil.isSecondaryScreen(currentFrame) && getExtendedState() == JFrame.NORMAL) {
                    super.setMaximizedBounds(UiUtil.getCurrentScreenSize(frame));
                } else {
                    super.setMaximizedBounds(bounds);
                }
            }
        };
        view.setProgressMonitorParent(frame);

        Dimension preferredDimension = UiUtil.getPreferredScreenSize();
        logger.info("Set dimendions to {}", preferredDimension);
        frame.setSize(preferredDimension);
        frame.setLocationRelativeTo(null);
        
        UiUtil.center(frame);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.setIconImages(Arrays.asList(new Image[] { ((ImageIcon) getSmallIcon()).getImage(), ((ImageIcon) getBigIcon()).getImage() }));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ActionManager.getInstance().invoke("application.exit");
            }
        });
        frame.setName("MainFrame");
        frame.getRootPane().updateUI();
        frame.setJMenuBar(view.getMenuBar());

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(view, BorderLayout.CENTER);
        frame.getContentPane().add(view.getStatusBar(), BorderLayout.SOUTH);
        
        GlassPane glassPane = new GlassPane();
        frame.setGlassPane(glassPane);
        glassPane.setVisible(false);
        

        customizeGUIFrame(view);
    }

    public View getView() {
        return view;
    }

    public Model getModel() {
        return model;
    }

    public abstract String getTitle();

    protected abstract void customizeGUIFrame(View view);

    protected void addPreferencesItem(final View view) {
        JMenuItem preferencesItem = new JMenuItem(new AbstractAction("Preferences", PREFERENCES_ICON) {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PreferencesDialog(isOS(), hasParaview(), hasFieldView(), hasEnsight(), hasSolverPreferences(), model.getDefaults().getDictDataFolder()).show();
            }
        });
        preferencesItem.setName("Application Preferences");
        view.getMenuBar().getEditMenu().add(preferencesItem);
    }

    protected abstract boolean hasParaview();

    protected abstract boolean hasFieldView();

    protected abstract boolean hasEnsight();

    protected boolean hasSolverPreferences() {
        return true;
    }

    protected boolean isOS() {
        return false;
    }

    protected void addHelpItem(final View view) {
        view.getMenuBar().getHelpMenu().add(new AbstractAction("About", INFO_ICON) {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AboutWindow(getMediumIcon(), getBannerIcon());
            }
        });
    }

    
    @Override
    public JPanel createAdPanel() {
        return new JPanel();
    }

    @Override
    public JPanel createVersionPanel() {
        return new JPanel();
    }
    
    /**
     * RESOURCES
     */

    public static final Icon PREFERENCES_ICON = ResourcesUtil.getIcon("preferences.icon");
    public static final Icon LICENSE_ICON = ResourcesUtil.getIcon("license.icon");
    public static final Icon INFO_ICON = ResourcesUtil.getIcon("info.icon");
    public static final Icon PDF_ICON = ResourcesUtil.getIcon("file.pdf");
    public static final Icon FOLDER_ICON = ResourcesUtil.getIcon("application.open.icon");

    public static final Icon SMALL_LOGO = ResourcesUtil.getIcon("engys.logo");
    public static final Icon BIG_LOGO = ResourcesUtil.getIcon("engys.logo.big");
    public static final Icon MEDIUM_LOGO = ResourcesUtil.getIcon("engys.logo.medium");
    public static final Icon FULL_LOGO = ResourcesUtil.getIcon("engys.logo.full");
}
