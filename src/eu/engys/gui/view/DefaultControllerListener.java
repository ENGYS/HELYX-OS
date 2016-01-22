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

package eu.engys.gui.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.ControllerListener;
import eu.engys.core.controller.GeometryToMesh;
import eu.engys.core.executor.TerminalManager;
import eu.engys.core.project.Model;

public class DefaultControllerListener implements ControllerListener {

    private static final Logger logger = LoggerFactory.getLogger(DefaultControllerListener.class);
    private View view;
    private ElementSelector selector;

    public DefaultControllerListener(Model model, View view) {
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
        TerminalManager.getInstance().clear();
        view.clear();
        if (view.getController().getClient() != null) {
            view.getController().getClient().reset();
        }
    }

    @Override
    public void afterNewCase() {
        logger.debug("AFTER NEW CASE");
        view.loadView();
        selector.goToFirstElement();
    }

    @Override
    public void beforeLoadCase() {
        logger.debug("BEFORE LOAD CASE");
        view.clear();
        TerminalManager.getInstance().clear();
    }

    @Override
    public void afterLoadCase() {
        logger.debug("AFTER LOAD CASE");
        view.loadView();
    }

    @Override
    public void beforeReopenCase() {
        logger.debug("BEFORE REOPEN CASE");
        view.clear();
    }

    @Override
    public void afterReopenCase() {
        logger.debug("AFTER REOPEN CASE");
        afterLoadCase();
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
    }

    @Override
    public void afterCheckMesh() {
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

}
