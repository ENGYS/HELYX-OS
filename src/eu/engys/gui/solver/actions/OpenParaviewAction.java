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

package eu.engys.gui.solver.actions;

import static eu.engys.core.OpenFOAMEnvironment.getEnvironment;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import eu.engys.core.OpenFOAMEnvironment;
import eu.engys.core.executor.Executor;
import eu.engys.core.project.Model;
import eu.engys.util.OpenFOAMCommands;
import eu.engys.util.PrefUtil;
import eu.engys.util.Util;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class OpenParaviewAction extends ViewAction {

    private static final String FOAM_EXTENSION = ".foam";

    private Model model;

    public OpenParaviewAction(Model model) {
        super(PARAVIEW_LABEL, PARAVIEW_ICON, PARAVIEW_TOOLTIP);
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        launchParaView();
    }

    private void launchParaView() {
        File paraView = PrefUtil.getParaViewEntry();
        if (OpenFOAMEnvironment.isParaviewPathSet()) {
            String foamFile = getCaseFoamFile();
            if (foamFile != null) {
                Executor.command(paraView, "--data=" + foamFile).inFolder(model.getProject().getBaseDir()).description(PARAVIEW_LABEL).exec();
            } else {
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "No .foam file found!", "Missing file", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if(Util.isWindows()){
                UiUtil.showEnvironmentNotLoadedWarning(PARAVIEW_LABEL);
            } else {
                if(OpenFOAMEnvironment.isEnvironementLoaded()){
                    Executor.command(OpenFOAMCommands.PARA_FOAM).inFolder(model.getProject().getBaseDir()).withOpenFoamEnv().env(getEnvironment(model)).description(PARAVIEW_LABEL).exec();
                } else {
                    UiUtil.showCoreEnvironmentNotLoadedWarning();
                }
            }
        }
    }

    private String getCaseFoamFile() {
        File baseDir = model.getProject().getBaseDir();
        String[] foamFiles = baseDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FOAM_EXTENSION);
            }
        });
        if (foamFiles.length == 0) {
            return null;
        } else {
            return baseDir.toPath().resolve(foamFiles[0]).toAbsolutePath().toString();
        }
    }

    /**
     * Resources
     */

    private static final Icon PARAVIEW_ICON = ResourcesUtil.getIcon("paraview.icon");

    private static final String PARAVIEW_LABEL = ResourcesUtil.getString("paraview.label");
    private static final String PARAVIEW_TOOLTIP = ResourcesUtil.getString("paraview.tooltip");
}
