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

import static eu.engys.dynamic.DynamicMeshDict.ACCELERATION_RELAXATION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.AMPLITUDE_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ANCHOR_KEY;
import static eu.engys.dynamic.DynamicMeshDict.AXIS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.CENTRE_OF_MASS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.CENTRE_OF_ROTATION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.COEFF_KEY;
import static eu.engys.dynamic.DynamicMeshDict.COFG_KEY;
import static eu.engys.dynamic.DynamicMeshDict.CONSTRAINTS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DAMPING_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DIRECTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DTI_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DTP_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DYNAMIC_FV_MESH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.DYNAMIC_REFINE_FV_MESH_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.FIELD_KEY;
import static eu.engys.dynamic.DynamicMeshDict.HEAVE_A_KEY;
import static eu.engys.dynamic.DynamicMeshDict.INNER_DISTANCE_KEY;
import static eu.engys.dynamic.DynamicMeshDict.LAMDA_KEY;
import static eu.engys.dynamic.DynamicMeshDict.LOWER_REFINE_LEVEL_KEY;
import static eu.engys.dynamic.DynamicMeshDict.MASS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.MAX_CELLS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.MAX_REFINEMENT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.MOMENT_OF_INERTIA_KEY;
import static eu.engys.dynamic.DynamicMeshDict.MULTI_SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.NORMAL_KEY;
import static eu.engys.dynamic.DynamicMeshDict.N_BUFFER_LAYERS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.OMEGA_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ORIGIN_KEY;
import static eu.engys.dynamic.DynamicMeshDict.OUTER_DISTANCE_KEY;
import static eu.engys.dynamic.DynamicMeshDict.PATCHES_KEY;
import static eu.engys.dynamic.DynamicMeshDict.PERIOD_KEY;
import static eu.engys.dynamic.DynamicMeshDict.POINT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.Q_KEY;
import static eu.engys.dynamic.DynamicMeshDict.RADIAL_VELOCITY_KEY;
import static eu.engys.dynamic.DynamicMeshDict.REFINE_INTERVAL_KEY;
import static eu.engys.dynamic.DynamicMeshDict.REF_ATTACHMENT_PT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.RESTRAINTS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.REST_LENGTH_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ROLL_A_MAX_KEY;
import static eu.engys.dynamic.DynamicMeshDict.ROLL_A_MIN_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SIX_DOF_RIGID_BODY_MOTION_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SIX_DOF_RIGID_BODY_MOTION_CONSTRAINT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SOLID_BODY_MOTION_FUNCTION_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.STIFFNESS_KEY;
import static eu.engys.dynamic.DynamicMeshDict.SWAY_A_KEY;
import static eu.engys.dynamic.DynamicMeshDict.T0_KEY;
import static eu.engys.dynamic.DynamicMeshDict.THETA_KEY;
import static eu.engys.dynamic.DynamicMeshDict.TPN_KEY;
import static eu.engys.dynamic.DynamicMeshDict.TP_KEY;
import static eu.engys.dynamic.DynamicMeshDict.UNREFINE_LEVEL_KEY;
import static eu.engys.dynamic.DynamicMeshDict.UPPER_REFINE_LEVEL_KEY;
import static eu.engys.dynamic.DynamicMeshDict.VELOCITY_KEY;

