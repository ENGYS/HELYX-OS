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
package eu.engys.dynamic.domain;

import static eu.engys.dynamic.data.DynamicAlgorithmType.SOLID_RIGID_BODY;
import static eu.engys.dynamic.data.singlebody.SolidBodyAlgorithm.FUNCTION;

import javax.swing.JComponent;

import eu.engys.core.project.Model;
import eu.engys.dynamic.DynamicModule;
import eu.engys.dynamic.data.singlebody.SolidBodyAlgorithm;
import eu.engys.dynamic.data.singlebody.functions.AxisRotationMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.LinearMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.OscillatingLinearMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.OscillatingRotatingMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.RotatingMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.RotatingStepMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.SDAMotionFunction;
import eu.engys.util.Symbols;
import eu.engys.util.bean.BeanModel;
import eu.engys.util.bean.BeanPanelBuilder;
import eu.engys.util.ui.JComboBoxWithItemsSupport;

public class SolidBodyDynamicPanel extends AbstractDynamicPanel {

    public static final String TYPE_LABEL = "Type";
    public static final String TYPE_TOOLTIP = "Rigid Body Motion Type";

    public static final String LINEAR_MOTION_LABEL = "Linear Motion";
    public static final String ROTATING_MOTION_LABEL = "Rotating Motion";
    public static final String ROTATING_STEP_MOTION_LABEL = "Rotating Step Motion";
    public static final String AXIS_ROTATION_MOTION_LABEL = "Axis Rotation Motion";
    public static final String OSCILLATING_LINEAR_MOTION_LABEL = "Oscillating Linear Motion";
    public static final String OSCILLATING_ROTATING_MOTION_LABEL = "Oscillating Rotating Motion";
    public static final String SDA_LABEL = "Ship Tank Sloshing";

    public static final String VELOCITY_LABEL = "Velocity " + Symbols.M_S;
    public static final String AMPLITUDE_LABEL = "Amplitude " + Symbols.M;
    public static final String CENTRE_OF_ROTATION_LABEL = "Centre of Rotation " + Symbols.M;
    public static final String AXIS_OF_ROTATION_LABEL = "Axis of Rotation " + Symbols.M;
    public static final String ROTATIONAL_SPEED_RAD_S_LABEL = "Rotational Speed " + Symbols.OMEGA_SYMBOL_RAD;
    public static final String STARTING_TIME_LABEL = "Starting Time " + Symbols.S;

    public static final String THETA_LABEL = "Phase " + Symbols.DEG;
    public static final String PERIOD_LABEL = "Period " + Symbols.S;

    public static final String LAMBDA_LABEL = "Model Scale Ratio";
    public static final String ROLL_AMIN_LABEL = "Min Roll Amplitude " + Symbols.RAD;
    public static final String ROLL_AMAX_LABEL = "Max Roll Amplitude " + Symbols.RAD;
    public static final String SWAY_A_LABEL = "Sway Amplitude " + Symbols.M;
    public static final String HEAVE_A_LABEL = "Heave Amplitude " + Symbols.M;
    public static final String TP_LABEL = "Time Period for Liquid " + Symbols.S;
    public static final String TPN_LABEL = "Natural Period of Ship " + Symbols.S;
    public static final String DTI_LABEL = "Current Roll Period " + Symbols.S;
    public static final String DTP_LABEL = "Increment";
    public static final String Q_LABEL = "Damping Coefficient";
    public static final String COFG_LABEL = "Centre of Gravity " + Symbols.M;
    
    protected BeanModel<SolidBodyAlgorithm> mainModel;
    protected BeanModel<LinearMotionFunction> linearMotionModel;
    protected BeanModel<RotatingMotionFunction> rotatingMotionModel;
    private BeanModel<RotatingStepMotionFunction> rotatingStepMotionModel;
    private BeanModel<AxisRotationMotionFunction> axisRotationMotionModel;
    protected BeanModel<OscillatingLinearMotionFunction> oscillatingLinearMotionModel;
    protected BeanModel<OscillatingRotatingMotionFunction> oscillatingRotatingMotionModel;
    protected BeanModel<SDAMotionFunction> sdaMotionModel;
    
