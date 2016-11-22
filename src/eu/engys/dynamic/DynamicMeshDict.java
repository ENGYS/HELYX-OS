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

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FoamFile;

public class DynamicMeshDict extends Dictionary {
    
    public static final String COEFFS_KEY = "Coeffs";
    
    public static final String DYNAMIC_MESH_DICT = "dynamicMeshDict";

    public static final String DYNAMIC_FV_MESH_KEY = "dynamicFvMesh";
    public static final String STATIC_FV_MESH_KEY = "staticFvMesh";
    public static final String SOLID_BODY_MOTION_FV_MESH_KEY = "solidBodyMotionFvMesh";
    public static final String DYNAMIC_MOTION_SOLVER_FV_MESH_KEY = "dynamicMotionSolverFvMesh";
    public static final String DYNAMIC_REFINE_FV_MESH_KEY = "dynamicRefineFvMesh";
    public static final String MULTI_SOLID_BODY_MOTION_FV_MESH_KEY = "multiSolidBodyMotionFvMesh";
    public static final String SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY = SOLID_BODY_MOTION_FV_MESH_KEY + COEFFS_KEY;
    public static final String MULTI_SOLID_BODY_MOTION_FV_MESH_COEFFS_KEY = MULTI_SOLID_BODY_MOTION_FV_MESH_KEY + COEFFS_KEY;
    public static final String DYNAMIC_REFINE_FV_MESH_COEFFS_KEY = DYNAMIC_REFINE_FV_MESH_KEY + COEFFS_KEY;

    /*
     * SOLID BODY MOTION FUNCTIONS
     */
    public static final String SOLID_BODY_MOTION_FUNCTION_KEY = "solidBodyMotionFunction";

    // Linear Motion
    public static final String LINEAR_MOTION_KEY = "linearMotion";
    public static final String LINEAR_MOTION_COEFFS_KEY = LINEAR_MOTION_KEY + COEFFS_KEY;

    // Rotating motion
    public static final String ROTATING_MOTION_KEY = "rotatingMotion";
    public static final String ROTATING_MOTION_COEFFS_KEY = ROTATING_MOTION_KEY + COEFFS_KEY;
    public static final String T0_KEY = "t0";

    // Axis rotation motion
    public static final String AXIS_ROTATION_MOTION_KEY = "axisRotationMotion";
    public static final String AXIS_ROTATION_MOTION_COEFFS_KEY = AXIS_ROTATION_MOTION_KEY + COEFFS_KEY;
    public static final String RADIAL_VELOCITY_KEY = "radialVelocity";

    // Oscillating Linear Motion
    public static final String OSCILLATING_LINEAR_MOTION_KEY = "oscillatingLinearMotion";
    public static final String OSCILLATING_LINEAR_MOTION_COEFFS_KEY = OSCILLATING_LINEAR_MOTION_KEY + COEFFS_KEY;

    // Oscillating Rotating Motion
    public static final String OSCILLATING_ROTATING_MOTION_KEY = "oscillatingRotatingMotion";
    public static final String OSCILLATING_ROTATING_MOTION_COEFFS_KEY = OSCILLATING_ROTATING_MOTION_KEY + COEFFS_KEY;

    // SDA
    public static final String SDA_KEY = "SDA";
    public static final String SDA_COEFFS_KEY = SDA_KEY + COEFFS_KEY;
    public static final String TPN_KEY = "Tpn";
    public static final String TP_KEY = "Tp";
    public static final String SWAY_A_KEY = "swayA";
    public static final String ROLL_A_MIN_KEY = "rollAmin";
    public static final String Q_KEY = "Q";
    public static final String LAMDA_KEY = "lamda";
    public static final String HEAVE_A_KEY = "heaveA";
    public static final String DTI_KEY = "dTi";
    public static final String ROLL_A_MAX_KEY = "rollAmax";
    public static final String DTP_KEY = "dTp";
    public static final String COFG_KEY = "CofG";

    // Rotating Step Motion
    public static final String ROTATING_STEP_MOTION_KEY = "rotatingStepMotion";
    public static final String ROTATING_STEP_MOTION_COEFFS_KEY = "rotatingStepMotionCoeffs";
    public static final String THETA_KEY = "theta";
    public static final String PERIOD_KEY = "period";
    