import org.apache.commons.math3.util.MathArrays;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.dynamic.DynamicMeshDict;
import eu.engys.dynamic.data.multibody.MultiBodyAlgorithm;
import eu.engys.dynamic.data.off.StaticAlgorithm;
import eu.engys.dynamic.data.refine.MeshRefineAlgorithm;
import eu.engys.dynamic.data.singlebody.SolidBodyAlgorithm;
import eu.engys.dynamic.data.singlebody.SolidBodyMotionFunction;
import eu.engys.dynamic.data.singlebody.SolidBodyMotionFunctionType;
import eu.engys.dynamic.data.singlebody.functions.AxisRotationMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.LinearMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.OscillatingLinearMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.OscillatingRotatingMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.RotatingMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.RotatingStepMotionFunction;
import eu.engys.dynamic.data.singlebody.functions.SDAMotionFunction;
import eu.engys.dynamic.data.sixdof.SixDoFDAlgorithm;
import eu.engys.dynamic.data.sixdof.constraint.rotation.AxisRotationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.rotation.FixOrientationRotationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.rotation.NoneRotationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.rotation.RotationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.rotation.RotationConstraintType;
import eu.engys.dynamic.data.sixdof.constraint.translation.LineTranslationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.NoneTranslationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.PlaneTranslationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.PointTranslationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.TranslationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.TranslationConstraintType;
import eu.engys.dynamic.data.sixdof.restraint.angular.AngularRestraint;
import eu.engys.dynamic.data.sixdof.restraint.angular.AngularRestraintType;
import eu.engys.dynamic.data.sixdof.restraint.angular.DamperAngularRestraint;
import eu.engys.dynamic.data.sixdof.restraint.angular.NoneAngularRestraint;
import eu.engys.dynamic.data.sixdof.restraint.angular.SpringAngularRestraint;
import eu.engys.dynamic.data.sixdof.restraint.linear.DamperLinearRestraint;
import eu.engys.dynamic.data.sixdof.restraint.linear.LinearRestraint;
import eu.engys.dynamic.data.sixdof.restraint.linear.LinearRestraintType;
import eu.engys.dynamic.data.sixdof.restraint.linear.NoneLinearRestraint;
import eu.engys.dynamic.data.sixdof.restraint.linear.SpringLinearRestraint;

public class DynamicDataReader {

    public DynamicAlgorithm readAlgorithm(Dictionary dynamicMeshDict) {
        String key = dynamicMeshDict.lookupString(DYNAMIC_FV_MESH_KEY);
        DynamicAlgorithmType type = DynamicAlgorithmType.byKey(key);
        switch (type) {
            case SOLID_RIGID_BODY:
                return readSingleBodyAlgorithm(dynamicMeshDict);
            case MULTI_RIGID_BODY:
                return readMultiBodyAlgorithm(dynamicMeshDict);
            case SIX_DOF:
                return readSixDofAlgorithm(dynamicMeshDict);
            case MESH_REFINE:
                return readDynamicRefineAlgorithm(dynamicMeshDict);
            default:
                return new StaticAlgorithm();
        }
    }

    DynamicAlgorithm readDynamicRefineAlgorithm(Dictionary dynamicMeshDict) {
        MeshRefineAlgorithm algo = new MeshRefineAlgorithm();
        if (dynamicMeshDict.found(DYNAMIC_REFINE_FV_MESH_COEFFS_KEY)) {
            Dictionary coeffsDict = dynamicMeshDict.subDict(DYNAMIC_REFINE_FV_MESH_COEFFS_KEY);
            if (coeffsDict.found(REFINE_INTERVAL_KEY)) {
                algo.setRefineInterval(coeffsDict.lookupInt(REFINE_INTERVAL_KEY));
            }
            if (coeffsDict.found(FIELD_KEY)) {
                algo.setField(coeffsDict.lookupString(FIELD_KEY));
            }
            if (coeffsDict.found(LOWER_REFINE_LEVEL_KEY)) {
                algo.setLowerRefineLevel(coeffsDict.lookupDouble(LOWER_REFINE_LEVEL_KEY));
            }
            if (coeffsDict.found(UPPER_REFINE_LEVEL_KEY)) {
                algo.setUpperRefineLevel(coeffsDict.lookupDouble(UPPER_REFINE_LEVEL_KEY));
            }
            if (coeffsDict.found(UNREFINE_LEVEL_KEY)) {
                algo.setUnrefineLevel(coeffsDict.lookupInt(UNREFINE_LEVEL_KEY));
            }
            if (coeffsDict.found(N_BUFFER_LAYERS_KEY)) {
                algo.setnBufferLayers(coeffsDict.lookupInt(N_BUFFER_LAYERS_KEY));
            }
            if (coeffsDict.found(MAX_REFINEMENT_KEY)) {
                algo.setMaxRefinement(coeffsDict.lookupInt(MAX_REFINEMENT_KEY));
            }
            if (coeffsDict.found(MAX_CELLS_KEY)) {
                algo.setMaxCells(coeffsDict.lookupInt(MAX_CELLS_KEY));
            }
        }
        return algo;
    }

