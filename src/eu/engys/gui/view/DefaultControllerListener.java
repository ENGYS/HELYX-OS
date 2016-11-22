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

import java.util.Set;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.Controller;
import eu.engys.core.controller.ControllerListener;
import eu.engys.core.controller.GeometryToMesh;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.project.InvalidProjectException;
import eu.engys.core.project.Model;
import eu.engys.util.ui.UiUtil;

public class DefaultControllerListener implements ControllerListener {

    private static final Logger logger = LoggerFactory.getLogger(DefaultControllerListener.class);
    private View view;
    private ElementSelector selector;
    private Set<ApplicationModule> modules;
    private Controller controller;

    public DefaultControllerListener(Model model, Controller controller, Set<ApplicationModule> modules, View view) {
        this.controller = controller;
        this.modules = modules;
        this.view = view;
        this.selector = new ElementSelector(model, view);
    }

    @Override
    public void saveLocation() {
        selector.saveLocation();
    }

    @Override
    public void goToLocation() {
        selector.goToLocation();
    }

    @Override
    public void selectDestinationAndGo() {
        selector.selectDestinationAndGo();
    }

    /*
     * Case
     */
    @Override
    public void beforeNewCase() {
        logger.debug("BEFORE NEW CASE");
        view.clear(true);
        if (view.getController().getClient() != null) {
            view.getController().getClient().reset();
        }
    }

    @Override
    public void afterNewCase(boolean loadMesh) {
        logger.debug("AFTER NEW CASE");
        view.loadView(loadMesh);
        selector.goToFirstElement();
    }

    @Override
    public void beforeLoadCase() {
        logger.debug("BEFORE LOAD CASE");
        view.clear(true);
        if (view.getController().getClient() != null) {
            view.getController().getClient().reset();
        }
    }

    @Override
    public void afterLoadCase(boolean loadMesh) {
        logger.debug("AFTER LOAD CASE");
        try {
            ModulesUtil.checkIfCanOpenCase(modules);
            view.loadView(loadMesh);
        } catch (InvalidProjectException e) {
            logger.error(e.getMessage());
            JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), e.getMessage(), "Project error", JOptionPane.ERROR_MESSAGE);
            controller.clearModel();
        }
    }

    @Override
    public void beforeReopenCase() {
        logger.debug("BEFORE REOPEN CASE");
        view.clear(false);
    }

    @Override
    public void afterReopenCase() {
        logger.debug("AFTER REOPEN CASE");
        afterLoadCase(true);
    }

    @Override
    public void beforeSaveCase() {
        logger.debug("BEFORE SAVE CASE");
        view.saveView();
    }

    @Override
    public void afterSaveCase() {
        logger.debug("AFTER SAVE CASE");
        view.loadToolbars();
    }

    /*
     * Base mesh from file
     */
    @Override
    public void afterBlockMesh() {
        view.getCanvasPanel().resetZoom();
    }

    /*
     * Check
     */

    @Override
    public void beforeCheckMesh() {
        logger.debug("BEFORE CHECK MESH");
    }

    @Override
    public void afterCheckMesh() {
        logger.debug("AFTER CHECK MESH");
        view.getCanvasPanel().getMeshController().readTimeSteps();
        view.getCanvasPanel().loadWidgets();
        selector.goToBoundaryMesh();
    }

    /*
     * Virtualise
     */

    @Override
    public void beforeVirtualise() {
        logger.debug("BEFORE VIRTUALISE");
        view.saveView();
        selector.saveLocation();
    }

    @Override
    public void afterVirtualise(GeometryToMesh g2m) {
        view.getCanvasPanel().geometryToMesh(g2m);
        selector.goToLocation();
    }

    /*
     * Solver
     */

    @Override
    public void beforeRunCase() {
        selector.goToResiduals();
    }

    @Override
    public void afterRunCase() {
        view.getCanvasPanel().getMeshController().readTimeSteps();
        view.getCanvasPanel().loadWidgets();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterSetupCase() {
        view.getMainPanel().load(ElementSelector.getMeshElementClass(), ElementSelector.getCaseSetupElementClass());
    }

}
