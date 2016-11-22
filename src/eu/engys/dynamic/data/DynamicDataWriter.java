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
package eu.engys.dynamic.data;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.dynamic.DynamicMeshDict.ACCELERATION_RELAXATION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.AMPLITUDE_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ANCHOR_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ANGULAR_RESTRAINT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.AXIS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.AXIS_ROTATION_MOTION_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.AXIS_ROTATION_MOTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.CENTRE_OF_MASS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.CENTRE_OF_ROTATION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.COEFF_KEY;
import static eu.engys.dynamic.DynamicMeshDict.COFG_KEY;
import static eu.engys.dynamic.DynamicMeshDict.CONSTRAINTS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.CORRECT_FLUXES_KEY;
import static eu.engys.dynamic.DynamicMeshDict.CORRECT_FLUXES_VALUE;
import static eu.engys.dynamic.DynamicMeshDict.DAMPING_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DIRECTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DTI_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DTP_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DUMP_LEVEL_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DYNAMIC_FV_MESH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DYNAMIC_MESH_DICT;
import static eu.engys.dynamic.DynamicMeshDict.DYNAMIC_MOTION_SOLVER_FV_MESH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DYNAMIC_REFINE_FV_MESH_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DYNAMIC_REFINE_FV_MESH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.FIELD_KEY;
import static eu.engys.dynamic.DynamicMeshDict.HEAVE_A_KEY;
import static eu.engys.dynamic.DynamicMeshDict.INNER_DISTANCE_KEY;
import static eu.engys.dynamic.DynamicMeshDict.LAMDA_KEY;
import static eu.engys.dynamic.DynamicMeshDict.LINEAR_DAMPER_KEY;
import static eu.engys.dynamic.DynamicMeshDict.LINEAR_MOTION_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.LINEAR_MOTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.LINEAR_RESTRAINT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.LINEAR_SPRING_KEY;
import static eu.engys.dynamic.DynamicMeshDict.LINE_KEY;
import static eu.engys.dynamic.DynamicMeshDict.LOWER_REFINE_LEVEL_KEY;
import static eu.engys.dynamic.DynamicMeshDict.MASS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.MAX_CELLS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.MAX_REFINEMENT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.MOMENT_OF_INERTIA_KEY;
import static eu.engys.dynamic.DynamicMeshDict.MOTION_SOLVER_LIBS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.MULTIBODY_DYNAMIC_LIBRARIES;
import static eu.engys.dynamic.DynamicMeshDict.MULTI_SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.MULTI_SOLID_BODY_MOTION_FV_MESH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.NEWMARK_KEY;
import static eu.engys.dynamic.DynamicMeshDict.NORMAL_KEY;
import static eu.engys.dynamic.DynamicMeshDict.N_BUFFER_LAYERS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.OMEGA_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ORIENTATION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ORIGIN_KEY;
import static eu.engys.dynamic.DynamicMeshDict.OSCILLATING_LINEAR_MOTION_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.OSCILLATING_LINEAR_MOTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.OSCILLATING_ROTATING_MOTION_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.OSCILLATING_ROTATING_MOTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.OUTER_DISTANCE_KEY;
import static eu.engys.dynamic.DynamicMeshDict.PATCHES_KEY;
import static eu.engys.dynamic.DynamicMeshDict.PERIOD_KEY;
import static eu.engys.dynamic.DynamicMeshDict.PLANE_KEY;
import static eu.engys.dynamic.DynamicMeshDict.POINT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.Q_KEY;
import static eu.engys.dynamic.DynamicMeshDict.RADIAL_VELOCITY_KEY;
import static eu.engys.dynamic.DynamicMeshDict.REFINE_INTERVAL_KEY;
import static eu.engys.dynamic.DynamicMeshDict.REF_ATTACHMENT_PT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.REPORT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.RESTRAINTS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.REST_LENGTH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.RHO_INF_KEY;
import static eu.engys.dynamic.DynamicMeshDict.RHO_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ROLL_A_MAX_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ROLL_A_MIN_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ROTATING_MOTION_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ROTATING_MOTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ROTATING_STEP_MOTION_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ROTATING_STEP_MOTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ROTATION_CONSTRAINT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SDA_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SDA_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SIX_DOF_DYNAMIC_LIBRARIES;
import static eu.engys.dynamic.DynamicMeshDict.SIX_DOF_RIGID_BODY_MOTION_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SIX_DOF_RIGID_BODY_MOTION_CONSTRAINT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SIX_DOF_RIGID_BODY_MOTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SOLID_BODY_MOTION_FUNCTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SOLID_BODY_MOTION_FV_MESH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SOLVER_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SPHERICAL_ANGULAR_DAMPER_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SPHERICAL_ANGULAR_SPRING_KEY;
import static eu.engys.dynamic.DynamicMeshDict.STATIC_FV_MESH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.STIFFNESS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SWAY_A_KEY;
import static eu.engys.dynamic.DynamicMeshDict.T0_KEY;
import static eu.engys.dynamic.DynamicMeshDict.THETA_KEY;
import static eu.engys.dynamic.DynamicMeshDict.TPN_KEY;
import static eu.engys.dynamic.DynamicMeshDict.TP_KEY;
import static eu.engys.dynamic.DynamicMeshDict.TRANSLATION_CONSTRAINT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.UNREFINE_LEVEL_KEY;
import static eu.engys.dynamic.DynamicMeshDict.UPPER_REFINE_LEVEL_KEY;
import static eu.engys.dynamic.DynamicMeshDict.VELOCITY_KEY;

