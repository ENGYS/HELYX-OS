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

package eu.engys.gui.solver;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.project.Model;
import eu.engys.core.project.ProjectReader;
import eu.engys.core.project.ProjectWriter;
import eu.engys.gui.Actions;
import eu.engys.gui.GUIPanel;
import eu.engys.gui.view.AbstractViewElement;
import eu.engys.gui.view.View3DElement;
import eu.engys.gui.view.ViewElementPanel;
import eu.engys.util.plaf.ILookAndFeel;

public class SolverElement extends AbstractViewElement {

    private static final Logger logger = LoggerFactory.getLogger(SolverElement.class);

    private ViewElementPanel viewElementPanel;
    private Model model;

    private Observer solverModelObserver;

    @Inject
    public SolverElement(Model model, @Solver String title, @Solver Set<GUIPanel> panels, Set<ApplicationModule> modules, @Solver View3DElement view3DElement, @Solver Actions actions, ILookAndFeel lookAndFeel) {
        super(title, panels, modules, view3DElement, actions, lookAndFeel);
        this.model = model;
    }

    @Override
    public void layoutComponents() {
        viewElementPanel = new ViewElementPanel(this);
        solverModelObserver = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                //arg is null, see SolverModel.setState
//                logger.debug("Observerd a change");
                actions.update();
            }
        };
        super.layoutComponents();
    }

    @Override
    public int getPreferredWidth() {
        return 800;
    }

    @Override
    public ViewElementPanel getPanel() {
        return viewElementPanel;
    }

    @Override
    public void start() {
        super.start();
        model.getSolverModel().addObserver(solverModelObserver);
    }

    @Override
    public void stop() {
        super.stop();
        model.getSolverModel().deleteObserver(solverModelObserver);
    }

    @Override
    public void load(Model model) {
        super.load(model);
    }

    @Override
    public ProjectReader getReader() {
        return null;
    }

    @Override
    public ProjectWriter getWriter() {
        return null;
    }

}
