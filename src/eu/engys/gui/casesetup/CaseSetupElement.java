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


package eu.engys.gui.casesetup;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulePanel;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.project.Model;
import eu.engys.core.project.ProjectReader;
import eu.engys.core.project.ProjectWriter;
import eu.engys.core.project.state.State;
import eu.engys.gui.Actions;
import eu.engys.gui.GUIPanel;
import eu.engys.gui.view.AbstractViewElement;
import eu.engys.gui.view.View3DElement;
import eu.engys.gui.view.ViewElementPanel;
import eu.engys.util.plaf.ILookAndFeel;

public class CaseSetupElement extends AbstractViewElement {

    private ViewElementPanel viewElementPanel;

    @Inject
    @CaseSetup
    private ProjectWriter writer;
    @Inject
    @CaseSetup
    private ProjectReader reader;

    @Inject
    public CaseSetupElement(@CaseSetup String title, @CaseSetup Set<GUIPanel> panels, Set<ApplicationModule> modules, @CaseSetup View3DElement view3DElement, @CaseSetup Actions actions, ILookAndFeel lookAndFeel) {
        super(title, panels, modules, view3DElement, actions, lookAndFeel);
    }

    @Override
    public ViewElementPanel getPanel() {
        return viewElementPanel;
    }

    @Override
    public void layoutComponents() {
        viewElementPanel = new ViewElementPanel(this);
        super.layoutComponents();
    }

    @Override
    protected Set<GUIPanel> getModulePanels() {
        Set<GUIPanel> allPanels = new HashSet<GUIPanel>();
        for (ModulePanel panel : ModulesUtil.getCaseSetupPanels(modules)) {
            allPanels.add((GUIPanel) panel);
        }
        return allPanels;
    }

    @Override
    public int getPreferredWidth() {
        return 700;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public ProjectReader getReader() {
        return reader;
    }

    @Override
    public ProjectWriter getWriter() {
        return writer;
    }
    
    @Override
    public void load(Model model) {
        super.load(model);
        ModulesUtil.updateTree(modules, getPanel());
    }
    
    @Override
    public void changeObserved(Object arg) {
    	if (arg instanceof State) {
			ModulesUtil.updateTree(modules, getPanel());
			getActions().update();
		}
    	super.changeObserved(arg);
    }
}