import org.apache.commons.math3.util.MathArrays;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.dynamic.data.multibody.MultiBodyAlgorithm;
import eu.engys.dynamic.data.refine.MeshRefineAlgorithm;
import eu.engys.dynamic.data.singlebody.SolidBodyAlgorithm;
import eu.engys.dynamic.data.singlebody.SolidBodyMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.AxisRotationMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.LinearMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.OscillatingLinearMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.OscillatingRotatingMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.RotatingMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.RotatingStepMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.SDAMotionFunction;
import eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm;
import eu.engys.dynamic.data.sixdof.constraint.rotation.AxisRotationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.rotation.RotationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.LineTranslationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.PlaneTranslationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.PointTranslationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.TranslationConstraint;
import eu.engys.dynamic.data.sixdof.restraint.angular.AngularRestraint;
import eu.engys.dynamic.data.sixdof.restraint.angular.DamperAngularRestraint;
import eu.engys.dynamic.data.sixdof.restraint.angular.SpringAngularRestraint;
import eu.engys.dynamic.data.sixdof.restraint.linear.DamperLinearRestraint;
import eu.engys.dynamic.data.sixdof.restraint.linear.LinearRestraint;
import eu.engys.dynamic.data.sixdof.restraint.linear.SpringLinearRestraint;

public class DynamicDataWriter {

    private Model model;

    public DynamicDataWriter(Model model) {
        this.model = model;
    }

    public Dictionary writeAlgorithm(DynamicAlgorithm algo) {
        Dictionary dynamicMeshDict = new Dictionary(DYNAMIC_MESH_DICT);
        switch (algo.getType()) {
            case SOLID_RIGID_BODY:
                writeSingleBodyAlgorithm(algo, dynamicMeshDict);
                break;
            case MULTI_RIGID_BODY:
                writeMultiBodyAlgorithm(algo, dynamicMeshDict);
                break;
            case SIX_DOF:
                writeSixDofAlgorithm(algo, dynamicMeshDict);
                break;
            case MESH_REFINE:
                writeDynamicRefineAlgorithm(algo, dynamicMeshDict);
                break;
            default:
                writeStaticAlgorithm(algo, dynamicMeshDict);
                break;
        }
        return dynamicMeshDict;
    }

    private void writeStaticAlgorithm(DynamicAlgorithm algo, Dictionary dynamicMeshDict) {
        dynamicMeshDict.add(DYNAMIC_FV_MESH_KEY, STATIC_FV_MESH_KEY);
    }

