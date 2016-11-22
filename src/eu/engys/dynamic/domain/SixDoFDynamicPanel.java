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

import static eu.engys.dynamic.data.DynamicAlgorithmType.SIX_DOF;
import static eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm.ACCELERATION_RELAXATION;
import static eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm.ANGULAR_RESTRAINT;
import static eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm.CENTRE_OF_MASS;
import static eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm.INNER_DISTANCE;
import static eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm.LINEAR_RESTRAINT;
import static eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm.MASS;
import static eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm.MOMENT_OF_INERTIA;
import static eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm.OUTER_DISTANCE;
import static eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm.PATCHES;
import static eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm.ROTATION_CONSTRAINT;
import static eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm.TRANSLATION_CONSTRAINT;
import static eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm.VELOCITY;
import static eu.engys.dynamic.data.sixdof.constraint.translation.LineTranslationConstraint.DIRECTION;
import static eu.engys.dynamic.data.sixdof.constraint.translation.PlaneTranslationConstraint.NORMAL;
import static eu.engys.dynamic.data.sixdof.constraint.translation.PointTranslationConstraint.POINT;
import static eu.engys.dynamic.data.sixdof.restraint.linear.SpringLinearRestraint.ANCHOR;
import static eu.engys.dynamic.data.sixdof.restraint.linear.SpringLinearRestraint.REF_ATTACHMENT_PT;
import static eu.engys.dynamic.data.sixdof.restraint.linear.SpringLinearRestraint.REST_LENGTH;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import eu.engys.core.project.Model;
import eu.engys.dynamic.DynamicModule;
import eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm;
import eu.engys.dynamic.data.sixdof.constraint.rotation.AxisRotationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.rotation.FixOrientationRotationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.rotation.NoneRotationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.LineTranslationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.NoneTranslationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.PlaneTranslationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.PointTranslationConstraint;
import eu.engys.dynamic.data.sixdof.restraint.angular.DamperAngularRestraint;
import eu.engys.dynamic.data.sixdof.restraint.angular.NoneAngularRestraint;
import eu.engys.dynamic.data.sixdof.restraint.angular.SpringAngularRestraint;
import eu.engys.dynamic.data.sixdof.restraint.linear.DamperLinearRestraint;
import eu.engys.dynamic.data.sixdof.restraint.linear.NoneLinearRestraint;
import eu.engys.dynamic.data.sixdof.restraint.linear.SpringLinearRestraint;
import eu.engys.gui.ListBuilderFactory;
import eu.engys.util.Symbols;
import eu.engys.util.bean.BeanModel;
import eu.engys.util.bean.BeanPanelBuilder;
import eu.engys.util.ui.builder.PanelBuilder;

public class SixDoFDynamicPanel extends AbstractDynamicPanel {

    public static final String PATCHES_LABEL = "Moving Boundaries";
    public static final String INNER_DISTANCE_LABEL = "Inner Distance " + Symbols.M;
    public static final String OUTER_DISTANCE_LABEL = "Outer Distance " + Symbols.M;
    public static final String MASS_LABEL = "Mass " + Symbols.KG;
    public static final String CENTRE_OF_MASS_LABEL = "Centre of Mass " + Symbols.M;
    public static final String MOMENT_OF_INERTIA_LABEL = "Moment of Inertia " + Symbols.KGM2;
    public static final String VELOCITY_LABEL = "Velocity " + Symbols.M_S;
    public static final String ACCELERATION_RELAXATION_LABEL = "Acceleration Relaxation";

    public static final String TRANSLATION_CONSTRAINT_LABEL = "Translation Constraint";
    public static final String POINT_LABEL = "Point";
    public static final String LINE_LABEL = "Line";
    public static final String PLANE_LABEL = "Plane";
    public static final String NORMAL_LABEL = "Normal";
    public static final String DIRECTION_LABEL = "Direction";

    public static final String ROTATION_CONSTRAINT_LABEL = "Rotation Constraint";
    public static final String AXIS_LABEL = "Axis";
    public static final String FIX_ORIENTATION_LABEL = "Fix Orientation";