    DynamicAlgorithm readSixDofAlgorithm(Dictionary dynamicMeshDict) {
        SixDoFDAlgorithm algo = new SixDoFDAlgorithm();
        if (dynamicMeshDict.found(SIX_DOF_RIGID_BODY_MOTION_COEFFS_KEY)) {
            Dictionary coeffsDict = dynamicMeshDict.subDict(SIX_DOF_RIGID_BODY_MOTION_COEFFS_KEY);
            if (coeffsDict.found(MASS_KEY)) {
                algo.setMass(coeffsDict.lookupDouble(MASS_KEY));
            }
            if (coeffsDict.found(PATCHES_KEY)) {
                if (coeffsDict.isList(PATCHES_KEY)) {
                    algo.setPatches(new String[0]);
                } else {
                    algo.setPatches(coeffsDict.lookupArray(PATCHES_KEY));
                }

            }
            if (coeffsDict.found(ACCELERATION_RELAXATION_KEY)) {
                algo.setAccelerationRelaxation(coeffsDict.lookupDouble(ACCELERATION_RELAXATION_KEY));
            }
            if (coeffsDict.found(VELOCITY_KEY)) {
                algo.setVelocity(coeffsDict.lookupDoubleArray(VELOCITY_KEY));
            }
            if (coeffsDict.found(MOMENT_OF_INERTIA_KEY)) {
                algo.setMomentOfInertia(coeffsDict.lookupDoubleArray(MOMENT_OF_INERTIA_KEY));
            }
            if (coeffsDict.found(CENTRE_OF_MASS_KEY)) {
                algo.setCentreOfMass(coeffsDict.lookupDoubleArray(CENTRE_OF_MASS_KEY));
            }
            if (coeffsDict.found(OUTER_DISTANCE_KEY)) {
                algo.setOuterDistance(coeffsDict.lookupDouble(OUTER_DISTANCE_KEY));
            }
            if (coeffsDict.found(INNER_DISTANCE_KEY)) {
                algo.setInnerDistance(coeffsDict.lookupDouble(INNER_DISTANCE_KEY));
            }
            if (coeffsDict.found(CONSTRAINTS_KEY)) {
                Dictionary constraintsDict = coeffsDict.subDict(CONSTRAINTS_KEY);
                if (constraintsDict.getDictionaries().size() == 1) {
                    readConstraint(algo, constraintsDict.getDictionaries().get(0));
                } else if (constraintsDict.getDictionaries().size() >= 2) {
                    readConstraint(algo, constraintsDict.getDictionaries().get(0));
                    readConstraint(algo, constraintsDict.getDictionaries().get(1));
                }
            }
            if (coeffsDict.found(RESTRAINTS_KEY)) {
                Dictionary restraintsDict = coeffsDict.subDict(RESTRAINTS_KEY);
                if (restraintsDict.getDictionaries().size() == 1) {
                    readRestraint(algo, restraintsDict.getDictionaries().get(0));
                } else if (restraintsDict.getDictionaries().size() >= 2) {
                    readRestraint(algo, restraintsDict.getDictionaries().get(0));
                    readRestraint(algo, restraintsDict.getDictionaries().get(1));
                }
            }

        }
        return algo;
    }

    private void readConstraint(SixDoFDAlgorithm algo, Dictionary dict) {
        if (TranslationConstraintType.isTranslationContraintDict(dict)) {
            algo.setTranslationConstraint(readTranslationConstraint(dict));
        } else if (RotationConstraintType.isRotationContraintDict(dict)) {
            algo.setRotationConstraint(readRotationConstraint(dict));
        }
    }

    private void readRestraint(SixDoFDAlgorithm algo, Dictionary dict) {
        if (LinearRestraintType.isLinearRestraintDict(dict)) {
            algo.setLinearRestraint(readLinearRestraint(dict));
        } else if (AngularRestraintType.isAngularRestraintDict(dict)) {
            algo.setAngularRestraint(readAngularRestraint(dict));
        }
    }

