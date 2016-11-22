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
package eu.engys.gui.view.fallback;

import java.util.Collections;
import java.util.Set;

import javax.swing.ImageIcon;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.project.Model;
import eu.engys.core.project.ProjectReader;
import eu.engys.core.project.ProjectWriter;
import eu.engys.gui.Actions;
import eu.engys.gui.GUIPanel;
import eu.engys.gui.tree.Tree;
import eu.engys.gui.view.View3DElement;
import eu.engys.gui.view.ViewElement;
import eu.engys.gui.view.ViewElementPanel;

public class FallbackViewElement implements ViewElement {

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public ViewElementPanel getPanel() {
        return null;
    }

    @Override
    public Tree getTree() {
        return null;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void clear() {
    }

    @Override
    public void load(Model model) {
    }

    @Override
    public void save(Model model) {
    }

    @Override
    public void layoutComponents() {
    }

    @Override
    public Set<GUIPanel> getPanels() {
        return Collections.emptySet();
    }

    @Override
    public Set<ApplicationModule> getModules() {
        return Collections.emptySet();
    }

    @Override
    public boolean isEnabled(Model model) {
        return false;
    }

    @Override
    public View3DElement getView3D() {
        return null;
    }

    @Override
    public Actions getActions() {
        return null;
    }

    @Override
    public ProjectReader getReader() {
        return null;
    }

    @Override
    public ProjectWriter getWriter() {
        return null;
    }

    @Override
    public int getPreferredWidth() {
        return 0;
    }

    @Override
    public void changeObserved(Object arg) {
    }

}