    public static final String LINEAR_RESTRAINT_LABEL = "Linear Restraint";
    public static final String COEFF_LINEAR_LABEL = "Damping " + Symbols.NS_M;
    public static final String STIFFNESS_LINEAR_LABEL = "Stiffness " + Symbols.N_M;
    public static final String DAMPING_LINEAR_LABEL = "Damping " + Symbols.NS_M;
    public static final String REF_ATTACHMENT_PT_LABEL = "Point of Attachment " + Symbols.M;
    public static final String REST_LENGTH_LABEL = "Rest Length " + Symbols.M;
    public static final String ANCHOR_LABEL = "Anchor " + Symbols.M;

    public static final String ANGULAR_RESTRAINT_LABEL = "Angular Restraint";
    public static final String COEFF_ANGULAR_LABEL = "Damping " + Symbols.NMS_RAD;
    public static final String STIFFNESS_ANGULAR_LABEL = "Stiffness " + Symbols.NM_RAD;
    public static final String DAMPING_ANGULAR_LABEL = "Damping " + Symbols.NMS_RAD;

    public static final String DAMPER_LABEL = "Damper";
    public static final String SPRING_LABEL = "Spring";
    public static final String NONE_LABEL = "None";

    private BeanModel<SixDoFDAlgorithm> beanModel;
    private BeanModel<NoneTranslationConstraint> noneTranslationConstraintBeanModel;
    private BeanModel<PointTranslationConstraint> pointTranslationConstraintBeanModel;
    private BeanModel<LineTranslationConstraint> lineTranslationConstraintBeanModel;
    private BeanModel<PlaneTranslationConstraint> planeTranslationConstraintBeanModel;

    private BeanModel<NoneRotationConstraint> noneRotationConstraintBeanModel;
    private BeanModel<AxisRotationConstraint> axisRotationConstraintBeanModel;
    private BeanModel<FixOrientationRotationConstraint> fixOrientationRotationConstraintBeanModel;

    private BeanModel<NoneLinearRestraint> noneLinearRestraintBeanModel;
    private BeanModel<DamperLinearRestraint> damperLinearRestraintBeanModel;
    private BeanModel<SpringLinearRestraint> springLinearRestraintBeanModel;

    private BeanModel<NoneAngularRestraint> noneAngularRestraintBeanModel;
    private BeanModel<DamperAngularRestraint> damperAngularRestraintBeanModel;
    private BeanModel<SpringAngularRestraint> springAngularRestraintBeanModel;

    private DynamicModule module;

    public SixDoFDynamicPanel(Model model, DynamicModule module) {
        super(model, SIX_DOF.getLabel());
        this.module = module;
        this.beanModel = new BeanModel<SixDoFDAlgorithm>(new SixDoFDAlgorithm());

        this.noneTranslationConstraintBeanModel = new BeanModel<NoneTranslationConstraint>(new NoneTranslationConstraint());
        this.pointTranslationConstraintBeanModel = new BeanModel<PointTranslationConstraint>(new PointTranslationConstraint());
        this.lineTranslationConstraintBeanModel = new BeanModel<LineTranslationConstraint>(new LineTranslationConstraint());
        this.planeTranslationConstraintBeanModel = new BeanModel<PlaneTranslationConstraint>(new PlaneTranslationConstraint());

        this.noneRotationConstraintBeanModel = new BeanModel<NoneRotationConstraint>(new NoneRotationConstraint());
        this.axisRotationConstraintBeanModel = new BeanModel<AxisRotationConstraint>(new AxisRotationConstraint());
        this.fixOrientationRotationConstraintBeanModel = new BeanModel<FixOrientationRotationConstraint>(new FixOrientationRotationConstraint());

        this.noneLinearRestraintBeanModel = new BeanModel<NoneLinearRestraint>(new NoneLinearRestraint());
        this.damperLinearRestraintBeanModel = new BeanModel<DamperLinearRestraint>(new DamperLinearRestraint());
        this.springLinearRestraintBeanModel = new BeanModel<SpringLinearRestraint>(new SpringLinearRestraint());

        this.noneAngularRestraintBeanModel = new BeanModel<NoneAngularRestraint>(new NoneAngularRestraint());
        this.damperAngularRestraintBeanModel = new BeanModel<DamperAngularRestraint>(new DamperAngularRestraint());
        this.springAngularRestraintBeanModel = new BeanModel<SpringAngularRestraint>(new SpringAngularRestraint());
    }

