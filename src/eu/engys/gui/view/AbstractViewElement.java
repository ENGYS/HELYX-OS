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

import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.geometry.Geometry;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.materials.Materials;
import eu.engys.core.project.runtimefields.RuntimeFields;
import eu.engys.core.project.state.Solver;
import eu.engys.core.project.state.State;
import eu.engys.core.project.system.fieldmanipulationfunctionobjects.FieldManipulationFunctionObjects;
import eu.engys.core.project.system.monitoringfunctionobjects.MonitoringFunctionObject;
import eu.engys.core.project.system.monitoringfunctionobjects.MonitoringFunctionObjects;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.patches.BoundaryType;
import eu.engys.gui.Actions;
import eu.engys.gui.GUIError;
import eu.engys.gui.GUIPanel;
import eu.engys.gui.ModelObserver;
import eu.engys.gui.tree.Tree;
import eu.engys.launcher.StartUpMonitor;
import eu.engys.util.plaf.ILookAndFeel;

public abstract class AbstractViewElement implements ViewElement {

    private static final Logger logger = LoggerFactory.getLogger(ViewElement.class);

    protected final String title;
    protected final View3DElement view3DElement;
    protected final Set<GUIPanel> panels;
    protected final Set<GUIPanel> modulePanels;
    protected final Set<ApplicationModule> modules;
    protected final ILookAndFeel lookAndFeel;
    protected final Actions actions;

    public AbstractViewElement(String title, Set<GUIPanel> panels, Set<ApplicationModule> modules, View3DElement view3DElement, Actions actions, ILookAndFeel lookAndFeel) {
        this.title = title;
        this.panels = panels;
        this.modules = modules;
        this.actions = actions;
        this.lookAndFeel = lookAndFeel;
        this.view3DElement = view3DElement;
        this.modulePanels = getModulePanels();
        logger.info("-> {}", getTitle());
    }

    protected Set<GUIPanel> getModulePanels() {
        Set<GUIPanel> allPanels = new HashSet<GUIPanel>();
        return allPanels;
    }

    @Override
    public void layoutComponents() {
        for (GUIPanel guiPanel : panels) {
            StartUpMonitor.info("Layout " + guiPanel.getTitle());
            logger.info("Layout " + guiPanel.getTitle());
            guiPanel.layoutPanel();
        }
        for (GUIPanel guiPanel : modulePanels) {
            StartUpMonitor.info("Layout " + guiPanel.getTitle());
            logger.info("Layout " + guiPanel.getTitle());
            guiPanel.layoutPanel();
        }
    }

    @Override
    public int getPreferredWidth() {
        return 0;
    }

    @Override
    public Actions getActions() {
        return actions;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public Set<GUIPanel> getPanels() {
        // do not add module panels. The module is responsible to add/remove the panel because it contains the logic (module on/off)
        return panels;

    }

    @Override
    public Set<ApplicationModule> getModules() {
        return modules;
    }

    @Override
    public Tree getTree() {
        return getPanel().getTree();
    }

    @Override
    public View3DElement getView3D() {
        return view3DElement;
    }

    @Override
    public void start() {
        logger.info("[START] {}", getTitle());
        getPanel().start();
    }

    @Override
    public void stop() {
        logger.info("[STOP] {}", getTitle());
        getPanel().stop();
    }

    @Override
    public void clear() {
        getPanel().clear();
        logger.info("[CLEAR] {}", getTitle());
    }

    @Override
    public void load(Model model) {
        logger.info("[LOAD] {}", getTitle());
        for (GUIPanel guiPanel : panels) {
            try {
                guiPanel.load();
                logger.info("[LOAD] -> {} LOADED", guiPanel.getKey());
            } catch (GUIError error) {
                logger.error("[LOAD ERROR] {}", error);
            }
        }
        for (GUIPanel guiPanel : modulePanels) {
            try {
                guiPanel.load();
                logger.info("[LOAD] -> {} LOADED", guiPanel.getKey());
            } catch (GUIError error) {
                logger.error("[LOAD ERROR] {}", error);
            }
        }
    }

    @Override
    public void save(Model model) {
        logger.info("[SAVE] {}", getTitle());
        for (GUIPanel guiPanel : panels) {
            try {
                guiPanel.save();
            } catch (GUIError error) {
                logger.error("[SAVE ERROR] {}", error);
            }
        }
        for (GUIPanel guiPanel : modulePanels) {
            try {
                guiPanel.save();
            } catch (GUIError error) {
                logger.error("[SAVE ERROR] {}", error);
            }
        }
    }

    @Override
    public void changeObserved(Object arg) {
        for (ModelObserver observer : getPanel().getObservers()) {
            logger.trace("[CHANGE OBSERVED] [{}] -> Panel: {}", arg.getClass().getSimpleName(), observer.getTitle());
            if (arg instanceof State) {
                observer.stateChanged();
            } else if (arg instanceof Solver) {
                observer.solverChanged();
            } else if (arg instanceof Materials) {
                observer.materialsChanged();
            } else if (arg instanceof Fields) {
                observer.fieldsChanged();
            } else if (arg instanceof BoundaryType) {
                observer.boundaryTypeChanged((BoundaryType) arg);
            } else if(arg instanceof Geometry){
                observer.geometryChanged();
            }else if(arg instanceof Surface){
                observer.geometryChanged();
            } else if (arg instanceof RuntimeFields) {
                observer.runtimeFieldsChanged();
            } else if (arg instanceof openFOAMProject) {
                observer.projectChanged();
            } else if (arg instanceof FieldManipulationFunctionObjects) {
                observer.fieldManipulationFunctionObjectsChanged();
            } else if (arg instanceof MonitoringFunctionObjects) {
                observer.monitoringFunctionObjectsChanged();
            } else if (arg instanceof MonitoringFunctionObject) {
                observer.monitoringFunctionObjectChanged((MonitoringFunctionObject) arg);
            }
        }
    }

    @Override
    public boolean isEnabled(Model model) {
        return true;
    }
}
