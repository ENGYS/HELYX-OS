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
package eu.engys.dynamic;

import javax.swing.AbstractButton;
import javax.swing.JRadioButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.modules.solutionmodelling.AbstractSolutionView;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.MultiphaseModel;
import eu.engys.core.project.state.SolutionState;
import eu.engys.core.project.state.State;
import eu.engys.core.project.state.ThermalState;
import eu.engys.dynamic.data.DynamicAlgorithmType;
import eu.engys.dynamic.data.multibody.MultiBodyAlgorithm;
import eu.engys.dynamic.data.off.StaticAlgorithm;
import eu.engys.dynamic.data.refine.MeshRefineAlgorithm;
import eu.engys.dynamic.data.singlebody.SolidBodyAlgorithm;
import eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm;
import eu.engys.util.ui.ChooserPanel;
import eu.engys.util.ui.builder.PanelBuilder;

public class DynamicSolutionView extends AbstractSolutionView {

    private static final Logger logger = LoggerFactory.getLogger(DynamicSolutionView.class);

    public static final String OFF_LABEL = "Off";
    public static final String SIX_DOF_LABEL = "6 DOF Domain Motion";
    public static final String SOLID_RIGID_BODY_LABEL = "Rigid Body Domain Motion";
    public static final String MULTI_RIGID_BODY_LABEL = "Rigid Body Cell Zone Motion";
    public static final String DYNAMIC_REFINE_FV_MESH_LABEL = "Dynamic Mesh Refinement";
    
    public static final String DYNAMIC_OFF_NAME = "Dynamic" + OFF_LABEL;

    private Model model;
    private DynamicModule module;
    protected DynamicPanel panel;

    public DynamicSolutionView(Model model, DynamicModule module) {
        this.model = model;
        this.module = module;
    }

    @Override
    public void buildDynamic(PanelBuilder builder) {
        this.panel = new DynamicPanel();
        builder.addFill(panel);
    }

    @Override
    public boolean hasChanged(State state) {
        DynamicAlgorithmType dynamicAlgorithmType = DynamicAlgorithmType.byLabel(panel.getSelectedState());
        if (module.getDynamicData().getAlgorithm().getType() != dynamicAlgorithmType) {
            logger.info("DYNAMIC [{} -> {}]", module.getDynamicData().getAlgorithm(), dynamicAlgorithmType);
            return true;
        }
        return false;
    }

    @Override
    public void updateGUIFromState(State state) {
        panel.updateFromState(state, module.getDynamicData().getAlgorithm().getType());
        super.updateGUIFromState(state);
    }

    @Override
    public void updateStateFromGUI(State state) {
        DynamicAlgorithmType dynamicAlgorithmType = panel.getDynamicModel();
        if (state.isDynamic() != dynamicAlgorithmType.isOn()) {
            state.setDynamic(dynamicAlgorithmType.isOn());
        }
        if (module.getDynamicData().getAlgorithm().getType() != dynamicAlgorithmType) {
            if(dynamicAlgorithmType.isSolidRigidBody()){
                module.getDynamicData().setAlgorithm(new SolidBodyAlgorithm());
            } else if(dynamicAlgorithmType.isMultiRigidBody()){
                module.getDynamicData().setAlgorithm(new MultiBodyAlgorithm());
            } else if(dynamicAlgorithmType.is6DOF()){
                module.getDynamicData().setAlgorithm(new SixDoFDAlgorithm());
            } else if(dynamicAlgorithmType.isMeshRefine()){
                module.getDynamicData().setAlgorithm(new MeshRefineAlgorithm());
            } else {
                module.getDynamicData().setAlgorithm(new StaticAlgorithm());
            }
        }
    }

    @Override
    public void fixSolutionState(SolutionState ss) {
        if (ss.areSolverTypeAndTimeAndFlowAndTurbulenceChoosen()) {
            if ((ss.isSegregated() && ss.isSteady()) || ss.isHighMach()) {
                panel.select(DynamicAlgorithmType.OFF.getLabel());
                panel.setEnabled(false);
            } else {
                panel.setEnabled(true);
                if(ss.isCoupled()){
                    panel.disableChoice(SIX_DOF_LABEL);
                    panel.disableChoice(SOLID_RIGID_BODY_LABEL);
                } else {
                    panel.enableChoice(SIX_DOF_LABEL);
                    panel.enableChoice(SOLID_RIGID_BODY_LABEL);
                }
                
                if(model.getCellZones().isEmpty()){
                    panel.disableChoice(MULTI_RIGID_BODY_LABEL);
                } else {
                    panel.enableChoice(MULTI_RIGID_BODY_LABEL);
                }
            }
        } else {
            panel.select(DynamicAlgorithmType.OFF.getLabel());
            panel.setEnabled(false);
        }
    }

    @Override
    public void fixMultiphase(MultiphaseModel mm) {
        if (mm.isOn() && !mm.isDynamic()) {
            panel.select(DynamicAlgorithmType.OFF.getLabel());
            panel.setEnabled(false);
        }
    }

    @Override
    public void fixThermal(SolutionState ss, ThermalState ts) {
        if (ts.isBuoyancy()) {
            panel.select(DynamicAlgorithmType.OFF.getLabel());
            panel.setEnabled(false);
        }
    }

    public class DynamicPanel extends ChooserPanel {

        public DynamicPanel() {
            super("");
            addChoices();
        }

        protected void addChoices() {
            JRadioButton offChoice = addChoice(OFF_LABEL);
            offChoice.setName(DYNAMIC_OFF_NAME);
            addChoice(SIX_DOF_LABEL);
            addChoice(SOLID_RIGID_BODY_LABEL);
            addChoice(MULTI_RIGID_BODY_LABEL);
            addChoice(DYNAMIC_REFINE_FV_MESH_LABEL);
        }

        public void updateFromState(State state, DynamicAlgorithmType dynamicData) {
            select(dynamicData.getLabel());
        }

        public DynamicAlgorithmType getDynamicModel() {
            return DynamicAlgorithmType.byLabel(getSelectedState());
        }
        
        public void enableChoice(String label) {
            AbstractButton button = getButton(label);
            if (button != null){
                if (!button.isEnabled() && panel.isEnabled()) {
                    button.setEnabled(true);
                }
            }
        }

        public void disableChoice(String label) {
            AbstractButton button = getButton(label);
            if (button != null){
                if (button.isSelected()) {
                    select(OFF_LABEL);
                }
                if (button.isEnabled()) {
                    button.setEnabled(false);
                }
            }
        }
    }
}