    TranslationConstraint readTranslationConstraint(Dictionary translationDict) {
        TranslationConstraintType type = TranslationConstraintType.byKey(translationDict.lookupString(SIX_DOF_RIGID_BODY_MOTION_CONSTRAINT_KEY));
        switch (type) {
            case POINT: {
                PointTranslationConstraint r = new PointTranslationConstraint();
                if (translationDict.found(POINT_KEY)) {
                    r.setPoint(translationDict.lookupDoubleArray(POINT_KEY));
                }
                // used by of+
                if (translationDict.found(CENTRE_OF_ROTATION_KEY)) {
                    r.setPoint(translationDict.lookupDoubleArray(CENTRE_OF_ROTATION_KEY));
                }
                return r;
            }
            case LINE: {
                LineTranslationConstraint r = new LineTranslationConstraint();
                if (translationDict.found(DIRECTION_KEY)) {
                    r.setDirection(translationDict.lookupDoubleArray(DIRECTION_KEY));
                }
                return r;
            }
            case PLANE: {
                PlaneTranslationConstraint r = new PlaneTranslationConstraint();
                if (translationDict.found(NORMAL_KEY)) {
                    r.setNormal(translationDict.lookupDoubleArray(NORMAL_KEY));
                }
                return r;
            }
            default:
                return new NoneTranslationConstraint();
        }
    }

    RotationConstraint readRotationConstraint(Dictionary rotationDict) {
        RotationConstraintType type = RotationConstraintType.byKey(rotationDict.lookupString(SIX_DOF_RIGID_BODY_MOTION_CONSTRAINT_KEY));
        switch (type) {
            case AXIS: {
                AxisRotationConstraint r = new AxisRotationConstraint();
                if (rotationDict.found(AXIS_KEY)) {
                    r.setAxis(rotationDict.lookupDoubleArray(AXIS_KEY));
                }
                return r;
            }
            case FIX_ORIENTATION: {
                return new FixOrientationRotationConstraint();
            }
            default:
                return new NoneRotationConstraint();
        }

    }

    LinearRestraint readLinearRestraint(Dictionary linearDict) {
        LinearRestraintType type = LinearRestraintType.byKey(linearDict.lookupString(SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY));
        switch (type) {
            case SPRING: {
                SpringLinearRestraint r = new SpringLinearRestraint();
                if (linearDict.found(STIFFNESS_KEY)) {
                    r.setStiffness(linearDict.lookupDouble(STIFFNESS_KEY));
                }
                if (linearDict.found(DAMPING_KEY)) {
                    r.setDamping(linearDict.lookupDouble(DAMPING_KEY));
                }
                if (linearDict.found(REF_ATTACHMENT_PT_KEY)) {
                    r.setRefAttachmentPt(linearDict.lookupDoubleArray(REF_ATTACHMENT_PT_KEY));
                }
                if (linearDict.found(REST_LENGTH_KEY)) {
                    r.setRestLength(linearDict.lookupDouble(REST_LENGTH_KEY));
                }
                if (linearDict.found(ANCHOR_KEY)) {
                    r.setAnchor(linearDict.lookupDoubleArray(ANCHOR_KEY));
                }
                return r;
            }
            case DAMPER: {
                DamperLinearRestraint r = new DamperLinearRestraint();
                if (linearDict.found(COEFF_KEY)) {
                    r.setCoeff(linearDict.lookupDouble(COEFF_KEY));
                }
                return r;
            }
            default:
                return new NoneLinearRestraint();
        }
    }

    AngularRestraint readAngularRestraint(Dictionary angularDict) {
        AngularRestraintType type = AngularRestraintType.byKey(angularDict.lookupString(SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY));
        switch (type) {
            case SPRING: {
                SpringAngularRestraint r = new SpringAngularRestraint();
                if (angularDict.found(STIFFNESS_KEY)) {
                    r.setStiffness(angularDict.lookupDouble(STIFFNESS_KEY));
                }
                if (angularDict.found(DAMPING_KEY)) {
                    r.setDamping(angularDict.lookupDouble(DAMPING_KEY));
                }
                return r;
            }
            case DAMPER: {
                DamperAngularRestraint r = new DamperAngularRestraint();
                if (angularDict.found(COEFF_KEY)) {
                    r.setCoeff(angularDict.lookupDouble(COEFF_KEY));
                }
                return r;
            }
            default:
                return new NoneAngularRestraint();
        }
    }

