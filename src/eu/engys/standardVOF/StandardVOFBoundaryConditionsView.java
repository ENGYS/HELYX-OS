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

import eu.engys.core.modules.boundaryconditions.BoundaryConditionsView;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.modules.boundaryconditions.IBoundaryConditionsPanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.patches.BoundaryType;
import eu.engys.gui.casesetup.boundaryconditions.panels.patch.MomentumPatch;
import eu.engys.gui.casesetup.boundaryconditions.panels.wall.StandardMomentumWall;
import eu.engys.gui.casesetup.boundaryconditions.parameterspanel.MomentumParametersPanel;

public class StandardVOFBoundaryConditionsView implements BoundaryConditionsView {

    public static final String MOMENTUM = " " + MomentumParametersPanel.MOMENTUM + " ";
    private StandardVOFModule module;

    public StandardVOFBoundaryConditionsView(StandardVOFModule module) {
        this.module = module;
    }

    @Override
    public void configure(BoundaryTypePanel panel) {
        if (panel.getType() == BoundaryType.WALL) {
            panel.addPanel(MOMENTUM, new VOFStandardMomentumWall(panel), 0);
        } else if (panel.getType() == BoundaryType.PATCH) {
            panel.addPanel(MOMENTUM, new VOFStandardMomentumPatch(panel), 0);
        }
    }
    
    class VOFStandardMomentumWall extends StandardMomentumWall {
        public VOFStandardMomentumWall(BoundaryTypePanel parent) {
            super(parent);
        }

        @Override
        public boolean isEnabled(Model model) {
            return module.isVOF();
        }
    }

    class VOFStandardMomentumPatch extends MomentumPatch {

        public VOFStandardMomentumPatch(BoundaryTypePanel parent) {
            super(parent);
        }

        @Override
        public boolean isEnabled(Model model) {
            return module.isVOF();
        }
    }
    
    @Override
    public void configure(IBoundaryConditionsPanel panel) {
    }

}