    @Override
    protected JComponent layoutComponents() {
        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(PATCHES_LABEL, beanModel.bindList(PATCHES, ListBuilderFactory.getPatchesListBuilder(model)));
        builder.addComponent(INNER_DISTANCE_LABEL, beanModel.bindDouble(INNER_DISTANCE));
        builder.addComponent(OUTER_DISTANCE_LABEL, beanModel.bindDouble(OUTER_DISTANCE));
        builder.addComponent(MASS_LABEL, beanModel.bindDouble(MASS));
        builder.addComponent(CENTRE_OF_MASS_LABEL, beanModel.bindPoint(CENTRE_OF_MASS));
        builder.addComponent(MOMENT_OF_INERTIA_LABEL, beanModel.bindPoint(MOMENT_OF_INERTIA));
        builder.addComponent(VELOCITY_LABEL, beanModel.bindPoint(VELOCITY));
        builder.addComponent(ACCELERATION_RELAXATION_LABEL, beanModel.bindDouble(ACCELERATION_RELAXATION));

        buildTranslationConstraintPanel(builder);
        buildRotationConstraintPanel(builder);
        buildLinearRestraintPanel(builder);
        buildAngularRestraintPanel(builder);

        return builder.removeMargins().getPanel();
    }

    @SuppressWarnings("unchecked")
    private void buildTranslationConstraintPanel(PanelBuilder builder) {
        BeanPanelBuilder translationBuilder = new BeanPanelBuilder();

        translationBuilder.startChoiceNoLabel(TRANSLATION_CONSTRAINT_LABEL, beanModel.bindComboController(TRANSLATION_CONSTRAINT, noneTranslationConstraintBeanModel, pointTranslationConstraintBeanModel, lineTranslationConstraintBeanModel, planeTranslationConstraintBeanModel));

        translationBuilder.startBean(NONE_LABEL, noneTranslationConstraintBeanModel);
        translationBuilder.endBean();

        translationBuilder.startBean(POINT_LABEL, pointTranslationConstraintBeanModel);
        translationBuilder.addComponent(POINT_LABEL, POINT_LABEL, pointTranslationConstraintBeanModel.bindPoint(POINT));
        translationBuilder.endBean();

        translationBuilder.startBean(LINE_LABEL, lineTranslationConstraintBeanModel);
        translationBuilder.addComponent(DIRECTION_LABEL, DIRECTION_LABEL, lineTranslationConstraintBeanModel.bindPoint(DIRECTION));
        translationBuilder.endBean();

        translationBuilder.startBean(PLANE_LABEL, planeTranslationConstraintBeanModel);
        translationBuilder.addComponent(NORMAL_LABEL, NORMAL_LABEL, planeTranslationConstraintBeanModel.bindPoint(NORMAL));
        translationBuilder.endBean();

        translationBuilder.endChoice();

        builder.addFill(translationBuilder.withTitle(TRANSLATION_CONSTRAINT_LABEL).getPanel());
    }

    @SuppressWarnings("unchecked")
    private void buildRotationConstraintPanel(PanelBuilder builder) {
        BeanPanelBuilder rotationBuilder = new BeanPanelBuilder();

        rotationBuilder.startChoiceNoLabel(ROTATION_CONSTRAINT_LABEL, beanModel.bindComboController(ROTATION_CONSTRAINT, noneRotationConstraintBeanModel, axisRotationConstraintBeanModel, fixOrientationRotationConstraintBeanModel));

        rotationBuilder.startBean(NONE_LABEL, noneRotationConstraintBeanModel);
        rotationBuilder.endBean();

        rotationBuilder.startBean(AXIS_LABEL, axisRotationConstraintBeanModel);
        rotationBuilder.addComponent(AXIS_LABEL, AXIS_LABEL, axisRotationConstraintBeanModel.bindPoint(AxisRotationConstraint.AXIS));
        rotationBuilder.endBean();

        rotationBuilder.startBean(FIX_ORIENTATION_LABEL, fixOrientationRotationConstraintBeanModel);
        rotationBuilder.endBean();

        rotationBuilder.endChoice();

        builder.addFill(rotationBuilder.withTitle(ROTATION_CONSTRAINT_LABEL).getPanel());
    }