    void writeSingleBodyAlgorithm(DynamicAlgorithm a, Dictionary dynamicMeshDict) {
        SolidBodyAlgorithm algo = (SolidBodyAlgorithm) a;

        Dictionary coeffsDict = new Dictionary(SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY);
        writeSingleBodyFunction(algo.getFunction(), coeffsDict);

        dynamicMeshDict.add(DYNAMIC_FV_MESH_KEY, SOLID_BODY_MOTION_FV_MESH_KEY);
        dynamicMeshDict.add(coeffsDict);
    }

    private void writeSingleBodyFunction(SolidBodyMotionFunction function, Dictionary coeffsDict) {
        switch (function.getFunctionType()) {
            case LINEAR_MOTION: {
                writeLinear(function, coeffsDict);
                break;
            }
            case ROTATING_MOTION: {
                writeRotating(function, coeffsDict);
                break;
            }
            case ROTATING_STEP_MOTION: {
                writeRotatingStep(function, coeffsDict);
                break;
            }
            case AXIS_ROTATION_MOTION: {
                writeAxisRotation(function, coeffsDict);
                break;
            }
            case OSCILLATING_LINEAR_MOTION: {
                writeOscillatingLinear(function, coeffsDict);
                break;
            }
            case OSCILLATING_ROTATING_MOTION: {
                writeOscillatingRotating(function, coeffsDict);
                break;
            }
            case SDA_MOTION: {
                writeSDA(function, coeffsDict);
                break;
            }
            default:
                break;
        }
    }

    void writeLinear(SolidBodyMotionFunction function, Dictionary coeffsDict) {
        LinearMotionFunction f = (LinearMotionFunction) function;
        Dictionary functionDict = new Dictionary(LINEAR_MOTION_COEFFS_KEY);
        functionDict.add(VELOCITY_KEY, f.getVelocity());

        coeffsDict.add(SOLID_BODY_MOTION_FUNCTION_KEY, LINEAR_MOTION_KEY);
        coeffsDict.add(functionDict);
    }

    void writeRotating(SolidBodyMotionFunction function, Dictionary coeffsDict) {
        RotatingMotionFunction f = (RotatingMotionFunction) function;
        Dictionary functionDict = new Dictionary(ROTATING_MOTION_COEFFS_KEY);
        functionDict.add(ORIGIN_KEY, f.getOrigin());
        functionDict.add(AXIS_KEY, f.getAxis());
        functionDict.add(OMEGA_KEY, f.getOmega());
        functionDict.add(T0_KEY, f.getT0());

        coeffsDict.add(SOLID_BODY_MOTION_FUNCTION_KEY, ROTATING_MOTION_KEY);
        coeffsDict.add(functionDict);
    }

    void writeRotatingStep(SolidBodyMotionFunction function, Dictionary coeffsDict) {
        RotatingStepMotionFunction f = (RotatingStepMotionFunction) function;
        Dictionary functionDict = new Dictionary(ROTATING_STEP_MOTION_COEFFS_KEY);
        functionDict.add(ORIGIN_KEY, f.getOrigin());
        functionDict.add(AXIS_KEY, f.getAxis());
        functionDict.add(THETA_KEY, f.getTheta());
        functionDict.add(PERIOD_KEY, f.getPeriod());

        coeffsDict.add(SOLID_BODY_MOTION_FUNCTION_KEY, ROTATING_STEP_MOTION_KEY);
        coeffsDict.add(functionDict);
    }

    void writeAxisRotation(SolidBodyMotionFunction function, Dictionary coeffsDict) {
        AxisRotationMotionFunction f = (AxisRotationMotionFunction) function;
        Dictionary functionDict = new Dictionary(AXIS_ROTATION_MOTION_COEFFS_KEY);
        functionDict.add(ORIGIN_KEY, f.getOrigin());
        functionDict.add(RADIAL_VELOCITY_KEY, MathArrays.scale(Math.toDegrees(f.getOmega()), f.getAxis()));

        coeffsDict.add(SOLID_BODY_MOTION_FUNCTION_KEY, AXIS_ROTATION_MOTION_KEY);
        coeffsDict.add(functionDict);
    }