    protected Model model;
    private DynamicModule module;
    protected JComboBoxWithItemsSupport<BeanModel> typeCombo;

    public SolidBodyDynamicPanel(Model model, DynamicModule module) {
        super(model, SOLID_RIGID_BODY.getLabel());
        this.model = model;
        this.module = module;
        this.mainModel = new BeanModel<SolidBodyAlgorithm>(new SolidBodyAlgorithm());
        this.linearMotionModel = new BeanModel<LinearMotionFunction>(new LinearMotionFunction());
        this.rotatingMotionModel = new BeanModel<RotatingMotionFunction>(new RotatingMotionFunction());
        this.rotatingStepMotionModel = new BeanModel<RotatingStepMotionFunction>(new RotatingStepMotionFunction());
        this.axisRotationMotionModel = new BeanModel<AxisRotationMotionFunction>(new AxisRotationMotionFunction());
        this.oscillatingLinearMotionModel = new BeanModel<OscillatingLinearMotionFunction>(new OscillatingLinearMotionFunction());
        this.oscillatingRotatingMotionModel = new BeanModel<OscillatingRotatingMotionFunction>(new OscillatingRotatingMotionFunction());
        this.sdaMotionModel = new BeanModel<SDAMotionFunction>(new SDAMotionFunction());
    }
    
