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
package eu.engys.standardDynamic.domain;

import eu.engys.core.project.Model;
import eu.engys.dynamic.DynamicModule;
import eu.engys.dynamic.domain.SolidBodyDynamicPanel;
import eu.engys.util.bean.BeanModel;
import eu.engys.util.bean.BeanPanelBuilder;

public class StandardSolidBodyDynamicPanel extends SolidBodyDynamicPanel {

    public StandardSolidBodyDynamicPanel(Model model, DynamicModule module) {
        super(model, module);
    }
    
    @Override
    protected BeanModel[] getBeanModels(){
        BeanModel[] models = new BeanModel[5];
        models[0] = linearMotionModel;
        models[1] = rotatingMotionModel;
        models[2] = oscillatingLinearMotionModel;
        models[3] = oscillatingRotatingMotionModel;
        models[4] = sdaMotionModel;
        return models;
    }

    @Override
    protected void addEngysTypes(BeanPanelBuilder builder) {
    }
    
    @Override
    public void stateChanged() {
        typeCombo.clearDisabledIndexes();
    }

}
