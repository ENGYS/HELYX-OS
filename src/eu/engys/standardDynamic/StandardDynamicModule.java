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
package eu.engys.standardDynamic;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import eu.engys.core.modules.ModulePanel;
import eu.engys.core.project.Model;
import eu.engys.dynamic.DynamicModule;
import eu.engys.standardDynamic.cellzones.StandardDynamicCellZonesView;
import eu.engys.standardDynamic.domain.StandardSolidBodyDynamicPanel;

public class StandardDynamicModule extends DynamicModule {

    @Inject
    public StandardDynamicModule(Model model) {
        super(model);
        this.cellZonesView = new StandardDynamicCellZonesView(model, this);
        this.solutionView = new StandardDynamicSolutionView(model, this);
        this.solidBodyPanel = new StandardSolidBodyDynamicPanel(model, this); 
        this.treeView = new StandardDynamicTreeView(this, sixDoFPanel, solidBodyPanel);
    }
    
    @Override
    public Set<ModulePanel> getCaseSetupPanels() {
        Set<ModulePanel> panels = new HashSet<>();
        panels.add(sixDoFPanel);
        panels.add(solidBodyPanel);
        return panels;
    }

}
