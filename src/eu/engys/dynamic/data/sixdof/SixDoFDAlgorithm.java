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
package eu.engys.dynamic.data.sixdof;

import eu.engys.dynamic.data.DynamicAlgorithm;
import eu.engys.dynamic.data.DynamicAlgorithmType;
import eu.engys.dynamic.data.sixdof.constraint.rotation.NoneRotationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.rotation.RotationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.NoneTranslationConstraint;
import eu.engys.dynamic.data.sixdof.constraint.translation.TranslationConstraint;
import eu.engys.dynamic.data.sixdof.restraint.angular.AngularRestraint;
import eu.engys.dynamic.data.sixdof.restraint.angular.NoneAngularRestraint;
import eu.engys.dynamic.data.sixdof.restraint.linear.LinearRestraint;
import eu.engys.dynamic.data.sixdof.restraint.linear.NoneLinearRestraint;

public class SixDoFDAlgorithm extends DynamicAlgorithm {

    public static final String MASS = "mass";
    public static final String PATCHES = "patches";
    public static final String ACCELERATION_RELAXATION = "accelerationRelaxation";
    public static final String VELOCITY = "velocity";
    public static final String MOMENT_OF_INERTIA = "momentOfInertia";
    public static final String CENTRE_OF_MASS = "centreOfMass";
    public static final String OUTER_DISTANCE = "outerDistance";
    public static final String INNER_DISTANCE = "innerDistance";
    public static final String TRANSLATION_CONSTRAINT = "translationConstraint";
    public static final String ROTATION_CONSTRAINT = "rotationConstraint";
    public static final String LINEAR_RESTRAINT = "linearRestraint";
    public static final String ANGULAR_RESTRAINT = "angularRestraint";

    private String[] patches = new String[0];
    private double innerDistance = 0.5;
    private double outerDistance = 1.0;
    private double mass = 1.0;
    private double[] centreOfMass = new double[] { 0.0, 0.0, 0.0 };
    private double[] momentOfInertia = new double[] { 1.0, 1.0, 1.0 };
    private double[] velocity = new double[] { 0.0, 0.0, 0.0 };
    private double accelerationRelaxation = 0.8;
    private TranslationConstraint translationConstraint = new NoneTranslationConstraint();
    private RotationConstraint rotationConstraint = new NoneRotationConstraint();
    private LinearRestraint linearRestraint = new NoneLinearRestraint();
    private AngularRestraint angularRestraint = new NoneAngularRestraint();

    public SixDoFDAlgorithm() {
    }

    @Override
    public DynamicAlgorithm copy() {
        SixDoFDAlgorithm copy = new SixDoFDAlgorithm();
        String copyPatches[] = new String[patches.length];
        for (int i = 0; i < patches.length; i++) {
            copyPatches[i] = patches[i];
        }
        copy.patches = copyPatches;
        copy.innerDistance = this.innerDistance;
        copy.outerDistance = this.outerDistance;
        copy.mass = this.mass;
        copy.centreOfMass[0] = centreOfMass[0];
        copy.centreOfMass[1] = centreOfMass[1];
        copy.centreOfMass[2] = centreOfMass[2];
        copy.momentOfInertia[0] = momentOfInertia[0];
        copy.momentOfInertia[1] = momentOfInertia[1];
        copy.momentOfInertia[2] = momentOfInertia[2];
        copy.velocity[0] = velocity[0];
        copy.velocity[1] = velocity[1];
        copy.velocity[2] = velocity[2];
        copy.accelerationRelaxation = this.accelerationRelaxation;
        copy.translationConstraint = this.translationConstraint.copy();
        copy.rotationConstraint = this.rotationConstraint.copy();
        copy.linearRestraint = this.linearRestraint.copy();
        copy.angularRestraint = this.angularRestraint.copy();
        return copy;
    }

    @Override
    public DynamicAlgorithmType getType() {
        return DynamicAlgorithmType.SIX_DOF;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        firePropertyChange(MASS, this.mass, this.mass = mass);
    }

    public String[] getPatches() {
        return patches;
    }

    public void setPatches(String[] patches) {
        firePropertyChange(PATCHES, this.patches, this.patches = patches);
    }

    public double getAccelerationRelaxation() {
        return accelerationRelaxation;
    }

    public void setAccelerationRelaxation(double accelerationRelaxation) {
        firePropertyChange(ACCELERATION_RELAXATION, this.accelerationRelaxation, this.accelerationRelaxation = accelerationRelaxation);
    }

    public double[] getVelocity() {
        return velocity;
    }

    public void setVelocity(double[] velocity) {
        firePropertyChange(VELOCITY, this.velocity, this.velocity = velocity);
    }

    public double[] getMomentOfInertia() {
        return momentOfInertia;
    }

    public void setMomentOfInertia(double[] momentOfInertia) {
        firePropertyChange(MOMENT_OF_INERTIA, this.momentOfInertia, this.momentOfInertia = momentOfInertia);
    }

    public double[] getCentreOfMass() {
        return centreOfMass;
    }

    public void setCentreOfMass(double[] centreOfMass) {
        firePropertyChange(CENTRE_OF_MASS, this.centreOfMass, this.centreOfMass = centreOfMass);
    }

    public double getOuterDistance() {
        return outerDistance;
    }

    public void setOuterDistance(double outerDistance) {
        firePropertyChange(OUTER_DISTANCE, this.outerDistance, this.outerDistance = outerDistance);
    }

    public double getInnerDistance() {
        return innerDistance;
    }

    public void setInnerDistance(double innerDistance) {
        firePropertyChange(INNER_DISTANCE, this.innerDistance, this.innerDistance = innerDistance);
    }

    public TranslationConstraint getTranslationConstraint() {
        return translationConstraint;
    }

    public void setTranslationConstraint(TranslationConstraint translationConstraint) {
        firePropertyChange(TRANSLATION_CONSTRAINT, this.translationConstraint, this.translationConstraint = translationConstraint);
    }

    public RotationConstraint getRotationConstraint() {
        return rotationConstraint;
    }

    public void setRotationConstraint(RotationConstraint rotationConstraint) {
        firePropertyChange(ROTATION_CONSTRAINT, this.rotationConstraint, this.rotationConstraint = rotationConstraint);
    }

    public LinearRestraint getLinearRestraint() {
        return linearRestraint;
    }

    public void setLinearRestraint(LinearRestraint linearRestraint) {
        firePropertyChange(LINEAR_RESTRAINT, this.linearRestraint, this.linearRestraint = linearRestraint);
    }

    public AngularRestraint getAngularRestraint() {
        return angularRestraint;
    }

    public void setAngularRestraint(AngularRestraint angularRestraint) {
        firePropertyChange(ANGULAR_RESTRAINT, this.angularRestraint, this.angularRestraint = angularRestraint);
    }

}