    /*
     * Dynamic Refine
     */
    public static final String REFINE_INTERVAL_KEY = "refineInterval";
    public static final String FIELD_KEY = "field";
    public static final String LOWER_REFINE_LEVEL_KEY = "lowerRefineLevel";
    public static final String UPPER_REFINE_LEVEL_KEY = "upperRefineLevel";
    public static final String UNREFINE_LEVEL_KEY = "unrefineLevel";
    public static final String N_BUFFER_LAYERS_KEY = "nBufferLayers";
    public static final String MAX_REFINEMENT_KEY = "maxRefinement";
    public static final String MAX_CELLS_KEY = "maxCells";
    public static final String CORRECT_FLUXES_KEY = "correctFluxes";
    public static final String CORRECT_FLUXES_VALUE = "( (phi none)  (phiAbs U) (phiAbs_0 U_0) (nHatf none) (rho*phi none) (ghf none) (phi_0 none) (rhoPhi none)  (\"(flux(alpha1)-flux(alpha1))\" none) )";
    public static final String DUMP_LEVEL_KEY = "dumpLevel";
    
    /*
     * 6DOF
     */
    public static final String SOLVER_KEY = "solver";
    
    public static final String SIX_DOF_RIGID_BODY_MOTION_KEY = "sixDoFRigidBodyMotion";
    public static final String SIX_DOF_RIGID_BODY_MOTION_COEFFS_KEY = SIX_DOF_RIGID_BODY_MOTION_KEY+COEFFS_KEY;
    
    public static final String MASS_KEY = "mass";
    public static final String PATCHES_KEY = "patches";
    public static final String ACCELERATION_RELAXATION_KEY = "accelerationRelaxation";
    public static final String REPORT_KEY = "report";
    public static final String MOMENT_OF_INERTIA_KEY = "momentOfInertia";
    public static final String CENTRE_OF_MASS_KEY = "centreOfMass";
    public static final String OUTER_DISTANCE_KEY = "outerDistance";
    public static final String INNER_DISTANCE_KEY = "innerDistance";
    public static final String RHO_KEY = "rho";
    public static final String RHO_NAME_KEY = "rhoName";
    public static final String RHO_INF_KEY = "rhoInf";
    public static final String NEWMARK_KEY = "Newmark";
    
    //CONSTRAINTS
    public static final String CONSTRAINTS_KEY = "constraints";
    
    public static final String SIX_DOF_RIGID_BODY_MOTION_CONSTRAINT_KEY = "sixDoFRigidBodyMotionConstraint";
    
    public static final String ROTATION_CONSTRAINT_KEY = "rotationConstraint";
    public static final String ORIENTATION_KEY = "orientation";
    
    public static final String TRANSLATION_CONSTRAINT_KEY = "translationConstraint";
    public static final String PLANE_KEY = "plane";
    public static final String NORMAL_KEY = "normal";
    public static final String LINE_KEY = "line";
    public static final String DIRECTION_KEY = "direction";
    public static final String POINT_KEY = "point";
    public static final String CENTRE_OF_ROTATION_KEY = "centreOfRotation";//used by of+
    
    //RESTRAINTS
    public static final String RESTRAINTS_KEY = "restraints";
    public static final String LINEAR_RESTRAINT_KEY = "linearRestraint";
    public static final String ANGULAR_RESTRAINT_KEY = "angularRestraint";
    public static final String SIX_DOF_RIGID_BODY_MOTION_RESTRAINT_KEY = "sixDoFRigidBodyMotionRestraint";
    public static final String LINEAR_DAMPER_KEY = "linearDamper";
    public static final String LINEAR_SPRING_KEY = "linearSpring";
    public static final String COEFF_KEY = "coeff";
    public static final String SPHERICAL_ANGULAR_SPRING_KEY = "sphericalAngularSpring";
    public static final String SPHERICAL_ANGULAR_DAMPER_KEY = "sphericalAngularDamper";
    public static final String STIFFNESS_KEY = "stiffness";
    public static final String DAMPING_KEY = "damping";
    public static final String REF_ATTACHMENT_PT_KEY = "refAttachmentPt";
    public static final String REST_LENGTH_KEY = "restLength";
    public static final String ANCHOR_KEY = "anchor";
    
    // Common
    public static final String ORIGIN_KEY = "origin";
    public static final String OMEGA_KEY = "omega";
    public static final String AMPLITUDE_KEY = "amplitude";
    public static final String AXIS_KEY = "axis";
    public static final String VELOCITY_KEY = "velocity";

    /*
     * Other
     */
    public static final String MOTION_SOLVER_LIBS_KEY = "motionSolverLibs";
    public static final String MULTIBODY_DYNAMIC_LIBRARIES = "( \"libfvMotionSolvers.so\" )";
    public static final String SIX_DOF_DYNAMIC_LIBRARIES = "( \"libfvMotionSolvers.so\" \"libsixDoFRigidBodyMotion.so\" )";
    
    public DynamicMeshDict() {
        super(DYNAMIC_MESH_DICT);
        setFoamFile(FoamFile.getDictionaryFoamFile(CONSTANT, DYNAMIC_MESH_DICT));
    }

}