    void writeOscillatingLinear(SolidBodyMotionFunction function, Dictionary coeffsDict) {
        OscillatingLinearMotionFunction f = (OscillatingLinearMotionFunction) function;
        Dictionary functionDict = new Dictionary(OSCILLATING_LINEAR_MOTION_COEFFS_KEY);
        functionDict.add(AMPLITUDE_KEY, f.getAmplitude());
        functionDict.add(OMEGA_KEY, f.getOmega());

        coeffsDict.add(SOLID_BODY_MOTION_FUNCTION_KEY, OSCILLATING_LINEAR_MOTION_KEY);
        coeffsDict.add(functionDict);
    }

    void writeOscillatingRotating(SolidBodyMotionFunction function, Dictionary coeffsDict) {
        OscillatingRotatingMotionFunction f = (OscillatingRotatingMotionFunction) function;
        Dictionary functionDict = new Dictionary(OSCILLATING_ROTATING_MOTION_COEFFS_KEY);
        functionDict.add(AMPLITUDE_KEY, f.getAmplitude());
        functionDict.add(OMEGA_KEY, f.getOmega());
        functionDict.add(ORIGIN_KEY, f.getOrigin());

        coeffsDict.add(SOLID_BODY_MOTION_FUNCTION_KEY, OSCILLATING_ROTATING_MOTION_KEY);
        coeffsDict.add(functionDict);
    }

    void writeSDA(SolidBodyMotionFunction function, Dictionary coeffsDict) {
        SDAMotionFunction f = (SDAMotionFunction) function;
        Dictionary functionDict = new Dictionary(SDA_COEFFS_KEY);
        functionDict.add(TPN_KEY, f.getTpn());
        functionDict.add(TP_KEY, f.getTp());
        functionDict.add(SWAY_A_KEY, f.getSwayA());
        functionDict.add(ROLL_A_MIN_KEY, f.getRollAmin());
        functionDict.add(Q_KEY, f.getQ());
        functionDict.add(LAMDA_KEY, f.getLambda());
        functionDict.add(HEAVE_A_KEY, f.getHeaveA());
        functionDict.add(DTI_KEY, f.getDti());
        functionDict.add(ROLL_A_MAX_KEY, f.getRollAmax());
        functionDict.add(DTP_KEY, f.getDtp());
        functionDict.add(COFG_KEY, f.getCofg());

        coeffsDict.add(SOLID_BODY_MOTION_FUNCTION_KEY, SDA_KEY);
        coeffsDict.add(functionDict);
    }

    void writeMultiBodyAlgorithm(DynamicAlgorithm a, Dictionary dynamicMeshDict) {
        MultiBodyAlgorithm algo = (MultiBodyAlgorithm) a;

        Dictionary coeffsDict = new Dictionary(MULTI_SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY);

        for (String czName : algo.getSingleBodyAlgorithms().keySet()) {
            Dictionary czDict = new Dictionary(czName);
            writeSingleBodyFunction(algo.getSingleBodyAlgorithms().get(czName).getFunction(), czDict);
            coeffsDict.add(czDict);
        }

        dynamicMeshDict.add(DYNAMIC_FV_MESH_KEY, MULTI_SOLID_BODY_MOTION_FV_MESH_KEY);
        dynamicMeshDict.add(coeffsDict);
        dynamicMeshDict.add(MOTION_SOLVER_LIBS_KEY, MULTIBODY_DYNAMIC_LIBRARIES);
    }