    @SuppressWarnings("unchecked")
    private void buildLinearRestraintPanel(PanelBuilder builder) {
        BeanPanelBuilder linearBuilder = new BeanPanelBuilder();

        linearBuilder.startChoiceNoLabel(LINEAR_RESTRAINT_LABEL, beanModel.bindComboController(LINEAR_RESTRAINT, noneLinearRestraintBeanModel, damperLinearRestraintBeanModel, springLinearRestraintBeanModel));

        linearBuilder.startBean(NONE_LABEL, noneLinearRestraintBeanModel);
        linearBuilder.endBean();

        linearBuilder.startBean(DAMPER_LABEL, damperLinearRestraintBeanModel);
        linearBuilder.addComponent(COEFF_LINEAR_LABEL, COEFF_LINEAR_LABEL, damperLinearRestraintBeanModel.bindDouble(DamperLinearRestraint.COEFF));
        linearBuilder.endBean();

        linearBuilder.startBean(SPRING_LABEL, springLinearRestraintBeanModel);
        linearBuilder.addComponent(ANCHOR_LABEL, ANCHOR_LABEL, springLinearRestraintBeanModel.bindPoint(ANCHOR));
        linearBuilder.addComponent(REF_ATTACHMENT_PT_LABEL, REF_ATTACHMENT_PT_LABEL, springLinearRestraintBeanModel.bindPoint(REF_ATTACHMENT_PT));
        linearBuilder.addComponent(STIFFNESS_LINEAR_LABEL, STIFFNESS_LINEAR_LABEL, springLinearRestraintBeanModel.bindDouble(SpringLinearRestraint.STIFFNESS));
        linearBuilder.addComponent(DAMPING_LINEAR_LABEL, DAMPING_LINEAR_LABEL, springLinearRestraintBeanModel.bindDouble(SpringLinearRestraint.DAMPING));
        linearBuilder.addComponent(REST_LENGTH_LABEL, REST_LENGTH_LABEL, springLinearRestraintBeanModel.bindDouble(REST_LENGTH));
        linearBuilder.endBean();

        linearBuilder.endChoice();

        builder.addFill(linearBuilder.withTitle(LINEAR_RESTRAINT_LABEL).getPanel());
    }

    @SuppressWarnings("unchecked")
    private void buildAngularRestraintPanel(PanelBuilder builder) {
        BeanPanelBuilder angularBuilder = new BeanPanelBuilder();

        angularBuilder.startChoiceNoLabel(ANGULAR_RESTRAINT_LABEL, beanModel.bindComboController(ANGULAR_RESTRAINT, noneAngularRestraintBeanModel, damperAngularRestraintBeanModel, springAngularRestraintBeanModel));

        angularBuilder.startBean(NONE_LABEL, noneAngularRestraintBeanModel);
        angularBuilder.endBean();

        angularBuilder.startBean(DAMPER_LABEL, damperAngularRestraintBeanModel);
        angularBuilder.addComponent(COEFF_ANGULAR_LABEL, COEFF_ANGULAR_LABEL, damperAngularRestraintBeanModel.bindDouble(DamperAngularRestraint.COEFF));
        angularBuilder.endBean();

        angularBuilder.startBean(SPRING_LABEL, springAngularRestraintBeanModel);
        angularBuilder.addComponent(STIFFNESS_ANGULAR_LABEL, STIFFNESS_ANGULAR_LABEL, springAngularRestraintBeanModel.bindDouble(SpringAngularRestraint.STIFFNESS));
        angularBuilder.addComponent(DAMPING_ANGULAR_LABEL, DAMPING_ANGULAR_LABEL, springAngularRestraintBeanModel.bindDouble(SpringAngularRestraint.DAMPING));
        angularBuilder.endBean();

        angularBuilder.endChoice();

        builder.addFill(angularBuilder.withTitle(ANGULAR_RESTRAINT_LABEL).getPanel());

    }

    @Override
    public void load() {
        if (module.getDynamicData().getAlgorithm().getType().is6DOF()) {
            SixDoFDAlgorithm algo = (SixDoFDAlgorithm) module.getDynamicData().getAlgorithm().copy();
            fixPatches(algo);
            beanModel.setBean(algo);
        }
    }

    private void fixPatches(SixDoFDAlgorithm algo) {
        List<String> fixedPatches = new LinkedList<String>();
        for (String p : algo.getPatches()) {
            if (model.getPatches().getPatchByName(p) != null) {
                fixedPatches.add(p);
            }
        }
        if (fixedPatches.size() != algo.getPatches().length) {
            algo.setPatches(fixedPatches.toArray(new String[0]));
        }
    }

    @Override
    public void save() {
        if (module.getDynamicData().getAlgorithm().getType().is6DOF()) {
            module.getDynamicData().setAlgorithm(beanModel.getBean().copy());
        }
    }

}
