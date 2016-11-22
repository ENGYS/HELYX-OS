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
package eu.engys.application;

import static eu.engys.launcher.StartUpMonitor.closeSplash;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Set;

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
import eu.engys.core.controller.Controller.OpenMode;
import eu.engys.core.controller.OpenOptions;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.gui.AboutWindow;
import eu.engys.gui.GlassPane;
import eu.engys.gui.PreferencesBean;
import eu.engys.gui.PreferencesDialog;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.view.View;
import eu.engys.gui.view3D.View3DEventListener;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;

public abstract class AbstractApplication implements Application {

	private static final Logger logger = LoggerFactory.getLogger(AbstractApplication.class);

	protected JFrame frame;
	public View view;
	protected Model model;
	public Controller controller;
	protected View3DEventListener view3dListener;

    protected Set<ApplicationModule> modules;

	public AbstractApplication(Model model, Set<ApplicationModule> modules, View view, Controller controller) {
		this.model = model;
        this.modules = modules;
		this.view = view;
		this.controller = controller;
	}

	@Override
	public JFrame getFrame() {
		return frame;
	}

	@Override
	public void checkVersion() {
	}

	@Override
	public void start(final Arguments arguments) {
		ExecUtil.invokeLater(new Runnable() {
			@Override
			public void run() {
				initFrame();
				frame.setVisible(true);
				trySettingOpenFoamFolder();

				executeStartupActions(arguments);

				closeSplash();
			}
		});
	}

	protected void executeStartupActions(Arguments arguments) {
		if (arguments.baseDir != null) {
			controller.openCase(OpenOptions.file(arguments.baseDir, OpenMode.CHECK_FOLDER_ASK_USER));
		} else {
			view.showStartupDialog(this);
		}
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
			 * This method fixes the Synthetica laf bug that causes incorrect fullscreen window size on secondary monitor.
			 */
			@Override
			public void setMaximizedBounds(Rectangle bounds) {
				GraphicsDevice currentFrame = getGraphicsConfiguration().getDevice();
				if (UiUtil.isSecondaryScreen(currentFrame) && getExtendedState() == JFrame.NORMAL) {
					super.setMaximizedBounds(UiUtil.getCurrentScreenSize(this));
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
				ActionManager.getInstance().invoke(View.EXIT);
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
		JMenuItem preferencesItem = new JMenuItem(new AbstractAction(PREFERENCES_LABEL, PREFERENCES_ICON) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (controller.isDemo()) {
					UiUtil.showDemoMessage();
				} else {
					new PreferencesDialog(getPreferencesBean(), model.getDefaults().getDictDataFolder()).show();
				}
			}
		});
		preferencesItem.setName("Application Preferences");
		view.getMenuBar().getEditMenu().add(preferencesItem);
	}

	protected abstract PreferencesBean getPreferencesBean();

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

	/*
	 * RESOURCES
	 */

	public static final String PREFERENCES_LABEL = "Preferences";
	public static final Icon PREFERENCES_ICON = ResourcesUtil.getIcon("preferences.icon");

	public static final String LICENSE_MANAGER_LABEL = "License Manager";
	public static final Icon LICENSE_MANAGER_ICON = ResourcesUtil.getIcon("license.icon");

	public static final String RELEASE_NOTES_LABEL = "Release Notes";
	public static final String RELEASE_NOTES_WARNING_MESSAGE = "Release notes not found or not installed";

	public static final String DOCUMENTATION_LABEL = "Documentation";
	public static final Icon DOCUMENTATION_ICON = ResourcesUtil.getIcon("file.pdf");
	public static final String DOCUMENTATION_WARNING_MESSAGE = "Documentation not found or not installed";

	public static final Icon INFO_ICON = ResourcesUtil.getIcon("info.icon");
	public static final Icon FOLDER_ICON = ResourcesUtil.getIcon("application.open.icon");

	public static final Icon SMALL_LOGO = ResourcesUtil.getIcon("engys.logo");
	public static final Icon BIG_LOGO = ResourcesUtil.getIcon("engys.logo.big");
	public static final Icon MEDIUM_LOGO = ResourcesUtil.getIcon("engys.logo.medium");
	public static final Icon FULL_LOGO = ResourcesUtil.getIcon("engys.logo.full");
}