    DynamicAlgorithm readMultiBodyAlgorithm(Dictionary dynamicMeshDict) {
        MultiBodyAlgorithm algo = new MultiBodyAlgorithm();
        if (dynamicMeshDict.found(MULTI_SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY)) {
            Dictionary coeffsDict = dynamicMeshDict.subDict(MULTI_SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY);
            for (Dictionary d : coeffsDict.getDictionaries()) {
                if (d.found(SOLID_BODY_MOTION_FUNCTION_KEY)) {
                    String function = d.lookupString(SOLID_BODY_MOTION_FUNCTION_KEY);
                    SolidBodyMotionFunctionType functionType = SolidBodyMotionFunctionType.byKey(function);
                    String functionCoeffsName = function + DynamicMeshDict.COEFFS_KEY;
                    if (d.found(functionCoeffsName)) {
                        Dictionary dict = d.subDict(functionCoeffsName);
                        algo.getSingleBodyAlgorithms().put(d.getName(), new SolidBodyAlgorithm(readSingleBodyFunction(dict, functionType)));
                    }
                }
            }
        }
        return algo;
    }

    DynamicAlgorithm readSingleBodyAlgorithm(Dictionary dynamicMeshDict) {
        SolidBodyAlgorithm algo = new SolidBodyAlgorithm();
        if (dynamicMeshDict.found(SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY)) {
            Dictionary coeffsDict = dynamicMeshDict.subDict(SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY);
            if (coeffsDict.found(SOLID_BODY_MOTION_FUNCTION_KEY)) {
                String function = coeffsDict.lookupString(SOLID_BODY_MOTION_FUNCTION_KEY);
                SolidBodyMotionFunctionType functionType = SolidBodyMotionFunctionType.byKey(function);
                if (coeffsDict.found(function + DynamicMeshDict.COEFFS_KEY)) {
                    algo.setFunction(readSingleBodyFunction(coeffsDict.subDict(function + DynamicMeshDict.COEFFS_KEY), functionType));
                }
            }
        }
        return algo;
    }

    private SolidBodyMotionFunction readSingleBodyFunction(Dictionary d, SolidBodyMotionFunctionType functionType) {
        SolidBodyMotionFunction function;
        switch (functionType) {
            case LINEAR_MOTION: {
                function = readLinear(d);
                break;
            }
            case ROTATING_MOTION: {
                function = readRotating(d);
                break;
            }
            case ROTATING_STEP_MOTION: {
                function = readRotatingStep(d);
                break;
            }
            case AXIS_ROTATION_MOTION: {
                function = readAxisRotation(d);
                break;
            }
            case OSCILLATING_LINEAR_MOTION: {
                function = readOscillatingLinear(d);
                break;
            }
            case OSCILLATING_ROTATING_MOTION: {
                function = readOscillatingRotating(d);
                break;
            }
            case SDA_MOTION: {
                function = readSDA(d);
                break;
            }
            default:
                function = new LinearMotionFunction();
                break;
        }
        return function;
    }

    SolidBodyMotionFunction readLinear(Dictionary d) {
        LinearMotionFunction motion = new LinearMotionFunction();
        if (d.found(VELOCITY_KEY)) {
            motion.setVelocity(d.lookupDoubleArray(VELOCITY_KEY));
        }
        return motion;

    }

    SolidBodyMotionFunction readRotating(Dictionary d) {
        RotatingMotionFunction motion = new RotatingMotionFunction();
        if (d.found(ORIGIN_KEY)) {
            motion.setOrigin(d.lookupDoubleArray(ORIGIN_KEY));
        }
        if (d.found(AXIS_KEY)) {
            motion.setAxis(d.lookupDoubleArray(AXIS_KEY));
        }
        if (d.found(OMEGA_KEY)) {
            motion.setOmega(d.lookupDouble(OMEGA_KEY));
        }
        if (d.found(T0_KEY)) {
            motion.setT0(d.lookupDouble(T0_KEY));
        }
        return motion;
    }

