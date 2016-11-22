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
package eu.engys.standardVOF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.modules.solutionmodelling.AbstractSolutionView;
import eu.engys.core.modules.solutionmodelling.MultiphaseBuilder;
import eu.engys.core.project.state.MultiphaseModel;
import eu.engys.core.project.state.SolutionState;
import eu.engys.util.ui.textfields.SpinnerField;

public class StandardVOFSolutionView extends AbstractSolutionView {

    private static final Logger logger = LoggerFactory.getLogger(StandardVOFSolutionView.class);

    private StandardVOFModule module;
    private MultiphaseBuilder builder;

    public StandardVOFSolutionView(StandardVOFModule module) {
        this.module = module;
    }

    @Override
    public void buildMultiphase(MultiphaseBuilder builder) {
        this.builder = builder;
        builder.addMultiphaseChoice(StandardVOFModule.VOF_MODEL);
    }

    @Override
    public void fixSolutionState(SolutionState ss) {
        if (ss.areSolverTypeAndTimeAndFlowAndTurbulenceChoosen()) {
            boolean isVOFState = ss.isTransient() && ss.isIncompressible();
            if (isVOFState) {
                builder.enableChoice(StandardVOFModule.VOF_MODEL);
            } else {
                builder.disableChoice(StandardVOFModule.VOF_MODEL);
            }
        }
    }

    @Override
    public void fixMultiphase(MultiphaseModel mm) {
        SpinnerField phasesNumber = builder.getPhasesField();
        if (mm.equals(StandardVOFModule.VOF_MODEL)) {
            phasesNumber.setIntValue(2);
            phasesNumber.setEnabled(false);
        }
    }

}