    protected BeanModel[] getBeanModels(){
        BeanModel[] models = new BeanModel[7];
        models[0] = linearMotionModel;
        models[1] = rotatingMotionModel;
        models[2] = rotatingStepMotionModel;
        models[3] = axisRotationMotionModel;
        models[4] = oscillatingLinearMotionModel;
        models[5] = oscillatingRotatingMotionModel;
        models[6] = sdaMotionModel;
        return models;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public JComponent layoutComponents() {
        BeanPanelBuilder builder = new BeanPanelBuilder();
        typeCombo = (JComboBoxWithItemsSupport) builder.startChoice(TYPE_LABEL, mainModel.bindComboController(FUNCTION, getBeanModels()), TYPE_TOOLTIP);

        builder.startBean(LINEAR_MOTION_LABEL, linearMotionModel);
        builder.addComponent(VELOCITY_LABEL, linearMotionModel.bindPoint(LinearMotionFunction.VELOCITY));
        builder.endBean();

        builder.startBean(ROTATING_MOTION_LABEL, rotatingMotionModel);
        builder.addComponent(CENTRE_OF_ROTATION_LABEL, rotatingMotionModel.bindPoint(RotatingMotionFunction.ORIGIN));
        builder.addComponent(AXIS_OF_ROTATION_LABEL, rotatingMotionModel.bindPoint(RotatingMotionFunction.AXIS));
        builder.addComponent(ROTATIONAL_SPEED_RAD_S_LABEL, rotatingMotionModel.bindDouble(RotatingMotionFunction.OMEGA));
        builder.addComponent(STARTING_TIME_LABEL, rotatingMotionModel.bindDouble(RotatingMotionFunction.T0));
        builder.endBean();

        addEngysTypes(builder);

        builder.startBean(OSCILLATING_LINEAR_MOTION_LABEL, oscillatingLinearMotionModel);
        builder.addComponent(AMPLITUDE_LABEL, oscillatingLinearMotionModel.bindPoint(OscillatingLinearMotionFunction.AMPLITUDE));
        builder.addComponent(ROTATIONAL_SPEED_RAD_S_LABEL, oscillatingLinearMotionModel.bindDouble(OscillatingLinearMotionFunction.OMEGA));
        builder.endBean();

        builder.startBean(OSCILLATING_ROTATING_MOTION_LABEL, oscillatingRotatingMotionModel);
        builder.addComponent(CENTRE_OF_ROTATION_LABEL, oscillatingRotatingMotionModel.bindPoint(OscillatingRotatingMotionFunction.ORIGIN));
        builder.addComponent(AMPLITUDE_LABEL, oscillatingRotatingMotionModel.bindPoint(OscillatingRotatingMotionFunction.AMPLITUDE));
        builder.addComponent(ROTATIONAL_SPEED_RAD_S_LABEL, oscillatingRotatingMotionModel.bindDouble(OscillatingRotatingMotionFunction.OMEGA));
        builder.endBean();

        builder.startBean(SDA_LABEL, sdaMotionModel);
        builder.addComponent(LAMBDA_LABEL, sdaMotionModel.bindDouble(SDAMotionFunction.LAMBDA));
        builder.addComponent(ROLL_AMIN_LABEL, sdaMotionModel.bindDouble(SDAMotionFunction.ROLL_AMIN));
        builder.addComponent(ROLL_AMAX_LABEL, sdaMotionModel.bindDouble(SDAMotionFunction.ROLL_AMAX));
        builder.addComponent(SWAY_A_LABEL, sdaMotionModel.bindDouble(SDAMotionFunction.SWAY_A));
        builder.addComponent(HEAVE_A_LABEL, sdaMotionModel.bindDouble(SDAMotionFunction.HEAVE_A));
        builder.addComponent(TP_LABEL, sdaMotionModel.bindDouble(SDAMotionFunction.TP));
        builder.addComponent(TPN_LABEL, sdaMotionModel.bindDouble(SDAMotionFunction.TPN));
        builder.addComponent(DTI_LABEL, sdaMotionModel.bindDouble(SDAMotionFunction.DTI));
        builder.addComponent(DTP_LABEL, sdaMotionModel.bindDouble(SDAMotionFunction.DTP));
        builder.addComponent(Q_LABEL, sdaMotionModel.bindDouble(SDAMotionFunction.Q));
        builder.addComponent(COFG_LABEL, sdaMotionModel.bindPoint(SDAMotionFunction.COFG));
        builder.endBean();

        builder.endChoice();
        
        return builder.removeMargins().getPanel();
    }

    protected void addEngysTypes(BeanPanelBuilder builder) {
        builder.startBean(ROTATING_STEP_MOTION_LABEL, rotatingStepMotionModel);
        builder.addComponent(CENTRE_OF_ROTATION_LABEL, rotatingStepMotionModel.bindPoint(RotatingStepMotionFunction.ORIGIN));
        builder.addComponent(AXIS_OF_ROTATION_LABEL, rotatingStepMotionModel.bindPoint(RotatingStepMotionFunction.AXIS));
        builder.addComponent(THETA_LABEL, rotatingStepMotionModel.bindDouble(RotatingStepMotionFunction.THETA));
        builder.addComponent(PERIOD_LABEL, rotatingStepMotionModel.bindInteger(RotatingStepMotionFunction.PERIOD));
        builder.endBean();

        builder.startBean(AXIS_ROTATION_MOTION_LABEL, axisRotationMotionModel);
        builder.addComponent(CENTRE_OF_ROTATION_LABEL, axisRotationMotionModel.bindPoint(AxisRotationMotionFunction.ORIGIN));
        builder.addComponent(AXIS_OF_ROTATION_LABEL, axisRotationMotionModel.bindPoint(AxisRotationMotionFunction.AXIS));
        builder.addComponent(ROTATIONAL_SPEED_RAD_S_LABEL, axisRotationMotionModel.bindDouble(AxisRotationMotionFunction.OMEGA));
        builder.endBean();
    }
    
    public BeanModel<SolidBodyAlgorithm> getMainModel() {
        return mainModel;
    }
    
    @Override
    public void load() {
        if(module.getDynamicData().getAlgorithm().getType().isSolidRigidBody()){
            mainModel.setBean((SolidBodyAlgorithm) module.getDynamicData().getAlgorithm().copy());
        }
    }
    
    @Override
    public void save() {
        if(module.getDynamicData().getAlgorithm().getType().isSolidRigidBody()){
            module.getDynamicData().setAlgorithm(mainModel.getBean().copy());
        }
    }

}