    SolidBodyMotionFunction readRotatingStep(Dictionary d) {
        RotatingStepMotionFunction motion = new RotatingStepMotionFunction();
        if (d.found(ORIGIN_KEY)) {
            motion.setOrigin(d.lookupDoubleArray(ORIGIN_KEY));
        }
        if (d.found(AXIS_KEY)) {
            motion.setAxis(d.lookupDoubleArray(AXIS_KEY));
        }
        if (d.found(THETA_KEY)) {
            motion.setTheta(d.lookupDouble(THETA_KEY));
        }
        if (d.found(PERIOD_KEY)) {
            motion.setPeriod(d.lookupInt(PERIOD_KEY));
        }
        return motion;
    }

    SolidBodyMotionFunction readAxisRotation(Dictionary d) {
        AxisRotationMotionFunction motion = new AxisRotationMotionFunction();
        if (d.found(ORIGIN_KEY)) {
            motion.setOrigin(d.lookupDoubleArray(ORIGIN_KEY));
        }
        if (d.found(RADIAL_VELOCITY_KEY)) {
            double[] rv = d.lookupDoubleArray(RADIAL_VELOCITY_KEY);
            double omega = Math.sqrt(Math.pow(rv[0], 2) + Math.pow(rv[1], 2) + Math.pow(rv[2], 2));
            double[] axis = omega > 0 ? MathArrays.scale(1 / omega, rv) : new double[] { 0, 0, 0 };

            motion.setAxis(axis);
            motion.setOmega(Math.toRadians(omega));
        }
        return motion;
    }

    SolidBodyMotionFunction readOscillatingLinear(Dictionary d) {
        OscillatingLinearMotionFunction motion = new OscillatingLinearMotionFunction();
        if (d.found(AMPLITUDE_KEY)) {
            motion.setAmplitude(d.lookupDoubleArray(AMPLITUDE_KEY));
        }
        if (d.found(OMEGA_KEY)) {
            motion.setOmega(d.lookupDouble(OMEGA_KEY));
        }
        return motion;
    }

    SolidBodyMotionFunction readOscillatingRotating(Dictionary d) {
        OscillatingRotatingMotionFunction motion = new OscillatingRotatingMotionFunction();
        if (d.found(ORIGIN_KEY)) {
            motion.setOrigin(d.lookupDoubleArray(ORIGIN_KEY));
        }
        if (d.found(AMPLITUDE_KEY)) {
            motion.setAmplitude(d.lookupDoubleArray(AMPLITUDE_KEY));
        }
        if (d.found(OMEGA_KEY)) {
            motion.setOmega(d.lookupDouble(OMEGA_KEY));
        }
        return motion;
    }

    SolidBodyMotionFunction readSDA(Dictionary d) {
        SDAMotionFunction motion = new SDAMotionFunction();
        if (d.found(TPN_KEY)) {
            motion.setTpn(d.lookupDouble(TPN_KEY));
        }
        if (d.found(TP_KEY)) {
            motion.setTp(d.lookupDouble(TP_KEY));
        }
        if (d.found(SWAY_A_KEY)) {
            motion.setSwayA(d.lookupDouble(SWAY_A_KEY));
        }
        if (d.found(ROLL_A_MIN_KEY)) {
            motion.setRollAmin(d.lookupDouble(ROLL_A_MIN_KEY));
        }
        if (d.found(Q_KEY)) {
            motion.setQ(d.lookupDouble(Q_KEY));
        }
        if (d.found(LAMDA_KEY)) {
            motion.setLambda(d.lookupDouble(LAMDA_KEY));
        }
        if (d.found(HEAVE_A_KEY)) {
            motion.setHeaveA(d.lookupDouble(HEAVE_A_KEY));
        }
        if (d.found(DTI_KEY)) {
            motion.setDti(d.lookupDouble(DTI_KEY));
        }
        if (d.found(ROLL_A_MAX_KEY)) {
            motion.setRollAmax(d.lookupDouble(ROLL_A_MAX_KEY));
        }
        if (d.found(DTP_KEY)) {
            motion.setDtp(d.lookupDouble(DTP_KEY));
        }
        if (d.found(COFG_KEY)) {
            motion.setCofg(d.lookupDoubleArray(COFG_KEY));
        }
        return motion;
    }

}