    void writeSixDofAlgorithm(DynamicAlgorithm a, Dictionary dynamicMeshDict) {
        SixDoFDAlgorithm algo = (SixDoFDAlgorithm) a;

        Dictionary coeffsDict = new Dictionary(SIX_DOF_RIGID_BODY_MOTION_COEFFS_KEY);
        coeffsDict.add(MASS_KEY, algo.getMass());
        coeffsDict.add(PATCHES_KEY, algo.getPatches());
        coeffsDict.add(ACCELERATION_RELAXATION_KEY, algo.getAccelerationRelaxation());
        coeffsDict.add(REPORT_KEY, true);
        coeffsDict.add(VELOCITY_KEY, algo.getVelocity());
        coeffsDict.add(MOMENT_OF_INERTIA_KEY, algo.getMomentOfInertia());
        coeffsDict.add(CENTRE_OF_MASS_KEY, algo.getCentreOfMass());
        coeffsDict.add(OUTER_DISTANCE_KEY, algo.getOuterDistance());
        coeffsDict.add(INNER_DISTANCE_KEY, algo.getInnerDistance());
        
        if (model.getState().isCompressible() || model.getState().getMultiphaseModel().isMultiphase()) {
            coeffsDict.add(RHO_INF_KEY, 1);
        } else if (model.getState().isIncompressible()) {
            coeffsDict.add(RHO_KEY, RHO_INF_KEY);
            coeffsDict.add(RHO_INF_KEY, 1);
        }
        
        
        //OS
        coeffsDict.add(new Dictionary(SOLVER_KEY));
        coeffsDict.subDict(SOLVER_KEY).add(TYPE, NEWMARK_KEY);

        Dictionary constraintsDict = new Dictionary(CONSTRAINTS_KEY);
        writeTranslationConstraint(algo.getTranslationConstraint(), constraintsDict);
        writeRotationConstraint(algo.getRotationConstraint(), constraintsDict);
        coeffsDict.add(constraintsDict);

        Dictionary restraintsDict = new Dictionary(RESTRAINTS_KEY);
        writeLinearRestraint(algo.getLinearRestraint(), restraintsDict);
        writeAngularRestraint(algo.getAngularRestraint(), restraintsDict);
        coeffsDict.add(restraintsDict);

        dynamicMeshDict.add(DYNAMIC_FV_MESH_KEY, DYNAMIC_MOTION_SOLVER_FV_MESH_KEY);
        dynamicMeshDict.add(SOLVER_KEY, SIX_DOF_RIGID_BODY_MOTION_KEY);
        dynamicMeshDict.add(coeffsDict);
        dynamicMeshDict.add(MOTION_SOLVER_LIBS_KEY, SIX_DOF_DYNAMIC_LIBRARIES);
    }

    void writeDynamicRefineAlgorithm(DynamicAlgorithm a, Dictionary dynamicMeshDict) {
        MeshRefineAlgorithm algo = (MeshRefineAlgorithm) a;

        Dictionary coeffsDict = new Dictionary(DYNAMIC_REFINE_FV_MESH_COEFFS_KEY);
        coeffsDict.add(REFINE_INTERVAL_KEY, algo.getRefineInterval());
        coeffsDict.add(FIELD_KEY, algo.getField());
        coeffsDict.add(LOWER_REFINE_LEVEL_KEY, algo.getLowerRefineLevel());
        coeffsDict.add(UPPER_REFINE_LEVEL_KEY, algo.getUpperRefineLevel());
        coeffsDict.add(UNREFINE_LEVEL_KEY, algo.getUnrefineLevel());
        coeffsDict.add(N_BUFFER_LAYERS_KEY, algo.getnBufferLayers());
        coeffsDict.add(MAX_REFINEMENT_KEY, algo.getMaxRefinement());
        coeffsDict.add(MAX_CELLS_KEY, algo.getMaxCells());
        coeffsDict.add(CORRECT_FLUXES_KEY, CORRECT_FLUXES_VALUE);
        coeffsDict.add(DUMP_LEVEL_KEY, true);

        dynamicMeshDict.add(DYNAMIC_FV_MESH_KEY, DYNAMIC_REFINE_FV_MESH_KEY);
        dynamicMeshDict.add(coeffsDict);
    }

