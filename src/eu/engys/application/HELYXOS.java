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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import eu.engys.core.OpenFOAMEnvironment;
import eu.engys.core.controller.Controller;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.patches.BoundaryType;
import eu.engys.gui.PreferencesBean;
import eu.engys.gui.view.View;
import eu.engys.util.ApplicationInfo;
import eu.engys.util.Symbols;
import eu.engys.util.Util;
import eu.engys.util.VersionChecker;
import eu.engys.util.VersionChecker.VersionType;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;

public class HELYXOS extends AbstractApplication {

    private static final Logger logger = LoggerFactory.getLogger(HELYXOS.class);
    private static final String DISCLAIMER = "This offering is not approved or endorsed by OpenCFD" + Symbols.COPYRIGHT + " Limited, the producer of the OPENFOAM" + Symbols.COPYRIGHT + " software and owner of the OPENFOAM" + Symbols.COPYRIGHT + "  and OpenCFD" + Symbols.COPYRIGHT + "  trade marks.";

    private JLabel versionLabel;
    private JButton versionButton;

    @Inject
    public HELYXOS(Model model, Set<ApplicationModule> modules, View view, Controller controller) {
        super(model, modules, view, controller);

        BoundaryType.registerBoundaryType(BoundaryType.PATCH);
        BoundaryType.registerBoundaryType(BoundaryType.WALL);
        BoundaryType.registerBoundaryType(BoundaryType.EMPTY);
        BoundaryType.registerBoundaryType(BoundaryType.CYCLIC_AMI);
        BoundaryType.registerBoundaryType(BoundaryType.CYCLIC);
        BoundaryType.registerBoundaryType(BoundaryType.SYMMETRY_PLANE);
        BoundaryType.registerBoundaryType(BoundaryType.SYMMETRY);
        BoundaryType.registerBoundaryType(BoundaryType.WEDGE);
    }

    @Override
    public String getTitle() {
        return ApplicationInfo.getName() + " - powered by " + ApplicationInfo.getVendor() + Symbols.REGISTERED;
    }

    @Override
    protected void customizeGUIFrame(final View view) {
        addPreferencesItem(view);
        addHelpItem(view);
        addSupportItem(view);
    }

    @Override
    protected void trySettingOpenFoamFolder() {
        OpenFOAMEnvironment.trySettingOpenFoamFolderOS(frame);
    }

    private void addSupportItem(final View view) {
        view.getMenuBar().getHelpMenu().add(new AbstractAction("Support", INFO_ICON) {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSupportWindow();
            }
        });
    }

    public void showSupportWindow() {
        new SupportWindow(getMediumIcon(), getBannerIcon(), DISCLAIMER);
    }

    @Override
    public void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final VersionType versionType = VersionChecker.isNewVersionAvailable();
                ExecUtil.invokeLater(new Runnable() {
                    public void run() {
                        if (versionType.isUpdated()) {
                            versionLabel.setText("Your version is up to date!");
                            versionLabel.setForeground(Color.GREEN.darker());
                            versionButton.setVisible(false);
                        } else if (versionType.isOld()) {
                            versionLabel.setText("Version " + VersionChecker.getOnlineVersion() + " is available for download!");
                            versionLabel.setForeground(Color.RED);
                            versionButton.setVisible(true);
                        } else if (versionType.isNotAvailable()) {
                            versionLabel.setText("Version not available!");
                            versionButton.setVisible(false);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public JPanel createAdPanel() {
        return new AdPanel();
    }

    @Override
    public JPanel createVersionPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(versionLabel = new JLabel("Checking for updates..."));
        versionLabel.setFont(new Font(versionLabel.getFont().getFontName(), Font.BOLD, versionLabel.getFont().getSize()));

        panel.add(versionButton = new JButton(new AbstractAction("Download") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String downloadPage = ApplicationInfo.getSite() + "/files";
                try {
                    Util.openWebpage(new URL(downloadPage));
                } catch (MalformedURLException e1) {
                    logger.error("Cannot open " + downloadPage);
                }
            }
        }));
        versionButton.setVisible(false);
        panel.setBorder(BorderFactory.createTitledBorder("Version"));
        return panel;
    }

    @Override
    protected PreferencesBean getPreferencesBean() {
        PreferencesBean bean = new PreferencesBean();
        bean.setPathPreferences(true);
        bean.setBatchPreferences(false);
        bean.setVtkPreferences(true);
        bean.setMiscPreferences(true);
        bean.setParaview(true);
        bean.setParaviewBatch(false);
        bean.setEnsight(false);
        bean.setFieldview(false);
        bean.setOS(true);
        return bean;
    }

    @Override
    public Icon getSmallIcon() {
        return SMALL_LOGO;
    }

    @Override
    public Icon getBigIcon() {
        return BIG_LOGO;
    }

    @Override
    public Icon getBannerIcon() {
        return BANNER;
    }

    @Override
    public Icon getBgIcon() {
        return STARTUP_BACKGROUND;
    }

    @Override
    public Icon getMediumIcon() {
        return MEDIUM_LOGO;
    }

    @Override
    public Icon getFullLogo() {
        return null;
    }

    public static final Icon BANNER = ResourcesUtil.getIcon("helyxos.banner");
    public static final Icon STARTUP_BACKGROUND = ResourcesUtil.getIcon("helyxos.startup");

}
