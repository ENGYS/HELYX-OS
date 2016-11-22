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

package eu.engys.gui.mesh;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.inject.Inject;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.project.Model;
import eu.engys.core.project.ProjectReader;
import eu.engys.core.project.ProjectWriter;
import eu.engys.gui.Actions;
import eu.engys.gui.GUIPanel;
import eu.engys.gui.mesh.actions.DefaultMeshActions;
import eu.engys.gui.view.AbstractViewElement;
import eu.engys.gui.view.View3DElement;
import eu.engys.gui.view.ViewElementPanel;
import eu.engys.util.plaf.ILookAndFeel;

public class MeshElement extends AbstractViewElement {

    private static final Logger logger = LoggerFactory.getLogger(MeshElement.class);
    
    private ViewElementPanel viewElementPanel;
    private Model model;
    private Observer modelObserver;

    @Inject
    public MeshElement(Model model, @Mesh String title, @Mesh Set<GUIPanel> panels, Set<ApplicationModule> modules, @Mesh View3DElement view3DElement, @Mesh Actions actions, ILookAndFeel lookAndFeel) {
        super(title, panels, modules, view3DElement, actions, lookAndFeel);
        this.model = model;
    }

    @Override
    public void layoutComponents() {
        viewElementPanel = new ViewElementPanel(this);
        modelObserver = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                logger.debug("Observerd a change: arg is " + arg.getClass());
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ((DefaultMeshActions) actions).update();
                    }
                });
            }
        };
        super.layoutComponents();
    }

    @Override
    public int getPreferredWidth() {
        return 650;
    }

    @Override
    public ViewElementPanel getPanel() {
        return viewElementPanel;
    }

    @Override
    public void start() {
        super.start();
        model.addObserver(modelObserver);
    }

    @Override
    public void stop() {
        model.deleteObserver(modelObserver);
        super.stop();
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