    void writeTranslationConstraint(TranslationConstraint constraint, Dictionary constraintsDict) {
        if (constraint.getType().isNone()) {
            // do nothing
        } else {
            Dictionary translationDict = new Dictionary(TRANSLATION_CONSTRAINT_KEY);
            switch (constraint.getType()) {
                case POINT:
                    translationDict.add(SIX_DOF_RIGID_BODY_MOTION_CONSTRAINT_KEY, POINT_KEY);
                    translationDict.add(POINT_KEY, ((PointTranslationConstraint) constraint).getPoint());
                    translationDict.add(CENTRE_OF_ROTATION_KEY, ((PointTranslationConstraint) constraint).getPoint());//used by of+
                    break;
                case LINE:
                    translationDict.add(SIX_DOF_RIGID_BODY_MOTION_CONSTRAINT_KEY, LINE_KEY);
                    translationDict.add(DIRECTION_KEY, ((LineTranslationConstraint) constraint).getDirection());
                    break;
                case PLANE:
                    translationDict.add(SIX_DOF_RIGID_BODY_MOTION_CONSTRAINT_KEY, PLANE_KEY);
                    translationDict.add(NORMAL_KEY, ((PlaneTranslationConstraint) constraint).getNormal());
                    break;
                default:
                    break;
            }
            constraintsDict.add(translationDict);
        }
    }

    void writeRotationConstraint(RotationConstraint constraint, Dictionary constraintsDict) {
        if (constraint.getType().isNone()) {
            // do nothing
        } else {
            Dictionary rotationDict = new Dictionary(ROTATION_CONSTRAINT_KEY);
            switch (constraint.getType()) {
                case AXIS:
                    rotationDict.add(SIX_DOF_RIGID_BODY_MOTION_CONSTRAINT_KEY, AXIS_KEY);
                    rotationDict.add(AXIS_KEY, ((AxisRotationConstraint) constraint).getAxis());
                    break;
                case FIX_ORIENTATION:
                    rotationDict.add(SIX_DOF_RIGID_BODY_MOTION_CONSTRAINT_KEY, ORIENTATION_KEY);
                    break;
                default:
                    break;
            }
            constraintsDict.add(rotationDict);
        }
    }

    void writeLinearRestraint(LinearRestraint restraint, Dictionary constraintsDict) {
        if (restraint.getType().isNone()) {
            // do nothing
        } else {
            Dictionary linearDict = new Dictionary(LINEAR_RESTRAINT_KEY);
            switch (restraint.getType()) {
                case DAMPER:
                    linearDict.add(SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY, LINEAR_DAMPER_KEY);
                    linearDict.add(COEFF_KEY, ((DamperLinearRestraint) restraint).getCoeff());
                    break;
                case SPRING:
                    linearDict.add(SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY, LINEAR_SPRING_KEY);
                    linearDict.add(STIFFNESS_KEY, ((SpringLinearRestraint) restraint).getStiffness());
                    linearDict.add(DAMPING_KEY, ((SpringLinearRestraint) restraint).getDamping());
                    linearDict.add(REF_ATTACHMENT_PT_KEY, ((SpringLinearRestraint) restraint).getRefAttachmentPt());
                    linearDict.add(REST_LENGTH_KEY, ((SpringLinearRestraint) restraint).getRestLength());
                    linearDict.add(ANCHOR_KEY, ((SpringLinearRestraint) restraint).getAnchor());
                    break;
                default:
                    break;
            }
            constraintsDict.add(linearDict);
        }
    }

    void writeAngularRestraint(AngularRestraint restraint, Dictionary constraintsDict) {
        if (restraint.getType().isNone()) {
            // do nothing
        } else {
            Dictionary angularDict = new Dictionary(ANGULAR_RESTRAINT_KEY);
            switch (restraint.getType()) {
                case DAMPER:
                    angularDict.add(SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY, SPHERICAL_ANGULAR_DAMPER_KEY);
                    angularDict.add(COEFF_KEY, ((DamperAngularRestraint) restraint).getCoeff());
                    break;
                case SPRING:
                    angularDict.add(SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY, SPHERICAL_ANGULAR_SPRING_KEY);
                    angularDict.add(STIFFNESS_KEY, ((SpringAngularRestraint) restraint).getStiffness());
                    angularDict.add(DAMPING_KEY, ((SpringAngularRestraint) restraint).getDamping());
                    break;
                default:
                    break;
            }
            constraintsDict.add(angularDict);
        }
    }

}
